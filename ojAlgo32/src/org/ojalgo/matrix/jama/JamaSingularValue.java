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

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * This class adapts JAMA's SingularValueDecomposition to ojAlgo's
 * {@linkplain SingularValue} interface.
 * 
 * speed: 52.641s
 * 
 *
 * @author apete
 */
public final class JamaSingularValue extends JamaAbstractDecomposition implements SingularValue<Double> {

    private SingularValueDecomposition myDelegate;
    private boolean myTransposed;
    private JamaMatrix myPseudoinverse;

    /**
     * Not recommended to use this constructor directly.
     * Consider using the static factory method
     * {@linkplain org.ojalgo.matrix.decomposition.SingularValueDecomposition#makeJama()}
     * instead.
     */
    public JamaSingularValue() {
        super();
    }

    public boolean compute(final Access2D<?> aMtrx, final boolean singularValuesOnly) {

        this.reset();

        final Matrix tmpCast = JamaAbstractDecomposition.cast(aMtrx);

        return this.compute(tmpCast, singularValuesOnly);
    }

    public boolean equals(final MatrixStore<Double> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(aStore, this, aCntxt);
    }

    public double getCondition() {
        return myDelegate.cond();
    }

    public JamaMatrix getD() {
        return new JamaMatrix(myDelegate.getS());
    }

    public double getFrobeniusNorm() {

        double retVal = PrimitiveMath.ZERO;
        double tmpVal;

        final Array1D<Double> tmpSingularValues = this.getSingularValues();

        for (int i = INT_ZERO; i < tmpSingularValues.size(); i++) {
            tmpVal = tmpSingularValues.doubleValue(i);
            retVal += tmpVal * tmpVal;
        }

        return Math.sqrt(retVal);
    }

    @Override
    public JamaMatrix getInverse() {

        if (myPseudoinverse == null) {

            final double[][] tmpQ1 = this.getQ1().getDelegate().getArray();
            final double[] tmpSingular = myDelegate.getSingularValues();

            final Matrix tmpMtrx = new Matrix(tmpSingular.length, tmpQ1.length);

            for (int i = INT_ZERO; i < tmpSingular.length; i++) {
                if (TypeUtils.isZero(tmpSingular[i])) {
                    for (int j = INT_ZERO; j < tmpQ1.length; j++) {
                        tmpMtrx.set(i, j, PrimitiveMath.ZERO);
                    }
                } else {
                    for (int j = INT_ZERO; j < tmpQ1.length; j++) {
                        tmpMtrx.set(i, j, tmpQ1[j][i] / tmpSingular[i]);
                    }
                }
            }

            myPseudoinverse = new JamaMatrix(this.getQ2().getDelegate().times(tmpMtrx));
        }

        return myPseudoinverse;
    }

    public double getKyFanNorm(final int k) {

        double retVal = PrimitiveMath.ZERO;

        final Array1D<Double> tmpSingularValues = this.getSingularValues();
        final int tmpK = Math.min(tmpSingularValues.size(), k);

        for (int i = INT_ZERO; i < tmpK; i++) {
            retVal += tmpSingularValues.doubleValue(i);
        }

        return retVal;
    }

    public double getOperatorNorm() {
        return this.getSingularValues().get(INT_ZERO);
    }

    public JamaMatrix getQ1() {
        return new JamaMatrix(myTransposed ? myDelegate.getV() : myDelegate.getU());
    }

    public JamaMatrix getQ2() {
        return new JamaMatrix(myTransposed ? myDelegate.getU() : myDelegate.getV());
    }

    public int getRank() {
        return myDelegate.rank();
    }

    public Array1D<Double> getSingularValues() {
        return Array1D.PRIMITIVE.copy(myDelegate.getSingularValues());
    }

    public double getTraceNorm() {
        return this.getKyFanNorm(myDelegate.getSingularValues().length);
    }

    public boolean isAspectRatioNormal() {
        return myTransposed;
    }

    public boolean isComputed() {
        return myDelegate != null;
    }

    public boolean isFullSize() {
        return BOOLEAN_FALSE;
    }

    public boolean isOrdered() {
        return BOOLEAN_TRUE;
    }

    public boolean isSolvable() {
        return this.isComputed();
    }

    public MatrixStore<Double> reconstruct() {
        return MatrixUtils.reconstruct(this);
    }

    public void reset() {

        myDelegate = null;

        myPseudoinverse = null;
    }

    /**
     * Internally this implementation uses the pseudoinverse that is recreated 
     * with every call. 
     */
    @Override
    public JamaMatrix solve(final MatrixStore<Double> aRHS) {
        return this.getInverse().multiplyRight(aRHS);
    }

    @Override
    public String toString() {
        return myDelegate.toString();
    }

    @Override
    boolean compute(final Matrix aDelegate) {
        return this.compute(aDelegate, BOOLEAN_FALSE);
    }

    boolean compute(final Matrix aDelegate, final boolean singularValuesOnly) {

        Matrix tmpMtrx;

        if (aDelegate.getColumnDimension() <= aDelegate.getRowDimension()) {
            myTransposed = BOOLEAN_FALSE;
            tmpMtrx = aDelegate;
        } else {
            myTransposed = BOOLEAN_TRUE;
            tmpMtrx = aDelegate.transpose();
        }

        myDelegate = new SingularValueDecomposition(tmpMtrx, !singularValuesOnly, !singularValuesOnly);

        return this.isComputed();
    }

    @Override
    Matrix solve(final Matrix aRHS) {
        ProgrammingError.throwForIllegalInvocation();
        return null;
    }

}
