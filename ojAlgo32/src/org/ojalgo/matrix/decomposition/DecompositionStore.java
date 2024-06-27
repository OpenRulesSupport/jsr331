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

import java.util.Iterator;

import org.ojalgo.access.Access2D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.scalar.ComplexNumber;

/**
 * <p>
 * Only classes that will act as a delegate to a {@linkplain MatrixDecomposition}
 * implementation from this package should implement this interface. The
 * interface specifications are entirely dictated by the classes in this
 * package.
 * </p><p>
 * Do not use it for anything else!
 * </p>
 *
 * @author apete
 */
public interface DecompositionStore<N extends Number> extends PhysicalStore<N> {

    public static final class HouseholderReference<N extends Number> implements Householder<N> {

        public int col = 0;
        public int row = 0;
        private transient Householder.Big myBigWorker = null;
        private final boolean myColumn;
        private transient Householder.Complex myComplexWorker = null;
        private transient Householder.Primitive myPrimitiveWorker = null;
        private final DecompositionStore<N> myStore;

        @SuppressWarnings("unused")
        private HouseholderReference() {
            this(null, true);
        }

        HouseholderReference(final DecompositionStore<N> aStore, final boolean aColumn) {

            super();

            myStore = aStore;
            myColumn = aColumn;
        }

        public double doubleValue(final int anInd) {
            if (myColumn) {
                if (anInd > row) {
                    return myStore.doubleValue(anInd, col);
                } else if (anInd == row) {
                    return PrimitiveMath.ONE;
                } else {
                    return PrimitiveMath.ZERO;
                }
            } else {
                if (anInd > col) {
                    return myStore.doubleValue(row, anInd);
                } else if (anInd == col) {
                    return PrimitiveMath.ONE;
                } else {
                    return PrimitiveMath.ZERO;
                }
            }
        }

        public int first() {
            return myColumn ? row : col;
        }

        public N get(final int anInd) {
            if (myColumn) {
                if (anInd > row) {
                    return myStore.get(anInd, col);
                } else if (anInd == row) {
                    return myStore.factory().getStaticOne().getNumber();
                } else {
                    return myStore.factory().getStaticZero().getNumber();
                }
            } else {
                if (anInd > col) {
                    return myStore.get(row, anInd);
                } else if (anInd == col) {
                    return myStore.factory().getStaticOne().getNumber();
                } else {
                    return myStore.factory().getStaticZero().getNumber();
                }
            }
        }

        public final Householder.Big getBigWorker() {

            if (myBigWorker == null) {
                if (myColumn) {
                    myBigWorker = new Householder.Big(myStore.getRowDim());
                } else {
                    myBigWorker = new Householder.Big(myStore.getColDim());
                }
            }

            return myBigWorker;
        }

        public final Householder.Complex getComplexWorker() {

            if (myComplexWorker == null) {
                if (myColumn) {
                    myComplexWorker = new Householder.Complex(myStore.getRowDim());
                } else {
                    myComplexWorker = new Householder.Complex(myStore.getColDim());
                }
            }

            return myComplexWorker;
        }

        public final Householder.Primitive getPrimitiveWorker() {

            if (myPrimitiveWorker == null) {
                if (myColumn) {
                    myPrimitiveWorker = new Householder.Primitive(myStore.getRowDim());
                } else {
                    myPrimitiveWorker = new Householder.Primitive(myStore.getColDim());
                }
            }

            return myPrimitiveWorker;
        }

        public final boolean isZero() {
            if (myColumn) {
                return myStore.asArray2D().isColumnZeros(row + 1, col);
            } else {
                return myStore.asArray2D().isRowZeros(row, col + 1);
            }
        }

        public final Iterator<N> iterator() {
            return new Iterator1D<N>(this);
        }

        public final int size() {
            if (myColumn) {
                return myStore.getRowDim();
            } else {
                return myStore.getColDim();
            }
        }

        @Override
        public String toString() {

            final StringBuilder retVal = new StringBuilder("{ ");

            final int tmpLastIndex = this.size() - 1;
            for (int i = 0; i < tmpLastIndex; i++) {
                retVal.append(this.get(i));
                retVal.append(", ");
            }
            retVal.append(this.get(tmpLastIndex));

            retVal.append(" }");

            return retVal.toString();
        }

    }

    /**
     * LU and Cholesky transformations
     */
    void applyTransformations(final int iterationPoint, final SimpleArray<N> multipliers, boolean hermitian);

    Array2D<N> asArray2D();

    Array1D<ComplexNumber> computeInPlaceSchur(PhysicalStore<N> aTransformationCollector, boolean eigenvalue);

    void divideAndCopyColumn(int aRow, int aCol, SimpleArray<N> aDestination);

    boolean generateApplyAndCopyHouseholderColumn(final int aRow, final int aCol, final Householder<N> aDestination);

    boolean generateApplyAndCopyHouseholderRow(final int aRow, final int aCol, final Householder<N> aDestination);

    int getIndexOfLargestInColumn(final int aRow, final int aCol);

    void negateColumn(int aCol);

    void rotateRight(int aLow, int aHigh, double aCos, double aSin);

    void setToIdentity(int aCol);

    /**
     * Will solve the equation system [A][X]=[B] where:
     * <ul>
     * <li>[aBody][this]=[this] is [A][X]=[B] ("this" is the right hand
     * side, and it will be overwritten with the solution).</li>
     * <li>[A] is upper/right triangular</li>
     * </ul>
     * 
     * @param aBody The equation system body parameters [A]
     * @param conjugated true if the upper/right part of aBody is
     * actually stored in the lower/left part of the matrix, and the elements conjugated.
     */
    void substituteBackwards(Access2D<N> aBody, boolean conjugated);

    /**
     * Will solve the equation system [A][X]=[B] where:
     * <ul>
     * <li>[aBody][this]=[this] is [A][X]=[B] ("this" is the right hand
     * side, and it will be overwritten with the solution).</li>
     * <li>[A] is lower/left triangular</li>
     * </ul>
     * 
     * @param aBody The equation system body parameters [A]
     * @param onesOnDiagonal true if aBody as ones on the diagonal
     */
    void substituteForwards(Access2D<N> aBody, boolean onesOnDiagonal, boolean zerosAboveDiagonal);

    void transformSymmetric(Householder<N> aTransf);

    void tred2(SimpleArray<N> mainDiagonal, SimpleArray<N> offDiagonal, boolean yesvecs);

}
