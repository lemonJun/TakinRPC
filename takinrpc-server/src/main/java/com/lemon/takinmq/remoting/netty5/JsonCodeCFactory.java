package com.lemon.takinmq.remoting.netty5;

import io.netty.handler.codec.json.JsonObjectDecoder;

public class JsonCodeCFactory {

    public static JsonObjectDecoder buildJsonDecoder() {
        return new JsonObjectDecoder();
    }

}
