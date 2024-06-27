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

public final class IntCount {

    private static final boolean BOOLEAN_FALSE = false;
    private static final boolean BOOLEAN_TRUE = true;

    private static final int INT_ONE = 1;
    private static final int INT_TWO = 2;
    private static final int INT_ZERO = 0;

    public final int count;
    public final boolean modified;

    public IntCount(final int aCount) {
        this(aCount, BOOLEAN_FALSE);
    }

    @SuppressWarnings("unused")
    private IntCount() {
        this(INT_ZERO, BOOLEAN_FALSE);
    }

    private IntCount(final int aCount, final boolean aModified) {

        super();

        count = aCount;
        modified = aModified;
    }

    /**
     * @return count - 1
     */
    public IntCount decrement() {
        return new IntCount(count - INT_ONE, BOOLEAN_TRUE);
    }

    /**
     * @return count / 2
     */
    public IntCount halve() {
        return new IntCount(count / INT_TWO, BOOLEAN_TRUE);
    }

}
