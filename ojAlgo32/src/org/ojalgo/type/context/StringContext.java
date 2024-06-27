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

import org.ojalgo.type.format.StringFormat;

/**
 * StringContext
 *
 * @author apete
 */
public final class StringContext extends TypeContext<String> {

    private static final Format DEFAULT_FORMAT = new StringFormat();

    private static final String EMPTY = "";
    private static final int ZERO = 0;
    private final int myLength;

    public StringContext() {

        super(DEFAULT_FORMAT);

        myLength = ZERO;
    }

    public StringContext(final Format aFormat) {

        super(aFormat);

        myLength = ZERO;
    }

    public StringContext(final Format aFormat, final int aLength) {

        super(aFormat);

        myLength = aLength;
    }

    public StringContext(final int aLength) {

        super(DEFAULT_FORMAT);

        myLength = aLength;
    }

    StringContext(final StringContext aContext) {

        super(aContext.getFormat());

        myLength = ZERO;
    }

    @Override
    public StringContext copy() {
        return new StringContext(this);
    }

    @Override
    public String enforce(final String anObject) {

        String retVal = anObject.trim();
        final int tmpLength = retVal.length();

        if ((myLength > ZERO) && (tmpLength > myLength)) {
            retVal = retVal.substring(ZERO, myLength - 1).trim() + "…";
        }

        return retVal;
    }

    @Override
    protected void configureFormat(final Format aFormat, final Object anObject) {

    }

    @Override
    protected String handleFormatException(final Format aFormat, final Object anObject) {
        return null;
    }

    @Override
    protected String handleParseException(final Format aFormat, final String aString) {
        return EMPTY;
    }

}
