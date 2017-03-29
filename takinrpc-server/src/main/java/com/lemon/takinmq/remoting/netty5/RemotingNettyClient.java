package com.lemon.takinmq.remoting.netty5;

import java.net.InetSocketAddress;
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

import org.apache.log4j.Logger;

import com.lemon.takinmq.common.util.SystemClock;
import com.lemon.takinmq.remoting.codec.JsonDecode;
import com.lemon.takinmq.remoting.codec.JsonEncode;
import com.lemon.takinmq.remoting.codec.KyroMsgDecoder;
import com.lemon.takinmq.remoting.codec.KyroMsgEncoder;
import com.lemon.takinmq.remoting.exception.RemotingConnectException;
import com.lemon.takinmq.remoting.exception.RemotingSendRequestException;
import com.lemon.takinmq.remoting.exception.RemotingTimeoutException;
import com.lemon.takinmq.remoting.util.RemotingHelper;
import com.lemon.takinmq.remoting.util.SelectorUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RemotingNettyClient extends RemotingAbstract {

    private static final Logger logger = Logger.getLogger(RemotingNettyClient.class);

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup group;
    private final ExecutorService publicExecutor;
    //保存所有服务端的地址   
    private final AtomicReference<List<String>> namesrvAddrList = new AtomicReference<List<String>>();

    private ConcurrentHashMap<String, ChannelWrapper> channelTables = new ConcurrentHashMap<String, ChannelWrapper>();

    public RemotingNettyClient(final NettyClientConfig nettyClientConfig) {
        super(nettyClientConfig.getClientOnewaySemaphoreValue(), nettyClientConfig.getClientAsyncSemaphoreValue());
        int publicThreadNums = nettyClientConfig.getClientCallbackExecutorThreads();
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

        group = new NioEventLoopGroup();
    }

    public void start() {
        bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                //                ch.pipeline().addLast(new IdleStateHandler(1, 1, 5));
                //                ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                //                ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                ch.pipeline().addLast(new KyroMsgDecoder());
                ch.pipeline().addLast(new KyroMsgEncoder());
                ch.pipeline().addLast(new ClientMessageHandler());
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
        logger.info("addresss:" + address);
        ChannelWrapper cw = channelTables.get(address);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }
        try {
            if (this.lockChannelTables.tryLock(1000, TimeUnit.MILLISECONDS)) {
                boolean createNewConnection = false;
                cw = this.channelTables.get(address);
                if (cw != null) {
                    // channel正常
                    if (cw.isOK()) {
                        return cw.getChannel();
                    }
                    // 正在连接，退出锁等待
                    else if (!cw.getChannelFuture().isDone()) {
                        createNewConnection = false;
                    }
                    // 说明连接不成功
                    else {
                        this.channelTables.remove(address);
                        createNewConnection = true;
                    }
                }
                // ChannelWrapper不存在
                else {
                    createNewConnection = true;
                }

                if (createNewConnection) {
                    ChannelFuture channelFuture = this.bootstrap.connect(new InetSocketAddress(address.split(":")[0], Integer.parseInt(address.split(":")[1])));
                    cw = new ChannelWrapper(channelFuture);
                    this.channelTables.put(address, cw);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            this.lockChannelTables.unlock();
        }

        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            if (channelFuture.awaitUninterruptibly(10 * 1000l)) {
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
    public RemotingProtocol invokeSync(String address, final RemotingProtocol message, int timeout) throws Exception, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        final Channel channel = this.createChannel(address);
        long start = System.currentTimeMillis();
        if (channel != null && channel.isActive()) {
            try {
                RemotingProtocol result = invokeSyncImpl(channel, message, timeout);
                long end = System.currentTimeMillis();
                logger.info(String.format("invoke address:%s , use time:%dms", address, (end - start)));
                return result;
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
                    SelectorUtil.closeChannel(channel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

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
