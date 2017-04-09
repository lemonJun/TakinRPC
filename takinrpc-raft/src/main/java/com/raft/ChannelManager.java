package com.raft;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/10/28.
 */
public class ChannelManager {
    private final Map<String, Channel> channels;

    public ChannelManager() {
        channels = new ConcurrentHashMap<String, Channel>();
    }

    public void addChannel(String key, Channel channel) {
        channels.putIfAbsent(key, channel);
    }

    public void removeChannel(String key) {
        channels.remove(key);
    }

    public List<Channel> list() {
        return new ArrayList<Channel>(channels.values());
    }
}
