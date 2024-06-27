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

import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.implementation.BigFunction;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;

public final class MultiplyLeft extends MatrixOperation {

    public static int THRESHOLD = 16;

    public static void invoke(final BigDecimal[] aData, final int aFirstRow, final int aRowLimit, final MatrixStore<BigDecimal> aLeftStore, final BigDecimal[] aRightArray) {

        final int tmpRowDim = aLeftStore.getRowDim();
        final int tmpCalcSize = aLeftStore.getColDim();
        final int tmpColDim = aRightArray.length / tmpCalcSize;

        final BigDecimal[] tmpLeftRow = new BigDecimal[tmpCalcSize];
        final BigDecimal[] tmpProdRow = new BigDecimal[tmpColDim];
        int tmpIndex;
        BigDecimal tmpVal;

        for (int i = aFirstRow; i < aRowLimit; i++) {

            for (int c = 0; c < tmpCalcSize; c++) {
                tmpLeftRow[c] = aLeftStore.get(i, c);
            }

            for (int j = 0; j < tmpColDim; j++) {
                tmpIndex = j * tmpCalcSize;
                tmpVal = BigMath.ZERO;
                for (int c = 0; c < tmpCalcSize; c++) {
                    tmpVal = BigFunction.ADD.invoke(tmpVal, BigFunction.MULTIPLY.invoke(tmpLeftRow[c], aRightArray[tmpIndex++]));
                }
                tmpProdRow[j] = tmpVal;
            }

            for (int j = 0; j < tmpColDim; j++) {
                aData[i + (j * tmpRowDim)] = tmpProdRow[j];
            }
        }
    }

    public static void invoke(final ComplexNumber[] aData, final int aFirstRow, final int aRowLimit, final MatrixStore<ComplexNumber> aLeftStore, final ComplexNumber[] aRightArray) {

        final int tmpRowDim = aLeftStore.getRowDim();
        final int tmpCalcSize = aLeftStore.getColDim();
        final int tmpColDim = aRightArray.length / tmpCalcSize;

        final ComplexNumber[] tmpLeftRow = new ComplexNumber[tmpCalcSize];
        final ComplexNumber[] tmpProdRow = new ComplexNumber[tmpColDim];
        int tmpIndex;
        ComplexNumber tmpVal;

        for (int i = aFirstRow; i < aRowLimit; i++) {

            for (int c = 0; c < tmpCalcSize; c++) {
                tmpLeftRow[c] = aLeftStore.get(i, c);
            }

            for (int j = 0; j < tmpColDim; j++) {
                tmpIndex = j * tmpCalcSize;
                tmpVal = ComplexNumber.ZERO;
                for (int c = 0; c < tmpCalcSize; c++) {
                    tmpVal = tmpVal.add(tmpLeftRow[c].multiply(aRightArray[tmpIndex++]));
                }
                tmpProdRow[j] = tmpVal;
            }

            for (int j = 0; j < tmpColDim; j++) {
                aData[i + (j * tmpRowDim)] = tmpProdRow[j];
            }
        }
    }

    public static void invoke(final double[] aData, final int aFirstRow, final int aRowLimit, final MatrixStore<Double> aLeftStore, final double[] aRightArray) {

        final int tmpRowDim = aLeftStore.getRowDim();
        final int tmpCalcSize = aLeftStore.getColDim();
        final int tmpColDim = aRightArray.length / tmpCalcSize;

        final double[] tmpLeftRow = new double[tmpCalcSize];
        final double[] tmpProdRow = new double[tmpColDim];
        int tmpIndex;
        double tmpVal;

        for (int i = aFirstRow; i < aRowLimit; i++) {

            for (int c = 0; c < tmpCalcSize; c++) {
                tmpLeftRow[c] = aLeftStore.doubleValue(i, c);
            }

            for (int j = 0; j < tmpColDim; j++) {
                tmpIndex = j * tmpCalcSize;
                tmpVal = PrimitiveMath.ZERO;
                for (int c = 0; c < tmpCalcSize; c++) {
                    tmpVal += tmpLeftRow[c] * aRightArray[tmpIndex++];
                }
                tmpProdRow[j] = tmpVal;
            }

            for (int j = 0; j < tmpColDim; j++) {
                aData[i + (j * tmpRowDim)] = tmpProdRow[j];
            }
        }
    }

    private MultiplyLeft() {
        super();
    }

    public int getThreshold() {
        return THRESHOLD;
    }

}
