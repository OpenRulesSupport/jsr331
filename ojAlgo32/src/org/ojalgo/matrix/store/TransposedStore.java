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

import org.ojalgo.scalar.Scalar;

public final class TransposedStore<N extends Number> extends TransjugatedStore<N> {

    public TransposedStore(final MatrixStore<N> aBase) {
        super(aBase);
    }

    @Override
    public PhysicalStore<N> copy() {
        return this.getBase().transpose();
    }

    public N get(final int aRow, final int aCol) {
        return this.getBase().get(aCol, aRow);
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#multiplyLeft(org.ojalgo.matrix.store.MatrixStore)
     */
    @Override
    public MatrixStore<N> multiplyLeft(final MatrixStore<N> aStore) {

        MatrixStore<N> retVal;

        if (aStore instanceof TransposedStore<?>) {

            retVal = this.getBase().multiplyRight(((TransposedStore<N>) aStore).getOriginal());

            retVal = new TransposedStore<N>(retVal);

        } else {

            retVal = super.multiplyLeft(aStore);
        }

        return retVal;
    }

    /**
     * @see org.ojalgo.matrix.store.MatrixStore#multiplyRight(org.ojalgo.matrix.store.MatrixStore)
     */
    @Override
    public MatrixStore<N> multiplyRight(final MatrixStore<N> aStore) {

        MatrixStore<N> retVal;

        if (aStore instanceof TransposedStore<?>) {

            retVal = this.getBase().multiplyLeft(((TransposedStore<N>) aStore).getOriginal());

            retVal = new TransposedStore<N>(retVal);

        } else {

            retVal = super.multiplyRight(aStore);
        }

        return retVal;
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        return this.getBase().toScalar(aCol, aRow);
    }

    @Override
    public PhysicalStore<N> transpose() {
        return this.getBase().copy();
    }

}
