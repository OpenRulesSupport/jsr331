package jsetl;

import jsetl.annotation.NotNull;

import java.util.*;

/**
 * Rewrite rules for constraints over sets other than equality and inequality.
 */
class RwRulesSet extends LibConstraintsRules {

    //////////////////////////////////////////////////////
    ////////////////// DATA MEMBERS //////////////////////
    //////////////////////////////////////////////////////

    /**
     * Rewrite rules used to handle equality constraints.
     */
    private RwRulesEq eqHandler;

    /**
     * Rewrite rules used to handle constraints over {@code SetLVar}s.
     */
    private RwRulesFS FSHandler;


    //////////////////////////////////////////////////////
    ////////////////// CONSTRUCTORS //////////////////////
    //////////////////////////////////////////////////////

    /**
     * Constructs an instance of rewrite rules and stores a reference to the solver.
     * @param solver reference to the solver.
     */
    public RwRulesSet(@NotNull SolverClass solver) {
        super(solver);
        assert solver != null;
        eqHandler = new RwRulesEq(solver);
        FSHandler = new RwRulesFS(solver);
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
        if (aConstraint.constraintKindCode == Environment.inCode)
            in(aConstraint);        // membership
        else if (aConstraint.constraintKindCode == Environment.ninCode)
            nin(aConstraint);       // not membership
        else if (aConstraint.constraintKindCode == Environment.disjCode)
            disj(aConstraint);      // s disjunction
        else if (aConstraint.constraintKindCode == Environment.unionCode)
            union(aConstraint);     // union
        else if (aConstraint.constraintKindCode == Environment.subsetCode)
            subset(aConstraint);    // subset
        else if (aConstraint.constraintKindCode == Environment.intersCode)
            inters(aConstraint);    // intersection
        else if (aConstraint.constraintKindCode == Environment.diffCode)
            diff(aConstraint);      // difference
        else if (aConstraint.constraintKindCode == Environment.lessCode)
            less(aConstraint);      // element removal
        else if (aConstraint.constraintKindCode == Environment.sizeCode)
            size(aConstraint);      // set cardinality
        else if(aConstraint.constraintKindCode == Environment.subsetUnionCode)
            subsetUnion(aConstraint); //subset union
        else if(aConstraint.constraintKindCode == Environment.unionSubsetCode)
            unionSubset(aConstraint); // union subset
        else if(aConstraint.constraintKindCode == Environment.nunionCode)
            nunion(aConstraint); // not union
        else if(aConstraint.constraintKindCode == Environment.ndisjCode)
            ndisj(aConstraint); // not disjoint
        else if(aConstraint.constraintKindCode == Environment.nsubsetCode)
            nsubset(aConstraint); // not subset
        else if(aConstraint.constraintKindCode == Environment.nintersCode)
            ninters(aConstraint); // not intersection
        else if(aConstraint.constraintKindCode == Environment.ndiffCode)
            ndiff(aConstraint); // not set difference
        else
            return false; // not handled
        return true; // handled
    }

    /**
     * Solves an atomic constraint of the form {@code A in B}.
     * {@code A, B} are respectively the first and second arguments
     * of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void in(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.inCode;

        manageEquChains(aConstraint);

        if (aConstraint.argument2 instanceof SetLVar)                      // intlvar.in(setlvar)
            FSHandler.in(aConstraint);
        else if (aConstraint.argument2 instanceof Ris)
            inRis( aConstraint.argument1, (Ris) aConstraint.argument2, aConstraint);            // any.in(ris)
        else if (aConstraint.argument1 instanceof LVar && aConstraint.argument2 instanceof CP)
            inLVarCP((LVar)aConstraint.argument1, (CP)aConstraint.argument2, aConstraint);          // lvar.in(cp)
        else if (aConstraint.argument1 instanceof LPair && aConstraint.argument2 instanceof CP)
            inLPairCP((LPair)aConstraint.argument1, (CP)aConstraint.argument2, aConstraint);        // lpair.in(cp)
        else if (aConstraint.argument1 instanceof LVar && aConstraint.argument2 instanceof LSet)
            inLvarSet((LVar) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);     // lvar.in(lset)
        else if (aConstraint.argument2 instanceof Set) {
            aConstraint.argument2 = new LSet((Set<?>)aConstraint.argument2);              // lvar.in(set)
            inLvarSet((LVar) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);
        }
        else if (aConstraint.argument2 instanceof LSet)                    // any.in(lset)
            inObjSet(aConstraint.argument1, (LSet)aConstraint.argument2, aConstraint);              // (for internal use only)
    }

    /**
     * Solves an atomic constraint of the form {@code A not in B}.
     * {@code A, B} are respectively the first and second arguments
     * of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void nin(AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ninCode;

        manageEquChains(aConstraint);

        if (aConstraint.argument2 instanceof SetLVar)                          // intlvar.nin(setlvar)
            FSHandler.nin(aConstraint);
        else if(aConstraint.argument2 instanceof Ris)                          // any.nin(ris)
            ninLVarRis(aConstraint.argument1, (Ris) aConstraint.argument2, aConstraint);
        else if (aConstraint.argument1 instanceof LVar && aConstraint.argument2 instanceof CP)
            ninLVarCP((LVar)aConstraint.argument1, (CP)aConstraint.argument2, aConstraint);             // lvar.nin(cp)
        else if (aConstraint.argument1 instanceof LPair && aConstraint.argument2 instanceof CP)
            ninLPairCP((LPair)aConstraint.argument1, (CP)aConstraint.argument2, aConstraint);           // lpair.nin(cp)
        else if (aConstraint.argument1 instanceof LVar && aConstraint.argument2 instanceof LCollection)
            ninLvarSet((LVar) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint); // lvar.nin(lset)
        else if (aConstraint.argument2 instanceof LCollection)
            ninObjSet(aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);         // any.nin(lset)
        else if (aConstraint.argument2 instanceof Set) {
            aConstraint.argument2 = new LSet((Set<?>)aConstraint.argument2);                  // any.nin(set)
            nin(aConstraint);
        }
    }

    /**
     * Solves an atomic constraint of the form {@code set1 disjoint set2},
     * in which {@code set1, set2} are respectively the first and second arguments
     * of {@code aConstraint}.
     * Two sets are disjoint if and only if they have no elements in common.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void disj(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.disjCode;

        manageEquChains(aConstraint);

        if(aConstraint.argument1 instanceof Ris || aConstraint.argument2 instanceof Ris)      // ris || ris
            disjRis((LSet) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);
        else if(aConstraint.argument1 instanceof CP && aConstraint.argument2 instanceof LSet) // cp || aConstraint, cp || cp
            disjCP((CP) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);
        else if(aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof CP){// aConstraint || cp
            Object tmp = aConstraint.argument1;
            aConstraint.argument1 = aConstraint.argument2;
            aConstraint.argument2 = tmp;
            disjCP((CP) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);
        }
        else if (aConstraint.argument1 instanceof SetLVar)                      // setlvar || lset
            FSHandler.disj(aConstraint);
        else if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof LSet)
            disj((LSet) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);              // lset || lset                                                                          // {...})
        else if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof Set) {
            aConstraint.argument2 = new LSet((Set<?>)aConstraint.argument2);                  // lset || set
            disj(aConstraint);
        }
        return;
    }

    /**
     * Solves an atomic constraint of the form {@code set3 = set1 union set2},
     * in which {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void union(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null || aConstraint.argument4 instanceof LSet || aConstraint.argument4 instanceof LList;
        assert aConstraint.constraintKindCode == Environment.unionCode;

        manageEquChains(aConstraint);

        if (aConstraint.argument4 != null && aConstraint.argument4 instanceof LSet)
            union((LSet)aConstraint.argument1, (LSet)aConstraint.argument2, (LSet)aConstraint.argument3, (LSet)aConstraint.argument4, aConstraint);     // union(s1,s2,s3,N)
        else if (aConstraint.argument4 != null && aConstraint.argument4 instanceof LList)
            union((LSet)aConstraint.argument1, (LSet)aConstraint.argument2, (LSet) aConstraint.argument3,
                    (LSet)((LList)aConstraint.argument4).get(0), (LSet)((LList)aConstraint.argument4).get(1), aConstraint); // union(s1,s2,s3,N,N1)
        else if(aConstraint.argument1 instanceof Ris || aConstraint.argument2 instanceof Ris || aConstraint.argument3 instanceof Ris)
            unionRis((LSet) aConstraint.argument1, (LSet) aConstraint.argument2, (LSet) aConstraint.argument3, aConstraint);   // ris = ris U ris
        else if(aConstraint.argument1 instanceof CP || aConstraint.argument2 instanceof CP || aConstraint.argument3 instanceof CP)
            unionCP((LSet) aConstraint.argument1, (LSet) aConstraint.argument2, (LSet) aConstraint.argument3, aConstraint);    // cp = cp U cp
        else if (aConstraint.argument1 instanceof SetLVar)
            FSHandler.union(aConstraint);          //aConstraint.argument1 = aConstraint.argument2 U aConstraint.argument3, aConstraint.argument1 SetLVar
        else
        if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof LSet && aConstraint.argument3 instanceof LSet)
            union((LSet) aConstraint.argument1, (LSet) aConstraint.argument2, (LSet) aConstraint.argument3, aConstraint); // lset = lset U lset                                                                            // {...})
        else if (aConstraint.argument3 instanceof LSet) {
            if (aConstraint.argument1 instanceof Set) {                           // lset = aConstraint U lset
                LSet aux = new LSet((Set<?>)aConstraint.argument1);
                aConstraint.argument1 = aux;
            }
            if (aConstraint.argument2 instanceof Set) {                           // lset = lset U aConstraint
                LSet aux = new LSet((Set<?>)aConstraint.argument2);
                aConstraint.argument2 = aux;
            }
            union(aConstraint);
        }
        return;
    }

    /**
     * Solves an atomic constraint of the form {@code set1 subset of set2},
     * in which {@code set1, set2} are first and second arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void subset(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null || aConstraint.argument4 instanceof LSet;
        assert aConstraint.constraintKindCode == Environment.subsetCode;

        manageEquChains(aConstraint);


        if (aConstraint.argument1 instanceof SetLVar)                     // setlvar subset lset
            FSHandler.subset(aConstraint);
        else if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof LSet)
            subset((LSet) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);       // lset subset lset
        else if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof Set) {
            LSet aux = new LSet((Set<?>)aConstraint.argument2);           // lset subset set
            aConstraint.argument2 = aux;
            subset(aConstraint);
        }
    }

    /**
     * Solves an atomic constraint of the form {@code set3 = set1 intersection set2},
     * in which {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void inters(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null || aConstraint.argument4 instanceof LSet;
        assert aConstraint.constraintKindCode == Environment.intersCode;

        manageEquChains(aConstraint);

        if (aConstraint.argument1 instanceof SetLVar)      //aConstraint.argument1 = aConstraint.argument2 /\ aConstraint.argument3, aConstraint.argument1 SetLVar
            FSHandler.inters(aConstraint);
        else                                //aConstraint.argument3 = aConstraint.argument1 /\ aConstraint.argument2, aConstraint.argument1 LSet
            if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof LSet && aConstraint.argument3 instanceof LSet)
                inters((LSet)aConstraint.argument1, (LSet)aConstraint.argument2, (LSet)aConstraint.argument3, aConstraint);  // lset = lset /\ lset
            else if (aConstraint.argument3 instanceof LSet) {
                if (aConstraint.argument1 instanceof Set) {                          // lset = aConstraint /\ lset
                    LSet aux = new LSet((Set<?>)aConstraint.argument1);
                    aConstraint.argument1 = aux;
                }
                if (aConstraint.argument2 instanceof Set) {                          // lset = lset /\ aConstraint
                    LSet aux = new LSet((Set<?>)aConstraint.argument2);
                    aConstraint.argument2 = aux;
                }
                inters(aConstraint);
            }
    }

    /**
     * Solves an atomic constraint of the form {@code set3 = set1 - set2},
     * in which {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void diff(@NotNull AConstraint aConstraint){
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null || aConstraint.argument4 instanceof LSet;
        assert aConstraint.constraintKindCode == Environment.diffCode;

        manageEquChains(aConstraint);

        if (aConstraint.argument1 instanceof SetLVar)   //aConstraint.argument1 = aConstraint.argument2 \ aConstraint.argument3
            FSHandler.diff(aConstraint);
        else                           //aConstraint.argument3 = aConstraint.argument1 \ aConstraint.argument2
            if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof LSet && aConstraint.argument3 instanceof LSet)
                diff((LSet)aConstraint.argument1, (LSet)aConstraint.argument2, (LSet)aConstraint.argument3, aConstraint);                                                                             // {...})
            else if (aConstraint.argument3 instanceof LSet) {
                if (aConstraint.argument1 instanceof Set) {
                    LSet aux = new LSet((Set<?>)aConstraint.argument1);
                    aConstraint.argument1 = aux;
                }
                if (aConstraint.argument2 instanceof Set) {
                    LSet aux = new LSet((Set<?>)aConstraint.argument2);
                    aConstraint.argument2 = aux;
                }
                diff(aConstraint);
            }
    }

    /**
     * Solves an atomic constraint of the form {@code set3 = set1 - {argument2}}
     * in which {@code set1, argument2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void less(AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null || aConstraint.argument4 instanceof LSet;
        assert aConstraint.constraintKindCode == Environment.lessCode;

        manageEquChains(aConstraint);

        if (aConstraint.argument1 instanceof LSet && aConstraint.argument3 instanceof LSet)
            less((LSet)aConstraint.argument1, aConstraint.argument2, (LSet)aConstraint.argument3, aConstraint);                                                                           // {...})
        else if (aConstraint.argument1 instanceof LSet) {
            if (aConstraint.argument3 instanceof Set) {
                LSet aux = new LSet((Set<?>)aConstraint.argument1);
                aConstraint.argument3 = aux;
            }
            if (!(aConstraint.argument2 instanceof LVar)) {
                LVar aux = new LVar(aConstraint.argument2);
                aConstraint.argument2 = aux;
            }
            less(aConstraint);
        }
    }

    /**
     * Solves an atomic constraint of the form {@code integer = cardinality(set)},
     * in which {@code integer, set} are respectively the first and second arguments of
     * {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void size(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet || aConstraint.argument1 instanceof SetLVar;
        assert aConstraint.argument2 instanceof Integer || aConstraint.argument2 instanceof IntLVar;
        assert aConstraint.argument4 == null || aConstraint.argument4 instanceof LSet;
        assert aConstraint.constraintKindCode == Environment.sizeCode;

        manageEquChains(aConstraint);

        if (aConstraint.argument1 instanceof SetLVar)
            FSHandler.size(aConstraint);
        else if(aConstraint.argument1 instanceof Ris){
            sizeRis(aConstraint);
        }
        else if (aConstraint.argument1 instanceof CP) {
            if (!(aConstraint.argument2 instanceof IntLVar)) {
                IntLVar aux = new IntLVar((Integer)aConstraint.argument2);
                aConstraint.argument2 = aux;
            }
            AConstraint c1 = new AConstraint(Environment.geCode, aConstraint.argument2, 0);  // aConstraint.argument2 >= 0
            solver.add(c1);
            sizeCP((CP)aConstraint.argument1, (IntLVar)aConstraint.argument2, aConstraint);
        }
        else if (aConstraint.argument1 instanceof LSet) {
            if (!(aConstraint.argument2 instanceof IntLVar)) {
                IntLVar aux = new IntLVar((Integer)aConstraint.argument2);
                aConstraint.argument2 = aux;
            }
            AConstraint c1 = new AConstraint(Environment.geCode, aConstraint.argument2, 0);  // aConstraint.argument2 >= 0
            solver.add(c1);
            if(!((LSet)aConstraint.argument1).isBound() && !((IntLVar)aConstraint.argument2).isBound())
                return;
            if (((IntLVar)aConstraint.argument2).isBound() && !((LSet)aConstraint.argument1).isBound() ||     //deterministic size: O(n)
                    ((LSet)aConstraint.argument1).isGround())
                detSize((LSet)aConstraint.argument1, (IntLVar)aConstraint.argument2, aConstraint);
            else
                if (solver.level >= 3)           //non-deterministic size
                    nonDetSize((LSet)aConstraint.argument1, (IntLVar)aConstraint.argument2, aConstraint);
        }
    }

    /**
     * Solves an atomic constraint of the form {@code set3 =/= set1 union set2},
     * in which {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void nunion(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet || aConstraint.argument1 instanceof Set;
        assert aConstraint.argument2 instanceof LSet || aConstraint.argument2 instanceof Set;
        assert aConstraint.argument3 instanceof LSet || aConstraint.argument3 instanceof Set;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.nunionCode;

        manageEquChains(aConstraint);

        LSet set1 = aConstraint.argument1 instanceof  LSet ? (LSet) aConstraint.argument1 : new LSet((Set) aConstraint.argument1);
        LSet set2 = aConstraint.argument2 instanceof  LSet ? (LSet) aConstraint.argument2 : new LSet((Set) aConstraint.argument2);
        LSet set3 = aConstraint.argument2 instanceof  LSet ? (LSet) aConstraint.argument3 : new LSet((Set) aConstraint.argument3);

        switch (aConstraint.alternative) {
            case 0:     //  N.in(C) & N.nin(A) & N.nin(B)
                solver.addChoicePoint(aConstraint);

                LVar N1 = new LVar();
                aConstraint.argument1 = N1;
                aConstraint.constraintKindCode = Environment.inCode;
                aConstraint.argument2 = set3;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;
                solver.add(new AConstraint(Environment.ninCode, N1, set1));
                solver.add(new AConstraint(Environment.ninCode, N1, set2));

                solver.storeUnchanged = false;
                return;
            case 1:     //  N.in(A) & N.nin(C)
                solver.addChoicePoint(aConstraint);

                LVar N2 = new LVar();
                aConstraint.argument1 = N2;
                aConstraint.argument2 = set1;
                aConstraint.constraintKindCode = Environment.inCode;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;
                solver.add(new AConstraint(Environment.ninCode, N2, set3));

                solver.storeUnchanged = false;
                return;

            case 2:     //  N.in(B) & N.nin(C)
                LVar N3 = new LVar();
                aConstraint.argument1 = N3;
                aConstraint.constraintKindCode = Environment.inCode;
                aConstraint.argument2 = set2;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;
                solver.add(new AConstraint(Environment.ninCode, N3, set3));

                solver.storeUnchanged = false;
                return;
        }
    }

    /**
     * Solves an atomic constraint of the form {@code set1 is not disjoint set2}.
     * Two sets are disjoint iff they have no elements in common.
     * @param aConstraint atomic constraint to solve
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void ndisj(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet || aConstraint.argument1 instanceof Set;
        assert aConstraint.argument2 instanceof LSet || aConstraint.argument2 instanceof Set;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ndisjCode;

        manageEquChains(aConstraint);

        LSet set1 = aConstraint.argument1 instanceof  LSet ? (LSet) aConstraint.argument1 : new LSet((Set) aConstraint.argument1);
        LSet set2 = aConstraint.argument2 instanceof  LSet ? (LSet) aConstraint.argument2 : new LSet((Set) aConstraint.argument2);

        LVar N = new LVar();
        aConstraint.argument1 = N;
        aConstraint.constraintKindCode = Environment.inCode;
        aConstraint.argument2 = set1;
        aConstraint.argument3 = null;
        aConstraint.alternative = 0;
        solver.add(new AConstraint(Environment.inCode, N, set2));
        solver.storeUnchanged = false;
    }

    /**
     * Solves an atomic constraint of the form {@code set1 is not subset of set2},
     * in which {@code set1, set2} are respectively the first and second argument of
     * {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void nsubset(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet || aConstraint.argument1 instanceof Set;
        assert aConstraint.argument2 instanceof LSet || aConstraint.argument2 instanceof Set;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.nsubsetCode;

        manageEquChains(aConstraint);

        LSet set1 = aConstraint.argument1 instanceof  LSet ? (LSet) aConstraint.argument1 : new LSet((Set) aConstraint.argument1);
        LSet set2 = aConstraint.argument2 instanceof  LSet ? (LSet) aConstraint.argument2 : new LSet((Set) aConstraint.argument2);

        LVar N = new LVar();
        aConstraint.argument1 = N;
        aConstraint.constraintKindCode = Environment.inCode;
        aConstraint.argument2 = set1;
        aConstraint.argument3 = null;
        aConstraint.argument4 = null;

        aConstraint.alternative = 0;

        solver.add(new AConstraint(Environment.ninCode, N, set2));
    }

    /**
     * Solves an atomic constraint of the form {@code set3 =/= set1 intersection set2},
     * in which {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void ninters(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet || aConstraint.argument1 instanceof Set;
        assert aConstraint.argument2 instanceof LSet || aConstraint.argument2 instanceof Set;
        assert aConstraint.argument3 instanceof LSet || aConstraint.argument3 instanceof Set;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.nintersCode;

        manageEquChains(aConstraint);

        LSet set1 = aConstraint.argument1 instanceof  LSet ? (LSet) aConstraint.argument1 : new LSet((Set) aConstraint.argument1);
        LSet set2 = aConstraint.argument2 instanceof  LSet ? (LSet) aConstraint.argument2 : new LSet((Set) aConstraint.argument2);
        LSet set3 = aConstraint.argument2 instanceof  LSet ? (LSet) aConstraint.argument3 : new LSet((Set) aConstraint.argument3);

        LVar N = new LVar();

        switch (aConstraint.alternative) {
            case 0:
                solver.addChoicePoint(aConstraint);

                aConstraint.argument1 = N;
                aConstraint.constraintKindCode = Environment.inCode;
                aConstraint.argument2 = set3;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;

                solver.add(new AConstraint(Environment.ninCode, N, set1));
                solver.storeUnchanged = false;
                return;
            case 1:
                solver.addChoicePoint(aConstraint);

                aConstraint.argument1 = N;
                aConstraint.constraintKindCode = Environment.inCode;
                aConstraint.argument2 = set3;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;

                solver.add(new AConstraint(Environment.ninCode, N, set2));
                solver.storeUnchanged = false;
                return;
            case 2: // N in A && N in B && N nin C
                aConstraint.argument1 = N;
                aConstraint.constraintKindCode = Environment.inCode;
                aConstraint.argument2 = set1;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;

                solver.add(new AConstraint(Environment.inCode, N, set2));
                solver.add(new AConstraint(Environment.ninCode, N, set3));

                solver.storeUnchanged = false;
                return;
        }

    }

    /**
     * Solves an atomic constraint of the form {@code set3 =/= set1 - set2},
     * in which {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void ndiff(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet || aConstraint.argument1 instanceof Set;
        assert aConstraint.argument2 instanceof LSet || aConstraint.argument2 instanceof Set;
        assert aConstraint.argument3 instanceof LSet || aConstraint.argument3 instanceof Set;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ndiffCode;

        manageEquChains(aConstraint);

        LSet set1 = aConstraint.argument1 instanceof  LSet ? (LSet) aConstraint.argument1 : new LSet((Set) aConstraint.argument1);
        LSet set2 = aConstraint.argument2 instanceof  LSet ? (LSet) aConstraint.argument2 : new LSet((Set) aConstraint.argument2);
        LSet set3 = aConstraint.argument2 instanceof  LSet ? (LSet) aConstraint.argument3 : new LSet((Set) aConstraint.argument3);

        LVar n = new LVar();
        switch (aConstraint.alternative) {
            case 0:
                solver.addChoicePoint(aConstraint);
                aConstraint.argument1 = n;
                aConstraint.constraintKindCode = Environment.inCode;
                aConstraint.argument2 = set3;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;
                solver.add(new AConstraint(Environment.ninCode, n, set1));
                solver.showStoreAll();
                solver.storeUnchanged = false;
                return;
            case 1:
                solver.addChoicePoint(aConstraint);
                aConstraint.argument1 = n;
                aConstraint.constraintKindCode = Environment.inCode;
                aConstraint.argument2 = set3;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;
                solver.add(new AConstraint(Environment.inCode, n, set2));
                solver.storeUnchanged = false;
                return;
            case 2:
                aConstraint.argument1 = n;
                aConstraint.constraintKindCode = Environment.ninCode;
                aConstraint.argument2 = set3;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;
                solver.add(new AConstraint(Environment.inCode, n, set1));
                solver.add(new AConstraint(Environment.ninCode, n, set2));
                solver.storeUnchanged = false;
                return;
        }
    }


    //////////////////////////////////////////////////////
    ////////////////// PRIVATE METHODS ///////////////////
    //////////////////////////////////////////////////////

    /**
     * Solves an atomic constraint of the form {@code lVar in CP}.
     * @param lVar a logical variable.
     * @param cp a cartesian product.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Marco Ghezzi
     */
    private void inLVarCP(@NotNull LVar lVar, @NotNull CP cp, @NotNull AConstraint aConstraint) {
        assert lVar != null;
        assert lVar.equ == null;
        assert cp != null;
        assert cp.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.inCode;

        LVar n1 = new LVar();
        LVar n2 = new LVar();
        LPair z = new LPair(n1,n2);
        aConstraint.argument1 = lVar;
        aConstraint.constraintKindCode = Environment.eqCode;            // z = (n1,n2)
        aConstraint.argument2 = z;
        solver.add(new AConstraint(Environment.inCode, z, cp));
        solver.storeUnchanged = false;
    }

    /**
     * Solves an atomic constraint of the form {@code lVar in CP}.
     * @param lPair a logical pair.
     * @param cp a cartesian product.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Marco Ghezzi
     */
    private void inLPairCP(@NotNull LPair lPair, @NotNull CP cp, @NotNull AConstraint aConstraint) {
        assert lPair != null;
        assert lPair.equ == null;
        assert cp != null;
        assert cp.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.inCode;

        if(!cp.isBound() || cp.isEmpty() ) {
            solver.fail(aConstraint);
            return;
        }
        else if(cp.isBound()) {
            LVar n1 = new LVar();
            LVar n2 = new LVar();
            LPair z1 = new LPair(n1,n2);
            aConstraint.argument1 = z1;
            aConstraint.constraintKindCode = Environment.eqCode;            // lPair = (n1,n2)
            aConstraint.argument2 = lPair;
            solver.add(new AConstraint(Environment.inCode, n1, cp.getFirstSet()));
            solver.add(new AConstraint(Environment.inCode, n2, cp.getSecondSet()));
        }
        else {
            solver.fail(aConstraint);
            return;
        }
    }

    /**
     * Solves an atomic constraint of the form {@code object in ris}.
     * @param object an object
     * @param ris a restricted intensional set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void inRis(@NotNull Object object, @NotNull Ris ris, @NotNull AConstraint aConstraint) {
        assert object != null;
        assert !(object instanceof LObject) || ((LObject) object).equ == null;
        assert ris != null;
        assert ris.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.inCode;

        LSet domain = ris.getDomain();
        //y in {x in {} | F(x) @ P(x)} --> FALSE
        if(domain.isBound() && domain.isEmpty()) {
            solver.fail(aConstraint);
            return;
        }
        else if(dealWithRisExpansion(aConstraint)){
            return;
        }
        Object d = ris.getNewControlTerm();
        aConstraint.argument1 = d;
        aConstraint.argument2 = domain;
        solver.add(ris.F(d));
        ConstraintClass rp = new ConstraintClass();
        Object p = ris.P(d, rp);
        if(!rp.isEmpty())
            solver.add(rp);
        solver.add(new AConstraint(Environment.eqCode, object, p));
        solver.storeUnchanged = false;
    }

    /**
     * Solves an atomic constraint of the form {@code lVar in lSet}.
     * @param lVar  logical variable.
     * @param lSet logical set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void inLvarSet(@NotNull LVar lVar, @NotNull LSet lSet, @NotNull AConstraint aConstraint) {
        assert lVar != null;
        assert lVar.equ == null;
        assert lSet != null;
        assert lSet.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.inCode;

        if (lSet.isBound()) { // (1)
            if (lSet.isEmpty()) {
                solver.fail(aConstraint);
                return;
            } else { // X.in( {t|R})
                if(solver.getOptimizationOptions().areInOptimizationsEnabled())
                    for(Object obj : lSet)
                        if(LObject.equals(obj, lVar)){
                            aConstraint.setSolved(true);
                           return;
                        }
                switch (aConstraint.alternative) {
                    case 0: // (2) (i)

                        if (lSet.countAllElements() > 1 ||
                                !(lSet.getTail()).isBound() ||
                                lSet.getTail() instanceof Ris) {
                            solver.addChoicePoint(aConstraint);

                        }
                        aConstraint.argument1 = lVar;
                        aConstraint.constraintKindCode = Environment.eqCode;
                        aConstraint.argument2 = lSet.getOne(); // X.eq(t)

                        eqHandler.eq(aConstraint);

                        solver.storeUnchanged = false;
                        return;
                    case 1: // (2) (ii)
                        aConstraint.alternative = 0;
                        aConstraint.argument2 =  lSet.removeOne(); // X.in(R)
                        in(aConstraint);
                        solver.storeUnchanged = false;
                        return;
                }
            }
        } else { // logical set not initialized. (3)
            LSet n = new LSet();
            aConstraint.argument1 = lSet;
            aConstraint.argument2 = n.ins(lVar);   // LCollection.eq( {X|N}) N
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.alternative = 0;
            eqHandler.eq(aConstraint);
            solver.storeUnchanged = false;
            return;
        }
    }

    /**
     * Solves an atomic constraint of the form {@code object in lSet}.
     * @param object object.
     * @param lSet logical set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void inObjSet(@NotNull Object object, @NotNull LSet lSet,@NotNull AConstraint aConstraint) {
        assert object != null;
        assert !(object instanceof LObject) || ((LObject) object).equ == null;
        assert lSet != null;
        assert lSet.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.inCode;

    	if (lSet.getTail() instanceof CP) {
            switch (aConstraint.alternative) {
                case 0: // (2) (i)
                    solver.addChoicePoint(aConstraint);
                    aConstraint.alternative = 0;
                    aConstraint.argument1 = object;
                    aConstraint.argument2 = lSet.getTail();
                    aConstraint.constraintKindCode = Environment.inCode;
                    solver.storeUnchanged = false;
                    return;
                case 1: // (2) (ii)
                    solver.addChoicePoint(aConstraint);
                    aConstraint.alternative = 0;
                    aConstraint.argument1 = object;
                    aConstraint.argument2 = lSet.createObj(lSet.toArrayList(), lSet.getTail());
                    aConstraint.constraintKindCode = Environment.inCode;
                    solver.storeUnchanged = false;
                    return;
            }
            return;
        }
        else if (lSet.isBound()) {
            if (lSet.isEmpty()) {
                solver.fail(aConstraint);
                return;
            } else {
                if(solver.getOptimizationOptions().areInOptimizationsEnabled())
                    for(Object o : lSet)
                        if(o.equals(object)){
                            aConstraint.setSolved(true);
                            return;
                        }
                switch (aConstraint.alternative) {
                    case 0: // (2) (i)
                        if (lSet.countAllElements() > 1 ||
                                !(lSet.getTail()).isBound() ||
                                (lSet.getTail() instanceof Ris))
                            solver.addChoicePoint(aConstraint);
                        aConstraint.argument1 = object;
                        aConstraint.constraintKindCode = Environment.eqCode;
                        aConstraint.argument2 = lSet.getOne();
                        eqHandler.eq(aConstraint);
                        solver.storeUnchanged = false;
                        return;
                    case 1: // (2) (ii)
                        aConstraint.alternative = 0;
                        aConstraint.argument2 = lSet.removeOne();
                        in(aConstraint);
                        solver.storeUnchanged = false;
                        return;
                }
            }
        } else {
            LSet n = new LSet();
            aConstraint.argument1 = lSet;
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.argument2 = n.ins(object);
            eqHandler.eq(aConstraint);
            solver.storeUnchanged = false;
            return;
        }
    }

    /**
     * Solves an atomic constraint of the form {@code lVar not in cp}.
     * @param lVar logical variable.
     * @param cp cartesian product.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Marco Ghezzi
     */
    private void ninLVarCP(@NotNull LVar lVar, @NotNull CP cp, @NotNull AConstraint aConstraint) {
        assert lVar != null;
        assert cp != null;
        assert lVar.equ == null;
        assert cp.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ninCode;

        LVar n1 = new LVar();
        LVar n2 = new LVar();
        LPair z = new LPair(n1,n2);
        aConstraint.argument1 = lVar;
        aConstraint.constraintKindCode = Environment.eqCode;            // z = (n1,n2)
        aConstraint.argument2 = z;
        solver.add(new AConstraint(Environment.ninCode, z, cp));
        solver.storeUnchanged = false;
    }

    /**
     * Solves an atomic constraint of the form {@code lPair not in cp}.
     * @param lPair logical pair.
     * @param cp cartesian product.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Marco Ghezzi
     */
    private void ninLPairCP(@NotNull LPair lPair, @NotNull CP cp, @NotNull AConstraint aConstraint) {
        assert lPair != null;
        assert cp != null;
        assert lPair.equ == null;
        assert cp.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ninCode;

        if(cp.isBound() && cp.isEmpty()) {
            aConstraint.setSolved(true);
            return;
        }
        else if(cp.isBound() && !cp.isEmpty() && lPair.isBound()) {
            Object n1 = lPair.getFirst();
            Object n2 = lPair.getSecond();
            switch(aConstraint.alternative) {
                case 0:
                    solver.addChoicePoint(aConstraint);
                    aConstraint.argument1 = n1;
                    aConstraint.constraintKindCode = Environment.ninCode;
                    aConstraint.argument2 = cp.getFirstSet();
                    solver.storeUnchanged = false;
                    return;
                case 1:
                    aConstraint.alternative = 0;
                    aConstraint.argument1 = n2;
                    aConstraint.constraintKindCode = Environment.ninCode;
                    aConstraint.argument2 = cp.getSecondSet();
                    solver.storeUnchanged = false;
                    return;
            }
        }
        else {
            solver.fail(aConstraint);
            return;
        }
    }

    /**
     * Solves an atomic constraint of the form {@code object not in ris}.
     * @param object any object.
     * @param ris restricted intensional set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void ninLVarRis(@NotNull Object object, @NotNull Ris ris, @NotNull AConstraint aConstraint) {
        assert object != null;
        assert !(object instanceof LObject) || ((LObject) object).equ == null;
        assert ris != null;
        assert ris.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ninCode;

        LSet domain = ris.getDomain();
        //y nin {} ----> TRUE
        if(domain.isBound() && domain.isEmpty()) {
            aConstraint.setSolved(true);
            return;
        }           
        else if(domain.isBound()) {
            if (dealWithRisExpansion(aConstraint)){
                nin(aConstraint);
                return;
            }
            switch(aConstraint.alternative) {
            case 0:             
                solver.addChoicePoint(aConstraint);
                Object d = domain.getOne();
                Ris D = new Ris(ris.getControlTerm(), domain.removeOne(), ris.getFilter(), ris.getPattern(), ris.getDummyVariables());

                ConstraintClass cc = new ConstraintClass();
                solver.add(ris.F(d));
                Object p = ris.P(d, cc);
                if(!cc.isEmpty())
                    solver.add(cc);

                solver.add(new AConstraint(Environment.neqCode, object, p));
                aConstraint.argument2 = D;
                solver.storeUnchanged = false;
                return;                     
            case 1:             
                aConstraint.alternative = 0;
                Object d1 = domain.getOne();
                Ris D1 = new Ris(ris.getControlTerm(), domain.removeOne(), ris.getFilter(), ris.getPattern(), ris.getDummyVariables());
                solver.add(ris.notF(d1));
                aConstraint.argument2 = D1;
                return;             
            }
        }
    }

    /**
     * Solves an atomic constraint of the form {@code lVar not in lset}.
     * @param lVar logical variable.
     * @param lSet logical collection.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void ninLvarSet(@NotNull LVar lVar, @NotNull LSet lSet, @NotNull AConstraint aConstraint) {
        assert lVar != null;
        assert lVar.equ == null;
        assert lSet != null;
        assert lSet.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ninCode;

        if (lSet.isBound() && lSet.isEmpty()) { // (1)
            aConstraint.setSolved(true);
            return;
        } else {
            if (!lSet.isBound())
                return; // (3)
            else {
                if(solver.getOptimizationOptions().areNinOptimizationsEnabled())
                    for(Object obj : lSet)
                        if(LObject.equals(obj, lVar))
                            solver.fail(aConstraint);

                if (lVar.isBound() && lVar.val instanceof LSet) {
                    aConstraint.argument1 = lVar.val;
                    solver.storeUnchanged = false;
                    return;
                } else { // (2)
                    aConstraint.argument2 = lSet.removeOne();
                    AConstraint st = new AConstraint(Environment.neqCode, lVar,
                            lSet.getOne());
                    solver.add(st);
                    solver.storeUnchanged = false;
                    return;
                }
            }
        }

    }

    /**
     * Solves an atomic constraint of the form {@code object not in lSet}.
     * @param object any object.
     * @param lSet logical set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void ninObjSet(@NotNull Object object, @NotNull LSet lSet, @NotNull AConstraint aConstraint) {
        assert object != null;
        assert !(object instanceof LObject) || ((LObject) object).equ == null;
        assert lSet != null;
        assert lSet.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.ninCode;

        if (!lSet.isBound())
            return;
        else {
            if(solver.getOptimizationOptions().areNinOptimizationsEnabled())
                for(Object element : lSet)
                    if(LObject.equals(element, object))
                      solver.fail(aConstraint);

            if (lSet.isBound() && lSet.isEmpty()) {
                aConstraint.setSolved(true);
                return;
            } else {
                aConstraint.argument2 = lSet.removeOne();
                AConstraint st = new AConstraint(Environment.neqCode, object,
                        lSet.getOne());
                solver.add(st);
                solver.storeUnchanged = false;
                return;
            }
        }
    }

    /**
     * Solves an atomic constraint of the form {@code cp disjoint lSet}.
     * @param cp cartesian product.
     * @param lSet logical set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Marco Ghezzi
     */
    private void disjCP(@NotNull CP cp, @NotNull LSet lSet, @NotNull AConstraint aConstraint) {
        assert cp != null;
        assert cp.equ == null;
        assert lSet != null;
        assert lSet.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.disjCode;

        if(cp.isVariable() && lSet.isVariable()) {
            aConstraint.setSolved(true);
            return;
        }
        else if(cp.isBound() && lSet.isBound() && lSet.isEmpty()) {
            aConstraint.setSolved(true);
            return;
        }
        else if(cp.isBound() && cp.isEmpty() && lSet.isBound()) {
            aConstraint.setSolved(true);
            return;
        }
        else if(cp instanceof CP && lSet instanceof LSet)
            if (!lSet.isBound() || lSet instanceof CP) {
                Object x = cp.getFirstSet().getOne();
                Object y = cp.getSecondSet().getOne();
                LPair p = new LPair(x,y);
                LSet N = LSet.empty().ins(p);

                aConstraint.argument1 = N;
                aConstraint.constraintKindCode = Environment.disjCode;
                aConstraint.argument2 = lSet;

                solver.add(new ConstraintClass(new AConstraint(
                        Environment.unionCode, new CP(LSet.empty().ins(x), cp.getSecondSet()),
                        new CP(cp.getFirstSet(), cp.getSecondSet().ins(y)),
                        N)));
                solver.storeUnchanged = false;
                return;
            }
            else {
                LPair z = (LPair)lSet.getOne();
                lSet = lSet.removeOne();

                aConstraint.argument1 = z;
                aConstraint.constraintKindCode = Environment.ninCode;
                aConstraint.argument2 = cp;

                solver.add(new AConstraint(Environment.disjCode, cp, lSet));
                solver.storeUnchanged = false;
                return;
            }
    }

   
    /**
     * Solves an atomic constraint of the form {@code lSet1 disjoint lSet2} when one of the two arguments is a RIS.
     * Two sets are disjoint if and only if they have no elements in common.
     * @param lSet1 first set
     * @param lSet2 second set
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private void disjRis(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert lSet1 instanceof Ris || lSet2 instanceof Ris;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.disjCode;

        if (dealWithRisExpansion(aConstraint)){
            disj(aConstraint);
            return;
        }

        if(!lSet1.isBound() && !lSet2.isBound() && lSet1 == lSet2){ // rule 1
            aConstraint.argument2 = LSet.empty();
            solver.storeUnchanged = false;
            return;
        }

        if(lSet2.isBound() && lSet2.isEmpty()){ // rule 2
            aConstraint.setSolved(true);
            return;
        }

        if(lSet1.isBound() && lSet1.isEmpty()){ //rule 3
            aConstraint.setSolved(true);
            return;
        }

        if(!(lSet2 instanceof Ris) && lSet2.isBound()){ //rule 4, lSet2 is not empty because of rule 2
            Object t = lSet2.getOne();
            aConstraint.argument2 = lSet2.removeOne();
            solver.storeUnchanged = false;
            solver.add(lSet1.ncontains(t));
            return;
        }

        if(!(lSet1 instanceof Ris) && lSet1.isBound()){ //rule 5, lSet1 is not empty because of rule 3
            Object t = lSet1.getOne();
            aConstraint.argument1 = lSet1.removeOne();
            solver.storeUnchanged = false;
            solver.add(lSet2.ncontains(t));
            return;
        }

        if(lSet2 instanceof Ris && lSet2.isBound()){ //rule 9
            LSet domain2 = ((Ris) lSet2).getDomain();
            Object t = domain2.getOne();
            LSet newDomain2 = domain2.removeOne();
            Ris newRis = new Ris(((Ris) lSet2).getControlTerm(), newDomain2, ((Ris) lSet2).getFilter(), ((Ris) lSet2).getPattern(), ((Ris) lSet2).getDummyVariables());
            switch(aConstraint.alternative){
                case 0:
                    solver.addChoicePoint(aConstraint);
                    solver.add(((Ris) lSet2).F(t));
                    ConstraintClass cc = new ConstraintClass();
                    Object p = ((Ris) lSet2).P(t,cc);
                    if(!cc.isEmpty())
                        solver.add(cc);
                    solver.add(lSet1.ncontains(p));
                    aConstraint.argument2 = newRis;
                    solver.storeUnchanged = false;
                    return;
                case 1:
                    solver.add(((Ris) lSet2).notF(t));
                    aConstraint.argument2 = newRis;
                    aConstraint.alternative = 0;
                    solver.storeUnchanged = false;
                    return;

            }

        }

        if(lSet1 instanceof Ris && lSet1.isBound()){ //rule 10
            aConstraint.argument1 = lSet2;
            aConstraint.argument2 = lSet1;
            disjRis((LSet)aConstraint.argument1, (LSet)aConstraint.argument2, aConstraint);
        }
    }

    /**
     * Solves an atomic constraint of the form {@code lSet1 disjoint lSet2}.
     * Two sets are disjoint if and only if they have no elements in common.
     * @param lSet1 first set.
     * @param lSet2 second set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void disj(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.disjCode;

        if (!lSet1.isBound() && !lSet2.isBound()) { // x disj y  (solved)
            if (lSet1 == lSet2) {                 // x disj x  (2)
                aConstraint.argument1 = lSet1;
                aConstraint.argument2 = LSet.empty();
                aConstraint.argument3 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                eqHandler.eq(aConstraint);
            }
            return;
        } else if (lSet1.isBoundAndEmpty() || lSet2.isBoundAndEmpty()) { // (1)
            aConstraint.setSolved(true);
            return;
        } else if (lSet1 == lSet2) {        // (4 special)
            solver.fail(aConstraint);
            return;
        } else if (lSet1.isBound() && !lSet2.isBound()) { // {t1|s1} || X //                                                         // (3)
            aConstraint.argument1 = lSet1.removeOne(); // s1||X
            ConstraintClass st = new ConstraintClass(Environment.ninCode, lSet1.getOne(),
                    lSet2); // t1.nin(X)
            solver.add(st);
            solver.storeUnchanged = false;
        } else if (!lSet1.isBound() && lSet2.isBound()) { // X || {t2|s2} //                                                        // (3)
            aConstraint.argument2 = lSet2.removeOne(); // X||s2
            ConstraintClass se = new ConstraintClass(Environment.ninCode, lSet2.getOne(),
                    lSet1); // t2.nin(X)
            solver.add(se);
            solver.storeUnchanged = false;
        } else { // {t1|s1}|| {t2|s2} (4)
            aConstraint.argument1 = lSet1.removeOne();
            aConstraint.argument2 = lSet2.removeOne(); // s1||s2
            ConstraintClass s1 = new ConstraintClass(Environment.neqCode, lSet1.getOne(),
                    lSet2.getOne()); // t1.neq(t2)
            ConstraintClass s2 = new ConstraintClass(Environment.ninCode, lSet1.getOne(),
                    lSet2.removeOne()); // t1.nin(s2)
            ConstraintClass s3 = new ConstraintClass(Environment.ninCode, lSet2.getOne(),
                    lSet1.removeOne()); // t2.nin(s1)
            solver.add(s1);
            solver.add(s2);
            solver.add(s3);
            solver.storeUnchanged = false;
        }
    }

    /**
     * Solves an atomic constraint of the form {@code lSet1 union lSet2 = lSet3}.
     * @param lSet1 first set.
     * @param lSet2 second set.
     * @param lSet3 third set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void unionCP(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull LSet lSet3, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert lSet3 != null;
        assert lSet3.equ == null;
        assert lSet1 instanceof CP || lSet2 instanceof CP || lSet3 instanceof CP;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null || aConstraint.argument4 instanceof LSet || aConstraint.argument4 instanceof LList;
        assert aConstraint.constraintKindCode == Environment.unionCode;

        //case i
        if(lSet1.isVariable() && lSet2.isVariable() && lSet2.isVariable()) {
            aConstraint.setSolved(true);
            return;
        }
        //case ii
        if((!lSet1.isVariable() && lSet1 instanceof CP)
                && (!lSet2.isVariable() && lSet2 instanceof CP)
                && (!lSet3.isVariable() && lSet3 instanceof CP)) {
            aConstraint.argument1 = lSet1;
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.argument2 = lSet2;
            solver.add(new AConstraint(Environment.eqCode, lSet1, lSet3));
            solver.storeUnchanged = false;
            return;
        }
        if((!lSet1.isVariable() && lSet1 instanceof CP)
                || (!lSet2.isVariable() && lSet2 instanceof CP)
                || (!lSet3.isVariable() && lSet3 instanceof CP)) {
            LSet N1 = new LSet();
            LSet N2 = new LSet();
            LSet N3 = new LSet();
            ConstraintClass c1 = UCP(lSet1, N1);
            ConstraintClass c2 = UCP(lSet2, N2);
            ConstraintClass c3 = UCP(lSet3, N3);
            AConstraint un = new AConstraint(Environment.unionCode, tauCP(lSet1, N1), tauCP(lSet2,N2), tauCP(lSet3, N3));

            aConstraint.argument1 = c1.get(0).argument1;
            aConstraint.argument2 = c1.get(0).argument2;
            aConstraint.argument3 = c1.get(0).argument3;
            aConstraint.constraintKindCode = c1.get(0).constraintKindCode;

            solver.add(c2);
            solver.add(c3);
            solver.add(un);
            solver.storeUnchanged = false;
        }

    }

    /**
     * Implements the function tau described in the CP paper.
     * @param t a logical set
     * @param Ni a new variable.
     * @return {@code t} if it is not bound, {@code LSet.empty()} if
     * {@code t} is bound and empty. If {@code t} is a {@code CP},
     * is initialized and not empty this method returns
     * a new logical set containing a pair that has the first element
     * taken from the first set of the cartesian product {@code t} and the
     * second element taken from the second set of the cartesian product {@code t}
     * and {@code Ni} as its tail.
     * @author Marco Ghezzi
     */
    private @NotNull LSet tauCP(@NotNull LSet t, @NotNull LSet Ni) {
        assert t != null;
        assert Ni != null;

        if(t instanceof CP && t.isBound() && !t.isEmpty()) {
            CP c = (CP) t;
            Object xi = c.getFirstSet().getOne();
            Object yi = c.getSecondSet().getOne();
            return Ni.ins(new LPair(xi, yi));
        }
        else if(t.isBound() && t.isEmpty())
            return LSet.empty();
        else
            return t;
    }

    /**
     * Implements the function UCP from the document containing the rewrite rules
     * for {@code CP}s.
     * @param t a logical set.
     * @param Ni a logical set.
     * @return the result of the UCP function.
     * @author Marco Ghezzi
     */
    private @NotNull
    ConstraintClass UCP(@NotNull LSet t, @NotNull LSet Ni) {
        assert t != null;
        assert Ni != null;

        if(t instanceof CP && t.isBound() && !t.isEmpty() && !t.isVariable()) {
            CP cp = (CP) t;
            Object xi = cp.getFirstSet().getOne();
            LSet Xi = cp.getFirstSet().removeOne();
            Object yi = cp.getSecondSet().getOne();
            LSet Yi = cp.getSecondSet().removeOne();

            if(!t.isEmpty() && !Xi.isEmpty() && !Yi.isEmpty()) {
                return new ConstraintClass(new AConstraint(
                        Environment.unionCode, new CP(LSet.empty().ins(xi), Yi),
                        new CP(Xi, cp.getSecondSet().ins(yi)),
                        Ni));
            }
            if(!Xi.isEmpty() && Yi.isEmpty())
                return Ni.eq(new CP(Xi, LSet.empty().ins(yi)));
            if(!Yi.isBoundAndEmpty() && Xi.isBoundAndEmpty())
                return Ni.eq(new CP(LSet.empty().ins(xi), Yi));
            if(Xi.isBoundAndEmpty() && Yi.isBoundAndEmpty())
                return Ni.eq(LSet.empty());
            return ConstraintClass.truec();
        }
        return ConstraintClass.truec();
    }
  
    /**
     * Solves an atomic constraint of the form {@code set1 union set2 = set3} when at least one of the three
     * arguments is a RIS.
     * @param lSet1 first logical set.
     * @param lSet2 second logical set.
     * @param lSet3 third logical set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private void unionRis(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull LSet lSet3, @NotNull AConstraint aConstraint){
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert lSet3 != null;
        assert lSet3.equ == null;
        assert lSet1 instanceof Ris || lSet2 instanceof Ris || lSet3 instanceof Ris;
        assert aConstraint != null;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null || aConstraint.argument4 instanceof LSet || aConstraint.argument4 instanceof LList;
        assert aConstraint.constraintKindCode == Environment.unionCode;

        if(!lSet1.isBound() && !lSet2.isBound() && !lSet3.isBound()) // solved form
            return;

        if(lSet2 instanceof Ris && ! (lSet1 instanceof Ris) && lSet3 instanceof Ris && lSet2.equals(lSet3)){
            Ris ris = (Ris) lSet2;
            if(ris.getDomain().getEndOfEquChain() == lSet1.getEndOfEquChain()
                    && LObject.equals(ris.getControlTerm(), ris.getPattern())
                    && lSet1.isBound()){
                if(lSet1.isEmpty()) {
                    aConstraint.setSolved(true);
                    return;
                }
                LSet tail = lSet1.getTail();
                for(Object element : lSet1)
                    solver.add(ris.F(element));
                solver.storeUnchanged =  true;
                aConstraint.argument1 = tail;
                aConstraint.argument2 = new Ris(
                        ris.getControlTerm(),
                        tail,
                        ris.getFilter(),
                        ris.getPattern(),
                        ris.getDummyVariables()
                );
                aConstraint.argument3 = aConstraint.argument2;
                return;
            }

        }

        if (dealWithRisExpansion(aConstraint)){
            union(aConstraint);
            return;
        }

        if(lSet1.equals(lSet2)){ // rule 1
            aConstraint.argument3 = null;
            aConstraint.argument2 = lSet3;
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }//end rule 1

        if(lSet3.isBound() && lSet3.isEmpty()){ //rule 2
            aConstraint.argument2 = LSet.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.argument3 = null;
            solver.add(lSet2.eq(LSet.empty()));
            solver.storeUnchanged = false;
            return;
        }//end rule 2

        if(lSet1.isBound() && lSet1.isEmpty() && !lSet3.isBound()){ //rule 3
            aConstraint.argument1 = lSet3;
            aConstraint.argument3 = null;
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }//end rule 3

        if(lSet2.isBound() && lSet2.isEmpty() && !lSet3.isBound()) { // rule 4
            aConstraint.argument1 = lSet3;
            aConstraint.argument2 = lSet1;
            aConstraint.argument3 = null;
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        } //end rule 4

        if(!(lSet1 instanceof Ris) && lSet1.isBound() && !lSet3.isBound()) { // rule 5
            Object t = lSet1.getOne();
            LSet N = new LSet();
            LSet N1 = new LSet();
            LSet N2 = new LSet();
            LSet tN1 = N1.ins(t);
            LSet tN = N.ins(t);
            LSet tN2 = N2.ins(t);

            switch(aConstraint.alternative){
                case 0:
                    solver.addChoicePoint(aConstraint);
                    solver.add(lSet2.ncontains(t));
                    aConstraint.argument1 = N1;
                    aConstraint.argument2 = lSet2;
                    aConstraint.argument3 = N;
                    solver.storeUnchanged = false;
                    solver.add(lSet1.eq(tN1));
                    solver.add(lSet3.eq(tN));
                    return;

                case 1:
                    solver.add(lSet2.eq(tN2));
                    aConstraint.argument1 = N1;
                    aConstraint.argument2 = N2;
                    aConstraint.argument3 = N;
                    aConstraint.alternative = 0;
                    solver.storeUnchanged = false;
                    solver.add(lSet1.eq(tN1));
                    solver.add(lSet3.eq(tN));
                    return;
            }
        }//end rule 5

        if(!(lSet2 instanceof  Ris) && lSet2.isBound() && !lSet3.isBound()) {//rule 6
            aConstraint.argument1 = lSet2;
            aConstraint.argument2 = lSet1;
            unionRis((LSet) aConstraint.argument1, (LSet) aConstraint.argument2, (LSet) aConstraint.argument3, aConstraint);
            return;
        } //end rule 6

        if(!(lSet3 instanceof Ris) && lSet3.isBound()){ //rule 7
            Object t = lSet3.getOne();
            LSet N = new LSet();
            LSet N1 = new LSet();
            LSet N2 = new LSet();


            switch(aConstraint.alternative){
                case 0:
                    solver.addChoicePoint(aConstraint);
                    solver.add(lSet1.eq(N1.ins(t)));
                    aConstraint.argument1 = N1;
                    aConstraint.argument3 = N;
                    solver.storeUnchanged = false;
                    break;
                case 1:
                    solver.addChoicePoint(aConstraint);
                    solver.add(lSet2.eq(N1.ins(t)));
                    aConstraint.argument2 = N1;
                    aConstraint.argument3 = N;
                    aConstraint.alternative = 0;
                    solver.storeUnchanged = false;
                    break;
                case 2:
                    solver.add(lSet1.eq(N1.ins(t)));
                    solver.add(lSet2.eq(N2.ins(t)));
                    aConstraint.argument1 = N1;
                    aConstraint.argument2 = N2;
                    aConstraint.argument3 = N;
                    aConstraint.alternative = 0;
                    solver.storeUnchanged = false;
                    break;
            }
            solver.add(lSet3.eq(N.ins(t)));
            return;
        }//end rule 7

        //rule 8
        LSet N1 = T_i(lSet1);
        LSet N2 = T_i(lSet2);
        LSet N3 = T_i(lSet3);

        aConstraint.argument1 = N1;
        aConstraint.argument2 = N2;
        aConstraint.argument3 = N3;
        solver.storeUnchanged = false;
        solver.add(K_i(lSet1, N1));
        solver.add(K_i(lSet2, N2));
        solver.add(K_i(lSet3, N3));
        //end rule 8
    }

    /**
     * Computes the function Tau_i of the given logical set.
     * If {@code A} is not bound returns {@code A}.
     * If {@code A} is not a {@code Ris} returns {@code A}.
     * If {@code A} is bound and empty returns {@code LSet.empty()}.
     * If the ris expansion optimization is active and {@code A} is an expandable {@code Ris}
     * returns the result of its expansion.
     * Otherwise returns a new instance of the class of the domain of {@code A} obtained using its nullary constructor
     * (i.e., a variable set). See the document containing the {@code Ris} rewrite rules for more info.
     * @param A logical set input.
     * @return the result of the tau.
     * @throws IllegalArgumentException if the last case of the method applies and
     * {@code A}'s domain's class has not an accessible nullary constructor.
     * @author Andrea Fois
     */
    private @NotNull LSet T_i(@NotNull LSet A) {
        assert A != null;

        if(!A.isBound())
            return A;
        if(!(A instanceof Ris))
            return A;
        if(A.isBound() && A.isEmpty()){
            return LSet.empty();
        }
        if(solver.getOptimizationOptions().isRisExpansionOptimizationEnabled() && A.isBound() && ((Ris) A).isExpandable())
            return ((Ris) A).forceExpansion(solver);
        else
            try{
                return ((Ris) A).getDomain().getClass().newInstance();
            }catch (Throwable throwable){
                throw new IllegalArgumentException("RIS DOMAIN NULLARY CONSTRUCTOR NOT AVAILABLE");
            }
    }

    /**
     * Computes the function K_i of the given logical sets.
     * @param A first logical set input.
     * @param N secon logical set input.
     * @return the result of the K_i function. See the document containing
     * {@code Ris} rewrite rules for more info.
     * @throws IllegalArgumentException if the last case of the method applies and
     *  {@code A}'s domain's class has not an accessible nullary constructor.
     * @author Andrea Fois
     *
     */
    private @NotNull
    ConstraintClass K_i(@NotNull LSet A, @NotNull LSet N){
        assert A != null;
        assert N != null;

        if(!A.isBound())
            return ConstraintClass.truec();
        if(! (A instanceof  Ris))
            return ConstraintClass.truec();
        if(A.isBound() && A.isEmpty())
            return ConstraintClass.truec();
        if(solver.getOptimizationOptions().isRisExpansionOptimizationEnabled() && A.isBound() && ((Ris) A).isExpandable())
            return ConstraintClass.truec();
        Ris ris = (Ris)A;
        LSet domain = ris.getDomain();

        assert ris.isBound();

        Object d = domain.getOne();
        ConstraintClass filterApply = ris.F(d);
        ConstraintClass cc = new ConstraintClass();
        Object p = ris.P(d,cc);
        if(cc.isEmpty())
            cc = ConstraintClass.truec();
        Ris newRis = new Ris(ris.getControlTerm(), domain.removeOne(), ris.getFilter(), ris.getPattern(), ris.getDummyVariables());
        LSet newSet;
        try {
             newSet = newRis.getDomain().getClass().newInstance();
             newSet = newSet.ins(p);
             newSet.rest = newRis;
        } catch (Throwable e) {
            throw new IllegalArgumentException("RIS DOMAIN NULLARY CONSTRUCTOR NOT AVAILABLE");
        }
        ConstraintClass returnedConstraint = (N.eq(newSet).and(filterApply).and(cc)).or(N.eq(newRis).and(ris.notF(d)));
        assert returnedConstraint != null;
        return returnedConstraint;

    }
 
    /**
     * Solves an atomic constraint of the form {@code lSet3 = lSet1 union lSet2}.
     * @param lSet3 result of union.
     * @param lSet1 first set of union.
     * @param lSet2 second set of union.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void union(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull LSet lSet3, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert lSet3 != null;
        assert lSet3.equ == null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.unionCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;

        if (lSet1 == lSet2) {
            aConstraint.argument1 = lSet1;
            aConstraint.argument2 = lSet3;
            aConstraint.argument3 = null;
            aConstraint.constraintKindCode = Environment.eqCode;
            eqHandler.eq(aConstraint);
            return;
        } else if(solver.getOptimizationOptions().areFastUnionRulesEnabled()){

                aConstraint.setSolved(true);
                solver.add(new AConstraint(Environment.unionSubsetCode, aConstraint.argument1, aConstraint.argument2, aConstraint.argument3));
                solver.add(new AConstraint(Environment.subsetUnionCode, aConstraint.argument1, aConstraint.argument2, aConstraint.argument3));
                return;
            }else if (!lSet1.isBound() && !lSet2.isBound() && !lSet3.isBound()) {
                return;
            } else if (lSet3.isBoundAndEmpty()) {
                aConstraint.argument1 = lSet1;
                aConstraint.argument2 = LSet.empty();
                aConstraint.argument3 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                AConstraint s1 = new AConstraint(Environment.eqCode, lSet2,
                        LSet.empty());
                solver.add(solver.indexOf(aConstraint) + 1, s1);
                eqHandler.eq(aConstraint);
                return;
            } else if (lSet1.isBoundAndEmpty()) {
                aConstraint.argument1 = lSet3;
                aConstraint.argument2 = lSet2;
                aConstraint.argument3 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                eqHandler.eq(aConstraint);
                return;
            } else if (lSet2.isBoundAndEmpty()) {
                aConstraint.argument1 = lSet3;
                aConstraint.argument2 = lSet1;
                aConstraint.argument3 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                eqHandler.eq(aConstraint);
                return;
            } else if (lSet1.isBound() && lSet2.isBound() &&
                    lSet1.isGround() && lSet2.isGround()) { // GROUND CASES

                aConstraint.argument1 = lSet1.concat(lSet2);
                aConstraint.argument2 = lSet3;
                aConstraint.argument3 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                eqHandler.eq(aConstraint);
                return;
            } else if (lSet1.isBound() && lSet1.isGround()) {
                aConstraint.argument1 = lSet1.appendGround(lSet2);
                aConstraint.argument2 = lSet3;
                aConstraint.argument3 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                eqHandler.eq(aConstraint);
                return;
            } else if (lSet2.isBound() && lSet2.isGround()) {
                aConstraint.argument1 = lSet2.appendGround(lSet1);
                aConstraint.argument2 = lSet3;
                aConstraint.argument3 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                eqHandler.eq(aConstraint);
                return;
            } else if (solver.level >= 3 &&
                    lSet3.countAllElements() > 0) {

                LSet N = new LSet();
                aConstraint.argument1 = lSet3;
                aConstraint.argument2 = N.ins(lSet3.getOne());
                aConstraint.argument3 = null;
                aConstraint.constraintKindCode = Environment.eqCode;     // {t1|t}.eq( {t1|N})
                AConstraint s1 = new AConstraint(Environment.ninCode, lSet3.getOne(),
                        N);
                AConstraint s2 = new AConstraint(Environment.unionCode, lSet1,
                        lSet2, lSet3, N);
                s2.alternative = 0;
                solver.add(solver.indexOf(aConstraint) + 1, s1);
                solver.add(s2);

                eqHandler.eq(aConstraint);

                return;
            }  else if (solver.level >= 3 &&
                    lSet1.countAllElements() > 0 && !lSet3.isBound()) {
                LSet M = new LSet();
                LSet M1 = new LSet();
                aConstraint.argument1 = lSet1;
                aConstraint.argument2 = M1.ins(lSet1.getOne());
                aConstraint.argument3 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                AConstraint z1 = new AConstraint(Environment.ninCode, lSet1.getOne(),
                        M1);
                AConstraint z2 = new AConstraint(Environment.eqCode, lSet3,
                        M.ins(lSet1.getOne()));
                AConstraint z3 = new AConstraint(Environment.ninCode, lSet1.getOne(),
                        M);
                AConstraint z4 = new AConstraint(Environment.unionCode, lSet1,
                        lSet2, lSet3, LList.empty().ins(M1).insn(M)); // union/4
                z4.alternative = 0;
                solver.add(solver.indexOf(aConstraint) + 1, z1);
                solver.add(solver.indexOf(aConstraint) + 1, z2);
                solver.add(solver.indexOf(aConstraint) + 1, z3);
                solver.add(z4);
                eqHandler.eq(aConstraint);
                return;
            } else if (solver.level >= 3 &&
                    lSet2.countAllElements() > 0 && !lSet3.isBound()) {
                aConstraint.argument1 = lSet2;
                aConstraint.argument2 = lSet1;
                aConstraint.alternative = 0;
                union(lSet2, lSet1, lSet3, aConstraint);
                return;
            } else {
                return;
            }

    }

    /**
     * Union with four arguments, used only within the union constraint rewriting rules.
     * @param lSet3 result of union.
     * @param lSet1 first set of union.
     * @param lSet2 second set of union.
     * @param N fourth argument.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void union(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull LSet lSet3, @NotNull LSet N, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert lSet3 != null;
        assert lSet3.equ == null;
        assert N != null;
        assert N.equ == null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.unionCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 != null;

        switch (aConstraint.alternative) {
            case 0:
                solver.addChoicePoint(aConstraint);
                LSet N1 = new LSet();
                aConstraint.argument1 = lSet1;
                aConstraint.argument2 = N1.ins(lSet3.getOne());
                aConstraint.argument3 = null;
                aConstraint.argument4 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                aConstraint.alternative = 0;
                AConstraint s4 = new AConstraint(Environment.ninCode, lSet3.getOne(), N1); // t1.nin(N1)
                AConstraint s4b = new AConstraint(Environment.ninCode, lSet3.getOne(), lSet2); // t1.nin(lSet2)
                AConstraint s5 = new AConstraint(Environment.unionCode, N1, lSet2, N); // union(N1, lSet2, N)
                solver.add(solver.indexOf(aConstraint) + 1, s4);
                solver.add(solver.indexOf(aConstraint) + 1, s4b);
                solver.add(s5);
                eqHandler.eq(aConstraint);
                return;
            case 1: // (4) (ii)
                solver.addChoicePoint(aConstraint);
                LSet NN1 = new LSet();
                aConstraint.argument1 = lSet2;
                aConstraint.argument2 = NN1.ins(lSet3.getOne());
                aConstraint.argument3 = null;
                aConstraint.argument4 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                aConstraint.alternative = 0;
                AConstraint s6 = new AConstraint(Environment.ninCode, lSet3.getOne(), NN1); // t1.nin(NN1)
                AConstraint s6b = new AConstraint(Environment.ninCode, lSet3.getOne(), lSet1); // t1.nin(lSet1)
                AConstraint s7 = new AConstraint(Environment.unionCode, lSet1, NN1, N); // union(lSet1, NN1, N)
                solver.add(solver.indexOf(aConstraint) + 1, s6);
                solver.add(solver.indexOf(aConstraint) + 1, s6b);
                solver.add(s7);
                eqHandler.eq(aConstraint);
                return;
            case 2: // (4) (iii)
                LSet NNN1 = new LSet();
                aConstraint.argument1 = lSet1; // lSet1.eq( {t1|NNN1})
                aConstraint.argument2 = NNN1.ins(lSet3.getOne());
                aConstraint.argument3 = null;
                aConstraint.argument4 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                aConstraint.alternative = 0;
                AConstraint s8 = new AConstraint(Environment.ninCode, lSet3.getOne(), NNN1); // t1.nin(NNN1)
                LSet N2 = new LSet();
                AConstraint s9 = new AConstraint(Environment.eqCode, lSet2,
                        N2.ins(lSet3.getOne())); // lSet2.eq( {t1|N2})
                AConstraint s10 = new AConstraint(Environment.ninCode, lSet3.getOne(),
                        N2); // t1.nin(N2)
                AConstraint s10b = new AConstraint(Environment.ninCode, lSet3.getOne(),
                        NNN1); // t1.nin(N1)
                AConstraint s11 = new AConstraint(Environment.unionCode, NNN1,
                        N2, N); // union(NNN1, N2, N)
                solver.add(solver.indexOf(aConstraint) + 1, s8);
                solver.add(solver.indexOf(aConstraint) + 1, s9);
                solver.add(solver.indexOf(aConstraint) + 1, s10);
                solver.add(solver.indexOf(aConstraint) + 1, s10b);
                solver.add(s11);
                eqHandler.eq(aConstraint);
                return;
        }
    }

    /**
     * Union with five arguments, used only within the union constraint rewriting rules.
     * @param lSet3 result of union.
     * @param lSet1 first set of union.
     * @param lSet2 second set of union.
     * @param M1 fourth argument.
     * @param M fifth argument.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void union(
            @NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull LSet lSet3, @NotNull LSet M1, @NotNull LSet M, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert lSet3 != null;
        assert lSet3.equ == null;
        assert M1 != null;
        assert M != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.unionCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 != null;
        M1 = M1.getEndOfEquChain();
        M = M.getEndOfEquChain();

        switch (aConstraint.alternative) {
            case 0: // (5) (i)
                solver.addChoicePoint(aConstraint);
                aConstraint.argument1 = lSet1.getOne(); // t1.nin(lSet2)
                aConstraint.argument2 = lSet2;
                aConstraint.argument3 = null;
                aConstraint.argument4 = null;
                aConstraint.constraintKindCode = Environment.ninCode;
                aConstraint.alternative = 0;
                AConstraint z5 = new AConstraint(Environment.unionCode, M1, lSet2, M); // union(M1,lSet2,M)
                solver.add(z5);
                nin(aConstraint);
                return;
            case 1: // (5) (ii)
                LSet M2 = new LSet();
                aConstraint.argument1 = lSet2; // lSet2.eq( {t1|M2})
                aConstraint.argument2 = M2.ins(lSet1.getOne());
                aConstraint.argument3 = null;
                aConstraint.argument4 = null;
                aConstraint.constraintKindCode = Environment.eqCode;
                aConstraint.alternative = 0;
                AConstraint z6 = new AConstraint(Environment.ninCode, lSet1.getOne(), M2); // t1.nin(M2)
                AConstraint z7 = new AConstraint(Environment.unionCode, M1, M2, M); // union(M1,M2,M)
                solver.add(solver.indexOf(aConstraint) + 1, z6);
                solver.add(z7);
                eqHandler.eq(aConstraint);
                return;
        }

    }

    /**
     * Solves an atomic constraint of the form {@code lSet1 subset of lSet2}.
     * @param lSet1 logical set.
     * @param lSet2 logical set.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void subset(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.subsetCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

        if(lSet1.isBound() && lSet1.isGround() && lSet1 instanceof CP)
            lSet1 = ((CP) lSet1).expand();
        if(lSet2.isBound() && lSet2.isGround() && lSet2 instanceof CP)
            lSet2 = ((CP) lSet2).expand();
        aConstraint.argument1 = lSet1;
        aConstraint.argument2 = lSet2;
        aConstraint.argument3 = lSet2;
        aConstraint.argument4 = null;
        aConstraint.constraintKindCode = Environment.unionCode;
        aConstraint.alternative = 0;
        solver.storeUnchanged = false;
    }

    /**
     * Solves an atomic constraint of the form {@code lSet3 = lSet1 intersection lSet2}.
     * @param lSet1 first set being intersected.
     * @param lSet2 second set being intersected.
     * @param lSet3 result of intersection.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void inters(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull LSet lSet3, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert lSet3 != null;
        assert lSet3.equ == null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.intersCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;

        LSet aux1 = new LSet();
        aConstraint.argument1 = aux1;
        aConstraint.argument2 = lSet3;
        aConstraint.argument3 = lSet1;
        aConstraint.argument4 = null;
        aConstraint.constraintKindCode = Environment.unionCode;     // union(aux1,lSet3,lSet1)
        aConstraint.alternative = 0;

        LSet aux2 = new LSet();
        AConstraint c1 = new AConstraint(Environment.unionCode, aux2, lSet3,lSet2);  // union(aux2,lSet3,lSet2)
        AConstraint c2 = new AConstraint(Environment.disjCode, aux1, aux2);        // disj(aux1,aux2)
        solver.add(solver.indexOf(aConstraint) + 1, c1);
        solver.add(c2);
        solver.storeUnchanged = false;
    }


    /**
     * Solves an atomic constraint of the form {@code lSet3 = lSet1 - lSet2}.
     * @param lSet1 first set of difference.
     * @param lSet2 second set of difference.
     * @param lSet3 result of difference.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void diff(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull LSet lSet3, @NotNull AConstraint aConstraint) {
        // diff(s1,s2,s3) <==> subset(s3,s1) & union(s2,s3,aux1) & subset(s1,aux1) & disj(s2,s3)
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert lSet3 != null;
        assert lSet3.equ == null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.diffCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;

        LSet aux1 = new LSet();
        aConstraint.argument1 = lSet3;
        aConstraint.argument2 = lSet1;
        aConstraint.argument3 = null;
        aConstraint.argument4 = null;
        aConstraint.constraintKindCode = Environment.subsetCode;     // subset(s3,s1)
        aConstraint.alternative = 0;

        AConstraint c1 = new AConstraint(Environment.unionCode, lSet2, lSet3,aux1);  // union(lSet2,lSet3,aux1)
        AConstraint c2 = new AConstraint(Environment.subsetCode, lSet1, aux1);      // subset(s1,aux1)
        AConstraint c3 = new AConstraint(Environment.disjCode, lSet2, lSet3);        // disj(s2,s3)
        solver.add(solver.indexOf(aConstraint) + 1, c1);
        solver.add(solver.indexOf(aConstraint) + 1, c2);
        solver.add(c3);
        solver.storeUnchanged = false;
    }

    /**
     * Solves an atomic constraint of the form {@code lSet2 = lSet1 - {element}}.
     * @param lSet1 first set of difference.
     * @param element only element of the singleton set.
     * @param lSet2 result of difference.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void less(@NotNull LSet lSet1, @NotNull Object element, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        // less(s1,element,s2) <==> s1 = {element|lSet2} & element nin s2
        assert lSet1 != null;
        assert lSet1.equ == null;
        assert lSet2 != null;
        assert lSet2.equ == null;
        assert element != null;
        assert ! (element instanceof LObject) || ((LObject) element).equ == null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.lessCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;

        LSet aux = lSet2.ins(element);
        aConstraint.argument1 = lSet1;
        aConstraint.constraintKindCode = Environment.eqCode;     // s1 = {element|lSet2}
        aConstraint.argument2 = aux;
        aConstraint.argument3 = null;
        aConstraint.argument4 = null;
        aConstraint.alternative = 0;
        solver.add(new AConstraint(Environment.ninCode, element, lSet2));  // element nin lSet2
        eqHandler.eq(aConstraint);
    }

    /**
     * Deterministic size, more efficient if arguments are ground (linear complexity).
     * @param lSet set whose size is to evaluate.
     * @param size cardinality of the set.
     * @param aConstraint atomic constraint {@code size}.
     * @throws jsetl.exception.Fail if inconsistencies are found
     * @author Roberto Amadini
     */
    private void detSize(@NotNull LSet lSet, @NotNull IntLVar size, @NotNull AConstraint aConstraint) {
        assert lSet != null;
        assert lSet.equ == null;
        assert size != null;
        assert size.equ == null;
        assert aConstraint != null;
        assert lSet.isGround() || size.isBound();
        assert aConstraint.constraintKindCode == Environment.sizeCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

        if (lSet.isGround()) {
            solver.add(size.eq(lSet.getSize()));
            aConstraint.setSolved(true);
            solver.storeUnchanged = false;
        }
        else { // size is bound
            Integer k = size.getValue();
            LSet newS = LSet.mkSet(k);
            if (k > 1)
                solver.add(ConstraintClass.allDifferent(newS.toArrayList()));
            solver.add(lSet.eq(newS));             // lSet={X1,..,Xk}
            solver.storeUnchanged = false;
            aConstraint.setSolved(true);
        }
    }

    /**
     * Nondeterministic size, uses chronological backtracking.
     * @param lSet set whose size is to evaluate.
     * @param size cardinality of the set.
     * @param aConstraint atomic constraint {@code size}.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Roberto Amadini
     */
    private void nonDetSize(@NotNull LSet lSet, @NotNull IntLVar size, @NotNull AConstraint aConstraint) {
        assert lSet != null;
        assert lSet.equ == null;
        assert size != null;
        assert size.equ == null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.sizeCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

        Object obj = lSet.getOne();
        LVar e;
        if (obj instanceof LVar)
            e = (LVar)(obj);
        else
            e = new LVar(obj);
        LSet r = lSet.removeOne();
        IntLVar M = new IntLVar();
        solver.add(size.ge(1));
        switch (aConstraint.getAlternative()) {
            case 0:
                solver.addChoicePoint(aConstraint);
                solver.add(e.nin(r));
                solver.add(M.eq(size.sub(1)));
                solver.add(r.size(M));
                aConstraint.setSolved(true);
                solver.storeUnchanged = false;
                break;
            case 1:
                LSet R = new LSet();
                LSet R1 = new LSet(R).ins(e);
                solver.add(r.eq(R1));
                solver.add(e.nin(R));
                solver.add(M.eq(size.sub(1)));
                solver.add(R.size(M));
                aConstraint.setSolved(true);
                solver.storeUnchanged = false;
                break;
        }
    }

    /**
     * Solves an atomic constraint of the form {@code integer = cardinality(RIS)},
     * in which {@code RIS, integer} are respectively the first and second arguments of
     * {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private void sizeRis(@NotNull AConstraint aConstraint){
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof Ris;
        assert aConstraint.argument2 instanceof Integer || aConstraint.argument2 instanceof IntLVar;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.sizeCode;

        if(dealWithRisExpansion(aConstraint)){
            size(aConstraint);
            return;
        }

        if (!(aConstraint.argument2 instanceof IntLVar)) {
            IntLVar aux = new IntLVar((Integer)aConstraint.argument2);
            aConstraint.argument2 = aux;
            sizeRis(aConstraint);
            return;
        }


        Ris ris = (Ris) aConstraint.argument1;
        IntLVar size = (IntLVar) aConstraint.argument2;
        if(!ris.isBound() && ! size.isBound()){
            AConstraint c1 = new AConstraint(Environment.geCode, aConstraint.argument2, 0);  // s.argument2 >= 0
            solver.add(c1);
        }
        else if(size.isBound() && size.getValue() < 0) {
            solver.fail(aConstraint);
        }
        else {
            LSet n = new LSet();
            solver.add(n.eq(ris));
            aConstraint.argument1 = n;
            solver.storeUnchanged = false;
            size(aConstraint);
        }

    }

    /**
     * Solves a size constraint on a {@code CP}, uses chronological backtracking.
     * @param cp set whose size is to evaluate.
     * @param size cardinality of the set.
     * @param aConstraint atomic constraint {@code size}.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Marco Ghezzi
     */
    private void sizeCP(@NotNull CP cp, @NotNull IntLVar size, @NotNull AConstraint aConstraint){
        assert cp != null;
        assert cp.equ == null;
        assert size != null;
        assert size.equ == null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.sizeCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;

        aConstraint.argument3 = null;
        if (cp.isBound() && cp.isGround()) {
            solver.add(size.eq(cp.getSize()));
            solver.storeUnchanged = false;
            aConstraint.setSolved(true);
        }
        else {
            switch (aConstraint.alternative) {
                case 0:  //  size = 0 and A = {}
                    solver.addChoicePoint(aConstraint);
                    int zero = 0;
                    aConstraint.argument1 = size;
                    aConstraint.constraintKindCode = Environment.eqCode;
                    aConstraint.argument2 = zero;
                    aConstraint.alternative = 0;
                    solver.add(new AConstraint(Environment.eqCode, cp.getFirstSet(), LSet.empty()));
                    solver.storeUnchanged = false;
                    return;
                case 1: // size = 0 and B = {}
                    solver.addChoicePoint(aConstraint);
                    IntLVar zero1 = new IntLVar(0);
                    aConstraint.argument1 = size;
                    aConstraint.constraintKindCode = Environment.eqCode;
                    aConstraint.argument2 = zero1;
                    aConstraint.alternative = 0;
                    solver.add(new AConstraint(Environment.eqCode, cp.getSecondSet(), LSet.empty()));
                    solver.storeUnchanged = false;
                    return;
                case 2: // size neq 0 and size(A,N1) and size(B,N2) and size = N1 * N2
                    IntLVar zero2 = new IntLVar(0);
                    aConstraint.argument1 = size;
                    aConstraint.constraintKindCode = Environment.neqCode;
                    aConstraint.argument2 = zero2;
                    aConstraint.alternative = 0;
                    IntLVar N1 = new IntLVar();
                    IntLVar N2 = new IntLVar();
                    solver.add(new AConstraint(Environment.sizeCode, cp.getFirstSet(), N1));
                    solver.add(new AConstraint(Environment.sizeCode, cp.getSecondSet(), N2));
                    solver.add(new AConstraint(Environment.eqCode, size, N1.mul(N2)));

                    solver.storeUnchanged = false;
            }
        }
    }

    /**
     * Solves an atomic constraint of the form {@code (set1 union set2) subset of set3},
     * in which {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private void unionSubset(@NotNull AConstraint aConstraint){
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.unionSubsetCode;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 instanceof LSet;
        assert aConstraint.argument4 == null;

        manageEquChains(aConstraint);

        LSet a = (LSet) aConstraint.argument1;
        LSet b = (LSet) aConstraint.argument2;
        LSet c = (LSet) aConstraint.argument3;

        if(a.isBoundAndEmpty() && b.isBoundAndEmpty()){
            aConstraint.setSolved(true);
            return;
        }

        if(c.isBoundAndEmpty()){
            aConstraint.setSolved(true);
            solver.add(a.eq(LSet.empty()));
            solver.add(b.eq(LSet.empty()));
            return;
        }

        if(a.isBound() && !a.isEmpty()){
            Object oa = a.getOne();
            for(Object oc : c){
                if(oc.equals(oa) || oa.equals(oc)){
                    aConstraint.argument1 = a.removeOne();
                    solver.storeUnchanged = false;
                    return;
                }
            }
        }

        if(b.isBound() && !b.isEmpty()){
            Object oa = b.getOne();
            for(Object oc : c){
                if(oc.equals(oa) || oa.equals(oc)){
                    aConstraint.argument2 = b.removeOne();
                    solver.storeUnchanged = false;
                    return;
                }
            }
        }

        if(a.isBound() && !a.isEmpty()){
            Object oa = a.getOne();
            solver.add(c.contains(oa));
            aConstraint.argument1 = a.removeOne();
            solver.storeUnchanged = false;
            return;
        }

        if(b.isBound() && !b.isEmpty()){
            Object ob = b.getOne();
            solver.add(c.contains(ob));
            aConstraint.argument2 = b.removeOne();
            solver.storeUnchanged = false;
            return;
        }

    }

    /**
     * Solves an atomic constraint of the form {@code set3 subset of (set1 union set2)},
     * in which {@code set1, set2, set3} are respectively the first, second and third
     * arguments of {@code aConstraint}.
     * @param aConstraint atomic constraint to solve.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private void subsetUnion(@NotNull AConstraint aConstraint){
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.subsetUnionCode;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 instanceof LSet;
        assert aConstraint.argument4 == null;

        manageEquChains(aConstraint);

        LSet a = (LSet) aConstraint.argument1;
        LSet b = (LSet) aConstraint.argument2;
        LSet c = (LSet) aConstraint.argument3;

        if(c.isBoundAndEmpty()){
            aConstraint.setSolved(true);
            return;
        }

        if(a.isBoundAndEmpty() && b.isBoundAndEmpty()){
            aConstraint.argument1 = c;
            aConstraint.argument2 = LSet.empty();
            aConstraint.argument3 = null;
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.alternative = 0;
            eqHandler.eq(aConstraint);
            return;
        }

        if(c.isBound()){
            Object oc = c.getOne();
            for(Object oa : a)
                if(oc.equals(oa) || oa.equals(oc)){
                    aConstraint.argument3 = c.removeOne();
                    solver.storeUnchanged = false;
                    return;
                }

            for(Object ob : b)
                if(oc.equals(ob) || ob.equals(oc)){
                    aConstraint.argument3 = c.removeOne();
                    solver.storeUnchanged = false;
                    return;
                }

            if(a.isBoundAndEmpty()){
                solver.add(b.contains(oc));
                aConstraint.argument3 = c.removeOne();
                solver.storeUnchanged = false;
                return;
            }

            if(b.isBoundAndEmpty()){
                solver.add(a.contains(oc));
                aConstraint.argument3 = c.removeOne();
                solver.storeUnchanged = false;
                return;
            }

            aConstraint.argument3 = c.removeOne();
            solver.storeUnchanged = false;
            solver.add(a.contains(oc).or(b.contains(oc)));
        }
    }



}
