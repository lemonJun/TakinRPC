package com.takin.rpc.remoting.nio.codec;

import java.nio.ByteBuffer;

import com.takin.rpc.remoting.nio.channel.NioChannel;

/**
 * @author Robert HG (254963746@qq.com) on 1/30/16.
 */
public interface Encoder {

    ByteBuffer encode(NioChannel channel, Object msg);

}
