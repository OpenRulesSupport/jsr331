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
package org.ojalgo.type;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.netio.ASCII;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.type.context.NumberContext;

public abstract class TypeUtils {

    /**
     * The default {@linkplain NumberContext} used with various
     * {@linkplain Object#equals(Object)} implementations.
     */
    public static final NumberContext EQUALS_NUMBER_CONTEXT = new NumberContext(7, 6, RoundingMode.HALF_DOWN);

    public static final long HOURS_PER_CENTURY = 876582L; // 365.2425 * 24 * 100 = 876582)
    public static final long MILLIS_PER_HOUR = 60L * 60L * 1000L;

    /**
     * {@link Deprecated} v32
     */
    public static final SimpleDateFormat SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * {@link Deprecated} v32
     */
    public static final SimpleDateFormat SQL_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    /**
     * {@link Deprecated} v32
     */
    public static final SimpleDateFormat SQL_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String HEX = "#";
    private static final char START = '{';

    /**
     * Compatible with slf4j. {} in the message pattern will be replaced by the arguments.
     */
    public static String format(final String aMessagePattern, final Object... someArgs) {

        if (aMessagePattern == null) {
            return null;
        }

        final int tmpPatternSize = aMessagePattern.length();
        final int tmpArgsCount = someArgs.length;

        int tmpFirst = 0;
        int tmpLimit = tmpPatternSize;

        final StringBuilder retVal = new StringBuilder(tmpPatternSize + (tmpArgsCount * 20));

        for (int a = 0; a < tmpArgsCount; a++) {

            tmpLimit = aMessagePattern.indexOf(TypeUtils.START, tmpFirst);

            if (tmpLimit == -1) {
                retVal.append(ASCII.SP);
                retVal.append(someArgs[a]);
            } else {
                retVal.append(aMessagePattern.substring(tmpFirst, tmpLimit));
                retVal.append(someArgs[a]);
                tmpFirst = tmpLimit + 2;
            }
        }

        retVal.append(aMessagePattern.substring(tmpFirst, tmpPatternSize));

        return retVal.toString();
    }

    public static final GregorianCalendar getHundredYearsAgo() {

        final GregorianCalendar retVal = new GregorianCalendar();

        retVal.add(Calendar.YEAR, -100);

        return retVal;
    }

    public static final GregorianCalendar getThousandYearsAgo() {

        final GregorianCalendar retVal = new GregorianCalendar();

        retVal.add(Calendar.YEAR, -1000);

        return retVal;
    }

    public static final GregorianCalendar getThousandYearsFromNow() {

        final GregorianCalendar retVal = new GregorianCalendar();

        retVal.add(Calendar.YEAR, 1000);

        return retVal;
    }

    public static boolean isZero(final double aVal) {
        return TypeUtils.isZero(aVal, PrimitiveMath.IS_ZERO);
    }

    public static boolean isZero(final double aVal, final double aTolerance) {
        return (Math.abs(aVal) <= aTolerance);
    }

    public static Date makeSqlDate(final long aTimeInMillis) {
        return Date.valueOf(new Date(aTimeInMillis).toString());
    }

    public static Time makeSqlTime(final long aTimeInMillis) {
        return Time.valueOf(new Time(aTimeInMillis).toString());
    }

    public static Timestamp makeSqlTimestamp(final long aTimeInMillis) {
        return Timestamp.valueOf(new Timestamp(aTimeInMillis).toString());
    }

    /**
     * If the input {@linkplain java.lang.Number} is a {@linkplain java.math.BigDecimal}
     * it is passed through unaltered. Otherwise an equivalent BigDecimal is
     * created.
     * 
     * @param aNumber Any Number
     * @return A corresponding BigDecimal
     */
    public static BigDecimal toBigDecimal(final Number aNumber) {

        BigDecimal retVal = BigMath.ZERO;

        if (aNumber != null) {

            if (aNumber instanceof BigDecimal) {

                retVal = (BigDecimal) aNumber;

            } else if (aNumber instanceof ComplexNumber) {

                retVal = TypeUtils.toBigDecimal(((ComplexNumber) aNumber).getReal());

            } else {

                try {

                    retVal = new BigDecimal(aNumber.toString());

                } catch (final NumberFormatException anException) {

                    final double tmpVal = aNumber.doubleValue();

                    if (Double.isNaN(tmpVal)) {
                        retVal = BigMath.ZERO;
                    } else if (Double.isInfinite(tmpVal) && (tmpVal > PrimitiveMath.ZERO)) {
                        retVal = BigMath.VERY_POSITIVE;
                    } else if (Double.isInfinite(tmpVal) && (tmpVal < PrimitiveMath.ZERO)) {
                        retVal = BigMath.VERY_NEGATIVE;
                    } else {
                        retVal = BigDecimal.valueOf(tmpVal);
                    }
                }
            }
        }

        return retVal;
    }

    public static BigDecimal toBigDecimal(final Number aNumber, final NumberContext aContext) {
        return aContext.enforce(TypeUtils.toBigDecimal(aNumber));
    }

    public static ComplexNumber toComplexNumber(final Number aNumber) {

        ComplexNumber retVal = ComplexNumber.ZERO;

        if (aNumber != null) {

            if (aNumber instanceof ComplexNumber) {

                retVal = (ComplexNumber) aNumber;

            } else {

                retVal = new ComplexNumber(aNumber.doubleValue());
            }
        }

        return retVal;
    }

    /**
     * The way colours are specified in html pages.
     */
    public static String toHexString(final Color aColor) {
        return HEX + Integer.toHexString(aColor.getRGB()).substring(2);
    }

    public static RationalNumber toRationalNumber(final Number aNumber) {

        RationalNumber retVal = RationalNumber.ZERO;

        if (aNumber != null) {

            if (aNumber instanceof RationalNumber) {

                retVal = (RationalNumber) aNumber;

            } else {

                retVal = new RationalNumber(TypeUtils.toBigDecimal(aNumber));
            }
        }

        return retVal;
    }

    static boolean isSameDate(final Calendar aCal1, final Calendar aCal2) {

        boolean retVal = aCal1.get(Calendar.YEAR) == aCal2.get(Calendar.YEAR);

        retVal = retVal && (aCal1.get(Calendar.MONTH) == aCal2.get(Calendar.MONTH));

        retVal = retVal && (aCal1.get(Calendar.DAY_OF_MONTH) == aCal2.get(Calendar.DAY_OF_MONTH));

        return retVal;
    }

    static boolean isSameTime(final Calendar aCal1, final Calendar aCal2) {

        boolean retVal = aCal1.get(Calendar.HOUR_OF_DAY) == aCal2.get(Calendar.HOUR_OF_DAY);

        retVal = retVal && (aCal1.get(Calendar.MINUTE) == aCal2.get(Calendar.MINUTE));

        retVal = retVal && (aCal1.get(Calendar.SECOND) == aCal2.get(Calendar.SECOND));

        return retVal;
    }

    protected TypeUtils() {
        super();
    }
}
