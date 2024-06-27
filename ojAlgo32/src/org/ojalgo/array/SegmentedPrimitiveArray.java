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
package org.ojalgo.array;

import org.ojalgo.OjAlgoUtils;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.machine.JavaType;
import org.ojalgo.machine.MemoryEstimator;
import org.ojalgo.scalar.Scalar;

class SegmentedPrimitiveArray extends BasicArray<Double> {

    public static final int SEGMENT_SIZE;

    static {

        final long tmpElementSize = JavaType.DOUBLE.memory();

        final long tmpWrapperClassOverhead = MemoryEstimator.estimateObject(PrimitiveArray.class);
        final long tmpActualArrayOverhead = MemoryEstimator.estimateArray(double.class, 0);

        // Those estimates are for 32bit pointers, which is probably not what's
        // used in this case - so I double everything
        final long tmpOverHead = 2L * (tmpWrapperClassOverhead + tmpActualArrayOverhead);

        final long tmpCacheL3 = OjAlgoUtils.ENVIRONMENT.cacheL3;

        final int tmpOptimalLength = (int) ((tmpCacheL3 - tmpOverHead) / tmpElementSize);

        SEGMENT_SIZE = Math.min(tmpOptimalLength, Integer.MAX_VALUE);
    }

    public final long length;

    /**
     * All arrays, except the last, must have the same number of elements
     */
    private final PrimitiveArray[] mySegments;

    public SegmentedPrimitiveArray(final int... shape) {

        super(AccessUtils.size(shape));

        length = AccessUtils.size(shape);

        final int tmpSegmentCount = (int) ((length % SEGMENT_SIZE) == 0 ? length / SEGMENT_SIZE : (length / SEGMENT_SIZE) + 1);

        mySegments = new PrimitiveArray[tmpSegmentCount];

        for (int i = 0; i < (tmpSegmentCount - 1); i++) {
            mySegments[i] = new PrimitiveArray(SEGMENT_SIZE);
        }
        mySegments[tmpSegmentCount - 1] = new PrimitiveArray((int) (length - ((tmpSegmentCount - 1) * SEGMENT_SIZE)));

    }

    public double doubleValue(final int anInd) {
        return mySegments[anInd / SEGMENT_SIZE].doubleValue(anInd % SEGMENT_SIZE);
    }

    public Double get(final int anInd) {
        return mySegments[anInd / SEGMENT_SIZE].get(anInd % SEGMENT_SIZE);
    }

    @Override
    protected void exchange(final int aFirstA, final int aFirstB, final int aStep, final int aCount) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void fill(final int aFirst, final int aLimit, final Access1D<Double> aLeftArg, final BinaryFunction<Double> aFunc,
            final Access1D<Double> aRightArg) {
        for (int i = aFirst; i < aLimit; i++) {
            this.set(i, aFunc.invoke(aLeftArg.doubleValue(i), aRightArg.doubleValue(i)));
        }
    }

    @Override
    protected void fill(final int aFirst, final int aLimit, final Access1D<Double> aLeftArg, final BinaryFunction<Double> aFunc, final Double aRightArg) {
        final double tmpRightArg = aRightArg.doubleValue();
        for (int i = aFirst; i < aLimit; i++) {
            this.set(i, aFunc.invoke(aLeftArg.doubleValue(i), tmpRightArg));
        }
    }

    @Override
    protected void fill(final int aFirst, final int aLimit, final Double aLeftArg, final BinaryFunction<Double> aFunc, final Access1D<Double> aRightArg) {
        final double tmpLeftArg = aLeftArg.doubleValue();
        for (int i = aFirst; i < aLimit; i++) {
            this.set(i, aFunc.invoke(tmpLeftArg, aRightArg.doubleValue(i)));
        }
    }

    @Override
    protected void fill(final int aFirst, final int aLimit, final int aStep, final Double aNmbr) {

        if (aStep <= SEGMENT_SIZE) {
            // Will use a continous range of segements

            final int tmpFirstSegment = aFirst / SEGMENT_SIZE;
            final int tmpLastSegemnt = (aLimit - 1) / SEGMENT_SIZE;

            int tmpFirstInSegment = aFirst % SEGMENT_SIZE;

            for (int s = tmpFirstSegment; s < tmpLastSegemnt; s++) {
                mySegments[s].fill(tmpFirstInSegment, SEGMENT_SIZE, aStep, aNmbr);
                tmpFirstInSegment = aStep - ((SEGMENT_SIZE - tmpFirstInSegment) % aStep);
            }
            mySegments[tmpLastSegemnt].fill(tmpFirstInSegment, mySegments[tmpLastSegemnt].length, aStep, aNmbr);

        } else {

            final double tmpNmbr = aNmbr.doubleValue();
            for (int i = aFirst; i < aLimit; i += aStep) {
                this.set(i, tmpNmbr);
            }
        }

    }

    @Override
    protected int getIndexOfLargest(final int aFirst, final int aLimit, final int aStep) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected boolean isAbsolute(final int anInd) {
        return mySegments[anInd / SEGMENT_SIZE].isAbsolute(anInd % SEGMENT_SIZE);
    }

    @Override
    protected boolean isPositive(final int anInd) {
        return mySegments[anInd / SEGMENT_SIZE].isPositive(anInd % SEGMENT_SIZE);
    }

    @Override
    protected boolean isReal(final int anInd) {
        return mySegments[anInd / SEGMENT_SIZE].isReal(anInd % SEGMENT_SIZE);
    }

    @Override
    protected boolean isZero(final int anInd) {
        return mySegments[anInd / SEGMENT_SIZE].isZero(anInd % SEGMENT_SIZE);
    }

    @Override
    protected boolean isZeros(final int aFirst, final int aLimit, final int aStep) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void modify(final int aFirst, final int aLimit, final int aStep, final Access1D<Double> aLeftArg, final BinaryFunction<Double> aFunc) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            this.set(i, aFunc.invoke(aLeftArg.doubleValue(i), this.doubleValue(i)));
        }
    }

    @Override
    protected void modify(final int aFirst, final int aLimit, final int aStep, final BinaryFunction<Double> aFunc, final Access1D<Double> aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            this.set(i, aFunc.invoke(this.doubleValue(i), aRightArg.doubleValue(i)));
        }
    }

    @Override
    protected void modify(final int aFirst, final int aLimit, final int aStep, final BinaryFunction<Double> aFunc, final Double aRightArg) {

        if (aStep <= SEGMENT_SIZE) {
            // Will use a continous range of segements

            final int tmpFirstSegment = aFirst / SEGMENT_SIZE;
            final int tmpLastSegemnt = (aLimit - 1) / SEGMENT_SIZE;

            int tmpFirstInSegment = aFirst % SEGMENT_SIZE;

            for (int s = tmpFirstSegment; s < tmpLastSegemnt; s++) {
                mySegments[s].modify(tmpFirstInSegment, SEGMENT_SIZE, aStep, aFunc, aRightArg);
                tmpFirstInSegment = aStep - ((SEGMENT_SIZE - tmpFirstInSegment) % aStep);
            }
            mySegments[tmpLastSegemnt].modify(tmpFirstInSegment, mySegments[tmpLastSegemnt].length, aStep, aFunc, aRightArg);

        } else {

            final double tmpRightArg = aRightArg.doubleValue();

            for (int i = aFirst; i < aLimit; i += aStep) {
                aFunc.invoke(this.doubleValue(i), tmpRightArg);
            }
        }
    }

    @Override
    protected void modify(final int aFirst, final int aLimit, final int aStep, final Double aLeftArg, final BinaryFunction<Double> aFunc) {

        if (aStep <= SEGMENT_SIZE) {
            // Will use a continous range of segements

            final int tmpFirstSegment = aFirst / SEGMENT_SIZE;
            final int tmpLastSegemnt = (aLimit - 1) / SEGMENT_SIZE;

            int tmpFirstInSegment = aFirst % SEGMENT_SIZE;

            for (int s = tmpFirstSegment; s < tmpLastSegemnt; s++) {
                mySegments[s].modify(tmpFirstInSegment, SEGMENT_SIZE, aStep, aLeftArg, aFunc);
                tmpFirstInSegment = aStep - ((SEGMENT_SIZE - tmpFirstInSegment) % aStep);
            }
            mySegments[tmpLastSegemnt].modify(tmpFirstInSegment, mySegments[tmpLastSegemnt].length, aStep, aLeftArg, aFunc);

        } else {

            final double tmpLeftArg = aLeftArg.doubleValue();

            for (int i = aFirst; i < aLimit; i += aStep) {
                aFunc.invoke(tmpLeftArg, this.doubleValue(i));
            }
        }
    }

    @Override
    protected void modify(final int aFirst, final int aLimit, final int aStep, final ParameterFunction<Double> aFunc, final int aParam) {

        if (aStep <= SEGMENT_SIZE) {
            // Will use a continous range of segements

            final int tmpFirstSegment = aFirst / SEGMENT_SIZE;
            final int tmpLastSegemnt = (aLimit - 1) / SEGMENT_SIZE;

            int tmpFirstInSegment = aFirst % SEGMENT_SIZE;

            for (int s = tmpFirstSegment; s < tmpLastSegemnt; s++) {
                mySegments[s].modify(tmpFirstInSegment, SEGMENT_SIZE, aStep, aFunc, aParam);
                tmpFirstInSegment = aStep - ((SEGMENT_SIZE - tmpFirstInSegment) % aStep);
            }
            mySegments[tmpLastSegemnt].modify(tmpFirstInSegment, mySegments[tmpLastSegemnt].length, aStep, aFunc, aParam);

        } else {

            for (int i = aFirst; i < aLimit; i += aStep) {
                aFunc.invoke(this.doubleValue(i), aParam);
            }
        }
    }

    @Override
    protected void modify(final int aFirst, final int aLimit, final int aStep, final UnaryFunction<Double> aFunc) {

        if (aStep <= SEGMENT_SIZE) {
            // Will use a continous range of segements

            final int tmpFirstSegment = aFirst / SEGMENT_SIZE;
            final int tmpLastSegemnt = (aLimit - 1) / SEGMENT_SIZE;

            int tmpFirstInSegment = aFirst % SEGMENT_SIZE;

            for (int s = tmpFirstSegment; s < tmpLastSegemnt; s++) {
                mySegments[s].modify(tmpFirstInSegment, SEGMENT_SIZE, aStep, aFunc);
                tmpFirstInSegment = aStep - ((SEGMENT_SIZE - tmpFirstInSegment) % aStep);
            }
            mySegments[tmpLastSegemnt].modify(tmpFirstInSegment, mySegments[tmpLastSegemnt].length, aStep, aFunc);

        } else {

            for (int i = aFirst; i < aLimit; i += aStep) {
                this.set(i, aFunc.invoke(this.doubleValue(i)));
            }

        }
    }

    @Override
    protected int searchAscending(final Double aNmbr) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void set(final int anInd, final double aNmbr) {
        mySegments[anInd / SEGMENT_SIZE].set(anInd % SEGMENT_SIZE, aNmbr);
    }

    @Override
    protected void set(final int anInd, final Double aNmbr) {
        mySegments[anInd / SEGMENT_SIZE].set(anInd % SEGMENT_SIZE, aNmbr);
    }

    @Override
    protected void sortAscending() {
        // TODO Auto-generated method stub
    }

    @Override
    protected Scalar<Double> toScalar(final int anInd) {
        return mySegments[anInd / SEGMENT_SIZE].toScalar(anInd % SEGMENT_SIZE);
    }

    @Override
    protected void visit(final int aFirst, final int aLimit, final int aStep, final AggregatorFunction<Double> aVisitor) {

        if (aStep <= SEGMENT_SIZE) {
            // Will use a continous range of segements

            final int tmpFirstSegment = aFirst / SEGMENT_SIZE;
            final int tmpLastSegemnt = (aLimit - 1) / SEGMENT_SIZE;

            int tmpFirstInSegment = aFirst % SEGMENT_SIZE;

            for (int s = tmpFirstSegment; s < tmpLastSegemnt; s++) {
                mySegments[s].visit(tmpFirstInSegment, SEGMENT_SIZE, aStep, aVisitor);
                tmpFirstInSegment = aStep - ((SEGMENT_SIZE - tmpFirstInSegment) % aStep);
            }
            mySegments[tmpLastSegemnt].visit(tmpFirstInSegment, mySegments[tmpLastSegemnt].length, aStep, aVisitor);

        } else {

            for (int i = aFirst; i < aLimit; i += aStep) {
                aVisitor.invoke(this.doubleValue(i));
            }
        }
    }
}
