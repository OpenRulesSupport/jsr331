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
package org.ojalgo.function;

/**
 * A {@linkplain BinaryFunction} with a set/fixed first argument.
 *
 * @author apete
 */
public final class PreconfiguredFirst<N extends Number> implements UnaryFunction<N> {

    private final BinaryFunction<N> myFunction;
    private final N myNumber;
    private final double myValue;

    public PreconfiguredFirst(final double aFirstArg, final BinaryFunction<N> aFunc) {

        super();

        myFunction = aFunc;

        myNumber = (N) Double.valueOf(aFirstArg);
        myValue = aFirstArg;
    }

    public PreconfiguredFirst(final N aFirstArg, final BinaryFunction<N> aFunc) {

        super();

        myFunction = aFunc;

        myNumber = aFirstArg;
        myValue = aFirstArg.doubleValue();
    }

    @SuppressWarnings("unused")
    private PreconfiguredFirst() {
        this(null, null);
    }

    public final double doubleValue() {
        return myValue;
    }

    public final BinaryFunction<N> getFunction() {
        return myFunction;
    }

    public final N getNumber() {
        return myNumber;
    }

    public double invoke(final double aSecondArg) {
        return myFunction.invoke(myValue, aSecondArg);
    }

    public N invoke(final N aSecondArg) {
        return myFunction.invoke(myNumber, aSecondArg);
    }

}
