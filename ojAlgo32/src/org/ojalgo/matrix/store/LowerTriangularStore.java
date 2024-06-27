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

public final class LowerTriangularStore<N extends Number> extends ShadingStore<N> {

    private final boolean myAssumeOne;

    public LowerTriangularStore(final MatrixStore<N> aBase, final boolean assumeOne) {

        super(aBase.getRowDim(), aBase.getMinDim(), aBase);

        myAssumeOne = assumeOne;
    }

    @SuppressWarnings("unused")
    private LowerTriangularStore(final int aRowDim, final int aColDim, final MatrixStore<N> aBase) {

        this(aBase, BOOLEAN_TRUE);

        ProgrammingError.throwForIllegalInvocation();
    }

    public double doubleValue(final int aRow, final int aCol) {
        if (aRow < aCol) {
            return PrimitiveMath.ZERO;
        } else if (myAssumeOne && (aRow == aCol)) {
            return PrimitiveMath.ONE;
        } else {
            return this.getBase().doubleValue(aRow, aCol);
        }
    }

    public N get(final int aRow, final int aCol) {
        if (aRow < aCol) {
            return this.factory().getStaticZero().getNumber();
        } else if (myAssumeOne && (aRow == aCol)) {
            return this.factory().getStaticOne().getNumber();
        } else {
            return this.getBase().get(aRow, aCol);
        }
    }

    public boolean isLowerLeftShaded() {
        return BOOLEAN_FALSE;
    }

    public boolean isUpperRightShaded() {
        return BOOLEAN_TRUE;
    }

    public Scalar<N> toScalar(final int aRow, final int aCol) {
        if (aRow < aCol) {
            return this.factory().getStaticZero();
        } else if (myAssumeOne && (aRow == aCol)) {
            return this.factory().getStaticOne();
        } else {
            return this.getBase().toScalar(aRow, aCol);
        }
    }

}
