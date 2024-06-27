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
package org.ojalgo.matrix.store;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access2D;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.context.NumberContext;

/**
 * <p>
 * A {@linkplain MatrixStore} is a matrix (two-dimensional) store of numbers/scalars.
 * </p><p>
 * This interface does not define any methods that require implementations
 * to alter the matrix. Either the methods return matrix elements, some
 * meta data or produce new instances.
 * </p><p>
 * The methods {@linkplain #conjugate()}, {@linkplain #copy()} and
 * {@linkplain #transpose()} return {@linkplain PhysicalStore} instances.
 * {@linkplain PhysicalStore} extends {@linkplain MatrixStore}. It defines
 * additional methods, and is mutable.
 * </p>
 *
 * @author apete
 */
public interface MatrixStore<N extends Number> extends Access2D<N> {

    public static final class Builder<N extends Number> {

        static <N extends Number> MatrixStore<N> buildColumn(final int aMinRowDim, final MatrixStore<N>... aColStore) {
            MatrixStore<N> retVal = aColStore[0];
            for (int i = 1; i < aColStore.length; i++) {
                retVal = new AboveBelowStore<N>(retVal, aColStore[i]);
            }
            final int tmpRowDim = retVal.getRowDim();
            if (tmpRowDim < aMinRowDim) {
                retVal = new AboveBelowStore<N>(retVal, new ZeroStore<N>(retVal.factory(), aMinRowDim - tmpRowDim, retVal.getColDim()));
            }
            return retVal;
        }

        static <N extends Number> MatrixStore<N> buildColumn(final PhysicalStore.Factory<N, ?> aFactory, final int aMinRowDim, final N... aColStore) {
            MatrixStore<N> retVal = aFactory.columns(aColStore);
            final int tmpRowDim = retVal.getRowDim();
            if (tmpRowDim < aMinRowDim) {
                retVal = new AboveBelowStore<N>(retVal, new ZeroStore<N>(aFactory, aMinRowDim - tmpRowDim, retVal.getColDim()));
            }
            return retVal;
        }

        static <N extends Number> MatrixStore<N> buildRow(final int aMinColDim, final MatrixStore<N>... aRowStore) {
            MatrixStore<N> retVal = aRowStore[0];
            for (int j = 1; j < aRowStore.length; j++) {
                retVal = new LeftRightStore<N>(retVal, aRowStore[j]);
            }
            final int tmpColDim = retVal.getColDim();
            if (tmpColDim < aMinColDim) {
                retVal = new LeftRightStore<N>(retVal, new ZeroStore<N>(retVal.factory(), retVal.getRowDim(), aMinColDim - tmpColDim));
            }
            return retVal;
        }

        static <N extends Number> MatrixStore<N> buildRow(final PhysicalStore.Factory<N, ?> aFactory, final int aMinColDim, final N... aRowStore) {
            MatrixStore<N> retVal = new TransposedStore<N>(aFactory.columns(aRowStore));
            final int tmpColDim = retVal.getColDim();
            if (tmpColDim < aMinColDim) {
                retVal = new LeftRightStore<N>(retVal, new ZeroStore<N>(aFactory, retVal.getRowDim(), aMinColDim - tmpColDim));
            }
            return retVal;
        }

        private MatrixStore<N> myStore;

        public Builder(final MatrixStore<N> aStore) {

            super();

            myStore = aStore;
        }

        @SuppressWarnings("unused")
        private Builder() {

            this(null);

            ProgrammingError.throwForIllegalInvocation();
        }

        public Builder<N> above(final int aRowDim) {
            final ZeroStore<N> tmpUpperStore = new ZeroStore<N>(myStore.factory(), aRowDim, myStore.getColDim());
            myStore = new AboveBelowStore<N>(tmpUpperStore, myStore);
            return this;
        }

        public Builder<N> above(final MatrixStore<N>... anUpperStore) {
            final MatrixStore<N> tmpUpperStore = Builder.buildRow(myStore.getColDim(), anUpperStore);
            myStore = new AboveBelowStore<N>(tmpUpperStore, myStore);
            return this;
        }

        public Builder<N> above(final N... anUpperStore) {
            final MatrixStore<N> tmpUpperStore = Builder.buildRow(myStore.factory(), myStore.getColDim(), anUpperStore);
            myStore = new AboveBelowStore<N>(tmpUpperStore, myStore);
            return this;
        }

        public Builder<N> below(final int aRowDim) {
            final ZeroStore<N> tmpLowerStore = new ZeroStore<N>(myStore.factory(), aRowDim, myStore.getColDim());
            myStore = new AboveBelowStore<N>(myStore, tmpLowerStore);
            return this;
        }

        public Builder<N> below(final MatrixStore<N>... aLowerStore) {
            final MatrixStore<N> tmpLowerStore = Builder.buildRow(myStore.getColDim(), aLowerStore);
            myStore = new AboveBelowStore<N>(myStore, tmpLowerStore);
            return this;
        }

        public Builder<N> below(final N... aLowerStore) {
            final MatrixStore<N> tmpLowerStore = Builder.buildRow(myStore.factory(), myStore.getColDim(), aLowerStore);
            myStore = new AboveBelowStore<N>(myStore, tmpLowerStore);
            return this;
        }

        public Builder<N> bidiagonal(final boolean upper, final boolean assumeOne) {
            if (upper) {
                myStore = new UpperTriangularStore<N>(new LowerHessenbergStore<N>(myStore), assumeOne);
            } else {
                myStore = new LowerTriangularStore<N>(new UpperHessenbergStore<N>(myStore), assumeOne);
            }
            return this;
        }

        public MatrixStore<N> build() {
            return myStore;
        }

        public Builder<N> columns(final int... aCol) {
            myStore = new ColumnsStore<N>(myStore, aCol);
            return this;
        }

        public Builder<N> conjugate() {
            if (myStore instanceof ConjugatedStore) {
                myStore = ((ConjugatedStore<N>) myStore).getOriginal();
            } else {
                myStore = new ConjugatedStore<N>(myStore);
            }
            return this;
        }

        public Builder<N> diagonal(final boolean assumeOne) {
            myStore = new UpperTriangularStore<N>(new LowerTriangularStore<N>(myStore, assumeOne), assumeOne);
            return this;
        }

        public Builder<N> diagonally(final MatrixStore<N>... aDiagonalStore) {

            final Factory<N, ?> tmpFactory = myStore.factory();

            MatrixStore<N> tmpDiagonalStore;
            for (int ij = 0; ij < aDiagonalStore.length; ij++) {

                tmpDiagonalStore = aDiagonalStore[ij];

                final int tmpBaseRowDim = myStore.getRowDim();
                final int tmpBaseColDim = myStore.getColDim();

                final int tmpDiagRowDim = tmpDiagonalStore.getRowDim();
                final int tmpDiagColDim = tmpDiagonalStore.getColDim();

                final MatrixStore<N> tmpRightStore = new ZeroStore<N>(tmpFactory, tmpBaseRowDim, tmpDiagColDim);
                final MatrixStore<N> tmpAboveStore = new LeftRightStore<N>(myStore, tmpRightStore);

                final MatrixStore<N> tmpLeftStore = new ZeroStore<N>(tmpFactory, tmpDiagRowDim, tmpBaseColDim);
                final MatrixStore<N> tmpBelowStore = new LeftRightStore<N>(tmpLeftStore, tmpDiagonalStore);

                myStore = new AboveBelowStore<N>(tmpAboveStore, tmpBelowStore);
            }

            return this;
        }

        public Builder<N> hessenberg(final boolean upper) {
            if (upper) {
                myStore = new UpperHessenbergStore<N>(myStore);
            } else {
                myStore = new LowerHessenbergStore<N>(myStore);
            }
            return this;
        }

        public Builder<N> left(final int aColDim) {
            final MatrixStore<N> tmpLeftStore = new ZeroStore<N>(myStore.factory(), myStore.getRowDim(), aColDim);
            myStore = new LeftRightStore<N>(tmpLeftStore, myStore);
            return this;
        }

        public Builder<N> left(final MatrixStore<N>... aLeftStore) {
            final MatrixStore<N> tmpLeftStore = Builder.buildColumn(myStore.getRowDim(), aLeftStore);
            myStore = new LeftRightStore<N>(tmpLeftStore, myStore);
            return this;
        }

        public Builder<N> left(final N... aLeftStore) {
            final MatrixStore<N> tmpLeftStore = Builder.buildColumn(myStore.factory(), myStore.getRowDim(), aLeftStore);
            myStore = new LeftRightStore<N>(tmpLeftStore, myStore);
            return this;
        }

        public Builder<N> modify(final UnaryFunction<N> aFunc) {
            myStore = new ModificationStore<N>(myStore, aFunc);
            return this;
        }

        public Builder<N> right(final int aColDim) {
            final MatrixStore<N> tmpRightStore = new ZeroStore<N>(myStore.factory(), myStore.getRowDim(), aColDim);
            myStore = new LeftRightStore<N>(myStore, tmpRightStore);
            return this;
        }

        public Builder<N> right(final MatrixStore<N>... aRightStore) {
            final MatrixStore<N> tmpRightStore = Builder.buildColumn(myStore.getRowDim(), aRightStore);
            myStore = new LeftRightStore<N>(myStore, tmpRightStore);
            return this;
        }

        public Builder<N> right(final N... aRightStore) {
            final MatrixStore<N> tmpRightStore = Builder.buildColumn(myStore.factory(), myStore.getRowDim(), aRightStore);
            myStore = new LeftRightStore<N>(myStore, tmpRightStore);
            return this;
        }

        public Builder<N> rows(final int... aRow) {
            myStore = new RowsStore<N>(myStore, aRow);
            return this;
        }

        public Builder<N> superimpose(final int aRow, final int aCol, final MatrixStore<N> aStore) {
            myStore = new SuperimposedStore<N>(myStore, aRow, aCol, aStore);
            return this;
        }

        public Builder<N> superimpose(final int aRow, final int aCol, final N aStore) {
            myStore = new SuperimposedStore<N>(myStore, aRow, aCol, new SingleStore<N>(myStore.factory(), aStore));
            return this;
        }

        public Builder<N> transpose() {
            if (myStore instanceof TransposedStore) {
                myStore = ((TransposedStore<N>) myStore).getOriginal();
            } else {
                myStore = new TransposedStore<N>(myStore);
            }
            return this;
        }

        public Builder<N> triangular(final boolean upper, final boolean assumeOne) {
            if (upper) {
                myStore = new UpperTriangularStore<N>(myStore, assumeOne);
            } else {
                myStore = new LowerTriangularStore<N>(myStore, assumeOne);
            }
            return this;
        }

        public Builder<N> tridiagonal() {
            myStore = new UpperHessenbergStore<N>(new LowerHessenbergStore<N>(myStore));
            return this;
        }

    }

    N aggregateAll(Aggregator aVisitor);

    MatrixStore.Builder<N> builder();

    /**
     * Each call must produce a new instance.
     * 
     * @return A new conjugated {@linkplain PhysicalStore} copy.
     */
    PhysicalStore<N> conjugate();

    /**
     * Each call must produce a new instance.
     * 
     * @return A new {@linkplain PhysicalStore} copy.
     */
    PhysicalStore<N> copy();

    boolean equals(MatrixStore<N> aStore, NumberContext aCntxt);

    PhysicalStore.Factory<N, ?> factory();

    /**
     * @deprecated v33 Use {@link #factory()} instead
     */
    @Deprecated
    PhysicalStore.Factory<N, ?> getFactory();

    int getMinDim();

    /**
     * @see Scalar#isAbsolute()
     */
    boolean isAbsolute(int aRow, int aCol);

    /**
     * The entries below (left of) the first subdiagonal are zero -
     * effectively an upper Hessenberg matrix.
     * 
     * @see #isUpperRightShaded()
     */
    boolean isLowerLeftShaded();

    /**
     * @see Scalar#isPositive()
     */
    boolean isPositive(final int aRow, final int aCol);

    /**
     * @see Scalar#isReal()
     */
    boolean isReal(int aRow, int aCol);

    /**
     * The entries above (right of) the first superdiagonal are zero -
     * effectively a lower Hessenberg matrix.
     * 
     * @see #isLowerLeftShaded()
     */
    boolean isUpperRightShaded();

    /**
     * @see Scalar#isZero()
     */
    boolean isZero(int aRow, int aCol);

    MatrixStore<N> multiplyLeft(MatrixStore<N> aStore);

    MatrixStore<N> multiplyRight(MatrixStore<N> aStore);

    Scalar<N> toScalar(int aRow, int aCol);

    /**
     * Each call must produce a new instance.
     * 
     * @return A new transposed {@linkplain PhysicalStore} copy.
     */
    PhysicalStore<N> transpose();

    void visitAll(AggregatorFunction<N> aVisitor);

    void visitColumn(int aRow, int aCol, AggregatorFunction<N> aVisitor);

    void visitDiagonal(int aRow, int aCol, AggregatorFunction<N> aVisitor);

    void visitRow(int aRow, int aCol, AggregatorFunction<N> aVisitor);

}
