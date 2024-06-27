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
package org.ojalgo.optimisation.linear;

import static org.ojalgo.constant.PrimitiveMath.*;
import static org.ojalgo.function.implementation.PrimitiveFunction.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.ojalgo.access.Access1D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.function.PreconfiguredSecond;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.ZeroStore;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.optimisation.Expression.Index;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * SimplexTableauSolver
 *
 * @author apete
 */
final class SimplexTableauSolver extends LinearSolver {

    static final class PivotPoint {

        int row = -1;
        int col = -1;

        PivotPoint() {

            super();

            this.reset();
        }

        void reset() {
            row = -1;
            col = -1;
        }

    }

    private int myObjectiveRow; // myCountConstraints+1 in Phase1 and myCountConstraints in Phase2

    private int myPivotCol;
    private int myPivotRow;
    private final int[] myBasis;
    private final PhysicalStore<Double> myTransposedTableau;

    SimplexTableauSolver(final ExpressionsBasedModel aModel, final Matrices matrices, final int[] suggestedPartialBasis) {

        super(aModel, matrices);

        final int tmpConstraintsCount = this.countConstraints();

        final MatrixStore.Builder<Double> tmpTableauBuilder = ZeroStore.makePrimitive(1, 1).builder();
        tmpTableauBuilder.left(matrices.getC().transpose());

        if (tmpConstraintsCount >= 1) {
            tmpTableauBuilder.above(matrices.getAE(), matrices.getBE());
        }
        tmpTableauBuilder.below(1);
        myTransposedTableau = tmpTableauBuilder.build().transpose();

        myObjectiveRow = tmpConstraintsCount + 1; // Phase1 objective function row

        for (int i = 0; i < tmpConstraintsCount; i++) {
            myTransposedTableau.caxpy(NEG, i, myObjectiveRow, 0);
        }

        myBasis = MatrixUtils.makeIncreasingRange(-tmpConstraintsCount, tmpConstraintsCount);

        options.iterations = myTransposedTableau.size();

        if (this.isDebug() && this.isTableauPrintable()) {
            this.printTableau("Tableau Created");
        }
    }

    @Override
    public Optimisation.Result solve() {

        while (this.needsAnotherIteration()) {

            this.performIteration(myPivotRow, myPivotCol);

            if (this.isDebug() && this.isTableauPrintable()) {
                this.printTableau("Tableau Iteration");
            }
        }

        final State tmpState = this.getState();
        final PhysicalStore<Double> tmpSolution = this.extractSolution();

        if (this.isModelSet()) {

            final ExpressionsBasedModel tmpModel = this.getModel();

            final Variable[] tmpVariables = tmpModel.getVariables();
            for (int v = 0; v < tmpVariables.length; v++) {
                tmpVariables[v].setValue(BigMath.ZERO);
            }

            final List<Variable> tmpPositiveVariables = tmpModel.getPositiveVariables();
            for (int p = 0; p < tmpPositiveVariables.size(); p++) {
                final Index tmpIndex = tmpModel.indexOf(tmpPositiveVariables.get(p));
                tmpVariables[tmpIndex.index].setValue(TypeUtils.toBigDecimal(tmpSolution.get(p), options.result));
            }

            final List<Variable> tmpNegativeVariables = tmpModel.getNegativeVariables();
            for (int n = 0; n < tmpNegativeVariables.size(); n++) {
                final Index tmpIndex = tmpModel.indexOf(tmpNegativeVariables.get(n));
                BigDecimal tmpValue = tmpVariables[tmpIndex.index].getValue();
                final BigDecimal tmpDiff = TypeUtils.toBigDecimal(tmpSolution.get(tmpPositiveVariables.size() + n), options.result);
                tmpValue = tmpValue.subtract(tmpDiff);
                tmpVariables[tmpIndex.index].setValue(tmpValue);
            }

            final Access1D<BigDecimal> tmpValues = tmpModel.getVariableValues();

            final double tmpValue = this.evaluateFunction(tmpValues);

            return new Optimisation.Result(tmpState, tmpValue, tmpValues);

        } else {

            final double tmpValue = this.evaluateFunction(tmpSolution);

            return new Optimisation.Result(tmpState, tmpValue, tmpSolution);
        }
    }

    private int countBasicArtificials() {
        int retVal = 0;
        final int tmpLength = myBasis.length;
        for (int i = 0; i < tmpLength; i++) {
            if (myBasis[i] < 0) {
                retVal++;
            }
        }
        return retVal;
    }

    /**
     * Extract solution MatrixStore from the tableau
     */
    private PhysicalStore<Double> extractSolution() {

        final int tmpCountVariables = this.countVariables();

        int tmpIndex;
        final int tmpLength = myBasis.length;
        for (int i = 0; i < tmpLength; i++) {
            tmpIndex = myBasis[i];
            if (tmpIndex >= 0) {
                this.setX(tmpIndex, myTransposedTableau.doubleValue(tmpCountVariables, i));
            }
        }

        return this.getX();
    }

    private int findColumnInRow(final int aRow, final boolean allowZero) {

        int retVal = -1;

        double tmpVal;
        double tmpMinVal = allowZero ? ZERO : -options.problem.getError();

        int tmpCol;
        final int[] tmpExcluded = this.getExcluded();

        for (int e = 0; e < tmpExcluded.length; e++) {
            tmpCol = tmpExcluded[e];
            tmpVal = myTransposedTableau.doubleValue(tmpCol, aRow);
            if ((tmpVal < tmpMinVal) || (allowZero && (tmpVal <= tmpMinVal))) {
                retVal = tmpCol;
                tmpMinVal = tmpVal;
                if (this.isDebug()) {
                    BasicLogger.println(options.debug, "Col: {}\t=>\tReduced Contribution Weight: {}.", tmpCol, tmpVal);
                }
            }
        }

        return retVal;
    }

    private int findNextPivotRow() {

        if (this.isDebug()) {
            BasicLogger.println(options.debug, "findNextPivotRow");
        }

        int retVal = -1;
        double tmpNumer, tmpDenom, tmpRatio;
        double tmpMinRatio = MAX_VALUE;

        final NumberContext tmpProblemContext = options.problem;

        final int tmpDenomCol = myPivotCol;
        final int tmpNumerCol = this.countVariables();

        final int tmpLimit = this.countConstraints();
        for (int i = 0; i < tmpLimit; i++) {

            tmpDenom = myTransposedTableau.doubleValue(tmpDenomCol, i);

            if (!tmpProblemContext.isZero(tmpDenom)) {

                tmpNumer = myTransposedTableau.doubleValue(tmpNumerCol, i);
                tmpRatio = tmpNumer / tmpDenom;

                if ((tmpRatio >= ZERO) && (tmpRatio < tmpMinRatio) && !(tmpProblemContext.isZero(tmpRatio) && (tmpDenom < ZERO))) {
                    retVal = i;
                    tmpMinRatio = tmpRatio;

                    if (this.isDebug()) {
                        BasicLogger.println(options.debug, "Row: {}\t=>\tRatio: {},\tNumerator/RHS: {}, \tDenominator/Pivot: {}.", i, tmpRatio, tmpNumer,
                                tmpDenom);
                    }
                }
            }
        }

        return retVal;
    }

    private int[] getRowsWithArticialBasics() {
        final int[] retVal = new int[this.countBasicArtificials()];
        int tmpInd = 0;
        for (int i = 0; i < myBasis.length; i++) {
            if (myBasis[i] < 0) {
                retVal[tmpInd] = i;
                tmpInd++;
            }
        }
        return retVal;
    }

    private final boolean isTableauPrintable() {
        return myTransposedTableau.size() <= HUNDRED;
    }

    private final void printTableau(final String aMessage) {
        BasicLogger.DEBUG.print(aMessage);
        BasicLogger.DEBUG.print("; Basics: " + Arrays.toString(myBasis));
        BasicLogger.printMtrx(BasicLogger.DEBUG, myTransposedTableau.transpose(), options.print);
    }

    @Override
    protected boolean needsAnotherIteration() {

        if (this.isDebug()) {
            BasicLogger.println(options.debug);
            BasicLogger.println(options.debug, "Needs Another Iteration?");
        }

        myPivotCol = this.findNextPivotCol();
        myPivotRow = -1;

        boolean retVal = myPivotCol != -1;

        if (!retVal && (myObjectiveRow > this.countConstraints())) {

            if (this.isDebug()) {
                BasicLogger.println(options.debug);
                BasicLogger.println(options.debug, "Switching to Phase2.");
                BasicLogger.println(options.debug);
            }

            int tmpCol = -1;
            final int[] tmpRows = this.getRowsWithArticialBasics();
            for (int i = 0; (tmpCol == -1) && (i < tmpRows.length); i++) {
                if (options.problem.isZero(myTransposedTableau.doubleValue(this.countVariables(), tmpRows[i]))) {
                    tmpCol = this.findColumnInRow(this.countConstraints(), true);
                    if (tmpCol != -1) {
                        myPivotCol = tmpCol;
                        myPivotRow = tmpRows[i];
                        if (myPivotRow != -1) {
                            this.performIteration(myPivotRow, myPivotCol);
                        }
                    }
                }
            }

            if (this.isDebug() && this.isTableauPrintable()) {
                this.printTableau("Cleaned Phase1 Tableau");
            }

            final double tmpPhaseOneValue = myTransposedTableau.doubleValue(this.countVariables(), myObjectiveRow);

            if (options.deprSolution.isZero(tmpPhaseOneValue)) {

                if (this.isDebug()) {
                    BasicLogger.println(options.debug, "Left Phase1 with {} artificial variable(s) in the basis.", this.countBasicArtificials());
                }

                myObjectiveRow = this.countConstraints();

                retVal = (myPivotCol = this.findNextPivotCol()) != -1;

            } else {

                myPivotCol = -1;
                myPivotRow = -1;

                this.setState(State.INFEASIBLE);

                retVal = false;
            }
        }

        if (retVal) {

            retVal = (myPivotRow = this.findNextPivotRow()) != -1;

            if (!retVal) {
                this.setState(State.UNBOUNDED);
            }

        } else if (myObjectiveRow == this.countConstraints()) {

            this.setState(State.OPTIMAL);
        }

        if (this.isDebug()) {
            if (retVal) {
                BasicLogger.println(options.debug, "Row: {},\tExit: {},\tColumn/Enter: {}.", myPivotRow, myBasis[myPivotRow], myPivotCol);
            } else {
                BasicLogger.println(options.debug, "No more iterations needed/possible.");
            }
        }

        return retVal;
    }

    int findNextPivotCol() {

        int retVal = -1;

        if (myObjectiveRow > this.countConstraints()) { // Phase1

            retVal = this.findColumnInRow(myObjectiveRow, false);

        } else { // Phase2

            // If there are still artificial variables in the basis
            final int[] tmpArtificialRows = this.getRowsWithArticialBasics();
            if (tmpArtificialRows.length > 0) {
                if (this.isDebug()) {
                    BasicLogger.println(options.debug, "Still {} artificial variable(s) in the basis.", tmpArtificialRows.length);
                }
                for (int i = 0; (retVal == -1) && (i < tmpArtificialRows.length); i++) {
                    retVal = this.findColumnInRow(tmpArtificialRows[i], false);
                }
            }

            // The normal objective row
            if (retVal == -1) {
                retVal = this.findColumnInRow(myObjectiveRow, false);
            }
        }

        return retVal;
    }

    void performIteration(final int aPivotRow, final int aPivotCol) {

        double tmpPivotElement = myTransposedTableau.doubleValue(aPivotCol, aPivotRow);

        if (Math.abs(tmpPivotElement) < ONE) {
            myTransposedTableau.modifyColumn(0, aPivotRow, new PreconfiguredSecond<Double>(DIVIDE, tmpPivotElement));
        } else if (tmpPivotElement != ONE) {
            myTransposedTableau.modifyColumn(0, aPivotRow, new PreconfiguredSecond<Double>(MULTIPLY, ONE / tmpPivotElement));
        }

        if (this.isDebug()) {
            BasicLogger.println(options.debug, "Pivot Element Before: {} and After: {}.", tmpPivotElement,
                    myTransposedTableau.doubleValue(aPivotCol, aPivotRow));
        }

        for (int i = 0; i <= myObjectiveRow; i++) {
            if (i != aPivotRow) {

                tmpPivotElement = myTransposedTableau.doubleValue(aPivotCol, i);

                if (!options.problem.isZero(tmpPivotElement)) {
                    myTransposedTableau.caxpy(-tmpPivotElement, aPivotRow, i, 0);
                }
            }
        }

        final int tmpOld = myBasis[aPivotRow];
        if (tmpOld >= 0) {
            this.exclude(tmpOld);
        }
        final int tmpNew = aPivotCol;
        if (tmpNew >= 0) {
            this.include(tmpNew);
        }
        myBasis[aPivotRow] = aPivotCol;

        if (this.isDebug()) {

            final ExpressionsBasedModel tmpModel = this.getModel();

            if ((tmpModel != null) && (myObjectiveRow == this.countConstraints())) { // Only if in Phase2
                if (!tmpModel.validateSolution(BigDenseStore.FACTORY.copy(this.extractSolution()), options.slack)) {
                    this.setState(State.FAILED);
                }
            }
        }
    }
}
