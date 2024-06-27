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

import org.ojalgo.access.Access2D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.implementation.BigFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.scalar.ComplexNumber;

public final class MultiplyBoth extends MatrixOperation {

    public static int THRESHOLD = 16;

    public static void invoke(final BigDecimal[] aData, final int aFirstRow, final int aRowLimit, final Access2D<BigDecimal> aLeftStore, final Access2D<BigDecimal> aRightStore) {

        final int tmpRowDim = aLeftStore.getRowDim();
        final int tmpCalcSize = aRightStore.getRowDim();
        final int tmpColDim = aRightStore.getColDim();

        final BigDecimal[] tmpLeftRow = new BigDecimal[tmpCalcSize];
        final BigDecimal[] tmpProdRow = new BigDecimal[tmpColDim];
        BigDecimal tmpVal;

        final boolean tmpLL = MatrixUtils.isLowerLeftShaded(aLeftStore);
        final boolean tmpLU = MatrixUtils.isUpperRightShaded(aLeftStore);
        final boolean tmpRL = MatrixUtils.isLowerLeftShaded(aRightStore);
        final boolean tmpRU = MatrixUtils.isUpperRightShaded(aRightStore);
        final boolean tmpPrune = tmpLL || tmpLU || tmpRL || tmpRU;
        int tmpFirst = 0;
        int tmpLimit = tmpCalcSize;

        for (int i = aFirstRow; i < aRowLimit; i++) {

            for (int c = 0; c < tmpCalcSize; c++) {
                tmpLeftRow[c] = aLeftStore.get(i, c);
            }

            for (int j = 0; j < tmpColDim; j++) {
                if (tmpPrune) {
                    tmpFirst = MatrixUtils.max(tmpLL ? i - 1 : 0, tmpRU ? j - 1 : 0, 0);
                    tmpLimit = MatrixUtils.min(tmpLU ? i + 2 : tmpCalcSize, tmpRL ? j + 2 : tmpCalcSize, tmpCalcSize);
                }
                tmpVal = BigMath.ZERO;
                for (int c = tmpFirst; c < tmpLimit; c++) {
                    tmpVal = BigFunction.ADD.invoke(tmpVal, BigFunction.MULTIPLY.invoke(tmpLeftRow[c], aRightStore.get(c, j)));
                }
                tmpProdRow[j] = tmpVal;
            }

            for (int j = 0; j < tmpColDim; j++) {
                aData[i + (j * tmpRowDim)] = tmpProdRow[j];
            }
        }
    }

    public static void invoke(final ComplexNumber[] aData, final int aFirstRow, final int aRowLimit, final Access2D<ComplexNumber> aLeftStore, final Access2D<ComplexNumber> aRightStore) {

        final int tmpRowDim = aLeftStore.getRowDim();
        final int tmpCalcSize = aRightStore.getRowDim();
        final int tmpColDim = aRightStore.getColDim();

        final ComplexNumber[] tmpLeftRow = new ComplexNumber[tmpCalcSize];
        final ComplexNumber[] tmpProdRow = new ComplexNumber[tmpColDim];
        ComplexNumber tmpVal;

        final boolean tmpLL = MatrixUtils.isLowerLeftShaded(aLeftStore);
        final boolean tmpLU = MatrixUtils.isUpperRightShaded(aLeftStore);
        final boolean tmpRL = MatrixUtils.isLowerLeftShaded(aRightStore);
        final boolean tmpRU = MatrixUtils.isUpperRightShaded(aRightStore);
        final boolean tmpPrune = tmpLL || tmpLU || tmpRL || tmpRU;
        int tmpFirst = 0;
        int tmpLimit = tmpCalcSize;

        for (int i = aFirstRow; i < aRowLimit; i++) {

            for (int c = 0; c < tmpCalcSize; c++) {
                tmpLeftRow[c] = aLeftStore.get(i, c);
            }

            for (int j = 0; j < tmpColDim; j++) {
                if (tmpPrune) {
                    tmpFirst = MatrixUtils.max(tmpLL ? i - 1 : 0, tmpRU ? j - 1 : 0, 0);
                    tmpLimit = MatrixUtils.min(tmpLU ? i + 2 : tmpCalcSize, tmpRL ? j + 2 : tmpCalcSize, tmpCalcSize);
                }
                tmpVal = ComplexNumber.ZERO;
                for (int c = tmpFirst; c < tmpLimit; c++) {
                    tmpVal = tmpVal.add(tmpLeftRow[c].multiply(aRightStore.get(c, j)));
                }
                tmpProdRow[j] = tmpVal;
            }

            for (int j = 0; j < tmpColDim; j++) {
                aData[i + (j * tmpRowDim)] = tmpProdRow[j];
            }
        }
    }

    public static void invoke(final double[] aData, final int aFirstRow, final int aRowLimit, final Access2D<?> aLeftStore, final Access2D<?> aRightStore) {

        final int tmpRowDim = aLeftStore.getRowDim();
        final int tmpCalcSize = aRightStore.getRowDim();
        final int tmpColDim = aRightStore.getColDim();

        final double[] tmpLeftRow = new double[tmpCalcSize];
        final double[] tmpProdRow = new double[tmpColDim];
        double tmpVal;

        final boolean tmpLL = MatrixUtils.isLowerLeftShaded(aLeftStore);
        final boolean tmpLU = MatrixUtils.isUpperRightShaded(aLeftStore);
        final boolean tmpRL = MatrixUtils.isLowerLeftShaded(aRightStore);
        final boolean tmpRU = MatrixUtils.isUpperRightShaded(aRightStore);
        final boolean tmpPrune = tmpLL || tmpLU || tmpRL || tmpRU;
        int tmpFirst = 0;
        int tmpLimit = tmpCalcSize;

        for (int i = aFirstRow; i < aRowLimit; i++) {

            for (int c = 0; c < tmpCalcSize; c++) {
                tmpLeftRow[c] = aLeftStore.doubleValue(i, c);
            }

            for (int j = 0; j < tmpColDim; j++) {
                if (tmpPrune) {
                    tmpFirst = MatrixUtils.max(tmpLL ? i - 1 : 0, tmpRU ? j - 1 : 0, 0);
                    tmpLimit = MatrixUtils.min(tmpLU ? i + 2 : tmpCalcSize, tmpRL ? j + 2 : tmpCalcSize, tmpCalcSize);
                }
                tmpVal = PrimitiveMath.ZERO;
                for (int c = tmpFirst; c < tmpLimit; c++) {
                    tmpVal += tmpLeftRow[c] * aRightStore.doubleValue(c, j);
                }
                tmpProdRow[j] = tmpVal;
            }

            for (int j = 0; j < tmpColDim; j++) {
                aData[i + (j * tmpRowDim)] = tmpProdRow[j];
            }
        }
    }

    private MultiplyBoth() {
        super();
    }

    public int getThreshold() {
        return THRESHOLD;
    }

}
