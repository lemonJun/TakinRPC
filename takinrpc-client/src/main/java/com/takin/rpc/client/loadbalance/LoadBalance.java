package com.takin.rpc.client.loadbalance;

import java.util.List;

/**
 * Robert HG (254963746@qq.com) on 3/25/15.
 */
public interface LoadBalance {

    public <S> S select(List<S> shards, String seed);
    
}
