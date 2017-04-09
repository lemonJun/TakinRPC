package com.raft.bootstrap;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import com.raft.ChannelManager;
import com.raft.RaftEvent;
import com.raft.RaftManager2;
import com.raft.RequestManager;
import com.raft.ServerNode;
import com.raft.ServerRole;
import com.raft.TaskManager;
import com.raft.TimerManager;
import com.raft.domain.HeartBeat;
import com.raft.domain.RequestInfo;
import com.raft.domain.Role;
import com.raft.domain.Server;
import com.raft.domain.VoteReq;
import com.raft.event.EventContext;
import com.raft.event.StandardEventManager;
import com.raft.future.BasicFuture;
import com.raft.handler.ServerHandler2;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.Timeout;

/**
 * Created by Administrator on 2016/10/28.
 */
public class ServerStartup {
    private final ChannelManager channelManager;
    private final RequestManager requestManager;
    private final TaskManager taskManager;
    private final TimerManager timerManager;

    private final AtomicLong requestId;

    private EventContext eventContext;

    private Server[] servers;
    private int port;

    public ServerStartup(int port, Server[] servers) {
        this.port = port;
        this.servers = servers;

        channelManager = new ChannelManager();
        requestManager = new RequestManager();
        taskManager = new TaskManager();
        timerManager = new TimerManager();
        requestId = new AtomicLong(0);

        eventContext = new StandardEventManager(10);
    }

    private Object sendData(Channel channel, Object obj) throws ExecutionException, InterruptedException {
        //递增requestId
        final long requestVal = requestId.incrementAndGet();

        BasicFuture future = new BasicFuture();
        requestManager.add(requestVal, future);

        //超时功能
        /*
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                BasicFuture future = requestManager.get(requestVal);
                //如果为空则代表已经完成了
                if(future == null)
                {
                    return;
                }
                //如果future还存在则代表已超时了.
                future.failed(new TimeoutException());
            }
        },1000,2000);
        */

        RequestInfo reqInfo = new RequestInfo();
        reqInfo.setService(obj);
        reqInfo.setAddr(channel.localAddress().toString());
        reqInfo.setRequestId(requestVal);

        channel.writeAndFlush(reqInfo);

        return future.get();
    }

    public void startUp() {
        for (final Server server : servers) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Bootstrap client = new Bootstrap();
                    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

                    final RaftManager2 manager = new RaftManager2(eventContext);
                    manager.initListener();

                    try {
                        client.group(eventLoopGroup);
                        client.option(ChannelOption.TCP_NODELAY, true);
                        client.channel(NioSocketChannel.class);
                        client.handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                //                                socketChannel.pipeline().addLast(new ServerHandler(channelManager));
                                socketChannel.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                                socketChannel.pipeline().addLast(new ObjectEncoder());
                                //                                socketChannel.pipeline().addLast(new ReServerHandler(requestManager, channelManager,timerManager,taskManager,false));
                                socketChannel.pipeline().addLast(new ServerHandler2(manager, true));
                            }
                        });

                        ChannelFuture future = client.connect(new InetSocketAddress(server.getAddr(), server.getPort())).sync();

                        future.channel().closeFuture().sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        eventLoopGroup.shutdownGracefully();
                    }
                }
            }, 10000);
        }

        final ServerBootstrap server = new ServerBootstrap();

        final RaftManager2 manager = new RaftManager2(eventContext);
        manager.initListener();

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            server.group(eventLoopGroup);
            server.channel(NioServerSocketChannel.class);
            server.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                    socketChannel.pipeline().addLast(new ObjectEncoder());
                    //                    socketChannel.pipeline().addLast(new ServerHandler(channelManager));
                    //                    socketChannel.pipeline().addLast(new ReServerHandler(requestManager,channelManager,timerManager,taskManager,true));
                    socketChannel.pipeline().addLast(new ServerHandler2(manager, true));
                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = server.bind("127.0.0.1", port).sync();

            //TODO 以下这些应该放在初始化的模块上
            //新增选举事件
            //            eventContext.addListener(RaftEvent.ElectionEvent,new RaftTransport());

            /*
            //设置心跳任务
            taskManager.setHeartBeatTask(new HeartBeatTask());
            
            //设置请求投票任务
            taskManager.setRequestVoteTask(new RequestVoteTask());
            timerManager.asTime(taskManager.getRequestVoteTask(),10000);
            */

            //触发选举
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    eventContext.fireAsyncEvent(RaftEvent.ElectionEvent, ServerRole.FOLLOWER);
                }
            }, 15000);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public class HeartBeatTask implements io.netty.util.TimerTask {
        public void run(Timeout timeout) throws Exception {
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

            for (Channel channel : channelManager.list()) {
                try {
                    //TODO 心跳检测是否需要客户端返回数据？
                    //TODO 应该加多一个不需要返回数据的sendData,或者是UDP协议的？因为不需要返回数据，则超时这个功能也就不需要了
                    Object result = sendData(channel, new HeartBeat());
                    System.out.println(result);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            timerManager.asTime(this, 2000);
        }
    }

    public class RequestVoteTask implements io.netty.util.TimerTask {
        public void run(Timeout tout) throws Exception {
            if (ServerNode.getRole() == Role.FOLLOWER) {
                //心跳超时的处理
                long timeout = System.currentTimeMillis() - ServerNode.getLastHeartBeatMills();
                if (timeout <= 3000) {
                    //开始下一次的选举
                    long nextTimeout = (new Random(System.currentTimeMillis()).nextInt(150) + 150) + 5000;
                    //                    timerManager.schedule(taskManager.getRequestVoteTask(),nextTimeout);
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

                try {
                    Object result = sendData(channel, voteReq);
                    System.out.println(result);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    timerManager.asTime(taskManager.getHeartBeatTask(), 0);

                    System.out.println("我成为leader了");

                    return;
                }
            }

            //开始下一次的选举
            long nextTimeout = (new Random(System.currentTimeMillis()).nextInt(150) + 150) + 5000;
            timerManager.asTime(taskManager.getRequestVoteTask(), nextTimeout);
        }
    }
}
