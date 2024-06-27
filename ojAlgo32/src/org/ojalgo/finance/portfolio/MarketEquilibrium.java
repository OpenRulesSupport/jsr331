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

import java.math.BigDecimal;

import org.ojalgo.ProgrammingError;
import org.ojalgo.array.ArrayUtils;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.MatrixBuilder;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.scalar.BigScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;

/**
 * MarketEquilibrium translates between the market portfolio weights
 * and the equilibrium excess returns. The only things needed to do
 * those translations are the covariance matrix and the risk aversion
 * factor - that's what you need to supply when you instantiate this
 * class.
 *
 * @see #calculateAssetReturns(BasicMatrix)
 * @see #calculateAssetWeights(BasicMatrix)
 * 
 * @author apete
 */
public class MarketEquilibrium {

    public static enum Target {

        WEIGHTS, RETURNS, BOTH;

    }

    private static final BigDecimal DEFAULT_RISK_AVERSION = BigMath.ONE;
    private static final String STRING_ZERO = "0";
    private static final String SYMBOL = "Asset_";

    /**
     * Calculates the portfolio return using the input instrument
     * weights and returns.
     */
    public static Scalar<?> calculatePortfolioReturn(final BasicMatrix aWeightsVctr, final BasicMatrix aReturnsVctr) {
        return aWeightsVctr.multiplyVectors(aReturnsVctr);
    }

    private static String[] makeSymbols(final int aCount) {

        final String[] retVal = new String[aCount];

        final int tmpMaxLength = Integer.toString(aCount - 1).length();

        String tmpNumberString;
        for (int i = 0; i < aCount; i++) {
            tmpNumberString = Integer.toString(i);
            while (tmpNumberString.length() < tmpMaxLength) {
                tmpNumberString = STRING_ZERO + tmpNumberString;
            }
            retVal[i] = SYMBOL + tmpNumberString;
        }

        return retVal;
    }

    private final BasicMatrix myCovariances;
    private BigDecimal myRiskAversion;
    private final String[] mySymbols;

    public MarketEquilibrium(final BasicMatrix aCovarianceMatrix) {
        this(aCovarianceMatrix, DEFAULT_RISK_AVERSION);
    }

    public MarketEquilibrium(final BasicMatrix aCovarianceMatrix, final Number aRiskAversionFactor) {
        this(MarketEquilibrium.makeSymbols(aCovarianceMatrix.getRowDim()), aCovarianceMatrix, aRiskAversionFactor);
    }

    public MarketEquilibrium(final String[] assetNamesOrKeys, final BasicMatrix aCovarianceMatrix) {

        super();

        mySymbols = ArrayUtils.copyOf(assetNamesOrKeys);
        myCovariances = aCovarianceMatrix;
        myRiskAversion = DEFAULT_RISK_AVERSION;
    }

    public MarketEquilibrium(final String[] assetNamesOrKeys, final BasicMatrix aCovarianceMatrix, final Number aRiskAversionFactor) {

        super();

        mySymbols = ArrayUtils.copyOf(assetNamesOrKeys);
        myCovariances = aCovarianceMatrix;
        myRiskAversion = TypeUtils.toBigDecimal(aRiskAversionFactor);
    }

    @SuppressWarnings("unused")
    private MarketEquilibrium() {

        this(null, null, null);

        ProgrammingError.throwForIllegalInvocation();
    }

    MarketEquilibrium(final MarketEquilibrium aMarket) {
        this(aMarket.getSymbols(), aMarket.getCovariances(), aMarket.getRiskAversion().getNumber());
    }

    /**
     * If the input vector of asset weights are the weights of the market
     * portfolio, then the ouput is the equilibrium excess returns.
     */
    public BasicMatrix calculateAssetReturns(final BasicMatrix aWeightsVctr) {
        final BasicMatrix tmpWeightsVctr = myRiskAversion.compareTo(DEFAULT_RISK_AVERSION) == 0 ? aWeightsVctr : aWeightsVctr.multiply(myRiskAversion);
        return myCovariances.multiplyRight(tmpWeightsVctr);
    }

    /**
     * If the input vector of returns are the equilibrium excess returns
     * then the output is the market portfolio weights. This is
     * unconstrained optimisation - there are no constraints on the
     * resulting instrument weights.
     */
    public BasicMatrix calculateAssetWeights(final BasicMatrix aReturnsVctr) {
        final BasicMatrix tmpWeightsVctr = myCovariances.solve(aReturnsVctr);
        if (myRiskAversion.compareTo(DEFAULT_RISK_AVERSION) == 0) {
            return tmpWeightsVctr;
        } else {
            return tmpWeightsVctr.divide(myRiskAversion);
        }
    }

    /**
     * Calculates the portfolio variance using the input instrument
     * weights.
     */
    public Scalar<?> calculatePortfolioVariance(final BasicMatrix aWeightsVctr) {

        BasicMatrix tmpLeft;
        BasicMatrix tmpRight;

        if (aWeightsVctr.getColDim() == 1) {
            tmpLeft = aWeightsVctr.transpose();
            tmpRight = aWeightsVctr;
        } else {
            tmpLeft = aWeightsVctr;
            tmpRight = aWeightsVctr.transpose();
        }

        return myCovariances.multiplyRight(tmpRight).multiplyLeft(tmpLeft).toScalar(0, 0);
    }

    /**
     * Will set the risk aversion factor to the best fit for an observed
     * pair of market portfolio asset weights and equilibrium/historical
     * excess returns.
     */
    public void calibrate(final BasicMatrix aWeightsVctr, final BasicMatrix aReturnsVctr) {

        final Scalar<?> tmpImpliedRiskAversion = this.calculateImpliedRiskAversion(aWeightsVctr, aReturnsVctr, Target.BOTH);

        this.setRiskAversion(tmpImpliedRiskAversion.getNumber());
    }

    public MarketEquilibrium copy() {
        return new MarketEquilibrium(this);
    }

    public BasicMatrix getCovariances() {
        return myCovariances;
    }

    public Scalar<?> getRiskAversion() {
        return new BigScalar(myRiskAversion);
    }

    public String[] getSymbols() {
        return ArrayUtils.copyOf(mySymbols);
    }

    public void setRiskAversion(final Number aFactor) {

        final BigDecimal tmpFactor = TypeUtils.toBigDecimal(aFactor);

        if (tmpFactor.signum() == 0) {
            throw new IllegalArgumentException("Zero Risk Aversion!");
        }

        myRiskAversion = tmpFactor;
    }

    public BasicMatrix toCorrelations() {

        final BasicMatrix tmpCovariances = this.getCovariances();

        final int tmpSize = tmpCovariances.getRowDim();

        final MatrixBuilder<Double> retVal = PrimitiveMatrix.getBuilder(tmpSize, tmpSize);

        final double[] tmpVolatilities = new double[tmpSize];
        for (int ij = 0; ij < tmpSize; ij++) {
            tmpVolatilities[ij] = Math.sqrt(tmpCovariances.doubleValue(ij, ij));
        }

        for (int j = 0; j < tmpSize; j++) {
            retVal.set(j, j, PrimitiveMath.ONE);
            for (int i = j + 1; i < tmpSize; i++) {
                final double tmpCovariance = tmpCovariances.doubleValue(i, j);
                final double tmpCorrelation = tmpCovariance / (tmpVolatilities[i] * tmpVolatilities[j]);
                retVal.set(i, j, tmpCorrelation);
                retVal.set(j, i, tmpCorrelation);
            }
        }

        return retVal.build();
    }

    /**
     * Will calculate the risk aversion factor that is the best fit for
     * an observed pair of market portfolio weights and equilibrium/historical
     * excess returns.
     */
    Scalar<?> calculateImpliedRiskAversion(final BasicMatrix aWeightsVctr, final BasicMatrix aReturnsVctr, final Target target) {

        BasicMatrix tmpTransformedWeights = null;
        BasicMatrix tmpTransformedReturns = null;

        BasicMatrix tmpLHS = null;
        BasicMatrix tmpRHS = null;

        switch (target) {

        case RETURNS:

            tmpTransformedWeights = myCovariances.multiplyRight(aWeightsVctr);

            tmpLHS = tmpTransformedWeights;
            tmpRHS = aReturnsVctr;

            break;

        case WEIGHTS:

            tmpTransformedReturns = myCovariances.solve(aReturnsVctr);

            tmpLHS = aWeightsVctr;
            tmpRHS = tmpTransformedReturns;

            break;

        default:

            tmpTransformedWeights = myCovariances.multiplyRight(aWeightsVctr);
            tmpTransformedReturns = myCovariances.solve(aReturnsVctr);

            tmpLHS = aWeightsVctr.mergeColumns(tmpTransformedWeights);
            tmpRHS = tmpTransformedReturns.mergeColumns(aReturnsVctr);

            break;
        }

        final Scalar<?> retVal = tmpLHS.solve(tmpRHS).toScalar(0, 0);
        if (retVal.isZero()) {
            return BigScalar.ONE;
        } else if (!retVal.isPositive()) {
            return retVal.negate();
        } else {
            return retVal;
        }
    }

}
