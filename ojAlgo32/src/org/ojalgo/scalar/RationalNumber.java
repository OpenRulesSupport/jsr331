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
import java.math.BigInteger;
import java.math.MathContext;

import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.implementation.BigFunction;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

public final class RationalNumber extends AbstractScalar<RationalNumber> {

    public static final RationalNumber NaN = new RationalNumber(BigInteger.ZERO, BigInteger.ZERO);
    public static final RationalNumber NEG = new RationalNumber(BigInteger.ONE.negate(), BigInteger.ONE);
    public static final RationalNumber NEGATIVE_INFINITY = new RationalNumber(BigInteger.ONE.negate(), BigInteger.ZERO);
    public static final RationalNumber ONE = new RationalNumber(BigInteger.ONE, BigInteger.ONE);
    public static final RationalNumber POSITIVE_INFINITY = new RationalNumber(BigInteger.ONE, BigInteger.ZERO);
    public static final RationalNumber TWO = new RationalNumber(BigInteger.ONE.add(BigInteger.ONE), BigInteger.ONE);
    public static final RationalNumber ZERO = new RationalNumber(BigInteger.ZERO, BigInteger.ONE);
    private static final String LEFT = "(";
    private static final String DIVIDE = " / ";
    private static final String RIGHT = ")";

    /**
     * Greatest Common Denominator
     */
    public static BigInteger gcd(final BigInteger aValue1, final BigInteger aValue2) {
        return aValue1.gcd(aValue2);
    }

    /**
     * Greatest Common Denominator
     */
    public static int gcd(int aValue1, int aValue2) {

        int retVal = INT_ONE;

        aValue1 = Math.abs(aValue1);
        aValue2 = Math.abs(aValue2);

        int tmpMax = Math.max(aValue1, aValue2);
        int tmpMin = Math.min(aValue1, aValue2);

        while (tmpMin != INT_ZERO) {
            retVal = tmpMin;
            tmpMin = tmpMax % tmpMin;
            tmpMax = retVal;
        }

        return retVal;
    }

    /**
     * Greatest Common Denominator
     */
    public static long gcd(long aValue1, long aValue2) {

        long retVal = LONG_ONE;

        aValue1 = Math.abs(aValue1);
        aValue2 = Math.abs(aValue2);

        long tmpMax = Math.max(aValue1, aValue2);
        long tmpMin = Math.min(aValue1, aValue2);

        while (tmpMin != LONG_ZERO) {
            retVal = tmpMin;
            tmpMin = tmpMax % tmpMin;
            tmpMax = retVal;
        }

        return retVal;
    }

    private static String toString(final RationalNumber aNmbr) {

        final StringBuilder retVal = new StringBuilder(LEFT);

        retVal.append(aNmbr.getNumerator());
        retVal.append(DIVIDE);
        retVal.append(aNmbr.getDenominator());

        return retVal.append(RIGHT).toString();
    }

    private transient BigDecimal myDecimal = null;

    private final BigInteger myDenominator;

    private final BigInteger myNumerator;

    public RationalNumber(final BigDecimal aNmbr) {

        super();

        final int tmpScale = aNmbr.scale();

        if (tmpScale < INT_ZERO) {

            myNumerator = aNmbr.unscaledValue().multiply(BigInteger.TEN.pow(-tmpScale));
            myDenominator = BigInteger.ONE;

        } else {

            final BigInteger tmpNumer = aNmbr.unscaledValue();
            final BigInteger tmpDenom = BigInteger.TEN.pow(tmpScale);

            final BigInteger tmpGCD = tmpNumer.gcd(tmpDenom);

            if (tmpGCD.compareTo(BigInteger.ONE) == INT_ONE) {
                myNumerator = tmpNumer.divide(tmpGCD);
                myDenominator = tmpDenom.divide(tmpGCD);
            } else {
                myNumerator = tmpNumer;
                myDenominator = tmpDenom;
            }
        }
    }

    public RationalNumber(final double aNmbr) {
        this(new BigDecimal(aNmbr, MathContext.DECIMAL64));
    }

    public RationalNumber(final int aNumerator, final int aDenominator) {

        super();

        final long tmpGCD = RationalNumber.gcd(aNumerator, aDenominator);

        if (tmpGCD > INT_ONE) {
            myNumerator = BigInteger.valueOf(aNumerator / tmpGCD);
            myDenominator = BigInteger.valueOf(aDenominator / tmpGCD);
        } else {
            myNumerator = BigInteger.valueOf(aNumerator);
            myDenominator = BigInteger.valueOf(aDenominator);
        }
    }

    public RationalNumber(final long aNumerator, final long aDenominator) {

        super();

        final long tmpGCD = RationalNumber.gcd(aNumerator, aDenominator);

        if (tmpGCD > LONG_ONE) {
            myNumerator = BigInteger.valueOf(aNumerator / tmpGCD);
            myDenominator = BigInteger.valueOf(aDenominator / tmpGCD);
        } else {
            myNumerator = BigInteger.valueOf(aNumerator);
            myDenominator = BigInteger.valueOf(aDenominator);
        }
    }

    RationalNumber() {

        super();

        myNumerator = BigInteger.ZERO;
        myDenominator = BigInteger.ONE;
    }

    RationalNumber(final BigInteger aNumerator, final BigInteger aDenominator) {

        super();

        myNumerator = aNumerator;
        myDenominator = aDenominator;
    }

    public RationalNumber add(final double aNmbr) {
        return this.add(new RationalNumber(aNmbr));
    }

    public RationalNumber add(final RationalNumber aNmbr) {

        if (myDenominator.equals(aNmbr.getDenominator())) {

            return new RationalNumber(myNumerator.add(aNmbr.getNumerator()), myDenominator);

        } else {

            final BigInteger tmpNumer = myNumerator.multiply(aNmbr.getDenominator()).add(aNmbr.getNumerator().multiply(myDenominator));
            final BigInteger tmpDenom = myDenominator.multiply(aNmbr.getDenominator());

            final BigInteger tmpGCD = tmpNumer.gcd(tmpDenom);

            if (tmpGCD.compareTo(BigInteger.ONE) == INT_ONE) {
                return new RationalNumber(tmpNumer.divide(tmpGCD), tmpDenom.divide(tmpGCD));
            } else {
                return new RationalNumber(tmpNumer, tmpDenom);
            }
        }
    }

    public int compareTo(final RationalNumber aNmbr) {
        return this.subtract(aNmbr).toBigDecimal().compareTo(BigMath.ZERO);
    }

    public RationalNumber conjugate() {
        return this;
    }

    public RationalNumber divide(final double aNmbr) {
        return this.divide(new RationalNumber(aNmbr));
    }

    public RationalNumber divide(final RationalNumber aNmbr) {

        if (myNumerator.equals(aNmbr.getNumerator())) {

            return new RationalNumber(aNmbr.getDenominator(), myDenominator);

        } else if (myDenominator.equals(aNmbr.getDenominator())) {

            return new RationalNumber(myNumerator, aNmbr.getNumerator());

        } else {

            final BigInteger tmpNumer = myNumerator.multiply(aNmbr.getDenominator());
            final BigInteger tmpDenom = myDenominator.multiply(aNmbr.getNumerator());

            final BigInteger tmpGCD = tmpNumer.gcd(tmpDenom);

            if (tmpGCD.compareTo(BigInteger.ONE) == INT_ONE) {
                return new RationalNumber(tmpNumer.divide(tmpGCD), tmpDenom.divide(tmpGCD));
            } else {
                return new RationalNumber(tmpNumer, tmpDenom);
            }
        }
    }

    @Override
    public double doubleValue() {
        return this.toBigDecimal().doubleValue();
    }

    public RationalNumber enforce(final NumberContext aCntxt) {
        return new RationalNumber(aCntxt.enforce(this.toBigDecimal()));
    }

    public boolean equals(final Scalar<?> aSclr) {
        return this.toBigDecimal().compareTo(aSclr.toBigDecimal()) == INT_ZERO;
    }

    @Override
    public float floatValue() {
        return this.toBigDecimal().floatValue();
    }

    public double getArgument() {
        return this.sign() == INT_NEG ? PrimitiveMath.PI : PrimitiveMath.ZERO;
    }

    public double getImaginary() {
        return PrimitiveMath.ZERO;
    }

    public double getModulus() {
        return Math.abs(this.doubleValue());
    }

    public RationalNumber getNumber() {
        return this;
    }

    public double getReal() {
        return this.doubleValue();
    }

    @Override
    public int intValue() {
        return this.toBigDecimal().intValue();
    }

    public RationalNumber invert() {
        return new RationalNumber(myDenominator, myNumerator);
    }

    public boolean isAbsolute() {
        return this.isPositive() || this.isZero();
    }

    public boolean isInfinite() {
        return ((myNumerator.signum() != INT_ZERO) && (myDenominator.signum() == INT_ZERO));
    }

    public boolean isNaN() {
        return ((myNumerator.signum() == INT_ZERO) && (myDenominator.signum() == INT_ZERO));
    }

    public boolean isPositive() {
        return this.sign() == INT_ONE;
    }

    public boolean isReal() {
        return BOOLEAN_TRUE;
    }

    public boolean isZero() {
        return TypeUtils.isZero(this.toBigDecimal().doubleValue());
    }

    @Override
    public long longValue() {
        return this.toBigDecimal().longValue();
    }

    public RationalNumber multiply(final double aNmbr) {
        return this.multiply(new RationalNumber(aNmbr));
    }

    public RationalNumber multiply(final RationalNumber aNmbr) {

        if (myNumerator.equals(aNmbr.getDenominator())) {

            return new RationalNumber(aNmbr.getNumerator(), myDenominator);

        } else if (myDenominator.equals(aNmbr.getNumerator())) {

            return new RationalNumber(myNumerator, aNmbr.getDenominator());

        } else {

            final BigInteger tmpNumer = myNumerator.multiply(aNmbr.getNumerator());
            final BigInteger tmpDenom = myDenominator.multiply(aNmbr.getDenominator());

            final BigInteger tmpGCD = tmpNumer.gcd(tmpDenom);

            if (tmpGCD.compareTo(BigInteger.ONE) == INT_ONE) {
                return new RationalNumber(tmpNumer.divide(tmpGCD), tmpDenom.divide(tmpGCD));
            } else {
                return new RationalNumber(tmpNumer, tmpDenom);
            }
        }
    }

    public RationalNumber negate() {
        return new RationalNumber(myNumerator.negate(), myDenominator);
    }

    public RationalNumber power(final int anExp) {

        final boolean tmpNegative = anExp < INT_ZERO;
        final int tmpExponent = tmpNegative ? -anExp : anExp;

        final BigInteger tmpNumer = tmpNegative ? myDenominator.pow(tmpExponent) : myNumerator.pow(tmpExponent);
        final BigInteger tmpDenom = tmpNegative ? myNumerator.pow(tmpExponent) : myDenominator.pow(tmpExponent);

        final BigInteger tmpGCD = tmpNumer.gcd(tmpDenom);

        if (tmpGCD.compareTo(BigInteger.ONE) == INT_ONE) {
            return new RationalNumber(tmpNumer.divide(tmpGCD), tmpDenom.divide(tmpGCD));
        } else {
            return new RationalNumber(tmpNumer, tmpDenom);
        }
    }

    public RationalNumber root(final int anExp) {
        return new RationalNumber(BigFunction.ROOT.invoke(this.toBigDecimal(), anExp));
    }

    public RationalNumber round(final NumberContext aCntxt) {
        return new RationalNumber(aCntxt.round(this.toBigDecimal()));
    }

    public RationalNumber signum() {
        if (this.isZero()) {
            return ZERO;
        } else if (this.sign() == INT_NEG) {
            return ONE.negate();
        } else {
            return ONE;
        }
    }

    public RationalNumber subtract(final double aNmbr) {
        return this.subtract(new RationalNumber(aNmbr));
    }

    public RationalNumber subtract(final RationalNumber aNmbr) {

        if (myDenominator.equals(aNmbr.getDenominator())) {

            return new RationalNumber(myNumerator.subtract(aNmbr.getNumerator()), myDenominator);

        } else {

            final BigInteger tmpNumer = myNumerator.multiply(aNmbr.getDenominator()).subtract(aNmbr.getNumerator().multiply(myDenominator));
            final BigInteger tmpDenom = myDenominator.multiply(aNmbr.getDenominator());

            final BigInteger tmpGCD = tmpNumer.gcd(tmpDenom);

            if (tmpGCD.compareTo(BigInteger.ONE) == INT_ONE) {
                return new RationalNumber(tmpNumer.divide(tmpGCD), tmpDenom.divide(tmpGCD));
            } else {
                return new RationalNumber(tmpNumer, tmpDenom);
            }
        }
    }

    public BigDecimal toBigDecimal() {

        if (myDecimal == null) {
            myDecimal = new BigDecimal(myNumerator).divide(new BigDecimal(myDenominator), MathContext.DECIMAL128);
        }

        return myDecimal;
    }

    public ComplexNumber toComplexNumber() {
        return new ComplexNumber(this.doubleValue());
    }

    public String toPlainString(final NumberContext aCntxt) {
        return aCntxt.enforce(this.toBigDecimal()).toPlainString();
    }

    public RationalNumber toRationalNumber() {
        return this;
    }

    @Override
    public String toString() {
        return RationalNumber.toString(this);
    }

    public String toString(final NumberContext aCntxt) {
        return RationalNumber.toString(this.enforce(aCntxt));
    }

    private BigInteger getDenominator() {
        return myDenominator;
    }

    private BigInteger getNumerator() {
        return myNumerator;
    }

    private int sign() {
        return myNumerator.signum() * myDenominator.signum();
    }

}
