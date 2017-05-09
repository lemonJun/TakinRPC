package com.takin.rpc.client.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.alibaba.fastjson.JSON;

/**
 * 随机负载均衡算法
 * Robert HG (254963746@qq.com) on 3/25/15.
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected <S> S doSelect(List<S> shards, String seed) {
        logger.info(JSON.toJSONString(shards));

        S s = shards.get(ThreadLocalRandom.current().nextInt(shards.size()));
        logger.info("random s=" + s);

        return s;
    }
}
