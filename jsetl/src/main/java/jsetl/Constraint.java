package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.Fail;
import jsetl.exception.Failure;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Objects of the class {@code Constraints} are conjunctions of atomic constraints (class {@code AConstraint}).
 * @see AConstraint
 */
public class Constraint  
implements Visitable {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC MEMBERS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * The maximum number of arguments that can be passed to the constructor of a constraint (excluding name and code).
     * This number is the effective maximum number of arguments of the constraint that is being created.
     * This number is currently equal to {@code AConstraint.MAX_ACONSTRAINT_ARGUMENTS (4)}.
     * @see AConstraint#MAX_ACONSTRAINT_ARGUMENTS
     */
    public static final int MAX_ARGUMENTS_PER_CONSTRAINT = AConstraint.MAX_ACONSTRAINT_ARGUMENTS;


    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns a constraint that is satisfied if and only if all the elements in {@code objects}
     * are different from each other.
     * @param objects variable arguments of type {@code Object}. None of them can be {@code null}.
     * @return the constructed constraint.
     * @throws NullPointerException if one of {@code objects} is {@code null}.
     */
    public static @NotNull Constraint allDifferent(@NotNull Object... objects){
        Objects.requireNonNull(objects);

        Constraint allDifferentConstraint = allDifferent(Arrays.asList(objects));

        assert allDifferentConstraint != null;
        return allDifferentConstraint;
    }

    /**
     * Returns a constraint that is satisfied if and only if all the elements in {@code objects}
     * are different from each other.
     * @param objects list of objects. None of them can be {@code null}.
     * @return the constructed constraint.
     * @throws NullPointerException if {@code objects} contains {@code null} values.
     */
    public static @NotNull Constraint allDifferent(@NotNull List<?> objects){
        Objects.requireNonNull(objects);
        if(objects.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        ArrayList<?> lObjectsArrayList = new ArrayList<>(objects);
        Constraint allDifferentConstraint = new Constraint();

        for(int i = 0; i < lObjectsArrayList.size(); ++i) {
            Object object1 = lObjectsArrayList.get(i);
            for (int j = i + 1; j < lObjectsArrayList.size(); ++j) {
                Object object2 = lObjectsArrayList.get(j);
                allDifferentConstraint.add(new AConstraint("_neq", object1, object2));
            }
        }

        assert allDifferentConstraint != null;
        return allDifferentConstraint;
    }

    /**
     * Creates and returns a constraint that is the conjunction of the result of the substitutions of {@code dummy} with each
     * element in {@code iterable} (one at a time) in {@code constraint}. For example: if {@code constraint} is {@code x.neq(1)}, the dummy
     * variable is {@code x} and the objects in {@code iterable} are "1,2,3,4", the constraint returned is "1 neq 1 AND 2 neq 1 AND 3 neq 1 AND 4 neq 1".
     * @param iterable an iterable containing the substitutions for {@code dummy}. Note that the objects in {@code iterable} should be compatible with
     *                 the type of {@code dummy} (e.g. if {@code dummy} is an {@code LSet} then the objects in {@code iterable} should be {@code LSet}s or {@code Set}s
     *                 but these conditions are not checked by the method.
     * @param dummy dummy variable in {@code constraint}.
     * @param constraint the constraint to apply to each element of {@code iterable}.
     * @param <T> a generic type. It should be compatible with the type of {@code dummy}.
     * @return the constructed constraint.
     */
    public static @NotNull <T> Constraint forAll(Iterable<T> iterable, LObject dummy, Constraint constraint){
        Constraint result = new Constraint();

        VariablesGetter variablesGetter = new VariablesGetter();
        List<LObject> dummyVariables = variablesGetter.getVariables(constraint).stream().filter(object -> object instanceof IntLVar && ((IntLVar)object).isDummy()).collect(Collectors.toList());
        DummyVariablesReplacer dummyVariablesReplacer = new DummyVariablesReplacer(dummyVariables);


        for(T t : iterable){
            dummyVariablesReplacer.generateNewSubstitutions();
            Constraint constraint2 = dummyVariablesReplacer.replaceInConstraint(constraint);
            ConstraintMapper constraintMapper = new ConstraintMapper(dummy, t);
            result.add(constraintMapper.mapConstraintDeeply(constraint2));
        }

        return result;
    }

    /**
     * Returns a constraint that is always satisfiable.
     * @return the constructed constraint.
     */
    public static @NotNull Constraint truec(){
        return new Constraint("_true");
    }

    /**
     * Returns a constraint that is never satisfiable.
     * @return the constructed constraint.
     */
    public static @NotNull Constraint falsec(){
        return new Constraint("_false");
    }


    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * A list containing each atomic constraint which appear in the conjunction.
     */
    private final ArrayList<AConstraint> aConstraintsList = new ArrayList<>();


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates an empty constraint conjunction, i.e. the conjunction of zero atomic constraints.
     */
    public Constraint() {
        //DOES NOTHING
    }

    /**
     * Creates a constraint conjunction of a single atomic constraint, identified by {@code name}
     * with at most {@code Constraint.MAX_ARGUMENTS_PER_CONSTRAINT} arguments in {@code arguments}.
     * @param name the name identifying the kind of atomic constraint to create.
     * @param arguments sequence of arguments (max {@code Constraint.MAX_ARGUMENTS_PER_CONSTRAINT}).
     * @throws IllegalArgumentException if too many arguments are provided or if the name is {@code "_and"}
     * and the number of non-null object arguments is not {@code 2} or if their types are not correct.
     */
    public Constraint(@NotNull String name, @NotNull Object... arguments) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(arguments);

        if(arguments.length > Constraint.MAX_ARGUMENTS_PER_CONSTRAINT)
            throw new IllegalArgumentException("MAX " + Constraint.MAX_ARGUMENTS_PER_CONSTRAINT + " ARGUMENTS ALLOWED IN arguments");

        if(name.equals("_and")){
            if(arguments.length < 2
                    || arguments.length == 3 && arguments[2] != null
                    || arguments.length == 4 && (arguments[2] != null || arguments[3] != null))
                throw new IllegalArgumentException("CANNOT CREATE A CONJUNCTION OF A NUMBER OF CONSTRAINT DIFFERENT THAN 2");

            Objects.requireNonNull(arguments[0]);
            Objects.requireNonNull(arguments[1]);
            if(arguments[0] instanceof Constraint
                    && arguments[1] instanceof Constraint){

                addAll(((Constraint)arguments[0]));
                addAll(((Constraint)arguments[1]));
            }
            else
                throw new IllegalArgumentException("ARGUMENT OF A CONJUNCTION MUST BE A CONSTRAINT");
        }
        else{
            aConstraintsList.add(new AConstraint(name, arguments));
        }
    }

    /**
     * Copy constructor creates a clone of the constraint {@code constraint} that contains a copy of each
     * atomic constraint in it.
     * @param constraint the constraint conjunction to copy.
     */
    protected Constraint(@NotNull Constraint constraint) {
        Objects.requireNonNull(constraint);

        for(AConstraint aConstraint : constraint.aConstraintsList)
            this.aConstraintsList.add(aConstraint.clone());
    }

    /**
     * Creates a constraint conjunction of a single atomic constraint, identified by the code {@code constraintKindCode}
     * with at most {@code Constraint.MAX_ARGUMENTS_PER_CONSTRAINT} arguments in {@code arguments}.
     * @param constraintKindCode the code of the kind of constraint to create.
     * @param arguments sequence of arguments (max {@code Constraint.MAX_ARGUMENTS_PER_CONSTRAINT}).
     */
    protected Constraint(int constraintKindCode, @NotNull Object... arguments) {
        this(new AConstraint(constraintKindCode, Objects.requireNonNull(arguments)));
    }

    /**
     * Creates a constraint conjunction of one atomic constraint {@code aConstraint}.
     * @param aConstraint the only atomic constraint in the conjunction
     */
    protected Constraint(@NotNull AConstraint aConstraint) {
        Objects.requireNonNull(aConstraint);

        aConstraintsList.add(aConstraint);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// GENERAL UTILITY METHODS //////////////////////

    /**
     * Solves this constraint using a local solver;
     * @return {@code true} if the constraint is satisfiable, {@code false} otherwise.
     */
    public boolean check() {
        Solver localSolver = new Solver();
        return localSolver.check(this);
    }

    /**
     * Creates and returns a clone of this constraint (the same as {@code new Constraint(this)}.
     * @return a clone of this constraint.
     */
    @Override
    public @NotNull Constraint clone() {
        return new Constraint(this);
    }

    /**
     * Checks whether this object is equal to {@code other}.
     * @param other the other argument of equality.
     * @return {@code true} if this object is equal to {@code other}, {@code false} otherwise.
     */
    @Override
    public boolean equals(@Nullable Object other) {
        if(other == null)
            return false;

        if(other instanceof Constraint){
            Constraint otherConstraint = (Constraint) other;

            if(size() != otherConstraint.size())
                return false;

            for(int i = 0; i < this.size(); i++)
                if (!this.get(i).equals(otherConstraint.get(i)))
                    return false;

            return true;
        }
        else
            return false;
    }

    /**
     * Gets the specified argument of the constraints if the constraint conjunction contains only one atomic constraint.
     * If it contains less than one atomic constraints this method returns {@code null}.
     * If it contains more than one atomic constraint in conjunction then it returns a new constraint conjunction made
     * of only the first atomic constraint in conjunction if {@code i} is 1, it returns a conjunction made of
     * all the atomic constraint except the first if {@code i} is 2, otherwise returns {@code null}.
     * @param i index of the argument.
     * @return If it contains less than one atomic constraints this method returns {@code null}.
     * If it contains more than one atomic constraint in conjunction then it returns a new constraint conjunction made
     * of only the first atomic constraint in conjunction if {@code i} is 1, it returns a conjunction made of
     * all the atomic constraint except the first if {@code i} is 2, otherwise returns {@code null}.
     */
    public @Nullable Object getArg(int i) {
        final int size = this.aConstraintsList.size();

        if(size == 1)
            return (this.aConstraintsList.get(0)).getArg(i);
        else if(size > 1){  // AND constraint: c1 AND c2 AND ... AND cn
            if (i == 1){   // the first conjunct: c1
                AConstraint argumentAConstraint = this.aConstraintsList.get(0);
                Constraint first = new Constraint(argumentAConstraint);
                return first;
            }
            if (i == 2){    // the rest of the conjunction: c2 AND ... AND cn
                Constraint rest = new Constraint();
                rest.aConstraintsList.addAll(this.aConstraintsList.subList(1, size));
                return rest;
            }
            else return null;
        }
        else
            return null;
    }
    
    /**
     * Gets the name identifying the constraint.
     * @return "no name" if the constraint conjunction is empty,
     * "and" if the conjunction contains at least two atomic constraints,
     * the name of the atomic constraint contained if it is the only one in the conjunction.
     */
    public @NotNull String getName() {
        switch(this.size()){
            case 0:
                return "no name";

            case 1:
                AConstraint firstAConstraint = this.get(0);
                String name = firstAConstraint.getName();
                if(name.startsWith("_"))
                    name = name.substring(1);
                assert name != null;
                return name;

            default:
                return "and";

        }
    }

    /**
     * Checks whether this constraint is ground (i.e. everything it contains is ground) or not.
     * @return {@code true} if the constraint is ground, {@code false} otherwise.
     */
    public boolean isGround() {
        for(AConstraint aConstraint : this.aConstraintsList)
            if(!aConstraint.isGround())
                return false;
        return true;
    }
 
    /**
     * Computes and returns the set of all possible values for {@code lVar} which satisfy this constraint conjunction.
     * This method uses a local solver.
     * @param lVar The logical variable which values are collected.
     * @return an {@code LSet} containing all possible solutions for {@code lVar} that satisfy this constraint.
     */
    public @NotNull LSet setof(@NotNull LVar lVar) {
        Objects.requireNonNull(lVar);

        Solver localSolver = new Solver();
        localSolver.add(this);
        if (lVar instanceof IntLVar)
        	localSolver.add(((IntLVar)lVar).label());

        LSet setof = localSolver.setof(lVar);
        assert setof != null;
        return setof;
    }
    
    /**
     * Solves this constraint using a local solver.
     * @throws Failure if the constraint is not satisfiable.
     */
    public void solve() throws Failure {
        Solver localSolver = new Solver();
        localSolver.solve(this);
    }

    /**
     * Solves this constraint using a local solver but leaving the constraint and all logical variables unchanged;
     * @return {@code true} if the constraint is satisfiable, otherwise it returns {@code false}.
     * @see Constraint#solve()
     */
    public boolean test() { 
        Constraint cCopy = this.clone();
        Solver localSolver = new Solver();
        localSolver.add(cCopy);
        return localSolver.test();
    }

    /**
     * Gives a string representation of the constraint conjunction like <strong>"_x = 1 + 2 AND _x.label()"</strong>
     * @return a {@code String} representing the constraint conjunction.
     */
    public @NotNull String toString() {
        String string = ConstraintStringifier.stringify(this);

        assert string != null;
        return string;
    }

    /**
     * Similar to the method {@code Constraint#toString()} but gives a different string representation: one of the form
     * <strong>"constraint(constraint_name, arg_1, arg_2, arg_3, arg_4) - alternative: index_of_alternative, solved?: boolean_value"</strong>.
     * Used for debugging purposes.
     * @return a {@code String} representation for the constraint conjunction.
     */
    public @NotNull String toStringInternals() {
        String internalString = ConstraintStringifier.internalStringify(this);

        assert internalString != null;
        return internalString;
    }

    /**
     * Needed for the visitor design pattern.
     * @param visitor the visitor that wants to visit the object.
     * @return the result of {@code visitor.visit(this)}.
     */
    public @Nullable Object accept(@NotNull Visitor visitor){
        Objects.requireNonNull(visitor);

        return visitor.visit(this);
    }

    /**
     * Raises an exception {@code Fail}. This exception is handled by the {@code Solver} which
     * will backtrack if there are open choice points or throw an exception {@code Failure} otherwise.
     * @throws Fail always.
     */
    public void fail() {
        for(AConstraint aConstraint : aConstraintsList)
            aConstraint.setSolved(true);

        throw new Fail();
    }

    /**
     * Checks whether the first atomic constraint is solved for the first time or not.
     * @return {@code true} if the first atomic constraint is solved for the first time, {@code false} otherwise.
     */
    public boolean firstCall() {
        assert this.size() >= 1;

        return this.aConstraintsList.get(0).firstCall;
    }

    /**
     * Sets the first atomic constraint {@code firstCall} field value to {@code false}.
     * It will return {@code false} when checked with {@code firstCall()}.
     * @see Constraint#firstCall()
     */
    public void notFirstCall() {
        assert this.size() >= 1;

        this.aConstraintsList.get(0).firstCall = false;
    }


    /**
     * Gets the next alternative of the first constraint in the conjunction if it is the only one.
     * @return an integer corresponding to the next branch of
     * the non-deterministic resolution of this constraint if the size of the conjunction is one.
     * Returns {@code 0} if the size is at least {@code 2}, raises an error otherwise.
     */
    public int getAlternative() {
        assert this.size() >= 1;

        if (this.aConstraintsList.size() > 1)
            return 0;
        else return this.aConstraintsList.get(0).getAlternative();
    }

    /**
     * Sets the solved flag of each atomic constraint in the conjunction to {@code false}.
     */
    public void notSolved() {
        for(AConstraint aConstraint : aConstraintsList)
            aConstraint.setSolved(false);
    }

    //////////////// CONSTRAINT METHODS //////////////////////

    /**
     * Logical "AND" between this constraint conjunction and the parameter {@code constraint}. Does not modify this object.
     * @param constraint the constraint conjunction which is the second argument of the logical "AND".
     * @return a new constraint conjunction obtained with the conjunction of this object with the parameter {@code constraint}.
     */  
    public @NotNull Constraint and(@NotNull Constraint constraint) {
        Objects.requireNonNull(constraint);

        Constraint conjunction = new Constraint(this);
        conjunction.add(constraint);

        assert conjunction != null;
        return conjunction;
    }

    /**
     * Adds each atomic constraint in {@code constraint} to this constraint conjunction then returns this constraint conjunction.
     * @param constraint the constraint conjunction which atomic constraints are to be added to this constraint conjunction.
     * @return this conjunction with the addition of each atomic constraint in {@code constraint}.
     */
    public @NotNull Constraint add(@NotNull Constraint constraint) {
        Objects.requireNonNull(constraint);

        this.aConstraintsList.addAll(constraint.aConstraintsList);
        return this;
    }

    /**
     * Deterministic "implication test" <strong>(A implies B)</strong> between this constraint
     * conjunction and the parameter constraint.
     * @param constraint the second argument of the "implication test".
     * @return a new constraint conjunction made of a single atomic constraint which is the implication test.
     * between this constraint conjunction and the parameter {@code constraint}.
     */
    public @NotNull Constraint impliesTest(@NotNull Constraint constraint) {
        Objects.requireNonNull(constraint);

        Constraint implicationTest = new Constraint(Environment.impliesTestCode, this, constraint);
        return implicationTest;
    }

    /**
     * Deterministic "negation test" <strong>not(A)</strong> over this constraint conjunction
     * @return a new constraint conjunction made of a single atomic constraint which is the negation test
     * over this constraint conjunction.
     */
    public @NotNull Constraint notTest() {
        Constraint notTest = new Constraint(Environment.notTestCode, this);
        return notTest;
    }

    /**
     * Logical and NON-DETERMINISTIC "OR" between this constraint conjunction and the parameter constraint.
     * @param constraint the constraint conjunction which is the second argument of the non-deterministic "OR".
     * @return a new constraint conjunction obtained with the disjunction of this object with the parameter {@code constraint}.
     */
    public @NotNull Constraint or(@NotNull Constraint constraint) {
        Objects.requireNonNull(constraint);

        Constraint disjunction = new Constraint(Environment.orCode, this, constraint);
        return disjunction;
    }

    /**
     * Deterministic "or test" between this constraint conjunction and the parameter {@code constraint}.
     * @param constraint the second argument of the "or test".
     * @return a new constraint conjunction which has the "or test" as its single atomic constraint.
     */
    public @NotNull Constraint orTest(@NotNull Constraint constraint) {
        Objects.requireNonNull(constraint);

        Constraint orTest = new Constraint(Environment.orTestCode, this, constraint);
        return orTest;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Adds the atomic constraint parameter to the conjunction.
     * NOTE: This method modifies the invocation object.
     * @param aConstraint the atomic constraint to add.
     */
    protected void add(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;

        this.aConstraintsList.add(aConstraint);

        return;
    }

    /**
     * Adds each atomic constraint in the parameter to this constraint conjunction.
     * NOTE: This method may modify the invocation object.
     * @param constraint the constraint conjunction whose atomic constraints are to be added.
     */
    protected void addAll(@NotNull Constraint constraint) {
        assert constraint != null;

        this.aConstraintsList.addAll(constraint.aConstraintsList);
        return;
    }

    /**
     * Gets the i-th atomic constraint in the conjunction (starting from 0).
     * @param i the index of the atomic constraint to retrieve (starting from 0).
     * @return the i-th (starting from 0) atomic constraint in the conjunction.
     */
    protected @NotNull AConstraint get(int i) {
        AConstraint aConstraint = aConstraintsList.get(i);

        assert aConstraint != null;
        return aConstraint;
    }

    /**
     * Checks whether the constraint conjunction is empty or not.
     * @return {@code true} if the constraint conjunction is empty, {@code false} otherwise.
     */
    protected boolean isEmpty() {
        return this.aConstraintsList.isEmpty();
    }

    /**
     * Logical and NON-DETERMINISTIC "OR" between this constraint conjunction and the parameter atomic constraint.
     * @param aConstraint the atomic constraint which is the second argument of the logical "OR".
     * @return a new constraint conjunction obtained with the disjunction of this object with the parameter {@code aConstraint}.
     */
    protected @NotNull AConstraint or(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;

        AConstraint c = new AConstraint(Environment.orCode, this, aConstraint);
        return c;
    }

    /**
     * The number of atomic constraints in the conjunction.
     * @return the number of atomic constraints in the conjunction.
     */
    protected int size() {
        return this.aConstraintsList.size();
    }

    /**
     * Returns the iterator over the list of atomic constraints in the conjunction.
     * @return an iterator over the atomic constraints in the conjunction.
     */
    protected @NotNull Iterator<AConstraint> iterator() {
        Iterator<AConstraint> iterator = this.aConstraintsList.iterator();

        assert iterator != null;
        return iterator;
    }
}
