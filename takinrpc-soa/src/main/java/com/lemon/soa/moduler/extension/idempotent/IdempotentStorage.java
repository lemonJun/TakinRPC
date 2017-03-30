package com.lemon.soa.moduler.extension.idempotent;

/**
 * 幂等机制数据持久化
 * 
 * me
 *
 * @param <RES>
 */
public class IdempotentStorage<RES> {

    private String id;
    private Long time;
    private RES res;

    public IdempotentStorage(String id, Long time, RES res) {
        this.id = id;
        this.time = time;
        this.res = res;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public RES getRes() {
        return res;
    }

    public void setRes(RES res) {
        this.res = res;
    }

}
