package jsetl;

import jsetl.annotation.NotNull;
import jsetl.exception.Failure;

/**
 * This abstract class provides basic methods and implementation of constructor for rewrite rules of built-in constraints.
 */
abstract class LibConstraintsRules {

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     *  Reference to the solver.
     */
    protected final Solver solver;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructor which takes a reference to a solver and stores it.
     * @param solver reference to a solver.
     */
    protected LibConstraintsRules(@NotNull Solver solver) {
        assert solver != null;
        this.solver = solver;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// ABSTRACT METHODS /////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Uses rewrite rules to solve the given atomic constraint.
     * @param aConstraint atomic constraint to solve.
     * @return {@code true} if some rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if the atomic constraint was found to be unsatisfiable.
     */
    protected abstract boolean solveConstraint(@NotNull AConstraint aConstraint);


    ///////////////////////////////////////////////////////////////
    //////////////// IMPLEMENTED METHODS //////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Manages the equ chain for each argument of {@code aConstraint}.
     * After the execution of this method no argument of {@code aConstraint} will have its {@code equ} field
     * with a value different than {@code null}.
     * @param aConstraint atomic constraint.
     * @return {@code true} if at least one argument of the atomic constraint was changed, {@code false} otherwise.
     */
    protected boolean manageEquChains(@NotNull AConstraint aConstraint){
        assert aConstraint != null;
        boolean somethingChanged = false;


        if(aConstraint.argument1 instanceof LObject && ((LObject) aConstraint.argument1).equ != null){
            aConstraint.argument1 = ((LObject) aConstraint.argument1).getEndOfEquChain();
            somethingChanged = true;
        }

        if(aConstraint.argument2 instanceof LObject && ((LObject) aConstraint.argument2).equ != null) {
            somethingChanged = true;
            aConstraint.argument2 = ((LObject) aConstraint.argument2).getEndOfEquChain();
        }

        if(aConstraint.argument3 instanceof LObject && ((LObject) aConstraint.argument3).equ != null) {
            somethingChanged = true;
            aConstraint.argument3 = ((LObject) aConstraint.argument3).getEndOfEquChain();
        }
        if(aConstraint.argument4 instanceof LObject && ((LObject) aConstraint.argument4).equ != null) {
            somethingChanged = true;
            aConstraint.argument4 = ((LObject) aConstraint.argument4).getEndOfEquChain();
        }

        return somethingChanged;
    }

    /**
     * Expands every expandable {@code Ris} arguments. This means that every expandable {@code Ris}
     * argument of {@code aConstraint} is substituted with its expansion in the atomic constraint provided.
     * @param aConstraint atomic constraint.
     * @return {@code true} if at least an argument was modified, {@code false} otherwise.
     */
    protected boolean dealWithRisExpansion(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        if(!solver.getOptimizationOptions().isRisExpansionOptimizationEnabled())
            return false;

        Object arg1 = aConstraint.argument1;
        Object arg2 = aConstraint.argument2;
        Object arg3 = aConstraint.argument3;
        boolean flag = false;
        if(arg1 instanceof Ris && ((Ris) arg1).isExpandable(solver)){
            aConstraint.argument1 = ((Ris) arg1).forceExpansion(solver);
            flag = true;
        }

        if(arg2 instanceof Ris && ((Ris) arg2).isExpandable(solver)){
            aConstraint.argument2 = ((Ris) arg2).forceExpansion(solver);
            flag = true;
        }

        if(arg3 instanceof Ris && ((Ris) arg3).isExpandable(solver)){
            aConstraint.argument3 = ((Ris) arg3).forceExpansion(solver);
            flag = true;
        }
        if(flag == true)
            solver.storeUnchanged = false;
        return flag;
    }
    
}
