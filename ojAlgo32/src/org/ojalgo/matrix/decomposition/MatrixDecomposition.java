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
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.type.context.NumberContext;

/**
 * Notation used to describe the various matrix decompositions:
 * <ul>
 * <li>[A] could be any matrix. (The matrix to decompose.)</li>
 * <li>[A]<sup>-1</sup> is the inverse of [A].</li>
 * <li>[A]<sup>T</sup> is the transpose of [A].</li>
 * <li>[A]<sup>H</sup> is the conjugate transpose of [A]. [A]<sup>H</sup> is equilvalent to [A]<sup>T</sup> if the elements are all real.</li>
 * <li>[D] is a diagonal matrix. Possibly bi-, tri- or block-diagonal.</li>
 * <li>[H] is an, upper or lower, Hessenberg matrix.</il>
 * <li>[I] is an identity matrix - obvioulsly orthogonal/unitary.</li>
 * <li>[L] is a lower (left) triangular matrix.</li>
 * <li>[P] is a permutation matrix - an identity matrix with interchanged rows or columns - and orthogonal/unitary.</li>
 * <li>[Q] is an orthogonal/unitary matrix. [Q]<sup>-1</sup> = [Q]<sup>H</sup>, and with real matrices = [Q]<sup>T</sup>.</li>
 * <li>[R] is a right (upper) tringular matrix. It is equivalent to [U].</li>
 * <li>[U] is an upper (right) triangular matrix. It is equivalent to [R].</li>
 * <li>[V] is an eigenvector matrix. The columns are the eigenvectors</il>
 * 
 * </ul>
 * 
 * @author apete
 */
public interface MatrixDecomposition<N extends Number> {

    /**
     * @param aMtrx A matrix to decompose
     * @return true if the computation suceeded; false if not
     */
    boolean compute(Access2D<?> aMtrx);

    boolean equals(MatrixDecomposition<N> aDecomp, NumberContext aCntxt);

    boolean equals(MatrixStore<N> aMtrx, NumberContext aCntxt);

    /**
     * The output must be a "right inverse" and a "generalised inverse".
     * 
     * @see BasicMatrix#invert()
     */
    MatrixStore<N> getInverse();

    /**
     * <p>
     * Implementiong this method is optional.
     * </p><p>
     * Exactly how a specific implementation makes use of <code>preallocated</code>
     * is not specified by this interface. It must be documented for each
     * implementation.
     * </p><p>
     * Should produce the same results as calling {@link #getInverse()}.
     * </p>
     * @param preallocated Preallocated memory for the results, possibly some
     * intermediate results. You must assume this is modified, but you
     * cannot assume it will contain the full/final/correct solution.
     * @return The inverse
     */
    MatrixStore<N> getInverse(DecompositionStore<N> preallocated);

    /**
     * @return true if computation has been attemped; false if not.
     * @see #compute(Access2D)
     * @see #isSolvable()
     */
    boolean isComputed();

    /**
     * @return True if the implementation generates a full sized decomposition.
     */
    boolean isFullSize();

    /**
     * @return true if it is ok to call {@linkplain #solve(MatrixStore)}
     * (computation was successful); false if not
     * @see #solve(MatrixStore)
     * @see #isComputed()
     */
    boolean isSolvable();

    MatrixStore<N> reconstruct();

    /**
     * Delete computed results, and resets attributes to default values
     */
    void reset();

    /**
     * [A][X]=[B] or [this][return]=[aRHS]
     */
    MatrixStore<N> solve(MatrixStore<N> aRHS);

    /**
     * <p>
     * Implementiong this method is optional.
     * </p><p>
     * Exactly how a specific implementation makes use of <code>preallocated</code>
     * is not specified by this interface. It must be documented for each
     * implementation.
     * </p><p>
     * Should produce the same results as calling {@link #solve(MatrixStore)}.
     * </p>
     * @param aRHS The Right Hand Side, wont be modfied
     * @param preallocated Preallocated memory for the results, possibly some
     * intermediate results. You must assume this is modified, but you
     * cannot assume it will contain the full/final/correct solution.
     * @return The solution
     */
    MatrixStore<N> solve(MatrixStore<N> aRHS, DecompositionStore<N> preallocated);

}
