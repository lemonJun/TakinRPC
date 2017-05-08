package org.robotninjas.raft.context;

import org.robotninjas.raft.RaftException;

public class NoLeaderException extends RaftException {

    public NoLeaderException() {
        super("Election in progress");
    }

    public NoLeaderException(Throwable throwable) {
        super("Election in progress", throwable);
    }

}
