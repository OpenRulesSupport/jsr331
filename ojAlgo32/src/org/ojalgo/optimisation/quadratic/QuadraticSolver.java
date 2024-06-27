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

import static org.ojalgo.function.implementation.PrimitiveFunction.*;

import java.math.BigDecimal;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.access.Access1D;
import org.ojalgo.function.ConfigurableBinaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.optimisation.BaseSolver;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.type.TypeUtils;

/**
 * QuadraticSolver solves optimisation problems of the form:
 * <p>
 * min 1/2 [X]<sup>T</sup>[Q][X] - [C]<sup>T</sup>[X]<br>
 * when [AE][X] == [BE]<br>
 *  and [AI][X] <= [BI]
 * </p><p>
 * You construct instances by using the {@linkplain Builder} class. It
 * will return an appropriate subclass for you. It's recommended that
 * you first create a {@linkplain ExpressionsBasedModel} and feed that to the
 * {@linkplain Builder}. Alternatively you can directly call
 * {@linkplain ExpressionsBasedModel#getDefaultSolver()} or even
 * {@linkplain ExpressionsBasedModel#minimise()} or
 * {@linkplain ExpressionsBasedModel#maximise()} on the model.
 * </p>
 * 
 * @author apete
 */
public abstract class QuadraticSolver extends BaseSolver {

    public static final class Builder extends AbstractBuilder<QuadraticSolver.Builder, QuadraticSolver> {

        public Builder(final MatrixStore<Double> Q, final MatrixStore<Double> C) {
            super(Q, C);
        }

        Builder() {
            super();
        }

        Builder(final BaseSolver.Matrices matrices) {
            super(matrices);
        }

        Builder(final ExpressionsBasedModel aModel) {

            super(aModel);

            QuadraticSolver.copy(aModel, this);
        }

        Builder(final MatrixStore<Double> C) {
            super(C);
        }

        Builder(final MatrixStore<Double>[] aMtrxArr) {
            super(aMtrxArr);
        }

        @Override
        public QuadraticSolver build() {

            final ExpressionsBasedModel tmpModel = this.getModel();

            final BaseSolver.Matrices tmpMatrices = new BaseSolver.Matrices(this);

            if (tmpMatrices.hasInequalityConstraints()) {
                return new ActiveSetSolver(tmpModel, tmpMatrices);
            } else if (tmpMatrices.hasObjective()) {
                return new LagrangeSolver(tmpModel, tmpMatrices);
            } else {
                return new EquationSystemSolver(tmpModel, tmpMatrices);
            }
        }

        @Override
        public QuadraticSolver.Builder equalities(final MatrixStore<Double> AE, final MatrixStore<Double> BE) {
            return super.equalities(AE, BE);
        }

        @Override
        public QuadraticSolver.Builder inequalities(final MatrixStore<Double> AI, final MatrixStore<Double> BI) {
            return super.inequalities(AI, BI);
        }

        @Override
        protected Builder objective(final MatrixStore<Double> Q, final MatrixStore<Double> C) {
            return super.objective(Q, C);
        }

    }

    static final PhysicalStore.Factory<Double, PrimitiveDenseStore> FACTORY = PrimitiveDenseStore.FACTORY;

    public static QuadraticSolver make(final ExpressionsBasedModel aModel) {

        final QuadraticSolver.Builder tmpBuilder = new QuadraticSolver.Builder(aModel);

        return tmpBuilder.build();
    }

    @SuppressWarnings("unchecked")
    static void copy(final ExpressionsBasedModel aSourceModel, final QuadraticSolver.Builder aDestinationBuilder) {

        final Variable[] tmpAllVar = aSourceModel.getVariables();
        final int tmpAllVarDim = tmpAllVar.length;

        // AE & BE

        final Expression[] tmpEqExpr = aSourceModel.selectExpressionsGeneralEquality();
        final int tmpEqExprDim = tmpEqExpr.length;
        final Variable[] tmpEqVar = aSourceModel.selectVariablesGeneralEquality();
        final int tmpEqVarDim = tmpEqVar.length;

        if ((tmpEqExprDim + tmpEqVarDim) > 0) {

            final PhysicalStore<Double> tmpAE = FACTORY.makeZero(tmpEqExprDim + tmpEqVarDim, tmpAllVarDim);
            final PhysicalStore<Double> tmpBE = FACTORY.makeZero(tmpEqExprDim + tmpEqVarDim, 1);

            if (tmpEqExprDim > 0) {
                for (int i = 0; i < tmpEqExprDim; i++) {
                    final Expression tmpExpression = tmpEqExpr[i];
                    for (final Expression.Index tmpKey : tmpExpression.getLinearFactorKeys()) {
                        tmpAE.set(i, tmpKey.index, tmpExpression.getAdjustedLinearFactor(tmpKey));
                    }
                    tmpBE.set(i, 0, tmpExpression.getAdjustedUpperLimit());
                }
            }

            if (tmpEqVarDim > 0) {
                for (int i = 0; i < tmpEqVarDim; i++) {
                    final Variable tmpVariable = tmpEqVar[i];
                    tmpAE.set(tmpEqExprDim + i, aSourceModel.indexOf(tmpVariable).index, tmpVariable.getAdjustmentFactor());
                    tmpBE.set(tmpEqExprDim + i, 0, tmpVariable.getAdjustedUpperLimit());
                }
            }

            aDestinationBuilder.equalities(tmpAE, tmpBE);
        }

        // Q & C

        final Expression tmpObjExpr = aSourceModel.getObjectiveExpression();

        PhysicalStore<Double> tmpQ = null;
        if (tmpObjExpr.isAnyQuadraticFactorNonZero()) {
            tmpQ = FACTORY.makeZero(tmpAllVarDim, tmpAllVarDim);

            //            final boolean tmpMax = aSourceModel.isMaximisation();
            //            for (int j = 0; j < tmpAllVarDim; j++) {
            //                for (int i = 0; i < tmpAllVarDim; i++) {
            //                    if (tmpMax) {
            //                        tmpQ.set(i, j, -(tmpObjExpr.getAdjustedQuadraticFactor(i, j) + tmpObjExpr.getAdjustedQuadraticFactor(j, i)));
            //                    } else {
            //                        tmpQ.set(i, j, tmpObjExpr.getAdjustedQuadraticFactor(i, j) + tmpObjExpr.getAdjustedQuadraticFactor(j, i));
            //                    }
            //                }
            //            }

            final ConfigurableBinaryFunction<Double> tmpBaseFunc = aSourceModel.isMaximisation() ? SUBTRACT : ADD;
            UnaryFunction<Double> tmpModifier;
            for (final Expression.RowColumn tmpKey : tmpObjExpr.getQuadraticFactorKeys()) {
                tmpModifier = tmpBaseFunc.second(tmpObjExpr.getAdjustedQuadraticFactor(tmpKey));
                tmpQ.modifyOne(tmpKey.row, tmpKey.column, tmpModifier);
                tmpQ.modifyOne(tmpKey.column, tmpKey.row, tmpModifier);
            }
        }

        PhysicalStore<Double> tmpC = null;
        if (tmpObjExpr.isAnyLinearFactorNonZero()) {
            tmpC = FACTORY.makeZero(tmpAllVarDim, 1);
            if (aSourceModel.isMinimisation()) {
                for (final Expression.Index tmpKey : tmpObjExpr.getLinearFactorKeys()) {
                    tmpC.set(tmpKey.index, 0, -tmpObjExpr.getAdjustedLinearFactor(tmpKey));
                }
            } else {
                for (final Expression.Index tmpKey : tmpObjExpr.getLinearFactorKeys()) {
                    tmpC.set(tmpKey.index, 0, tmpObjExpr.getAdjustedLinearFactor(tmpKey));
                }
            }
        }

        aDestinationBuilder.objective(tmpQ, tmpC);

        // AI & BI

        final Expression[] tmpUpExpr = aSourceModel.selectExpressionsGeneralUpper();
        final int tmpUpExprDim = tmpUpExpr.length;
        final Variable[] tmpUpVar = aSourceModel.selectVariablesGeneralUpper();
        final int tmpUpVarDim = tmpUpVar.length;

        final Expression[] tmpLoExpr = aSourceModel.selectExpressionsGeneralLower();
        final int tmpLoExprDim = tmpLoExpr.length;
        final Variable[] tmpLoVar = aSourceModel.selectVariablesGeneralLower();
        final int tmpLoVarDim = tmpLoVar.length;

        if ((tmpUpExprDim + tmpUpVarDim + tmpLoExprDim + tmpLoVarDim) > 0) {

            final PhysicalStore<Double> tmpUAI = FACTORY.makeZero(tmpUpExprDim + tmpUpVarDim, tmpAllVarDim);
            final PhysicalStore<Double> tmpUBI = FACTORY.makeZero(tmpUpExprDim + tmpUpVarDim, 1);

            if (tmpUpExprDim > 0) {
                for (int i = 0; i < tmpUpExprDim; i++) {
                    final Expression tmpExpression = tmpUpExpr[i];
                    for (final Expression.Index tmpKey : tmpExpression.getLinearFactorKeys()) {
                        tmpUAI.set(i, tmpKey.index, tmpExpression.getAdjustedLinearFactor(tmpKey));
                    }
                    tmpUBI.set(i, 0, tmpExpression.getAdjustedUpperLimit());
                }
            }

            if (tmpUpVarDim > 0) {
                for (int i = 0; i < tmpUpVarDim; i++) {
                    final Variable tmpVariable = tmpUpVar[i];
                    tmpUAI.set(tmpUpExprDim + i, aSourceModel.indexOf(tmpVariable).index, tmpVariable.getAdjustmentFactor());
                    tmpUBI.set(tmpUpExprDim + i, 0, tmpVariable.getAdjustedUpperLimit());
                }
            }

            final PhysicalStore<Double> tmpLAI = FACTORY.makeZero(tmpLoExprDim + tmpLoVarDim, tmpAllVarDim);
            final PhysicalStore<Double> tmpLBI = FACTORY.makeZero(tmpLoExprDim + tmpLoVarDim, 1);

            if (tmpLoExprDim > 0) {
                for (int i = 0; i < tmpLoExprDim; i++) {
                    final Expression tmpExpression = tmpLoExpr[i];
                    for (final Expression.Index tmpKey : tmpExpression.getLinearFactorKeys()) {
                        tmpLAI.set(i, tmpKey.index, -tmpExpression.getAdjustedLinearFactor(tmpKey));
                    }
                    tmpLBI.set(i, 0, -tmpExpression.getAdjustedLowerLimit());
                }
            }

            if (tmpLoVarDim > 0) {
                for (int i = 0; i < tmpLoVarDim; i++) {
                    final Variable tmpVariable = tmpLoVar[i];
                    tmpLAI.set(tmpLoExprDim + i, aSourceModel.indexOf(tmpVariable).index, -tmpVariable.getAdjustmentFactor());
                    tmpLBI.set(tmpLoExprDim + i, 0, -tmpVariable.getAdjustedLowerLimit());
                }
            }

            final MatrixStore<Double> tmpAI = tmpLAI.builder().above(tmpUAI).build();
            final MatrixStore<Double> tmpBI = tmpLBI.builder().above(tmpUBI).build();

            aDestinationBuilder.inequalities(tmpAI, tmpBI);

        }

    }

    protected QuadraticSolver(final ExpressionsBasedModel aModel, final Matrices matrices) {
        super(aModel, matrices);
    }

    @Override
    public final Optimisation.Result solve() {

        this.initialise();

        this.resetIterationsCount();

        do {
            this.iterate();
        } while (!this.getState().isFailure() && this.needsAnotherIteration());

        final State tmpState = this.getState();
        final MatrixStore<Double> tmpSolution = this.getSolutionX();

        if (this.isModelSet()) {

            final ExpressionsBasedModel tmpModel = this.getModel();

            final Variable[] tmpVariables = tmpModel.getVariables();
            for (int v = 0; v < tmpVariables.length; v++) {
                tmpVariables[v].setValue(TypeUtils.toBigDecimal(tmpSolution.get(v), options.result));
            }

            final Access1D<BigDecimal> tmpValues = tmpModel.getVariableValues();

            final double tmpValue = this.evaluateFunction(tmpValues);

            return new Optimisation.Result(tmpState, tmpValue, tmpSolution);

        } else {

            final double tmpValue = this.evaluateFunction(tmpSolution);

            return new Optimisation.Result(tmpState, tmpValue, tmpSolution);
        }

    }

    private State iterate() {

        if (!this.getState().isFailure()) {

            try {

                this.incrementIterationsCount();

                this.performIteration();

                if (!this.getState().isFeasible()) {
                    this.setState(State.APPROXIMATE);
                }

            } catch (final RecoverableCondition anException) {

                this.setState(State.FAILED);
            }
        }

        return this.getState();
    }

    abstract protected void initialise();

    abstract protected void performIteration() throws RecoverableCondition;

    MatrixStore<Double> getSolutionLE() {
        return this.getLE();
    }

    MatrixStore<Double> getSolutionLI(final int... aRowSelector) {
        return this.getLI(aRowSelector);
    }

    MatrixStore<Double> getSolutionX() {
        return this.getX();
    }

}
