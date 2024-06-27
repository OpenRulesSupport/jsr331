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

import java.lang.management.ManagementFactory;

import org.ojalgo.ProgrammingError;
import org.ojalgo.netio.ASCII;
import org.ojalgo.netio.BasicLogger;

public final class VirtualMachine extends AbstractMachine {

    private static final String AMD64 = "amd64";
    private static final String I386 = "i386";
    private static final String X86 = "x86";
    private static final String X86_64 = "x86_64";

    public static String getArchitecture() {

        // http://fantom.org/sidewalk/topic/756

        final String tmpProperty = ManagementFactory.getOperatingSystemMXBean().getArch().toLowerCase();

        if (tmpProperty.equals(I386)) {
            return X86;
        } else if (tmpProperty.equals(AMD64)) {
            return X86_64;
        } else {
            return tmpProperty;
        }
    }

    public static long getMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static int getThreads() {
        return Runtime.getRuntime().availableProcessors();
    }

    private final Hardware myHardware;
    private final Runtime myRuntime;

    @SuppressWarnings("unused")
    private VirtualMachine(final String anArchitecture, final BasicMachine[] someLevels) {

        super(anArchitecture, someLevels);

        myHardware = null;
        myRuntime = null;

        ProgrammingError.throwForIllegalInvocation();
    }

    VirtualMachine(final Hardware aHardware, final Runtime aRuntime) {

        super(aHardware, aRuntime);

        myHardware = aHardware;
        myRuntime = aRuntime;
    }

    public void collectGarbage() {

        myRuntime.runFinalization();

        long tmpIsFree = myRuntime.freeMemory();
        long tmpWasFree;

        do {
            tmpWasFree = tmpIsFree;
            myRuntime.gc();
            try {
                Thread.sleep(LONG_0008);
            } catch (final InterruptedException anException) {
                BasicLogger.logError(anException.getMessage());
            }
            tmpIsFree = myRuntime.freeMemory();
        } while (tmpIsFree > tmpWasFree);

        myRuntime.runFinalization();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return BOOLEAN_TRUE;
        }
        if (!super.equals(obj)) {
            return BOOLEAN_FALSE;
        }
        if (!(obj instanceof VirtualMachine)) {
            return BOOLEAN_FALSE;
        }
        final VirtualMachine other = (VirtualMachine) obj;
        if (myHardware == null) {
            if (other.myHardware != null) {
                return BOOLEAN_FALSE;
            }
        } else if (!myHardware.equals(other.myHardware)) {
            return BOOLEAN_FALSE;
        }
        return BOOLEAN_TRUE;
    }

    public int getAvailableDim1D(final long elementSize) {
        return (int) AbstractMachine.elements(this.getAvailableMemory(), elementSize);
    }

    public int getAvailableDim2D(final long elementSize) {
        return (int) Math.sqrt(AbstractMachine.elements(this.getAvailableMemory(), elementSize));
    }

    public long getAvailableMemory() {

        final long tmpMax = myRuntime.maxMemory();
        final long tmpTotal = myRuntime.totalMemory();
        final long tmpFree = myRuntime.freeMemory();

        return (tmpMax - tmpTotal) + tmpFree;
    }

    @Override
    public int hashCode() {
        final int prime = INT_0031;
        int result = super.hashCode();
        result = (prime * result) + ((myHardware == null) ? INT_0000 : myHardware.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return super.toString() + ASCII.SP + myHardware.toString();
    }

}
