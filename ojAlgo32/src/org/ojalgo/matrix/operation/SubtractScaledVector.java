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
package org.ojalgo.matrix.operation;

import java.math.BigDecimal;

import org.ojalgo.function.implementation.BigFunction;
import org.ojalgo.scalar.ComplexNumber;

/**
 * y -= ax
 * 
 * @param aData y-data
 * @param aBaseDataIndex y-column base index
 * @param aMultipliers x-data
 * @param aBaseMultiplierIndex x-column base index
 * @param aScalar a
 * @param aFirst First index
 * @param aLimit Index limit 
 */
public final class SubtractScaledVector extends MatrixOperation {

    public static int THRESHOLD = 128;

    public static void invoke(final BigDecimal[] aData, final int aBaseDataIndex, final BigDecimal[] aVector, final int aBaseVectorIndex,
            final BigDecimal aScalar, final int aFirst, final int aLimit) {
        for (int i = aFirst; i < aLimit; i++) {
            aData[aBaseDataIndex + i] = BigFunction.SUBTRACT.invoke(aData[aBaseDataIndex + i],
                    BigFunction.MULTIPLY.invoke(aScalar, aVector[aBaseVectorIndex + i])); // y -= ax
        }
    }

    public static void invoke(final ComplexNumber[] aData, final int aBaseDataIndex, final ComplexNumber[] aVector, final int aBaseVectorIndex,
            final ComplexNumber aScalar, final int aFirst, final int aLimit) {
        for (int i = aFirst; i < aLimit; i++) {
            aData[aBaseDataIndex + i] = aData[aBaseDataIndex + i].subtract(aScalar.multiply(aVector[aBaseVectorIndex + i])); // y -= ax
        }
    }

    public static void invoke(final double[] aData, final int aBaseDataIndex, final double[] aVector, final int aBaseVectorIndex, final double aScalar,
            final int aFirst, final int aLimit) {
        for (int i = aFirst; i < aLimit; i++) {
            aData[aBaseDataIndex + i] -= aScalar * aVector[aBaseVectorIndex + i]; // y -= ax
        }
    }

    private SubtractScaledVector() {
        super();
    }

    @Override
    public int getThreshold() {
        return THRESHOLD;
    }

}
