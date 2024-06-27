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

import static org.ojalgo.constant.PrimitiveMath.*;

import java.util.Arrays;

import org.ojalgo.access.Access1D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.scalar.ComplexNumber;

/**
 * A one- and/or arbitrary-dimensional array of {@linkplain org.ojalgo.scalar.ComplexNumber}.
 * 
 * @see PrimitiveArray
 * @author apete
 */
public class ComplexArray extends BasicArray<ComplexNumber> {

    protected static void exchange(final ComplexNumber[] aData, final int aFirstA, final int aFirstB, final int aStep, final int aCount) {

        int tmpIndexA = aFirstA;
        int tmpIndexB = aFirstB;

        ComplexNumber tmpVal;

        for (int i = 0; i < aCount; i++) {

            tmpVal = aData[tmpIndexA];
            aData[tmpIndexA] = aData[tmpIndexB];
            aData[tmpIndexB] = tmpVal;

            tmpIndexA += aStep;
            tmpIndexB += aStep;
        }
    }

    protected static void fill(final ComplexNumber[] aData, final int aFirst, final int aLimit, final int aStep, final ComplexNumber aNmbr) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aNmbr;
        }
    }

    protected static void invoke(final ComplexNumber[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<ComplexNumber> aLeftArg, final BinaryFunction<ComplexNumber> aFunc, final Access1D<ComplexNumber> aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(aLeftArg.get(i), aRightArg.get(i));
        }
    }

    protected static void invoke(final ComplexNumber[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<ComplexNumber> aLeftArg, final BinaryFunction<ComplexNumber> aFunc, final ComplexNumber aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(aLeftArg.get(i), aRightArg);
        }
    }

    protected static void invoke(final ComplexNumber[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<ComplexNumber> anArg, final ParameterFunction<ComplexNumber> aFunc, final int aParam) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(anArg.get(i), aParam);
        }
    }

    protected static void invoke(final ComplexNumber[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<ComplexNumber> anArg, final UnaryFunction<ComplexNumber> aFunc) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(anArg.get(i));
        }
    }

    protected static void invoke(final ComplexNumber[] aData, final int aFirst, final int aLimit, final int aStep, final AggregatorFunction<ComplexNumber> aVisitor) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aVisitor.invoke(aData[i]);
        }
    }

    protected static void invoke(final ComplexNumber[] aData, final int aFirst, final int aLimit, final int aStep, final ComplexNumber aLeftArg, final BinaryFunction<ComplexNumber> aFunc, final Access1D<ComplexNumber> aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(aLeftArg, aRightArg.get(i));
        }
    }

    private final ComplexNumber[] myData;

    protected ComplexArray(final ComplexNumber[] anArray) {

        super(anArray.length);

        myData = anArray;
    }

    protected ComplexArray(final int aLength) {

        super(aLength);

        myData = new ComplexNumber[aLength];

        this.fill(0, aLength, 1, ComplexNumber.ZERO);
    }

    public final double doubleValue(final int anInd) {
        return myData[anInd].doubleValue();
    }

    @Override
    public boolean equals(final Object anObj) {
        if (anObj instanceof ComplexArray) {
            return Arrays.equals(myData, ((ComplexArray) anObj).data());
        } else {
            return super.equals(anObj);
        }
    }

    public final ComplexNumber get(final int anInd) {
        return myData[anInd];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(myData);
    }

    protected final ComplexNumber[] copyOfData() {
        return ArrayUtils.copyOf(myData);
    }

    protected final ComplexNumber[] data() {
        return myData;
    }

    @Override
    protected final void exchange(final int aFirstA, final int aFirstB, final int aStep, final int aCount) {
        ComplexArray.exchange(myData, aFirstA, aFirstB, aStep, aCount);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final Access1D<ComplexNumber> aLeftArg, final BinaryFunction<ComplexNumber> aFunc, final Access1D<ComplexNumber> aRightArg) {
        ComplexArray.invoke(myData, aFirst, aLimit, 1, aLeftArg, aFunc, aRightArg);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final Access1D<ComplexNumber> aLeftArg, final BinaryFunction<ComplexNumber> aFunc, final ComplexNumber aRightArg) {
        ComplexArray.invoke(myData, aFirst, aLimit, 1, aLeftArg, aFunc, aRightArg);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final ComplexNumber aLeftArg, final BinaryFunction<ComplexNumber> aFunc, final Access1D<ComplexNumber> aRightArg) {
        ComplexArray.invoke(myData, aFirst, aLimit, 1, aLeftArg, aFunc, aRightArg);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final int aStep, final ComplexNumber aNmbr) {
        ComplexArray.fill(myData, aFirst, aLimit, aStep, aNmbr);
    }

    @Override
    protected final int getIndexOfLargest(final int aFirst, final int aLimit, final int aStep) {

        int retVal = aFirst;
        double tmpLargest = ZERO;
        double tmpValue;

        for (int i = aFirst; i < aLimit; i += aStep) {
            tmpValue = myData[i].getModulus();
            if (tmpValue > tmpLargest) {
                tmpLargest = tmpValue;
                retVal = i;
            }
        }

        return retVal;
    }

    @Override
    protected final boolean isAbsolute(final int anInd) {
        return myData[anInd].isAbsolute();
    }

    @Override
    protected final boolean isReal(final int anInd) {
        return myData[anInd].isReal();
    }

    @Override
    protected boolean isPositive(final int anInd) {
        return myData[anInd].isPositive();
    }

    @Override
    protected final boolean isZero(final int anInd) {
        return myData[anInd].isZero();
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
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final Access1D<ComplexNumber> aLeftArg, final BinaryFunction<ComplexNumber> aFunc) {
        ComplexArray.invoke(myData, aFirst, aLimit, aStep, aLeftArg, aFunc, this);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final BinaryFunction<ComplexNumber> aFunc, final Access1D<ComplexNumber> aRightArg) {
        ComplexArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc, aRightArg);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final BinaryFunction<ComplexNumber> aFunc, final ComplexNumber aRightArg) {
        ComplexArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc, aRightArg);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final ComplexNumber aLeftArg, final BinaryFunction<ComplexNumber> aFunc) {
        ComplexArray.invoke(myData, aFirst, aLimit, aStep, aLeftArg, aFunc, this);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final ParameterFunction<ComplexNumber> aFunc, final int aParam) {
        ComplexArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc, aParam);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final UnaryFunction<ComplexNumber> aFunc) {
        ComplexArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc);
    }

    /**
     * @see org.ojalgo.array.BasicArray#searchAscending(java.lang.Number)
     */
    @Override
    protected final int searchAscending(final ComplexNumber aNmbr) {
        return Arrays.binarySearch(myData, aNmbr);
    }

    @Override
    protected final void set(final int anInd, final ComplexNumber aNmbr) {
        myData[anInd] = aNmbr;
    }

    @Override
    protected final void set(final int anInd, final double aNmbr) {
        myData[anInd] = new ComplexNumber(aNmbr);
    }

    @Override
    protected final void sortAscending() {
        Arrays.sort(myData);
    }

    @Override
    protected final ComplexNumber toScalar(final int anInd) {
        return myData[anInd];
    }

    @Override
    protected final void visit(final int aFirst, final int aLimit, final int aStep, final AggregatorFunction<ComplexNumber> aVisitor) {
        ComplexArray.invoke(myData, aFirst, aLimit, aStep, aVisitor);
    }

}
