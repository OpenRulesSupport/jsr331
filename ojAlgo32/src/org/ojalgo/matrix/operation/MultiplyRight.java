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

import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;

public final class MultiplyRight extends MatrixOperation {

    public static int THRESHOLD = 16;

    public static void invoke(final BigDecimal[] aData, final int aFirstCol, final int aColLimit, final BigDecimal[] aLeftArray, final MatrixStore<BigDecimal> aRightStore) {

        final int tmpCalcSize = aRightStore.getRowDim();
        final int tmpRowDim = aLeftArray.length / tmpCalcSize;

        for (int j = aFirstCol; j < aColLimit; j++) {
            for (int c = 0; c < tmpCalcSize; c++) {
                CAXPY.invoke(aData, j * tmpRowDim, aLeftArray, c * tmpRowDim, aRightStore.get(c, j), 0, tmpRowDim);
            }
        }
    }

    public static void invoke(final ComplexNumber[] aData, final int aFirstCol, final int aColLimit, final ComplexNumber[] aLeftArray, final MatrixStore<ComplexNumber> aRightStore) {

        final int tmpCalcSize = aRightStore.getRowDim();
        final int tmpRowDim = aLeftArray.length / tmpCalcSize;

        for (int j = aFirstCol; j < aColLimit; j++) {
            for (int c = 0; c < tmpCalcSize; c++) {
                CAXPY.invoke(aData, j * tmpRowDim, aLeftArray, c * tmpRowDim, aRightStore.get(c, j), 0, tmpRowDim);
            }
        }
    }

    public static void invoke(final double[] aData, final int aFirstCol, final int aColLimit, final double[] aLeftArray, final MatrixStore<Double> aRightStore) {

        final int tmpCalcSize = aRightStore.getRowDim();
        final int tmpRowDim = aLeftArray.length / tmpCalcSize;

        for (int j = aFirstCol; j < aColLimit; j++) {
            for (int c = 0; c < tmpCalcSize; c++) {
                CAXPY.invoke(aData, j * tmpRowDim, aLeftArray, c * tmpRowDim, aRightStore.doubleValue(c, j), 0, tmpRowDim);
            }
        }
    }

    private MultiplyRight() {
        super();
    }

    public int getThreshold() {
        return THRESHOLD;
    }

}
