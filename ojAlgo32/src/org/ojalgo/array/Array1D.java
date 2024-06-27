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
import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import org.ojalgo.access.Access1D;
import org.ojalgo.access.Factory1D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;

/**
 * Array1D
 *
 * @author apete
 */
public final class Array1D<N extends Number> extends AbstractList<N> implements Access1D<N>, RandomAccess, Serializable {

    public static interface Factory<N extends Number> extends Factory1D<Array1D<N>> {

        Array1D<N> wrap(final SimpleArray<N> aSimple);

    }

    public static final Array1D.Factory<BigDecimal> BIG = new Array1D.Factory<BigDecimal>() {

        public Array1D<BigDecimal> copy(final Access1D<?> aSource) {

            final int tmpSize = aSource.size();

            final BigDecimal[] tmpArray = new BigDecimal[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toBigDecimal(aSource.get(i));
            }

            return new BigArray(tmpArray).asArray1D();
        }

        public Array1D<BigDecimal> copy(final double... aSource) {

            final int tmpSize = aSource.length;

            final BigDecimal[] tmpArray = new BigDecimal[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toBigDecimal(aSource[i]);
            }

            return new BigArray(tmpArray).asArray1D();
        }

        public Array1D<BigDecimal> copy(final List<? extends Number> aSource) {

            final int tmpSize = aSource.size();

            final BigDecimal[] tmpArray = new BigDecimal[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toBigDecimal(aSource.get(i));
            }

            return new BigArray(tmpArray).asArray1D();
        }

        public Array1D<BigDecimal> copy(final Number... aSource) {

            final int tmpSize = aSource.length;

            final BigDecimal[] tmpArray = new BigDecimal[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toBigDecimal(aSource[i]);
            }

            return new BigArray(tmpArray).asArray1D();
        }

        public Array1D<BigDecimal> makeRandom(final int aSize, final RandomNumber aRndm) {

            final BigDecimal[] tmpArray = new BigDecimal[aSize];
            for (int i = 0; i < aSize; i++) {
                tmpArray[i] = TypeUtils.toBigDecimal(aRndm);
            }

            return new BigArray(tmpArray).asArray1D();
        }

        public Array1D<BigDecimal> makeZero(final int aSize) {
            return new BigArray(aSize).asArray1D();
        }

        public Array1D<BigDecimal> wrap(final SimpleArray<BigDecimal> aSimple) {
            return new BigArray(((SimpleArray.Big) aSimple).data).asArray1D();
        }

    };

    public static final Array1D.Factory<ComplexNumber> COMPLEX = new Array1D.Factory<ComplexNumber>() {

        public Array1D<ComplexNumber> copy(final Access1D<?> aSource) {

            final int tmpSize = aSource.size();

            final ComplexNumber[] tmpArray = new ComplexNumber[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toComplexNumber(aSource.get(i));
            }

            return new ComplexArray(tmpArray).asArray1D();
        }

        public Array1D<ComplexNumber> copy(final double... aSource) {

            final int tmpSize = aSource.length;

            final ComplexNumber[] tmpArray = new ComplexNumber[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toComplexNumber(aSource[i]);
            }

            return new ComplexArray(tmpArray).asArray1D();
        }

        public Array1D<ComplexNumber> copy(final List<? extends Number> aSource) {

            final int tmpSize = aSource.size();

            final ComplexNumber[] tmpArray = new ComplexNumber[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toComplexNumber(aSource.get(i));
            }

            return new ComplexArray(tmpArray).asArray1D();
        }

        public Array1D<ComplexNumber> copy(final Number... aSource) {

            final int tmpSize = aSource.length;

            final ComplexNumber[] tmpArray = new ComplexNumber[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toComplexNumber(aSource[i]);
            }

            return new ComplexArray(tmpArray).asArray1D();
        }

        public Array1D<ComplexNumber> makeRandom(final int aSize, final RandomNumber aRndm) {

            final ComplexNumber[] tmpArray = new ComplexNumber[aSize];
            for (int i = 0; i < aSize; i++) {
                tmpArray[i] = TypeUtils.toComplexNumber(aRndm);
            }

            return new ComplexArray(tmpArray).asArray1D();
        }

        public Array1D<ComplexNumber> makeZero(final int aSize) {
            return new ComplexArray(aSize).asArray1D();
        }

        public Array1D<ComplexNumber> wrap(final SimpleArray<ComplexNumber> aSimple) {
            return new ComplexArray(((SimpleArray.Complex) aSimple).data).asArray1D();
        }

    };

    public static final Array1D.Factory<Double> PRIMITIVE = new Array1D.Factory<Double>() {

        public Array1D<Double> copy(final Access1D<?> aSource) {

            final int tmpSize = aSource.size();

            final double[] tmpArray = new double[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = aSource.doubleValue(i);
            }

            return new PrimitiveArray(tmpArray).asArray1D();
        }

        public Array1D<Double> copy(final double... aSource) {
            return new PrimitiveArray(ArrayUtils.copyOf(aSource)).asArray1D();
        }

        public Array1D<Double> copy(final List<? extends Number> aSource) {

            final int tmpSize = aSource.size();

            final double[] tmpArray = new double[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = aSource.get(i).doubleValue();
            }

            return new PrimitiveArray(tmpArray).asArray1D();
        }

        public Array1D<Double> copy(final Number... aSource) {

            final int tmpSize = aSource.length;

            final double[] tmpArray = new double[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = aSource[i].doubleValue();
            }

            return new PrimitiveArray(tmpArray).asArray1D();
        }

        public Array1D<Double> makeRandom(final int aSize, final RandomNumber aRndm) {

            final double[] tmpArray = new double[aSize];
            for (int i = 0; i < aSize; i++) {
                tmpArray[i] = aRndm.doubleValue();
            }

            return new PrimitiveArray(tmpArray).asArray1D();
        }

        public Array1D<Double> makeZero(final int aSize) {
            return new PrimitiveArray(aSize).asArray1D();
        }

        public Array1D<Double> wrap(final SimpleArray<Double> aSimple) {
            return new PrimitiveArray(((SimpleArray.Primitive) aSimple).data).asArray1D();
        }

    };

    public static final Array1D.Factory<RationalNumber> RATIONAL = new Array1D.Factory<RationalNumber>() {

        public Array1D<RationalNumber> copy(final Access1D<?> aSource) {

            final int tmpSize = aSource.size();

            final RationalNumber[] tmpArray = new RationalNumber[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toRationalNumber(aSource.get(i));
            }

            return new RationalArray(tmpArray).asArray1D();
        }

        public Array1D<RationalNumber> copy(final double... aSource) {

            final int tmpSize = aSource.length;

            final RationalNumber[] tmpArray = new RationalNumber[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toRationalNumber(aSource[i]);
            }

            return new RationalArray(tmpArray).asArray1D();
        }

        public Array1D<RationalNumber> copy(final List<? extends Number> aSource) {

            final int tmpSize = aSource.size();

            final RationalNumber[] tmpArray = new RationalNumber[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toRationalNumber(aSource.get(i));
            }

            return new RationalArray(tmpArray).asArray1D();
        }

        public Array1D<RationalNumber> copy(final Number... aSource) {

            final int tmpSize = aSource.length;

            final RationalNumber[] tmpArray = new RationalNumber[tmpSize];
            for (int i = 0; i < tmpSize; i++) {
                tmpArray[i] = TypeUtils.toRationalNumber(aSource[i]);
            }

            return new RationalArray(tmpArray).asArray1D();
        }

        public Array1D<RationalNumber> makeRandom(final int aSize, final RandomNumber aRndm) {

            final RationalNumber[] tmpArray = new RationalNumber[aSize];
            for (int i = 0; i < aSize; i++) {
                tmpArray[i] = TypeUtils.toRationalNumber(aRndm);
            }

            return new RationalArray(tmpArray).asArray1D();
        }

        public Array1D<RationalNumber> makeZero(final int aSize) {
            return new RationalArray(aSize).asArray1D();
        }

        public Array1D<RationalNumber> wrap(final SimpleArray<RationalNumber> aSimple) {
            return new RationalArray(((SimpleArray.Rational) aSimple).data).asArray1D();
        }

    };

    private static final int INT_ONE = 1;
    private static final int INT_ZERO = 0;

    @SuppressWarnings("unchecked")
    private static <T extends Number> T[] copyAndSort(final Array1D<T> anArray) {

        final int tmpLength = anArray.length;
        final T[] retVal = (T[]) new Number[tmpLength];

        for (int i = INT_ZERO; i < tmpLength; i++) {
            retVal[i] = anArray.get(i);
        }

        Arrays.sort(retVal);

        return retVal;
    }

    public final int length;

    private final BasicArray<N> myDelegate;

    private final int myFirst;
    private final int myLimit;
    private final int myStep;

    @SuppressWarnings("unused")
    private Array1D() {
        this(null);
    }

    Array1D(final BasicArray<N> aDelegate) {
        this(aDelegate, INT_ZERO, aDelegate.length, INT_ONE);
    }

    Array1D(final BasicArray<N> aDelegate, final int aFirst, final int aLimit, final int aStep) {

        super();

        myDelegate = aDelegate;

        myFirst = aFirst;
        myLimit = aLimit;
        myStep = aStep;

        length = (myLimit - myFirst) / myStep;
    }

    @Override
    public boolean contains(final Object anObj) {
        return this.indexOf(anObj) != -INT_ONE;
    }

    @SuppressWarnings("unchecked")
    public Array1D<N> copy() {

        BasicArray<N> retVal = null;

        if (myDelegate instanceof PrimitiveArray) {

            retVal = (BasicArray<N>) new PrimitiveArray(length);

            for (int i = 0; i < length; i++) {
                retVal.set(i, this.doubleValue(i));
            }

        } else if (myDelegate instanceof ComplexArray) {

            retVal = (BasicArray<N>) new ComplexArray(length);

            for (int i = 0; i < length; i++) {
                retVal.set(i, this.get(i));
            }

        } else if (myDelegate instanceof BigArray) {

            retVal = (BasicArray<N>) new BigArray(length);

            for (int i = 0; i < length; i++) {
                retVal.set(i, this.get(i));
            }
        }

        return new Array1D<N>(retVal);
    }

    public double doubleValue(final int anInd) {
        return myDelegate.doubleValue(myFirst + (myStep * anInd));
    }

    public void fillAll(final N aNmbr) {
        myDelegate.fill(myFirst, myLimit, myStep, aNmbr);
    }

    public void fillRange(final int aFirst, final int aLimit, final N aNmbr) {
        myDelegate.fill(myFirst + (myStep * aFirst), myFirst + (myStep * aLimit), myStep, aNmbr);
    }

    @Override
    public N get(final int anInd) {
        return myDelegate.get(myFirst + (myStep * anInd));
    }

    public int getIndexOfLargestInRange(final int aFirst, final int aLimit) {
        return (myDelegate.getIndexOfLargest(myFirst + (myStep * aFirst), myFirst + (myStep * aLimit), myStep) - myFirst) / myStep;
    }

    @Override
    public int indexOf(final Object anObj) {
        final int tmpLength = length;
        if (anObj == null) {
            for (int i = INT_ZERO; i < tmpLength; i++) {
                if (this.get(i) == null) {
                    return i;
                }
            }
        } else if (anObj instanceof Number) {
            for (int i = INT_ZERO; i < tmpLength; i++) {
                if (anObj.equals(this.get(i))) {
                    return i;
                }
            }
        }
        return -INT_ONE;
    }

    /**
     * @see Scalar#isAbsolute()
     */
    public boolean isAbsolute(final int anInd) {
        return myDelegate.isAbsolute(myFirst + (myStep * anInd));
    }

    public boolean isAllZeros() {
        return myDelegate.isZeros(myFirst, myLimit, myStep);
    }

    @Override
    public boolean isEmpty() {
        return length == INT_ZERO;
    }

    /**
     * @see Scalar#isPositive()
     */
    public boolean isPositive(final int anInd) {
        return myDelegate.isPositive(myFirst + (myStep * anInd));
    }

    public boolean isRangeZeros(final int aFirst, final int aLimit) {
        return myDelegate.isZeros(myFirst + (myStep * aFirst), myFirst + (myStep * aLimit), myStep);
    }

    /**
     * @see Scalar#isReal()
     */
    public boolean isReal(final int anInd) {
        return myDelegate.isReal(myFirst + (myStep * anInd));
    }

    /**
     * @see Scalar#isZero()
     */
    public boolean isZero(final int anInd) {
        return myDelegate.isZero(myFirst + (myStep * anInd));
    }

    @Override
    public final Iterator<N> iterator() {
        return new Iterator1D<N>(this);
    }

    public void modifyAll(final BinaryFunction<N> aFunc, final N aNmbr) {
        myDelegate.modify(myFirst, myLimit, myStep, aFunc, aNmbr);
    }

    public void modifyAll(final N aNmbr, final BinaryFunction<N> aFunc) {
        myDelegate.modify(myFirst, myLimit, myStep, aNmbr, aFunc);
    }

    public void modifyAll(final ParameterFunction<N> aFunc, final int aParam) {
        myDelegate.modify(myFirst, myLimit, myStep, aFunc, aParam);
    }

    public void modifyAll(final UnaryFunction<N> aFunc) {
        myDelegate.modify(myFirst, myLimit, myStep, aFunc);
    }

    public void modifyMatching(final Access1D<N> anArray, final BinaryFunction<N> aFunc) {
        final int tmpLength = Math.min(length, anArray.size());
        if (myDelegate instanceof PrimitiveArray) {
            for (int i = INT_ZERO; i < tmpLength; i++) {
                this.set(i, aFunc.invoke(anArray.doubleValue(i), this.doubleValue(i)));
            }
        } else {
            for (int i = INT_ZERO; i < tmpLength; i++) {
                this.set(i, aFunc.invoke(anArray.get(i), this.get(i)));
            }
        }
    }

    public void modifyMatching(final BinaryFunction<N> aFunc, final Access1D<N> anArray) {
        final int tmpLength = Math.min(length, anArray.size());
        if (myDelegate instanceof PrimitiveArray) {
            for (int i = INT_ZERO; i < tmpLength; i++) {
                this.set(i, aFunc.invoke(this.doubleValue(i), anArray.doubleValue(i)));
            }
        } else {
            for (int i = INT_ZERO; i < tmpLength; i++) {
                this.set(i, aFunc.invoke(this.get(i), anArray.get(i)));
            }
        }
    }

    public void modifyRange(final int aFirst, final int aLimit, final BinaryFunction<N> aFunc, final N aNmbr) {
        myDelegate.modify(myFirst + (myStep * aFirst), myFirst + (myStep * aLimit), myStep, aFunc, aNmbr);
    }

    public void modifyRange(final int aFirst, final int aLimit, final N aNmbr, final BinaryFunction<N> aFunc) {
        myDelegate.modify(myFirst + (myStep * aFirst), myFirst + (myStep * aLimit), myStep, aNmbr, aFunc);
    }

    public void modifyRange(final int aFirst, final int aLimit, final ParameterFunction<N> aFunc, final int aParam) {
        myDelegate.modify(myFirst + (myStep * aFirst), myFirst + (myStep * aLimit), myStep, aFunc, aParam);
    }

    public void modifyRange(final int aFirst, final int aLimit, final UnaryFunction<N> aFunc) {
        myDelegate.modify(myFirst + (myStep * aFirst), myFirst + (myStep * aLimit), myStep, aFunc);
    }

    /**
     * Asssumes you have first called {@linkplain #sortAscending()}.
     */
    public int searchAscending(final N aKey) {

        final int tmpLength = length;
        if (tmpLength != myDelegate.length) {

            final Number[] tmpArray = new Number[tmpLength];

            for (int i = INT_ZERO; i < tmpLength; i++) {
                tmpArray[i] = this.get(i);
            }

            return Arrays.binarySearch(tmpArray, aKey);

        } else {
            return myDelegate.searchAscending(aKey);
        }
    }

    /**
     * Asssumes you have first called {@linkplain #sortDescending()}.
     */
    public int searchDescending(final N aKey) {

        final int tmpLength = length;
        final Number[] tmpArray = new Number[tmpLength];

        for (int i = INT_ZERO; i < tmpLength; i++) {
            tmpArray[i] = this.get(tmpLength - INT_ONE - i);
        }

        final int tmpInd = Arrays.binarySearch(tmpArray, aKey);

        if (tmpInd >= INT_ZERO) {
            return tmpLength - INT_ONE - tmpInd;
        } else if (tmpInd < -INT_ONE) {
            return -tmpLength - tmpInd - INT_ONE;
        } else {
            return -INT_ONE;
        }
    }

    public void set(final int anInd, final double aNmbr) {
        myDelegate.set(myFirst + (myStep * anInd), aNmbr);
    }

    @Override
    public N set(final int anInd, final N aNmbr) {
        final int tmpIndex = myFirst + (myStep * anInd);
        final N retVal = myDelegate.get(tmpIndex);
        myDelegate.set(tmpIndex, aNmbr);
        return retVal;
    }

    @Override
    public int size() {
        return length;
    }

    public void sortAscending() {

        final int tmpLength = length;
        if (tmpLength != myDelegate.length) {

            final N[] tmpArray = Array1D.copyAndSort(this);

            for (int i = INT_ZERO; i < tmpLength; i++) {
                this.set(i, tmpArray[i]);
            }
        } else {
            myDelegate.sortAscending();
        }
    }

    public void sortDescending() {

        final N[] tmpArray = Array1D.copyAndSort(this);

        final int tmpLength = length;
        for (int i = INT_ZERO; i < tmpLength; i++) {
            this.set(i, tmpArray[tmpLength - INT_ONE - i]);
        }
    }

    @Override
    public Array1D<N> subList(final int aFirst, final int aLimit) {
        return new Array1D<N>(myDelegate, myFirst + (myStep * aFirst), myFirst + (myStep * aLimit), myStep);
    }

    public double[] toRawCopy() {

        final int tmpLength = length;
        final double[] retVal = new double[tmpLength];

        for (int i = INT_ZERO; i < tmpLength; i++) {
            retVal[i] = this.doubleValue(i);
        }

        return retVal;
    }

    public Scalar<N> toScalar(final int anInd) {
        return myDelegate.toScalar(myFirst + (myStep * anInd));
    }

    public void visitAll(final AggregatorFunction<N> aVisitor) {
        myDelegate.visit(myFirst, myLimit, myStep, aVisitor);
    }

    public void visitRange(final int aFirst, final int aLimit, final AggregatorFunction<N> aVisitor) {
        myDelegate.visit(myFirst + (myStep * aFirst), myFirst + (myStep * aLimit), myStep, aVisitor);
    }

    BasicArray<N> getDelegate() {
        return myDelegate;
    }

}
