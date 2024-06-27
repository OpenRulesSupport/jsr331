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

import static org.ojalgo.constant.BigMath.*;
import static org.ojalgo.function.implementation.BigFunction.*;

import java.math.BigDecimal;
import java.util.List;

import org.ojalgo.OjAlgoUtils;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.BigArray;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.concurrent.DivideAndMerge;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.BigAggregator;
import org.ojalgo.function.implementation.BigFunction;
import org.ojalgo.function.implementation.FunctionSet;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.operation.*;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.BigScalar;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * A {@linkplain BigDecimal} implementation of {@linkplain PhysicalStore}.
 *
 * @author apete
 */
public final class BigDenseStore extends BigArray implements PhysicalStore<BigDecimal>, DecompositionStore<BigDecimal> {

    public static final DecompositionStore.Factory<BigDecimal, BigDenseStore> FACTORY = new DecompositionStore.Factory<BigDecimal, BigDenseStore>() {

        public BigDenseStore columns(final Access1D<?>... aSource) {

            final int tmpRowDim = aSource[0].size();
            final int tmpColDim = aSource.length;

            final BigDecimal[] tmpData = new BigDecimal[tmpRowDim * tmpColDim];

            Access1D<?> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toBigDecimal(tmpColumn.get(i));
                }
            }

            return new BigDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public BigDenseStore columns(final double[]... aSource) {

            final int tmpRowDim = aSource[0].length;
            final int tmpColDim = aSource.length;

            final BigDecimal[] tmpData = new BigDecimal[tmpRowDim * tmpColDim];

            double[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toBigDecimal(tmpColumn[i]);
                }
            }

            return new BigDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public BigDenseStore columns(final List<? extends Number>... aSource) {

            final int tmpRowDim = aSource[0].size();
            final int tmpColDim = aSource.length;

            final BigDecimal[] tmpData = new BigDecimal[tmpRowDim * tmpColDim];

            List<? extends Number> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toBigDecimal(tmpColumn.get(i));
                }
            }

            return new BigDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public BigDenseStore columns(final Number[]... aSource) {

            final int tmpRowDim = aSource[0].length;
            final int tmpColDim = aSource.length;

            final BigDecimal[] tmpData = new BigDecimal[tmpRowDim * tmpColDim];

            Number[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toBigDecimal(tmpColumn[i]);
                }
            }

            return new BigDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public BigDenseStore conjugate(final Access2D<?> aSource) {
            return this.transpose(aSource);
        }

        public BigDenseStore copy(final Access2D<?> aSource) {

            final BigDenseStore retVal = new BigDenseStore(aSource.getRowDim(), aSource.getColDim());

            retVal.fillMatching(aSource);

            return retVal;
        }

        public AggregatorCollection<BigDecimal> getAggregatorCollection() {
            return BigAggregator.getCollection();
        }

        public FunctionSet<BigDecimal> getFunctionSet() {
            return BigFunction.getSet();
        }

        public BigDecimal getNumber(final double aNmbr) {
            return new BigDecimal(aNmbr);
        }

        public BigDecimal getNumber(final Number aNmbr) {
            return TypeUtils.toBigDecimal(aNmbr);
        }

        public BigScalar getStaticOne() {
            return BigScalar.ONE;
        }

        public BigScalar getStaticZero() {
            return BigScalar.ZERO;
        }

        public SimpleArray.Big makeArray(final int aLength) {
            return SimpleArray.makeBig(aLength);
        }

        public BigDenseStore makeEye(final int aRowDim, final int aColDim) {

            final BigDenseStore retVal = this.makeZero(aRowDim, aColDim);

            retVal.myUtility.fillDiagonal(0, 0, this.getStaticOne().getNumber());

            return retVal;
        }

        public Householder.Big makeHouseholder(final int aLength) {
            return new Householder.Big(aLength);
        }

        public BigDenseStore makeRandom(final int aRowDim, final int aColDim, final RandomNumber aRndm) {

            final int tmpRowDim = aRowDim;
            final int tmpColDim = aColDim;

            final int tmpLength = tmpRowDim * tmpColDim;

            final BigDecimal[] tmpData = new BigDecimal[tmpLength];

            for (int i = 0; i < tmpLength; i++) {
                tmpData[i] = TypeUtils.toBigDecimal(aRndm.doubleValue());
            }

            return new BigDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public Rotation.Big makeRotation(final int aLow, final int aHigh, final BigDecimal aCos, final BigDecimal aSin) {
            return new Rotation.Big(aLow, aHigh, aCos, aSin);
        }

        public Rotation.Big makeRotation(final int aLow, final int aHigh, final double aCos, final double aSin) {
            return this.makeRotation(aLow, aHigh, new BigDecimal(aCos), new BigDecimal(aSin));
        }

        public BigDenseStore makeZero(final int aRowDim, final int aColDim) {
            return new BigDenseStore(aRowDim, aColDim);
        }

        public BigDenseStore rows(final Access1D<?>... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].size();

            final BigDecimal[] tmpData = new BigDecimal[tmpRowDim * tmpColDim];

            Access1D<?> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toBigDecimal(tmpRow.get(j));
                }
            }

            return new BigDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public BigDenseStore rows(final double[]... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].length;

            final BigDecimal[] tmpData = new BigDecimal[tmpRowDim * tmpColDim];

            double[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toBigDecimal(tmpRow[j]);
                }
            }

            return new BigDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public BigDenseStore rows(final List<? extends Number>... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].size();

            final BigDecimal[] tmpData = new BigDecimal[tmpRowDim * tmpColDim];

            List<? extends Number> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toBigDecimal(tmpRow.get(j));
                }
            }

            return new BigDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public BigDenseStore rows(final Number[]... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].length;

            final BigDecimal[] tmpData = new BigDecimal[tmpRowDim * tmpColDim];

            Number[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toBigDecimal(tmpRow[j]);
                }
            }

            return new BigDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public BigScalar toScalar(final double aNmbr) {
            return new BigScalar(aNmbr);
        }

        public BigScalar toScalar(final Number aNmbr) {
            return new BigScalar(TypeUtils.toBigDecimal(aNmbr));
        }

        public BigDenseStore transpose(final Access2D<?> aSource) {

            MatrixStore<BigDecimal> tmpSource = new WrapperStore<BigDecimal>(this, aSource);
            tmpSource = new TransposedStore<BigDecimal>(tmpSource);

            final BigDenseStore retVal = new BigDenseStore(tmpSource.getRowDim(), tmpSource.getColDim());

            retVal.fillMatching(tmpSource);

            return retVal;
        }
    };

    static Householder.Big cast(final Householder<BigDecimal> aTransf) {
        if (aTransf instanceof Householder.Big) {
            return (Householder.Big) aTransf;
        } else if (aTransf instanceof DecompositionStore.HouseholderReference<?>) {
            return ((DecompositionStore.HouseholderReference<BigDecimal>) aTransf).getBigWorker().copy(aTransf);
        } else {
            return new Householder.Big(aTransf);
        }
    }

    static BigDenseStore cast(final MatrixStore<BigDecimal> aStore) {
        if (aStore instanceof BigDenseStore) {
            return (BigDenseStore) aStore;
        } else {
            return FACTORY.copy(aStore);
        }
    }

    static Rotation.Big cast(final Rotation<BigDecimal> aTransf) {
        if (aTransf instanceof Rotation.Big) {
            return (Rotation.Big) aTransf;
        } else {
            return new Rotation.Big(aTransf);
        }
    }

    static void doMultiplyBoth(final BigDecimal[] aProductArray, final Access2D<BigDecimal> aLeftStore, final Access2D<BigDecimal> aRightStore) {

        final int tmpRowDim = aLeftStore.getRowDim();

        if (tmpRowDim > MultiplyBoth.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(MultiplyBoth.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    MultiplyBoth.invoke(aProductArray, aFirst, aLimit, aLeftStore, aRightStore);
                }
            };

            tmpConquerer.divide(0, tmpRowDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            MultiplyBoth.invoke(aProductArray, 0, tmpRowDim, aLeftStore, aRightStore);
        }
    }

    static void doMultiplyLeft(final BigDecimal[] aProductArray, final MatrixStore<BigDecimal> aLeftStore, final BigDecimal[] aRightArray) {

        final int tmpRowDim = aLeftStore.getRowDim();

        if (tmpRowDim > MultiplyLeft.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(MultiplyLeft.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    MultiplyLeft.invoke(aProductArray, aFirst, aLimit, aLeftStore, aRightArray);
                }
            };

            tmpConquerer.divide(0, tmpRowDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            MultiplyLeft.invoke(aProductArray, 0, tmpRowDim, aLeftStore, aRightArray);
        }
    }

    static void doMultiplyRight(final BigDecimal[] aProductArray, final BigDecimal[] aLeftArray, final MatrixStore<BigDecimal> aRightStore) {

        final int tmpColDim = aRightStore.getColDim();

        if (tmpColDim > MultiplyRight.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(MultiplyRight.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    MultiplyRight.invoke(aProductArray, aFirst, aLimit, aLeftArray, aRightStore);
                }
            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            MultiplyRight.invoke(aProductArray, 0, tmpColDim, aLeftArray, aRightStore);
        }
    }

    private final int myColDim;
    private final int myRowDim;
    private final Array2D<BigDecimal> myUtility;

    BigDenseStore(final BigDecimal[] anArray) {

        super(anArray);

        myRowDim = anArray.length;
        myColDim = 1;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    BigDenseStore(final int aLength) {

        super(aLength);

        myRowDim = aLength;
        myColDim = 1;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    BigDenseStore(final int aRowDim, final int aColDim) {

        super(aRowDim * aColDim);

        myRowDim = aRowDim;
        myColDim = aColDim;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    BigDenseStore(final int aRowDim, final int aColDim, final BigDecimal[] anArray) {

        super(anArray);

        myRowDim = aRowDim;
        myColDim = aColDim;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    public BigDecimal aggregateAll(final Aggregator aVisitor) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > AggregateAll.THRESHOLD) {

            final DivideAndMerge<BigDecimal> tmpConquerer = new DivideAndMerge<BigDecimal>(AggregateAll.THRESHOLD) {

                @Override
                public BigDecimal conquer(final int aFirst, final int aLimit) {

                    final AggregatorFunction<BigDecimal> tmpAggrFunc = aVisitor.getBigFunction();

                    BigDenseStore.this.visit(tmpRowDim * aFirst, tmpRowDim * aLimit, 1, tmpAggrFunc);

                    return tmpAggrFunc.getNumber();
                }

                @Override
                public BigDecimal merge(final BigDecimal aFirstResult, final BigDecimal aSecondResult) {

                    final AggregatorFunction<BigDecimal> tmpAggrFunc = aVisitor.getBigFunction();

                    tmpAggrFunc.merge(aFirstResult);
                    tmpAggrFunc.merge(aSecondResult);

                    return tmpAggrFunc.getNumber();
                }
            };

            return tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            final AggregatorFunction<BigDecimal> tmpAggrFunc = aVisitor.getBigFunction();

            BigDenseStore.this.visit(0, length, 1, tmpAggrFunc);

            return tmpAggrFunc.getNumber();
        }
    }

    public void applyTransformations(final int iterationPoint, final SimpleArray<BigDecimal> multipliers, final boolean hermitian) {

        final BigDecimal[] tmpData = this.data();
        final BigDecimal[] tmpColumn = ((SimpleArray.Big) multipliers).data;

        if ((myColDim - iterationPoint - 1) > ApplyTransformations.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(ApplyTransformations.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    ApplyTransformations.invoke(tmpData, myRowDim, aFirst, aLimit, tmpColumn, iterationPoint, hermitian);
                }
            };

            tmpConquerer.divide(iterationPoint + 1, myColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            ApplyTransformations.invoke(tmpData, myRowDim, iterationPoint + 1, myColDim, tmpColumn, iterationPoint, hermitian);
        }
    }

    public Array2D<BigDecimal> asArray2D() {
        return myUtility;
    }

    public Array1D<BigDecimal> asList() {
        return myUtility.asArray1D();
    }

    public final MatrixStore.Builder<BigDecimal> builder() {
        return new MatrixStore.Builder<BigDecimal>(this);
    }

    public void caxpy(final BigDecimal aSclrA, final int aColX, final int aColY, final int aFirstRow) {
        CAXPY.invoke(this.data(), aColY * myRowDim, this.data(), aColX * myRowDim, aSclrA, aFirstRow, myRowDim);
    }

    public Array1D<ComplexNumber> computeInPlaceSchur(final PhysicalStore<BigDecimal> aTransformationCollector, final boolean eigenvalue) {
        throw new UnsupportedOperationException();
    }

    public BigDenseStore conjugate() {
        return this.transpose();
    }

    public BigDenseStore copy() {
        return new BigDenseStore(myRowDim, myColDim, this.copyOfData());
    }

    public void divideAndCopyColumn(final int aRow, final int aCol, final SimpleArray<BigDecimal> aDestination) {

        final BigDecimal[] tmpData = this.data();
        final int tmpRowDim = myRowDim;

        final BigDecimal[] tmpDestination = ((SimpleArray.Big) aDestination).data;

        int tmpIndex = aRow + (aCol * tmpRowDim);
        final BigDecimal tmpDenominator = tmpData[tmpIndex];

        for (int i = aRow + 1; i < tmpRowDim; i++) {
            tmpIndex++;
            tmpDestination[i] = tmpData[tmpIndex] = BigFunction.DIVIDE.invoke(tmpData[tmpIndex], tmpDenominator);
        }
    }

    public double doubleValue(final int aRow, final int aCol) {
        return myUtility.doubleValue(aRow, aCol);
    }

    public boolean equals(final MatrixStore<BigDecimal> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(this, aStore, aCntxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object anObj) {
        if (anObj instanceof MatrixStore) {
            return this.equals((MatrixStore<BigDecimal>) anObj, TypeUtils.EQUALS_NUMBER_CONTEXT);
        } else {
            return super.equals(anObj);
        }
    }

    public void exchangeColumns(final int aColA, final int aColB) {
        myUtility.exchangeColumns(aColA, aColB);
    }

    public void exchangeRows(final int aRowA, final int aRowB) {
        myUtility.exchangeRows(aRowA, aRowB);
    }

    public PhysicalStore.Factory<BigDecimal, BigDenseStore> factory() {
        return FACTORY;
    }

    public void fillAll(final BigDecimal aNmbr) {
        myUtility.fillAll(aNmbr);
    }

    public void fillByMultiplying(final MatrixStore<BigDecimal> aLeftStore, final MatrixStore<BigDecimal> aRightStore) {

        final BigDecimal[] tmpProductData = this.data();

        if (aRightStore instanceof BigDenseStore) {

            BigDenseStore.doMultiplyLeft(tmpProductData, aLeftStore, BigDenseStore.cast(aRightStore).data());

        } else if (aLeftStore instanceof BigDenseStore) {

            this.fillAll(ZERO);

            BigDenseStore.doMultiplyRight(tmpProductData, BigDenseStore.cast(aLeftStore).data(), aRightStore);

        } else {

            BigDenseStore.doMultiplyBoth(tmpProductData, aLeftStore, aRightStore);
        }
    }

    public void fillColumn(final int aRow, final int aCol, final BigDecimal aNmbr) {
        myUtility.fillColumn(aRow, aCol, aNmbr);
    }

    public void fillDiagonal(final int aRow, final int aCol, final BigDecimal aNmbr) {
        myUtility.fillDiagonal(aRow, aCol, aNmbr);
    }

    public void fillMatching(final Access2D<? extends Number> aSource2D) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingSingle.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingSingle.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    FillMatchingSingle.invoke(BigDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aSource2D);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            FillMatchingSingle.invoke(this.data(), tmpRowDim, 0, tmpColDim, aSource2D);
        }
    }

    public void fillMatching(final BigDecimal aLeftArg, final BinaryFunction<BigDecimal> aFunc, final MatrixStore<BigDecimal> aRightArg) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingRight.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingRight.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    BigDenseStore.this.fill(tmpRowDim * aFirst, tmpRowDim * aLimit, aLeftArg, aFunc, aRightArg);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.fill(0, tmpRowDim * tmpColDim, aLeftArg, aFunc, aRightArg);
        }
    }

    public void fillMatching(final MatrixStore<BigDecimal> aLeftArg, final BinaryFunction<BigDecimal> aFunc, final BigDecimal aRightArg) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingLeft.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingLeft.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    BigDenseStore.this.fill(tmpRowDim * aFirst, tmpRowDim * aLimit, aLeftArg, aFunc, aRightArg);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.fill(0, tmpRowDim * tmpColDim, aLeftArg, aFunc, aRightArg);
        }
    }

    public void fillMatching(final MatrixStore<BigDecimal> aLeftArg, final BinaryFunction<BigDecimal> aFunc, final MatrixStore<BigDecimal> aRightArg) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingBoth.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingBoth.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    BigDenseStore.this.fill(tmpRowDim * aFirst, tmpRowDim * aLimit, aLeftArg, aFunc, aRightArg);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.fill(0, tmpRowDim * tmpColDim, aLeftArg, aFunc, aRightArg);
        }
    }

    public void fillRow(final int aRow, final int aCol, final BigDecimal aNmbr) {
        myUtility.fillRow(aRow, aCol, aNmbr);
    }

    public boolean generateApplyAndCopyHouseholderColumn(final int aRow, final int aCol, final Householder<BigDecimal> aDestination) {
        return GenerateApplyAndCopyHouseholderColumn.invoke(this.data(), myRowDim, aRow, aCol, (Householder.Big) aDestination);
    }

    public boolean generateApplyAndCopyHouseholderRow(final int aRow, final int aCol, final Householder<BigDecimal> aDestination) {
        return GenerateApplyAndCopyHouseholderRow.invoke(this.data(), myRowDim, aRow, aCol, (Householder.Big) aDestination);
    }

    public BigDecimal get(final int aRow, final int aCol) {
        return myUtility.get(aRow, aCol);
    }

    public int getColDim() {
        return myColDim;
    }

    /**
     * @deprecated v33 Use {@link #factory()} instead
     */
    @Deprecated
    public PhysicalStore.Factory<BigDecimal, BigDenseStore> getFactory() {
        return this.factory();
    }

    public int getIndexOfLargestInColumn(final int aRow, final int aCol) {
        return myUtility.getIndexOfLargestInColumn(aRow, aCol);
    }

    public int getMinDim() {
        return Math.min(myRowDim, myColDim);
    }

    public int getRowDim() {
        return myRowDim;
    }

    @Override
    public int hashCode() {
        return MatrixUtils.hashCode(this);
    }

    public boolean isAbsolute(final int aRow, final int aCol) {
        return myUtility.isAbsolute(aRow, aCol);
    }

    public boolean isLowerLeftShaded() {
        return false;
    }

    public boolean isPositive(final int aRow, final int aCol) {
        return myUtility.isPositive(aRow, aCol);
    }

    public boolean isReal(final int aRow, final int aCol) {
        return myUtility.isReal(aRow, aCol);
    }

    public boolean isUpperRightShaded() {
        return false;
    }

    public boolean isZero(final int aRow, final int aCol) {
        return myUtility.isZero(aRow, aCol);
    }

    public void maxpy(final BigDecimal aSclrA, final MatrixStore<BigDecimal> aMtrxX) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > MAXPY.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(MAXPY.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    MAXPY.invoke(BigDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aSclrA, aMtrxX);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            MAXPY.invoke(this.data(), tmpRowDim, 0, tmpColDim, aSclrA, aMtrxX);
        }
    }

    public void modifyAll(final UnaryFunction<BigDecimal> aFunc) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > ModifyAll.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(ModifyAll.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    BigDenseStore.this.modify(tmpRowDim * aFirst, tmpRowDim * aLimit, 1, aFunc);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.modify(tmpRowDim * 0, tmpRowDim * tmpColDim, 1, aFunc);
        }
    }

    public void modifyColumn(final int aRow, final int aCol, final UnaryFunction<BigDecimal> aFunc) {
        myUtility.modifyColumn(aRow, aCol, aFunc);
    }

    public void modifyDiagonal(final int aRow, final int aCol, final UnaryFunction<BigDecimal> aFunc) {
        myUtility.modifyDiagonal(aRow, aCol, aFunc);
    }

    public void modifyOne(final int aRow, final int aCol, final UnaryFunction<BigDecimal> aFunc) {

        BigDecimal tmpValue = this.get(aRow, aCol);

        tmpValue = aFunc.invoke(tmpValue);

        this.set(aRow, aCol, tmpValue);
    }

    public void modifyRow(final int aRow, final int aCol, final UnaryFunction<BigDecimal> aFunc) {
        myUtility.modifyRow(aRow, aCol, aFunc);
    }

    public MatrixStore<BigDecimal> multiplyLeft(final MatrixStore<BigDecimal> aStore) {

        final BigDenseStore retVal = FACTORY.makeZero(aStore.getRowDim(), myColDim);

        BigDenseStore.doMultiplyLeft(retVal.data(), aStore, this.data());

        return retVal;
    }

    public MatrixStore<BigDecimal> multiplyRight(final MatrixStore<BigDecimal> aStore) {

        final BigDenseStore retVal = FACTORY.makeZero(myRowDim, aStore.getColDim());

        BigDenseStore.doMultiplyRight(retVal.data(), this.data(), aStore);

        return retVal;
    }

    public void negateColumn(final int aCol) {
        myUtility.modifyColumn(0, aCol, BigFunction.NEGATE);
    }

    public void raxpy(final BigDecimal aSclrA, final int aRowX, final int aRowY, final int aFirstCol) {
        RAXPY.invoke(this.data(), aRowY, this.data(), aRowX, aSclrA, aFirstCol, myColDim);
    }

    public void rotateRight(final int aLow, final int aHigh, final double aCos, final double aSin) {
        RotateRight.invoke(this.data(), myRowDim, aLow, aHigh, FACTORY.getNumber(aCos), FACTORY.getNumber(aSin));
    }

    public void set(final int aRow, final int aCol, final BigDecimal aNmbr) {
        myUtility.set(aRow, aCol, aNmbr);
    }

    public void set(final int aRow, final int aCol, final double aNmbr) {
        myUtility.set(aRow, aCol, aNmbr);
    }

    public void setToIdentity(final int aCol) {
        myUtility.set(aCol, aCol, ONE);
        myUtility.fillColumn(aCol + 1, aCol, ZERO);
    }

    public void substituteBackwards(final Access2D<BigDecimal> aBody, final boolean conjugated) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > SubstituteBackwards.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(SubstituteBackwards.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    SubstituteBackwards.invoke(BigDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aBody, conjugated);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            SubstituteBackwards.invoke(this.data(), tmpRowDim, 0, tmpColDim, aBody, conjugated);
        }
    }

    public void substituteForwards(final Access2D<BigDecimal> aBody, final boolean onesOnDiagonal, final boolean zerosAboveDiagonal) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > SubstituteForwards.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(SubstituteForwards.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    SubstituteForwards.invoke(BigDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aBody, onesOnDiagonal, zerosAboveDiagonal);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            SubstituteForwards.invoke(this.data(), tmpRowDim, 0, tmpColDim, aBody, onesOnDiagonal, zerosAboveDiagonal);
        }
    }

    public Scalar<BigDecimal> toScalar(final int aRow, final int aCol) {
        return new BigScalar(this.get(aRow, aCol));
    }

    public void transformLeft(final Householder<BigDecimal> aTransf, final int aFirstCol) {

        final Householder.Big tmpTransf = BigDenseStore.cast(aTransf);

        final BigDecimal[] tmpData = this.data();

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > HouseholderLeft.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(HouseholderLeft.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    HouseholderLeft.invoke(tmpData, tmpRowDim, aFirst, aLimit, tmpTransf);
                }

            };

            tmpConquerer.divide(aFirstCol, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            HouseholderLeft.invoke(tmpData, tmpRowDim, aFirstCol, tmpColDim, tmpTransf);
        }
    }

    public void transformLeft(final Rotation<BigDecimal> aTransf) {

        final Rotation.Big tmpTransf = BigDenseStore.cast(aTransf);

        final int tmpLow = tmpTransf.low;
        final int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if ((tmpTransf.cos != null) && (tmpTransf.sin != null)) {
                RotateLeft.invoke(this.data(), myColDim, tmpLow, tmpHigh, tmpTransf.cos, tmpTransf.sin);
            } else {
                myUtility.exchangeRows(tmpLow, tmpHigh);
            }
        } else {
            if (tmpTransf.cos != null) {
                myUtility.modifyRow(tmpLow, 0, MULTIPLY, tmpTransf.cos);
            } else if (tmpTransf.sin != null) {
                myUtility.modifyRow(tmpLow, 0, DIVIDE, tmpTransf.sin);
            } else {
                myUtility.modifyRow(tmpLow, 0, NEGATE);
            }
        }
    }

    public void transformRight(final Householder<BigDecimal> aTransf, final int aFirstRow) {

        final Householder.Big tmpTransf = BigDenseStore.cast(aTransf);

        final BigDecimal[] tmpData = this.data();

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > HouseholderRight.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(HouseholderRight.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    HouseholderRight.invoke(tmpData, aFirst, aLimit, tmpColDim, tmpTransf);
                }

            };

            tmpConquerer.divide(aFirstRow, tmpRowDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            HouseholderRight.invoke(tmpData, aFirstRow, tmpRowDim, tmpColDim, tmpTransf);
        }
    }

    public void transformRight(final Rotation<BigDecimal> aTransf) {

        final Rotation.Big tmpTransf = BigDenseStore.cast(aTransf);

        final int tmpLow = tmpTransf.low;
        final int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if ((tmpTransf.cos != null) && (tmpTransf.sin != null)) {
                RotateRight.invoke(this.data(), myRowDim, tmpLow, tmpHigh, tmpTransf.cos, tmpTransf.sin);
            } else {
                myUtility.exchangeColumns(tmpLow, tmpHigh);
            }
        } else {
            if (tmpTransf.cos != null) {
                myUtility.modifyColumn(0, tmpHigh, MULTIPLY, tmpTransf.cos);
            } else if (tmpTransf.sin != null) {
                myUtility.modifyColumn(0, tmpHigh, DIVIDE, tmpTransf.sin);
            } else {
                myUtility.modifyColumn(0, tmpHigh, NEGATE);
            }
        }
    }

    public void transformSymmetric(final Householder<BigDecimal> aTransf) {
        HouseholderHermitian.invoke(this.data(), BigDenseStore.cast(aTransf), new BigDecimal[aTransf.size()]);
    }

    public BigDenseStore transpose() {

        final BigDenseStore retVal = new BigDenseStore(myColDim, myRowDim);

        retVal.fillMatching(new TransposedStore<BigDecimal>(this));

        return retVal;
    }

    public void tred2(final SimpleArray<BigDecimal> mainDiagonal, final SimpleArray<BigDecimal> offDiagonal, final boolean yesvecs) {
        throw new UnsupportedOperationException();
    }

    public void visitAll(final AggregatorFunction<BigDecimal> aVisitor) {
        myUtility.visitAll(aVisitor);
    }

    public void visitColumn(final int aRow, final int aCol, final AggregatorFunction<BigDecimal> aVisitor) {
        myUtility.visitColumn(aRow, aCol, aVisitor);
    }

    public void visitDiagonal(final int aRow, final int aCol, final AggregatorFunction<BigDecimal> aVisitor) {
        myUtility.visitDiagonal(aRow, aCol, aVisitor);
    }

    public void visitRow(final int aRow, final int aCol, final AggregatorFunction<BigDecimal> aVisitor) {
        myUtility.visitRow(aRow, aCol, aVisitor);
    }

}
