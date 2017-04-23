package com.takin.rpc.remoting.nio.handler;

import com.takin.rpc.remoting.nio.channel.NioChannel;
import com.takin.rpc.remoting.nio.idle.IdleState;

/**
 * @author Robert HG (254963746@qq.com) on 1/24/16.
 */
public interface NioHandler {

    void exceptionCaught(NioChannel channel, Exception cause);

    void messageReceived(NioChannel channel, Object msg) throws Exception;

    void channelConnected(NioChannel channel);

    void channelIdle(NioChannel channel, IdleState state);
}
