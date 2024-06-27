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
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.ComplexNumber;

/**
 * [x]<sup>T</sup>[Q][x]
 *
 * @author apete
 */
public final class QuadraticFunction<N extends Number> extends AbstractMultiary<N> {

    public static QuadraticFunction<BigDecimal> makeBig(final Access2D<? extends Number> someParameters) {
        return new QuadraticFunction<BigDecimal>(BigDenseStore.FACTORY.copy(someParameters));
    }

    public static QuadraticFunction<BigDecimal> makeBig(final int aDim) {
        return new QuadraticFunction<BigDecimal>(BigDenseStore.FACTORY.makeZero(aDim, aDim));
    }

    public static QuadraticFunction<ComplexNumber> makeComplex(final Access2D<? extends Number> someParameters) {
        return new QuadraticFunction<ComplexNumber>(ComplexDenseStore.FACTORY.copy(someParameters));
    }

    public static QuadraticFunction<ComplexNumber> makeComplex(final int aDim) {
        return new QuadraticFunction<ComplexNumber>(ComplexDenseStore.FACTORY.makeZero(aDim, aDim));
    }

    public static QuadraticFunction<Double> makePrimitive(final Access2D<? extends Number> someParameters) {
        return new QuadraticFunction<Double>(PrimitiveDenseStore.FACTORY.copy(someParameters));
    }

    public static QuadraticFunction<Double> makePrimitive(final int aDim) {
        return new QuadraticFunction<Double>(PrimitiveDenseStore.FACTORY.makeZero(aDim, aDim));
    }

    private PhysicalStore<N> myFactors;

    @SuppressWarnings("unused")
    private QuadraticFunction() {

        this(null);

        ProgrammingError.throwForIllegalInvocation();
    }

    QuadraticFunction(final PhysicalStore<N> someFactors) {

        super();

        myFactors = someFactors;

        if (myFactors.getRowDim() != myFactors.getColDim()) {
            throw new IllegalArgumentException("Must be sqaure!");
        }
    }

    public int dim() {
        return myFactors.getMinDim();
    }

    public N getFactor(final int aVar1, final int aVar2) {
        return myFactors.get(aVar1, aVar2);
    }

    @Override
    public MatrixStore<N> getGradient(final MatrixStore<N> anArg) {
        return this.getHessian(anArg).multiplyRight(anArg);
    }

    @Override
    public MatrixStore<N> getHessian(final MatrixStore<N> anArg) {
        return myFactors.builder().superimpose(0, 0, myFactors.builder().transpose().build()).build();
    }

    @Override
    public N invoke(final MatrixStore<N> anArg) {
        return myFactors.multiplyRight(anArg).multiplyLeft(anArg.builder().transpose().build()).get(0, 0);
    }

    public void setFactor(final int aVar1, final int aVar2, final N aValue) {
        myFactors.set(aVar1, aVar2, aValue);
    }

    @Override
    protected Factory<N, ?> getFactory() {
        return myFactors.factory();
    }

}
