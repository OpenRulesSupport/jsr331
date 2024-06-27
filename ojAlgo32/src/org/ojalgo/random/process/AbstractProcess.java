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
package org.ojalgo.random.process;

import static org.ojalgo.constant.PrimitiveMath.*;

import org.ojalgo.array.Array2D;
import org.ojalgo.random.ContinuousDistribution;
import org.ojalgo.random.Distribution;

abstract class AbstractProcess<D extends ContinuousDistribution> implements RandomProcess<D> {

    private double myValue;

    @SuppressWarnings("unused")
    private AbstractProcess() {
        this(NaN);
    }

    protected AbstractProcess(final double initialValue) {

        super();

        myValue = initialValue;
    }

    /**
     * Equivalent to calling {@link #getDistribution(double)} with argumant
     * <code>1.0</code>, and then {@link Distribution#getExpected()}.
     */
    public final double getExpected() {
        return this.getExpected(ONE);
    }

    /**
     * The same thing can be achieved by first calling {@link #getDistribution(double)}
     * with argumant <code>1.0</code>, and then {@link ContinuousDistribution#getQuantile(double)}
     * (but with different input argument).
     */
    public final double getLowerConfidenceQuantile(final double aConfidence) {
        return this.getLowerConfidenceQuantile(ONE, aConfidence);
    }

    /**
     * Equivalent to calling {@link #getDistribution(double)} with argumant
     * <code>1.0</code>, and then {@link Distribution#getStandardDeviation()}.
     */
    public final double getStandardDeviation() {
        return this.getStandardDeviation(ONE);
    }

    /**
     * The same thing can be achieved by first calling {@link #getDistribution(double)}
     * with argumant <code>1.0</code>, and then {@link ContinuousDistribution#getQuantile(double)}
     * (but with different input argument).
     */
    public final double getUpperConfidenceQuantile(final double aConfidence) {
        return this.getUpperConfidenceQuantile(ONE, aConfidence);
    }

    public final double getValue() {
        return myValue;
    }

    /**
     * Equivalent to calling {@link #getDistribution(double)} with argumant
     * <code>1.0</code>, and then {@link Distribution#getVariance()}.
     */
    public final double getVariance() {
        return this.getVariance(ONE);
    }

    public final void setValue(final double newValue) {
        myValue = newValue;
    }

    /**
     * @return An array of sample sets. The array has aNumberOfSteps
     * elements, and each sample set has aNumberOfRealisations samples.
     */
    public final RandomProcess.SimulationResults simulate(final int aNumberOfRealisations, final int aNumberOfSteps, final double aStepSize) {

        final double tmpInitialState = this.getValue();

        final Array2D<Double> tmpRealisationValues = Array2D.PRIMITIVE.makeZero(aNumberOfRealisations, aNumberOfSteps);

        for (int r = 0; r < aNumberOfRealisations; r++) {
            for (int s = 0; s < aNumberOfSteps; s++) {
                tmpRealisationValues.set(r, s, this.step(aStepSize));
            }
            this.setValue(tmpInitialState);
        }

        return new RandomProcess.SimulationResults(tmpInitialState, tmpRealisationValues);
    }

    public final double step(final double aStepSize) {
        return this.step(aStepSize, this.getNormalisedRandomIncrement());
    }

    protected abstract double getNormalisedRandomIncrement();

    protected abstract double step(final double aStepSize, final double aNormalisedRandomIncrement);

    abstract double getExpected(double aStepSize);

    abstract double getLowerConfidenceQuantile(double aStepSize, final double aConfidence);

    abstract double getStandardDeviation(double aStepSize);

    abstract double getUpperConfidenceQuantile(double aStepSize, final double aConfidence);

    abstract double getVariance(double aStepSize);

}
