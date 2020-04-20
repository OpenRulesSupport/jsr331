package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.NotPFunException;

import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Instances of this class are logical maps, i.e., logical sets whose
 * elements are logical pairs that denote partial functions. Partial functions
 * are binary relations in which there can't be two pairs with the same
 * first element and different second element.
 */
public class LMap extends LRel implements Cloneable, Visitable{

	///////////////////////////////////////////////////////////////
	//////////////// STATIC METHODS ///////////////////////////////
	///////////////////////////////////////////////////////////////

	//////////////// PUBLIC METHODS //////////////////////////////

	/**
	 * Creates an empty {@code LMap}.
	 *
	 * @return an empty {@code LMap}.
	 */
	public static @NotNull LMap empty(){
		return new LMap(new HashSet<>(0));
	}

	//////////////// PROTECTED METHODS //////////////////////////

	/**
	 * This method checks if {@code collection} satisfies the partial function condition:
	 * {@code checkPFunConditionAndThrowException(collection) == true} <==> forall x,y1,y2 :( [x,y1] in collection and [x,y2] in collection => y1 = y2).
	 * @param collection the collection of pairs that is checked. It must not contain {@code null} values.
	 * @return {@code true} if the partial function condition is satisfied by {@code collection}, {@code false} otherwise.
	 * @throws NullPointerException if {@code collection} contains {@code null} values.
	 */
	protected static boolean pfunCheck(@NotNull Collection<?> collection){
		Objects.requireNonNull(collection);
		if(collection.stream().anyMatch(Objects::isNull))
			throw new NullPointerException("NULL VALUES NOT ALLOWED");

		ArrayList<Object> elements = new ArrayList<>(collection);
		for (int i = 0; i < elements.size(); i++) {
			Object element = elements.get(i);
			if (elements.subList(i, elements.size()).stream().anyMatch(remainingElement -> {
				LPair p1 = ((LPair) element).getEndOfEquChain();
				LPair p2 = ((LPair) remainingElement).getEndOfEquChain();

				if (p1.isInitialized() && p2.isInitialized()) {

					Object x1 = p1.getFirst();
					Object y1 = p1.getSecond();
					Object x2 = p2.getFirst();
					Object y2 = p2.getSecond();

					if (LObject.equals(x1, x2) && LObject.isGround(y1) && LObject.isGround(y2) && !LObject.equals(y1, y2))
						return true;
				}

				return false;
			}))
				return false;
		}

		return true;
	}


	//////////////// PRIVATE METHODS /////////////////////////////

	/**
	 * This method checks if {@code lSet} satisfies the partial function condition:
	 * {@code checkPFunConditionAndThrowException(lSet) == true} <==> forall x,y1,y2 :( [x,y1] in lSet and [x,y2] in lSet => y1 = y2).
	 * @param lSet the logical set that is checked.
	 * @throws NotPFunException if the given collection does not represent a partial function.
	 */
	private static void checkPFunConditionAndThrowException(@NotNull LSet lSet){
		assert lSet != null;
		assert StreamSupport.stream(lSet.spliterator(), false).noneMatch(Objects::isNull);
		if(!pfunCheck(lSet.toArrayList()))
			throw new NotPFunException();
	}

	/**
	 * This method checks if {@code lPairCollection} satisfies the partial function condition:
	 * {@code checkPFunConditionAndThrowException(lPairCollection) == true} <==> forall x,y1,y2 :( [x,y1] in lPairCollection and [x,y2] in lPairCollection => y1 = y2).
	 * @param lPairCollection the collection of pairs that is checked. It must not contain {@code null} values.
	 * @throws NullPointerException if {@code lPairCollection} contains {@code null} values.
	 * @throws NotPFunException if the given collection does not represent a partial function.
	 */
	private static void checkPFunConditionAndThrowException(@NotNull Collection<?> lPairCollection){
		assert lPairCollection != null;
		assert lPairCollection.stream().noneMatch(Objects::isNull);
		if(!pfunCheck(lPairCollection))
			throw new NotPFunException();

	}

	///////////////////////////////////////////////////////////////
	//////////////// CONSTRUCTORS /////////////////////////////////
	///////////////////////////////////////////////////////////////

	//////////////// PUBLIC CONSTRUCTORS //////////////////////////

	/**
	 * Constructs an unspecified logical map with default name.
	 */
	public LMap() {
		super();
	}

	/**
	 * Constructs an unbound logical map with given name.
	 * @param name name of the logical map.
	 */
	public LMap(@NotNull String name) {
		super(name);
	}

	/**
	 * Constructs a bound and closed logical map whose elements are those in {@code set} and with a default name.
	 * @param set a java set whose elements will be the elements of the constructed logical map.
	 *            It must not contain {@code null} values.
	 * @throws NullPointerException if {@code set} contains {@code null} values.
	 * @throws NotPFunException if {@code set} does not represent a partial function.
	 */
	public LMap(@NotNull Set<LPair> set) {
		this(defaultName(), set);
	}

	/**
	 * Constructs a bound and closed logical map whose elements are those in {@code set} and with a given name.
	 * @param set a java set whose elements will be the elements of the constructed logical map.
	 *            It must not contain {@code null} values.
	 * @param name the name of the logical map.
	 * @throws NullPointerException if {@code set} contains {@code null} values.
	 * @throws NotPFunException if {@code set} does not represent a partial function.
	 */
	public LMap(@NotNull String name, @NotNull Set<LPair> set) {
		super(name, set);
		checkPFunConditionAndThrowException(this);
	}

	/**
	 * Constructs a logical map which is equal to the parameter {@code lMap} and with default name.
	 * This constructor is equivalent to solving the constraint {@code this.eq(lMap)}.
	 * @param lMap the logical map to which the constructed logical map is equal.
	 */
	public LMap(@NotNull LMap lMap) {
		super(lMap);
	}

	/**
	 * Constructs a logical map which is equal to the parameter {@code lMap} and with given name.
	 * This constructor is equivalent to solving the constraint {@code this.eq(lMap)}.
	 * @param name name of the logical map.
	 * @param lMap the map to which the constructed logical map is equal.
	 */
	public LMap(@NotNull String name, @NotNull LMap lMap) {
		super(name,lMap);
	}


	///////////// PROTECTED CONSTRUCTORS //////////////////////////

	/**
	 * Constructs a logical map whose elements are those contained in {@code elements} and whose rest is {@code rest}.
	 * The constructed map has a default name.
	 * @param elements list containing the known elements for this logical map.
	 *                 It may be {@code null}, but if it is not it must not contain {@code null} values.
	 * @param rest rest for this logical map.
	 * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
	 * @throws NotPFunException if the elements in {@code elements} and those in {@code rest} do not represent a partial function.
	 */
	protected LMap(@NotNull ArrayList<LPair> elements, @NotNull LMap rest) {
		this(defaultName(), elements, rest);
	}

	/**
	 * Constructs a logical map whose elements are those contained in {@code elements} and whose rest is {@code rest}.
	 * The constructed map has a default name.
	 * @param name name of the logical map.
	 * @param elements list containing the known elements for this logical map.
	 *                 It may be {@code null}, but if it is not it must not contain {@code null} values.
	 * @param rest rest for this logical map.
	 * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
	 * @throws NotPFunException if the elements in {@code elements} and those in {@code rest} do not represent a partial function.
	 */
	protected LMap(@NotNull String name, @NotNull ArrayList<LPair> elements, LMap rest) {
		super(name,elements,rest);
		checkPFunConditionAndThrowException(this);
	}


	///////////////////////////////////////////////////////////////
	//////////////// PUBLIC METHODS ///////////////////////////////
	///////////////////////////////////////////////////////////////

	//////////////// INTERFACE METHODS IMPLEMENTATIONS ///////////

	/**
	 * Implementation of this method is needed for the Visitor design pattern.
	 * @param visitor the visitor that wants to visit the object.
	 * @return the result of the call to {@code visitor.visit(this)}.
	 */
	public @Nullable Object accept(@NotNull Visitor visitor){
		Objects.requireNonNull(visitor);

		return visitor.visit(this);
	}


	//////////////// SETTERS /////////////////////////////////////

	/**
	 * Sets the name of this {@code LMap} to {@code name}.
	 *
	 * @param name name for this {@code LMap}.
	 * @return the object on which this method was invoked.
	 */
	@Override
	public @NotNull LMap setName(@NotNull String name){
		Objects.requireNonNull(name);

		LMap nameSet = (LMap) super.setName(name);
		assert nameSet == this;
		return nameSet;
	}


	//////////////// CREATING NEW BOUND LOGICAL SETS /////////////

	/**
	 * Creates and returns a new logical map obtained from the union of {@code this} with {@code {lPair}}.
	 * The invocation object is not modified.
	 * @param lPair the logical pair to use when creating the new set.
	 * @return returns a (new) logical map obtained by adding {@code lPair} to this logical map.
	 */
	@Override
	public @NotNull LMap ins(@NotNull LPair lPair){
		Objects.requireNonNull(lPair);

		LMap afterInsertion = (LMap) super.ins(lPair);
		checkPFunConditionAndThrowException(afterInsertion);

		assert afterInsertion != null;
		return afterInsertion;
	}

	/**
	 * Creates and returns a new logical map obtained from the union of {@code this} with the set of {@code lPairs}.
	 * The invocation object is not modified.
	 * @param lPairs the logical pairs to use when creating the new set. None of its elements can be {@code null}
	 * @return returns a (new) logical map obtained by adding {@code lPairs} to this logical map.
	 * @throws NullPointerException if some of the elements in {@code lPairs} are {@code null}.
	 */
	@Override
	public @NotNull LMap ins(@NotNull LPair... lPairs){
		return insAll(lPairs);
	}

	/**
	 * Creates and returns a new logical map obtained from this by adding all
	 * elements of the parameter {@code lPairs}. Does not modify this logical map.
	 * @param lPairs array of elements to add. None of its elements can be {@code null}.
	 * @return the constructed logical map.
	 * @throws NullPointerException if at least one of the elements in {@code objects} is {@code null}.
	 */
	public @NotNull LMap insAll(@NotNull LPair[] lPairs) {
		Objects.requireNonNull(lPairs);
		if(Arrays.stream(lPairs).anyMatch(Objects::isNull))
			throw new NullPointerException("NULL VALUES NOT ALLOWED");

		LMap afterInsertions = (LMap) super.insAll(lPairs);
		checkPFunConditionAndThrowException(afterInsertions);

		assert afterInsertions != null;
		return afterInsertions;
	}

	/**
	 * Creates and returns a new logical set obtained from this by adding all
	 * elements of the parameter {@code newElements}. Does not modify this logical map.
	 * @param newElements collection of elements to add. None of its elements can be {@code null}.
	 * @return the constructed logical map.
	 * @throws NullPointerException if at least one of the elements in {@code newElements} is {@code null}.
	 */
	public @NotNull LMap insAll(@NotNull ArrayList<LPair> newElements) {
		Objects.requireNonNull(newElements);
		if(newElements.stream().anyMatch(Objects::isNull))
			throw new NullPointerException("NULL VALUES NOT ALLOWED");

		LMap afterInsertions = (LMap) super.insAll(newElements);
		checkPFunConditionAndThrowException(afterInsertions);

		assert afterInsertions != null;
		return afterInsertions;
	}


	//////////////// GENERAL UTILITY METHODS /////////////////////

	/**
	 * Creates a bit-by-bit clone of this {@code LMap}.
	 *
	 * @return a clone of this {@code LMap}.
	 * */
	@Override
	public @NotNull LMap clone(){
		LMap clone = (LMap) super.clone();

		assert clone != null;
		return clone;
	}


	///////////////////////////////////////////////////////////////
	//////////////// PROTECTED METHODS ////////////////////////////
	///////////////////////////////////////////////////////////////

	//////////////// IMPLEMENTED ABSTRACT METHODS ///////////////

	/**
	 * Creates and returns a new logical map whose elements are those contained in {@code elements} and rest is {@code rest}.
	 * @param elements list containing elements for this logical set.
	 *                 It may be {@code null}, but if it is not it must not contain {@code null} values.
	 *                 Moreover, it can only contain instances of {@code LPair}.
	 * @param rest rest for this logical set.
	 * @return the constructed logical map.
	 */
	@Override
	protected @NotNull LMap createObj(@NotNull ArrayList<?> elements, @NotNull LCollection rest) {
		assert elements != null;
		assert elements.stream().noneMatch(Objects::isNull);
		assert elements.stream().allMatch(element -> element instanceof LPair);
		assert rest != null;
		assert StreamSupport.stream(rest.spliterator(), false).noneMatch(Objects::isNull);

		ArrayList<LPair> lPairs = new ArrayList<>(elements.size());
		elements.forEach(element -> lPairs.add((LPair) element));
		checkPFunConditionAndThrowException(lPairs);

		return new LMap(lPairs,(LMap)rest);
	}


	//////////////// LOGICAL SET METHODS ///////////////////////

	@Override
	protected @NotNull LMap removeOne(){
		LMap result = new LMap();
		result.equ = super.removeOne();

		assert result != null;
		return result;
	}


	////////////////////////////////////////////////////////////
	//////////// PUBLIC CONSTRAINT METHODS /////////////////////
	////////////////////////////////////////////////////////////

	//////////// PARTIAL FUNCTION CONSTRAINTS //////////////////

	/**
	 * This method creates the "pfun" constraint, which is satisfied if
	 * and only if {@code this} represents a partial function.
	 * Note: this constraint should be posted together with other methods such as
	 * "eq", "in" and so on to ensure that the logical map still represents a
	 * partial function even after the resolution of those constraints (which don't
	 * take into account the nature of partial function of the objects of class {@code LMAp}.
	 *
	 * @return the constructed constraint.
	 * */
	public @NotNull
    ConstraintClass pfun(){
		return new ConstraintClass(Environment.pfunCode, this, null);
	}


	//////////// POSITIVE OVERRIDDEN CONSTRAINTS //////////////

	/**
	 * This method creates the "dom" constraint.
	 * @param  dom: domain of the {@code LMap} on which this method is invoked.
	 *            It is the domain of {@code this} logical map.
	 *
	 * @return the constructed constraint.
	 */
	@Override
    public @NotNull
    ConstraintClass dom(@NotNull LSet dom){
		Objects.requireNonNull(dom);

    	return new ConstraintClass(Environment.pfDomCode, this, dom).and(pfun());
    }

	/**
	 * This method creates the "ran" constraint.
	 * @param ran: range of the {@code LMap} on which this method is invoked.
	 *           It is the range of {@code this} logical map.
	 *
	 * @return constructed constraint.
	 */
	@Override
    public @NotNull
    ConstraintClass ran(@NotNull LSet ran){
		Objects.requireNonNull(ran);

    	return new ConstraintClass(Environment.pfRanCode, this, ran).and(pfun());
    }

	/**
	 * This method creates the "comp" constraint which requires that
	 * {@code this comp lMap1 = lMap2}.
	 *
	 * @param lMap1 second argument of the functional composition.
	 * @param lMap2 result of the functional composition.
	 *
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass comp(@NotNull LMap lMap1, @NotNull LMap lMap2){
		Objects.requireNonNull(lMap1);
		Objects.requireNonNull(lMap2);

		return new ConstraintClass(Environment.pfCompCode, this, lMap1, lMap2).and(pfun()).and(lMap1.pfun()).and(lMap2.pfun());
	}
	
}


