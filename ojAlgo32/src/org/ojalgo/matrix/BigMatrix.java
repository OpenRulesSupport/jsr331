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

import java.math.BigDecimal;

import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;

/**
 * BigMatrix
 *
 * @author apete
 */
public final class BigMatrix extends AbstractMatrix<BigDecimal> {

    public static final MatrixFactory<BigDecimal> FACTORY = new MatrixFactory<BigDecimal>(BigMatrix.class, BigDenseStore.FACTORY);

    public static MatrixBuilder<BigDecimal> getBuilder(final int aLength) {
        return FACTORY.getBuilder(aLength);
    }

    public static MatrixBuilder<BigDecimal> getBuilder(final int aRowDim, final int aColDim) {
        return FACTORY.getBuilder(aRowDim, aColDim);
    }

    /**
     * This method is for internal use only - YOU should NOT use it!
     */
    BigMatrix(final MatrixStore<BigDecimal> aStore) {
        super(aStore);
    }

    public BasicMatrix enforce(final NumberContext aContext) {

        final BigDenseStore retVal = BigDenseStore.FACTORY.copy(this.getStore());

        retVal.modifyAll(aContext.getBigEnforceFunction());

        return this.getFactory().instantiate(retVal);
    }

    public BasicMatrix round(final NumberContext aContext) {

        final BigDenseStore retVal = BigDenseStore.FACTORY.copy(this.getStore());

        retVal.modifyAll(aContext.getBigRoundFunction());

        return this.getFactory().instantiate(retVal);
    }

    public BigDecimal toBigDecimal(final int aRow, final int aCol) {
        return this.getStore().get(aRow, aCol);
    }

    @Override
    public PhysicalStore<BigDecimal> toBigStore() {
        return this.getStore().copy();
    }

    public ComplexNumber toComplexNumber(final int aRow, final int aCol) {
        return new ComplexNumber(this.getStore().doubleValue(aRow, aCol));
    }

    public String toString(final int aRow, final int aCol) {
        return this.toBigDecimal(aRow, aCol).toPlainString();
    }

    @Override
    PhysicalStore<BigDecimal> copyOf(final BasicMatrix aMtrx) {
        return aMtrx.toBigStore();
    }

    @Override
    MatrixFactory<BigDecimal> getFactory() {
        return FACTORY;
    }

    @Override
    MatrixStore<BigDecimal> getStoreFrom(final BasicMatrix aMtrx) {
        if (aMtrx instanceof BigMatrix) {
            return ((BigMatrix) aMtrx).getStore();
        } else {
            return aMtrx.toBigStore();
        }
    }

}
