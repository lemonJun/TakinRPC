package org.robotninjas.raft.context;

import java.util.concurrent.atomic.AtomicLong;

import org.robotninjas.raft.ReplicaInfo;
import org.robotninjas.raft.log.GetEntriesResult;
import org.robotninjas.raft.log.RaftLog;
import org.robotninjas.raft.rpc.RaftProto;
import org.robotninjas.raft.rpc.RpcClient;
import org.robotninjas.raft.rpc.RpcClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.inject.Inject;

class UpdateManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DefaultContext ctx;
    private final ReplicaInfo replica;
    private final RpcClientFactory clientFactory;
    private final RaftLog log;
    private final AtomicLong nextIndex;
    private volatile boolean running = false;
    private volatile boolean updateRequested = false;
    private volatile SettableFuture<RaftProto.AppendEntriesResponse> response = SettableFuture.create();

    @Inject
    public UpdateManager(ReplicaInfo replica, long nextIndex, RpcClientFactory clientFactory, RaftLog log, DefaultContext ctx) {
        this.replica = replica;
        this.clientFactory = clientFactory;
        this.ctx = ctx;
        this.log = log;
        this.nextIndex = new AtomicLong(nextIndex);
    }

    private synchronized void sendUpdate() {

        if (!updateRequested) {
            running = false;
            return;
        }

        updateRequested = false;

        GetEntriesResult entries = log.getEntries(nextIndex.get());

        final RaftProto.AppendEntries request = RaftProto.AppendEntries.newBuilder().setLeaderId(ctx.getInfo().toString()).setCommitIndex(ctx.getCommitIndex()).setTerm(ctx.getTerm()).setPrevLogIndex(entries.getPrevEntryIndex()).setPrevLogTerm(entries.getPrevEntryTerm()).addAllEntries(entries.getEntries()).build();

        RpcClient client = clientFactory.newClient(replica);
        ListenableFuture<RaftProto.AppendEntriesResponse> result = client.appendEntries(request);

        Futures.addCallback(result, new FutureCallback<RaftProto.AppendEntriesResponse>() {

            @Override
            public void onSuccess(RaftProto.AppendEntriesResponse result) {
                updateNextIndex(request, result);
                sendUpdate();
            }

            @Override
            public void onFailure(Throwable t) {
                requestFailed(request, t);
                sendUpdate();
            }

        });

    }

    private synchronized void requestFailed(RaftProto.AppendEntries request, Throwable t) {
        response.setException(t);
        response = SettableFuture.create();
    }

    private synchronized void updateNextIndex(RaftProto.AppendEntries request, RaftProto.AppendEntriesResponse result) {
        long next = nextIndex.get();
        if (result.getSuccess()) {
            next += request.getEntriesCount();
            response.set(result);
            response = SettableFuture.create();
        } else {
            next--;
            updateRequested = true;
        }
        nextIndex.set(next);
    }

    public synchronized ListenableFuture<RaftProto.AppendEntriesResponse> fireUpdate() {
        updateRequested = true;
        if (!running) {
            running = true;
            sendUpdate();
        }
        return response;
    }

}
