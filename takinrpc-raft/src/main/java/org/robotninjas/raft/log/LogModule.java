package org.robotninjas.raft.log;

import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import journal.io.api.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.google.common.base.Throwables.propagate;

public class LogModule extends PrivateModule {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final File stateDirectory;

    public LogModule(File stateDirectory) {
        this.stateDirectory = stateDirectory;
    }

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    @Exposed
    RaftLog getLog(Journal journal) {
        RaftLog log = new RaftLog(journal);
        log.init();
        return log;
    }

    @Provides
    @Singleton
    Journal getJournal() {
        try {
            Journal journal = new Journal();
            logger.info("Journaling entries to {}", stateDirectory);
            journal.setDirectory(stateDirectory);
            journal.open();
            return journal;
        } catch (Exception e) {
            e.printStackTrace();
            throw propagate(e);
        }
    }

}
