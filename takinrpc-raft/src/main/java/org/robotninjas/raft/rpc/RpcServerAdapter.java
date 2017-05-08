package org.robotninjas.raft.rpc;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import org.robotninjas.raft.context.RaftContext;

import static org.robotninjas.raft.rpc.RaftProto.*;
import static org.robotninjas.raft.rpc.RaftProto.CommitOperationResponse.Response;
import static org.robotninjas.raft.rpc.RaftProto.CommitOperationResponse.Type.RESPONSE;

public class RpcServerAdapter implements RaftProto.RaftService.Interface {

    private final RaftContext context;

    public RpcServerAdapter(RaftContext context) {
        this.context = context;
    }

    @Override
    public void requestVote(RpcController controller, RequestVote request, RpcCallback<RequestVoteResponse> done) {
        try {
            done.run(context.requestVote(request));
        } catch (Exception e) {
            controller.setFailed(e.getMessage());
            done.run(null);
        }
    }

    @Override
    public void appendEntries(RpcController controller, AppendEntries request, RpcCallback<AppendEntriesResponse> done) {
        try {
            done.run(context.appendEntries(request));
        } catch (Exception e) {
            controller.setFailed(e.getMessage());
            done.run(null);
        }
    }

    @Override
    public void commitOperation(final RpcController controller, CommitOperation request, final RpcCallback<CommitOperationResponse> done) {
        ListenableFuture<Boolean> result = context.commitOperation(request);
        Futures.addCallback(result, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                done.run(CommitOperationResponse.newBuilder().setType(RESPONSE).setResponse(Response.newBuilder()).build());
            }

            @Override
            public void onFailure(Throwable t) {
                controller.setFailed(t.getMessage());
                done.run(null);
            }
        });

    }
}
