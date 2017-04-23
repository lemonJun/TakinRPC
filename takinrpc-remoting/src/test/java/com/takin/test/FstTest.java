package com.takin.test;

import org.nustaq.serialization.FSTConfiguration;

import com.alibaba.fastjson.JSON;
import com.takin.rpc.remoting.netty4.RemotingProtocol;

public class FstTest {

    public static void main(String[] args) {
        try {
            RemotingProtocol message = new RemotingProtocol<>();
            message.setMethod("aadsf");
            message.setArgs(args);
            FSTConfiguration config = FSTConfiguration.createDefaultConfiguration();
            byte[] data2 = config.asByteArray(message);

            // 2. deserialize
            RemotingProtocol object2 = (RemotingProtocol) config.asObject(data2);
            System.out.println(JSON.toJSONString(object2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
