package org.robotninjas.barge;

public class NotLeaderException extends RaftException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Replica leader;

    public NotLeaderException(Replica leader) {
        this.leader = leader;
    }

    public Replica getLeader() {
        return leader;
    }
}
