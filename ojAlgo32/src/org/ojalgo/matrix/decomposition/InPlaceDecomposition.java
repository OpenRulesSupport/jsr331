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
package org.ojalgo.matrix.decomposition;

import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.MatrixStore;

abstract class InPlaceDecomposition<N extends Number> extends AbstractDecomposition<N> {

    private int myColDim;
    private DecompositionStore<N> myInPlace;
    private int myRowDim;

    protected InPlaceDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory) {
        super(aFactory);
    }

    public MatrixStore<N> getInverse() {
        throw new UnsupportedOperationException();
    }

    public MatrixStore<N> getInverse(final DecompositionStore<N> preallocated) {
        throw new UnsupportedOperationException();
    }

    public MatrixStore<N> solve(final MatrixStore<N> aRHS) {
        throw new UnsupportedOperationException();
    }

    public MatrixStore<N> solve(final MatrixStore<N> aRHS, final DecompositionStore<N> preallocated) {
        throw new UnsupportedOperationException();
    }

    protected final int getColDim() {
        return myColDim;
    }

    protected final DecompositionStore<N> getInPlace() {
        return myInPlace;
    }

    protected final int getMinDim() {
        return Math.min(myRowDim, myColDim);
    }

    protected final int getRowDim() {
        return myRowDim;
    }

    final DecompositionStore<N> setInPlace(final Access2D<?> aStore) {

        final int tmpRowDim = aStore.getRowDim();
        final int tmpColDim = aStore.getColDim();

        if ((myInPlace != null) && (myRowDim == tmpRowDim) && (myColDim == tmpColDim)) {

            myInPlace.fillMatching(aStore);

        } else {

            myInPlace = this.copy(aStore);

            myRowDim = tmpRowDim;
            myColDim = tmpColDim;
        }

        this.aspectRatioNormal(tmpRowDim >= tmpColDim);
        this.computed(false);

        return myInPlace;
    }

}
