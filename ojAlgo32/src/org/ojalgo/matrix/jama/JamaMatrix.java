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

import static org.ojalgo.constant.PrimitiveMath.*;
import static org.ojalgo.function.implementation.PrimitiveFunction.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.array.ArrayUtils;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.PreconfiguredSecond;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.MatrixBuilder;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * This class adapts JAMA's Matrix to ojAlgo's {@linkplain BasicMatrix} and
 * {@linkplain PhysicalStore} interfaces.
 *
 * @author apete
 */
public final class JamaMatrix extends Object implements BasicMatrix, PhysicalStore<Double>, Serializable {

    public static final JamaFactory FACTORY = new JamaFactory();

    public static MatrixBuilder<Double> getBuilder(final int aLength) {
        return FACTORY.getBuilder(aLength);
    }

    public static MatrixBuilder<Double> getBuilder(final int aRowDim, final int aColDim) {
        return FACTORY.getBuilder(aRowDim, aColDim);
    }

    private static Matrix convert(final Access2D<Double> aStore) {

        Matrix retVal = null;

        if (aStore instanceof JamaMatrix) {
            retVal = ((JamaMatrix) aStore).getDelegate();
        } else {
            retVal = new Matrix(ArrayUtils.toRawCopyOf(aStore), aStore.getRowDim(), aStore.getColDim());
        }

        return retVal;
    }

    private static Matrix convert(final BasicMatrix aMtrx) {

        Matrix retVal = null;

        if (aMtrx instanceof JamaMatrix) {
            retVal = ((JamaMatrix) aMtrx).getDelegate();
        } else {
            retVal = new Matrix(ArrayUtils.toRawCopyOf(aMtrx.toPrimitiveStore()), aMtrx.getRowDim(), aMtrx.getColDim());
        }

        return retVal;
    }

    static Rotation.Primitive cast(final Rotation<Double> aTransf) {
        if (aTransf instanceof Rotation.Primitive) {
            return (Rotation.Primitive) aTransf;
        } else {
            return new Rotation.Primitive(aTransf);
        }
    }

    private final Matrix myDelegate;

    public JamaMatrix(final BasicMatrix aMtrx) {

        super();

        myDelegate = JamaMatrix.convert(aMtrx);
    }

    public JamaMatrix(final MatrixStore<Double> aStore) {

        super();

        myDelegate = JamaMatrix.convert(aStore);
    }

    @SuppressWarnings("unused")
    private JamaMatrix() {

        super();

        myDelegate = null;

        ProgrammingError.throwForIllegalInvocation();
    }

    JamaMatrix(final double[][] aRaw) {

        super();

        myDelegate = new Matrix(aRaw, aRaw.length, aRaw[0].length);
    }

    JamaMatrix(final Matrix aDelegate) {

        super();

        myDelegate = aDelegate;
    }

    public JamaMatrix add(final BasicMatrix aMtrx) {
        return new JamaMatrix(myDelegate.plus(JamaMatrix.convert(aMtrx)));
    }

    public JamaMatrix add(final int aRow, final int aCol, final BasicMatrix aMtrx) {

        final double[][] tmpArrayCopy = myDelegate.getArrayCopy();

        double[] tmpLocalRowRef;
        for (int i = 0; i < aMtrx.getRowDim(); i++) {
            tmpLocalRowRef = tmpArrayCopy[aRow + i];
            for (int j = 0; j < aMtrx.getColDim(); j++) {
                tmpLocalRowRef[aCol + j] = aMtrx.doubleValue(i, j);
            }
        }

        return new JamaMatrix(new Matrix(tmpArrayCopy));
    }

    public JamaMatrix add(final int aRow, final int aCol, final Number aNmbr) {

        final double[][] tmpArrayCopy = myDelegate.getArrayCopy();
        tmpArrayCopy[aRow][aCol] += aNmbr.doubleValue();

        return new JamaMatrix(new Matrix(tmpArrayCopy));
    }

    public JamaMatrix add(final Number aNmbr) {

        final double[][] retVal = myDelegate.getArrayCopy();

        ArrayUtils.modifyAll(retVal, new PreconfiguredSecond<Double>(ADD, aNmbr.doubleValue()));

        return new JamaMatrix(retVal);
    }

    public Double aggregateAll(final Aggregator aVisitor) {

        final AggregatorFunction<Double> tmpVisitor = aVisitor.getPrimitiveFunction();

        this.visitAll(tmpVisitor);

        return tmpVisitor.doubleValue();
    }

    public List<Double> asList() {

        final int tmpColDim = JamaMatrix.this.getColDim();

        return new AbstractList<Double>() {

            @Override
            public Double get(final int someIndex) {

                return JamaMatrix.this.getDelegate().get(someIndex / tmpColDim, someIndex % tmpColDim);
            }

            @Override
            public Double set(final int someIndex, final Double aValue) {
                final Double retVal = this.get(someIndex);
                JamaMatrix.this.getDelegate().set(someIndex / tmpColDim, someIndex % tmpColDim, aValue);
                return retVal;
            }

            @Override
            public int size() {
                return JamaMatrix.this.size();
            }
        };
    }

    public final MatrixStore.Builder<Double> builder() {
        return new MatrixStore.Builder<Double>(this);
    }

    public void caxpy(final Double aSclrA, final int aColX, final int aColY, final int aFirstRow) {

        final double tmpValA = aSclrA.doubleValue();
        final double[][] tmpArray = myDelegate.getArray();

        final int tmpRowDim = myDelegate.getRowDimension();

        for (int i = aFirstRow; i < tmpRowDim; i++) {
            tmpArray[i][aColY] += tmpValA * tmpArray[i][aColX];
        }
    }

    public JamaMatrix conjugate() {
        return this.transpose();
    }

    public JamaMatrix copy() {
        return new JamaMatrix(myDelegate.getArrayCopy());
    }

    public MatrixBuilder<Double> copyToBuilder() {

        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = this.getColDim();
        final MatrixBuilder<Double> retVal = FACTORY.getBuilder(tmpRowDim, tmpColDim);

        for (int i = 0; i < tmpRowDim; i++) {
            for (int j = 0; j < tmpColDim; j++) {
                retVal.set(i, j, this.doubleValue(i, j));
            }
        }

        return retVal;
    }

    public JamaMatrix divide(final Number aNmbr) {

        final double[][] retVal = myDelegate.getArrayCopy();

        ArrayUtils.modifyAll(retVal, new PreconfiguredSecond<Double>(DIVIDE, aNmbr.doubleValue()));

        return new JamaMatrix(retVal);
    }

    public JamaMatrix divideElements(final BasicMatrix aMtrx) {
        return new JamaMatrix(myDelegate.arrayRightDivide(JamaMatrix.convert(aMtrx)));
    }

    public double doubleValue(final int anInd) {
        return myDelegate.get(AccessUtils.row(anInd, myDelegate.getRowDimension()), AccessUtils.column(anInd, myDelegate.getRowDimension()));
    }

    public double doubleValue(final int aRow, final int aCol) {
        return myDelegate.get(aRow, aCol);
    }

    public JamaMatrix enforce(final NumberContext aContext) {

        final double[][] retVal = myDelegate.getArrayCopy();

        ArrayUtils.modifyAll(retVal, aContext.getPrimitiveEnforceFunction());

        return new JamaMatrix(retVal);
    }

    public final boolean equals(final BasicMatrix aMtrx, final NumberContext aCntxt) {
        return MatrixUtils.equals(this, aMtrx, aCntxt);
    }

    public boolean equals(final MatrixStore<Double> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(this, aStore, aCntxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean equals(final Object anObject) {
        if (anObject instanceof MatrixStore) {
            return this.equals((MatrixStore<Double>) anObject, TypeUtils.EQUALS_NUMBER_CONTEXT);
        } else if (anObject instanceof BasicMatrix) {
            return this.equals((BasicMatrix) anObject, TypeUtils.EQUALS_NUMBER_CONTEXT);
        } else {
            return super.equals(anObject);
        }
    }

    public void exchangeColumns(final int aColA, final int aColB) {
        ArrayUtils.exchangeColumns(myDelegate.getArray(), aColA, aColB);
    }

    public void exchangeRows(final int aRowA, final int aRowB) {
        ArrayUtils.exchangeRows(myDelegate.getArray(), aRowA, aRowB);
    }

    public PhysicalStore.Factory<Double, JamaMatrix> factory() {
        return FACTORY;
    }

    public void fillAll(final Double aNmbr) {
        ArrayUtils.fillAll(myDelegate.getArray(), aNmbr);
    }

    public void fillByMultiplying(final MatrixStore<Double> aLeftArg, final MatrixStore<Double> aRightArg) {

        final Matrix tmpLeft = JamaMatrix.convert(aLeftArg);
        final Matrix tmpRight = JamaMatrix.convert(aRightArg);

        myDelegate.setMatrix(0, this.getRowDim() - 1, 0, this.getColDim() - 1, tmpLeft.times(tmpRight));
    }

    public void fillColumn(final int aRow, final int aCol, final Double aNmbr) {
        ArrayUtils.fillColumn(myDelegate.getArray(), aRow, aCol, aNmbr);
    }

    public void fillDiagonal(final int aRow, final int aCol, final Double aNmbr) {
        ArrayUtils.fillDiagonal(myDelegate.getArray(), aRow, aCol, aNmbr);
    }

    public void fillMatching(final Access2D<? extends Number> aSource2D) {

        final double[][] tmpDelegateArray = myDelegate.getArray();

        for (int i = 0; i < aSource2D.getRowDim(); i++) {
            for (int j = 0; j < aSource2D.getColDim(); j++) {
                tmpDelegateArray[i][j] = aSource2D.doubleValue(i, j);
            }
        }
    }

    public void fillMatching(final Double aLeftArg, final BinaryFunction<Double> aFunc, final MatrixStore<Double> aRightArg) {
        ArrayUtils.fillMatching(myDelegate.getArray(), aLeftArg, aFunc, JamaMatrix.convert(aRightArg).getArray());
    }

    public void fillMatching(final MatrixStore<Double> aLeftArg, final BinaryFunction<Double> aFunc, final Double aRightArg) {
        ArrayUtils.fillMatching(myDelegate.getArray(), JamaMatrix.convert(aLeftArg).getArray(), aFunc, aRightArg);
    }

    public void fillMatching(final MatrixStore<Double> aLeftArg, final BinaryFunction<Double> aFunc, final MatrixStore<Double> aRightArg) {
        if (aLeftArg == this) {
            if (aFunc == ADD) {
                myDelegate.plusEquals(JamaMatrix.convert(aRightArg));
            } else if (aFunc == DIVIDE) {
                myDelegate.arrayRightDivideEquals(JamaMatrix.convert(aRightArg));
            } else if (aFunc == MULTIPLY) {
                myDelegate.arrayTimesEquals(JamaMatrix.convert(aRightArg));
            } else if (aFunc == SUBTRACT) {
                myDelegate.minusEquals(JamaMatrix.convert(aRightArg));
            } else {
                ArrayUtils.fillMatching(myDelegate.getArray(), myDelegate.getArray(), aFunc, JamaMatrix.convert(aRightArg).getArray());
            }
        } else if (aRightArg == this) {
            if (aFunc == ADD) {
                myDelegate.plusEquals(JamaMatrix.convert(aLeftArg));
            } else if (aFunc == DIVIDE) {
                myDelegate.arrayLeftDivideEquals(JamaMatrix.convert(aLeftArg));
            } else if (aFunc == MULTIPLY) {
                myDelegate.arrayTimesEquals(JamaMatrix.convert(aLeftArg));
            } else if (aFunc == SUBTRACT) {
                ArrayUtils.fillMatching(myDelegate.getArray(), JamaMatrix.convert(aLeftArg).getArray(), aFunc, myDelegate.getArray());
            } else {
                ArrayUtils.fillMatching(myDelegate.getArray(), JamaMatrix.convert(aLeftArg).getArray(), aFunc, myDelegate.getArray());
            }
        } else {
            ArrayUtils.fillMatching(myDelegate.getArray(), JamaMatrix.convert(aLeftArg).getArray(), aFunc, JamaMatrix.convert(aRightArg).getArray());
        }
    }

    public void fillRow(final int aRow, final int aCol, final Double aNmbr) {
        ArrayUtils.fillRow(myDelegate.getArray(), aRow, aCol, aNmbr);
    }

    public void flushCache() {
        ;
    }

    public Double get(final int anInd) {
        return myDelegate.get(AccessUtils.row(anInd, myDelegate.getRowDimension()), AccessUtils.column(anInd, myDelegate.getRowDimension()));
    }

    public Double get(final int aRow, final int aCol) {
        return myDelegate.get(aRow, aCol);
    }

    public int getColDim() {
        return myDelegate.getColumnDimension();
    }

    public PrimitiveScalar getCondition() {
        return new PrimitiveScalar(this.getSingularValueDecomposition().getCondition());
    }

    public PrimitiveScalar getDeterminant() {
        return new PrimitiveScalar(myDelegate.det());
    }

    public List<ComplexNumber> getEigenvalues() {
        return this.getEigenvalueDecomposition().getEigenvalues();
    }

    /**
     * @deprecated v33 Use {@link #factory()} instead
     */
    @Deprecated
    public PhysicalStore.Factory<Double, JamaMatrix> getFactory() {
        return this.factory();
    }

    public PrimitiveScalar getFrobeniusNorm() {
        return new PrimitiveScalar(myDelegate.normF());
    }

    /**
     * @see org.ojalgo.matrix.store.PhysicalStore#getIndexOfLargestInColumn(int, int)
     */
    public int getIndexOfLargestInColumn(final int aRow, final int aCol) {
        return -1;
    }

    /**
     * @see org.ojalgo.matrix.store.PhysicalStore#getIndexOfLargestInRow(int, int)
     */
    public int getIndexOfLargestInRow(final int aRow, final int aCol) {
        return -1;
    }

    public PrimitiveScalar getInfinityNorm() {
        return new PrimitiveScalar(myDelegate.normInf());
    }

    public PrimitiveScalar getKyFanNorm(final int k) {
        return new PrimitiveScalar(this.getSingularValueDecomposition().getKyFanNorm(k));
    }

    public int getMinDim() {
        return Math.min(myDelegate.getRowDimension(), myDelegate.getColumnDimension());
    }

    public Scalar<?> getOneNorm() {
        return new PrimitiveScalar(myDelegate.norm1());
    }

    public PrimitiveScalar getOperatorNorm() {
        return new PrimitiveScalar(this.getSingularValueDecomposition().getOperatorNorm());
    }

    public int getRank() {
        return this.getSingularValueDecomposition().getRank();
    }

    public int getRowDim() {
        return myDelegate.getRowDimension();
    }

    public List<Double> getSingularValues() {
        return this.getSingularValueDecomposition().getSingularValues();
    }

    public PrimitiveScalar getTrace() {
        return new PrimitiveScalar(myDelegate.trace());
    }

    public PrimitiveScalar getTraceNorm() {
        return new PrimitiveScalar(this.getSingularValueDecomposition().getTraceNorm());

    }

    public PrimitiveScalar getVectorNorm(final int aDegree) {

        if (aDegree == 0) {

            final AggregatorFunction<Double> tmpFunc = PrimitiveAggregator.getCollection().cardinality();

            ArrayUtils.visitAll(myDelegate.getArray(), tmpFunc);

            return (PrimitiveScalar) tmpFunc.toScalar();

        } else if (aDegree == 1) {

            final AggregatorFunction<Double> tmpFunc = PrimitiveAggregator.getCollection().norm1();

            ArrayUtils.visitAll(myDelegate.getArray(), tmpFunc);

            return (PrimitiveScalar) tmpFunc.toScalar();

        } else if (aDegree == 2) {

            return this.getFrobeniusNorm();

        } else {

            final AggregatorFunction<Double> tmpFunc = PrimitiveAggregator.getCollection().largest();

            this.visitAll(tmpFunc);

            return (PrimitiveScalar) tmpFunc.toScalar();
        }
    }

    @Override
    public final int hashCode() {
        return MatrixUtils.hashCode((BasicMatrix) this);
    }

    public JamaMatrix invert() {
        return new JamaMatrix(myDelegate.inverse());
    }

    public boolean isAbsolute(final int aRow, final int aCol) {
        return myDelegate.get(aRow, aCol) >= ZERO;
    }

    public boolean isEmpty() {
        return ((this.getRowDim() <= 0) || (this.getColDim() <= 0));
    }

    public boolean isFat() {
        return (!this.isEmpty() && (this.getRowDim() < this.getColDim()));
    }

    public boolean isFullRank() {
        return this.getRank() == this.getMinDim();
    }

    public boolean isHermitian() {
        return this.isSymmetric();
    }

    public boolean isLowerLeftShaded() {
        return false;
    }

    public boolean isPositive(final int aRow, final int aCol) {
        return myDelegate.get(aRow, aCol) > ZERO;
    }

    public boolean isReal(final int aRow, final int aCol) {
        return true;
    }

    public boolean isScalar() {
        return (myDelegate.getRowDimension() == 1) && (myDelegate.getColumnDimension() == 1);
    }

    public boolean isSquare() {
        return (!this.isEmpty() && (this.getRowDim() == this.getColDim()));
    }

    public boolean isSymmetric() {
        return this.isSquare() && this.equals((BasicMatrix) this.transpose(), TypeUtils.EQUALS_NUMBER_CONTEXT);
    }

    public boolean isTall() {
        return (!this.isEmpty() && (this.getRowDim() > this.getColDim()));
    }

    public boolean isUpperRightShaded() {
        return false;
    }

    public boolean isVector() {
        return ((this.getColDim() == 1) || (this.getRowDim() == 1));
    }

    public boolean isZero(final int aRow, final int aCol) {
        return TypeUtils.isZero(myDelegate.get(aRow, aCol));
    }

    public final Iterator<Double> iterator() {
        return new Iterator1D<Double>(this);
    }

    public void maxpy(final Double aSclrA, final MatrixStore<Double> aMtrxX) {

        final double tmpValA = aSclrA;
        final double[][] tmpArray = myDelegate.getArray();

        final int tmpRowDim = myDelegate.getRowDimension();
        final int tmpColDim = myDelegate.getColumnDimension();

        for (int i = 0; i < tmpRowDim; i++) {
            for (int j = 0; j < tmpColDim; j++) {
                tmpArray[i][j] += tmpValA * aMtrxX.doubleValue(i, j);
            }
        }
    }

    public JamaMatrix mergeColumns(final BasicMatrix aMtrx) {

        final int tmpRowDim = this.getRowDim() + aMtrx.getRowDim();
        final int tmpColDim = this.getColDim();

        final Matrix retVal = new Matrix(tmpRowDim, tmpColDim);

        retVal.setMatrix(0, this.getRowDim() - 1, 0, tmpColDim - 1, myDelegate);
        retVal.setMatrix(this.getRowDim(), tmpRowDim - 1, 0, tmpColDim - 1, JamaMatrix.convert(aMtrx));

        return new JamaMatrix(retVal);
    }

    public JamaMatrix mergeRows(final BasicMatrix aMtrx) {

        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = this.getColDim() + aMtrx.getColDim();

        final Matrix retVal = new Matrix(tmpRowDim, tmpColDim);

        retVal.setMatrix(0, tmpRowDim - 1, 0, this.getColDim() - 1, myDelegate);
        retVal.setMatrix(0, tmpRowDim - 1, this.getColDim(), tmpColDim - 1, JamaMatrix.convert(aMtrx));

        return new JamaMatrix(retVal);
    }

    public void modifyAll(final UnaryFunction<Double> aFunc) {
        ArrayUtils.modifyAll(myDelegate.getArray(), aFunc);
    }

    public void modifyColumn(final int aRow, final int aCol, final UnaryFunction<Double> aFunc) {
        ArrayUtils.modifyColumn(myDelegate.getArray(), aRow, aCol, aFunc);
    }

    public void modifyDiagonal(final int aRow, final int aCol, final UnaryFunction<Double> aFunc) {
        ArrayUtils.modifyDiagonal(myDelegate.getArray(), aRow, aCol, aFunc);
    }

    public void modifyOne(final int aRow, final int aCol, final UnaryFunction<Double> aFunc) {

        double tmpValue = this.doubleValue(aRow, aCol);

        tmpValue = aFunc.invoke(tmpValue);

        this.set(aRow, aCol, tmpValue);
    }

    public void modifyRow(final int aRow, final int aCol, final UnaryFunction<Double> aFunc) {
        ArrayUtils.modifyRow(myDelegate.getArray(), aRow, aCol, aFunc);
    }

    public JamaMatrix multiply(final Number aNmbr) {
        return new JamaMatrix(myDelegate.times(aNmbr.doubleValue()));
    }

    public JamaMatrix multiplyElements(final BasicMatrix aMtrx) {
        return new JamaMatrix(myDelegate.arrayTimes(JamaMatrix.convert(aMtrx)));
    }

    public JamaMatrix multiplyLeft(final BasicMatrix aMtrx) {
        return new JamaMatrix(JamaMatrix.convert(aMtrx).times(myDelegate));
    }

    public JamaMatrix multiplyLeft(final MatrixStore<Double> aStore) {
        return new JamaMatrix(JamaMatrix.convert(aStore).times(myDelegate));
    }

    public JamaMatrix multiplyRight(final BasicMatrix aMtrx) {
        return new JamaMatrix(myDelegate.times(JamaMatrix.convert(aMtrx)));
    }

    public JamaMatrix multiplyRight(final MatrixStore<Double> aStore) {
        return new JamaMatrix(myDelegate.times(JamaMatrix.convert(aStore)));
    }

    public PrimitiveScalar multiplyVectors(final BasicMatrix aVctr) {

        final List<Double> tmpThis = this.asList();
        final List<Double> tmpThat = aVctr.toPrimitiveStore().asList();

        double retVal = ZERO;

        for (int i = 0; i < tmpThis.size(); i++) {
            retVal += tmpThis.get(i) * tmpThat.get(i);
        }

        return new PrimitiveScalar(retVal);
    }

    public JamaMatrix negate() {
        return new JamaMatrix(myDelegate.uminus());
    }

    public void raxpy(final Double aSclrA, final int aRowX, final int aRowY, final int aFirstCol) {

        final double tmpValA = aSclrA.doubleValue();
        final double[][] tmpArray = myDelegate.getArray();

        final int tmpColDim = myDelegate.getColumnDimension();

        for (int j = aFirstCol; j < tmpColDim; j++) {
            tmpArray[aRowY][j] += tmpValA * tmpArray[aRowX][j];

        }
    }

    public JamaMatrix replace(final int aRow, final int aCol, final Number aNmbr) {

        final JamaMatrix retVal = new JamaMatrix(new Matrix(myDelegate.getArrayCopy()));

        retVal.update(aRow, aCol, aNmbr.doubleValue());

        return retVal;
    }

    public JamaMatrix round(final NumberContext aCntxt) {

        final double[][] retVal = myDelegate.getArrayCopy();

        ArrayUtils.modifyAll(retVal, aCntxt.getPrimitiveRoundFunction());

        return new JamaMatrix(retVal);
    }

    public JamaMatrix selectColumns(final int... someCols) {
        return new JamaMatrix(myDelegate.getMatrix(MatrixUtils.makeIncreasingRange(0, this.getRowDim()), someCols));
    }

    public JamaMatrix selectRows(final int... someRows) {
        return new JamaMatrix(myDelegate.getMatrix(someRows, MatrixUtils.makeIncreasingRange(0, this.getColDim())));
    }

    public void set(final int aRow, final int aCol, final double aNmbr) {
        myDelegate.set(aRow, aCol, aNmbr);
    }

    public void set(final int aRow, final int aCol, final Double aNmbr) {
        myDelegate.set(aRow, aCol, aNmbr);
    }

    public int size() {
        return myDelegate.getRowDimension() * myDelegate.getColumnDimension();
    }

    public JamaMatrix solve(final BasicMatrix aRHS) {

        Matrix retVal = JamaMatrix.convert(aRHS);

        try {
            if (this.isTall()) {
                retVal = new QRDecomposition(myDelegate).solve(retVal);
            } else {
                retVal = new LUDecomposition(myDelegate).solve(retVal);
            }
        } catch (final RuntimeException anRE) {
            final JamaSingularValue tmpMD = new JamaSingularValue();
            tmpMD.compute(myDelegate);
            retVal = tmpMD.solve(new JamaMatrix(retVal)).getDelegate();
        }

        return new JamaMatrix(retVal);
    }

    public JamaMatrix subtract(final BasicMatrix aMtrx) {
        return new JamaMatrix(myDelegate.minus(JamaMatrix.convert(aMtrx)));
    }

    public JamaMatrix subtract(final Number aNmbr) {

        final double[][] retVal = myDelegate.getArrayCopy();

        ArrayUtils.modifyAll(retVal, new PreconfiguredSecond<Double>(SUBTRACT, aNmbr.doubleValue()));

        return new JamaMatrix(retVal);
    }

    public BigDecimal toBigDecimal(final int aRow, final int aCol) {
        return new BigDecimal(myDelegate.get(aRow, aCol));
    }

    public PhysicalStore<BigDecimal> toBigStore() {
        return BigDenseStore.FACTORY.copy(this);
    }

    public ComplexNumber toComplexNumber(final int aRow, final int aCol) {
        return new ComplexNumber(myDelegate.get(aRow, aCol));
    }

    public PhysicalStore<ComplexNumber> toComplexStore() {
        return ComplexDenseStore.FACTORY.copy(this);
    }

    public List<BasicMatrix> toListOfColumns() {

        final int tmpColDim = this.getColDim();

        final List<BasicMatrix> retVal = new ArrayList<BasicMatrix>(tmpColDim);

        for (int j = 0; j < tmpColDim; j++) {
            retVal.add(j, this.selectColumns(new int[] { j }));
        }

        return retVal;
    }

    public List<Double> toListOfElements() {
        return this.toPrimitiveStore().asList();
    }

    public List<BasicMatrix> toListOfRows() {

        final int tmpRowDim = this.getRowDim();

        final List<BasicMatrix> retVal = new ArrayList<BasicMatrix>(tmpRowDim);

        for (int i = 0; i < tmpRowDim; i++) {
            retVal.add(i, this.selectRows(new int[] { i }));
        }

        return retVal;
    }

    public JamaMatrix toPrimitiveStore() {
        return new JamaMatrix(myDelegate.getArrayCopy());
    }

    public PrimitiveScalar toScalar(final int aRow, final int aCol) {
        return new PrimitiveScalar(myDelegate.get(aRow, aCol));
    }

    @Override
    public String toString() {
        return MatrixUtils.toString(this);
    }

    public String toString(final int aRow, final int aCol) {
        return Double.toString(myDelegate.get(aRow, aCol));
    }

    public void transformLeft(final Householder<Double> aTransf, final int aFirstCol) {

        final double[][] tmpArray = myDelegate.getArray();
        final int tmpRowDim = myDelegate.getRowDimension();
        final int tmpColDim = myDelegate.getColumnDimension();

        final int tmpFirst = aTransf.first();

        final double[] tmpWorkCopy = new double[aTransf.size()];

        double tmpScale;
        for (int j = aFirstCol; j < tmpColDim; j++) {
            tmpScale = ZERO;
            for (int i = tmpFirst; i < tmpRowDim; i++) {
                tmpScale += tmpWorkCopy[i] * tmpArray[i][j];
            }
            double tmpVal, tmpVal2 = PrimitiveMath.ZERO;
            final int tmpSize = aTransf.size();
            for (int i1 = aTransf.first(); i1 < tmpSize; i1++) {
                tmpVal = aTransf.doubleValue(i1);
                tmpVal2 += tmpVal * tmpVal;
                tmpWorkCopy[i1] = tmpVal;
            }
            tmpScale *= PrimitiveMath.TWO / tmpVal2;
            for (int i = tmpFirst; i < tmpRowDim; i++) {
                tmpArray[i][j] -= tmpScale * tmpWorkCopy[i];
            }
        }
    }

    public void transformLeft(final Rotation<Double> aTransf) {

        final Rotation.Primitive tmpTransf = JamaMatrix.cast(aTransf);

        final int tmpLow = tmpTransf.low;
        final int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {

                final double[][] tmpArray = myDelegate.getArray();
                double tmpOldLow;
                double tmpOldHigh;

                for (int j = 0; j < tmpArray[0].length; j++) {

                    tmpOldLow = tmpArray[tmpLow][j];
                    tmpOldHigh = tmpArray[tmpHigh][j];

                    tmpArray[tmpLow][j] = (tmpTransf.cos * tmpOldLow) + (tmpTransf.sin * tmpOldHigh);
                    tmpArray[tmpHigh][j] = (tmpTransf.cos * tmpOldHigh) - (tmpTransf.sin * tmpOldLow);
                }
            } else {
                this.exchangeRows(tmpLow, tmpHigh);
            }
        } else {
            if (!Double.isNaN(tmpTransf.cos)) {
                this.modifyRow(tmpLow, 0, new PreconfiguredSecond<Double>(MULTIPLY, tmpTransf.cos));
            } else if (!Double.isNaN(tmpTransf.sin)) {
                this.modifyRow(tmpLow, 0, new PreconfiguredSecond<Double>(DIVIDE, tmpTransf.sin));
            } else {
                this.modifyRow(tmpLow, 0, NEGATE);
            }
        }
    }

    public void transformRight(final Householder<Double> aTransf, final int aFirstRow) {

        final double[][] tmpArray = myDelegate.getArray();
        final int tmpRowDim = myDelegate.getRowDimension();
        final int tmpColDim = myDelegate.getColumnDimension();

        final int tmpFirst = aTransf.first();

        final double[] tmpWorkCopy = new double[aTransf.size()];

        double tmpScale;
        for (int i = aFirstRow; i < tmpRowDim; i++) {
            tmpScale = ZERO;
            for (int j = tmpFirst; j < tmpColDim; j++) {
                tmpScale += tmpWorkCopy[j] * tmpArray[i][j];
            }
            double tmpVal, tmpVal2 = PrimitiveMath.ZERO;
            final int tmpSize = aTransf.size();
            for (int i1 = aTransf.first(); i1 < tmpSize; i1++) {
                tmpVal = aTransf.doubleValue(i1);
                tmpVal2 += tmpVal * tmpVal;
                tmpWorkCopy[i1] = tmpVal;
            }
            tmpScale *= PrimitiveMath.TWO / tmpVal2;
            for (int j = tmpFirst; j < tmpColDim; j++) {
                tmpArray[i][j] -= tmpScale * tmpWorkCopy[j];
            }
        }
    }

    public void transformRight(final Rotation<Double> aTransf) {

        final Rotation.Primitive tmpTransf = JamaMatrix.cast(aTransf);

        final int tmpLow = tmpTransf.low;
        final int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {

                final double[][] tmpArray = myDelegate.getArray();
                double tmpOldLow;
                double tmpOldHigh;

                for (int i = 0; i < tmpArray.length; i++) {

                    tmpOldLow = tmpArray[i][tmpLow];
                    tmpOldHigh = tmpArray[i][tmpHigh];

                    tmpArray[i][tmpLow] = (tmpTransf.cos * tmpOldLow) - (tmpTransf.sin * tmpOldHigh);
                    tmpArray[i][tmpHigh] = (tmpTransf.cos * tmpOldHigh) + (tmpTransf.sin * tmpOldLow);
                }
            } else {
                this.exchangeColumns(tmpLow, tmpHigh);
            }
        } else {
            if (!Double.isNaN(tmpTransf.cos)) {
                this.modifyColumn(0, tmpHigh, new PreconfiguredSecond<Double>(MULTIPLY, tmpTransf.cos));
            } else if (!Double.isNaN(tmpTransf.sin)) {
                this.modifyColumn(0, tmpHigh, new PreconfiguredSecond<Double>(DIVIDE, tmpTransf.sin));
            } else {
                this.modifyColumn(0, tmpHigh, NEGATE);
            }
        }
    }

    public JamaMatrix transpose() {
        return new JamaMatrix(myDelegate.transpose());
    }

    public final void update(final int aFirstRow, final int aRowCount, final int aFirstCol, final int aColCount, final JamaMatrix aMtrx) {
        myDelegate.setMatrix(aFirstRow, aRowCount - aFirstRow - 1, aFirstCol, aColCount - aFirstCol - 1, aMtrx.getDelegate());
    }

    public final void update(final int aFirstRow, final int aRowCount, final int[] someColumns, final JamaMatrix aMtrx) {
        myDelegate.setMatrix(aFirstRow, aRowCount - aFirstRow - 1, someColumns, aMtrx.getDelegate());
    }

    public final void update(final int aRow, final int aCol, final Number aNmbr) {
        myDelegate.set(aRow, aCol, aNmbr.doubleValue());
    }

    public final void update(final int[] someRows, final int aFirstCol, final int aColCount, final JamaMatrix aMtrx) {
        myDelegate.setMatrix(someRows, aFirstCol, aColCount - aFirstCol - 1, aMtrx.getDelegate());
    }

    public final void update(final int[] someRows, final int[] someColumns, final JamaMatrix aMtrx) {
        myDelegate.setMatrix(someRows, someColumns, aMtrx.getDelegate());
    }

    public void visitAll(final AggregatorFunction<Double> aVisitor) {
        ArrayUtils.visitAll(myDelegate.getArray(), aVisitor);
    }

    public void visitColumn(final int aRow, final int aCol, final AggregatorFunction<Double> aVisitor) {
        ArrayUtils.visitColumn(myDelegate.getArray(), aRow, aCol, aVisitor);
    }

    public void visitDiagonal(final int aRow, final int aCol, final AggregatorFunction<Double> aVisitor) {
        ArrayUtils.visitDiagonal(myDelegate.getArray(), aRow, aCol, aVisitor);
    }

    public void visitRow(final int aRow, final int aCol, final AggregatorFunction<Double> aVisitor) {
        ArrayUtils.visitRow(myDelegate.getArray(), aRow, aCol, aVisitor);
    }

    final JamaCholesky getCholeskyDecomposition() {
        final JamaCholesky retVal = new JamaCholesky();
        retVal.compute(myDelegate);
        return retVal;
    }

    final Matrix getDelegate() {
        return myDelegate;
    }

    final JamaEigenvalue getEigenvalueDecomposition() {
        final JamaEigenvalue retVal = MatrixUtils.isSymmetric(this) ? new JamaEigenvalue.Symmetric() : new JamaEigenvalue.Nonsymmetric();
        retVal.compute(myDelegate);
        return retVal;
    }

    final JamaLU getLUDecomposition() {
        final JamaLU retVal = new JamaLU();
        retVal.compute(myDelegate);
        return retVal;
    }

    final JamaQR getQRDecomposition() {
        final JamaQR retVal = new JamaQR();
        retVal.compute(myDelegate);
        return retVal;
    }

    final JamaSingularValue getSingularValueDecomposition() {
        final JamaSingularValue retVal = new JamaSingularValue();
        retVal.compute(myDelegate);
        return retVal;
    }

}
