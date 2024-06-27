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

import java.io.Serializable;
import java.util.Iterator;

import org.ojalgo.access.Access1D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.netio.ASCII;
import org.ojalgo.scalar.Scalar;

/**
 * <p>
 * A BasicArray is one-dimensional, but designed to easily be extended
 * or encapsulated, and then treated as arbitrary-dimensional.
 * It stores/handles (any subclass of) {@linkplain java.lang.Number}
 * elements depending on the subclass/implementation.
 * </p><p>
 * This abstract class defines a set of methods to access and modify
 * array elements. It does not "know" anything about linear algebra or
 * similar.
 * </p>
 *
 * @author apete
 */
abstract class BasicArray<N extends Number> implements Access1D<N>, Serializable {

    private static final char LCB = '{';
    private static final char RCB = '}';

    public final int length;

    @SuppressWarnings("unused")
    private BasicArray() {
        this(0);
    }

    protected BasicArray(final int aLength) {

        super();

        length = aLength;
    }

    public final Iterator<N> iterator() {
        return new Iterator1D<N>(this);
    }

    public final int size() {
        return length;
    }

    @Override
    public String toString() {

        final StringBuilder retVal = new StringBuilder();

        retVal.append(LCB);
        retVal.append(ASCII.SP);
        final int tmpLength = length;
        if (tmpLength >= 1) {
            retVal.append(this.get(0).toString());
            for (int i = 1; i < tmpLength; i++) {
                retVal.append(ASCII.COMMA);
                retVal.append(ASCII.SP);
                retVal.append(this.get(i).toString());
            }
            retVal.append(ASCII.SP);
        }
        retVal.append(RCB);

        return retVal.toString();
    }

    /**
     * <p>
     * A utility facade that conveniently/consistently presents the
     * {@linkplain org.ojalgo.array.BasicArray} as a one-dimensional array.
     * Note that you will modify the actual array by accessing it
     * through this facade.
     * </p><p>
     * Disregards the array structure, and simply treats it as one-domensional.
     * </p>
     */
    protected final Array1D<N> asArray1D() {
        return new Array1D<N>(this);
    }

    /**
     * <p>
     * A utility facade that conveniently/consistently presents the
     * {@linkplain org.ojalgo.array.BasicArray} as a two-dimensional array.
     * Note that you will modify the actual array by accessing it
     * through this facade.
     * </p><p>
     * If "this" has more than two dimensions then only the first plane
     * of the first cube of the first... is used/accessed. If this only
     * has one dimension then everything is assumed to be in the first
     * column of the first plane of the first cube...
     * </p>
     */
    protected final Array2D<N> asArray2D(final int aRowDim, final int aColDim) {
        return new Array2D<N>(this, aRowDim, aColDim);
    }

    /**
     * <p>
     * A utility facade that conveniently/consistently presents the
     * {@linkplain org.ojalgo.array.BasicArray} as a multi-dimensional array.
     * Note that you will modify the actual array by accessing it
     * through this facade.
     * </p>
     */
    protected final ArrayAnyD<N> asArrayAnyD(final int[] aStructure) {
        return new ArrayAnyD<N>(this, aStructure);
    }

    protected abstract void exchange(int aFirstA, int aFirstB, int aStep, int aCount);

    protected abstract void fill(final int aFirst, final int aLimit, final Access1D<N> aLeftArg, final BinaryFunction<N> aFunc, final Access1D<N> aRightArg);

    protected abstract void fill(final int aFirst, final int aLimit, final Access1D<N> aLeftArg, final BinaryFunction<N> aFunc, final N aRightArg);

    protected abstract void fill(int aFirst, int aLimit, int aStep, N aNmbr);

    protected abstract void fill(final int aFirst, final int aLimit, final N aLeftArg, final BinaryFunction<N> aFunc, final Access1D<N> aRightArg);

    protected abstract int getIndexOfLargest(int aFirst, int aLimit, int aStep);

    /**
     * @see Scalar#isAbsolute()
     */
    protected abstract boolean isAbsolute(int anInd);

    /**
     * @see Scalar#isPositive()
     */
    protected abstract boolean isPositive(int anInd);

    /**
     * @see Scalar#isReal()
     */
    protected abstract boolean isReal(int anInd);

    /**
     * @see Scalar#isZero()
     */
    protected abstract boolean isZero(int anInd);

    protected abstract boolean isZeros(int aFirst, int aLimit, int aStep);

    protected abstract void modify(int aFirst, int aLimit, int aStep, Access1D<N> aLeftArg, BinaryFunction<N> aFunc);

    protected abstract void modify(int aFirst, int aLimit, int aStep, BinaryFunction<N> aFunc, Access1D<N> aRightArg);

    protected abstract void modify(int aFirst, int aLimit, int aStep, BinaryFunction<N> aFunc, N aRightArg);

    protected abstract void modify(int aFirst, int aLimit, int aStep, N aLeftArg, BinaryFunction<N> aFunc);

    protected abstract void modify(int aFirst, int aLimit, int aStep, ParameterFunction<N> aFunc, int aParam);

    protected abstract void modify(int aFirst, int aLimit, int aStep, UnaryFunction<N> aFunc);

    /**
     * @see java.util.Arrays#binarySearch(Object[], Object)
     * @see #sortAscending()
     */
    protected abstract int searchAscending(N aNmbr);

    protected abstract void set(int anInd, double aNmbr);

    protected abstract void set(int anInd, N aNmbr);

    /**
     * @see java.util.Arrays#sort(Object[])
     * @see #searchAscending(Number)
     */
    protected abstract void sortAscending();

    protected abstract Scalar<N> toScalar(int anInd);

    protected abstract void visit(int aFirst, int aLimit, int aStep, AggregatorFunction<N> aVisitor);

}
