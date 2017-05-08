package org.robotninjas.raft.rpc;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.netty.NettyRpcChannel;
import org.apache.commons.pool.KeyedObjectPool;
import org.robotninjas.raft.ReplicaInfo;

import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static org.robotninjas.raft.rpc.RaftProto.*;

class RemoteRpcClient implements RpcClient {

    private final KeyedObjectPool<ReplicaInfo, NettyRpcChannel> channelCache;
    private final ReplicaInfo info;

    RemoteRpcClient(KeyedObjectPool<ReplicaInfo, NettyRpcChannel> channelCache, ReplicaInfo info) {
        this.channelCache = channelCache;
        this.info = info;
    }

    @Override
    public ListenableFuture<RequestVoteResponse> requestVote(RequestVote request) {
        NettyRpcChannel channel = null;
        try {
            channel = channelCache.borrowObject(info);
            RaftProto.RaftService.Stub stub = RaftProto.RaftService.newStub(channel);
            RpcController controller = channel.newRpcController();
            ResponseHandler<RequestVoteResponse> response = new ResponseHandler<RequestVoteResponse>(controller);
            stub.requestVote(controller, request, response);
            return response;
        } catch (Exception e) {
            return immediateFailedFuture(e);
        } finally {
            try {
                if (channel != null) {
                    channelCache.returnObject(info, channel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ListenableFuture<AppendEntriesResponse> appendEntries(AppendEntries request) {
        NettyRpcChannel channel = null;
        try {
            channel = channelCache.borrowObject(info);
            RaftProto.RaftService.Stub stub = RaftProto.RaftService.newStub(channel);
            RpcController controller = channel.newRpcController();
            ResponseHandler<AppendEntriesResponse> response = new ResponseHandler<AppendEntriesResponse>(controller);
            stub.appendEntries(controller, request, response);
            return response;
        } catch (Exception e) {
            return immediateFailedFuture(e);
        } finally {
            try {
                if (channel != null) {
                    channelCache.returnObject(info, channel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ListenableFuture<CommitOperationResponse> commitOperation(CommitOperation request) {
        NettyRpcChannel channel = null;
        try {
            channel = channelCache.borrowObject(info);
            RaftProto.RaftService.Stub stub = RaftProto.RaftService.newStub(channel);
            RpcController controller = channel.newRpcController();
            ResponseHandler<CommitOperationResponse> response = new ResponseHandler<CommitOperationResponse>(controller);
            stub.commitOperation(controller, request, response);
            return response;
        } catch (Exception e) {
            return immediateFailedFuture(e);
        } finally {
            try {
                if (channel != null) {
                    channelCache.returnObject(info, channel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
