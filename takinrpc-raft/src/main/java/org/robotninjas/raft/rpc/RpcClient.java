package org.robotninjas.raft.rpc;

import com.google.common.util.concurrent.ListenableFuture;

import static org.robotninjas.raft.rpc.RaftProto.*;

public interface RpcClient {

    ListenableFuture<RequestVoteResponse> requestVote(RequestVote request);

    ListenableFuture<AppendEntriesResponse> appendEntries(AppendEntries request);

    ListenableFuture<CommitOperationResponse> commitOperation(CommitOperation request);

}
