package org.robotninjas.raft.context;

import com.google.common.util.concurrent.ListenableFuture;

import static org.robotninjas.raft.rpc.RaftProto.*;

public interface RaftContext {

    RequestVoteResponse requestVote(RequestVote request);

    AppendEntriesResponse appendEntries(AppendEntries request);

    ListenableFuture<Boolean> commitOperation(CommitOperation operation);

}
