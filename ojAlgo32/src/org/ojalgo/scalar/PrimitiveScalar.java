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
package org.ojalgo.scalar;

import java.math.BigDecimal;
import java.math.MathContext;

import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

public final class PrimitiveScalar extends AbstractScalar<Double> {

    public static final PrimitiveScalar NaN = new PrimitiveScalar(PrimitiveMath.NaN);
    public static final PrimitiveScalar NEGATIVE_INFINITY = new PrimitiveScalar(PrimitiveMath.NEGATIVE_INFINITY);
    public static final PrimitiveScalar ONE = new PrimitiveScalar(PrimitiveMath.ONE);
    public static final PrimitiveScalar POSITIVE_INFINITY = new PrimitiveScalar(PrimitiveMath.POSITIVE_INFINITY);
    public static final PrimitiveScalar ZERO = new PrimitiveScalar(PrimitiveMath.ZERO);
    private final double myValue;

    public PrimitiveScalar(final double aVal) {

        super();

        myValue = aVal;
    }

    public PrimitiveScalar(final Number aNmbr) {

        super();

        myValue = aNmbr.doubleValue();
    }

    @SuppressWarnings("unused")
    private PrimitiveScalar() {

        super();

        myValue = PrimitiveMath.ZERO;
    }

    public Scalar<Double> add(final double aNmbr) {
        return new PrimitiveScalar(myValue + aNmbr);
    }

    public PrimitiveScalar add(final Double aNmbr) {
        return new PrimitiveScalar(myValue + aNmbr);
    }

    public int compareTo(final Double aNmbr) {
        return Double.compare(myValue, aNmbr);
    }

    public PrimitiveScalar conjugate() {
        return this;
    }

    public PrimitiveScalar divide(final double aNmbr) {
        return new PrimitiveScalar(myValue / aNmbr);
    }

    public PrimitiveScalar divide(final Double aNmbr) {
        return new PrimitiveScalar(myValue / aNmbr);
    }

    @Override
    public double doubleValue() {
        return myValue;
    }

    public PrimitiveScalar enforce(final NumberContext aCntxt) {
        return new PrimitiveScalar(aCntxt.enforce(myValue));
    }

    public boolean equals(final Scalar<?> aSclr) {
        return TypeUtils.isZero(myValue - aSclr.getReal());
    }

    @Override
    public float floatValue() {
        return (float) myValue;
    }

    public double getArgument() {
        return myValue < PrimitiveMath.ZERO ? PrimitiveMath.PI : PrimitiveMath.ZERO;
    }

    public double getImaginary() {
        return PrimitiveMath.ZERO;
    }

    public double getModulus() {
        return Math.abs(myValue);
    }

    public Double getNumber() {
        return myValue;
    }

    public double getReal() {
        return myValue;
    }

    @Override
    public int intValue() {
        return (int) myValue;
    }

    public PrimitiveScalar invert() {
        return new PrimitiveScalar(PrimitiveMath.ONE / myValue);
    }

    public boolean isAbsolute() {
        return (this.getReal() >= PrimitiveMath.ZERO);
    }

    public boolean isInfinite() {
        return Double.isInfinite(myValue);
    }

    public boolean isNaN() {
        return Double.isNaN(myValue);
    }

    public boolean isPositive() {
        return (myValue > PrimitiveMath.ZERO) && !TypeUtils.isZero(myValue);
    }

    public boolean isReal() {
        return BOOLEAN_TRUE;
    }

    public boolean isZero() {
        return TypeUtils.isZero(myValue);
    }

    @Override
    public long longValue() {
        return (long) myValue;
    }

    public PrimitiveScalar multiply(final double aNmbr) {
        return new PrimitiveScalar(myValue * aNmbr);
    }

    public PrimitiveScalar multiply(final Double aNmbr) {
        return new PrimitiveScalar(myValue * aNmbr);
    }

    public PrimitiveScalar negate() {
        return new PrimitiveScalar(-myValue);
    }

    public PrimitiveScalar power(final int anExp) {
        return new PrimitiveScalar(PrimitiveFunction.POWER.invoke(myValue, anExp));
    }

    public PrimitiveScalar root(final int anExp) {
        return new PrimitiveScalar(PrimitiveFunction.ROOT.invoke(myValue, anExp));
    }

    public PrimitiveScalar round(final NumberContext aCntxt) {
        return new PrimitiveScalar(aCntxt.round(myValue));
    }

    public PrimitiveScalar signum() {
        return new PrimitiveScalar(Math.signum(myValue));
    }

    public PrimitiveScalar subtract(final double aNmbr) {
        return new PrimitiveScalar(myValue - aNmbr);
    }

    public PrimitiveScalar subtract(final Double aNmbr) {
        return new PrimitiveScalar(myValue - aNmbr);
    }

    public BigDecimal toBigDecimal() {
        return new BigDecimal(myValue, MathContext.DECIMAL64);
    }

    public ComplexNumber toComplexNumber() {
        return new ComplexNumber(myValue);
    }

    public String toPlainString(final NumberContext aCntxt) {
        return aCntxt.enforce(this.toBigDecimal()).toPlainString();
    }

    public RationalNumber toRationalNumber() {
        return new RationalNumber(this.toBigDecimal());
    }

    @Override
    public String toString() {
        return Double.toString(myValue);
    }

    public String toString(final NumberContext aCntxt) {
        return aCntxt.enforce(this.toBigDecimal()).toString();
    }

}
