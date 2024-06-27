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
 * Certain waiting times.
 * Rounding errors.
 *
 * @author apete
 */
public class Uniform extends AbstractContinuous {

    private static final long serialVersionUID = -8198257914507986404L;

    /**
     * @return An integer: 0 <= ? < aLimit
     */
    public static int randomInteger(final int aLimit) {
        return (int) Math.floor(aLimit * Math.random());
    }

    /**
     * @return An integer: aLower <= ? < aHigher
     */
    public static int randomInteger(final int aLower, final int aHigher) {
        return aLower + Uniform.randomInteger(aHigher - aLower);
    }

    private final double myLower;
    private final double myRange;

    public Uniform() {
        this(ZERO, ONE);
    }

    public Uniform(final double aLower, final double aRange) {

        super();

        if (aRange <= ZERO) {
            throw new IllegalArgumentException("The range must be larger than 0.0!");
        }

        myLower = aLower;
        myRange = aRange;
    }

    public double getDistribution(final double aValue) {

        double retVal = ZERO;

        if ((aValue <= (myLower + myRange)) && (myLower <= aValue)) {
            retVal = (aValue - myLower) / myRange;
        } else if (myLower <= aValue) {
            retVal = ONE;
        }

        return retVal;
    }

    public double getExpected() {
        return myLower + (myRange / TWO);
    }

    public double getProbability(final double aValue) {

        double retVal = ZERO;

        if ((myLower <= aValue) && (aValue <= (myLower + myRange))) {
            retVal = ONE / myRange;
        }

        return retVal;
    }

    public double getQuantile(final double aProbality) {

        this.checkProbabilty(aProbality);

        return myLower + (aProbality * myRange);
    }

    @Override
    public double getVariance() {
        return (myRange * myRange) / TWELVE;
    }

    @Override
    protected double generate() {
        return myLower + (myRange * this.random().nextDouble());
    }
}
