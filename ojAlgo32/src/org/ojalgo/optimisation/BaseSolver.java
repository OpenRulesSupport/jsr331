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
package org.ojalgo.optimisation;

import static org.ojalgo.constant.PrimitiveMath.*;

import org.ojalgo.ProgrammingError;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.PreconfiguredSecond;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.ZeroStore;

public abstract class BaseSolver extends GenericSolver {

    @SuppressWarnings("unchecked")
    public static final class Matrices {

        private static final int LENGTH = 9;

        /**
         * {[AE], [BE], [Q], [C], [AI], [BI], [X], [LE], [LI]}
         */
        private final MatrixStore<Double>[] myMatrices = new MatrixStore[LENGTH];

        public Matrices(final AbstractBuilder<?, ?> aBuilder) {

            super();

            myMatrices[0] = aBuilder.getAE();
            myMatrices[1] = aBuilder.getBE();

            myMatrices[2] = aBuilder.getQ();
            myMatrices[3] = aBuilder.getC();

            myMatrices[4] = aBuilder.getAI();
            myMatrices[5] = aBuilder.getBI();

            this.validate();
        }

        protected Matrices(final Matrices aMatrices) {
            this(aMatrices.getArray());
        }

        protected Matrices(final MatrixStore<Double>[] aMtrxArr) {

            super();

            final int tmpMinLength = Math.min(LENGTH, aMtrxArr.length);

            for (int i = 0; i < tmpMinLength; i++) {
                myMatrices[i] = aMtrxArr[i];
            }

            this.validate();
        }

        Matrices() {
            super();
        }

        /**
         * Will rescale problem parameters to minimise rounding and
         * representation errors.
         */
        public final Matrices balance() {

            if (this.hasEqualityConstraints()) {
                this.balanceEqualityConstraints();
            }

            if (this.hasInequalityConstraints()) {
                this.balanceInequalityConstraints();
            }

            if (this.hasObjective()) {
                this.balanceObjective();
            }

            return this;
        }

        public final int countEqualityConstraints() {
            return (this.getAE() != null) ? this.getAE().getRowDim() : 0;
        }

        public final int countInequalityConstraints() {
            return (this.getAI() != null) ? this.getAI().getRowDim() : 0;
        }

        public final int countVariables() {

            int retVal = -1;

            if (this.getAE() != null) {
                retVal = this.getAE().getColDim();
            } else if (this.getAI() != null) {
                retVal = this.getAI().getColDim();
            } else if (this.getQ() != null) {
                retVal = this.getQ().getRowDim();
            } else if (this.getC() != null) {
                retVal = this.getC().getRowDim();
            } else {
                throw new ProgrammingError("Cannot deduce the number of variables!");
            }

            return retVal;
        }

        /**
         * [AE][X] == [BE]
         */
        public final MatrixStore<Double> getAE() {
            return myMatrices[0];
        }

        /**
         * [AI][X] <= [BI]
         */
        public final MatrixStore<Double> getAI() {
            return myMatrices[4];
        }

        /**
         * [AE][X] == [BE]
         */
        public final MatrixStore<Double> getBE() {
            return myMatrices[1];
        }

        /**
         * [AI][X] <= [BI]
         */
        public final MatrixStore<Double> getBI() {
            return myMatrices[5];
        }

        /**
         * Linear objective: [C]
         */
        public final MatrixStore<Double> getC() {
            return myMatrices[3];
        }

        /**
         * Lagrange multipliers / dual variables for Equalities
         */
        public final PhysicalStore<Double> getLE() {
            if (myMatrices[7] == null) {
                myMatrices[7] = PrimitiveDenseStore.FACTORY.makeZero(this.countEqualityConstraints(), 1);
            }
            return (PhysicalStore<Double>) myMatrices[7];
        }

        /**
         * Lagrange multipliers / dual variables for selected inequalities
         */
        public final MatrixStore<Double> getLI(final int... aRowSelector) {
            final MatrixStore<Double> tmpLI = this.getLI();
            if (tmpLI != null) {
                return tmpLI.builder().rows(aRowSelector).build();
            } else {
                return null;
            }
        }

        /**
         * Quadratic objective: [Q]
         */
        public final MatrixStore<Double> getQ() {
            return myMatrices[2];
        }

        /**
         * Slack for Equalities: [SE] = [BE] - [AE][X]
         */
        public final PhysicalStore<Double> getSE() {

            PhysicalStore<Double> retVal = null;

            if ((this.getAE() != null) && (this.getBE() != null) && (this.getX() != null)) {

                retVal = this.getBE().copy();

                retVal.fillMatching(retVal, PrimitiveFunction.SUBTRACT, this.getAE().multiplyRight(this.getX()));
            }

            return retVal;
        }

        /**
         * Selected Slack for Inequalities
         */
        public final MatrixStore<Double> getSI(final int... aRowSelector) {
            final PhysicalStore<Double> tmpSI = this.getSI();
            if (tmpSI != null) {
                return tmpSI.builder().rows(aRowSelector).build();
            } else {
                return null;
            }
        }

        /**
         * Solution / Variables: [X]
         */
        public final PhysicalStore<Double> getX() {
            if (myMatrices[6] == null) {
                myMatrices[6] = PrimitiveDenseStore.FACTORY.makeZero(this.countVariables(), 1);
            }
            return (PhysicalStore<Double>) myMatrices[6];
        }

        public final boolean hasEqualityConstraints() {
            return (this.getAE() != null) && (this.getAE().getRowDim() > 0);
        }

        public final boolean hasInequalityConstraints() {
            return (this.getAI() != null) && (this.getAI().getRowDim() > 0);
        }

        public final boolean hasObjective() {
            return (this.getQ() != null) || (this.getC() != null);
        }

        public final void resetLE() {
            this.getLE().fillAll(PrimitiveMath.ZERO);
        }

        public final void resetLI() {
            this.getLI().fillAll(PrimitiveMath.ZERO);
        }

        public final void resetX() {
            this.getX().fillAll(PrimitiveMath.ZERO);
        }

        public final void setLE(final int index, final double value) {
            this.getLE().set(index, 0, value);
        }

        public final void setLI(final int index, final double value) {
            this.getLI().set(index, 0, value);
        }

        public final void setX(final int index, final double value) {
            this.getX().set(index, 0, value);
        }

        @Override
        public final String toString() {

            final StringBuilder retVal = new StringBuilder("<" + this.getClass().getSimpleName() + ">");

            retVal.append("\n[AE] = " + (this.getAE() != null ? PrimitiveMatrix.FACTORY.copy(this.getAE()) : "?"));

            retVal.append("\n[BE] = " + (this.getBE() != null ? PrimitiveMatrix.FACTORY.copy(this.getBE()) : "?"));

            retVal.append("\n[Q] = " + (this.getQ() != null ? PrimitiveMatrix.FACTORY.copy(this.getQ()) : "?"));

            retVal.append("\n[C] = " + (this.getC() != null ? PrimitiveMatrix.FACTORY.copy(this.getC()) : "?"));

            retVal.append("\n[AI] = " + (this.getAI() != null ? PrimitiveMatrix.FACTORY.copy(this.getAI()) : "?"));

            retVal.append("\n[BI] = " + (this.getBI() != null ? PrimitiveMatrix.FACTORY.copy(this.getBI()) : "?"));

            retVal.append("\n[X] = " + (this.getX() != null ? PrimitiveMatrix.FACTORY.copy(this.getX()) : "?"));

            retVal.append("\n[LE] = " + (this.getLE() != null ? PrimitiveMatrix.FACTORY.copy(this.getLE()) : "?"));

            retVal.append("\n[LI] = " + (this.getLI() != null ? PrimitiveMatrix.FACTORY.copy(this.getLI()) : "?"));

            retVal.append("\n[SE] = " + (this.getSE() != null ? PrimitiveMatrix.FACTORY.copy(this.getSE()) : "?"));

            retVal.append("\n[SI] = " + (this.getSI() != null ? PrimitiveMatrix.FACTORY.copy(this.getSI()) : "?"));

            retVal.append("\n</" + this.getClass().getSimpleName() + ">");

            return retVal.toString();
        }

        private final void balanceEqualityConstraints() {

            final PhysicalStore<Double> tmpBody = this.getAE().copy();
            final PhysicalStore<Double> tmpRHS = this.getBE().copy();

            this.balanceRows(tmpBody, tmpRHS, true);

            myMatrices[0] = tmpBody;
            myMatrices[1] = tmpRHS;

            this.validate();
        }

        private final void balanceInequalityConstraints() {

            final PhysicalStore<Double> tmpBody = this.getAI().copy();
            final PhysicalStore<Double> tmpRHS = this.getBI().copy();

            this.balanceRows(tmpBody, tmpRHS, false);

            myMatrices[4] = tmpBody;
            myMatrices[5] = tmpRHS;

            this.validate();
        }

        private final double balanceMatrices(final PhysicalStore<Double>[] someMatrices) {

            final AggregatorFunction<Double> tmpLargestAggr = PrimitiveAggregator.getCollection().largest();
            final AggregatorFunction<Double> tmpSmallestAggr = PrimitiveAggregator.getCollection().smallest();

            for (final PhysicalStore<Double> tmpMatrix : someMatrices) {
                if (tmpMatrix != null) {
                    tmpMatrix.visitAll(tmpLargestAggr);
                    tmpMatrix.visitAll(tmpSmallestAggr);
                }
            }

            final double tmpExponent = OptimisationUtils.getAdjustmentFactorExponent(tmpLargestAggr, tmpSmallestAggr);
            final double tmpFactor = Math.pow(TEN, tmpExponent);

            final UnaryFunction<Double> tmpModifier = new PreconfiguredSecond<Double>(PrimitiveFunction.MULTIPLY, tmpFactor);

            for (final PhysicalStore<Double> tmpMatrix : someMatrices) {
                if (tmpMatrix != null) {
                    tmpMatrix.modifyAll(tmpModifier);
                }
            }

            return tmpFactor;
        }

        private final void balanceObjective() {

            final PhysicalStore<Double>[] tmpMatrices = new PhysicalStore[2];

            if (this.getQ() != null) {
                tmpMatrices[0] = this.getQ().copy();
            }
            if (this.getC() != null) {
                tmpMatrices[1] = this.getC().copy();
            }

            this.balanceMatrices(tmpMatrices);
            myMatrices[2] = tmpMatrices[0];
            myMatrices[3] = tmpMatrices[1];

            this.validate();

        }

        private final void balanceRows(final PhysicalStore<Double> tmpBody, final PhysicalStore<Double> tmpRHS, final boolean assertPositiveRHS) {

            final AggregatorFunction<Double> tmpLargestAggr = PrimitiveAggregator.getCollection().largest();
            final AggregatorFunction<Double> tmpSmallestAggr = PrimitiveAggregator.getCollection().smallest();

            double tmpExponent;
            double tmpFactor;

            UnaryFunction<Double> tmpModifier;

            for (int i = 0; i < tmpBody.getRowDim(); i++) {

                tmpLargestAggr.reset();
                tmpSmallestAggr.reset();

                tmpBody.visitRow(i, 0, tmpLargestAggr);
                tmpBody.visitRow(i, 0, tmpSmallestAggr);

                tmpRHS.visitRow(i, 0, tmpLargestAggr);
                tmpRHS.visitRow(i, 0, tmpSmallestAggr);

                tmpExponent = OptimisationUtils.getAdjustmentFactorExponent(tmpLargestAggr, tmpSmallestAggr);
                tmpFactor = Math.pow(TEN, tmpExponent);
                if (assertPositiveRHS && (Math.signum(tmpRHS.doubleValue(i, 0)) < ZERO)) {
                    tmpFactor = -tmpFactor;
                }

                tmpModifier = new PreconfiguredSecond<Double>(PrimitiveFunction.MULTIPLY, tmpFactor);

                tmpBody.modifyRow(i, 0, tmpModifier);
                tmpRHS.modifyRow(i, 0, tmpModifier);
            }
        }

        private final void validate() {

            if (this.hasEqualityConstraints()) {

                if (this.getAE() == null) {
                    throw new ProgrammingError("AE cannot be null!");
                } else if (this.getAE().getColDim() != this.countVariables()) {
                    throw new ProgrammingError("AE has the wrong number of columns!");
                } else if (this.getBE() == null) {
                    myMatrices[1] = PrimitiveDenseStore.FACTORY.makeZero(this.getAE().getRowDim(), 1);
                } else if (this.getAE().getRowDim() != this.getBE().getRowDim()) {
                    throw new ProgrammingError("AE and BE do not have the same number of rows!");
                } else if (this.getBE().getColDim() != 1) {
                    throw new ProgrammingError("BE must have precisely one column!");
                }

            } else {

                myMatrices[0] = null;
                myMatrices[1] = null;
            }

            if (this.hasObjective()) {

                if ((this.getQ() != null) && ((this.getQ().getRowDim() != this.countVariables()) || (this.getQ().getColDim() != this.countVariables()))) {
                    throw new ProgrammingError("Q has the wrong number of rows and/or columns!");
                }

                if (this.getC() == null) {
                    myMatrices[3] = PrimitiveDenseStore.FACTORY.makeZero(this.countVariables(), 1);
                } else if ((this.getC().getRowDim() != this.countVariables()) || (this.getC().getColDim() != 1)) {
                    throw new ProgrammingError("C has the wrong number of rows and/or columns!");
                }

            } else {

                myMatrices[2] = null;
                myMatrices[3] = null;
            }

            if (this.hasInequalityConstraints()) {

                if (this.getAI() == null) {
                    throw new ProgrammingError("AI cannot be null!");
                } else if (this.getAI().getColDim() != this.countVariables()) {
                    throw new ProgrammingError("AI has the wrong number of columns!");
                } else if (this.getBI() == null) {
                    myMatrices[5] = PrimitiveDenseStore.FACTORY.makeZero(this.getAI().getRowDim(), 1);
                } else if (this.getAI().getRowDim() != this.getBI().getRowDim()) {
                    throw new ProgrammingError("AI and BI do not have the same number of rows!");
                } else if (this.getBI().getColDim() != 1) {
                    throw new ProgrammingError("BI must have precisely one column!");
                }

            } else {

                myMatrices[4] = null;
                myMatrices[5] = null;
            }
        }

        protected final MatrixStore<Double>[] getArray() {
            return myMatrices.clone();
        }

        /**
         * Lagrange multipliers / dual variables for Inequalities
         */
        final PhysicalStore<Double> getLI() {
            if (myMatrices[8] == null) {
                myMatrices[8] = PrimitiveDenseStore.FACTORY.makeZero(this.countInequalityConstraints(), 1);
            }
            return (PhysicalStore<Double>) myMatrices[8];
        }

        /**
         * Slack for Inequalities: [SI] = [BI] - [AI][X]
         */
        final PhysicalStore<Double> getSI() {

            PhysicalStore<Double> retVal = null;

            if ((this.getAI() != null) && (this.getBI() != null) && (this.getX() != null)) {

                retVal = this.getBI().copy();

                retVal.fillMatching(retVal, PrimitiveFunction.SUBTRACT, this.getAI().multiplyRight(this.getX()));
            }

            return retVal;
        }

    }

    protected static abstract class AbstractBuilder<B extends AbstractBuilder<?, ?>, S extends BaseSolver> {

        @SuppressWarnings("unchecked")
        private final MatrixStore.Builder<Double>[] myBuilders = new MatrixStore.Builder[6];
        private final ExpressionsBasedModel myModel;

        protected AbstractBuilder() {

            super();

            myModel = null;

            myBuilders[0] = null;
            myBuilders[1] = null;

            myBuilders[2] = null;
            myBuilders[3] = null;

            myBuilders[4] = null;
            myBuilders[5] = null;
        }

        protected AbstractBuilder(final ExpressionsBasedModel aModel) {

            super();

            myModel = aModel;

            myBuilders[0] = null;
            myBuilders[1] = null;

            myBuilders[2] = null;
            myBuilders[3] = null;

            myBuilders[4] = null;
            myBuilders[5] = null;
        }

        protected AbstractBuilder(final Matrices matrices) {

            super();

            myModel = null;

            if (matrices.hasEqualityConstraints()) {
                this.equalities(matrices.getAE(), matrices.getBE());
            }

            if (matrices.hasObjective()) {
                if (matrices.getQ() != null) {
                    this.objective(matrices.getQ(), matrices.getC());
                } else {
                    this.objective(matrices.getC());
                }
            }

            if (matrices.hasInequalityConstraints()) {
                this.inequalities(matrices.getAI(), matrices.getBI());
            }
        }

        protected AbstractBuilder(final MatrixStore<Double> C) {

            super();

            myModel = null;

            myBuilders[0] = null;
            myBuilders[1] = null;

            myBuilders[2] = null;
            myBuilders[3] = C.builder();

            myBuilders[4] = null;
            myBuilders[5] = null;
        }

        protected AbstractBuilder(final MatrixStore<Double> Q, final MatrixStore<Double> C) {

            super();

            myModel = null;

            myBuilders[0] = null;
            myBuilders[1] = null;

            myBuilders[2] = Q.builder();
            if (C != null) {
                myBuilders[3] = C.builder();
            } else {
                myBuilders[3] = ZeroStore.makePrimitive(Q.getRowDim(), 1).builder();
            }

            myBuilders[4] = null;
            myBuilders[5] = null;
        }

        protected AbstractBuilder(final MatrixStore<Double>[] aMtrxArr) {

            super();

            myModel = null;

            if ((aMtrxArr.length >= 2) && (aMtrxArr[0] != null) && (aMtrxArr[1] != null)) {
                this.equalities(aMtrxArr[0], aMtrxArr[1]);
            }

            if (aMtrxArr.length >= 4) {
                if (aMtrxArr[2] != null) {
                    this.objective(aMtrxArr[2], aMtrxArr[3]);
                } else if (aMtrxArr[3] != null) {
                    this.objective(aMtrxArr[3]);
                }
            }

            if ((aMtrxArr.length >= 6) && (aMtrxArr[4] != null) && (aMtrxArr[5] != null)) {
                this.inequalities(aMtrxArr[4], aMtrxArr[5]);
            }
        }

        public abstract S build();

        @SuppressWarnings("unchecked")
        protected B equalities(final MatrixStore<Double> AE, final MatrixStore<Double> BE) {

            if (myBuilders[0] != null) {
                myBuilders[0].below(AE);
            } else {
                myBuilders[0] = AE.builder();
            }

            if (myBuilders[1] != null) {
                myBuilders[1].below(BE);
            } else {
                myBuilders[1] = BE.builder();
            }

            return (B) this;
        }

        protected ExpressionsBasedModel getModel() {
            return myModel;
        }

        @SuppressWarnings("unchecked")
        protected B inequalities(final MatrixStore<Double> AI, final MatrixStore<Double> BI) {

            if (myBuilders[4] != null) {
                myBuilders[4].below(AI);
            } else {
                myBuilders[4] = AI.builder();
            }

            if (myBuilders[5] != null) {
                myBuilders[5].below(BI);
            } else {
                myBuilders[5] = BI.builder();
            }

            return (B) this;
        }

        @SuppressWarnings("unchecked")
        protected B objective(final MatrixStore<Double> C) {

            myBuilders[2] = null;
            myBuilders[3] = C.builder();

            return (B) this;
        }

        @SuppressWarnings("unchecked")
        protected B objective(final MatrixStore<Double> Q, final MatrixStore<Double> C) {

            myBuilders[2] = Q.builder();
            if (C != null) {
                myBuilders[3] = C.builder();
            } else {
                myBuilders[3] = ZeroStore.makePrimitive(Q.getRowDim(), 1).builder();
            }

            return (B) this;
        }

        /**
         * [AE][X] == [BE]
         */
        MatrixStore<Double> getAE() {
            if (myBuilders[0] != null) {
                return myBuilders[0].build();
            } else {
                return null;
            }
        }

        /**
         * [AI][X] <= [BI]
         */
        MatrixStore<Double> getAI() {
            if (myBuilders[4] != null) {
                return myBuilders[4].build();
            } else {
                return null;
            }
        }

        /**
         * [AE][X] == [BE]
         */
        MatrixStore<Double> getBE() {
            if (myBuilders[1] != null) {
                return myBuilders[1].build();
            } else {
                return null;
            }
        }

        /**
         * [AI][X] <= [BI]
         */
        MatrixStore<Double> getBI() {
            if (myBuilders[5] != null) {
                return myBuilders[5].build();
            } else {
                return null;
            }
        }

        /**
         * Linear objective: [C]
         */
        MatrixStore<Double> getC() {
            if (myBuilders[3] != null) {
                return myBuilders[3].build();
            } else {
                return null;
            }
        }

        /**
         * Quadratic objective: [Q]
         */
        MatrixStore<Double> getQ() {
            if (myBuilders[2] != null) {
                return myBuilders[2].build();
            } else {
                return null;
            }
        }

    }

    private BaseSolver.Matrices myMatrices;

    private BaseSolver(final ExpressionsBasedModel aModel) {
        super(aModel);
    }

    protected BaseSolver(final ExpressionsBasedModel aModel, final BaseSolver.Matrices matrices) {

        super(aModel);

        myMatrices = matrices;
    }

    @Override
    public final String toString() {
        return myMatrices.toString();
    }

    protected final int countEqualityConstraints() {
        return myMatrices.countEqualityConstraints();
    }

    protected final int countInequalityConstraints() {
        return myMatrices.countInequalityConstraints();
    }

    protected final int countVariables() {
        return myMatrices.countVariables();
    }

    protected final MatrixStore<Double> getAE() {
        return myMatrices.getAE();
    }

    protected final MatrixStore<Double> getAI() {
        return myMatrices.getAI();
    }

    protected final MatrixStore<Double> getBE() {
        return myMatrices.getBE();
    }

    protected final MatrixStore<Double> getBI() {
        return myMatrices.getBI();
    }

    protected final MatrixStore<Double> getC() {
        return myMatrices.getC();
    }

    protected final MatrixStore<Double> getLE() {
        return myMatrices.getLE();
    }

    protected final MatrixStore<Double> getLI(final int... aRowSelector) {
        return myMatrices.getLI(aRowSelector);
    }

    protected final MatrixStore<Double> getQ() {
        return myMatrices.getQ();
    }

    protected final PhysicalStore<Double> getSE() {
        return myMatrices.getSE();
    }

    protected final MatrixStore<Double> getSI(final int... aRowSelector) {
        return myMatrices.getSI(aRowSelector);
    }

    protected final PhysicalStore<Double> getX() {
        return myMatrices.getX();
    }

    protected final boolean hasEqualityConstraints() {
        return myMatrices.hasEqualityConstraints();
    }

    protected final boolean hasInequalityConstraints() {
        return myMatrices.hasInequalityConstraints();
    }

    protected final boolean hasObjective() {
        return myMatrices.hasObjective();
    }

    protected final void resetLE() {
        myMatrices.resetLE();
    }

    protected final void resetLI() {
        myMatrices.resetLI();
    }

    protected final void resetX() {
        myMatrices.resetX();
    }

    protected final void setLE(final int index, final double value) {
        myMatrices.setLE(index, value);
    }

    protected final void setLI(final int index, final double value) {
        myMatrices.setLI(index, value);
    }

    protected final void setX(final int index, final double value) {
        myMatrices.setX(index, value);
    }

}
