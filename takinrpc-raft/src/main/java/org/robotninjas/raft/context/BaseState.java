package org.robotninjas.raft.context;

import com.google.common.util.concurrent.ListenableFuture;
import org.robotninjas.raft.ReplicaInfo;
import org.robotninjas.raft.log.RaftLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.robotninjas.raft.context.DefaultContext.State.*;
import static org.robotninjas.raft.rpc.RaftProto.*;

abstract class BaseState implements RaftState {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void init(DefaultContext ctx) {
    }

    @Override
    public void destroy(DefaultContext ctx) {
    }

    private boolean vote(DefaultContext ctx, RequestVote request, ReplicaInfo candidate) {

        if (ctx.getVote().isPresent()) {
            return false;
        }

        if (request.getLastLogTerm() < ctx.getLastLogTerm()) {
            return false;
        }

        if (request.getLastLogTerm() > ctx.getLastLogTerm()) {
            ctx.setVote(candidate);
            return true;
        }

        if (request.getLastLogIndex() >= ctx.getLastLogIndex()) {
            ctx.setVote(candidate);
            return true;
        }

        return false;
    }

    @Override
    public abstract ListenableFuture<Boolean> commitOperation(DefaultContext ctx, CommitOperation operation);

    @Override
    public RequestVoteResponse requestVote(DefaultContext ctx, RequestVote request) {

        ReplicaInfo candidate = ReplicaInfo.fromString(request.getCandidateId());

        long term = request.getTerm();
        long currentTerm = ctx.getTerm();

        boolean voteGranted = false;
        if (term >= currentTerm) {
            if (term > currentTerm) {
                stepDown(ctx);
                ctx.setTerm(request.getTerm());
            }
            voteGranted = vote(ctx, request, candidate);
            requestVoteReceived(ctx);
        }

        RequestVoteResponse response = RequestVoteResponse.newBuilder().setVoteGranted(voteGranted).setTerm(term).build();

        return response;
    }

    @Override
    public AppendEntriesResponse appendEntries(DefaultContext ctx, AppendEntries request) {

        ReplicaInfo leader = ReplicaInfo.fromString(request.getLeaderId());
        long commitIndex = request.getCommitIndex();

        long term = request.getTerm();
        long currentTerm = ctx.getTerm();

        boolean success = false;
        if (term >= currentTerm) {
            if (term > currentTerm) {
                ctx.setTerm(term);
            }
            stepDown(ctx);
            ctx.setLeader(leader);
            RaftLog log = ctx.getLog();
            success = log.append(request);
            if (success) {
                ctx.setCommitIndex(commitIndex);
            }
            heartbeatReceived(ctx);
        }

        AppendEntriesResponse response = AppendEntriesResponse.newBuilder().setTerm(term).setSuccess(success).build();

        return response;
    }

    private void stepDown(DefaultContext ctx) {
        if (ctx.isCandidate() || ctx.isLeader()) {
            ctx.transition(ctx.isCandidate() ? CANDIDATE : LEADER, FOLLOWER);
        }
    }

    protected void heartbeatReceived(DefaultContext ctx) {

    }

    protected void requestVoteReceived(DefaultContext ctx) {

    }

}
