package com.raft.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/3.
 */
public class HeartBeatResp implements Serializable {
    private boolean isAccept = true;

    public boolean isAccept() {
        return isAccept;
    }

    public void setIsAccept(boolean isAccept) {
        this.isAccept = isAccept;
    }
}
