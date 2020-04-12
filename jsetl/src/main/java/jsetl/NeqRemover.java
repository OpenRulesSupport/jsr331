package jsetl;

import jsetl.annotation.NotNull;

import java.util.*;

/**
 * This class is used by {@code solver} to remove inequalities in solved form in the constraint store (by rewriting them)
 * that could hide potential contradictions.
 * @see Solver
 * @author Andrea Fois
 */
class NeqRemover {

    ///////////////////////////////////////////////////////
    ////////////// STATIC MEMBERS /////////////////////////
    ///////////////////////////////////////////////////////

    /**
     * Set of integers containing the constraint kind codes of the unsafe solved forms.
     */
    private static final Set<Integer> unsafeSolvedFormsConstraintKinds = new HashSet<>(Arrays.asList(
            Environment.eqCode,
            Environment.unionCode,
            Environment.subsetUnionCode,
            Environment.unionSubsetCode,
            Environment.brCompCode,
            Environment.subsetCompCode,
            Environment.compSubsetCode,
            Environment.pfCompCode,
            Environment.brRanCode,
            Environment.pfRanCode,
            Environment.brDomCode,
            Environment.pfDomCode,
            Environment.invCode,
            Environment.idCode)
    );


    ///////////////////////////////////////////////////////
    ////////////// DATA MEMBERS ///////////////////////////
    ///////////////////////////////////////////////////////

    /**
     * Reference to the solver from whose constraint store unsafe inequalities are to be removed.
     */
    private final Solver solver;


    ///////////////////////////////////////////////////////
    /////////////// CONSTRUCTORS //////////////////////////
    ///////////////////////////////////////////////////////

    /**
     * Constructs a new {@code NeqRemover} for the given {@code solver}.
     * @param solver reference to the solver.
     */
    NeqRemover(@NotNull Solver solver){
        assert solver != null;
        this.solver = solver;
    }


    ///////////////////////////////////////////////////////
    ////////////// PACKAGE-SCOPED METHODS /////////////////
    ///////////////////////////////////////////////////////

    /**
     * Rewrites all the inequalities that contain references to objects
     * that appear also in unsafe solved forms. This method modifies the constraint store of the
     * {@code solver} that was passed to the constructor of this object.
     */
    void removeNeq(){
        List<AConstraint> inequalities = getInequalities();
        Set<LSet> setsInUnsafeSolvedForms = getSetsInUnsafeSolvedForms();

        for(AConstraint inequality : inequalities){
            if(rewriteNeeded(inequality, setsInUnsafeSolvedForms)){
                Constraint rewrittenInequality = getRewrittenInequality(inequality);
                solver.store.rewrite(inequality, rewrittenInequality);
            }
        }

    }


    /////////////////////////////////////////////////////////
    ////////////// PRIVATE METHODS //////////////////////////
    /////////////////////////////////////////////////////////

    /**
     * Constructs and returns a set containing all the
     * logical sets that appear in unsafe solved forms.
     * @return the constructed set.
     */
    @NotNull Set<LSet> getSetsInUnsafeSolvedForms(){
        Set<LSet> sets = new HashSet<>();
        for(AConstraint aConstraint : solver.store){
            if(aConstraint.getSolved() || !unsafeSolvedFormsConstraintKinds.contains(aConstraint.constraintKindCode))
                continue;
            for(int i = 1; i <= AConstraint.MAX_ACONSTRAINT_ARGUMENTS; ++i){
                Object arg = aConstraint.getArg(i);
                if(arg instanceof LSet) {
                    if(!((LSet) arg).isBound())
                        sets.add((LSet) arg);
                    if(arg instanceof Ris){
                        LSet innermostDomain = getInnermostRisDomain((Ris) arg);
                        if(!innermostDomain.isBound())
                            sets.add(innermostDomain);
                    }
                }
            }
        }

        assert sets != null;
        return sets;
    }

    /**
     * Returns a list containing all the (not solved) inequalities in {@code constraintStore}.
     * @return a list containing all the inequalities.
     */
    @NotNull List<AConstraint> getInequalities(){
        List<AConstraint> inequalities = new LinkedList<>();
        for(AConstraint aConstraint : solver.store)
            if(aConstraint.constraintKindCode == Environment.neqCode && !aConstraint.getSolved())
                inequalities.add(aConstraint);
        assert inequalities != null;
        return inequalities;
    }

    /**
     * Returns the innermost domain of the {@code Ris}.
     * If the domain of the {@code ris} is not a {@code Ris}, then it is
     * the innermost domain, otherwise the innermost domain of the domain
     * is returned.
     * @param ris {@code Ris} of which the innermost domain is to be retrieved.
     * @return the innermost domain of the argument.
     */
    @NotNull LSet getInnermostRisDomain(@NotNull Ris ris){
        assert ris != null;

        LSet domain = ris.getDomain();
        while(domain instanceof Ris){
            domain = ((Ris) domain).getDomain();
        }

        assert domain != null;
        return domain;
    }

    /**
     * Checks whether the given {@code inequality} should be rewritten.
     * It should if it contains variables that appear also in {@code setsInUnsafeSolvedForms}.
     * @param inequality an inequality.
     * @param setsInUnsafeSolvedForms sets of logical sets that appear in unsafe solved forms.
     * @return {@code true} if {@code inequality} should be rewritten, {@code false} otherwise.
     */
    boolean rewriteNeeded(@NotNull AConstraint inequality, @NotNull Set<LSet> setsInUnsafeSolvedForms){
        assert inequality != null;
        assert inequality.argument3 == null;
        assert inequality.argument4 == null;
        assert inequality.constraintKindCode == Environment.neqCode;
        assert setsInUnsafeSolvedForms != null;

        Object arg1 = inequality.getArg(1);
        assert arg1 != null;
        Object arg2 = inequality.getArg(2);
        assert arg2 != null;

        for(LSet lSet : setsInUnsafeSolvedForms){
            if(LObject.equals(arg1, lSet) || LObject.equals(arg2, lSet))
                return true;
        }

        return false;
    }

    /**
     * Constructs and returns the constraint containing the rewritten inequality {@code neq}.
     * @param neq inequality to rewrite.
     * @return the constructed constraint.
     */
    @NotNull Constraint getRewrittenInequality(@NotNull AConstraint neq){
        assert neq != null;

        if(neq.argument1 instanceof LSet == false && neq.argument2 instanceof LSet == false)
            throw new IllegalArgumentException();
        if(neq.argument1 instanceof LSet == false) {
            Object tmp = neq.argument1;
            neq.argument1 = neq.argument2;
            neq.argument2 = tmp;
        }
        Object newLVar = new LVar("newlvar");
        if(neq.argument1 instanceof IntLSet || neq.argument2 instanceof IntLSet)
            newLVar = new IntLVar("newintlvar");
        if(neq.argument1 instanceof LRel || neq.argument2 instanceof  LRel)
            newLVar = new LPair(new LVar(), new LVar());
        Constraint newConstraintFirst = new Constraint(Environment.inCode, newLVar, neq.argument1)
                        .and(new Constraint(Environment.ninCode, newLVar, neq.argument2));

        Constraint newConstraintSecond =
                new Constraint(Environment.inCode, newLVar, neq.argument2)
                        .and(new Constraint(Environment.ninCode, newLVar, neq.argument1));

        Constraint newConstraintThird =
                new Constraint(Environment.eqCode, neq.argument1, LSet.empty())
                        .and(new Constraint(Environment.neqCode, neq.argument2, LSet.empty()));

        Constraint rewritten = newConstraintFirst.or(newConstraintSecond).or(newConstraintThird);
        assert rewritten != null;
        return rewritten;
    }

}
