package com.takin.rpc.client.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.takin.emmet.algo.ConsistentHashSelector;
import com.takin.emmet.string.StringUtil;

/**
 * 一致性hash算法
 * Robert HG (254963746@qq.com) on 3/25/15.
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    @Override
    protected <S> S doSelect(List<S> shards, String seed) {
        if (StringUtil.isNullOrEmpty(seed)) {
            seed = "HASH-".concat(String.valueOf(ThreadLocalRandom.current().nextInt()));
        }
        ConsistentHashSelector<S> selector = new ConsistentHashSelector<S>(shards);
        return selector.selectForKey(seed);
    }
}
