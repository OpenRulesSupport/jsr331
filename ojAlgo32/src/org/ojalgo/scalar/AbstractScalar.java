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
package org.ojalgo.scalar;


abstract class AbstractScalar<N extends Number> extends Number implements Scalar<N> {

    protected static final boolean BOOLEAN_FALSE = false;
    protected static final boolean BOOLEAN_TRUE = true;
    protected static final int INT_NEG = -1;
    protected static final int INT_ONE = 1;
    protected static final int INT_ZERO = 0;
    protected static final long LONG_ONE = 1L;
    protected static final long LONG_ZERO = 0L;

    public AbstractScalar() {
        super();
    }

    @Override
    public final boolean equals(final Object someObj) {
        if (someObj instanceof Scalar) {
            return this.equals((Scalar<?>) someObj);
        } else {
            return super.equals(someObj);
        }
    }

    @Override
    public final int hashCode() {
        return new Double(this.getReal()).hashCode();
    }

}
