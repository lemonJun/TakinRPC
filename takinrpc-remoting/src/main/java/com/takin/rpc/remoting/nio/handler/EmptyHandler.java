package com.takin.rpc.remoting.nio.handler;

import com.takin.rpc.remoting.nio.channel.NioChannel;
import com.takin.rpc.remoting.nio.idle.IdleState;

/**
 * @author Robert HG (254963746@qq.com) on 1/24/16.
 */
public class EmptyHandler implements NioHandler {

    @Override
    public void exceptionCaught(NioChannel connection, Exception cause) {

    }

    @Override
    public void messageReceived(NioChannel connection, Object msg) throws Exception {

    }

    @Override
    public void channelConnected(NioChannel connection) {

    }

    @Override
    public void channelIdle(NioChannel channel, IdleState state) {

    }
}
