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
package org.ojalgo.array;

import static org.ojalgo.constant.BigMath.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.ojalgo.access.Access1D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.scalar.BigScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;

/**
 * A one- and/or arbitrary-dimensional array of {@linkplain java.math.BigDecimal}.
 * 
 * @see PrimitiveArray
 * @author apete
 */
public class BigArray extends BasicArray<BigDecimal> {

    protected static void exchange(final BigDecimal[] aData, final int aFirstA, final int aFirstB, final int aStep, final int aCount) {

        int tmpIndexA = aFirstA;
        int tmpIndexB = aFirstB;

        BigDecimal tmpVal;

        for (int i = 0; i < aCount; i++) {

            tmpVal = aData[tmpIndexA];
            aData[tmpIndexA] = aData[tmpIndexB];
            aData[tmpIndexB] = tmpVal;

            tmpIndexA += aStep;
            tmpIndexB += aStep;
        }
    }

    protected static void fill(final BigDecimal[] aData, final int aFirst, final int aLimit, final int aStep, final BigDecimal aNmbr) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aNmbr;
        }
    }

    protected static void invoke(final BigDecimal[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<BigDecimal> aLeftArg, final BinaryFunction<BigDecimal> aFunc, final Access1D<BigDecimal> aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(aLeftArg.get(i), aRightArg.get(i));
        }
    }

    protected static void invoke(final BigDecimal[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<BigDecimal> aLeftArg, final BinaryFunction<BigDecimal> aFunc, final BigDecimal aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(aLeftArg.get(i), aRightArg);
        }
    }

    protected static void invoke(final BigDecimal[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<BigDecimal> anArg, final ParameterFunction<BigDecimal> aFunc, final int aParam) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(anArg.get(i), aParam);
        }
    }

    protected static void invoke(final BigDecimal[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<BigDecimal> anArg, final UnaryFunction<BigDecimal> aFunc) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(anArg.get(i));
        }
    }

    protected static void invoke(final BigDecimal[] aData, final int aFirst, final int aLimit, final int aStep, final AggregatorFunction<BigDecimal> aVisitor) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aVisitor.invoke(aData[i]);
        }
    }

    protected static void invoke(final BigDecimal[] aData, final int aFirst, final int aLimit, final int aStep, final BigDecimal aLeftArg, final BinaryFunction<BigDecimal> aFunc, final Access1D<BigDecimal> aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(aLeftArg, aRightArg.get(i));
        }
    }

    private final BigDecimal[] myData;

    protected BigArray(final BigDecimal[] anArray) {

        super(anArray.length);

        myData = anArray;
    }

    protected BigArray(final int aLength) {

        super(aLength);

        myData = new BigDecimal[aLength];

        this.fill(0, aLength, 1, ZERO);
    }

    public final double doubleValue(final int anInd) {
        return myData[anInd].doubleValue();
    }

    @Override
    public boolean equals(final Object anObj) {
        if (anObj instanceof BigArray) {
            return Arrays.equals(myData, ((BigArray) anObj).data());
        } else {
            return super.equals(anObj);
        }
    }

    public final BigDecimal get(final int anInd) {
        return myData[anInd];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(myData);
    }

    protected final BigDecimal[] copyOfData() {
        return ArrayUtils.copyOf(myData);
    }

    protected final BigDecimal[] data() {
        return myData;
    }

    @Override
    protected final void exchange(final int aFirstA, final int aFirstB, final int aStep, final int aCount) {
        BigArray.exchange(myData, aFirstA, aFirstB, aStep, aCount);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final Access1D<BigDecimal> aLeftArg, final BinaryFunction<BigDecimal> aFunc, final Access1D<BigDecimal> aRightArg) {
        BigArray.invoke(myData, aFirst, aLimit, 1, aLeftArg, aFunc, aRightArg);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final Access1D<BigDecimal> aLeftArg, final BinaryFunction<BigDecimal> aFunc, final BigDecimal aRightArg) {
        BigArray.invoke(myData, aFirst, aLimit, 1, aLeftArg, aFunc, aRightArg);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final BigDecimal aLeftArg, final BinaryFunction<BigDecimal> aFunc, final Access1D<BigDecimal> aRightArg) {
        BigArray.invoke(myData, aFirst, aLimit, 1, aLeftArg, aFunc, aRightArg);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final int aStep, final BigDecimal aNmbr) {
        BigArray.fill(myData, aFirst, aLimit, aStep, aNmbr);
    }

    @Override
    protected final int getIndexOfLargest(final int aFirst, final int aLimit, final int aStep) {

        int retVal = aFirst;
        BigDecimal tmpLargest = ZERO;
        BigDecimal tmpValue;

        for (int i = aFirst; i < aLimit; i += aStep) {
            tmpValue = myData[i].abs();
            if (tmpValue.compareTo(tmpLargest) == 1) {
                tmpLargest = tmpValue;
                retVal = i;
            }
        }

        return retVal;
    }

    @Override
    protected final boolean isAbsolute(final int anInd) {
        return myData[anInd].compareTo(BigMath.ZERO) != -1;
    }

    @Override
    protected final boolean isReal(final int anInd) {
        return true;
    }

    @Override
    protected boolean isPositive(final int anInd) {
        return myData[anInd].compareTo(BigMath.ZERO) == 1;
    }

    @Override
    protected final boolean isZero(final int anInd) {
        return TypeUtils.isZero(myData[anInd].doubleValue());
    }

    @Override
    protected final boolean isZeros(final int aFirst, final int aLimit, final int aStep) {

        boolean retVal = true;

        for (int i = aFirst; retVal && (i < aLimit); i += aStep) {
            retVal &= this.isZero(i);
        }

        return retVal;
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final Access1D<BigDecimal> aLeftArg, final BinaryFunction<BigDecimal> aFunc) {
        BigArray.invoke(myData, aFirst, aLimit, aStep, aLeftArg, aFunc, this);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final BigDecimal aLeftArg, final BinaryFunction<BigDecimal> aFunc) {
        BigArray.invoke(myData, aFirst, aLimit, aStep, aLeftArg, aFunc, this);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final BinaryFunction<BigDecimal> aFunc, final Access1D<BigDecimal> aRightArg) {
        BigArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc, aRightArg);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final BinaryFunction<BigDecimal> aFunc, final BigDecimal aRightArg) {
        BigArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc, aRightArg);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final ParameterFunction<BigDecimal> aFunc, final int aParam) {
        BigArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc, aParam);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final UnaryFunction<BigDecimal> aFunc) {
        BigArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc);
    }

    /**
     * @see org.ojalgo.array.BasicArray#searchAscending(java.lang.Number)
     */
    @Override
    protected final int searchAscending(final BigDecimal aNmbr) {
        return Arrays.binarySearch(myData, aNmbr);
    }

    @Override
    protected final void set(final int anInd, final BigDecimal aNmbr) {
        myData[anInd] = aNmbr;
    }

    @Override
    protected final void set(final int anInd, final double aNmbr) {
        myData[anInd] = new BigDecimal(aNmbr);
    }

    @Override
    protected final void sortAscending() {
        Arrays.sort(myData);
    }

    @Override
    protected final Scalar<BigDecimal> toScalar(final int anInd) {
        return new BigScalar(myData[anInd]);
    }

    @Override
    protected final void visit(final int aFirst, final int aLimit, final int aStep, final AggregatorFunction<BigDecimal> aVisitor) {
        BigArray.invoke(myData, aFirst, aLimit, aStep, aVisitor);
    }

}
