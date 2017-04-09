package com.raft;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/11/1.
 */
public class TimerManager {
    private Timer timer;

    public TimerManager() {
        this.timer = new HashedWheelTimer();
    }

    public void asTime(TimerTask task, long timeout) {
        this.timer.newTimeout(task, timeout, TimeUnit.MILLISECONDS);
    }
}
