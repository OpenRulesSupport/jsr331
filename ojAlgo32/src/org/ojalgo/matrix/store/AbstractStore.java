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

import java.io.Serializable;
import java.util.Iterator;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

abstract class AbstractStore<N extends Number> implements MatrixStore<N>, Serializable {

    protected static final boolean BOOLEAN_FALSE = false;
    protected static final boolean BOOLEAN_TRUE = true;
    protected static final int INT_ONE = 1;
    protected static final int INT_ZERO = 0;
    private final int myColDim;

    private transient Class<?> myComponentType = null;
    private final int myRowDim;

    @SuppressWarnings("unused")
    private AbstractStore() {

        this(0, 0);

        ProgrammingError.throwForIllegalInvocation();
    }

    protected AbstractStore(final int aRowDim, final int aColDim) {

        super();

        myRowDim = aRowDim;
        myColDim = aColDim;
    }

    @SuppressWarnings("unchecked")
    public N aggregateAll(final Aggregator aVisitor) {

        final AggregatorFunction<N> tmpFunction = (AggregatorFunction<N>) aVisitor.getFunction(this.getComponentType());

        this.visitAll(tmpFunction);

        return tmpFunction.getNumber();
    }

    public final MatrixStore.Builder<N> builder() {
        return new MatrixStore.Builder<N>(this);
    }

    public PhysicalStore<N> conjugate() {
        return this.factory().conjugate(this);
    }

    public PhysicalStore<N> copy() {
        return this.factory().copy(this);
    }

    public double doubleValue(final int anInd) {
        return this.doubleValue(AccessUtils.row(anInd, myRowDim), AccessUtils.column(anInd, myRowDim));
    }

    public final boolean equals(final MatrixStore<N> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(this, aStore, aCntxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean equals(final Object someObj) {
        if (someObj instanceof MatrixStore) {
            return this.equals((MatrixStore<N>) someObj, TypeUtils.EQUALS_NUMBER_CONTEXT);
        } else {
            return super.equals(someObj);
        }
    }

    public N get(final int anInd) {
        return this.get(AccessUtils.row(anInd, myRowDim), AccessUtils.column(anInd, myRowDim));
    }

    public final int getColDim() {
        return myColDim;
    }

    public int getMinDim() {
        return Math.min(myRowDim, myColDim);
    }

    public final int getRowDim() {
        return myRowDim;
    }

    @Override
    public final int hashCode() {
        return MatrixUtils.hashCode(this);
    }

    public boolean isAbsolute(final int aRow, final int aCol) {
        return this.toScalar(aRow, aCol).isAbsolute();
    }

    public boolean isPositive(final int aRow, final int aCol) {
        return this.toScalar(aRow, aCol).isPositive();
    }

    public boolean isReal(final int aRow, final int aCol) {
        return this.toScalar(aRow, aCol).isReal();
    }

    public boolean isZero(final int aRow, final int aCol) {
        return this.toScalar(aRow, aCol).isZero();
    }

    public final Iterator<N> iterator() {
        return new Iterator1D<N>(this);
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#multiplyLeft(org.ojalgo.matrix.store.MatrixStore)
     */
    public MatrixStore<N> multiplyLeft(final MatrixStore<N> aStore) {

        final int tmpRowDim = aStore.getRowDim();
        final int tmpColDim = this.getColDim();

        final PhysicalStore<N> retVal = this.factory().makeZero(tmpRowDim, tmpColDim);

        retVal.fillByMultiplying(aStore, this);

        return retVal;
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#multiplyRight(org.ojalgo.matrix.store.MatrixStore)
     */
    public MatrixStore<N> multiplyRight(final MatrixStore<N> aStore) {

        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = aStore.getColDim();

        final PhysicalStore<N> retVal = this.factory().makeZero(tmpRowDim, tmpColDim);

        retVal.fillByMultiplying(this, aStore);

        return retVal;
    }

    public int size() {
        return myRowDim * myColDim;
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + " " + this.getRowDim() + " x " + this.getColDim();
    }

    public PhysicalStore<N> transpose() {
        return this.factory().transpose(this);
    }

    public void visitAll(final AggregatorFunction<N> aVisitor) {
        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = this.getColDim();
        for (int j = 0; j < tmpColDim; j++) {
            for (int i = 0; i < tmpRowDim; i++) {
                aVisitor.invoke(this.get(i, j));
            }
        }
    }

    public void visitColumn(final int aRow, final int aCol, final AggregatorFunction<N> aVisitor) {
        final int tmpRowDim = this.getRowDim();
        for (int i = aRow; i < tmpRowDim; i++) {
            aVisitor.invoke(this.get(i, aCol));
        }
    }

    public void visitDiagonal(final int aRow, final int aCol, final AggregatorFunction<N> aVisitor) {
        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = this.getColDim();
        for (int ij = 0; ((aRow + ij) < tmpRowDim) && ((aCol + ij) < tmpColDim); ij++) {
            aVisitor.invoke(this.get(aRow + ij, aCol + ij));
        }
    }

    public void visitRow(final int aRow, final int aCol, final AggregatorFunction<N> aVisitor) {
        final int tmpColDim = this.getColDim();
        for (int j = aCol; j < tmpColDim; j++) {
            aVisitor.invoke(this.get(aRow, j));
        }
    }

    protected final Class<?> getComponentType() {
        if (myComponentType == null) {
            myComponentType = this.get(0, 0).getClass();
        }
        return myComponentType;
    }

}
