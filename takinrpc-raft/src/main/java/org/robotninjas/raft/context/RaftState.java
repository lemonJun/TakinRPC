package org.robotninjas.raft.context;

import com.google.common.util.concurrent.ListenableFuture;

import static org.robotninjas.raft.rpc.RaftProto.*;

interface RaftState {

    void init(DefaultContext ctx);

    void destroy(DefaultContext ctx);

    RequestVoteResponse requestVote(DefaultContext ctx, RequestVote request);

    AppendEntriesResponse appendEntries(DefaultContext ctx, AppendEntries request);

    ListenableFuture<Boolean> commitOperation(DefaultContext ctx, CommitOperation operation);

}
