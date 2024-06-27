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
import org.ojalgo.access.Access2D;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;

/**
 * @author apete
 */
public final class WrapperStore<N extends Number> extends FactoryStore<N> {

    public static AbstractStore<BigDecimal> makeBig(final Access2D<?> anAccess) {
        return new WrapperStore<BigDecimal>(BigDenseStore.FACTORY, anAccess);
    }

    public static AbstractStore<ComplexNumber> makeComplex(final Access2D<?> anAccess) {
        return new WrapperStore<ComplexNumber>(ComplexDenseStore.FACTORY, anAccess);
    }

    public static AbstractStore<Double> makePrimitive(final Access2D<?> anAccess) {
        return new WrapperStore<Double>(PrimitiveDenseStore.FACTORY, anAccess);
    }

    private final Access2D<?> myAccess;

    public WrapperStore(final PhysicalStore.Factory<N, ?> aFactory, final Access2D<?> anAccess) {

        super(anAccess.getRowDim(), anAccess.getColDim(), aFactory);

        myAccess = anAccess;
    }

    @SuppressWarnings("unused")
    private WrapperStore(final PhysicalStore.Factory<N, ?> aFactory) {

        this(aFactory, null);

        ProgrammingError.throwForIllegalInvocation();
    }

    public double doubleValue(final int aRow, final int aCol) {
        return myAccess.doubleValue(aRow, aCol);
    }

    public N get(final int aRow, final int aCol) {
        return this.factory().getNumber(myAccess.get(aRow, aCol));
    }

    public boolean isLowerLeftShaded() {
        return BOOLEAN_FALSE;
    }

    public boolean isUpperRightShaded() {
        return BOOLEAN_FALSE;
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        return this.factory().toScalar(myAccess.get(aRow, aCol));
    }

}
