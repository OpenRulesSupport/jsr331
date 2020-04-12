package jsetl;


import jsetl.annotation.NotNull;

/**
 * Rewrite rules for constraints over finite sets ({@code SetLVar}s).
 */
class RwRulesFS extends LibConstraintsRules {

    //////////////////////////////////////////////////////
    ////////////////// CONSTRUCTORS //////////////////////
    //////////////////////////////////////////////////////
    
    /**
     * Constructs an instance of rewrite rules and stores a reference to the solver.
     * @param solver reference to the solver.
     */
    protected RwRulesFS(@NotNull Solver solver) {
        super(solver);
        assert solver != null;
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
    protected boolean solveConstraint(@NotNull AConstraint aConstraint)  {
        assert aConstraint != null;
        if (aConstraint.constraintKindCode == Environment.complCode)
                compl(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.disjCode)
                disj(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.subsetCode)
                subset(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.sizeCode)
                size(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.intersCode)
                inters(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.unionCode)
                union(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.diffCode)
                diff(aConstraint);
        else 
                return false;
        return true;
    }

    /**
     * Solves an atomic constraint of the form {@code intLVar in setLVar}.
     * The first argument of the constraint must be an {@code IntLVar},
     * the second argument must be a {@code SetLVar}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    in(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof IntLVar;
        assert aConstraint.argument2 instanceof SetLVar;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.inCode;

        manageEquChains(aConstraint);

        IntLVar intLVar = (IntLVar) aConstraint.argument1;
        SetLVar setLVar = (SetLVar) aConstraint.argument2;
        solver.domainRulesFS.inRule(intLVar, setLVar, aConstraint);
    }

    /**
     * Solves an atomic constraint of the form {@code intLVar not in setLVar}.
     * {@code intLVar} is an {@code IntLVar} and is the first argument of {@code aConstraint}.
     * {@code setLVar} is a {@code SetLVar} and is the second argument of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    nin(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof IntLVar;
        assert aConstraint.argument2 instanceof SetLVar;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ninCode;

        manageEquChains(aConstraint);

        IntLVar intLVar = (IntLVar) aConstraint.argument1;
        SetLVar setLVar = (SetLVar) aConstraint.argument2;
        solver.domainRulesFS.ninRule(intLVar, setLVar, aConstraint);
    }

    /**
     * Solves an atomic constraint of the form {@code setLVar1 = complement(setLVar2)}.
     * {@code setLVar1, setLVar2} are respectively the first and second arguments
     * of {@code aConstraint} and are their type is {@code SetLVar}.
     * A set is a complement of the other one (and vice versa) if it has every possible
     * element that are not in the other one.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    compl(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof SetLVar;
        assert aConstraint.argument2 instanceof SetLVar;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.complCode;

        manageEquChains(aConstraint);

        SetLVar setLVar1 = (SetLVar) aConstraint.argument1;
        SetLVar setLVar2 = (SetLVar) aConstraint.argument2;
        solver.domainRulesFS.complRule(setLVar1, setLVar2, aConstraint);
    }

    /**
     * Solves an atomic constraint of the form {@code set1 disjoint set2}.
     * {@code set1, set2} are respectively the first and second arguments
     * of {@code aConstraint} and are their type is {@code SetLVar}.
     * Two sets are disjoint if and only if they have no elements in common.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    disj(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof SetLVar;
        assert aConstraint.argument2 instanceof SetLVar || aConstraint.argument2 instanceof MultiInterval;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.disjCode;

        manageEquChains(aConstraint);

        if (aConstraint.argument2 instanceof SetLVar)
            solver.domainRulesFS.disjRule((SetLVar) aConstraint.argument1, (SetLVar) aConstraint.argument2, aConstraint);
        else
            solver.domainRulesFS.disjRule((SetLVar) aConstraint.argument1, (MultiInterval) aConstraint.argument2, aConstraint);
    }

    /**
     * Solves an atomic constraint of the form {@code set1 subset of set2}.
     * {@code set1, set2} are respectively the first and second arguments
     * of {@code aConstraint} and are their type is {@code SetLVar}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    subset(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof SetLVar;
        assert aConstraint.argument2 instanceof SetLVar || aConstraint.argument2 instanceof MultiInterval;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.subsetCode;

        manageEquChains(aConstraint);

        if (aConstraint.argument2 instanceof SetLVar)
            solver.domainRulesFS.subsetRule((SetLVar) aConstraint.argument1, (SetLVar) aConstraint.argument2, aConstraint);
        else
            solver.domainRulesFS.subsetRule((SetLVar) aConstraint.argument1, (MultiInterval) aConstraint.argument2, aConstraint);
    }

    /**
     * Solves an atomic constraint of the form {@code integer = cardinality(set)}.
     * {@code set} is a {@code SetLVar} and is the first argument of {@code aConstraint}.
     * {@code integer} is an {@code IntLVar} and is the second argument of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    size(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof SetLVar;
        assert aConstraint.argument2 instanceof IntLVar;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.sizeCode;

        manageEquChains(aConstraint);

        SetLVar setLVar = (SetLVar) aConstraint.argument1;
        IntLVar intLVar = (IntLVar) aConstraint.argument2;
        solver.domainRulesFS.sizeRule(setLVar, intLVar, aConstraint);
    }

    /**
     * Solves an atomic constraint of the form {@code set1 = set2 intersection set3}.
     * {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint} and are of type {@code SetLVar}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    inters(@NotNull AConstraint aConstraint) {
         assert aConstraint != null;
         assert aConstraint.argument1 instanceof SetLVar;
         assert aConstraint.argument2 instanceof SetLVar;
         assert aConstraint.argument3 instanceof SetLVar;
         assert aConstraint.argument4 == null;
         assert aConstraint.constraintKindCode == Environment.intersCode;

         manageEquChains(aConstraint);

         SetLVar setLVar1 = (SetLVar) aConstraint.argument1;
         SetLVar setLVar2 = (SetLVar) aConstraint.argument2;
         SetLVar setLVar3 = (SetLVar) aConstraint.argument3;

         solver.domainRulesFS.intersRule(setLVar1, setLVar2, setLVar3, aConstraint);
    }

    /**
     * Solves an atomic constraint of the form {@code set1 = set2 union set3}.
     * {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint} and are of type {@code SetLVar}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    union(@NotNull AConstraint aConstraint) {
         assert aConstraint != null;
         assert aConstraint.argument1 instanceof SetLVar;
         assert aConstraint.argument2 instanceof SetLVar;
         assert aConstraint.argument3 instanceof SetLVar;
         assert aConstraint.argument4 == null;
         assert aConstraint.constraintKindCode == Environment.unionCode;

         manageEquChains(aConstraint);

         SetLVar setLVar1 = (SetLVar) aConstraint.argument1;
         SetLVar setLVar2 = (SetLVar) aConstraint.argument2;
         SetLVar setLVar3 = (SetLVar) aConstraint.argument3;

         solver.domainRulesFS.unionRule(setLVar1, setLVar2, setLVar3, aConstraint);
    }


    /**
     * Solves an atomic constraint of the form {@code set1 = set2 - set3}.
     * {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint} and are of type {@code SetLVar}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void
    diff(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof SetLVar;
        assert aConstraint.argument2 instanceof SetLVar;
        assert aConstraint.argument3 instanceof SetLVar;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.diffCode;

        manageEquChains(aConstraint);

        SetLVar setLVar1 = (SetLVar) aConstraint.argument1;
        SetLVar setLVar2 = (SetLVar) aConstraint.argument2;
        SetLVar setLVar3 = (SetLVar) aConstraint.argument3;
        solver.domainRulesFS.diffRule(setLVar1, setLVar2, setLVar3, aConstraint);
    }

}
