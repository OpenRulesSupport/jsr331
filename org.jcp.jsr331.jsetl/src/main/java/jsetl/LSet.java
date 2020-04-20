package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.InitLObjectException;

import java.util.*;

/**
 * This class implements logical sets, i.e. a type of logical collection in which the order and repetition of elements don't matter.
 * logical sets can be completely specified (all their elements are known),
 * partially specified (some of their elements are unknown or there is an unspecified rest)
 * or completely unspecified.
 * Elements of logical sets need not be of the same type.
 * Elements of logical sets may be other logical objects (even logical sets).
 * logical objects which are elements of logical sets may be unspecified.
 */
public class LSet extends LCollection 
                  implements Cloneable, Visitable {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates and returns an empty logical set (completely specified).
     * @return the empty logical set.
     */
    public static @NotNull LSet empty() {
        return new LSet(new HashSet<>(0));
    }

    /**
     * Constructs and returns a closed logical set containing {@code numberOfElements} unbound {@code LVars}.
     * @param numberOfElements number of unbound {@code LVars}. Must be greater than or equal to zero.
     * @return the constructed logical set.
     * @throws IllegalArgumentException if {@code numberOfElements} is negative.
     */
    public static @NotNull LSet mkSet(int numberOfElements) {
        if(numberOfElements < 0)
            throw new IllegalArgumentException("NEGATIVE NUMBER OF ELEMENTS");

        ArrayList<Object> list = new ArrayList<>(numberOfElements);

        for (int i = 0; i < numberOfElements; i++) {
            LVar lvar = new LVar();
            list.add(lvar);
        }

        LSet set = new LSet(list,LSet.empty());
        return set;
    }

    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// PUBLIC CONSTRUCTORS //////////////////////////

    /**
     * Constructs an unspecified logical set with default name.
     */
    public LSet() {
        super();
    }

    /**
     * Constructs an unspecified logical set with given name.
     * @param name name of the logical set.
     */
    public LSet(@NotNull String name) {
        super(name);
    }

    /**
     * Constructs a bound and closed logical set whose elements are those in {@code set} and with a default name.
     * @param set a java set whose elements will be the elements of the constructed logical set.
     *            It must not contain {@code null} values.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public LSet(@NotNull Set<?> set) {         // equivalent to solving the constraint this.eq(s)
        this(set.size() == 0 ? "emptyLSet" : defaultName(), set);
    }

    /**
     * Constructs a bound and closed logical set whose elements are those in {@code set} and with a given name.
     * @param set a java set whose elements will be the elements of the constructed logical set.
     *            It must not contain {@code null} values.
     * @param name the name of the logical set.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public LSet(@NotNull String name, @NotNull Set<?> set) {
        super(
                name,
                set.size() == 0 ? null : new ArrayList<>(set),
                set.size() == 0 ? null : LSet.empty());

        this.initialized = true;
    }

    /**
     * Constructs a logical set which is equal to the parameter {@code lSet} and with default name.
     * This constructor is equivalent to solving the constraint {@code this.eq(lSet)}.
     * @param lSet the logical set to which the constructed logical set is equal.
     */
    public LSet(@NotNull LSet lSet) {
        this(defaultName(), lSet);
    }

    /**
     * Constructs a logical set which is equal to the parameter {@code set} and with given name.
     * This constructor is equivalent to solving the constraint {@code this.eq(lSet)}.
     * @param name name of the logical set.
     * @param lSet the set to which the constructed logical set is equal.
     */
    public LSet(@NotNull String name, @NotNull LSet lSet) {
        super(name);

        Objects.requireNonNull(lSet);
        this.initialized = true;
        this.equ   = lSet;
    }


    ///////////// PROTECTED CONSTRUCTORS //////////////////////////

    /**
     * Constructs a logical set whose elements are those contained in {@code elements} and whose rest is {@code rest}.
     * The constructed set has a default name.
     * @param elements list containing the known elements for this logical set. It may be {@code null},
     *                 but if it is not it must not contain {@code null} values.
     * @param rest rest for this logical set.
     * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
     */
    protected LSet(@Nullable ArrayList<?> elements, @Nullable LSet rest) {
        super(elements,rest);
    }

    /**
     * Constructs a logical set whose elements are those contained in {@code elements} and whose rest is {@code rest}.
     * The constructed set has a given name.
     * @param name name of the logical set.
     * @param elements list containing the known elements for this logical set. It may be {@code null},
     *                 but if it is not it must not contain {@code null} values.
     * @param rest rest for this logical set.
     * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
     */
    protected LSet(@NotNull String name, @Nullable ArrayList<?> elements, @Nullable LSet rest) {
        super(name,elements,rest);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// INTERFACE METHODS IMPLEMENTATIONS /////////////////////

    /**
     * Implementation of this method is needed for the Visitor design pattern.
     * @param visitor the visitor that wants to visit the object.
     * @return the result of the call to {@code visitor.visit(this)}.
     */
    @Override
    public @Nullable Object accept(@NotNull Visitor visitor){
        Objects.requireNonNull(visitor);
        return visitor.visit(this);
    }


    //////////////// SETTERS /////////////////////////////////////

    /**
     * Sets the name of this logical object and returns {@code this} logical set.
     * @param name the new name for this logical set.
     * @return {@code this} logical set modified as above.
     */
    @Override
    public @NotNull LSet setName(@NotNull String name) {
        Objects.requireNonNull(name);

        LSet result =  (LSet) super.setName(name);

        assert result == this;
        return result;
    }


    //////////////// CREATING NEW BOUND LOGICAL SETS /////////////

    /**
     * Creates and returns a new logical set obtained from the union of {@code this} with {@code {object}}.
     * The invocation object is not modified.
     * @param objects the object to use when creating the new set.
     * @return returns a (new) logical set obtained by adding {@code object} to this logical set.
     * @throws NullPointerException if {@code objects} contains {@code null} values.
     */
    @Override
    public @NotNull LSet ins(@NotNull Object... objects) {
        LSet result = insAll(objects);

        assert result != null;
        return  result;
    }

    /**
     * Creates and returns a new logical set obtained from this by adding all
     * elements of the parameter {@code objects}. Does not modify this logical object.
     * @param objects array of elements to add. None of its elements can be {@code null}.
     * @return the constructed logical object.
     * @throws NullPointerException if at least one of the elements in {@code objects} is {@code null}.
     */
    @Override
    public @NotNull LSet insAll(@NotNull Object[] objects) {
        Objects.requireNonNull(objects);
        if(Arrays.stream(objects).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        LSet result = (LSet) super.insAll(objects);

        assert result != null;
        return result;
    }

    /**
     * Creates and returns a new logical set obtained from this by adding all
     * elements of the parameter {@code newElements}. Does not modify this logical set.
     * @param newElements collection of elements to add. None of its elements can be {@code null}.
     * @return the constructed logical set.
     * @throws NullPointerException if at least one of the elements in {@code newElements} is {@code null}.
     */
    @Override
    public @NotNull LSet insAll(@NotNull Collection<?> newElements) {
        Objects.requireNonNull(newElements);
        if(newElements.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        LSet result = (LSet) super.insAll(newElements);

        assert result != null;
        return result;
    }


    //////////////// GENERAL UTILITY METHODS /////////////////////

    /**
     * Creates and returns a copy of this logical set.
     * This clone has a shallow copy of the list of elements and rest but does not share them directly
     * with the original logical s, which means that an addition of an element to the cloned logical set
     * doesn't add them to the original.
     * @return a clone of this object.
     */
    @Override
    public @NotNull LSet clone() {
        LSet clone = (LSet) super.clone();

        assert clone != null;
        return clone;
    }

    /**
     * Checks whether this {@code LSet} is equal to the specified {@code Object}.
     * The objects are equal when:
     * - they are the same object or reference to the same object,
     *   even if the object or the reference are not initialized,
     * - they contain equal objects or they contain references to
     *   equal objects (order does not matter!)
     * (ignores the unspecified rest of the LSet if present)
     *
     * @param object the tested object.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(@Nullable Object object) {
        if(object == null)
            return false;
        if(object == this)
            return true;
        if(object instanceof LSet) return this.equalsLSet((LSet)object);
        else if (this.equ == null)
            if (!this.initialized) return false;
            else return this.getValue().equals(object);
        else
            return this.equ.equals(object);
    }

    /**
     * Gets the value of this {@code LSet} (i.e., a {@code HashSet} object containing all the
     * elements of this {@code LSet} (ignores the unspecified rest of the {@code LSet},
     * if present)). The result is {@code null} if and only if this logical set is not bound.
     * The returned set does not contain {@code null} values;
     * @return the value of this logical set.
     */
    public @Nullable HashSet<?> getValue() {
        HashSet<?> value;
        if (this.getEqu() == null)
            if (!initialized)
                value = null;
            else
                value = this.value();


        else
            value = this.getEndOfEquChain().getValue();

        assert !this.isBound() && value == null
                || this.isBound() && value != null && value.stream().allMatch(Objects::nonNull);
        return value;
    }

    /**
     * Sets the value of this {@code LSet} to the parameter java set.
     * This set becomes bound and closed.
     * Calling this method is equivalent to solving the constraint {@code this.eq(s)}.
     *
     * @param set the set containing all elements of this logical set. It must not contain {@code null} values.
     * @return this {@code LSet} modified.
     * @throws NullPointerException if at least one of the elements of {@code set} is {@code null}.
     * @throws InitLObjectException if this logical set is already bound.
     */
    public @NotNull LSet setValue(@NotNull Set<?> set) {
        Objects.requireNonNull(set);
        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        this.rest = LSet.empty();
        LSet result = (LSet) super.setValue(set);

        assert result != null;
        return result;
    }

    /**
     * Returns a string corresponding to the logical set value.
     * If this logical set is initialized this method converts its value to a string,
     * otherwise it generates its name with "_" as prefix in order to distinguish its name
     * from each string or character used as variable values.
     *
     * Each initialized collection are of the form {} or {a1,a2,...,an} or {a1,a2,...,an|r} where
     * r is the result of {@code toString()} to the rest of this collection, if r is not empty.
     *
     * @return the computed string representation of this logical set.
     */
    @Override
    public @NotNull String toString() {
        String string;

        if(this.equ == null) {
            LSet normalizeSet = this.normalizeSet();
            string = normalizeSet.toStringCollection();
        }else{
            string =  this.getEndOfEquChain().toString();
        }

        assert string != null;
        return string;
    }

    /**
     * Prints all the (known) elements of this logical set to standard output.
     * @param c character used between elements to separate them from each other.
     */
    @Override
    public void printElems(char c) {
        LSet tmp = this.normalizeSet();
        tmp.printElemsCollection(c);
        return;
    }


    //////////////// LOGICAL COLLECTION METHODS /////////////////////

    /**
     * Tests if {@code object} is part of this collection (an element, the tail, or a part of an element).
     * @param object object to search deeply inside this logical collection.
     * @return {@code true} if {@code object} is an element of this collection, {@code false} otherwise.
     * @author Andrea Fois
     */
    @Override
    public boolean occurs(@NotNull Object object){
        Objects.requireNonNull(object);
        LSet removedTail = LSet.empty().insAll(this.toArrayList());
        return removedTail.testOccursLCollection(object);
    }

    /**
     * Gets the tail of this {@code LSet}. The tail can be either
     * the empty logical set (closed logical set) or an uninitialized
     * {@code LSet} (open logical set).
     * @return the tail of this logical set.
     */
    @Override
    public @Nullable LSet getTail() {
        return (LSet)super.getTail();
    }

    /**
     * Returns the number of distinct elements of this logical set
     * (0 for the empty or uninitialized logical set).
     * Ignores the unspecified tail of this logical set, if present.
     * @return the size of this logical set.
     */
    @Override
    public int getSize() {
        LSet tmp = this.normalizeSet();
        return tmp.countAllElements();
    }

    /**
     * Creates and returns a logical set which has the same elements (without duplicates) and tail as {@code this}.
     * Does not modify {@code this} logical set.
     * @return the constructed logical set.
     */
    public @NotNull LSet normalizeSet() {
        LSet normalizedSet;

        if (this.equ == null) {
            if (!this.initialized) normalizedSet = this;
            else if (this.isBoundAndEmpty()) normalizedSet = this.clone(); //LSet.empty();
            else {
                ArrayList<Object> thisElements = this.toArrayList();
                ArrayList<Object> normalizedLSetElements = new ArrayList<>(thisElements.size());
                for(int i = 0; i < thisElements.size(); ++i){
                    Object element = thisElements.get(i);
                    if(thisElements.subList(0, i).stream().noneMatch(otherElement -> LObject.equals(otherElement, element)))
                        normalizedLSetElements.add(element);
                }
                normalizedSet = this.getTail().insAll(normalizedLSetElements);
            }
        }
        else
            normalizedSet = getEndOfEquChain().normalizeSet();

        assert normalizedSet != null;
        return normalizedSet;
    }




    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// IMPLEMENTED ABSTRACT METHODS /////////////////////

    /**
     * Creates and returns a new logical set whose elements are those contained in {@code elements} and rest is {@code rest}.
     * @param elements list containing elements for this logical set.
     *                 It may be {@code null}, but if it is not it must not contain {@code null} values.
     * @param rest rest for this logical set. It must be an instance of {@code LSet}.
     * @return the constructed logical set.
     * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
     */
    @Override
    protected LSet createObj(@Nullable ArrayList<?> elements, @Nullable LCollection rest) {
        return new LSet(elements, (LSet) rest);
    }


    //////////////// GETTERS AND SETTERS /////////////////////////////

    /**
     * Returns the value of the {@code equ} field, thus making a step in the {@code equ} chain.
     * @return the value of the {@code equ} field.
     */
    @Override
    protected @Nullable LSet getEqu() {
        return (LSet) super.equ;
    }

    /**
     * Follows the {@code equ} chain and returns its last node.
     * @return the last node of the equ chain
     */
    @Override
    protected @NotNull LSet getEndOfEquChain(){
        LSet endOfEquChain = (LSet) super.getEndOfEquChain();
        return endOfEquChain;
    }


    ///////////// GENERAL UTILITY METHODS ///////////////

    /**
     * Gets the value of this {@code LSet} as a {@code HashSet}.
     * The returned set does not contain {@code null} values.
     * Ignores the unspecified tail, if present.
     * @return the value of this {@code LSet}.
     */
    protected @NotNull HashSet<Object> value() {

        if(equ != null)
            return getEndOfEquChain().value();

        HashSet<Object> newSet = new HashSet<>();
        for(Object elem : this)
            if (elem instanceof LVar)
                newSet.add(((LVar)elem).value());
            else if (elem instanceof LCollection)
                if (((LCollection)elem).isInitialized())
                    newSet.add(((LCollection)elem).value());
                else newSet.add(elem);
            else
                newSet.add(elem);

        assert newSet != null;
        assert newSet.stream().noneMatch(Objects::isNull);
        return newSet;
     }

 
    ///////////// LOGICAL COLLECTION METHODS ////////////////////

    /**
     *  Constructs and returns a logical set which is the concatenation of this logical set and {@code lSet}.
     *  @param lSet the set to append.
     *  @return the constructed set.
     */
    protected @NotNull LSet appendGround(@NotNull LSet lSet) {
        assert lSet != null;

        LSet result;
        if (this.equ == null && lSet.getEqu() == null) {
            if (this.isBoundAndEmpty())
                result = lSet;
            else{
                result = new LSet(this.toArrayList(), lSet);
            }
        }
        else if (this.equ == null)
            result = this.concatElems(lSet.getEqu());
        else
            result = this.getEndOfEquChain().concatElems(lSet);

        assert result != null;
        return result;
    }



    /**
     * Constructs and returns a new logical set which has the same elements as {@code this}
     * except for an arbitrary one and the same tail.
     * @return the constructed logical set.
     */
    @Override
    protected @NotNull LSet removeOne() {
        LSet result = (LSet) super.removeOne();

        assert result != null;
        return result;
    }

    /**
     * Default open delimiter for {@code LSet}.
     * @return the default open delimiter for logical sets.
     */
    @Override
    protected @NotNull Character openDelimitator() {
        return '{';
    }

    /**
     * Default close delimiter for {@code LSet}.
     * @return a character which is the default close delimiter for logical sets.
     */
    @Override
    protected @NotNull Character closeDelimitator() {
        return '}';
    }

    /**
     * Default rest delimiter for {@code LSet}.
     * @return a character which is the default rest delimiter for logical sets.
     */
    @Override
    protected @NotNull Character restDelimitator() {
        return '/';
    }



    ////////////////////////////////////////////////////////////
    //////////// PRIVATE METHODS ///////////////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * Creates a new logical set which has all elements from {@code this} and {@code lSet} and the same tail as {@code lSet}
     * @param lSet second parameter of the concatenation of logical sets.
     * @return the constructed logical set.
     */
    private @NotNull LSet concatElems(@NotNull LSet lSet) {
        assert lSet != null;

        LSet concatenated = (LSet) super.concat(lSet);

        assert concatenated != null;
        return concatenated;
    }

    /**
     * Calls {@code LCollection#printElemsCollection(delimitator)}.
     * @param delimitator character used to separate elements from each other in the output.
     * @see LCollection#printElems(char).
     */
    private void printElemsCollection(char delimitator) {
        super.printElems(delimitator);
    }

    /**
     * Returns the result of {@code LCollection#toString()} over this object.
     * @return the result of {@code LCollection#toString()} over this object.
     * @see LCollection#toString()
     */
    private @NotNull String toStringCollection() {
        String string = super.toString();
        assert string != null;
        return string;
    }

    /**
     * Checks whether this {@code LSet} is equal to the specified {@code LSet}.
     * The {@code LSet}s are equal when:
     * - they are the same object or reference to the same object,
     *   even if the object or the reference are not initialized,
     * - they contain equal objects or they contain references to
     *   equal objects (order does not matter!)
     * (ignores the unspecified rest of the LSet if present)
     *
     * @param lSet the other logical set.
     * @return {@code true} if the logical sets are equal, {@code false} otherwise.
     */
    private boolean equalsLSet(@NotNull LSet lSet) {
        assert lSet != null;

        if (this.equ == null && (lSet).getEqu() == null) {
            if (this == lSet) return true;
                // true if the sets are the same reference.
                // note that the result is true even if the
                // sets are the same ref but they are not initialized
            else if(!this.isInitialized() || !lSet.isInitialized()) return false;
                // false if one or both the sets are not initialized
            else if (this.isBoundAndEmpty() && lSet.isBoundAndEmpty()) return true;
                // true if the sets are both the empty s
            else if (this.isBoundAndEmpty() && !lSet.isBoundAndEmpty()) return false;

            else if(!this.isBoundAndEmpty() && lSet.isBoundAndEmpty()) return false;
            else if(lSet.getTail() instanceof Ris && !lSet.getTail().equals(this.getTail()))
                return false;
            else if(!(lSet.getTail() instanceof Ris) && !this.getTail().equals(lSet.getTail()))
                return false;
            else {
                ArrayList<?> thisLSetElements = this.toArrayList();
                ArrayList<?> otherLSetElements = lSet.toArrayList();
                for (Object thisLSetElement: thisLSetElements)
                    if(otherLSetElements.stream().noneMatch(otherLSetElement -> LObject.equals(otherLSetElement, thisLSetElement)))
                        return false;
                for(Object otherLSetElement : otherLSetElements)
                    if(thisLSetElements.stream().noneMatch(thisLSetElement -> LObject.equals(thisLSetElement, otherLSetElement)))
                        return false;

                return true;
            }
        } else if (this.equ != null) return this.getEndOfEquChain().equals(lSet);
        else return this.equals(lSet.getEndOfEquChain());
    }

    /**
     * Returns the result of the call to the method {@code LCollection#occurs} with {@code object} as parameter.
     * @param object the parameter to pass to the super method.
     * @return the result of the call to the super method.
     */
    private boolean testOccursLCollection(@NotNull Object object){
        assert object != null;
        return super.occurs(object);
    }


    ////////////////////////////////////////////////////////////
    //////////// PUBLIC CONSTRAINT METHODS /////////////////////
    ////////////////////////////////////////////////////////////

    //////////// (IN)EQUALITY CONSTRAINTS //////////////////////

    /**
     * Constructs and returns a new constraint which demands that {@code this} is equal to {@code lSet}.
     * @param lSet second parameter of the constraint.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass eq(@NotNull LSet lSet){
        Objects.requireNonNull(lSet);

        return new ConstraintClass("_eq", this, lSet);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is equal to {@code set}
     * @param set second parameter of the constraint. It must not contain {@code null} values.
     * @return the constructed constraint.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass eq(@NotNull Set<?> set){
        Objects.requireNonNull(set);
        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass("_eq", this, set);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is not equal to {@code lSet}.
     * @param lSet second parameter of the constraint.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass neq(@NotNull LSet lSet){
        Objects.requireNonNull(lSet);

        return new ConstraintClass("_neq", this, lSet);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is not equal to {@code set}
     * @param set second parameter of the constraint. It must not contain {@code null} values.
     * @return the constructed constraint.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass neq(@NotNull Set<?> set){
        Objects.requireNonNull(set);
        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass("_neq", this, set);
    }


    //////////// MEMBERSHIP CONSTRAINTS ///////////////////////////

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code object} is an element of {@code this}.
     * @param object object who should be an element of this set.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass contains(@NotNull Object object) {
        Objects.requireNonNull(object);

        return new ConstraintClass(Environment.inCode, object, this);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code object} is not an element of {@code this}.
     * @param object object who should not be an element of this set.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass ncontains(@NotNull Object object) {
        Objects.requireNonNull(object);

        return new ConstraintClass(Environment.ninCode, object, this);
    }


    ////////// SET-THEORETICAL POSITIVE CONSTRAINTS //////////////////////

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - lSet1 == lSet2}.
     * @param lSet1 second argument of set difference.
     * @param lSet2 result of set difference.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass diff(@NotNull LSet lSet1, @NotNull LSet lSet2) {
        Objects.requireNonNull(lSet1);
        Objects.requireNonNull(lSet2);

        return new ConstraintClass(Environment.diffCode, this, lSet1, lSet2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - lSet == set}.
     * @param lSet second argument of set difference.
     * @param set result of set difference. It must not contain {@code null values}.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass diff(@NotNull LSet lSet, @NotNull Set<?> set) {
        Objects.requireNonNull(lSet);
        Objects.requireNonNull(set);
        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.diffCode, this, lSet, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - set == lSet}.
     * @param set second argument of set difference. It must not contain {@code null} values.
     * @param lSet result of set difference.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass diff(@NotNull Set<?> set, @NotNull LSet lSet) {
        Objects.requireNonNull(lSet);
        Objects.requireNonNull(set);
        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.diffCode, this, set, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - set1 == set2}.
     * @param set1 second argument of set difference. It must not contain {@code null} values.
     * @param set2 result of set difference. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set1} or {@code set2} contain {@code null} values.
     */
    public @NotNull
    ConstraintClass diff(@NotNull Set<?> set1, @NotNull Set<?> set2) {
        Objects.requireNonNull(set1);
        Objects.requireNonNull(set2);

        if(set1.stream().anyMatch(Objects::isNull) || set2.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.diffCode, this, set1, set2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this} and {@code lSet} are disjoint.
     * Note that two sets are said to be disjoint if and only if they have no element in common.
     * @param lSet the set which should be disjoint to {@code this}.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass disj(@NotNull LSet lSet) {
        Objects.requireNonNull(lSet);

        return new ConstraintClass(Environment.disjCode, this, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this} and {@code set} are disjoint.
     * Note that two sets are said to be disjoint if and only if they have no element in common.
     * @param set the set which should be disjoint to {@code this}. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass disj(@NotNull Set<?> set) {
        Objects.requireNonNull(set);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.disjCode, this, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this intersection lSet1 == lSet2}.
     * This means that {@code lSet2} is equal to the intersection of {@code this} and {@code lSet1}, i.e.,
     * for all x: x in {@code lSet2} iff x in {@code this} and x in {@code lSet1}.
     * @param lSet1 second argument of set intersection.
     * @param lSet2 result of set intersection.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass inters(@NotNull LSet lSet1, @NotNull LSet lSet2) {
        Objects.requireNonNull(lSet1);
        Objects.requireNonNull(lSet2);

        return new ConstraintClass(Environment.intersCode, this, lSet1, lSet2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this intersection lSet == set}.
     * This means that {@code set} is equal to the intersection of {@code this} and {@code lSet}, i.e.,
     * for all x: x in {@code set} iff x in {@code this} and x in {@code lSet}.
     * @param lSet second argument of set intersection.
     * @param set result of set intersection. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass inters(@NotNull LSet lSet, @NotNull Set<?> set) {
        Objects.requireNonNull(lSet);
        Objects.requireNonNull(set);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.intersCode, this, lSet, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this intersection set == lSet}.
     * This means that {@code lSet} is equal to the intersection of {@code this} and {@code set}, i.e.,
     * for all x: x in {@code lSet} iff x in {@code this} and x in {@code set}.
     * @param set second argument of set intersection. It must not contain {@code null} values.
     * @param lSet result of of set intersection.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass inters(@NotNull Set<?> set, @NotNull LSet lSet) {
        Objects.requireNonNull(set);
        Objects.requireNonNull(lSet);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.intersCode, this, set, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this intersection set1 == set2}.
     * This means that {@code set2} is equal to the intersection of {@code this} and {@code set1}, i.e.,
     * for all x: x in {@code set2} iff x in {@code this} and x in {@code set1}.
     * @param set1 second argument of set intersection. It must not contain {@code null} values.
     * @param set2 result of set intersection. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set1} or {@code set2} contain {@code null} values.
     */
    public @NotNull
    ConstraintClass inters(@NotNull Set<?> set1, @NotNull Set<?> set2) {
        Objects.requireNonNull(set1);
        Objects.requireNonNull(set2);

        if(set1.stream().anyMatch(Objects::isNull) || set2.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.intersCode, this, set1, set2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - {lVar} == lSet}.
     * @param lVar the only element of the second argument of the set difference.
     * @param lSet result of set difference.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass less(@NotNull LVar lVar, @NotNull LSet lSet) {
        Objects.requireNonNull(lSet);
        Objects.requireNonNull(lVar);

        return new ConstraintClass(Environment.lessCode, this, lVar, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - {object} == lSet}.
     * @param object the only element of the second argument of the set difference.
     * @param lSet result of of set difference.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass less(@NotNull Object object, @NotNull LSet lSet) {
        Objects.requireNonNull(lSet);
        Objects.requireNonNull(object);

        return new ConstraintClass(Environment.lessCode, this, object, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - {lVar} == set}.
     * @param lVar the only element of the second argument of the set difference.
     * @param set result of set difference. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass less(@NotNull LVar lVar, @NotNull Set<?> set) {
        Objects.requireNonNull(set);
        Objects.requireNonNull(lVar);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.lessCode, this, lVar, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - {object} == set}.
     * @param object the only element of the second argument of the set difference.
     * @param set result of set difference. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass less(@NotNull Object object, @NotNull Set<?> set) {
        Objects.requireNonNull(set);
        Objects.requireNonNull(object);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.lessCode, this, object, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code cardinality(this) == size}.
     * @param size the cardinality of {@code this} logical set.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass size(@NotNull IntLVar size) {
        Objects.requireNonNull(size);

        return new ConstraintClass(Environment.sizeCode, this, size);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code cardinality(this) == size}.
     * @param size the cardinality of {@code this} logical set.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass size(@NotNull Integer size) {
        Objects.requireNonNull(size);

        return new ConstraintClass(Environment.sizeCode, this, size);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this} is a subset of {@code lSet}.
     * @param lSet the other argument of the subset constraint.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass subset(@NotNull LSet lSet) {
        Objects.requireNonNull(lSet);

        return new ConstraintClass(Environment.subsetCode, this, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this} is a subset of {@code set}.
     * @param set the other argument of the subset constraint. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass subset(@NotNull Set<?> set) {
        Objects.requireNonNull(set);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.subsetCode, this, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this union lSet1 == lSet2}.
     * @param lSet1 second argument of set union.
     * @param lSet2 result of set union.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass union(@NotNull LSet lSet1, @NotNull LSet lSet2) {
        Objects.requireNonNull(lSet1);
        Objects.requireNonNull(lSet2);

        return new ConstraintClass(Environment.unionCode, this, lSet1, lSet2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this union lSet == set}.
     * @param lSet second argument of set union.
     * @param set result of set union. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass union(@NotNull LSet lSet, @NotNull Set<?> set) {
        Objects.requireNonNull(lSet);
        Objects.requireNonNull(set);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.unionCode, this, lSet, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this union set == lSet}.
     * @param set second argument of set union. It must not contain {@code null} values.
     * @param lSet result of set union.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass union(@NotNull Set<?> set, @NotNull LSet lSet) {
        Objects.requireNonNull(set);
        Objects.requireNonNull(lSet);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.unionCode, this, set, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this union set1 == set2}.
     * @param set1 second argument of set union. It must not contain {@code null} values.
     * @param set2 result of set union. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set1} or {@code set2} contain {@code null} values.
     */
    public @NotNull
    ConstraintClass union(@NotNull Set<?> set1, @NotNull Set<?> set2) {
        Objects.requireNonNull(set1);
        Objects.requireNonNull(set2);

        if(set1.stream().anyMatch(Objects::isNull) || set2.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.unionCode, this, set1, set2);
    }


    /////////////// NEGATIVE SET-THEORETICAL CONSTRAINTS ///////////////////

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this union lSet1 != lSet2}.
     * @param lSet1 second argument of set union.
     * @param lSet2 different than the result of of set union.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass nunion(@NotNull LSet lSet1, @NotNull LSet lSet2) {
        Objects.requireNonNull(lSet1);
        Objects.requireNonNull(lSet2);

        return new ConstraintClass(Environment.nunionCode, this, lSet1, lSet2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this union lSet != set}.
     * @param lSet second argument of set union.
     * @param set different than the result of set union. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass nunion(@NotNull LSet lSet, @NotNull Set<?> set) {
        Objects.requireNonNull(lSet);
        Objects.requireNonNull(set);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.nunionCode, this, lSet, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this union set != lSet}.
     * @param set second argument of set union. It must not contain {@code null} values.
     * @param lSet different than the result of set union.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass nunion(@NotNull Set<?> set, LSet lSet) {
        Objects.requireNonNull(set);
        Objects.requireNonNull(lSet);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.nunionCode, this, set, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this union set1 != set2}.
     * @param set1 second argument of set union. It must not contain {@code null} values.
     * @param set2 different than the result of set union. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set1} or {@code set2} contain {@code null} values.
     */
    public @NotNull
    ConstraintClass nunion(@NotNull Set<?> set1, @NotNull Set<?> set2) {
        Objects.requireNonNull(set1);
        Objects.requireNonNull(set2);

        if(set1.stream().anyMatch(Objects::isNull) || set2.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.nunionCode, this, set1, set2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this} and {@code lSet} are not disjoint.
     * Note that two sets are not disjoint if and only if they have at least one element in common.
     * @param lSet the set which should be disjoint to {@code this}.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass ndisj(@NotNull LSet lSet) {
        Objects.requireNonNull(lSet);

        return new ConstraintClass(Environment.ndisjCode, this, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this} and {@code set} are not disjoint.
     * Note that two sets are not disjoint if and only if they have at least one element in common.
     * @param set the set which should be disjoint to {@code this}. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass ndisj(@NotNull Set<?> set) {
        Objects.requireNonNull(set);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.ndisjCode, this, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this} is not a subset of {@code lSet}.
     * @param lSet the other argument of the not subset constraint.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass nsubset(@NotNull LSet lSet) {
        Objects.requireNonNull(lSet);

        return new ConstraintClass(Environment.nsubsetCode, this, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this} is not a subset of {@code set}.
     * @param set the other argument of the not subset constraint. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass nsubset(@NotNull Set<?> set) {
        Objects.requireNonNull(set);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.nsubsetCode, this, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this intersection lSet1 != lSet2}.
     * This means that {@code lSet2} is not equal to the intersection of {@code this} and {@code lSet1}.
     * @param lSet1 second argument of set intersection.
     * @param lSet2 different than the result of set intersection.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass ninters(@NotNull LSet lSet1, @NotNull LSet lSet2) {
        Objects.requireNonNull(lSet1);
        Objects.requireNonNull(lSet2);

        return new ConstraintClass(Environment.nintersCode, this, lSet1, lSet2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this intersection lSet != set}.
     * This means that {@code set} is not equal to the intersection of {@code this} and {@code lSet}.
     * @param lSet second argument of set intersection.
     * @param set different than the result of set intersection. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass ninters(@NotNull LSet lSet, @NotNull Set<?> set) {
        Objects.requireNonNull(lSet);
        Objects.requireNonNull(set);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.nintersCode, this, lSet, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this intersection set != lSet}.
     * This means that {@code lSet} is not equal to the intersection of {@code this} and {@code set}.
     * @param set second argument of set intersection. It must not contain {@code null} values.
     * @param lSet different than the result of set intersection.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass ninters(@NotNull Set<?> set, @NotNull LSet lSet) {
        Objects.requireNonNull(set);
        Objects.requireNonNull(lSet);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.nintersCode, this, set, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this intersection set1 != set2}.
     * This means that {@code set2} is not equal to the intersection of {@code this} and {@code set1}.
     * @param set1 second argument of set intersection. It must not contain {@code null} values.
     * @param set2 different than the result of set intersection. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set1} or {@code set2} contain {@code null} values.
     */
    public @NotNull
    ConstraintClass ninters(@NotNull Set<?> set1, @NotNull Set<?> set2) {
        Objects.requireNonNull(set1);
        Objects.requireNonNull(set2);

        if(set1.stream().anyMatch(Objects::isNull) || set2.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("NULL VALUES NOT ALLOWED");
        }

        return new ConstraintClass(Environment.nintersCode, this, set1, set2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - lSet1 != lSet2}.
     * @param lSet1 second argument of set difference.
     * @param lSet2 different than the result of set difference.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass ndiff(@NotNull LSet lSet1, @NotNull LSet lSet2) {
        Objects.requireNonNull(lSet1);
        Objects.requireNonNull(lSet2);

        return new ConstraintClass(Environment.ndiffCode, this, lSet1, lSet2);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - lSet != set}.
     * @param lSet second argument of set difference.
     * @param set different than the result of set difference. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass ndiff(@NotNull LSet lSet, @NotNull Set<?> set) {
        Objects.requireNonNull(lSet);
        Objects.requireNonNull(set);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.ndiffCode, this, lSet, set);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - set != lSet}.
     * @param set second argument of set difference. It must not contain {@code null} values.
     * @param lSet different than the result of set difference.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass ndiff(@NotNull Set<?> set, @NotNull LSet lSet) {
        Objects.requireNonNull(set);
        Objects.requireNonNull(lSet);

        if(set.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.ndiffCode, this, set, lSet);
    }

    /**
     * Returns a constraint conjunction whose sole atomic constraint demands that {@code this - set1 != set2}.
     * @param set1 second argument of set difference. It must not contain {@code null} values.
     * @param set2 different than the result of set difference. It must not contain {@code null} values.
     * @return the constructed constraint conjunction.
     * @throws NullPointerException if {@code set1} or {@code set2} contain {@code null} values.
     */
    public @NotNull
    ConstraintClass ndiff(@NotNull Set<?> set1, @NotNull Set<?> set2) {
        Objects.requireNonNull(set1);
        Objects.requireNonNull(set2);

        if(set1.stream().anyMatch(Objects::isNull) || set2.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.ndiffCode, this, set1, set2);
    }


}
