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
package org.ojalgo.access;

import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;

public abstract class AccessUtils {

    private static final int INT_ONE = 1;
    private static final int INT_ZERO = 0;

    public static int column(final int anIndex, final int aRowDim) {
        return anIndex / aRowDim;
    }

    public static int column(final int anIndex, final int[] aStructure) {
        return AccessUtils.column(anIndex, aStructure[INT_ZERO]);
    }

    @SuppressWarnings("unchecked")
    public static boolean equals(final Access1D<?> accessA, final Access1D<?> accessB, final NumberContext aCntxt) {

        final int tmpLength = accessA.size();

        boolean retVal = tmpLength == accessB.size();

        final double tmpError = aCntxt.getError();

        if ((accessA.get(0) instanceof ComplexNumber) || (accessB.get(0) instanceof ComplexNumber)) {

            final Access1D<ComplexNumber> tmpAccessA = (Access1D<ComplexNumber>) accessA;
            final Access1D<ComplexNumber> tmpAccessB = (Access1D<ComplexNumber>) accessB;

            for (int i = 0; retVal && (i < tmpLength); i++) {
                retVal &= (tmpAccessA.get(i).subtract(tmpAccessB.get(i))).getModulus() <= tmpError;
            }

        } else {

            for (int i = 0; retVal && (i < tmpLength); i++) {
                retVal &= Math.abs(accessA.doubleValue(i) - accessB.doubleValue(i)) <= tmpError;
            }
        }

        return retVal;
    }

    public static boolean equals(final Access2D<?> accessA, final Access2D<?> accessB, final NumberContext aCntxt) {
        return (accessA.getRowDim() == accessB.getRowDim()) && (accessA.getColDim() == accessB.getColDim())
                && AccessUtils.equals((Access1D<?>) accessA, (Access1D<?>) accessB, aCntxt);
    }

    public static boolean equals(final AccessAnyD<?> accessA, final AccessAnyD<?> accessB, final NumberContext aCntxt) {

        boolean retVal = true;
        int d = 0;
        int tmpSize;

        do {
            tmpSize = accessA.size(d);
            retVal &= tmpSize == accessB.size(d);
            d++;
        } while (retVal && ((d <= 3) || (tmpSize > 1)));

        return retVal && AccessUtils.equals((Access1D<?>) accessA, (Access1D<?>) accessB, aCntxt);
    }

    /**
     * @param aStructure An access structure
     * @param aReference An access element reference
     * @return The index of that element
     */
    public static int index(final int[] aStructure, final int[] aReference) {
        int retVal = aReference[INT_ZERO];
        int tmpFactor = aStructure[INT_ZERO];
        final int tmpLength = aReference.length;
        for (int i = INT_ONE; i < tmpLength; i++) {
            retVal += tmpFactor * aReference[i];
            tmpFactor *= aStructure[i];
        }
        return retVal;
    }

    public static int row(final int anIndex, final int aRowDim) {
        return anIndex % aRowDim;
    }

    public static int row(final int anIndex, final int[] aStructure) {
        return AccessUtils.row(anIndex, aStructure[INT_ZERO]);
    }

    /**
     * @param aStructure An access structure
     * @return The size of an access with that structure
     */
    public static int size(final int[] aStructure) {
        int retVal = INT_ONE;
        final int tmpLength = aStructure.length;
        for (int i = INT_ZERO; i < tmpLength; i++) {
            retVal *= aStructure[i];
        }
        return retVal;
    }

    /**
     * @param aStructure An access structure
     * @param aDimension A dimension index
     * @return The size of that dimension
     */
    public static int size(final int[] aStructure, final int aDimension) {
        return aStructure.length > aDimension ? aStructure[aDimension] : INT_ONE;
    }

    /**
     * @param aStructure An access structure
     * @param aDimension A dimension index indication a direction
     * @return The step size (index change) in that direction
     */
    public static int step(final int[] aStructure, final int aDimension) {
        int retVal = INT_ONE;
        for (int i = INT_ZERO; i < aDimension; i++) {
            retVal *= AccessUtils.size(aStructure, i);
        }
        return retVal;
    }

    /**
     * A more complex/general version of {@linkplain #step(int[], int)}.
     * 
     * @param aStructure An access structure
     * @param aReferenceIncrement A vector indication a direction (and size)
     * @return The step size (index change)
     */
    public static int step(final int[] aStructure, final int[] aReferenceIncrement) {
        int retVal = INT_ZERO;
        int tmpFactor = INT_ONE;
        final int tmpLimit = aReferenceIncrement.length;
        for (int i = INT_ONE; i < tmpLimit; i++) {
            retVal += tmpFactor * aReferenceIncrement[i];
            tmpFactor *= aStructure[i];
        }
        return retVal;
    }

    public static int[] structure(final StructureAnyD aStructure) {

        final int tmpSize = aStructure.size();

        int tmpTotal = aStructure.size(INT_ZERO);
        int tmpRank = INT_ONE;

        while (tmpTotal < tmpSize) {
            tmpTotal *= aStructure.size(tmpRank);
            tmpRank++;
        }

        final int[] retVal = new int[tmpRank];

        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = aStructure.size(i);
        }

        return retVal;
    }

    private AccessUtils() {
        super();
    }

}
