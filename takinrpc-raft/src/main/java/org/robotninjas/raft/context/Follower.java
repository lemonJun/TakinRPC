package org.robotninjas.raft.context;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Futures.immediateFailedFuture;
import static com.google.common.util.concurrent.Futures.immediateFuture;
import static com.google.common.util.concurrent.Futures.transform;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.robotninjas.raft.context.DefaultContext.State.CANDIDATE;
import static org.robotninjas.raft.context.DefaultContext.State.FOLLOWER;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.Nullable;

import org.robotninjas.raft.RaftException;
import org.robotninjas.raft.ReplicaInfo;
import org.robotninjas.raft.annotations.Timeout;
import org.robotninjas.raft.rpc.RaftProto.CommitOperation;
import org.robotninjas.raft.rpc.RaftProto.CommitOperationResponse;
import org.robotninjas.raft.rpc.RpcClient;
import org.robotninjas.raft.rpc.RpcClientFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

class Follower extends BaseState {

    private final ScheduledExecutorService scheduler;
    private final RpcClientFactory clientFactory;
    private final long timeout;
    private ScheduledFuture timer;

    @Inject
    Follower(ScheduledExecutorService scheduler, RpcClientFactory clientFactory, @Timeout long timeout) {
        this.scheduler = scheduler;
        this.clientFactory = clientFactory;
        this.timeout = timeout;
    }

    private ScheduledFuture scheduleTimer(final DefaultContext ctx) {
        return scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                ctx.transition(FOLLOWER, CANDIDATE);
            }
        }, timeout * 2, MILLISECONDS);
    }

    @Override
    public void init(DefaultContext ctx) {
        timer = scheduleTimer(ctx);
    }

    @Override
    protected void requestVoteReceived(DefaultContext ctx) {
        timer.cancel(false);
        timer = scheduleTimer(ctx);
    }

    @Override
    protected void heartbeatReceived(DefaultContext ctx) {
        timer.cancel(false);
        timer = scheduleTimer(ctx);
    }

    @Override
    public void destroy(DefaultContext ctx) {
        timer.cancel(false);
    }

    @Override
    public ListenableFuture<Boolean> commitOperation(DefaultContext ctx, CommitOperation operation) {
        Optional<ReplicaInfo> leader = ctx.getLeader();
        if (!leader.isPresent()) {
            return immediateFailedFuture(new RaftException("No Leader"));
        }
        RpcClient client = clientFactory.newClient(leader.get());
        return transform(client.commitOperation(operation), toFuture());
    }

    private static AsyncFunction<CommitOperationResponse, Boolean> toFuture() {
        return new AsyncFunction<CommitOperationResponse, Boolean>() {
            @Override
            public ListenableFuture<Boolean> apply(@Nullable CommitOperationResponse input) {
                checkNotNull(input);
                //noinspection ConstantConditions
                if (input.hasResponse()) {
                    return immediateFuture(input.hasResponse());
                } else {
                    return immediateFailedFuture(new RaftException(input.getError().getReason()));
                }
            }
        };
    }

}
