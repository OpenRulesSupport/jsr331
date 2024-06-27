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
package org.ojalgo.matrix.decomposition;

import org.ojalgo.OjAlgoUtils;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.function.implementation.FunctionSet;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.WrapperStore;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * AbstractDecomposition
 *
 * @author apete
 */
abstract class AbstractDecomposition<N extends Number> implements MatrixDecomposition<N> {

    private boolean myAspectRatioNormal = true;
    private boolean myComputed = false;
    private final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> myFactory;

    @SuppressWarnings("unused")
    private AbstractDecomposition() {
        this(null);
    }

    protected AbstractDecomposition(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory) {

        super();

        myFactory = aFactory;
    }

    public final boolean equals(final MatrixDecomposition<N> aDecomp, final NumberContext aCntxt) {
        return MatrixUtils.equals(this.reconstruct(), aDecomp.reconstruct(), aCntxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object someObj) {
        if (someObj instanceof MatrixStore) {
            return this.equals((MatrixStore<N>) someObj, TypeUtils.EQUALS_NUMBER_CONTEXT);
        } else {
            return super.equals(someObj);
        }
    }

    public boolean isAspectRatioNormal() {
        return myAspectRatioNormal;
    }

    public final boolean isComputed() {
        return myComputed;
    }

    public void reset() {
        myAspectRatioNormal = true;
        myComputed = false;
    }

    protected final boolean aspectRatioNormal(final boolean aFlag) {
        return (myAspectRatioNormal = aFlag);
    }

    protected final boolean computed(final boolean aState) {
        return (myComputed = aState);
    }

    protected final DecompositionStore<N> copy(final Access2D<?> aSource) {
        return myFactory.copy(aSource);
    }

    protected final AggregatorCollection<N> getAggregatorCollection() {
        return myFactory.getAggregatorCollection();
    }

    protected final FunctionSet<N> getFunctionSet() {
        return myFactory.getFunctionSet();
    }

    protected final int getMaxDimToFitInCache() {
        return OjAlgoUtils.ENVIRONMENT.getCacheL2Dim2D(8L) / 3;
    }

    protected final N getStaticOne() {
        return myFactory.getStaticOne().getNumber();
    }

    protected final N getStaticZero() {
        return myFactory.getStaticZero().getNumber();
    }

    protected final SimpleArray<N> makeArray(final int aLength) {
        return myFactory.makeArray(aLength);
    }

    protected final DecompositionStore<N> makeEye(final int aRowDim, final int aColDim) {
        return myFactory.makeEye(aRowDim, aColDim);
    }

    protected final Householder<N> makeHouseholder(final int aDim) {
        return myFactory.makeHouseholder(aDim);
    }

    protected final Rotation<N> makeRotation(final int aLow, final int aHigh, final double aCos, final double aSin) {
        return myFactory.makeRotation(aLow, aHigh, aCos, aSin);
    }

    protected final Rotation<N> makeRotation(final int aLow, final int aHigh, final N aCos, final N aSin) {
        return myFactory.makeRotation(aLow, aHigh, aCos, aSin);
    }

    protected final DecompositionStore<N> makeZero(final int aRowDim, final int aColDim) {
        return myFactory.makeZero(aRowDim, aColDim);
    }

    protected final MatrixStore<N> wrap(final Access2D<?> aSource) {
        return new WrapperStore<N>(myFactory, aSource);
    }

}
