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

import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;

/**
 * [x]<sup>T</sup>[Q][x] + [c]<sup>T</sup>[x] + b
 *
 * @author apete
 */
public final class CompoundFunction<N extends Number> extends AbstractMultiary<N> {

    public static CompoundFunction<BigDecimal> makeBig(final Access2D<? extends Number> someQuadParams, final Access1D<? extends Number> someLinearParams) {
        final QuadraticFunction<BigDecimal> tmpQuadratic = QuadraticFunction.makeBig(someQuadParams);
        final LinearFunction<BigDecimal> tmpLinear = LinearFunction.makeBig(someLinearParams);
        return new CompoundFunction<BigDecimal>(tmpQuadratic, tmpLinear);
    }

    public static CompoundFunction<BigDecimal> makeBig(final int aDim) {
        final QuadraticFunction<BigDecimal> tmpQuadratic = QuadraticFunction.makeBig(aDim);
        final LinearFunction<BigDecimal> tmpLinear = LinearFunction.makeBig(aDim);
        return new CompoundFunction<BigDecimal>(tmpQuadratic, tmpLinear);
    }

    public static CompoundFunction<ComplexNumber> makeComplex(final Access2D<? extends Number> someQuadParams, final Access1D<? extends Number> someLinearParams) {
        final QuadraticFunction<ComplexNumber> tmpQuadratic = QuadraticFunction.makeComplex(someQuadParams);
        final LinearFunction<ComplexNumber> tmpLinear = LinearFunction.makeComplex(someLinearParams);
        return new CompoundFunction<ComplexNumber>(tmpQuadratic, tmpLinear);
    }

    public static CompoundFunction<ComplexNumber> makeComplex(final int aDim) {
        final QuadraticFunction<ComplexNumber> tmpQuadratic = QuadraticFunction.makeComplex(aDim);
        final LinearFunction<ComplexNumber> tmpLinear = LinearFunction.makeComplex(aDim);
        return new CompoundFunction<ComplexNumber>(tmpQuadratic, tmpLinear);
    }

    public static CompoundFunction<Double> makePrimitive(final Access2D<? extends Number> someQuadParams, final Access1D<? extends Number> someLinearParams) {
        final QuadraticFunction<Double> tmpQuadratic = QuadraticFunction.makePrimitive(someQuadParams);
        final LinearFunction<Double> tmpLinear = LinearFunction.makePrimitive(someLinearParams);
        return new CompoundFunction<Double>(tmpQuadratic, tmpLinear);
    }

    public static CompoundFunction<Double> makePrimitive(final int aDim) {
        final QuadraticFunction<Double> tmpQuadratic = QuadraticFunction.makePrimitive(aDim);
        final LinearFunction<Double> tmpLinear = LinearFunction.makePrimitive(aDim);
        return new CompoundFunction<Double>(tmpQuadratic, tmpLinear);
    }

    private N myConstant;
    private final LinearFunction<N> myLinear;
    private final QuadraticFunction<N> myQuadratic;

    @SuppressWarnings("unused")
    private CompoundFunction() {
        this((QuadraticFunction<N>) null, (LinearFunction<N>) null);
    }

    CompoundFunction(final QuadraticFunction<N> aQuadratic, final LinearFunction<N> aLinear) {

        super();

        myQuadratic = aQuadratic;
        myLinear = aLinear;

        if (myQuadratic.dim() != myLinear.dim()) {
            throw new IllegalArgumentException("Must have the same dim()!");
        }
    }

    public int dim() {
        return myLinear.dim();
    }

    public N getConstant() {
        return myConstant;
    }

    @Override
    public MatrixStore<N> getGradient(final MatrixStore<N> anArg) {
        return myQuadratic.getGradient(anArg).builder().superimpose(0, 0, myLinear.getGradient(anArg)).build();
    }

    @Override
    public MatrixStore<N> getHessian(final MatrixStore<N> anArg) {
        return myQuadratic.getHessian(anArg);
    }

    public N getLinearFactor(final int aVar) {
        return myLinear.getFactor(aVar);
    }

    public N getQuadraticFactor(final int aVar1, final int aVar2) {
        return myQuadratic.getFactor(aVar1, aVar2);
    }

    @Override
    public N invoke(final MatrixStore<N> anArg) {

        Scalar<N> retVal = this.getFactory().getStaticZero();

        if (myConstant != null) {
            retVal = retVal.add(myConstant);
        }

        retVal = retVal.add(myLinear.invoke(anArg));

        retVal = retVal.add(myQuadratic.invoke(anArg));

        return retVal.getNumber();
    }

    public void setConstant(final N aConstant) {
        myConstant = aConstant;
    }

    public void setLinearFactor(final int aVar, final N aValue) {
        myLinear.setFactor(aVar, aValue);
    }

    public void setQuadraticFactor(final int aVar1, final int aVar2, final N aValue) {
        myQuadratic.setFactor(aVar1, aVar2, aValue);
    }

    @Override
    protected Factory<N, ?> getFactory() {
        return myLinear.getFactory();
    }

}
