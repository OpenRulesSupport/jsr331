package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.annotation.UnsupportedOperation;
import jsetl.exception.InitLObjectException;
import jsetl.exception.NotInitLObjectException;

import java.util.*;
import java.util.stream.Stream;

/**
 * Objects of this class are restricted intentional sets of the form {x : D | F(x) @ P(x)}, which denote the intensional
 * set {P(x) | x in D and F(x)}. {@code x} is the control term of the RIS, i.e. a dummy variable,
 * {@code D} is the domain of the RIS, i.e. a finite (possibly unspecified) s that is also the domain of {@code x}.
 * {@code F(x)}, the filter, is a constraint in which could appear {@code x} or free variables (or dummy variables)
 * defined outside of the RIS, this constraint must be satisfied by all {@code x}
 * in order for them to be used in the formula {@code P(x)}, the pattern,
 * to form the member terms of the intensional set.
 */
public class Ris extends LSet implements Visitable {

	//////////////////////////////////////////////////////
	/////// DATA MEMBERS /////////////////////////////////
	//////////////////////////////////////////////////////

	/**
	 * This logical object is the control term of the restricted intensional set.
	 */
	private final @NotNull LObject controlTerm;

	/**
	 * This logical set is the domain of the restricted intensional set: the set to which
	 * each instances of the control term must belong.
	 */
	private final @NotNull LSet domain;

	/**
	 * This constraint needs to be satisfied by a control term instance (and other variables that may be involved)
	 * in order to use it to create an instance of the pattern (i.e. an element of the restricted intensional set).
	 */
	private final @NotNull Constraint filter;

	/**
	 * This logical object is the pattern of the restricted intensional set, i.e. the result of an expression
	 * that forms the elements of the set.
	 */
	private final @NotNull LObject pattern;

	/**
	 * Instance of a dummy variables replacer, used to replace all instances of dummy variables in the filter and pattern.
	 */
	private final @NotNull DummyVariablesReplacer dummyVariablesReplacer;

	/**
	 * Instance of an expansor for this {@code Ris}. It is used to check if this set can be expanded and to construct
	 * its expansion.
	 */
	private final @NotNull RisExpansor expansor;


	////////////////////////////////////////////////////////
	/////// CONSTRUCTORS ///////////////////////////////////
	////////////////////////////////////////////////////////

	/**
	 * Constructs the restricted intensional set {@code {controlTerm : domain | filter @ pattern}} and treats each variable in
	 * {@code dummyVariables} as a dummy variable (i.e. creates a new instance for every application of filter and pattern).
	 * Note that also the variables created to represent the flattened form of expressions are treated as dummy variables, even if
	 * they do not occur in {@code dummyVariables}.
	 *
	 * @param controlTerm control term of this {@code Ris}.
	 * @param domain domain of this of this {@code Ris}.
	 * @param filter filter of this {@code Ris}.
	 * @param pattern pattern of this {@code Ris}.
	 * @param dummyVariables each variable in this array will be treated as a dummy variable if it occurs in {@code filter} or {@code pattern}.
	 *                       It can not contain {@code null} values.
	 * @throws NullPointerException if {@code dummyVariables} contains {@code null} values.
	 */
	public Ris(@NotNull LObject controlTerm, @NotNull LSet domain, @NotNull Constraint filter, @NotNull LObject pattern, @NotNull LObject... dummyVariables){
		super();

		//Not Null checks
		Objects.requireNonNull(controlTerm);
		Objects.requireNonNull(domain);
		Objects.requireNonNull(filter);
		Objects.requireNonNull(pattern);
		Objects.requireNonNull(dummyVariables);
		if(Stream.of(dummyVariables).anyMatch(Objects::isNull))
			throw new NullPointerException("NULL VALUES NOT ALLOWED");
		if(Stream.of(dummyVariables).anyMatch(LObject::isBound))
			throw new InitLObjectException();

		this.controlTerm = controlTerm;
		this.domain = domain;
		this.filter = filter;
		this.pattern = pattern;

		List<LObject> existentialVariables = getDummyVariables(dummyVariables);

		this.dummyVariablesReplacer = new DummyVariablesReplacer(existentialVariables);
		this.expansor = new RisExpansor(this);
	}

	/**
	 * Constructs the restricted intensional set {@code {controlTerm : domain | filter @ controlTerm}}.
	 * Note that also the variables created to represent the flattened form of expressions are treated as dummy dummyVariables.
	 *
	 * @param controlTerm control term (and pattern) of this {@code Ris}.
	 * @param domain domain of this of this {@code Ris}.
	 * @param filter filter of this {@code Ris}.
	 */
	public Ris(@NotNull LObject controlTerm, @NotNull LSet domain, @NotNull Constraint filter){
		this(controlTerm, domain, filter, controlTerm);
	}

	/**
	 * Constructs a restricted intensional set that has the same {@code controlTerm}, {@code domain}, {@code filter}, {@code pattern} and
	 * dummy variables as the argument {@code ris}.
	 * @param ris the restricted intensional set to copy.
	 */
	public Ris(@NotNull Ris ris){
		Objects.requireNonNull(ris);

		this.controlTerm = ris.controlTerm;
		this.domain = ris.domain;
		this.filter = ris.filter;
		this.pattern = ris.pattern;
		this.dummyVariablesReplacer = ris.dummyVariablesReplacer.clone();
		this.expansor = new RisExpansor(this);

		//ASSERTIONS
		assert this.controlTerm != null;
		assert this.domain != null;
		assert this.filter != null;
		assert this.pattern != null;
		assert this.dummyVariablesReplacer != null;
		assert this.expansor != null;
	}


	///////////////////////////////////////////////////////
	////// PUBLIC UTILITY METHODS /////////////////////////
	///////////////////////////////////////////////////////

	/**
	 * Return the control term of this {@code Ris}.
	 * @return the control term.
	 */
	@NotNull
	public LObject getControlTerm(){
		assert controlTerm != null;

		return controlTerm;
	}

	/**
	 * Return the domain of this {@code Ris}.
	 * @return the domain.
	 */
	@NotNull
	public LSet getDomain(){
		assert domain != null;

		return domain;
	}

	/**
	 * Return the filter of this {@code Ris}.
	 * @return the filter.
	 */
	@NotNull
	public Constraint getFilter(){
		assert filter != null;

		return filter;
	}

	/**
	 * Return the pattern of this {@code Ris}.
	 * @return the pattern.
	 */
	@NotNull
	public LObject getPattern(){
		assert pattern != null;

		return pattern;
	}

	/**
	 * Sets the name of this {@code Ris} to {@code name} and returns {@code this}.
	 * @param name the new name for this {@code Ris}.
	 * @return the invocation object.
	 */
	@Override
	public @NotNull Ris setName(@NotNull String name){
		Objects.requireNonNull(name);

		super.setName(name);

		return this;
	}

	/**
	 * Same as calling {@code getValue()} on the value returned by the {@code expand()} method.
	 * @return the value of this {@code Ris}.
	 * @throws IllegalStateException if this {@code Ris} is not expandable.
	 * @see Ris#expand()
	 * @see Ris#isExpandable()
	 */
	@Override
	public HashSet<?> getValue(){
		return this.expand().getValue();
	}

	/**
	 * Outputs a description of this {@code Ris} to the standard output.
	 * The description is of the form "name = toString".
	 */
	@Override
	public void output(){
		String output = "_"+this.getName() + " = " + this.toString();
		System.out.println(output);
	}

	/**
	 * Constructs and returns a clone of this {@code Ris}, the same as {@code new Ris(this)}.
	 * @return a clone of this {@code ris}.
	 */
	@Override
	@NotNull
	public Ris clone(){
		Ris clonedRis =  new Ris(this);

		assert  clonedRis != null;
		return clonedRis;
	}

	/**
	 * Checks whether this {@code Ris} is expandable or not.
	 * A {@code Ris} is expandable if and only if its domain is empty or (it has at least a ground element
	 * and the filter has no free (not dummy and not part of control term) variables).
	 * @return {@code true} if this {@code Ris} is expandable, {@code false} otherwise.
	 * @see Ris#expand()
	 */
	public boolean isExpandable(){
		return expansor.canExpand(new Solver());
	}

	/**
	 * Returns a logical set containing the expansion of this {@code Ris}, if it is expandable.
	 * The expansion of a {@code Ris} is a {@code LSet} containing each possible {@code P(d)} for each ground {@code d in domain}
	 * that satisfies {@code F(d)}. The constructed {@code LSet} has an empty tail ({@code LSet.empty()})
	 * if the domain of the {@code Ris} is ground,
	 * otherwise it is a {@code Ris} with the same control term, filter and pattern as the original {@code Ris} and
	 * a {@code LSet} containing each non-ground element of {@code domain} as its domain.
	 * @return the expansion of this {@code Ris}.
	 * @throws IllegalStateException if the this {@code Ris} is not expandable
	 * @see Ris#isExpandable()
	 */
	@NotNull
	public LSet expand(){
		Solver solver = new Solver();
		if(expansor.canExpand(solver)){
			LSet risExpansion = expansor.expand(solver);

			assert risExpansion != null;
			return risExpansion;
		}

		else
			throw new IllegalStateException("RIS NOT EXPANDABLE");
	}

	/**
	 * Returns tha tail of this restricted intensional set, which is {@code this}.
	 * @return {@code this}.
	 */
	@Override
	@NotNull
	public Ris getTail(){
		return this;
	}

	/**
	 * Checks whether the {@code Ris} is initialized or not: it is if and only if its domain is bound.
	 * @return {@code true} if this {@code Ris} is initialized, {@code false} otherwise.
	 */
	@Override
	protected boolean isInitialized() {
		return domain.isBound();
	}

	/**
	 * Checks whether the {@code Ris} domain is bound or not.
	 * @return {@code true} if the domain of this {@code Ris} is initialized, {@code false} otherwise.
	 */
	@Override
	public boolean isBound() {
		return domain.isBound();
	}

	/**
	 * Checks whether the {@code Ris} is ground or not: it is if and only if its domain is ground.
	 * @return {@code true} if this {@code Ris} is ground, {@code false} otherwise.
	 */
	@Override
	public boolean isGround(){
		return getDomain().isGround();
	}

	/**
	 * Checks whether the {@code Ris} domain is closed or not.
	 * @return {@code true} if this {@code Ris} domain is closed, {@code false} otherwise.
	 */
	@Override
	public boolean isClosed(){
		return domain.isClosed();
	}

	/**
	 * Constructs and returns a string representation of this restricted intensional set,
	 * the string is of the form {@code "{ controlTerm : domain | filter @ pattern }"}.
	 * @return a string representation of this {@code Ris}.
	 */
	@Override
	@NotNull
	public String toString() {
		String patternString = pattern.toString();
		if(pattern instanceof IntLVar && !((IntLVar) pattern).getConstraint().isEmpty())
			patternString += ", " + ((IntLVar) pattern).getConstraint().toString();
		else if(pattern instanceof SetLVar && !((SetLVar) pattern).getConstraint().isEmpty())
			patternString += "," +((SetLVar) pattern).getConstraint().toString();

		return "{ " + controlTerm + " : " + domain + " | " + filter + " @ " + patternString + " }";
	}

	/**
	 * Tests whether this {@code Ris} contains the parameter {@code object} as its (or inside its) control term, domain, filter or pattern.
	 * @param object object to search inside {@code this}.
	 * @return {@code true} if {@code object} appears inside {@code this}, {@code false} otherwise.
	 */
	@Override
	public boolean occurs(@NotNull Object object){
		Objects.requireNonNull(object);

		DeepExplorer deepExplorer = new DeepExplorer();

		Object found = deepExplorer.exploreInternally(this, (o) -> {
			if (o == object)
				deepExplorer.stopExploration(true);
		});

		return found != null && found.equals(true);
	}

	/**
	 * Checks whether this object is equal to the parameter {@code object}.
	 * Note that equal {@code Ris}s will have the same hash code.
	 * @param object the supposedly equal object.
	 * @return {@code true} if this object is equal to {@code object}, {@code false} otherwise.
	 */
	@Override
	public boolean equals(@Nullable Object object){
		if(object == null)
			return false;
		if(object == this)
			return true;
		if(object instanceof LSet)
			return this.equals((LSet) object);
		else
			return false;
	}

	/**
	 * Returns the hashcode for this {@code Ris}. Note that equal {@code Ris}s will have the same hash code.
	 * @return the hashcode.
	 */
	@Override
	public int hashCode(){
		return controlTerm.hashCode() + domain.hashCode() + filter.hashCode() + pattern.hashCode();
	}

	/**
	 * Calling this method is the same as calling {@code this.expand().iterator()}.
	 * @return an iterator over the expansion of this {@code Ris}.
	 * @throws IllegalStateException if this {@code Ris} is not expandable.
	 * @see Ris#expand()
	 * @see Ris#isExpandable()
	 */
	@Override
	public @NotNull Iterator<Object> iterator(){
		Iterator<Object> iterator = this.expand().iterator();
		assert iterator != null;
		return iterator;
	}

	/**
	 * Calling this method is the same as calling {@code this.expand().getSize()}.
	 * @return the size of the expansion of this {@code Ris}.
	 * @throws IllegalStateException if this {@code Ris} is not expandable.
	 * @see Ris#expand()
	 * @see Ris#isExpandable()
	 */
	@Override
	public int getSize(){
		return this.expand().getSize();
	}

	/**
	 * Calling this method is the same as calling {@code this.expand().toArray()}.
	 * @return an array of objects containing all the elements of the expansion of this {@code ris}.
	 * @throws IllegalStateException if this {@code Ris} is not expandable.
	 * @see Ris#expand()
	 * @see Ris#isExpandable()
	 */
	@Override
	public @NotNull Object[] toArray(){
		Object[] result = this.expand().toArray();
		assert result != null;
		return result;
	}

	/**
	 * Calling this method is the same as calling {@code this.expand().getSize(delimiter)}.
	 * It outputs to standard output the list of elements in the expansion of this {@code Ris} separated by {@code delimiter}.
	 * @param delimiter character to use between elements.
	 * @throws IllegalStateException if this {@code Ris} is not expandable.
	 * @see Ris#expand()
	 * @see Ris#isExpandable()
	 */
	@Override
	public void printElems(char delimiter){
		this.expand().printElems(delimiter);
	}

	/**
	 * Needed for the Visitor pattern to work.
	 * @param visitor the visitor that wants to visit the object.
	 * @return the result of {@code visitor.visit(this)}
	 */
	@Override
	public @Nullable Object accept(@NotNull Visitor visitor){
		Objects.requireNonNull(visitor);

		return visitor.visit(this);
	}

	/**
	 * This method returns a constraint that is true if and only if
	 * for every element in {@code this}, that element satisfies the constraint
	 * {@code constraint} with {@code lVar} replaced with such element in it.
	 */
	@Override
	public @NotNull Constraint forallElems(@NotNull LVar lVar, @NotNull Constraint constraint){
		Objects.requireNonNull(lVar);
		Objects.requireNonNull(constraint);

		Constraint cc = new Constraint();
		LObject patternApplied = (LObject) this.P(getControlTerm(),cc);
		ConstraintMapper constraintMapper = new ConstraintMapper(lVar, patternApplied);
		Constraint mappedConstraint = constraintMapper.mapConstraintDeeply(constraint);

		LObject[] dummyVariables = getDummyVariables();
		LObject[] newDummyVariables = new LObject[dummyVariables.length + 1];
		for (int i = 0; i < dummyVariables.length; ++i)
			newDummyVariables[i] = dummyVariables[i];
		newDummyVariables[dummyVariables.length] = patternApplied;

		Constraint result = this.eq(new Ris(getControlTerm(), getDomain(), getFilter().and(mappedConstraint).and(cc), getPattern(), newDummyVariables));
		assert result != null;
		return result;
	}

	////// UNSUPPORTED METHODS ////////////////////////////


	/**
	 * This operation is not supported by {@code Ris}, so it will always throw {@code UnsupportedOperationException}.
	 * @return nothing, throws an exception always.
	 * @throws UnsupportedOperationException always.
	 */
	@Override
	@UnsupportedOperation
	public Ris normalizeSet(){
		throw new UnsupportedOperationException();
	}

	/**
	 * Calling this method always throws {@code UnsupportedOperationException}.
	 * @param value the value to s.
	 * @return nothing, it always throws an exception.
	 * @throws UnsupportedOperationException always.
	 */
	@Override
	@UnsupportedOperation
	public LSet setValue(Set<?> value) {
		throw new UnsupportedOperationException();
	}

	///////////////////////////////////////////////////////
	////// PROTECTED UTILITY METHODS //////////////////////
	///////////////////////////////////////////////////////

	/**
	 * Checks whether this restricted intensional set is equal to the set {@code lSet}.
	 * @param lSet object to test for equality with {@code this}.
	 * @return {@code true} if {@code this} is equal to {@code lSet}, {@code false} otherwise.
	 */
	protected boolean equals(@Nullable LSet lSet) {
		if(lSet == null)
			return false;

		if(lSet == this)
			return true;

		if(lSet instanceof Ris)
			return equals((Ris) lSet);

		if(lSet.isBound() && lSet.isEmpty() && this.isBound() && this.isEmpty())
			return true;
		if(this.isExpandable() && this.forceExpansion(new Solver()).equals(lSet))
			return true;
		else
			return false;

	}

	/**
	 * Checks whether this restricted intensional set is equal to the restricted intensional set {@code ris}.
	 * @param ris object to test for equality with {@code this}.
	 * @return {@code true} if {@code this} is equal to {@code ris}, {@code false} otherwise.
	 */
	protected boolean equals(@Nullable Ris ris) {
		if(ris == null)
			return false;

		if(ris == this)
			return true;

		return ris.getControlTerm().equals(this.controlTerm)
				&& ris.getDomain().equals(this.domain)
				&& ris.getPattern().equals(this.pattern )
				&& ris.getFilter().equals(this.filter);
	}


	///////////////////////////////////////////////////////////
	////////// PROTECTED METHODS //////////////////////////////
	///////////////////////////////////////////////////////////

	/**
	 * Creates a deep (consistent: equal variables are replaced with equal clones) clone of the control term.
	 * @return a deep clone of the control term.
	 */

	protected @NotNull Object getNewControlTerm(){
		Object newControlTerm =  new DeepCloner().visit(controlTerm);

		assert newControlTerm != null;
		return newControlTerm;
	}

	/**
	 * Returns a new constraint which is equal to the (deep) substitution of {@code controlTermInstance} with the control term inside
	 * the filter and with the substitution of each dummy variable with a new instance.
	 * Note that calling this method will generate new substitutions for dummy variables: this method must always be called
	 * right before calling {@code Ris#P(...)}.
	 * @param controlTermInstance instance of the control term.
	 * @return the constructed constraint
	 */

	@NotNull
	protected Constraint F(@NotNull Object controlTermInstance){
		assert controlTermInstance != null;

		Object newControlTermInstance = getNewControlTerm();
		if(!Solver.checkUnification(newControlTermInstance, controlTermInstance))
			return new Constraint(Environment.falseCode, null, null);

		//Generate new substitutions for dummy dummyVariables
		dummyVariablesReplacer.generateNewSubstitutions();


		//Get all dummyVariables in pattern and save their internal constraint (if present)
		Constraint internalConstraints = getInternalConstraints(filter);


		Constraint actualFilter = filter.and(internalConstraints);
		//Replace instances of control term with the corresponding control term instance parts
		//Replace dummy dummyVariables with new instances
		Constraint generatedConstraintInstance = generateConstraintInstance(actualFilter, newControlTermInstance);

		assert generatedConstraintInstance != null;
		return generatedConstraintInstance;
	}

	/**
	 * Returns a new constraint which is equal to the (deep) substitution of {@code controlTermInstance} with the control term inside
	 * the negation of the filter and with the substitution of each dummy variable with a new instance.
	 * Note that calling this method will generate new substitutions for dummy variables.
	 * @param controlTermInstance instance of the control term.
	 * @return the constructed constraint.
	 */
	protected @NotNull Constraint notF(@NotNull Object controlTermInstance){
		assert controlTermInstance != null;

		Object newControlTermInstance = getNewControlTerm();
		if(!Solver.checkUnification(newControlTermInstance, controlTermInstance))
			return Constraint.truec();

		//Generate new substitutions for dummy dummyVariables
		dummyVariablesReplacer.generateNewSubstitutions();

		//Get the negation of the filter
		Constraint negatedFilter = new ConstraintNegator().negate(filter);

		Constraint internalConstraints = getInternalConstraints(negatedFilter);
		Constraint actualNegatedFilter = negatedFilter.and(internalConstraints);


		//Replace instances of control term with the corresponding control term instance parts
		//Replace dummy dummyVariables with new instances
		Constraint generatedConstraintInstance = generateConstraintInstance(actualNegatedFilter, controlTermInstance);

		assert generatedConstraintInstance != null;
		return generatedConstraintInstance;
	}

	/**
	 * Tis method will construct and return the result of the application of the pattern with the given {@code controlTermInstance}
	 * and will replace dummy variables occurring in the pattern with the substitutions generated in a previous call of
	 * {@code Ris#F(controlTermInstance)} or {@code Ris#notF(controlTermInstance)}. Dummy variables not occurring in the filter will get
	 * a fresh new substitution. Calling this method will also add to {@code constraint} each internal constraint of the variables in
	 * the pattern, after replacing occurrences of dummy variables and control term parts.
	 * @param controlTermInstance instance of the control term.
	 * @param constraint a constraint in which will be added the mapped internal constraints of the variables in the pattern.
	 * @return the result of the application of the pattern with the given {@code controlTermInstance}.
	 */
	protected @NotNull Object P(@NotNull Object controlTermInstance, @NotNull Constraint constraint){
		assert controlTermInstance != null;
		assert constraint != null;

		//Replace control term with its instance
		Object controlTermReplaced = new LObjectMapper(controlTerm, controlTermInstance).mapLObjectDeeply(pattern);
		Object patternReplaced = controlTermReplaced;
		//replace dummy dummyVariables with the same instance they were substituted in filter
		if(controlTermReplaced instanceof LObject)
			patternReplaced =  dummyVariablesReplacer.replaceInLObject((LObject) controlTermReplaced);

		Constraint beforeReplacementOfDummyVariables = getInternalConstraints(pattern);

		//Add the saved internal constraints of the variables to the input {@code constraint},
		// after replacing dummy variables with their instance
		if(!beforeReplacementOfDummyVariables.isEmpty()){
			beforeReplacementOfDummyVariables = generateConstraintInstance(beforeReplacementOfDummyVariables, controlTermInstance);
			constraint.addAll(beforeReplacementOfDummyVariables);
		}

		assert patternReplaced != null;
		return patternReplaced;
	}

	/**
	 * Constructs a {@code Constraint} that is the conjunction of each internal constraint in
	 * each variable {@code IntLVar} (currently {@code SetLVar and BoolLVar} are not supported)
	 * appearing in {@code object}.
	 * @param object the object that may contain variables with internal constraints.
	 *               It usually is a {@code Constraint} or a {@code LObject}.
	 * @return the constructed constraint.
	 */
	protected @NotNull Constraint getInternalConstraints(@NotNull Object object){
		assert object != null;

		Constraint internalConstraints = new Constraint();

		List<LObject> variablesInConstraint = new VariablesGetter().getVariables(object);
		for(LObject lObject : variablesInConstraint)
			if(lObject instanceof IntLVar){
				internalConstraints.add(((IntLVar) lObject).getConstraint());
			}
			//TODO add support for SetLVar and BoolLVar
		assert internalConstraints != null;
		return internalConstraints;
	}

	/**
	 * Checks whether this {@code Ris} in the context of the given solver is expandable or not.
	 * A {@code Ris} is considered expandable if its domain is empty. If the domain is not empty
	 * the {@code Ris} is expandable if its filter does not contain free variables and its domain
	 * contains at least one ground element that is not currently being expanded.
	 *
	 * @return {@code true} if this {@code Ris} is expandable, {@code false} otherwise.
	 * @see Ris#expand()
	 */
	protected boolean isExpandable(@NotNull Solver solver){
		assert solver != null;
		return expansor.canExpand(solver);
	}

	/**
	 * Returns the expansion of this restricted intensional set. The precondition of this method is that
	 * calling {@code isExpandable()} returns {@code true}.
	 * @return the expansion of this {@code Ris}.
	 */
	protected @NotNull LSet forceExpansion(@NotNull Solver solver){
		assert solver != null;

		LSet expansion = expansor.expand(solver);
		assert expansion != null;
		return expansion;
	}

	/**
	 * Returns an array of dummy variables used in this {@code Ris}.
	 * @return an array of dummy variables.
	 */
	protected @NotNull LObject[] getDummyVariables(){
		ArrayList<LObject> dummyVariablesList = dummyVariablesReplacer.getDummyVariables();

		LObject[] result = dummyVariablesList.toArray(new LObject[dummyVariablesList.size()]);
		assert result != null;
		return result;
	}

	/**
	 * Checks whether the {@code Ris} is empty or not: it is if and only if its domain is bound and empty.
	 * @return {@code true} if this {@code Ris} is empty, {@code false} otherwise.
	 * @throws NotInitLObjectException if the domain of this {@code Ris} is not bound.
	 * @see NotInitLObjectException
	 */
	@Override
	protected boolean isBoundAndEmpty() {
		return domain.isBoundAndEmpty();
	}


	//////////////////////////////////////////////////////
	//////// PRIVATE METHODS /////////////////////////////
	//////////////////////////////////////////////////////

	/**
	 * Generates an instance of the given {@code constraint} by replacing all parts of the control term with the corresponding
	 * parts of {@code controlTermInstance} and by replacing all dummy dummyVariables with the substitutions generated in a previous call
	 * of {@code dummyVariablesReplacer.generateNewSubstitutions()}.
	 * @param constraint the constraint to use to generate the instance constraint.
	 * @param controlTermInstance instance of the control term.
	 * @return the instance constraint.
	 */
	private @NotNull Constraint generateConstraintInstance(@NotNull Constraint constraint, @NotNull Object controlTermInstance){
		assert constraint != null;
		assert controlTermInstance != null;

		Constraint controlTermReplacedFilter = new ConstraintMapper(controlTerm, controlTermInstance).mapConstraintDeeply(constraint);
		Constraint dummyVariablesAndControlTermReplacedFilter = dummyVariablesReplacer.replaceInConstraint(controlTermReplacedFilter);

		assert dummyVariablesAndControlTermReplacedFilter != null;
		return dummyVariablesAndControlTermReplacedFilter;
	}

	/**
	 * Returns a list containing all dummy variables in {@code dummyVariables} and all those that occur inside the filter or pattern.
	 * @param dummyVariables array of variables to treat as dummy variables.
	 * @return a list of all dummy variables inside this {@code Ris}.
	 */
	private @NotNull List<LObject> getDummyVariables(@NotNull LObject[] dummyVariables){
		assert dummyVariables != null;

		ArrayList<LObject> existentialVariables = new ArrayList<>();
		VariablesGetter variablesGetter = new VariablesGetter();
		variablesGetter.avoidRis();
		List<LObject> variables = variablesGetter.getVariables(filter);
		variables.addAll(new VariablesGetter().getVariables(pattern));
		for (LObject dummyVariable : dummyVariables) {
			existentialVariables.add(dummyVariable);
		}
		variables.forEach(object -> {
			if(object instanceof IntLVar && ((IntLVar)object).isDummy()){
				for(LObject existentialVariable : existentialVariables)
					if(existentialVariable == object)
						return;
				existentialVariables.add(object);
			}
		});

		assert existentialVariables != null;
		return existentialVariables;
	}
}
	
