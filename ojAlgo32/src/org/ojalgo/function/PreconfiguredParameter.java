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
 * A {@linkplain ParameterFunction} with a set/fixed parameter.
 *
 * @author apete
 */
public final class PreconfiguredParameter<N extends Number> implements UnaryFunction<N> {

    private final ParameterFunction<N> myFunction;
    private final int myParameter;

    public PreconfiguredParameter(final ParameterFunction<N> aFunc, final int aParam) {

        super();

        myFunction = aFunc;
        myParameter = aParam;
    }

    @SuppressWarnings("unused")
    private PreconfiguredParameter() {
        this(null, 0);
    }

    public final ParameterFunction<N> getFunction() {
        return myFunction;
    }

    public final int getParameter() {
        return myParameter;
    }

    public double invoke(final double aFirstArg) {
        return myFunction.invoke(aFirstArg, myParameter);
    }

    public N invoke(final N aFirstArg) {
        return myFunction.invoke(aFirstArg, myParameter);
    }

}
