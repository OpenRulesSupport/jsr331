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

import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;

import org.ojalgo.ProgrammingError;
import org.ojalgo.netio.ASCII;

public abstract class TypeContext<T> implements Serializable {

    /**
     * Use 'Non-Breaking SPace' character instead of ardinary 'space' character.
     */
    public static final boolean NBSP = true;

    private Format myFormat;
    private boolean myConfigured = false;

    @SuppressWarnings("unused")
    private TypeContext() {
        this((Format) null);
    }

    protected TypeContext(final Format aFormat) {

        super();

        myFormat = (Format) aFormat.clone();

        ProgrammingError.throwIfNull(myFormat);
    }

    public abstract TypeContext<T> copy();

    public abstract T enforce(T anObject);

    public final String format(final Object value) {

        if (value != null) {

            try {

                if (!myConfigured) {
                    this.configureFormat(myFormat, value);
                    myConfigured = true;
                }

                if (NBSP) {
                    return myFormat.format(value).replace(ASCII.SP, ASCII.NBSP);
                } else {
                    return myFormat.format(value);
                }

            } catch (final IllegalArgumentException exception) {

                return this.handleFormatException(myFormat, value);
            }

        } else {

            return null;
        }
    }

    public final Format getFormat() {
        return (Format) myFormat.clone();
    }

    @SuppressWarnings("unchecked")
    public final T parse(final String aString) {

        if (aString != null) {

            try {
                return (T) myFormat.parseObject(NBSP ? aString.replace(ASCII.NBSP, ASCII.SP) : aString);
            } catch (final ParseException anException) {
                return this.handleParseException(myFormat, aString);
            }

        } else {

            return null;
        }
    }

    /**
     * First calls {@linkplain #parse(String)} and then {@linkplain #enforce(Object)}.
     */
    public final T parseAndEnforce(final String aString) {
        return this.enforce(this.parse(aString));
    }

    public final void setFormat(final Format aFormat) {
        myFormat = (Format) aFormat.clone();
    }

    protected abstract void configureFormat(Format aFormat, Object anObject);

    protected abstract String handleFormatException(Format aFormat, Object anObject);

    protected abstract T handleParseException(Format aFormat, String aString);

}
