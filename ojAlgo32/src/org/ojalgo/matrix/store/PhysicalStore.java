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

import java.io.Serializable;
import java.util.List;

import org.ojalgo.access.Access2D;
import org.ojalgo.access.Factory2D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.function.implementation.FunctionSet;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.scalar.Scalar;

/**
 * <p>
 * PhysicalStore:s, as opposed to MatrixStore:s, are mutable. The vast
 * majorty of the methods defined here return void and none return
 * {@linkplain PhysicalStore} or {@linkplain MatrixStore}.
 * </p><p>
 * This interface and its implementations are central to ojAlgo.
 * </p>
 *
 * @author apete
 */
public interface PhysicalStore<N extends Number> extends MatrixStore<N> {

    public static interface Factory<N extends Number, I extends PhysicalStore<N>> extends Factory2D<I>, Serializable {

        I conjugate(Access2D<?> aSource);

        AggregatorCollection<N> getAggregatorCollection();

        FunctionSet<N> getFunctionSet();

        N getNumber(double aNmbr);

        N getNumber(Number aNmbr);

        Scalar<N> getStaticOne();

        Scalar<N> getStaticZero();

        SimpleArray<N> makeArray(int aLength);

        Householder<N> makeHouseholder(int aLength);

        Rotation<N> makeRotation(int aLow, int aHigh, double aCos, double aSin);

        Rotation<N> makeRotation(int aLow, int aHigh, N aCos, N aSin);

        Scalar<N> toScalar(double aNmbr);

        Scalar<N> toScalar(Number aNmbr);

        I transpose(Access2D<?> aSource);

    }

    /**
     * @return The elements of the physical store as a fixed size
     * (1 dimensional) list. The elements may be accessed either row or
     * colomn major.
     */
    List<N> asList();

    /**
     * <p>
     * <b>c</b>olumn <b>a</b> * <b>x</b> <b>p</b>lus <b>y</b>
     * </p>
     * [this(*,aColY)] = aSclrA [this(*,aColX)] + [this(*,aColY)]
     * @deprecated v32 Let me know if you need this
     */
    @Deprecated
    void caxpy(final N aSclrA, final int aColX, final int aColY, final int aFirstRow);

    void exchangeColumns(int aColA, int aColB);

    void exchangeRows(int aRowA, int aRowB);

    void fillAll(N aNmbr);

    void fillByMultiplying(final MatrixStore<N> aLeftStore, final MatrixStore<N> aRightStore);

    void fillColumn(int aRow, int aCol, N aNmbr);

    void fillDiagonal(int aRow, int aCol, N aNmbr);

    void fillMatching(Access2D<? extends Number> aSource2D);

    /**
     * <p>
     * Will replace the elements of [this] with the results of element
     * wise invocation of the input binary funtion:
     * </p>
     * <code>this(i,j) = aFunc.invoke(aLeftArg(i,j),aRightArg(i,j))</code>
     */
    void fillMatching(MatrixStore<N> aLeftArg, BinaryFunction<N> aFunc, MatrixStore<N> aRightArg);

    /**
     * <p>
     * Will replace the elements of [this] with the results of element
     * wise invocation of the input binary funtion:
     * </p>
     * <code>this(i,j) = aFunc.invoke(aLeftArg(i,j),aRightArg))</code>
     */
    void fillMatching(MatrixStore<N> aLeftArg, BinaryFunction<N> aFunc, N aRightArg);

    /**
     * <p>
     * Will replace the elements of [this] with the results of element
     * wise invocation of the input binary funtion:
     * </p>
     * <code>this(i,j) = aFunc.invoke(aLeftArg,aRightArg(i,j))</code>
     */
    void fillMatching(N aLeftArg, BinaryFunction<N> aFunc, MatrixStore<N> aRightArg);

    void fillRow(int aRow, int aCol, N aNmbr);

    /**
     * <p>
     * <b>m</b>atrix <b>a</b> * <b>x</b> <b>p</b>lus <b>y</b>
     * </p>
     * [this] = aSclrA [aMtrxX] + [this]
     * @deprecated v32 Let me know if you need this
     */
    @Deprecated
    void maxpy(final N aSclrA, final MatrixStore<N> aMtrxX);

    void modifyAll(UnaryFunction<N> aFunc);

    void modifyColumn(int aRow, int aCol, UnaryFunction<N> aFunc);

    void modifyDiagonal(int aRow, int aCol, UnaryFunction<N> aFunc);

    void modifyOne(int aRow, int aCol, UnaryFunction<N> aFunc);

    void modifyRow(int aRow, int aCol, UnaryFunction<N> aFunc);

    /**
     * <p>
     * <b>r</b>ow <b>a</b> * <b>x</b> <b>p</b>lus <b>y</b>
     * </p>
     * [this(aRowY,*)] = aSclrA [this(aRowX,*)] + [this(aRowY,*)]
     * @deprecated v32 Let me know if you need this
     */
    @Deprecated
    void raxpy(final N aSclrA, final int aRowX, final int aRowY, final int aFirstCol);

    void set(int aRow, int aCol, double aNmbr);

    void set(int aRow, int aCol, N aNmbr);

    void transformLeft(Householder<N> aTransf, int aFirstCol);

    /**
     * <p>
     * As in {@link MatrixStore#multiplyLeft(MatrixStore)} where the
     * left/parameter matrix is a plane rotation.
     * </p><p>
     * Multiplying by a plane rotation from the left means that [this]
     * gets two of its rows updated to new combinations of those two (current)
     * rows.
     * </p><p>
     * There are two ways to transpose/invert a rotation. Either you negate
     * the angle or you interchange the two indeces that define the rotation
     * plane.
     * </p>
     * @see #transformRight(Rotation)
     */
    void transformLeft(Rotation<N> aTransf);

    void transformRight(Householder<N> aTransf, int aFirstRow);

    /**
     * <p>
     * As in {@link MatrixStore#multiplyRight(MatrixStore)} where the
     * right/parameter matrix is a plane rotation.
     * </p><p>
     * Multiplying by a plane rotation from the right means that [this]
     * gets two of its columns updated to new combinations of those two
     * (current) columns.
     * </p><p>
     * There result is undefined if the two input indeces are the same
     * (in which case the rotation plane is undefined).
     * </p>
     * @see #transformLeft(Rotation)
     */
    void transformRight(Rotation<N> aTransf);

}
