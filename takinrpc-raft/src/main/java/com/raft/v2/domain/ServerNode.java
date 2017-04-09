package com.raft.v2.domain;

/**
 * Created by Administrator on 2016/11/8.
 */
public class ServerNode {
    private static final ServerNode INSTANCE = new ServerNode();

    private String serviceId;
    private String voteFor;
    private int commitTerm;
    private int currentTerm;

    public static ServerNode getInstance() {
        return INSTANCE;
    }
}
