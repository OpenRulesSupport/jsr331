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

import org.ojalgo.type.IntCount;

abstract class AbstractMachine extends BasicMachine {

    static final boolean BOOLEAN_FALSE = false;
    static final boolean BOOLEAN_TRUE = true;
    static final int INT_0000 = 0;
    static final int INT_0001 = 1;
    static final int INT_0002 = 2;
    static final int INT_0003 = 3;
    static final int INT_0004 = 4;
    static final int INT_0007 = 7;
    static final int INT_0008 = 8;
    static final int INT_0016 = 16;
    static final int INT_0031 = 31;
    static final int INT_0032 = 32;
    static final int INT_1024 = 1024;
    static final long LONG_0000 = 0L;
    static final long LONG_0001 = 1L;
    static final long LONG_0002 = 2L;
    static final long LONG_0003 = 3L;
    static final long LONG_0004 = 4L;
    static final long LONG_0005 = 5L;
    static final long LONG_0006 = 6L;
    static final long LONG_0007 = 7L;
    static final long LONG_0008 = 8L;
    static final long LONG_0012 = 12L;
    static final long LONG_0016 = 16L;
    static final long LONG_0032 = 32L;
    static final long LONG_0256 = 256L;
    static final long LONG_0512 = 512L;
    static final long LONG_1024 = 1024L;

    static long elements(final long availableMemory, final long elementSize) {
        return (availableMemory - LONG_0016) / elementSize;
    }

    /**
     * The size of one L1 cache unit in bytes. Defined to be the memory
     * of the last {@linkplain BasicMachine} specified.
     */
    public final long cacheL1;
    /**
     * The size of one L2 cache unit in bytes. Defined to be the memory
     * of the second last {@linkplain BasicMachine} specified.
     */
    public final long cacheL2;

    /**
     * The size of one L3 cache unit in bytes. Defined to be the memory
     * of the second {@linkplain BasicMachine} specified.
     */
    public final long cacheL3;

    public final String architecture;//x86_64
    /**
     * The total number of processor cores.
     */
    public final int cores;
    /**
     * The number of L3 cache units. (It is assumed there is 1 L3 cache
     * unit per processor.) If no L3 cache is specified then this will
     * be equal to <code>units</code>.
     */
    public final int processors;

    /**
     * The number of L2 cache units. If no L2 cache is specified then
     * this will be equal to <code>1</code>.
     */
    public final int units;

    @SuppressWarnings("unused")
    private AbstractMachine(final long aMemory, final int aThreads) {

        super(aMemory, aThreads);

        throw new IllegalArgumentException();
    }

    protected AbstractMachine(final Hardware aHardware, final Runtime aRuntime) {

        super(aRuntime.maxMemory(), aRuntime.availableProcessors());

        architecture = aHardware.architecture;

        cacheL1 = aHardware.cacheL1;
        cacheL2 = aHardware.cacheL2;
        cacheL3 = aHardware.cacheL3;

        cores = aHardware.cores;
        units = aHardware.units;
        processors = aHardware.processors;
    }

    /**
     * <code>new MemoryThreads[] { SYSTEM, L3, L2, L1 }</code>
     * or
     * <code>new MemoryThreads[] { SYSTEM, L2, L1 }</code>
     * or in worst case
     * <code>new MemoryThreads[] { SYSTEM, L1 }</code>
     */
    protected AbstractMachine(final String anArchitecture, final BasicMachine[] someLevels) {

        super(someLevels[INT_0000].memory, someLevels[INT_0000].threads);

        architecture = anArchitecture;

        cacheL1 = someLevels[someLevels.length - INT_0001].memory;
        cacheL2 = someLevels[someLevels.length - INT_0002].memory;
        cacheL3 = someLevels[INT_0001].memory;

        cores = threads / someLevels[someLevels.length - INT_0001].threads;
        units = threads / someLevels[someLevels.length - INT_0002].threads;
        processors = threads / someLevels[INT_0001].threads;
    }

    public IntCount countCores() {
        return new IntCount(cores);
    }

    public IntCount countProcessors() {
        return new IntCount(processors);
    }

    public IntCount countThreads() {
        return new IntCount(threads);
    }

    public IntCount countUnits() {
        return new IntCount(units);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return BOOLEAN_TRUE;
        }
        if (!super.equals(obj)) {
            return BOOLEAN_FALSE;
        }
        if (!(obj instanceof AbstractMachine)) {
            return BOOLEAN_FALSE;
        }
        final AbstractMachine other = (AbstractMachine) obj;
        if (architecture == null) {
            if (other.architecture != null) {
                return BOOLEAN_FALSE;
            }
        } else if (!architecture.equals(other.architecture)) {
            return BOOLEAN_FALSE;
        }
        if (cacheL1 != other.cacheL1) {
            return BOOLEAN_FALSE;
        }
        if (cacheL2 != other.cacheL2) {
            return BOOLEAN_FALSE;
        }
        if (cacheL3 != other.cacheL3) {
            return BOOLEAN_FALSE;
        }
        if (cores != other.cores) {
            return BOOLEAN_FALSE;
        }
        if (processors != other.processors) {
            return BOOLEAN_FALSE;
        }
        if (units != other.units) {
            return BOOLEAN_FALSE;
        }
        return BOOLEAN_TRUE;
    }

    public int getCacheL1Dim1D(final long elementSize) {
        return (int) AbstractMachine.elements(cacheL1, elementSize);
    }

    public int getCacheL1Dim2D(final long elementSize) {
        return (int) Math.sqrt(AbstractMachine.elements(cacheL1, elementSize));
    }

    /**
     * The dimension (length) of an array that will fit in an L2 cache
     * unit given the input element size.
     */
    public int getCacheL2Dim1D(final long elementSize) {
        return (int) AbstractMachine.elements(cacheL2, elementSize);
    }

    /**
     * The dimension (row and/or col count) of a (2 dimensional) array
     * that will fit in an L2 cache unit given the input element size.
     */
    public int getCacheL2Dim2D(final long elementSize) {
        return (int) Math.sqrt(AbstractMachine.elements(cacheL2, elementSize));
    }

    public int getCacheL3Dim1D(final long elementSize) {
        return (int) AbstractMachine.elements(cacheL3, elementSize);
    }

    public int getCacheL3Dim2D(final long elementSize) {
        return (int) Math.sqrt(AbstractMachine.elements(cacheL3, elementSize));
    }

    public int getMemoryDim1D(final long elementSize) {
        return (int) AbstractMachine.elements(memory, elementSize);
    }

    public int getMemoryDim2D(final long elementSize) {
        return (int) Math.sqrt(AbstractMachine.elements(memory, elementSize));
    }

    @Override
    public int hashCode() {
        final int prime = INT_0031;
        int result = super.hashCode();
        result = (prime * result) + ((architecture == null) ? INT_0000 : architecture.hashCode());
        result = (prime * result) + (int) (cacheL1 ^ (cacheL1 >>> INT_0032));
        result = (prime * result) + (int) (cacheL2 ^ (cacheL2 >>> INT_0032));
        result = (prime * result) + (int) (cacheL3 ^ (cacheL3 >>> INT_0032));
        result = (prime * result) + cores;
        result = (prime * result) + processors;
        result = (prime * result) + units;
        return result;
    }

    public boolean isMultiCore() {
        return cores > INT_0001;
    }

    public boolean isMultiProcessor() {
        return processors > INT_0001;
    }

    public boolean isMultiThread() {
        return threads > INT_0001;
    }

    public boolean isMultiUnit() {
        return units > INT_0001;
    }

}
