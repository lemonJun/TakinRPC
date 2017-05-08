package org.robotninjas.raft;

public interface LogListener {

    void onCommit(byte[] entry);

}
