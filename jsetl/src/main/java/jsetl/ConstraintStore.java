package jsetl;

import jsetl.annotation.NotNull;

import java.util.*;

/**
 * Objects of this class are stores of atomic constraints. Addition, removal, size and other operations are available for the objects of this class.
 */
class ConstraintStore implements Iterable<AConstraint>{

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
      * List of constraints.
     */ 
    protected  ArrayList<AConstraint> constraintList;
    
    /**
      * Reference to the solver.
     */ 
    private Solver solver;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates a new instance of {@code ConstraintStore} with the given solver reference.
     *
     * @param solver a solver reference.
     */    
    protected ConstraintStore(@NotNull Solver solver) {
        assert solver != null;
        this.constraintList = new ArrayList<>();
        this.solver = solver;
    }
  
    /**
     * Creates a copy of the passed constraint store. Each atomic constraint is cloned.
     *
     * @param constraintStore a constraint store instance.
     */      
    protected ConstraintStore(@NotNull ConstraintStore constraintStore) {
        assert constraintStore != null;

        this.constraintList  = new ArrayList<>();
        for(AConstraint aConstraint : this)
            constraintList.add(aConstraint.clone());
        this.solver = constraintStore.solver;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Provides implementation of the necessary method for interface {@code Iterable} of {@code AConstraint}.
     * @return an iterator over the atomic constraints in the store.
     */
    @Override
    public @NotNull Iterator<AConstraint> iterator() {
        Iterator<AConstraint> iterator = this.constraintList.iterator();
        assert iterator != null;
        return iterator;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Adds a constraint at the end of the constraints store.
     *
     * @param aConstraint a constraint.
     */
    protected void add(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        this.constraintList.add(aConstraint);
    }

    /**
     * Adds a constraint to the constraints store right before the constraint at position {@code index}.
     *
     * @param index  the integer index where insert the constraint. It must be a valid index.
     * @param aConstraint a constraint.
     */
    protected void add(int index, @NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        this.constraintList.add(index,aConstraint);
    }

    /**
     * Gets the index on the constraints store for the given constraint.
     *
     * @param aConstraint a constraint.
     * @return the index of the first occurrence of the object argument in the 
     *         constraints store, returns {@code -1} if the object is not found.
     */
    protected int indexOf(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;

        for(int i = 0; i < size(); ++i)
            if(constraintList.get(i) == aConstraint)
                return i;
        return -1;
    }
   
    /**
     * Returns the number of atomic constraints in the store.
     *
     * @return the number of atomic constraints in the store.
     */
    protected int size() {
        return this.constraintList.size();
    }

    /**
     * Returns the constraint with index {@code index} (starting from {@code 0}) in the constraints store.
     * @param index a valid index.
     * @return the constraint at index index.
     */    
    protected AConstraint get(int index) {
        return this.constraintList.get(index);
    }

    /**
     * Returns a new constraint conjunction, namely an object of class {@code Constraint}, containing all
     * the atomic constraints in the store which are not solved.
     *
     * @return a constraint conjunction representing the conjunction of all not-solved atomic constraints in the store.
     */
    protected @NotNull Constraint getConstraint() {
        Constraint constraint = new Constraint();

        for(AConstraint aConstraint : this)
            if(!aConstraint.getSolved())
                constraint.add(aConstraint);

        assert constraint != null;
        return constraint;
    }

    /**
     * Returns a new constraint conjunction, namely an object of class {@code Constraint}, containing all
     * the atomic constraints in the store.
     *
     * @return a constraint conjunction representing the conjunction of all atomic constraints in the store.
     */
    protected @NotNull Constraint getConstraintAll() {
        Constraint constraint = new Constraint();
        for(AConstraint aConstraint : this.constraintList)
            constraint.add(aConstraint);
        assert constraint != null;
        return constraint;
    }

    /**
     * Checks whether the store is empty or not.
     *
     * @return {@code true} if the store is empty or if each atomic constraint in the store is solved, {@code false} otherwise.
     */
    protected boolean isEmpty() {
        for(AConstraint aConstraint : this)
            if(!aConstraint.getSolved())
                return false;
        return true;
    }

    /**
     * Clones the constraint store. The solver is copied by reference, the atomic constraints
     * in the store are cloned.
     * 
     * @return a clone of the constraints store.
     */
    @Override
    protected @NotNull ConstraintStore clone() {
        ConstraintStore newStore = new ConstraintStore(this.solver);

        for(AConstraint aConstraint : this)
            newStore.add(aConstraint.clone());
        
        assert newStore != null;
        return newStore;
    }
    
    /**
     * Rewrites the atomic constraint {@code aConstraint} into a new constraint conjunction {@code constraint}.
     * The store is not considered "unchanged" by the solver after this operation.
     * @param aConstraint the atomic constraint to rewrite.
     * @param constraint the constraint conjunction that replaces the old atomic constraint {@code aConstraint}.
     */
    protected void rewrite(@NotNull AConstraint aConstraint, @NotNull Constraint constraint) {
        assert aConstraint != null;
        assert constraint != null;
        
        remove(aConstraint);

        Iterator<AConstraint> iterator = constraint.iterator();
        while(iterator.hasNext())
            add(iterator.next());

        solver.storeUnchanged = false;
    }

    /**
     * Rewrites the atomic constraint {@code oldAConstraint} into a new atomic constraint {@code newAConstraint}.
     * It does so by setting {@code oldAConstraint} to be equal to {@code newAConstraint}.
     * @param oldAConstraint the atomic constraint to rewrite.
     * @param newAConstraint the new atomic constraint that replaces the old atomic constraint {@code oldAConstraint}.
     */
    protected void rewrite(@NotNull AConstraint oldAConstraint, @NotNull AConstraint newAConstraint) {
        assert oldAConstraint != null;
        assert newAConstraint != null;

        oldAConstraint.setEqualTo(newAConstraint);
    }

    /**
     * Removes the atomic constraint {@code aConstraint} from the store by setting its solved flag to {@code true}.
     * @param aConstraint the atomic constraint to remove from the store.
     */
    protected void remove(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        aConstraint.setSolved(true); //sets aConstraint as solved, this way it will not be taken into consideration
    }

    /**
     * Removes each solved atomic constraint from the store.
     */
    protected void trim(){
        ArrayList<AConstraint> unsolvedAConstraints = new ArrayList<>(this.constraintList.size());
        for(AConstraint aConstraint : this.constraintList)
            if(!aConstraint.getSolved())
                unsolvedAConstraints.add(aConstraint);
        this.constraintList = unsolvedAConstraints;
    }

}
