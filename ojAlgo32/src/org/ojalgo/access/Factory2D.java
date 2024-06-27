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
package org.ojalgo.access;

import java.util.List;

import org.ojalgo.random.RandomNumber;

public interface Factory2D<I> {

    I columns(Access1D<?>... aSource);

    I columns(double[]... aSource);

    I columns(List<? extends Number>... aSource);

    I columns(Number[]... aSource);

    I copy(Access2D<?> aSource);

    I makeEye(int aRowDim, int aColDim);

    I makeRandom(int aRowDim, int aColDim, RandomNumber aRndm);

    I makeZero(int aRowDim, int aColDim);

    I rows(Access1D<?>... aSource);

    I rows(double[]... aSource);

    I rows(List<? extends Number>... aSource);

    I rows(Number[]... aSource);

}
