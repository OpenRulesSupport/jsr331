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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.ojalgo.concurrent.DaemonPoolExecutor;

abstract class DelegatingStore<N extends Number> extends LogicalStore<N> {

    private static final class MultiplyLeft<N extends Number> implements Callable<MatrixStore<N>> {

        private MatrixStore<N> myLeftStore;
        private MatrixStore<N> myThisStore;

        public MultiplyLeft(final MatrixStore<N> aThisStore, final MatrixStore<N> aLeftStore) {

            super();

            myThisStore = aThisStore;
            myLeftStore = aLeftStore;
        }

        @SuppressWarnings("unused")
        private MultiplyLeft() {
            this(null, null);
        }

        public MatrixStore<N> call() throws Exception {
            return myThisStore.multiplyLeft(myLeftStore);
        }

    }

    private static final class MultiplyRight<N extends Number> implements Callable<MatrixStore<N>> {

        private MatrixStore<N> myRightStore;
        private MatrixStore<N> myThisStore;

        public MultiplyRight(final MatrixStore<N> aThisStore, final MatrixStore<N> aRightStore) {

            super();

            myThisStore = aThisStore;
            myRightStore = aRightStore;
        }

        @SuppressWarnings("unused")
        private MultiplyRight() {
            this(null, null);
        }

        public MatrixStore<N> call() throws Exception {
            return myThisStore.multiplyRight(myRightStore);
        }

    }

    protected DelegatingStore(final int aRowDim, final int aColDim, final MatrixStore<N> aBase) {
        super(aRowDim, aColDim, aBase);
    }

    protected final Future<MatrixStore<N>> executeMultiplyLeftOnBase(final MatrixStore<N> aStore) {
        return DaemonPoolExecutor.INSTANCE.submit(new MultiplyLeft<N>(this.getBase(), aStore));
    }

    protected final Future<MatrixStore<N>> executeMultiplyRightOnBase(final MatrixStore<N> aStore) {
        return DaemonPoolExecutor.INSTANCE.submit(new MultiplyRight<N>(this.getBase(), aStore));
    }

}
