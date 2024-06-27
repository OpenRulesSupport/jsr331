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

import org.ojalgo.matrix.store.MatrixStore;

/**
 * A general matrix [A] can be factorized by similarity
 * transformations into the form [A]=[Q1][D][Q2]<sup>-1</sup> where:
 * <ul>
 * <li>[A] (m-by-n) is any, real or complex, matrix</li>
 * <li>[D] (r-by-r) or (m-by-n) is, upper or lower, bidiagonal</li>
 * <li>[Q1] (m-by-r) or (m-by-m) is orthogonal</li>
 * <li>[Q2] (n-by-r) or (n-by-n) is orthogonal</li>
 * <li>r = min(m,n)</li>
 * </ul>
 *
 * @author apete
 */
public interface Bidiagonal<N extends Number> extends MatrixDecomposition<N> {

    MatrixStore<N> getD();

    MatrixStore<N> getQ1();

    MatrixStore<N> getQ2();

    boolean isUpper();

}
