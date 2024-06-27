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
import org.ojalgo.function.PreconfiguredFirst;
import org.ojalgo.function.PreconfiguredParameter;
import org.ojalgo.function.PreconfiguredSecond;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;

/**
 * <p>
 * A one- and/or arbitrary-dimensional array of double.
 * </p><p>
 * You cannot instantiate a PrimitiveArray directly. You have to either
 * subclass it and implement instantiation code in that subclass, or use
 * one of the static factory methods in {@linkplain Array1D}, {@linkplain Array2D}
 * or {@linkplain ArrayAnyD}.
 * </p>
 * 
 * @author apete
 */
public class PrimitiveArray extends BasicArray<Double> {

    private static void add(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double aLeftArg, final double[] aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg + aRightArg[i];
        }
    }

    private static void add(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final double aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg[i] + aRightArg;
        }
    }

    private static void add(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final double[] aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg[i] + aRightArg[i];
        }
    }

    private static void divide(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double aLeftArg, final double[] aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg / aRightArg[i];
        }
    }

    private static void divide(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final double aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg[i] / aRightArg;
        }
    }

    private static void divide(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final double[] aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg[i] / aRightArg[i];
        }
    }

    private static void multiply(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double aLeftArg, final double[] aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg * aRightArg[i];
        }
    }

    private static void multiply(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final double aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg[i] * aRightArg;
        }
    }

    private static void multiply(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final double[] aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg[i] * aRightArg[i];
        }
    }

    private static void negate(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] anArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = -anArg[i];
        }
    }

    private static void subtract(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double aLeftArg, final double[] aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg - aRightArg[i];
        }
    }

    private static void subtract(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final double aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg[i] - aRightArg;
        }
    }

    private static void subtract(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final double[] aRightArg) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aLeftArg[i] - aRightArg[i];
        }
    }

    protected static void exchange(final double[] aData, final int aFirstA, final int aFirstB, final int aStep, final int aCount) {

        int tmpIndexA = aFirstA;
        int tmpIndexB = aFirstB;

        double tmpVal;

        for (int i = 0; i < aCount; i++) {

            tmpVal = aData[tmpIndexA];
            aData[tmpIndexA] = aData[tmpIndexB];
            aData[tmpIndexB] = tmpVal;

            tmpIndexA += aStep;
            tmpIndexB += aStep;
        }
    }

    protected static void fill(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double aVal) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aVal;
        }
    }

    protected static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<Double> aLeftArg, final BinaryFunction<Double> aFunc, final Access1D<Double> aRightArg) {
        if ((aLeftArg instanceof PrimitiveArray) && (aRightArg instanceof PrimitiveArray)) {
            PrimitiveArray.invoke(aData, aFirst, aLimit, aStep, ((PrimitiveArray) aLeftArg).data(), aFunc, ((PrimitiveArray) aRightArg).data());
        } else {
            for (int i = aFirst; i < aLimit; i += aStep) {
                aData[i] = aFunc.invoke(aLeftArg.doubleValue(i), aRightArg.doubleValue(i));
            }
        }
    }

    protected static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<Double> aLeftArg, final BinaryFunction<Double> aFunc, final double aRightArg) {
        if (aLeftArg instanceof PrimitiveArray) {
            PrimitiveArray.invoke(aData, aFirst, aLimit, aStep, ((PrimitiveArray) aLeftArg).data(), aFunc, aRightArg);
        } else {
            for (int i = aFirst; i < aLimit; i += aStep) {
                aData[i] = aFunc.invoke(aLeftArg.doubleValue(i), aRightArg);
            }
        }
    }

    protected static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<Double> anArg, final ParameterFunction<Double> aFunc, final int aParam) {
        if (anArg instanceof PrimitiveArray) {
            PrimitiveArray.invoke(aData, aFirst, aLimit, aStep, ((PrimitiveArray) anArg).data(), aFunc, aParam);
        } else {
            for (int i = aFirst; i < aLimit; i += aStep) {
                aData[i] = aFunc.invoke(anArg.doubleValue(i), aParam);
            }
        }
    }

    protected static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final Access1D<Double> anArg, final UnaryFunction<Double> aFunc) {
        if (anArg instanceof PrimitiveArray) {
            PrimitiveArray.invoke(aData, aFirst, aLimit, aStep, ((PrimitiveArray) anArg).data(), aFunc);
        } else {
            for (int i = aFirst; i < aLimit; i += aStep) {
                aData[i] = aFunc.invoke(anArg.doubleValue(i));
            }
        }
    }

    protected static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final AggregatorFunction<Double> aVisitor) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aVisitor.invoke(aData[i]);
        }
    }

    protected static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double aLeftArg, final BinaryFunction<Double> aFunc, final Access1D<Double> aRightArg) {
        if (aRightArg instanceof PrimitiveArray) {
            PrimitiveArray.invoke(aData, aFirst, aLimit, aStep, aLeftArg, aFunc, ((PrimitiveArray) aRightArg).data());
        } else {
            for (int i = aFirst; i < aLimit; i += aStep) {
                aData[i] = aFunc.invoke(aLeftArg, aRightArg.doubleValue(i));
            }
        }
    }

    static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double aLeftArg, final BinaryFunction<Double> aFunc, final double[] aRightArg) {
        if (aFunc == PrimitiveFunction.ADD) {
            PrimitiveArray.add(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else if (aFunc == PrimitiveFunction.DIVIDE) {
            PrimitiveArray.divide(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else if (aFunc == PrimitiveFunction.MULTIPLY) {
            PrimitiveArray.multiply(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else if (aFunc == PrimitiveFunction.SUBTRACT) {
            PrimitiveArray.subtract(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else {
            for (int i = aFirst; i < aLimit; i += aStep) {
                aData[i] = aFunc.invoke(aLeftArg, aRightArg[i]);
            }
        }
    }

    static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final BinaryFunction<Double> aFunc, final double aRightArg) {
        if (aFunc == PrimitiveFunction.ADD) {
            PrimitiveArray.add(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else if (aFunc == PrimitiveFunction.DIVIDE) {
            PrimitiveArray.divide(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else if (aFunc == PrimitiveFunction.MULTIPLY) {
            PrimitiveArray.multiply(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else if (aFunc == PrimitiveFunction.SUBTRACT) {
            PrimitiveArray.subtract(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else {
            for (int i = aFirst; i < aLimit; i += aStep) {
                aData[i] = aFunc.invoke(aLeftArg[i], aRightArg);
            }
        }
    }

    static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] aLeftArg, final BinaryFunction<Double> aFunc, final double[] aRightArg) {
        if (aFunc == PrimitiveFunction.ADD) {
            PrimitiveArray.add(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else if (aFunc == PrimitiveFunction.DIVIDE) {
            PrimitiveArray.divide(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else if (aFunc == PrimitiveFunction.MULTIPLY) {
            PrimitiveArray.multiply(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else if (aFunc == PrimitiveFunction.SUBTRACT) {
            PrimitiveArray.subtract(aData, aFirst, aLimit, aStep, aLeftArg, aRightArg);
        } else {
            for (int i = aFirst; i < aLimit; i += aStep) {
                aData[i] = aFunc.invoke(aLeftArg[i], aRightArg[i]);
            }
        }
    }

    static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] anArg, final ParameterFunction<Double> aFunc, final int aParam) {
        for (int i = aFirst; i < aLimit; i += aStep) {
            aData[i] = aFunc.invoke(anArg[i], aParam);
        }
    }

    static void invoke(final double[] aData, final int aFirst, final int aLimit, final int aStep, final double[] anArg, final UnaryFunction<Double> aFunc) {
        if (aFunc == PrimitiveFunction.NEGATE) {
            PrimitiveArray.negate(aData, aFirst, aLimit, aStep, anArg);
        } else if (aFunc instanceof PreconfiguredFirst<?>) {
            final PreconfiguredFirst<Double> tmpFunc = (PreconfiguredFirst<Double>) aFunc;
            PrimitiveArray.invoke(aData, aFirst, aLimit, aStep, tmpFunc.doubleValue(), tmpFunc.getFunction(), aData);
        } else if (aFunc instanceof PreconfiguredSecond<?>) {
            final PreconfiguredSecond<Double> tmpFunc = (PreconfiguredSecond<Double>) aFunc;
            PrimitiveArray.invoke(aData, aFirst, aLimit, aStep, aData, tmpFunc.getFunction(), tmpFunc.doubleValue());
        } else if (aFunc instanceof PreconfiguredParameter<?>) {
            final PreconfiguredParameter<Double> tmpFunc = (PreconfiguredParameter<Double>) aFunc;
            PrimitiveArray.invoke(aData, aFirst, aLimit, aStep, aData, tmpFunc.getFunction(), tmpFunc.getParameter());
        } else {
            for (int i = aFirst; i < aLimit; i += aStep) {
                aData[i] = aFunc.invoke(anArg[i]);
            }
        }
    }

    private final double[] myData;

    /**
     * Array not copied! No checking!
     */
    protected PrimitiveArray(final double[] anArray) {

        super(anArray.length);

        myData = anArray;
    }

    protected PrimitiveArray(final int aLength) {

        super(aLength);

        myData = new double[aLength];
    }

    public final double doubleValue(final int anInd) {
        return myData[anInd];
    }

    @Override
    public boolean equals(final Object anObj) {
        if (anObj instanceof PrimitiveArray) {
            return Arrays.equals(myData, ((PrimitiveArray) anObj).data());
        } else {
            return super.equals(anObj);
        }
    }

    public final Double get(final int anInd) {
        return myData[anInd];
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(myData);
    }

    protected final double[] copyOfData() {
        return ArrayUtils.copyOf(myData);
    }

    protected final double[] data() {
        return myData;
    }

    @Override
    protected final void exchange(final int aFirstA, final int aFirstB, final int aStep, final int aCount) {
        PrimitiveArray.exchange(myData, aFirstA, aFirstB, aStep, aCount);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final Access1D<Double> aLeftArg, final BinaryFunction<Double> aFunc, final Access1D<Double> aRightArg) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, 1, aLeftArg, aFunc, aRightArg);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final Access1D<Double> aLeftArg, final BinaryFunction<Double> aFunc, final Double aRightArg) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, 1, aLeftArg, aFunc, aRightArg);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final Double aLeftArg, final BinaryFunction<Double> aFunc, final Access1D<Double> aRightArg) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, 1, aLeftArg, aFunc, aRightArg);
    }

    @Override
    protected final void fill(final int aFirst, final int aLimit, final int aStep, final Double aNmbr) {
        PrimitiveArray.fill(myData, aFirst, aLimit, aStep, aNmbr);
    }

    @Override
    protected final int getIndexOfLargest(final int aFirst, final int aLimit, final int aStep) {

        int retVal = aFirst;
        double tmpLargest = ZERO;
        double tmpValue;

        for (int i = aFirst; i < aLimit; i += aStep) {
            tmpValue = Math.abs(myData[i]);
            if (tmpValue > tmpLargest) {
                tmpLargest = tmpValue;
                retVal = i;
            }
        }

        return retVal;
    }

    @Override
    protected final boolean isAbsolute(final int anInd) {
        return myData[anInd] >= ZERO;
    }

    @Override
    protected final boolean isReal(final int anInd) {
        return true;
    }

    @Override
    protected boolean isPositive(final int anInd) {
        return myData[anInd] > ZERO;
    }

    @Override
    protected final boolean isZero(final int anInd) {
        return TypeUtils.isZero(myData[anInd]);
    }

    @Override
    protected final boolean isZeros(final int aFirst, final int aLimit, final int aStep) {

        boolean retVal = true;

        for (int i = aFirst; retVal && (i < aLimit); i += aStep) {
            retVal &= TypeUtils.isZero(myData[i]);
        }

        return retVal;
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final Access1D<Double> aLeftArg, final BinaryFunction<Double> aFunc) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, aStep, aLeftArg, aFunc, this);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final BinaryFunction<Double> aFunc, final Access1D<Double> aRightArg) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc, aRightArg);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final BinaryFunction<Double> aFunc, final Double aRightArg) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, aStep, myData, aFunc, aRightArg);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final Double aLeftArg, final BinaryFunction<Double> aFunc) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, aStep, aLeftArg, aFunc, myData);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final ParameterFunction<Double> aFunc, final int aParam) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, aStep, myData, aFunc, aParam);
    }

    @Override
    protected final void modify(final int aFirst, final int aLimit, final int aStep, final UnaryFunction<Double> aFunc) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, aStep, this, aFunc);
    }

    @Override
    protected final int searchAscending(final Double aNmbr) {
        return Arrays.binarySearch(myData, aNmbr.doubleValue());
    }

    @Override
    protected final void set(final int anInd, final double aNmbr) {
        myData[anInd] = aNmbr;
    }

    @Override
    protected final void set(final int anInd, final Double aNmbr) {
        myData[anInd] = aNmbr;
    }

    @Override
    protected final void sortAscending() {
        Arrays.sort(myData);
    }

    @Override
    protected final Scalar<Double> toScalar(final int anInd) {
        return new PrimitiveScalar(myData[anInd]);
    }

    @Override
    protected final void visit(final int aFirst, final int aLimit, final int aStep, final AggregatorFunction<Double> aVisitor) {
        PrimitiveArray.invoke(myData, aFirst, aLimit, aStep, aVisitor);
    }
}
