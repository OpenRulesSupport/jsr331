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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.AboveBelowStore;
import org.ojalgo.matrix.store.IdentityStore;
import org.ojalgo.matrix.store.LeftRightStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.ZeroStore;
import org.ojalgo.random.RandomNumber;

/**
 * MatrixFactory creates instances of classes that implement the
 * {@linkplain org.ojalgo.matrix.BasicMatrix} interface and have a 
 * constructor that takes a MatrixStore as input.
 * 
 * @author apete
 */
public final class MatrixFactory<N extends Number> implements BasicMatrix.Factory<BasicMatrix> {

    private static Constructor<? extends BasicMatrix> getConstructor(final Class<? extends BasicMatrix> aTemplate) {
        try {
            final Constructor<? extends BasicMatrix> retVal = aTemplate.getDeclaredConstructor(MatrixStore.class);
            retVal.setAccessible(true);
            return retVal;
        } catch (final SecurityException anException) {
            return null;
        } catch (final NoSuchMethodException anException) {
            return null;
        }
    }

    private final Constructor<? extends BasicMatrix> myConstructor;
    private final PhysicalStore.Factory<N, ?> myPhysicalFactory;

    @SuppressWarnings("unused")
    private MatrixFactory() {

        this(null, null);

        ProgrammingError.throwForIllegalInvocation();
    }

    MatrixFactory(final Class<? extends BasicMatrix> aTemplate, final PhysicalStore.Factory<N, ?> aPhysical) {

        super();

        myPhysicalFactory = aPhysical;
        myConstructor = MatrixFactory.getConstructor(aTemplate);
    }

    public BasicMatrix columns(final Access1D<?>... aSource) {
        return this.instantiate(myPhysicalFactory.columns(aSource));
    }

    public BasicMatrix columns(final double[]... aSource) {
        return this.instantiate(myPhysicalFactory.columns(aSource));
    }

    public BasicMatrix columns(final List<? extends Number>... aSource) {
        return this.instantiate(myPhysicalFactory.columns(aSource));
    }

    public BasicMatrix columns(final Number[]... aSource) {
        return this.instantiate(myPhysicalFactory.columns(aSource));
    }

    public BasicMatrix copy(final Access2D<?> aSource) {
        return this.instantiate(myPhysicalFactory.copy(aSource));
    }

    @SuppressWarnings("unchecked")
    public MatrixBuilder<N> getBuilder(final int aLength) {
        return this.getBuilder(aLength, 1);
    }

    @SuppressWarnings("unchecked")
    public MatrixBuilder<N> getBuilder(final int aRowDim, final int aColDim) {

        return new MatrixBuilder<N>(myPhysicalFactory, aRowDim, aColDim) {

            @Override
            public BasicMatrix build() {
                return MatrixFactory.this.instantiate(this.getPhysicalStore());
            }
        };
    }

    public BasicMatrix makeEye(final int aRowDim, final int aColDim) {

        final int tmpMinDim = Math.min(aRowDim, aColDim);

        MatrixStore<N> retVal = new IdentityStore<N>(myPhysicalFactory, tmpMinDim);

        if (aRowDim > tmpMinDim) {
            retVal = new AboveBelowStore<N>(retVal, new ZeroStore<N>(myPhysicalFactory, aRowDim - tmpMinDim, aColDim));
        } else if (aColDim > tmpMinDim) {
            retVal = new LeftRightStore<N>(retVal, new ZeroStore<N>(myPhysicalFactory, aRowDim, aColDim - tmpMinDim));
        }

        return this.instantiate(retVal);
    }

    public BasicMatrix makeRandom(final int aRowDim, final int aColDim, final RandomNumber aRndm) {
        return this.instantiate(myPhysicalFactory.makeRandom(aRowDim, aColDim, aRndm));
    }

    public BasicMatrix makeZero(final int aRowDim, final int aColDim) {
        return this.instantiate(new ZeroStore<N>(myPhysicalFactory, aRowDim, aColDim));
    }

    public BasicMatrix rows(final Access1D<?>... aSource) {
        return this.instantiate(myPhysicalFactory.rows(aSource));
    }

    public BasicMatrix rows(final double[]... aSource) {
        return this.instantiate(myPhysicalFactory.rows(aSource));
    }

    public BasicMatrix rows(final List<? extends Number>... aSource) {
        return this.instantiate(myPhysicalFactory.rows(aSource));
    }

    public BasicMatrix rows(final Number[]... aSource) {
        return this.instantiate(myPhysicalFactory.rows(aSource));
    }

    protected final PhysicalStore.Factory<N, ?> getPhysicalFactory() {
        return myPhysicalFactory;
    }

    /**
     * This method is for internal use only - YOU should NOT use it!
     */
    final BasicMatrix instantiate(final MatrixStore<N> aStore) {
        try {
            return myConstructor.newInstance(aStore);
        } catch (final IllegalArgumentException anException) {
            throw new ProgrammingError(anException);
        } catch (final InstantiationException anException) {
            throw new ProgrammingError(anException);
        } catch (final IllegalAccessException anException) {
            throw new ProgrammingError(anException);
        } catch (final InvocationTargetException anException) {
            throw new ProgrammingError(anException);
        }
    }

}
