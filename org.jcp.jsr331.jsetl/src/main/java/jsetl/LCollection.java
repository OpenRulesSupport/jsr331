package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.InitLObjectException;
import jsetl.exception.NotInitLObjectException;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class implements logical collections.
 * logical collections can be completely specified,
 * partially specified (having an unspecified rest or elements) or completely unspecified.
 * Elements of logical collections may be of different types and can even be other logical objects, even if they are unspecified.
 */
public abstract class LCollection extends LObject implements Cloneable, Iterable<Object> {

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    /**
     * The (specified) elements in the collection.
     */
    protected ArrayList<?> elements;

    /**
     * The rest of the collection (a reference to another collection).
     */
    protected LCollection rest;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs a completely unspecified logical collection with default name.
     */
    protected LCollection() {
        this(defaultName());
    }

    /**
     * Constructs a completely unspecified logical collection with given name.
     * @param name name of the logical collection.
     */
    protected LCollection(@NotNull String name) {
        this(name, null, null);
    }

    /**
     * Constructs a partially or completely specified logical collection with the given elements and rest and default name.
     * @param elements list containing elements for this collection. It may be {@code null}, but if it is not it must not
     *      *                 contain {@code null} values.
     * @param rest rest for this collection.
     * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
     */
    protected LCollection(@Nullable ArrayList<?> elements, @Nullable LCollection rest) {
        this(defaultName(), elements, rest);
    }

    /**
     * Constructs a partially or completely specified logical collection with the given elements and rest and name.
     * None of the elements in {@code elements} can be {@code null}.
     * @param name name of this collection.
     * @param elements list containing elements for this collection. It may be {@code null}, but if it is not it must not
     *                 contain {@code null} values.
     * @param rest rest for this collection.
     * @throws NullPointerException if {@code elements} is not {@code null} but contains {@code null} values.
     */
    protected LCollection(@NotNull String name, @Nullable ArrayList<?> elements, @Nullable LCollection rest) {
        super(name);

        assert name != null;
        this.elements = elements;
        this.rest = rest;

        if(this.elements == null && this.rest == null) {
            this.initialized = false;
            registerNotInitializedLObject();
        }
        else{
            if(elements.stream().anyMatch(Objects::isNull))
                throw new NullPointerException("NULL VALUES NOT ALLOWED");
            this.initialized = true;
        }
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// GENERAL UTILITY METHODS //////////////////////

    /**
     * Returns a string corresponding to the object value.
     * If this collection is initialized this method converts its value to a string,
     * otherwise it generates its name with "_" as prefix in order to distinguish its name
     * from each string or character used as variable values.
     *
     * Each initialized collection are of the form [] or [a1,a2,...,an] or [a1,a2,...,an|r] where
     * r is the result of {@code toString()} to the rest of this collection, if r is not empty.
     *
     * @return the computed string representation of this collection
     * @author Andrea Fois
     */
    public @NotNull String toString() {
        String string = this.toString(",", null, null, null);

        assert string != null;
        return string;
    }

    //////////////// COLLECTION METHODS //////////////////////////

    /**
     * Returns the number of (possibly repeated) elements of the collection.
     * @return The number of objects in the collection, {@code 0} for the empty
     * collection.
     * Note that it ignores the possibly unspecified tail of the collection.
     * @throws NotInitLObjectException if the collection is unbound.
     */
    public int getSize() throws NotInitLObjectException {
        if(!this.isBound())
            throw new NotInitLObjectException();
        else
            return this.countAllElements();
    }

    /**
     * Returns {@code true} if the tail of this logical collection is the empty
     * collection (= closed collection); otherwise it returns {@code false}
     * @return {@code true} if the collection is closed, {@code false} otherwise.
     */
    public boolean isClosed(){
        if(equ != null)
            return getEndOfEquChain().isClosed();
        return this.initialized && (this.isBoundAndEmpty() || this.getTail().isBoundAndEmpty());
    }

    /**
     * Tests if this logical collection is bound and empty.
     * @return {@code true} if and only if this {@code LCollection} is bound and has no elements,
     * that is, its size is zero; {@code false} if it bound and has at least one element.
     * @throws NotInitLObjectException if this logical collection is unbound.
     */
    public boolean isEmpty() throws NotInitLObjectException {
        if(!this.isBound())
            throw new NotInitLObjectException();

        return this.isBoundAndEmpty();
    }

    /**
     * Tests if this {@code LCollection} contains any non-ground logical object.
     * @return {@code true} if this object is empty, completely specified
     * and does not contain non-ground logical objects,
     * {@code false} otherwise.
     *
     * @author Andrea Fois
     */
    @Override
    public boolean isGround() {
        if(equ != null)
            return getEndOfEquChain().isGround();

        return this.initialized && this.isClosed() &&
                this.stream().allMatch(LObject::isGround);
    }


    /**
     * Returns an iterator over all elements of this collection.
     * Ignores the tail of this collection but returns the elements contained in its rest.
     * @return an iterator over elements of this collection.
     */
    @Override
    public @NotNull Iterator<Object> iterator() {
        return new LCollectionIterator();
    }

    /**
     * Prints all elements of this {@code LCollection}.
     * All elements are separated by the
     * character {@code separator} (ignores the unspecified tail of the {@code LCollection},
     * if present).
     * @param separator character used to separate elements from each other in the output.
     */
    public void printElems(char separator) {
        System.out.println(this.toString("" + separator, "", "", "|"));
    }

    /**
     * Tests whether the parameter is an element of this logical collection.
     * @param object the object to test for inclusion.
     * @return {@code true} if the parameter is an element of this logical collection, {@code false} otherwise.
     */
    public boolean testContains(@NotNull Object object){
        return this.stream().anyMatch(element -> LObject.equals(element, object));
    }

    /**
     * This method puts each known element of the collection (ignoring its tail) into an array of objects.
     * @return an array containing the elements of this collection.
     */
    public @NotNull Object[] toArray(){
        Object[] array =  this.stream().toArray();

        assert array != null;
        return array;
    }

    //////////////// CONSTRAINT METHODS ///////////////////////////

    /**
     * Constructs a conjunction of constraints {@code constraint}[{@code y} substituted with e_i] for all
     * elements e_i of this {@code LCollection}, where {@code constraint}[{@code y} substituted with e_i] is the constraint
     * obtained from {@code constraint} by replacing all occurrences of the {@code LVar y} with e_i.
     * For example, if s is {1,2,Z} then {@code s.forallElems(X,X.neq(0))} generates
     *          1 neq 0  and  2 neq 0  and  Z neq 0
     * Note that this method ignores the tail of this logical collection.
     *
     * @param y the dummy logical variable used in the constraint {@code constraint}.
     * @param constraint a constraint conjunction which should be satisfied for each (known) element
     *          of this collection, after substituting {@code y} with them.
     * @return the constructed conjunction of constraints
     * @throws NotInitLObjectException if {@code this} is not bound.
     * @throws InitLObjectException if {@code y} is initialized.
     */
    public @NotNull
    ConstraintClass forallElems(@NotNull LVar y, @NotNull ConstraintClass constraint)
            throws NotInitLObjectException {
        assert y != null;
        assert constraint != null;

        if(!this.isInitialized())
            throw new NotInitLObjectException();

        if(y.isBound())
            throw new InitLObjectException();

        ConstraintClass cc = new ConstraintClass();

        for(Object object : this){
            ConstraintClass constraintInstance = new ConstraintMapper(y, object).mapConstraintDeeply(constraint);
            cc.add(constraintInstance);
        }

        assert cc != null;
        return cc;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// ABSTRACT METHODS /////////////////////////////

    /**
     * Constructs and returns a partially or completely specified logical collection with the given elements and rest and default name.
     * @param elements list containing elements for this collection.
     *                 It may be {@code null}, but if it is not it must not contain {@code null} values.
     * @param rest rest for this collection.
     * @return the constructed collection.
     * @throws NullPointerException if {@code elements} it not {@code null} but contains {@code null} values.
     */
    protected abstract @NotNull LCollection createObj(@Nullable ArrayList<?> elements, @Nullable LCollection rest);


    //////////////// GETTERS AND SETTERS //////////////////////////

    /**
     * Returns the value of the {@code equ} field, thus making a step in the {@code equ} chain.
     * @return the value of the {@code equ} field.
     */
    protected @Nullable LCollection getEqu() {
        return (LCollection) this.equ;
    }

    /**
     * Returns the value of the {@code elements} field.
     * If elements is not {@code null}, it does not contain {@code null} values.
     * @return the value of the {@code elements} field.
     */
    protected @Nullable ArrayList<?> getElements() {
        assert this.elements == null || this.elements.stream().allMatch(Objects::nonNull);

        return this.elements;
    }

    /**
     * Returns the value of the {@code rest} field.
     * @return the value of the {@code rest} field.
     */
    protected @Nullable LCollection getRest() {
        return this.rest;
    }

    /**
     * Sets the value of the {@code equ} field.
     * @param equ the new value for the {@code equ} field.
     */
    protected void setEqu(@Nullable LCollection equ) {
        this.equ = equ;
    }

    /**
     * Sets the value of the {@code elements} field.
     * Elements must not contain {@code null} values.
     * The new {@code elements} field will be a reference to the parameter.
     * @param elements the new value for the equ field.
     */
    protected void setElements(@Nullable ArrayList<Object> elements) {
        assert elements == null || elements.stream().allMatch(Objects::nonNull);

        this.elements = elements;
    }

    /**
     * Setter for field {@code rest}. Sets the given parameter as the new rest of the logical collection.
     * @param rest the new rest of the logical collection
     */
    protected void setRest(@Nullable LCollection rest) {
        this.rest = rest;
    }

    /**
     * Gets the tail of this {@code LCollection}. The tail can be either
     * the empty collection (= closed collection) or an uninitialized
     * {@code LCollection} (= open collection). The result is equal to {@code this}
     * if the object is unbound.
     * @return the tail of this collection.
     */
    protected @NotNull LCollection getTail() {
        LCollection tail = this;

        if(this.equ != null)
            return getEndOfEquChain().getTail();

        if(this.rest != null)
            tail = this.rest.getTail();

        assert tail != null;
        assert this.initialized || tail == this;
        return tail;
    }

    /**
     * Follows the {@code equ} chain and returns its last node.
     * @return the last node of the equ chain.
     */
    @Override
    protected @NotNull LCollection getEndOfEquChain() {
        LCollection endOfEquChain = (LCollection) super.getEndOfEquChain();

        assert endOfEquChain != null;
        return endOfEquChain;
    }

    /**
     * This method puts each known element of the collection (ignoring its tail) to an array list of objects
     * @return a list containing the elements of this collection.
     */
    protected @NotNull ArrayList<Object> toArrayList() {
            ArrayList<Object> arrayList =  new ArrayList<>(Arrays.asList(toArray()));

            assert arrayList != null;
            return arrayList;
    }

    /**
     * Creates and returns a copy of this object.
     * This clone has a shallow copy of the list and rest but does not share them directly
     * with the original collection, which means that an addition of an element to the cloned collection
     * doesn't add them to the original.
     * @return a clone of this object.
     */
    protected @NotNull LCollection clone() {
        LCollection copy;
        try{
            copy = (LCollection)super.clone(); //bit-by-bit copy
        } catch(CloneNotSupportedException x){
            throw new UnsupportedOperationException("CLONE NOT SUPPORTED FOR CLASS " + this.getClass());
        }

        assert copy != null;
        return copy;
    }

    /**
     * Default open delimiter for {@code LCollection}s.
     * @return the default open delimiter for logical collections.
     */
    protected @NotNull Character openDelimitator() {
        return '[';
    }

    /**
     * Default close delimiter for {@code LCollections}
     * @return a character which is the default close delimiter for collections.
     */
    protected @NotNull Character closeDelimitator() {
        return ']';
     }

    /**
     * Default delimiter to use between the elements list and the rest.
     * @return a character which is the default rest delimiter for collections.
     * @author Andrea Fois
     */
    protected @NotNull Character restDelimitator() {
        return '|';
    }

    /**
     * Returns a collection which is the concatenation of this collection with {@code secondLCollection}.
     * The returned collection has the same tail as {@code secondLCollection}, which means that the tail of {@code this}
     * is ignored. Elements are added in order with elements from {@code this} having indexes smaller than elements
     * from {@code secondLCollection}. E.g. [1,2,3| r1].concat([4,5,6|r2]) returns [1,2,3,4,5,6|r2].
     * @param secondLCollection the other collection.
     * @return the concatenated collection.
     */
    protected @NotNull LCollection concat(@NotNull LCollection secondLCollection) {
        assert secondLCollection != null;

        LCollection returned;
        if (this.equ == null && secondLCollection.getEqu() == null) {
            if(this.isBoundAndEmpty() || !this.initialized)
                returned = secondLCollection;
             else
                returned = createObj(this.elements, this.rest.concat(secondLCollection));
        } else if (this.equ == null) returned = this.concat(secondLCollection.getEqu());
        else if (secondLCollection.getEqu() == null) returned = this.getEqu().concat(secondLCollection);
        else returned = this.getEqu().concat(secondLCollection.getEqu());

        assert returned != null;
        return returned;
    }

    /**
     * Counts elements of this collection.
     * @return the number of (known) elements in this collection.
     */
    protected int countAllElements() {
        return (int) this.stream().count();
    }

    /**
     * Returns the {@code i}-th element (starting from 0) of this collection in the concrete representation.
     * @param i the index of the element.
     * @return the {@code i}-th element of this collection.
     * @throws IndexOutOfBoundsException if {@code i} is less than zero or greater than the size of this collection.
     * @throws NotInitLObjectException if this collection is not bound.
     */
    protected @NotNull Object get(int i) throws NotInitLObjectException {
        if(i < 0)
            throw new IndexOutOfBoundsException();

        if(!this.isBound())
            throw new NotInitLObjectException();

        for(Object element : this)
            if(i == 0){
                assert element != null;
                return element;
            }
            else
                --i;

        throw new IndexOutOfBoundsException();
    }
      
    /**
     * Gets one element of this {@code LCollection}
     * (actually the first element in the concrete representation).
     * Precondition of this method is that the logical collection
     * has at least one known element.
     *
     * @return the first element of this collection.
     */
    protected @NotNull Object getOne() {
        assert this.countAllElements() > 0;

        Object one = this.iterator().next();

        assert one != null;
        return one;
    }
  
    /**
     * Insertion of objects into a collection. Does not modify the original object.
     * @param objects the objects to insert.
     * @return returns a (new) collection obtained by adding {@code object} to this collection.
     */
    protected @NotNull LCollection ins(@NotNull Object... objects) {
        assert objects != null;
        assert Arrays.stream(objects).noneMatch(Objects::isNull);

        LCollection result = this.insAll(objects);

        assert result != null;
        return result;
    }

    /**
     * Creates and returns a new collection obtained from this by adding all
     * elements of the parameter {@code array}. Does not modify this collection.
     * @param array array of elements to add. It must not contain {@code null} values.
     * @return the newly constructed collection.
     */
    protected @NotNull LCollection insAll(@NotNull Object[] array) {
        assert array != null;
        assert Arrays.stream(array).allMatch(Objects::nonNull);

        if(equ != null)
            return getEndOfEquChain().insAll(array);

        ArrayList<Object> list = new ArrayList<>(Arrays.asList(array));

        LCollection lCollection = createObj(list,this);

        assert lCollection != null;
        return lCollection;
    }

    /**
     * Creates and returns a new logical collection obtained from this by adding all
     * elements of the parameter {@code newElements}. Does not modify this collection.
     * @param newElements collection of elements to add, it must not contain {@code null} values.
     * @return the constructed logical collection.
     */
    protected @NotNull LCollection insAll(@NotNull Collection<?> newElements) {
        assert newElements != null;
        assert newElements.stream().allMatch(Objects::nonNull);

        if(equ != null)
            return getEndOfEquChain().insAll(newElements);

        ArrayList<Object> list = new ArrayList<>(newElements);

        LCollection result = createObj(list,this);
        assert result != null;
        return result;
    }

    /**
     * Same as {@code isEmpty()}, but it returns {@code false} if this object is
     * not bound.
     * @return {@code true} if the object is initialized and empty, {@code false} otherwise.
    */
    protected boolean isBoundAndEmpty() {
        if(equ != null)
            return getEndOfEquChain().isBoundAndEmpty();
        if(!this.isBound())
            return false;

        else if(this.elements == null && this.rest == null)
            return true;
        else
            return this.elements != null
                    && this.rest != null
                    && this.elements.size() == 0
                    && this.rest.isBoundAndEmpty();
    }

    /**
     * Checks whether the logical collection is bound or not.
     * @return {@code false} if it is bound, {@code true} otherwise.
     */
    protected boolean isVariable() {   
      	return !this.isBound();
    }

    /**
     * Removes one element of this {@code LCollection}
     * (actually the first element in the concrete representation).
     * Returns a (new) collection with one element less.
     * This method ignores the possibly unspecified part of the collection.
     * Does not modify the invocation object.
     * @return a new logical collection which has the same rest and elements as the invocation object
     * except for the first (which is missing).
     */
    protected @NotNull LCollection removeOne() {
        LCollection removedOne;
        if(this.equ == null) {
            if (this.elements.size() == 1)
                 return this.rest;
            else if (this.countAllElements() == 1)
                 return this.getTail();
            else if (this.elements.size() == 0)
                 return this.rest.removeOne();

            removedOne = createObj(new ArrayList<>(this.elements.subList(1, this.elements.size())), this.rest);
        } else
            removedOne = this.getEqu().removeOne();

        assert removedOne != null;
        return removedOne;
    }

    /**
     * Sets the list of elements in this collection (not those in the rest of the collection) to a new
     * list containing the elements in the collection {@code elements}.
     * @param elements an collection which contains the new elements.
     * @return this collection modified as stated above.
     * @throws InitLObjectException if this collection is already bound.
     */
    protected @NotNull LCollection setValue(@NotNull Collection<?> elements) {
        if(this.isBound())
            throw new InitLObjectException();

        LCollection thisLCollection = this.getEndOfEquChain();
        thisLCollection.elements = new ArrayList<>(elements);
        this.initialized = true;
        thisLCollection.initialized = true;
        return this;
    }

    /**
     * Returns the value of this collection, i.e. a list containing its (known) elements.
     * If the collection is not initialized then the collection itself is returned.
     * @return a list containing all (known) elements of this collection if it is initialized,
     * the collection itself otherwise.
     */
    protected @NotNull Object value() {
        //follow equ chain
        if(equ != null)
            return getEndOfEquChain().value();

        //if this is not initialized the value is the collection itself
        if(!initialized)
            return this;

        //returns a list containing elements of this collection
        Object returned = Arrays.asList(this.toArray());
        assert returned != null;
        return returned;
     }

    /**
     * Transforms the collection into a variable collection. Used when backtracking.
     */
    @Override
    protected void makeVariable(){
        super.makeVariable();
        elements = null;
        rest = null;
    }

    /**
     * Tests if {@code object} is part of this collection (an element, the tail, or a part of an element).
     * @param object object to search deeply inside this logical collection.
     * @return {@code true} if {@code object} is an element of this collection, {@code false} otherwise.
     * @author Andrea Fois
     */
    protected boolean occurs(@NotNull Object object) throws NotInitLObjectException {
        Objects.requireNonNull(object);

        if(!this.isBound())
            throw new NotInitLObjectException();
        if(this.isEmpty())
            return false;

        DeepExplorer deepExplorer = new DeepExplorer();
        Boolean contains = (Boolean) deepExplorer.exploreInternally(this, part -> {
            if(LObject.equals(object, part))
                deepExplorer.stopExploration(true);
        });

        return contains != null;
    }

    //////////////// CONSTRAINT METHODS ///////////////////////////

    /**
     * Constructs and returns a new constraint which demands that {@code this} is equal to {@code lCollection}.
     * @param lCollection the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    protected @NotNull
    ConstraintClass eq(@NotNull LCollection lCollection) {
        assert lCollection != null;

        return new ConstraintClass(Environment.eqCode, this, lCollection);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is equal to {@code collection}.
     * @param collection the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    protected @NotNull
    ConstraintClass eq(@NotNull Collection<?> collection) {
        assert collection != null;
        return new ConstraintClass(Environment.eqCode, this, collection);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is not equal to {@code lCollection}.
     * @param lCollection the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    protected @NotNull
    ConstraintClass neq(@NotNull LCollection lCollection) {
        assert lCollection != null;

        return new ConstraintClass(Environment.neqCode, this, lCollection);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is not equal to {@code collection}.
     * @param collection the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    protected @NotNull
    ConstraintClass neq(@NotNull Collection<?> collection) {
        assert collection != null;

        return new ConstraintClass(Environment.neqCode, this, collection);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PRIVATE METHODS //////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns a stream over the known elements of this logical collection.
     * @return a stream.
     */
    private @NotNull Stream<Object> stream(){
        return StreamSupport.stream(this.spliterator(),false);
    }

    /**
     * Returns a string corresponding to the object value.
     * If this collection is initialized this method converts its value to a string,
     * otherwise it generates its name with "_" as prefix in order to distinguish its name
     * from each string or character used as variable values.
     *
     * Each initialized collection are of the form {@code openingDelimitator}{@code closingDelimitator}
     * or {@code openingDelimitator}a1,a2,...,an{@code closingDelimitator}
     * or {@code openingDelimitator}a1,a2,...,an|r{@code closingDelimitator} where
     * r is the result of {@code toString(separator, openingDelimitator, closingDelimitator)}
     * to the tail of this collection, if r is not empty.
     *
     * @param separator string to use between elements of the collection. Defaults to ",".
     * @param openingDelimitator string to use before the listing of elements and rest. Defaults to {@code this.openingDelimitator()}.
     * @param closingDelimitator string to use after the listing of elements and rest. Defaults to {@code this.closingDelimitator()}.
     * @param restDelimitator string to use between the list of known elements and the rest. Defaults to {@code this.restDelimitator()}.
     * @return the computed string representation of this collection.
     * @author Andrea Fois
     */
    private @NotNull String toString(
            @Nullable String separator,
            @Nullable String openingDelimitator,
            @Nullable String closingDelimitator,
            @Nullable String restDelimitator) {

        //Getting to the end of equ chain
        LCollection lCollection = getEndOfEquChain();

        if(openingDelimitator == null)
            openingDelimitator = lCollection.openDelimitator().toString();

        if(closingDelimitator == null)
            closingDelimitator = lCollection.closeDelimitator().toString();

        if(restDelimitator == null)
            restDelimitator = lCollection.restDelimitator().toString();

        if(separator == null)
            separator = ",";

        //Checking not initialized case
        if(!lCollection.initialized)
            return "_" + lCollection.name;

        String output = openingDelimitator; //[

        //Adding known elements
        Iterator<Object> iterator = lCollection.iterator();
        StringBuilder builder = new StringBuilder();
        while(iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext())
                builder.append(separator);
        }
        output += builder.toString();

        //Adding tail
        LCollection tail = this.getTail();
        if(tail != null && !tail.isBoundAndEmpty())
            output += restDelimitator + tail.toString();

        output += closingDelimitator; //]

        assert output != null;
        return output;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// INNER CLASSES ////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Inner class that provides an implementation for iterators over logical collections.
     */
    private class LCollectionIterator implements Iterator<Object> {

        /////////////////////////////////////////////////////////
        /////////////// DATA MEMBERS ////////////////////////////
        /////////////////////////////////////////////////////////

        /**
         * Reference to the collection to iterate over.
         */
        private LCollection lCollection;

        /**
         * Iterator of the current list of elements being iterated.
         */
        private Iterator<?> iterator;


        /////////////////////////////////////////////////////////
        /////////////// CONSTRUCTORS ////////////////////////////
        /////////////////////////////////////////////////////////

        /**
         * Constructs an iterator over the logical collection.
         * Automatically stores a reference to the parent class object.
         */
        public LCollectionIterator(){
            lCollection = getFirstNotEmptyInitializedLCollection(LCollection.this);
            if(lCollection != null){
                iterator = lCollection.elements.iterator();
            }
        }


        /////////////////////////////////////////////////////////
        /////////////// PUBLIC METHODS //////////////////////////
        /////////////////////////////////////////////////////////

        /**
         * Checks whether there are more elements to iterate through or not.
         * @return {@code true} if there are more elements to iterate through, {@code false} otherwise.
         */
        @Override
        public boolean hasNext() {
            if(iterator == null || lCollection == null)
                return false;
            if(iterator.hasNext())
                return true;
            lCollection = getFirstNotEmptyInitializedLCollection(lCollection.rest);

            if(lCollection == null)
                return false;
            iterator = lCollection.elements.iterator();
            return true;

        }

        /**
         * Retrieves and returns the next element of the collection.
         * @return the next element of the collection.
         */
        @Override
        public @NotNull Object next() {
            return iterator.next();
        }


        /////////////////////////////////////////////////////////
        /////////////// PRIVATE METHODS /////////////////////////
        /////////////////////////////////////////////////////////

        /**
         * Follows the chain of {@code equ} and {@code rest} and returns the first bound and not empty
         * logical collection, if it exists, otherwise returns {@code null}.
         * @param lCollection starting point of the chain.
         * @return the first bound and not empty logical collection found.
         */
        private @Nullable LCollection getFirstNotEmptyInitializedLCollection(@Nullable LCollection lCollection){
            if(lCollection == null)
                return null;

            lCollection = lCollection.getEndOfEquChain();

            if(lCollection.elements == null || lCollection.elements.isEmpty())
                return getFirstNotEmptyInitializedLCollection(lCollection.rest);

            return lCollection;
        }

    }

}
