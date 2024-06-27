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
package org.ojalgo.optimisation.quadratic;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.decomposition.LU;
import org.ojalgo.matrix.decomposition.LUDecomposition;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.decomposition.SingularValueDecomposition;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.optimisation.ExpressionsBasedModel;

/**
 * @author apete
 */
class EquationSystemSolver extends QuadraticSolver {

    private final LU<Double> myDelegateLU = LUDecomposition.makePrimitive();
    private final SingularValue<Double> myDelegateSingularValue = SingularValueDecomposition.makePrimitive();

    EquationSystemSolver(final ExpressionsBasedModel aModel, final Matrices matrices) {
        super(aModel, matrices);
    }

    @Override
    protected void initialise() {
        ;
    }

    @Override
    protected boolean needsAnotherIteration() {
        return false;
    }

    @Override
    protected void performIteration() throws RecoverableCondition {

        myDelegateLU.compute(this.getAE());

        final DecompositionStore<Double> tmpX = (DecompositionStore<Double>) this.getX();

        if (myDelegateLU.isSolvable()) {

            if (this.isDebug()) {
                BasicLogger.println(options.debug, "LU solvable");
            }

            myDelegateLU.solve(this.getBE(), tmpX);
            this.setState(State.FEASIBLE);

        } else {

            if (this.isDebug()) {
                BasicLogger.println(options.debug, "LU not solvable, trying SVD");
            }

            myDelegateSingularValue.compute(this.getAE());

            if (myDelegateSingularValue.isSolvable()) {

                if (this.isDebug()) {
                    BasicLogger.println(options.debug, "SVD solvable");
                }

                myDelegateSingularValue.solve(this.getBE(), tmpX);
                this.setState(State.FEASIBLE);

                final AggregatorFunction<Double> tmpFrobNormCalc = PrimitiveAggregator.getCollection().norm2();
                final MatrixStore<Double> tmpSlack = this.getSE();
                tmpSlack.visitAll(tmpFrobNormCalc);

                if (!options.deprSolution.isZero(tmpFrobNormCalc.doubleValue())) {

                    if (this.isDebug()) {
                        BasicLogger.println(options.debug, "Solution not accurate enough!");
                    }

                    this.resetX();
                    this.setState(State.FAILED);
                }

            } else {

                if (this.isDebug()) {
                    BasicLogger.println(options.debug, "SVD not solvable");
                }

                this.resetX();
                this.setState(State.FAILED);

                throw new RecoverableCondition("Couldn't solve this problem!");
            }
        }

    }
}
