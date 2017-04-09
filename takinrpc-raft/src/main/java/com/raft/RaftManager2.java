package com.raft;

import com.raft.domain.Role;
import com.raft.domain.VoteReq;
import com.raft.event.EventContext;
import com.raft.event.EventListener;
import io.netty.channel.Channel;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.Random;

/**
 * Created by Administrator on 2016/11/5.
 */
public class RaftManager2 {
    private ChannelManager channelManager;
    private RequestManager requestManager;
    private TimerManager timerManager;
    private TaskManager taskManager;

    private RaftOperator raftOperator;

    private EventContext eventContext;

    public RaftManager2(EventContext eventContext) {
        channelManager = new ChannelManager();
        requestManager = new RequestManager();
        timerManager = new TimerManager();
        taskManager = new TaskManager();

        raftOperator = new RaftOperator(requestManager, channelManager);

        this.eventContext = eventContext;
    }

    public RaftOperator getRaftOperator() {
        return raftOperator;
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public void initListener() {
        this.eventContext.addListener(RaftEvent.ElectionEvent, new RaftManager2.RaftTransport());
    }

    //主要任务是接收心跳
    private void processForFollower() {
        final long lastLeaderForHeartBeat = ServerNode.getLastHeartBeatMills();
        this.timerManager.asTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                if (ServerNode.getRole() != Role.FOLLOWER) {
                    return;
                }

                boolean lastHeartBeat = ServerNode.getLastHeartBeatMills() > lastLeaderForHeartBeat;
                if (lastHeartBeat) {
                    return;
                }

                if (ServerNode.getRole() == Role.FOLLOWER) {
                    eventContext.fireAsyncEvent(RaftEvent.ElectionEvent, ServerRole.CANDIDATE);
                }
            }
        }, 5000);
    }

    //主要任务是进行请求选举
    private void processForCandidate() {
        //自增term
        ServerNode.termIncr();

        for (Channel channel : channelManager.list()) {
            //组装拉票数据
            VoteReq voteReq = new VoteReq();
            voteReq.setTerm(String.valueOf(ServerNode.getCurrentTerm()));
            voteReq.setRemoteAddress(channel.localAddress().toString());

            Object result = requestManager.asyncRequest(channel, voteReq);
            System.out.println(result);
        }

        //获取票数
        final int votes = ServerNode.getVotes();

        //重计投票数
        ServerNode.resetVotes();

        long nextTimeout = (new Random(System.currentTimeMillis()).nextInt(150) + 150) + 5000;
        this.timerManager.asTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                if (ServerNode.getRole() != Role.CANDIDATES) {
                    return;
                }

                if (votes * 2 > ServerNode.getServers()) {
                    eventContext.fireAsyncEvent(RaftEvent.ElectionEvent, ServerRole.LEADER);
                }
            }
        }, nextTimeout);
    }

    //主要任务就是发送心跳
    private void processForLeader() {
        System.out.println("发送心跳");
        if (ServerNode.getRole() != Role.LEADER) {
            System.out.println("你不是leader你还开启了HeartBeatTask???");
            if (ServerNode.getRole() == Role.FOLLOWER) {
                //开始下一次的选举
                long nextTimeout = (new Random(System.currentTimeMillis()).nextInt(150) + 150) + 5000;
                timerManager.asTime(taskManager.getRequestVoteTask(), nextTimeout);
            }
            return;
        }

        this.timerManager.asTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                if (ServerNode.getRole() != Role.LEADER) {
                    return;
                }

                processForLeader();
            }
        }, 2000);
    }

    class RaftTransport implements EventListener<ServerRole> {
        public void onEvent(String event, ServerRole eventData) throws Throwable {
            if (eventData == ServerRole.FOLLOWER) {
                processForFollower();
            } else if (eventData == ServerRole.CANDIDATE) {
                processForCandidate();
            } else if (eventData == ServerRole.LEADER) {
                processForLeader();
            }
        }
    }

}
