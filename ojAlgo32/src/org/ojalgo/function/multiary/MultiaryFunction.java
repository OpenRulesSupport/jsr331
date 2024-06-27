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
import org.ojalgo.function.Function;
import org.ojalgo.matrix.store.MatrixStore;

public interface MultiaryFunction<N extends Number> extends Function<N> {

    int dim();

    MatrixStore<N> getGradient(Access1D<?> anArg);

    MatrixStore<N> getGradient(double[] anArg);

    MatrixStore<N> getGradient(List<? extends Number> anArg);

    MatrixStore<N> getGradient(final MatrixStore<N> anArg);

    MatrixStore<N> getGradient(Number[] anArg);

    MatrixStore<N> getHessian(Access1D<?> anArg);

    MatrixStore<N> getHessian(double[] anArg);

    MatrixStore<N> getHessian(List<? extends Number> anArg);

    MatrixStore<N> getHessian(final MatrixStore<N> anArg);

    MatrixStore<N> getHessian(Number[] anArg);

    N invoke(Access1D<?> anArg);

    N invoke(double[] anArg);

    N invoke(List<? extends Number> anArg);

    N invoke(final MatrixStore<N> anArg);

    N invoke(Number[] anArg);

}
