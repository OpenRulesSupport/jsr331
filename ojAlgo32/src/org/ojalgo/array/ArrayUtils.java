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

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.store.MatrixStore;

public abstract class ArrayUtils {

    public static double[] copyOf(final double[] original) {
        final int tmpLength = original.length;
        final double[] retVal = new double[tmpLength];
        System.arraycopy(original, 0, retVal, 0, tmpLength);
        return retVal;
    }

    public static int[] copyOf(final int[] original) {
        final int tmpLength = original.length;
        final int[] retVal = new int[tmpLength];
        System.arraycopy(original, 0, retVal, 0, tmpLength);
        return retVal;
    }

    public static long[] copyOf(final long[] original) {
        final int tmpLength = original.length;
        final long[] retVal = new long[tmpLength];
        System.arraycopy(original, 0, retVal, 0, tmpLength);
        return retVal;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] copyOf(final T[] original) {
        final int tmpLength = original.length;
        final T[] retVal = (T[]) Array.newInstance(original.getClass().getComponentType(), tmpLength);
        System.arraycopy(original, 0, retVal, 0, tmpLength);
        return retVal;
    }

    public static void exchangeColumns(final double[][] aRawArray, final int aColA, final int aColB) {
        double tmpElem;
        final int tmpLength = aRawArray.length;
        for (int i = 0; i < tmpLength; i++) {
            tmpElem = aRawArray[i][aColA];
            aRawArray[i][aColA] = aRawArray[i][aColB];
            aRawArray[i][aColB] = tmpElem;
        }
    }

    public static void exchangeRows(final double[][] aRawArray, final int aRowA, final int aRowB) {
        final double[] tmpRow = aRawArray[aRowA];
        aRawArray[aRowA] = aRawArray[aRowB];
        aRawArray[aRowB] = tmpRow;
    }

    public static void fillAll(final double[][] aRawArray, final double aNmbr) {
        final int tmpLength = aRawArray.length;
        for (int i = 0; i < tmpLength; i++) {
            final int tmpInnerLength = aRawArray[i].length;
            for (int j = 0; j < tmpInnerLength; j++) {
                aRawArray[i][j] = aNmbr;
            }
        }
    }

    public static void fillColumn(final double[][] aRawArray, final int aRow, final int aCol, final double aNmbr) {
        final int tmpLength = aRawArray.length;
        for (int i = aRow; i < tmpLength; i++) {
            aRawArray[i][aCol] = aNmbr;
        }
    }

    public static void fillDiagonal(final double[][] aRawArray, final int aRow, final int aCol, final double aNmbr) {
        final int tmpLength = aRawArray.length;
        for (int ij = 0; ((aRow + ij) < tmpLength) && ((aCol + ij) < aRawArray[aRow + ij].length); ij++) {
            aRawArray[aRow + ij][aCol + ij] = aNmbr;
        }
    }

    public static void fillMatching(final double[][] anArrayToBeUpdated, final double aLeftFirstArg, final BinaryFunction<Double> aFunc,
            final double[][] aRightSecondArg) {
        final int tmpLength = anArrayToBeUpdated.length;
        for (int i = 0; i < tmpLength; i++) {
            final int tmpInnerLength = anArrayToBeUpdated[i].length;
            for (int j = 0; j < tmpInnerLength; j++) {
                anArrayToBeUpdated[i][j] = aFunc.invoke(aLeftFirstArg, aRightSecondArg[i][j]);
            }
        }
    }

    public static void fillMatching(final double[][] anArrayToBeUpdated, final double[][] aLeftFirstArg, final BinaryFunction<Double> aFunc,
            final double aRightSecondArg) {
        final int tmpLength = anArrayToBeUpdated.length;
        for (int i = 0; i < tmpLength; i++) {
            final int tmpInnerLength = anArrayToBeUpdated[i].length;
            for (int j = 0; j < tmpInnerLength; j++) {
                anArrayToBeUpdated[i][j] = aFunc.invoke(aLeftFirstArg[i][j], aRightSecondArg);
            }
        }
    }

    public static void fillMatching(final double[][] anArrayToBeUpdated, final double[][] aLeftFirstArg, final BinaryFunction<Double> aFunc,
            final double[][] aRightSecondArg) {
        final int tmpLength = anArrayToBeUpdated.length;
        for (int i = 0; i < tmpLength; i++) {
            final int tmpInnerLength = anArrayToBeUpdated[i].length;
            for (int j = 0; j < tmpInnerLength; j++) {
                anArrayToBeUpdated[i][j] = aFunc.invoke(aLeftFirstArg[i][j], aRightSecondArg[i][j]);
            }
        }
    }

    public static void fillRow(final double[][] aRawArray, final int aRow, final int aCol, final double aNmbr) {
        final int tmpLength = aRawArray[aRow].length;
        for (int j = aCol; j < tmpLength; j++) {
            aRawArray[aRow][j] = aNmbr;
        }
    }

    public static void modifyAll(final double[][] aRawArray, final UnaryFunction<?> aFunc) {
        final int tmpLength = aRawArray.length;
        for (int i = 0; i < tmpLength; i++) {
            final int tmpInnerLength = aRawArray[i].length;
            for (int j = 0; j < tmpInnerLength; j++) {
                aRawArray[i][j] = aFunc.invoke(aRawArray[i][j]);
            }
        }
    }

    public static void modifyColumn(final double[][] aRawArray, final int aRow, final int aCol, final UnaryFunction<?> aFunc) {
        final int tmpLength = aRawArray.length;
        for (int i = aRow; i < tmpLength; i++) {
            aRawArray[i][aCol] = aFunc.invoke(aRawArray[i][aCol]);
        }
    }

    public static void modifyDiagonal(final double[][] aRawArray, final int aRow, final int aCol, final UnaryFunction<?> aFunc) {
        final int tmpLength = aRawArray.length;
        for (int ij = 0; ((aRow + ij) < tmpLength) && ((aCol + ij) < aRawArray[aRow + ij].length); ij++) {
            aRawArray[aRow + ij][aCol + ij] = aFunc.invoke(aRawArray[aRow + ij][aCol + ij]);
        }
    }

    public static void modifyRow(final double[][] aRawArray, final int aRow, final int aCol, final UnaryFunction<?> aFunc) {
        final int tmpLength = aRawArray[aRow].length;
        for (int j = aCol; j < tmpLength; j++) {
            aRawArray[aRow][j] = aFunc.invoke(aRawArray[aRow][j]);
        }
    }

    public static double[] toRawCopyOf(final Access1D<?> original) {

        final int tmpLength = original.size();

        final double[] retVal = new double[tmpLength];

        for (int i = tmpLength; i-- != 0;) {
            retVal[i] = original.doubleValue(i);
        }

        return retVal;
    }

    public static double[][] toRawCopyOf(final Access2D<?> original) {

        final int tmpRowDim = original.getRowDim();
        final int tmpColDim = original.getColDim();

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        double[] tmpRow;
        for (int i = tmpRowDim; i-- != 0;) {
            tmpRow = retVal[i];
            for (int j = tmpColDim; j-- != 0;) {
                tmpRow[j] = original.doubleValue(i, j);
            }
        }

        return retVal;
    }

    public static double[][] toRawCopyOf(final MatrixStore<?> original) {
        return ArrayUtils.toRawCopyOf((Access2D<?>) original);
    }

    public static void visitAll(final double[][] aRawArray, final AggregatorFunction<?> aVisitor) {
        final int tmpLength = aRawArray.length;
        for (int i = 0; i < tmpLength; i++) {
            final int tmpInnerLength = aRawArray[i].length;
            for (int j = 0; j < tmpInnerLength; j++) {
                aVisitor.invoke(aRawArray[i][j]);
            }
        }
    }

    public static void visitColumn(final double[][] aRawArray, final int aRow, final int aCol, final AggregatorFunction<?> aVisitor) {
        final int tmpLength = aRawArray[aRow].length;
        for (int j = aCol; j < tmpLength; j++) {
            aVisitor.invoke(aRawArray[aRow][j]);
        }
    }

    public static void visitDiagonal(final double[][] aRawArray, final int aRow, final int aCol, final AggregatorFunction<?> aVisitor) {
        final int tmpLength = aRawArray.length;
        for (int ij = 0; ((aRow + ij) < tmpLength) && ((aCol + ij) < aRawArray[aRow + ij].length); ij++) {
            aVisitor.invoke(aRawArray[aRow + ij][aCol + ij]);
        }
    }

    public static void visitRow(final double[][] aRawArray, final int aRow, final int aCol, final AggregatorFunction<?> aVisitor) {
        final int tmpLength = aRawArray.length;
        for (int i = aRow; i < tmpLength; i++) {
            aVisitor.invoke(aRawArray[i][aCol]);
        }
    }

    public static Access1D<Double> wrapAccess1D(final double[] aRaw) {
        return new Access1D<Double>() {

            public double doubleValue(final int anInd) {
                return aRaw[anInd];
            }

            public Double get(final int anInd) {
                return aRaw[anInd];
            }

            public final Iterator<Double> iterator() {
                return new Iterator1D<Double>(this);
            }

            public int size() {
                return aRaw.length;
            }
        };
    }

    public static <N extends Number> Access1D<N> wrapAccess1D(final List<? extends N> aList) {
        return new Access1D<N>() {

            public double doubleValue(final int anInd) {
                return aList.get(anInd).doubleValue();
            }

            public N get(final int anInd) {
                return aList.get(anInd);
            }

            public final Iterator<N> iterator() {
                return new Iterator1D<N>(this);
            }

            public int size() {
                return aList.size();
            }
        };
    }

    public static <N extends Number> Access1D<N> wrapAccess1D(final N[] aRaw) {
        return new Access1D<N>() {

            public double doubleValue(final int anInd) {
                return aRaw[anInd].doubleValue();
            }

            public N get(final int anInd) {
                return aRaw[anInd];
            }

            public final Iterator<N> iterator() {
                return new Iterator1D<N>(this);
            }

            public int size() {
                return aRaw.length;
            }
        };
    }

    public static Access2D<Double> wrapAccess2D(final double[][] aRaw) {
        return new Access2D<Double>() {

            public double doubleValue(final int anInd) {
                return aRaw[AccessUtils.row(anInd, aRaw.length)][AccessUtils.column(anInd, aRaw.length)];
            }

            public double doubleValue(final int aRow, final int aCol) {
                return aRaw[aRow][aCol];
            }

            public Double get(final int anInd) {
                return aRaw[AccessUtils.row(anInd, aRaw.length)][AccessUtils.column(anInd, aRaw.length)];
            }

            public Double get(final int aRow, final int aCol) {
                return aRaw[aRow][aCol];
            }

            public int getColDim() {
                return aRaw[0].length;
            }

            public int getRowDim() {
                return aRaw.length;
            }

            public Iterator<Double> iterator() {
                return new Iterator1D<Double>(this);
            }

            public int size() {
                return aRaw.length * aRaw[0].length;
            }
        };
    }

    public static <N extends Number> Access2D<N> wrapAccess2D(final N[][] aRaw) {
        return new Access2D<N>() {

            public double doubleValue(final int anInd) {
                return aRaw[AccessUtils.row(anInd, aRaw.length)][AccessUtils.column(anInd, aRaw.length)].doubleValue();
            }

            public double doubleValue(final int aRow, final int aCol) {
                return aRaw[aRow][aCol].doubleValue();
            }

            public N get(final int anInd) {
                return aRaw[AccessUtils.row(anInd, aRaw.length)][AccessUtils.column(anInd, aRaw.length)];
            }

            public N get(final int aRow, final int aCol) {
                return aRaw[aRow][aCol];
            }

            public int getColDim() {
                return aRaw[0].length;
            }

            public int getRowDim() {
                return aRaw.length;
            }

            public Iterator<N> iterator() {
                return new Iterator1D<N>(this);
            }

            public int size() {
                return aRaw.length * aRaw[0].length;
            }
        };
    }

    private ArrayUtils() {
        super();
    }

}
