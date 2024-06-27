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

import org.ojalgo.type.format.BooleanFormat;

/**
 * BooleanContext
 *
 * @author apete
 */
public final class BooleanContext extends TypeContext<Boolean> {

    private static final Format DEFAULT_FORMAT = new BooleanFormat();

    public BooleanContext() {
        super(DEFAULT_FORMAT);
    }

    public BooleanContext(final Format aFormat) {
        super(aFormat != null ? aFormat : DEFAULT_FORMAT);
    }

    BooleanContext(final BooleanContext aContext) {
        super(aContext.getFormat());
    }

    @Override
    public BooleanContext copy() {
        return new BooleanContext(this);
    }

    @Override
    public Boolean enforce(final Boolean anObject) {
        return anObject;
    }

    @Override
    protected void configureFormat(final Format aFormat, final Object anObject) {

    }

    @Override
    protected String handleFormatException(final Format aFormat, final Object anObject) {
        return null;
    }

    @Override
    protected Boolean handleParseException(final Format aFormat, final String aString) {
        return false;
    }

}
