package org.robotninjas.raft.log;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import journal.io.api.Journal;
import journal.io.api.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.propagate;
import static journal.io.api.Journal.WriteType;
import static org.robotninjas.raft.rpc.RaftProto.*;

public class RaftLog {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final Journal journal;
    private final LoadingCache<Long, Entry> entries;
    private final SortedMap<Long, EntryMeta> index = new ConcurrentSkipListMap<Long, EntryMeta>();
    @GuardedBy("lock")
    private volatile long lastLogIndex = -1;

    @Inject
    RaftLog(Journal journal) {
        this.journal = journal;
        Loader loader = new Loader(index, journal);
        this.entries = CacheBuilder.newBuilder().maximumSize(1000).build(loader);
    }

    public void init() {
        writeLock.lock();
        try {
            Entry entry = Entry.newBuilder().setTerm(-1L).setCommand(ByteString.EMPTY).build();
            storeEntry(-1L, entry);
        } finally {
            writeLock.unlock();
        }
    }

    private void storeEntry(long index, Entry entry) {
        try {
            Location loc = journal.write(entry.toByteArray(), WriteType.SYNC);
            EntryMeta meta = new EntryMeta(index, entry.getTerm(), loc);
            this.index.put(index, meta);
            this.entries.put(index, entry);
        } catch (Exception e) {
            e.printStackTrace();
            throw propagate(e);
        }
    }

    public long append(CommitOperation operation, long term) {

        writeLock.lock();
        try {
            checkState(index.containsKey(lastLogIndex));
            checkState(!index.containsKey(lastLogIndex + 1));

            long index = ++lastLogIndex;

            logger.debug("leader append: index {}, term {}", index, term);

            Entry entry = Entry.newBuilder().setCommand(operation.getOp()).setTerm(term).build();

            storeEntry(index, entry);

            return index;

        } finally {
            writeLock.unlock();
        }

    }

    public boolean append(AppendEntries appendEntries) {

        writeLock.lock();
        try {
            long prevLogTerm = appendEntries.getPrevLogTerm();
            long prevLogIndex = appendEntries.getPrevLogIndex();

            EntryMeta previousEntry = index.get(prevLogIndex);
            if ((previousEntry == null) || (previousEntry.getTerm() != prevLogTerm)) {
                return false;
            }

            long index = lastLogIndex + 1;
            logger.debug("follower append: index {}, term {}", index, appendEntries.getTerm());

            this.index.tailMap(prevLogIndex + 1).clear();
            lastLogIndex = appendEntries.getPrevLogIndex();

            for (Entry entry : appendEntries.getEntriesList()) {
                storeEntry(++lastLogIndex, entry);
            }

            return true;
        } finally {
            writeLock.unlock();
        }

    }

    public GetEntriesResult getEntries(final long beginningIndex) {
        readLock.lock();
        try {
            checkArgument(beginningIndex >= 0, "index must be >= 0, actual value: %s", beginningIndex);
            Set<Long> indices = index.tailMap(beginningIndex).keySet();
            Iterable<Entry> values = Iterables.transform(indices, new Function<Long, Entry>() {
                @Nullable
                @Override
                public Entry apply(@Nullable Long input) {
                    try {
                        return entries.get(input);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
            long prevEntryIndex = beginningIndex - 1;
            Entry previousEntry = entries.get(prevEntryIndex);
            long prevEntryTerm = previousEntry.getTerm();
            return new GetEntriesResult(prevEntryTerm, prevEntryIndex, ImmutableList.copyOf(values));
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw propagate(e);
        } finally {
            readLock.unlock();
        }
    }

    public long getLastLogIndex() {
        readLock.lock();
        try {
            return lastLogIndex;
        } finally {
            readLock.unlock();
        }
    }

    public long getLastLogTerm() {
        readLock.lock();
        try {
            return index.get(getLastLogIndex()).getTerm();
        } finally {
            readLock.unlock();
        }
    }

    public LogEntryInfo getLastLogEntryInfo() {
        readLock.lock();
        try {
            long index = getLastLogIndex();
            EntryMeta entry = this.index.get(index);
            long term = entry.getTerm();
            return new LogEntryInfo(index, term);
        } finally {
            readLock.unlock();
        }
    }

}
