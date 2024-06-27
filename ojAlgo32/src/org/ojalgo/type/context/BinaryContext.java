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

import org.ojalgo.type.format.BinaryFormat;

public final class BinaryContext extends TypeContext<byte[]> {

    private static final Format DEFAULT_FORMAT = new BinaryFormat();

    public BinaryContext() {
        super(DEFAULT_FORMAT);
    }

    public BinaryContext(final Format aFormat) {
        super(aFormat);
    }

    BinaryContext(final BinaryContext aContext) {
        super(aContext.getFormat());
    }

    @Override
    public BinaryContext copy() {
        return new BinaryContext(this);
    }

    @Override
    public byte[] enforce(final byte[] anObject) {
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
    protected byte[] handleParseException(final Format aFormat, final String aString) {
        return new byte[] {};
    }

}
