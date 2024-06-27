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

import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.implementation.BigFunction;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

public final class BigScalar extends AbstractScalar<BigDecimal> {

    public static final BigScalar ONE = new BigScalar(BigMath.ONE);
    public static final BigScalar ZERO = new BigScalar();
    private static final MathContext MATH = MathContext.DECIMAL128;

    private final BigDecimal myNumber;

    public BigScalar(final BigDecimal aNmbr) {

        super();

        myNumber = aNmbr;
    }

    public BigScalar(final Number aNmbr) {

        super();

        myNumber = TypeUtils.toBigDecimal(aNmbr);
    }

    private BigScalar() {

        super();

        myNumber = BigMath.ZERO;
    }

    public BigScalar add(final BigDecimal aNmbr) {
        return new BigScalar(myNumber.add(aNmbr));
    }

    public BigScalar add(final double aNmbr) {
        return this.add(new BigDecimal(aNmbr));
    }

    public int compareTo(final BigDecimal aNmbr) {
        return myNumber.compareTo(aNmbr);
    }

    public BigScalar conjugate() {
        return this;
    }

    public BigScalar divide(final BigDecimal aNmbr) {
        return new BigScalar(myNumber.divide(aNmbr, MATH));
    }

    public Scalar<BigDecimal> divide(final double aNmbr) {
        return this.divide(new BigDecimal(aNmbr));
    }

    @Override
    public double doubleValue() {
        return myNumber.doubleValue();
    }

    public BigScalar enforce(final NumberContext aCntxt) {
        return new BigScalar(aCntxt.enforce(myNumber));
    }

    public boolean equals(final Scalar<?> aSclr) {
        return myNumber.compareTo(aSclr.toBigDecimal()) == INT_ZERO;
    }

    @Override
    public float floatValue() {
        return myNumber.floatValue();
    }

    public double getArgument() {
        return myNumber.signum() == INT_NEG ? PrimitiveMath.PI : PrimitiveMath.ZERO;
    }

    public double getImaginary() {
        return PrimitiveMath.ZERO;
    }

    public double getModulus() {
        return myNumber.abs().doubleValue();
    }

    public BigDecimal getNumber() {
        return myNumber;
    }

    public double getReal() {
        return myNumber.doubleValue();
    }

    @Override
    public int intValue() {
        return myNumber.intValueExact();
    }

    public BigScalar invert() {
        return ONE.divide(myNumber);
    }

    public boolean isAbsolute() {
        return myNumber.compareTo(BigMath.ZERO) != INT_NEG;
    }

    public boolean isInfinite() {
        return BOOLEAN_FALSE;
    }

    public boolean isNaN() {
        return BOOLEAN_FALSE;
    }

    public boolean isPositive() {
        return myNumber.compareTo(BigMath.ZERO) == INT_ONE;
    }

    public boolean isReal() {
        return BOOLEAN_TRUE;
    }

    public boolean isZero() {
        return TypeUtils.isZero(myNumber.doubleValue());
    }

    @Override
    public long longValue() {
        return myNumber.longValueExact();
    }

    public BigScalar multiply(final BigDecimal aNmbr) {
        return new BigScalar(myNumber.multiply(aNmbr));
    }

    public Scalar<BigDecimal> multiply(final double aNmbr) {
        return this.multiply(new BigDecimal(aNmbr));
    }

    public BigScalar negate() {
        return new BigScalar(myNumber.negate());
    }

    public BigScalar power(final int anExp) {
        return new BigScalar(BigFunction.POWER.invoke(myNumber, anExp));
    }

    public BigScalar root(final int anExp) {
        return new BigScalar(BigFunction.ROOT.invoke(myNumber, anExp));
    }

    public Scalar<BigDecimal> round(final NumberContext aCntxt) {
        return new BigScalar(aCntxt.round(myNumber));
    }

    public BigScalar signum() {
        return new BigScalar(BigFunction.SIGNUM.invoke(myNumber));
    }

    public BigScalar subtract(final BigDecimal aNmbr) {
        return new BigScalar(myNumber.subtract(aNmbr));
    }

    public Scalar<BigDecimal> subtract(final double aNmbr) {
        return this.subtract(new BigDecimal(aNmbr));
    }

    public BigDecimal toBigDecimal() {
        return myNumber;
    }

    public ComplexNumber toComplexNumber() {
        return new ComplexNumber(myNumber.doubleValue());
    }

    public String toPlainString(final NumberContext aCntxt) {
        return aCntxt.enforce(myNumber).toPlainString();
    }

    public RationalNumber toRationalNumber() {
        return new RationalNumber(myNumber);
    }

    @Override
    public String toString() {
        return myNumber.toString();
    }

    public String toString(final NumberContext aCntxt) {
        return aCntxt.enforce(myNumber).toString();
    }
}
