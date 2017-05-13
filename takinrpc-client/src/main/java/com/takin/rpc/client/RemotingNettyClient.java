package com.takin.rpc.client;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.emmet.util.SystemClock;
import com.takin.rpc.remoting.InvokeCallback;
import com.takin.rpc.remoting.codec.KyroMsgDecoder;
import com.takin.rpc.remoting.codec.KyroMsgEncoder;
import com.takin.rpc.remoting.exception.RemotingConnectException;
import com.takin.rpc.remoting.exception.RemotingSendRequestException;
import com.takin.rpc.remoting.exception.RemotingTimeoutException;
import com.takin.rpc.remoting.netty4.ChannelWrapper;
import com.takin.rpc.remoting.netty4.RemotingAbstract;
import com.takin.rpc.remoting.netty4.RemotingProtocol;
import com.takin.rpc.remoting.netty4.ResponseFuture;
import com.takin.rpc.remoting.util.RemotingHelper;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RemotingNettyClient extends RemotingAbstract {

    private static final Logger logger = LoggerFactory.getLogger(RemotingNettyClient.class);

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup group;
    @SuppressWarnings("unused")
    private final ExecutorService publicExecutor;
    //保存所有服务端的地址   
    private final AtomicReference<List<String>> namesrvAddrList = new AtomicReference<List<String>>();

    private ConcurrentHashMap<String, ChannelWrapper> channelTables = new ConcurrentHashMap<String, ChannelWrapper>();

    public static volatile RemotingNettyClient instance;
    //    private static final AtomicBoolean once = new AtomicBoolean(false);

    public static RemotingNettyClient getInstance() {
        if (instance == null) {
            synchronized (RemotingNettyClient.class) {
                if (instance == null) {
                    instance = new RemotingNettyClient(new NettyClientConfig());
                    instance.start();
                }
            }
        }
        return instance;
    }

    private RemotingNettyClient(final NettyClientConfig nettyClientConfig) {
        super(nettyClientConfig.getOnewaySemaphoreValue(), nettyClientConfig.getAsyncSemaphoreValue());
        int publicThreadNums = nettyClientConfig.getCallbackExecutorThreads();
        if (publicThreadNums <= 0) {
            publicThreadNums = 4;
        }
        this.publicExecutor = Executors.newFixedThreadPool(publicThreadNums, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyClientPublicExecutor_" + this.threadIndex.incrementAndGet());
            }
        });

        group = new NioEventLoopGroup(nettyClientConfig.getWorkerThreads(), Executors.newFixedThreadPool(1));
    }

    public void start() {
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                //                ch.pipeline().addLast(new IdleStateHandler(1, 1, 5));
                ch.pipeline().addLast(new KyroMsgDecoder());
                ch.pipeline().addLast(new KyroMsgEncoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });

        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                scanResponseTable(3000);
            }
        }, 5 * 1000, 5000, TimeUnit.MILLISECONDS);
    }

    private final Lock lockChannelTables = new ReentrantLock();

    /**
     * 新建通道
     * @param address
     * @return
     */
    private Channel createChannel(final String address) {
        ChannelWrapper cw = channelTables.get(address);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }
        try {
            if (this.lockChannelTables.tryLock(1000 * 1000, TimeUnit.NANOSECONDS)) {
                boolean createNewConnection = false;
                cw = this.channelTables.get(address);
                if (cw != null) {
                    if (cw.isOK()) {// channel正常
                        return cw.getChannel();
                    } else if (!cw.getChannelFuture().isDone()) {// 正在连接，退出锁等待
                        createNewConnection = false;
                    } else {// 说明连接不成功
                        this.channelTables.remove(address);
                        createNewConnection = true;
                    }
                } else { // ChannelWrapper不存在
                    createNewConnection = true;
                }

                if (createNewConnection) {
                    String host = address.split(":")[0];
                    int port = Integer.parseInt(address.split(":")[1]);
                    ChannelFuture channelFuture = this.bootstrap.connect(host, port).sync();
                    cw = new ChannelWrapper(channelFuture);
                    this.channelTables.put(address, cw);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            this.lockChannelTables.unlock();
        }
        logger.info(String.format("create new channel for:%S", address));
        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            if (channelFuture.awaitUninterruptibly(10)) {
                if (cw.isOK()) {
                    return cw.getChannel();
                }
            }
        }
        return null;
    }

    /**
     * 发起同步调用
     * 其实后面也是异步的
     * 这个同步是指：可以携带结果返回
     * 
     * @param address
     * @param message
     * @param timeout
     * @return 
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public RemotingProtocol invokeSync(String address, final RemotingProtocol message, int timeout) throws Exception, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        //        Stopwatch watch = Stopwatch.createStarted();

        final Channel channel = this.createChannel(address);
        //        logger.info(String.format("create channel use:%s", watch.toString()));
        if (channel != null && channel.isActive()) {
            try {
                RemotingProtocol proto = invokeSyncImpl(channel, message, timeout);
                //                logger.info(String.format("invokesync use:%s", watch.toString()));
                return proto;
            } catch (Exception e) {
                logger.error("", e);
                throw e;
            }
        } else {
            closeChannel(channel, address);
            throw new RemotingConnectException(address);
        }
    }

    @SuppressWarnings("rawtypes")
    public void invokeASync(String address, final RemotingProtocol message, int timeout, InvokeCallback callback) throws Exception, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        final Channel channel = this.createChannel(address);
        if (channel != null && channel.isActive()) {
            try {
                invokeAsyncImpl(channel, message, timeout, callback);
            } catch (Exception e) {
                logger.error("", e);
                throw e;
            }
        } else {
            closeChannel(channel, address);
            throw new RemotingConnectException(address);
        }
    }

    /**
     * 关闭资源
     * 
     */
    public void shutdown() {
        try {
            for (ChannelWrapper cw : this.channelTables.values()) {
                this.closeChannel(cw.getChannel(), null);
            }
            this.channelTables.clear();
            this.group.shutdownGracefully();
        } catch (Exception e) {
            logger.error("shutdown error", e);
        }
    }

    /**
     * 如果通道不可用  则应将其关闭 以免占用资源
     * @param channel
     * @param address
     */
    public void closeChannel(Channel channel, String address) {
        if (channel == null) {
            return;
        }
        logger.info("close channel " + channel.id());
        final String addrRemote = null == address ? RemotingHelper.parseChannelRemoteAddr(channel) : address;
        try {
            if (this.lockChannelTables.tryLock(2000, TimeUnit.MILLISECONDS)) {
                try {
                    boolean removeItemFromTable = true;
                    final ChannelWrapper prevCW = this.channelTables.get(addrRemote);
                    if (null == prevCW) {
                        removeItemFromTable = false;
                    } else if (prevCW.getChannel() != channel) {
                        removeItemFromTable = false;
                    }

                    if (removeItemFromTable) {
                        this.channelTables.remove(addrRemote);
                    }
                    closeChannel(channel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void closeChannel(Channel channel) {
        final String addrRemote = RemotingHelper.parseChannelRemoteAddr(channel);
        channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                logger.info("closeChannel: close the connection to remote address[{}] result: {}", addrRemote, future.isSuccess());
            }
        });
    }

    /**
     * 描述所有的对外请求
     * 看是否有超时未处理情况
     * 此超时时间已经考虑了 任务的默认等待时间
     */
    public void scanResponseTable(long timeout) {
        Iterator<Entry<Long, ResponseFuture>> it = responseTable.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, ResponseFuture> next = it.next();
            ResponseFuture rep = next.getValue();

            if ((rep.getBeginTimestamp() + rep.getTimeoutMillis() + timeout) <= SystemClock.now()) {
                logger.info(String.format("remove responsefuture %d", rep.getOpaque()));
                it.remove();
            }
        }
    }

    @Override
    public ExecutorService getCallbackExecutor() {
        return null;
    }

    public void updateNameServerAddressList(List<String> addrs) {
        List<String> old = this.namesrvAddrList.get();
        boolean update = false;

        if (!addrs.isEmpty()) {
            if (null == old) {
                update = true;
            } else if (addrs.size() != old.size()) {
                update = true;
            } else {
                for (int i = 0; i < addrs.size() && !update; i++) {
                    if (!old.contains(addrs.get(i))) {
                        update = true;
                    }
                }
            }

            if (update) {
                Collections.shuffle(addrs);
                this.namesrvAddrList.set(addrs);
            }
        }
    }

    public List<String> getNameServerAddressList() {
        return this.namesrvAddrList.get();
    }

}
