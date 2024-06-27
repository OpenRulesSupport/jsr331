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
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;

/**
 * ZeroStore
 *
 * @author apete
 */
public final class ZeroStore<N extends Number> extends FactoryStore<N> {

    public static ZeroStore<BigDecimal> makeBig(final int aRowDim, final int aColDim) {
        return new ZeroStore<BigDecimal>(BigDenseStore.FACTORY, aRowDim, aColDim);
    }

    public static ZeroStore<ComplexNumber> makeComplex(final int aRowDim, final int aColDim) {
        return new ZeroStore<ComplexNumber>(ComplexDenseStore.FACTORY, aRowDim, aColDim);
    }

    public static ZeroStore<Double> makePrimitive(final int aRowDim, final int aColDim) {
        return new ZeroStore<Double>(PrimitiveDenseStore.FACTORY, aRowDim, aColDim);
    }

    private final N myNumberZero;
    private final Scalar<N> myScalarZero;

    public ZeroStore(final PhysicalStore.Factory<N, ?> aFactory, final int aRowDim, final int aColDim) {

        super(aRowDim, aColDim, aFactory);

        myScalarZero = aFactory.getStaticZero();
        myNumberZero = myScalarZero.getNumber();
    }

    @SuppressWarnings("unused")
    private ZeroStore(final PhysicalStore.Factory<N, ?> aFactory) {

        this(aFactory, INT_ZERO, INT_ZERO);

        ProgrammingError.throwForIllegalInvocation();
    }

    @Override
    public PhysicalStore<N> conjugate() {
        return this.factory().makeZero(this.getColDim(), this.getRowDim());
    }

    @Override
    public PhysicalStore<N> copy() {
        return this.factory().makeZero(this.getRowDim(), this.getColDim());
    }

    @Override
    public double doubleValue(final int anInd) {
        return PrimitiveMath.ZERO;
    }

    public double doubleValue(final int aRow, final int aCol) {
        return PrimitiveMath.ZERO;
    }

    @Override
    public N get(final int anInd) {
        return myNumberZero;
    }

    public N get(final int aRow, final int aCol) {
        return myNumberZero;
    }

    public boolean isLowerLeftShaded() {
        return BOOLEAN_TRUE;
    }

    public boolean isUpperRightShaded() {
        return BOOLEAN_TRUE;
    }

    @Override
    public ZeroStore<N> multiplyLeft(final MatrixStore<N> aStore) {
        return new ZeroStore<N>(this.factory(), aStore.getRowDim(), this.getColDim());
    }

    @Override
    public ZeroStore<N> multiplyRight(final MatrixStore<N> aStore) {
        return new ZeroStore<N>(this.factory(), this.getRowDim(), aStore.getColDim());
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        return myScalarZero;
    }

    @Override
    public PhysicalStore<N> transpose() {
        return this.factory().makeZero(this.getColDim(), this.getRowDim());
    }

}
