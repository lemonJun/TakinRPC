package com.raft;

import io.netty.util.TimerTask;

/**
 * Created by Administrator on 2016/11/1.
 */
public class TaskManager {
    private TimerTask heartBeatTask;
    private TimerTask requestVoteTask;

    public TimerTask getHeartBeatTask() {
        return heartBeatTask;
    }

    public void setHeartBeatTask(TimerTask heartBeatTask) {
        this.heartBeatTask = heartBeatTask;
    }

    public TimerTask getRequestVoteTask() {
        return requestVoteTask;
    }

    public void setRequestVoteTask(TimerTask requestVoteTask) {
        this.requestVoteTask = requestVoteTask;
    }
}
