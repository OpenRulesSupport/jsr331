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
package org.ojalgo.finance.data;

import java.util.Calendar;
import java.util.Date;

import org.ojalgo.type.CalendarDate;
import org.ojalgo.type.keyvalue.KeyValue;

public abstract class DatePrice implements KeyValue<CalendarDate, Double> {

    public final CalendarDate key;

    protected DatePrice(final Calendar aDate) {

        super();

        key = new CalendarDate(aDate);
    }

    protected DatePrice(final Date aDate) {

        super();

        key = new CalendarDate(aDate);
    }

    protected DatePrice(final long aDate) {

        super();

        key = new CalendarDate(aDate);
    }

    public int compareTo(final KeyValue<CalendarDate, ?> ref) {
        return key.compareTo(ref.getKey());
    }

    public final CalendarDate getKey() {
        return key;
    }

    public abstract double getPrice();

    public final Double getValue() {
        return this.getPrice();
    }

    @Override
    public final String toString() {
        return key + ": " + this.getPrice();
    }

}
