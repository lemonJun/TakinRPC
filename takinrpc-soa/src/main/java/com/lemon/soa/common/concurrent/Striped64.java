package com.lemon.soa.common.concurrent;

import java.util.Random;

@SuppressWarnings("restriction")
abstract class Striped64 extends Number {

    private static final long serialVersionUID = -3403386352761423917L;

    /**
     * Padded variant of AtomicLong supporting only raw accesses plus CAS.
     * The value field is placed between pads, hoping that the JVM doesn't
     * reorder them.
     *
     * JVM intrinsics note: It would be possible to use a release-only
     * form of CAS here, if it were provided.
     */
    static final class Cell {
        volatile long p0, p1, p2, p3, p4, p5, p6;
        volatile long value;
        volatile long q0, q1, q2, q3, q4, q5, q6;

        Cell(long x) {
            value = x;
        }

        final boolean cas(long cmp, long val) {
            return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
        }

        // Unsafe mechanics
        private static final sun.misc.Unsafe UNSAFE;
        private static final long valueOffset;
        static {
            try {
                UNSAFE = getUnsafe();
                Class<?> ak = Cell.class;
                valueOffset = UNSAFE.objectFieldOffset(ak.getDeclaredField("value"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }

    }

    /**
     * Holder for the thread-local hash code. The code is initially
     * random, but may be set to a different value upon collisions.
     */
    static final class HashCode {
        static final Random rng = new Random();
        int code;

        HashCode() {
            int h = rng.nextInt(); // Avoid zero to allow xorShift rehash
            code = (h == 0) ? 1 : h;
        }
    }

    /**
     * The corresponding ThreadLocal class
     */
    static final class ThreadHashCode extends ThreadLocal<HashCode> {
        public HashCode initialValue() {
            return new HashCode();
        }
    }

    /**
     * Static per-thread hash codes. Shared across all instances to
     * reduce ThreadLocal pollution and because adjustments due to
     * collisions in one table are likely to be appropriate for
     * others.
     */
    static final ThreadHashCode threadHashCode = new ThreadHashCode();

    /** Number of CPUS, to place bound on table size */
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    /**
     * Table of cells. When non-null, size is a power of 2.
     */
    transient volatile Cell[] cells;

    /**
     * Base value, used mainly when there is no contention, but also as
     * a fallback during table initialization races. Updated via CAS.
     */
    transient volatile long base;

    /**
     * Spinlock (locked via CAS) used when resizing and/or creating Cells.
     */
    transient volatile int busy;

    /**
     * Package-private default constructor
     */
    Striped64() {
    }

    /**
     * CASes the base field.
     */
    final boolean casBase(long cmp, long val) {
        return UNSAFE.compareAndSwapLong(this, baseOffset, cmp, val);
    }

    /**
     * CASes the busy field from 0 to 1 to acquire lock.
     */
    final boolean casBusy() {
        return UNSAFE.compareAndSwapInt(this, busyOffset, 0, 1);
    }

    /**
     * Computes the function of current and new value. Subclasses
     * should open-code this update function for most uses, but the
     * virtualized form is needed within retryUpdate.
     *
     * @param currentValue the current value (of either base or a cell)
     * @param newValue the argument from a user update call
     * @return result of the update function
     */
    abstract long fn(long currentValue, long newValue);

    /**
     * Handles cases of updates involving initialization, resizing,
     * creating new Cells, and/or contention. See above for
     * explanation. This method suffers the usual non-modularity
     * problems of optimistic retry code, relying on rechecked sets of
     * reads.
     *
     * @param x the value
     * @param hc the hash code holder
     * @param wasUncontended false if CAS failed before call
     */
    final void retryUpdate(long x, HashCode hc, boolean wasUncontended) {
        int h = hc.code;
        boolean collide = false; // True if last slot nonempty
        for (;;) {
            Cell[] as;
            Cell a;
            int n;
            long v;
            if ((as = cells) != null && (n = as.length) > 0) {
                if ((a = as[(n - 1) & h]) == null) {
                    if (busy == 0) { // Try to attach new Cell
                        Cell r = new Cell(x); // Optimistically create
                        if (busy == 0 && casBusy()) {
                            boolean created = false;
                            try { // Recheck under lock
                                Cell[] rs;
                                int m, j;
                                if ((rs = cells) != null && (m = rs.length) > 0 && rs[j = (m - 1) & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                busy = 0;
                            }
                            if (created)
                                break;
                            continue; // Slot is now non-empty
                        }
                    }
                    collide = false;
                } else if (!wasUncontended) // CAS already known to fail
                    wasUncontended = true; // Continue after rehash
                else if (a.cas(v = a.value, fn(v, x)))
                    break;
                else if (n >= NCPU || cells != as)
                    collide = false; // At max size or stale
                else if (!collide)
                    collide = true;
                else if (busy == 0 && casBusy()) {
                    try {
                        if (cells == as) { // Expand table unless stale
                            Cell[] rs = new Cell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            cells = rs;
                        }
                    } finally {
                        busy = 0;
                    }
                    collide = false;
                    continue; // Retry with expanded table
                }
                h ^= h << 13; // Rehash
                h ^= h >>> 17;
                h ^= h << 5;
            } else if (busy == 0 && cells == as && casBusy()) {
                boolean init = false;
                try { // Initialize table
                    if (cells == as) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(x);
                        cells = rs;
                        init = true;
                    }
                } finally {
                    busy = 0;
                }
                if (init)
                    break;
            } else if (casBase(v = base, fn(v, x)))
                break; // Fall back on using base
        }
        hc.code = h; // Record index for next time
    }

    /**
     * Sets base and all cells to the given value.
     */
    final void internalReset(long initialValue) {
        Cell[] as = cells;
        base = initialValue;
        if (as != null) {
            int n = as.length;
            for (int i = 0; i < n; ++i) {
                Cell a = as[i];
                if (a != null)
                    a.value = initialValue;
            }
        }
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long baseOffset;
    private static final long busyOffset;
    static {
        try {
            UNSAFE = getUnsafe();
            Class<?> sk = Striped64.class;
            baseOffset = UNSAFE.objectFieldOffset(sk.getDeclaredField("base"));
            busyOffset = UNSAFE.objectFieldOffset(sk.getDeclaredField("busy"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    /**
     * Returns a sun.misc.Unsafe.  Suitable for use in a 3rd party package.
     * Replace with a simple call to Unsafe.getUnsafe when integrating
     * into a jdk.
     *
     * @return a sun.misc.Unsafe
     */
    private static sun.misc.Unsafe getUnsafe() {
        try {
            return sun.misc.Unsafe.getUnsafe();
        } catch (SecurityException se) {
            try {
                return java.security.AccessController.doPrivileged(new java.security.PrivilegedExceptionAction<sun.misc.Unsafe>() {
                    public sun.misc.Unsafe run() throws Exception {
                        java.lang.reflect.Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                        f.setAccessible(true);
                        return (sun.misc.Unsafe) f.get(null);
                    }
                });
            } catch (java.security.PrivilegedActionException e) {
                throw new RuntimeException("Could not initialize intrinsics", e.getCause());
            }
        }
    }

}