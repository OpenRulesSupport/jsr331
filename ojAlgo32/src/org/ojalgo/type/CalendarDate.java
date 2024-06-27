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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Corresponds to a {@linkplain Calendar} AND a {@linkplain Date}.
 * It is immutable and interacts with {@linkplain CalendarDateUnit}.
 *
 * @author apete
 */
public final class CalendarDate implements Comparable<CalendarDate> {

    public static CalendarDate make(final Calendar aCalendar, final CalendarDateUnit aResolution) {
        return new CalendarDate(aResolution.toTimeInMillis(aCalendar));
    }

    public static CalendarDate make(final CalendarDateUnit aResolution) {
        return new CalendarDate(aResolution.toTimeInMillis(System.currentTimeMillis()));
    }

    public static CalendarDate make(final Date aDate, final CalendarDateUnit aResolution) {
        return new CalendarDate(aResolution.toTimeInMillis(aDate));
    }

    public static CalendarDate make(final long aTimeInMIllis, final CalendarDateUnit aResolution) {
        return new CalendarDate(aResolution.toTimeInMillis(aTimeInMIllis));
    }

    public final long millis;

    public CalendarDate() {

        super();

        millis = System.currentTimeMillis();
    }

    public CalendarDate(final Calendar aCalendar) {

        super();

        millis = aCalendar.getTimeInMillis();
    }

    public CalendarDate(final Date aDate) {

        super();

        millis = aDate.getTime();
    }

    public CalendarDate(final long timeInMillis) {

        super();

        millis = timeInMillis;
    }

    public CalendarDate(final String anSqlString) {

        super();

        final boolean tmpDatePart = anSqlString.indexOf('-') >= 0;
        final boolean tmpTimePart = anSqlString.indexOf(':') >= 0;

        if (tmpDatePart && tmpTimePart) {
            millis = StandardType.SQL_DATETIME.parse(anSqlString).getTime();
        } else if (tmpDatePart && !tmpTimePart) {
            millis = StandardType.SQL_DATE.parse(anSqlString).getTime();
        } else if (!tmpDatePart && tmpTimePart) {
            millis = StandardType.SQL_TIME.parse(anSqlString).getTime();
        } else {
            millis = 0L;
        }
    }

    public int compareTo(final CalendarDate ref) {
        return Long.signum(millis - ref.millis);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CalendarDate)) {
            return false;
        }
        final CalendarDate other = (CalendarDate) obj;
        if (millis != other.millis) {
            return false;
        }
        return true;
    }

    public CalendarDate filter(final CalendarDateUnit aResolution) {
        if (aResolution.isCalendarUnit()) {
            return new CalendarDate(aResolution.toTimeInMillis(this.getCalendar()));
        } else {
            return new CalendarDate(aResolution.toTimeInMillis(millis));
        }
    }

    public Calendar getCalendar() {
        final GregorianCalendar retVal = new GregorianCalendar();
        retVal.setTimeInMillis(millis);
        return retVal;
    }

    public Calendar getCalendar(final Locale aLocale) {
        final GregorianCalendar retVal = new GregorianCalendar(aLocale);
        retVal.setTimeInMillis(millis);
        return retVal;
    }

    public Calendar getCalendar(final TimeZone aTimeZone) {
        final GregorianCalendar retVal = new GregorianCalendar(aTimeZone);
        retVal.setTimeInMillis(millis);
        return retVal;
    }

    public Calendar getCalendar(final TimeZone aTimeZone, final Locale aLocale) {
        final GregorianCalendar retVal = new GregorianCalendar(aTimeZone, aLocale);
        retVal.setTimeInMillis(millis);
        return retVal;
    }

    public Date getDate() {
        return new Date(millis);
    }

    @Override
    public int hashCode() {
        return (int) (millis ^ (millis >>> 32));
    }

    public CalendarDate step(final CalendarDateUnit aStepUnit) {
        return this.step(1, aStepUnit);
    }

    public CalendarDate step(final int aStepCount, final CalendarDateUnit aStepUnit) {
        if (aStepUnit.isCalendarUnit()) {
            return new CalendarDate(aStepUnit.step(this.getCalendar(), aStepCount));
        } else {
            return new CalendarDate(aStepUnit.step(millis, aStepCount));
        }
    }

    public java.sql.Date toSqlDate() {
        return TypeUtils.makeSqlDate(millis);
    }

    public java.sql.Time toSqlTime() {
        return TypeUtils.makeSqlTime(millis);
    }

    public java.sql.Timestamp toSqlTimestamp() {
        return TypeUtils.makeSqlTimestamp(millis);
    }

    @Override
    public String toString() {
        return StandardType.SQL_DATETIME.format(this.getDate());
    }

    public long toTimeInMillis(final CalendarDateUnit aResolution) {
        if (aResolution.isCalendarUnit()) {
            return aResolution.toTimeInMillis(this.getCalendar());
        } else {
            return aResolution.toTimeInMillis(millis);
        }
    }

}
