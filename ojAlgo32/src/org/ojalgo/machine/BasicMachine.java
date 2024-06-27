package org.ojalgo.machine;

/**
 * How much memory, and how many threads share that memory.
 * Used to describe either total system resources (system RAM and
 * total number of threads handled by the processors) or a cache
 * (processor's L1, L2 or L3 cache).
 *
 * @author apete
 */
public class BasicMachine {

    private static final String BYTES = "bytes/";
    private static final String GIGA = "GB/";
    private static final String KILO = "kB/";
    private static final String MEGA = "MB/";
    private static final String THREAD = "thread";
    private static final String THREADS = "threads";

    public final long memory;
    public final int threads;

    public BasicMachine(final long aMemory, final int aThreads) {

        super();

        memory = aMemory;
        threads = aThreads;
    }

    @SuppressWarnings("unused")
    private BasicMachine() {
        this(AbstractMachine.LONG_0000, AbstractMachine.INT_0000);
    }

    public boolean equals(final Object obj) {
        if (this == obj) {
            return AbstractMachine.BOOLEAN_TRUE;
        }
        if (obj == null) {
            return AbstractMachine.BOOLEAN_FALSE;
        }
        if (!(obj instanceof BasicMachine)) {
            return AbstractMachine.BOOLEAN_FALSE;
        }
        final BasicMachine other = (BasicMachine) obj;
        if (memory != other.memory) {
            return AbstractMachine.BOOLEAN_FALSE;
        }
        if (threads != other.threads) {
            return AbstractMachine.BOOLEAN_FALSE;
        }
        return AbstractMachine.BOOLEAN_TRUE;
    }

    public int hashCode() {
        final int prime = AbstractMachine.INT_0031;
        int result = AbstractMachine.INT_0001;
        result = (prime * result) + (int) (memory ^ (memory >>> AbstractMachine.INT_0032));
        result = (prime * result) + threads;
        return result;
    }

    @Override
    public String toString() {

        int tmpPrefix = AbstractMachine.INT_0001;
        int tmpMeasure = (int) (memory / AbstractMachine.LONG_1024);

        while ((tmpMeasure / AbstractMachine.INT_1024) > AbstractMachine.INT_0000) {
            tmpPrefix++;
            tmpMeasure /= AbstractMachine.INT_1024;
        }

        switch (tmpPrefix) {

        case 1:

            return tmpMeasure + KILO + threads + ((threads == AbstractMachine.INT_0001) ? THREAD : THREADS);

        case 2:

            return tmpMeasure + MEGA + threads + ((threads == AbstractMachine.INT_0001) ? THREAD : THREADS);

        case 3:

            return tmpMeasure + GIGA + threads + ((threads == AbstractMachine.INT_0001) ? THREAD : THREADS);

        default:

            return memory + BYTES + threads + ((threads == AbstractMachine.INT_0001) ? THREAD : THREADS);
        }

    }
}
