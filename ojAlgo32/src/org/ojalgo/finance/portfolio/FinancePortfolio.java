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

import static org.ojalgo.constant.PrimitiveMath.*;

import java.math.BigDecimal;
import java.util.List;

import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.MatrixBuilder;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.random.RandomUtils;
import org.ojalgo.random.process.GeometricBrownianMotion;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.StandardType;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * Classes in this package relate to modelling of financial investment
 * portfolios, and Modern Portfolio Theory.
 * <ul><li>
 * An asset is a resource with economic value, and something that can
 * be owned.
 * </li><li>
 * A portfolio is a collection of something (anything).
 * </li><li>
 * An instrument is a tool or device - an enabler.
 * </li><li>
 * Financial instruments enable financial transactions, and are defined
 * in terms of assets. A financial instrument can for instance be
 * evidence of ownership of, or a contractual right/obligation to receive
 * or deliver, an asset. Financial instruments are often assets
 * themselves as they can be owned/traded.
 * </li><li>
 * Financial portfolios contain financial instruments. Typically, in
 * terms of investments, it is a collection of assets with a common owner.
 * </li><li>
 * A portfolio is also an asset... Any asset can be viewed as a portfolio
 * containing only itself.
 * </li><li>
 * Here the term portfolio represents the collection and ownership, and the term
 * asset represents components/contents and underlying value. The term
 * instrument is not used here.
 * </li></ul>
 *
 * @author apete
 */
public abstract class FinancePortfolio implements Comparable<FinancePortfolio> {

    public static interface Context {

        double calculatePortfolioReturn(final FinancePortfolio weightsPortfolio);

        double calculatePortfolioVariance(final FinancePortfolio weightsPortfolio);

        BasicMatrix getAssetReturns();

        BasicMatrix getCorrelations();

        BasicMatrix getCovariances();

    }

    private static final double _0_95 = 0.95;

    protected static final NumberContext WEIGHT_CONTEXT = StandardType.PERCENT.copy();

    protected FinancePortfolio() {
        super();
    }

    public int compareTo(final FinancePortfolio ref) {

        final double tmpVolat1 = this.getVolatility();
        final double tmpVolat2 = ref.getVolatility();

        if (tmpVolat1 == tmpVolat2) {
            return Double.compare(this.getMeanReturn(), ref.getMeanReturn());
        } else {
            return Double.compare(tmpVolat1, tmpVolat2);
        }
    }

    public final GeometricBrownianMotion forecast() {

        final double tmpInitialValue = ONE;
        final double tmpExpectedValue = ONE + this.getMeanReturn();
        final double tmpValueVariance = this.getReturnVariance();
        final double tmpHorizon = ONE;

        return GeometricBrownianMotion.make(tmpInitialValue, tmpExpectedValue, tmpValueVariance, tmpHorizon);
    }

    public double getConformance(final FinancePortfolio aReference) {

        final BasicMatrix tmpMyWeights = this.makeColumnVector(this.getWeights());
        final BasicMatrix tmpRefWeights = this.makeColumnVector(aReference.getWeights());

        final Scalar<?> tmpNumerator = tmpMyWeights.multiplyVectors(tmpRefWeights);
        final Scalar<?> tmpDenom1 = tmpMyWeights.multiplyVectors(tmpMyWeights).root(2);
        final Scalar<?> tmpDenom2 = tmpRefWeights.multiplyVectors(tmpRefWeights).root(2);

        return tmpNumerator.doubleValue() / (tmpDenom1.doubleValue() * tmpDenom2.doubleValue());
    }

    /**
     * The mean/expected return of this instrument. 
     * May return either the absolute or excess return of the instrument. 
     * The context in which an instance is used should make it clear 
     * which. Calling {@linkplain #shift(Number)} with an appropriate
     * argument will transform between absolute and excess return. 
     */
    public abstract double getMeanReturn();

    /**
     * 'this' portfolio supplies the asset weights, and the portfolio
     * context supplies everything else that's required for the calculations.
     */
    public final double getMeanReturn(final Context aContext) {
        return aContext.calculatePortfolioReturn(this);
    }

    public double getReturnOverVariance() {
        return this.getMeanReturn() / this.getReturnVariance();
    }

    /**
     * The instrument's return variance.
     * 
     * Subclasses must override either {@linkplain #getReturnVariance()} or {@linkplain #getVolatility()}.
     */
    public double getReturnVariance() {
        final double tmpVolatility = this.getVolatility();
        return tmpVolatility * tmpVolatility;
    }

    /**
     * 'this' portfolio supplies the asset weights, and the portfolio
     * context supplies everything else that's required for the calculations.
     */
    public final double getReturnVariance(final Context aContext) {
        return aContext.calculatePortfolioVariance(this);
    }

    public double getSharpeRatio() {
        return this.getSharpeRatio(null);
    }

    public final double getSharpeRatio(final Number aRiskFreeReturn) {
        if (aRiskFreeReturn != null) {
            return (this.getMeanReturn() - aRiskFreeReturn.doubleValue()) / this.getVolatility();
        } else {
            return this.getMeanReturn() / this.getVolatility();
        }
    }

    /**
     * Value at Risk (VaR) is the maximum loss not exceeded with a 
     * given probability defined as the confidence level, over a given 
     * period of time.
     */
    public final double getValueAtRisk(final Number aConfidenceLevel, final Number aTimePeriod) {

        final double aReturn = this.getMeanReturn();
        final double aStdDev = this.getVolatility();

        final double tmpConfidenceScale = SQRT_TWO * RandomUtils.erfi(ONE - (TWO * (ONE - aConfidenceLevel.doubleValue())));
        final double tmpTimePeriod = aTimePeriod.doubleValue();

        return Math.max((Math.sqrt(tmpTimePeriod) * aStdDev * tmpConfidenceScale) - (tmpTimePeriod * aReturn), ZERO);
    }

    public double getValueAtRisk95() {
        return this.getValueAtRisk(_0_95, PrimitiveMath.ONE);
    }

    public double getVarianceOverReturn() {
        return this.getReturnVariance() / this.getMeanReturn();
    }

    /**
     * Volatility refers to the standard deviation of 
     * the change in value of an asset with a specific 
     * time horizon. It is often used to quantify the risk of the 
     * asset over that time period.
     * 
     * Subclasses must override either {@linkplain #getReturnVariance()} or {@linkplain #getVolatility()}.
     */
    public double getVolatility() {
        return PrimitiveFunction.SQRT.invoke(this.getReturnVariance());
    }

    /**
     * 'this' portfolio supplies the asset weights, and the portfolio
     * context supplies everything else that's required for the calculations.
     */
    public final double getVolatility(final Context aContext) {
        return Math.sqrt(aContext.calculatePortfolioVariance(this));
    }

    /**
     * This method returns a list of the weights of the Portfolio's
     * contained assets. An asset weight is NOT restricted to being
     * a share/percentage - it can be anything. Most subclasses do
     * however assume that the list of asset weights are
     * shares/percentages that sum up to 100%. Calling
     * {@linkplain #normalise()} will transform any set of weights to
     * that form.
     */
    public abstract List<BigDecimal> getWeights();

    /**
     * Normalised weights Portfolio
     */
    public final FinancePortfolio normalise() {
        return new NormalisedPortfolio(this);
    }

    @Override
    public String toString() {
        return TypeUtils.format("{}: Return={}, Variance={}, Volatility={}, Weights={}", this.getClass().getSimpleName(), this.getMeanReturn(),
                this.getReturnVariance(), this.getVolatility(), this.getWeights());
    }

    protected final MatrixBuilder<Double> getMatrixBuilder(final int aSize) {
        return PrimitiveMatrix.FACTORY.getBuilder(aSize);
    }

    protected final MatrixBuilder<Double> getMatrixBuilder(final int aRowDim, final int aColDim) {
        return PrimitiveMatrix.FACTORY.getBuilder(aRowDim, aColDim);
    }

    @SuppressWarnings("unchecked")
    protected final BasicMatrix makeColumnVector(final List<? extends Number> aColumn) {
        return PrimitiveMatrix.FACTORY.columns(aColumn);
    }

    protected abstract void reset();

}
