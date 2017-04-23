package com.takin.rpc.remoting.nio.codec;

import java.nio.ByteBuffer;
import java.util.List;

import com.takin.rpc.remoting.nio.channel.NioChannel;

/**
 * @author Robert HG (254963746@qq.com) on 1/30/16.
 */
public interface Decoder {

    List<Object> decode(NioChannel channel, ByteBuffer in) throws Exception;

}
