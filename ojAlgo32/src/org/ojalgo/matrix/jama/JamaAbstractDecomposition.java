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
package org.ojalgo.matrix.jama;

import org.ojalgo.access.Access2D;
import org.ojalgo.array.ArrayUtils;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.decomposition.MatrixDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.type.context.NumberContext;

/**
 * JamaAbstractDecomposition
 *
 * @author apete
 */
abstract class JamaAbstractDecomposition implements MatrixDecomposition<Double> {

    protected static final boolean BOOLEAN_FALSE = false;
    protected static final boolean BOOLEAN_TRUE = true;
    protected static final int INT_ZERO = 0;

    static Matrix cast(final Access2D<?> aStore) {
        if (aStore instanceof JamaMatrix) {
            return ((JamaMatrix) aStore).getDelegate();
        } else {
            return new Matrix(ArrayUtils.toRawCopyOf(aStore));
        }
    }

    protected JamaAbstractDecomposition() {
        super();
    }

    public final boolean compute(final Access2D<?> aStore) {

        this.reset();

        return this.compute(JamaAbstractDecomposition.cast(aStore));
    }

    public final boolean equals(final MatrixDecomposition<Double> aDecomp, final NumberContext aCntxt) {
        return MatrixUtils.equals(this.reconstruct(), aDecomp.reconstruct(), aCntxt);
    }

    public abstract JamaMatrix getInverse();

    /**
     * Makes no use of <code>preallocated</code> at all. Simply delegates to {@link #getInverse()}.
     * 
     * @see org.ojalgo.matrix.decomposition.MatrixDecomposition#getInverse(org.ojalgo.matrix.decomposition.DecompositionStore)
     */
    public final MatrixStore<Double> getInverse(final DecompositionStore<Double> preallocated) {
        return this.getInverse();
    }

    public final JamaMatrix invert(final MatrixStore<Double> aStore) {

        this.compute(aStore);

        return this.getInverse();
    }

    public JamaMatrix solve(final MatrixStore<Double> aRHS) {
        return new JamaMatrix(this.solve(JamaAbstractDecomposition.cast(aRHS)));
    }

    /**
     * Makes no use of <code>preallocated</code> at all. Simply delegates to {@link #solve(MatrixStore)}.
     * 
     * @see org.ojalgo.matrix.decomposition.MatrixDecomposition#solve(org.ojalgo.matrix.store.MatrixStore, org.ojalgo.matrix.decomposition.DecompositionStore)
     */
    public final JamaMatrix solve(final MatrixStore<Double> aRHS, final DecompositionStore<Double> preallocated) {
        return this.solve(aRHS);
    }

    protected JamaMatrix makeEyeStore(final int aRowDim, final int aColDim) {
        return new JamaMatrix(Matrix.identity(aRowDim, aColDim));
    }

    abstract boolean compute(Matrix aDelegate);

    abstract Matrix solve(Matrix aRHS);
}
