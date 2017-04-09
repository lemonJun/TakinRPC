package com.raft;

import java.util.concurrent.atomic.AtomicInteger;

import com.raft.domain.Role;

/**
 * Created by Administrator on 2016/10/28.
 */
public class ServerNode {
    private final static AtomicInteger votes = new AtomicInteger(0); //当前的票数
    private final static AtomicInteger currentTerm = new AtomicInteger(0);

    private volatile static Role role = Role.FOLLOWER; //当前服务器的角色

    private static volatile long lastHeartBeatMills = 0;

    private static AtomicInteger servers = new AtomicInteger(0);

    public static final long DEFAULT_TIMEOUT = 1000;

    /**
     * 自增票数
     * @return
     */
    public static int voteIncr() {
        return votes.incrementAndGet();
    }

    public static int getVotes() {
        return votes.get();
    }

    public static int serverIncr() {
        return servers.incrementAndGet();
    }

    public static int getServers() {
        return servers.get();
    }

    /**
     * 自增term
     * @return
     */
    public static int termIncr() {
        return currentTerm.incrementAndGet();
    }

    public static int getCurrentTerm() {
        return currentTerm.get();
    }

    /**
     * 票数清零
     */
    public static void resetVotes() {
        votes.set(0);
    }

    public static void setLastHeartBeatMills(long lastHeartBeatMills) {
        ServerNode.lastHeartBeatMills = lastHeartBeatMills;
    }

    public static long getLastHeartBeatMills() {
        return lastHeartBeatMills;
    }

    public static Role getRole() {
        return role;
    }

    public static void setRole(Role role) {
        ServerNode.role = role;
    }
}
