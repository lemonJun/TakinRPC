package org.robotninjas.raft.context;

import org.robotninjas.raft.ReplicaInfo;

interface UpdateManagerFactory {

    UpdateManager newManager(DefaultContext ctx, ReplicaInfo info, long nextIndex);

}
