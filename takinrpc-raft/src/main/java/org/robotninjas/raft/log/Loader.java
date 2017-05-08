package org.robotninjas.raft.log;

import com.google.common.cache.CacheLoader;
import journal.io.api.Journal;
import journal.io.api.Location;
import org.robotninjas.raft.rpc.RaftProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

class Loader extends CacheLoader<Long, RaftProto.Entry> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<Long, EntryMeta> index;
    private final Journal journal;

    Loader(Map<Long, EntryMeta> index, Journal journal) {
        this.index = index;
        this.journal = journal;
    }

    @Override
    public RaftProto.Entry load(Long key) throws Exception {
        try {
            logger.debug("Loading {}", key);
            EntryMeta meta = index.get(key);
            Location loc = meta.getLocation();
            byte[] data = journal.read(loc, Journal.ReadType.ASYNC);
            return RaftProto.Entry.parseFrom(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
