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
package org.ojalgo.matrix;

import org.ojalgo.ProgrammingError;

/**
 * MatrixError
 *
 * @author apete
 */
public class MatrixError extends ProgrammingError {

    public static void throwIfMultiplicationNotPossible(final BasicMatrix aMtrxLeft, final BasicMatrix aMtrxRight) {

        if (aMtrxLeft.getColDim() != aMtrxRight.getRowDim()) {
            throw new MatrixError("The column dimension of the left matrix does not match the row dimension of the right matrix!");
        }
    }

    public static void throwIfNotEqualColumnDimensions(final BasicMatrix aMtrx1, final BasicMatrix aMtrx2) {

        if (aMtrx1.getColDim() != aMtrx2.getColDim()) {
            throw new MatrixError("Column dimensions are not equal!");
        }
    }

    public static void throwIfNotEqualDimensions(final BasicMatrix aMtrx1, final BasicMatrix aMtrx2) {

        MatrixError.throwIfNotEqualRowDimensions(aMtrx1, aMtrx2);

        MatrixError.throwIfNotEqualColumnDimensions(aMtrx1, aMtrx2);
    }

    public static void throwIfNotEqualRowDimensions(final BasicMatrix aMtrx1, final BasicMatrix aMtrx2) {

        if (aMtrx1.getRowDim() != aMtrx2.getRowDim()) {
            throw new MatrixError("Row dimensions are not equal!");
        }
    }

    public static void throwIfNotSquare(final BasicMatrix aMtrx) {

        if (aMtrx.getRowDim() != aMtrx.getColDim()) {
            throw new MatrixError("Matrix is not square!");
        }
    }

    public MatrixError(final String aString) {
        super(aString);
    }

}
