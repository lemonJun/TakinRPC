package org.robotninjas.raft.log;

import com.google.common.collect.ImmutableList;
import org.robotninjas.raft.rpc.RaftProto;

public class GetEntriesResult {

    private final long prevEntryTerm;
    private final long prevEntryIndex;
    private final ImmutableList<RaftProto.Entry> entries;

    GetEntriesResult(long prevEntryTerm, long prevEntryIndex, ImmutableList<RaftProto.Entry> entries) {
        this.prevEntryTerm = prevEntryTerm;
        this.prevEntryIndex = prevEntryIndex;
        this.entries = entries;
    }

    public long getPrevEntryTerm() {
        return prevEntryTerm;
    }

    public long getPrevEntryIndex() {
        return prevEntryIndex;
    }

    public ImmutableList<RaftProto.Entry> getEntries() {
        return entries;
    }

    public long getNextIndex() {
        return prevEntryIndex + 1 + entries.size();
    }

}
