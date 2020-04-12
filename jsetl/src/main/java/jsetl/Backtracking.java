package jsetl;

import jsetl.annotation.NotNull;
import jsetl.ris.cache.RisExpansionCache;

import java.util.*;

/**
 * The {@code Backtracking} class implements chronological backtracking.
 */
class Backtracking {

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

	/**
     * Reference to the solver.
     */
    private final Solver solver;
    
    /**
     * The stack of open choice points.
     */
    private final Stack<ChoicePoint> openChoicePointsStack = new Stack<>();


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    /**
     * Creates a new instance of {@code Backtracking}.
     * @param solver a solver instance.
     */
    protected Backtracking(@NotNull Solver solver) {
        assert solver != null;

        this.solver = solver;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /* The method backtrack() pops a new alternative from the stack using a call to the
       method getAlternative(), restores the constraint store and its variables back to the state
       in which they were when the choice point, to which the failure is related, was opened.
       After that the method proceeds on solving atomic constraints starting from the atomic constraint
       in solver.store which has given the new current alternative.
       If the choice points stack is empty when the method tries to get an alternative, an EmptyStackException
       is raised: in this situations there are no more open choice points to try so the exception is managed by
       returning false, which signals that the computation has failed, i.e. the constraint conjunction in
       Solved.store does not have a solution. Otherwise true is returned, which means that the backtracking was performed.
     */
    /**
     * Finds an open choice points and restores the constraint store to a
     * correct state, variables have the same values as they had at the 
     * moment when the choice point was opened.
     *
     * @return {@code true} if there were open choice points, {@code false} otherwise.
     */
    protected boolean backtrack(){
        try {
            this.tryBacktracking();
            return true;
        }
        catch(EmptyStackException emptyStackException) {
            return false;
        }
    }

    /**
     * Adds a choice point to the open choice points stack.
     *
     * @param aConstraint an atomic constraint.
     */
    protected void addChoicePoint(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        ++aConstraint.alternative;
        this.openChoicePointsStack.push(createBackupChoicePoint());
        --aConstraint.alternative;
    }

    /**
     * Creates and returns a choice point that contains the information needed to restore the
     * state of the constraint store and variables back to the moment this method was called.
     * The method {@code restoreFromChoicePoint} is used to restore the state using the created
     * choice point.
     * @return the created choice point.
     * @see Backtracking#restoreFromChoicePoint(ChoicePoint)
     */
    protected @NotNull ChoicePoint createBackupChoicePoint(){
        VariablesState variablesState = getVarState();
        ConstraintStore constraintStore = cloneStoreUnSolved();
        RisExpansionCache risExpansionCache = null;
        if(solver.getOptimizationOptions().isRisExpansionCacheEnabled())
            risExpansionCache = getRisExpansionCacheBackup();
        return new ChoicePoint(variablesState, constraintStore, risExpansionCache);
    }

    /**
     * Uses a choice point previously created (via the method {@code createBackupChoicePoint} to restore
     * the state of the constraint store and variables.
     * @param backupChoicePoint the choice point containing the information needed to restore the
     *                          state.
     * @see Backtracking#createBackupChoicePoint()
     */
    protected void restoreFromChoicePoint(@NotNull ChoicePoint backupChoicePoint){
        assert backupChoicePoint != null;

        restoreVariablesState(backupChoicePoint.varsState);
        restoreStoreState(backupChoicePoint.constraintStore);
        if(solver.getOptimizationOptions().isRisExpansionCacheEnabled())
            restoreRisCache(backupChoicePoint.risExpansionCache);
    }

    /**
     * Extracts a choice point from the open choice points stack.
     *
     * @return the choice point at the top of the choice points stack.
     * @throws EmptyStackException if the open choice point stack is empty.
     */
    protected @NotNull ChoicePoint getAlternative() throws EmptyStackException {
        ChoicePoint choicePoint = this.openChoicePointsStack.pop();

        assert choicePoint != null;
        return choicePoint;
    }

    /**
     * Gets the number of choice points in the open choice points stack.
     *
     * @return  The size of of the open choice points stack.
     * @exception  EmptyStackException if the open choice points stack is empty.
     */
    protected int getNumberOfAlternatives() throws EmptyStackException {
        return this.openChoicePointsStack.size();
    }

    /**
     * Updates the alternatives (i.e. choice points in the open choice points stack)
     * by adding new constraints and variables to the all choice points in the stack of alternatives.
     */
    protected void updateAlternatives(){
        this.updateAlternativesStores();
        this.updateAlternativesVars();
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PRIVATE METHODS //////////////////////////////
    ///////////////////////////////////////////////////////////////

    /////////////// RESTORING PREVIOUS STATE //////////////////////

    /**
     * Common operation for backtracking. Tries finding an alternative
     * and restores a correct state for it.
     *
     * @exception  EmptyStackException  if no open choice points are available.
     * @see     jsetl.Backtracking#backtrack
     */
    private void tryBacktracking() throws EmptyStackException {
        ChoicePoint choicePoint = this.getAlternative();
        restoreFromChoicePoint(choicePoint);
        this.solver.storeUnchanged = false;
        return;
    }

    /**
     * Restores variables to uninitialized value.
     *
     * @param variablesState a variables state stored.
     */
    private void restoreVariablesState(@NotNull VariablesState variablesState) {
        assert variablesState != null;

        List<LObject> notInitializedLObjects = LObject.getNotInitializedLObjectsArrayList();
        notInitializedLObjects.clear();
        notInitializedLObjects.addAll(variablesState.notInitializedLObjects);
        int size = variablesState.notInitializedLObjects.size();

        assert size == variablesState.setLVarDomains.size();
        assert size == variablesState.intLVarDomains.size();

        for(int i = 0; i < size; ++i){
            LObject lObject = variablesState.notInitializedLObjects.get(i);
            lObject.makeVariable();

            if(lObject instanceof IntLVar)
                ((IntLVar) lObject).setDomain(variablesState.intLVarDomains.get(i));
            else if(lObject instanceof SetLVar)
                ((SetLVar) lObject).setDomain(variablesState.setLVarDomains.get(i));
        }

    }

    /**
     * Restores the constraint store of the solver.
     *
     * @param constraintStore a constraint store to be restored.
     */
    private void restoreStoreState(@NotNull ConstraintStore constraintStore) {
        assert constraintStore != null;

        this.solver.store = constraintStore;
        return;
    }

    /**
     * Restores the Ris Expansion Cache to be equal to the parameter {@code cache}.
     * @param cache the previous state of the Ris Expansion Cache to be restored.
     */
    private void restoreRisCache(@NotNull RisExpansionCache cache){
        assert cache != null;
        solver.getRisExpansionCache().restore(cache);
    }

    /////////////// SAVING PREVIOUS STATE //////////////////////

    /*
        Returns a new element with type VariablesState which has one attribute of type ArrayList<LObject>.
        This list contains all the not initialized variables. That information is stored
        for later use, in particular ot will be needed to restore the constraint store to a
        previous state when a tryBacktracking occurs: variables which were not initialized will be
        not initialized again. Domains of variables are also stored, when present.
     */
    /**
     * Gets an object containing references to all currently uninitialized logical objects (and their domains).
     *
     * @return  An object of type {@code VariablesState} that contains references to all the uninitialized
     * variables (and their domains).
     */
    private @NotNull
    VariablesState getVarState() {
        ArrayList<LObject> notInitLObjectsList = LObject.getNotInitializedLObjectsArrayList();

        ArrayList<LObject> newNotInitLObjectsList = new ArrayList<>(notInitLObjectsList.size());
        ArrayList<MultiInterval> lVarDomState = new ArrayList<>(notInitLObjectsList.size());
        ArrayList<SetInterval> fsVarDomState = new ArrayList<>(notInitLObjectsList.size());

        for(LObject lObject : notInitLObjectsList){
            if(lObject instanceof Ris || lObject.isInitialized())
                continue;

            newNotInitLObjectsList.add(lObject);

            if(lObject instanceof IntLVar && ((IntLVar) lObject).getDomain() != null)
                lVarDomState.add(((IntLVar) lObject).getDomain().clone());
            else
                lVarDomState.add(null);

            if (lObject instanceof SetLVar && ((SetLVar) lObject).getDomain() != null)
                fsVarDomState.add(((SetLVar)lObject).getDomain().clone());
            else
                fsVarDomState.add(null);
        }

        notInitLObjectsList.clear();
        notInitLObjectsList.addAll(newNotInitLObjectsList);

        return new VariablesState(newNotInitLObjectsList, lVarDomState, fsVarDomState);
    }

    /**
     * Creates a constraint store that contains a clone of each non-solved atomic constraint
     * in the store of the solver, with atomic constraints in the same relative order, and returns it.
     *
     * @return the created constraint store.
     */
    private @NotNull ConstraintStore cloneStoreUnSolved(){
        ConstraintStore store = this.solver.store;
        ConstraintStore newConstraintStore = new ConstraintStore(this.solver);

        for(AConstraint aConstraint : store)
            if(!aConstraint.getSolved())
                newConstraintStore.constraintList.add(aConstraint.clone());

        return newConstraintStore;
    }

    /**
     * Constructs and returns a backup of the current Ris Expansion Cache.
     * @return the constructed object.
     */
    private RisExpansionCache getRisExpansionCacheBackup(){
        RisExpansionCache cloned = solver.getRisExpansionCache().clone();
        return cloned;
    }


    ///////////// CHOICE POINTS UPDATING ////////////////////

    /**
     * Updates the stack of open choice points.
     * New atomic constraint are added to the store backup of each
     * choice point that was added to the open choice points stack before the addition of those
     * atomic constraints. This way new atomic constraints are not
     * lost when backtracking to a choice point that precedes in time their addition to the store.
     */
    private void updateAlternativesStores() {
        for(int i = this.solver.storeSize; i < this.solver.store.size(); ++i) {
            AConstraint aConstraint = this.solver.get(i);

            for(ChoicePoint choicePoint : this.openChoicePointsStack) {
                AConstraint aConstraintCloned = aConstraint.clone();
                choicePoint.constraintStore.add(aConstraintCloned);
            }
        }
    }

    /**
     * Updates the stack of open choice points.
     * New uninitialized variables are added to the store backup of each
     * choice point that was added to the open choice points stack before the addition of those
     * uninitialized variables. This way new variables are not
     * lost when backtracking to a choice point that precedes in time their addition to the store.
     */
    private void updateAlternativesVars() {
        ArrayList<LObject> notInitializedLObjects = LObject.getNotInitializedLObjectsArrayList();

        for(int i = 0; i < openChoicePointsStack.size(); ++i) {
            ChoicePoint alternative = openChoicePointsStack.get(i);
            ArrayList<LObject> notInitializedLObjectsBackup = alternative.varsState.notInitializedLObjects;
            for (int j = 0; j < notInitializedLObjects.size(); ++j) {
                LObject lObject = notInitializedLObjects.get(j);

                if (!lObject.isInitialized() && !notInitializedLObjectsBackup.contains(lObject)) {
                    notInitializedLObjectsBackup.add(lObject);

                    if (lObject instanceof IntLVar)
                        alternative.varsState.intLVarDomains.add(((IntLVar) lObject).getDomain().clone());
                    else
                        alternative.varsState.intLVarDomains.add(null);

                    if (lObject instanceof SetLVar)
                        alternative.varsState.setLVarDomains.add(((SetLVar) lObject).getDomain().clone());
                    else
                        alternative.varsState.setLVarDomains.add(null);
                }
            }
        }
    }
}
