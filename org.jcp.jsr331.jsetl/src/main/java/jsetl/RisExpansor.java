package jsetl;

import jsetl.annotation.NotNull;
import jsetl.ris.cache.CacheKey;
import jsetl.ris.cache.RisExpansionCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * This class deals with the expansion of the {@code Ris}.
 * It is used to check if the {@code Ris} is expandable and to construct such expansion.
 */
class RisExpansor {

    //////////////////////////////////////////////////////
    /////// DATA MEMBERS /////////////////////////////////
    //////////////////////////////////////////////////////

    /**
     * Reference to the {@code Ris} that is to expand.
     */
    private Ris ris;


    ////////////////////////////////////////////////////////
    /////// CONSTRUCTORS ///////////////////////////////////
    ////////////////////////////////////////////////////////

    /**
     * Constructs a {@code RisExpansor} for the given {@code ris}.
     * @param ris {@code Ris} to expand.
     */
    RisExpansor(@NotNull Ris ris) {
        assert ris != null;
        this.ris = ris;
    }

    /**
     * Checks whether the parent {@code Ris} is expandable or not.
     * A {@code Ris} is expandable if and only if its domain is empty or (it has at least a ground element
     * and the filter has no free (not dummy and not part of control term) variables).
     * @return {@code true} if the parent {@code Ris} is expandable, {@code false} otherwise.
     * @see #expand(SolverClass)
     */
    protected boolean canExpand(@NotNull SolverClass solver){
        assert solver != null;
        return domainIsEmpty() || (StreamSupport.stream(ris.getDomain().spliterator(), false).anyMatch(e -> this.canExpand(solver,e)) && filterHasNoFreeVariables());

    }

    /**
     * Constructs and returns the expansion of the parent {@code Ris}.
     * Precondition of this method is that calling {@code #canExpand()} returns {@code true}.
     * The expansion of a {@code Ris} is a {@code LSet} containing each possible {@code P(d)} for each ground {@code d in domain}
     * that satisfies {@code F(d)}. The constructed {@code LSet} has an empty tail ({@code LSet.empty()}) if the domain of the {@code Ris} is ground,
     * otherwise it contains a {@code Ris} with the same control term, filter and pattern as the original {@code Ris} and
     * a {@code LSet} containing each non-ground element of {@code domain} as its domain.
     * @return the expansion of the parent {@code Ris}.
     * @see #canExpand(SolverClass)
     */
    protected @NotNull
    LSet expand(@NotNull SolverClass solver){
        assert solver != null;

        final boolean useExpansionCache = solver.getOptimizationOptions().isRisExpansionCacheEnabled();
        ArrayList<LObject> backup = new ArrayList<>(LObject.getNotInitializedLObjectsArrayList());
        LSet domainTail = ris.getDomain().getTail();
        if(domainTail == null)
            domainTail = LSet.empty();

        List<Object> list = new ArrayList<>();
        RisExpansionCache risExpansionCache = null;
        if(useExpansionCache)
            risExpansionCache = solver.getRisExpansionCache();
        boolean filterHasExistentialVariables = filterHasExistentialVariables();
        for(Object element : ris.getDomain()){
            SolverClass localSolver = new SolverClass();
            if(useExpansionCache)
                localSolver.setRisExpansionCache(risExpansionCache);

            localSolver.setCurrentExpansions(solver.getCurrentExpansions());

            if(!useExpansionCache && isRisRecursive())
                localSolver.getOptimizationOptions()
                        .setUseRisExpansionOptimizationFlag(false);

            if(LObject.isGround(element)  && canExpand(solver, element)) {

                ArrayList<Object> list2 = new ArrayList<>();

                if(useExpansionCache) {
                    List<Object> expansion = localSolver.getRisExpansionCache().getExpansion(ris, element);
                    if (expansion != null) {
                        list.addAll(expansion);
                        continue;
                    }
                }

                localSolver.getCurrentExpansions().add(new CacheKey(ris.getControlTerm(), element, ris.getFilter(), ris.getPattern()));


                localSolver.add(ris.F(element));
                //multiple solutions for existential dummyVariables in recursive ris are not handled
                if(!isRisRecursive() && filterHasExistentialVariables)
                    localSolver.forEachSolution(i -> {
                        SolverClass solverCp = new SolverClass();
                        ConstraintClass cp = new ConstraintClass();
                            Object pp = ris.P(element, cp);
                            solverCp.add(cp);
                            if(solverCp.check())
                            list2.add(new DeepCloner().visit(pp));

                    });
                else{
                    ConstraintClass cc = new ConstraintClass();
                    Object p = ris.P(element, cc);
                    if (!cc.isEmpty())
                        localSolver.add(cc);
                    if (localSolver.check())
                        list2.add(p);

                }
                if(useExpansionCache)
                    localSolver.getRisExpansionCache().put(ris, element, list2);
                
                list.addAll(list2);
                localSolver.getCurrentExpansions().remove(new CacheKey(ris.getControlTerm(), element, ris.getFilter(), ris.getPattern()));

            } else{
                domainTail = domainTail.ins(element);
            }
        }

        LSet set = new Ris(ris.getControlTerm(), domainTail, ris.getFilter(), ris.getPattern(), ris.getDummyVariables());
        set.initialized = true;
        if(set.isBoundAndEmpty())
            set = LSet.empty();
        if(!list.isEmpty())
            set = set.insAll(list);
        List<LObject> notInitializedLObjects = LObject.getNotInitializedLObjectsArrayList();
        notInitializedLObjects.clear();
        notInitializedLObjects.addAll(backup);

        assert set != null;
        return set;
    }


    /////////////////////////////////////////
    ///// PRIVATE METHODS ///////////////////
    /////////////////////////////////////////

    /**
     * Checks whether the element of the domain {@code element} can be expanded by {@code this Ris} using the given {@code solver}.
     * @param solver a reference to a solver.
     * @param element an element to expand.
     * @return {@code true} if the element can be expanded, {@code false} otherwise.
     */
    private boolean canExpand(@NotNull SolverClass solver, @NotNull Object element){
        assert solver != null;
        assert element != null;
        return LObject.isGround(element) && !solver.getCurrentExpansions().contains(new CacheKey(ris.getControlTerm(), element, ris.getFilter(), ris.getPattern()));
    }

    /**
     * Checks whether the domain is bound to the empty set or not.
     * @return {@code true} if the domain is bound and empty, {@code false} otherwise.
     */
    private boolean domainIsEmpty(){
        return ris.getDomain().isBoundAndEmpty();
    }

    /**
     * Checks whether the filter has existential variables.
     * @return {@code true} if the filter has existential variables, {@code false} otherwise.
     */
    private boolean filterHasExistentialVariables(){
        VariablesGetter variablesGetter = new VariablesGetter();
        variablesGetter.avoidRis();
        List<LObject> filterVariables = variablesGetter.getVariables(ris.getFilter());
        for(LObject variable : filterVariables)
            if(isDummyVariable(variable))
                return true;
        return false;
    }

    /**
     * Checks whether the filter has no free variables (i.e. variables that are not part of the control term nor dummy variables) or not.
     * @return {@code true} if the filter has no free variables, {@code false} otherwise.
     */
    private boolean filterHasNoFreeVariables(){
        List<LObject> internalControlTerms = new LinkedList<>();
        new DeepExplorer().explore(ris, obj -> {
            if(obj instanceof Ris) internalControlTerms.add(((Ris) obj).getControlTerm());});
        List<LObject> controlTermVariables = new LinkedList<>();
        for(LObject lObject : internalControlTerms)
            controlTermVariables.addAll(new VariablesGetter().getVariables(lObject));
        VariablesGetter variablesGetter = new VariablesGetter();
        variablesGetter.ignoreVariablesInternalConstraints();
        List<LObject> filterVariables = variablesGetter.getVariables(ris.getFilter());
        for(LObject variable : filterVariables){
            boolean occursInControlTerm = false;
            for(LObject controlTermVariable : controlTermVariables)
                occursInControlTerm = occursInControlTerm || LObject.equals(variable, controlTermVariable);
            if(!occursInControlTerm && !variable.isBound() && !isDummyVariable(variable))
                return false;
        }
        return true;
    }

    /**
     * Checks whether the parent {@code Ris} is recursive or not.
     * @return {@code true} if the parent {@code Ris} is recursive, {@code false} otherwise.
     */
    private boolean isRisRecursive(){
        return ris.occurs(ris);
    }

    /**
     * Checks whether the given {@code object} is a dummy variable or not.
     * @param object object to test.
     * @return {@code true} if {@code object} is a dummy variable, {@code false} otherwise.
     */
    private boolean isDummyVariable(@NotNull Object object){
        assert object != null;

        if(object instanceof LObject){
            if(object instanceof IntLVar && ((IntLVar)object).isDummy())
                return true;
            else
                return Arrays.stream(ris.getDummyVariables()).anyMatch(dummy -> LObject.equals(dummy, object));
        }
        else
            return false;
    }

}
