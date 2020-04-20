package jsetl;

import jsetl.annotation.NotNull;

import java.util.*;

/**
 * Rewrite rules for constraints over finite domains (and labeling of all supported variable types in {@code jsetl}).
 * Note that the implementation of the rewrite rule for labeling of {@code IntLVar},
 * {@code SetLVar}, {@code BoolLVar} and {@code ArrayList<>} of those are all located
 * in this class.
 * Constraints implemented include labeling, sum, difference, multiplication,
 * comparisons and so on.
 */
class RwRulesFD extends LibConstraintsRules {

    //////////////////////////////////////////////////////
    ////////////////// CONSTRUCTORS //////////////////////
    //////////////////////////////////////////////////////
    
    /**
     * Constructs an instance of rewrite rules and stores a reference to the solver
     * @param solver reference to the solver.
     */
    protected  
    RwRulesFD(@NotNull SolverClass solver) {
        super(solver);
    }

    //////////////////////////////////////////////////////
    ////////////////// PROTECTED METHODS /////////////////
    //////////////////////////////////////////////////////

    /**
     * Uses the appropriate method to solve the given atomic constraint.
     * @param aConstraint atomic constraint to solve.
     * @return {@code true} the constraint has been handled, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    @Override
    protected boolean solveConstraint(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        if (aConstraint.constraintKindCode == Environment.leCode)
                le(aConstraint);        // less or equal
        else if (aConstraint.constraintKindCode == Environment.ltCode)
                lt(aConstraint);        // less
        else if (aConstraint.constraintKindCode == Environment.geCode)
                ge(aConstraint); // greater or equal
        else if (aConstraint.constraintKindCode == Environment.gtCode)
                gt(aConstraint); // greater than
        else if (aConstraint.constraintKindCode == Environment.sumCode)
                sum(aConstraint);       // sum
        else if (aConstraint.constraintKindCode == Environment.subCode)
                sub(aConstraint);       // sub
        else if (aConstraint.constraintKindCode == Environment.mulCode)
                mul(aConstraint);       // mul
        else if (aConstraint.constraintKindCode == Environment.divCode)
                div(aConstraint);       // div
        else if (aConstraint.constraintKindCode == Environment.modCode)
                mod(aConstraint);       // mod
        else if (aConstraint.constraintKindCode == Environment.absCode)
            	abs(aConstraint); // absolute value
        else if (aConstraint.constraintKindCode == Environment.domCode)
                domain(aConstraint); 	  // FD domain
        else if (aConstraint.constraintKindCode == Environment.labelCode)
                label(aConstraint); 	  // label
        else
                return false; // constraint not handled
        return true; // constraint handled.
    }

    /**
     * Solves an atomic constraint of the type {@code x is less than or equal to y}.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void 
    le(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.leCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        if (aConstraint.argument2 instanceof IntLVar)
            // X le Y.
            leLvar(x, (IntLVar) aConstraint.argument2, aConstraint);
        else
            // X le k.
            leLvarInt(x, (Integer) aConstraint.argument2, aConstraint);
    }


    /**
     * Solves an atomic constraint of the type {@code x is less than y}.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void 
    lt(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ltCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        if (aConstraint.argument2 instanceof IntLVar)
            // X lt Y.
            ltLvar(x, (IntLVar) aConstraint.argument2, aConstraint);
        else
            // X lt k.
            ltLvarInt(x, (Integer) aConstraint.argument2, aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code x is greater than or equal to to y}.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void
    ge(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.geCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        if (aConstraint.argument2 instanceof IntLVar)
            // X ge Y <==> Y le X.
            leLvar((IntLVar) aConstraint.argument2, x, aConstraint);
        else
            // X ge i <==> Y le X AND Y = k.
            leLvar(new IntLVar((Integer) aConstraint.argument2), x, aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code x is greater than y}.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void
    gt(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.gtCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        if (aConstraint.argument2 instanceof IntLVar)
            // X gt Y <==> Y lt X.
            ltLvar((IntLVar) aConstraint.argument2, x, aConstraint);
        else
            // X gt k <==> Y lt X AND Y = k.
            ltLvar(new IntLVar((Integer) aConstraint.argument2), x, aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code x = y + z}.
     * In which {@code x, y, z} are respectively the first, second
     * and third argument of the atomic constraint.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void
    sum(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.sumCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        IntLVar y = (IntLVar) aConstraint.argument2;
        if (aConstraint.argument3 instanceof LVar)
            // X = Y + Z.
            solver.domainRulesFD.sumRule(x, y, (IntLVar) aConstraint.argument3, aConstraint);
        else
            // X = Y + K.
            solver.domainRulesFD.sumRule(x, y, (Integer) aConstraint.argument3, aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code x = y - z}.
     * In which {@code x, y, z} are respectively the first, second
     * and third argument of the atomic constraint.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void
    sub(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.subCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        IntLVar y = (IntLVar) aConstraint.argument2;
        if (aConstraint.argument3 instanceof LVar)
            // X = Y - Z.
            solver.domainRulesFD.sumRule(y, x, (IntLVar) aConstraint.argument3, aConstraint);
        else
            // X = Y - k.
            solver.domainRulesFD.sumRule(y, x, (Integer) aConstraint.argument3, aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code x = y * z}.
     * In which {@code x, y, z} are respectively the first, second
     * and third argument of the atomic constraint.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void
    mul(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.mulCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        IntLVar y = (IntLVar) aConstraint.argument2;
        if (aConstraint.argument3 instanceof LVar)
            // X = Y * Z.
            solver.domainRulesFD.mulRule(x, y, (IntLVar) aConstraint.argument3, aConstraint);
        else
            // X = Y * k.
            solver.domainRulesFD.mulRule(x, y, (Integer) aConstraint.argument3, aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code x = y / z}.
     * In which {@code x, y, z} are respectively the first, second
     * and third argument of the atomic constraint.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void div(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.divCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        IntLVar y = (IntLVar) aConstraint.argument2;
        if (aConstraint.argument3 instanceof LVar)
            // X = Y / Z.
            solver.domainRulesFD.mulRule(y, x, (IntLVar) aConstraint.argument3, aConstraint);
        else
            // X = Y / k.
            solver.domainRulesFD.mulRule(y, x, (Integer) aConstraint.argument3, aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code x = y mod z}.
     * In which {@code x, y, z} are respectively the first, second
     * and third argument of the atomic constraint.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void mod(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.modCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        IntLVar y = (IntLVar) aConstraint.argument2;
        if (aConstraint.argument3 instanceof LVar)
            // X = Y mod Z.
            solver.domainRulesFD.modRule(x, y, (IntLVar) aConstraint.argument3, aConstraint);
        else
            // X = Y mod k.
            solver.domainRulesFD.modRule(x, y, (Integer) aConstraint.argument3, aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code x = abs(y)}
     * In which {@code x, y} are respectively the first and second
     * argument of the atomic constraint.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void
    abs(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.absCode;

        manageEquChains(aConstraint);

        IntLVar x = (IntLVar) aConstraint.argument1;
        IntLVar y = (IntLVar) aConstraint.argument2;

        solver.domainRulesFD.absRule(x, y, aConstraint);
    }

    /**
     * Rewrite rules implementing the labeling mechanism over integer logical variables.
     * The first argument of the atomic constraint is the variable (or array list of variables)
     * to label.
     * The second argument of the constraint is the {@code LabelingOptions} to use for labeling.
     * The third argument
     * @param aConstraint labeling atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void label(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof IntLVar
                || aConstraint.argument1 instanceof SetLVar
                || aConstraint.argument1 instanceof BoolLVar
                || aConstraint.argument1 instanceof ArrayList;

        assert aConstraint.argument2 instanceof LabelingOptions;
        assert aConstraint.argument3 == null ||
                (aConstraint.argument3 instanceof MultiInterval && aConstraint.argument1 instanceof IntLVar)
                || (aConstraint.argument3 instanceof Integer && aConstraint.argument1 instanceof SetLVar)
                || (aConstraint.argument3 instanceof Boolean && aConstraint.argument1 instanceof BoolLVar);
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.labelCode;

        if (aConstraint.argument1 instanceof IntLVar)
            solver.domainRulesFD.labelRule((IntLVar) aConstraint.argument1, (LabelingOptions) aConstraint.argument2, aConstraint);
        else if (aConstraint.argument1 instanceof SetLVar)
            solver.domainRulesFS.labelRule((SetLVar) aConstraint.argument1, (LabelingOptions) aConstraint.argument2, aConstraint);
        else if (aConstraint.argument1 instanceof BoolLVar)
            solver.rwRulesBool.labelRule((BoolLVar) aConstraint.argument1, (LabelingOptions) aConstraint.argument2, aConstraint);
        else
            label((ArrayList<?>) aConstraint.argument1, (LabelingOptions) aConstraint.argument2, aConstraint);
    }

    /**
     * Rewriting rule for atomic constraints of the kind {@code domain(A,B)}: the domain of A is B,
     * in which A,B are respectively the first and second argument of the atomic constraint.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void domain(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.domCode;
        assert aConstraint.argument1 instanceof IntLVar && aConstraint.argument2 instanceof MultiInterval
                || aConstraint.argument1 instanceof SetLVar && aConstraint.argument2 instanceof SetInterval;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

        manageEquChains(aConstraint);

        if (aConstraint.argument1 instanceof IntLVar)
            solver.domainRulesFD.domainRule((IntLVar) aConstraint.argument1, new MultiInterval((MultiInterval) aConstraint.argument2), aConstraint);
        else
            solver.domainRulesFS.domainRule((SetLVar) aConstraint.argument1, (SetInterval) aConstraint.argument2, aConstraint);

        aConstraint.setSolved(true);
        solver.storeUnchanged = false;
    }

    //////////////////////////////////////////////////////
    ////////////////// PRIVATE METHODS ///////////////////
    //////////////////////////////////////////////////////

    /**
     * Solves an atomic constraint of the type {@code l1 is less than or equal to l2}.
     * @param l1 first argument.
     * @param l2 second argument.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void
    leLvar(@NotNull IntLVar l1, @NotNull IntLVar l2, @NotNull AConstraint aConstraint) {
        assert l1 != null;
        assert l2 != null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

            if (!l1.isInitialized())
                if (!l2.isInitialized()) {
                    // X <= Y.
                    solver.domainRulesFD.leRule(l1, l2, aConstraint);
                    return;
                }
                else {
                    // X <= k.
                    leLvarInt(l1, (Integer) l2.val, aConstraint);
                    return;
                }
            else if (!l2.isInitialized()) {
                // k <= X.
                int k = (Integer) l1.val;
                if (k <= Interval.INF) {
                    aConstraint.setSolved(true);
                    return;
                }
                else if (k > Interval.SUP)
                    solver.fail(aConstraint);
                MultiInterval mi = new MultiInterval(k, Interval.SUP);
                solver.domainRulesFD.domainRule(l2, mi, aConstraint);
                aConstraint.setSolved(true);
                return;
            }
            else
                // h <= k.
                if ((Integer) l1.val
                 <= (Integer) l2.val) {
                    aConstraint.setSolved(true);
                    return;
                } 
                else
                    solver.fail(aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code l1 is less than or equal to k}.
     * @param l1 first argument.
     * @param k second argument.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void
    leLvarInt(@NotNull IntLVar l1, @NotNull Integer k, @NotNull AConstraint aConstraint) {
        assert l1 != null;
        assert k != null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

            if (!l1.isInitialized()) {
                // X <= k.
                if (k >= Interval.SUP) {
                    aConstraint.setSolved(true);
                    return;
                }
                else if (k < Interval.INF)
                    solver.fail(aConstraint);
                solver.domainRulesFD.leRule(l1, k, aConstraint);
                aConstraint.setSolved(true);
                return;
            }
            else 
                // h <= k.
                if ((Integer) l1.val <= k) {
                    aConstraint.setSolved(true);
                    return;
                } 
                else
                    solver.fail(aConstraint);

    }

    /**
     * Solves an atomic constraint of the type {@code l1 is less than l2}.
     * @param l1 first argument.
     * @param l2 second argument.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void
    ltLvar(@NotNull IntLVar l1, @NotNull IntLVar l2, @NotNull AConstraint aConstraint) {
        assert l1 != null;
        assert l2 != null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

            if (!l1.isInitialized())
                if (!l2.isInitialized()) {
                    // X < Y.
                    solver.domainRulesFD.ltRule(l1, l2, aConstraint);
                    return;
                }
                else {
                    // X < k.
                    ltLvarInt(l1, (Integer) l2.val, aConstraint);
                    return;
                }
            else if (!l2.isInitialized()) {
                // k < X.
                int k = (Integer) l1.val;
                if (k <= Interval.INF) {
                    aConstraint.setSolved(true);
                    return;
                }
                else if ((Integer) l1.val >= Interval.SUP)
                    solver.fail(aConstraint);
                MultiInterval mi = new MultiInterval(k + 1, Interval.SUP);
                solver.domainRulesFD.domainRule(l2, mi, aConstraint);
                aConstraint.setSolved(true);
                return;
            }
            else 
                if ((Integer) l1.val < (Integer) l2.val) {
                    aConstraint.setSolved(true);
                    return;
                } 
                else
                    solver.fail(aConstraint);
    }

    /**
     * Solves an atomic constraint of the type {@code l1 is less than k}.
     * @param l1 first argument.
     * @param k second argument.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void
    ltLvarInt(@NotNull IntLVar l1, @NotNull Integer k, @NotNull AConstraint aConstraint) {
        assert l1 != null;
        assert k != null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

            if (!l1.isInitialized()) {
                // X < k.
                if (k >= Interval.SUP) {
                    aConstraint.setSolved(true);
                    return;
                }
                else if (k <= Interval.INF)
                    solver.fail(aConstraint);
                solver.domainRulesFD.ltRule(l1, k, aConstraint);
                aConstraint.setSolved(true);
                return;
            }
            else 
                // h < k.
                if ((Integer) l1.val< k) {
                    aConstraint.setSolved(true);
                    return;
                } 
                else
                    solver.fail(aConstraint);

    }

    /**
     * Rewrite rules implementing the labeling mechanism over a variables of variables using the given labeling options.
     * @param variables variables of variables to label.
     * @param labelingOptions labeling options.
     * @param aConstraint labeling atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    @SuppressWarnings("unchecked")
    private void
    label(@NotNull ArrayList<?> variables, @NotNull LabelingOptions labelingOptions, @NotNull AConstraint aConstraint) {
        assert variables != null;
        assert labelingOptions != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.labelCode;
        assert aConstraint.argument1 == variables;
        assert aConstraint.argument2 == labelingOptions;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

        if (variables.isEmpty()) {
            aConstraint.setSolved(true);
            return;
        }
        Object first = variables.iterator().next();
        if (first instanceof IntLVar)
            solver.domainRulesFD.labelRule((ArrayList<IntLVar>) variables, labelingOptions, aConstraint);
        else if (first instanceof SetLVar)
            solver.domainRulesFS.labelRule((ArrayList<SetLVar>) variables, labelingOptions, aConstraint);
        else
            solver.rwRulesBool.labelRule((ArrayList<BoolLVar>) variables, labelingOptions, aConstraint);
    }

}
