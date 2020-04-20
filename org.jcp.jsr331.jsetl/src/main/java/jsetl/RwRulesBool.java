package jsetl;

import jsetl.annotation.NotNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Rewrite rules for constraints over boolean logical variables, {@code BoolLVar}s.
 */
class RwRulesBool extends LibConstraintsRules {

    //////////////////////////////////////////////////////
    ////////////////// DATA MEMBERS //////////////////////
    //////////////////////////////////////////////////////

    /**
     * Handler for equality solving.
     */
    private RwRulesEq eqHandler;


    //////////////////////////////////////////////////////
    ////////////////// CONSTRUCTORS //////////////////////
    //////////////////////////////////////////////////////

    /**
     * Creates an instance of rewrite rules and stores a reference to the solver.
     * @param solver reference to the solver.
     */
    RwRulesBool(@NotNull SolverClass solver) {
        super(solver);
        assert solver != null;
        eqHandler = new RwRulesEq(solver);
    }


    //////////////////////////////////////////////////////
    ////////////////// PROTECTED METHODS /////////////////
    //////////////////////////////////////////////////////

    /**
     * Uses rewrite rules to solve the given atomic constraint.
     * @param aConstraint atomic constraint to solve.
     * @return {@code true} if some rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if the atomic constraint was found to be unsatisfiable.
     */
    @Override
    protected boolean 
    solveConstraint(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;

        if (aConstraint.constraintKindCode == Environment.andBoolCode)
                and(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.orBoolCode)
                or(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.impliesBoolCode)
                implies(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.iffBoolCode)
                iff(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.notBoolCode)
                not(aConstraint);
        else 
                return false;
        return true;
    }

    /**
     * Rewrite rule used to solve "and" constraint (b_1 = b_2 and b_3).
     * @param aConstraint and atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void 
    and(@NotNull AConstraint aConstraint)  {
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.andBoolCode;
        assert aConstraint.argument1 instanceof BoolLVar;
        assert aConstraint.argument2 instanceof BoolLVar;
        assert aConstraint.argument3 instanceof BoolLVar;
        assert aConstraint.argument4 == null;

        manageEquChains(aConstraint);

        BoolLVar z = (BoolLVar) aConstraint.argument1;
        BoolLVar x = (BoolLVar) aConstraint.argument2;
        BoolLVar y = (BoolLVar) aConstraint.argument3;

        if (z.isTrue()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, x, true));
            eqHandler.eq(new AConstraint(Environment.eqCode, y, true));
            aConstraint.setSolved(true);
            return;
        }
        if (x.isBound()) {
            if (y.isBound())
                eqHandler.eq(new AConstraint(Environment.eqCode, z,
                        x.getValue() && y.getValue()));
            else if (x.isTrue())
                eqHandler.eq(new AConstraint(Environment.eqCode, z, y));
            else
                eqHandler.eq(new AConstraint(Environment.eqCode, z, false));
            aConstraint.setSolved(true);
            return;
        }
        if (y.isBound()) {
            if (y.isTrue())
                eqHandler.eq(new AConstraint(Environment.eqCode, z, x));
            else
                eqHandler.eq(new AConstraint(Environment.eqCode, z, false));
            aConstraint.setSolved(true);
            return;
        }
        if (x.equals(z)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, y, true));
            aConstraint.setSolved(true);
            return;
        }
        if (y.equals(z)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, x, true));
            aConstraint.setSolved(true);
            return;
        }
        if (x.equals(y)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, x));
            aConstraint.setSolved(true);
        }
    }

    /**
     * Rewrite rule used to solve "or" constraint (b_1 = b_2 OR b_3).
     * @param aConstraint or atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    or(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.orBoolCode;
        assert aConstraint.argument1 instanceof BoolLVar;
        assert aConstraint.argument2 instanceof BoolLVar;
        assert aConstraint.argument3 instanceof BoolLVar;
        assert aConstraint.argument4 == null;

        manageEquChains(aConstraint);

        BoolLVar z = (BoolLVar) aConstraint.argument1;
        BoolLVar x = (BoolLVar) aConstraint.argument2;
        BoolLVar y = (BoolLVar) aConstraint.argument3;

        if (z.isFalse()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, x, false));
            eqHandler.eq(new AConstraint(Environment.eqCode, y, false));
            aConstraint.setSolved(true);
            return;
        }
        if (x.isBound()) {
            if (y.isBound())
                eqHandler.eq(new AConstraint(Environment.eqCode, z,
                        x.getValue() || y.getValue()));
            else if (x.isTrue())
                eqHandler.eq(new AConstraint(Environment.eqCode, z, true));
            else
                eqHandler.eq(new AConstraint(Environment.eqCode, z, y));
            aConstraint.setSolved(true);
            return;
        }
        if (y.isBound()) {
            if (y.isTrue())
                eqHandler.eq(new AConstraint(Environment.eqCode, z, true));
            else
                eqHandler.eq(new AConstraint(Environment.eqCode, z, x));
            aConstraint.setSolved(true);
            return;
        }
        if (x.equals(y)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, x));
            aConstraint.setSolved(true);
            return;
        }
    }

    /**
     * Rewrite rule used to solve "implies" constraint (b_1 = b_2 implies b_3).
     * @param aConstraint implies atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    implies(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.impliesBoolCode;
        assert aConstraint.argument1 instanceof BoolLVar;
        assert aConstraint.argument2 instanceof BoolLVar;
        assert aConstraint.argument3 instanceof BoolLVar;
        assert aConstraint.argument4 == null;

        manageEquChains(aConstraint);

        BoolLVar z = (BoolLVar) aConstraint.argument1;
        BoolLVar x = (BoolLVar) aConstraint.argument2;
        BoolLVar y = (BoolLVar) aConstraint.argument3;

        if (z.isFalse()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, x, true));
            eqHandler.eq(new AConstraint(Environment.eqCode, y, false));
            aConstraint.setSolved(true);
            return;
        }
        if (x.isBound()) {
            if (y.isBound())
                eqHandler.eq(new AConstraint(Environment.eqCode, z,
                        !x.getValue() || y.getValue()));
            else if (x.isTrue())
                eqHandler.eq(new AConstraint(Environment.eqCode, z, y));
            else
                eqHandler.eq(new AConstraint(Environment.eqCode, z, true));
            aConstraint.setSolved(true);
            return;
        }
        if (y.isBound())
            if (y.isTrue()) {
                eqHandler.eq(new AConstraint(Environment.eqCode, z, true));
                aConstraint.setSolved(true);
                return;
            }
            else
                eqHandler.neq(new AConstraint(Environment.neqCode, z, x));
        if (x.equals(z)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, y, true));
            return;
        }
        if (x.equals(y)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, true));
            aConstraint.setSolved(true);
        }
    }

    /**
     * Rewrite rule used to solve "iff" constraint (b_1 = b_2 iff b_3).
     * @param aConstraint iff atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    iff(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.iffBoolCode;
        assert aConstraint.argument1 instanceof BoolLVar;
        assert aConstraint.argument2 instanceof BoolLVar;
        assert aConstraint.argument3 instanceof BoolLVar;
        assert aConstraint.argument4 == null;

        manageEquChains(aConstraint);

        BoolLVar z = (BoolLVar) aConstraint.argument1;
        BoolLVar x = (BoolLVar) aConstraint.argument2;
        BoolLVar y = (BoolLVar) aConstraint.argument3;

        if (x.isBound())
            if (y.isBound()) {
                eqHandler.eq(new AConstraint(Environment.eqCode, z,
                        !(x.getValue() ^ y.getValue())));
                aConstraint.setSolved(true);
                return;
            }
            else if (x.isTrue()) {
                eqHandler.eq(new AConstraint(Environment.eqCode, z, y));
                aConstraint.setSolved(true);
                return;
            }
            else
                eqHandler.neq(new AConstraint(Environment.neqCode, z, y));
        if (y.isBound())
            if (y.isTrue()) {
                eqHandler.eq(new AConstraint(Environment.eqCode, z, x));
                aConstraint.setSolved(true);
                return;
            }
            else
                eqHandler.neq(new AConstraint(Environment.neqCode, z, x));
        if (z.isBound())
            if (z.isTrue()) {
                eqHandler.eq(new AConstraint(Environment.eqCode, x, y));
                aConstraint.setSolved(true);
                return;
            }
            else
                eqHandler.neq(new AConstraint(Environment.neqCode, x, y));
        if (x.equals(z)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, y, true));
            aConstraint.setSolved(true);
            return;
        }
        if (x.equals(y)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, true));
            aConstraint.setSolved(true);
            return;
        }
        if (y.equals(z)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, x, true));
            aConstraint.setSolved(true);
            return;
        }
    }

    /**
     * Rewrite rule used to solve "not" constraint (b_1 = not b_2).
     * @param aConstraint not atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    not(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.notBoolCode;
        assert aConstraint.argument1 instanceof BoolLVar;
        assert aConstraint.argument2 instanceof BoolLVar;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

        manageEquChains(aConstraint);

        BoolLVar y = (BoolLVar) aConstraint.argument1;
        BoolLVar x = (BoolLVar) aConstraint.argument2;

        if (x.isBound()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, y,
                    !x.getValue()));
            aConstraint.setSolved(true);
            return;
        }
        if (y.isBound()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, x,
                    !y.getValue()));
            aConstraint.setSolved(true);
            return;
        }
        if (x.equals(y))
            solver.fail(aConstraint);
    }

    /**
     * Rewrite rule used for labeling using the given labeling options.
     * @param boolLVar variable to label.
     * @param labelingOptions labeling options.
     * @param aConstraint label atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    labelRule(@NotNull BoolLVar boolLVar, @NotNull LabelingOptions labelingOptions, @NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert boolLVar != null;
        assert labelingOptions != null;

        manageEquChains(aConstraint);

        boolLVar = (BoolLVar) aConstraint.argument1;

        if (boolLVar.isBound()) {
            aConstraint.setSolved(true);
            return;
        }
        if (!super.solver.storeUnchanged)
            return;
        switch (aConstraint.alternative) {
        case 0:
            boolean val = labelingOptions.getBoolValue();
            aConstraint.argument3 = !val;
            super.solver.backtracking.addChoicePoint(aConstraint);
            aConstraint.argument1 = boolLVar;
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.argument2 = val;
            eqHandler.eq(aConstraint);
            return;
        case 1:
            --aConstraint.alternative;
            aConstraint.argument1 = boolLVar;
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.argument2 = (Boolean) aConstraint.argument3;
            eqHandler.eq(aConstraint);
            labelRule(boolLVar, labelingOptions, aConstraint);
        }

    }

    /**
     * Rewrite rule used for labeling each variable in {@code boolLVars} using the given labeling options.
     * @param boolLVars if of variables to label. It must not contain {@code null} values.
     * @param labelingOptions labeling options.
     * @param aConstraint label atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void 
    labelRule(@NotNull ArrayList<BoolLVar> boolLVars, @NotNull LabelingOptions labelingOptions, @NotNull AConstraint aConstraint) {
        assert boolLVars != null;
        assert boolLVars.stream().noneMatch(Objects::isNull);
        assert labelingOptions != null;
        assert aConstraint != null;

        if (!super.solver.storeUnchanged)
            return;
        BoolLVar var = labelingOptions.getBoolVariable(boolLVars);
        boolLVars.remove(var);
        if(!solver.check(new AConstraint(Environment.labelCode, var, labelingOptions)))
            solver.fail(aConstraint);
    }
    
}
