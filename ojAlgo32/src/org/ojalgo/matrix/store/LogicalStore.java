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
import org.ojalgo.matrix.store.PhysicalStore.Factory;

/**
 * Logical stores are (intended to be) immutable. Therefore LogicalStore
 * subclasses should be made .
 * 
 * @author apete
 */
abstract class LogicalStore<N extends Number> extends AbstractStore<N> {

    private MatrixStore<N> myBase;

    @SuppressWarnings("unused")
    private LogicalStore(final int aRowDim, final int aColDim) {

        this(aRowDim, aColDim, null);

        ProgrammingError.throwForIllegalInvocation();
    }

    protected LogicalStore(final int aRowDim, final int aColDim, final MatrixStore<N> aBase) {

        super(aRowDim, aColDim);

        myBase = aBase;

        if (myBase == null) {
            throw new IllegalArgumentException(this.getClass().getName() + " cannot have a null 'base'!");
        }
    }

    public final Factory<N, ?> factory() {
        return myBase.factory();
    }

    /**
     * @deprecated v33 Use {@link #factory()} instead
     */
    @Deprecated
    public final Factory<N, ?> getFactory() {
        return this.factory();
    }

    protected final MatrixStore<N> getBase() {
        return myBase;
    }

}
