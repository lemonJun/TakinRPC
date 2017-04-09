package com.raft;

import com.raft.domain.HeartBeat;
import com.raft.domain.Role;
import com.raft.domain.VoteReq;
import com.raft.event.EventContext;
import com.raft.event.EventListener;
import io.netty.channel.Channel;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2016/11/3.
 */
public class RaftManager {
    private ChannelManager channelManager;
    private RequestManager requestManager;
    private TimerManager timerManager;
    private TaskManager taskManager;

    private EventContext eventContext;

    public RaftManager(EventContext eventContext) {
        channelManager = new ChannelManager();
        requestManager = new RequestManager();
        timerManager = new TimerManager();
        taskManager = new TaskManager();

        this.eventContext = eventContext;
        this.eventContext.addListener(RaftEvent.ElectionEvent, new RaftTransport());
    }

    private void doHeartBeat() {
        try {
            //TODO 心跳检测是否需要客户端返回数据？
            //TODO 应该加多一个不需要返回数据的sendData,或者是UDP协议的？因为不需要返回数据，则超时这个功能也就不需要了
            for (Channel channel : channelManager.list()) {
                Object result = requestManager.syncRequest(channel, new HeartBeat());
                System.out.println(result);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //leader的心跳请求
    private void heartBeat() {
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

        //发送心跳
        doHeartBeat();

        //准备下一次的心跳发送任务.
        timerManager.asTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                heartBeat();
            }
        }, 2000);
    }

    //leader发送的rpc请求
    private Object appendEntries() {
        return null;
    }

    //candidate的拉票请求
    public void voteRequest() {
        if (ServerNode.getRole() != Role.FOLLOWER) {
            //心跳超时的处理
            long timeout = System.currentTimeMillis() - ServerNode.getLastHeartBeatMills();
            if (timeout <= 3000) {
                //开始下一次的选举
                long nextTimeout = (new Random(System.currentTimeMillis()).nextInt(150) + 150) + 5000;
                timerManager.asTime(taskManager.getRequestVoteTask(), nextTimeout);
                return;
            }
        }

        //自增term
        ServerNode.termIncr();
        ServerNode.setRole(Role.CANDIDATES);

        for (Channel channel : channelManager.list()) {
            //组装拉票数据
            VoteReq voteReq = new VoteReq();
            voteReq.setTerm(String.valueOf(ServerNode.getCurrentTerm()));
            voteReq.setRemoteAddress(channel.localAddress().toString());

            Object result = requestManager.asyncRequest(channel, voteReq);
            System.out.println(result);
        }

        //获取票数
        int votes = ServerNode.getVotes();

        //重计投票数
        ServerNode.resetVotes();

        //是否选举成功
        if ((votes * 2 + 1) > ServerNode.getServers()) {
            //leader已经选举出来了,
            //选举任务应该还是要继续的,选举任务要判断isStopElection,表示是否需要进行选举的操作,
            //选举任务也要判断上一次的心跳是什么时候.如果心跳超时,则就转换成candidate角色进行开始选举
            //所以心跳时间一般比下一次的选举要短.

            //开始尝试成为leader
            if (ServerNode.getRole() == Role.CANDIDATES) {
                ServerNode.setRole(Role.LEADER);

                //成为leader后启动心跳的任务
                timerManager.asTime(new TimerTask() {
                    public void run(Timeout timeout) throws Exception {
                        //                        heartBeat();
                        //执行心跳发送
                        eventContext.fireAsyncEvent(RaftEvent.ElectionEvent, RaftOperate.HeartBeat);
                    }
                }, 0);

                System.out.println("我成为leader了");

                return;
            }
        }

        //开始下一次的选举
        long nextTimeout = (new Random(System.currentTimeMillis()).nextInt(150) + 150) + 5000;
        timerManager.asTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                voteRequest();
            }
        }, nextTimeout);
    }

    class RaftTransport implements EventListener<RaftOperate> {
        public void onEvent(String event, RaftOperate eventData) throws Throwable {
            if (eventData == RaftOperate.HeartBeat) {
                heartBeat();
            } else if (eventData == RaftOperate.Vote) {
                voteRequest();
            } else if (eventData == RaftOperate.AppendEntries) {
                appendEntries();
            }
        }
    }
}
