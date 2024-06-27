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
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.jama.JamaEigenvalue;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.WrapperStore;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;

/**
 * You create instances of (some subclass of) this class by calling one
 * of the static factory methods: {@linkplain #makeBig()},
 * {@linkplain #makePrimitive()} or
 * {@linkplain #makeJama()}.
 *
 * @author apete
 */
public abstract class EigenvalueDecomposition<N extends Number> extends AbstractDecomposition<N> implements Eigenvalue<N> {

    @SuppressWarnings("unchecked")
    public static final <N extends Number> Eigenvalue<N> make(final Access2D<N> aTypical) {

        final N tmpNumber = aTypical.get(0, 0);
        final int tmpDim = aTypical.getColDim();

        if (tmpNumber instanceof BigDecimal) {

            final boolean tmpSymmetric = MatrixUtils.isSymmetric(aTypical);

            return (Eigenvalue<N>) EigenvalueDecomposition.makeBig(tmpSymmetric);

        } else if (tmpNumber instanceof ComplexNumber) {

            final boolean tmpHermitian = MatrixUtils.isHermitian(WrapperStore.makeComplex(aTypical));

            return (Eigenvalue<N>) EigenvalueDecomposition.makeComplex(tmpHermitian);

        } else if (tmpNumber instanceof Double) {

            final boolean tmpSymmetric = MatrixUtils.isSymmetric(aTypical);

            if ((tmpDim > 128) && (tmpDim < 46340)) {

                return (Eigenvalue<N>) EigenvalueDecomposition.makePrimitive(tmpSymmetric);

            } else {

                return (Eigenvalue<N>) EigenvalueDecomposition.makeJama(tmpSymmetric);
            }

        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @return A BigDecimal adapter to PrimitiveEigenvalue.
     * @deprecated v30
     */
    @Deprecated
    public static final Eigenvalue<BigDecimal> makeBig() {
        return new Eigenvalue<BigDecimal>() {

            private final Eigenvalue<Double> myDelegate = EigenvalueDecomposition.makePrimitive();

            public boolean compute(final Access2D<?> aStore) {
                return myDelegate.compute(PrimitiveDenseStore.FACTORY.copy(aStore));
            }

            public boolean compute(final Access2D<?> aMtrx, final boolean eigenvaluesOnly) {
                return myDelegate.compute(PrimitiveDenseStore.FACTORY.copy(aMtrx), eigenvaluesOnly);
            }

            public boolean equals(final MatrixDecomposition<BigDecimal> aDecomp, final NumberContext aCntxt) {
                return false;
            }

            public boolean equals(final MatrixStore<BigDecimal> aStore, final NumberContext aCntxt) {
                return MatrixUtils.equals(aStore, this, aCntxt);
            }

            public MatrixStore<BigDecimal> getD() {
                return BigDenseStore.FACTORY.copy(myDelegate.getD());
            }

            public ComplexNumber getDeterminant() {
                return myDelegate.getDeterminant();
            }

            public Array1D<ComplexNumber> getEigenvalues() {
                return myDelegate.getEigenvalues();
            }

            public MatrixStore<BigDecimal> getInverse() {
                return BigDenseStore.FACTORY.copy(myDelegate.getInverse());
            }

            public MatrixStore<BigDecimal> getInverse(final DecompositionStore<BigDecimal> aSolution) {
                return BigDenseStore.FACTORY.copy(myDelegate.solve(PrimitiveDenseStore.FACTORY.copy(aSolution)));
            }

            public ComplexNumber getTrace() {
                return myDelegate.getTrace();
            }

            public MatrixStore<BigDecimal> getV() {
                return BigDenseStore.FACTORY.copy(myDelegate.getV());
            }

            public boolean isComputed() {
                return myDelegate.isComputed();
            }

            public boolean isFullSize() {
                return myDelegate.isFullSize();
            }

            public boolean isHermitian() {
                return myDelegate.isHermitian();
            }

            public boolean isOrdered() {
                return myDelegate.isOrdered();
            }

            public boolean isSolvable() {
                return myDelegate.isSolvable();
            }

            /**
             * @deprecated Use {@link #isHermitian()} instead
             */
            @Deprecated
            public boolean isSymmetric() {
                return this.isHermitian();
            }

            public MatrixStore<BigDecimal> reconstruct() {
                return BigDenseStore.FACTORY.copy(myDelegate.reconstruct());
            }

            public void reset() {
                myDelegate.reset();
            }

            public MatrixStore<BigDecimal> solve(final MatrixStore<BigDecimal> aRHS) {
                return BigDenseStore.FACTORY.copy(myDelegate.solve(PrimitiveDenseStore.FACTORY.copy(aRHS)));
            }

            public MatrixStore<BigDecimal> solve(final MatrixStore<BigDecimal> aRHS, final DecompositionStore<BigDecimal> aSolution) {
                return BigDenseStore.FACTORY.copy(myDelegate.solve(PrimitiveDenseStore.FACTORY.copy(aRHS), PrimitiveDenseStore.FACTORY.copy(aSolution)));
            }

        };
    }

    public static final Eigenvalue<BigDecimal> makeBig(final boolean symmetric) {
        return symmetric ? new HermitianEvD32.Big() : EigenvalueDecomposition.makeBig();
    }

    public static final Eigenvalue<ComplexNumber> makeComplex() {
        return EigenvalueDecomposition.makeComplex(true);
    }

    public static final Eigenvalue<ComplexNumber> makeComplex(final boolean symmetric) {
        return symmetric ? new HermitianEvD32.Complex() : null;
    }

    public static final Eigenvalue<Double> makeJama() {
        return new JamaEigenvalue.General();
    }

    public static final Eigenvalue<Double> makeJama(final boolean symmetric) {
        return symmetric ? new JamaEigenvalue.Symmetric() : new JamaEigenvalue.Nonsymmetric();
    }

    public static final Eigenvalue<Double> makePrimitive() {
        return new GeneralEvD.Primitive();
    }

    public static final Eigenvalue<Double> makePrimitive(final boolean symmetric) {
        return symmetric ? new HermitianEvD32.Primitive() : new NonsymmetricEvD.Primitive();
    }

    private MatrixStore<N> myD = null;
    private Array1D<ComplexNumber> myEigenvalues = null;
    private boolean myEigenvaluesOnly = false;
    private MatrixStore<N> myV = null;

    protected EigenvalueDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory) {
        super(aFactory);
    }

    public final boolean compute(final Access2D<?> aMtrx) {
        return this.compute(aMtrx, false);
    }

    public final MatrixStore<N> getD() {

        if ((myD == null) && !myEigenvaluesOnly && this.isComputed()) {
            myD = this.makeD();
        }

        return myD;
    }

    public final Array1D<ComplexNumber> getEigenvalues() {

        if ((myEigenvalues == null) && !myEigenvaluesOnly && this.isComputed()) {
            myEigenvalues = this.makeEigenvalues();
        }

        return myEigenvalues;
    }

    public final MatrixStore<N> getV() {

        if ((myV == null) && !myEigenvaluesOnly && this.isComputed()) {
            myV = this.makeV();
        }

        return myV;
    }

    public final MatrixStore<N> reconstruct() {
        return MatrixUtils.reconstruct(this);
    }

    @Override
    public void reset() {

        super.reset();

        myD = null;
        myEigenvalues = null;
        myV = null;

        myEigenvaluesOnly = false;
    }

    public final MatrixStore<N> solve(final MatrixStore<N> aRHS) {
        return this.getInverse().multiplyRight(aRHS);
    }

    public final MatrixStore<N> solve(final MatrixStore<N> aRHS, final DecompositionStore<N> preallocated) {
        preallocated.fillByMultiplying(this.getInverse(), aRHS);
        return preallocated;
    }

    protected final boolean compute(final Access2D<?> aMtrx, final boolean symmetric, final boolean eigenvaluesOnly) {

        this.reset();

        myEigenvaluesOnly = eigenvaluesOnly;

        boolean retVal = false;

        try {

            if (symmetric) {

                retVal = this.doSymmetric(aMtrx, eigenvaluesOnly);

            } else {

                retVal = this.doNonsymmetric(aMtrx, eigenvaluesOnly);
            }

        } catch (final Exception anException) {

            BasicLogger.logError(anException.toString());

            this.reset();

            retVal = false;
        }

        return this.computed(retVal);
    }

    protected abstract boolean doNonsymmetric(final Access2D<?> aMtrx, final boolean eigenvaluesOnly);

    protected abstract boolean doSymmetric(final Access2D<?> aMtrx, final boolean eigenvaluesOnly);

    protected abstract MatrixStore<N> makeD();

    protected abstract Array1D<ComplexNumber> makeEigenvalues();

    protected abstract MatrixStore<N> makeV();

    final void setD(final MatrixStore<N> newD) {
        myD = newD;
    }

    final void setEigenvalues(final Array1D<ComplexNumber> newEigenvalues) {
        myEigenvalues = newEigenvalues;
    }

    final void setV(final MatrixStore<N> newV) {
        myV = newV;
    }

}
