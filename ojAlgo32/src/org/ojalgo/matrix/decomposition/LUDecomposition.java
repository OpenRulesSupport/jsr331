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

import static org.ojalgo.constant.PrimitiveMath.*;

import java.math.BigDecimal;

import org.ojalgo.access.Access2D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.jama.JamaLU;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.LowerTriangularStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.RowsStore;
import org.ojalgo.matrix.store.UpperTriangularStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;

/**
 * You create instances of (some subclass of) this class by calling one
 * of the static factory methods: {@linkplain #makeBig()},
 * {@linkplain #makeComplex()}, {@linkplain #makePrimitive()} or
 * {@linkplain #makeJama()}.
 *
 * @author apete
 */
public abstract class LUDecomposition<N extends Number> extends InPlaceDecomposition<N> implements LU<N> {

    public static final class Pivot {

        private final int[] myOrder;
        private int mySign;
        private boolean myModified = false;

        public Pivot(final int aRowDim) {

            super();

            myOrder = MatrixUtils.makeIncreasingRange(0, aRowDim);
            mySign = 1;
        }

        public void change(final int aRow1, final int aRow2) {

            if (aRow1 != aRow2) {

                final int temp = myOrder[aRow1];
                myOrder[aRow1] = myOrder[aRow2];
                myOrder[aRow2] = temp;

                mySign = -mySign;

                myModified = true;

            } else {
                // Why?!
            }
        }

        public int[] getOrder() {
            return myOrder;
        }

        public final boolean isModified() {
            return myModified;
        }

        public int signum() {
            return mySign;
        }

    }

    static final class Big extends LUDecomposition<BigDecimal> {

        Big() {
            super(BigDenseStore.FACTORY);
        }

    }

    static final class Complex extends LUDecomposition<ComplexNumber> {

        Complex() {
            super(ComplexDenseStore.FACTORY);
        }

    }

    static final class Primitive extends LUDecomposition<Double> {

        Primitive() {
            super(PrimitiveDenseStore.FACTORY);
        }

    }

    @SuppressWarnings("unchecked")
    public static final <N extends Number> LU<N> make(final Access2D<N> aTypical) {

        final N tmpNumber = aTypical.get(0, 0);

        if (tmpNumber instanceof BigDecimal) {
            return (LU<N>) LUDecomposition.makeBig();
        } else if (tmpNumber instanceof ComplexNumber) {
            return (LU<N>) LUDecomposition.makeComplex();
        } else if (tmpNumber instanceof Double) {

            final int tmpMaxDim = Math.max(aTypical.getRowDim(), aTypical.getColDim());

            if ((tmpMaxDim <= 32) || (tmpMaxDim >= 46340)) { //16,32,2
                return (LU<N>) LUDecomposition.makeJama();
            } else {
                return (LU<N>) LUDecomposition.makePrimitive();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static final LU<BigDecimal> makeBig() {
        return new Big();
    }

    public static final LU<ComplexNumber> makeComplex() {
        return new Complex();
    }

    public static final LU<Double> makeJama() {
        return new JamaLU();
    }

    public static final LU<Double> makePrimitive() {
        return new Primitive();
    }

    private Pivot myPivot;

    protected LUDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory) {
        super(aFactory);
    }

    public boolean compute(final Access2D<?> aStore) {
        return this.compute(aStore, false);
    }

    public boolean computeWithoutPivoting(final MatrixStore<?> aStore) {
        return this.compute(aStore, true);
    }

    public boolean equals(final MatrixStore<N> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(aStore, this, aCntxt);
    }

    public N getDeterminant() {

        final AggregatorFunction<N> tmpAggrFunc = this.getAggregatorCollection().product();

        this.getInPlace().visitDiagonal(0, 0, tmpAggrFunc);

        if (myPivot.signum() == -1) {
            return tmpAggrFunc.toScalar().negate().getNumber();
        } else {
            return tmpAggrFunc.getNumber();
        }
    }

    @Override
    public MatrixStore<N> getInverse() {
        return this.invert(this.makeZero(this.getColDim(), this.getRowDim()));
    }

    @Override
    public MatrixStore<N> getInverse(final DecompositionStore<N> preallocated) {
        preallocated.fillAll(this.getStaticZero());
        return this.invert(preallocated);
    }

    public MatrixStore<N> getL() {
        return new LowerTriangularStore<N>(this.getInPlace(), true);
    }

    public int[] getPivotOrder() {
        return myPivot.getOrder();
    }

    public int getRank() {

        int retVal = 0;

        final DecompositionStore<N> tmpStore = this.getInPlace();

        final int tmpMinDim = tmpStore.getMinDim();
        for (int ij = 0; ij < tmpMinDim; ij++) {
            if (!tmpStore.isZero(ij, ij)) {
                retVal++;
            }
        }

        return retVal;
    }

    public int[] getReducedPivots() {

        final int[] retVal = new int[this.getRank()];
        final int[] tmpFullPivots = this.getPivotOrder();

        final DecompositionStore<N> tmpInPlace = this.getInPlace();

        int tmpRedInd = 0;
        for (int ij = 0; ij < tmpFullPivots.length; ij++) {
            if (!tmpInPlace.isZero(ij, ij)) {
                retVal[tmpRedInd++] = tmpFullPivots[ij];
            }
        }

        return retVal;
    }

    public MatrixStore<N> getU() {
        return new UpperTriangularStore<N>(this.getInPlace(), false);
    }

    public final boolean isFullSize() {
        return false;
    }

    public boolean isSolvable() {
        return this.isComputed() && this.isSquareAndNotSingular();
    }

    public final boolean isSquareAndNotSingular() {

        boolean retVal = this.getRowDim() == this.getColDim();

        final DecompositionStore<N> tmpStore = this.getInPlace();
        final int tmpMinDim = tmpStore.getMinDim();

        for (int ij = 0; retVal && (ij < tmpMinDim); ij++) {
            retVal &= !tmpStore.isZero(ij, ij);
        }

        return retVal;
    }

    public MatrixStore<N> reconstruct() {
        return MatrixUtils.reconstruct(this);
    }

    @Override
    public void reset() {

        super.reset();

        myPivot = null;
    }

    /**
     * Solves [this][X] = [aRHS] by first solving
     * <pre>[L][Y] = [aRHS]</pre>
     * and then
     * <pre>[U][X] = [Y]</pre>.
     * 
     * @param aRHS The right hand side
     * @return [X]
     */
    @Override
    public MatrixStore<N> solve(final MatrixStore<N> aRHS) {

        final DecompositionStore<N> retVal = this.makeZero(aRHS.getRowDim(), aRHS.getColDim());

        return this.solve(aRHS, retVal);
    }

    /**
     * The solution will be written to "preallocated" and then returned.
     * 
     * @see org.ojalgo.matrix.decomposition.AbstractDecomposition#solve(org.ojalgo.matrix.store.MatrixStore, org.ojalgo.matrix.decomposition.DecompositionStore)
     */
    @Override
    public MatrixStore<N> solve(final MatrixStore<N> aRHS, final DecompositionStore<N> preallocated) {

        preallocated.fillMatching(new RowsStore<N>(aRHS, myPivot.getOrder()));

        final DecompositionStore<N> tmpBody = this.getInPlace();

        preallocated.substituteForwards(tmpBody, true, false);

        preallocated.substituteBackwards(tmpBody, false);

        return preallocated;
    }

    private final boolean compute(final Access2D<?> aStore, final boolean assumeNoPivotingRequired) {

        this.reset();

        final DecompositionStore<N> tmpInPlace = this.setInPlace(aStore);

        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = this.getColDim();
        final int tmpMinDim = Math.min(tmpRowDim, tmpColDim);

        myPivot = new Pivot(tmpRowDim);

        final SimpleArray<N> tmpMultipliers = this.makeArray(tmpRowDim);

        // Main loop - along the diagonal
        for (int ij = 0; ij < tmpMinDim; ij++) {

            if (!assumeNoPivotingRequired) {
                // Find next pivot row
                final int tmpPivotRow = tmpInPlace.getIndexOfLargestInColumn(ij, ij);

                // Pivot?
                if (tmpPivotRow != ij) {
                    tmpInPlace.exchangeRows(tmpPivotRow, ij);
                    myPivot.change(tmpPivotRow, ij);
                }
            }

            // Do the calculations...
            if (!tmpInPlace.isZero(ij, ij)) {

                // Calculate multipliers and copy to local column
                // Current column, below the diagonal
                tmpInPlace.divideAndCopyColumn(ij, ij, tmpMultipliers);

                // Apply transformations to everything below and to the right of the pivot element
                tmpInPlace.applyTransformations(ij, tmpMultipliers, false);

            } else {

                tmpInPlace.set(ij, ij, ZERO);
            }

        }

        return this.computed(true);
    }

    private MatrixStore<N> invert(final DecompositionStore<N> retVal) {

        final int[] tmpPivotOrder = myPivot.getOrder();
        final int tmpRowDim = this.getRowDim();
        for (int i = 0; i < tmpRowDim; i++) {
            retVal.set(i, tmpPivotOrder[i], PrimitiveMath.ONE);
        }

        final DecompositionStore<N> tmpBody = this.getInPlace();

        retVal.substituteForwards(tmpBody, true, !myPivot.isModified());

        retVal.substituteBackwards(tmpBody, false);

        return retVal;
    }

}
