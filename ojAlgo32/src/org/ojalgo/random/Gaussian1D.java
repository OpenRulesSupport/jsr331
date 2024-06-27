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

import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.decomposition.CholeskyDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;

public class Gaussian1D extends Random1D<Normal> {

    private static final RandomNumber GAUSSIAN = new RandomNumber() {

        public double getExpected() {
            return ZERO;
        }

        @Override
        public double getStandardDeviation() {
            return ONE;
        }

        @Override
        public double getVariance() {
            return ONE;
        }

        @Override
        protected double generate() {
            return this.random().nextGaussian();
        }
    };

    public final int length;
    private final MatrixStore<Double> myCholeskiedCorrelations;

    public Gaussian1D(final Access2D<?> aCorrelationsMatrix) {

        super();

        final Cholesky<Double> tmpCholesky = CholeskyDecomposition.makePrimitive();
        tmpCholesky.compute(aCorrelationsMatrix);
        myCholeskiedCorrelations = tmpCholesky.getL();

        tmpCholesky.reset();

        length = myCholeskiedCorrelations.getMinDim();
    }

    /**
     * If the variables are uncorrelated.
     */
    public Gaussian1D(final int aLength) {

        super();

        myCholeskiedCorrelations = null;

        length = aLength;
    }

    @SuppressWarnings("unused")
    private Gaussian1D() {
        this(null);
    }

    /**
     * An array of correlated random numbers, provided that you gave a
     * correlations matrix to the constructor.
     * 
     * @see org.ojalgo.random.Random1D#generate()
     */
    @Override
    public Array1D<Double> generate() {

        final PrimitiveDenseStore tmpUncorrelated = PrimitiveDenseStore.FACTORY.makeZero(length, 1);

        for (int i = 0; i < length; i++) {
            tmpUncorrelated.set(i, 0, GAUSSIAN.generate());
        }

        if (myCholeskiedCorrelations != null) {
            return ((PrimitiveDenseStore) tmpUncorrelated.multiplyLeft(myCholeskiedCorrelations)).asList();
        } else {
            return tmpUncorrelated.asList();
        }
    }

    public int size() {
        return length;
    }

}
