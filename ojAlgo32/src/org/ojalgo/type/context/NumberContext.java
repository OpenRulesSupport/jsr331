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
package org.ojalgo.type.context;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.type.format.NumberStyle;

/**
 * <p>
 * Think of this as a {@linkplain MathContext} that specifies both precision and
 * scale. Numeric data types (non-integers) in databases are specified using
 * precison and scale. While doing maths the precision is all that matters, but
 * before sending a number to a database, or printing/displaying it, rounding to
 * a specified scale is desireable.
 * </p><p>
 * The enforce methods first enforce the precision and then set the scale.
 * It is possible that this will create a number with trailing zeros and more
 * digits than the precision allows. It is also possible to define a context
 * with a scale that is larger than the precision. This is NOT how precision and
 * scale is used with numeric types in databases.
 * </p>
 *
 * @author apete
 */
public final class NumberContext extends TypeContext<Number> {

    public static interface FormatPattern {

        String toLocalizedPattern();

        String toPattern();

    }

    private static final MathContext DEFAULT_MATH = MathContext.DECIMAL64;

    private static final NumberStyle DEFAULT_STYLE = NumberStyle.GENERAL;

    public static NumberContext getCurrency(final Locale aLocale) {

        final NumberFormat tmpFormat = NumberStyle.CURRENCY.getFormat(aLocale);
        final int tmpPrecision = DEFAULT_MATH.getPrecision();
        final int tmpScale = 2;
        final RoundingMode tmpRoundingMode = DEFAULT_MATH.getRoundingMode();

        return new NumberContext(tmpFormat, tmpPrecision, tmpScale, tmpRoundingMode);
    }

    public static NumberContext getGeneral(final int aScale) {

        final NumberFormat tmpFormat = NumberStyle.GENERAL.getFormat();
        final int tmpPrecision = DEFAULT_MATH.getPrecision();
        final int tmpScale = aScale;
        final RoundingMode tmpRoundingMode = DEFAULT_MATH.getRoundingMode();

        return new NumberContext(tmpFormat, tmpPrecision, tmpScale, tmpRoundingMode);
    }

    public static NumberContext getGeneral(final int aPrecision, final int aScale) {

        final NumberFormat tmpFormat = NumberStyle.GENERAL.getFormat();
        final int tmpPrecision = aPrecision;
        final int tmpScale = aScale;
        final RoundingMode tmpRoundingMode = DEFAULT_MATH.getRoundingMode();

        return new NumberContext(tmpFormat, tmpPrecision, tmpScale, tmpRoundingMode);
    }

    public static NumberContext getGeneral(final int aScale, final RoundingMode aRoundingMode) {

        final NumberFormat tmpFormat = NumberStyle.GENERAL.getFormat();
        final int tmpPrecision = DEFAULT_MATH.getPrecision();
        final int tmpScale = aScale;
        final RoundingMode tmpRoundingMode = aRoundingMode;

        return new NumberContext(tmpFormat, tmpPrecision, tmpScale, tmpRoundingMode);
    }

    public static NumberContext getGeneral(final MathContext aContext) {

        final NumberFormat tmpFormat = NumberStyle.GENERAL.getFormat();
        final int tmpPrecision = aContext.getPrecision();
        final int tmpScale = tmpPrecision / 2;
        final RoundingMode tmpRoundingMode = aContext.getRoundingMode();

        return new NumberContext(tmpFormat, tmpPrecision, tmpScale, tmpRoundingMode);
    }

    public static NumberContext getInteger(final Locale aLocale) {

        final NumberFormat tmpFormat = NumberStyle.INTEGER.getFormat(aLocale);
        final int tmpPrecision = 0;
        final int tmpScale = 0;
        final RoundingMode tmpRoundingMode = DEFAULT_MATH.getRoundingMode();

        return new NumberContext(tmpFormat, tmpPrecision, tmpScale, tmpRoundingMode);
    }

    public static NumberContext getPercent(final int aScale, final Locale aLocale) {

        final NumberFormat tmpFormat = NumberStyle.PERCENT.getFormat(Locale.getDefault());
        final int tmpPrecision = MathContext.DECIMAL32.getPrecision();
        final int tmpScale = aScale;
        final RoundingMode tmpRoundingMode = MathContext.DECIMAL32.getRoundingMode();

        return new NumberContext(tmpFormat, tmpPrecision, tmpScale, tmpRoundingMode);
    }

    public static NumberContext getPercent(final Locale aLocale) {
        return NumberContext.getPercent(4, aLocale);
    }

    private final double myError;

    private final MathContext myMathContext;

    private final int myPrecision;

    private final RoundingMode myRoundingMode;
    private final int myScale;
    private final double myScaleFactor;

    public NumberContext() {
        this(DEFAULT_STYLE.getFormat(), DEFAULT_MATH.getPrecision(), DEFAULT_MATH.getPrecision() / 2, DEFAULT_MATH.getRoundingMode());
    }

    public NumberContext(final Format aFormat, final int aPrecision, final int aScale, final RoundingMode aRoundingMode) {

        super(aFormat);

        myPrecision = aPrecision;

        myScale = aScale;
        myScaleFactor = PrimitiveFunction.POWER.invoke(PrimitiveMath.TEN, aScale);

        myRoundingMode = aRoundingMode;

        myMathContext = new MathContext(aPrecision, aRoundingMode);

        myError = (PrimitiveMath.HALF * Math.pow(PrimitiveMath.TEN, -aScale)) + PrimitiveMath.MACHINE_DOUBLE_ERROR;
    }

    public NumberContext(final int aPrecision, final int aScale, final RoundingMode aRoundingMode) {
        this(DEFAULT_STYLE.getFormat(), aPrecision, aScale, aRoundingMode);
    }

    public NumberContext(final int aScale, final RoundingMode aRoundingMode) {
        this(DEFAULT_STYLE.getFormat(), DEFAULT_MATH.getPrecision(), aScale, aRoundingMode);
    }

    public NumberContext(final NumberContext aContextToCopy, final RoundingMode aDifferentMode) {
        this(aContextToCopy.getFormat(), aContextToCopy.getPrecision(), aContextToCopy.getScale(), aDifferentMode);
    }

    public NumberContext(final RoundingMode aRoundingMode) {
        this(DEFAULT_STYLE.getFormat(), DEFAULT_MATH.getPrecision(), DEFAULT_MATH.getPrecision() / 2, aRoundingMode);
    }

    @SuppressWarnings("unused")
    private NumberContext(final Format aFormat) {
        this(aFormat, DEFAULT_MATH.getPrecision(), DEFAULT_MATH.getPrecision() / 2, DEFAULT_MATH.getRoundingMode());
    }

    NumberContext(final NumberContext aContext) {
        this(aContext.getFormat(), aContext.getPrecision(), aContext.getScale(), aContext.getRoundingMode());
    }

    @Override
    public NumberContext copy() {
        return new NumberContext(this);
    }

    /**
     * Will first enforce the precision, and then the scale. Both
     * operations will comply with the rounding mode. Finally trailing
     * zeros are stripped.
     */
    public BigDecimal enforce(final BigDecimal aNmbr) {
        final BigDecimal tmpEnforced = aNmbr.plus(this.getMathContext()).setScale(myScale, myRoundingMode).stripTrailingZeros();
        if (tmpEnforced.signum() == 0) {
            return BigMath.ZERO;
        } else {
            return tmpEnforced;
        }
    }

    /**
     * @see ComplexNumber#enforce(NumberContext)
     */
    public ComplexNumber enforce(final ComplexNumber aNmbr) {
        return aNmbr.enforce(this);
    }

    /**
     * Converts the input number to a BigDecimal, enforces using
     * {@linkplain #enforce(BigDecimal)}, and then extracts a double again.
     */
    public double enforce(final double aNmbr) {
        return this.enforce(new BigDecimal(Double.toString(aNmbr))).doubleValue();
    }

    @Override
    public Number enforce(final Number anObject) {

        if (anObject instanceof BigDecimal) {
            return this.enforce((BigDecimal) anObject);
        } else if (anObject instanceof ComplexNumber) {
            return this.enforce((ComplexNumber) anObject);
        } else if (anObject instanceof RationalNumber) {
            return this.enforce((RationalNumber) anObject);
        } else {
            return this.enforce(anObject.doubleValue());
        }
    }

    /**
     * @see RationalNumber#enforce(NumberContext)
     */
    public RationalNumber enforce(final RationalNumber aNmbr) {
        return aNmbr.enforce(this);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NumberContext)) {
            return false;
        }
        final NumberContext other = (NumberContext) obj;
        if (myMathContext == null) {
            if (other.myMathContext != null) {
                return false;
            }
        } else if (!myMathContext.equals(other.myMathContext)) {
            return false;
        }
        if (myPrecision != other.myPrecision) {
            return false;
        }
        if (myRoundingMode == null) {
            if (other.myRoundingMode != null) {
                return false;
            }
        } else if (!myRoundingMode.equals(other.myRoundingMode)) {
            return false;
        }
        if (myScale != other.myScale) {
            return false;
        }
        if (Double.doubleToLongBits(myScaleFactor) != Double.doubleToLongBits(other.myScaleFactor)) {
            return false;
        }
        return true;
    }

    public UnaryFunction<BigDecimal> getBigEnforceFunction() {
        return new UnaryFunction<BigDecimal>() {

            public BigDecimal invoke(final BigDecimal anArg) {
                return NumberContext.this.enforce(anArg);
            }

            public double invoke(final double anArg) {
                return NumberContext.this.enforce(anArg);
            }
        };
    }

    public UnaryFunction<BigDecimal> getBigRoundFunction() {
        return new UnaryFunction<BigDecimal>() {

            public BigDecimal invoke(final BigDecimal anArg) {
                return NumberContext.this.round(anArg);
            }

            public double invoke(final double anArg) {
                return NumberContext.this.round(anArg);
            }
        };
    }

    public UnaryFunction<ComplexNumber> getComplexEnforceFunction() {
        return new UnaryFunction<ComplexNumber>() {

            public ComplexNumber invoke(final ComplexNumber anArg) {
                return NumberContext.this.enforce(anArg);
            }

            public double invoke(final double anArg) {
                return NumberContext.this.enforce(anArg);
            }
        };
    }

    public UnaryFunction<ComplexNumber> getComplexRoundFunction() {
        return new UnaryFunction<ComplexNumber>() {

            public ComplexNumber invoke(final ComplexNumber anArg) {
                return NumberContext.this.round(anArg);
            }

            public double invoke(final double anArg) {
                return NumberContext.this.round(anArg);
            }
        };
    }

    public double getError() {
        return myError;
    }

    public MathContext getMathContext() {
        return myMathContext;
    }

    public int getPrecision() {
        return myPrecision;
    }

    public UnaryFunction<Double> getPrimitiveEnforceFunction() {
        return new UnaryFunction<Double>() {

            public double invoke(final double anArg) {
                return NumberContext.this.enforce(anArg);
            }

            public Double invoke(final Double anArg) {
                return NumberContext.this.enforce(anArg.doubleValue());
            }
        };
    }

    public UnaryFunction<Double> getPrimitiveRoundFunction() {
        return new UnaryFunction<Double>() {

            public double invoke(final double anArg) {
                return NumberContext.this.round(anArg);
            }

            public Double invoke(final Double anArg) {
                return NumberContext.this.round(anArg.doubleValue());
            }
        };
    }

    public UnaryFunction<RationalNumber> getRationalEnforceFunction() {
        return new UnaryFunction<RationalNumber>() {

            public double invoke(final double anArg) {
                return NumberContext.this.enforce(anArg);
            }

            public RationalNumber invoke(final RationalNumber anArg) {
                return NumberContext.this.enforce(anArg);
            }
        };
    }

    public UnaryFunction<RationalNumber> getRationalRoundFunction() {
        return new UnaryFunction<RationalNumber>() {

            public double invoke(final double anArg) {
                return NumberContext.this.round(anArg);
            }

            public RationalNumber invoke(final RationalNumber anArg) {
                return NumberContext.this.round(anArg);
            }
        };
    }

    public RoundingMode getRoundingMode() {
        return myRoundingMode;
    }

    public int getScale() {
        return myScale;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((myMathContext == null) ? 0 : myMathContext.hashCode());
        result = (prime * result) + myPrecision;
        result = (prime * result) + ((myRoundingMode == null) ? 0 : myRoundingMode.hashCode());
        result = (prime * result) + myScale;
        long temp;
        temp = Double.doubleToLongBits(myScaleFactor);
        result = (prime * result) + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public boolean isZero(final double aValue) {
        return Math.abs(aValue) <= myError;
    }

    public BigDecimal round(final BigDecimal aNmbr) {
        return aNmbr.setScale(myScale, myRoundingMode);
    }

    public ComplexNumber round(final ComplexNumber aNmbr) {
        return aNmbr.round(this);
    }

    /**
     * Does not enforce the precision and does not use the specified
     * rounding mode. The precision is given by the type double and the
     * rounding mode is always "half even" as given by
     * {@linkplain StrictMath#rint(double)}.
     */
    public double round(final double aNmbr) {
        return Math.rint(myScaleFactor * aNmbr) / myScaleFactor;
    }

    public RationalNumber round(final RationalNumber aNmbr) {
        return aNmbr.round(this);
    }

    public void setFormat(final NumberStyle aStyle, final Locale aLocale) {
        this.setFormat(aStyle.getFormat(aLocale));
    }

    /**
     * Will create an "enforced" BigDecimal instance.
     */
    public BigDecimal toBigDecimal(final double aNmbr) {
        return new BigDecimal(aNmbr, this.getMathContext()).setScale(myScale, myRoundingMode);
    }

    /**
     * Works with {@linkplain DecimalFormat} and {@linkplain FormatPattern} implementations.
     * In other cases it returns null.
     */
    public String toLocalizedPattern() {

        String retVal = null;

        if (this.getFormat() instanceof DecimalFormat) {
            retVal = ((DecimalFormat) this.getFormat()).toLocalizedPattern();
        } else if (this.getFormat() instanceof FormatPattern) {
            retVal = ((FormatPattern) this.getFormat()).toLocalizedPattern();
        }

        return retVal;
    }

    /**
     * Works with {@linkplain DecimalFormat} and {@linkplain FormatPattern} implementations.
     * In other cases it returns null.
     */
    public String toPattern() {

        String retVal = null;

        if (this.getFormat() instanceof DecimalFormat) {
            retVal = ((DecimalFormat) this.getFormat()).toPattern();
        } else if (this.getFormat() instanceof FormatPattern) {
            retVal = ((FormatPattern) this.getFormat()).toPattern();
        }

        return retVal;
    }

    @Override
    protected void configureFormat(final Format aFormat, final Object anObject) {

        if (aFormat instanceof DecimalFormat) {

            final DecimalFormat tmpDF = (DecimalFormat) aFormat;

            final int tmpModScale = myScale - (int) Math.log10(tmpDF.getMultiplier());

            tmpDF.setMaximumFractionDigits(tmpModScale);
            tmpDF.setMinimumFractionDigits(tmpModScale);

            if (anObject instanceof BigDecimal) {
                ((DecimalFormat) this.getFormat()).setParseBigDecimal(true);
            } else {
                ((DecimalFormat) this.getFormat()).setParseBigDecimal(false);
            }
        }
    }

    @Override
    protected String handleFormatException(final Format aFormat, final Object anObject) {
        return null;
    }

    @Override
    protected Number handleParseException(final Format aFormat, final String aString) {
        return BigMath.ZERO;
    }

}
