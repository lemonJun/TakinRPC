package com.raft.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/31.
 */
public class VoteResp implements Serializable {
    private boolean isVote;
    public String respAddr;

    public boolean isVote() {
        return isVote;
    }

    public void setIsVote(boolean isVote) {
        this.isVote = isVote;
    }

    public String getRespAddr() {
        return respAddr;
    }

    public void setRespAddr(String respAddr) {
        this.respAddr = respAddr;
    }
}
