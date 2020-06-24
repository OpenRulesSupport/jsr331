package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class implements logical objects, i.e., objects which can be used to post constraints and can be uninitialized.
 */
public abstract class LObject {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC MEMBERS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * If {@code true} the name of internal variables (that are not dummy {@code IntLVar})
     * will be of the form "?" rather than "_N"+ unique ID.
     * SET TO TRUE ONLY FOR DEBUGGING PURPOSES.
     */
    public static boolean USEJOLLYTOKEN = false;

    /**
     * Counter used to name anonymous variables. Only for debugging purposes.
     */
    private static int anonymous_id = 0; //THIS FIELD IS USED ONLY WHEN DEBUGGING.

    /**
     * This field maps threads to the corresponding not initialized logical objects record (which in turn
     * contains a reference to the current executing solver in that thread and the list of not initialized logical objects).
     */
    private static final ConcurrentHashMap<Thread, NotInitializedLObjectsRecord> notInitializedLObjectsMap = new ConcurrentHashMap<>();


    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns the appropriate list containing all the not initialized logical objects
     * depending on the thread that calls the method. Each thread has its own list,
     * which is created, if not present.
     * @return a list containing all uninitialized logical variables that may be needed by the
     * current thread.
     */
    protected static @NotNull ArrayList<LObject> getNotInitializedLObjectsArrayList(){
        Thread thread = Thread.currentThread();
        NotInitializedLObjectsRecord record = notInitializedLObjectsMap.get(thread);
        ArrayList<LObject> notInitializedLObjects = null;
        SolverClass currentExecutingSolver = null;
        if(record != null){
            notInitializedLObjects = record.notInitializedLObjectsList;
            currentExecutingSolver = record.currentExecutingSolver;
        }


        if (notInitializedLObjects == null) {
            notInitializedLObjectsMap.entrySet().removeIf(
                    entry -> !entry.getKey().isAlive());
            notInitializedLObjects = new ArrayList<>();
            notInitializedLObjectsMap.put(thread, new NotInitializedLObjectsRecord(currentExecutingSolver, notInitializedLObjects));
        }

        return notInitializedLObjects;


    }

    /**
     * Sets the not initialized logical objects list provided in the record for the current thread in {@code notInitializedLObjectsMap}.
     * @param list a list containing all necessary not initialized logical objects. It must not contain {@code null} values.
     */
    protected static void setNotInitializedLObjectsList(@NotNull ArrayList<LObject> list){
        assert list != null;
        assert list.stream().noneMatch(Objects::isNull);

        Thread thread = Thread.currentThread();
        NotInitializedLObjectsRecord record = getNotInitializedLObjectsRecord();
        SolverClass solver = null;
        if(record != null){
            solver = record.currentExecutingSolver;
        }
        notInitializedLObjectsMap.put(thread, new NotInitializedLObjectsRecord(solver, list));
    }

    /**
     * Sets the current executing solver in the record corresponding to the current thread in {@code notInitializedLObjectsMap}.
     * @param solver a reference to the current executing solver.
     */
    protected static void setCurrentExecutingSolver(@Nullable SolverClass solver){
        Thread thread = Thread.currentThread();
        NotInitializedLObjectsRecord record = notInitializedLObjectsMap.get(thread);
        record.currentExecutingSolver = solver;
    }

    /**
     * Returns, if present, the not initialized logical objects record corresponding to the current thread
     * in the map {@code notInitializedLObjectsMap}. If it is not present returns {@code null}.
     * @return the retrieved not initialized logical objects record.
     */
    protected static @Nullable NotInitializedLObjectsRecord getNotInitializedLObjectsRecord(){
        Thread thread = Thread.currentThread();
        return notInitializedLObjectsMap.get(thread);
    }

    /**
     * Checks if {@code first} and {@code second} are equals: they are both {@code null} or both non-null
     * and either {@code first.equals(second)} or {@code second.equals(first)} return {@code true}.
     * @param first first object.
     * @param second second object.
     * @return {@code true} if {@code first} and {@code second} are equal, {@code false} otherwise.
     */
    public static boolean equals(@Nullable Object first, @Nullable Object second){
        return Objects.equals(first,second) || Objects.equals(second,first);
    }

    /**
     * Returns the default name for logical objects.
     * @return the default name for logical objects
     */
    protected static @NotNull String defaultName() {
        if(USEJOLLYTOKEN)
            return "?";
        else
            return "N" + (++anonymous_id);
    }

    /**
     * Returns {@code true} if the given {@code object} is ground (i.e. it is not a logical object or
     * it does not contain variables).
     * @param object test object.
     * @return {@code true} if the parameter is ground, {@code false} if it isn't.
     */
    public static boolean isGround(@NotNull Object object){
        assert object != null;

        return !(object instanceof LObject) || ((LObject)object).getEndOfEquChain().isGround();
    }


    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Name of the logical object.
     */
    protected String  name;

    /**
     * {@code true} if the logical object is initialized; {@code false} otherwise.
     */
    protected boolean initialized = false;

    /**
     * Reference for binding equal logical objects.
     */
    protected LObject equ = null;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    /**
     * Constructs a not initialized logical object with default name.
     */
    public LObject() {     
        this(defaultName());
    } 

    /**
     * Constructs a not initialized logical object with a specified name.
     * @param name the name of the logical object.
     */
    public LObject(@NotNull String name) {
        Objects.requireNonNull(name);

        if (name.length() == 0 || name.charAt(0) != '$') {
            this.name = name;
        }
        else {
            String realName = name.substring(1);
            if (realName.length() == 0)
                this.name = defaultName();
            else
                this.name = realName;
        }
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns the value of the logical object, if it is bound, returns {@code null} if it is not bound.
     * @return the value of the logical object.
     */
    public abstract @Nullable Object getValue();

    /**
     * Retrieves the name of this logical object.
     * @return the name of this logical object.
     */
    public @NotNull String getName() {
        assert this.name != null;
        return this.name;
    }

    /**
     * Checks whether this logical object is bound or not.
     * @return {@code true} if this logical object is bound, {@code false} otherwise.
     * @see LObject#isInitialized()
     */
    public boolean isBound() {
        return getEqu() == null? isInitialized() : getEndOfEquChain().isBound();
    }

    /**
     * Sets the name of this logical object and returns the logical object.
     * @param name the new name for this logical object.
     * @return this logical object modified as above.
     */
    public @NotNull LObject setName(@NotNull String name) {
        Objects.requireNonNull(name);

        this.name = name;
        return this;
    }

    /**
     * Returns {@code true} if the invocation object is ground, {@code false} if it isn't.
     * @return {@code true} if the object is ground, {@code false} otherwise.
     */
    public abstract boolean isGround();

    /**
     *  Prints the name and the value of this logical object to standard output.
     *  The format of the output is "VARIABLE_NAME = VARIABLE_VALUE\n", in which
     *  VARIABLE_VALUE is the string representation of the value of the logical object
     *  or "unknown" if the object is unbound.
     */
    public void output() {
        this.outputLine();
        System.out.println();
        return;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Checks whether this logical object is initialized or not.
     * @return {@code true} if this logical object is initialized, {@code false} otherwise.
     * @see LObject#isBound()
     */
    protected boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Sets the value of {@code initialized} field of this logical object, i.e. sets it to initialized or not.
     * @param initialized the new value for this logical object field {@code initialized}.
     */
    protected void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Returns the value of the {@code equ} field, thus making a step in the {@code equ} chain.
     * @return the value of the {@code equ} field.
     */
    protected @Nullable LObject getEqu(){
        return this.equ;
    }

    /**
     * Sets the value of {@code equ} field of this logical object, i.e. sets it to be unified to another logical object.
     * @param lObject the new value for this logical object field {@code equ}.
     */
    protected void setEqu(@Nullable LObject lObject){
        this.equ = lObject;
    }

    /**
     * Follows the {@code equ} chain and returns its last node.
     * @return the last node of the equ chain.
     */
    protected @NotNull LObject getEndOfEquChain(){
        LObject end = this;

        while(end.equ != null)
            end = end.equ;

        assert end != null;
        return end;
    }

    /**
     * Transforms the object into a variable. Used when backtracking.
     */
    protected void makeVariable(){
        initialized = false;
        equ = null;
    }

    /**
     * Method that MUST be called when creating a variable logical object.
     * Must be called in the constructor of subclasses.
     */
    protected final void registerNotInitializedLObject(){
        LObject.getNotInitializedLObjectsArrayList().add(this);
    }

    /**
     *  Prints the name and the value of this logical object to standard output.
     *  The format of the output is "VARIABLE_NAME = VARIABLE_VALUE" in which
     *  "VARIABLE_VALUE" is the string representation of the value or "unknown" if
     *  it is unbound.
     */
    protected void outputLine() {
        System.out.print("_"+this.name + " = ");
        if (this.equ == null) {
            if(!this.initialized) System.out.print("unknown");
            else System.out.print(this);
        } else System.out.print(this.equ);
        return;
    }

    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRAINT METHODS ///////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs and returns a new constraint which demands that {@code this} is an element of {@code lSet}.
     * @param lSet the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass in(@NotNull LSet lSet) {
        Objects.requireNonNull(lSet);
        return new ConstraintClass(Environment.inCode, this, lSet);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is an element of {@code set}.
     * @param set the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass in(@NotNull Set<?> set) {
        Objects.requireNonNull(set);
        return new ConstraintClass(Environment.inCode, this, set);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is not an element of {@code lSet}.
     * @param lSet the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass nin(@NotNull LSet lSet) {
        Objects.requireNonNull(lSet);
        return new ConstraintClass(Environment.ninCode, this, lSet);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is not an element of {@code set}.
     * @param set the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    public @NotNull
    ConstraintClass nin(@NotNull Set<?> set) {
        Objects.requireNonNull(set);
        return new ConstraintClass(Environment.ninCode, this, set);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// INNER CLASSES ////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Objects of this class contain a reference to the current executing solver in the current thread
     * and to the list of not initialized logical objects.
     * @author Andrea Fois
     */
    protected static class NotInitializedLObjectsRecord {

        ///////////////////////////////////////////////////////////////
        //////////////// DATA MEMBERS /////////////////////////////////
        ///////////////////////////////////////////////////////////////

        /**
         * Reference to the current executing solver (in the current thread).
         */
        SolverClass currentExecutingSolver;

        /**
         * List containing references to all not initialized logical objects used in solvers in the current thread. It
         * can contain references to logical objects that are initialized.
         */
        final ArrayList<LObject> notInitializedLObjectsList;


        ///////////////////////////////////////////////////////////////
        //////////////// CONSTRUCTORS /////////////////////////////////
        ///////////////////////////////////////////////////////////////

        /**
         * Creates a new record containing the current executing solver and not initialized logical objects list provided.
         * @param currentExecutingSolver reference to the current executing solver (in the current thread).
         * @param notInitializedLObjectsList list containing all (but not necessarily only) not initialized logical objects
         *                                   that appear in the constraint store used by solvers in the current thread.
         *                                   It must not contain {@code null} values.
         */
        NotInitializedLObjectsRecord(@Nullable SolverClass currentExecutingSolver, @NotNull ArrayList<LObject> notInitializedLObjectsList) {
            assert notInitializedLObjectsList != null;
            assert notInitializedLObjectsList.stream().noneMatch(Objects::isNull);

            this.currentExecutingSolver = currentExecutingSolver;
            this.notInitializedLObjectsList = notInitializedLObjectsList;
        }
    }


}
