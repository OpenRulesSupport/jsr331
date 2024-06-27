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

import org.ojalgo.matrix.store.PhysicalStore;

public abstract class MatrixBuilder<N extends Number> {

    private final PhysicalStore<N> myPhysicalStore;
    private final PhysicalStore.Factory<N, ?> myFactory;

    @SuppressWarnings("unused")
    private MatrixBuilder() {
        this(null, 0, 0);
    }

    protected MatrixBuilder(final PhysicalStore.Factory<N, ?> aFactory, final int aRowDim, final int aColDim) {

        super();

        myPhysicalStore = aFactory.makeZero(aRowDim, aColDim);
        myFactory = aFactory;
    }

    MatrixBuilder(final PhysicalStore<N> aPhysicalStore) {

        super();

        myPhysicalStore = aPhysicalStore;
        myFactory = aPhysicalStore.factory();
    }

    public abstract BasicMatrix build();

    public final MatrixBuilder<N> fillAll(final Number aNmbr) {
        myPhysicalStore.fillAll(myFactory.getNumber(aNmbr));
        return this;
    }

    public final MatrixBuilder<N> fillColumn(final int aRow, final int aCol, final Number aNmbr) {
        myPhysicalStore.fillColumn(aRow, aCol, myFactory.getNumber(aNmbr));
        return this;
    }

    public final MatrixBuilder<N> fillDiagonal(final int aRow, final int aCol, final Number aNmbr) {
        myPhysicalStore.fillDiagonal(aRow, aCol, myFactory.getNumber(aNmbr));
        return this;
    }

    public final MatrixBuilder<N> fillRow(final int aRow, final int aCol, final Number aNmbr) {
        myPhysicalStore.fillRow(aRow, aCol, myFactory.getNumber(aNmbr));
        return this;
    }

    public final int getColDim() {
        return myPhysicalStore.getColDim();
    }

    public final int getMinDim() {
        return myPhysicalStore.getMinDim();
    }

    public final int getRowDim() {
        return myPhysicalStore.getRowDim();
    }

    public final MatrixBuilder<N> set(final int index, final double aNmbr) {
        myPhysicalStore.set(index, 0, aNmbr);
        return this;
    }

    public final MatrixBuilder<N> set(final int aRow, final int aCol, final double aNmbr) {
        myPhysicalStore.set(aRow, aCol, aNmbr);
        return this;
    }

    public final MatrixBuilder<N> set(final int aRow, final int aCol, final Number aNmbr) {
        myPhysicalStore.set(aRow, aCol, myFactory.getNumber(aNmbr));
        return this;
    }

    public final MatrixBuilder<N> set(final int index, final Number aNmbr) {
        myPhysicalStore.set(index, 0, myFactory.getNumber(aNmbr));
        return this;
    }

    protected final PhysicalStore<N> getPhysicalStore() {
        return myPhysicalStore;
    }
}
