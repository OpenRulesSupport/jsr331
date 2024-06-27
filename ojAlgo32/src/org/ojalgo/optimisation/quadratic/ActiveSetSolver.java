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

import org.ojalgo.RecoverableCondition;
import org.ojalgo.matrix.store.AboveBelowStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.RowsStore;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.type.IndexSelector;

/**
 * @author apete
 */
class ActiveSetSolver extends QuadraticSolver {

    private final IndexSelector myActivator;

    ActiveSetSolver(final ExpressionsBasedModel aModel, final Matrices aBuilder) {

        super(aModel, aBuilder);

        if (this.hasInequalityConstraints()) {
            myActivator = new IndexSelector(this.countInequalityConstraints());
        } else {
            myActivator = new IndexSelector(0);
        }

        int tmpIterationsLimit = Math.max(this.getAI().getRowDim(), this.getAI().getColDim());
        tmpIterationsLimit = (int) (9.0 + Math.sqrt(tmpIterationsLimit));
        tmpIterationsLimit = tmpIterationsLimit * tmpIterationsLimit;

        options.iterations = tmpIterationsLimit;

    }

    private QuadraticSolver buildIterationSolver() {

        MatrixStore<Double> tmpSubAE = null;
        MatrixStore<Double> tmpSubBE = null;
        final MatrixStore<Double> tmpSubQ = this.getQ();
        final MatrixStore<Double> tmpSubC = this.getC();

        final int[] tmpActivator = myActivator.getIncluded();

        if (tmpActivator.length == 0) {
            if (this.hasEqualityConstraints()) {
                tmpSubAE = this.getAE();
                tmpSubBE = this.getBE();
            } else {
                tmpSubAE = null;
                tmpSubBE = null;
            }
        } else {
            if (this.hasEqualityConstraints()) {
                tmpSubAE = new AboveBelowStore<Double>(this.getAE(), new RowsStore<Double>(this.getAI(), tmpActivator));
                tmpSubBE = new AboveBelowStore<Double>(this.getBE(), new RowsStore<Double>(this.getBI(), tmpActivator));
            } else {
                tmpSubAE = new RowsStore<Double>(this.getAI(), tmpActivator);
                tmpSubBE = new RowsStore<Double>(this.getBI(), tmpActivator);
            }
        }

        final Builder retVal = new Builder(tmpSubQ, tmpSubC);
        if ((tmpSubAE != null) && (tmpSubBE != null)) {
            retVal.equalities(tmpSubAE, tmpSubBE);
        }
        return retVal.build();
    }

    /**
     * Find the minimum (largest negative) lagrange multiplier - for the
     * active inequalities - to potentially deactivate.
     */
    private int suggestConstraintToExclude() {

        int retVal = -1;

        final int[] tmpIncluded = myActivator.getIncluded();
        final int tmpLastIncluded = myActivator.getLastIncluded();
        int tmpIndexOfLast = -1;

        double tmpMin = POSITIVE_INFINITY;
        double tmpVal;

        final MatrixStore<Double> tmpLI = this.getLI(tmpIncluded);

        if (this.isDebug() && (tmpLI.size() > 0)) {
            BasicLogger.println(options.debug, "Looking for the largest negative lagrange multiplier among these: {}.", tmpLI.copy().toString());
        }

        for (int i = 0; i < tmpLI.getRowDim(); i++) {
            if (tmpIncluded[i] != tmpLastIncluded) {

                tmpVal = tmpLI.doubleValue(i, 0);

                if ((tmpVal < ZERO) && !options.deprSolution.isZero(tmpVal) && (tmpVal < tmpMin)) {
                    tmpMin = tmpVal;
                    retVal = i;
                    if (this.isDebug()) {
                        BasicLogger.println(options.debug, "Best so far: {} @ {}.", tmpMin, retVal);
                    }
                }

            } else {

                tmpIndexOfLast = i;
            }
        }

        if ((retVal < 0) && (tmpIndexOfLast >= 0)) {

            tmpVal = tmpLI.doubleValue(tmpIndexOfLast, 0);

            if ((tmpVal < ZERO) && !options.deprSolution.isZero(tmpVal) && (tmpVal < tmpMin)) {
                tmpMin = tmpVal;
                retVal = tmpIndexOfLast;
                if (this.isDebug()) {
                    BasicLogger.println(options.debug, "Only the last included needs to be excluded: {} @ {}.", tmpMin, retVal);
                }
            }
        }

        return retVal >= 0 ? tmpIncluded[retVal] : retVal;
    }

    /**
     * Find minimum (largest negative) slack - for the inactive
     * inequalities - to potentially activate. Negative slack means the
     * constraint is violated. Need to make sure it is enforced by
     * activating it.
     */
    private int suggestConstraintToInclude() {

        int retVal = -1;

        final int[] tmpExcluded = myActivator.getExcluded();
        final int tmpLastExcluded = myActivator.getLastExcluded();
        int tmpIndexOfLast = -1;

        double tmpMin = POSITIVE_INFINITY;
        double tmpVal;

        final MatrixStore<Double> tmpSI = this.getSI(tmpExcluded);

        if (this.isDebug() && (tmpSI.size() > 0)) {
            BasicLogger.println(options.debug, "Looking for the largest negative slack among these: {}.", tmpSI.copy().toString());
        }

        for (int i = 0; i < tmpSI.getRowDim(); i++) {
            if (tmpExcluded[i] != tmpLastExcluded) {

                tmpVal = tmpSI.doubleValue(i, 0);

                if ((tmpVal < ZERO) && !options.slack.isZero(tmpVal) && (tmpVal < tmpMin)) {
                    tmpMin = tmpVal;
                    retVal = i;
                    if (this.isDebug()) {
                        BasicLogger.println(options.debug, "Best so far: {} @ {}.", tmpMin, retVal);
                    }
                }

            } else {

                tmpIndexOfLast = i;
            }
        }

        if ((retVal < 0) && (tmpIndexOfLast >= 0)) {

            tmpVal = tmpSI.doubleValue(tmpIndexOfLast, 0);

            if ((tmpVal < ZERO) && !options.slack.isZero(tmpVal) && (tmpVal < tmpMin)) {
                tmpMin = tmpVal;
                retVal = tmpIndexOfLast;
                if (this.isDebug()) {
                    BasicLogger.println(options.debug, "Only the last excluded needs to be included: {} @ {}.", tmpMin, retVal);
                }
            }
        }

        return retVal >= 0 ? tmpExcluded[retVal] : retVal;
    }

    @Override
    protected void initialise() {

        myActivator.excludeAll();

        final int[] tmpExcluded = myActivator.getExcluded();

        //        final MatrixStore<Double> tmpSI = this.getSI(tmpExcluded);
        //        if (tmpSI != null) {
        //
        //            for (int i = 0; i < tmpSI.getRowDim(); i++) {
        //                if (options.slack.isZero(tmpSI.doubleValue(i, 0))) {
        //                    myActivator.include(tmpExcluded[i]);
        //                }
        //            }
        //        }
    }

    @Override
    protected boolean needsAnotherIteration() {

        if (this.isDebug()) {
            BasicLogger.println(options.debug);
            BasicLogger.println(options.debug, "NeedsAnotherIteration?");
            BasicLogger.println(options.debug, myActivator.toString());
        }

        int tmpToInclude = -1;
        int tmpToExclude = -1;

        if (this.hasInequalityConstraints()) {
            tmpToInclude = this.suggestConstraintToInclude();
            tmpToExclude = this.suggestConstraintToExclude();
        }

        if (this.isDebug()) {
            BasicLogger.println(options.debug, "Suggested to include: {}", tmpToInclude);
            BasicLogger.println(options.debug, "Suggested to exclude: {}", tmpToExclude);
        }

        if (tmpToExclude == -1) {
            if (tmpToInclude == -1) {
                // Suggested to do nothing
                this.setState(State.OPTIMAL);
                return false;
            } else {
                // Only suggested to include
                myActivator.include(tmpToInclude);
                this.setState(State.APPROXIMATE);
                return true;
            }
        } else {
            if (tmpToInclude == -1) {
                // Only suggested to exclude
                myActivator.exclude(tmpToExclude);
                this.setState(State.APPROXIMATE);
                return true;
            } else {
                // Suggested both to exclude and include
                myActivator.exclude(tmpToExclude);
                myActivator.include(tmpToInclude);
                this.setState(State.APPROXIMATE);
                return true;
            }
        }
    }

    @Override
    protected void performIteration() throws RecoverableCondition {

        final QuadraticSolver tmpSolver = this.buildIterationSolver();
        final Optimisation.Result tmpResult = tmpSolver.solve();

        final int[] tmpIncluded = myActivator.getIncluded();

        final int tmpCountVariables = this.countVariables();
        final int tmpCountEqualityConstraints = this.countEqualityConstraints();
        final int tmpCountActiveInequalityConstraints = tmpIncluded.length;

        if (tmpResult.getState().isFeasible()) {

            final MatrixStore<Double> tmpSolutionX = tmpSolver.getSolutionX();
            final MatrixStore<Double> tmpSolutionLE = tmpSolver.getSolutionLE();

            for (int i = 0; i < tmpCountVariables; i++) {
                this.setX(i, tmpSolutionX.doubleValue(i));
            }

            for (int i = 0; i < tmpCountEqualityConstraints; i++) {
                this.setLE(i, tmpSolutionLE.doubleValue(i));
            }

            for (int i = 0; i < tmpCountActiveInequalityConstraints; i++) {
                this.setLI(tmpIncluded[i], tmpSolutionLE.doubleValue(tmpCountEqualityConstraints + i));
            }

            this.setState(State.APPROXIMATE);

        } else if (tmpCountActiveInequalityConstraints >= 1) {

            if (myActivator.isLastIncluded()) {
                myActivator.revertLastInclusion();
            }

            myActivator.shrink();

            if (this.isDebug()) {
                BasicLogger.println(options.debug, "Did shrink!");
                BasicLogger.println(options.debug, myActivator.toString());
            }

            this.performIteration();

        } else {

            this.resetX();
            this.setState(State.INFEASIBLE);

            throw new RecoverableCondition("Not able to solve this problem!");
        }

    }

}
