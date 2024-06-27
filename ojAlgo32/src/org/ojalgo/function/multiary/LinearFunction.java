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
package org.ojalgo.function.multiary;

import java.math.BigDecimal;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.ZeroStore;
import org.ojalgo.scalar.ComplexNumber;

/**
 * [c]<sup>T</sup>[x]
 *
 * @author apete
 */
public final class LinearFunction<N extends Number> extends AbstractMultiary<N> {

    public static LinearFunction<BigDecimal> makeBig(final Access1D<? extends Number> someParameters) {
        return new LinearFunction<BigDecimal>(BigDenseStore.FACTORY.columns(someParameters).transpose());
    }

    public static LinearFunction<BigDecimal> makeBig(final int aDim) {
        return new LinearFunction<BigDecimal>(BigDenseStore.FACTORY.makeZero(1, aDim));
    }

    public static LinearFunction<ComplexNumber> makeComplex(final Access1D<? extends Number> someParameters) {
        return new LinearFunction<ComplexNumber>(ComplexDenseStore.FACTORY.columns(someParameters).transpose());
    }

    public static LinearFunction<ComplexNumber> makeComplex(final int aDim) {
        return new LinearFunction<ComplexNumber>(ComplexDenseStore.FACTORY.makeZero(1, aDim));
    }

    public static LinearFunction<Double> makePrimitive(final Access1D<? extends Number> someParameters) {
        return new LinearFunction<Double>(PrimitiveDenseStore.FACTORY.columns(someParameters).transpose());
    }

    public static LinearFunction<Double> makePrimitive(final int aDim) {
        return new LinearFunction<Double>(PrimitiveDenseStore.FACTORY.makeZero(1, aDim));
    }

    private PhysicalStore<N> myFactors;

    @SuppressWarnings("unused")
    private LinearFunction() {

        this(null);

        ProgrammingError.throwForIllegalInvocation();
    }

    LinearFunction(final PhysicalStore<N> someFactors) {

        super();

        myFactors = someFactors;

        if (myFactors.getRowDim() != 1) {
            throw new IllegalArgumentException("Must be a row vector!");
        }
    }

    public int dim() {
        return myFactors.getColDim();
    }

    public N getFactor(final int aVar) {
        return myFactors.get(0, aVar);
    }

    @Override
    public MatrixStore<N> getGradient(final MatrixStore<N> anArg) {
        return myFactors.builder().transpose().build();
    }

    @Override
    public MatrixStore<N> getHessian(final MatrixStore<N> anArg) {
        return new ZeroStore<N>(this.getFactory(), this.dim(), this.dim());
    }

    @Override
    public N invoke(final MatrixStore<N> anArg) {
        return myFactors.multiplyRight(anArg).get(0, 0);
    }

    public void setFactor(final int aVar, final N aValue) {
        myFactors.set(0, aVar, aValue);
    }

    @Override
    protected Factory<N, ?> getFactory() {
        return myFactors.factory();
    }

}
