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
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * You create instances of (some subclass of) this class by calling one
 * of the static factory methods: {@linkplain #makeBig()},
 * {@linkplain #makeComplex()} or {@linkplain #makePrimitive()}.
 *
 * @author apete
 */
public abstract class BidiagonalDecomposition<N extends Number> extends InPlaceDecomposition<N> implements Bidiagonal<N> {

    static final class Big extends BidiagonalDecomposition<BigDecimal> {

        Big() {
            super(BigDenseStore.FACTORY);
        }

        @Override
        Array1D<BigDecimal> makeReal() {
            return null;
        }

    }

    static final class Complex extends BidiagonalDecomposition<ComplexNumber> {

        Complex() {
            super(ComplexDenseStore.FACTORY);
        }

        @Override
        Array1D<ComplexNumber> makeReal() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    static final class Primitive extends BidiagonalDecomposition<Double> {

        Primitive() {
            super(PrimitiveDenseStore.FACTORY);
        }

        @Override
        Array1D<Double> makeReal() {
            return null;
        }

    }

    @SuppressWarnings("unchecked")
    public static final <N extends Number> Bidiagonal<N> make(final Access2D<N> aTypical) {

        final N tmpNumber = aTypical.get(0, 0);

        if (tmpNumber instanceof BigDecimal) {
            return (Bidiagonal<N>) BidiagonalDecomposition.makeBig();
        } else if (tmpNumber instanceof ComplexNumber) {
            return (Bidiagonal<N>) BidiagonalDecomposition.makeComplex();
        } else if (tmpNumber instanceof Double) {
            return (Bidiagonal<N>) BidiagonalDecomposition.makePrimitive();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static final Bidiagonal<BigDecimal> makeBig() {
        return new BidiagonalDecomposition.Big();
    }

    public static final Bidiagonal<ComplexNumber> makeComplex() {
        return new BidiagonalDecomposition.Complex();
    }

    public static final Bidiagonal<Double> makePrimitive() {
        return new BidiagonalDecomposition.Primitive();
    }

    private transient DiagonalAccess<N> myDiagonalAccessD;
    private transient DecompositionStore<N> myQ1;
    private transient DecompositionStore<N> myQ2;

    private Array1D<N> myInitDiagQ1 = null;
    private Array1D<N> myInitDiagQ2 = null;

    protected BidiagonalDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory) {
        super(aFactory);
    }

    public final boolean compute(final Access2D<?> aStore) {

        this.reset();

        final DecompositionStore<N> tmpStore = this.setInPlace(aStore);

        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = this.getColDim();

        final int tmpLimit = Math.min(tmpRowDim, tmpColDim);

        final Householder<N> tmpHouseholderRow = this.makeHouseholder(tmpColDim);
        final Householder<N> tmpHouseholderCol = this.makeHouseholder(tmpRowDim);

        if (this.isAspectRatioNormal()) {

            for (int ij = 0; ij < tmpLimit; ij++) {

                if (((ij + 1) < tmpRowDim) && tmpStore.generateApplyAndCopyHouseholderColumn(ij, ij, tmpHouseholderCol)) {
                    tmpStore.transformLeft(tmpHouseholderCol, ij + 1);
                }

                if (((ij + 2) < tmpColDim) && tmpStore.generateApplyAndCopyHouseholderRow(ij, ij + 1, tmpHouseholderRow)) {
                    tmpStore.transformRight(tmpHouseholderRow, ij + 1);
                }
            }

            myInitDiagQ2 = this.makeReal();

        } else {

            for (int ij = 0; ij < tmpLimit; ij++) {

                if (((ij + 1) < tmpColDim) && tmpStore.generateApplyAndCopyHouseholderRow(ij, ij, tmpHouseholderRow)) {
                    tmpStore.transformRight(tmpHouseholderRow, ij + 1);
                }

                if (((ij + 2) < tmpRowDim) && tmpStore.generateApplyAndCopyHouseholderColumn(ij + 1, ij, tmpHouseholderCol)) {
                    tmpStore.transformLeft(tmpHouseholderCol, ij + 1);
                }
            }

            myInitDiagQ1 = this.makeReal();
        }

        return this.computed(true);
    }

    public final boolean equals(final MatrixStore<N> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(aStore, this, aCntxt);
    }

    public final MatrixStore<N> getD() {
        return this.getInPlace().builder().bidiagonal(this.isAspectRatioNormal(), false).build();
    }

    public final MatrixStore<N> getQ1() {
        if (myQ1 == null) {
            myQ1 = this.makeQ1(this.makeEye(this.getRowDim(), this.getMinDim()), true);
        }
        return myQ1;
    }

    public final MatrixStore<N> getQ2() {
        if (myQ2 == null) {
            myQ2 = this.makeQ2(this.makeEye(this.getColDim(), this.getMinDim()), true);
        }
        return myQ2;
    }

    public final boolean isFullSize() {
        return false;
    }

    public final boolean isSolvable() {
        return false;
    }

    public final boolean isUpper() {
        return this.isAspectRatioNormal();
    }

    public final MatrixStore<N> reconstruct() {
        return MatrixUtils.reconstruct(this);
    }

    @Override
    public final void reset() {

        super.reset();

        myQ1 = null;
        myQ2 = null;
        myDiagonalAccessD = null;

        myInitDiagQ1 = null;
        myInitDiagQ2 = null;
    }

    private final DiagonalAccess<N> makeDiagonalAccessD() {

        final Array2D<N> tmpArray2D = this.getInPlace().asArray2D();

        final Array1D<N> tmpMain = tmpArray2D.sliceDiagonal(0, 0);
        Array1D<N> tmpSuper;
        Array1D<N> tmpSub;

        if (this.isAspectRatioNormal()) {
            tmpSuper = tmpArray2D.sliceDiagonal(0, 1);
            tmpSub = null;
        } else {
            tmpSub = tmpArray2D.sliceDiagonal(1, 0);
            tmpSuper = null;
        }

        return new DiagonalAccess<N>(tmpMain, tmpSuper, tmpSub, this.getStaticZero());
    }

    /**
     * Will solve the equation system [aMtrxV][aMtrxD][X]=[aMtrxSimilar]<sup>T</sup> and overwrite the solution [X] to [aV].
     */
    private final void solve(final PhysicalStore<N> aMtrxV, final MatrixStore<N> aMtrxD, final DiagonalAccess<N> aMtrxSimilar) {

        final int tmpDim = aMtrxV.getRowDim();
        final int tmpLim = tmpDim - 1;

        double tmpSingular;
        for (int j = 0; j < tmpDim; j++) {
            tmpSingular = aMtrxD.doubleValue(j, j);
            if (TypeUtils.isZero(tmpSingular)) {
                for (int i = 0; i < tmpDim; i++) {
                    aMtrxV.set(i, j, PrimitiveMath.ZERO);
                }
            } else {
                for (int i = 0; i < tmpLim; i++) {
                    aMtrxV.set(i, j,
                            ((aMtrxSimilar.doubleValue(i, i) * aMtrxV.doubleValue(i, j)) + (aMtrxSimilar.doubleValue(i, i + 1) * aMtrxV.doubleValue(i + 1, j)))
                                    / tmpSingular);
                }
                aMtrxV.set(tmpLim, j, (aMtrxSimilar.doubleValue(tmpLim, tmpLim) * aMtrxV.doubleValue(tmpLim, j)) / tmpSingular);
            }
        }
    }

    private final DecompositionStore<N> solve2(final PhysicalStore<N> aMtrxV, final MatrixStore<N> aMtrxD, final DiagonalAccess<N> aMtrxSimilar) {

        final int tmpDim = aMtrxV.getRowDim();
        final int tmpLim = tmpDim - 1;

        final DecompositionStore<N> retVal = this.makeZero(tmpDim, tmpDim);

        double tmpSingular;
        for (int j = 0; j < tmpDim; j++) {
            tmpSingular = aMtrxD.doubleValue(j, j);
            if (TypeUtils.isZero(tmpSingular)) {
                for (int i = 0; i < tmpDim; i++) {
                    retVal.set(i, j, aMtrxV.doubleValue(i, j));
                }
            } else {
                for (int i = 0; i < tmpLim; i++) {
                    retVal.set(i, j,
                            ((aMtrxSimilar.doubleValue(i, i) * aMtrxV.doubleValue(i, j)) + (aMtrxSimilar.doubleValue(i, i + 1) * aMtrxV.doubleValue(i + 1, j)))
                                    / tmpSingular);
                }
                retVal.set(tmpLim, j, (aMtrxSimilar.doubleValue(tmpLim, tmpLim) * aMtrxV.doubleValue(tmpLim, j)) / tmpSingular);
            }
        }

        return retVal;
    }

    protected final DecompositionStore<N> makeQ1(final DecompositionStore<N> aStoreToTransform, final boolean eye) {

        final DecompositionStore.HouseholderReference<N> tmpHouseholderReference = new DecompositionStore.HouseholderReference<N>(this.getInPlace(), true);

        final int tmpRowDim = aStoreToTransform.getRowDim();
        final int tmpColDim = aStoreToTransform.getColDim();

        final boolean tmpUpper = this.isUpper();
        for (int ij = (tmpUpper && (tmpRowDim != tmpColDim)) ? tmpColDim - 1 : tmpColDim - 2; ij >= 0; ij--) {

            tmpHouseholderReference.row = tmpUpper ? ij : ij + 1;
            tmpHouseholderReference.col = ij;

            if (!tmpHouseholderReference.isZero()) {
                aStoreToTransform.transformLeft(tmpHouseholderReference, eye ? ij : 0);
            }
        }

        return aStoreToTransform;
    }

    protected final DecompositionStore<N> makeQ2(final DecompositionStore<N> aStoreToTransform, final boolean eye) {

        final DecompositionStore.HouseholderReference<N> tmpHouseholderReference = new DecompositionStore.HouseholderReference<N>(this.getInPlace(), false);

        final int tmpColDim = aStoreToTransform.getColDim();

        final boolean tmpUpper = this.isUpper();
        for (int ij = tmpUpper ? tmpColDim - 2 : tmpColDim - 1; ij >= 0; ij--) {

            tmpHouseholderReference.row = ij;
            tmpHouseholderReference.col = tmpUpper ? ij + 1 : ij;

            if (!tmpHouseholderReference.isZero()) {
                aStoreToTransform.transformLeft(tmpHouseholderReference, eye ? ij : 0);
            }
        }

        return aStoreToTransform;
    }

    final DecompositionStore<N> doQ1(final DecompositionStore<N> aStoreToTransform) {
        return this.makeQ1(aStoreToTransform, false);
    }

    final DecompositionStore<N> doQ2(final DecompositionStore<N> aStoreToTransform) {
        return this.makeQ2(aStoreToTransform, false);
    }

    final DiagonalAccess<N> getDiagonalAccessD() {
        if (myDiagonalAccessD == null) {
            myDiagonalAccessD = this.makeDiagonalAccessD();
        }
        return myDiagonalAccessD;
    }

    abstract Array1D<N> makeReal();

}
