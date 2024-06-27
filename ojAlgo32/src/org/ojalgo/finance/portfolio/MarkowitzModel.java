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
package org.ojalgo.finance.portfolio;

import static org.ojalgo.constant.BigMath.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Optimisation.State;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;

/**
 * <p>
 * The Markowitz model, in this class, is defined as:
 * </p><p>
 * min (RAF/2) [w]<sup>T</sup>[C][w] - [w]<sup>T</sup>[r]
 * <br>
 * subject to |[w]| = 1
 * </p><p>
 * RAF stands for Risk Aversion Factor. Instead of specifying a desired
 * risk or return level you specify a level of risk aversion that is
 * used to balance the risk and return.
 * </p><p>
 * The expected returns for each of the instruments/assets must be
 * excess returns. Otherwise this formulation is wrong.
 * </p><p>
 * The total weights of all instruments will always be 100%, but
 * shorting can be allowed or not according to your preference. (
 * {@linkplain #setShortingAllowed(boolean)}
 * ) In addition you may set lower and upper limits on any individual
 * instrument. (
 * {@linkplain #setLowerLimit(int, BigDecimal)}
 * and
 * {@linkplain #setUpperLimit(int, BigDecimal)}
 * )
 * </p><p>
 * Risk-free asset: That means there is no excess return and zero
 * variance. Don't (try to) include a risk-free asset here.
 * </p><p>
 * Do not worry about the minus sign in front of the return part of
 * the objective function - it is handled/negated for you. When you're
 * asked to supply the expected excess returns you should supply
 * precisely that.
 * </p>
 * 
 * @author apete
 */
public final class MarkowitzModel extends EquilibriumModel {

    private static final class LowerUpper {

        final BigDecimal lower;
        final BigDecimal upper;

        LowerUpper(final BigDecimal someLower, final BigDecimal someUpper) {

            super();

            lower = someLower;
            upper = someUpper;
        }
    }

    private static final double _0_000005 = 0.000005;

    private static final String BALANCE = "Balance";

    private static final String RETURN = "Return";

    private static final String VARIANCE = "Variance";
    private final HashMap<int[], LowerUpper> myConstraints = new HashMap<int[], LowerUpper>();
    private final BasicMatrix myExpectedExcessReturns;
    private transient ExpressionsBasedModel myOptimisationModel;

    private transient State myOptimisationState = State.UNEXPLORED;
    private boolean myShortingAllowed = false;
    private BigDecimal myTargetReturn;
    private BigDecimal myTargetVariance;
    private final Variable[] myVariables;

    public MarkowitzModel(final Context aContext) {

        super(aContext);

        myExpectedExcessReturns = aContext.getAssetReturns();

        final String[] tmpSymbols = this.getMarketEquilibrium().getSymbols();
        myVariables = new Variable[tmpSymbols.length];
        for (int i = 0; i < tmpSymbols.length; i++) {
            myVariables[i] = new Variable(tmpSymbols[i]);
            myVariables[i].weight(myExpectedExcessReturns.toBigDecimal(i, 0).negate());
        }

    }

    public MarkowitzModel(final MarketEquilibrium aMarketEquilibrium, final BasicMatrix anExpectedExcessReturns) {

        super(aMarketEquilibrium);

        myExpectedExcessReturns = anExpectedExcessReturns;

        final String[] tmpSymbols = this.getMarketEquilibrium().getSymbols();
        myVariables = new Variable[tmpSymbols.length];
        for (int i = 0; i < tmpSymbols.length; i++) {
            myVariables[i] = new Variable(tmpSymbols[i]);
            myVariables[i].weight(myExpectedExcessReturns.toBigDecimal(i, 0).negate());
        }
    }

    @SuppressWarnings("unused")
    private MarkowitzModel(final MarketEquilibrium aMarketEquilibrium) {

        super(aMarketEquilibrium);

        myExpectedExcessReturns = null;
        myVariables = null;

        ProgrammingError.throwForIllegalInvocation();
    }

    public LowerUpper addConstraint(final BigDecimal aLower, final BigDecimal anUpper, final int... someInstrumentIndeces) {
        return myConstraints.put(someInstrumentIndeces, new LowerUpper(aLower, anUpper));
    }

    public final void clearAllConstraints() {
        myConstraints.clear();
        this.reset();
    }

    public final State getOptimisationState() {
        if (myOptimisationState == null) {
            myOptimisationState = State.UNEXPLORED;
        }
        return myOptimisationState;
    }

    public final void setLowerLimit(final int anInstrumentIndex, final BigDecimal aLimit) {
        myVariables[anInstrumentIndex].lower(aLimit);
        this.reset();
    }

    public final void setShortingAllowed(final boolean aFlag) {
        myShortingAllowed = aFlag;
        this.reset();
    }

    /**
     * <p>
     * Will set the target return to whatever you input and the
     * target variance to <code>null</code>.
     * </p><p>
     * Setting the target return implies that you disregard the risk
     * aversion factor and want the minimum risk portfolio with return
     * that is equal to or greater than the target.
     * </p><p>
     * By setting the target return too high it is possible to define
     * an infeasible optimisation problem. It is in fact (in
     * combination with setting lower and upper bounds on the
     * instrument weights) very easy to do so without realising it.
     * </p><p>
     * Setting a target return is not recommnded. It's much better to
     * modify the risk aversion factor.
     * </p>
     * @see #setTargetVariance(BigDecimal)
     */
    public final void setTargetReturn(final BigDecimal aTargetReturn) {
        myTargetReturn = aTargetReturn;
        myTargetVariance = null;
        this.reset();
    }

    /**
     * <p>
     * Will set the target variance to whatever you input and the
     * target return to <code>null</code>.
     * </p><p>
     * Setting the target variance implies that you disregard the risk
     * aversion factor and want the maximum return portfolio with risk
     * that is equal to or as close to the target as possible.
     * </p><p>
     * A target variance isn't an infeasibility risk the way a return
     * target is. The algorithm will return a solution, but there is no
     * guaranty the portfolio variance is equal to or less than the
     * target (as one may expect).
     * </p><p>
     * There is a performance penalty for setting a target variance as
     * the underlying optimisation model has to be solved several
     * (many) times with different pararmeters (different risk aversion
     * factors).
     * </p><p>
     * Setting a target variance is not recommnded. It's much better to
     * modify the risk aversion factor.
     * </p>
     * @see #setTargetReturn(BigDecimal)
     */
    public final void setTargetVariance(final BigDecimal aTargetVariance) {
        myTargetVariance = aTargetVariance;
        myTargetReturn = null;
        this.reset();
    }

    public final void setUpperLimit(final int anInstrumentIndex, final BigDecimal aLimit) {
        myVariables[anInstrumentIndex].upper(aLimit);
        this.reset();
    }

    @Override
    public String toString() {

        if (myOptimisationModel == null) {
            this.calculateAssetWeights();
        }

        return myOptimisationModel.toString();
    }

    private ExpressionsBasedModel generateOptimisationModel(final BigDecimal aRiskAversion, final BigDecimal aLowerReturnLimit,
            final BigDecimal anUpperReturnLimit) {

        final Variable[] tmpVariables = new Variable[myVariables.length];
        for (int i = 0; i < tmpVariables.length; i++) {
            tmpVariables[i] = myVariables[i].copy();
            if (!myShortingAllowed && ((myVariables[i].getLowerLimit() == null) || (myVariables[i].getLowerLimit().signum() == -1))) {
                tmpVariables[i].lower(BigMath.ZERO);
            }
        }

        final ExpressionsBasedModel retVal = new ExpressionsBasedModel(tmpVariables);

        final Expression tmpVarExpr = retVal.addExpression(VARIANCE, this.getCovariances().toBigStore(), null);
        tmpVarExpr.weight(aRiskAversion.multiply(BigMath.HALF));
        final int tmpLength = retVal.countVariables();

        final Expression tmpBalExpr = retVal.addExpression(BALANCE);
        for (int i = 0; i < tmpLength; i++) {
            tmpBalExpr.setLinearFactor(i, ONE);
        }
        tmpBalExpr.lower(BigMath.ONE);
        tmpBalExpr.upper(BigMath.ONE);

        if ((aLowerReturnLimit != null) || (anUpperReturnLimit != null)) {
            final int tmpLength1 = retVal.countVariables();

            final Expression tmpRetExpr = retVal.addExpression(RETURN);

            for (int i = 0; i < tmpLength1; i++) {
                tmpRetExpr.setLinearFactor(i, myExpectedExcessReturns.toBigStore().asList().get(i));
            }
            tmpRetExpr.lower(aLowerReturnLimit);
            tmpRetExpr.upper(anUpperReturnLimit);
        }

        for (final Map.Entry<int[], LowerUpper> tmpConstraintSet : myConstraints.entrySet()) {
            final int[] groupIDs = tmpConstraintSet.getKey();
            final Expression retVal2 = retVal.addExpression(Arrays.toString(tmpConstraintSet.getKey()));

            final int tmpLength1 = groupIDs.length;

            for (int i = 0; i < tmpLength1; i++) {
                retVal2.setLinearFactor(groupIDs[i], ONE);
            }
            final Expression tmpAddWeightGroupExpression = retVal2;
            tmpAddWeightGroupExpression.lower(tmpConstraintSet.getValue().lower).upper(tmpConstraintSet.getValue().upper);
        }

        return retVal;
    }

    private Optimisation.Result optimise() {

        Optimisation.Result retVal;

        if (myTargetReturn != null) {

            myOptimisationModel = this.generateOptimisationModel(this.getRiskAversion().toBigDecimal().multiply(BigMath.THOUSAND), myTargetReturn, null);
            retVal = myOptimisationModel.getDefaultSolver().solve();

        } else if (myTargetVariance != null) {

            BigDecimal tmpRiskAversion = this.getRiskAversion().toBigDecimal();
            BigDecimal tmpReturn = null;
            BigDecimal tmpVariance = null;

            BigDecimal tmpLowRiskAversion = null;
            BigDecimal tmpLowReturn = null;
            BigDecimal tmpLowVariance = null;

            BigDecimal tmpHighRiskAversion = null;
            BigDecimal tmpHighReturn = null;
            BigDecimal tmpHighVariance = null;

            BigDecimal tmpTargetDiff = null;

            myOptimisationModel = this.generateOptimisationModel(tmpRiskAversion, tmpLowReturn, tmpHighReturn);
            retVal = myOptimisationModel.getDefaultSolver().solve();

            tmpReturn = this.calculatePortfolioReturn(retVal, myExpectedExcessReturns).toBigDecimal();
            tmpVariance = this.calculatePortfolioVariance(retVal).toBigDecimal();
            tmpTargetDiff = tmpVariance.subtract(myTargetVariance);

            if (tmpTargetDiff.signum() > 0) {

                tmpHighRiskAversion = tmpRiskAversion;
                tmpHighReturn = tmpReturn;

                tmpRiskAversion = tmpRiskAversion.multiply(BigMath.TEN);

                myOptimisationModel = this.generateOptimisationModel(tmpRiskAversion, tmpLowReturn, tmpHighReturn);
                retVal = myOptimisationModel.getDefaultSolver().solve();

                tmpReturn = this.calculatePortfolioReturn(retVal, myExpectedExcessReturns).toBigDecimal();
                tmpVariance = this.calculatePortfolioVariance(retVal).toBigDecimal();
                tmpTargetDiff = tmpVariance.subtract(myTargetVariance);

                tmpLowRiskAversion = tmpRiskAversion;
                tmpLowReturn = tmpReturn;

            } else {

                tmpLowRiskAversion = tmpRiskAversion;
                tmpLowReturn = tmpReturn;

                tmpRiskAversion = tmpRiskAversion.multiply(BigMath.TENTH);

                myOptimisationModel = this.generateOptimisationModel(tmpRiskAversion, tmpLowReturn, tmpHighReturn);
                retVal = myOptimisationModel.getDefaultSolver().solve();

                tmpReturn = this.calculatePortfolioReturn(retVal, myExpectedExcessReturns).toBigDecimal();
                tmpVariance = this.calculatePortfolioVariance(retVal).toBigDecimal();
                tmpTargetDiff = tmpVariance.subtract(myTargetVariance);

                tmpHighRiskAversion = tmpRiskAversion;
                tmpHighReturn = tmpReturn;
            }

            int tmpIterCount = 0;

            do {

                tmpRiskAversion = tmpHighRiskAversion.add(tmpLowRiskAversion).multiply(BigMath.HALF);

                myOptimisationModel = this.generateOptimisationModel(tmpRiskAversion, tmpLowReturn, tmpHighReturn);
                retVal = myOptimisationModel.getDefaultSolver().solve();

                if (retVal != null) {

                    tmpReturn = this.calculatePortfolioReturn(retVal, myExpectedExcessReturns).toBigDecimal();
                    tmpVariance = this.calculatePortfolioVariance(retVal).toBigDecimal();
                    tmpTargetDiff = tmpVariance.subtract(myTargetVariance);

                    if (tmpTargetDiff.signum() < 0) {
                        tmpLowRiskAversion = tmpRiskAversion;
                        tmpLowReturn = tmpReturn;
                        tmpLowVariance = tmpVariance;
                    } else if (tmpTargetDiff.signum() > 0) {
                        tmpHighRiskAversion = tmpRiskAversion;
                        tmpHighReturn = tmpReturn;
                        tmpHighVariance = tmpVariance;
                    }

                    tmpIterCount++;

                    //                        BasicLogger.logDebug();
                    //                        BasicLogger.logDebug("Iter:   {}", tmpIterCount);
                    //                        BasicLogger.logDebug("Low:    {}", tmpLowVariance);
                    //                        BasicLogger.logDebug("Target: {}", myTargetVariance);
                    //                        BasicLogger.logDebug("High:   {}", tmpHighVariance);
                    //                        BasicLogger.logDebug("Diff:   {}", tmpTargetDiff);

                } else {

                    tmpIterCount = 20;
                    tmpTargetDiff = BigMath.ZERO;
                }

            } while ((tmpIterCount < 20) && (Math.abs(tmpTargetDiff.doubleValue()) > _0_000005));

        } else {

            myOptimisationModel = this.generateOptimisationModel(this.getRiskAversion().toBigDecimal(), null, null);
            retVal = myOptimisationModel.getDefaultSolver().solve();
        }

        if (retVal.getState().isNotLessThan(State.FEASIBLE)) {
            final Variable[] tmpVariables = myOptimisationModel.getVariables();
            for (int v = 0; v < tmpVariables.length; v++) {
                final BigDecimal tmpBigDecimal = TypeUtils.toBigDecimal(retVal.get(v), WEIGHT_CONTEXT);
                tmpVariables[v].setValue(tmpBigDecimal);
            }
        }

        return retVal;
    }

    @Override
    protected BasicMatrix calculateAssetReturns() {
        return myExpectedExcessReturns;
    }

    /**
     * Constrained optimisation.
     */
    @Override
    protected BasicMatrix calculateAssetWeights() {

        final Optimisation.Result tmpResult = this.optimise();

        myOptimisationState = tmpResult.getState();

        return PrimitiveMatrix.FACTORY.columns(tmpResult);
    }

    @Override
    protected void reset() {

        super.reset();

        myOptimisationModel = null;
        myOptimisationState = State.UNEXPLORED;
    }

    final Scalar<?> calculatePortfolioReturn(final Access1D<?> aWeightsVctr, final BasicMatrix aReturnsVctr) {
        return super.calculatePortfolioReturn(PrimitiveMatrix.FACTORY.columns(aWeightsVctr), aReturnsVctr);
    }

    final Scalar<?> calculatePortfolioVariance(final Access1D<?> aWeightsVctr) {
        return super.calculatePortfolioVariance(PrimitiveMatrix.FACTORY.columns(aWeightsVctr));
    }

}
