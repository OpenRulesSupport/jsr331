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

import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.type.context.NumberContext;

/**
 * This class adapts JAMA's QRDecomposition to ojAlgo's {@linkplain QR}
 * interface.
 *
 * @author apete
 */
public final class JamaQR extends JamaAbstractDecomposition implements QR<Double> {

    private QRDecomposition myDelegate;

    /**
     * Not recommended to use this constructor directly.
     * Consider using the static factory method
     * {@linkplain org.ojalgo.matrix.decomposition.QRDecomposition#makeJama()}
     * instead.
     */
    public JamaQR() {
        super();
    }

    public boolean equals(final MatrixStore<Double> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(aStore, this, aCntxt);
    }

    @Override
    public JamaMatrix getInverse() {
        return this.solve(this.makeEyeStore(myDelegate.getQ().getRowDimension(), myDelegate.getR().getColumnDimension()));
    }

    public JamaMatrix getQ() {
        return new JamaMatrix(myDelegate.getQ());
    }

    public JamaMatrix getR() {
        return new JamaMatrix(myDelegate.getR());
    }

    public int getRank() {

        int retVal = INT_ZERO;

        final MatrixStore<Double> tmpR = this.getR();
        final int tmpMinDim = tmpR.getMinDim();

        for (int ij = INT_ZERO; ij < tmpMinDim; ij++) {
            if (!tmpR.toScalar(ij, ij).isZero()) {
                retVal++;
            }
        }

        return retVal;
    }

    public boolean isAspectRatioNormal() {
        return myDelegate.getQ().getRowDimension() >= myDelegate.getR().getColumnDimension();

    }

    public boolean isComputed() {
        return myDelegate != null;
    }

    public boolean isFullColumnRank() {
        return this.isSolvable();
    }

    public boolean isFullSize() {
        return BOOLEAN_FALSE;
    }

    public boolean isSolvable() {
        return (myDelegate != null) && myDelegate.isFullRank();
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

        myDelegate = new QRDecomposition(aDelegate);

        return this.isComputed();
    }

    @Override
    Matrix solve(final Matrix aRHS) {
        return myDelegate.solve(aRHS);
    }

}
