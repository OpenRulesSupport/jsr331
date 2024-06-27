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
import org.ojalgo.function.implementation.ComplexFunction;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * ComplexNumber is an immutable complex number class. It only
 * implements the most basic complex number operations.
 * {@linkplain org.ojalgo.function.implementation.ComplexFunction}
 * implements some of the more complicated ones.
 * 
 * @author apete
 * @see org.ojalgo.function.implementation.ComplexFunction
 */
public final class ComplexNumber extends AbstractScalar<ComplexNumber> {

    public static final ComplexNumber I = ComplexNumber.makeRectangular(PrimitiveMath.ZERO, PrimitiveMath.ONE);
    public static final ComplexNumber INFINITY = ComplexNumber.makePolar(Double.POSITIVE_INFINITY, PrimitiveMath.ZERO);
    public static final ComplexNumber NEG = ComplexNumber.makeRectangular(PrimitiveMath.NEG, PrimitiveMath.ZERO);
    public static final ComplexNumber ONE = ComplexNumber.makeRectangular(PrimitiveMath.ONE, PrimitiveMath.ZERO);
    public static final ComplexNumber TWO = ComplexNumber.makeRectangular(PrimitiveMath.TWO, PrimitiveMath.ZERO);
    public static final ComplexNumber ZERO = ComplexNumber.makeRectangular(PrimitiveMath.ZERO, PrimitiveMath.ZERO);
    private static final String LEFT = "(";
    private static final String MINUS = " - ";
    private static final String PLUS = " + ";
    private static final String RIGHT = "i)";

    public static ComplexNumber makePolar(final double modulus, final double argument) {
        return new ComplexNumber(PrimitiveMath.NaN, PrimitiveMath.NaN, modulus, argument);
    }

    public static ComplexNumber makeRectangular(final double real, final double imaginary) {
        return new ComplexNumber(real, imaginary, PrimitiveMath.NaN, PrimitiveMath.NaN);
    }

    private double myArg;
    private double myIm;
    private double myMod;
    private double myRe;

    public ComplexNumber(final double aRealValue) {
        this(aRealValue, PrimitiveMath.ZERO, PrimitiveMath.NaN, PrimitiveMath.NaN);
    }

    @SuppressWarnings("unused")
    private ComplexNumber() {
        this(PrimitiveMath.NaN, PrimitiveMath.NaN, PrimitiveMath.NaN, PrimitiveMath.NaN);
    }

    ComplexNumber(final double real, final double imaginary, final double modulus, final double argument) {

        super();

        myRe = real;
        myIm = imaginary;
        myMod = modulus;
        myArg = argument;
    }

    public ComplexNumber add(final ComplexNumber aNumber) {

        final double retRe = this.getReal() + aNumber.getReal();
        final double retIm = this.getImaginary() + aNumber.getImaginary();

        return ComplexNumber.makeRectangular(retRe, retIm);
    }

    public ComplexNumber add(final double aValue) {

        final double retRe = this.getReal() + aValue;
        final double retIm = this.getImaginary();

        return ComplexNumber.makeRectangular(retRe, retIm);
    }

    public int compareTo(final ComplexNumber aNmbr) {

        final double tmpMod1 = this.getModulus();
        final double tmpMod2 = aNmbr.getModulus();

        if (tmpMod1 == tmpMod2) {

            final double tmpRe1 = this.getReal();
            final double tmpRe2 = aNmbr.getReal();

            if (tmpRe1 == tmpRe2) {

                final double tmpIm1 = this.getImaginary();
                final double tmpIm2 = aNmbr.getImaginary();

                if (tmpIm1 == tmpIm2) {

                    return 0;

                } else {

                    return Double.compare(tmpIm1, tmpIm2);
                }

            } else {

                return Double.compare(tmpRe1, tmpRe2);
            }

        } else {

            return Double.compare(tmpMod1, tmpMod2);
        }
    }

    public ComplexNumber conjugate() {

        final double retRe = this.getReal();
        final double retIm = -this.getImaginary();

        return ComplexNumber.makeRectangular(retRe, retIm);
    }

    public ComplexNumber divide(final ComplexNumber aNumber) {

        if (this.getModulus() == aNumber.getModulus()) {

            final double retMod = PrimitiveMath.ONE;
            final double retArg = this.getArgument() - aNumber.getArgument();

            return ComplexNumber.makePolar(retMod, retArg);

        } else if (aNumber.isReal() && (aNumber.getReal() != PrimitiveMath.ZERO)) {

            return this.divide(aNumber.getReal());

        } else if (this.isReal() && (this.getReal() != PrimitiveMath.ZERO)) {

            return aNumber.invert().multiply(this.getReal());

        } else {

            final double retMod = this.getModulus() / aNumber.getModulus();
            final double retArg = this.getArgument() - aNumber.getArgument();

            return ComplexNumber.makePolar(retMod, retArg);
        }
    }

    public ComplexNumber divide(final double aValue) {

        final double retRe = this.getReal() / aValue;
        final double retIm = this.getImaginary() / aValue;

        return ComplexNumber.makeRectangular(retRe, retIm);
    }

    /**
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        if (this.getReal() < PrimitiveMath.ZERO) {
            return -this.getModulus();
        } else {
            return this.getModulus();
        }
    }

    /**
     * Will call {@linkplain NumberContext#enforce(double)} on the real
     * and imaginary parts separately.
     * 
     * @see org.ojalgo.scalar.Scalar#enforce(org.ojalgo.type.context.NumberContext)
     */
    public ComplexNumber enforce(final NumberContext aCntxt) {

        final double tmpRe = aCntxt.enforce(this.getReal());
        final double tmpIm = aCntxt.enforce(this.getImaginary());

        return ComplexNumber.makeRectangular(tmpRe, tmpIm);
    }

    public boolean equals(final Scalar<?> aSclr) {
        return TypeUtils.isZero(this.getReal() - aSclr.getReal()) && TypeUtils.isZero(this.getImaginary() - aSclr.getImaginary());
    }

    /**
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        return (float) this.doubleValue();
    }

    public double getArgument() {

        if (Double.isNaN(myArg)) {
            myArg = Math.atan2(myIm, myRe);
        }

        return myArg;
    }

    public double getImaginary() {

        if (Double.isNaN(myIm)) {

            myIm = PrimitiveMath.ZERO;

            if (myMod != PrimitiveMath.ZERO) {

                final double tmpSin = Math.sin(myArg);

                if (tmpSin != PrimitiveMath.ZERO) {
                    myIm = myMod * tmpSin;
                }
            }
        }

        return myIm;
    }

    public double getModulus() {

        if (Double.isNaN(myMod)) {
            myMod = Math.hypot(myRe, myIm);
        }

        return myMod;
    }

    public ComplexNumber getNumber() {
        return this;
    }

    public double getReal() {

        if (Double.isNaN(myRe)) {

            myRe = PrimitiveMath.ZERO;

            if (myMod != PrimitiveMath.ZERO) {

                final double tmpCos = Math.cos(myArg);

                if (tmpCos != PrimitiveMath.ZERO) {
                    myRe = myMod * tmpCos;
                }
            }
        }

        return myRe;
    }

    /**
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return (int) this.doubleValue();
    }

    public ComplexNumber invert() {

        final double retMod = PrimitiveMath.ONE / this.getModulus();
        final double retArg = -this.getArgument();

        return ComplexNumber.makePolar(retMod, retArg);
    }

    public boolean isAbsolute() {
        return this.isReal() && (this.getReal() >= PrimitiveMath.ZERO);
    }

    public boolean isInfinite() {
        return (!Double.isNaN(myMod) && Double.isInfinite(myMod)) || (Double.isInfinite(myRe) || Double.isInfinite(myIm));
    }

    public boolean isNaN() {
        return (Double.isNaN(myRe) || Double.isNaN(myIm)) && (Double.isNaN(myMod) || Double.isNaN(myArg));
    }

    public boolean isPositive() {
        return this.isReal() && !this.isZero() && (this.getReal() > PrimitiveMath.ZERO);
    }

    public boolean isReal() {

        final double tmpTolerance = this.getModulus() * PrimitiveMath.MACHINE_DOUBLE_ERROR;
        final double tmpImaginary = this.getImaginary();

        return TypeUtils.isZero(tmpImaginary, tmpTolerance);
    }

    public boolean isZero() {
        return TypeUtils.isZero(this.getModulus());
    }

    /**
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return (long) this.doubleValue();
    }

    public ComplexNumber multiply(final ComplexNumber aNumber) {

        final boolean tmpThisReal = this.isReal();
        final boolean tmpThatReal = aNumber.isReal();

        if (tmpThisReal) {

            if (tmpThatReal) {
                return new ComplexNumber(this.getReal() * aNumber.getReal());
            } else {
                return aNumber.multiply(this.getReal());
            }

        } else if (tmpThatReal) {

            if (tmpThisReal) {
                return new ComplexNumber(this.getReal() * aNumber.getReal());
            } else {
                return this.multiply(aNumber.getReal());
            }

        } else {

            final double retMod = this.getModulus() * aNumber.getModulus();
            final double retArg = this.getArgument() + aNumber.getArgument();

            return ComplexNumber.makePolar(retMod, retArg);
        }
    }

    public ComplexNumber multiply(final double aValue) {

        final double retRe = this.getReal() * aValue;
        final double retIm = this.getImaginary() * aValue;

        return ComplexNumber.makeRectangular(retRe, retIm);
    }

    public ComplexNumber negate() {

        final double retRe = -this.getReal();
        final double retIm = -this.getImaginary();

        return ComplexNumber.makeRectangular(retRe, retIm);
    }

    public Scalar<ComplexNumber> power(final int anExp) {
        return ComplexFunction.POWER.invoke(this, anExp);
    }

    public ComplexNumber root(final int anExp) {
        return ComplexFunction.ROOT.invoke(this, anExp);
    }

    /**
     * Will call {@linkplain NumberContext#round(double)} on the real
     * and imaginary parts separately.
     * 
     * @see org.ojalgo.scalar.Scalar#enforce(org.ojalgo.type.context.NumberContext)
     */
    public ComplexNumber round(final NumberContext aCntxt) {

        final double tmpRe = aCntxt.round(this.getReal());
        final double tmpIm = aCntxt.round(this.getImaginary());

        return ComplexNumber.makeRectangular(tmpRe, tmpIm);
    }

    public ComplexNumber signum() {
        if (this.isZero()) {
            return ComplexNumber.makePolar(PrimitiveMath.ONE, PrimitiveMath.ZERO);
        } else {
            return ComplexNumber.makePolar(PrimitiveMath.ONE, this.getArgument());
        }
    }

    public ComplexNumber subtract(final ComplexNumber aNumber) {

        final double retRe = this.getReal() - aNumber.getReal();
        final double retIm = this.getImaginary() - aNumber.getImaginary();

        return ComplexNumber.makeRectangular(retRe, retIm);
    }

    public ComplexNumber subtract(final double aValue) {

        final double retRe = this.getReal() - aValue;
        final double retIm = this.getImaginary();

        return ComplexNumber.makeRectangular(retRe, retIm);
    }

    public BigDecimal toBigDecimal() {
        return new BigDecimal(this.doubleValue(), MathContext.DECIMAL64);
    }

    public ComplexNumber toComplexNumber() {
        return this;
    }

    public String toPlainString(final NumberContext aCntxt) {
        return aCntxt.enforce(this.toBigDecimal()).toPlainString();
    }

    public RationalNumber toRationalNumber() {
        return new RationalNumber(this.toBigDecimal());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        final StringBuilder retVal = new StringBuilder(LEFT);

        final double tmpRe = this.getReal();
        final double tmpIm = this.getImaginary();

        retVal.append(Double.toString(tmpRe));

        if (tmpIm < PrimitiveMath.ZERO) {
            retVal.append(MINUS);
        } else {
            retVal.append(PLUS);
        }
        retVal.append(Double.toString(Math.abs(tmpIm)));

        return retVal.append(RIGHT).toString();
    }

    public String toString(final NumberContext aCntxt) {

        final StringBuilder retVal = new StringBuilder(LEFT);

        final BigDecimal tmpRe = aCntxt.enforce(new BigDecimal(this.getReal(), MathContext.DECIMAL64));
        final BigDecimal tmpIm = aCntxt.enforce(new BigDecimal(this.getImaginary(), MathContext.DECIMAL64));

        retVal.append(tmpRe.toString());

        if (tmpIm.signum() < 0) {
            retVal.append(MINUS);
        } else {
            retVal.append(PLUS);
        }
        retVal.append(tmpIm.abs().toString());

        return retVal.append(RIGHT).toString();
    }

}
