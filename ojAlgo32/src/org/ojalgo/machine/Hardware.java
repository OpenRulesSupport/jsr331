/*
 * Copyright 2003-2012 Optimatika (www.optimatika.se)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.machine;

import java.util.Arrays;
import java.util.TreeSet;

import org.ojalgo.ProgrammingError;
import org.ojalgo.array.ArrayUtils;
import org.ojalgo.netio.ASCII;

/**
 * <ul>
 * <li>
 * The first element in the array should correspond to total system
 * resources; the total amount of RAM and the total number of
 * threads (Typically the same as what is returned by
 * {@linkplain Runtime#availableProcessors()}).
 * </li><li>
 * The last element in the array should describe the L1 cache.
 * Typically Intel processors have 32k L1 cache and AMD 64k. 1 or
 * maybe 2 threads use/share this cache.
 * </li><li>
 * Caches, all levels except L1, are described between the first
 * and last elements in descending order (L3 cache comes before L2
 * cache). Specify the size of the cache and the number of threads
 * using/sharing the cache. (Do not worry about how
 * many cache units there are - describe one unit.)
 * </li><li>
 * The array must have at least 2 elements. You must describe the
 * total system resources and the L1 cache. It is strongly
 * recommended to also describe the L2 cache. The L3 cache, if it
 * exists, is less important to describe. The derived attributes
 * <code>processors</code>, <code>cores</code> and <code>units</code>
 * may be incorrectly calculated if you fail to specify the caches.
 * Known issue: If you have more than one processor, nut no L3
 * cache; the <code>processors</code> attribute will be incorrectly
 * set 1. A workaround that currently works is to define an L3
 * cache anyway and set the memory/size of that cache to 0bytes.
 * This workoround may stop working in the future. 
 * </li><li>
 * <code>new MemoryThreads[] { SYSTEM, L3, L2, L1 }</code>
 * or
 * <code>new MemoryThreads[] { SYSTEM, L2, L1 }</code>
 * or
 * <code>new MemoryThreads[] { SYSTEM, L1 }</code>
 * </li>
 * <ul>
 *
 * @author apete
 */
public final class Hardware extends AbstractMachine implements Comparable<Hardware> {

    /**
     * Should contain all available hardware in ascending "power" order.
     * 
     * Threads# / Cores# / Processors#
     * ===============================
     * INTEL1: 1/1/1
     * MANTA: 2/2/1
     * I7_620M: 4/2/1
     * QXX00: 4/4/1
     * I7_920: 8/4/1
     * SAILFISH: 16/8/2
     * 
     */
    public static final TreeSet<Hardware> PREDEFINED = new TreeSet<Hardware>();

    /**
     * 1 processor
     * 2 cores per processor
     * 1 thread per core
     * Total 2 threads
     * 
     * 3.5GB RAM
     * 6MB L2 cache per processor
     * 32kB L1 cache per core
     */
    static final Hardware B5950053 = new Hardware("x86", new BasicMachine[] { new BasicMachine(LONG_0007 * LONG_0512 * LONG_1024 * LONG_1024, INT_0002), new BasicMachine(LONG_0006 * LONG_1024 * LONG_1024, INT_0002), new BasicMachine(LONG_0032 * LONG_1024, INT_0001) });

    /**
     * Model Name:  MacBook Air
     * Model Identifier: MacBookAir4,2
     * Processor Name:   Intel Core i5
     * Processor Speed:  1,7 GHz
     * Number of Processors: 1
     * Total Number of Cores:    2
     * L2 Cache (per Core):  256 KB
     * L3 Cache: 3 MB
     * Memory:   4 GB
     * Boot ROM Version: MBA41.0077.B0E
     * SMC Version (system): 1.73f63
     * Serial Number (system):   C02GG10RDJWQ
     * Hardware UUID:    871034F4-0D15-522A-984F-63AFD3551969
     * 
     * 1 processor
     * 2 cores per processor
     * 2 thread per core
     * Total 4 threads
     * 
     * 4GB RAM
     * 3MB L3 cache per core
     * 256kB L2 cache per processor
     * 32kB L1 cache per core
     */
    static final Hardware BUBBLE = new Hardware("x86_64", new BasicMachine[] { new BasicMachine(LONG_0004 * LONG_1024 * LONG_1024 * LONG_1024, INT_0004), new BasicMachine(LONG_0003 * LONG_1024 * LONG_1024, INT_0004), new BasicMachine(LONG_0256 * LONG_1024, INT_0002), new BasicMachine(LONG_0032 * LONG_1024, INT_0002) });

    /**
     * Model Name:    iBook G4
     * Model Identifier: PowerBook6,5
     * Processor Name:   PowerPC G4  (1.1)
     * Processor Speed:  1.2 GHz
     * Number Of CPUs:   1
     * L2 Cache (per CPU):   512 KB
     * Memory:   1,25 GB
     * Bus Speed:    133 MHz
     * Boot ROM Version: 4.8.7f1
     * Serial Number (system):   4H50812GRCQ
     * Hardware UUID:    00000000-0000-1000-8000-000D93753C3A
     * 
     * 1 processor
     * 1 core per processor
     * 1 thread per core
     * Total 1 thread
     * 
     * 1,25GB RAM
     * 512kB L2 cache per processor
     * 32kB L1 cache per processor
     */
    static final Hardware CLAM = new Hardware("ppc", new BasicMachine[] { new BasicMachine(LONG_0005 * LONG_0256 * LONG_1024 * LONG_1024, INT_0001), new BasicMachine(LONG_0512 * LONG_1024, INT_0001), new BasicMachine(LONG_0032 * LONG_1024, INT_0001) });

    /**
     * Peter Abeles's (EJML) Intel Core i7-620M laptop
     *      
     * 1 processor
     * 2 cores per processor
     * 2 threads per core
     * Total 4 threads
     * 
     * 8GB RAM
     * 4MB L3 cache per processor
     * 256kB L2 cache per core
     * 32kB L1 cache per core
     */
    static final Hardware I7_620M = new Hardware("x86_64", new BasicMachine[] { new BasicMachine(LONG_0008 * LONG_1024 * LONG_1024 * LONG_1024, INT_0004), new BasicMachine(LONG_0004 * LONG_1024 * LONG_1024, INT_0004), new BasicMachine(LONG_0256 * LONG_1024, INT_0002), new BasicMachine(LONG_0032 * LONG_1024, INT_0002) });

    /**
     * Holger Arndt's (UJMP) Intel Core i7-920 server
     * 
     * 1 processor
     * 4 cores per processor
     * 2 threads per core
     * Total 8 threads
     * 
     * 8GB RAM
     * 8MB L3 cache per processor
     * 256kB L2 cache per core
     * 32kB L1 cache per core
     */
    static final Hardware I7_920 = new Hardware("x86_64", new BasicMachine[] { new BasicMachine(LONG_0008 * LONG_1024 * LONG_1024 * LONG_1024, INT_0008), new BasicMachine(LONG_0008 * LONG_1024 * LONG_1024, INT_0008), new BasicMachine(LONG_0256 * LONG_1024, INT_0002), new BasicMachine(LONG_0032 * LONG_1024, INT_0002) });

    /**
     * 1 processor
     * 1 core per processor
     * 1 thread per core
     * Total 1 threads
     * 
     * 1GB RAM
     * 1MB L2 cache per processor
     * 32kB L1 cache per core
     */
    static final Hardware INTEL1 = new Hardware("x86", new BasicMachine[] { new BasicMachine(LONG_0001 * LONG_1024 * LONG_1024 * LONG_1024, INT_0001), new BasicMachine(LONG_0001 * LONG_1024 * LONG_1024, INT_0001), new BasicMachine(LONG_0032 * LONG_1024, INT_0001) });

    /**
     * Model Name:  iMac
     * Model Identifier: iMac7,1
     * Processor Name:   Intel Core 2 Duo
     * Processor Speed:  2,4 GHz
     * Number of Processors: 1
     * Total Number of Cores:    2
     * L2 Cache: 4 MB
     * Memory:   3 GB
     * Bus Speed:    800 MHz
     * Boot ROM Version: IM71.007A.B03
     * SMC Version (system): 1.20f4
     * Serial Number (system):   W8735035X88
     * Hardware UUID:    00000000-0000-1000-8000-001B639A63C6
     * 
     * 1 processor
     * 2 cores per processor
     * 1 thread per core
     * Total 2 threads
     * 
     * 3GB RAM
     * 4MB L2 cache per processor
     * 32kB L1 cache per core
     */
    static final Hardware MANTA = new Hardware("x86_64", new BasicMachine[] { new BasicMachine(LONG_0003 * LONG_1024 * LONG_1024 * LONG_1024, INT_0002), new BasicMachine(LONG_0004 * LONG_1024 * LONG_1024, INT_0002), new BasicMachine(LONG_0032 * LONG_1024, INT_0001) });

    /**
     * Peter Abeles' (EJML) Q9400 and Q6600 machines.
     * 
     * 1 processor
     * 4 cores per processor
     * 1 thread per core
     * Total 4 threads
     * 
     * 8GB RAM (Q6600: 8GB, Q9400: 3GB)
     * 3MB L2 cache per 2 cores  (Q6600: 2x4MB, Q9400: 2x3MB)
     * 32kB L1 cache per core
     */
    static final Hardware QXX00 = new Hardware("x86_64", new BasicMachine[] { new BasicMachine(LONG_0008 * LONG_1024 * LONG_1024 * LONG_1024, INT_0004), new BasicMachine(LONG_0003 * LONG_1024 * LONG_1024, INT_0002), new BasicMachine(LONG_0032 * LONG_1024, INT_0001) });

    /**
     * Model Name:   Mac Pro
     * Model Identifier: MacPro4,1
     * Processor Name:   Quad-Core Intel Xeon
     * Processor Speed:  2,26 GHz
     * Number of Processors: 2
     * Total Number of Cores:    8
     * L2 Cache (per Core):  256 KB
     * L3 Cache (per Processor): 8 MB
     * Memory:   12 GB
     * Processor Interconnect Speed: 5.86 GT/s
     * Boot ROM Version: MP41.0081.B07
     * SMC Version (system): 1.39f5
     * SMC Version (processor tray): 1.39f5
     * Serial Number (system):   CK91602Z20H
     * Serial Number (processor tray):   J591301N71LUC
     * Hardware UUID:    089DB270-35D2-521B-9A3E-B734624B7165
     * 
     * 2 processors
     * 4 cores per processor
     * 2 threads per core
     * Total 16 threads
     * 
     * 12GB RAM
     * 8MB L3 cache per processor
     * 256kB L2 cache per core
     * 32kB L1 cache per core
     */
    static final Hardware SAILFISH = new Hardware("x86_64", new BasicMachine[] { new BasicMachine(LONG_0012 * LONG_1024 * LONG_1024 * LONG_1024, INT_0016), new BasicMachine(LONG_0008 * LONG_1024 * LONG_1024, INT_0008), new BasicMachine(LONG_0256 * LONG_1024, INT_0002), new BasicMachine(LONG_0032 * LONG_1024, INT_0002) });

    static {
        PREDEFINED.add(B5950053);
        PREDEFINED.add(BUBBLE);
        PREDEFINED.add(CLAM);
        PREDEFINED.add(I7_620M);
        PREDEFINED.add(I7_920);
        PREDEFINED.add(INTEL1);
        PREDEFINED.add(MANTA);
        PREDEFINED.add(QXX00);
        PREDEFINED.add(SAILFISH);
    }

    public static Hardware makeSimple() {
        return Hardware.makeSimple(VirtualMachine.getArchitecture(), VirtualMachine.getMemory(), VirtualMachine.getThreads());
    }

    public static Hardware makeSimple(final String systemArchitecture, final long systemMemory, final int systemThreads) {

        if (systemThreads > INT_0008) {
            // Assume hyperthreading, L3 cache and more than 1 CPU

            final BasicMachine tmpL1Machine = new BasicMachine(LONG_0032 * LONG_1024, INT_0002); //Hyperthreading

            final BasicMachine tmpL2Machine = new BasicMachine(LONG_0256 * LONG_1024, tmpL1Machine.threads);

            final BasicMachine tmpL3Machine = new BasicMachine(LONG_0004 * LONG_1024 * LONG_1024, systemThreads / ((systemThreads + INT_0007) / INT_0008)); //More than 1 CPU

            final BasicMachine tmpSystemMachine = new BasicMachine(systemMemory, systemThreads);

            return new Hardware(systemArchitecture, new BasicMachine[] { tmpSystemMachine, tmpL3Machine, tmpL2Machine, tmpL1Machine });

        } else if (systemThreads >= INT_0004) {
            // Assume hyperthreading, L3 cache but only 1 CPU

            final BasicMachine tmpL1Machine = new BasicMachine(LONG_0032 * LONG_1024, INT_0002); //Hyperthreading

            final BasicMachine tmpL2Machine = new BasicMachine(LONG_0256 * LONG_1024, tmpL1Machine.threads);

            final BasicMachine tmpL3Machine = new BasicMachine(LONG_0003 * LONG_1024 * LONG_1024, systemThreads);

            final BasicMachine tmpSystemMachine = new BasicMachine(systemMemory, systemThreads);

            return new Hardware(systemArchitecture, new BasicMachine[] { tmpSystemMachine, tmpL3Machine, tmpL2Machine, tmpL1Machine });

        } else {
            // No hyperthreading, no L3 cache and 1 CPU

            final BasicMachine tmpL1Machine = new BasicMachine(LONG_0032 * LONG_1024, INT_0001); //No hyperthreading

            final BasicMachine tmpL2Machine = new BasicMachine(LONG_0002 * LONG_1024 * LONG_1024, tmpL1Machine.threads);

            final BasicMachine tmpSystemMachine = new BasicMachine(systemMemory, systemThreads);

            return new Hardware(systemArchitecture, new BasicMachine[] { tmpSystemMachine, tmpL2Machine, tmpL1Machine });
        }
    }

    private final BasicMachine[] myLevels;

    /**
     * <code>new BasicMachine[] { SYSTEM, L3, L2, L1 }</code>
     * or
     * <code>new BasicMachine[] { SYSTEM, L2, L1 }</code>
     * or in worst case
     * <code>new BasicMachine[] { SYSTEM, L1 }</code>
     */
    public Hardware(final String anArchitecture, final BasicMachine[] someLevels) {

        super(anArchitecture, someLevels);

        if (someLevels.length < INT_0002) {
            throw new IllegalArgumentException();
        }

        myLevels = ArrayUtils.copyOf(someLevels);
    }

    @SuppressWarnings("unused")
    private Hardware(final Hardware aHardware, final Runtime aRuntime) {

        super(aHardware, aRuntime);

        myLevels = null;

        ProgrammingError.throwForIllegalInvocation();
    }

    public int compareTo(final Hardware aReference) {
        if (threads != aReference.threads) {
            return threads - aReference.threads;
        } else if (processors != aReference.processors) {
            return processors - aReference.processors;
        } else if (units != aReference.units) {
            return units - aReference.units;
        } else if (cores != aReference.cores) {
            return cores - aReference.cores;
        } else if (memory != aReference.memory) {
            return (int) (memory - aReference.memory);
        } else if (cacheL3 != aReference.cacheL3) {
            return (int) (cacheL3 - aReference.cacheL3);
        } else if (cacheL2 != aReference.cacheL2) {
            return (int) (cacheL2 - aReference.cacheL2);
        } else if (cacheL1 != aReference.cacheL1) {
            return (int) (cacheL1 - aReference.cacheL1);
        } else {
            return INT_0000;
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return BOOLEAN_TRUE;
        }
        if (!super.equals(obj)) {
            return BOOLEAN_FALSE;
        }
        if (!(obj instanceof Hardware)) {
            return BOOLEAN_FALSE;
        }
        final Hardware other = (Hardware) obj;
        if (!Arrays.equals(myLevels, other.myLevels)) {
            return BOOLEAN_FALSE;
        }
        return BOOLEAN_TRUE;
    }

    @Override
    public int hashCode() {
        final int prime = INT_0031;
        int result = super.hashCode();
        result = (prime * result) + Arrays.hashCode(myLevels);
        return result;
    }

    public boolean isL2Specified() {
        return myLevels.length > INT_0002;
    }

    public boolean isL3Specified() {
        return myLevels.length > INT_0003;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ASCII.SP + Arrays.toString(myLevels);
    }

    public final VirtualMachine virtualise() {
        return new VirtualMachine(this, Runtime.getRuntime());
    }

}
