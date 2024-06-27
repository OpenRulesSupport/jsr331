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

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;

import org.ojalgo.ProgrammingError;
import org.ojalgo.RecoverableCondition;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.BigMatrix;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.type.CalendarDateUnit;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

public interface Optimisation {

    /**
     * Constraint
     * 
     * @author apete
     */
    public static interface Constraint extends Optimisation {

        /**
         * May return null
         */
        BigDecimal getLowerLimit();

        /**
         * May return null
         */
        BigDecimal getUpperLimit();

        /**
         * The Constraint has a lower or an upper limit (possibly both).
         */
        boolean isConstraint();

        /**
         * The Constraint has both a lower limit and an upper limit, and they
         * are equal.
         */
        boolean isEqualityConstraint();

        /**
         * The Constraint has a lower limit, and the upper limit (if it exists)
         * is different.
         */
        boolean isLowerConstraint();

        /**
         * The Constraint has an upper limit, and the lower limit (if it exists)
         * is different.
         */
        boolean isUpperConstraint();

    }

    public static interface Model<S extends Optimisation.Solver> extends Optimisation {

        /**
         * @return A solver that should be able to solve this problem. 
         */
        S getDefaultSolver();

        Optimisation.Result maximise();

        Optimisation.Result minimise();

        /**
         * @return true If eveything is ok
         * @return false The model is structurally ok, but the "value" breaks
         * constraints - the solution is infeasible.
         * @throws ValidationException If something is structurally impossible.
         * Maybe you've defined a lower bound that is leger than the upper bound.
         */
        boolean validate() throws ValidationException;

    }

    /**
     * Objective
     * 
     * @author apete
     */
    public static interface Objective extends Optimisation {

        /**
         * May return null
         */
        BigDecimal getContributionWeight();

        /**
         * @return true if this objective has a non zero contribution weight.
         */
        boolean isObjective();

    }

    public static final class Options implements Optimisation, Cloneable {

        private static final PrintStream DEBUG_STREAM = null;
        private static final int ITERATIONS_LIMIT = Integer.MAX_VALUE;
        private static final NumberContext PRINT_CONTEXT = NumberContext.getGeneral(6, 10);
        private static final NumberContext PROBLEM_CONTEXT = NumberContext.getGeneral(10, RoundingMode.HALF_EVEN);
        private static final NumberContext RESULT_CONTEXT = new NumberContext(12, 14, RoundingMode.HALF_EVEN);
        private static final NumberContext SLACK_CONTEXT = NumberContext.getGeneral(10, RoundingMode.HALF_EVEN);
        private static final long TIME_LIMIT = CalendarDateUnit.MILLENIUM.size();

        /**
         * If this is null nothing printed, if it is not null then debug
         * statements are printed to that PrintStream.
         */
        public PrintStream debug = DEBUG_STREAM;
        /**
         * Solution variables; primary and dual (langrange multipliers)
         * @deprecated v33 Use one of the others instead
         */
        @Deprecated
        public NumberContext deprSolution = NumberContext.getGeneral(6, RoundingMode.HALF_EVEN);
        /**
         * The maximmum number of iterations allowed for the solve() command.
         */
        public int iterations = ITERATIONS_LIMIT;
        /**
         * For display only!
         */
        public NumberContext print = PRINT_CONTEXT;
        /**
         * Problem parameters; constraints and objective function
         */
        public NumberContext problem = PROBLEM_CONTEXT;
        /**
         * Used, only, when copying a solvers solution back to the model
         */
        public NumberContext result = RESULT_CONTEXT;
        /**
         * Used when determining if a constraint is violated or not.
         * Calculate the slack - zero if the constraint is "active".
         */
        public NumberContext slack = SLACK_CONTEXT;
        /**
         * The maximmum number of millis allowed for the solve() command.
         */
        public long time = TIME_LIMIT;

        Options() {
            super();
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

    }

    public static final class Result implements Optimisation, Access1D<BigDecimal>, Comparable<Optimisation.Result> {

        private final Access1D<?> mySolution;
        private final Optimisation.State myState;
        private final double myValue; // Objective Function Value

        public Result(final Optimisation.State state, final double value, final Access1D<?> solution) {

            super();

            ProgrammingError.throwIfNull(state);
            ProgrammingError.throwIfNull(solution);

            myState = state;
            myValue = value;
            mySolution = solution;
        }

        public int compareTo(final Result reference) {

            final double tmpReference = reference.getValue();

            if (myValue > tmpReference) {
                return 1;
            } else if (myValue < tmpReference) {
                return -1;
            } else {
                return 0;
            }
        }

        public double doubleValue(final int anInd) {
            return mySolution.doubleValue(anInd);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final Result other = (Result) obj;
            if (myState != other.myState) {
                return false;
            }
            if (Double.doubleToLongBits(myValue) != Double.doubleToLongBits(other.myValue)) {
                return false;
            }
            return true;
        }

        public BigDecimal get(final int anInd) {
            return TypeUtils.toBigDecimal(mySolution.get(anInd));
        }

        /**
         * @deprecated v33 Use {@linkplain #get(int)} or {@linkplain #doubleValue(int)} instead.
         */
        @Deprecated
        public BasicMatrix getSolution() {
            return BigMatrix.FACTORY.columns(this);
        }

        public Optimisation.State getState() {
            return myState;
        }

        public double getValue() {
            return myValue;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((myState == null) ? 0 : myState.hashCode());
            long temp;
            temp = Double.doubleToLongBits(myValue);
            result = (prime * result) + (int) (temp ^ (temp >>> 32));
            return result;
        }

        public Iterator<BigDecimal> iterator() {
            return new Iterator1D<BigDecimal>(this);
        }

        public int size() {
            return mySolution.size();
        }

        @Override
        public String toString() {
            return myState + " " + myValue + " @ " + PrimitiveDenseStore.FACTORY.columns(mySolution);
        }
    }

    /**
     * <p>
     * An {@linkplain Optimisation.Solver} instance implements a specific
     * optimisation algorithm. Typically each algorithm solves problems of
     * (at least) one problem category. {@linkplain Optimisation.Model}
     * represents a problem category.
     * </p><p>
     * A solver internally works with primitive double.
     * </p>
     * 
     * @author apete
     */
    public static interface Solver extends Optimisation {

        Optimisation.Result solve();

    }

    public static enum State implements Optimisation {

        /**
         * Approximate and/or Intermediate solution - Iteration point
         * Probably infeasible, but still "good"
         */
        APPROXIMATE(8, 32),

        /**
         * Unique (and optimal) solution - there is no other solution that
         * is equal or better
         */
        DISTINCT(256, 112),

        /**
         * Failed
         */
        FAILED(-1, -128),

        /**
         * Solved - a solution that complies with all constraints
         */
        FEASIBLE(16, 64),

        /**
         * Optimal, but not distinct solution - there are other solutions that
         * are equal, but not better.
         */
        INDISTINCT(-128, 96),

        /**
         * No solution that complies with all constraints exists
         */
        INFEASIBLE(-8, -64),

        /**
         * Model/problem components/entities are invalid
         */
        INVALID(-2, -128),

        /**
         * Approximate and/or Intermediate solution - Iteration point
         * Probably infeasible, but still "good"
         * @deprecated v33 Use APPROXIMATE instead
         */
        ITERATION(8, 32),

        /**
         * New/changed problem
         * @deprecated v33 Use UNEXPLORED instead
         */
        NEW(0, 0),

        /**
         * Optimal solution - there is no better
         */
        OPTIMAL(64, 96),

        /**
         * There's an infinite number of feasible solutions and no bound on the objective function value
         */
        UNBOUNDED(-32, -32),

        /**
         * New/changed problem
         */
        UNEXPLORED(0, 0),

        /**
         * Unique (and optimal) solution - there is no other solution that
         * is equal or better
         * @deprecated v33 Use DISTINCT instead
         */
        UNIQUE(256, 112),

        /**
         * Model/problem components/entities are valid
         */
        VALID(4, 0);

        private final byte myByte;
        private final int myValue;

        State(final int aValue, final int aByte) {
            myValue = aValue;
            myByte = (byte) aByte;
        }

        public boolean isDistinct() {
            return this.absValue() >= DISTINCT.absValue();
        }

        /**
         * 0
         * 
         * @deprecated v33
         */
        @Deprecated
        public boolean isExactly(final State aState) {
            return myByte == aState.byteValue();
        }

        /**
         * FAILED, INVALID, INFEASIBLE, UNBOUNDED or INDISTINCT
         */
        public boolean isFailure() {
            return myValue < 0;
        }

        public boolean isFeasible() {
            return this.absValue() >= FEASIBLE.absValue();
        }

        /**
         * 0
         * 
         * @deprecated v33
         */
        @Deprecated
        public boolean isLessThan(final State aState) {
            return myByte < aState.byteValue();
        }

        /**
         * 0
         * 
         * @deprecated v33
         */
        @Deprecated
        public boolean isMoreThan(final State aState) {
            return myByte > aState.byteValue();
        }

        /**
         * 17
         * 
         * @deprecated v33
         */
        @Deprecated
        public boolean isNotLessThan(final State aState) {
            return myByte >= aState.byteValue();
        }

        /**
         * 0
         * 
         * @deprecated v33
         */
        @Deprecated
        public boolean isNotMoreThan(final State aState) {
            return myByte <= aState.byteValue();
        }

        public boolean isOptimal() {
            return this.absValue() >= OPTIMAL.absValue();
        }

        /**
         * VALID, APPROXIMATE, FEASIBLE, OPTIMAL or DISTINCT
         */
        public boolean isSuccess() {
            return myValue > 0;
        }

        /**
         * UNEXPLORED
         */
        public boolean isUnexplored() {
            return myValue == 0;
        }

        public boolean isValid() {
            return this.absValue() >= VALID.absValue();
        }

        private int absValue() {
            return Math.abs(myValue);
        }

        private byte byteValue() {
            return myByte;
        }

    }

    public static final class ValidationException extends RecoverableCondition implements Optimisation {

        public ValidationException(final String aDescription) {
            super(aDescription);
        }

    }

}
