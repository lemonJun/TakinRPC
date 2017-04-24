package com.takin.rpc.remoting.nio.processor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.takin.emmet.collection.CollectionUtil;
import com.takin.emmet.util.SystemClock;
import com.takin.rpc.remoting.NamedThreadFactory;
import com.takin.rpc.remoting.nio.NioException;
import com.takin.rpc.remoting.nio.channel.ChannelInitializer;
import com.takin.rpc.remoting.nio.channel.NioChannel;
import com.takin.rpc.remoting.nio.handler.Futures;
import com.takin.rpc.remoting.nio.handler.NioHandler;
import com.takin.rpc.remoting.nio.idle.IdleDetector;
import com.takin.rpc.remoting.nio.loop.NioSelectorLoop;

/**
 * 
 *
 * @author WangYazhou
 * @date  2017年4月24日 下午1:53:18
 * @see
 */
public abstract class AbstractNioProcessor implements NioProcessor {
    protected static final Logger LOGGER = LoggerFactory.getLogger(NioProcessor.class);
    private NioHandler eventHandler;
    protected NioSelectorLoop selectorLoop;
    private Executor executor;
    private ConcurrentMap<NioChannel, WriteQueue> QUEUE_MAP = new ConcurrentHashMap<NioChannel, WriteQueue>();
    //    protected NioSelectorLoopPool readWriteSelectorPool;
    private AtomicBoolean started = new AtomicBoolean(false);

    protected IdleDetector idleDetector;
    protected ChannelInitializer channelInitializer;

    public AbstractNioProcessor(NioHandler eventHandler, ChannelInitializer channelInitializer) {
        this.eventHandler = eventHandler;
        int AVAILABLE_PROCESSOR = Runtime.getRuntime().availableProcessors();

        this.executor = Executors.newFixedThreadPool(AVAILABLE_PROCESSOR, new NamedThreadFactory("NioProcessorExecutor", true));
        this.selectorLoop = new NioSelectorLoop("AcceptSelectorLoop-I/O", this);
        //        this.readWriteSelectorPool = new FixedNioSelectorLoopPool(Constants.AVAILABLE_PROCESSOR + 1, "Server", this);
        this.idleDetector = new IdleDetector();
        this.channelInitializer = channelInitializer;
        this.idleDetector.start();
    }

    public Futures.WriteFuture writeAndFlush(NioChannel channel, Object msg) {
        SelectionKey key = channel.socketChannel().keyFor(selectorLoop.selector());
        if (key != null && key.isValid()) {
            key.interestOps(SelectionKey.OP_WRITE);
        }
        return write(channel, msg, true);
    }
    
    private Futures.WriteFuture write(NioChannel channel, Object msg, boolean flush) {
        Futures.WriteFuture future = Futures.newWriteFuture();
        if (msg == null) {
            future.setSuccess(true);
            future.setMsg("msg is null");
            future.notifyListeners();
            return future;
        }
        ByteBuffer buf = null;
        try {
            buf = channel.getEncoder().encode(channel, msg);
            if (buf == null) {
                future.setSuccess(false);
                future.setMsg("encode msg error");
                future.notifyListeners();
                return future;
            }
            QUEUE_MAP.get(channel).offer(new WriteRequest(buf, future));
        } catch (Exception e) {
            throw new NioException("encode msg " + msg + " error", e);
        }
        if (flush) {
            doFlush(channel);
        }
        return future;
    }

    public void flush(NioChannel channel) {
        doFlush(channel);
    }

    private void doFlush(final NioChannel channel) {

        executor().execute(new Runnable() {
            @Override
            public void run() {

                WriteQueue queue = QUEUE_MAP.get(channel);

                if (!queue.tryLock()) {
                    // 说明有线程在写
                    return;
                }
                try {
                    while (!queue.isEmpty()) {
                        WriteRequest msg = queue.peek();
                        if (msg == null) {
                            continue;
                        }
                        Futures.WriteFuture writeFuture = msg.getWriteFuture();

                        try {

                            ByteBuffer buf = msg.getMessage();

                            // 已经写的字节数
                            int written = channel.socketChannel().write(buf);

                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("wrote bytes {}", written);
                            }

                            channel.setLastWriteTime(SystemClock.now());

                            if (buf.remaining() == 0) {

                                queue.poll();

                                writeFuture.setSuccess(true);
                                writeFuture.notifyListeners();

                            } else {
                                // 输出socket buffer已经满了 等下一个周期
                                break;
                            }
                        } catch (Exception e) {
                            LOGGER.error("IOE while writing", e);
                            writeFuture.setSuccess(false);
                            writeFuture.setCause(e);
                            writeFuture.notifyListeners();
                            eventHandler().exceptionCaught(channel, e);
                            break;
                        }
                    }

                    SelectionKey key = channel.socketChannel().keyFor(selectorLoop.selector());
                    if (key != null && key.isValid()) {
                        key.interestOps(SelectionKey.OP_READ);
                    }
                } finally {
                    queue.unlock();
                }
            }
        });
    }

    public void read(NioChannel channel) {
        try {

            // TODO 优化
            ByteBuffer readBuffer = ByteBuffer.allocate(64 * 1024);

            final int readCount = channel.socketChannel().read(readBuffer);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("read {} bytes", readCount);
            }

            if (readCount < 0) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("channel closed by the remote peer");
                }
                channel.close();
            } else if (readCount > 0) {

                readBuffer.flip();

                //  TODO SSL处理

                doMessageReceived(channel, readBuffer);

                readBuffer.clear();
            }

        } catch (IOException e) {
            LOGGER.error("IOE while reading : ", e);
            eventHandler().exceptionCaught(channel, e);
        }
    }

    private void doMessageReceived(final NioChannel channel, final ByteBuffer message) {
        executor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Object> objs = channel.getDecoder().decode(channel, message);
                    if (CollectionUtil.isNotEmpty(objs)) {
                        for (Object obj : objs) {
                            eventHandler().messageReceived(channel, obj);
                        }
                    }
                    channel.setLastReadTime(SystemClock.now());
                } catch (Exception e) {
                    eventHandler().exceptionCaught(channel, e);
                }
            }
        });
    }

    public Futures.ConnectFuture connect(SocketAddress remoteAddress) {
        Futures.ConnectFuture connectFuture = Futures.newConnectFuture();
        NioChannel channel = doConnect(remoteAddress, selectorLoop, connectFuture);
        QUEUE_MAP.putIfAbsent(channel, new WriteQueue());
        return connectFuture;
    }

    public void accept(SelectionKey key) {
        NioChannel channel = doAccept(selectorLoop);
        QUEUE_MAP.putIfAbsent(channel, new WriteQueue());
    }

    public void start() {
        if (started.compareAndSet(false, true)) {
            selectorLoop.start();
        }
    }

    protected abstract NioChannel doAccept(NioSelectorLoop selectorLoop);

    protected abstract NioChannel doConnect(SocketAddress remoteAddress, NioSelectorLoop selectorLoop, Futures.ConnectFuture connectFuture);

    protected NioHandler eventHandler() {
        return this.eventHandler;
    }

    protected Executor executor() {
        return this.executor;
    }
}
