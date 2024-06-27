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
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;

/**
 * IdentityStore
 *
 * @author apete
 */
public final class IdentityStore<N extends Number> extends FactoryStore<N> {

    public static AbstractStore<BigDecimal> makeBig(final int aDim) {
        return new IdentityStore<BigDecimal>(BigDenseStore.FACTORY, aDim);
    }

    public static AbstractStore<ComplexNumber> makeComplex(final int aDim) {
        return new IdentityStore<ComplexNumber>(ComplexDenseStore.FACTORY, aDim);
    }

    public static AbstractStore<Double> makePrimitive(final int aDim) {
        return new IdentityStore<Double>(PrimitiveDenseStore.FACTORY, aDim);
    }

    private final int myDim;

    public IdentityStore(final Factory<N, ?> aFactory, final int aDim) {

        super(aDim, aDim, aFactory);

        myDim = aDim;
    }

    @SuppressWarnings("unused")
    private IdentityStore(final Factory<N, ?> aFactory) {

        this(aFactory, INT_ZERO);

        ProgrammingError.throwForIllegalInvocation();
    }

    @Override
    public PhysicalStore<N> conjugate() {
        return this.factory().makeEye(myDim, myDim);
    }

    @Override
    public PhysicalStore<N> copy() {
        return this.factory().makeEye(myDim, myDim);
    }

    public double doubleValue(final int aRow, final int aCol) {
        if (aRow == aCol) {
            return PrimitiveMath.ONE;
        } else {
            return PrimitiveMath.ZERO;
        }
    }

    public N get(final int aRow, final int aCol) {
        if (aRow == aCol) {
            return this.factory().getStaticOne().getNumber();
        } else {
            return this.factory().getStaticZero().getNumber();
        }
    }

    public boolean isLowerLeftShaded() {
        return BOOLEAN_TRUE;
    }

    public boolean isUpperRightShaded() {
        return BOOLEAN_TRUE;
    }

    @Override
    public PhysicalStore<N> multiplyLeft(final MatrixStore<N> aStore) {
        return aStore.copy();
    }

    @Override
    public PhysicalStore<N> multiplyRight(final MatrixStore<N> aStore) {
        return aStore.copy();
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        if (aRow == aCol) {
            return this.factory().getStaticOne();
        } else {
            return this.factory().getStaticZero();
        }
    }

    @Override
    public PhysicalStore<N> transpose() {
        return this.factory().makeEye(myDim, myDim);
    }

}
