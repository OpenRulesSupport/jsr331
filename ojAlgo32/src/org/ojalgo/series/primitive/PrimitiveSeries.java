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
package org.ojalgo.series.primitive;

import java.util.Iterator;

import org.ojalgo.access.Access1D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.array.Array1D;
import org.ojalgo.function.PreconfiguredSecond;
import org.ojalgo.function.implementation.PrimitiveFunction;

public abstract class PrimitiveSeries implements Access1D<Double> {

    public static PrimitiveSeries copy(final Access1D<?> aBase) {
        return new AccessSeries(Array1D.PRIMITIVE.copy(aBase));
    }

    public static PrimitiveSeries wrap(final Access1D<?> aBase) {
        return new AccessSeries(aBase);
    }

    public PrimitiveSeries() {
        super();
    }

    public PrimitiveSeries add(final double aValue) {
        return new UnaryFunctionSeries(this, new PreconfiguredSecond<Double>(PrimitiveFunction.ADD, aValue));
    }

    public PrimitiveSeries add(final PrimitiveSeries aSeries) {
        return new BinaryFunctionSeries(this, PrimitiveFunction.ADD, aSeries);
    }

    public PrimitiveSeries copy() {
        return this.toDataSeries();
    }

    public PrimitiveSeries differences() {
        return new DifferencesSeries(this);
    }

    public PrimitiveSeries divide(final double aValue) {
        return new UnaryFunctionSeries(this, new PreconfiguredSecond<Double>(PrimitiveFunction.DIVIDE, aValue));
    }

    public PrimitiveSeries divide(final PrimitiveSeries aSeries) {
        return new BinaryFunctionSeries(this, PrimitiveFunction.DIVIDE, aSeries);
    }

    public final double doubleValue(final int anInd) {
        return this.value(anInd);
    }

    public PrimitiveSeries exp() {
        return new UnaryFunctionSeries(this, PrimitiveFunction.EXP);
    }

    public final Double get(final int anInd) {
        return this.value(anInd);
    }

    public final Iterator<Double> iterator() {
        return new Iterator1D<Double>(this);
    }

    public PrimitiveSeries log() {
        return new UnaryFunctionSeries(this, PrimitiveFunction.LOG);
    }

    public PrimitiveSeries multiply(final double aValue) {
        return new UnaryFunctionSeries(this, new PreconfiguredSecond<Double>(PrimitiveFunction.MULTIPLY, aValue));
    }

    public PrimitiveSeries multiply(final PrimitiveSeries aSeries) {
        return new BinaryFunctionSeries(this, PrimitiveFunction.MULTIPLY, aSeries);
    }

    public PrimitiveSeries quotients() {
        return new QuotientsSeries(this);
    }

    public PrimitiveSeries runningProduct(final double initialValue) {

        final int tmpNewSize = this.size() + 1;

        final double[] tmpValues = new double[tmpNewSize];

        double tmpAggrVal = tmpValues[0] = initialValue;
        for (int i = 1; i < tmpNewSize; i++) {
            tmpValues[i] = tmpAggrVal *= this.value(i - 1);
        }

        return DataSeries.wrap(tmpValues);
    }

    public PrimitiveSeries runningSum(final double initialValue) {

        final int tmpNewSize = this.size() + 1;

        final double[] tmpValues = new double[tmpNewSize];

        double tmpAggrVal = tmpValues[0] = initialValue;
        for (int i = 1; i < tmpNewSize; i++) {
            tmpValues[i] = tmpAggrVal += this.value(i - 1);
        }

        return DataSeries.wrap(tmpValues);
    }

    public PrimitiveSeries subtract(final double aValue) {
        return new UnaryFunctionSeries(this, new PreconfiguredSecond<Double>(PrimitiveFunction.SUBTRACT, aValue));
    }

    public PrimitiveSeries subtract(final PrimitiveSeries aSeries) {
        return new BinaryFunctionSeries(this, PrimitiveFunction.SUBTRACT, aSeries);
    }

    public final DataSeries toDataSeries() {
        return DataSeries.wrap(this.values());
    }

    public abstract double value(final int index);

    public final double[] values() {

        final int tmpSize = this.size();
        final double[] retVal = new double[tmpSize];

        for (int i = 0; i < tmpSize; i++) {
            retVal[i] = this.value(i);
        }

        return retVal;
    }

}
