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
package org.ojalgo.optimisation.integer;

import static org.ojalgo.constant.PrimitiveMath.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.concurrent.DaemonPoolExecutor;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.GenericSolver;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.type.TypeUtils;

/**
 * IntegerSolver
 *
 * @author apete
 */
public final class IntegerSolver extends GenericSolver {

    public static IntegerSolver make(final ExpressionsBasedModel aModel) {
        return new IntegerSolver(aModel);
    }

    static Optimisation.Result solve(final ExpressionsBasedModel aModel, final IntegerSolver aSolver) {

        if (aSolver.isDebug()) {
            BasicLogger.println(aSolver.options.debug, "IntegerIterationModel");
            BasicLogger.println(aSolver.options.debug, aModel.toString());
        }

        if (aSolver.isOkToContinue()) {

            final Optimisation.Result tmpResult = aModel.getDefaultSolver().solve();
            try {
                aSolver.incrementIterationsCount();
            } catch (final RecoverableCondition anException) {
                anException.printStackTrace();
            }

            if (tmpResult.getState().isNotLessThan(State.OPTIMAL)) {

                if (aSolver.isDebug()) {
                    BasicLogger.println(aSolver.options.debug, "Solved to optimality!");
                }

                final int tmpBranchIndex = aSolver.identifyNonIntegerVariable(tmpResult);
                final double tmpSolutionValue = aSolver.evaluateSolution(tmpResult);

                if (tmpBranchIndex == -1) {

                    if (aSolver.isDebug()) {
                        BasicLogger.println(aSolver.options.debug, "Integer solution! Store it among the others, and stop this branch!");
                    }

                    final Optimisation.Result tmpArg0 = new Optimisation.Result(Optimisation.State.APPROXIMATE, tmpSolutionValue, tmpResult);
                    aSolver.add(tmpArg0);

                    final Optimisation.Result tmpInterRes = aSolver.getBestResultSoFar();

                    if (tmpInterRes != null) {
                        if (aModel.isMinimisation()) {
                            aModel.getObjectiveExpression().upper(aSolver.options.problem.toBigDecimal(tmpInterRes.getValue()));
                        } else {
                            aModel.getObjectiveExpression().lower(aSolver.options.problem.toBigDecimal(tmpInterRes.getValue()));
                        }
                    }

                } else {

                    if (aSolver.isDebug()) {
                        BasicLogger.println(aSolver.options.debug, "Not an Integer Solution");
                    }

                    final double tmpVariableValue = tmpResult.doubleValue(tmpBranchIndex);

                    if (aSolver.isDebug()) {
                        BasicLogger.println(aSolver.options.debug, "Not an Integer Solution: " + tmpSolutionValue + ", branching on " + tmpBranchIndex + " @ "
                                + tmpVariableValue);
                    }

                    if (aSolver.isGoodEnoughToContinueBranching(tmpSolutionValue)) {

                        if (aSolver.isDebug()) {
                            BasicLogger.println(aSolver.options.debug, "Still hope for a better integer solution");
                        }

                        final BigDecimal tmpUpperLimitForLowerBranch = new BigDecimal(Math.floor(tmpVariableValue));
                        final BigDecimal tmpLowerLimitForUpperBranch = new BigDecimal(Math.ceil(tmpVariableValue));

                        if (aSolver.isDebug()) {
                            BasicLogger.println(aSolver.options.debug, "<= {} & {} <=", tmpUpperLimitForLowerBranch, tmpLowerLimitForUpperBranch);
                        }

                        final boolean tmpBranchInSeparateThread = DaemonPoolExecutor.INSTANCE.isDaemonAvailable();

                        if (tmpBranchInSeparateThread) {

                            final Future<?> tmpLowerBranchProblem = DaemonPoolExecutor.INSTANCE.submit(new Runnable() {

                                public void run() {

                                    final ExpressionsBasedModel tmpLowerBranchModel = aModel.copy();
                                    tmpLowerBranchModel.getVariable(tmpBranchIndex).upper(tmpUpperLimitForLowerBranch);

                                    IntegerSolver.solve(tmpLowerBranchModel, aSolver);
                                }
                            });

                            final Future<?> tmpUpperBranchProblem = DaemonPoolExecutor.INSTANCE.submit(new Runnable() {

                                public void run() {

                                    final ExpressionsBasedModel tmpUpperBranchModel = aModel.copy();
                                    tmpUpperBranchModel.getVariable(tmpBranchIndex).lower(tmpLowerLimitForUpperBranch);
                                    IntegerSolver.solve(tmpUpperBranchModel, aSolver);
                                }
                            });

                            try {
                                tmpLowerBranchProblem.get();
                                tmpUpperBranchProblem.get();
                            } catch (final InterruptedException anException) {
                                anException.printStackTrace();
                            } catch (final ExecutionException anException) {
                                anException.printStackTrace();
                            }

                        } else {

                            final BigDecimal tmpOldLowerLimit = aModel.getVariable(tmpBranchIndex).getLowerLimit();
                            final BigDecimal tmpOldUpperLimit = aModel.getVariable(tmpBranchIndex).getUpperLimit();

                            final ExpressionsBasedModel tmpLowerBranchModel = aModel;
                            tmpLowerBranchModel.getVariable(tmpBranchIndex).upper(tmpUpperLimitForLowerBranch);
                            IntegerSolver.solve(tmpLowerBranchModel, aSolver);
                            tmpLowerBranchModel.getVariable(tmpBranchIndex).upper(tmpOldUpperLimit);

                            final ExpressionsBasedModel tmpUpperBranchModel = aModel;
                            tmpUpperBranchModel.getVariable(tmpBranchIndex).lower(tmpLowerLimitForUpperBranch);
                            IntegerSolver.solve(tmpUpperBranchModel, aSolver);
                            tmpUpperBranchModel.getVariable(tmpBranchIndex).lower(tmpOldLowerLimit);
                        }

                    } else {

                        if (aSolver.isDebug()) {
                            BasicLogger.println(aSolver.options.debug, "Can't find better integer solutions - stop this branch!");
                        }

                    }
                }
            } else {

                if (aSolver.isDebug()) {
                    BasicLogger.println(aSolver.options.debug, "Failed to solve problem - stop this branch!");
                }

                if (aSolver.getBestResultSoFar() == null) {

                    final Optimisation.State tmpState = tmpResult.getState();
                    final double tmpValue = aModel.isMinimisation() ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;

                    final Optimisation.Result tmpArg0 = new Optimisation.Result(tmpState, tmpValue, tmpResult);

                    aSolver.add(tmpArg0);
                }

            }
        } else {

            if (aSolver.isDebug()) {
                BasicLogger.println(aSolver.options.debug, "Reached iterations or time limit - stop!");
            }
        }

        return aSolver.getBestResultSoFar();
    }

    private boolean[] myIntegers;
    private final SortedSet<Optimisation.Result> myIntermediateResults = Collections.synchronizedSortedSet(new TreeSet<Optimisation.Result>());

    IntegerSolver(final ExpressionsBasedModel aModel) {

        super(aModel);

        if (aModel != null) {
            myIntegers = aModel.getIntegers();
        } else {
            myIntegers = new boolean[0];
        }
        myIntermediateResults.clear();
    }

    @Override
    public final Optimisation.Result solve() {

        this.resetIterationsCount();

        IntegerSolver.solve(this.getModel().copy().relax(), this);

        Optimisation.Result retVal = this.getBestResultSoFar();

        if (retVal.getState().isNotLessThan(State.APPROXIMATE)) {

            final Optimisation.State tmpState = State.OPTIMAL;
            final double tmpValue = retVal.getValue();
            final Optimisation.Result tmpSolution = retVal;

            retVal = new Optimisation.Result(tmpState, tmpValue, tmpSolution);

        }

        final Variable[] tmpVariables = this.getModel().getVariables();
        for (int v = 0; v < tmpVariables.length; v++) {
            tmpVariables[v].setValue(TypeUtils.toBigDecimal(retVal.get(v), options.result));
        }

        return retVal;

    }

    protected final double evaluateSolution(final Optimisation.Result result) {

        final int tmpCountVariables = this.getModel().countVariables();

        return this.evaluateFunction(new Access1D<Double>() {

            public double doubleValue(final int anInd) {
                return result.doubleValue(anInd);
            }

            public Double get(final int anInd) {
                return this.get(anInd);
            }

            public Iterator<Double> iterator() {
                return new Iterator1D<Double>(this);
            }

            public int size() {
                return tmpCountVariables;
            }

        });
    }

    @Override
    protected boolean needsAnotherIteration() {
        // TODO Auto-generated method stub
        return false;
    }

    synchronized boolean add(final Optimisation.Result aResult) {

        //    BasicLogger.logDebug("Latest solution: {}", aResult);

        final boolean retVal = myIntermediateResults.add(aResult);
        final Optimisation.Result tmpBestResultSoFar = this.getBestResultSoFar();

        //    BasicLogger.logDebug("Best solution  : {}", tmpBestResultSoFar);

        return retVal;
    }

    synchronized Optimisation.Result getBestResultSoFar() {
        if (myIntermediateResults.size() == 0) {
            return null;
        } else if (this.getModel().isMaximisation()) {
            return myIntermediateResults.last();
        } else if (this.getModel().isMinimisation()) {
            return myIntermediateResults.first();
        } else {
            return null;
        }
    }

    synchronized int identifyNonIntegerVariable(final Optimisation.Result aSolution) {

        int retVal = -1;

        final double tmpTolerance = options.deprSolution.getError();

        double tmpValue;
        double tmpFraction;
        double tmpMaxFraction = ZERO;

        for (int i = 0; i < myIntegers.length; i++) {

            tmpValue = aSolution.doubleValue(i);
            tmpFraction = Math.abs(tmpValue - Math.rint(tmpValue));

            if (myIntegers[i] && !(tmpFraction <= tmpTolerance) && (tmpFraction > tmpMaxFraction)) {
                retVal = i;
                tmpMaxFraction = tmpFraction;
            }
        }

        return retVal;
    }

    synchronized boolean isGoodEnoughToContinueBranching(final double aValue) {

        final Optimisation.Result tmpResult = this.getBestResultSoFar();

        if (tmpResult == null) {
            return true;
        } else {
            if (this.getModel().isMinimisation()) {
                return (aValue < tmpResult.getValue());
            } else {
                return (aValue > tmpResult.getValue());
            }
        }
    }

}
