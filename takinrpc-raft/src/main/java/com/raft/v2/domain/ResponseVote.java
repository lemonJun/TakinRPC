package com.raft.v2.domain;

/**
 * Created by Administrator on 2016/11/8.
 */
public class ResponseVote {
    private int term;
    private boolean voteGranted;

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isVoteGranted() {
        return voteGranted;
    }

    public void setVoteGranted(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }
}
