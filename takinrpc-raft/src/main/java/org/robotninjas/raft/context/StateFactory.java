package org.robotninjas.raft.context;

interface StateFactory {

    Candidate newCandidateState();

    Follower newFollowerState();

    Leader newLeaderState();

}
