//package com.takin.rpc.remoting.netty5;
//
//import io.netty.channel.ChannelHandlerAdapter;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.timeout.ReadTimeoutException;
//
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
///**
// * 
// *
// *
// * @author lemon
// * @version 1.0
// * @date  2015年9月7日 下午12:58:21
// * @see 
// * @since
// */
//public class ReadTimeoutHandler extends ChannelHandlerAdapter {
//
//    private final long timeoutMillis;
//
//    private volatile ScheduledFuture<?> timeout;
//    private volatile long lastReadTime;
//
//    private volatile int state; // 0 - none, 1 - Initialized, 2 - Destroyed;
//
//    private boolean closed;
//
//    /**
//     * Creates a new instance.
//     *
//     * @param timeoutSeconds
//     *        read timeout in seconds
//     */
//    public ReadTimeoutHandler(int timeoutSeconds) {
//        this(timeoutSeconds, TimeUnit.SECONDS);
//    }
//
//    /**
//     * Creates a new instance.
//     *
//     * @param timeout
//     *        read timeout
//     * @param unit
//     *        the {@link TimeUnit} of {@code timeout}
//     */
//    public ReadTimeoutHandler(long timeout, TimeUnit unit) {
//        if (unit == null) {
//            throw new NullPointerException("unit");
//        }
//
//        if (timeout <= 0) {
//            timeoutMillis = 0;
//        } else {
//            timeoutMillis = Math.max(unit.toMillis(timeout), 1);
//        }
//    }
//
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        if (ctx.channel().isActive() && ctx.channel().isRegistered()) {
//            // channelActvie() event has been fired already, which means this.channelActive() will
//            // not be invoked. We have to initialize here instead.
//            initialize(ctx);
//        } else {
//            // channelActive() event has not been fired yet.  this.channelActive() will be invoked
//            // and initialization will occur there.
//        }
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        destroy();
//    }
//
//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        // Initialize early if channel is active already.
//        if (ctx.channel().isActive()) {
//            initialize(ctx);
//        }
//        super.channelRegistered(ctx);
//    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        // This method will be invoked only if this handler was added
//        // before channelActive() event is fired.  If a user adds this handler
//        // after the channelActive() event, initialize() will be called by beforeAdd().
//        initialize(ctx);
//        super.channelActive(ctx);
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        destroy();
//        super.channelInactive(ctx);
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        lastReadTime = System.currentTimeMillis();
//        ctx.fireChannelRead(msg);
//    }
//
//    private void initialize(ChannelHandlerContext ctx) {
//        // Avoid the case where destroy() is called before scheduling timeouts.
//        // See: https://github.com/netty/netty/issues/143
//        switch (state) {
//            case 1:
//            case 2:
//                return;
//        }
//
//        state = 1;
//
//        lastReadTime = System.currentTimeMillis();
//        if (timeoutMillis > 0) {
//            timeout = ctx.executor().schedule(new ReadTimeoutTask(ctx), timeoutMillis, TimeUnit.MILLISECONDS);
//        }
//    }
//
//    private void destroy() {
//        state = 2;
//
//        if (timeout != null) {
//            timeout.cancel(false);
//            timeout = null;
//        }
//    }
//
//    /**
//     * Is called when a read timeout was detected.
//     */
//    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
//        if (!closed) {
//            ctx.fireExceptionCaught(ReadTimeoutException.INSTANCE);
//            ctx.close();
//            closed = true;
//        }
//    }
//
//    private final class ReadTimeoutTask implements Runnable {
//
//        private final ChannelHandlerContext ctx;
//
//        ReadTimeoutTask(ChannelHandlerContext ctx) {
//            this.ctx = ctx;
//        }
//
//        @Override
//        public void run() {
//            if (!ctx.channel().isOpen()) {
//                return;
//            }
//
//            long currentTime = System.currentTimeMillis();
//            long nextDelay = timeoutMillis - (currentTime - lastReadTime);
//            if (nextDelay <= 0) {
//                // Read timed out - set a new timeout and notify the callback.
//                timeout = ctx.executor().schedule(this, timeoutMillis, TimeUnit.MILLISECONDS);
//                try {
//                    readTimedOut(ctx);
//                } catch (Throwable t) {
//                    ctx.fireExceptionCaught(t);
//                }
//            } else {
//                // Read occurred before the timeout - set a new timeout with shorter delay.
//                timeout = ctx.executor().schedule(this, nextDelay, TimeUnit.MILLISECONDS);
//            }
//        }
//    }
//}
