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
 * A merger of two {@linkplain MatrixStore} instances by placing one store to
 * the right of the other. The two matrices must have the same number of
 * rows. The rows of the two matrices are logically merged to form new
 * longer rows.
 * 
 * @author apete
 */
public final class LeftRightStore<N extends Number> extends DelegatingStore<N> {

    private final int myColSplit;
    private final MatrixStore<N> myRightStore;

    public LeftRightStore(final MatrixStore<N> aBase, final MatrixStore<N> aRightStore) {

        super(aBase.getRowDim(), aBase.getColDim() + aRightStore.getColDim(), aBase);

        myRightStore = aRightStore;
        myColSplit = aBase.getColDim();
    }

    @SuppressWarnings("unused")
    private LeftRightStore(final MatrixStore<N> aBase) {

        this(aBase, null);

        ProgrammingError.throwForIllegalInvocation();
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#doubleValue(int, int)
     */
    public double doubleValue(final int aRow, final int aCol) {
        return (aCol >= myColSplit) ? myRightStore.doubleValue(aRow, aCol - myColSplit) : this.getBase().doubleValue(aRow, aCol);
    }

    public N get(final int aRow, final int aCol) {
        return (aCol >= myColSplit) ? myRightStore.get(aRow, aCol - myColSplit) : this.getBase().get(aRow, aCol);
    }

    public boolean isLowerLeftShaded() {
        return this.getBase().isLowerLeftShaded();
    }

    public boolean isUpperRightShaded() {
        return BOOLEAN_FALSE;
    }

    @Override
    public MatrixStore<N> multiplyLeft(final MatrixStore<N> aStore) {

        MatrixStore<N> tmpBase = null;
        final Future<MatrixStore<N>> tmpBaseFuture = this.executeMultiplyLeftOnBase(aStore);

        final MatrixStore<N> tmpRight = myRightStore.multiplyLeft(aStore);

        try {
            tmpBase = tmpBaseFuture.get();
        } catch (final InterruptedException anException) {
            tmpBase = null;
        } catch (final ExecutionException anException) {
            tmpBase = null;
        }

        return new LeftRightStore<N>(tmpBase, tmpRight);
    }

    @Override
    public MatrixStore<N> multiplyRight(final MatrixStore<N> aStore) {

        MatrixStore<N> tmpBase = null;
        final Future<MatrixStore<N>> tmpBaseFuture = this.executeMultiplyRightOnBase(new RowsStore<N>(aStore, MatrixUtils.makeIncreasingRange(0, myColSplit)));

        final MatrixStore<N> tmpDiff = myRightStore.multiplyRight(new RowsStore<N>(aStore, MatrixUtils.makeIncreasingRange(myColSplit, aStore.getRowDim() - myColSplit)));

        try {
            tmpBase = tmpBaseFuture.get();
        } catch (final InterruptedException anException) {
            tmpBase = null;
        } catch (final ExecutionException anException) {
            tmpBase = null;
        }

        if (tmpBase instanceof ZeroStore<?>) {
            return tmpDiff;
        } else if (tmpDiff instanceof ZeroStore<?>) {
            return tmpBase;
        } else if (tmpBase instanceof PhysicalStore<?>) {
            ((PhysicalStore<N>) tmpBase).fillMatching(tmpBase, this.factory().getFunctionSet().add(), tmpDiff);
            return tmpBase;
        } else if (tmpDiff instanceof PhysicalStore<?>) {
            ((PhysicalStore<N>) tmpDiff).fillMatching(tmpBase, this.factory().getFunctionSet().add(), tmpDiff);
            return tmpDiff;
        } else {
            return new SuperimposedStore<N>(tmpBase, tmpDiff);
        }
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        return (aCol >= myColSplit) ? myRightStore.toScalar(aRow, aCol - myColSplit) : this.getBase().toScalar(aRow, aCol);
    }

}
