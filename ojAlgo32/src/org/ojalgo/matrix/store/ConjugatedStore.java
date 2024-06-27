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

/**
 * ConjugatedStore
 *
 * @author apete
 */
public final class ConjugatedStore<N extends Number> extends TransjugatedStore<N> {

    public ConjugatedStore(final MatrixStore<N> aBase) {
        super(aBase);
    }

    @Override
    public PhysicalStore<N> conjugate() {
        return this.getBase().copy();
    }

    @Override
    public PhysicalStore<N> copy() {
        return this.getBase().conjugate();
    }

    public N get(final int aRow, final int aCol) {
        return this.getBase().toScalar(aCol, aRow).conjugate().getNumber();
    }

    @Override
    public MatrixStore<N> multiplyLeft(final MatrixStore<N> aStore) {

        MatrixStore<N> retVal;

        if (aStore instanceof ConjugatedStore<?>) {

            retVal = this.getBase().multiplyRight(((ConjugatedStore<N>) aStore).getOriginal());

            retVal = new ConjugatedStore<N>(retVal);

        } else {

            retVal = super.multiplyLeft(aStore);
        }

        return retVal;
    }

    @Override
    public MatrixStore<N> multiplyRight(final MatrixStore<N> aStore) {

        MatrixStore<N> retVal;

        if (aStore instanceof ConjugatedStore<?>) {

            retVal = this.getBase().multiplyLeft(((ConjugatedStore<N>) aStore).getOriginal());

            retVal = new ConjugatedStore<N>(retVal);

        } else {

            retVal = super.multiplyRight(aStore);
        }

        return retVal;
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        return this.getBase().toScalar(aCol, aRow).conjugate();
    }

}
