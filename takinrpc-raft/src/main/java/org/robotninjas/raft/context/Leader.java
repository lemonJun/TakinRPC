package org.robotninjas.raft.context;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import org.robotninjas.raft.ReplicaInfo;
import org.robotninjas.raft.annotations.Timeout;
import org.robotninjas.raft.log.RaftLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static com.google.common.util.concurrent.Futures.successfulAsList;
import static com.google.common.util.concurrent.Futures.transform;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.robotninjas.raft.rpc.RaftProto.AppendEntriesResponse;
import static org.robotninjas.raft.rpc.RaftProto.CommitOperation;

class Leader extends BaseState {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ScheduledFuture timer;
    private List<UpdateManager> managers;
    private final ScheduledExecutorService scheduler;
    private final UpdateManagerFactory managerFactory;
    private final long timeout;
    private final RaftLog log;

    @Inject
    Leader(ScheduledExecutorService scheduler, UpdateManagerFactory managerFactory, RaftLog log, @Timeout long timeout) {
        this.scheduler = scheduler;
        this.managerFactory = managerFactory;
        this.timeout = timeout;
        this.log = log;
    }

    private List<ListenableFuture<AppendEntriesResponse>> sendUpdates() {
        List<ListenableFuture<AppendEntriesResponse>> responses = Lists.newArrayListWithCapacity(managers.size());
        for (UpdateManager manager : managers) {
            responses.add(manager.fireUpdate());
        }
        return responses;
    }

    @Override
    public void init(final DefaultContext ctx) {
        ctx.setLeader(ctx.getInfo());
        final long nextIndexValue = ctx.getLastLogIndex() + 1;
        managers = Lists.newCopyOnWriteArrayList(Iterables.transform(ctx.getServers(), new Function<ReplicaInfo, UpdateManager>() {
            public UpdateManager apply(ReplicaInfo replica) {
                return managerFactory.newManager(ctx, replica, nextIndexValue);
            }
        }));
        sendUpdates();
        HeartbeatTask heartbeat = new HeartbeatTask(ctx);
        timer = scheduler.scheduleAtFixedRate(heartbeat, timeout, timeout, MILLISECONDS);
    }

    @Override
    public void destroy(DefaultContext ctx) {
        timer.cancel(false);
    }

    @Override
    public ListenableFuture<Boolean> commitOperation(final DefaultContext ctx, CommitOperation operation) {
        final long commitIndex = log.append(operation, ctx.getTerm());
        List<ListenableFuture<AppendEntriesResponse>> responses = sendUpdates();

        return transform(successfulAsList(responses), new Function<List<AppendEntriesResponse>, Boolean>() {
            public Boolean apply(List<AppendEntriesResponse> input) {
                double half = ctx.getNumServers() / 2.0;
                int successful = 1;
                for (AppendEntriesResponse response : input) {
                    successful += (response != null && response.getSuccess()) ? 1 : 0;
                    if (successful > half) {
                        ctx.setCommitIndex(commitIndex);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private class HeartbeatTask implements Runnable {

        private final Logger logger = LoggerFactory.getLogger(getClass());
        private final DefaultContext ctx;

        private HeartbeatTask(DefaultContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            sendUpdates();
        }
    }

}
