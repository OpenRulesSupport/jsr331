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

import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.random.Distribution;
import org.ojalgo.random.SampleSet;
import org.ojalgo.series.primitive.PrimitiveSeries;

/**
 * @author apete
 */
public interface RandomProcess<D extends Distribution> {

    public static final class SimulationResults {

        private final double myInitialValue;

        private final Array2D<Double> myResults;

        /**
         * @param initialValue
         * @param results (Random values) scenarios/realisations/series in rows, and sample sets in columns.
         */
        public SimulationResults(final double initialValue, final Array2D<Double> results) {

            super();

            myInitialValue = initialValue;
            myResults = results;
        }

        @SuppressWarnings("unused")
        private SimulationResults() {

            super();

            myInitialValue = 0.0;
            myResults = null;
        }

        public int countSampleSets() {
            return myResults.getColDim();
        }

        public int countScenarios() {
            return myResults.getRowDim();
        }

        public double getInitialValue() {
            return myInitialValue;
        }

        public SampleSet getSampleSet(final int index) {
            return SampleSet.wrap(myResults.sliceColumn(0, index));
        }

        /**
         * A series representing one scenario. Each series has length
         * "number of simulation steps" + 1 as the series includes the initial value.
         */
        public PrimitiveSeries getScenario(final int index) {

            final Array1D<Double> tmpSlicedRow = myResults.sliceRow(index, 0);

            return new PrimitiveSeries() {

                public int size() {
                    return tmpSlicedRow.size() + 1;
                }

                @Override
                public double value(final int index) {
                    if (index == 0) {
                        return myInitialValue;
                    } else {
                        return tmpSlicedRow.doubleValue(index - 1);
                    }
                }

            };
        }

    }

    /**
     * Calling this method repeatedly gives the same ressult, unless
     * you call {@linkplain #step(double)} inbetween.
     * 
     * @param aStepSize How far into the future?
     * @return The distribution for the process value at that future time.
     */
    D getDistribution(double aStepSize);

    /**
     * @return An array of sample sets. The array has aNumberOfSteps
     * elements, and each sample set has aNumberOfRealisations samples.
     */
    RandomProcess.SimulationResults simulate(final int aNumberOfRealisations, final int aNumberOfSteps, final double aStepSize);

    /**
     * Calling this method repeatedly gives different, random, ressults.
     * Further, calling this method should update the process state so
     * that subsequent calls to getDistribution would be different.
     * 
     * @param aStepSize How far into the future?
     * @return The new (randomly generated) process value at that future time.
     */
    double step(double aStepSize);

}
