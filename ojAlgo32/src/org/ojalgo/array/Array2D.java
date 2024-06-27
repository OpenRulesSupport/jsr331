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
import java.util.Iterator;
import java.util.List;

import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.Factory2D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;

/**
 * Array2D
 *
 * @author apete
 */
public final class Array2D<N extends Number> implements Access2D<N>, Serializable {

    public static final Factory2D<Array2D<BigDecimal>> BIG = new Factory2D<Array2D<BigDecimal>>() {

        public Array2D<BigDecimal> columns(final Access1D<?>... aSource) {
            return BigDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<BigDecimal> columns(final double[]... aSource) {
            return BigDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<BigDecimal> columns(final List<? extends Number>... aSource) {
            return BigDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<BigDecimal> columns(final Number[]... aSource) {
            return BigDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<BigDecimal> copy(final Access2D<?> aSource) {
            return BigDenseStore.FACTORY.copy(aSource).asArray2D();
        }

        public Array2D<BigDecimal> makeEye(final int aRowDim, final int aColDim) {
            return BigDenseStore.FACTORY.makeEye(aRowDim, aColDim).asArray2D();
        }

        public Array2D<BigDecimal> makeRandom(final int aRowDim, final int aColDim, final RandomNumber aRndm) {
            return BigDenseStore.FACTORY.makeRandom(aRowDim, aColDim, aRndm).asArray2D();
        }

        public Array2D<BigDecimal> makeZero(final int aRowDim, final int aColDim) {
            return BigDenseStore.FACTORY.makeZero(aRowDim, aColDim).asArray2D();
        }

        public Array2D<BigDecimal> rows(final Access1D<?>... aSource) {
            return BigDenseStore.FACTORY.rows(aSource).asArray2D();
        }

        public Array2D<BigDecimal> rows(final double[]... aSource) {
            return BigDenseStore.FACTORY.rows(aSource).asArray2D();
        }

        public Array2D<BigDecimal> rows(final List<? extends Number>... aSource) {
            return BigDenseStore.FACTORY.rows(aSource).asArray2D();
        }

        public Array2D<BigDecimal> rows(final Number[]... aSource) {
            return BigDenseStore.FACTORY.rows(aSource).asArray2D();
        }

    };

    public static final Factory2D<Array2D<ComplexNumber>> COMPLEX = new Factory2D<Array2D<ComplexNumber>>() {

        public Array2D<ComplexNumber> columns(final Access1D<?>... aSource) {
            return ComplexDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<ComplexNumber> columns(final double[]... aSource) {
            return ComplexDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<ComplexNumber> columns(final List<? extends Number>... aSource) {
            return ComplexDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<ComplexNumber> columns(final Number[]... aSource) {
            return ComplexDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<ComplexNumber> copy(final Access2D<?> aSource) {
            return ComplexDenseStore.FACTORY.copy(aSource).asArray2D();
        }

        public Array2D<ComplexNumber> makeEye(final int aRowDim, final int aColDim) {
            return ComplexDenseStore.FACTORY.makeEye(aRowDim, aColDim).asArray2D();
        }

        public Array2D<ComplexNumber> makeRandom(final int aRowDim, final int aColDim, final RandomNumber aRndm) {
            return ComplexDenseStore.FACTORY.makeRandom(aRowDim, aColDim, aRndm).asArray2D();
        }

        public Array2D<ComplexNumber> makeZero(final int aRowDim, final int aColDim) {
            return ComplexDenseStore.FACTORY.makeZero(aRowDim, aColDim).asArray2D();
        }

        public Array2D<ComplexNumber> rows(final Access1D<?>... aSource) {
            return ComplexDenseStore.FACTORY.rows(aSource).asArray2D();
        }

        public Array2D<ComplexNumber> rows(final double[]... aSource) {
            return ComplexDenseStore.FACTORY.rows(aSource).asArray2D();
        }

        public Array2D<ComplexNumber> rows(final List<? extends Number>... aSource) {
            return ComplexDenseStore.FACTORY.rows(aSource).asArray2D();
        }

        public Array2D<ComplexNumber> rows(final Number[]... aSource) {
            return ComplexDenseStore.FACTORY.rows(aSource).asArray2D();
        }

    };

    public static final Factory2D<Array2D<Double>> PRIMITIVE = new Factory2D<Array2D<Double>>() {

        public Array2D<Double> columns(final Access1D<?>... aSource) {
            return PrimitiveDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<Double> columns(final double[]... aSource) {
            return PrimitiveDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<Double> columns(final List<? extends Number>... aSource) {
            return PrimitiveDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<Double> columns(final Number[]... aSource) {
            return PrimitiveDenseStore.FACTORY.columns(aSource).asArray2D();
        }

        public Array2D<Double> copy(final Access2D<?> aSource) {
            return PrimitiveDenseStore.FACTORY.copy(aSource).asArray2D();
        }

        public Array2D<Double> makeEye(final int aRowDim, final int aColDim) {
            return PrimitiveDenseStore.FACTORY.makeEye(aRowDim, aColDim).asArray2D();
        }

        public Array2D<Double> makeRandom(final int aRowDim, final int aColDim, final RandomNumber aRndm) {
            return PrimitiveDenseStore.FACTORY.makeRandom(aRowDim, aColDim, aRndm).asArray2D();
        }

        public Array2D<Double> makeZero(final int aRowDim, final int aColDim) {
            return PrimitiveDenseStore.FACTORY.makeZero(aRowDim, aColDim).asArray2D();
        }

        public Array2D<Double> rows(final Access1D<?>... aSource) {
            return PrimitiveDenseStore.FACTORY.rows(aSource).asArray2D();
        }

        public Array2D<Double> rows(final double[]... aSource) {
            return PrimitiveDenseStore.FACTORY.rows(aSource).asArray2D();
        }

        public Array2D<Double> rows(final List<? extends Number>... aSource) {
            return PrimitiveDenseStore.FACTORY.rows(aSource).asArray2D();
        }

        public Array2D<Double> rows(final Number[]... aSource) {
            return PrimitiveDenseStore.FACTORY.rows(aSource).asArray2D();
        }

    };

    private static final int INT_ONE = 1;
    private static final int INT_ZERO = 0;

    private final int myColDim;

    private final BasicArray<N> myDelegate;
    private final int myRowDim;

    @SuppressWarnings("unused")
    private Array2D() {
        this(null, INT_ZERO, INT_ZERO);
    }

    Array2D(final BasicArray<N> aDelegate, final int aRowDim, final int aColDim) {

        super();

        myDelegate = aDelegate;

        myRowDim = aRowDim;
        myColDim = aColDim;
    }

    /**
     * Flattens this two dimensional array to a one dimensional array.
     * The (internal/actual) array is not copied, it is just accessed through
     * a different adaptor.
     */
    public Array1D<N> asArray1D() {
        return myDelegate.asArray1D();
    }

    public double doubleValue(final int anInd) {
        return myDelegate.doubleValue(anInd);
    }

    public double doubleValue(final int aRow, final int aCol) {
        return myDelegate.doubleValue(aRow + (aCol * myRowDim));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Array2D) {
            final Array2D<N> tmpObj = (Array2D<N>) obj;
            return (myRowDim == tmpObj.getRowDim()) && (myColDim == tmpObj.getColDim()) && myDelegate.equals(tmpObj.getDelegate());
        } else {
            return super.equals(obj);
        }
    }

    public void exchangeColumns(final int aColA, final int aColB) {
        myDelegate.exchange(aColA * myRowDim, aColB * myRowDim, INT_ONE, myRowDim);
    }

    public void exchangeRows(final int aRowA, final int aRowB) {
        myDelegate.exchange(aRowA, aRowB, myRowDim, myColDim);
    }

    public void fillAll(final N aNmbr) {
        myDelegate.fill(INT_ZERO, myDelegate.length, INT_ONE, aNmbr);
    }

    public void fillColumn(final int aRow, final int aCol, final N aNmbr) {
        myDelegate.fill(aRow + (aCol * myRowDim), myRowDim + (aCol * myRowDim), INT_ONE, aNmbr);
    }

    public void fillDiagonal(final int aRow, final int aCol, final N aNmbr) {

        final int tmpCount = Math.min(myRowDim - aRow, myColDim - aCol);

        myDelegate.fill(aRow + (aCol * myRowDim), aRow + tmpCount + ((aCol + tmpCount) * myRowDim), INT_ONE + myRowDim, aNmbr);
    }

    public void fillMatching(final Array2D<N> aLeftArg, final BinaryFunction<N> aFunc, final Array2D<N> aRightArg) {
        myDelegate.fill(0, myDelegate.length, aLeftArg.getDelegate(), aFunc, aRightArg.getDelegate());
    }

    public void fillRow(final int aRow, final int aCol, final N aNmbr) {
        myDelegate.fill(aRow + (aCol * myRowDim), aRow + (myColDim * myRowDim), myRowDim, aNmbr);
    }

    public N get(final int anInd) {
        return myDelegate.get(anInd);
    }

    public N get(final int aRow, final int aCol) {
        return myDelegate.get(aRow + (aCol * myRowDim));
    }

    public int getColDim() {
        return myColDim;
    }

    public int getIndexOfLargestInColumn(final int aRow, final int aCol) {
        return myDelegate.getIndexOfLargest(aRow + (aCol * myRowDim), myRowDim + (aCol * myRowDim), INT_ONE) % myRowDim;
    }

    public int getIndexOfLargestInRow(final int aRow, final int aCol) {
        return myDelegate.getIndexOfLargest(aRow + (aCol * myRowDim), aRow + (myColDim * myRowDim), myRowDim) / myRowDim;
    }

    public int getMaxDim() {
        return Math.max(myRowDim, myColDim);
    }

    public int getMinDim() {
        return Math.min(myRowDim, myColDim);
    }

    public int getRowDim() {
        return myRowDim;
    }

    @Override
    public int hashCode() {
        return myRowDim * myColDim * myDelegate.hashCode();
    }

    /**
     * @see Scalar#isAbsolute()
     */
    public boolean isAbsolute(final int aRow, final int aCol) {
        return myDelegate.isAbsolute(aRow + (aCol * myRowDim));
    }

    public boolean isAllZeros() {
        return myDelegate.isZeros(INT_ZERO, myDelegate.length, INT_ONE);
    }

    public boolean isColumnZeros(final int aRow, final int aCol) {
        return myDelegate.isZeros(aRow + (aCol * myRowDim), myRowDim + (aCol * myRowDim), INT_ONE);
    }

    public boolean isDiagonalZeros(final int aRow, final int aCol) {

        final int tmpCount = Math.min(myRowDim - aRow, myColDim - aCol);

        return myDelegate.isZeros(aRow + (aCol * myRowDim), aRow + tmpCount + ((aCol + tmpCount) * myRowDim), INT_ONE + myRowDim);
    }

    /**
     * @see Scalar#isPositive()
     */
    public boolean isPositive(final int aRow, final int aCol) {
        return myDelegate.isPositive(aRow + (aCol * myRowDim));
    }

    /**
     * @see Scalar#isReal()
     */
    public boolean isReal(final int aRow, final int aCol) {
        return myDelegate.isReal(aRow + (aCol * myRowDim));
    }

    public boolean isRowZeros(final int aRow, final int aCol) {
        return myDelegate.isZeros(aRow + (aCol * myRowDim), aRow + (myColDim * myRowDim), myRowDim);
    }

    /**
     * @see Scalar#isZero()
     */
    public boolean isZero(final int aRow, final int aCol) {
        return myDelegate.isZero(aRow + (aCol * myRowDim));
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

    public void modifyColumn(final int aRow, final int aCol, final BinaryFunction<N> aFunc, final N aNmbr) {
        myDelegate.modify(aRow + (aCol * myRowDim), myRowDim + (aCol * myRowDim), INT_ONE, aFunc, aNmbr);
    }

    public void modifyColumn(final int aRow, final int aCol, final N aNmbr, final BinaryFunction<N> aFunc) {
        myDelegate.modify(aRow + (aCol * myRowDim), myRowDim + (aCol * myRowDim), INT_ONE, aNmbr, aFunc);
    }

    public void modifyColumn(final int aRow, final int aCol, final ParameterFunction<N> aFunc, final int aParam) {
        myDelegate.modify(aRow + (aCol * myRowDim), myRowDim + (aCol * myRowDim), INT_ONE, aFunc, aParam);
    }

    public void modifyColumn(final int aRow, final int aCol, final UnaryFunction<N> aFunc) {
        myDelegate.modify(aRow + (aCol * myRowDim), myRowDim + (aCol * myRowDim), INT_ONE, aFunc);
    }

    public void modifyDiagonal(final int aRow, final int aCol, final BinaryFunction<N> aFunc, final N aNmbr) {

        final int tmpCount = Math.min(myRowDim - aRow, myColDim - aCol);

        myDelegate.modify(aRow + (aCol * myRowDim), aRow + tmpCount + ((aCol + tmpCount) * myRowDim), INT_ONE + myRowDim, aFunc, aNmbr);
    }

    public void modifyDiagonal(final int aRow, final int aCol, final N aNmbr, final BinaryFunction<N> aFunc) {

        final int tmpCount = Math.min(myRowDim - aRow, myColDim - aCol);

        myDelegate.modify(aRow + (aCol * myRowDim), aRow + tmpCount + ((aCol + tmpCount) * myRowDim), INT_ONE + myRowDim, aNmbr, aFunc);
    }

    public void modifyDiagonal(final int aRow, final int aCol, final ParameterFunction<N> aFunc, final int aParam) {

        final int tmpCount = Math.min(myRowDim - aRow, myColDim - aCol);

        myDelegate.modify(aRow + (aCol * myRowDim), aRow + tmpCount + ((aCol + tmpCount) * myRowDim), INT_ONE + myRowDim, aFunc, aParam);
    }

    public void modifyDiagonal(final int aRow, final int aCol, final UnaryFunction<N> aFunc) {

        final int tmpCount = Math.min(myRowDim - aRow, myColDim - aCol);

        myDelegate.modify(aRow + (aCol * myRowDim), aRow + tmpCount + ((aCol + tmpCount) * myRowDim), INT_ONE + myRowDim, aFunc);
    }

    public void modifyMatching(final Array2D<N> aLeftArg, final BinaryFunction<N> aFunc) {
        myDelegate.modify(INT_ZERO, myDelegate.length, INT_ONE, aLeftArg.getDelegate(), aFunc);
    }

    public void modifyMatching(final BinaryFunction<N> aFunc, final Array2D<N> aRightArg) {
        myDelegate.modify(INT_ZERO, myDelegate.length, INT_ONE, aFunc, aRightArg.getDelegate());
    }

    public void modifyRow(final int aRow, final int aCol, final BinaryFunction<N> aFunc, final N aNmbr) {
        myDelegate.modify(aRow + (aCol * myRowDim), aRow + (myColDim * myRowDim), myRowDim, aFunc, aNmbr);
    }

    public void modifyRow(final int aRow, final int aCol, final N aNmbr, final BinaryFunction<N> aFunc) {
        myDelegate.modify(aRow + (aCol * myRowDim), aRow + (myColDim * myRowDim), myRowDim, aNmbr, aFunc);
    }

    public void modifyRow(final int aRow, final int aCol, final ParameterFunction<N> aFunc, final int aParam) {
        myDelegate.modify(aRow + (aCol * myRowDim), aRow + (myColDim * myRowDim), myRowDim, aFunc, aParam);
    }

    public void modifyRow(final int aRow, final int aCol, final UnaryFunction<N> aFunc) {
        myDelegate.modify(aRow + (aCol * myRowDim), aRow + (myColDim * myRowDim), myRowDim, aFunc);
    }

    public void set(final int aRow, final int aCol, final double aNmbr) {
        myDelegate.set(aRow + (aCol * myRowDim), aNmbr);
    }

    public void set(final int aRow, final int aCol, final N aNmbr) {
        myDelegate.set(aRow + (aCol * myRowDim), aNmbr);
    }

    public int size() {
        return myDelegate.length;
    }

    public Array1D<N> sliceColumn(final int aRow, final int aCol) {
        return new Array1D<N>(myDelegate, aRow + (aCol * myRowDim), myRowDim + (aCol * myRowDim), INT_ONE);
    }

    public Array1D<N> sliceDiagonal(final int aRow, final int aCol) {

        final int tmpCount = Math.min(myRowDim - aRow, myColDim - aCol);

        return new Array1D<N>(myDelegate, aRow + (aCol * myRowDim), aRow + tmpCount + ((aCol + tmpCount) * myRowDim), INT_ONE + myRowDim);
    }

    public Array1D<N> sliceRow(final int aRow, final int aCol) {
        return new Array1D<N>(myDelegate, aRow + (aCol * myRowDim), aRow + (myColDim * myRowDim), myRowDim);
    }

    /**
     * @return An array of arrays of doubles
     */
    public double[][] toRawCopy() {
        return ArrayUtils.toRawCopyOf(this);
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        return myDelegate.toScalar(aRow + (aCol * myRowDim));
    }

    @Override
    public String toString() {
        return myDelegate.toString();
    }

    public void visitAll(final AggregatorFunction<N> aVisitor) {
        myDelegate.visit(INT_ZERO, myDelegate.length, INT_ONE, aVisitor);
    }

    public void visitColumn(final int aRow, final int aCol, final AggregatorFunction<N> aVisitor) {
        myDelegate.visit(aRow + (aCol * myRowDim), myRowDim + (aCol * myRowDim), INT_ONE, aVisitor);
    }

    public void visitDiagonal(final int aRow, final int aCol, final AggregatorFunction<N> aVisitor) {

        final int tmpCount = Math.min(myRowDim - aRow, myColDim - aCol);

        myDelegate.visit(aRow + (aCol * myRowDim), aRow + tmpCount + ((aCol + tmpCount) * myRowDim), INT_ONE + myRowDim, aVisitor);
    }

    public void visitRow(final int aRow, final int aCol, final AggregatorFunction<N> aVisitor) {
        myDelegate.visit(aRow + (aCol * myRowDim), aRow + (myColDim * myRowDim), myRowDim, aVisitor);
    }

    BasicArray<N> getDelegate() {
        return myDelegate;
    }

}
