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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.decomposition.EigenvalueDecomposition;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.decomposition.LUDecomposition;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.decomposition.QRDecomposition;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.decomposition.SingularValueDecomposition;
import org.ojalgo.matrix.store.*;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * ArbitraryMatrix
 *
 * @author apete
 */
abstract class AbstractMatrix<N extends Number> extends Object implements BasicMatrix, Access2D<N>, Serializable {

    private transient Eigenvalue<N> myEigenvalue = null;
    private transient int myHashCode = 0;
    private transient LU<N> myLU = null;
    private final PhysicalStore.Factory<N, ? extends PhysicalStore<N>> myPhysicalFactory;
    private transient QR<N> myQR = null;
    private transient SingularValue<N> mySingularValue = null;
    private final MatrixStore<N> myStore;

    @SuppressWarnings("unused")
    private AbstractMatrix() {

        this(null);

        ProgrammingError.throwForIllegalInvocation();
    }

    AbstractMatrix(final MatrixStore<N> aStore) {

        super();

        myStore = aStore;
        myPhysicalFactory = this.getFactory().getPhysicalFactory();
    }

    public BasicMatrix add(final BasicMatrix aMtrx) {

        MatrixError.throwIfNotEqualDimensions(this, aMtrx);

        final PhysicalStore<N> retVal = myPhysicalFactory.makeZero(this.getRowDim(), this.getColDim());

        retVal.fillMatching(myStore, myPhysicalFactory.getFunctionSet().add(), this.getStoreFrom(aMtrx));

        return this.getFactory().instantiate(retVal);
    }

    public BasicMatrix add(final int aRow, final int aCol, final BasicMatrix aMtrx) {

        final MatrixStore<N> tmpDiff = this.getStoreFrom(aMtrx);

        return this.getFactory().instantiate(new SuperimposedStore<N>(myStore, aRow, aCol, tmpDiff));
    }

    public BasicMatrix add(final int aRow, final int aCol, final Number aNmbr) {

        final PhysicalStore.Factory<N, ?> tmpPhysicalFactory = myStore.factory();

        final SingleStore<N> tmpDiff = new SingleStore<N>(tmpPhysicalFactory, tmpPhysicalFactory.getNumber(aNmbr));

        return this.getFactory().instantiate(new SuperimposedStore<N>(myStore, aRow, aCol, tmpDiff));
    }

    public BasicMatrix add(final Number aNmbr) {

        final PhysicalStore<N> retVal = myPhysicalFactory.makeZero(this.getRowDim(), this.getColDim());

        retVal.fillMatching(myStore, myPhysicalFactory.getFunctionSet().add(), myPhysicalFactory.getNumber(aNmbr));

        return this.getFactory().instantiate(retVal);
    }

    public BasicMatrix conjugate() {

        MatrixStore<N> retVal;

        if (myStore instanceof ConjugatedStore) {
            retVal = ((ConjugatedStore<N>) myStore).getOriginal();
        } else {
            retVal = myStore.builder().conjugate().build();
        }

        return this.getFactory().instantiate(retVal);
    }

    public MatrixBuilder<N> copyToBuilder() {
        return new MatrixBuilder<N>(myStore.copy()) {

            @Override
            public BasicMatrix build() {
                return AbstractMatrix.this.getFactory().instantiate(this.getPhysicalStore());
            }
        };
    }

    public BasicMatrix divide(final Number aNmbr) {

        final PhysicalStore<N> retVal = myPhysicalFactory.makeZero(this.getRowDim(), this.getColDim());

        retVal.fillMatching(myStore, myPhysicalFactory.getFunctionSet().divide(), myPhysicalFactory.getNumber(aNmbr));

        return this.getFactory().instantiate(retVal);
    }

    public BasicMatrix divideElements(final BasicMatrix aMtrx) {

        MatrixError.throwIfNotEqualDimensions(this, aMtrx);

        final PhysicalStore<N> retVal = myPhysicalFactory.makeZero(this.getRowDim(), this.getColDim());

        retVal.fillMatching(myStore, myPhysicalFactory.getFunctionSet().divide(), this.getStoreFrom(aMtrx));

        return this.getFactory().instantiate(retVal);
    }

    public double doubleValue(final int anInd) {
        return myStore.doubleValue(anInd);
    }

    public double doubleValue(final int i, final int j) {
        return myStore.doubleValue(i, j);
    }

    public boolean equals(final BasicMatrix aMtrx, final NumberContext aCntxt) {
        return MatrixUtils.equals(this, aMtrx, aCntxt);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof BasicMatrix) {
            return this.equals((BasicMatrix) obj, TypeUtils.EQUALS_NUMBER_CONTEXT);
        } else {
            return super.equals(obj);
        }
    }

    public void flushCache() {

        myHashCode = 0;

        myEigenvalue = null;
        myLU = null;
        myQR = null;
        mySingularValue = null;
    }

    public N get(final int anInd) {
        return myStore.get(anInd);
    }

    public N get(final int aRow, final int aColumn) {
        return this.getStore().get(aRow, aColumn);
    }

    public int getColDim() {
        return myStore.getColDim();
    }

    public Scalar<N> getCondition() {
        return myPhysicalFactory.toScalar(this.getComputedSingularValue().getCondition());
    }

    public Scalar<N> getDeterminant() {
        if (!(myStore.get(0, 0) instanceof ComplexNumber) && this.getEigenvalue().isComputed()) {
            return myPhysicalFactory.toScalar(this.getComputedEigenvalue().getDeterminant());
        } else {
            return myPhysicalFactory.toScalar(this.getComputedLU().getDeterminant());
        }
    }

    public List<ComplexNumber> getEigenvalues() {
        return this.getComputedEigenvalue().getEigenvalues();
    }

    /**
     * @see org.ojalgo.matrix.BasicMatrix#getFrobeniusNorm()
     */
    public Scalar<N> getFrobeniusNorm() {

        if (this.getSingularValue().isComputed()) {

            return myPhysicalFactory.toScalar(this.getSingularValue().getFrobeniusNorm());

        } else {

            return myPhysicalFactory.toScalar(myStore.aggregateAll(Aggregator.NORM2));
        }
    }

    public Scalar<?> getInfinityNorm() {

        double retVal = PrimitiveMath.ZERO;
        final AggregatorFunction<N> tmpRowSumAggr = myPhysicalFactory.getAggregatorCollection().norm1();

        final int tmpRowDim = myStore.getRowDim();
        for (int i = 0; i < tmpRowDim; i++) {
            myStore.visitRow(i, 0, tmpRowSumAggr);
            retVal = Math.max(retVal, tmpRowSumAggr.doubleValue());
            tmpRowSumAggr.reset();
        }

        return myPhysicalFactory.toScalar(retVal);
    }

    public Scalar<N> getKyFanNorm(final int k) {
        return myPhysicalFactory.toScalar(this.getComputedSingularValue().getKyFanNorm(k));
    }

    public int getMinDim() {
        return myStore.getMinDim();
    }

    public Scalar<?> getOneNorm() {

        double retVal = PrimitiveMath.ZERO;
        final AggregatorFunction<N> tmpColSumAggr = myPhysicalFactory.getAggregatorCollection().norm1();

        final int tmpColDim = myStore.getColDim();
        for (int j = 0; j < tmpColDim; j++) {
            myStore.visitColumn(0, j, tmpColSumAggr);
            retVal = Math.max(retVal, tmpColSumAggr.doubleValue());
            tmpColSumAggr.reset();
        }

        return myPhysicalFactory.toScalar(retVal);
    }

    public Scalar<N> getOperatorNorm() {
        return myPhysicalFactory.toScalar(this.getComputedSingularValue().getOperatorNorm());
    }

    public int getRank() {
        if (this.getSingularValue().isComputed() || this.isFat()) {
            return this.getComputedSingularValue().getRank();
        } else if (this.getQR().isComputed() || this.isTall()) {
            return this.getComputedQR().getRank();
        } else {
            return this.getComputedLU().getRank();
        }
    }

    public int getRowDim() {
        return myStore.getRowDim();
    }

    public List<Double> getSingularValues() {
        return this.getComputedSingularValue().getSingularValues();
    }

    public Scalar<N> getTrace() {

        final AggregatorFunction<N> tmpAggr = myPhysicalFactory.getAggregatorCollection().sum();

        myStore.visitDiagonal(0, 0, tmpAggr);

        return myPhysicalFactory.toScalar(tmpAggr.getNumber());
    }

    public Scalar<N> getTraceNorm() {
        return myPhysicalFactory.toScalar(this.getComputedSingularValue().getTraceNorm());
    }

    public Scalar<N> getVectorNorm(final int aDegree) {

        switch (aDegree) {

        case 0:

            return myPhysicalFactory.toScalar(myStore.aggregateAll(Aggregator.CARDINALITY));

        case 1:

            return myPhysicalFactory.toScalar(myStore.aggregateAll(Aggregator.NORM1));

        case 2:

            return myPhysicalFactory.toScalar(myStore.aggregateAll(Aggregator.NORM2));

        default:

            return myPhysicalFactory.toScalar(myStore.aggregateAll(Aggregator.LARGEST));
        }
    }

    @Override
    public int hashCode() {
        if (myHashCode == 0) {
            myHashCode = MatrixUtils.hashCode(this);
        }
        return myHashCode;
    }

    public BasicMatrix invert() {

        MatrixStore<N> retVal = null;

        if (this.isSquare() && this.getComputedLU().isSolvable()) {
            retVal = this.getComputedLU().getInverse();
        } else if (this.isTall() && this.getComputedQR().isSolvable()) {
            retVal = this.getComputedQR().getInverse();
        } else {
            retVal = this.getComputedSingularValue().getInverse();
        }

        return this.getFactory().instantiate(retVal);
    }

    public boolean isEmpty() {
        return ((this.getRowDim() <= 0) || (this.getColDim() <= 0));
    }

    public boolean isFat() {
        return (!this.isEmpty() && (this.getRowDim() < this.getColDim()));
    }

    public boolean isFullRank() {
        return this.getRank() == myStore.getMinDim();
    }

    public boolean isHermitian() {
        return this.isSquare() && this.equals(this.conjugate(), TypeUtils.EQUALS_NUMBER_CONTEXT);
    }

    public boolean isScalar() {
        return (myStore.getRowDim() == 1) && (myStore.getColDim() == 1);
    }

    public boolean isSquare() {
        return (!this.isEmpty() && (this.getRowDim() == this.getColDim()));
    }

    public boolean isSymmetric() {
        return this.isSquare() && this.equals(this.transpose(), TypeUtils.EQUALS_NUMBER_CONTEXT);
    }

    public boolean isTall() {
        return (!this.isEmpty() && (this.getRowDim() > this.getColDim()));
    }

    public boolean isVector() {
        return ((this.getColDim() == 1) || (this.getRowDim() == 1));
    }

    public Iterator<N> iterator() {
        return new Iterator1D<N>(this);
    }

    public BasicMatrix mergeColumns(final BasicMatrix aMtrx) {

        MatrixError.throwIfNotEqualColumnDimensions(this, aMtrx);

        return this.getFactory().instantiate(new AboveBelowStore<N>(myStore, this.getStoreFrom(aMtrx)));
    }

    public BasicMatrix mergeRows(final BasicMatrix aMtrx) {

        MatrixError.throwIfNotEqualRowDimensions(this, aMtrx);

        return this.getFactory().instantiate(new LeftRightStore<N>(myStore, this.getStoreFrom(aMtrx)));
    }

    public BasicMatrix multiply(final Number aNmbr) {

        final PhysicalStore<N> retVal = myPhysicalFactory.makeZero(this.getRowDim(), this.getColDim());

        retVal.fillMatching(myStore, myPhysicalFactory.getFunctionSet().multiply(), myPhysicalFactory.getNumber(aNmbr));

        return this.getFactory().instantiate(retVal);
    }

    public BasicMatrix multiplyElements(final BasicMatrix aMtrx) {

        MatrixError.throwIfNotEqualDimensions(this, aMtrx);

        final PhysicalStore<N> retVal = myPhysicalFactory.makeZero(this.getRowDim(), this.getColDim());

        retVal.fillMatching(myStore, myPhysicalFactory.getFunctionSet().multiply(), this.getStoreFrom(aMtrx));

        return this.getFactory().instantiate(retVal);
    }

    public BasicMatrix multiplyLeft(final BasicMatrix aMtrx) {

        MatrixError.throwIfMultiplicationNotPossible(aMtrx, this);

        return this.getFactory().instantiate(myStore.multiplyLeft(this.getStoreFrom(aMtrx)));
    }

    public BasicMatrix multiplyRight(final BasicMatrix aMtrx) {

        MatrixError.throwIfMultiplicationNotPossible(this, aMtrx);

        return this.getFactory().instantiate(myStore.multiplyRight(this.getStoreFrom(aMtrx)));
    }

    @SuppressWarnings("unchecked")
    public Scalar<N> multiplyVectors(final BasicMatrix aVctr) {
        if (this.getRowDim() == 1) {
            return (Scalar<N>) this.multiplyRight(aVctr.getColDim() == 1 ? aVctr : aVctr.transpose()).toScalar(0, 0);
        } else if (this.getColDim() == 1) {
            return (Scalar<N>) this.multiplyLeft(aVctr.getRowDim() == 1 ? aVctr : aVctr.transpose()).toScalar(0, 0);
        } else {
            throw new ProgrammingError("Not a vector!");
        }
    }

    public BasicMatrix negate() {

        final PhysicalStore<N> retVal = myStore.copy();

        retVal.modifyAll(myPhysicalFactory.getFunctionSet().negate());

        return this.getFactory().instantiate(retVal);
    }

    public BasicMatrix replace(final int aRow, final int aCol, final Number aNmbr) {

        final Scalar<N> tmpDifference = myStore.factory().toScalar(aNmbr).subtract(myStore.get(aRow, aCol));

        if (tmpDifference.isZero()) {
            return this;
        } else {
            return this.getFactory().instantiate(
                    new SuperimposedStore<N>(myStore, aRow, aCol, new SingleStore<N>(myStore.factory(), tmpDifference.getNumber())));
        }
    }

    public BasicMatrix selectColumns(final int... someCols) {
        return this.getFactory().instantiate(myStore.builder().columns(someCols).build());
    }

    public BasicMatrix selectRows(final int... someRows) {
        return this.getFactory().instantiate(myStore.builder().rows(someRows).build());
    }

    public int size() {
        return this.getRowDim() * this.getColDim();
    }

    public BasicMatrix solve(final BasicMatrix aRHS) {

        MatrixStore<N> retVal = null;

        if (this.isSquare() && this.getComputedLU().isSolvable()) {
            retVal = this.getComputedLU().solve(this.getStoreFrom(aRHS));
        } else if (this.isTall() && this.getComputedQR().isSolvable()) {
            retVal = this.getComputedQR().solve(this.getStoreFrom(aRHS));
        } else {
            retVal = this.getComputedSingularValue().solve(this.getStoreFrom(aRHS));
        }

        return this.getFactory().instantiate(retVal);
    }

    public BasicMatrix subtract(final BasicMatrix aMtrx) {

        MatrixError.throwIfNotEqualDimensions(this, aMtrx);

        final PhysicalStore<N> retVal = myPhysicalFactory.makeZero(this.getRowDim(), this.getColDim());

        retVal.fillMatching(myStore, myPhysicalFactory.getFunctionSet().subtract(), this.getStoreFrom(aMtrx));

        return this.getFactory().instantiate(retVal);
    }

    public BasicMatrix subtract(final Number aNmbr) {

        final PhysicalStore<N> retVal = myPhysicalFactory.makeZero(this.getRowDim(), this.getColDim());

        retVal.fillMatching(myStore, myPhysicalFactory.getFunctionSet().subtract(), myPhysicalFactory.getNumber(aNmbr));

        return this.getFactory().instantiate(retVal);
    }

    public PhysicalStore<BigDecimal> toBigStore() {
        return BigDenseStore.FACTORY.copy(this);
    }

    public PhysicalStore<ComplexNumber> toComplexStore() {
        return ComplexDenseStore.FACTORY.copy(this);
    }

    public List<BasicMatrix> toListOfColumns() {

        final int tmpColDim = this.getColDim();

        final List<BasicMatrix> retVal = new ArrayList<BasicMatrix>(tmpColDim);

        for (int j = 0; j < tmpColDim; j++) {
            retVal.add(j, this.selectColumns(j));
        }

        return retVal;
    }

    public List<N> toListOfElements() {
        return this.copyOf(this).asList();
    }

    public List<BasicMatrix> toListOfRows() {

        final int tmpRowDim = this.getRowDim();

        final List<BasicMatrix> retVal = new ArrayList<BasicMatrix>(tmpRowDim);

        for (int i = 0; i < tmpRowDim; i++) {
            retVal.add(i, this.selectRows(i));
        }

        return retVal;
    }

    public PhysicalStore<Double> toPrimitiveStore() {
        return PrimitiveDenseStore.FACTORY.copy(this);
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        return myStore.toScalar(aRow, aCol);
    }

    @Override
    public String toString() {
        return MatrixUtils.toString(this);
    }

    public BasicMatrix transpose() {

        MatrixStore<N> retVal;

        if (myStore instanceof TransposedStore) {
            retVal = ((TransposedStore<N>) myStore).getOriginal();
        } else {
            retVal = myStore.builder().transpose().build();
        }

        return this.getFactory().instantiate(retVal);
    }

    private final Eigenvalue<N> getComputedEigenvalue() {

        final Eigenvalue<N> retVal = this.getEigenvalue();

        if (!retVal.isComputed()) {
            retVal.compute(myStore);
        }

        return retVal;
    }

    private final LU<N> getComputedLU() {

        final LU<N> retVal = this.getLU();

        if (!retVal.isComputed()) {
            retVal.compute(myStore);
        }

        return retVal;
    }

    private final QR<N> getComputedQR() {

        final QR<N> retVal = this.getQR();

        if (!retVal.isComputed()) {
            retVal.compute(myStore);
        }

        return retVal;
    }

    private final SingularValue<N> getComputedSingularValue() {

        final SingularValue<N> retVal = this.getSingularValue();

        if (!retVal.isComputed()) {
            retVal.compute(myStore);
        }

        return retVal;
    }

    private final Eigenvalue<N> getEigenvalue() {

        if (myEigenvalue == null) {
            myEigenvalue = EigenvalueDecomposition.make(myStore);
        }

        return myEigenvalue;
    }

    private final LU<N> getLU() {
        if (myLU == null) {
            myLU = LUDecomposition.make(myStore);
        }
        return myLU;
    }

    private final QR<N> getQR() {
        if (myQR == null) {
            myQR = QRDecomposition.make(myStore);
        }
        return myQR;
    }

    private final SingularValue<N> getSingularValue() {
        if (mySingularValue == null) {
            mySingularValue = SingularValueDecomposition.make(myStore);
        }
        return mySingularValue;
    }

    abstract PhysicalStore<N> copyOf(BasicMatrix aMtrx);

    abstract MatrixFactory<N> getFactory();

    final MatrixStore<N> getStore() {
        return myStore;
    }

    abstract MatrixStore<N> getStoreFrom(BasicMatrix aMtrx);

}
