package com.raft;

import java.util.Random;

import com.raft.domain.HeartBeat;
import com.raft.domain.HeartBeatResp;
import com.raft.domain.RequestInfo;
import com.raft.domain.RequestRpc;
import com.raft.domain.ResponseInfo;
import com.raft.domain.ResponseRpc;
import com.raft.domain.Role;
import com.raft.domain.VoteReq;
import com.raft.domain.VoteResp;
import com.raft.future.BasicFuture;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

/**
 * Created by Administrator on 2016/10/28.
 */
public class RaftOperator {
    private static Object object = new Object();

    private RequestManager requestManager;
    private ChannelManager channelManager;

    public RaftOperator(RequestManager requestManager, ChannelManager channelManager) {
        this.requestManager = requestManager;
        this.channelManager = channelManager;
    }

    //Cadidates：请求follwer对我进行投票
    public static void requestVote(Channel channel) {
        channel.writeAndFlush(Unpooled.copiedBuffer("voteToMe".getBytes()));
    }

    //follower：回复Cadidates我的投票结果
    public static void responseVote() {
        ServerNode.voteIncr();

        if ((ServerNode.getVotes() * 2 > ServerNode.getServers())) {
            System.out.println("我拥有票数为" + ServerNode.getVotes() + ",已经超过一半了，我要成为老大了");
            synchronized (object) {
                /*
                ServerNode.heartBeatTimer = new Timer();
                ServerNode.heartBeatTimer.schedule(ServerNode.heartBeatTask, 1000, 1000);
                
                ServerNode.requestVoteTimer.cancel();
                ServerNode.requestVoteTimer = null;
                */
            }
        }
    }

    //leader：向follower发送AppendEntries请求
    public static void requestRpc() {

    }

    //follower：回复leader发过来的AppendEntries的请求
    public static void responseRpc() {

    }

    //leader：心跳发送
    public static void requestHeartBeat(Channel channel) {
        channel.writeAndFlush(Unpooled.copiedBuffer("ping".getBytes()));
        System.out.println("发送心跳");
    }

    //follower：心跳接受,同步lastHeartBeat时间
    public static void responseHeartBeat() {
        System.out.println("接受心跳");
        ServerNode.setLastHeartBeatMills(System.currentTimeMillis());
        ServerNode.setRole(Role.FOLLOWER);

        /*
        if(ServerNode.requestVoteTimer != null) {
            System.out.println("停止拉票了");
            ServerNode.requestVoteTimer.cancel();
            ServerNode.requestVoteTimer = null;
        }
        */
    }

    public void invoke(RequestInfo reqInfo, Channel channel) {
        Object service = reqInfo.getService();
        if (service instanceof VoteReq) {
            int val = new Random().nextInt(3);
            boolean isVote = false;
            if (val > 1) {
                System.out.println("我把票投给了" + reqInfo.getAddr());
                isVote = true;
            }

            //请求拉票的返回数据
            VoteResp voteResp = new VoteResp();
            voteResp.setIsVote(isVote);
            voteResp.setRespAddr(channel.localAddress().toString());

            ResponseInfo respInfo = new ResponseInfo();
            respInfo.setService(voteResp);
            respInfo.setRequestId(reqInfo.getRequestId());

            //写入channel中
            channel.writeAndFlush(respInfo);
        } else if (service instanceof RequestRpc) {
            //TODO
        } else if (service instanceof HeartBeat) {
            synchronized (object) {
                System.out.println("接受心跳.身份为follower");
                ServerNode.setLastHeartBeatMills(System.currentTimeMillis());
                ServerNode.setRole(Role.FOLLOWER);

                ResponseInfo respInfo = new ResponseInfo();
                respInfo.setRequestId(reqInfo.getRequestId());
                respInfo.setService(new HeartBeatResp());

                channel.writeAndFlush(respInfo);
                /*
                BasicFuture future = requestManager.get(reqInfo.getRequestId());
                future.completed(reqInfo.getService());
                requestManager.remove(reqInfo.getRequestId());
                */

                /*
                if (ServerNode.requestVoteTimer != null) {
                    System.out.println("停止拉票了");
                    ServerNode.requestVoteTimer.cancel();
                    ServerNode.requestVoteTimer = null;
                }
                */
            }
        }
    }

    public void invoke(ResponseInfo respInfo) {
        Object service = respInfo.getService();
        if (service instanceof VoteResp) {
            VoteResp voteResp = (VoteResp) service;
            if (voteResp.isVote()) {
                ServerNode.voteIncr();
            }
        } else if (service instanceof ResponseRpc) {

        } else if (service instanceof HeartBeatResp) {
            System.out.println("follower回答了心跳");
        }

        //将返回的数据填入future中
        BasicFuture future;
        if ((future = requestManager.get(respInfo.getRequestId())) != null) {
            future.completed(respInfo.getService());

            requestManager.remove(respInfo.getRequestId());
        }
    }
}
