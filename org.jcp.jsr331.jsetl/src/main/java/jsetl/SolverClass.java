package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.Fail;
import jsetl.exception.Failure;
import jsetl.exception.NotDefConstraintException;
import jsetl.ris.cache.CacheKey;
import jsetl.ris.cache.RisExpansionCache;

import java.util.*;
import java.util.function.Consumer;

/**
 * Objects of this class are solvers for constraints conjunctions. Constraints can be added to the solver (which stores them).
 * Solvers are able to determine if solutions for the given constraint conjunction exist and are able to find them all.
 * In order to solve non-deterministic constraints solvers use extensively the backtracking method.
 */
public class SolverClass {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Tries to solve che constraint {@code a = b} and leaves the not initialized logical
     * objects array list unchanged in the process. Note that if {@code a} or {@code b} are
     * logical objects they (or their parts) may become bound due to the constraint resolution process.
     * @param a first argument of the equality constraint.
     * @param b second argument of the equality constraint.
     * @return {@code true} if the constraint {@code a = b} is satisfiable, {@code false} otherwise.
     * @author Andrea Fois
     */
    protected static boolean checkUnification(Object a, Object b){
        boolean unificationSucceeded;
        ConstraintClass equalityConstraint = new ConstraintClass(Environment.eqCode, a, b);
        List<LObject> backup = new ArrayList<>();
        backup.addAll(LObject.getNotInitializedLObjectsArrayList());
        unificationSucceeded = equalityConstraint.check();
        LObject.getNotInitializedLObjectsArrayList().clear();
        LObject.getNotInitializedLObjectsArrayList().addAll(backup);

        return unificationSucceeded;
    }

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Set containing cache keys for each current ris expansions.
     * This information is needed to avoid loops.
     */
    private Set<CacheKey> currentExpansions = new HashSet<>();

    /**
     * A reference to the last executing solver.
     */
    private SolverClass lastExecutingSolver;

    /**
     * This object is used to remove unsafe "neq" constraints
     * in solved form.
     */
    private final NeqRemover neqRemover;

    /**
     * {@code true} if no changes in the constraint store
     * occurred; {@code false} otherwise.
     */
    protected boolean storeUnchanged = true;

    /**
     * Size of the constraint store.
     */
    protected int storeSize = 0;

    /**
     * The constraint store.
     */
    protected ConstraintStore store = new ConstraintStore(this);

    /**
     * Level used internally to handle heavily non-deterministic constraints last.
     * Its default value is {@code 3}, which is the highest.
     */
    protected int level = 3;

    /**
     * A backtracking object to be used by this solver.
     */
    protected Backtracking backtracking = new Backtracking(this);

    /**
     * All constraint rewriting handlers (at most 20).
     */
    protected LibConstraintsRules constraintHandlers[] = new LibConstraintsRules[20];

    /**
     * The number of constraint handlers in {@code constraintHandlers}.
     */
    protected int nHandlers = 0;

    /**
     * To handle operations on the domain of logical variables
     */
    protected DomainRulesFD domainRulesFD = new DomainRulesFD(this);
    
    /**
     * To handle operations on the domain of IntLSet.
     */
    protected DomainRulesFS domainRulesFS = new DomainRulesFS(this);
    
    /**
     * Rewrite rules for {@code BoolLVar} constraints.
     */
    protected RwRulesBool rwRulesBool = new RwRulesBool(this);
    
    /**
     * To handle user-defined constraints.
     */
    protected ArrayList<NewConstraints> newConstraints = new ArrayList<NewConstraints>();

    /**
     * Backup of not initialized logical objects list. Used when exiting
     * (and resuming) the constraint solving methods.
     */
    private List<LObject> notInitLObjectsBackup;

    /**
     * Options used to know which optimizations should be used when solving constraints.
     */
    private OptimizationOptions optimizationsOptions = new OptimizationOptions();

    /**
     * A cache for the expansion of {@code Ris}s, particularly useful for {@code Ris} which have other {@code Ris}
     * appear in their filter}. This field is {@code null} if the ris expansion cache is not enabled.
     */
    private RisExpansionCache risExpansionCache = optimizationsOptions.isRisExpansionCacheEnabled()? new RisExpansionCache() : null;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs a new solver.
     */
    public SolverClass() {
        constraintHandlers[nHandlers++] = new RwRulesEq(this);

        constraintHandlers[nHandlers++] = new RwRulesFD(this);

        constraintHandlers[nHandlers++] = new RwRulesSet(this);

        constraintHandlers[nHandlers++] = new RwRulesFS(this);

        constraintHandlers[nHandlers++] = new RwRulesMeta(this);

        constraintHandlers[nHandlers++] = new RwRulesBool(this);

        constraintHandlers[nHandlers++] = new RwRulesBR(this);

        neqRemover = new NeqRemover(this);


    }

    
    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Adds a constraint conjunction at the end of the store.
     * 
     * @param constraint The constraint conjunction to be added to the store.
     */
    public void add(@NotNull ConstraintClass constraint) {
        Objects.requireNonNull(constraint);
        for (int i = 0; i < constraint.size(); i++) {
            this.store.add(constraint.get(i));
        }
    }

    /**
     * Opens a choice point for the first atomic constraint in the constraint conjunction {@code constraint}.
     * @param constraint the constraint that is opening the choice point. It must be an atomic constraint (not a conjunction).
     */
    public void addChoicePoint(@NotNull ConstraintClass constraint) {
        Objects.requireNonNull(constraint);
        assert constraint.size() == 1;
        this.addChoicePoint(constraint.get(0));
    }

    /**
     * Removes all constraints from the ConstraintClass Store
     */
    public void clearStore() {
        storeUnchanged = true;
        storeSize = 0;
        store = new ConstraintStore(this);
        level = 3;
        backtracking = new Backtracking(this);
    }

    /**
     * Returns a constraint conjunction consisting of all the not solved atomic constraints currently in the store.
     * @return the created constraint conjunction.
     */
    public @NotNull
    ConstraintClass getConstraint() {
        ConstraintClass result = this.store.getConstraint();
        assert result != null;
        return result;
    }

    /**
     * This method prints each constraint that is not solved yet to standard output.
     * The output format is "Store: CONSTRAINT" in which CONSTRAINT is the result of
     * calling {@code toString} on the returned constraint of the method {@code getConstraint()}.
     */
    public void showStore() {
        System.out.print("Store: ");    
        System.out.println(this.getConstraint());
    }

    /**
     * This method prints each constraint still in the constraint store (solved or not) to standard output.
     * Note that solved constraints are not guaranteed to remain in the store.
     */
    public void showStoreAll() {
        System.out.print("Store All: ");
        System.out.println(this.store.getConstraintAll());
    }

    /**
     * Like {@code showStoreAll()} but prints the internal view of the constraint store to standard output.
     * Used for debugging purposes.
     */
    public void showStoreInternals() {
        System.out.print("Store Internals: ");
        System.out.println(this.store.getConstraintAll().toStringInternals());
    }


    /**
     * This method returns the number of atomic constraints in the store that are not yet solved.
     * @return the number of atomic constraints in the store that are not yet solved.
     */
    public int size() {
        return (int) store.constraintList.stream().filter(aConstraint -> !aConstraint.getSolved()).count();
    }

    /**
     * Solves the conjunction of constraints in the current constraint store.
     * @return {@code true} if the conjunction is satisfiable, {@code false} otherwise.
     */
    public boolean check() {
        try {
            this.solve();           
            return true;
        } catch (Failure f) {
            return false;
        }
    }
 
    /**
     * Solves the passed constraint {@code constraint} in the context of the current
     * constraint store and returns a boolean result. If {@code constraint} is unsatisfiable,
     * {@code constraint} is removed from the constraint store.
     * @param constraint the constraint conjunction to solve.
     * @return {@code true} {@code constraint} is satisfiable in the context of the current store, {@code false} otherwise.
    */ 
    public boolean check(@NotNull ConstraintClass constraint) {
        Objects.requireNonNull(constraint);
        ChoicePoint backupChoicePoint = this.backtracking.createBackupChoicePoint();
        try {
            this.add(constraint);
            this.solve();           
            return true;
        } catch (Failure f) {
            this.backtracking.restoreFromChoicePoint(backupChoicePoint);
            return false;
        }
    }

    /**
     * Method used to signal that the constraint resolution mechanism has failed and no solutions could be found.
     * @throws Failure always.
     */
    public void failure() throws Failure {
        throw new Failure();
    }

    /**
     * Runs the given runnable for each solution, counts the number of solutions and returns it.
     * @param consumer a consumer that gets the index of the solution and performs an action.
     * @return the number of solutions.
     */
    public int forEachSolution(@NotNull Consumer<Integer> consumer){
        Objects.requireNonNull(consumer);
        int numberOfSolutions = 0;
        if(this.check()) {
            do {
                ++numberOfSolutions;
                consumer.accept(numberOfSolutions);
            } while (this.nextSolution());
        }
        return numberOfSolutions;
    }

    /**
     * Solves (using backtracking) the constraints that are not in solved form.
     * Note: A {@code Fail} exception can be raised if constraints are inconsistent,
     * this exception is caught inside the method and starts the backtracking
     * procedure.
     * 
     * @throws Failure If the constraint in the store is unsatisfiable.
     */
    public void solve() throws Failure {
            solve(this.level);
    }

    /**
     * Adds the given constraint to the constraint store and solves it using solve()
     * @param constraint constraint conjunction to add to the constraint store.
     * @throws Failure if the constraint store and {@code constraint} together are unsatisfiable.
     */
    public void solve(@NotNull ConstraintClass constraint) throws Failure {
        Objects.requireNonNull(constraint);
        this.add(constraint);
        this.solve();
    }

    /**
     * Tests whether the constraint in the store is satisfiable but leaves each variable
     * that occur in the store unchanged.
     * @return {@code true} if the constraint is satisfiable, otherwise it returns {@code false}.
     */
    public boolean test() {
        findNotInitializedLogicalObjectsInStore();
        ChoicePoint backupChoicePoint = this.backtracking.createBackupChoicePoint();
        ConstraintClass cCopy = this.getConstraint().clone();
        SolverClass localSolver = new SolverClass();
        localSolver.add(cCopy);
        boolean solutionFound;
        try {
            localSolver.solve();
            this.backtracking.restoreFromChoicePoint(backupChoicePoint);
            solutionFound = true;
        } catch (Failure f) {
            this.backtracking.restoreFromChoicePoint(backupChoicePoint);
            solutionFound =  false;
        }
        LObject.getNotInitializedLObjectsArrayList().clear();
        return solutionFound;

    }

    /**
     * Minimizes the given integer logical variable and returns its minimum
     * (in the context of the current constraint store).
     * @param intLVar variable to minimize.
     * @return the minimum value of {@code intLVar}.
     */
    public @NotNull Integer minimize(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        findNotInitializedLogicalObjectsInStore();
        ChoicePoint backupChoicePoint = this.backtracking.createBackupChoicePoint();
        Integer min = java.lang.Integer.MAX_VALUE;
        do {
            ConstraintClass cCopy = this.getConstraint().clone();
            SolverClass localSolver = new SolverClass();
            localSolver.add(cCopy.and(intLVar.lt(min)));
            boolean b = localSolver.check(cCopy);
            if (!b) break;
            if (intLVar.getValue() < min)
                min = intLVar.getValue();
            localSolver.backtracking.restoreFromChoicePoint(backupChoicePoint);
        } while(true);
        LObject.getNotInitializedLObjectsArrayList().clear();
        assert min != null;
        return min;
    }

    /**
     * Maximises the given integer logical variable and returns its maximum
     * (in the context of the current constraint store).
     * @param intLVar variable to minimize.
     * @return the maximum value of {@code intLVar}.
     */
    public @NotNull Integer maximize(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        Integer max = java.lang.Integer.MIN_VALUE/2 + 1;
        findNotInitializedLogicalObjectsInStore();
        ChoicePoint backupChoicePoint = this.backtracking.createBackupChoicePoint();

        do {
            ConstraintClass cCopy = this.getConstraint().clone();
            SolverClass localSolver = new SolverClass();
            localSolver.add(cCopy.and(intLVar.gt(max)));
            boolean b = localSolver.check(cCopy);
            if (!b) break;
            if (intLVar.getValue() > max)
                max = intLVar.getValue();
            localSolver.backtracking.restoreFromChoicePoint(backupChoicePoint);
        } while(true);
        LObject.getNotInitializedLObjectsArrayList().clear();

        assert max != null;
        return max;
    }

    /**
     * This method tries to find a
     * new solution (if any) each time it is called.
     * This method should only be called after a successful call to either
     * {@code check} or {@code solve}.
     * @return {@code true} if a next solution exists, {@code false} otherwise.
     * @see SolverClass#solve
     */
    public boolean nextSolution() {
        if (backtracking.getNumberOfAlternatives() > 0) {
            findNotInitializedLogicalObjectsInStore();
            boolean result;
            try {
                backtracking.backtrack();
                result = solveSetof();
                LObject.getNotInitializedLObjectsArrayList().clear();
                return result;
            } catch(Throwable throwable){
                LObject.getNotInitializedLObjectsArrayList().clear();
                throw throwable;
            }

        } else
            return false;
    }

    /**
     * Finds all possible values (by calling {@code x.getValue)} for a logical object that satisfy the constraint
     * in current constraint store of this solver.
     *
     * @param x The logical object of which we search the values.
     * @return The set of all possibles values for the logical object (obtained by calling {@code x.getValue()}.
     * @see SolverClass#solve
     */
    public @NotNull LSet setof(@NotNull LObject x) {
        Objects.requireNonNull(x);
        ChoicePoint backupChoicePoint = this.backtracking.createBackupChoicePoint();
        findNotInitializedLogicalObjectsInStore();
        try {
            HashSet<Object> values = new HashSet<>();
            this.forEachSolution((i) -> values.add(x.getValue()));
            backtracking.restoreFromChoicePoint(backupChoicePoint);
            return new LSet(values);
        } catch(Throwable throwable){
            backtracking.restoreFromChoicePoint(backupChoicePoint);
            LObject.getNotInitializedLObjectsArrayList().clear();
            throw throwable;
        }

    }

    /**
     * Computes and returns the (logical) set of all values (obtained calling {@code x.getValue()} of {@code x} that satisfy {@code constraint}
     * and the constraints in the constraint store.
     * @param x logical variable the values of which are collected.
     * @param constraint constraint that must be satisfied by the values of {@code x}.
     * @return the set of all values of {@code x}.
     */
    public LSet setof(@NotNull LObject x, @NotNull ConstraintClass constraint) {
        Objects.requireNonNull(x);
        Objects.requireNonNull(constraint);
        this.add(constraint);
        LSet result = setof(x);
        assert result != null;
        return result;
    }

    /**
     * Returns the instance of {@code OptimizationOptions} used by the solver.
     * @return the optimizations options used by this solver.
     */
    public @NotNull OptimizationOptions getOptimizationOptions(){
        assert optimizationsOptions != null;
        return optimizationsOptions;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Solves the constraint conjunction in the constraint store starting from the given level of nondeterminism.
     * @param lev level of nondeterminism.
     * @throws Failure if no solutions can be found.
     */
    protected void solve(int lev) throws Failure {
        List<LObject> notInitializedLObjectsList = LObject.getNotInitializedLObjectsArrayList();
        ArrayList<LObject> backup = new ArrayList<>(notInitializedLObjectsList);
        notInitializedLObjectsList.clear();

        findNotInitializedLogicalObjectsInStore();
        lastExecutingSolver = LObject.getNotInitializedLObjectsRecord().currentExecutingSolver;
        LObject.setCurrentExecutingSolver(this);

        try {
            solveLevel(lev);
            resetCurrentExecutingSolver();
            LObject.setNotInitializedLObjectsList(new ArrayList<>());
            if(lastExecutingSolver != null)
                LObject.setNotInitializedLObjectsList(backup);
        } catch (Throwable throwable) {
            resetCurrentExecutingSolver();
            LObject.setNotInitializedLObjectsList(new ArrayList<>());
            if(lastExecutingSolver != null)
                LObject.setNotInitializedLObjectsList(backup);
            throw throwable;
        }
    }

    /**
     * Adds the given constraint to the constraint store and solves it with the given nondeterminism level
     * @param cc constraint conjunction to add to the constraint store
     * @param lev nondeterminism level
     */
    protected void solve(ConstraintClass cc, int lev)
    throws Failure, NotDefConstraintException {
        this.add(cc);
        this.solve(lev);
    }

    /**
     * Resets the current executing object for {@code LObject} to the
     * solver referenced by {@code lastExecutingSolver}.
     * @author Andrea Fois
     */
    protected void resetCurrentExecutingSolver() {
        LObject.setCurrentExecutingSolver(lastExecutingSolver);
    }

    /**
     * Solves the constraint conjunction in the constraint store starting from the given level of nondeterminism.
     * @param lev level of nondeterminism.
     * @throws Failure if no solutions can be found.
     */
    protected void solveLevel(int lev) throws Failure {

        if (backtracking.getNumberOfAlternatives() > 0) {
            backtracking.updateAlternatives();
        }

        int currLevel = this.level;
        this.level = 1;
        do{
            do {
                do {
                    try {
                        this.risNoLabel();

                    } catch (Fail f) {
                        if(!backtracking.backtrack())
                            throw new Failure();
                    }

                    if(store.size() > 1000)
                        this.store.trim();

                } while (!this.storeUnchanged);    // exit when storeUnchanged = true

                try {
                    this.risLabel();
                } catch (Fail f) {
                    if(!backtracking.backtrack())
                        throw new Failure();
                }




            } while (!this.storeUnchanged);
            if (lev > 1) {
                this.level++;
                do {
                    storeUnchanged = true;
                    try {
                        //this.ris();   // it does nothing at level 2 in current version
                    } catch (Fail f) {
                        if(!backtracking.backtrack())
                            throw new Failure();
                    }
                } while (!this.storeUnchanged);
            }
            if (lev > 2) {
                this.level++;
                do {
                    storeUnchanged = true;
                    try {
                        this.ris();
                    } catch (Fail f) {
                        if(!backtracking.backtrack())
                            throw new Failure();
                    }
                } while (!this.storeUnchanged);
            }
            neqRemover.removeNeq();
        }while(!this.storeUnchanged);

        this.level = currLevel;
        this.storeSize = this.store.size();
    }

    /**
     * Solves each and only label constraint in the constraint store.
     * @throws Fail if an inconsistency is found but alternatives are present for backtracking.
     */
    protected void risLabel()
            throws  Fail {
        int i = 0;
        while (i < store.size()) {
            AConstraint tmp = this.get(i);
            if (tmp.constraintKindCode == Environment.labelCode)
                risAConstraint(tmp);
            ++i;
        }
    }

    /**
     * Solves each constraint in the constraint store which is not a label.
     * @throws Fail if an inconsistency is found but alternatives are present for backtracking.
     */
    protected void risNoLabel()
            throws Fail {
        this.storeUnchanged = true;
        int i = 0;
        while (i < store.size()) {
            AConstraint tmp = this.get(i);
            if (tmp.constraintKindCode != Environment.labelCode)
                risAConstraint(tmp);

            ++i;
        }
    }

    /**
     * Solves each constraint in the constraint store.
     * @throws Fail if an inconsistency is found but alternatives are present for backtracking.
     */
    protected void ris()
            throws Fail {
        this.storeUnchanged = true;
        int i = 0;
        while (i < store.size()) {
            AConstraint s = this.get(i);
            risAConstraint(s);
            ++i;
        }
    }

    /**
     * Solves the given atomic constraint.
     * @param aConstraint atomic constraint to solve.
     * @throws Fail if an inconsistency is found but alternatives are present for backtracking.
     */
    protected void risAConstraint(@NotNull AConstraint aConstraint)
            throws Fail {
        assert aConstraint != null;
        if (!aConstraint.getSolved()) {
            boolean rewritten = false;
            if (aConstraint.constraintKindCode < Environment.FIRST_NEW_CONSTRAINTS_CODE) {
                for (int i=0; i<nHandlers && !rewritten; i++)
                    rewritten = constraintHandlers[i].solveConstraint(aConstraint);
                if (!rewritten)
                    throw new NotDefConstraintException();
            } else if(level >=3){ // user-defined constraint
                if (!newConstraints.isEmpty()) {
                    aConstraint.setSolved(true);
                    Iterator<NewConstraints> itr = newConstraints.iterator();
                    NewConstraints CN;
                    while(itr.hasNext()) {
                        CN = itr.next();
                        try {
                            CN.user_code(new ConstraintClass(aConstraint));
                            rewritten = true;
                            break;
                        }
                        catch (NotDefConstraintException ndce) {
                            rewritten = false;
                        }
                    }
                }
                if (!rewritten)
                    throw new NotDefConstraintException();
            } // end user-defined constraint
        }
    }

    /**
     * Method called internally when solving the constraint store to find the
     * set of all possible values of a given logical variable that satisfy the constraint conjunction in the constraint store.
     * This method finds a single solution.
     * @return {@code true} if a solution was found, {@code false} otherwise
     */
    protected boolean solveSetof() {
        if (backtracking.getNumberOfAlternatives() > 0) {
            backtracking.updateAlternatives();
        }
        do {
            try {
                this.ris();
            } catch (Fail f) {
                if (backtracking.getNumberOfAlternatives() > 0)
                    backtracking.backtrack();
                else 
                    return false;
            }
        } while (!this.storeUnchanged);

        this.storeSize = this.store.size();
        return true;
    }
   

    /**
     * Add the constraint at the {@code index}-th position in the store.
     * 
     * @param index position in the constraint store.
     * @param aConstraint constraint to added to the store.
     */
    protected void add(int index, @NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        this.store.add(index, aConstraint);
    }

    /**
     * Add the constraint {@code aConstraint} at the end of the store.
     * @param aConstraint atomic constraint to add.
     */
    protected void add(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        this.store.add(aConstraint);
    }

    /**
     * Creates a new choice point for the atomic constraint {@code aConstraint}.
     * @param aConstraint atomic constraint that is opening the choice point.
     */
    protected void addChoicePoint(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        boolean solved = aConstraint.getSolved();
        aConstraint.setSolved(false);
        backtracking.addChoicePoint(aConstraint);
        aConstraint.setSolved(solved);
    }

    /**
     * Checks whether a solution to the constraints in the constraint store and the parameter exists or not.
     * The variables in the constraint store may become bound due to the constraint resolution process.
     * @param aConstraint atomic constraint to add to the constraint store before searching for a solution.
     * @return {@code true} if a solution was found, {@code false} otherwise.
     */
    protected boolean check(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        ChoicePoint backupChoicePoint = this.backtracking.createBackupChoicePoint();
        try {
            this.add(aConstraint);
            this.solve();           
            return true;
        } catch (Failure f) {
            this.backtracking.restoreFromChoicePoint(backupChoicePoint);
            return false;
        }
    }

    /**
     * Gets the {@code i}-th atomic constraint in the constraint store.
     * @param i the index of the atomic constraint to retrieve.
     * @return the {@code i}-th atomic constraint in the constraint store.
     */
    protected @NotNull AConstraint get(int i) {
        AConstraint result = this.store.get(i);
        assert result != null;
        return result;
    }

    /**
     * Raises an exception {@ode Fail}. This exception is handled by the {@code SolverClass} which
     * will backtrack if there are open choice points or throw an exception {@code Failure} otherwise.
     * @param aConstraint the atomic constraint that failed.
     * @throws Fail always.
     */
    protected void fail(@NotNull AConstraint aConstraint) throws Fail {
        assert aConstraint != null;
        aConstraint.setSolved(true);
        throw new Fail();
    }

    /**
     * Searches for the first occurrence of the given argument in the constraint store.
     * 
     * @param aConstraint the constraint to be searched.
     * @return the index of the first occurrence of the constraint searched.
     *         Returns {@code -1} if the constraint is not found.
     */
    protected int indexOf(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        return this.store.indexOf(aConstraint);
    }

    /**
     * Adds the constraint {@code aConstraint} to the store and then solves the
     * constraint conjunction in the store using the method {@code solve()}.
     * @param aConstraint constraint to add and solve.
     * @throws Failure if {@code aConstraint} and the constraint in the store are unsatisfiable.
     * @throws NotDefConstraintException if some of the constraints in the store or {@code aConstraint}
     * have an undefined constraint kind.
     */
    protected void solve(@NotNull AConstraint aConstraint)
    throws Failure, NotDefConstraintException {
        assert aConstraint != null;
        this.add(aConstraint);
        this.solve();
    }

    /**
     * Adds an user-defined constraint to the list of known user-defined constraints.
     * @param newConstraints user-defined constraints.
     */
    protected void setNewConstraints(@NotNull NewConstraints newConstraints) {
        assert newConstraints != null;
        this.newConstraints.add(newConstraints);
    }

    /**
     * Returns a reference to the RIS expansion cache.
     * @return a reference to the RIS expansion cache.
     * @author Andrea Fois
     */
    protected @Nullable RisExpansionCache getRisExpansionCache(){
        return risExpansionCache;
    }

    /**
     * Sets the RIS expansion cache to {@code risExpansionCache}.
     * @param risExpansionCache the new RIS expansion cache.
     * @author Andrea Fois
     */
    protected void setRisExpansionCache(@Nullable RisExpansionCache risExpansionCache){
        this.risExpansionCache = risExpansionCache;
    }

    /**
     * This method adds each logical variable found in the constraint store to the not initialized logical objects
     * list.
     * @author Andrea Fois
     */
    protected void findNotInitializedLogicalObjectsInStore(){
        LObject.getNotInitializedLObjectsArrayList().addAll(new VariablesGetter().getVariables(this.getConstraint()));
    }

    /**
     * Sets the current expansion field to the parameter {@code currentExpansions}.
     * @param currentExpansions the new value for the {@code currentExpansions} field.
     * @author Andrea Fois
     */
    protected void setCurrentExpansions(@NotNull Set<CacheKey> currentExpansions){
        assert currentExpansions != null;
        assert currentExpansions.stream().noneMatch(Objects::isNull);

        this.currentExpansions = currentExpansions;
    }

    /**
     * Returns a reference to the {@code currentExpansion} field.
     * @return a reference.
     * @author Andrea Fois
     */
    protected @NotNull Set<CacheKey> getCurrentExpansions(){
        assert currentExpansions != null;
        assert currentExpansions.stream().noneMatch(Objects::isNull);
        return currentExpansions;
    }


    ////////////////////////////////////////////////////////////////////////////////
    ////////// INNER CLASSES FOR OPTIMISATIONS /////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Class used to store and modify info about which optimizations are enabled.
     * @author Andrea Fois
     */
    public class OptimizationOptions {

        /////////////////////////////////////////////////////
        /////////// DATA MEMBERS ////////////////////////////
        /////////////////////////////////////////////////////

        /**
         * This value determines whether the RIS expansion optimisation should be used or not.
         * Its value is the result of {@code defaultUseRisExpansionOptimizationFlag()}.
         * @see OptimizationOptions#defaultUseRisExpansionOptimizationFlag()
         */
        private boolean useRisExpansionOptimization = defaultUseRisExpansionOptimizationFlag();

        /**
         * This value determines whether the fast "union" rules should be used or not.
         * Its value is the result of {@code defaultUseFastUnionRulesFlag()}.
         * @see OptimizationOptions#defaultUseFastUnionRulesFlag()
         */
        private boolean useFastUnionRules = defaultUseFastUnionRulesFlag();

        /**
         * This value determines whether the fast "comp" rules should be used or not.
         * Its value is the result of {@code defaultUseFastCompRulesFlag()}.
         * @see OptimizationOptions#defaultUseFastCompRulesFlag()
         */
        private boolean useFastCompRules = defaultUseFastCompRulesFlag();

        /**
         * This value determines whether the "in" optimizations should be used or not.
         * Its value is the result of {@code defaultUseInOptimizationsFlag()}.
         * @see OptimizationOptions#defaultUseInOptimizationsFlag()
         */
        private boolean useInOptimizations = defaultUseInOptimizationsFlag();

        /**
         * This value determines whether the "nin" optimizations should be used or not.
         * Its value is the result of {@code defaultUseNinOptimizationsFlag()}.
         * @see OptimizationOptions#defaultUseNinOptimizationsFlag()
         */
        private boolean useNinOptimizations = defaultUseNinOptimizationsFlag();

        /**
         * This value determines whether the set "eq" optimizations should be used or not.
         * Its value is the result of {@code defaultUseSetUnificationOptimizationsFlag()}.
         * @see OptimizationOptions#defaultUseSetUnificationOptimizationsFlag()
         */
        private boolean useSetUnificationOptimizations = defaultUseSetUnificationOptimizationsFlag();

        /**
         * This value determines whether the RIS expansion cache should be used or not.
         * Its value is the result of {@code defaultUseRisExpansionCache()}.
         * @see OptimizationOptions#defaultUseRisExpansionCache()
         */
        private boolean useRisExpansionCache = defaultUseRisExpansionCache();

        /**
         * Method used to retrieve the default value for the useRisExpansionOptimization flag
         * @return the default value for the flag.
         */
        private boolean defaultUseRisExpansionOptimizationFlag() {
            String environmentDefault = System.getenv("jsetlUseRisExpansionOptimization");
            if (environmentDefault == null)
                return true;
            else if (environmentDefault.equalsIgnoreCase("true"))
                return true;
            else
                return false;
        }


        /////////////////////////////////////////////////////
        /////////// PUBLIC METHODS //////////////////////////
        /////////////////////////////////////////////////////

        /**
         * Returns {@code true} if the {@code Ris} expansion is enabled when solving constraints involving {@code Ris}.
         * Defaults to {@code true}.
         * @return {@code true} if Ris expansion is enabled, {@code false} otherwise.
         */
        public boolean isRisExpansionOptimizationEnabled(){
            return useRisExpansionOptimization;
        }

        /**
         * Returns {@code true} if fast rewrite rules for union constraints are enabled.
         * Note that fast union rules DO NOT WORK with {@code Ris} and {@code CP}.
         * Defaults to {@code false}.
         * @return {@code true} if fast rewrite rules for unions are enabled, {@code false} otherwise.
         */
        public boolean areFastUnionRulesEnabled(){
            return useFastUnionRules;
        }

        /**
         * Returns {@code true} if fast rewrite rules for comp constraints are enabled.
         * Defaults to {@code true}.
         * @return {@code true} if fast union rules for comps are enabled, {@code false} otherwise.
         */
        public boolean areFastCompRulesEnabled(){
            return useFastCompRules;
        }

        /**
         * Returns {@code true} if optimizations for "in" constraints are enabled.
         * Defaults to {@code true}.
         * @return {@code true} if optimizations for "in" constraints are enabled, {@code false} otherwise.
         */
        public boolean areInOptimizationsEnabled(){
            return useInOptimizations;
        }

        /**
         * Returns {@code true} if optimizations for "nin" constraints are enabled.
         * Defaults to {@code true}.
         * @return {@code true} if optimizations for "nin" constraints are enabled, {@code false} otherwise.
         */
        public boolean areNinOptimizationsEnabled(){
            return useNinOptimizations;
        }

        /**
         * Returns {@code true} if optimizations for set unification constraints are enabled.
         * Defaults to {@code true}.
         * @return {@code true} if optimizations for set unification constraints are enabled, {@code false} otherwise.
         */
        public boolean areSetUnificationOptimizationsEnabled(){
            return useSetUnificationOptimizations;
        }

        /**
         * Returns {@code true} if the {@code Ris} expansion cache is enabled when solving constraints involving {@code Ris}.
         * Defaults to {@code true}.
         * @return {@code true} if Ris expansion is enabled, {@code false} otherwise.
         */
        public boolean isRisExpansionCacheEnabled(){
            return useRisExpansionCache && useRisExpansionOptimization;
        }

        /**
         * Enables ris expansion optimization if {@code flag == true}, disables it otherwise.
         * @param flag a boolean flag to enable/disable the optimization.
         */
        public void setUseRisExpansionOptimizationFlag(boolean flag){
            useRisExpansionOptimization = flag;
        }

        /**
         * Enables fast union rules if {@code flag == true}, disables them otherwise.
         * NOTE that fast union rules DOES NOT WORK with {@code Ris} or {@code CP}.
         * @param flag a boolean flag to enable/disable the optimization.
         */
        public void setUseFastUnionRulesFlag(boolean flag){
            useFastUnionRules = flag;
        }

        /**
         * Enables fast comp rules if {@code flag == true}, disables them otherwise.
         * @param flag a boolean flag to enable/disable the optimization.
         */
        public void setUseFastCompRulesFlag(boolean flag){
            useFastCompRules = flag;
        }

        /**
         * Enables "in" optimizations if {@code flag == true}, disables them otherwise.
         * @param flag a boolean flag to enable/disable the optimization.
         */
        public void setUseInOptimizationsFlag(boolean flag){
            useInOptimizations = flag;
        }

        /**
         * Enables "nin" optimizations if {@code flag == true}, disables them otherwise.
         * @param flag a boolean flag to enable/disable the optimization.
         */
        public void setUseNinOptimizationsFlag(boolean flag){
            useNinOptimizations = flag;
        }

        /**
         * Enables s unification optimizations if {@code flag == true}, disables them otherwise.
         * @param flag a boolean flag to enable/disable the optimization.
         */
        public void setUseSetUnificationOptimizationsFlag(boolean flag){
            useSetUnificationOptimizations = flag;
        }

        /**
         * Enables ris expansion optimization if {@code flag == true} and enables the cache,
         * disables the cache otherwise.
         * @param flag a boolean flag to enable/disable the optimization.
         */
        public void setUseRisExpansionCacheFlag(boolean flag){
            useRisExpansionCache = flag;
            if(flag)
                useRisExpansionOptimization = true;
            if(risExpansionCache == null)
                risExpansionCache = new RisExpansionCache();
        }

        /**
         * If the ris expansion cache is enabled sets the cache size to {@code maxEntries}.
         * @param maxEntries the maximum number of entries in the cache.
         */
        public void setRisExpansionCacheSize(int maxEntries){
            if(isRisExpansionCacheEnabled())
                getRisExpansionCache().setMaxSize(maxEntries);
        }


        /////////////////////////////////////////////////////
        /////////// PRIVATE METHODS /////////////////////////
        /////////////////////////////////////////////////////

        /**
         * Method used to retrieve the default value for the {@code useFastUnionRules} flag.
         * Defaults to {@code false}.
         * @return the default value for the flag.
         */
        private boolean defaultUseFastUnionRulesFlag(){
            String environmentDefault = System.getenv("jsetlUseFastUnionRules");
            if (environmentDefault == null)
                return false;
            else if (environmentDefault.equalsIgnoreCase("true"))
                return true;
            else
                return false;
        }

        /**
         * Method used to retrieve the default value for the {@code useFastCompRules} flag.
         * Defaults to {@code true}.
         * @return the default value for the flag.
         */
        private boolean defaultUseFastCompRulesFlag(){
            String environmentDefault = System.getenv("jsetlUseFastCompRules");
            if (environmentDefault == null)
                return true;
            else if (environmentDefault.equalsIgnoreCase("true"))
                return true;
            else
                return false;
        }

        /**
         * Method used to retrieve the default value for the {@code useInOptimizations} flag.
         * Defaults to {@code true}.
         * @return the default value for the flag.
         */
        private boolean defaultUseInOptimizationsFlag(){
            String environmentDefault = System.getenv("jsetlUseInOptimizations");
            if (environmentDefault == null)
                return true;
            else if (environmentDefault.equalsIgnoreCase("true"))
                return true;
            else
                return false;
        }

        /**
         * Method used to retrieve the default value for the {@code useNinOptimizations} flag.
         * Defaults to {@code true}.
         * @return the default value for the flag.
         */
        private boolean defaultUseNinOptimizationsFlag(){
            String environmentDefault = System.getenv("jsetlUseNinOptimizations");
            if (environmentDefault == null)
                return true;
            else if (environmentDefault.equalsIgnoreCase("true"))
                return true;
            else
                return false;
        }

        /**
         * Method used to retrieve the default value for the {@code useSetUnificationOptimizations} flag.
         * Defaults to {@code true}.
         * @return the default value for the flag.
         */
        private boolean defaultUseSetUnificationOptimizationsFlag(){
            String environmentDefault = System.getenv("jsetlUseSetUnificationOptimizations");
            if (environmentDefault == null)
                return true;
            else if (environmentDefault.equalsIgnoreCase("true"))
                return true;
            else
                return false;
        }

        /**
         * Method used to retrieve the default value for the {@code useRisExpansionCache} flag.
         * Defaults to {@code true}.
         * @return the default value for the flag.
         */
        private boolean defaultUseRisExpansionCache() {
            if(!useRisExpansionOptimization)
                return false;

            String environmentDefault = System.getenv("jsetlUseRisExpansionCache");
            if (environmentDefault == null)
                return true;
            else if (environmentDefault.equalsIgnoreCase("true"))
                return true;
            else
                return true;
        }
    }


}
