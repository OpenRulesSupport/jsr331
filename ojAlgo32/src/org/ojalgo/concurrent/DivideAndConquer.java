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
package org.ojalgo.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ojalgo.type.IntCount;

public abstract class DivideAndConquer extends Object {

    private final int myThreshold;

    public DivideAndConquer(final int aThreshold) {

        super();

        myThreshold = aThreshold;
    }

    @SuppressWarnings("unused")
    private DivideAndConquer() {
        this(2);
    }

    public final void divide(final int aFirst, final int aLimit, final IntCount availableWorkers) {

        final int tmpCount = ConcurrentUtils.calculateIndexCount(aFirst, aLimit);

        if (ConcurrentUtils.shouldBranch(tmpCount, myThreshold, availableWorkers)) {

            final int tmpSplit = ConcurrentUtils.calculateSplitIndex(aFirst, tmpCount);
            final IntCount tmpWorkers = availableWorkers.halve();

            final Future<?> tmpFirstPart = DaemonPoolExecutor.INSTANCE.submit(new Runnable() {

                public void run() {
                    DivideAndConquer.this.divide(aFirst, tmpSplit, tmpWorkers);
                }
            });

            final Future<?> tmpSecondPart = DaemonPoolExecutor.INSTANCE.submit(new Runnable() {

                public void run() {
                    DivideAndConquer.this.divide(tmpSplit, aLimit, tmpWorkers);
                }
            });

            try {
                tmpFirstPart.get();
                tmpSecondPart.get();
            } catch (final InterruptedException anException) {
                anException.printStackTrace();
            } catch (final ExecutionException anException) {
                anException.printStackTrace();
            }

        } else {

            this.conquer(aFirst, aLimit);
        }
    }

    protected abstract void conquer(final int aFirst, final int aLimit);

}
