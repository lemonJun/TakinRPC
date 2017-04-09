package com.raft.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by Administrator on 2016/10/31.
 */
public class CustomDecoder extends LengthFieldBasedFrameDecoder {
    public CustomDecoder(int maxBodyLength) {
        super(maxBodyLength, 10, 3, 0, 0);
    }
}
