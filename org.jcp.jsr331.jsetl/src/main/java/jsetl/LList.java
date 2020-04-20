package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.InitLObjectException;
import jsetl.exception.NotInitLObjectException;

import java.util.*;

/**
 * This class implements logical lists, which are a type of logical collections.
 * logical lists can be completely specified, partially specified or completely unspecified.
 * Elements of logical lists don't have to be of the same type.
 * Elements of logical lists can be logical objects.
 * Uninitialized logical objects may be elements of logical lists.
 */
public class LList extends LCollection 
                  implements Cloneable, Visitable {


    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates and returns the empty logical list.
     * @return an initialized empty logical list.
     */
    public static @NotNull LList empty() {
        return new LList(new ArrayList<>(0));
    }


    /**
     * Constructs and returns a closed logical list containing {@code length} unbound {@code LVars}.
     * @param length number of unbound {@code LVar}s.
     * @return the constructed logical list.
     */
    public static @NotNull LList mkList(int length) {
        ArrayList<Object> list = new ArrayList<>(length);
        for (int i = 0; i < length; ++i) {
            LVar lVar = new LVar();
            list.add(lVar);
        }
        LList resultLList = new LList(list,LList.empty());
        assert resultLList != null;
        return resultLList;
    }

    /**
     * Constructs and returns a closed logical list containing {@code length} unbound {@code IntLVars}.
     * @param length number of unbound {@code IntLVar}s.
     * @return the constructed logical list.
     */
    public static @NotNull LList mkIntList(int length) {
        ArrayList<Object> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            IntLVar intLVar = new IntLVar();
            list.add(intLVar);
        }
        LList resultLList = new LList(list,LList.empty());
        assert resultLList != null;
        return resultLList;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// PUBLIC CONSTRUCTORS //////////////////////////

    /**
     * Constructs an unbound logical list with default name.
     */
    public LList() {
        super();
    }
      
    /**
     * Constructs an unbound logical list with a given name.
     * @param name name of this logical list.
     */
    public LList(@NotNull String name) {
        super(name);
    }
     
    /**
     * Constructs a (completely specified) logical list with value a {@code elements} and default name.
     * Using this constructor is equivalent to creating an unbound logical list and solving the constraint
     * {@code this.eq(elements)}.
     * @param elements list of elements for this logical list. It must not contain {@code null} values.
     * @throws NullPointerException if {@code elements} contains {@code null} values.
     */
    public LList(@NotNull List<?> elements) {
        this(elements.size() == 0 ? "emptyLList" : defaultName(), elements);
       }
            
    /**
     * Constructs a (completely specified) logical list with value {@code elements} and given name.
     * @param name name for the logical collection.
     * @param elements elements for the logical collection. It must not contain {@code null} values.
     * @throws NullPointerException if {@code elements} contains {@code null} values.
     */
    public LList(@NotNull String name, @NotNull List<?> elements) {
        super(name, elements.size() == 0? null : new ArrayList<>(elements),
                elements.size() == 0 ? null : LList.empty());
        this.initialized = true;
    }

    /**
     * Constructs a logical list equal to the logical list {@code lList} and with default name.
     * Using this constructor is equivalent to creating an unbound {@code LList} and posting and
     * solving the constraint {@code this.eq(lList)}.
     * @param lList the logical list to copy.
     */
    public LList(@NotNull LList lList) {
        this(defaultName(), lList);
    }

    /**
     * Constructs a logical list equal to the logical list {@code lList} and with given name.
     * @param name name for the logical list.
     * @param lList the logical list to copy.
     */
    public LList(@NotNull String name, @NotNull LList lList) {
        super(name);
        Objects.requireNonNull(lList);
        this.equ = lList;
        this.initialized = true;
    }


    ///////////// PROTECTED CONSTRUCTORS //////////////////////////

    /**
     * Constructs a logical list with all objects in {@code elements}, the given rest and default name.
     * @param elements list of elements for this logical list. It may be {@code null}, but if it isn't it
     *                 must not contain {@code null} values.
     * @param rest rest of this logical list.
     * @throws NullPointerException if {@code elements} is not {@code null} and contains {@code null} values.
     */
    protected LList(@Nullable ArrayList<?> elements, @Nullable LList rest) {
        super(elements, rest);
        assert elements == null || elements.stream().allMatch(Objects::nonNull);
    }

    /**
     * Constructs a  logical list with all objects in {@code elements}, the given rest and given name.
     * @param name name for the logical list.
     * @param elements list of elements for this logical list. It may be {@code null}, but if it isn't it
     *                 must not contain {@code null} values.
     * @param rest rest of this logical list.
     * @throws NullPointerException if {@code elements} is not {@code null} and contains {@code null} values.
     */
    protected LList(@NotNull String name, @Nullable ArrayList<?> elements, @Nullable LList rest) {
        super(name, elements, rest);
        assert elements == null || elements.stream().allMatch(Objects::nonNull);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// INTERFACE METHODS IMPLEMENTATIONS /////////////////////

    /**
     * This method is used for the Visitor design pattern.
     * @param visitor the visitor that wants to visit the object.
     * @return the result of the call to {@code visitor.visit(this)}.
     */
    public @Nullable Object accept(@NotNull Visitor visitor){
        Objects.requireNonNull(visitor);

        return visitor.visit(this);
    }

    //////////////// SETTERS /////////////////////////////////////

    /**
     * Sets the name of this logical list and returns the logical list.
     * @param name the new name for this logical list.
     * @return this logical list modified as above.
     */
    @Override
    public LList setName(@NotNull String name) {
        Objects.requireNonNull(name);

        super.setName(name);
        return this;
    }


    //////////////// CREATING NEW BOUND LOGICAL LISTS ////////////

    /**
     * Constructs a new logical list which is the result of the addition of {@code objects} to the head of this logical list.
     * Does not modify this logical list.
     *
     * @param objects the objects to insert. It must not contain {@code null} values.
     * @return the constructed logical list.
     * @throws NullPointerException if some of {@code objects} are {@code null}.
     */
    @Override
    public @NotNull LList ins(@NotNull Object... objects) {
        Objects.requireNonNull(objects);
        if(Arrays.stream(objects).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        LList result = (LList) super.ins(objects);
        assert result != null;
        return result;
    }

    /**
     * Constructs a new logical list which is the result of the addition of {@code object} as the last (known) element of this logical list.
     * Does not modify this logical list.
     *
     * @param object the object to insert.
     * @return the constructed logical list.
     */
    public @NotNull LList insn(@NotNull Object object) {
        Objects.requireNonNull(object);

        ArrayList<Object> newElements = this.toArrayList();
        newElements.add(object);
        LList tail = getTail();
        LList resultLList = new LList(newElements, tail);
        assert resultLList != null;
        return resultLList;
    }

    /**
     * Constructs a new logical list which is the result of the addition of each object in {@code objects} to the head of this logical list.
     * Does not modify this logical list.
     *
     * @param objects array of objects to insert. It must not contain {@code null} elements.
     * @return the constructed logical list.
     * @throws NullPointerException if {@code objects} contains {@code null} values.
     */
    @Override
    public @NotNull LList insAll(@NotNull Object[] objects) {
        Objects.requireNonNull(objects);
        if(Arrays.stream(objects).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        LList result = (LList) super.insAll(objects);

        assert result != null;
        return result;
    }

    /**
     * Constructs a new logical list which is the result of the addition of each object in {@code objects} to the head of this logical list.
     * Does not modify this logical list.
     *
     * @param newElements iterable of objects to insert. It must not contain {@code null} values.
     * @return the constructed logical list.
     * @throws NullPointerException if {@code newElements} contains {@code null} values.
     */
    @Override
    public @NotNull LList insAll(@NotNull Collection<?> newElements) {
        Objects.requireNonNull(newElements);
        if(newElements.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        LList result = (LList) super.insAll(newElements);

        assert result != null;
        return result;
    }


    //////////////// GENERAL UTILITY METHODS /////////////////////

    /**
     * Gets the value of this {@code LList} as an {@code ArrayList}
     * @return the value of this {@code LList}. The result may be {@code null}, but if it isn't
     * it does not contain {@code null} values.
     */
    public @Nullable ArrayList<?> getValue() {
        ArrayList<?> value;
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
     * Sets the value of this logical list to the parameter.
     * After the execution of this method the logical list will be completely specified with elements in {@code list}.
     * Using this method is equivalent to solving the constraint {@code this.eq(list)}.
     * @param list the new value of this logical list. It must not contain {@code null} values.
     * @return this list modified as above.
     * @throws InitLObjectException if {@code this} is bound.
     * @throws NullPointerException if {@code list} contains {@code null} values.
     */
    public @NotNull LList setValue(@NotNull List<?> list) {
        Objects.requireNonNull(list);
        if(list.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        this.rest = LList.empty();
        super.setValue(list);
        return this;
    }

    /**
     * Creates and returns a bit-by-bit copy of this object.
     * This visit has a shallow copy of the list and rest but does not share them directly
     * with the original collection, which means that an addition of an element to the cloned collection
     * doesn't add them to the original.
     * @return a clone of this object.
     */
    @Override
    public @NotNull LList clone() {
        LList clone = (LList) super.clone();
        assert clone != null;
        return clone;
    }

    /**
     * Checks whether this {@code LList} is equal to the specified {@code Object}.
     * The {@code Objects} are equal when:
     * - they are the same object or reference to the same object, 
     *   even if the object or the reference are not initialized,
     * - they contain equal objects or they contain references to 
     *   equal objects (order does matter).
     * (ignores the unspecified rest of the logical list if present)
     *
     * @param other object to check for equality with {@code this}
     * @return {@code true} if the {@code LLists} are equals, {@code false} otherwise.
     */
    @Override
    public boolean equals(@Nullable Object other) {
        if(other == null)
            return false;
        if(other instanceof LList) return this.equals((LList)other);
        else 
            if (this.equ == null) 
                if (!this.initialized) return false;
                else return this.getValue().equals(other);
            else return this.equ.equals(other);
    }


    //////////////// LOGICAL COLLECTION METHODS /////////////////////

    /**
     * Gets the tail of this {@code LList}. The tail can be either
     * the empty logical list (= closed logical list) or an uninitialized
     * {@code LList} (= open logical list).
     * @return the tail of this logical list.
     */
    @Override
    public @NotNull LList getTail() {
        LList tail = (LList)super.getTail();

        assert tail != null;
        return tail;
    }

    /**
     * Returns the number of elements of this logical list.
     * Ignores the unspecified tail, if present.
     * (returns {@code 0} for the empty or uninitialized list).
     * @return the size of this logical list.
     */
    @Override
    public int getSize() {
        return this.countAllElements();
    }

    /**
     * Returns the {@code i}-th (starting from 0) element of this collection.
     * @param i the index of the element.
     * @return the {@code i}-th element of this collection.
     * @throws IndexOutOfBoundsException if {@code i} is less than zero or greater than the size of this collection.
     * @throws NotInitLObjectException if this collection is not initialized.
     */
    public @Nullable Object get(int i) throws NotInitLObjectException {
        return super.get(i);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// IMPLEMENTED ABSTRACT METHODS /////////////////

    /**
     * Constructs and returns a logical list with all objects in {@code elements}, the given rest and default name.
     * @param elements list of elements for this logical list. It may be {@code null}, but if it isn't it
     *                 must not contain {@code null} values.
     * @param rest rest of this logical list.
     * @return the constructed logical list.
     * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
     */
    @Override
    protected LList createObj(@Nullable ArrayList<?> elements, @Nullable LCollection rest) {
        assert elements == null || elements.stream().allMatch(Objects::nonNull);

        return new LList(elements, (LList) rest);
    }


    //////////////// GETTERS AND SETTERS /////////////////////////////

    /**
     * Returns the value of the {@code equ} field, thus making a step in the {@code equ} chain.
     * @return the value of the {@code equ} field.
     */
    @Override
    protected @Nullable LList getEqu() {
        return (LList)super.equ;
    }

    /**
     * Follows the {@code equ} chain and returns its last node.
     * @return the last node of the equ chain
     */
    @Override
    protected @NotNull LList getEndOfEquChain(){
        LList endOfEquChain = (LList) super.getEndOfEquChain();

        assert endOfEquChain != null;
        return endOfEquChain;
    }


    ///////////// GENERAL UTILITY METHODS ///////////////

    /**
     * Gets the value of this {@code LList} as an {@code ArrayList}.
     * Ignores the unspecified tail, if present.
     * @return the value of this {@code LList}. The returned array list does not contain {@code null} values.
     */
    @Override
    protected @NotNull ArrayList<Object> value() {
        ArrayList<Object> newList = new ArrayList<>(countAllElements());
        for(Object elem : this)
            if (elem instanceof LVar)
                newList.add(((LVar)elem).value());
            else if (elem instanceof LCollection)
                if (((LCollection)elem).isInitialized())
                    newList.add(((LCollection)elem).value());
                else newList.add(elem);
            else
                newList.add(elem);

        assert newList != null;
        assert newList.stream().allMatch(Objects::nonNull);
        return newList;
     }


    ///////////// LOGICAL COLLECTION METHODS ////////////////////

    /**
      * Removes the first element of this {@code LList}
      * Returns a (new) logical list with one element less.
      * This method ignores the possibly unspecified part of the logical list.
      * Does not modify the invocation object.
      * @return a new logical list which has the same rest and elements as the invocation object
      * except for the first element (which is missing).
      */
    @Override
    protected @NotNull LList removeOne() {
        LList removedOne = (LList)super.removeOne();

        assert removedOne != null;
        return removedOne;
    }


    ////////////////////////////////////////////////////////////
    //////////// PRIVATE METHODS ///////////////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * Checks whether this {@code LList} is equal to the specified {@code LList}.
     * The {@code LList} are equal when:
     * - they are the same object or reference to the same object,
     *   even if the object or the reference are not initialized,
     * - they contain equal objects or they contain references to
     *   equal objects in the same order
     * (ignores the unspecified rest of the LList if present)
     *
     * @param lList logical list to check for equality with {@code this}
     * @return true if the {@code LLists} are equals, false otherwise.
     */
    private boolean equals(@NotNull LList lList) {
        if (this.equ == null && lList.getEqu() == null)
        {
            if (this == lList) return true;
                // true if the lists are the same reference.
                // note that the result is true even if the
                // lists are the same ref but they are not initialized
            else if(!this.initialized || !lList.isInitialized()) return false;
                // false if one or both the lists are not initialized
            else if (this.isBoundAndEmpty() && lList.isBoundAndEmpty()) return true;
                // true if the lists are both the empty list
            else
            {
                ArrayList<Object> t_set = this.toArrayList();
                ArrayList<Object> s_set = lList.toArrayList();
                Object t_tmp;
                Object s_tmp;
                boolean equal;

                if(t_set.size() != s_set.size())
                    return false;
                else
                {
                    equal = true;
                    int i = 0;
                    while(i < t_set.size() && equal)
                    {
                        t_tmp = t_set.get(i);
                        s_tmp = s_set.get(i);
                        if ((s_tmp instanceof LVar) || (s_tmp instanceof LCollection)) {
                            if (!s_tmp.equals(t_tmp)) equal = false;
                        }
                        else if (!t_tmp.equals(s_tmp)) equal = false;
                        i++;
                    }
                    if (!equal) return false;
                }
                return this.getTail().equals(lList.getTail());
            }
        }
        else
        {
            if (this.equ != null) return this.getEqu().equals(lList);
            else return this.equals(lList.getEqu());
        }
    }


    ////////////////////////////////////////////////////////////
    //////////// PUBLIC CONSTRAINT METHODS /////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * Constructs and returns a new constraint which demands that {@code this} is equal to {@code lList}
     * @param lList second parameter of the constraint.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass eq(@NotNull LList lList){
        Objects.requireNonNull(lList);

        return new ConstraintClass("_eq", this, lList);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is equal to {@code list}
     * @param list second parameter of the constraint. It must not contain {@code null} values.
     * @return the constructed constraint.
     * @throws NullPointerException if {@code list} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass eq(@NotNull List<?> list){
        Objects.requireNonNull(list);
        if(list.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass("_eq", this, list);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is not equal to {@code lList}.
     * @param lList second parameter of the constraint.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass neq(@NotNull LList lList){
        Objects.requireNonNull(lList);

        return new ConstraintClass("_neq", this, lList);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is not equal to {@code list}.
     * @param list second parameter of the constraint. It must not contain {@code null} values.
     * @return the constructed constraint.
     * @throws NullPointerException if {@code list} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass neq(@NotNull List<?> list){
        Objects.requireNonNull(list);
        if(list.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass("_neq", this, list);
    }

}
