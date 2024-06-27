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
package org.ojalgo.matrix.jama;

import java.util.List;

import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.function.implementation.FunctionSet;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.MatrixBuilder;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.PrimitiveScalar;

/**
 * Implements both {@linkplain BasicMatrix.Factory} and
 * {@linkplain PhysicalStore.Factory}, and creates
 * {@linkplain JamaMatrix} instances.
 *
 * @author apete
 */
public final class JamaFactory extends Object implements BasicMatrix.Factory<JamaMatrix>, PhysicalStore.Factory<Double, JamaMatrix> {

    JamaFactory() {
        super();
    }

    public JamaMatrix columns(final Access1D<?>... aSource) {

        final int tmpRowDim = aSource[0].size();
        final int tmpColDim = aSource.length;

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        Access1D<?> tmpColumn;
        for (int j = 0; j < tmpColDim; j++) {
            tmpColumn = aSource[j];
            for (int i = 0; i < tmpRowDim; i++) {
                retVal[i][j] = tmpColumn.doubleValue(i);
            }
        }

        return new JamaMatrix(retVal);
    }

    public JamaMatrix columns(final double[]... aSource) {

        final int tmpRowDim = aSource[0].length;
        final int tmpColDim = aSource.length;

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        double[] tmpColumn;
        for (int j = 0; j < tmpColDim; j++) {
            tmpColumn = aSource[j];
            for (int i = 0; i < tmpRowDim; i++) {
                retVal[i][j] = tmpColumn[i];
            }
        }

        return new JamaMatrix(retVal);
    }

    public JamaMatrix columns(final List<? extends Number>... aSource) {

        final int tmpRowDim = aSource[0].size();
        final int tmpColDim = aSource.length;

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        List<? extends Number> tmpColumn;
        for (int j = 0; j < tmpColDim; j++) {
            tmpColumn = aSource[j];
            for (int i = 0; i < tmpRowDim; i++) {
                retVal[i][j] = tmpColumn.get(i).doubleValue();
            }
        }

        return new JamaMatrix(retVal);
    }

    public JamaMatrix columns(final Number[]... aSource) {

        final int tmpRowDim = aSource[0].length;
        final int tmpColDim = aSource.length;

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        Number[] tmpColumn;
        for (int j = 0; j < tmpColDim; j++) {
            tmpColumn = aSource[j];
            for (int i = 0; i < tmpRowDim; i++) {
                retVal[i][j] = tmpColumn[i].doubleValue();
            }
        }

        return new JamaMatrix(retVal);
    }

    public JamaMatrix conjugate(final Access2D<?> aSource) {
        return this.transpose(aSource);
    }

    public JamaMatrix copy(final Access2D<?> aSource) {

        final int tmpRowDim = aSource.getRowDim();
        final int tmpColDim = aSource.getColDim();

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        for (int i = 0; i < tmpRowDim; i++) {
            for (int j = 0; j < tmpColDim; j++) {
                retVal[i][j] = aSource.doubleValue(i, j);
            }
        }

        return new JamaMatrix(retVal);
    }

    public AggregatorCollection<Double> getAggregatorCollection() {
        return PrimitiveAggregator.getCollection();
    }

    @SuppressWarnings("unchecked")
    public MatrixBuilder<Double> getBuilder(final int aLength) {
        return this.getBuilder(aLength, 1);
    }

    @SuppressWarnings("unchecked")
    public MatrixBuilder<Double> getBuilder(final int aRowDim, final int aColDim) {

        return new MatrixBuilder<Double>(this, aRowDim, aColDim) {

            @Override
            public BasicMatrix build() {
                return new JamaMatrix(this.getPhysicalStore());
            }
        };
    }

    public FunctionSet<Double> getFunctionSet() {
        return PrimitiveFunction.getSet();
    }

    public Double getNumber(final double aNmbr) {
        return aNmbr;
    }

    public Double getNumber(final Number aNmbr) {
        return aNmbr.doubleValue();
    }

    public PrimitiveScalar getStaticOne() {
        return PrimitiveScalar.ONE;
    }

    public PrimitiveScalar getStaticZero() {
        return PrimitiveScalar.ZERO;
    }

    public SimpleArray<Double> makeArray(final int aLength) {
        return SimpleArray.makePrimitive(aLength);
    }

    public JamaMatrix makeEye(final int aRowDim, final int aColDim) {

        final JamaMatrix retVal = this.makeZero(aRowDim, aColDim);

        retVal.fillDiagonal(0, 0, this.getStaticOne().getNumber());

        return retVal;
    }

    public Householder<Double> makeHouseholder(final int aLength) {
        return new Householder.Primitive(aLength);
    }

    public JamaMatrix makeRandom(final int aRowDim, final int aColDim, final RandomNumber aRndm) {

        final double[][] retVal = new double[aRowDim][aColDim];

        for (int i = 0; i < aRowDim; i++) {
            for (int j = 0; j < aColDim; j++) {
                retVal[i][j] = aRndm.doubleValue();
            }
        }

        return new JamaMatrix(retVal);
    }

    public Rotation<Double> makeRotation(final int aLow, final int aHigh, final double aCos, final double aSin) {
        return new Rotation.Primitive(aLow, aHigh, aCos, aSin);
    }

    public Rotation<Double> makeRotation(final int aLow, final int aHigh, final Double aCos, final Double aSin) {
        return new Rotation.Primitive(aLow, aHigh, aCos, aSin);
    }

    public JamaMatrix makeZero(final int aRowDim, final int aColDim) {
        return new JamaMatrix(new double[aRowDim][aColDim]);
    }

    public JamaMatrix rows(final Access1D<?>... aSource) {

        final int tmpRowDim = aSource.length;
        final int tmpColDim = aSource[0].size();

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        Access1D<?> tmpSource;
        double[] tmpDestination;
        for (int i = 0; i < tmpRowDim; i++) {
            tmpSource = aSource[i];
            tmpDestination = retVal[i];
            for (int j = 0; j < tmpColDim; j++) {
                tmpDestination[j] = tmpSource.doubleValue(j);
            }
        }

        return new JamaMatrix(retVal);
    }

    public JamaMatrix rows(final double[]... aSource) {

        final int tmpRowDim = aSource.length;
        final int tmpColDim = aSource[0].length;

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        double[] tmpSource;
        double[] tmpDestination;
        for (int i = 0; i < tmpRowDim; i++) {
            tmpSource = aSource[i];
            tmpDestination = retVal[i];
            for (int j = 0; j < tmpColDim; j++) {
                tmpDestination[j] = tmpSource[j];
            }
        }

        return new JamaMatrix(retVal);
    }

    public JamaMatrix rows(final List<? extends Number>... aSource) {

        final int tmpRowDim = aSource.length;
        final int tmpColDim = aSource[0].size();

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        List<? extends Number> tmpSource;
        double[] tmpDestination;
        for (int i = 0; i < tmpRowDim; i++) {
            tmpSource = aSource[i];
            tmpDestination = retVal[i];
            for (int j = 0; j < tmpColDim; j++) {
                tmpDestination[j] = tmpSource.get(j).doubleValue();
            }
        }

        return new JamaMatrix(retVal);
    }

    public JamaMatrix rows(final Number[]... aSource) {

        final int tmpRowDim = aSource.length;
        final int tmpColDim = aSource[0].length;

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        Number[] tmpSource;
        double[] tmpDestination;
        for (int i = 0; i < tmpRowDim; i++) {
            tmpSource = aSource[i];
            tmpDestination = retVal[i];
            for (int j = 0; j < tmpColDim; j++) {
                tmpDestination[j] = tmpSource[j].doubleValue();
            }
        }

        return new JamaMatrix(retVal);
    }

    public PrimitiveScalar toScalar(final double aNmbr) {
        return new PrimitiveScalar(aNmbr);
    }

    public PrimitiveScalar toScalar(final Number aNmbr) {
        return new PrimitiveScalar(aNmbr);
    }

    public JamaMatrix transpose(final Access2D<?> aSource) {

        final int tmpRowDim = aSource.getColDim();
        final int tmpColDim = aSource.getRowDim();

        final double[][] retVal = new double[tmpRowDim][tmpColDim];

        for (int i = 0; i < tmpRowDim; i++) {
            for (int j = 0; j < tmpColDim; j++) {
                retVal[i][j] = aSource.doubleValue(j, i);
            }
        }

        return new JamaMatrix(retVal);
    }

}
