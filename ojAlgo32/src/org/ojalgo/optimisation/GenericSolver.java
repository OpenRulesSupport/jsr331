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

import java.util.concurrent.atomic.AtomicInteger;

import org.ojalgo.RecoverableCondition;
import org.ojalgo.access.Access1D;
import org.ojalgo.function.multiary.MultiaryFunction;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.type.context.NumberContext;

public abstract class GenericSolver implements Optimisation.Solver {

    public final Optimisation.Options options = new Optimisation.Options();

    private final AtomicInteger myIterationsCount = new AtomicInteger(0);
    private final ExpressionsBasedModel myModel;
    private final MultiaryFunction<Double> myFunction;
    private long myResetTime;
    private State myState = State.UNEXPLORED;

    /**
     * @param aModel
     */
    protected GenericSolver(final ExpressionsBasedModel aModel) {

        super();

        myModel = aModel;
        if (aModel != null) {
            myFunction = aModel.getObjectiveExpression().toFunction();
        } else {
            myFunction = null;
        }
    }

    protected final double evaluateFunction(final Access1D<?> solution) {
        if ((myFunction != null) && (solution != null)) {
            return myFunction.invoke(solution);
        } else {
            return Double.NaN;
        }
    }

    protected final int getIterationsCount() {
        return myIterationsCount.get();
    }

    protected final ExpressionsBasedModel getModel() {
        return myModel;
    }

    protected final State getState() {
        return myState;
    }

    /**
     * Should be called after a completed iteration. The iterations count
     * is not "1" untill the first iteration is completed.
     */
    protected final int incrementIterationsCount() throws RecoverableCondition {

        final int retVal = myIterationsCount.incrementAndGet();

        if (retVal > options.iterations) {
            throw new RecoverableCondition("Too many iterations!");
        }

        return retVal;
    }

    protected final boolean isDebug() {
        return options.debug != null;
    }

    protected final boolean isFunctionSet() {
        return myFunction != null;
    }

    protected final boolean isModelSet() {
        return myModel != null;
    }

    /**
     * Should be called at the start of an iteration (before it starts)
     * to check if you should abort instead. Will return false if either
     * the iterations count or the execution time has reached their
     * respective limits.
     */
    protected final boolean isOkToContinue() {
        return (myIterationsCount.get() < options.iterations) && ((System.currentTimeMillis() - myResetTime) < options.time);
    }

    abstract protected boolean needsAnotherIteration();

    protected final void resetIterationsCount() {
        myIterationsCount.set(0);
        myResetTime = System.currentTimeMillis();
    }

    protected final void setState(final State aState) {
        myState = aState;
    }

    /**
     * @deprecated Kanske inte, men implementationen suger
     */
    @Deprecated
    protected final boolean validateSolution(final MatrixStore<Double> aSolution, final NumberContext aContext) {
        return myModel.validateSolution(BigDenseStore.FACTORY.copy(aSolution), aContext);
    }

}
