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

import static org.ojalgo.constant.PrimitiveMath.*;
import static org.ojalgo.function.implementation.PrimitiveFunction.*;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.matrix.store.AboveBelowStore;
import org.ojalgo.matrix.store.LeftRightStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.ZeroStore;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;

/**
 * @author apete
 */
class LagrangeSolver extends QuadraticSolver {

    LagrangeSolver(final ExpressionsBasedModel aModel, final Matrices matrices) {
        super(aModel, matrices);
    }

    private QuadraticSolver buildIterationSolver(final boolean addSmallDiagonal) {

        MatrixStore<Double> tmpQ = this.getQ();
        final MatrixStore<Double> tmpC = this.getC();

        if (addSmallDiagonal) {

            final PhysicalStore<Double> tmpCopyQ = tmpQ.copy();

            final double tmpLargest = tmpCopyQ.aggregateAll(Aggregator.LARGEST);
            final double tmpRelativelySmall = MACHINE_DOUBLE_ERROR * tmpLargest;
            final double tmpPracticalLimit = MACHINE_DOUBLE_ERROR + IS_ZERO;
            final double tmpSmallToAdd = Math.max(tmpRelativelySmall, tmpPracticalLimit);

            final UnaryFunction<Double> tmpFunc = ADD.second(tmpSmallToAdd);

            tmpCopyQ.modifyDiagonal(0, 0, tmpFunc);
            tmpQ = tmpCopyQ;
        }

        if (this.hasEqualityConstraints()) {

            final MatrixStore<Double> tmpAE = this.getAE();
            final MatrixStore<Double> tmpBE = this.getBE();

            final int tmpZeroSize = tmpAE.getRowDim();

            final MatrixStore<Double> tmpUpperLeftAE = tmpQ;
            final MatrixStore<Double> tmpUpperRightAE = tmpAE.builder().transpose().build();
            final MatrixStore<Double> tmpLowerLefAE = tmpAE;
            final MatrixStore<Double> tmpLowerRightAE = ZeroStore.makePrimitive(tmpZeroSize, tmpZeroSize);

            final MatrixStore<Double> tmpSubAE = new AboveBelowStore<Double>(new LeftRightStore<Double>(tmpUpperLeftAE, tmpUpperRightAE),
                    new LeftRightStore<Double>(tmpLowerLefAE, tmpLowerRightAE));

            final MatrixStore<Double> tmpUpperBE = tmpC;
            final MatrixStore<Double> tmpLowerBE = tmpBE;

            final MatrixStore<Double> tmpSubBE = new AboveBelowStore<Double>(tmpUpperBE, tmpLowerBE);

            return new Builder().equalities(tmpSubAE, tmpSubBE).build();

        } else {

            return new Builder().equalities(tmpQ, tmpC).build();
        }
    }

    private void extractSolution(final QuadraticSolver aSolver) {

        final MatrixStore<Double> tmpSolutionX = aSolver.getSolutionX();

        final int tmpCountVariables = this.countVariables();
        final int tmpCountEqualityConstraints = this.countEqualityConstraints();

        for (int i = 0; i < tmpCountVariables; i++) {
            this.setX(i, tmpSolutionX.doubleValue(i));
        }

        for (int i = 0; i < tmpCountEqualityConstraints; i++) {
            this.setLE(i, tmpSolutionX.doubleValue(tmpCountVariables + i));
        }
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

        QuadraticSolver tmpSolver = this.buildIterationSolver(false);

        Optimisation.Result tmpResult = tmpSolver.solve();

        if (tmpResult.getState().isNotLessThan(State.FEASIBLE)) {

            this.extractSolution(tmpSolver);

            this.setState(State.OPTIMAL);

        } else {

            tmpSolver = this.buildIterationSolver(true);
            tmpResult = tmpSolver.solve();

            if (tmpResult.getState().isNotLessThan(State.FEASIBLE)) {

                this.extractSolution(tmpSolver);

                this.setState(State.OPTIMAL);

            } else {

                this.resetX();
                this.setState(State.INFEASIBLE);
            }
        }
    }

}
