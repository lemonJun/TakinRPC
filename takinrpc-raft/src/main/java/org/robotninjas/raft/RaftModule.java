package org.robotninjas.raft;

import com.google.inject.PrivateModule;
import com.google.inject.TypeLiteral;
import org.robotninjas.raft.annotations.ClusterReplicas;
import org.robotninjas.raft.annotations.LocalReplica;
import org.robotninjas.raft.context.ContextModule;
import org.robotninjas.raft.log.LogModule;
import org.robotninjas.raft.rpc.RpcModule;

import java.io.File;
import java.util.List;

public class RaftModule extends PrivateModule {

    private static final int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors();

    private final ReplicaInfo info;
    private final List<ReplicaInfo> servers;
    private final int timeout;
    private final File stateDirectory;

    public RaftModule(ReplicaInfo info, List<ReplicaInfo> servers, int timeout, File stateDirectory) {
        this.info = info;
        this.servers = servers;
        this.timeout = timeout;
        this.stateDirectory = stateDirectory;
    }

    @Override
    protected void configure() {
        install(new RpcModule(DEFAULT_THREADS));
        install(new ContextModule(timeout, DEFAULT_THREADS));
        install(new LogModule(stateDirectory));
        bind(ReplicaInfo.class).annotatedWith(LocalReplica.class).toInstance(info);
        bind(new TypeLiteral<List<ReplicaInfo>>() {
        }).annotatedWith(ClusterReplicas.class).toInstance(servers);
        bind(LogService.class);
        expose(LogService.class);
    }

}
