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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * Orginalet, sedan ett tag
 * 
 * Based on SVDnew2, but with transposing so that calculations are
 * always made on a matrix that "isAspectRationNormal".
 * 
 * Based on SVDnew5, but with Rotation replaced by the new alternative.
 * 
 * 
 *
 * @author apete
 */
abstract class SVDnew32<N extends Number & Comparable<N>> extends SingularValueDecomposition<N> {

    static final class Primitive extends SVDnew32<Double> {

        static void doCase1(final double[] s, final double[] e, final int p, final int k, final DecompositionStore<Double> aQ2) {

            double f = e[p - 2];
            e[p - 2] = PrimitiveMath.ZERO;

            double t;
            double cs;
            double sn;

            for (int j = p - 2; j >= k; j--) {

                t = Math.hypot(s[j], f);
                cs = s[j] / t;
                sn = f / t;

                s[j] = t;
                if (j != k) {
                    f = -sn * e[j - 1];
                    e[j - 1] = cs * e[j - 1];
                }

                if (aQ2 != null) {
                    aQ2.rotateRight(p - 1, j, cs, sn);
                }
            }
        }

        static void doCase2(final double[] s, final double[] e, final int p, final int k, final DecompositionStore<Double> aQ1) {

            double f = e[k - 1];
            e[k - 1] = PrimitiveMath.ZERO;

            double t;
            double cs;
            double sn;

            for (int j = k; j < p; j++) {

                t = Math.hypot(s[j], f);
                cs = s[j] / t;
                sn = f / t;

                s[j] = t;
                f = -sn * e[j];
                e[j] = cs * e[j];

                if (aQ1 != null) {
                    aQ1.rotateRight(k - 1, j, cs, sn);
                }
            }
        }

        static void doCase3(final double[] s, final double[] e, final int p, final int k, final DecompositionStore<Double> aQ1,
                final DecompositionStore<Double> aQ2) {

            final int indPm1 = p - 1;
            final int indPm2 = p - 2;

            // Calculate the shift.
            final double scale = Math.max(Math.max(Math.max(Math.max(Math.abs(s[indPm1]), Math.abs(s[indPm2])), Math.abs(e[indPm2])), Math.abs(s[k])),
                    Math.abs(e[k]));

            final double sPm1 = s[indPm1] / scale;
            final double sPm2 = s[indPm2] / scale;
            final double ePm2 = e[indPm2] / scale;
            final double sK = s[k] / scale;
            final double eK = e[k] / scale;

            final double b = (((sPm2 + sPm1) * (sPm2 - sPm1)) + (ePm2 * ePm2)) / PrimitiveMath.TWO;
            final double c = (sPm1 * ePm2) * (sPm1 * ePm2);

            double shift = PrimitiveMath.ZERO;
            if ((b != PrimitiveMath.ZERO) || (c != PrimitiveMath.ZERO)) {
                shift = Math.sqrt((b * b) + c);
                if (b < PrimitiveMath.ZERO) {
                    shift = -shift;
                }
                shift = c / (b + shift);
            }

            double f = ((sK + sPm1) * (sK - sPm1)) + shift;
            double g = sK * eK;

            double t;
            double cs;
            double sn;

            // Chase zeros.
            for (int j = k; j < indPm1; j++) {

                t = Math.hypot(f, g);
                cs = f / t;
                sn = g / t;
                if (j != k) {
                    e[j - 1] = t;
                }
                f = (cs * s[j]) + (sn * e[j]);
                e[j] = (cs * e[j]) - (sn * s[j]);
                g = sn * s[j + 1];
                s[j + 1] = cs * s[j + 1];

                if (aQ2 != null) {
                    aQ2.rotateRight(j + 1, j, cs, sn);

                }

                t = Math.hypot(f, g);
                cs = f / t;
                sn = g / t;
                s[j] = t;
                f = (cs * e[j]) + (sn * s[j + 1]);
                s[j + 1] = (-sn * e[j]) + (cs * s[j + 1]);
                g = sn * e[j + 1];
                e[j + 1] = cs * e[j + 1];

                if (aQ1 != null) {
                    aQ1.rotateRight(j + 1, j, cs, sn);

                }
            }

            e[indPm2] = f;
        }

        static void doCase4(final double[] s, final int k, final DecompositionStore<Double> aQ1, final DecompositionStore<Double> aQ2) {

            final int tmpDiagDim = s.length;

            // Make the singular values positive.
            final double tmpSk = s[k];
            if (tmpSk < PrimitiveMath.ZERO) {
                s[k] = -tmpSk;

                if (aQ2 != null) {
                    aQ2.modifyColumn(0, k, PrimitiveFunction.NEGATE);

                }
            } else if (tmpSk == PrimitiveMath.ZERO) {
                s[k] = PrimitiveMath.ZERO; // To get rid of negative zeros
            }

            // Order the singular values.
            int tmpK = k;

            while (tmpK < (tmpDiagDim - 1)) {
                if (s[tmpK] >= s[tmpK + 1]) {
                    break;
                }
                final double t = s[tmpK];
                s[tmpK] = s[tmpK + 1];
                s[tmpK + 1] = t;

                if ((aQ1 != null) || (aQ2 != null)) {
                    aQ1.exchangeColumns(tmpK + 1, tmpK);
                    aQ2.exchangeColumns(tmpK + 1, tmpK);
                }

                tmpK++;
            }
        }

        Primitive() {
            super(PrimitiveDenseStore.FACTORY, new BidiagonalDecomposition.Primitive());
        }

        @Override
        Array1D<Double> compute(final Array1D<Double> mainDiagonal, final Array1D<Double> offDiagonal, final boolean singularValuesOnly) {

            final DecompositionStore<Double> tmpQ1 = !singularValuesOnly ? this.getBidiagonalQ1() : null;
            final DecompositionStore<Double> tmpQ2 = !singularValuesOnly ? this.getBidiagonalQ2() : null;

            final int tmpDiagDim = mainDiagonal.length;

            final double[] s = mainDiagonal.toRawCopy(); // s
            final double[] e = new double[tmpDiagDim]; // e
            final int tmpOffLength = offDiagonal.length;
            for (int i = 0; i < tmpOffLength; i++) {
                e[i] = offDiagonal.doubleValue(i);
            }

            // Main iteration loop for the singular values.
            int kase;
            int k;
            int p = tmpDiagDim;
            while (p > 0) {

                //
                // This section of the program inspects for negligible elements in the s and e arrays.
                // On completion the variables kase and k are set as follows:
                //
                // kase = 1     if s[p] and e[k-1] are negligible and k<p                           => deflate negligible s[p]
                // kase = 2     if s[k] is negligible and k<p                                       => split at negligible s[k]
                // kase = 3     if e[k-1] is negligible, k<p, and s(k)...s(p) are not negligible    => perform QR-step
                // kase = 4     if e[p-1] is negligible                                             => convergence.
                //

                kase = 0;
                k = 0;

                for (k = p - 2; k >= -1; k--) {
                    if (k == -1) {
                        break;
                    }
                    if (Math.abs(e[k]) <= (PrimitiveMath.MACHINE_DOUBLE_ERROR * (Math.abs(s[k]) + Math.abs(s[k + 1])))) {
                        e[k] = PrimitiveMath.ZERO;
                        break;
                    }
                }
                if (k == (p - 2)) {
                    kase = 4;
                } else {
                    int ks;
                    for (ks = p - 1; ks >= k; ks--) {
                        if (ks == k) {
                            break;
                        }
                        final double t = (ks != p ? Math.abs(e[ks]) : PrimitiveMath.ZERO) + (ks != (k + 1) ? Math.abs(e[ks - 1]) : PrimitiveMath.ZERO);
                        if (Math.abs(s[ks]) <= (PrimitiveMath.MACHINE_DOUBLE_ERROR * t)) {
                            s[ks] = PrimitiveMath.ZERO;
                            break;
                        }
                    }
                    if (ks == k) {
                        kase = 3;
                    } else if (ks == (p - 1)) {
                        kase = 1;
                    } else {
                        kase = 2;
                        k = ks;
                    }
                }
                k++;

                switch (kase) { // Perform the task indicated by kase.

                case 1: // Deflate negligible s[p]

                    Primitive.doCase1(s, e, p, k, tmpQ2);
                    break;

                case 2: // Split at negligible s[k]

                    Primitive.doCase2(s, e, p, k, tmpQ1);
                    break;

                case 3: // Perform QR-step.

                    Primitive.doCase3(s, e, p, k, tmpQ1, tmpQ2);
                    break;

                case 4: // Convergence

                    Primitive.doCase4(s, k, tmpQ1, tmpQ2);
                    p--;
                    break;

                default:

                    throw new IllegalStateException();

                } // switch
            } // while

            if (!singularValuesOnly) {
                if (this.isTransposed()) {
                    this.setQ1(tmpQ2);
                    this.setQ2(tmpQ1);
                } else {
                    this.setQ1(tmpQ1);
                    this.setQ2(tmpQ2);
                }
            }

            //return new PrimitiveArray(s).asArray1D();
            return Array1D.PRIMITIVE.wrap(SimpleArray.wrapPrimitive(s));
        }

    }

    private transient Future<DecompositionStore<N>> myFuture1 = null;
    private transient Future<DecompositionStore<N>> myFuture2 = null;

    protected SVDnew32(final DecompositionStore.Factory<N, ? extends DecompositionStore<N>> aFactory, final BidiagonalDecomposition<N> aBidiagonal) {
        super(aFactory, aBidiagonal);
    }

    public boolean equals(final MatrixStore<N> aMtrx, final NumberContext aCntxt) {
        return MatrixUtils.equals(aMtrx, this, TypeUtils.EQUALS_NUMBER_CONTEXT);
    }

    public boolean isFullSize() {
        return false;
    }

    public boolean isOrdered() {
        return true;
    }

    public boolean isSolvable() {
        return this.isComputed();
    }

    @Override
    public final MatrixStore<N> solve(final MatrixStore<N> aRHS) {
        return this.getInverse().multiplyRight(aRHS);
    }

    @Override
    protected boolean doCompute(final Access2D<?> aMtrx, final boolean singularValuesOnly) {

        this.computeBidiagonal(aMtrx);

        final DiagonalAccess<N> tmpBidiagonal = this.getBidiagonalAccessD();

        this.setSingularValues(this.compute(tmpBidiagonal.mainDiagonal, tmpBidiagonal.superdiagonal, singularValuesOnly));

        return this.computed(true);
    }

    @Override
    protected final MatrixStore<N> makeD() {
        return this.wrap(new DiagonalAccess<Double>(this.getSingularValues(), null, null, PrimitiveMath.ZERO));
    }

    @Override
    protected final MatrixStore<N> makeQ1() {
        if (myFuture1 != null) {
            try {
                return myFuture1.get();
            } catch (final InterruptedException anException) {
                return null;
            } catch (final ExecutionException anException) {
                return null;
            }
        } else {
            return this.getBidiagonalQ1();
        }
    }

    @Override
    protected final MatrixStore<N> makeQ2() {
        if (myFuture2 != null) {
            try {
                return myFuture2.get();
            } catch (final InterruptedException anException) {
                return null;
            } catch (final ExecutionException anException) {
                return null;
            }
        } else {
            return this.getBidiagonalQ2();
        }
    }

    @Override
    protected final Array1D<Double> makeSingularValues() {
        throw new IllegalStateException("Should never have to be called!");
    }

    abstract Array1D<Double> compute(Array1D<N> mainDiagonal, Array1D<N> offDiagonal, final boolean singularValuesOnly);

}
