package org.robotninjas.raft.context;

import com.google.common.util.concurrent.ListenableFuture;

import static org.robotninjas.raft.rpc.RaftProto.*;

class Start implements RaftState {
    @Override
    public void init(DefaultContext ctx) {

    }

    @Override
    public void destroy(DefaultContext ctx) {

    }

    @Override
    public RequestVoteResponse requestVote(DefaultContext ctx, RequestVote request) {
        return null;
    }

    @Override
    public AppendEntriesResponse appendEntries(DefaultContext ctx, AppendEntries request) {
        return null;
    }

    @Override
    public ListenableFuture<Boolean> commitOperation(DefaultContext ctx, CommitOperation operation) {
        return null;
    }
}
