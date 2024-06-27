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
package org.ojalgo.matrix;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Iterator;

import org.ojalgo.access.Access2D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.array.Array1D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.decomposition.*;
import org.ojalgo.matrix.operation.*;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.random.Uniform;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

public abstract class MatrixUtils {

    public static boolean equals(final BasicMatrix aMtrx1, final BasicMatrix aMtrx2, final NumberContext aCntxt) {
        final BasicMatrix tmpSubtract = aMtrx1.subtract(aMtrx2);
        final Scalar<?> tmpFrobeniusNorm = tmpSubtract.getFrobeniusNorm();
        final double tmpModulus = tmpFrobeniusNorm.getModulus();
        final double tmpError = aCntxt.getError();
        final boolean retVal = (aMtrx1.getRowDim() == aMtrx2.getRowDim()) && (aMtrx1.getColDim() == aMtrx2.getColDim()) && (tmpModulus <= tmpError);
        return retVal;
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore, final Bidiagonal<N> aDecomp, final NumberContext aCntxt) {

        final int tmpRowDim = aStore.getRowDim();
        final int tmpColDim = aStore.getColDim();

        final MatrixStore<N> tmpQ1 = aDecomp.getQ1();
        final MatrixStore<N> tmpD = aDecomp.getD();
        final MatrixStore<N> tmpQ2 = aDecomp.getQ2();

        final MatrixStore<N> tmpConjugatedQ1 = tmpQ1.builder().conjugate().build();
        final MatrixStore<N> tmpConjugatedQ2 = tmpQ2.builder().conjugate().build();

        MatrixStore<N> tmpThis;
        MatrixStore<N> tmpThat;

        boolean retVal = (tmpRowDim == tmpQ1.getRowDim()) && (tmpQ2.getRowDim() == tmpColDim);

        // Check that it's possible to reconstruct the original matrix.
        if (retVal) {

            tmpThis = aStore;
            tmpThat = aDecomp.reconstruct();

            retVal &= tmpThis.equals(tmpThat, aCntxt);
        }

        // If Q1 is square, then check if it is orthogonal/unitary.
        if (retVal && (tmpQ1.getRowDim() == tmpQ1.getColDim())) {

            tmpThis = tmpQ1;
            tmpThat = tmpConjugatedQ1.multiplyLeft(tmpQ1).multiplyRight(tmpQ1);

            retVal &= tmpThis.equals(tmpThat, aCntxt);
        }

        // If Q2 is square, then check if it is orthogonal/unitary.
        if (retVal && (tmpQ2.getRowDim() == tmpQ2.getColDim())) {

            tmpThis = tmpQ2;
            tmpThat = tmpConjugatedQ2.multiplyLeft(tmpQ2).multiplyRight(tmpQ2);

            retVal &= tmpThis.equals(tmpThat, aCntxt);
        }

        return retVal;
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore, final Cholesky<N> aDecomp, final NumberContext aCntxt) {

        boolean retVal = false;

        final MatrixStore<N> tmpL = aDecomp.getL();

        retVal = MatrixUtils.equals(tmpL.multiplyRight(tmpL.builder().conjugate().build()), aStore, aCntxt);

        return retVal;
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore, final Eigenvalue<N> aDecomp, final NumberContext aCntxt) {

        final MatrixStore<N> tmpD = aDecomp.getD();
        final MatrixStore<N> tmpV = aDecomp.getV();

        // Check that [A][V] == [V][D] ([A] == [V][D][V]<sup>T</sup> is not always true)
        final MatrixStore<N> tmpStore1 = aStore.multiplyRight(tmpV);
        final MatrixStore<N> tmpStore2 = tmpD.multiplyLeft(tmpV);

        return MatrixUtils.equals(tmpStore1, tmpStore2, aCntxt);
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore, final Hessenberg<N> aDecomp, final NumberContext aCntxt) {

        final MatrixStore<N> tmpH = aDecomp.getH();
        final MatrixStore<N> tmpQ = aDecomp.getQ();

        final MatrixStore<N> tmpStore1 = aStore.multiplyRight(tmpQ);
        final MatrixStore<N> tmpStore2 = tmpH.multiplyLeft(tmpQ);

        return MatrixUtils.equals(tmpStore1, tmpStore2, aCntxt);
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore, final LU<N> aDecomp, final NumberContext aCntxt) {

        final MatrixStore<N> tmpL = aDecomp.getL();
        final MatrixStore<N> tmpU = aDecomp.getU();
        final int[] tmpPivotOrder = aDecomp.getPivotOrder();

        return MatrixUtils.equals(aStore.builder().rows(tmpPivotOrder).build(), tmpL.multiplyRight(tmpU), aCntxt);
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore1, final MatrixStore<N> aStore2, final NumberContext aCntxt) {
        return AccessUtils.equals(aStore1, aStore2, aCntxt);
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore, final QR<N> aDecomp, final NumberContext aCntxt) {

        final MatrixStore<N> tmpQ = aDecomp.getQ();
        final MatrixStore<N> tmpR = aDecomp.getR();

        final MatrixStore<N> tmpStore = tmpQ.multiplyRight(tmpR);

        return MatrixUtils.equals(tmpStore, aStore, aCntxt);
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore, final Schur<N> aDecomp, final NumberContext aCntxt) {

        final MatrixStore<N> tmpU = aDecomp.getU();
        final MatrixStore<N> tmpQ = aDecomp.getQ();

        // Check that [A][Q] == [Q][D] ([A] == [Q][U][Q]<sup>T</sup> is not always true)
        final MatrixStore<N> tmpStore1 = aStore.multiplyRight(tmpQ);
        final MatrixStore<N> tmpStore2 = tmpU.multiplyLeft(tmpQ);

        return MatrixUtils.equals(tmpStore1, tmpStore2, aCntxt);
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore, final SingularValue<N> aDecomp, final NumberContext aCntxt) {

        final int tmpRowDim = aStore.getRowDim();
        final int tmpColDim = aStore.getColDim();

        final MatrixStore<N> tmpQ1 = aDecomp.getQ1();
        final MatrixStore<N> tmpD = aDecomp.getD();
        final MatrixStore<N> tmpQ2 = aDecomp.getQ2();

        MatrixStore<N> tmpThis;
        MatrixStore<N> tmpThat;

        boolean retVal = (tmpRowDim == tmpQ1.getRowDim()) && (tmpQ2.getRowDim() == tmpColDim);

        // Check that [A][Q2] == [Q1][D]
        if (retVal) {

            tmpThis = aStore.multiplyRight(tmpQ2);
            tmpThat = tmpD.multiplyLeft(tmpQ1);

            retVal &= tmpThis.equals(tmpThat, aCntxt);
        }

        // If Q1 is square, then check if it is orthogonal/unitary.
        if (retVal && (tmpQ1.getRowDim() == tmpQ1.getColDim())) {

            tmpThis = tmpQ1.factory().makeEye(tmpRowDim, tmpRowDim);
            tmpThat = tmpQ1.builder().conjugate().build().multiplyRight(tmpQ1);

            retVal &= tmpThis.equals(tmpThat, aCntxt);
        }

        // If Q2 is square, then check if it is orthogonal/unitary.
        if (retVal && (tmpQ2.getRowDim() == tmpQ2.getColDim())) {

            tmpThis = tmpQ2.factory().makeEye(tmpColDim, tmpColDim);
            tmpThat = tmpQ2.builder().conjugate().build().multiplyLeft(tmpQ2);

            retVal &= tmpThis.equals(tmpThat, aCntxt);
        }

        // Check the pseudoinverse.
        if (retVal) {
            retVal &= aStore.equals(aDecomp.getInverse().multiplyRight(aStore).multiplyLeft(aStore), aCntxt);
        }

        // Check that the singular values are sorted in descending order
        if (retVal) {
            final Array1D<Double> tmpSV = aDecomp.getSingularValues();
            for (int i = 1; retVal && (i < tmpSV.size()); i++) {
                retVal &= tmpSV.doubleValue(i - 1) >= tmpSV.doubleValue(i);
            }
            if (retVal && aDecomp.isOrdered()) {
                for (int ij = 1; retVal && (ij < tmpD.getMinDim()); ij++) {
                    retVal &= tmpD.doubleValue(ij - 1, ij - 1) >= tmpD.doubleValue(ij, ij);
                }
            }
        }

        return retVal;
    }

    public static <N extends Number> boolean equals(final MatrixStore<N> aStore, final Tridiagonal<N> aDecomp, final NumberContext aCntxt) {

        // Check that [A] == [Q][D][Q]<sup>T</sup>
        return MatrixUtils.equals(aStore, MatrixUtils.reconstruct(aDecomp), aCntxt);

        // Check that Q is orthogonal/unitary...
    }

    public static int hashCode(final BasicMatrix aMtrx) {
        int retVal = aMtrx.size() * PrimitiveMath.getPrimeNumber(30);
        final int tmpLimit = Math.min(aMtrx.getRowDim(), aMtrx.getColDim());
        for (int ij = 0; ij < tmpLimit; ij++) {
            retVal *= TypeUtils.EQUALS_NUMBER_CONTEXT.round(aMtrx.toScalar(ij, ij).getModulus());
        }
        return retVal;
    }

    public static <N extends Number> int hashCode(final MatrixStore<N> aStore) {
        int retVal = aStore.size() * PrimitiveMath.getPrimeNumber(40);
        final int tmpLimit = Math.min(aStore.getRowDim(), aStore.getColDim());
        for (int ij = 0; ij < tmpLimit; ij++) {
            retVal *= TypeUtils.EQUALS_NUMBER_CONTEXT.round(aStore.toScalar(ij, ij).getModulus());
        }
        return retVal;
    }

    public static <N extends Number> boolean isHermitian(final MatrixStore<N> aMtrx) {
        return aMtrx.equals(aMtrx.builder().conjugate().build(), TypeUtils.EQUALS_NUMBER_CONTEXT);
    }

    public static final boolean isLowerLeftShaded(final Access2D<?> anAccess) {
        return anAccess instanceof MatrixStore<?> ? ((MatrixStore<?>) anAccess).isLowerLeftShaded() : false;
    }

    public static <N extends Number> boolean isNormal(final MatrixStore<N> aMtrx) {

        final MatrixStore<N> tmpConjugate = aMtrx.builder().conjugate().build();

        return aMtrx.multiplyLeft(tmpConjugate).equals(aMtrx.multiplyRight(tmpConjugate));
    }

    public static boolean isSymmetric(final Access2D<?> aMtrx) {

        final int tmpDim = aMtrx.getRowDim();

        boolean retVal = true;

        for (int j = 0; retVal && (j < tmpDim); j++) {
            for (int i = j + 1; retVal && (i < tmpDim); i++) {
                retVal &= TypeUtils.isZero(aMtrx.doubleValue(i, j) - aMtrx.doubleValue(j, i));
            }
        }
        return retVal;
    }

    public static final boolean isUpperRightShaded(final Access2D<?> anAccess) {
        return anAccess instanceof MatrixStore<?> ? ((MatrixStore<?>) anAccess).isUpperRightShaded() : false;
    }

    public static int[] makeDecreasingRange(final int aFirst, final int aCount) {
        final int[] retVal = new int[aCount];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = aFirst - i;
        }
        return retVal;
    }

    public static int[] makeIncreasingRange(final int aFirst, final int aCount) {
        final int[] retVal = new int[aCount];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = aFirst + i;
        }
        return retVal;
    }

    public static PhysicalStore<ComplexNumber> makeRandomComplexStore(final int aRowDim, final int aColDim) {

        final PhysicalStore<ComplexNumber> retVal = ComplexDenseStore.FACTORY.makeZero(aRowDim, aColDim);

        final Uniform tmpArgGen = new Uniform(PrimitiveMath.ZERO, PrimitiveMath.TWO_PI);

        for (int j = 0; j < aColDim; j++) {
            for (int i = 0; i < aRowDim; i++) {
                retVal.set(i, j, ComplexNumber.makePolar(PrimitiveMath.E, tmpArgGen.doubleValue()).add(PrimitiveMath.PI));
            }
        }

        return retVal;
    }

    public static int[] makeRange(final int anInd) {
        return new int[] { anInd };
    }

    public static int max(final int... values) {
        int retVal = Integer.MIN_VALUE;
        for (int i = values.length; i-- != 0;) {
            retVal = values[i] > retVal ? values[i] : retVal;
        }
        return retVal;
    }

    public static int min(final int... values) {
        int retVal = Integer.MAX_VALUE;
        for (int i = values.length; i-- != 0;) {
            retVal = values[i] < retVal ? values[i] : retVal;
        }
        return retVal;
    }

    /**
     * @deprecated v33 Use {@link BasicLogger#printToStream(PrintStream,Access2D<?>,NumberContext)} instead
     */
    @Deprecated
    public static void printToStream(final PrintStream aPrintStream, final Access2D<?> aStore, final NumberContext aCntxt) {
        BasicLogger.printMtrx(aPrintStream, aStore, aCntxt);
    }

    /**
     * @deprecated v33 Use {@link BasicLogger#printMtrx(PrintStream,BasicMatrix,NumberContext,boolean)} instead
     */
    @Deprecated
    public static void printToStream(final PrintStream aPrintStream, final BasicMatrix aMtrx, final NumberContext aCntxt, final boolean plainString) {
        BasicLogger.printMtrx(aPrintStream, aMtrx, aCntxt, plainString);
    }

    public static <N extends Number> MatrixStore<N> reconstruct(final Bidiagonal<N> aDecomp) {
        return aDecomp.getD().multiplyLeft(aDecomp.getQ1()).multiplyRight(aDecomp.getQ2().conjugate());
    }

    public static <N extends Number> MatrixStore<N> reconstruct(final Cholesky<N> aDecomp) {
        final MatrixStore<N> tmpL = aDecomp.getL();
        return tmpL.multiplyRight(tmpL.conjugate());
    }

    public static <N extends Number> MatrixStore<N> reconstruct(final Eigenvalue<N> aDecomp) {
        final MatrixStore<N> tmpV = aDecomp.getV();
        return aDecomp.getD().multiplyLeft(tmpV).multiplyRight(tmpV.conjugate());
    }

    public static <N extends Number> MatrixStore<N> reconstruct(final Hessenberg<N> aDecomp) {
        final MatrixStore<N> tmpQ = aDecomp.getQ();
        return aDecomp.getH().multiplyLeft(tmpQ).multiplyRight(tmpQ.transpose());
    }

    public static <N extends Number> MatrixStore<N> reconstruct(final LU<N> aDecomp) {
        return aDecomp.getL().multiplyRight(aDecomp.getU());
    }

    public static <N extends Number> MatrixStore<N> reconstruct(final QR<N> aDecomp) {
        return aDecomp.getQ().multiplyRight(aDecomp.getR());
    }

    public static <N extends Number> MatrixStore<N> reconstruct(final Schur<N> aDecomp) {
        final MatrixStore<N> tmpQ = aDecomp.getQ();
        return aDecomp.getU().multiplyLeft(tmpQ).multiplyRight(tmpQ.builder().transpose().build());
    }

    public static <N extends Number> MatrixStore<N> reconstruct(final SingularValue<N> aDecomp) {
        return aDecomp.getQ1().multiplyRight(aDecomp.getD()).multiplyRight(aDecomp.getQ2().conjugate());
    }

    public static <N extends Number> MatrixStore<N> reconstruct(final Tridiagonal<N> aDecomp) {
        final MatrixStore<N> tmpQ = aDecomp.getQ();
        return aDecomp.getD().multiplyLeft(tmpQ).multiplyRight(tmpQ.conjugate());
    }

    public static void setAllOperationThresholds(final int aValue) {
        AggregateAll.THRESHOLD = aValue;
        ApplyTransformations.THRESHOLD = aValue;
        CAXPY.THRESHOLD = aValue;
        FillMatchingBoth.THRESHOLD = aValue;
        FillMatchingLeft.THRESHOLD = aValue;
        FillMatchingRight.THRESHOLD = aValue;
        FillMatchingSingle.THRESHOLD = aValue;
        GenerateApplyAndCopyHouseholderColumn.THRESHOLD = aValue;
        GenerateApplyAndCopyHouseholderRow.THRESHOLD = aValue;
        HouseholderLeft.THRESHOLD = aValue;
        HouseholderRight.THRESHOLD = aValue;
        HouseholderHermitian.THRESHOLD = aValue;
        MAXPY.THRESHOLD = aValue;
        ModifyAll.THRESHOLD = aValue;
        MultiplyBoth.THRESHOLD = aValue;
        MultiplyLeft.THRESHOLD = aValue;
        MultiplyRight.THRESHOLD = aValue;
        MultiplyHermitianAndVector.THRESHOLD = aValue;
        RAXPY.THRESHOLD = aValue;
        RotateLeft.THRESHOLD = aValue;
        RotateRight.THRESHOLD = aValue;
        SubstituteBackwards.THRESHOLD = aValue;
        SubstituteForwards.THRESHOLD = aValue;
        HermitianRank2Update.THRESHOLD = aValue;
    }

    public static String toString(final BasicMatrix aMtrx) {

        final StringBuilder retVal = new StringBuilder(aMtrx.getClass().toString());

        if (aMtrx.isEmpty()) {

            retVal.append(" is empty!");

        } else {

            // First element
            retVal.append("\n{{" + aMtrx.toString(0, 0));

            // Rest of the first row
            for (int j = 1; j < aMtrx.getColDim(); j++) {
                retVal.append(",\t" + aMtrx.toString(0, j));
            }

            // For each of the remaining rows
            for (int i = 1; i < aMtrx.getRowDim(); i++) {

                // First column
                retVal.append("},\n{" + aMtrx.toString(i, 0));

                // Remaining coulmns
                for (int j = 1; j < aMtrx.getColDim(); j++) {
                    retVal.append(",\t" + aMtrx.toString(i, j));
                }
            }

            // Finish
            retVal.append("}}\n");
        }

        return retVal.toString();
    }

    public static Access2D<BigDecimal> wrapBigAccess2D(final BasicMatrix aMtrx) {
        return new Access2D<BigDecimal>() {

            public double doubleValue(final int anInd) {
                return aMtrx.doubleValue(anInd);
            }

            public double doubleValue(final int aRow, final int aCol) {
                return aMtrx.doubleValue(aRow, aCol);
            }

            public BigDecimal get(final int anInd) {
                return this.get(AccessUtils.row(anInd, aMtrx.getRowDim()), AccessUtils.column(anInd, aMtrx.getRowDim()));

            }

            public BigDecimal get(final int aRow, final int aCol) {
                return aMtrx.toBigDecimal(aRow, aCol);
            }

            public int getColDim() {
                return aMtrx.getColDim();
            }

            public int getRowDim() {
                return aMtrx.getRowDim();
            }

            public Iterator<BigDecimal> iterator() {
                return new Iterator1D<BigDecimal>(this);
            }

            public int size() {
                return aMtrx.size();
            }

        };
    }

    public static Access2D<ComplexNumber> wrapComplexAccess2D(final BasicMatrix aMtrx) {
        return new Access2D<ComplexNumber>() {

            public double doubleValue(final int anInd) {
                return aMtrx.doubleValue(anInd);
            }

            public double doubleValue(final int aRow, final int aCol) {
                return aMtrx.doubleValue(aRow, aCol);
            }

            public ComplexNumber get(final int anInd) {
                return this.get(AccessUtils.row(anInd, aMtrx.getRowDim()), AccessUtils.column(anInd, aMtrx.getRowDim()));
            }

            public ComplexNumber get(final int aRow, final int aCol) {
                return aMtrx.toComplexNumber(aRow, aCol);
            }

            public int getColDim() {
                return aMtrx.getColDim();
            }

            public int getRowDim() {
                return aMtrx.getRowDim();
            }

            public Iterator<ComplexNumber> iterator() {
                return new Iterator1D<ComplexNumber>(this);
            }

            public int size() {
                return aMtrx.size();
            }

        };
    }

    public static Access2D<Double> wrapPrimitiveAccess2D(final BasicMatrix aMtrx) {
        return new Access2D<Double>() {

            public double doubleValue(final int anInd) {
                return aMtrx.doubleValue(anInd);
            }

            public double doubleValue(final int aRow, final int aCol) {
                return aMtrx.doubleValue(aRow, aCol);
            }

            public Double get(final int anInd) {
                return this.get(AccessUtils.row(anInd, aMtrx.getRowDim()), AccessUtils.column(anInd, aMtrx.getRowDim()));

            }

            public Double get(final int aRow, final int aCol) {
                return aMtrx.doubleValue(aRow, aCol);
            }

            public int getColDim() {
                return aMtrx.getColDim();
            }

            public int getRowDim() {
                return aMtrx.getRowDim();
            }

            public Iterator<Double> iterator() {
                return new Iterator1D<Double>(this);
            }

            public int size() {
                return aMtrx.size();
            }

        };
    }

    private MatrixUtils() {
        super();
    }
}
