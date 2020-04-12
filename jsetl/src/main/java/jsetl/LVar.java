package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.InitLObjectException;

import java.util.*;

/**
 * This class provides implementation for logical variables.
 * logical variables may be initialized with any value.
 * logical variables may be un-initialized.
 */
public class LVar extends LObject implements Visitable {

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
    * Logical variable value, {@code null} if not initialized.
    */
    protected Object val;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
    * Constructs a not initialized logical variable with default name.
    */
    public LVar() {
        this(defaultName());
    }

    /**
    * Constructs a not initialized logical variable with a specified name.
    * @param name name of the variable.
    */
    public LVar(@NotNull String name) {
        super(name);
        assert name != null;

        this.val = null;
        getNotInitializedLObjectsArrayList().add(this);
    }

    /**
    * Constructs an initialized logical variable with given value and default name.
    * Using this constructor is equivalent to creating a new unbound logical variable
    * and solving the constraint {@code this.eq(value)}.
    * @param value value of the logical variable.
    */
    public LVar(@NotNull Object value) {
        this(defaultName(), value);
        assert value != null;
    }

    /**
    * Constructs an initialized logical variable with given value and name.
    * @param name name of the logical variable.
    * @param value value of the logical variable.
    */
    public LVar(@NotNull String name, @NotNull Object value) {
        super(name);
        assert name != null;
        assert value != null;
        this.initialized = true;
        this.val  = value;
    }

    /**
    * Constructs a logical variable equal to the logical variable {@code lVar}.
    * The constructed logical variable has a default name.
    * Using this constructor is equivalent to creating a new unbound
    * logical variable and solving the constraint {@code this.eq(lVar)}.
    * @param lVar the logical variable to which {@code this} is equal to.
    */
    public LVar(@NotNull LVar lVar) {
        this(defaultName(), lVar);
    }

    /**
    * Constructs a logical variable equal to the logical variable {@code lVar} and with a specified name.
    * Using this constructor is equivalent to creating a new unbound
    * logical variable and solving the constraint {@code this.eq(lVar)}.
    * @param name name of the logical variable.
    * @param lVar the logical variable to which {@code this} is equal to.
    */
    public LVar(@NotNull String name, @NotNull LVar lVar) {
        super(name);
        assert name != null;
        assert lVar != null;
        this.initialized = true;
        this.equ  = lVar;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    //////////////// GENERAL UTILITY METHODS //////////////////////

    /**
     * Creates and returns a (bit-by-bit) copy of this object.
     * @return the cloned logical variable.
     */
    @Override
    public @NotNull LVar clone() {
        if(this.equ != null)
            return getEndOfEquChain().clone();

       LVar lvarClone = new LVar();
       lvarClone.initialized = this.initialized;
       lvarClone.name = this.name;
       lvarClone.equ  = null;
       lvarClone.val = val;
       return lvarClone;
    }

    /**
     * Tests if {@code this} logical variable is equal to the given {@code object}.
     * Returns {@code true} if and only if {@code object} is a reference to {@code this}
     * or to a logical variable that is bound to {@code this} or is an object that is equal
     * to the value of this {@code logical variable}.
     * @param object object to check for equality.
     * @return {@code true} if {@code this} and {@code object} are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(@Nullable Object object) {
        if(object == null)
            return false;
        if(object == this)
            return true;

        if(this.equ != null)
            return getEndOfEquChain().equals(object);

        if(object instanceof  LVar){
            LVar lVar = (LVar) object;
            if(this.equ != null)
                return this.getEndOfEquChain().equals(lVar);

            if(lVar.equ != null)
                return equals(lVar.getEndOfEquChain());

            if(this == lVar)
                return true;

            if(!this.initialized || !lVar.initialized)
                return false;

            return this.val.equals(lVar.val);
        }

        if(!this.initialized)
            return false;

        return this.val.equals(object);
    }

    /**
     * Gets the value of this {@code LVar} or {@code null} if the variable is not initialized.
     * @return the value of this {@code LVar}.
     */    
    public @Nullable Object getValue() {
        if(this.equ != null)
            return this.getEndOfEquChain().getValue();
        if (this.equ == null) {
            if (!initialized) return null;
            else return this.val;
        }
        else return this.getEqu().getValue();
    }

    /**
     * Sets the name of this logical variable and returns the logical variable
     * @param name the new name for this logical variable.
     * @return this logical variable modified as above.
     */
    @Override
    public @NotNull LVar setName(@NotNull String name) {
        Objects.requireNonNull(name);
    	super.setName(name);
    	return this;
    }

    /**
     * Returns a string corresponding to the logical variable value.
     * If this logical variable is initialized this method converts its value to a string,
     * otherwise it generates its name with "_" as prefix in order to distinguish its name
     * from each string or character used as variable values.
     *
     * @return the computed string representation of this logical variable
     */
    @Override
    public @NotNull String toString() {
        if (this.equ == null) {
            if (!this.initialized)
                 return "_"+this.name;
            else return this.val.toString(); 
       }
       else return this.equ.toString();
     }

    /**
     * Sets the value of this {@code LVar} to the parameter {@code object}.
     *
     * @param object the new value of this logical variable
     * @return this {@code LVar} modified.
     */
    public @NotNull LVar setValue(@NotNull Object object) {
        Objects.requireNonNull(object);
        if(this.equ != null)
            return getEndOfEquChain().setValue(object);

        if(this.initialized)
            throw new InitLObjectException();

        this.val  = object;
        this.initialized = true;
        return this;
    }

    /**
     * Returns {@code true} if the invocation object is ground, {@code false} if it isn't.
     * @return {@code true} if the object is ground, {@code false} otherwise.
     */
    @Override
    public boolean isGround(){
        if(equ != null)
            return getEndOfEquChain().isGround();

        return this.isInitialized() && LObject.isGround(this.val);

    }

    /**
     * Needed for the visitor pattern.
     * @param visitor the visitor that wants to visit the object.
     * @return the result of {@code visitor.visit(this)}.
     */
    public @Nullable Object accept(@NotNull Visitor visitor){
        assert visitor != null;
        return visitor.visit(this);
    }

    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns the value of the {@code equ} field, thus making a step in the {@code equ} chain.
     * @return the value of the {@code equ} field.
     */
    protected @Nullable LVar getEqu(){
        return (LVar) equ;
    }

    /**
     * Follows the {@code equ} chain and returns its last node.
     * @return the last node of the equ chain.
     */
    @Override
    protected @NotNull LVar getEndOfEquChain(){
        return (LVar) super.getEndOfEquChain();
    }

    /**
     * Returns the value of this logical variable if it is initialized;
     * otherwise, it returns the logical variable itself.
     * @return the value of this logical variable.
     */
    protected @NotNull Object value() {
        if(this.equ != null)
            return getEndOfEquChain().value();

        if (!initialized)
            return this;

        return val;
    }

    /**
     * Transforms the collection into a variable collection. Used when backtracking.
     */
    @Override
    protected void makeVariable(){
        super.makeVariable();
        val = null;
    }

    ////////////////////////////////////////////////////////////
    //////////// PUBLIC CONSTRAINT METHODS /////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * Constructs and returns a new constraint which demands that {@code this} is equal to {@code object}
     * @param object the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    public @NotNull Constraint eq(@NotNull Object object) {
        Objects.requireNonNull(object);
        return new Constraint(Environment.eqCode, this, object);
    }

    /**
     * Constructs and returns a new constraint which demands that {@code this} is not equal to {@code object}.
     * @param object the other parameter of the constraint.
     * @return the constructed constraint conjunction.
     */
    public @NotNull Constraint neq(@NotNull Object object) {
        Objects.requireNonNull(object);
        return new Constraint(Environment.neqCode, this, object);
    }
}
