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
package org.ojalgo.function.multiary;

import java.util.List;

import org.ojalgo.access.Access1D;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore.Factory;

abstract class AbstractMultiary<N extends Number> implements MultiaryFunction<N> {

    protected AbstractMultiary() {
        super();
    }

    public final MatrixStore<N> getGradient(final Access1D<?> anArg) {
        return this.getGradient(this.getFactory().columns(anArg));
    }

    public final MatrixStore<N> getGradient(final double[] anArg) {
        return this.getGradient(this.getFactory().columns(anArg));
    }

    @SuppressWarnings("unchecked")
    public final MatrixStore<N> getGradient(final List<? extends Number> anArg) {
        return this.getGradient(this.getFactory().columns(anArg));
    }

    public final MatrixStore<N> getGradient(final Number[] anArg) {
        return this.getGradient(this.getFactory().columns(anArg));
    }

    public final MatrixStore<N> getHessian(final Access1D<?> anArg) {
        return this.getHessian(this.getFactory().columns(anArg));
    }

    public final MatrixStore<N> getHessian(final double[] anArg) {
        return this.getHessian(this.getFactory().columns(anArg));
    }

    @SuppressWarnings("unchecked")
    public final MatrixStore<N> getHessian(final List<? extends Number> anArg) {
        return this.getHessian(this.getFactory().columns(anArg));
    }

    public final MatrixStore<N> getHessian(final Number[] anArg) {
        return this.getHessian(this.getFactory().columns(anArg));
    }

    public final N invoke(final Access1D<?> anArg) {
        return this.invoke(this.getFactory().columns(anArg));
    }

    public final N invoke(final double[] anArg) {
        return this.invoke(this.getFactory().columns(anArg));
    }

    @SuppressWarnings("unchecked")
    public final N invoke(final List<? extends Number> anArg) {
        return this.invoke(this.getFactory().columns(anArg));
    }

    public final N invoke(final Number[] anArg) {
        return this.invoke(this.getFactory().columns(anArg));
    }

    protected abstract Factory<N, ?> getFactory();

}
