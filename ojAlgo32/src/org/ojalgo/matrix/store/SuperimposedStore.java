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
package org.ojalgo.matrix.store;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ojalgo.ProgrammingError;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.scalar.Scalar;

/**
 * SuperimposedStore
 *
 * @author apete
 */
public final class SuperimposedStore<N extends Number> extends DelegatingStore<N> {

    private final int myColFirst;
    private final int myColLimit;
    private final int[] myColSet;
    private final MatrixStore<N> myDiff;
    private final int myRowFirst;
    private final int myRowLimit;
    private final int[] myRowSet;

    public SuperimposedStore(final MatrixStore<N> aBase, final int aRow, final int aCol, final MatrixStore<N> aDiff) {

        super(aBase.getRowDim(), aBase.getColDim(), aBase);

        myRowFirst = aRow;
        myColFirst = aCol;

        final int tmpDiffRowDim = aDiff.getRowDim();
        final int tmpDiffColDim = aDiff.getColDim();

        myRowLimit = aRow + tmpDiffRowDim;
        myColLimit = aCol + tmpDiffColDim;

        myRowSet = MatrixUtils.makeIncreasingRange(aRow, tmpDiffRowDim);
        myColSet = MatrixUtils.makeIncreasingRange(aCol, tmpDiffColDim);

        myDiff = aDiff;
    }

    @SuppressWarnings("unused")
    private SuperimposedStore(final int aRowDim, final int aColDim, final MatrixStore<N> aBase) {

        this(aBase, INT_ZERO, INT_ZERO, (MatrixStore<N>) null);

        ProgrammingError.throwForIllegalInvocation();
    }

    SuperimposedStore(final MatrixStore<N> aBase, final MatrixStore<N> aDiff) {
        this(aBase, INT_ZERO, INT_ZERO, aDiff);
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#doubleValue(int, int)
     */
    public double doubleValue(final int aRow, final int aCol) {

        double retVal = this.getBase().doubleValue(aRow, aCol);

        if (this.isCovered(aRow, aCol)) {
            retVal += myDiff.doubleValue(aRow - myRowFirst, aCol - myColFirst);
        }

        return retVal;
    }

    public N get(final int aRow, final int aCol) {

        N retVal = this.getBase().get(aRow, aCol);

        if (this.isCovered(aRow, aCol)) {
            retVal = myDiff.toScalar(aRow - myRowFirst, aCol - myColFirst).add(retVal).getNumber();
        }

        return retVal;
    }

    public boolean isLowerLeftShaded() {
        return BOOLEAN_FALSE;
    }

    public boolean isUpperRightShaded() {
        return BOOLEAN_FALSE;
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#multiplyLeft(org.ojalgo.matrix.store.MatrixStore)
     */
    @Override
    public MatrixStore<N> multiplyLeft(final MatrixStore<N> aStore) {

        MatrixStore<N> tmpBase = null;
        final Future<MatrixStore<N>> tmpBaseFuture = this.executeMultiplyLeftOnBase(aStore);

        final MatrixStore<N> tmpDiff = myDiff.multiplyLeft(new ColumnsStore<N>(aStore, myRowSet));

        try {
            tmpBase = tmpBaseFuture.get();
        } catch (final InterruptedException anException) {
            tmpBase = null;
        } catch (final ExecutionException anException) {
            tmpBase = null;
        }

        if (tmpBase != null) {
            return new SuperimposedStore<N>(tmpBase, INT_ZERO, myColFirst, tmpDiff);
        } else {
            return null;
        }
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#multiplyRight(org.ojalgo.matrix.store.MatrixStore)
     */
    @Override
    public MatrixStore<N> multiplyRight(final MatrixStore<N> aStore) {

        MatrixStore<N> tmpBase = null;
        final Future<MatrixStore<N>> tmpBaseFuture = this.executeMultiplyRightOnBase(aStore);

        final MatrixStore<N> tmpDiff = myDiff.multiplyRight(new RowsStore<N>(aStore, myColSet));

        try {
            tmpBase = tmpBaseFuture.get();
        } catch (final InterruptedException anException) {
            tmpBase = null;
        } catch (final ExecutionException anException) {
            tmpBase = null;
        }

        if (tmpBase != null) {
            return new SuperimposedStore<N>(tmpBase, myRowFirst, INT_ZERO, tmpDiff);
        } else {
            return null;
        }
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {

        Scalar<N> retVal = this.getBase().toScalar(aRow, aCol);

        if (this.isCovered(aRow, aCol)) {
            retVal = retVal.add(myDiff.get(aRow - myRowFirst, aCol - myColFirst));
        }

        return retVal;
    }

    private final boolean isCovered(final int aRow, final int aCol) {
        return (myRowFirst <= aRow) && (myColFirst <= aCol) && (aRow < myRowLimit) && (aCol < myColLimit);
    }

}
