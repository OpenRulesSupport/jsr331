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

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.ojalgo.netio.ASCII;
import org.ojalgo.type.CalendarDateUnit;

public class GoogleSymbol extends DataSource<GoogleSymbol.Data> {

    public static final class Data extends DatePrice {

        public double close;
        public double high;
        public double low;
        public double open;
        public double volume;

        protected Data(final Calendar aDate) {
            super(aDate);
        }

        @Override
        public double getPrice() {
            return close;
        }

    }

    private static final String CSV = "csv";
    private static final String DAILY = "daily";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yy");
    private static final String FINANCE_GOOGLE_COM = "finance.google.com";
    private static final String FINANCE_HISTORICAL = "/finance/historical";
    private static final String HISTPERIOD = "histperiod";
    private static final String JAN_2_1970 = "Jan+2,+1970";
    private static final String OUTPUT = "output";
    private static final String Q = "q";
    private static final String STARTDATE = "startdate";
    private static final String WEEKLY = "weekly";

    public GoogleSymbol(final String aSymbol) {
        this(aSymbol, CalendarDateUnit.DAY);
    }

    public GoogleSymbol(final String aSymbol, final CalendarDateUnit aResolution) {

        super(aSymbol, aResolution);

        this.setHost(FINANCE_GOOGLE_COM);
        this.setPath(FINANCE_HISTORICAL);
        this.addQueryParameter(Q, aSymbol);
        this.addQueryParameter(STARTDATE, JAN_2_1970);
        switch (aResolution) {
        case WEEK:
            this.addQueryParameter(HISTPERIOD, WEEKLY);
            break;
        default:
            this.addQueryParameter(HISTPERIOD, DAILY);
            break;
        }
        this.addQueryParameter(OUTPUT, CSV);
    }

    @Override
    protected GoogleSymbol.Data parse(final String aLine) {

        Data retVal = null;

        int tmpInclusiveBegin = 0;
        int tmpExclusiveEnd = aLine.indexOf(ASCII.COMMA, tmpInclusiveBegin);
        String tmpString = aLine.substring(tmpInclusiveBegin, tmpExclusiveEnd);
        final Calendar tmpCalendar = new GregorianCalendar();
        try {
            tmpCalendar.setTime(new Date(DATE_FORMAT.parse(tmpString).getTime()));
        } catch (final ParseException anException) {
            anException.printStackTrace();
            return retVal;
        }
        this.getResolution().round(tmpCalendar);
        retVal = new Data(tmpCalendar);

        tmpInclusiveBegin = tmpExclusiveEnd + 1;
        tmpExclusiveEnd = aLine.indexOf(ASCII.COMMA, tmpInclusiveBegin);
        tmpString = aLine.substring(tmpInclusiveBegin, tmpExclusiveEnd);
        retVal.open = Double.parseDouble(tmpString);

        tmpInclusiveBegin = tmpExclusiveEnd + 1;
        tmpExclusiveEnd = aLine.indexOf(ASCII.COMMA, tmpInclusiveBegin);
        tmpString = aLine.substring(tmpInclusiveBegin, tmpExclusiveEnd);
        retVal.high = Double.parseDouble(tmpString);

        tmpInclusiveBegin = tmpExclusiveEnd + 1;
        tmpExclusiveEnd = aLine.indexOf(ASCII.COMMA, tmpInclusiveBegin);
        tmpString = aLine.substring(tmpInclusiveBegin, tmpExclusiveEnd);
        retVal.low = Double.parseDouble(tmpString);

        tmpInclusiveBegin = tmpExclusiveEnd + 1;
        tmpExclusiveEnd = aLine.indexOf(ASCII.COMMA, tmpInclusiveBegin);
        tmpString = aLine.substring(tmpInclusiveBegin, tmpExclusiveEnd);
        retVal.close = Double.parseDouble(tmpString);

        tmpInclusiveBegin = tmpExclusiveEnd + 1;
        tmpString = aLine.substring(tmpInclusiveBegin);
        retVal.volume = Double.parseDouble(tmpString);

        return retVal;
    }

}
