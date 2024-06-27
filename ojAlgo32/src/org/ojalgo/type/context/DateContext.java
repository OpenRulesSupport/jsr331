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

import java.text.Format;
import java.util.Date;
import java.util.Locale;

import org.ojalgo.type.StandardType;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.format.DatePart;
import org.ojalgo.type.format.DateStyle;

/**
 * DateContext
 *
 * @author apete
 */
public final class DateContext extends TypeContext<Date> {

    private static final DatePart DEFAULT_PART = DatePart.DATETIME;
    private static final DateStyle DEFAULT_STYLE = DateStyle.SHORT;

    private DatePart myPart = DEFAULT_PART;
    private DateStyle myStyle = DEFAULT_STYLE;

    public DateContext() {
        super(StandardType.SQL_DATETIME.getFormat());
    }

    public DateContext(final DatePart aPart) {
        this(aPart, DEFAULT_STYLE, Locale.getDefault());
    }

    public DateContext(final DatePart aPart, final DateStyle aStyle, final Locale aLocale) {

        super(aPart != null ? aPart.getFormat(aStyle, aLocale) : DEFAULT_PART.getFormat(aStyle, aLocale));

        myPart = aPart != null ? aPart : DEFAULT_PART;
        myStyle = aStyle != null ? aStyle : DEFAULT_STYLE;
    }

    private DateContext(final Format aFormat) {
        super(aFormat);
    }

    DateContext(final DateContext aContext) {
        super(aContext.getFormat());
    }

    @Override
    public DateContext copy() {
        return new DateContext(this);
    }

    @Override
    public Date enforce(final Date anObject) {

        switch (myPart) {

        case DATE:

            return TypeUtils.makeSqlDate(anObject.getTime());

        case TIME:

            return TypeUtils.makeSqlTime(anObject.getTime());

        default:

            return TypeUtils.makeSqlTimestamp(anObject.getTime());
        }
    }

    public DatePart getPart() {
        return myPart;
    }

    public DateStyle getStyle() {
        return myStyle;
    }

    public void setFormat(final DatePart aPart, final DateStyle aStyle, final Locale aLocale) {

        if (aPart != null) {
            myPart = aPart;
        }

        if (aStyle != null) {
            myStyle = aStyle;
        }

        this.setFormat(myPart.getFormat(myStyle, aLocale));
    }

    @Override
    protected void configureFormat(final Format aFormat, final Object anObject) {
        // No need to do anything
    }

    @Override
    protected String handleFormatException(final Format aFormat, final Object anObject) {
        return null;
    }

    @Override
    protected Date handleParseException(final Format aFormat, final String aString) {
        return new Date();
    }

}
