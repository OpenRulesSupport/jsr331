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
import java.util.Arrays;
import java.util.Iterator;

import org.ojalgo.access.AccessAnyD;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.access.FactoryAnyD;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;

/**
 * ArrayAnyD
 *
 * @author apete
 */
public final class ArrayAnyD<N extends Number> implements AccessAnyD<N>, Serializable {

    public static final FactoryAnyD<ArrayAnyD<BigDecimal>> BIG = new FactoryAnyD<ArrayAnyD<BigDecimal>>() {

        public ArrayAnyD<BigDecimal> copy(final AccessAnyD<?> aSource) {

            final int tmpSize = aSource.size();

            final BigDecimal[] tmpData = new BigDecimal[tmpSize];
            final int[] tmpStructure = AccessUtils.structure(aSource);

            for (int i = 0; i < tmpSize; i++) {
                tmpData[i] = TypeUtils.toBigDecimal(aSource.get(i));
            }

            return new BigArray(tmpData).asArrayAnyD(tmpStructure);
        }

        public ArrayAnyD<BigDecimal> makeRandom(final RandomNumber aRndm, final int... aStructure) {

            final int tmpSize = AccessUtils.size(aStructure);

            final BigDecimal[] tmpData = new BigDecimal[tmpSize];

            for (int i = 0; i < tmpSize; i++) {
                tmpData[i] = TypeUtils.toBigDecimal(aRndm.doubleValue());
            }

            return new BigArray(tmpData).asArrayAnyD(aStructure);
        }

        public ArrayAnyD<BigDecimal> makeZero(final int... aStructure) {
            return new BigArray(AccessUtils.size(aStructure)).asArrayAnyD(aStructure);
        }

    };

    public static final FactoryAnyD<ArrayAnyD<ComplexNumber>> COMPLEX = new FactoryAnyD<ArrayAnyD<ComplexNumber>>() {

        public ArrayAnyD<ComplexNumber> copy(final AccessAnyD<?> aSource) {

            final int tmpSize = aSource.size();

            final ComplexNumber[] tmpData = new ComplexNumber[tmpSize];
            final int[] tmpStructure = AccessUtils.structure(aSource);

            for (int i = 0; i < tmpSize; i++) {
                tmpData[i] = TypeUtils.toComplexNumber(aSource.get(i));
            }

            return new ComplexArray(tmpData).asArrayAnyD(tmpStructure);
        }

        public ArrayAnyD<ComplexNumber> makeRandom(final RandomNumber aRndm, final int... aStructure) {

            final int tmpSize = AccessUtils.size(aStructure);

            final ComplexNumber[] tmpData = new ComplexNumber[tmpSize];

            for (int i = 0; i < tmpSize; i++) {
                tmpData[i] = TypeUtils.toComplexNumber(aRndm.doubleValue());
            }

            return new ComplexArray(tmpData).asArrayAnyD(aStructure);
        }

        public ArrayAnyD<ComplexNumber> makeZero(final int... aStructure) {
            return new ComplexArray(AccessUtils.size(aStructure)).asArrayAnyD(aStructure);
        }

    };

    public static final FactoryAnyD<ArrayAnyD<Double>> PRIMITIVE = new FactoryAnyD<ArrayAnyD<Double>>() {

        public ArrayAnyD<Double> copy(final AccessAnyD<?> aSource) {

            final int tmpSize = aSource.size();

            final double[] tmpData = new double[tmpSize];
            final int[] tmpStructure = AccessUtils.structure(aSource);

            for (int i = 0; i < tmpSize; i++) {
                tmpData[i] = aSource.doubleValue(i);
            }

            return new PrimitiveArray(tmpData).asArrayAnyD(tmpStructure);
        }

        public ArrayAnyD<Double> makeRandom(final RandomNumber aRndm, final int... aStructure) {

            final int tmpSize = AccessUtils.size(aStructure);

            final double[] tmpData = new double[tmpSize];

            for (int i = 0; i < tmpSize; i++) {
                tmpData[i] = aRndm.doubleValue();
            }

            return new PrimitiveArray(tmpData).asArrayAnyD(aStructure);
        }

        public ArrayAnyD<Double> makeZero(final int... aStructure) {
            return new PrimitiveArray(AccessUtils.size(aStructure)).asArrayAnyD(aStructure);
        }

    };

    private static final int INT_ONE = 1;
    private static final int INT_ZERO = 0;

    private final BasicArray<N> myDelegate;
    private final int[] myStructure;

    @SuppressWarnings("unused")
    private ArrayAnyD() {
        this(null, new int[INT_ZERO]);
    }

    ArrayAnyD(final BasicArray<N> aDelegate, final int[] aStructure) {

        super();

        myDelegate = aDelegate;
        myStructure = aStructure;
    }

    /**
     * Flattens this abitrary dimensional array to a one dimensional array.
     * The (internal/actual) array is not copied, it is just accessed through
     * a different adaptor.
     */
    public Array1D<N> asArray1D() {
        return myDelegate.asArray1D();
    }

    public double doubleValue(final int... aRef) {
        return myDelegate.doubleValue(AccessUtils.index(myStructure, aRef));
    }

    public double doubleValue(final int anInd) {
        return myDelegate.doubleValue(anInd);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ArrayAnyD) {
            final ArrayAnyD<N> tmpObj = (ArrayAnyD<N>) obj;
            return Arrays.equals(myStructure, tmpObj.structure()) && myDelegate.equals(tmpObj.getDelegate());
        } else {
            return super.equals(obj);
        }
    }

    public void fillAll(final N aNmbr) {
        myDelegate.fill(INT_ZERO, myDelegate.length, INT_ONE, aNmbr);
    }

    public void fillSet(final int[] aFirst, final int aDim, final N aNmbr) {

        final int tmpCount = AccessUtils.size(myStructure, aDim) - aFirst[aDim];

        final int tmpFirst = AccessUtils.index(myStructure, aFirst);
        final int tmpStep = AccessUtils.step(myStructure, aDim);
        final int tmpLimit = tmpFirst * tmpStep * tmpCount;

        myDelegate.fill(tmpFirst, tmpLimit, tmpStep, aNmbr);
    }

    public N get(final int... aRef) {
        return myDelegate.get(AccessUtils.index(myStructure, aRef));
    }

    public N get(final int anInd) {
        return myDelegate.get(anInd);
    }

    /**
     * @deprecated v33
     */
    @Deprecated
    public int getColDim() {
        return this.size(INT_ONE);
    }

    /**
     * @deprecated v33
     */
    @Deprecated
    public int getMinDim() {
        return MatrixUtils.min(myStructure);
    }

    /**
     * @deprecated v33
     */
    @Deprecated
    public int getRowDim() {
        return this.size(INT_ZERO);
    }

    @Override
    public int hashCode() {
        return myDelegate.hashCode();
    }

    /**
     * @see Scalar#isAbsolute()
     */
    public boolean isAbsolute(final int[] aRef) {
        return myDelegate.isAbsolute(AccessUtils.index(myStructure, aRef));
    }

    public boolean isAllZeros() {
        return myDelegate.isZeros(INT_ZERO, myDelegate.length, INT_ONE);
    }

    /**
     * @see Scalar#isPositive()
     */
    public boolean isPositive(final int[] aRef) {
        return myDelegate.isPositive(AccessUtils.index(myStructure, aRef));
    }

    /**
     * @see Scalar#isReal()
     */
    public boolean isReal(final int[] aRef) {
        return myDelegate.isReal(AccessUtils.index(myStructure, aRef));
    }

    /**
     * @see Scalar#isZero()
     */
    public boolean isZero(final int[] aRef) {
        return myDelegate.isZero(AccessUtils.index(myStructure, aRef));
    }

    public boolean isZeros(final int[] aFirst, final int aDim) {

        final int tmpCount = AccessUtils.size(myStructure, aDim) - aFirst[aDim];

        final int tmpFirst = AccessUtils.index(myStructure, aFirst);
        final int tmpStep = AccessUtils.step(myStructure, aDim);
        final int tmpLimit = tmpFirst * tmpStep * tmpCount;

        return myDelegate.isZeros(tmpFirst, tmpLimit, tmpStep);
    }

    public Iterator<N> iterator() {
        return new Iterator1D<N>(this);
    }

    public void modifyAll(final BinaryFunction<N> aFunc, final N aNmbr) {
        myDelegate.modify(INT_ZERO, myDelegate.length, INT_ONE, aFunc, aNmbr);
    }

    public void modifyAll(final N aNmbr, final BinaryFunction<N> aFunc) {
        myDelegate.modify(INT_ZERO, myDelegate.length, INT_ONE, aNmbr, aFunc);
    }

    public void modifyAll(final ParameterFunction<N> aFunc, final int aParam) {
        myDelegate.modify(INT_ZERO, myDelegate.length, INT_ONE, aFunc, aParam);
    }

    public void modifyAll(final UnaryFunction<N> aFunc) {
        myDelegate.modify(INT_ZERO, myDelegate.length, INT_ONE, aFunc);
    }

    public void modifyMatching(final ArrayAnyD<N> aLeftArg, final BinaryFunction<N> aFunc) {
        myDelegate.modify(INT_ZERO, myDelegate.length, INT_ONE, aLeftArg.getDelegate(), aFunc);
    }

    public void modifyMatching(final BinaryFunction<N> aFunc, final ArrayAnyD<N> aRightArg) {
        myDelegate.modify(INT_ZERO, myDelegate.length, INT_ONE, aFunc, aRightArg.getDelegate());
    }

    public void modifySet(final int[] aFirst, final int aDim, final BinaryFunction<N> aFunc, final N aNmbr) {

        final int tmpCount = AccessUtils.size(myStructure, aDim) - aFirst[aDim];

        final int tmpFirst = AccessUtils.index(myStructure, aFirst);
        final int tmpStep = AccessUtils.step(myStructure, aDim);
        final int tmpLimit = tmpFirst * tmpStep * tmpCount;

        myDelegate.modify(tmpFirst, tmpLimit, tmpStep, aFunc, aNmbr);
    }

    public void modifySet(final int[] aFirst, final int aDim, final N aNmbr, final BinaryFunction<N> aFunc) {

        final int tmpCount = AccessUtils.size(myStructure, aDim) - aFirst[aDim];

        final int tmpFirst = AccessUtils.index(myStructure, aFirst);
        final int tmpStep = AccessUtils.step(myStructure, aDim);
        final int tmpLimit = tmpFirst * tmpStep * tmpCount;

        myDelegate.modify(tmpFirst, tmpLimit, tmpStep, aNmbr, aFunc);
    }

    public void modifySet(final int[] aFirst, final int aDim, final ParameterFunction<N> aFunc, final int aParam) {

        final int tmpCount = AccessUtils.size(myStructure, aDim) - aFirst[aDim];

        final int tmpFirst = AccessUtils.index(myStructure, aFirst);
        final int tmpStep = AccessUtils.step(myStructure, aDim);
        final int tmpLimit = tmpFirst * tmpStep * tmpCount;

        myDelegate.modify(tmpFirst, tmpLimit, tmpStep, aFunc, aParam);
    }

    public void modifySet(final int[] aFirst, final int aDim, final UnaryFunction<N> aFunc) {

        final int tmpCount = AccessUtils.size(myStructure, aDim) - aFirst[aDim];

        final int tmpFirst = AccessUtils.index(myStructure, aFirst);
        final int tmpStep = AccessUtils.step(myStructure, aDim);
        final int tmpLimit = tmpFirst * tmpStep * tmpCount;

        myDelegate.modify(tmpFirst, tmpLimit, tmpStep, aFunc);
    }

    public int rank() {
        return myStructure.length;
    }

    public void set(final int[] aRef, final double aNmbr) {
        myDelegate.set(AccessUtils.index(myStructure, aRef), aNmbr);
    }

    public void set(final int[] aRef, final N aNmbr) {
        myDelegate.set(AccessUtils.index(myStructure, aRef), aNmbr);
    }

    public int size() {
        return myDelegate.length;
    }

    public int size(final int aDim) {
        return AccessUtils.size(myStructure, aDim);
    }

    public Array1D<N> slice(final int[] aFirst, final int aDim) {

        final int tmpCount = AccessUtils.size(myStructure, aDim) - aFirst[aDim];

        final int tmpFirst = AccessUtils.index(myStructure, aFirst);
        final int tmpStep = AccessUtils.step(myStructure, aDim);
        final int tmpLimit = tmpFirst * tmpStep * tmpCount;

        return new Array1D<N>(myDelegate, tmpFirst, tmpLimit, tmpStep);
    }

    public int[] structure() {
        return myStructure;
    }

    public Scalar<N> toScalar(final int... aRef) {
        return myDelegate.toScalar(AccessUtils.index(myStructure, aRef));
    }

    @Override
    public String toString() {

        final StringBuilder retVal = new StringBuilder();

        retVal.append('<');
        retVal.append(myStructure[INT_ZERO]);
        for (int i = INT_ONE; i < myStructure.length; i++) {
            retVal.append('x');
            retVal.append(myStructure[i]);
        }
        retVal.append('>');

        final int tmpLength = this.size();
        if ((tmpLength >= INT_ONE) && (tmpLength <= 100)) {
            retVal.append(' ');
            retVal.append(myDelegate.toString());
        }

        return retVal.toString();
    }

    public void visitAll(final AggregatorFunction<N> aVisitor) {
        myDelegate.visit(INT_ZERO, myDelegate.length, INT_ONE, aVisitor);
    }

    public void visitSet(final int[] aFirst, final int aDim, final AggregatorFunction<N> aVisitor) {

        final int tmpCount = AccessUtils.size(myStructure, aDim) - aFirst[aDim];

        final int tmpFirst = AccessUtils.index(myStructure, aFirst);
        final int tmpStep = AccessUtils.step(myStructure, aDim);
        final int tmpLimit = tmpFirst * tmpStep * tmpCount;

        myDelegate.visit(tmpFirst, tmpLimit, tmpStep, aVisitor);
    }

    final BasicArray<N> getDelegate() {
        return myDelegate;
    }

}
