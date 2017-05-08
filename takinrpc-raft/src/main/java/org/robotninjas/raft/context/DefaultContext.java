package org.robotninjas.raft.context;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import org.robotninjas.raft.ReplicaInfo;
import org.robotninjas.raft.annotations.ClusterReplicas;
import org.robotninjas.raft.annotations.LocalReplica;
import org.robotninjas.raft.log.RaftLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.robotninjas.raft.context.DefaultContext.State.*;
import static org.robotninjas.raft.rpc.RaftProto.*;

@ThreadSafe
class DefaultContext implements RaftContext {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    static enum State {
        START, FOLLOWER, CANDIDATE, LEADER
    }

    private final StateFactory stateFactory;
    private final List<ReplicaInfo> servers = newArrayList();
    private final RaftLog log;
    private final ReplicaInfo info;
    private volatile State state = START;
    private volatile RaftState delegate = new Start();
    private volatile long term = 0;
    private volatile Optional<ReplicaInfo> votedFor = Optional.absent();
    private volatile Optional<ReplicaInfo> leader = Optional.absent();
    private volatile long commitIndex;

    @Inject
    public DefaultContext(StateFactory stateFactory, @LocalReplica ReplicaInfo info, @ClusterReplicas List<ReplicaInfo> servers, RaftLog log) {
        this.stateFactory = stateFactory;
        this.info = info;
        this.servers.addAll(servers);
        this.log = log;
        transition(START, FOLLOWER);
    }

    public synchronized boolean transition(State oldState, State newState) {

        if (!state.equals(oldState)) {
            logger.warn("Attempting to transition from {} to {} but currently in {}", oldState, newState, state);
            return false;
        }

        logger.info("Transitioning from {} to {}", state, newState);
        delegate.destroy(this);
        switch (newState) {
            case CANDIDATE:
                delegate = stateFactory.newCandidateState();
                break;
            case FOLLOWER:
                delegate = stateFactory.newFollowerState();
                break;
            case LEADER:
                delegate = stateFactory.newLeaderState();
                break;
            default:
                break;
        }
        state = newState;
        delegate.init(this);
        return true;
    }

    public void setVote(ReplicaInfo candidate) {
        logger.debug("Voting for {}", candidate);
        this.votedFor = Optional.of(candidate);
    }

    public Optional<ReplicaInfo> getVote() {
        return votedFor;
    }

    public void clearVote() {
        this.votedFor = Optional.absent();
    }

    public void setTerm(long term) {
        logger.debug("New term {}", term);
        clearVote();
        clearLeader();
        this.term = term;
    }

    public Optional<ReplicaInfo> getLeader() {
        return leader;
    }

    public ReplicaInfo getInfo() {
        return info;
    }

    public int getNumServers() {
        return servers.size();
    }

    public List<ReplicaInfo> getServers() {
        return servers;
    }

    public long getTerm() {
        return term;
    }

    public long getLastLogIndex() {
        return log.getLastLogIndex();
    }

    public long getLastLogTerm() {
        return log.getLastLogTerm();
    }

    public RaftLog getLog() {
        return log;
    }

    public void setLeader(ReplicaInfo leader) {
        this.leader = Optional.fromNullable(leader);
    }

    public void clearLeader() {
        this.leader = Optional.absent();
    }

    public void setCommitIndex(long commitIndex) {
        logger.debug("Committing {}", commitIndex);
        this.commitIndex = commitIndex;
    }

    public long getCommitIndex() {
        return commitIndex;
    }

    public boolean isLeader() {
        return state.equals(LEADER);
    }

    public boolean isCandidate() {
        return state.equals(CANDIDATE);
    }

    public boolean isFollower() {
        return state.equals(FOLLOWER);
    }

    @Override
    public synchronized RequestVoteResponse requestVote(RequestVote request) {
        return delegate.requestVote(this, request);
    }

    @Override
    public synchronized AppendEntriesResponse appendEntries(AppendEntries request) {
        return delegate.appendEntries(this, request);
    }

    @Override
    public synchronized ListenableFuture<Boolean> commitOperation(CommitOperation operation) {
        return delegate.commitOperation(this, operation);
    }

}
