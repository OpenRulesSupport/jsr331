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

import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;

/**
 * [A] = [V][D][V]<sup>-1</sup> ([A][V] = [V][D])
 * <ul>
 * <li>[A] = any square matrix.</li>
 * <li>[V] = contains the eigenvectors as columns.</li>
 * <li>[D] = a diagonal matrix with the eigenvalues on the diagonal
 * (possibly in blocks).</li>
 * </ul>
 * <p>
 * [A] is normal if [A][A]<sup>H</sup> = [A]<sup>H</sup>[A], and
 * [A] is normal if and only if there exists a unitary matrix [Q] such
 * that [A] = [Q][D][Q]<sup>H</sup>. Hermitian matrices are normal.
 * </p>
 * <p>
 * [V] and [D] can always be calculated in the sense that they will
 * satisfy [A][V] = [V][D], but it is not always possible to calculate
 * [V]<sup>-1</sup>. (Check the rank and/or the condition number of [V]
 * to determine the validity of [V][D][V]<sup>-1</sup>.)
 * </p>
 * 
 * @author apete
 */
public interface Eigenvalue<N extends Number> extends MatrixDecomposition<N> {

    /**
     * @param aMtrx A matrix to decompose
     * @param eigenvaluesOnly No need to calculate eigenvectors
     * @return true/false if the computation succeeded or not
     */
    boolean compute(Access2D<?> aMtrx, boolean eigenvaluesOnly);

    /**
     * The only requirements on [D] are that it should contain the eigenvalues
     * and that [A][V] = [V][D]. The ordering of the eigenvalues is not specified.
     * <ul><li>
     * If [A] is real and symmetric then [D] is (purely) diagonal with real
     * eigenvalues.
     * </li><li>
     * If [A] is real but not symmetric then [D] is block-diagonal with real
     * eigenvalues in 1-by-1 blocks and complex eigenvalues in 2-by-2 blocks.
     * </li><li>
     * If [A] is complex then [D] is (purely) diagonal with complex eigenvalues.
     * </li></ul>
     * 
     * @return The (block) diagonal eigenvalue matrix.
     */
    MatrixStore<N> getD();

    /**
     * <p>
     * A matrix' determinant is the product of its eigenvalues.
     * </p>
     * 
     * @return The matrix' determinant
     */
    ComplexNumber getDeterminant();

    /**
     * <p>
     * Even for real matrices the eigenvalues are potentially complex numbers.
     * Typically they need to be expressed as complex numbers when [A] is not
     * symmetric.
     * </p><p>
     * The eigenvalues in this array should be ordered in descending order -
     * largest (modulus) first.
     * </p>
     * 
     * @return The eigenvalues in an ordered array.
     */
    Array1D<ComplexNumber> getEigenvalues();

    /**
     * A matrix' trace is the sum of the diagonal elements. It is also the sum
     * of the eigenvalues. This method should return the sum of the eigenvalues.
     * 
     * @return The matrix' trace
     */
    ComplexNumber getTrace();

    /**
     * The columns of [V] represent the eigenvectors of [A] in the sense that
     * [A][V] = [V][D].
     * 
     * @return The eigenvector matrix.
     */
    MatrixStore<N> getV();

    /**
     * If [A] is hermitian then [V][D][V]<sup>-1</sup> becomes [Q][D][Q]<sup>H</sup>...
     */
    boolean isHermitian();

    boolean isOrdered();

    /**
     * @deprecated v32 Use {@link #isHermitian()} instead
     */
    @Deprecated
    boolean isSymmetric();

}
