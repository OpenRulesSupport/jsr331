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
 * A {@linkplain BinaryFunction} with a set/fixed second argument.
 *
 * @author apete
 */
public final class PreconfiguredSecond<N extends Number> implements UnaryFunction<N> {

    private final BinaryFunction<N> myFunction;
    private final N myNumber;
    private final double myValue;

    @SuppressWarnings("unchecked")
    public PreconfiguredSecond(final BinaryFunction<N> aFunc, final double aSecondArg) {

        super();

        myFunction = aFunc;

        myNumber = (N) Double.valueOf(aSecondArg);
        myValue = aSecondArg;
    }

    public PreconfiguredSecond(final BinaryFunction<N> aFunc, final N aSecondArg) {

        super();

        myFunction = aFunc;

        myNumber = aSecondArg;
        myValue = aSecondArg.doubleValue();
    }

    @SuppressWarnings("unused")
    private PreconfiguredSecond() {
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

    public double invoke(final double aFirstArg) {
        return myFunction.invoke(aFirstArg, myValue);
    }

    public N invoke(final N aFirstArg) {
        return myFunction.invoke(aFirstArg, myNumber);
    }

}
