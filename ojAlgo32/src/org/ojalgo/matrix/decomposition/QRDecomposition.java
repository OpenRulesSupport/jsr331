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
import org.ojalgo.matrix.jama.JamaQR;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.RowsStore;
import org.ojalgo.matrix.store.UpperTriangularStore;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.context.NumberContext;

/**
 * You create instances of (some subclass of) this class by calling one
 * of the static factory methods: {@linkplain #makeBig()},
 * {@linkplain #makeComplex()}, {@linkplain #makePrimitive()} or
 * {@linkplain #makeJama()}.
 *
 * @author apete
 */
public abstract class QRDecomposition<N extends Number> extends InPlaceDecomposition<N> implements QR<N> {

    static final class Big extends QRDecomposition<BigDecimal> {

        Big() {
            super(BigDenseStore.FACTORY);
        }

    }

    static final class Complex extends QRDecomposition<ComplexNumber> {

        Complex() {
            super(ComplexDenseStore.FACTORY);
        }

    }

    static final class Primitive extends QRDecomposition<Double> {

        Primitive() {
            super(PrimitiveDenseStore.FACTORY);
        }

    }

    @SuppressWarnings("unchecked")
    public static final <N extends Number> QR<N> make(final Access2D<N> aTypical) {

        final N tmpNumber = aTypical.get(0, 0);

        if (tmpNumber instanceof BigDecimal) {
            return (QR<N>) QRDecomposition.makeBig();
        } else if (tmpNumber instanceof ComplexNumber) {
            return (QR<N>) QRDecomposition.makeComplex();
        } else if (tmpNumber instanceof Double) {

            final int tmpMaxDim = Math.max(aTypical.getRowDim(), aTypical.getColDim());

            if ((tmpMaxDim <= 16) || (tmpMaxDim >= 46340)) { //16,16,8
                return (QR<N>) QRDecomposition.makeJama();
            } else {
                return (QR<N>) QRDecomposition.makePrimitive();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static final QR<BigDecimal> makeBig() {
        return new Big();
    }

    public static final QR<ComplexNumber> makeComplex() {
        return new Complex();
    }

    public static final QR<Double> makeJama() {
        return new JamaQR();
    }

    public static final QR<Double> makePrimitive() {
        return new Primitive();
    }

    protected QRDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory) {
        super(aFactory);
    }

    public boolean compute(final Access2D<?> aStore) {

        this.reset();

        final DecompositionStore<N> tmpStore = this.setInPlace(aStore);

        final int tmpRowDim = tmpStore.getRowDim();
        final int tmpColDim = tmpStore.getColDim();

        final Householder<N> tmpHouseholder = this.makeHouseholder(tmpRowDim);

        final int tmpLimit = Math.min(tmpRowDim, tmpColDim);

        for (int ij = 0; ij < tmpLimit; ij++) {
            if (((ij + 1) < tmpRowDim) && tmpStore.generateApplyAndCopyHouseholderColumn(ij, ij, tmpHouseholder)) {
                tmpStore.transformLeft(tmpHouseholder, ij + 1);
            }
        }

        return this.computed(true);
    }

    public boolean equals(final MatrixStore<N> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(aStore, this, aCntxt);
    }

    @Override
    public MatrixStore<N> getInverse() {
        return this.solve(this.makeEye(this.getColDim(), this.getRowDim()));
    }

    @Override
    public MatrixStore<N> getInverse(final DecompositionStore<N> preallocated) {
        return this.solve(this.makeEye(this.getColDim(), this.getRowDim()), preallocated);
    }

    public MatrixStore<N> getQ() {

        final DecompositionStore<N> tmpStore = this.getInPlace();
        final int tmpRowDim = tmpStore.getRowDim();
        final int tmpColDim = tmpStore.getMinDim();

        final DecompositionStore<N> retVal = this.makeEye(tmpRowDim, tmpColDim);

        final DecompositionStore.HouseholderReference<N> tmpReference = new DecompositionStore.HouseholderReference<N>(tmpStore, true);

        for (int j = tmpColDim - 1; j >= 0; j--) {

            tmpReference.row = j;
            tmpReference.col = j;

            if (!tmpReference.isZero()) {
                retVal.transformLeft(tmpReference, j);
            }
        }

        return retVal;
    }

    public MatrixStore<N> getR() {
        return new UpperTriangularStore<N>(this.getInPlace(), false);
    }

    public int getRank() {

        int retVal = 0;

        Scalar<N> tmpVal;
        for (int ij = 0; ij < this.getInPlace().getMinDim(); ij++) {
            tmpVal = this.getInPlace().toScalar(ij, ij);
            if (!tmpVal.isZero()) {
                retVal++;
            }
        }

        return retVal;
    }

    /**
     * @see org.ojalgo.matrix.decomposition.QR#isFullColumnRank()
     */
    public boolean isFullColumnRank() {
        return this.getRank() == this.getMinDim();
    }

    public final boolean isFullSize() {
        return false;
    }

    public final boolean isSolvable() {
        return this.isComputed() && this.isFullColumnRank();
    }

    public MatrixStore<N> reconstruct() {
        return MatrixUtils.reconstruct(this);
    }

    @Override
    public void reset() {
        super.reset();
    }

    /**
     * Solve [A]*[X]=[B] by first solving [Q]*[Y]=[B] and then [R]*[X]=[Y]. [X]
     * minimises the 2-norm of [Q]*[R]*[X]-[B].
     * 
     * @param aRHS The right hand side [B]
     * @return [X]
     */
    @Override
    public MatrixStore<N> solve(final MatrixStore<N> aRHS) {
        return this.solve(aRHS, this.makeZero(aRHS.getRowDim(), aRHS.getColDim()));
    }

    /**
     * "preallocated" is used to form the results, but the solution is
     * in the returned MatrixStore.
     * 
     * @see org.ojalgo.matrix.decomposition.AbstractDecomposition#solve(org.ojalgo.matrix.store.MatrixStore, org.ojalgo.matrix.decomposition.DecompositionStore)
     */
    @Override
    public MatrixStore<N> solve(final MatrixStore<N> aRHS, final DecompositionStore<N> preallocated) {

        preallocated.fillMatching(aRHS);

        final DecompositionStore<N> tmpStore = this.getInPlace();
        final int tmpRowDim = tmpStore.getRowDim();
        final int tmpColDim = tmpStore.getColDim();

        final DecompositionStore.HouseholderReference<N> tmpReference = new DecompositionStore.HouseholderReference<N>(tmpStore, true);

        final int tmpLimit = tmpStore.getMinDim();
        for (int j = 0; j < tmpLimit; j++) {

            tmpReference.row = j;
            tmpReference.col = j;

            if (!tmpReference.isZero()) {
                preallocated.transformLeft(tmpReference, 0);
            }
        }

        preallocated.substituteBackwards(tmpStore, false);

        if (tmpColDim < tmpRowDim) {
            final int[] tmpRange = MatrixUtils.makeIncreasingRange(0, tmpColDim);
            return new RowsStore<N>(preallocated, tmpRange);
        } else {
            return preallocated;
        }
    }

    /**
     * @return L as in R<sup>T</sup>.
     */
    protected final DecompositionStore<N> getL() {

        final int tmpRowDim = this.getColDim();
        final int tmpColDim = this.getMinDim();

        final DecompositionStore<N> retVal = this.makeZero(tmpRowDim, tmpColDim);

        final DecompositionStore<N> tmpStore = this.getInPlace();
        for (int j = 0; j < tmpColDim; j++) {
            for (int i = j; i < tmpRowDim; i++) {
                retVal.set(i, j, tmpStore.get(j, i));
            }
        }

        return retVal;
    }

}
