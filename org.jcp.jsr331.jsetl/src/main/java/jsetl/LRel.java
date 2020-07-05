package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.InitLObjectException;

import java.util.*;

/**
 * The class {@code LRel} implements logical relations, which are a type of logical sets
 * whose elements are logical pairs. This class provides methods for creating logical relations
 * and relational constraints on them.ss
 */
public class LRel extends LSet implements Cloneable, Visitable{

	///////////////////////////////////////////////////////////////
	//////////////// STATIC METHODS ///////////////////////////////
	///////////////////////////////////////////////////////////////

	//////////////// PUBLIC METHODS //////////////////////////////

	/**
	 * Creates an empty {@code LRel}.
	 *
	 * @return an empty {@code LRel}.
	 */
	public static @NotNull LRel empty(){
		return new LRel(new HashSet<>(0));
	}


	//////////////// PROTECTED METHODS //////////////////////////

	/**
	 * This method checks if the domain of {@code lSet} is ground:
	 * <strong>isDomainGround() = true iff for each x,y:( [x,y] in lSet and isGround(x)) </strong>
	 *
	 * @param lSet logical s whose domain groundness is checked.
	 * @return {@code true} if the domain of {@code lSet} is ground, {@code false} otherwise.
	 */
	protected static boolean isDomainGround(@NotNull LSet lSet){
		assert lSet != null;

		LPair p;
		LSet rest = lSet;
		if(!lSet.isInitialized())
			return false;
		if(!lSet.isClosed())
			return false;
		while(rest.getSize() >= 1){
			p = (LPair)rest.getOne();
			rest = rest.removeOne();
			if(!p.isBound() || !LObject.isGround(p.getFirst()))
				return false;
		}
		return true;
	}


	///////////////////////////////////////////////////////////////
	//////////////// CONSTRUCTORS /////////////////////////////////
	///////////////////////////////////////////////////////////////

	//////////////// PUBLIC CONSTRUCTORS //////////////////////////

	/**
	 * Constructs an unspecified logical relation with default name.
	 */
	public LRel() {
        super();
    }

	/**
	 * Constructs an unspecified logical relation with given name.
	 * @param name name of the logical relation.
	 */
    public LRel(@NotNull String name) {
        super(name);
    }

	/**
	 * Constructs a bound and closed logical relation whose elements are those in {@code set} and with a default name.
	 * @param set a java set whose elements will be the elements of the constructed logical relation.
	 *            It must not contain {@code null} values.
	 * @throws NullPointerException if {@code set} contains {@code null} values.
	 */
    public LRel(@NotNull Set<LPair> set) {
		this(set.isEmpty()? "emptyLRel" : defaultName(), set);
    }

	/**
	 * Constructs a bound and closed logical relation whose elements are those in {@code set} and with a given name.
	 * @param set a java set whose elements will be the elements of the constructed logical relation.
	 *            It must not contain {@code null} values.
	 * @param name the name of the logical relation.
	 * @throws NullPointerException if {@code set} contains {@code null} values.
	 */
    public LRel(@NotNull String name, @NotNull Set<LPair> set) {
		super(name, set);
		assert name != null;
		assert set != null;
	}

	/**
	 * Constructs a logical relation which is equal to the parameter {@code lRel} and with default name.
	 * This constructor is equivalent to solving the constraint {@code this.eq(lRel)}.
	 * @param lRel the logical relation to which the constructed logical relation is equal.
	 */
    public LRel(@NotNull LRel lRel) {
        super(lRel);
        assert lRel != null;
    }

	/**
	 * Constructs a logical relation which is equal to the parameter {@code lRel} and with given name.
	 * This constructor is equivalent to solving the constraint {@code this.eq(lRel)}.
	 * @param name name of the logical relation.
	 * @param lRel the relation to which the constructed logical relation is equal
	 */
    public LRel(@NotNull String name, @NotNull LRel lRel) {
        super(name,lRel);
        assert name != null;
        assert lRel != null;
    }


	///////////// PROTECTED CONSTRUCTORS //////////////////////////

	/**
	 * Constructs a logical relation whose elements are those contained in {@code elements} and whose rest is {@code rest}.
	 * The constructed relation has a default name.
	 * @param elements list containing the known elements for this logical relation.
	 *                 It may be {@code null}, but if it is not it must not contain {@code null} values.
	 * @param rest rest for this logical relation.
	 * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
	 */
    protected LRel(@Nullable ArrayList<LPair> elements, @Nullable LRel rest) {
        super(elements, rest);
    }

	/**
	 * Constructs a logical relation whose elements are those contained in {@code elements} and whose rest is {@code rest}.
	 * The constructed relation has a given name.
	 * @param name name of the logical relation.
	 * @param elements list containing the known elements for this logical relation.
	 *                 It may be {@code null}, but if it is not it must not contain {@code null} values.
	 *                 Moreover, it must only contain instances of {@code LPair}.
	 * @param rest rest for this logical relation.
	 * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
	 */
    protected LRel(@NotNull String name, @Nullable ArrayList<LPair> elements, @Nullable LRel rest) {
        super(name, elements ,rest);
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
	 * Sets the name of this {@code LRel} to {@code name}.
	 *
	 * @param name name for this {@code LRel}.
	 * @return the object on which this method was invoked.
	 */
	@Override
	public @NotNull LRel setName(@NotNull String name){
		Objects.requireNonNull(name);

		super.setName(name);

		return this;
	}


	//////////////// CREATING NEW BOUND LOGICAL SETS /////////////

	/**
	 * Creates and returns a new logical relation obtained from the union of {@code this} with {@code {lPair}}.
	 * The invocation object is not modified.
	 * @param lPair the logical pair to use when creating the new set.
	 * @return returns a (new) logical relation obtained by adding {@code lPair} to this logical relation.
	 */
	public @NotNull LRel ins(@NotNull LPair lPair){
		Objects.requireNonNull(lPair);

		LRel result = (LRel)super.ins(lPair);

		assert result != null;
		return result;
	}

	/**
	 * Creates and returns a new logical relation obtained from this by adding all
	 * elements of the parameter {@code lPairs}. Does not modify this logical relation.
	 * @param lPairs elements to add. None of them can be {@code null}.
	 * @return the constructed logical relation.
	 * @throws NullPointerException if at least one of the elements in {@code lPairs} is {@code null}.
	 */
	public @NotNull LRel ins(@NotNull LPair... lPairs) {
		return insAll(lPairs);
	}

	/**
	 * Creates and returns a new logical relation obtained from this by adding all
	 * elements of the parameter {@code lPairs}. Does not modify this logical relation.
	 * @param lPairs elements to add. None of them can be {@code null}.
	 * @return the constructed logical relation.
	 * @throws NullPointerException if at least one of the elements in {@code lPairs} is {@code null}.
	 */
	public @NotNull LRel insAll(@NotNull LPair[] lPairs) {
		Objects.requireNonNull(lPairs);
		if(Arrays.stream(lPairs).anyMatch(Objects::isNull))
			throw new NullPointerException("NULL VALUES NOT ALLOWED");

		LRel result = (LRel) super.insAll(lPairs);

		assert result != null;
		return result;
	}

	/**
	 * Creates and returns a new logical s obtained from this by adding all
	 * elements of the parameter {@code newElements}. Does not modify this logical relation.
	 * @param newElements collection of elements to add. None of its elements can be {@code null}.
	 * @return the constructed logical relation.
	 * @throws NullPointerException if at least one of the elements in {@code newElements} is {@code null}.
	 */
	public @NotNull LRel insAll(@NotNull ArrayList<LPair> newElements) {
		Objects.requireNonNull(newElements);
		if(newElements.stream().anyMatch(Objects::isNull))
			throw new NullPointerException("NULL VALUES NOT ALLOWED");

		LRel result = (LRel) super.insAll(newElements);

		assert result != null;
		return result;
	}


	//////////////// GENERAL UTILITY METHODS /////////////////////

	/**
	 * Creates a clone of this {@code LRel}.
	 * 
	 * @return LRel clone.
	 */
    @Override
	public @NotNull LRel clone(){
		LRel clone = (LRel) super.clone();

		assert clone != null;
		return clone;
	}

	/**
	 * Gets the value of this {@code LRel} (i.e., a {@code HashSet<LPair>}) object containing all the
	 * elements of this {@code LRel} (ignores the unspecified rest of the {@code LRel},
	 * if present)). The result is {@code null} if and only if this logical relation is not bound.
	 * The returned set does not contain {@code null} values;
	 * @return the value of this logical set.
	 */
	@Override
    @SuppressWarnings("unchecked")
    public @Nullable HashSet<LPair> getValue() {
    	HashSet<Object> aux = (HashSet<Object>) super.getValue();
    	if(aux == null)
    		return null;

    	Iterator<Object> it = aux.iterator();
    	HashSet<LPair> lPairs = new HashSet<LPair>();

    	while (it.hasNext()) {
    			Object object = it.next();
    			if(object instanceof LPair)
    				lPairs.add((LPair) object);
    			else {
					ArrayList<Object> APair = (ArrayList<Object>) object;
					lPairs.add(new LPair(APair.get(0), APair.get(1)));
				}
    	}

    	assert lPairs != null;
    	assert lPairs.stream().noneMatch(Objects::isNull);
    	return lPairs;
    }

	/**
	 * Sets the value of this {@code LRel} to the parameter java set.
	 * This relation becomes bound and closed.
	 * Calling this method is equivalent to solving the constraint {@code this.eq(s)}.
	 *
	 * @param set the set containing all elements of this logical relation. It must not contain {@code null} values.
	 *            It is required that each element of {@code set} is a {@code LPair}.
	 * @return this {@code LRel} modified.
	 * @throws NullPointerException if at least one of the elements of {@code set} is {@code null}.
	 * @throws IllegalArgumentException if at least one
	 * element of {@code set} is not {@code null} an instance of {@code LPair}.
	 * @throws InitLObjectException if this logical relation is already bound.
	 */
	@Override
	public @NotNull LRel setValue(@NotNull Set<?> set){
    	Objects.requireNonNull(set);
    	if(set.stream().anyMatch(Objects::isNull))
			throw new NullPointerException("NULL VALUES NOT ALLOWED");

    	if(!(set.stream().allMatch(element -> element instanceof LPair)))
			throw new IllegalArgumentException("LPAIR ONLY ALLOWED");

    	LRel result = (LRel) super.setValue(set);
    	assert result != null;
    	return result;
	}


	///////////////////////////////////////////////////////////////
	//////////////// PROTECTED METHODS ////////////////////////////
	///////////////////////////////////////////////////////////////

	//////////////// IMPLEMENTED ABSTRACT METHODS ///////////////

	/**
	 * Creates and returns a new logical relation whose elements are those contained in {@code elements} and rest is {@code rest}.
	 * @param elements list containing elements for this logical set.
	 *                 It may be {@code null}, but if it is not it must not contain {@code null} values.
	 *                 Moreover, it can only contain instances of {@code LPair}.
	 * @param rest rest for this logical set. It must be an instance of {@code LRel}.
	 * @return the constructed logical relation.
	 * @throws NullPointerException if {@code elements} is not {@code null} and contains {@code null} values
	 * or values that are not instances of {@code LPair}.
	 */
	@Override
	protected @NotNull LRel createObj(@Nullable ArrayList<?> elements, @Nullable LCollection rest) {
		if(elements != null && elements.stream().anyMatch(Objects::isNull))
			throw new NullPointerException("NULL VALUES NOT ALLOWED");

		ArrayList<LPair> lPairs = new ArrayList<>(elements.size());
		elements.forEach(element -> {
			if(element instanceof LVar)
				lPairs.add((LPair)(((LVar) element).getValue()));
			else
				lPairs.add((LPair) element);

		});
		LRel lRel = new LRel(lPairs, null);
		lRel.rest = rest;
		return lRel;
	}


	//////////////// LOGICAL SET METHODS ///////////////////////

	/**
	 * Constructs and returns a new logical relation which has the same elements as {@code this},
	 * except for an arbitrary one, and the same tail.
	 * @return the constructed logical relation.
	 */
    @Override
    protected @NotNull LRel removeOne(){
    	LRel result = new LRel();
    	result.equ = super.removeOne();

    	assert  result != null;
    	return result;
    }


	////////////////////////////////////////////////////////////
	//////////// PUBLIC CONSTRAINT METHODS /////////////////////
	////////////////////////////////////////////////////////////

	//////////// POSITIVE RELATIONAL CONSTRAINTS //////////////

    /**
     * This method creates the "dom" constraint.
     * @param  dom: domain of the {@code LRel} on which this method is invoked.
	 *            It is the domain of {@code this} relation.
     * 
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass dom(@NotNull LSet dom){
    	Objects.requireNonNull(dom);

    	return new ConstraintClass(Environment.brDomCode, this, dom);
    }
    
    /**
     * This method creates the "ran" constraint.
     * @param ran: range of the {@code LRel} on which this method is invoked.
	 *           It is the range of {@code this} relation.
     * 
     * @return constructed constraint.
     */
    public @NotNull
    ConstraintClass ran(@NotNull LSet ran){
    	Objects.requireNonNull(ran);

    	return new ConstraintClass(Environment.brRanCode, this, ran);
    }   
    
    /**
     * This method creates the "comp" constraint which requires that
	 * {@code this comp lRel1 = lRel2}.
     * 
     * @param lRel1 second argument of the relational composition.
     * @param lRel2 result of the relational composition.
     * 
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass comp(@NotNull LRel lRel1, @NotNull LRel lRel2){
    	Objects.requireNonNull(lRel1);
    	Objects.requireNonNull(lRel2);

    	return new ConstraintClass(Environment.brCompCode, this, lRel1, lRel2);
    }

	/**
	 * This method creates an "id" constraint. The constructed constraint requires
	 * that {@code this} is the identity relation over the logical set {@code lSet}.
	 *
	 * @param lSet a logical set.
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass id(@NotNull LSet lSet){
	 	Objects.requireNonNull(lSet);

		return new ConstraintClass(Environment.idCode, this, lSet);
	}

	/**
	 * This method creates an "inv" constraint which requires that
	 * {@code lRel} is the inverse relation of {@code this}.
	 *
	 * @param lRel the inverse relation of {@code this}.
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass inv(@NotNull LRel lRel){
		Objects.requireNonNull(lRel);

		return new ConstraintClass(Environment.invCode, this, lRel);
	}

	/**
	 * This method creates an "id" constraint, which requires
	 * that {@code this} is a identity relation over some logical set.
	 *
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass id(){
		LSet a = new LSet();

		ConstraintClass domr = this.dom(a);
		ConstraintClass ranr = this.ran(a);
		ConstraintClass compr = this.comp(this, this);
		ConstraintClass returned = domr.add(ranr).add(compr);

		assert returned != null;
		return returned;
	}

	/**
	 * This method creates a "dres" constraint of the form {@code dres(this, lSet, lRel)}.
	 *
	 * @param lSet domain restriction of the "dres" constraint.
	 *
	 * @param lRel result of the "dres".
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass dres(@NotNull LSet lSet, @NotNull LRel lRel){
		Objects.requireNonNull(lRel);
		Objects.requireNonNull(lSet);

		LSet N3 = new LSet();
		LSet N2 = new LSet();
		LRel N1 = new LRel();

		ConstraintClass dom1 = lRel.dom(N2);
		ConstraintClass dom2 = N1.dom(N3);
		ConstraintClass un = lRel.union(N1, this);
		ConstraintClass sub = N2.subset(lSet);
		ConstraintClass disj = lSet.disj(N3);

		ConstraintClass returned = dom1.and(dom2).and(un).and(sub).and(disj);

		assert returned != null;
		return returned;
	}

	/**
	 * This method creates a "rres" constraint of the form {@code rres(this, lSet, lRel)}.
	 *
	 * @param lSet range restriction of the "rres" constraint.
	 *
	 * @param lRel result of the "rres".
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass rres(@NotNull LSet lSet, @NotNull LRel lRel){
		Objects.requireNonNull(lRel);
		Objects.requireNonNull(lSet);

		LSet N3 = new LSet();
		LSet N2 = new LSet();
		LRel N1 = new LRel();

		ConstraintClass ran1 = lRel.ran(N2);
		ConstraintClass ran2 = N1.ran(N3);
		ConstraintClass un = this.union(N1, lRel);
		ConstraintClass sub = N2.subset(lSet);
		ConstraintClass disj = lSet.disj(N3);

		ConstraintClass returned = ran1.and(ran2).and(un).and(sub).and(disj);

		assert returned != null;
		return returned;
	}

	//////////// NEGATIVE RELATIONAL CONSTRAINTS //////////////

	/**
	* This method creates a "ndom" constraint, which requires
	* that {@code lSet} is not the domain {@code this} relation.
	*
	* @param lSet set which must not be the domain of {@code this} relation.
	* @return the constructed constraint.
	*/
	public @NotNull
    ConstraintClass ndom(@NotNull LSet lSet){
		Objects.requireNonNull(lSet);

		LVar n1 = new LVar();
		LVar n2 = new LVar();
		LPair p = new LPair(n1, n2);
		LRel m = LRel.empty().ins(new LPair(n1,n1));
		LRel empty = LRel.empty();

		ConstraintClass part1 = p.in(this).and(n1.nin(lSet));
		ConstraintClass part2 = n1.in(lSet).and(m.comp(this, empty));

		ConstraintClass returned = part1.or(part2);

		assert returned != null;
		return returned;
	}

	/**
	 * This method creates a "nran" constraint, which requires
	 * that {@code lSet} is not the range {@code this} relation.
	 *
	 * @param lSet set which must not be the range of {@code this} relation.
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass nran(@NotNull LSet lSet){
		Objects.requireNonNull(lSet);

		LVar n1 = new LVar();
		LVar n2 = new LVar();

		LPair p = new LPair(n1, n2);
		LRel m = LRel.empty().ins(new LPair(n1,n1));
		LRel empty = LRel.empty();

		ConstraintClass part1 = p.in(this).and(n2.nin(lSet));
		ConstraintClass part2 = n1.in(lSet).and(m.comp(this, empty));

		ConstraintClass returned = part1.or(part2);

		assert returned != null;
		return returned;
	}

	/**
	 * This method creates an "ninv" constraint, which requires
	 * that {@code lRel} is not the inverse of {@code this} relation.
	 *
	 * @param lRel relation which must not be the inverse of {@code this} relation.
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass ninv(@NotNull LRel lRel){
		Objects.requireNonNull(lRel);

		LVar n1 = new LVar();
		LVar n2 = new LVar();
		LPair p1 = new LPair(n1, n2);
		LPair p2 = new LPair(n2, n1);

		ConstraintClass part1 = p1.in(this).and(p2.nin(lRel));
		ConstraintClass part2 = p1.nin(this).and(p2.in(lRel));

		ConstraintClass returned = part1.or(part2);

		assert returned != null;
		return returned;
	}

	/**
	 * This method creates a "nid" constraint, which requires
	 * that {@code this} relation is not the identity over the set {@code lSet}.
	 *
	 * @param lSet a logical set.
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass nid(@NotNull LSet lSet){
		Objects.requireNonNull(lSet);

		LVar n1 = new LVar();
		LVar n2 = new LVar();
		LPair p1 = new LPair(n1, n1);
		LPair p2 = new LPair(n2, n1);

		ConstraintClass part1 = n1.in(lSet).and(p1.nin(this));
		ConstraintClass part2 = n1.nin(lSet).and(p1.in(this));
		ConstraintClass part3 = n1.neq(n2).and(p2.in(this));

		ConstraintClass returned = part1.or(part2).or(part3);

		assert returned != null;
		return returned;
	}

	/**
	 * This method creates a "ncomp" constraint, which requires
	 * that {@code this comp lRel1 != lRel2}.
	 *
	 * @param lRel1 second argument of the not composition constraint.
	 * @param lRel2 third argument of the not composition constraint.
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass ncomp(@NotNull LRel lRel1, @NotNull LRel lRel2){
		Objects.requireNonNull(lRel1);
		Objects.requireNonNull(lRel2);

		LVar n1 = new LVar();
		LVar n2 = new LVar();
		LVar n3 = new LVar();
		LPair p1 = new LPair(n1, n2);
		LPair p2 = new LPair(n2, n3);
		LPair p3 = new LPair(n1, n3);
		LRel m1 = LRel.empty().ins(new LPair(n1, n1));
		LRel m2 = LRel.empty().ins(new LPair(n3, n3));
		LRel N1 = new LRel();
		LRel N2 = new LRel();
		LRel empty = LRel.empty();

		ConstraintClass part1 = p1.in(this).
							and(p2.in(lRel1)).and(p3.nin(lRel2));

		ConstraintClass part2 = p3.in(lRel2).and(m1.comp(this, N1)).
							and(lRel1.comp(m2, N2)).and(N1.comp(N2, empty));

		ConstraintClass returned = part1.or(part2);

		assert returned != null;
		return returned;
	}

	/**
	 * This method creates a "ndres" constraint of the form {@code ndres(this, lSet, lRel)}.
	 *
	 * @param lSet domain restriction of the "ndres" constraint.
	 * @param lRel different than the result of the domain restriction.
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass ndres(@NotNull LSet lSet, @NotNull LRel lRel){
		Objects.requireNonNull(lRel);
		Objects.requireNonNull(lSet);

		LRel b = new LRel();

		ConstraintClass dre = dres(lSet, b);
		ConstraintClass dif = this.diff(b, lRel);
		ConstraintClass un = lSet.union(b, lRel);

		ConstraintClass returned = dre.add(dif);

		assert returned != null;
		return returned;
	}

	/**
	 * This method creates a "nrres" constraint of the form {@code nrres(this, lSet, lRel)}.
	 *
	 * @param lSet range restriction of the "nrres" constraint.
	 * @param lRel different than the result of the range restriction.
	 * @return the constructed constraint.
	 */
	public @NotNull
    ConstraintClass nrres(@NotNull LSet lSet, @NotNull LRel lRel){
		Objects.requireNonNull(lRel);
		Objects.requireNonNull(lSet);

		LRel a = new LRel();

		ConstraintClass rre = this.rres(a, lRel);
		ConstraintClass dif = this.diff(a, lRel);

		ConstraintClass returned = rre.add(dif);

		assert returned != null;
		return returned;
	}

}
