package com.takin.rpc.client.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.alibaba.fastjson.JSON;
import com.takin.emmet.algo.ConsistentHashSelector;
import com.takin.emmet.string.StringUtil;

/**
 * 一致性hash算法
 * Robert HG (254963746@qq.com) on 3/25/15.
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    @Override
    protected <S> S doSelect(List<S> shards, String seed) {
        //        logger.info(JSON.toJSONString(shards));
        if (StringUtil.isNullOrEmpty(seed)) {
            seed = "HASH-".concat(String.valueOf(ThreadLocalRandom.current().nextInt()));
        }
        ConsistentHashSelector<S> selector = new ConsistentHashSelector<S>(shards);
        S s = selector.selectForKey(seed);
        //        logger.info("consistent s=" + s);
        return s;
    }
}
