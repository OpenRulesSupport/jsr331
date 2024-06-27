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

import java.util.List;

import org.ojalgo.ProgrammingError;
import org.ojalgo.matrix.BasicMatrix;

public final class FixedWeightsPortfolio extends EquilibriumModel {

    private final BasicMatrix myWeights;

    public FixedWeightsPortfolio(final MarketEquilibrium aMarketEquilibrium, final BasicMatrix assetWeightsInColumn) {

        super(aMarketEquilibrium);

        myWeights = assetWeightsInColumn;
    }

    public FixedWeightsPortfolio(final Context aContext, final FinancePortfolio weightsPortfolio) {

        super(aContext);

        myWeights = this.makeColumnVector(weightsPortfolio.getWeights());
    }

    @SuppressWarnings("unused")
    private FixedWeightsPortfolio(final MarketEquilibrium aMarketEquilibrium) {

        super(aMarketEquilibrium);

        myWeights = null;

        ProgrammingError.throwForIllegalInvocation();
    }

    public void calibrate(final EquilibriumModel targetReturns) {
        this.calibrate(myWeights, targetReturns.getAssetReturns(), MarketEquilibrium.Target.RETURNS);
    }

    public void calibrate(final List<? extends Number> targetReturns) {
        this.calibrate(myWeights, this.makeColumnVector(targetReturns), MarketEquilibrium.Target.RETURNS);
    }

    public void calibrate(final SimplePortfolio targetReturns) {
        this.calibrate(myWeights, targetReturns.getAssetReturns(), MarketEquilibrium.Target.RETURNS);
    }

    @Override
    protected BasicMatrix calculateAssetReturns() {
        return this.calculateAssetReturns(myWeights);
    }

    @Override
    protected BasicMatrix calculateAssetWeights() {
        return myWeights;
    }

}
