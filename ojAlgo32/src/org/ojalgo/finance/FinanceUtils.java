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
package org.ojalgo.finance;

import static org.ojalgo.constant.PrimitiveMath.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;

import org.ojalgo.array.Array1D;
import org.ojalgo.array.ArrayUtils;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.MatrixBuilder;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.random.Deterministic;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.random.RandomUtils;
import org.ojalgo.random.SampleSet;
import org.ojalgo.random.process.GeometricBrownianMotion;
import org.ojalgo.series.CalendarDateSeries;
import org.ojalgo.series.CoordinationSet;
import org.ojalgo.type.CalendarDate;
import org.ojalgo.type.CalendarDateUnit;

public abstract class FinanceUtils {

    public static double calculateValueAtRisk(final double aReturn, final double aStdDev, final double aConfidence, final double aTime) {

        final double tmpConfidenceScale = SQRT_TWO * RandomUtils.erfi(ONE - (TWO * (ONE - aConfidence)));

        return Math.max((Math.sqrt(aTime) * aStdDev * tmpConfidenceScale) - (aTime * aReturn), ZERO);
    }

    public static GeometricBrownianMotion estimateExcessDiffusionProcess(final CalendarDateSeries<?> aPriceSeries,
            final CalendarDateSeries<?> aRiskFreeInterestRateSeries, final CalendarDateUnit aTimeUnit) {

        final SampleSet tmpSampleSet = FinanceUtils.makeExcessGrowthRateSampleSet(aPriceSeries, aRiskFreeInterestRateSeries);

        // The average number of millis between to subsequent keys in the series.
        double tmpStepSize = aPriceSeries.getResolution().size();
        // The time between to keys expressed in terms of the specified time meassure and unit.
        tmpStepSize /= aTimeUnit.size();

        final double tmpExp = tmpSampleSet.getMean();
        final double tmpVar = tmpSampleSet.getVariance();

        final double tmpDiff = Math.sqrt(tmpVar / tmpStepSize);
        final double tmpDrift = (tmpExp / tmpStepSize) + ((tmpDiff * tmpDiff) / TWO);

        final GeometricBrownianMotion retVal = new GeometricBrownianMotion(tmpDrift, tmpDiff);

        return retVal;
    }

    public static CalendarDateSeries<RandomNumber> forecast(final CalendarDateSeries<? extends Number> aSeries, final int aPointCount,
            final CalendarDateUnit aTimeUnit, final boolean includeOriginalSeries) {

        final CalendarDateSeries<RandomNumber> retVal = new CalendarDateSeries<RandomNumber>(aTimeUnit);
        retVal.name(aSeries.getName()).colour(aSeries.getColour());

        final double tmpSamplePeriod = (double) aSeries.getAverageStepSize() / (double) aTimeUnit.size();
        final GeometricBrownianMotion tmpProcess = GeometricBrownianMotion.estimate(aSeries.getDataSeries(), tmpSamplePeriod);

        if (includeOriginalSeries) {
            for (final Entry<CalendarDate, ? extends Number> tmpEntry : aSeries.entrySet()) {
                retVal.put(tmpEntry.getKey(), new Deterministic(tmpEntry.getValue()));
            }
        }

        final CalendarDate tmpLastKey = aSeries.lastKey();
        final double tmpLastValue = aSeries.lastValue().doubleValue();

        tmpProcess.setValue(tmpLastValue);

        for (int i = 1; i <= aPointCount; i++) {
            retVal.put(tmpLastKey.millis + (i * aTimeUnit.size()), tmpProcess.getDistribution(i));
        }

        return retVal;
    }

    public static CalendarDateSeries<BigDecimal> makeCalendarPriceSeries(final double[] somePrices, final Calendar aStartCalendar,
            final CalendarDateUnit aResolution) {

        final CalendarDateSeries<BigDecimal> retVal = new CalendarDateSeries<BigDecimal>(aResolution);

        FinanceUtils.copyValues(retVal, new CalendarDate(aStartCalendar), somePrices);

        return retVal;
    }

    /**
     * @param aTimeSeriesCollection
     * @return Annualised covariances
     */
    public static <V extends Number> BasicMatrix makeCovarianceMatrix(final Collection<CalendarDateSeries<V>> aTimeSeriesCollection) {

        final CoordinationSet<V> tmpCoordinator = new CoordinationSet<V>(aTimeSeriesCollection).prune();

        final ArrayList<SampleSet> tmpSampleSets = new ArrayList<SampleSet>();
        for (final CalendarDateSeries<V> tmpTimeSeries : aTimeSeriesCollection) {
            final double[] someValues = tmpCoordinator.get(tmpTimeSeries).getPrimitiveValues();
            final int tmpSize1 = someValues.length - 1;

            final double[] retVal = new double[tmpSize1];

            for (int i = 0; i < tmpSize1; i++) {
                retVal[i] = Math.log(someValues[i + 1] / someValues[i]);
            }
            final SampleSet tmpMakeUsingLogarithmicChanges = SampleSet.wrap(ArrayUtils.wrapAccess1D(retVal));
            tmpSampleSets.add(tmpMakeUsingLogarithmicChanges);
        }

        final int tmpSize = aTimeSeriesCollection.size();

        final MatrixBuilder<Double> retValStore = PrimitiveMatrix.getBuilder(tmpSize, tmpSize);

        final double tmpToYearFactor = (double) CalendarDateUnit.YEAR.size() / (double) tmpCoordinator.getResolution().size();

        SampleSet tmpRowSet;
        SampleSet tmpColSet;

        for (int j = 0; j < tmpSize; j++) {

            tmpColSet = tmpSampleSets.get(j);

            for (int i = 0; i < tmpSize; i++) {

                tmpRowSet = tmpSampleSets.get(i);

                retValStore.set(i, j, tmpToYearFactor * tmpRowSet.getCovariance(tmpColSet));
            }
        }

        return retValStore.build();
    }

    public static CalendarDateSeries<BigDecimal> makeDatePriceSeries(final double[] somePrices, final Date aStartDate, final CalendarDateUnit aResolution) {

        final CalendarDateSeries<BigDecimal> retVal = new CalendarDateSeries<BigDecimal>(aResolution);

        FinanceUtils.copyValues(retVal, new CalendarDate(aStartDate), somePrices);

        return retVal;
    }

    /**
     * @param aPriceSeries A series of prices
     * @param aRiskFreeInterestRateSeries A series of interest rates (risk free return expressed in %, 5.0 means 5.0% annualized risk free return)
     * @return A sample set of price growth rates adjusted for risk free return
     */
    public static SampleSet makeExcessGrowthRateSampleSet(final CalendarDateSeries<?> aPriceSeries, final CalendarDateSeries<?> aRiskFreeInterestRateSeries) {

        if (aPriceSeries.size() != aRiskFreeInterestRateSeries.size()) {
            throw new IllegalArgumentException("The two series must have the same size (number of elements).");
        }

        if (!aPriceSeries.firstKey().equals(aRiskFreeInterestRateSeries.firstKey())) {
            throw new IllegalArgumentException("The two series must have the same first key (date or calendar).");
        }

        if (!aPriceSeries.lastKey().equals(aRiskFreeInterestRateSeries.lastKey())) {
            throw new IllegalArgumentException("The two series must have the same last key (date or calendar).");
        }

        final double[] tmpPrices = aPriceSeries.getPrimitiveValues();
        final double[] tmpRiskFreeInterestRates = aRiskFreeInterestRateSeries.getPrimitiveValues();

        final Array1D<Double> retVal = Array1D.PRIMITIVE.makeZero(tmpPrices.length - 1);

        final CalendarDateUnit tmpUnit = aPriceSeries.getResolution();
        double tmpThisRiskFree, tmpNextRiskFree, tmpAvgRiskFree, tmpRiskFreeGrowthRate, tmpThisPrice, tmpNextPrice, tmpPriceGrowthFactor, tmpPriceGrowthRate, tmpAdjustedPriceGrowthRate;

        for (int i = 0; i < retVal.size(); i++) {

            tmpThisRiskFree = tmpRiskFreeInterestRates[i] / PrimitiveMath.HUNDRED;
            tmpNextRiskFree = tmpRiskFreeInterestRates[i + 1] / PrimitiveMath.HUNDRED;
            tmpAvgRiskFree = (tmpThisRiskFree + tmpNextRiskFree) / PrimitiveMath.TWO;
            tmpRiskFreeGrowthRate = FinanceUtils.toGrowthRateFromAnnualReturn(tmpAvgRiskFree, tmpUnit);

            tmpThisPrice = tmpPrices[i];
            tmpNextPrice = tmpPrices[i + 1];
            tmpPriceGrowthFactor = tmpNextPrice / tmpThisPrice;
            tmpPriceGrowthRate = Math.log(tmpPriceGrowthFactor);

            tmpAdjustedPriceGrowthRate = tmpPriceGrowthRate - tmpRiskFreeGrowthRate;

            retVal.set(i, tmpAdjustedPriceGrowthRate);
        }

        return SampleSet.wrap(retVal);
    }

    /**
     * @param aPriceSeries A series of prices
     * @param aRiskFreeInterestRateSeries A series of interest rates (risk free return expressed in %, 5.0 means 5.0%)
     * @return A sample set of price growth rates adjusted for risk free return
     * @deprecated v32 Use {@link #makeExcessGrowthRateSampleSet(CalendarDateSeries,CalendarDateSeries)} instead
     */
    @Deprecated
    public static SampleSet makeExcessSampleSet(final CalendarDateSeries<?> aPriceSeries, final CalendarDateSeries<?> aRiskFreeInterestRateSeries) {
        return FinanceUtils.makeExcessGrowthRateSampleSet(aPriceSeries, aRiskFreeInterestRateSeries);
    }

    /**
     * GrowthRate = ln(GrowthFactor)
     * 
     * @param growthFactor A growth factor per unit (day, week, month, year...)
     * @param growthFactorUnit A growth factor unit
     * @return Annualised return (percentage per year)
     */
    public static double toAnnualReturnFromGrowthFactor(final double growthFactor, final CalendarDateUnit growthFactorUnit) {
        final double tmpGrowthFactorUnitsPerYear = growthFactorUnit.convert(CalendarDateUnit.YEAR);
        return PrimitiveFunction.POW.invoke(growthFactor, tmpGrowthFactorUnitsPerYear) - PrimitiveMath.ONE;
    }

    /**
     * AnnualReturn = exp(GrowthRate * GrowthRateUnitsPerYear) - 1.0
     * 
     * @param growthRate A growth rate per unit (day, week, month, year...)
     * @param growthRateUnit A growth rate unit
     * @return Annualised return (percentage per year)
     */
    public static double toAnnualReturnFromGrowthRate(final double growthRate, final CalendarDateUnit growthRateUnit) {
        final double tmpGrowthRateUnitsPerYear = growthRateUnit.convert(CalendarDateUnit.YEAR);
        return PrimitiveFunction.EXPM1.invoke(growthRate * tmpGrowthRateUnitsPerYear);
    }

    /**
     * GrowthFactor = exp(GrowthRate)
     *
     * @param annualReturn Annualised return (percentage per year)
     * @param growthFactorUnit A growth factor unit
     * @return A growth factor per unit (day, week, month, year...)
     */
    public static double toGrowthFactorFromAnnualReturn(final double annualReturn, final CalendarDateUnit growthFactorUnit) {
        final double tmpAnnualGrowthFactor = PrimitiveMath.ONE + annualReturn;
        final double tmpYearsPerGrowthFactorUnit = CalendarDateUnit.YEAR.convert(growthFactorUnit);
        return PrimitiveFunction.POW.invoke(tmpAnnualGrowthFactor, tmpYearsPerGrowthFactorUnit);
    }

    /**
     * GrowthFactor = exp(GrowthRate)
     *
     * @param anInterestRate Annualised return (percentage per year)
     * @param aGrowthFactorUnit A growth factor unit
     * @return A growth factor per unit (day, week, month, year...)
     * @deprecated v32 Use {@link #toGrowthFactorFromAnnualReturn(double,CalendarDateUnit)} instead
     */
    @Deprecated
    public static double toGrowthFactorFromInterestRate(final double anInterestRate, final CalendarDateUnit aGrowthFactorUnit) {
        return FinanceUtils.toGrowthFactorFromAnnualReturn(anInterestRate, aGrowthFactorUnit);
    }

    /**
     * GrowthRate = ln(1.0 + InterestRate) / GrowthRateUnitsPerYear
     *
     * @param annualReturn Annualised return (percentage per year)
     * @param growthRateUnit A growth rate unit
     * @return A growth rate per unit (day, week, month, year...)
     */
    public static double toGrowthRateFromAnnualReturn(final double annualReturn, final CalendarDateUnit growthRateUnit) {
        final double tmpAnnualGrowthRate = PrimitiveFunction.LOG1P.invoke(annualReturn);
        final double tmpYearsPerGrowthRateUnit = CalendarDateUnit.YEAR.convert(growthRateUnit);
        return tmpAnnualGrowthRate * tmpYearsPerGrowthRateUnit;
    }

    /**
     * GrowthRate = ln(1.0 + InterestRate) / GrowthRateUnitsPerYear
     *
     * @param anInterestRate Annualised interest rate (percentage per year)
     * @param aGrowthRateUnit A growth rate unit
     * @return A growth rate per unit (day, week, month, year...)
     * @deprecated v32 Use {@link #toGrowthRateFromAnnualReturn(double,CalendarDateUnit)} instead
     */
    @Deprecated
    public static double toGrowthRateFromInterestRate(final double anInterestRate, final CalendarDateUnit aGrowthRateUnit) {
        return FinanceUtils.toGrowthRateFromAnnualReturn(anInterestRate, aGrowthRateUnit);
    }

    /**
     * GrowthRate = ln(GrowthFactor)
     * 
     * @param aGrowthFactor A growth factor per unit (day, week, month, year...)
     * @param aGrowthFactorUnit A growth factor unit
     * @return Annualised interest rate (percentage per year)
     * @deprecated v32 Use {@link #toAnnualReturnFromGrowthFactor(double,CalendarDateUnit)} instead
     */
    @Deprecated
    public static double toInterestRateFromGrowthFactor(final double aGrowthFactor, final CalendarDateUnit aGrowthFactorUnit) {
        return FinanceUtils.toAnnualReturnFromGrowthFactor(aGrowthFactor, aGrowthFactorUnit);
    }

    /**
     * InterestRate = exp(GroiwthRate * GrowthRateUnitsPerYear) + 1.0
     * 
     * @param aGrowthRate A growth rate per unit (day, week, month, year...)
     * @param aGrowthRateUnit A growth rate unit
     * @return Annualised interest rate (percentage per year)
     * @deprecated v32 Use {@link #toAnnualReturnFromGrowthRate(double,CalendarDateUnit)} instead
     */
    @Deprecated
    public static double toInterestRateFromGrowthRate(final double aGrowthRate, final CalendarDateUnit aGrowthRateUnit) {
        return FinanceUtils.toAnnualReturnFromGrowthRate(aGrowthRate, aGrowthRateUnit);
    }

    private static <K extends Comparable<K>> void copyValues(final CalendarDateSeries<BigDecimal> aSeries, final CalendarDate aFirstKey,
            final double[] someValues) {

        CalendarDate tmpKey = aFirstKey;

        for (int tmpValueIndex = 0; tmpValueIndex < someValues.length; tmpValueIndex++) {

            aSeries.put(tmpKey, new BigDecimal(someValues[tmpValueIndex]));

            tmpKey = aSeries.step(tmpKey);
        }
    }

    private FinanceUtils() {
        super();
    }

}
