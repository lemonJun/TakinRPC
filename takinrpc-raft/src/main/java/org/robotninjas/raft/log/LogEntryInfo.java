package org.robotninjas.raft.log;

public class LogEntryInfo {

    private final long index;
    private final long term;

    public LogEntryInfo(long index, long term) {
        this.index = index;
        this.term = term;
    }

    public long getIndex() {
        return index;
    }

    public long getTerm() {
        return term;
    }

}
