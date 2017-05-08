package org.robotninjas.raft.context;

import static com.google.common.util.concurrent.MoreExecutors.getExitingScheduledExecutorService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.robotninjas.raft.annotations.Timeout;

import com.google.inject.PrivateModule;

public class ContextModule extends PrivateModule {

    private final long timeout;
    private final int numSchedulerThreads;

    public ContextModule(long timeout, int numSchedulerThreads) {
        this.timeout = timeout;
        this.numSchedulerThreads = numSchedulerThreads;
    }

    @Override
    protected void configure() {

        ScheduledExecutorService scheduler = getExitingScheduledExecutorService(new ScheduledThreadPoolExecutor(numSchedulerThreads));

        bind(ScheduledExecutorService.class).toInstance(scheduler);

        //        install(new FactoryModuleBuilder().build(StateFactory.class));

        bind(RaftContext.class).to(DefaultContext.class).asEagerSingleton();
        expose(RaftContext.class);

        //        install(new FactoryModuleBuilder().build(UpdateManagerFactory.class));

        bind(Long.class).annotatedWith(Timeout.class).toInstance(timeout);
        bind(Candidate.class);
        bind(Follower.class);
        bind(Leader.class);

    }

}
