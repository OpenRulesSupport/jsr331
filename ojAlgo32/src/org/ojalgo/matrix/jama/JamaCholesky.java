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
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.context.NumberContext;

/**
 * This class adapts JAMA's CholeskyDecomposition to ojAlgo's
 * {@linkplain Cholesky} interface.
 *
 * @author apete
 */
public final class JamaCholesky extends JamaAbstractDecomposition implements Cholesky<Double> {

    private CholeskyDecomposition myDelegate;

    /**
     * Not recommended to use this constructor directly.
     * Consider using the static factory method
     * {@linkplain org.ojalgo.matrix.decomposition.CholeskyDecomposition#makeJama()}
     * instead.
     */
    public JamaCholesky() {
        super();
    }

    public boolean compute(final Access2D<?> aStore, final boolean checkHermitian) {
        return this.compute(aStore);
    }

    public boolean computeWithCheck(final MatrixStore<?> aStore) {
        return this.compute(aStore);
    }

    public boolean equals(final MatrixStore<Double> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(aStore, this, aCntxt);
    }

    public JamaMatrix getD() {

        final Matrix tmpL = myDelegate.getL();

        final int tmpRowDim = tmpL.getRowDimension();
        final int tmpColDim = tmpL.getColumnDimension();
        final int tmpMinDim = Math.min(tmpRowDim, tmpColDim);

        final JamaMatrix retVal = new JamaMatrix(new Matrix(tmpRowDim, tmpColDim));

        double tmpVal;
        for (int ij = INT_ZERO; ij < tmpMinDim; ij++) {
            tmpVal = tmpL.get(ij, ij);
            retVal.update(ij, ij, tmpVal * tmpVal);
        }

        return retVal;
    }

    public Double getDeterminant() {

        final MatrixStore<Double> tmpD = this.getD();
        final int tmpMinDim = tmpD.getMinDim();

        Scalar<Double> retVal = tmpD.toScalar(INT_ZERO, INT_ZERO);
        for (int ij = 1; ij < tmpMinDim; ij++) {
            retVal = retVal.multiply(tmpD.get(ij, ij));
        }

        return retVal.getNumber();
    }

    @Override
    public JamaMatrix getInverse() {
        return this.solve(this.makeEyeStore(myDelegate.getL().getRowDimension(), myDelegate.getL().getColumnDimension()));
    }

    public JamaMatrix getL() {
        return new JamaMatrix(myDelegate.getL());
    }

    public JamaMatrix getOldL() {

        final Matrix tmpL = myDelegate.getL();

        final int tmpRowDim = tmpL.getRowDimension();
        final int tmpColDim = tmpL.getColumnDimension();

        final JamaMatrix retVal = new JamaMatrix(new Matrix(tmpRowDim, tmpColDim));

        double tmpDiagVal;
        for (int j = INT_ZERO; j < tmpColDim; j++) {
            tmpDiagVal = tmpL.get(j, j);
            for (int i = j; i < tmpRowDim; i++) {
                retVal.update(i, j, tmpL.get(i, j) / tmpDiagVal);
            }
        }

        return retVal;
    }

    public JamaMatrix getOldU() {
        return this.getOldL().transpose();
    }

    public JamaMatrix getP() {
        return this.makeEyeStore(myDelegate.getL().getRowDimension(), myDelegate.getL().getRowDimension());
        //return MatrixUtils.makeIdentity(PrimitiveDenseStore.FACTORY, myDelegate.getL().getRowDimension());
    }

    public int[] getPivotOrder() {
        return MatrixUtils.makeIncreasingRange(INT_ZERO, this.getOldL().getRowDim());
    }

    public JamaMatrix getR() {
        return new JamaMatrix(myDelegate.getL().transpose());
    }

    public int getRank() {

        int retVal = INT_ZERO;

        final MatrixStore<Double> tmpD = this.getD();
        final int tmpMinDim = tmpD.getMinDim();

        for (int ij = INT_ZERO; ij < tmpMinDim; ij++) {
            if (!tmpD.toScalar(ij, ij).isZero()) {
                retVal++;
            }
        }

        return retVal;
    }

    public JamaMatrix getRowEchelonForm() {
        return this.getOldU();
    }

    public boolean isAspectRatioNormal() {
        return BOOLEAN_TRUE;
    }

    public boolean isComputed() {
        return myDelegate != null;
    }

    public boolean isFullSize() {
        return BOOLEAN_TRUE;
    }

    public boolean isSingular() {

        boolean retVal = BOOLEAN_TRUE;

        final MatrixStore<Double> tmpD = this.getD();
        final int tmpMinDim = tmpD.getMinDim();

        for (int ij = INT_ZERO; retVal && (ij < tmpMinDim); ij++) {
            retVal &= !tmpD.toScalar(ij, ij).isZero();
        }

        return !retVal;
    }

    public boolean isSolvable() {
        return (myDelegate != null) && myDelegate.isSPD();
    }

    public boolean isSPD() {
        return this.isSolvable();
    }

    public boolean isSquareAndNotSingular() {

        boolean retVal = BOOLEAN_TRUE;

        final MatrixStore<Double> tmpD = this.getD();
        final int tmpMinDim = tmpD.getMinDim();

        for (int ij = INT_ZERO; retVal && (ij < tmpMinDim); ij++) {
            retVal &= !tmpD.toScalar(ij, ij).isZero();
        }

        return retVal;
    }

    public MatrixStore<Double> reconstruct() {
        return MatrixUtils.reconstruct(this);
    }

    public void reset() {
        myDelegate = null;
    }

    @Override
    public String toString() {
        return myDelegate.toString();
    }

    @Override
    boolean compute(final Matrix aDelegate) {
        myDelegate = new CholeskyDecomposition(aDelegate);
        return myDelegate.isSPD();
    }

    @Override
    Matrix solve(final Matrix aRHS) {
        return myDelegate.solve(aRHS);
    }

}
