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
import java.util.ArrayList;
import java.util.List;

import org.ojalgo.access.Access2D;
import org.ojalgo.finance.portfolio.FinancePortfolio.Context;
import org.ojalgo.finance.portfolio.simulator.PortfolioSimulator;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.MatrixBuilder;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.random.process.GeometricBrownianMotion;

public final class SimplePortfolio extends FinancePortfolio implements Context {

    static List<SimpleAsset> toSimpleAssets(final Number[] someWeights) {

        final ArrayList<SimpleAsset> retVal = new ArrayList<SimpleAsset>(someWeights.length);

        for (int i = 0; i < someWeights.length; i++) {
            retVal.add(new SimpleAsset(someWeights[i]));
        }

        return retVal;
    }

    private transient BasicMatrix myAssetReturns = null;
    private transient BasicMatrix myAssetWeights = null;
    private final List<SimpleAsset> myComponents;
    private final BasicMatrix myCorrelations;
    private transient BasicMatrix myCovariances = null;
    private transient Number myMeanReturn;
    private transient Number myReturnVariance;

    private transient List<BigDecimal> myWeights;

    /**
     * @deprecated v32 Use {@link #SimplePortfolio(BasicMatrix, List)} instead.
     */
    @Deprecated
    public SimplePortfolio(final Access2D<?> aCorrelationsMatrix, final List<SimpleAsset> someAssets) {

        super();

        if ((someAssets.size() != aCorrelationsMatrix.getRowDim()) || (someAssets.size() != aCorrelationsMatrix.getColDim())) {
            throw new IllegalArgumentException("Input dimensions don't match!");
        }

        myCorrelations = PrimitiveMatrix.FACTORY.copy(aCorrelationsMatrix);
        myComponents = someAssets;
    }

    public SimplePortfolio(final BasicMatrix correlationsMatrix, final List<SimpleAsset> someAssets) {

        super();

        if ((someAssets.size() != correlationsMatrix.getRowDim()) || (someAssets.size() != correlationsMatrix.getColDim())) {
            throw new IllegalArgumentException("Input dimensions don't match!");
        }

        myCorrelations = correlationsMatrix;
        myComponents = someAssets;
    }

    public SimplePortfolio(final List<SimpleAsset> someAssets) {
        this(PrimitiveMatrix.FACTORY.makeEye(someAssets.size(), someAssets.size()), someAssets);
    }

    public SimplePortfolio(final Number... someWeights) {
        this(SimplePortfolio.toSimpleAssets(someWeights));
    }

    public SimplePortfolio(final Context aContext, final FinancePortfolio weightsPortfolio) {

        super();

        myCorrelations = aContext.getCorrelations();

        final BasicMatrix tmpCovariances = aContext.getCovariances();
        final BasicMatrix tmpAssetReturns = aContext.getAssetReturns();

        final List<BigDecimal> tmpWeights = weightsPortfolio.getWeights();

        if ((tmpWeights.size() != myCorrelations.getRowDim()) || (tmpWeights.size() != myCorrelations.getColDim())) {
            throw new IllegalArgumentException("Input dimensions don't match!");
        }

        myComponents = new ArrayList<SimpleAsset>(tmpWeights.size());
        for (int i = 0; i < tmpWeights.size(); i++) {
            final double tmpMeanReturn = tmpAssetReturns.doubleValue(i, 0);
            final double tmpVolatilty = Math.sqrt(tmpCovariances.doubleValue(i, i));
            final BigDecimal tmpWeight = tmpWeights.get(i);
            myComponents.add(new SimpleAsset(tmpMeanReturn, tmpVolatilty, tmpWeight));
        }
    }

    @SuppressWarnings("unused")
    private SimplePortfolio() {
        this((BasicMatrix) null, null);
    }

    public double calculatePortfolioReturn(final FinancePortfolio weightsPortfolio) {
        final List<BigDecimal> tmpWeights = weightsPortfolio.getWeights();
        final BasicMatrix tmpAssetWeights = this.makeColumnVector(tmpWeights);
        final BasicMatrix tmpAssetReturns = this.getAssetReturns();
        return MarketEquilibrium.calculatePortfolioReturn(tmpAssetWeights, tmpAssetReturns).doubleValue();
    }

    public double calculatePortfolioVariance(final FinancePortfolio weightsPortfolio) {
        final List<BigDecimal> tmpWeights = weightsPortfolio.getWeights();
        final BasicMatrix tmpAssetWeights = this.makeColumnVector(tmpWeights);
        return new MarketEquilibrium(this.getCovariances()).calculatePortfolioVariance(tmpAssetWeights).doubleValue();
    }

    public BasicMatrix getAssetReturns() {

        if (myAssetReturns == null) {

            final int tmpSize = myComponents.size();

            final MatrixBuilder<Double> tmpReturns = this.getMatrixBuilder(tmpSize, 1);

            for (int i = 0; i < tmpSize; i++) {
                tmpReturns.set(i, 0, this.getMeanReturn(i));
            }

            myAssetReturns = tmpReturns.build();
        }

        return myAssetReturns;
    }

    public double getCorrelation(final int aRow, final int aCol) {
        return myCorrelations.doubleValue(aRow, aCol);
    }

    public BasicMatrix getCorrelations() {
        return myCorrelations;
    }

    public double getCovariance(final int aRow, final int aCol) {

        final BasicMatrix tmpCovariances = myCovariances;

        if (tmpCovariances != null) {

            return tmpCovariances.doubleValue(aRow, aCol);

        } else {

            final double tmpRowRisk = this.getVolatility(aRow);
            final double tmpColRisk = this.getVolatility(aCol);

            final double tmpCorrelation = this.getCorrelation(aRow, aCol);

            return tmpRowRisk * tmpCorrelation * tmpColRisk;
        }
    }

    public BasicMatrix getCovariances() {

        if (myCovariances == null) {

            final int tmpSize = myComponents.size();

            final MatrixBuilder<Double> tmpCovaris = this.getMatrixBuilder(tmpSize, tmpSize);

            for (int j = 0; j < tmpSize; j++) {
                for (int i = 0; i < tmpSize; i++) {
                    tmpCovaris.set(i, j, this.getCovariance(i, j));
                }
            }

            myCovariances = tmpCovaris.build();
        }

        return myCovariances;
    }

    @Override
    public double getMeanReturn() {

        if (myMeanReturn == null) {
            final BasicMatrix tmpWeightsVector = this.getAssetWeights();
            final BasicMatrix tmpReturnsVector = this.getAssetReturns();
            myMeanReturn = MarketEquilibrium.calculatePortfolioReturn(tmpWeightsVector, tmpReturnsVector).getNumber();
        }

        return myMeanReturn.doubleValue();
    }

    public double getMeanReturn(final int index) {
        return myComponents.get(index).getMeanReturn();
    }

    @Override
    public double getReturnVariance() {

        if (myReturnVariance == null) {
            final MarketEquilibrium tmpMarketEquilibrium = new MarketEquilibrium(this.getCovariances());
            final BasicMatrix tmpWeightsVector = this.getAssetWeights();
            myReturnVariance = tmpMarketEquilibrium.calculatePortfolioVariance(tmpWeightsVector).getNumber();
        }

        return myReturnVariance.doubleValue();
    }

    public double getReturnVariance(final int index) {
        return myComponents.get(index).getReturnVariance();
    }

    public PortfolioSimulator getSimulator() {

        final List<GeometricBrownianMotion> tmpAssetProcesses = new ArrayList<GeometricBrownianMotion>(myComponents.size());

        for (final SimpleAsset tmpAsset : myComponents) {
            final GeometricBrownianMotion tmpForecast = tmpAsset.forecast();
            tmpForecast.setValue(tmpAsset.getWeight().doubleValue());
            tmpAssetProcesses.add(tmpForecast);
        }

        return new PortfolioSimulator(myCorrelations.toPrimitiveStore(), tmpAssetProcesses);

    }

    public double getVolatility(final int index) {
        return myComponents.get(index).getVolatility();
    }

    public BigDecimal getWeight(final int index) {
        return myComponents.get(index).getWeight();
    }

    @Override
    public List<BigDecimal> getWeights() {

        if (myWeights == null) {

            myWeights = new ArrayList<BigDecimal>(myComponents.size());

            for (final SimpleAsset tmpAsset : myComponents) {
                myWeights.add(tmpAsset.getWeight());
            }
        }

        return myWeights;
    }

    @Override
    protected void reset() {

        myMeanReturn = null;
        myReturnVariance = null;
        myWeights = null;

        myCovariances = null;
        myAssetReturns = null;
        myAssetWeights = null;

        for (final SimpleAsset tmpAsset : myComponents) {
            tmpAsset.reset();
        }
    }

    BasicMatrix getAssetWeights() {

        if (myAssetWeights == null) {

            final int tmpSize = myComponents.size();

            final MatrixBuilder<Double> tmpWeights = this.getMatrixBuilder(tmpSize, 1);

            for (int i = 0; i < tmpSize; i++) {
                tmpWeights.set(i, 0, this.getWeight(i));
            }

            myAssetWeights = tmpWeights.build();
        }

        return myAssetWeights;
    }

}
