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

import java.math.BigDecimal;

import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;

/**
 * You create instances of (some subclass of) this class by calling one
 * of the static factory methods: {@linkplain #makeBig()},
 * {@linkplain #makeComplex()} or {@linkplain #makePrimitive()}.
 *
 * @author apete
 */
public abstract class HessenbergDecomposition<N extends Number> extends InPlaceDecomposition<N> implements Hessenberg<N> {

    static final class Big extends HessenbergDecomposition<BigDecimal> {

        Big() {
            super(BigDenseStore.FACTORY);
        }

    }

    static final class Complex extends HessenbergDecomposition<ComplexNumber> {

        Complex() {
            super(ComplexDenseStore.FACTORY);
        }

    }

    static final class Primitive extends HessenbergDecomposition<Double> {

        Primitive() {
            super(PrimitiveDenseStore.FACTORY);
        }

    }

    @SuppressWarnings("unchecked")
    public static final <N extends Number> Hessenberg<N> make(final Access2D<N> aTypical) {

        final N tmpNumber = aTypical.get(0, 0);

        if (tmpNumber instanceof BigDecimal) {
            return (Hessenberg<N>) HessenbergDecomposition.makeBig();
        } else if (tmpNumber instanceof ComplexNumber) {
            return (Hessenberg<N>) HessenbergDecomposition.makeComplex();
        } else if (tmpNumber instanceof Double) {
            return (Hessenberg<N>) HessenbergDecomposition.makePrimitive();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static Hessenberg<BigDecimal> makeBig() {
        return new HessenbergDecomposition.Big();
    }

    public static Hessenberg<ComplexNumber> makeComplex() {
        return new HessenbergDecomposition.Complex();
    }

    public static Hessenberg<Double> makePrimitive() {
        return new HessenbergDecomposition.Primitive();
    }

    private boolean myUpper = true;
    private transient DecompositionStore<N> myQ = null;

    protected HessenbergDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory) {
        super(aFactory);
    }

    public final boolean compute(final Access2D<?> aMtrx) {
        return this.compute(aMtrx, true);
    }

    public final boolean compute(final Access2D<?> aMtrx, final boolean upper) {

        this.reset();

        myUpper = upper;

        final DecompositionStore<N> tmpStore = this.setInPlace(aMtrx);

        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = this.getColDim();

        if (upper) {

            final Householder<N> tmpHouseholderCol = this.makeHouseholder(tmpRowDim);

            final int tmpLimit = Math.min(tmpRowDim, tmpColDim) - 2;

            for (int ij = 0; ij < tmpLimit; ij++) {
                if (tmpStore.generateApplyAndCopyHouseholderColumn(ij + 1, ij, tmpHouseholderCol)) {
                    tmpStore.transformLeft(tmpHouseholderCol, ij + 1);
                    tmpStore.transformRight(tmpHouseholderCol, 0);
                }
            }

        } else {

            final Householder<N> tmpHouseholderRow = this.makeHouseholder(tmpColDim);

            final int tmpLimit = Math.min(tmpRowDim, tmpColDim) - 2;

            for (int ij = 0; ij < tmpLimit; ij++) {
                if (tmpStore.generateApplyAndCopyHouseholderRow(ij, ij + 1, tmpHouseholderRow)) {
                    tmpStore.transformRight(tmpHouseholderRow, ij + 1);
                    tmpStore.transformLeft(tmpHouseholderRow, 0);
                }
            }
        }

        return this.computed(true);
    }

    public final boolean equals(final MatrixStore<N> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(aStore, this, aCntxt);
    }

    public final MatrixStore<N> getH() {
        return this.getInPlace().builder().hessenberg(myUpper).build();
    }

    public final MatrixStore<N> getQ() {
        if (myQ == null) {
            myQ = this.makeQ(this.makeEye(this.getRowDim(), this.getColDim()), myUpper, true);
        }
        return myQ;
    }

    public final boolean isFullSize() {
        return true;
    }

    public final boolean isSolvable() {
        return false;
    }

    public boolean isUpper() {
        return myUpper;
    }

    public MatrixStore<N> reconstruct() {
        return MatrixUtils.reconstruct(this);
    }

    @Override
    public void reset() {

        super.reset();

        myQ = null;
        myUpper = true;
    }

    @Override
    public final MatrixStore<N> solve(final MatrixStore<N> aRHS) {
        throw new UnsupportedOperationException();
    }

    private final DecompositionStore<N> makeQ(final DecompositionStore<N> aStoreToTransform, final boolean tmpUpper, final boolean eye) {

        final int tmpRowAndColDim = aStoreToTransform.getRowDim();

        final DecompositionStore.HouseholderReference<N> tmpHouseholderReference = new DecompositionStore.HouseholderReference<N>(this.getInPlace(), tmpUpper);

        for (int ij = tmpRowAndColDim - 3; ij >= 0; ij--) {

            tmpHouseholderReference.row = tmpUpper ? ij + 1 : ij;
            tmpHouseholderReference.col = tmpUpper ? ij : ij + 1;

            if (!tmpHouseholderReference.isZero()) {
                aStoreToTransform.transformLeft(tmpHouseholderReference, eye ? ij : 0);
            }
        }

        return aStoreToTransform;
    }

    final DecompositionStore<N> doQ(final DecompositionStore<N> aStoreToTransform) {
        return this.makeQ(aStoreToTransform, myUpper, false);
    }

}
