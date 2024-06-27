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
package org.ojalgo.random;

import static org.ojalgo.constant.PrimitiveMath.*;

/**
 * Distribution of number of points in random point process under certain simple
 * assumptions. Approximation to the binomial distribution when aCount is
 * large and aProbability is small. aLambda = aCount * aProbability.
 *
 * @author apete
 */
public class Poisson extends AbstractDiscrete {

    private static final long serialVersionUID = -5382163736545207782L;

    private final double myLambda;

    public Poisson() {
        this(ONE);
    }

    public Poisson(final double aLambda) {

        super();

        myLambda = aLambda;
    }

    public double getExpected() {
        return myLambda;
    }

    public double getProbability(final int aVal) {
        return (Math.exp(-myLambda) * Math.pow(myLambda, aVal)) / RandomUtils.factorial(aVal);
    }

    @Override
    public double getVariance() {
        return myLambda;
    }

    @Override
    protected double generate() {

        double retVal = -ONE, tmpVal = ZERO;

        while (tmpVal <= ONE) {

            retVal++;

            tmpVal -= Math.log(this.random().nextDouble()) / myLambda;
        }

        return retVal;
    }

}
