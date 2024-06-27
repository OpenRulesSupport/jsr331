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

import static org.ojalgo.function.implementation.ComplexFunction.*;
import static org.ojalgo.scalar.ComplexNumber.*;

import java.util.List;

import org.ojalgo.OjAlgoUtils;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.ComplexArray;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.concurrent.DivideAndMerge;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.ComplexAggregator;
import org.ojalgo.function.implementation.ComplexFunction;
import org.ojalgo.function.implementation.FunctionSet;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.operation.*;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * A {@linkplain ComplexNumber} implementation of {@linkplain PhysicalStore}.
 *
 * @author apete
 */
public final class ComplexDenseStore extends ComplexArray implements PhysicalStore<ComplexNumber>, DecompositionStore<ComplexNumber> {

    public static final DecompositionStore.Factory<ComplexNumber, ComplexDenseStore> FACTORY = new DecompositionStore.Factory<ComplexNumber, ComplexDenseStore>() {

        public ComplexDenseStore columns(final Access1D<?>... aSource) {

            final int tmpRowDim = aSource[0].size();
            final int tmpColDim = aSource.length;

            final ComplexNumber[] tmpData = new ComplexNumber[tmpRowDim * tmpColDim];

            Access1D<?> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toComplexNumber(tmpColumn.get(i));
                }
            }

            return new ComplexDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public ComplexDenseStore columns(final double[]... aSource) {

            final int tmpRowDim = aSource[0].length;
            final int tmpColDim = aSource.length;

            final ComplexNumber[] tmpData = new ComplexNumber[tmpRowDim * tmpColDim];

            double[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toComplexNumber(tmpColumn[i]);
                }
            }

            return new ComplexDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public ComplexDenseStore columns(final List<? extends Number>... aSource) {

            final int tmpRowDim = aSource[0].size();
            final int tmpColDim = aSource.length;

            final ComplexNumber[] tmpData = new ComplexNumber[tmpRowDim * tmpColDim];

            List<? extends Number> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toComplexNumber(tmpColumn.get(i));
                }
            }

            return new ComplexDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public ComplexDenseStore columns(final Number[]... aSource) {

            final int tmpRowDim = aSource[0].length;
            final int tmpColDim = aSource.length;

            final ComplexNumber[] tmpData = new ComplexNumber[tmpRowDim * tmpColDim];

            Number[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toComplexNumber(tmpColumn[i]);
                }
            }

            return new ComplexDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public ComplexDenseStore conjugate(final Access2D<?> aSource) {

            MatrixStore<ComplexNumber> tmpSource = new WrapperStore<ComplexNumber>(this, aSource);
            tmpSource = new ConjugatedStore<ComplexNumber>(tmpSource);

            final ComplexDenseStore retVal = new ComplexDenseStore(tmpSource.getRowDim(), tmpSource.getColDim());

            retVal.fillMatching(tmpSource);

            return retVal;
        }

        public ComplexDenseStore copy(final Access2D<?> aSource) {

            final ComplexDenseStore retVal = new ComplexDenseStore(aSource.getRowDim(), aSource.getColDim());

            retVal.fillMatching(aSource);

            return retVal;
        }

        public AggregatorCollection<ComplexNumber> getAggregatorCollection() {
            return ComplexAggregator.getCollection();
        }

        public FunctionSet<ComplexNumber> getFunctionSet() {
            return ComplexFunction.getSet();
        }

        public ComplexNumber getNumber(final double aNmbr) {
            return new ComplexNumber(aNmbr);
        }

        public ComplexNumber getNumber(final Number aNmbr) {
            return TypeUtils.toComplexNumber(aNmbr);
        }

        public ComplexNumber getStaticOne() {
            return ComplexNumber.ONE;
        }

        public ComplexNumber getStaticZero() {
            return ComplexNumber.ZERO;
        }

        public SimpleArray.Complex makeArray(final int aLength) {
            return SimpleArray.makeComplex(aLength);
        }

        public ComplexDenseStore makeEye(final int aRowDim, final int aColDim) {

            final ComplexDenseStore retVal = this.makeZero(aRowDim, aColDim);

            retVal.myUtility.fillDiagonal(0, 0, this.getStaticOne().getNumber());

            return retVal;
        }

        public Householder.Complex makeHouseholder(final int aLength) {
            return new Householder.Complex(aLength);
        }

        public ComplexDenseStore makeRandom(final int aRowDim, final int aColDim, final RandomNumber aRndm) {

            final int tmpRowDim = aRowDim;
            final int tmpColDim = aColDim;

            final int tmpLength = tmpRowDim * tmpColDim;

            final ComplexNumber[] tmpData = new ComplexNumber[tmpLength];

            for (int i = 0; i < tmpLength; i++) {
                tmpData[i] = TypeUtils.toComplexNumber(aRndm.doubleValue());
            }

            return new ComplexDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public Rotation.Complex makeRotation(final int aLow, final int aHigh, final ComplexNumber aCos, final ComplexNumber aSin) {
            return new Rotation.Complex(aLow, aHigh, aCos, aSin);
        }

        public Rotation.Complex makeRotation(final int aLow, final int aHigh, final double aCos, final double aSin) {
            return this.makeRotation(aLow, aHigh, new ComplexNumber(aCos), new ComplexNumber(aSin));
        }

        public ComplexDenseStore makeZero(final int aRowDim, final int aColDim) {
            return new ComplexDenseStore(aRowDim, aColDim);
        }

        public ComplexDenseStore rows(final Access1D<?>... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].size();

            final ComplexNumber[] tmpData = new ComplexNumber[tmpRowDim * tmpColDim];

            Access1D<?> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toComplexNumber(tmpRow.get(j));
                }
            }

            return new ComplexDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public ComplexDenseStore rows(final double[]... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].length;

            final ComplexNumber[] tmpData = new ComplexNumber[tmpRowDim * tmpColDim];

            double[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toComplexNumber(tmpRow[j]);
                }
            }

            return new ComplexDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public ComplexDenseStore rows(final List<? extends Number>... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].size();

            final ComplexNumber[] tmpData = new ComplexNumber[tmpRowDim * tmpColDim];

            List<? extends Number> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toComplexNumber(tmpRow.get(j));
                }
            }

            return new ComplexDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public ComplexDenseStore rows(final Number[]... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].length;

            final ComplexNumber[] tmpData = new ComplexNumber[tmpRowDim * tmpColDim];

            Number[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = TypeUtils.toComplexNumber(tmpRow[j]);
                }
            }

            return new ComplexDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public ComplexNumber toScalar(final double aNmbr) {
            return new ComplexNumber(aNmbr);
        }

        public ComplexNumber toScalar(final Number aNmbr) {
            return TypeUtils.toComplexNumber(aNmbr);
        }

        public ComplexDenseStore transpose(final Access2D<?> aSource) {

            MatrixStore<ComplexNumber> tmpSource = new WrapperStore<ComplexNumber>(this, aSource);
            tmpSource = new TransposedStore<ComplexNumber>(tmpSource);

            final ComplexDenseStore retVal = new ComplexDenseStore(tmpSource.getRowDim(), tmpSource.getColDim());

            retVal.fillMatching(tmpSource);

            return retVal;
        }
    };

    static Householder.Complex cast(final Householder<ComplexNumber> aTransf) {
        if (aTransf instanceof Householder.Complex) {
            return (Householder.Complex) aTransf;
        } else if (aTransf instanceof DecompositionStore.HouseholderReference<?>) {
            return ((DecompositionStore.HouseholderReference<ComplexNumber>) aTransf).getComplexWorker().copy(aTransf);
        } else {
            return new Householder.Complex(aTransf);
        }
    }

    static ComplexDenseStore cast(final MatrixStore<ComplexNumber> aStore) {
        if (aStore instanceof ComplexDenseStore) {
            return (ComplexDenseStore) aStore;
        } else {
            return FACTORY.copy(aStore);
        }
    }

    static Rotation.Complex cast(final Rotation<ComplexNumber> aTransf) {
        if (aTransf instanceof Rotation.Complex) {
            return (Rotation.Complex) aTransf;
        } else {
            return new Rotation.Complex(aTransf);
        }
    }

    static void doMultiplyBoth(final ComplexNumber[] aProductArray, final Access2D<ComplexNumber> aLeftStore, final Access2D<ComplexNumber> aRightStore) {

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

    static void doMultiplyLeft(final ComplexNumber[] aProductArray, final MatrixStore<ComplexNumber> aLeftStore, final ComplexNumber[] aRightArray) {

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

    static void doMultiplyRight(final ComplexNumber[] aProductArray, final ComplexNumber[] aLeftArray, final MatrixStore<ComplexNumber> aRightStore) {

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
    private final Array2D<ComplexNumber> myUtility;

    ComplexDenseStore(final ComplexNumber[] anArray) {

        super(anArray);

        myRowDim = anArray.length;
        myColDim = 1;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    ComplexDenseStore(final int aLength) {

        super(aLength);

        myRowDim = aLength;
        myColDim = 1;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    ComplexDenseStore(final int aRowDim, final int aColDim) {

        super(aRowDim * aColDim);

        myRowDim = aRowDim;
        myColDim = aColDim;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    ComplexDenseStore(final int aRowDim, final int aColDim, final ComplexNumber[] anArray) {

        super(anArray);

        myRowDim = aRowDim;
        myColDim = aColDim;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    public ComplexNumber aggregateAll(final Aggregator aVisitor) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > AggregateAll.THRESHOLD) {

            final DivideAndMerge<ComplexNumber> tmpConquerer = new DivideAndMerge<ComplexNumber>(AggregateAll.THRESHOLD) {

                @Override
                public ComplexNumber conquer(final int aFirst, final int aLimit) {

                    final AggregatorFunction<ComplexNumber> tmpAggrFunc = aVisitor.getComplexFunction();

                    ComplexDenseStore.this.visit(tmpRowDim * aFirst, tmpRowDim * aLimit, 1, tmpAggrFunc);

                    return tmpAggrFunc.getNumber();
                }

                @Override
                public ComplexNumber merge(final ComplexNumber aFirstResult, final ComplexNumber aSecondResult) {

                    final AggregatorFunction<ComplexNumber> tmpAggrFunc = aVisitor.getComplexFunction();

                    tmpAggrFunc.merge(aFirstResult);
                    tmpAggrFunc.merge(aSecondResult);

                    return tmpAggrFunc.getNumber();
                }
            };

            return tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            final AggregatorFunction<ComplexNumber> tmpAggrFunc = aVisitor.getComplexFunction();

            ComplexDenseStore.this.visit(0, length, 1, tmpAggrFunc);

            return tmpAggrFunc.getNumber();
        }
    }

    public void applyTransformations(final int iterationPoint, final SimpleArray<ComplexNumber> multipliers, final boolean hermitian) {

        final ComplexNumber[] tmpData = this.data();
        final ComplexNumber[] tmpColumn = ((SimpleArray.Complex) multipliers).data;

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

    public Array2D<ComplexNumber> asArray2D() {
        return myUtility;
    }

    public Array1D<ComplexNumber> asList() {
        return myUtility.asArray1D();
    }

    public final MatrixStore.Builder<ComplexNumber> builder() {
        return new MatrixStore.Builder<ComplexNumber>(this);
    }

    public void caxpy(final ComplexNumber aSclrA, final int aColX, final int aColY, final int aFirstRow) {
        CAXPY.invoke(this.data(), aColY * myRowDim, this.data(), aColX * myRowDim, aSclrA, aFirstRow, myRowDim);
    }

    public Array1D<ComplexNumber> computeInPlaceSchur(final PhysicalStore<ComplexNumber> aTransformationCollector, final boolean eigenvalue) {
        throw new UnsupportedOperationException();
    }

    public ComplexDenseStore conjugate() {

        final ComplexDenseStore retVal = new ComplexDenseStore(myColDim, myRowDim);

        retVal.fillMatching(new ConjugatedStore<ComplexNumber>(this));

        return retVal;
    }

    public ComplexDenseStore copy() {
        return new ComplexDenseStore(myRowDim, myColDim, this.copyOfData());
    }

    public void divideAndCopyColumn(final int aRow, final int aCol, final SimpleArray<ComplexNumber> aDestination) {

        final ComplexNumber[] tmpData = this.data();
        final int tmpRowDim = myRowDim;

        final ComplexNumber[] tmpDestination = ((SimpleArray.Complex) aDestination).data;

        int tmpIndex = aRow + (aCol * tmpRowDim);
        final ComplexNumber tmpDenominator = tmpData[tmpIndex];

        for (int i = aRow + 1; i < tmpRowDim; i++) {
            tmpIndex++;
            tmpDestination[i] = tmpData[tmpIndex] = tmpData[tmpIndex].divide(tmpDenominator);
        }
    }

    public double doubleValue(final int aRow, final int aCol) {
        return this.doubleValue(aRow + (aCol * myRowDim));
    }

    public boolean equals(final MatrixStore<ComplexNumber> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(this, aStore, aCntxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object anObj) {
        if (anObj instanceof MatrixStore) {
            return this.equals((MatrixStore<ComplexNumber>) anObj, TypeUtils.EQUALS_NUMBER_CONTEXT);
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

    public PhysicalStore.Factory<ComplexNumber, ComplexDenseStore> factory() {
        return FACTORY;
    }

    public void fillAll(final ComplexNumber aNmbr) {
        myUtility.fillAll(aNmbr);
    }

    public void fillByMultiplying(final MatrixStore<ComplexNumber> aLeftStore, final MatrixStore<ComplexNumber> aRightStore) {

        final ComplexNumber[] tmpProductData = this.data();

        if (aRightStore instanceof ComplexDenseStore) {

            ComplexDenseStore.doMultiplyLeft(tmpProductData, aLeftStore, ComplexDenseStore.cast(aRightStore).data());

        } else if (aLeftStore instanceof ComplexDenseStore) {

            this.fillAll(ZERO);

            ComplexDenseStore.doMultiplyRight(tmpProductData, ComplexDenseStore.cast(aLeftStore).data(), aRightStore);

        } else {

            ComplexDenseStore.doMultiplyBoth(tmpProductData, aLeftStore, aRightStore);
        }
    }

    public void fillColumn(final int aRow, final int aCol, final ComplexNumber aNmbr) {
        myUtility.fillColumn(aRow, aCol, aNmbr);
    }

    public void fillDiagonal(final int aRow, final int aCol, final ComplexNumber aNmbr) {
        myUtility.fillDiagonal(aRow, aCol, aNmbr);
    }

    public void fillMatching(final Access2D<? extends Number> aSource2D) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingSingle.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingSingle.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    FillMatchingSingle.invoke(ComplexDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aSource2D);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            FillMatchingSingle.invoke(this.data(), tmpRowDim, 0, tmpColDim, aSource2D);
        }
    }

    public void fillMatching(final ComplexNumber aLeftArg, final BinaryFunction<ComplexNumber> aFunc, final MatrixStore<ComplexNumber> aRightArg) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingRight.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingRight.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    ComplexDenseStore.this.fill(tmpRowDim * aFirst, tmpRowDim * aLimit, aLeftArg, aFunc, aRightArg);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.fill(0, tmpRowDim * tmpColDim, aLeftArg, aFunc, aRightArg);
        }
    }

    public void fillMatching(final MatrixStore<ComplexNumber> aLeftArg, final BinaryFunction<ComplexNumber> aFunc, final ComplexNumber aRightArg) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingLeft.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingLeft.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    ComplexDenseStore.this.fill(tmpRowDim * aFirst, tmpRowDim * aLimit, aLeftArg, aFunc, aRightArg);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.fill(0, tmpRowDim * tmpColDim, aLeftArg, aFunc, aRightArg);
        }
    }

    public void fillMatching(final MatrixStore<ComplexNumber> aLeftArg, final BinaryFunction<ComplexNumber> aFunc, final MatrixStore<ComplexNumber> aRightArg) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingBoth.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingBoth.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    ComplexDenseStore.this.fill(tmpRowDim * aFirst, tmpRowDim * aLimit, aLeftArg, aFunc, aRightArg);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.fill(0, tmpRowDim * tmpColDim, aLeftArg, aFunc, aRightArg);
        }
    }

    public void fillRow(final int aRow, final int aCol, final ComplexNumber aNmbr) {
        myUtility.fillRow(aRow, aCol, aNmbr);
    }

    public boolean generateApplyAndCopyHouseholderColumn(final int aRow, final int aCol, final Householder<ComplexNumber> aDestination) {
        return GenerateApplyAndCopyHouseholderColumn.invoke(this.data(), myRowDim, aRow, aCol, (Householder.Complex) aDestination);
    }

    public boolean generateApplyAndCopyHouseholderRow(final int aRow, final int aCol, final Householder<ComplexNumber> aDestination) {
        return GenerateApplyAndCopyHouseholderRow.invoke(this.data(), myRowDim, aRow, aCol, (Householder.Complex) aDestination);
    }

    public ComplexNumber get(final int aRow, final int aCol) {
        return myUtility.get(aRow, aCol);
    }

    public int getColDim() {
        return myColDim;
    }

    /**
     * @deprecated v33 Use {@link #factory()} instead
     */
    @Deprecated
    public PhysicalStore.Factory<ComplexNumber, ComplexDenseStore> getFactory() {
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

    public void maxpy(final ComplexNumber aSclrA, final MatrixStore<ComplexNumber> aMtrxX) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > MAXPY.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(MAXPY.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    MAXPY.invoke(ComplexDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aSclrA, aMtrxX);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            MAXPY.invoke(this.data(), tmpRowDim, 0, tmpColDim, aSclrA, aMtrxX);
        }
    }

    public void modifyAll(final UnaryFunction<ComplexNumber> aFunc) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > ModifyAll.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(ModifyAll.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    ComplexDenseStore.this.modify(tmpRowDim * aFirst, tmpRowDim * aLimit, 1, aFunc);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.modify(tmpRowDim * 0, tmpRowDim * tmpColDim, 1, aFunc);
        }
    }

    public void modifyColumn(final int aRow, final int aCol, final UnaryFunction<ComplexNumber> aFunc) {
        myUtility.modifyColumn(aRow, aCol, aFunc);
    }

    public void modifyDiagonal(final int aRow, final int aCol, final UnaryFunction<ComplexNumber> aFunc) {
        myUtility.modifyDiagonal(aRow, aCol, aFunc);
    }

    public void modifyOne(final int aRow, final int aCol, final UnaryFunction<ComplexNumber> aFunc) {

        ComplexNumber tmpValue = this.get(aRow, aCol);

        tmpValue = aFunc.invoke(tmpValue);

        this.set(aRow, aCol, tmpValue);
    }

    public void modifyRow(final int aRow, final int aCol, final UnaryFunction<ComplexNumber> aFunc) {
        myUtility.modifyRow(aRow, aCol, aFunc);
    }

    public MatrixStore<ComplexNumber> multiplyLeft(final MatrixStore<ComplexNumber> aStore) {

        final ComplexDenseStore retVal = FACTORY.makeZero(aStore.getRowDim(), myColDim);

        ComplexDenseStore.doMultiplyLeft(retVal.data(), aStore, this.data());

        return retVal;
    }

    public MatrixStore<ComplexNumber> multiplyRight(final MatrixStore<ComplexNumber> aStore) {

        final ComplexDenseStore retVal = FACTORY.makeZero(myRowDim, aStore.getColDim());

        ComplexDenseStore.doMultiplyRight(retVal.data(), this.data(), aStore);

        return retVal;
    }

    public void negateColumn(final int aCol) {
        myUtility.modifyColumn(0, aCol, ComplexFunction.NEGATE);
    }

    public void raxpy(final ComplexNumber aSclrA, final int aRowX, final int aRowY, final int aFirstCol) {
        RAXPY.invoke(this.data(), aRowY, this.data(), aRowX, aSclrA, aFirstCol, myColDim);
    }

    public void rotateRight(final int aLow, final int aHigh, final double aCos, final double aSin) {
        RotateRight.invoke(this.data(), myRowDim, aLow, aHigh, FACTORY.getNumber(aCos), FACTORY.getNumber(aSin));
    }

    public void set(final int aRow, final int aCol, final ComplexNumber aNmbr) {
        myUtility.set(aRow, aCol, aNmbr);
    }

    public void set(final int aRow, final int aCol, final double aNmbr) {
        myUtility.set(aRow, aCol, aNmbr);
    }

    public void setToIdentity(final int aCol) {
        myUtility.set(aCol, aCol, ComplexNumber.ONE);
        myUtility.fillColumn(aCol + 1, aCol, ComplexNumber.ZERO);
    }

    public void substituteBackwards(final Access2D<ComplexNumber> aBody, final boolean conjugated) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > SubstituteBackwards.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(SubstituteBackwards.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    SubstituteBackwards.invoke(ComplexDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aBody, conjugated);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            SubstituteBackwards.invoke(this.data(), tmpRowDim, 0, tmpColDim, aBody, conjugated);
        }
    }

    public void substituteForwards(final Access2D<ComplexNumber> aBody, final boolean onesOnDiagonal, final boolean zerosAboveDiagonal) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > SubstituteForwards.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(SubstituteForwards.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    SubstituteForwards.invoke(ComplexDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aBody, onesOnDiagonal, zerosAboveDiagonal);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            SubstituteForwards.invoke(this.data(), tmpRowDim, 0, tmpColDim, aBody, onesOnDiagonal, zerosAboveDiagonal);
        }
    }

    public Scalar<ComplexNumber> toScalar(final int aRow, final int aCol) {
        return myUtility.toScalar(aRow, aCol);
    }

    public void transformLeft(final Householder<ComplexNumber> aTransf, final int aFirstCol) {

        final Householder.Complex tmpTransf = ComplexDenseStore.cast(aTransf);

        final ComplexNumber[] tmpData = this.data();

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

    public void transformLeft(final Rotation<ComplexNumber> aTransf) {

        final Rotation.Complex tmpTransf = ComplexDenseStore.cast(aTransf);

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

    public void transformRight(final Householder<ComplexNumber> aTransf, final int aFirstRow) {

        final Householder.Complex tmpTransf = ComplexDenseStore.cast(aTransf);

        final ComplexNumber[] tmpData = this.data();

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

    public void transformRight(final Rotation<ComplexNumber> aTransf) {

        final Rotation.Complex tmpTransf = ComplexDenseStore.cast(aTransf);

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

    public void transformSymmetric(final Householder<ComplexNumber> aTransf) {
        HouseholderHermitian.invoke(this.data(), ComplexDenseStore.cast(aTransf), new ComplexNumber[aTransf.size()]);
    }

    public ComplexDenseStore transpose() {

        final ComplexDenseStore retVal = new ComplexDenseStore(myColDim, myRowDim);

        retVal.fillMatching(new TransposedStore<ComplexNumber>(this));

        return retVal;
    }

    public void tred2(final SimpleArray<ComplexNumber> mainDiagonal, final SimpleArray<ComplexNumber> offDiagonal, final boolean yesvecs) {
        throw new UnsupportedOperationException();
    }

    public void visitAll(final AggregatorFunction<ComplexNumber> aVisitor) {
        myUtility.visitAll(aVisitor);
    }

    public void visitColumn(final int aRow, final int aCol, final AggregatorFunction<ComplexNumber> aVisitor) {
        myUtility.visitColumn(aRow, aCol, aVisitor);
    }

    public void visitDiagonal(final int aRow, final int aCol, final AggregatorFunction<ComplexNumber> aVisitor) {
        myUtility.visitDiagonal(aRow, aCol, aVisitor);
    }

    public void visitRow(final int aRow, final int aCol, final AggregatorFunction<ComplexNumber> aVisitor) {
        myUtility.visitRow(aRow, aCol, aVisitor);
    }

}
