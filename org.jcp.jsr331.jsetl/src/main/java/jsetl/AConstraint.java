package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.*;

/**
 * Class implementing atomic constraints, i.e. constraints that are not conjunctions of other constraints.
 * Examples include x = y + 4, z != j, constraint_1 or constraint_2, ...
 * The kind of constraint is encoded by an integer.
 */
class AConstraint implements Visitable {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC MEMBERS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * The maximum number of arguments that can be passed in the constructors of this class.
     * This value is final and is equal to 4.
     */
    public static final int MAX_ACONSTRAINT_ARGUMENTS = 4;


    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * This field is used to mark constraints that are created to "define" temporary variables
     * needed for the flattened form of expressions, for example arithmetical. Those constraints
     * should not be negated when inside a constraint that is being negated.
     */
    private boolean definitionConstraint = false;

    /**
     * First argument of constraint.
     */
    protected Object argument1;
    
    /**
     * Second argument of constraint.
     */    
    protected Object argument2;
    
    /**
     * Third argument of constraint.
     */    
    protected Object argument3;
    
    /**
     * Fourth argument of constraint.
     */    
    protected Object argument4;
    
    /**
     * Identifies the constraint type.
     */
    protected int constraintKindCode;
    
    /**
     * Identifies the nondeterministic resolution alternative.
     */    
    protected int alternative = 0;
        
    /**
     * {@code true} if the constraint has been solved, {@code false} otherwise.
     */
    private boolean solved = false;
    
    /**
     * {@code true} if the constraint is solved for the first time, {@code false} otherwise.
     */  
    protected boolean firstCall = true;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs a new {@code AConstraint} with a max of {@code MAX_ACONSTRAINT_ARGUMENTS} arguments
     * and with the specified kind of atomic constraint.
     * @param constraintKindCode the code of the kind of the atomic constraint.
     * @param arguments sequence of max {@code MAX_ACONSTRAINT_ARGUMENTS} arguments for the atomic constraint.
     * @throws IllegalStateException if {@code arguments} has more than 4 elements.
     * @see AConstraint#MAX_ACONSTRAINT_ARGUMENTS
     */
    protected AConstraint(int constraintKindCode, @NotNull Object... arguments) {
        Objects.requireNonNull(arguments);

        if(arguments.length > MAX_ACONSTRAINT_ARGUMENTS)
            throw new IllegalArgumentException("MORE THAN 4 PARAMETERS FOR ACONSTRAINT");

   		this.constraintKindCode = constraintKindCode;

   		if(arguments.length >= 1) {
            this.argument1 = arguments[0];
            if (arguments.length >= 2) {
                this.argument2 = arguments[1];
                if (arguments.length >= 3) {
                    this.argument3 = arguments[2];
                    if (arguments.length >= 4)
                        this.argument4 = arguments[3];
                }
            }
        }
   }
    
    /**
     * Constructs an atomic constraint equal to the atomic constraint passed.
     * The arguments are copied by reference if they are not {@code ConstraintClass} or {@code AConstraint},
     * they are cloned otherwise.
     * @param aConstraint an atomic constraint to be copied.
     */
    protected AConstraint(@NotNull AConstraint aConstraint) {
        Objects.requireNonNull(aConstraint);

        //cloning arguments
        this.argument1 = cloneArgumentIfNeeded(aConstraint.argument1);
        this.argument2 = cloneArgumentIfNeeded(aConstraint.argument2);
        this.argument3 = cloneArgumentIfNeeded(aConstraint.argument3);
        this.argument4 = cloneArgumentIfNeeded(aConstraint.argument4);

        //cloning other fields
        this.constraintKindCode = aConstraint.constraintKindCode;
        this.definitionConstraint = aConstraint.definitionConstraint;
        this.alternative = aConstraint.alternative;
        this.solved = aConstraint.solved;
    }

    /**
     * Constructs a new atomic constraint with a list of max {@code MAX_ACONSTRAINT_ARGUMENTS} arguments in {@code arguments}
     * and with its kind identified by the name {@code name}.
     * @param name the name of kind of the atomic constraint.
     * @param arguments sequence of max {@code MAX_ACONSTRAINT_ARGUMENTS} arguments for the atomic constraint.
     * @throws IllegalStateException if {@code arguments} has more than 4 elements.
     * @see AConstraint#MAX_ACONSTRAINT_ARGUMENTS
     */
    protected AConstraint(@NotNull String name, @NotNull Object... arguments) {
        this(Environment.name_to_code_add_if_not_found(name), arguments);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Needed for the visitor pattern.
     * @param visitor visitor for this.
     */
    public @Nullable Object accept(@NotNull Visitor visitor){
        Objects.requireNonNull(visitor);

        return visitor.visit(this);
    }

    /**
     * Checks whether {@code this} object is equal to {@code other}.
     * Two atomic constraints are equal if and only if they have the same kind (i.e. the same {@code constraintKindCode})
     * and if their arguments are equal (both {@code null} or both instantiated and {@code equals}).
     * @param other the object to compare {@code this} with.
     * @return {@code true} if {@code this} is equal to the parameter {@code other}, {@code false} otherwise.
     */
    @Override
    public boolean equals(@Nullable Object other) {
        if(other == null || !(other instanceof AConstraint))
            return false;

        AConstraint otherAConstraint = (AConstraint) other;
        if(this.constraintKindCode != otherAConstraint.constraintKindCode)
            return false;

        for(int i = 0; i < 4; ++i)
            if(!LObject.equals(this.getArg(i), otherAConstraint.getArg(i)))
                return false;
        return true;
    }


    /**
     * Computes and returns a string representation of the atomic constraint.
     * @return a string representation of the atomic constraint.
     */
    @Override
    public @NotNull String toString() {
        String string = ConstraintStringifier.stringify(this);

        assert string != null;
        return string;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Getter for the field {@code definitionConstraint}.
     * @return {@code true} if the constraint is defining an existential temporary variable,
     *  {@code false} otherwise.
     */
    protected boolean getDefinitionConstraint() {
        return definitionConstraint;
    }

    /**
     * Setter for the field {@code definitionConstraint}.
     * @param definitionConstraint its value is {@code true} if the constraint is defining an existential temporary variable,
     *  {@code false} otherwise.
     */
    protected void setDefinitionConstraint(boolean definitionConstraint) {
        this.definitionConstraint = definitionConstraint;
    }

    /**
     * Gets the name identifying the kind atomic constraint.
     * @return A string identifying the kind atomic constraint.
     */
    protected @NotNull String getName() {
         String name = Environment.code_to_name(this.constraintKindCode);
         assert name != null;
         return name;
    }
 
    /**
     * Gets the integer corresponding to the next branch of
     * non deterministic resolution of this constraint.
     * @return An integer corresponding to the next case of 
     * non deterministic resolution.
     */
    protected int getAlternative() {
         return alternative;
    }      

    /**
     * Gets the specified argument of the atomic constraint.
     * @param i index of the argument (starting from 1).
     * @return The {@code i}-th argument (starting from 1) of the atomic constraint.
     */
    protected Object getArg(int i) {   
       if (i==1) return this.argument1;
       if (i==2) return this.argument2;
       if (i==3) return this.argument3;
       if (i==4) return this.argument4;
       else return null;
    }

    /**
     * Checks whether the atomic constraint is solved or not.
     * @return {@code true} if the atomic constraint is solved, {@code false} otherwise.
     */
    protected boolean getSolved(){
        return solved;
    }

    /**
     * Sets the atomic constraint as solved or not.
     * @param solved if {@code true} the atomic constraint will be ignored by the solver.
     */
    protected void setSolved(boolean solved) {
        this.solved = solved;
        return; 
    }
    
    /**
     * Checks whether this constraint is ground or not.
     * @return {@code true} if the atomic constraint is ground, {@code false} otherwise.
     */
    protected boolean isGround() {
    	for (int i=1; i <= 4; i++) {
    	    Object argument = this.getArg(i);
    		if (argument != null)
    		    if(argument instanceof AConstraint && ! ((AConstraint) argument).isGround())
    		        return false;
    		    else if(argument instanceof ConstraintClass && ! ((ConstraintClass) argument).isGround())
    		        return false;
    		    else if(!LObject.isGround(this.getArg(i)))
    		        return false;

    	}
    	return true;
    }


    /**
     * Creates and returns a copy of {@code this} object.
     * The arguments are copied by reference if they are not {@code ConstraintClass} or {@code AConstraint},
     * they are cloned otherwise.
     * Calling this method is equivalent to {@code new AConstraint(this)}.
     * @return a copy of {@code this}.
     */
    @Override
    protected AConstraint clone() {
    	return new AConstraint(this);
    }

    /**
     * Modifies the state of the invocation object to make it the same
     * as the parameter object, copying its member references.
     * After calling this method, calling {@code equals(aConstraint)} on {@code this}
     * object will return {@code true}.
     * @param aConstraint atomic constraint to become equal to.
     */
    protected void setEqualTo(@NotNull AConstraint aConstraint){
        assert aConstraint != null;

        //setting arguments
        this.argument1 = aConstraint.argument1;
        this.argument2 = aConstraint.argument2;
        this.argument3 = aConstraint.argument3;
        this.argument4 = aConstraint.argument4;

        //kind of constraint
        this.constraintKindCode = aConstraint.constraintKindCode;

        //definitionConstraint
        this.definitionConstraint = aConstraint.definitionConstraint;

        //index of next alternative
        this.alternative = aConstraint.alternative;

        //true if the atomic constraint has already been solved
        this.solved = aConstraint.solved;

        assert this.equals(aConstraint);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRAINT METHODS ///////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates and returns a constraint conjunction involving {@code this}
     * constraint and the parameter {@code aConstraint}.
     * @param aConstraint second argument of constraint conjunction.
     * @return a {@code ConstraintClass} which stands for the conjunction of {@code this} atomic constraint
     * with the atomic constraint {@code aConstraint}.
     */
    protected @NotNull
    ConstraintClass and(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;

        ConstraintClass conjunction = new ConstraintClass(this);
        conjunction.add(aConstraint);

        assert conjunction != null;
        return conjunction;
    }

    /**
     * Creates and returns a constraint conjunction involving {@code this}
     * constraint and the parameter {@code constraint}.
     * @param constraint second argument of constraint conjunction.
     * @return a {@code ConstraintClass} which stands for the conjunction of {@code this} atomic constraint
     * with the constraint conjunction {@code constraint}.
     */
    protected @NotNull
    ConstraintClass and(@NotNull ConstraintClass constraint) {
        assert constraint != null;

        ConstraintClass conjunction = new ConstraintClass(this);
        conjunction.addAll(constraint);

        assert conjunction != null;
        return conjunction;
    }

    /**
     * Creates and returns an atomic constraint consisting of the non-deterministic <strong>or</strong>
     * between {@code this} atomic constraint and the atomic constraint {@code aConstraint}.
     * @param aConstraint second argument of the <strong>or</strong> constraint.
     * @return the non-deterministic <strong>or</strong> between {@code this} atomic constraint and the
     * atomic constraint {@code aConstraint}.
     */
    protected @NotNull AConstraint or(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;

        AConstraint disjunction = new AConstraint(Environment.orCode, this, aConstraint);

        assert disjunction != null;
        return disjunction;
    }

    /**
     * Creates and returns an atomic constraint consisting of the non-deterministic <strong>or</strong>
     * between {@code this} atomic constraint and the constraint conjunction {@code constraint}.
     * @param constraint second argument the <strong>or</strong> constraint.
     * @return the non-deterministic <strong>or</strong> between {@code this} constraint and the
     * constraint conjunction {@code constraint}.
     */
    protected @NotNull AConstraint or(@NotNull ConstraintClass constraint) {
        assert constraint != null;

        AConstraint disjunction = new AConstraint(Environment.orCode, this, constraint);

        assert disjunction != null;
        return disjunction;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PRIVATE METHODS //////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Clones the given argument if needed, otherwise returns it.
     * Only arguments of type {@code AConstraint} or {@code ConstraintClass}
     * are cloned (by calling their {@code clone()} method).
     * The returned object is {@code null} if and only if {@code argument} is {@code null}.
     * @param argument argument to clone.
     * @return the argument or its clone depending on its run-time type.
     */
    private @Nullable Object cloneArgumentIfNeeded(@Nullable Object argument){
        if(argument == null)
            return argument;

        Object returnedObject;
        if(argument instanceof AConstraint)
            returnedObject = ((AConstraint) argument).clone();
        else if(argument instanceof ConstraintClass)
            returnedObject = ((ConstraintClass) argument).clone();
        else
            returnedObject = argument;

        assert returnedObject != null;
        return returnedObject;
    }


}

