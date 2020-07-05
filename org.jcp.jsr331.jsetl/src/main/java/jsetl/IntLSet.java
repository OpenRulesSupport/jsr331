package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.*;

/**
 * Logical set whose elements can only be integers (or logical variables with integer values, namely {@code IntLVar}s).
 */
public class IntLSet extends LSet
        implements Cloneable, Visitable {

    /////////////////////////////////////////////////////////
    //////////////// STATIC METHODS /////////////////////////
    /////////////////////////////////////////////////////////

    /**
     * Returns an empty integer logical set.
     * @return a canonical representation of the empty set.
     */
    public static @NotNull IntLSet empty() {
        return new IntLSet(new HashSet<>(0));
    }

    /**
     * Constructs and returns a closed logical set containing {@code n} unbound {@code IntLVars}.
     * @param n number of unbound {@code IntLVars}. Must not be negative.
     * @return the constructed logical set.
     * @throws IllegalArgumentException if {@code n < 0}.
     */
    public static @NotNull IntLSet mkIntSet(int n) {
        if(n < 0)
            throw new IllegalArgumentException("ARGUMENT MUST NOT BE NEGATIVE");

        ArrayList<Object> list = new ArrayList<>(n);
        for(int i = 0; i < n; ++i)
            list.add(new IntLVar());

        IntLSet set = new IntLSet(list,IntLSet.empty());
        return set;
    }


    ////////////////////////////////////////////////////////
    /////// CONSTRUCTORS ///////////////////////////////////
    ////////////////////////////////////////////////////////

    ////// PUBLIC CONSTRUCTORS /////////////////////////////

    /**
     * Constructs a not initialized set with a default name.
     */
    public IntLSet() {
        super();
    }

    /**
     * Constructs a not initialized set with the given name.
     * @param name the name of the set.
     */
    public IntLSet(@NotNull String name) {
        super(name);
    }

    /**
     * Constructs an initialized set whose values are the values of {@code set} with a default name
     * @param set the set containing values for this set. It must not contain {@code null} values.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public IntLSet(@NotNull Set<Integer> set) {
        this(set.isEmpty()? "emptyIntLSet" : defaultName(), set);
    }

    /**
     * Constructs an initialized set whose values are the values of {@code set} with the given name
     * @param name the name of the set
     * @param set the set containing values for this set. It must not contain {@code null} values.
     * @throws NullPointerException if {@code set} contains {@code null} values.
     */
    public IntLSet(@NotNull String name, @NotNull Set<Integer> set) {
        super(
                name,
                set.isEmpty() ? null : new ArrayList<>(set),
                set.isEmpty() ? null : IntLSet.empty());
        this.initialized = true;
    }

    /**
     * Constructs a set containing all the integers from {@code p} to {@code q} (i.e. the interval {@code [p,q]}) with a default name.
     * Constructs the empty set if {@code q} is less than {@code p}.
     * @param p the smallest integer in the interval.
     * @param q the greatest integer in the interval.
     */
    public IntLSet(int p, int q)  {
        this(q < p ? "emptyIntLSet" : defaultName(), p, q);
    }

    /**
     * Constructs a set containing all the integers from {@code p} to {@code q} (i.e. the interval {@code [p,q]}) with the given name.
     * Constructs the empty set if {@code q} is less than {@code p}.
     * @param name the name of the set.
     * @param p the smallest integer in the interval.
     * @param q the greatest integer in the interval.
     */
    public IntLSet(@NotNull String name, int p, int q) {
        this(name, new MultiInterval(p, q));
    }

    /**
     * Creates an integer logical set containing each element of {@code multiInterval}.
     * @param multiInterval a multi interval.
     */
    public IntLSet(@NotNull MultiInterval multiInterval) {
        this(multiInterval.isEmpty() ? "emptyIntLSet" : defaultName(), multiInterval);
    }

    /**
     * Constructs an initialized set with all the integers contained in the multi interval {@code multiInterval} and the given name.
     * @param name the name of the set.
     * @param multiInterval the multi interval.
     */
    public IntLSet(@NotNull String name, @NotNull MultiInterval multiInterval) {
        super(name, multiInterval.toSet());
    }

    /**
     * Constructs a new set equal to {@code intLSet} with a default name.
     * Using this constructor is the same as constructing an uninitialized {@code IntLSet}
     * and then posting and solving the constraint {@code this.eq(intLSet)}.
     * @param intLSet a set.
     */
    public IntLSet(@NotNull IntLSet intLSet) {
        super(intLSet);
    }

    /**
     * Constructs a new set equal to {@code intLSet} with a given name.
     * Using this constructor is the same as constructing an uninitialized {@code IntLSet}
     * and then posting and solving the constraint {@code this.eq(intLSet)}.
     * @param name the name of the new set.
     * @param intLSet a set.
     */
    public IntLSet(@NotNull String name, @NotNull IntLSet intLSet) {
        super(name,intLSet);
    }


    ////// PROTECTED CONSTRUCTORS /////////////////////////////

    /**
     * Constructs a new set of integers which contains all the values in {@code values} and has {@code tail} as rest and has a default name.
     * @param values the list containing the values for the set. It must not contain {@code null} values.
     * @param tail the tail of the set.
     * @throws NullPointerException if {@code values} contains {@code null} values.
     *
     */
    protected IntLSet(@NotNull ArrayList<?> values, @NotNull LSet tail) {
        super(values,tail);
    }

    /**
     * Constructs a new tail of integers which contains all the values in {@code values} and has {@code tail} as rest and with the given name.
     * @param name the name of the set.
     * @param values the list containing the values for the set. It must not contain {@code null} values.
     * @param tail the tail of the set.
     * @throws NullPointerException if {@code values} contains {@code null} values.
     */
    protected IntLSet(@NotNull String name, @NotNull ArrayList<?> values, @NotNull IntLSet tail) {
        super(name,values,tail);
    }


    ///////////////////////////////////////////////////////
    ////// IMPLEMENTED ABSTRACT METHODS ///////////////////
    ///////////////////////////////////////////////////////

    /**
     * Constructs a new logical set of integers which is the union of the elements in {@code elements}
     * with the elements in {@code rest} and the same tail.
     * @param elements the list containing some of the values for the set. It may be {@code null}, but if it
     *                 isn't it must not contain {@code null} values.
     * @param rest an {@code LCollection} containing the other values for the set and, possibly, its tail.
     *
     * @return the constructed logical set of integers.
     * @throws NullPointerException if {@code elements} contains {@code null} values.
     */
    protected @NotNull IntLSet createObj(@Nullable ArrayList<?> elements, @Nullable LCollection rest) {
        if(rest.isBoundAndEmpty())
            return new IntLSet(elements, IntLSet.empty());
        else
            return new IntLSet(elements, (LSet) rest);
    }

    /**
     * Needed for the Visitor design pattern.
     * @param visitor the visitor that wants to visit the object.
     * @return the result of {@code visitor.visit(this)}.
     */
    public @Nullable Object accept(@NotNull Visitor visitor){
        return visitor.visit(this);
    }


    ///////////////////////////////////////////////////////
    ////// PUBLIC UTILITY METHODS /////////////////////////
    ///////////////////////////////////////////////////////

    @Override
    public @NotNull String toString(){
        if(!this.isBound() || this.isBoundAndEmpty())
            return super.toString();
        Set<Integer> groundElements = new HashSet<>();
        List<Object> notGroundElements = new LinkedList<>();
        this.forEach(element -> {
            if(element instanceof Integer)
                groundElements.add((Integer) element);
            else if(element instanceof LVar && LObject.isGround(element))
                groundElements.add((Integer) ((LVar) element).getValue());
            else
                notGroundElements.add(element);
        });

        MultiInterval multiInterval = new MultiInterval(groundElements);
        String multiIntervalString = multiInterval.toString();
        multiIntervalString = "{" + multiIntervalString.substring(1, multiIntervalString.length() - 1);
        String tail = "}";
        if(!this.getTail().isBound())
            tail = this.restDelimitator() + this.getTail().toString() + "}";
        if(notGroundElements.isEmpty())
            return multiIntervalString +  tail;
        else if(groundElements.isEmpty())
            return super.toString();
        else{
            String notGroundElementsString = notGroundElements.toString();
            notGroundElementsString = notGroundElementsString.substring(1,notGroundElementsString.length()-1);
            return multiIntervalString + ", " + notGroundElementsString + tail;

        }

    }
    /**
     * Sets the name of this set and returns it.
     * @param name the new name of this set.
     * @return this set with the name changed.
     */
    public @NotNull IntLSet setName(@NotNull String name) {
        Objects.requireNonNull(name);
        IntLSet result =  (IntLSet) super.setName(name);

        assert result != null;
        assert result == this;
        return result;
    }

    /**
     * Computes an instance of {@code HashSet} containing the known elements in this set.
     * @return an {@code HashSet} of integers containing the known elements in this set.
     */
    @SuppressWarnings("unchecked")
    public @Nullable HashSet<Integer> getValue() {
        HashSet<Integer> value = (HashSet<Integer>) super.getValue();
        return value;
    }

    /**
     * Clones this set and returns it.
     * @return a clone of this set.
     */
    public @NotNull IntLSet clone() {
        IntLSet result = (IntLSet)super.clone();
        assert result != null;
        return result;
    }


    ////////// INTLSET UTILITY METHODS /////////////////////////

    /**
     * Computes and returns a new {@code IntLSet} which is equal to this set with the addition of the element {@code integer}.
     * DOES NOT modify this set.
     * @param integer the new element
     * @return the constructed set.
     */
    public @NotNull IntLSet ins(@NotNull Integer integer) {
        Objects.requireNonNull(integer);
        IntLSet result = (IntLSet) super.ins(integer);
        assert result != null;
        return result;
    }

    /**
     * Computes and returns a new {@code IntLSet} which is equal to this set with the addition of the element {@code intLVar}.
     * DOES NOT modify this set.
     * @param intLVar the new element
     * @return the constructed set.
     */
    public @NotNull IntLSet ins(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);
        IntLSet result = (IntLSet) super.ins(intLVar);
        assert result != null;
        return result;
    }

    /**
     * Computes and returns a new {@code IntLSet} which is equal to this set with the addition of the elements in {@code integers}.
     * DOES NOT modify this set.
     * @param integers the new elements. None of them can be {@code null}.
     * @return the constructed set.
     * @throws NullPointerException if some of the values in {@code integers} are {@code null}.
     */
    public @NotNull IntLSet ins(@NotNull Integer... integers) {
        Objects.requireNonNull(integers);
        if(Arrays.stream(integers).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        IntLSet result = insAll(integers);
        assert result != null;
        return result;
    }

    /**
     * Computes and returns a new {@code IntLSet} which is equal to this set with the addition of the elements in {@code intLVars}.
     * DOES NOT modify this set.
     * @param intLVars the new elements. None of them can be {@code null}.
     * @return the constructed set.
     * @throws NullPointerException if some of the values in {@code intLVars} are {@code null}.
     */
    public @NotNull IntLSet ins(@NotNull IntLVar... intLVars) {
        Objects.requireNonNull(intLVars);
        if(Arrays.stream(intLVars).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        IntLSet result = insAll(intLVars);
        assert result != null;
        return result;
    }

    /**
     * Computes and returns a new {@code IntLSet} which is equal to this set with the addition of the elements in {@code integers}.
     * DOES NOT modify this set.
     * @param integers the array of elements to add. None of its elements can be {@code null}.
     * @return the constructed set.
     * @throws NullPointerException if some of the values in {@code integers} are {@code null}.
     */
    public @NotNull IntLSet insAll(@NotNull Integer[] integers) {
        Objects.requireNonNull(integers);
        if(Arrays.stream(integers).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        IntLSet result = (IntLSet) super.insAll(integers);
        assert result != null;
        return result;
    }

    /**
     * Computes and returns a new {@code IntLSet} which is equal to this set with the addition of the elements in {@code intLVars}.
     * DOES NOT modify this set.
     * @param intLVars the array of elements to add. None of its elements can be {@code null}.s
     * @return the constructed s.
     * @throws NullPointerException if some of the values in {@code intLVars} are {@code null}.
     */
    public @NotNull IntLSet insAll(@NotNull IntLVar[] intLVars) {
        Objects.requireNonNull(intLVars);
        if(Arrays.stream(intLVars).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        IntLSet result = (IntLSet) super.insAll(intLVars);
        assert result != null;
        return result;
    }

    /**
     * Computes and returns a new {@code IntLSet} which is equal to this set with the addition of the elements in {@code integers}.
     * DOES NOT modify this set.
     * @param integers the list of elements to add. None of its elements can be {@code null}.
     * @return the constructed set.
     * @throws NullPointerException if some of the values in {@code integers} are {@code null}.
     */
    public @NotNull IntLSet insAll(@NotNull ArrayList<Integer> integers) {
        Objects.requireNonNull(integers);
        if(integers.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        IntLSet result = (IntLSet) super.insAll(integers);
        assert result != null;
        return result;
    }

    /**
     * Computes and returns a new set with the same known elements and tail as {@code this} but without duplicates.
     * Does not modify this set.
     * @return the set without duplicates.
     */
    @Override
    public @NotNull IntLSet normalizeSet() {
        if(!isBound() || isBoundAndEmpty())
            return this;

        IntLSet ret =  new IntLSet(super.normalizeSet().toArrayList(), IntLSet.empty());
        LSet tail = getTail();
        if(tail != null)
            ret.rest = tail;

        assert ret != null;
        return ret;
    }


    ////////////////////////////////////////////////////////////
    //////////// PUBLIC CONSTRAINT METHODS /////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * Creates glb constraint which requires that all {@code IntLVars} in this set have their domains intersect the interval {@code [glb,lub]}.
     * The constraint is applied to all known {@code IntLVar}s in this set at the moment of the execution of this method.
     *
     * @param glb greatest lower bound of the interval.
     * @param lub least upper bound of the interval.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass domAll(int glb, int lub){
        ConstraintClass result = new ConstraintClass();
        for(Object l : this)
            if (l instanceof IntLVar)
                result.add(((IntLVar)l).dom(glb,lub));


        assert result != null;
        return result;
    }

    /**
     * Creates a constraint which requires the labeling of all known variables in this set at the moment of the execution of this method.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass labelAll() {
        ConstraintClass result = labelAll(new LabelingOptions());
        assert result != null;
        return result;
    }

    /**
     * Creates a constraint which requires the labeling of all known variables in this set at the moment of the execution of this method
     * The constraint resolution will use the given labeling option {@code labelingOptions}.
     *
     * @param labelingOptions labeling options to be used.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass labelAll(@NotNull LabelingOptions labelingOptions) {
        Objects.requireNonNull(labelingOptions);

        LinkedList<IntLVar> list = new LinkedList<>();
        for(Object i : this)
            list.add((IntLVar) i);

        return new ConstraintClass(Environment.labelCode, list, labelingOptions);
    }

}