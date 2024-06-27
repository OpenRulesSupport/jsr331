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
package org.ojalgo.matrix.store;

import org.ojalgo.ProgrammingError;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.scalar.Scalar;

/**
 * A Hessenberg matrix is one that is "almost" triangular. An upper
 * Hessenberg matrix has zero entries below the first subdiagonal.
 *
 * @author apete
 */
public final class UpperHessenbergStore<N extends Number> extends ShadingStore<N> {

    public UpperHessenbergStore(final MatrixStore<N> aBase) {
        super(aBase.getMinDim(), aBase.getColDim(), aBase);
    }

    @SuppressWarnings("unused")
    private UpperHessenbergStore(final int aRowDim, final int aColDim, final MatrixStore<N> aBase) {

        this(aBase);

        ProgrammingError.throwForIllegalInvocation();
    }

    public double doubleValue(final int aRow, final int aCol) {
        if (aRow > aCol + INT_ONE) {
            return PrimitiveMath.ZERO;
        } else {
            return this.getBase().doubleValue(aRow, aCol);
        }
    }

    public N get(final int aRow, final int aCol) {
        if (aRow > aCol + INT_ONE) {
            return this.factory().getStaticZero().getNumber();
        } else {
            return this.getBase().get(aRow, aCol);
        }
    }

    public boolean isLowerLeftShaded() {
        return BOOLEAN_TRUE;
    }

    public boolean isUpperRightShaded() {
        return BOOLEAN_FALSE;
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        if (aRow > aCol + INT_ONE) {
            return this.factory().getStaticZero();
        } else {
            return this.getBase().toScalar(aRow, aCol);
        }
    }
}
