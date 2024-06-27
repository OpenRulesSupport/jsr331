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

import org.ojalgo.scalar.ComplexNumber;

public final class ApplyTransformations extends MatrixOperation {

    /**
     * MacPro 20110922: 64
     * iMac 20110922: 128
     */
    public static int THRESHOLD = 128;

    public static void invoke(final BigDecimal[] aData, final int aRowDim, final int aFirstCol, final int aColLimit, final BigDecimal[] multipliers,
            final int iterationPoint, final boolean hermitian) {
        if (hermitian) {
            for (int j = aFirstCol; j < aColLimit; j++) {
                SubtractScaledVector.invoke(aData, j * aRowDim, multipliers, 0, multipliers[j], j, aRowDim);
            }
        } else {
            for (int j = aFirstCol; j < aColLimit; j++) {
                SubtractScaledVector.invoke(aData, j * aRowDim, multipliers, 0, aData[iterationPoint + (j * aRowDim)], iterationPoint + 1, aRowDim);
            }
        }
    }

    public static void invoke(final ComplexNumber[] aData, final int aRowDim, final int aFirstCol, final int aColLimit, final ComplexNumber[] multipliers,
            final int iterationPoint, final boolean hermitian) {
        if (hermitian) {
            for (int j = aFirstCol; j < aColLimit; j++) {
                SubtractScaledVector.invoke(aData, j * aRowDim, multipliers, 0, multipliers[j].conjugate(), j, aRowDim);
            }
        } else {
            for (int j = aFirstCol; j < aColLimit; j++) {
                SubtractScaledVector.invoke(aData, j * aRowDim, multipliers, 0, aData[iterationPoint + (j * aRowDim)], iterationPoint + 1, aRowDim);
            }
        }
    }

    public static void invoke(final double[] aData, final int aRowDim, final int aFirstCol, final int aColLimit, final double[] multipliers,
            final int iterationPoint, final boolean hermitian) {
        if (hermitian) {
            for (int j = aFirstCol; j < aColLimit; j++) {
                SubtractScaledVector.invoke(aData, j * aRowDim, multipliers, 0, multipliers[j], j, aRowDim);
            }
        } else {
            for (int j = aFirstCol; j < aColLimit; j++) {
                SubtractScaledVector.invoke(aData, j * aRowDim, multipliers, 0, aData[iterationPoint + (j * aRowDim)], iterationPoint + 1, aRowDim);
            }
        }
    }

    private ApplyTransformations() {
        super();
    }

    @Override
    public int getThreshold() {
        return THRESHOLD;
    }

}
