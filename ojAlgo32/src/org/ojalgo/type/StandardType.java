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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Locale;

import org.ojalgo.type.context.BooleanContext;
import org.ojalgo.type.context.DateContext;
import org.ojalgo.type.context.NumberContext;
import org.ojalgo.type.context.StringContext;
import org.ojalgo.type.format.DatePart;
import org.ojalgo.type.format.DateStyle;
import org.ojalgo.type.format.NumberStyle;

public abstract class StandardType {

    /**
     * <ul>
     * <li>Precision: 16</li>
     * <li>Scale: 2</li>
     * <li>Rounding Mode: {@linkplain RoundingMode#HALF_EVEN}</li>
     * <li>Locale: JVM Default</li>
     * <li>Style: {@linkplain NumberStyle#CURRENCY}</li>
     * </ul>
     * Fits within the Sybase (and MS SQL Server) money type which is (19,4).
     * Typically you have {@linkplain #QUANTITY} x {@linkplain #PRICE} = {@linkplain #AMOUNT},
     * an alternative is {@linkplain #QUANTITY} x {@linkplain #PRICE} = {@linkplain #QUANTITY}.
     */
    public static final NumberContext AMOUNT = NumberContext.getCurrency(Locale.getDefault());

    public static final BooleanContext BOOLEAN = new BooleanContext();

    public static final DateContext DATE = new DateContext(DatePart.DATE);

    /**
     * 'datetime' and/or 'timestamp' as in {@linkplain #DATE} and {@linkplain #TIME}.
     */
    public static final DateContext DATETIME = new DateContext(DatePart.DATETIME);

    /**
     * <ul>
     * <li>Precision: 7</li>
     * <li>Scale: 3</li>
     * <li>Rounding Mode: {@linkplain RoundingMode#HALF_EVEN}</li>
     * <li>Locale: JVM Default</li>
     * <li>Style: {@linkplain NumberStyle#GENERAL}</li>
     * </ul>
     */
    public static final NumberContext DECIMAL_032 = NumberContext.getGeneral(MathContext.DECIMAL32);

    /**
     * <ul>
     * <li>Precision: 16</li>
     * <li>Scale: 8</li>
     * <li>Rounding Mode: {@linkplain RoundingMode#HALF_EVEN}</li>
     * <li>Locale: JVM Default</li>
     * <li>Style: {@linkplain NumberStyle#GENERAL}</li>
     * </ul>
     */
    public static final NumberContext DECIMAL_064 = NumberContext.getGeneral(MathContext.DECIMAL64);

    /**
     * <ul>
     * <li>Precision: 34</li>
     * <li>Scale: 17</li>
     * <li>Rounding Mode: {@linkplain RoundingMode#HALF_EVEN}</li>
     * <li>Locale: JVM Default</li>
     * <li>Style: {@linkplain NumberStyle#GENERAL}</li>
     * </ul>
     */
    public static final NumberContext DECIMAL_128 = NumberContext.getGeneral(MathContext.DECIMAL128);

    /**
     * <ul>
     * <li>Precision: 7</li>
     * <li>Scale: 4</li>
     * <li>Rounding Mode: {@linkplain RoundingMode#HALF_EVEN}</li>
     * <li>Locale: JVM Default</li>
     * <li>Style: {@linkplain NumberStyle#PERCENT}</li>
     * </ul>
     */
    public static final NumberContext PERCENT = NumberContext.getPercent(Locale.getDefault());

    /**
     * Price or conversion rate (foreign exchange rate).
     * <ul>
     * <li>Precision: 16</li>
     * <li>Scale: 8</li>
     * <li>Rounding Mode: {@linkplain RoundingMode#HALF_EVEN}</li>
     * <li>Locale: JVM Default</li>
     * <li>Style: {@linkplain NumberStyle#GENERAL}</li>
     * </ul>
     * Typically you have {@linkplain #QUANTITY} x {@linkplain #PRICE} = {@linkplain #AMOUNT},
     * an alternative is {@linkplain #QUANTITY} x {@linkplain #PRICE} = {@linkplain #QUANTITY}.
     */
    public static final NumberContext PRICE = NumberContext.getGeneral(8);

    /**
     * <ul>
     * <li>Precision: 16</li>
     * <li>Scale: 6</li>
     * <li>Rounding Mode: {@linkplain RoundingMode#HALF_EVEN}</li>
     * <li>Locale: JVM Default</li>
     * <li>Style: {@linkplain NumberStyle#GENERAL}</li>
     * </ul>
     * Typically you have {@linkplain #QUANTITY} x {@linkplain #PRICE} = {@linkplain #AMOUNT},
     * an alternative is {@linkplain #QUANTITY} x {@linkplain #PRICE} = {@linkplain #QUANTITY}.
     */
    public static final NumberContext QUANTITY = NumberContext.getGeneral(6);

    public static final DateContext SQL_DATE = new DateContext(DatePart.DATE, DateStyle.SQL, null);

    public static final DateContext SQL_DATETIME = new DateContext(DatePart.DATETIME, DateStyle.SQL, null);

    public static final DateContext SQL_TIME = new DateContext(DatePart.TIME, DateStyle.SQL, null);

    public static final StringContext STRING_1 = new StringContext(1);

    public static final StringContext STRING_3 = new StringContext(3);

    public static final StringContext STRING_9 = new StringContext(9);

    public static final StringContext STRING_M = new StringContext(128);

    public static final StringContext TEXT = new StringContext(0);

    public static final DateContext TIME = new DateContext(DatePart.TIME);

    static {
        PERCENT.format(new BigDecimal("0.5000"));
    }

    protected StandardType() {
        super();
    }

    /**
     * {@linkplain #getQuantity()} * {@linkplain #getPrice()} = {@linkplain #getAmount()}
     */
    public NumberContext getAmount() {
        return AMOUNT;
    }

    public BooleanContext getBoolean() {
        return BOOLEAN;
    }

    public DateContext getDate() {
        return DATE;
    }

    public NumberContext getDecimal032() {
        return DECIMAL_032;
    }

    public NumberContext getDecimal064() {
        return DECIMAL_064;
    }

    public NumberContext getDecimal128() {
        return DECIMAL_128;
    }

    public DateContext getMoment() {
        return DATETIME;
    }

    public NumberContext getMoney() {
        return AMOUNT;
    }

    public NumberContext getParameter() {
        return DECIMAL_064;
    }

    public NumberContext getPercent() {
        return PERCENT;
    }

    /**
     * {@linkplain #getQuantity()} * {@linkplain #getPrice()} = {@linkplain #getAmount()}
     */
    public NumberContext getPrice() {
        return PRICE;
    }

    /**
     * {@linkplain #getQuantity()} * {@linkplain #getPrice()} = {@linkplain #getAmount()}
     */
    public NumberContext getQuantity() {
        return QUANTITY;
    }

    public StringContext getString1() {
        return STRING_1;
    }

    public StringContext getString3() {
        return STRING_3;
    }

    public StringContext getString9() {
        return STRING_9;
    }

    public StringContext getStringM() {
        return STRING_M;
    }

    public StringContext getText() {
        return TEXT;
    }

    public DateContext getTime() {
        return TIME;
    }

    public DateContext getTimestamp() {
        return DATETIME;
    }

}
