package com.takin.rpc.client.loadbalance;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Robert HG (254963746@qq.com) on 3/25/15.
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractLoadBalance.class);

    @Override
    public <S> S select(List<S> shards, String seed) {
        if (shards == null || shards.size() == 0) {
            return null;
        }

        if (shards.size() == 1) {
            return shards.get(0);
        }

        return doSelect(shards, seed);
    }

    protected abstract <S> S doSelect(List<S> shards, String seed);

}
