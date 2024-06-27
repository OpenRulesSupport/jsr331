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

import java.math.BigDecimal;

import org.ojalgo.ProgrammingError;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;

public final class SingleStore<N extends Number> extends FactoryStore<N> {

    public static SingleStore<BigDecimal> makeBig(final BigDecimal aSingleElement) {
        return new SingleStore<BigDecimal>(BigDenseStore.FACTORY, aSingleElement);
    }

    public static SingleStore<ComplexNumber> makeComplex(final ComplexNumber aSingleElement) {
        return new SingleStore<ComplexNumber>(ComplexDenseStore.FACTORY, aSingleElement);
    }

    public static SingleStore<Double> makePrimitive(final double aSingleElement) {
        return new SingleStore<Double>(PrimitiveDenseStore.FACTORY, aSingleElement);
    }

    private final N myNumber;
    private final double myValue;

    public SingleStore(final PhysicalStore.Factory<N, ?> aFactory, final N anElement) {

        super(INT_ONE, INT_ONE, aFactory);

        myNumber = anElement;
        myValue = anElement.doubleValue();
    }

    @SuppressWarnings("unused")
    private SingleStore(final PhysicalStore.Factory<N, ?> aFactory) {

        this(aFactory, aFactory.getStaticZero().getNumber());

        ProgrammingError.throwForIllegalInvocation();
    }

    @Override
    public PhysicalStore<N> conjugate() {

        final PhysicalStore<N> retVal = this.factory().makeZero(INT_ONE, INT_ONE);

        retVal.set(INT_ZERO, INT_ZERO, this.factory().toScalar(myNumber).conjugate().getNumber());

        return retVal;
    }

    @Override
    public PhysicalStore<N> copy() {

        final PhysicalStore<N> retVal = this.factory().makeZero(INT_ONE, INT_ONE);

        retVal.set(INT_ZERO, INT_ZERO, myNumber);

        return retVal;
    }

    @Override
    public double doubleValue(final int anInd) {
        return myValue;
    }

    public double doubleValue(final int aRow, final int aCol) {
        return myValue;
    }

    @Override
    public N get(final int anInd) {
        return myNumber;
    }

    public N get(final int aRow, final int aCol) {
        return myNumber;
    }

    public boolean isLowerLeftShaded() {
        return BOOLEAN_TRUE;
    }

    public boolean isUpperRightShaded() {
        return BOOLEAN_TRUE;
    }

    @Override
    public MatrixStore<N> multiplyLeft(final MatrixStore<N> aStore) {

        final int tmpRowDim = aStore.getRowDim();
        final int tmpColDim = this.getColDim();

        final Factory<N, ?> tmpFactory = this.factory();

        final PhysicalStore<N> retVal = tmpFactory.makeZero(tmpRowDim, tmpColDim);

        retVal.fillMatching(aStore, tmpFactory.getFunctionSet().multiply(), myNumber);

        return retVal;
    }

    @Override
    public MatrixStore<N> multiplyRight(final MatrixStore<N> aStore) {

        final int tmpRowDim = this.getRowDim();
        final int tmpColDim = aStore.getColDim();

        final Factory<N, ?> tmpFactory = this.factory();

        final PhysicalStore<N> retVal = tmpFactory.makeZero(tmpRowDim, tmpColDim);

        retVal.fillMatching(myNumber, tmpFactory.getFunctionSet().multiply(), aStore);

        return retVal;
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        return this.factory().toScalar(myNumber);
    }

    @Override
    public PhysicalStore<N> transpose() {
        return this.copy();
    }

}
