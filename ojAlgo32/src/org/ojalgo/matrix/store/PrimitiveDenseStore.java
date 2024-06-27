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

import static org.ojalgo.constant.PrimitiveMath.*;
import static org.ojalgo.function.implementation.PrimitiveFunction.*;

import java.util.List;

import org.ojalgo.OjAlgoUtils;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.Array2D;
import org.ojalgo.array.PrimitiveArray;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.concurrent.DivideAndConquer;
import org.ojalgo.concurrent.DivideAndMerge;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.function.implementation.FunctionSet;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.machine.JavaType;
import org.ojalgo.machine.MemoryEstimator;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.operation.*;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.PrimitiveScalar;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * A {@linkplain Double} (actually double) implementation of {@linkplain PhysicalStore}.
 *
 * @author apete
 */
public final class PrimitiveDenseStore extends PrimitiveArray implements PhysicalStore<Double>, DecompositionStore<Double> {

    public static final DecompositionStore.Factory<Double, PrimitiveDenseStore> FACTORY = new DecompositionStore.Factory<Double, PrimitiveDenseStore>() {

        public PrimitiveDenseStore columns(final Access1D<?>... aSource) {

            final int tmpRowDim = aSource[0].size();
            final int tmpColDim = aSource.length;

            final double[] tmpData = new double[tmpRowDim * tmpColDim];

            Access1D<?> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = tmpColumn.doubleValue(i);
                }
            }

            return new PrimitiveDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public PrimitiveDenseStore columns(final double[]... aSource) {

            final int tmpRowDim = aSource[0].length;
            final int tmpColDim = aSource.length;

            final double[] tmpData = new double[tmpRowDim * tmpColDim];

            double[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = tmpColumn[i];
                }
            }

            return new PrimitiveDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public PrimitiveDenseStore columns(final List<? extends Number>... aSource) {

            final int tmpRowDim = aSource[0].size();
            final int tmpColDim = aSource.length;

            final double[] tmpData = new double[tmpRowDim * tmpColDim];

            List<? extends Number> tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = tmpColumn.get(i).doubleValue();
                }
            }

            return new PrimitiveDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public PrimitiveDenseStore columns(final Number[]... aSource) {

            final int tmpRowDim = aSource[0].length;
            final int tmpColDim = aSource.length;

            final double[] tmpData = new double[tmpRowDim * tmpColDim];

            Number[] tmpColumn;
            for (int j = 0; j < tmpColDim; j++) {
                tmpColumn = aSource[j];
                for (int i = 0; i < tmpRowDim; i++) {
                    tmpData[i + (tmpRowDim * j)] = tmpColumn[i].doubleValue();
                }
            }

            return new PrimitiveDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public PrimitiveDenseStore conjugate(final Access2D<?> aSource) {
            return this.transpose(aSource);
        }

        public PrimitiveDenseStore copy(final Access2D<?> aSource) {

            final PrimitiveDenseStore retVal = new PrimitiveDenseStore(aSource.getRowDim(), aSource.getColDim());

            retVal.fillMatching(aSource);

            return retVal;
        }

        public AggregatorCollection<Double> getAggregatorCollection() {
            return PrimitiveAggregator.getCollection();
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

        public SimpleArray.Primitive makeArray(final int aLength) {
            return SimpleArray.makePrimitive(aLength);
        }

        public PrimitiveDenseStore makeEye(final int aRowDim, final int aColDim) {

            final PrimitiveDenseStore retVal = this.makeZero(aRowDim, aColDim);

            retVal.myUtility.fillDiagonal(0, 0, this.getStaticOne().getNumber());

            return retVal;
        }

        public Householder.Primitive makeHouseholder(final int aLength) {
            return new Householder.Primitive(aLength);
        }

        public PrimitiveDenseStore makeRandom(final int aRowDim, final int aColDim, final RandomNumber aRndm) {

            final int tmpRowDim = aRowDim;
            final int tmpColDim = aColDim;

            final int tmpLength = tmpRowDim * tmpColDim;

            final double[] tmpData = new double[tmpLength];

            for (int i = 0; i < tmpLength; i++) {
                tmpData[i] = aRndm.doubleValue();
            }

            return new PrimitiveDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public Rotation.Primitive makeRotation(final int aLow, final int aHigh, final double aCos, final double aSin) {
            return new Rotation.Primitive(aLow, aHigh, aCos, aSin);
        }

        public Rotation.Primitive makeRotation(final int aLow, final int aHigh, final Double aCos, final Double aSin) {
            return this.makeRotation(aLow, aHigh, aCos != null ? aCos.doubleValue() : Double.NaN, aSin != null ? aSin.doubleValue() : Double.NaN);
        }

        public PrimitiveDenseStore makeZero(final int aRowDim, final int aColDim) {
            return new PrimitiveDenseStore(aRowDim, aColDim);
        }

        public PrimitiveDenseStore rows(final Access1D<?>... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].size();

            final double[] tmpData = new double[tmpRowDim * tmpColDim];

            Access1D<?> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = tmpRow.doubleValue(j);
                }
            }

            return new PrimitiveDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public PrimitiveDenseStore rows(final double[]... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].length;

            final double[] tmpData = new double[tmpRowDim * tmpColDim];

            double[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = tmpRow[j];
                }
            }

            return new PrimitiveDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public PrimitiveDenseStore rows(final List<? extends Number>... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].size();

            final double[] tmpData = new double[tmpRowDim * tmpColDim];

            List<? extends Number> tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = tmpRow.get(j).doubleValue();
                }
            }

            return new PrimitiveDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public PrimitiveDenseStore rows(final Number[]... aSource) {

            final int tmpRowDim = aSource.length;
            final int tmpColDim = aSource[0].length;

            final double[] tmpData = new double[tmpRowDim * tmpColDim];

            Number[] tmpRow;
            for (int i = 0; i < tmpRowDim; i++) {
                tmpRow = aSource[i];
                for (int j = 0; j < tmpColDim; j++) {
                    tmpData[i + (tmpRowDim * j)] = tmpRow[j].doubleValue();
                }
            }

            return new PrimitiveDenseStore(tmpRowDim, tmpColDim, tmpData);
        }

        public PrimitiveScalar toScalar(final double aNmbr) {
            return new PrimitiveScalar(aNmbr);
        }

        public PrimitiveScalar toScalar(final Number aNmbr) {
            return new PrimitiveScalar(aNmbr.doubleValue());
        }

        public PrimitiveDenseStore transpose(final Access2D<?> aSource) {

            MatrixStore<Double> tmpSource = new WrapperStore<Double>(this, aSource);
            tmpSource = new TransposedStore<Double>(tmpSource);

            final PrimitiveDenseStore retVal = new PrimitiveDenseStore(tmpSource.getRowDim(), tmpSource.getColDim());

            retVal.fillMatching(tmpSource);

            return retVal;
        }
    };

    static final long ELEMENT_SIZE = JavaType.DOUBLE.memory();
    static final long SHALLOW_SIZE = MemoryEstimator.estimateObject(PrimitiveDenseStore.class);

    static Householder.Primitive cast(final Householder<Double> aTransf) {
        if (aTransf instanceof Householder.Primitive) {
            return (Householder.Primitive) aTransf;
        } else if (aTransf instanceof DecompositionStore.HouseholderReference<?>) {
            return ((DecompositionStore.HouseholderReference<Double>) aTransf).getPrimitiveWorker().copy(aTransf);
        } else {
            return new Householder.Primitive(aTransf);
        }
    }

    static PrimitiveDenseStore cast(final MatrixStore<Double> aStore) {
        if (aStore instanceof PrimitiveDenseStore) {
            return (PrimitiveDenseStore) aStore;
        } else {
            return FACTORY.copy(aStore);
        }
    }

    static Rotation.Primitive cast(final Rotation<Double> aTransf) {
        if (aTransf instanceof Rotation.Primitive) {
            return (Rotation.Primitive) aTransf;
        } else {
            return new Rotation.Primitive(aTransf);
        }
    }

    static void doAfter(final double[] aMtrxH, final double[] aMtrxV, final double[] tmpMainDiagonal, final double[] tmpOffDiagonal, double r, double s,
            double z, final double aNorm1) {

        final int tmpDiagDim = (int) Math.sqrt(aMtrxH.length);
        final int tmpDiagDimMinusOne = tmpDiagDim - 1;

        //        BasicLogger.logDebug("r={}, s={}, z={}", r, s, z);

        double p;
        double q;
        double t;
        double w;
        double x;
        double y;

        for (int ij = tmpDiagDimMinusOne; ij >= 0; ij--) {

            p = tmpMainDiagonal[ij];
            q = tmpOffDiagonal[ij];

            // Real vector
            if (q == 0) {
                int l = ij;
                aMtrxH[ij + (tmpDiagDim * ij)] = 1.0;
                for (int i = ij - 1; i >= 0; i--) {
                    w = aMtrxH[i + (tmpDiagDim * i)] - p;
                    r = PrimitiveMath.ZERO;
                    for (int j = l; j <= ij; j++) {
                        r = r + (aMtrxH[i + (tmpDiagDim * j)] * aMtrxH[j + (tmpDiagDim * ij)]);
                    }
                    if (tmpOffDiagonal[i] < PrimitiveMath.ZERO) {
                        z = w;
                        s = r;
                    } else {
                        l = i;
                        if (tmpOffDiagonal[i] == PrimitiveMath.ZERO) {
                            if (w != PrimitiveMath.ZERO) {
                                aMtrxH[i + (tmpDiagDim * ij)] = -r / w;
                            } else {
                                aMtrxH[i + (tmpDiagDim * ij)] = -r / (PrimitiveMath.MACHINE_DOUBLE_ERROR * aNorm1);
                            }

                            // Solve real equations
                        } else {
                            x = aMtrxH[i + (tmpDiagDim * (i + 1))];
                            y = aMtrxH[(i + 1) + (tmpDiagDim * i)];
                            q = ((tmpMainDiagonal[i] - p) * (tmpMainDiagonal[i] - p)) + (tmpOffDiagonal[i] * tmpOffDiagonal[i]);
                            t = ((x * s) - (z * r)) / q;
                            aMtrxH[i + (tmpDiagDim * ij)] = t;
                            if (Math.abs(x) > Math.abs(z)) {
                                aMtrxH[(i + 1) + (tmpDiagDim * ij)] = (-r - (w * t)) / x;
                            } else {
                                aMtrxH[(i + 1) + (tmpDiagDim * ij)] = (-s - (y * t)) / z;
                            }
                        }

                        // Overflow control
                        t = Math.abs(aMtrxH[i + (tmpDiagDim * ij)]);
                        if (((PrimitiveMath.MACHINE_DOUBLE_ERROR * t) * t) > 1) {
                            for (int j = i; j <= ij; j++) {
                                aMtrxH[j + (tmpDiagDim * ij)] = aMtrxH[j + (tmpDiagDim * ij)] / t;
                            }
                        }
                    }
                }

                // Complex vector
            } else if (q < 0) {
                int l = ij - 1;

                // Last vector component imaginary so matrix is triangular
                if (Math.abs(aMtrxH[ij + (tmpDiagDim * (ij - 1))]) > Math.abs(aMtrxH[(ij - 1) + (tmpDiagDim * ij)])) {
                    aMtrxH[(ij - 1) + (tmpDiagDim * (ij - 1))] = q / aMtrxH[ij + (tmpDiagDim * (ij - 1))];
                    aMtrxH[(ij - 1) + (tmpDiagDim * ij)] = -(aMtrxH[ij + (tmpDiagDim * ij)] - p) / aMtrxH[ij + (tmpDiagDim * (ij - 1))];
                } else {

                    final ComplexNumber tmpX = ComplexNumber.makeRectangular(PrimitiveMath.ZERO, (-aMtrxH[(ij - 1) + (tmpDiagDim * ij)]));
                    final ComplexNumber tmpY = ComplexNumber.makeRectangular((aMtrxH[(ij - 1) + (tmpDiagDim * (ij - 1))] - p), q);

                    final ComplexNumber tmpZ = tmpX.divide(tmpY);

                    aMtrxH[(ij - 1) + (tmpDiagDim * (ij - 1))] = tmpZ.getReal();
                    aMtrxH[(ij - 1) + (tmpDiagDim * ij)] = tmpZ.getImaginary();
                }
                aMtrxH[ij + (tmpDiagDim * (ij - 1))] = PrimitiveMath.ZERO;
                aMtrxH[ij + (tmpDiagDim * ij)] = 1.0;
                for (int i = ij - 2; i >= 0; i--) {
                    double ra, sa, vr, vi;
                    ra = PrimitiveMath.ZERO;
                    sa = PrimitiveMath.ZERO;
                    for (int j = l; j <= ij; j++) {
                        ra = ra + (aMtrxH[i + (tmpDiagDim * j)] * aMtrxH[j + (tmpDiagDim * (ij - 1))]);
                        sa = sa + (aMtrxH[i + (tmpDiagDim * j)] * aMtrxH[j + (tmpDiagDim * ij)]);
                    }
                    w = aMtrxH[i + (tmpDiagDim * i)] - p;

                    if (tmpOffDiagonal[i] < PrimitiveMath.ZERO) {
                        z = w;
                        r = ra;
                        s = sa;
                    } else {
                        l = i;
                        if (tmpOffDiagonal[i] == 0) {
                            final ComplexNumber tmpX = ComplexNumber.makeRectangular((-ra), (-sa));
                            final ComplexNumber tmpY = ComplexNumber.makeRectangular(w, q);

                            final ComplexNumber tmpZ = tmpX.divide(tmpY);

                            aMtrxH[i + (tmpDiagDim * (ij - 1))] = tmpZ.getReal();
                            aMtrxH[i + (tmpDiagDim * ij)] = tmpZ.getImaginary();
                        } else {

                            // Solve complex equations
                            x = aMtrxH[i + (tmpDiagDim * (i + 1))];
                            y = aMtrxH[(i + 1) + (tmpDiagDim * i)];
                            vr = (((tmpMainDiagonal[i] - p) * (tmpMainDiagonal[i] - p)) + (tmpOffDiagonal[i] * tmpOffDiagonal[i])) - (q * q);
                            vi = (tmpMainDiagonal[i] - p) * 2.0 * q;
                            if ((vr == PrimitiveMath.ZERO) & (vi == PrimitiveMath.ZERO)) {
                                vr = PrimitiveMath.MACHINE_DOUBLE_ERROR * aNorm1 * (Math.abs(w) + Math.abs(q) + Math.abs(x) + Math.abs(y) + Math.abs(z));
                            }

                            final ComplexNumber tmpX = ComplexNumber.makeRectangular((((x * r) - (z * ra)) + (q * sa)), ((x * s) - (z * sa) - (q * ra)));
                            final ComplexNumber tmpY = ComplexNumber.makeRectangular(vr, vi);

                            final ComplexNumber tmpZ = tmpX.divide(tmpY);

                            aMtrxH[i + (tmpDiagDim * (ij - 1))] = tmpZ.getReal();
                            aMtrxH[i + (tmpDiagDim * ij)] = tmpZ.getImaginary();

                            if (Math.abs(x) > (Math.abs(z) + Math.abs(q))) {
                                aMtrxH[(i + 1) + (tmpDiagDim * (ij - 1))] = ((-ra - (w * aMtrxH[i + (tmpDiagDim * (ij - 1))])) + (q * aMtrxH[i
                                        + (tmpDiagDim * ij)]))
                                        / x;
                                aMtrxH[(i + 1) + (tmpDiagDim * ij)] = (-sa - (w * aMtrxH[i + (tmpDiagDim * ij)]) - (q * aMtrxH[i + (tmpDiagDim * (ij - 1))]))
                                        / x;
                            } else {
                                final ComplexNumber tmpX1 = ComplexNumber.makeRectangular((-r - (y * aMtrxH[i + (tmpDiagDim * (ij - 1))])), (-s - (y * aMtrxH[i
                                        + (tmpDiagDim * ij)])));
                                final ComplexNumber tmpY1 = ComplexNumber.makeRectangular(z, q);

                                final ComplexNumber tmpZ1 = tmpX1.divide(tmpY1);

                                aMtrxH[(i + 1) + (tmpDiagDim * (ij - 1))] = tmpZ1.getReal();
                                aMtrxH[(i + 1) + (tmpDiagDim * ij)] = tmpZ1.getImaginary();
                            }
                        }

                        // Overflow control
                        t = Math.max(Math.abs(aMtrxH[i + (tmpDiagDim * (ij - 1))]), Math.abs(aMtrxH[i + (tmpDiagDim * ij)]));
                        if (((PrimitiveMath.MACHINE_DOUBLE_ERROR * t) * t) > 1) {
                            for (int j = i; j <= ij; j++) {
                                aMtrxH[j + (tmpDiagDim * (ij - 1))] = aMtrxH[j + (tmpDiagDim * (ij - 1))] / t;
                                aMtrxH[j + (tmpDiagDim * ij)] = aMtrxH[j + (tmpDiagDim * ij)] / t;
                            }
                        }
                    }
                }
            }
        }

        // Back transformation to get eigenvectors of original matrix
        for (int j = tmpDiagDimMinusOne; j >= 0; j--) {
            for (int i = 0; i <= tmpDiagDimMinusOne; i++) {
                z = PrimitiveMath.ZERO;
                for (int k = 0; k <= j; k++) {
                    z += aMtrxV[i + (tmpDiagDim * k)] * aMtrxH[k + (tmpDiagDim * j)];
                }
                aMtrxV[i + (tmpDiagDim * j)] = z;
            }
        }
    }

    static int doHessenberg(final double[] aMtrxH, final double[] aMtrxV) {

        final int tmpDiagDim = (int) Math.sqrt(aMtrxH.length);
        final int tmpDiagDimMinusTwo = tmpDiagDim - 2;

        final double[] tmpWorkCopy = new double[tmpDiagDim];

        for (int ij = 0; ij < tmpDiagDimMinusTwo; ij++) {

            // Scale column.
            double tmpColNorm1 = PrimitiveMath.ZERO;
            for (int i = ij + 1; i < tmpDiagDim; i++) {
                tmpColNorm1 += Math.abs(aMtrxH[i + (tmpDiagDim * ij)]);
            }

            if (tmpColNorm1 != PrimitiveMath.ZERO) {

                // Compute Householder transformation.
                double tmpInvBeta = PrimitiveMath.ZERO;
                for (int i = tmpDiagDim - 1; i >= (ij + 1); i--) {
                    tmpWorkCopy[i] = aMtrxH[i + (tmpDiagDim * ij)] / tmpColNorm1;
                    tmpInvBeta += tmpWorkCopy[i] * tmpWorkCopy[i];
                }
                double g = Math.sqrt(tmpInvBeta);
                if (tmpWorkCopy[ij + 1] > 0) {
                    g = -g;
                }
                tmpInvBeta = tmpInvBeta - (tmpWorkCopy[ij + 1] * g);
                tmpWorkCopy[ij + 1] = tmpWorkCopy[ij + 1] - g;

                // Apply Householder similarity transformation
                // H = (I-u*u'/h)*H*(I-u*u')/h)
                for (int j = ij + 1; j < tmpDiagDim; j++) {
                    double f = PrimitiveMath.ZERO;
                    for (int i = tmpDiagDim - 1; i >= (ij + 1); i--) {
                        f += tmpWorkCopy[i] * aMtrxH[i + (tmpDiagDim * j)];
                    }
                    f = f / tmpInvBeta;
                    for (int i = ij + 1; i <= (tmpDiagDim - 1); i++) {
                        aMtrxH[i + (tmpDiagDim * j)] -= f * tmpWorkCopy[i];
                    }
                }

                for (int i = 0; i < tmpDiagDim; i++) {
                    double f = PrimitiveMath.ZERO;
                    for (int j = tmpDiagDim - 1; j >= (ij + 1); j--) {
                        f += tmpWorkCopy[j] * aMtrxH[i + (tmpDiagDim * j)];
                    }
                    f = f / tmpInvBeta;
                    for (int j = ij + 1; j < tmpDiagDim; j++) {
                        aMtrxH[i + (tmpDiagDim * j)] -= f * tmpWorkCopy[j];
                    }
                }

                tmpWorkCopy[ij + 1] = tmpColNorm1 * tmpWorkCopy[ij + 1];
                aMtrxH[(ij + 1) + (tmpDiagDim * ij)] = tmpColNorm1 * g;
            }
        }

        //  BasicLogger.logDebug("Jama H", new PrimitiveDenseStore(tmpDiagDim, tmpDiagDim, aMtrxH));

        // Här borde Hessenberg vara klar
        // Nedan börjar uträkningen av Q

        // Accumulate transformations (Algol's ortran).
        for (int ij = tmpDiagDimMinusTwo; ij >= 1; ij--) {
            final int tmpIndex = ij + (tmpDiagDim * (ij - 1));
            if (aMtrxH[tmpIndex] != PrimitiveMath.ZERO) {
                for (int i = ij + 1; i <= (tmpDiagDim - 1); i++) {
                    tmpWorkCopy[i] = aMtrxH[i + (tmpDiagDim * (ij - 1))];
                }
                for (int j = ij; j <= (tmpDiagDim - 1); j++) {
                    double g = PrimitiveMath.ZERO;
                    for (int i = ij; i <= (tmpDiagDim - 1); i++) {
                        g += tmpWorkCopy[i] * aMtrxV[i + (tmpDiagDim * j)];
                    }
                    // Double division avoids possible underflow
                    g = (g / tmpWorkCopy[ij]) / aMtrxH[tmpIndex];
                    for (int i = ij; i <= (tmpDiagDim - 1); i++) {
                        aMtrxV[i + (tmpDiagDim * j)] += g * tmpWorkCopy[i];
                    }
                }
            } else {
                //                BasicLogger.logDebug("Iter V", new PrimitiveDenseStore(tmpDiagDim, tmpDiagDim, aMtrxV));
            }
        }

        //      BasicLogger.logDebug("Jama V", new PrimitiveDenseStore(tmpDiagDim, tmpDiagDim, aMtrxV));

        return tmpDiagDim;
    }

    static void doMultiplyBoth(final double[] aProductArray, final Access2D<?> aLeftStore, final Access2D<?> aRightStore) {

        final int tmpRowDim = aLeftStore.getRowDim();

        if (tmpRowDim > MultiplyBoth.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(MultiplyBoth.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    MultiplyBoth.invoke(aProductArray, aFirst, aLimit, aLeftStore, aRightStore);
                }
            };

            tmpConquerer.divide(0, tmpRowDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            MultiplyBoth.invoke(aProductArray, 0, tmpRowDim, aLeftStore, aRightStore);
        }
    }

    static void doMultiplyLeft(final double[] aProductArray, final MatrixStore<Double> aLeftStore, final double[] aRightArray) {

        final int tmpRowDim = aLeftStore.getRowDim();

        if (tmpRowDim > MultiplyLeft.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(MultiplyLeft.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    MultiplyLeft.invoke(aProductArray, aFirst, aLimit, aLeftStore, aRightArray);
                }
            };

            tmpConquerer.divide(0, tmpRowDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            MultiplyLeft.invoke(aProductArray, 0, tmpRowDim, aLeftStore, aRightArray);
        }
    }

    static void doMultiplyRight(final double[] aProductArray, final double[] aLeftArray, final MatrixStore<Double> aRightStore) {

        final int tmpColDim = aRightStore.getColDim();

        if (tmpColDim > MultiplyRight.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(MultiplyRight.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    MultiplyRight.invoke(aProductArray, aFirst, aLimit, aLeftArray, aRightStore);
                }
            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            MultiplyRight.invoke(aProductArray, 0, tmpColDim, aLeftArray, aRightStore);
        }
    }

    static double[][] doSchur(final double[] aMtrxH, final double[] aMtrxV, final boolean allTheWay) {

        final int tmpDiagDim = (int) Math.sqrt(aMtrxH.length);
        final int tmpDiagDimMinusOne = tmpDiagDim - 1;

        // Store roots isolated by balanc and compute matrix norm
        double tmpVal = PrimitiveMath.ZERO;
        for (int j = 0; j < tmpDiagDim; j++) {
            for (int i = Math.min(j + 1, tmpDiagDim - 1); i >= 0; i--) {
                tmpVal += Math.abs(aMtrxH[i + (tmpDiagDim * j)]);
            }
        }
        final double tmpNorm1 = tmpVal;

        final double[] tmpMainDiagonal = new double[tmpDiagDim];
        final double[] tmpOffDiagonal = new double[tmpDiagDim];

        double exshift = PrimitiveMath.ZERO;
        double p = 0, q = 0, r = 0, s = 0, z = 0;

        double w, x, y;
        // Outer loop over eigenvalue index
        int tmpIterCount = 0;
        int tmpMainIterIndex = tmpDiagDimMinusOne;
        while (tmpMainIterIndex >= 0) {

            // Look for single small sub-diagonal element
            int l = tmpMainIterIndex;
            while (l > 0) {
                s = Math.abs(aMtrxH[(l - 1) + (tmpDiagDim * (l - 1))]) + Math.abs(aMtrxH[l + (tmpDiagDim * l)]);
                if (s == PrimitiveMath.ZERO) {
                    s = tmpNorm1;
                }
                if (Math.abs(aMtrxH[l + (tmpDiagDim * (l - 1))]) < (PrimitiveMath.MACHINE_DOUBLE_ERROR * s)) {
                    break;
                }
                l--;
            }

            // Check for convergence
            // One root found
            if (l == tmpMainIterIndex) {
                aMtrxH[tmpMainIterIndex + (tmpDiagDim * tmpMainIterIndex)] = aMtrxH[tmpMainIterIndex + (tmpDiagDim * tmpMainIterIndex)] + exshift;
                tmpMainDiagonal[tmpMainIterIndex] = aMtrxH[tmpMainIterIndex + (tmpDiagDim * tmpMainIterIndex)];
                tmpOffDiagonal[tmpMainIterIndex] = PrimitiveMath.ZERO;
                tmpMainIterIndex--;
                tmpIterCount = 0;

                // Two roots found
            } else if (l == (tmpMainIterIndex - 1)) {
                w = aMtrxH[tmpMainIterIndex + (tmpDiagDim * (tmpMainIterIndex - 1))] * aMtrxH[(tmpMainIterIndex - 1) + (tmpDiagDim * tmpMainIterIndex)];
                p = (aMtrxH[(tmpMainIterIndex - 1) + (tmpDiagDim * (tmpMainIterIndex - 1))] - aMtrxH[tmpMainIterIndex + (tmpDiagDim * tmpMainIterIndex)]) / 2.0;
                q = (p * p) + w;
                z = Math.sqrt(Math.abs(q));
                aMtrxH[tmpMainIterIndex + (tmpDiagDim * tmpMainIterIndex)] = aMtrxH[tmpMainIterIndex + (tmpDiagDim * tmpMainIterIndex)] + exshift;
                aMtrxH[(tmpMainIterIndex - 1) + (tmpDiagDim * (tmpMainIterIndex - 1))] = aMtrxH[(tmpMainIterIndex - 1) + (tmpDiagDim * (tmpMainIterIndex - 1))]
                        + exshift;
                x = aMtrxH[tmpMainIterIndex + (tmpDiagDim * tmpMainIterIndex)];

                // Real pair
                if (q >= 0) {
                    if (p >= 0) {
                        z = p + z;
                    } else {
                        z = p - z;
                    }
                    tmpMainDiagonal[tmpMainIterIndex - 1] = x + z;
                    tmpMainDiagonal[tmpMainIterIndex] = tmpMainDiagonal[tmpMainIterIndex - 1];
                    if (z != PrimitiveMath.ZERO) {
                        tmpMainDiagonal[tmpMainIterIndex] = x - (w / z);
                    }
                    tmpOffDiagonal[tmpMainIterIndex - 1] = PrimitiveMath.ZERO;
                    tmpOffDiagonal[tmpMainIterIndex] = PrimitiveMath.ZERO;
                    x = aMtrxH[tmpMainIterIndex + (tmpDiagDim * (tmpMainIterIndex - 1))];
                    s = Math.abs(x) + Math.abs(z);
                    p = x / s;
                    q = z / s;
                    r = Math.sqrt((p * p) + (q * q));
                    p = p / r;
                    q = q / r;

                    // Row modification
                    for (int j = tmpMainIterIndex - 1; j < tmpDiagDim; j++) {
                        z = aMtrxH[(tmpMainIterIndex - 1) + (tmpDiagDim * j)];
                        aMtrxH[(tmpMainIterIndex - 1) + (tmpDiagDim * j)] = (q * z) + (p * aMtrxH[tmpMainIterIndex + (tmpDiagDim * j)]);
                        aMtrxH[tmpMainIterIndex + (tmpDiagDim * j)] = (q * aMtrxH[tmpMainIterIndex + (tmpDiagDim * j)]) - (p * z);
                    }

                    // Column modification
                    for (int i = 0; i <= tmpMainIterIndex; i++) {
                        z = aMtrxH[i + (tmpDiagDim * (tmpMainIterIndex - 1))];
                        aMtrxH[i + (tmpDiagDim * (tmpMainIterIndex - 1))] = (q * z) + (p * aMtrxH[i + (tmpDiagDim * tmpMainIterIndex)]);
                        aMtrxH[i + (tmpDiagDim * tmpMainIterIndex)] = (q * aMtrxH[i + (tmpDiagDim * tmpMainIterIndex)]) - (p * z);
                    }

                    // Accumulate transformations
                    for (int i = 0; i <= tmpDiagDimMinusOne; i++) {
                        z = aMtrxV[i + (tmpDiagDim * (tmpMainIterIndex - 1))];
                        aMtrxV[i + (tmpDiagDim * (tmpMainIterIndex - 1))] = (q * z) + (p * aMtrxV[i + (tmpDiagDim * tmpMainIterIndex)]);
                        aMtrxV[i + (tmpDiagDim * tmpMainIterIndex)] = (q * aMtrxV[i + (tmpDiagDim * tmpMainIterIndex)]) - (p * z);
                    }

                    // Complex pair
                } else {
                    tmpMainDiagonal[tmpMainIterIndex - 1] = x + p;
                    tmpMainDiagonal[tmpMainIterIndex] = x + p;
                    tmpOffDiagonal[tmpMainIterIndex - 1] = z;
                    tmpOffDiagonal[tmpMainIterIndex] = -z;
                }
                tmpMainIterIndex = tmpMainIterIndex - 2;
                tmpIterCount = 0;

                // No convergence yet
            } else {

                // Form shift
                x = aMtrxH[tmpMainIterIndex + (tmpDiagDim * tmpMainIterIndex)];
                y = PrimitiveMath.ZERO;
                w = PrimitiveMath.ZERO;
                if (l < tmpMainIterIndex) {
                    y = aMtrxH[(tmpMainIterIndex - 1) + (tmpDiagDim * (tmpMainIterIndex - 1))];
                    w = aMtrxH[tmpMainIterIndex + (tmpDiagDim * (tmpMainIterIndex - 1))] * aMtrxH[(tmpMainIterIndex - 1) + (tmpDiagDim * tmpMainIterIndex)];
                }

                // Wilkinson's original ad hoc shift
                if (tmpIterCount == 10) {
                    exshift += x;
                    for (int i = 0; i <= tmpMainIterIndex; i++) {
                        aMtrxH[i + (tmpDiagDim * i)] -= x;
                    }
                    s = Math.abs(aMtrxH[tmpMainIterIndex + (tmpDiagDim * (tmpMainIterIndex - 1))])
                            + Math.abs(aMtrxH[(tmpMainIterIndex - 1) + (tmpDiagDim * (tmpMainIterIndex - 2))]);
                    x = y = 0.75 * s;
                    w = -0.4375 * s * s;
                }

                // MATLAB's new ad hoc shift
                if (tmpIterCount == 30) {
                    s = (y - x) / 2.0;
                    s = (s * s) + w;
                    if (s > 0) {
                        s = Math.sqrt(s);
                        if (y < x) {
                            s = -s;
                        }
                        s = x - (w / (((y - x) / 2.0) + s));
                        for (int i = 0; i <= tmpMainIterIndex; i++) {
                            aMtrxH[i + (tmpDiagDim * i)] -= s;
                        }
                        exshift += s;
                        x = y = w = 0.964;
                    }
                }

                tmpIterCount++; // (Could check iteration count here.)

                // Look for two consecutive small sub-diagonal elements
                int m = tmpMainIterIndex - 2;
                while (m >= l) {
                    z = aMtrxH[m + (tmpDiagDim * m)];
                    r = x - z;
                    s = y - z;
                    p = (((r * s) - w) / aMtrxH[(m + 1) + (tmpDiagDim * m)]) + aMtrxH[m + (tmpDiagDim * (m + 1))];
                    q = aMtrxH[(m + 1) + (tmpDiagDim * (m + 1))] - z - r - s;
                    r = aMtrxH[(m + 2) + (tmpDiagDim * (m + 1))];
                    s = Math.abs(p) + Math.abs(q) + Math.abs(r);
                    p = p / s;
                    q = q / s;
                    r = r / s;
                    if (m == l) {
                        break;
                    }
                    if ((Math.abs(aMtrxH[m + (tmpDiagDim * (m - 1))]) * (Math.abs(q) + Math.abs(r))) < (PrimitiveMath.MACHINE_DOUBLE_ERROR * (Math.abs(p) * (Math
                            .abs(aMtrxH[(m - 1) + (tmpDiagDim * (m - 1))]) + Math.abs(z) + Math.abs(aMtrxH[(m + 1) + (tmpDiagDim * (m + 1))]))))) {
                        break;
                    }
                    m--;
                }

                for (int i = m + 2; i <= tmpMainIterIndex; i++) {
                    aMtrxH[i + (tmpDiagDim * (i - 2))] = PrimitiveMath.ZERO;
                    if (i > (m + 2)) {
                        aMtrxH[i + (tmpDiagDim * (i - 3))] = PrimitiveMath.ZERO;
                    }
                }

                // Double QR step involving rows l:n and columns m:n
                for (int k = m; k <= (tmpMainIterIndex - 1); k++) {
                    final boolean notlast = (k != (tmpMainIterIndex - 1));
                    if (k != m) {
                        p = aMtrxH[k + (tmpDiagDim * (k - 1))];
                        q = aMtrxH[(k + 1) + (tmpDiagDim * (k - 1))];
                        r = (notlast ? aMtrxH[(k + 2) + (tmpDiagDim * (k - 1))] : PrimitiveMath.ZERO);
                        x = Math.abs(p) + Math.abs(q) + Math.abs(r);
                        if (x != PrimitiveMath.ZERO) {
                            p = p / x;
                            q = q / x;
                            r = r / x;
                        }
                    }
                    if (x == PrimitiveMath.ZERO) {
                        break;
                    }
                    s = Math.sqrt((p * p) + (q * q) + (r * r));
                    if (p < 0) {
                        s = -s;
                    }
                    if (s != 0) {
                        if (k != m) {
                            aMtrxH[k + (tmpDiagDim * (k - 1))] = -s * x;
                        } else if (l != m) {
                            aMtrxH[k + (tmpDiagDim * (k - 1))] = -aMtrxH[k + (tmpDiagDim * (k - 1))];
                        }
                        p = p + s;
                        x = p / s;
                        y = q / s;
                        z = r / s;
                        q = q / p;
                        r = r / p;

                        // Row modification
                        for (int j = k; j < tmpDiagDim; j++) {
                            p = aMtrxH[k + (tmpDiagDim * j)] + (q * aMtrxH[(k + 1) + (tmpDiagDim * j)]);
                            if (notlast) {
                                p = p + (r * aMtrxH[(k + 2) + (tmpDiagDim * j)]);
                                aMtrxH[(k + 2) + (tmpDiagDim * j)] = aMtrxH[(k + 2) + (tmpDiagDim * j)] - (p * z);
                            }
                            aMtrxH[k + (tmpDiagDim * j)] = aMtrxH[k + (tmpDiagDim * j)] - (p * x);
                            aMtrxH[(k + 1) + (tmpDiagDim * j)] = aMtrxH[(k + 1) + (tmpDiagDim * j)] - (p * y);
                        }

                        // Column modification
                        for (int i = 0; i <= Math.min(tmpMainIterIndex, k + 3); i++) {
                            p = (x * aMtrxH[i + (tmpDiagDim * k)]) + (y * aMtrxH[i + (tmpDiagDim * (k + 1))]);
                            if (notlast) {
                                p = p + (z * aMtrxH[i + (tmpDiagDim * (k + 2))]);
                                aMtrxH[i + (tmpDiagDim * (k + 2))] = aMtrxH[i + (tmpDiagDim * (k + 2))] - (p * r);
                            }
                            aMtrxH[i + (tmpDiagDim * k)] = aMtrxH[i + (tmpDiagDim * k)] - p;
                            aMtrxH[i + (tmpDiagDim * (k + 1))] = aMtrxH[i + (tmpDiagDim * (k + 1))] - (p * q);
                        }

                        // Accumulate transformations
                        for (int i = 0; i <= tmpDiagDimMinusOne; i++) {
                            p = (x * aMtrxV[i + (tmpDiagDim * k)]) + (y * aMtrxV[i + (tmpDiagDim * (k + 1))]);
                            if (notlast) {
                                p = p + (z * aMtrxV[i + (tmpDiagDim * (k + 2))]);
                                aMtrxV[i + (tmpDiagDim * (k + 2))] = aMtrxV[i + (tmpDiagDim * (k + 2))] - (p * r);
                            }
                            aMtrxV[i + (tmpDiagDim * k)] = aMtrxV[i + (tmpDiagDim * k)] - p;
                            aMtrxV[i + (tmpDiagDim * (k + 1))] = aMtrxV[i + (tmpDiagDim * (k + 1))] - (p * q);
                        }
                    } // (s != 0)
                } // k loop
            } // check convergence
        } // while (n >= low)

        // Backsubstitute to find vectors of upper triangular form
        if (allTheWay && (tmpNorm1 != PrimitiveMath.ZERO)) {
            PrimitiveDenseStore.doAfter(aMtrxH, aMtrxV, tmpMainDiagonal, tmpOffDiagonal, r, s, z, tmpNorm1);
        }

        return new double[][] { tmpMainDiagonal, tmpOffDiagonal };
    }

    private final int myColDim;
    private final int myRowDim;
    private final Array2D<Double> myUtility;

    PrimitiveDenseStore(final double[] anArray) {

        super(anArray);

        myRowDim = anArray.length;
        myColDim = 1;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    PrimitiveDenseStore(final int aLength) {

        super(aLength);

        myRowDim = aLength;
        myColDim = 1;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    PrimitiveDenseStore(final int aRowDim, final int aColDim) {

        super(aRowDim * aColDim);

        myRowDim = aRowDim;
        myColDim = aColDim;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    PrimitiveDenseStore(final int aRowDim, final int aColDim, final double[] anArray) {

        super(anArray);

        myRowDim = aRowDim;
        myColDim = aColDim;
        myUtility = this.asArray2D(myRowDim, myColDim);
    }

    public Double aggregateAll(final Aggregator aVisitor) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > AggregateAll.THRESHOLD) {

            final DivideAndMerge<Double> tmpConquerer = new DivideAndMerge<Double>(AggregateAll.THRESHOLD) {

                @Override
                public Double conquer(final int aFirst, final int aLimit) {

                    final AggregatorFunction<Double> tmpAggrFunc = aVisitor.getPrimitiveFunction();

                    PrimitiveDenseStore.this.visit(tmpRowDim * aFirst, tmpRowDim * aLimit, 1, tmpAggrFunc);

                    return tmpAggrFunc.getNumber();
                }

                @Override
                public Double merge(final Double aFirstResult, final Double aSecondResult) {

                    final AggregatorFunction<Double> tmpAggrFunc = aVisitor.getPrimitiveFunction();

                    tmpAggrFunc.merge(aFirstResult);
                    tmpAggrFunc.merge(aSecondResult);

                    return tmpAggrFunc.getNumber();
                }
            };

            return tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            final AggregatorFunction<Double> tmpAggrFunc = aVisitor.getPrimitiveFunction();

            PrimitiveDenseStore.this.visit(0, length, 1, tmpAggrFunc);

            return tmpAggrFunc.getNumber();
        }
    }

    public void applyTransformations(final int iterationPoint, final SimpleArray<Double> multipliers, final boolean hermitian) {

        final double[] tmpData = this.data();
        final double[] tmpColumn = ((SimpleArray.Primitive) multipliers).data;

        if ((myColDim - iterationPoint - 1) > ApplyTransformations.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(ApplyTransformations.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    ApplyTransformations.invoke(tmpData, myRowDim, aFirst, aLimit, tmpColumn, iterationPoint, hermitian);
                }
            };

            tmpConquerer.divide(iterationPoint + 1, myColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            ApplyTransformations.invoke(tmpData, myRowDim, iterationPoint + 1, myColDim, tmpColumn, iterationPoint, hermitian);
        }
    }

    public Array2D<Double> asArray2D() {
        return myUtility;
    }

    public Array1D<Double> asList() {
        return myUtility.asArray1D();
    }

    public final MatrixStore.Builder<Double> builder() {
        return new MatrixStore.Builder<Double>(this);
    }

    public void caxpy(final Double aSclrA, final int aColX, final int aColY, final int aFirstRow) {
        CAXPY.invoke(this.data(), aColY * myRowDim, this.data(), aColX * myRowDim, aSclrA.doubleValue(), aFirstRow, myRowDim);
    }

    public Array1D<ComplexNumber> computeInPlaceSchur(final PhysicalStore<Double> aTransformationCollector, final boolean eigenvalue) {

        //        final PrimitiveDenseStore tmpThisCopy = this.copy();
        //        final PrimitiveDenseStore tmpCollCopy = (PrimitiveDenseStore) aTransformationCollector.copy();
        //
        //        tmpThisCopy.computeInPlaceHessenberg(true);

        // Actual

        final double[] tmpData = this.data();

        final double[] tmpCollectorData = ((PrimitiveDenseStore) aTransformationCollector).data();

        PrimitiveDenseStore.doHessenberg(tmpData, tmpCollectorData);

        //        BasicLogger.logDebug("Schur Step", this);
        //        BasicLogger.logDebug("Hessenberg", tmpThisCopy);

        final double[][] tmpDiags = PrimitiveDenseStore.doSchur(tmpData, tmpCollectorData, eigenvalue);
        final double[] aRawReal = tmpDiags[0];
        final double[] aRawImag = tmpDiags[1];
        final int tmpLength = Math.min(aRawReal.length, aRawImag.length);

        final SimpleArray.Complex retVal = SimpleArray.makeComplex(tmpLength);
        final ComplexNumber[] tmpRaw = retVal.data;

        for (int i = 0; i < tmpLength; i++) {
            tmpRaw[i] = ComplexNumber.makeRectangular(aRawReal[i], aRawImag[i]);
        }

        return Array1D.COMPLEX.wrap(retVal);
    }

    public PrimitiveDenseStore conjugate() {
        return this.transpose();
    }

    public PrimitiveDenseStore copy() {
        return new PrimitiveDenseStore(myRowDim, myColDim, this.copyOfData());
    }

    public void divideAndCopyColumn(final int aRow, final int aCol, final SimpleArray<Double> aDestination) {

        final double[] tmpData = this.data();
        final int tmpRowDim = myRowDim;

        final double[] tmpDestination = ((SimpleArray.Primitive) aDestination).data;

        int tmpIndex = aRow + (aCol * tmpRowDim);
        final double tmpDenominator = tmpData[tmpIndex];

        for (int i = aRow + 1; i < tmpRowDim; i++) {
            tmpDestination[i] = tmpData[++tmpIndex] /= tmpDenominator;
        }
    }

    public double doubleValue(final int aRow, final int aCol) {
        return myUtility.doubleValue(aRow, aCol);
    }

    public boolean equals(final MatrixStore<Double> aStore, final NumberContext aCntxt) {
        return MatrixUtils.equals(this, aStore, aCntxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object anObj) {
        if (anObj instanceof MatrixStore) {
            return this.equals((MatrixStore<Double>) anObj, TypeUtils.EQUALS_NUMBER_CONTEXT);
        } else {
            return super.equals(anObj);
        }
    }

    public void exchangeColumns(final int aColA, final int aColB) {
        myUtility.exchangeColumns(aColA, aColB);
    }

    public void exchangeRows(final int aRowA, final int aRowB) {
        myUtility.exchangeRows(aRowA, aRowB);
    }

    public PhysicalStore.Factory<Double, PrimitiveDenseStore> factory() {
        return FACTORY;
    }

    public void fillAll(final Double aNmbr) {
        myUtility.fillAll(aNmbr);
    }

    public void fillByMultiplying(final MatrixStore<Double> aLeftStore, final MatrixStore<Double> aRightStore) {

        final double[] tmpProductData = this.data();

        if (aRightStore instanceof PrimitiveDenseStore) {

            PrimitiveDenseStore.doMultiplyLeft(tmpProductData, aLeftStore, PrimitiveDenseStore.cast(aRightStore).data());

        } else if (aLeftStore instanceof PrimitiveDenseStore) {

            this.fillAll(ZERO);

            PrimitiveDenseStore.doMultiplyRight(tmpProductData, PrimitiveDenseStore.cast(aLeftStore).data(), aRightStore);

        } else {

            PrimitiveDenseStore.doMultiplyBoth(tmpProductData, aLeftStore, aRightStore);
        }
    }

    public void fillColumn(final int aRow, final int aCol, final Double aNmbr) {
        myUtility.fillColumn(aRow, aCol, aNmbr);
    }

    public void fillDiagonal(final int aRow, final int aCol, final Double aNmbr) {
        myUtility.fillDiagonal(aRow, aCol, aNmbr);
    }

    public void fillMatching(final Access2D<? extends Number> aSource2D) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingSingle.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingSingle.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    FillMatchingSingle.invoke(PrimitiveDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aSource2D);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            FillMatchingSingle.invoke(this.data(), tmpRowDim, 0, tmpColDim, aSource2D);
        }
    }

    public void fillMatching(final Double aLeftArg, final BinaryFunction<Double> aFunc, final MatrixStore<Double> aRightArg) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingRight.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingRight.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    PrimitiveDenseStore.this.fill(tmpRowDim * aFirst, tmpRowDim * aLimit, aLeftArg, aFunc, aRightArg);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.fill(0, tmpRowDim * tmpColDim, aLeftArg, aFunc, aRightArg);
        }
    }

    public void fillMatching(final MatrixStore<Double> aLeftArg, final BinaryFunction<Double> aFunc, final Double aRightArg) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingLeft.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingLeft.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    PrimitiveDenseStore.this.fill(tmpRowDim * aFirst, tmpRowDim * aLimit, aLeftArg, aFunc, aRightArg);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.fill(0, tmpRowDim * tmpColDim, aLeftArg, aFunc, aRightArg);
        }
    }

    public void fillMatching(final MatrixStore<Double> aLeftArg, final BinaryFunction<Double> aFunc, final MatrixStore<Double> aRightArg) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > FillMatchingBoth.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(FillMatchingBoth.THRESHOLD) {

                @Override
                protected void conquer(final int aFirst, final int aLimit) {
                    PrimitiveDenseStore.this.fill(tmpRowDim * aFirst, tmpRowDim * aLimit, aLeftArg, aFunc, aRightArg);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.fill(0, tmpRowDim * tmpColDim, aLeftArg, aFunc, aRightArg);
        }
    }

    public void fillRow(final int aRow, final int aCol, final Double aNmbr) {
        myUtility.fillRow(aRow, aCol, aNmbr);
    }

    public boolean generateApplyAndCopyHouseholderColumn(final int aRow, final int aCol, final Householder<Double> aDestination) {
        return GenerateApplyAndCopyHouseholderColumn.invoke(this.data(), myRowDim, aRow, aCol, (Householder.Primitive) aDestination);
    }

    public boolean generateApplyAndCopyHouseholderRow(final int aRow, final int aCol, final Householder<Double> aDestination) {
        return GenerateApplyAndCopyHouseholderRow.invoke(this.data(), myRowDim, aRow, aCol, (Householder.Primitive) aDestination);
    }

    public Double get(final int aRow, final int aCol) {
        return myUtility.get(aRow, aCol);
    }

    public int getColDim() {
        return myColDim;
    }

    /**
     * @deprecated v33 Use {@link #factory()} instead
     */
    @Deprecated
    public PhysicalStore.Factory<Double, PrimitiveDenseStore> getFactory() {
        return this.factory();
    }

    public int getIndexOfLargestInColumn(final int aRow, final int aCol) {
        return myUtility.getIndexOfLargestInColumn(aRow, aCol);
    }

    public int getMinDim() {
        return Math.min(myRowDim, myColDim);
    }

    public int getRowDim() {
        return myRowDim;
    }

    @Override
    public int hashCode() {
        return MatrixUtils.hashCode(this);
    }

    public boolean isAbsolute(final int aRow, final int aCol) {
        return myUtility.isAbsolute(aRow, aCol);
    }

    public boolean isLowerLeftShaded() {
        return false;
    }

    public boolean isPositive(final int aRow, final int aCol) {
        return myUtility.isPositive(aRow, aCol);
    }

    public boolean isReal(final int aRow, final int aCol) {
        return myUtility.isReal(aRow, aCol);
    }

    public boolean isUpperRightShaded() {
        return false;
    }

    public boolean isZero(final int aRow, final int aCol) {
        return myUtility.isZero(aRow, aCol);
    }

    public void maxpy(final Double aSclrA, final MatrixStore<Double> aMtrxX) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > MAXPY.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(MAXPY.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    MAXPY.invoke(PrimitiveDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aSclrA, aMtrxX);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            MAXPY.invoke(this.data(), tmpRowDim, 0, tmpColDim, aSclrA, aMtrxX);
        }
    }

    public void modifyAll(final UnaryFunction<Double> aFunc) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > ModifyAll.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(ModifyAll.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    PrimitiveDenseStore.this.modify(tmpRowDim * aFirst, tmpRowDim * aLimit, 1, aFunc);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            this.modify(tmpRowDim * 0, tmpRowDim * tmpColDim, 1, aFunc);
        }
    }

    public void modifyColumn(final int aRow, final int aCol, final UnaryFunction<Double> aFunc) {
        myUtility.modifyColumn(aRow, aCol, aFunc);
    }

    public void modifyDiagonal(final int aRow, final int aCol, final UnaryFunction<Double> aFunc) {
        myUtility.modifyDiagonal(aRow, aCol, aFunc);
    }

    public void modifyOne(final int aRow, final int aCol, final UnaryFunction<Double> aFunc) {

        double tmpValue = this.doubleValue(aRow, aCol);

        tmpValue = aFunc.invoke(tmpValue);

        this.set(aRow, aCol, tmpValue);
    }

    public void modifyRow(final int aRow, final int aCol, final UnaryFunction<Double> aFunc) {
        myUtility.modifyRow(aRow, aCol, aFunc);
    }

    public MatrixStore<Double> multiplyLeft(final MatrixStore<Double> aStore) {

        final PrimitiveDenseStore retVal = FACTORY.makeZero(aStore.getRowDim(), myColDim);

        PrimitiveDenseStore.doMultiplyLeft(retVal.data(), aStore, this.data());

        return retVal;
    }

    public MatrixStore<Double> multiplyRight(final MatrixStore<Double> aStore) {

        final PrimitiveDenseStore retVal = FACTORY.makeZero(myRowDim, aStore.getColDim());

        PrimitiveDenseStore.doMultiplyRight(retVal.data(), this.data(), aStore);

        return retVal;
    }

    public void negateColumn(final int aCol) {
        myUtility.modifyColumn(0, aCol, PrimitiveFunction.NEGATE);
    }

    public void raxpy(final Double aSclrA, final int aRowX, final int aRowY, final int aFirstCol) {
        RAXPY.invoke(this.data(), aRowY, this.data(), aRowX, aSclrA, aFirstCol, myColDim);
    }

    public void rotateRight(final int aLow, final int aHigh, final double aCos, final double aSin) {
        RotateRight.invoke(this.data(), myRowDim, aLow, aHigh, aCos, aSin);
    }

    public void set(final int aRow, final int aCol, final double aNmbr) {
        myUtility.set(aRow, aCol, aNmbr);
    }

    public void set(final int aRow, final int aCol, final Double aNmbr) {
        myUtility.set(aRow, aCol, aNmbr);
    }

    public void setToIdentity(final int aCol) {
        myUtility.set(aCol, aCol, ONE);
        myUtility.fillColumn(aCol + 1, aCol, ZERO);
    }

    public void substituteBackwards(final Access2D<Double> aBody, final boolean conjugated) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > SubstituteBackwards.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(SubstituteBackwards.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    SubstituteBackwards.invoke(PrimitiveDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aBody, conjugated);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            SubstituteBackwards.invoke(this.data(), tmpRowDim, 0, tmpColDim, aBody, conjugated);
        }
    }

    public void substituteForwards(final Access2D<Double> aBody, final boolean onesOnDiagonal, final boolean zerosAboveDiagonal) {

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > SubstituteForwards.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(SubstituteForwards.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    SubstituteForwards.invoke(PrimitiveDenseStore.this.data(), tmpRowDim, aFirst, aLimit, aBody, onesOnDiagonal, zerosAboveDiagonal);
                }

            };

            tmpConquerer.divide(0, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            SubstituteForwards.invoke(this.data(), tmpRowDim, 0, tmpColDim, aBody, onesOnDiagonal, zerosAboveDiagonal);
        }
    }

    public PrimitiveScalar toScalar(final int aRow, final int aCol) {
        return new PrimitiveScalar(this.doubleValue(aRow + (aCol * myRowDim)));
    }

    public void transformLeft(final Householder<Double> aTransf, final int aFirstCol) {

        final Householder.Primitive tmpTransf = PrimitiveDenseStore.cast(aTransf);

        final double[] tmpData = this.data();

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > HouseholderLeft.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(HouseholderLeft.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    HouseholderLeft.invoke(tmpData, tmpRowDim, aFirst, aLimit, tmpTransf);
                }

            };

            tmpConquerer.divide(aFirstCol, tmpColDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            HouseholderLeft.invoke(tmpData, tmpRowDim, aFirstCol, tmpColDim, tmpTransf);
        }
    }

    public void transformLeft(final Rotation<Double> aTransf) {

        final Rotation.Primitive tmpTransf = PrimitiveDenseStore.cast(aTransf);

        final int tmpLow = tmpTransf.low;
        final int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {
                RotateLeft.invoke(this.data(), myColDim, tmpLow, tmpHigh, tmpTransf.cos, tmpTransf.sin);
            } else {
                myUtility.exchangeRows(tmpLow, tmpHigh);
            }
        } else {
            if (!Double.isNaN(tmpTransf.cos)) {
                myUtility.modifyRow(tmpLow, 0, MULTIPLY, tmpTransf.cos);
            } else if (!Double.isNaN(tmpTransf.sin)) {
                myUtility.modifyRow(tmpLow, 0, DIVIDE, tmpTransf.sin);
            } else {
                myUtility.modifyRow(tmpLow, 0, NEGATE);
            }
        }
    }

    public void transformRight(final Householder<Double> aTransf, final int aFirstRow) {

        final Householder.Primitive tmpTransf = PrimitiveDenseStore.cast(aTransf);

        final double[] tmpData = this.data();

        final int tmpRowDim = myRowDim;
        final int tmpColDim = myColDim;

        if (tmpColDim > HouseholderRight.THRESHOLD) {

            final DivideAndConquer tmpConquerer = new DivideAndConquer(HouseholderRight.THRESHOLD) {

                @Override
                public void conquer(final int aFirst, final int aLimit) {
                    HouseholderRight.invoke(tmpData, aFirst, aLimit, tmpColDim, tmpTransf);
                }

            };

            tmpConquerer.divide(aFirstRow, tmpRowDim, OjAlgoUtils.ENVIRONMENT.countThreads());

        } else {

            HouseholderRight.invoke(tmpData, aFirstRow, tmpRowDim, tmpColDim, tmpTransf);
        }
    }

    public void transformRight(final Rotation<Double> aTransf) {

        final Rotation.Primitive tmpTransf = PrimitiveDenseStore.cast(aTransf);

        final int tmpLow = tmpTransf.low;
        final int tmpHigh = tmpTransf.high;

        if (tmpLow != tmpHigh) {
            if (!Double.isNaN(tmpTransf.cos) && !Double.isNaN(tmpTransf.sin)) {
                RotateRight.invoke(this.data(), myRowDim, tmpLow, tmpHigh, tmpTransf.cos, tmpTransf.sin);
            } else {
                myUtility.exchangeColumns(tmpLow, tmpHigh);
            }
        } else {
            if (!Double.isNaN(tmpTransf.cos)) {
                myUtility.modifyColumn(0, tmpHigh, MULTIPLY, tmpTransf.cos);
            } else if (!Double.isNaN(tmpTransf.sin)) {
                myUtility.modifyColumn(0, tmpHigh, DIVIDE, tmpTransf.sin);
            } else {
                myUtility.modifyColumn(0, tmpHigh, NEGATE);
            }
        }
    }

    public void transformSymmetric(final Householder<Double> aTransf) {
        HouseholderHermitian.invoke(this.data(), PrimitiveDenseStore.cast(aTransf), new double[aTransf.size()]);
    }

    public PrimitiveDenseStore transpose() {

        final PrimitiveDenseStore retVal = new PrimitiveDenseStore(myColDim, myRowDim);

        retVal.fillMatching(new TransposedStore<Double>(this));

        return retVal;
    }

    public void tred2(final SimpleArray<Double> mainDiagonal, final SimpleArray<Double> offDiagonal, final boolean yesvecs) {
        HouseholderHermitian.tred2j(this.data(), ((SimpleArray.Primitive) mainDiagonal).data, ((SimpleArray.Primitive) offDiagonal).data, yesvecs);
    }

    public void visitAll(final AggregatorFunction<Double> aVisitor) {
        myUtility.visitAll(aVisitor);
    }

    public void visitColumn(final int aRow, final int aCol, final AggregatorFunction<Double> aVisitor) {
        myUtility.visitColumn(aRow, aCol, aVisitor);
    }

    public void visitDiagonal(final int aRow, final int aCol, final AggregatorFunction<Double> aVisitor) {
        myUtility.visitDiagonal(aRow, aCol, aVisitor);
    }

    public void visitRow(final int aRow, final int aCol, final AggregatorFunction<Double> aVisitor) {
        myUtility.visitRow(aRow, aCol, aVisitor);
    }

}
