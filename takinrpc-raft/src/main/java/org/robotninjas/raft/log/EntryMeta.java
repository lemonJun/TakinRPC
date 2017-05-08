package org.robotninjas.raft.log;

import journal.io.api.Location;

class EntryMeta {

    private final long index;
    private final long term;
    private final Location location;

    EntryMeta(long index, long term, Location location) {
        this.index = index;
        this.term = term;
        this.location = location;
    }

    long getIndex() {
        return index;
    }

    long getTerm() {
        return term;
    }

    Location getLocation() {
        return location;
    }

}
