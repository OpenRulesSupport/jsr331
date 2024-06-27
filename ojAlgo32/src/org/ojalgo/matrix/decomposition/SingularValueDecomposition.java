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
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.jama.JamaSingularValue;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.TypeUtils;

/**
 * You create instances of (some subclass of) this class by calling one
 * of the static factory methods: {@linkplain #makeBig()},
 * {@linkplain #makeComplex()}, {@linkplain #makePrimitive()},
 * {@linkplain #makeAlternative()} or {@linkplain #makeJama()}.
 *
 * @author apete
 */
public abstract class SingularValueDecomposition<N extends Number & Comparable<N>> extends AbstractDecomposition<N> implements SingularValue<N> {

    @SuppressWarnings("unchecked")
    public static final <N extends Number> SingularValue<N> make(final Access2D<N> aTypical) {

        final N tmpNumber = aTypical.get(0, 0);

        if (tmpNumber instanceof BigDecimal) {

            return (SingularValue<N>) SingularValueDecomposition.makeBig();

        } else if (tmpNumber instanceof ComplexNumber) {

            return (SingularValue<N>) SingularValueDecomposition.makeComplex();

        } else if (tmpNumber instanceof Double) {

            final int tmpMaxDim = Math.max(aTypical.getRowDim(), aTypical.getColDim());

            if ((tmpMaxDim > 128) && (tmpMaxDim < 46340)) {

                return (SingularValue<N>) SingularValueDecomposition.makePrimitive();

            } else {

                return (SingularValue<N>) SingularValueDecomposition.makeJama();
            }

        } else {

            throw new IllegalArgumentException();
        }
    }

    public static final SingularValue<Double> makeAlternative() {
        return new SVDold30.Primitive();
    }

    public static final SingularValue<BigDecimal> makeBig() {
        return new SVDold30.Big();
    }

    public static final SingularValue<ComplexNumber> makeComplex() {
        return new SVDold30.Complex();
    }

    public static final SingularValue<Double> makeJama() {
        return new JamaSingularValue();
    }

    public static final SingularValue<Double> makePrimitive() {
        return new SVDnew32.Primitive();
    }

    private final BidiagonalDecomposition<N> myBidiagonal;
    private transient MatrixStore<N> myD;
    private transient MatrixStore<N> myQ1;
    private transient MatrixStore<N> myQ2;
    private transient Array1D<Double> mySingularValues;
    private boolean mySingularValuesOnly = false;
    private boolean myTransposed = false;
    private transient MatrixStore<N> myInverse;

    @SuppressWarnings("unused")
    private SingularValueDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory) {
        this(aFactory, null);
    }

    protected SingularValueDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory,
            final BidiagonalDecomposition<N> aBidiagonal) {

        super(aFactory);

        myBidiagonal = aBidiagonal;
    }

    public final boolean compute(final Access2D<?> aMtrx) {
        return this.compute(aMtrx, false);
    }

    public boolean compute(final Access2D<?> aMtrx, final boolean singularValuesOnly) {

        this.reset();

        if (aMtrx.getRowDim() >= aMtrx.getColDim()) {
            myTransposed = false;
        } else {
            myTransposed = true;
        }

        mySingularValuesOnly = singularValuesOnly;

        boolean retVal = false;

        try {

            retVal = this.doCompute(myTransposed ? this.wrap(aMtrx).builder().transpose().build() : aMtrx, singularValuesOnly);

        } catch (final Exception anException) {

            BasicLogger.logError(anException.toString());

            this.reset();

            retVal = false;
        }

        return this.computed(retVal);
    }

    public final double getCondition() {

        final Array1D<Double> tmpSingularValues = this.getSingularValues();

        return tmpSingularValues.doubleValue(0) / tmpSingularValues.doubleValue(tmpSingularValues.length - 1);
    }

    public final MatrixStore<N> getD() {

        if ((myD == null) && this.isComputed()) {
            myD = this.makeD();
        }

        return myD;
    }

    public final double getFrobeniusNorm() {

        double retVal = PrimitiveMath.ZERO;

        final Array1D<Double> tmpSingularValues = this.getSingularValues();
        double tmpVal;

        for (int i = tmpSingularValues.size() - 1; i >= 0; i--) {
            tmpVal = tmpSingularValues.doubleValue(i);
            retVal += tmpVal * tmpVal;
        }

        return Math.sqrt(retVal);
    }

    public final MatrixStore<N> getInverse() {

        if (myInverse == null) {

            final MatrixStore<N> tmpQ1 = this.getQ1();
            final MatrixStore<N> tmpD = this.getD();

            final int tmpRowDim = tmpD.getRowDim();
            final int tmpColDim = tmpQ1.getRowDim();

            final PhysicalStore<N> tmpMtrx = this.makeZero(tmpRowDim, tmpColDim);

            final N tmpZero = this.getStaticZero();

            N tmpSingularValue;
            for (int i = 0; i < tmpRowDim; i++) {
                if (tmpD.isZero(i, i)) {
                    tmpMtrx.fillRow(i, 0, tmpZero);
                } else {
                    tmpSingularValue = tmpD.get(i, i);
                    for (int j = 0; j < tmpColDim; j++) {
                        tmpMtrx.set(i, j, tmpQ1.toScalar(j, i).divide(tmpSingularValue).getNumber());
                    }
                }
            }

            myInverse = tmpMtrx.multiplyLeft(this.getQ2());
        }

        return myInverse;
    }

    public final MatrixStore<N> getInverse(final DecompositionStore<N> preallocated) {

        if (myInverse == null) {

            final MatrixStore<N> tmpQ1 = this.getQ1();
            final MatrixStore<N> tmpD = this.getD();

            final int tmpRowDim = tmpD.getRowDim();
            final int tmpColDim = tmpQ1.getRowDim();

            final PhysicalStore<N> tmpMtrx = preallocated;

            final N tmpZero = this.getStaticZero();

            N tmpSingularValue;
            for (int i = 0; i < tmpRowDim; i++) {
                if (tmpD.isZero(i, i)) {
                    tmpMtrx.fillRow(i, 0, tmpZero);
                } else {
                    tmpSingularValue = tmpD.get(i, i);
                    for (int j = 0; j < tmpColDim; j++) {
                        tmpMtrx.set(i, j, tmpQ1.toScalar(j, i).divide(tmpSingularValue).getNumber());
                    }
                }
            }

            myInverse = tmpMtrx.multiplyLeft(this.getQ2());
        }

        return myInverse;
    }

    public final double getKyFanNorm(final int k) {

        final Array1D<Double> tmpSingularValues = this.getSingularValues();

        double retVal = PrimitiveMath.ZERO;

        for (int i = Math.min(tmpSingularValues.size(), k) - 1; i >= 0; i--) {
            retVal += tmpSingularValues.doubleValue(i);
        }

        return retVal;
    }

    public final double getOperatorNorm() {
        return this.getSingularValues().doubleValue(0);
    }

    public final MatrixStore<N> getQ1() {

        if ((myQ1 == null) && !mySingularValuesOnly && this.isComputed()) {
            if (myTransposed) {
                myQ1 = this.makeQ2();
            } else {
                myQ1 = this.makeQ1();
            }
        }

        return myQ1;
    }

    public final MatrixStore<N> getQ2() {

        if ((myQ2 == null) && !mySingularValuesOnly && this.isComputed()) {
            if (myTransposed) {
                myQ2 = this.makeQ1();
            } else {
                myQ2 = this.makeQ2();
            }
        }

        return myQ2;
    }

    public final int getRank() {

        final Array1D<Double> tmpSingularValues = this.getSingularValues();
        int retVal = tmpSingularValues.length;

        // Tolerance based on min-dim but should be max-dim
        final double tmpTolerance = retVal * tmpSingularValues.doubleValue(0) * PrimitiveMath.MACHINE_DOUBLE_ERROR;

        for (int i = retVal - 1; i >= 0; i--) {
            if (TypeUtils.isZero(tmpSingularValues.doubleValue(i), tmpTolerance)) {
                retVal--;
            } else {
                return retVal;
            }
        }

        return retVal;
    }

    public final Array1D<Double> getSingularValues() {

        if ((mySingularValues == null) && this.isComputed()) {
            mySingularValues = this.makeSingularValues();
        }

        return mySingularValues;
    }

    public final double getTraceNorm() {
        return this.getKyFanNorm(this.getSingularValues().length);
    }

    @Override
    public final boolean isAspectRatioNormal() {
        return super.aspectRatioNormal(myBidiagonal.isAspectRatioNormal());
    }

    public MatrixStore<N> reconstruct() {
        return MatrixUtils.reconstruct(this);
    }

    @Override
    public void reset() {

        super.reset();

        myBidiagonal.reset();

        myD = null;
        myQ1 = null;
        myQ2 = null;

        myInverse = null;

        mySingularValuesOnly = false;
        myTransposed = false;
    }

    public MatrixStore<N> solve(final MatrixStore<N> aRHS) {
        return this.getInverse().multiplyRight(aRHS);
    }

    public MatrixStore<N> solve(final MatrixStore<N> aRHS, final DecompositionStore<N> preallocated) {
        preallocated.fillByMultiplying(this.getInverse(), aRHS);
        return preallocated;
    }

    protected final boolean computeBidiagonal(final Access2D<?> aStore) {
        return myBidiagonal.compute(aStore);
    }

    protected abstract boolean doCompute(Access2D<?> aMtrx, boolean singularValuesOnly);

    protected final DiagonalAccess<N> getBidiagonalAccessD() {
        return myBidiagonal.getDiagonalAccessD();
    }

    protected final DecompositionStore<N> getBidiagonalQ1() {
        return (DecompositionStore<N>) myBidiagonal.getQ1();
    }

    protected final DecompositionStore<N> getBidiagonalQ2() {
        return (DecompositionStore<N>) myBidiagonal.getQ2();
    }

    protected final boolean isTransposed() {
        return myTransposed;
    }

    protected abstract MatrixStore<N> makeD();

    protected abstract MatrixStore<N> makeQ1();

    protected abstract MatrixStore<N> makeQ2();

    protected abstract Array1D<Double> makeSingularValues();

    final void setD(final MatrixStore<N> someD) {
        myD = someD;
    }

    final void setQ1(final MatrixStore<N> someQ1) {
        myQ1 = someQ1;
    }

    final void setQ2(final MatrixStore<N> someQ2) {
        myQ2 = someQ2;
    }

    final void setSingularValues(final Array1D<Double> someSingularValues) {
        mySingularValues = someSingularValues;
    }

}
