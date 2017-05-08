package org.robotninjas.raft.context;

import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import org.robotninjas.raft.ReplicaInfo;
import org.robotninjas.raft.annotations.Timeout;
import org.robotninjas.raft.rpc.RpcClient;
import org.robotninjas.raft.rpc.RpcClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.util.concurrent.Futures.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.robotninjas.raft.context.DefaultContext.State.CANDIDATE;
import static org.robotninjas.raft.context.DefaultContext.State.LEADER;
import static org.robotninjas.raft.rpc.RaftProto.*;

class Candidate extends BaseState {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Random rand = new Random(System.currentTimeMillis());
    private final ScheduledExecutorService scheduler;
    private final RpcClientFactory clientFactory;
    private final long timeout;
    private ScheduledFuture timer;

    @Inject
    Candidate(ScheduledExecutorService scheduler, RpcClientFactory clientFactory, @Timeout long timeout) {
        this.scheduler = scheduler;
        this.clientFactory = clientFactory;
        this.timeout = timeout;
    }

    @Override
    public void init(final DefaultContext ctx) {

        // Initialize the election
        ctx.clearLeader();
        ctx.setTerm(ctx.getTerm() + 1);
        ctx.setVote(ctx.getInfo());

        ListenableFuture<Integer> votes = collectVotes(ctx);

        // Set timer
        long to = timeout + (rand.nextLong() % timeout);
        logger.debug("timeout {}", to);
        timer = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                ctx.transition(CANDIDATE, CANDIDATE);
            }
        }, to, MILLISECONDS);

        addCallback(votes, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer votes) {
                double majority = (ctx.getNumServers() / 2.0) + 1.0;
                logger.debug("votes {}, majority {}", votes, majority);
                if (votes >= majority) {
                    timer.cancel(false);
                    ctx.transition(CANDIDATE, LEADER);
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }

        });

    }

    @Override
    public void destroy(DefaultContext ctx) {
        timer.cancel(false);
    }

    private List<ListenableFuture<RequestVoteResponse>> issueRequests(DefaultContext ctx) {

        RequestVote request = RequestVote.newBuilder().setCandidateId(ctx.getInfo().toString()).setLastLogIndex(ctx.getLastLogIndex()).setLastLogTerm(ctx.getLastLogTerm()).setTerm(ctx.getTerm()).build();

        List<ListenableFuture<RequestVoteResponse>> requests = newArrayList();
        for (ReplicaInfo server : ctx.getServers()) {
            RpcClient client = clientFactory.newClient(server);
            requests.add(client.requestVote(request));
        }

        return requests;

    }

    private Function<List<RequestVoteResponse>, Integer> countVotes() {
        return new Function<List<RequestVoteResponse>, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable List<RequestVoteResponse> input) {
                checkNotNull(input);
                int votes = 1;
                //noinspection ConstantConditions
                for (RequestVoteResponse response : input) {
                    votes += (response != null && response.getVoteGranted() ? 1 : 0);
                }
                return votes;
            }
        };
    }

    private ListenableFuture<Integer> collectVotes(final DefaultContext ctx) {

        List<ListenableFuture<RequestVoteResponse>> requests = issueRequests(ctx);
        ListenableFuture<List<RequestVoteResponse>> responses = successfulAsList(requests);
        return transform(responses, countVotes());

    }

    @Override
    public ListenableFuture<Boolean> commitOperation(DefaultContext ctx, CommitOperation operation) {
        return Futures.immediateFailedFuture(new NoLeaderException());
    }

}
