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

import java.util.Arrays;
import java.util.List;

import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.optimisation.BaseSolver;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.type.IndexSelector;

/**
 * LinearSolver solves optimisation problems of the (LP standard) form:
 * <p>
 * min [C]<sup>T</sup>[X]<br>
 * when [AE][X] == [BE]<br>
 *  and 0 <= [X]<br>
 *  and 0 <= [BE]
 * </p>
 * A Linear Program is in Standard Form if:
 * <ul>
 * <li>All constraints are equality constraints.</li>
 * <li>All variables have a nonnegativity sign restriction.</li>
 * </ul>
 * <p>
 * Further it is required here that the constraint right hand sides are
 * nonnegative (nonnegative elements in [BE]).
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
 * http://www.cise.ufl.edu/~davis/Morgan/chapter2.htm
 * 
 * @author apete
 */
public abstract class LinearSolver extends BaseSolver {

    public static final class Builder extends AbstractBuilder<LinearSolver.Builder, LinearSolver> {

        private final int[] myPivots;

        public Builder(final MatrixStore<Double> C) {

            super(C);

            myPivots = null;
        }

        Builder() {

            super();

            myPivots = null;
        }

        Builder(final BaseSolver.Matrices matrices) {

            super(matrices);

            myPivots = null;
        }

        Builder(final ExpressionsBasedModel aModel) {

            super(aModel);

            myPivots = LinearSolver.copy(aModel, this);
        }

        Builder(final MatrixStore<Double> Q, final MatrixStore<Double> C) {

            super(Q, C);

            myPivots = null;
        }

        Builder(final MatrixStore<Double>[] aMtrxArr) {

            super(aMtrxArr);

            myPivots = null;
        }

        @Override
        public LinearSolver build() {

            final ExpressionsBasedModel tmpModel = this.getModel();

            final BaseSolver.Matrices tmpMatrices = new BaseSolver.Matrices(this);

            final int tmpCountEqualityConstraints = tmpMatrices.countEqualityConstraints();
            final int[] tmpPivots = myPivots != null ? myPivots : LinearSolver.makePivots(tmpCountEqualityConstraints);

            return new SimplexTableauSolver(tmpModel, tmpMatrices, tmpPivots);
        }

        @Override
        public Builder equalities(final MatrixStore<Double> AE, final MatrixStore<Double> BE) {
            return super.equalities(AE, BE);
        }

        @Override
        protected Builder objective(final MatrixStore<Double> C) {
            return super.objective(C);
        }
    }

    static final Factory<Double, PrimitiveDenseStore> FACTORY = PrimitiveDenseStore.FACTORY;

    public static LinearSolver make(final ExpressionsBasedModel aModel) {

        final LinearSolver.Builder tmpBuilder = new LinearSolver.Builder(aModel);

        return tmpBuilder.build();
    }

    static int[] copyOld(final ExpressionsBasedModel aSourceModel, final LinearSolver.Builder aDestinationBuilder) {

        final boolean tmpMaximisation = aSourceModel.isMaximisation();

        final Variable[] tmpVariables = aSourceModel.getVariables();

        final Expression tmpObjFunc = aSourceModel.getObjectiveExpression();

        final Expression[] tmpExprsNegEq = aSourceModel.selectExpressionsNegativeEquality();
        final Expression[] tmpExprsPosEq = aSourceModel.selectExpressionsPositiveEquality();
        final Expression[] tmpExprsNegLo = aSourceModel.selectExpressionsNegativeLower();
        final Expression[] tmpExprsPosLo = aSourceModel.selectExpressionsPositiveLower();
        final Expression[] tmpExprsNegUp = aSourceModel.selectExpressionsNegativeUpper();
        final Expression[] tmpExprsPosUp = aSourceModel.selectExpressionsPositiveUpper();

        final Variable[] tmpVarsPosEq = aSourceModel.selectVariablesPositiveEquality();
        final Variable[] tmpVarsPosLo = aSourceModel.selectVariablesPositiveLower();
        final Variable[] tmpVarsPosUp = aSourceModel.selectVariablesPositiveUpper();

        final Variable[] tmpVarsNegEq = aSourceModel.selectVariablesNegativeEquality();
        final Variable[] tmpVarsNegLo = aSourceModel.selectVariablesNegativeLower();
        final Variable[] tmpVarsNegUp = aSourceModel.selectVariablesNegativeUpper();

        final int tmpConstraiCount = tmpExprsNegEq.length + tmpExprsPosEq.length + tmpExprsNegLo.length + tmpExprsPosLo.length + tmpExprsNegUp.length
                + tmpExprsPosUp.length + tmpVarsPosLo.length + tmpVarsPosUp.length + tmpVarsPosEq.length;
        final int tmpProblVarCount = tmpVariables.length;
        final int tmpSlackVarCount = tmpExprsNegLo.length + tmpExprsPosLo.length + tmpExprsNegUp.length + tmpExprsPosUp.length + tmpVarsPosLo.length
                + tmpVarsPosUp.length;
        final int tmpTotalVarCount = tmpProblVarCount + tmpSlackVarCount;

        final int[] retValPivots = LinearSolver.makePivots(tmpConstraiCount);

        final PhysicalStore<Double> tmpC = FACTORY.makeZero(tmpTotalVarCount, 1);
        final PhysicalStore<Double> tmpAE = FACTORY.makeZero(tmpConstraiCount, tmpTotalVarCount);
        final PhysicalStore<Double> tmpBE = FACTORY.makeZero(tmpConstraiCount, 1);

        aDestinationBuilder.objective(tmpC);
        aDestinationBuilder.equalities(tmpAE, tmpBE);

        int tmpSlackColIndex = tmpProblVarCount;
        for (int j = 0; j < tmpProblVarCount; j++) {

            // C

            if (tmpMaximisation) {
                tmpC.set(j, 0, -tmpObjFunc.getAdjustedLinearFactor(j));
            } else {
                tmpC.set(j, 0, tmpObjFunc.getAdjustedLinearFactor(j));
            }

            // AE & BE

            int tmpRowBaseIndex = 0;

            // NON BASIC

            // Negative Equality Expressions
            for (int i = 0; i < tmpExprsNegEq.length; i++) {
                tmpAE.set(tmpRowBaseIndex + i, j, -tmpExprsNegEq[i].getAdjustedLinearFactor(j));
                if (j == 0) {
                    tmpBE.set(tmpRowBaseIndex + i, j, -tmpExprsNegEq[i].getAdjustedUpperLimit());
                }
            }
            tmpRowBaseIndex += tmpExprsNegEq.length;

            // Positive Equality Expressions
            for (int i = 0; i < tmpExprsPosEq.length; i++) {
                tmpAE.set(tmpRowBaseIndex + i, j, tmpExprsPosEq[i].getAdjustedLinearFactor(j));
                if (j == 0) {
                    tmpBE.set(tmpRowBaseIndex + i, j, tmpExprsPosEq[i].getAdjustedUpperLimit());
                }
            }
            tmpRowBaseIndex += tmpExprsPosEq.length;

            // Positive Lower Limit Expressions
            for (int i = 0; i < tmpExprsPosLo.length; i++) {
                tmpAE.set(tmpRowBaseIndex + i, j, tmpExprsPosLo[i].getAdjustedLinearFactor(j));
                if (j == 0) {
                    tmpAE.set(tmpRowBaseIndex + i, tmpSlackColIndex++, NEG);
                    tmpBE.set(tmpRowBaseIndex + i, j, tmpExprsPosLo[i].getAdjustedLowerLimit());
                }
            }
            tmpRowBaseIndex += tmpExprsPosLo.length;

            // Negative Upper Limit Expressions
            for (int i = 0; i < tmpExprsNegUp.length; i++) {
                tmpAE.set(tmpRowBaseIndex + i, j, -tmpExprsNegUp[i].getAdjustedLinearFactor(j));
                if (j == 0) {
                    tmpAE.set(tmpRowBaseIndex + i, tmpSlackColIndex++, NEG);
                    tmpBE.set(tmpRowBaseIndex + i, j, -tmpExprsNegUp[i].getAdjustedUpperLimit());
                }
            }
            tmpRowBaseIndex += tmpExprsNegUp.length;

            // Lower Limit Variables
            for (int i = 0; i < tmpVarsPosLo.length; i++) {
                if (tmpVarsPosLo[i].equals(tmpVariables[j])) {
                    tmpAE.set(tmpRowBaseIndex + i, j, ONE);
                    tmpAE.set(tmpRowBaseIndex + i, tmpSlackColIndex++, NEG);
                    tmpBE.set(tmpRowBaseIndex + i, 0, tmpVarsPosLo[i].getLowerLimit().doubleValue());
                }
            }
            tmpRowBaseIndex += tmpVarsPosLo.length;

            // Equality Variables
            for (int i = 0; i < tmpVarsPosEq.length; i++) {
                if (tmpVarsPosEq[i].equals(tmpVariables[j])) {
                    //retValPivots[tmpRowBaseIndex + i] = j;
                    tmpAE.set(tmpRowBaseIndex + i, j, ONE);
                    tmpBE.set(tmpRowBaseIndex + i, 0, tmpVarsPosEq[i].getUpperLimit().doubleValue());

                }
            }
            tmpRowBaseIndex += tmpVarsPosEq.length;

            // BASIC

            // Negative Lower Limit Expressions
            for (int i = 0; i < tmpExprsNegLo.length; i++) {
                tmpAE.set(tmpRowBaseIndex + i, j, -tmpExprsNegLo[i].getAdjustedLinearFactor(j));
                if (j == 0) {
                    retValPivots[tmpRowBaseIndex + i] = tmpSlackColIndex;
                    tmpAE.set(tmpRowBaseIndex + i, tmpSlackColIndex++, ONE);
                    tmpBE.set(tmpRowBaseIndex + i, j, -tmpExprsNegLo[i].getAdjustedLowerLimit());
                }
            }
            tmpRowBaseIndex += tmpExprsNegLo.length;

            // Positive Upper Limit Expressions
            for (int i = 0; i < tmpExprsPosUp.length; i++) {
                tmpAE.set(tmpRowBaseIndex + i, j, tmpExprsPosUp[i].getAdjustedLinearFactor(j));
                if (j == 0) {
                    retValPivots[tmpRowBaseIndex + i] = tmpSlackColIndex;
                    tmpAE.set(tmpRowBaseIndex + i, tmpSlackColIndex++, ONE);
                    tmpBE.set(tmpRowBaseIndex + i, j, tmpExprsPosUp[i].getAdjustedUpperLimit());
                }
            }
            tmpRowBaseIndex += tmpExprsPosUp.length;

            // Upper Limit Variables
            for (int i = 0; i < tmpVarsPosUp.length; i++) {
                if (tmpVarsPosUp[i].equals(tmpVariables[j])) {
                    tmpAE.set(tmpRowBaseIndex + i, j, ONE);
                    retValPivots[tmpRowBaseIndex + i] = tmpSlackColIndex;
                    tmpAE.set(tmpRowBaseIndex + i, tmpSlackColIndex++, ONE);
                    tmpBE.set(tmpRowBaseIndex + i, 0, tmpVarsPosUp[i].getUpperLimit().doubleValue());
                }
            }
            tmpRowBaseIndex += tmpVarsPosUp.length;

        }

        //        BasicLogger.logDebug("C");
        //        MatrixUtils.printToStream(BasicLogger.DEBUG, tmpC, Options.DEFAULT_PRINT_CONTEXT);
        //        BasicLogger.logDebug("AE");
        //        MatrixUtils.printToStream(BasicLogger.DEBUG, tmpAE, Options.DEFAULT_PRINT_CONTEXT);
        //        BasicLogger.logDebug("BE");
        //        MatrixUtils.printToStream(BasicLogger.DEBUG, tmpBE, Options.DEFAULT_PRINT_CONTEXT);
        //        BasicLogger.logDebug("Basis: {}", Arrays.toString(retValPivots));

        return retValPivots;
    }

    static int[] copy(final ExpressionsBasedModel aSourceModel, final LinearSolver.Builder aDestinationBuilder) {

        final boolean tmpMaximisation = aSourceModel.isMaximisation();

        final Variable[] tmpVariables = aSourceModel.getVariables();
        final List<Variable> tmpPosVariables = aSourceModel.getPositiveVariables();
        final List<Variable> tmpNegVariables = aSourceModel.getNegativeVariables();

        final Expression tmpObjFunc = aSourceModel.getObjectiveExpression();

        final Expression[] tmpExprsNegEq = aSourceModel.selectExpressionsNegativeEquality();
        final Expression[] tmpExprsPosEq = aSourceModel.selectExpressionsPositiveEquality();
        final Expression[] tmpExprsNegLo = aSourceModel.selectExpressionsNegativeLower();
        final Expression[] tmpExprsPosLo = aSourceModel.selectExpressionsPositiveLower();
        final Expression[] tmpExprsNegUp = aSourceModel.selectExpressionsNegativeUpper();
        final Expression[] tmpExprsPosUp = aSourceModel.selectExpressionsPositiveUpper();

        final Variable[] tmpVarsPosEq = aSourceModel.selectVariablesPositiveEquality();
        final Variable[] tmpVarsPosLo = aSourceModel.selectVariablesPositiveLower();
        final Variable[] tmpVarsPosUp = aSourceModel.selectVariablesPositiveUpper();

        final Variable[] tmpVarsNegEq = aSourceModel.selectVariablesNegativeEquality();
        final Variable[] tmpVarsNegLo = aSourceModel.selectVariablesNegativeLower();
        final Variable[] tmpVarsNegUp = aSourceModel.selectVariablesNegativeUpper();

        final int tmpConstraiCount = tmpExprsNegEq.length + tmpExprsPosEq.length + tmpExprsNegLo.length + tmpExprsPosLo.length + tmpExprsNegUp.length
                + tmpExprsPosUp.length + tmpVarsPosEq.length + tmpVarsPosLo.length + tmpVarsPosUp.length + tmpVarsNegEq.length + tmpVarsNegLo.length
                + tmpVarsNegUp.length;
        final int tmpProblVarCount = tmpPosVariables.size() + tmpNegVariables.size();
        final int tmpSlackVarCount = tmpExprsNegLo.length + tmpExprsPosLo.length + tmpExprsNegUp.length + tmpExprsPosUp.length + tmpVarsPosLo.length
                + tmpVarsPosUp.length + tmpVarsNegLo.length + tmpVarsNegUp.length;
        final int tmpTotalVarCount = tmpProblVarCount + tmpSlackVarCount;

        final int[] retValPivots = LinearSolver.makePivots(tmpConstraiCount);

        final PhysicalStore<Double> tmpC = FACTORY.makeZero(tmpTotalVarCount, 1);
        final PhysicalStore<Double> tmpAE = FACTORY.makeZero(tmpConstraiCount, tmpTotalVarCount);
        final PhysicalStore<Double> tmpBE = FACTORY.makeZero(tmpConstraiCount, 1);

        aDestinationBuilder.objective(tmpC);
        aDestinationBuilder.equalities(tmpAE, tmpBE);

        final int tmpPosVarsBaseIndex = 0;
        final int tmpNegVarsBaseIndex = tmpPosVarsBaseIndex + tmpPosVariables.size();
        final int tmpSlaVarsBaseIndex = tmpNegVarsBaseIndex + tmpNegVariables.size();

        for (final Expression.Index tmpKey : tmpObjFunc.getLinearFactorKeys()) {

            final double tmpFactor = tmpMaximisation ? -tmpObjFunc.getAdjustedLinearFactor(tmpKey) : tmpObjFunc.getAdjustedLinearFactor(tmpKey);

            final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
            if (tmpPosInd >= 0) {
                tmpC.set(tmpPosInd, 0, tmpFactor);
            }

            final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
            if (tmpNegInd >= 0) {
                tmpC.set(tmpNegVarsBaseIndex + tmpNegInd, 0, -tmpFactor);
            }
        }

        int tmpConstrBaseIndex = 0;
        int tmpCurrentSlackVarIndex = tmpSlaVarsBaseIndex;

        final int tmpExprsNegEqLength = tmpExprsNegEq.length;
        for (int c = 0; c < tmpExprsNegEqLength; c++) {

            final Expression tmpExpr = tmpExprsNegEq[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, -tmpExpr.getAdjustedLowerLimit());

            for (final Expression.Index tmpKey : tmpExpr.getLinearFactorKeys()) {

                final double tmpFactor = tmpExpr.getAdjustedLinearFactor(tmpKey);

                final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
                if (tmpPosInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, -tmpFactor);
                }

                final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
                if (tmpNegInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, tmpFactor);
                }
            }
        }
        tmpConstrBaseIndex += tmpExprsNegEqLength;

        final int tmpExprsPosEqLength = tmpExprsPosEq.length;
        for (int c = 0; c < tmpExprsPosEqLength; c++) {

            final Expression tmpExpr = tmpExprsPosEq[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, tmpExpr.getAdjustedUpperLimit());

            for (final Expression.Index tmpKey : tmpExpr.getLinearFactorKeys()) {

                final double tmpFactor = tmpExpr.getAdjustedLinearFactor(tmpKey);

                final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
                if (tmpPosInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, tmpFactor);
                }

                final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
                if (tmpNegInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, -tmpFactor);
                }
            }
        }
        tmpConstrBaseIndex += tmpExprsPosEqLength;

        final int tmpExprsNegLoLength = tmpExprsNegLo.length;
        for (int c = 0; c < tmpExprsNegLoLength; c++) {

            final Expression tmpExpr = tmpExprsNegLo[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, -tmpExpr.getAdjustedLowerLimit());
            retValPivots[tmpConstrBaseIndex + c] = tmpCurrentSlackVarIndex;
            tmpAE.set(tmpConstrBaseIndex + c, tmpCurrentSlackVarIndex++, ONE);

            for (final Expression.Index tmpKey : tmpExpr.getLinearFactorKeys()) {

                final double tmpFactor = tmpExpr.getAdjustedLinearFactor(tmpKey);

                final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
                if (tmpPosInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, -tmpFactor);
                }

                final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
                if (tmpNegInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, tmpFactor);
                }
            }
        }
        tmpConstrBaseIndex += tmpExprsNegLoLength;

        final int tmpExprsPosLoLength = tmpExprsPosLo.length;
        for (int c = 0; c < tmpExprsPosLoLength; c++) {

            final Expression tmpExpr = tmpExprsPosLo[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, tmpExpr.getAdjustedLowerLimit());
            tmpAE.set(tmpConstrBaseIndex + c, tmpCurrentSlackVarIndex++, NEG);

            for (final Expression.Index tmpKey : tmpExpr.getLinearFactorKeys()) {

                final double tmpFactor = tmpExpr.getAdjustedLinearFactor(tmpKey);

                final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
                if (tmpPosInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, tmpFactor);
                }

                final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
                if (tmpNegInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, -tmpFactor);
                }
            }
        }
        tmpConstrBaseIndex += tmpExprsPosLoLength;

        final int tmpExprsNegUpLength = tmpExprsNegUp.length;
        for (int c = 0; c < tmpExprsNegUpLength; c++) {

            final Expression tmpExpr = tmpExprsNegUp[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, -tmpExpr.getAdjustedUpperLimit());
            tmpAE.set(tmpConstrBaseIndex + c, tmpCurrentSlackVarIndex++, NEG);

            for (final Expression.Index tmpKey : tmpExpr.getLinearFactorKeys()) {

                final double tmpFactor = tmpExpr.getAdjustedLinearFactor(tmpKey);

                final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
                if (tmpPosInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, -tmpFactor);
                }

                final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
                if (tmpNegInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, tmpFactor);
                }
            }
        }
        tmpConstrBaseIndex += tmpExprsNegUpLength;

        final int tmpExprsPosUpLength = tmpExprsPosUp.length;
        for (int c = 0; c < tmpExprsPosUpLength; c++) {

            final Expression tmpExpr = tmpExprsPosUp[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, tmpExpr.getAdjustedUpperLimit());
            retValPivots[tmpConstrBaseIndex + c] = tmpCurrentSlackVarIndex;
            tmpAE.set(tmpConstrBaseIndex + c, tmpCurrentSlackVarIndex++, ONE);

            for (final Expression.Index tmpKey : tmpExpr.getLinearFactorKeys()) {

                final double tmpFactor = tmpExpr.getAdjustedLinearFactor(tmpKey);

                final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
                if (tmpPosInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, tmpFactor);
                }

                final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
                if (tmpNegInd >= 0) {
                    tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, -tmpFactor);
                }
            }
        }
        tmpConstrBaseIndex += tmpExprsPosUpLength;

        final int tmpVarsPosEqLength = tmpVarsPosEq.length;
        for (int c = 0; c < tmpVarsPosEqLength; c++) {

            final Variable tmpVar = tmpVarsPosEq[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, tmpVar.getAdjustedUpperLimit());

            final Expression.Index tmpKey = aSourceModel.indexOf(tmpVar);

            final double tmpFactor = tmpVar.getAdjustmentFactor();

            final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
            if (tmpPosInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, tmpFactor);
            }

            final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
            if (tmpNegInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, -tmpFactor);
            }

        }
        tmpConstrBaseIndex += tmpVarsPosEqLength;

        final int tmpVarsPosLoLength = tmpVarsPosLo.length;
        for (int c = 0; c < tmpVarsPosLoLength; c++) {

            final Variable tmpVar = tmpVarsPosLo[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, tmpVar.getAdjustedLowerLimit());
            tmpAE.set(tmpConstrBaseIndex + c, tmpCurrentSlackVarIndex++, NEG);

            final Expression.Index tmpKey = aSourceModel.indexOf(tmpVar);

            final double tmpFactor = tmpVar.getAdjustmentFactor();

            final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
            if (tmpPosInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, tmpFactor);
            }

            final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
            if (tmpNegInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, -tmpFactor);
            }

        }
        tmpConstrBaseIndex += tmpVarsPosLoLength;

        final int tmpVarsPosUpLength = tmpVarsPosUp.length;
        for (int c = 0; c < tmpVarsPosUpLength; c++) {

            final Variable tmpVar = tmpVarsPosUp[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, tmpVar.getAdjustedUpperLimit());
            retValPivots[tmpConstrBaseIndex + c] = tmpCurrentSlackVarIndex;
            tmpAE.set(tmpConstrBaseIndex + c, tmpCurrentSlackVarIndex++, ONE);

            final Expression.Index tmpKey = aSourceModel.indexOf(tmpVar);

            final double tmpFactor = tmpVar.getAdjustmentFactor();

            final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
            if (tmpPosInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, tmpFactor);
            }

            final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
            if (tmpNegInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, -tmpFactor);
            }

        }
        tmpConstrBaseIndex += tmpVarsPosUpLength;

        final int tmpVarsNegEqLength = tmpVarsNegEq.length;
        for (int c = 0; c < tmpVarsNegEqLength; c++) {

            final Variable tmpVar = tmpVarsNegEq[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, -tmpVar.getAdjustedLowerLimit());

            final Expression.Index tmpKey = aSourceModel.indexOf(tmpVar);

            final double tmpFactor = tmpVar.getAdjustmentFactor();

            final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
            if (tmpPosInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, -tmpFactor);
            }

            final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
            if (tmpNegInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, tmpFactor);
            }

        }
        tmpConstrBaseIndex += tmpVarsNegEqLength;

        final int tmpVarsNegLoLength = tmpVarsNegLo.length;
        for (int c = 0; c < tmpVarsNegLoLength; c++) {

            final Variable tmpVar = tmpVarsNegLo[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, -tmpVar.getAdjustedLowerLimit());
            retValPivots[tmpConstrBaseIndex + c] = tmpCurrentSlackVarIndex;
            tmpAE.set(tmpConstrBaseIndex + c, tmpCurrentSlackVarIndex++, ONE);

            final Expression.Index tmpKey = aSourceModel.indexOf(tmpVar);

            final double tmpFactor = tmpVar.getAdjustmentFactor();

            final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
            if (tmpPosInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, -tmpFactor);
            }

            final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
            if (tmpNegInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, tmpFactor);
            }

        }
        tmpConstrBaseIndex += tmpVarsNegLoLength;

        final int tmpVarsNegUpLength = tmpVarsNegUp.length;
        for (int c = 0; c < tmpVarsNegUpLength; c++) {

            final Variable tmpVar = tmpVarsNegUp[c];

            tmpBE.set(tmpConstrBaseIndex + c, 0, -tmpVar.getAdjustedUpperLimit());
            tmpAE.set(tmpConstrBaseIndex + c, tmpCurrentSlackVarIndex++, NEG);

            final Expression.Index tmpKey = aSourceModel.indexOf(tmpVar);

            final double tmpFactor = tmpVar.getAdjustmentFactor();

            final int tmpPosInd = aSourceModel.indexOfPositiveVariable(tmpKey.index);
            if (tmpPosInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpPosVarsBaseIndex + tmpPosInd, -tmpFactor);
            }

            final int tmpNegInd = aSourceModel.indexOfNegativeVariable(tmpKey.index);
            if (tmpNegInd >= 0) {
                tmpAE.set(tmpConstrBaseIndex + c, tmpNegVarsBaseIndex + tmpNegInd, tmpFactor);
            }

        }
        tmpConstrBaseIndex += tmpVarsNegUpLength;

        return retValPivots;
    }

    static int[] makePivots(final int aNumberOfConstraints) {
        final int[] retVal = new int[aNumberOfConstraints];
        Arrays.fill(retVal, -1);
        return retVal;
    }

    private final IndexSelector mySelector;

    protected LinearSolver(final ExpressionsBasedModel aModel, final Matrices matrices) {

        super(aModel, matrices);

        mySelector = new IndexSelector(matrices.countVariables());
    }

    protected final int countBasisDeficit() {
        return this.countEqualityConstraints() - mySelector.countIncluded();
    }

    protected final int countConstraints() {
        return this.countEqualityConstraints();
    }

    protected final void exclude(final int anIndexToExclude) {
        mySelector.exclude(anIndexToExclude);
    }

    protected final void excludeAll() {
        mySelector.excludeAll();
    }

    protected final int[] getExcluded() {
        return mySelector.getExcluded();
    }

    protected final int[] getIncluded() {
        return mySelector.getIncluded();
    }

    protected final boolean hasConstraints() {
        return this.hasEqualityConstraints();
    }

    protected final void include(final int anIndexToInclude) {
        mySelector.include(anIndexToInclude);
    }

    protected final void include(final int[] someIndecesToInclude) {
        mySelector.include(someIndecesToInclude);
    }

}
