package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.*;

/**
 * Objects of this class are (possibly uninitialized) logical variables whose values can either be true or false.
 * Objects of this class support many logical operations as constraints (meaning they can be performed when the variables are uninitialized).
 */
public class BoolLVar extends LVar {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs and returns a new constraint conjunction which tells the solver to label the boolean logical variables in {@code list}.
     * @param list a list of boolean logical variables to label. None of them can be {@code null}.
     * @return a new constraint conjunction which tells the solver to label the boolean logical variables in {@code list}.
     * @throws NullPointerException if one of the boolean logical variables in {@code list} is {@code null}.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull List<BoolLVar> list) {
        return label(new LabelingOptions(), list);
    }

    /**
     * Constructs and returns a new constraint conjunction which tells the solver to label the boolean logical variables in {@code list}
     * using the given labeling options.
     * @param labelingOptions the labeling options to use when labeling the boolean logical variables.
     * @param list a list of boolean logical variables to label. None of them can be {@code null}.
     * @return a new constraint conjunction which tells the solver to label the boolean logical variables in {@code list}
     * using the given labeling options.
     * @throws NullPointerException if one of the boolean logical variables in {@code list} is {@code null}.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull LabelingOptions labelingOptions, @NotNull List<BoolLVar> list) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(labelingOptions);
        if(list.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.labelCode, list, labelingOptions);
    }

    /**
     * Constructs and returns a new constraint conjunction which tells the solver to label the boolean logical variables in {@code boolLVars}.
     * @param boolLVars an array containing the boolean logical variables to label. None of them can be {@code null}.
     * @return a new constraint conjunction which tells the solver to label the boolean logical variables in {@code boolLVars}.
     * @throws NullPointerException if one of the boolean logical variables in {@code list} is {@code null}.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull BoolLVar... boolLVars) {
        return label(new ArrayList<>(Arrays.asList(boolLVars)));
    }

    /**
     * Constructs and returns a new constraint conjunction which tells the solver to label the boolean logical variables in {@code boolLVars}
     * using the given labeling options.
     * @param labelingOptions the labeling options to use when labeling the boolean logical variables.
     * @param boolLVars an array containing boolean logical variables to label. None of them can be {@code null}.
     * @return a new constraint conjunction which tells the solver to label the boolean logical variables in {@code boolLVars}
     * using the given labeling options.
     * @throws NullPointerException if one of the boolean logical variables in {@code list} is {@code null}.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull LabelingOptions labelingOptions, @NotNull BoolLVar... boolLVars) {
        return label(labelingOptions, new ArrayList<>(Arrays.asList(boolLVars)));
    }

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * The constraint associated to the this logical boolean variable. It is empty if no constraint is associated.
     */
    private ConstraintClass constraint;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs an uninitialized boolean logical variable with an empty constraint associated and default name.
     */
    public 
    BoolLVar() { 
        this(defaultName());
    }

    /**
     * Constructs an uninitialized boolean logical variable with the given {@code name} and with an empty constraint associated.
     * @param name the name of the variable.
     */
    public 
    BoolLVar(@NotNull String name) {
        super(name);
        this.constraint = new ConstraintClass();
    }

    /**
     * Constructs a boolean logical variable initialized with the value {@code bool} and with an empty constraint associated.
     * @param bool the value to give to the variable.
     */
    public 
    BoolLVar(boolean bool) {
        this(defaultName(), bool);
    }


    /**
     * Constructs a boolean logical variable initialized with the value {@code bool}
     * and with an empty constraint associated and with the given name.
     * @param name the name of the variable.
     * @param bool the value to give to the variable.
     */
    public 
    BoolLVar(@NotNull String name, boolean bool) {
        super(name,bool);
        this.constraint = new ConstraintClass();
    }

    /**
     * Constructs a boolean logical variable which is a equal to the parameter {@code boolLVar} with default name.
     * Using this constructor is equivalent to creating an unbound variable and the bounding it to {@code boolLVar}
     * solving the constraint {@code this.eq(boolLVar)}.
     * @param boolLVar the constructed variable will be bound to this parameter.
     */
    public 
    BoolLVar(@NotNull BoolLVar boolLVar) {
        this(defaultName(), boolLVar);
    }

    /**
     * Constructs a boolean logical variable which is a equal to the parameter {@code boolLVar} with the given name.
     * Using this constructor is equivalent to creating an unbound variable and the bounding it to {@code boolLVar}
     * solving the constraint {@code this.eq(boolLVar)}.
     * @param name the name of the variable
     * @param boolLVar the constructed variable will be bound to this parameter.
     */
     public 
     BoolLVar(@NotNull String name, @NotNull BoolLVar boolLVar) {
         super(name, boolLVar);
         this.constraint = boolLVar.constraint;
     }


    ///////////////////////////////////////////////////////////////
    //////////////// LOGICAL VARIABLE METHODS /////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Gets the value of the variable.
     * @return the value of the variable, {@code null} if the variable is uninitialized.
     */
    @Override
    public @Nullable Boolean
    getValue() { 
        return (Boolean) super.getValue();
    }

    /**
     * Sets the name of the variable to the given name and then returns the variable.
     * @param name the new name of the variable.
     * @return the variable{@code this} with the name changed.
     */
    @Override
    public @NotNull BoolLVar
    setName(@NotNull String name) {
        Objects.requireNonNull(name);

        BoolLVar result = (BoolLVar) super.setName(name);
        assert result == this;
        return result;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// GENERAL UTILITY METHODS //////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Needed for the visitor pattern.
     * @param visitor a visitor.
     * @return the result of the call {@code visitor.visit(this)}.
     */
    public @Nullable Object accept(@NotNull Visitor visitor){
        return visitor.visit(this);
    }

    /**
     * Returns the constraint conjunction associated with this variable.
     * @return the constraint conjunction of this variable.
     */
    public @NotNull
    ConstraintClass
    getConstraint() {
        assert this.constraint != null;
        return this.constraint;
    }

    /**
     * Creates and returns a bit-by-bit copy of the variable.
     * @return a copy of the variable.
     */
    @Override
    public @NotNull BoolLVar clone() {
        //dealing with equ field
        BoolLVar boolLVar = this;
        while(boolLVar.equ != null)
            boolLVar = (BoolLVar)boolLVar.equ;

        //copying fields into a new BoolLVar
        BoolLVar cloned = new BoolLVar();
        cloned.initialized = boolLVar.initialized;
        cloned.name = boolLVar.name;
        cloned.equ  = null;
        cloned.val = boolLVar.val;
        cloned.constraint = boolLVar.constraint;

        assert cloned != null;
        return cloned;
    }

    /**
     * Outputs to standard output a description of the variable,
     * including its name if uninitialized (or value otherwise) and its associated constraint.
     */
    @Override
    public void output() {
        super.outputLine();
        if (!this.isBound() && !this.constraint.isEmpty()) {
            System.out.print(" -- ConstraintClass: ");
            System.out.print(this.constraint);
        }
        System.out.println();
        return;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRAINT METHODS ///////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates and returns a constraint which represents the equality between {@code this} and the parameter {@code other}
     * The constraint created and returned contains the constraints in the field {@code constraint} of both {@code this} and {@code other}
     * @param other The second argument of the equality constraint.
     * @return a constraint which represents the equality between {@code this} and the parameter {@code other}.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull BoolLVar other) {
        Objects.requireNonNull(other);

        ConstraintClass result =  boolConstraint(other, Environment.eqCode);
        assert result != null;
        return result;
    }

    /**
     * Creates and returns a constraint which represents the equality between {@code this} and the parameter {@code bool}
     * The constraint created and returned contains the constraints in the field {@code constraint} of {@code this}.
     * @param bool The second argument of the equality constraint.
     * @return a constraint which represents the equality between {@code this} and the parameter {@code bool}.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull Boolean bool) {
        Objects.requireNonNull(bool);

        ConstraintClass result =  boolConstraint(bool, Environment.eqCode);
        assert result != null;
        return result;
    }

    /**
     * Creates and returns a constraint which represents the inequality between {@code this} and the parameter {@code other}
     * The constraint created and returned contains the constraints in the field {@code constraint} of both {@code this} and {@code other}.
     * @param other The second argument of the inequality constraint.
     * @return a constraint which represents the inequality between {@code this} and the parameter {@code other}.
     */
    public @NotNull
    ConstraintClass
    neq(@NotNull BoolLVar other) {
        Objects.requireNonNull(other);

        ConstraintClass result = boolConstraint(other, Environment.neqCode);
        assert result != null;
        return result;
    }

    /**
     * Creates and returns a constraint which represents the inequality between {@code this} and the parameter {@code bool}
     * The constraint created and returned contains the constraints in the field {@code constraint} of {@code this}.
     * @param bool The second argument of the inequality constraint.
     * @return a constraint which represents the inequality between {@code this} and the parameter {@code bool}.
     */
    public ConstraintClass
    neq(@NotNull Boolean bool) {
        Objects.requireNonNull(bool);

        ConstraintClass result = boolConstraint(bool, Environment.neqCode);
        assert result != null;
        return result;
    }


    /**
     * Constructs and returns a new constraint conjunction which contains
     * the atomic constraint of kind {@code constraintCode} with {@code this} as the first argument
     * and {@code other} as the second argument and each atomic constraint
     * in either {@code this.constraint} or {@code other.constraint}.
     * @param other the second argument of the atomic constraint.
     * @param constraintCode a code for the kind of atomic constraint to create.
     * @return the constraint conjunction created which is of the form
     * atomic_constraint({@code this}, {@code other}) ^ {@code this.constraint} ^ {@code other.constraint}.
     */
    private @NotNull
    ConstraintClass
    boolConstraint(@NotNull BoolLVar other, int constraintCode) {
        Objects.requireNonNull(other);

        ConstraintClass cc = new ConstraintClass();
        cc.add(new AConstraint(constraintCode, this, other));
        cc.addAll(this.constraint);
        cc.addAll(other.constraint);

        assert cc != null;
        return cc;
    }

    /**
     * Constructs and returns a new constraint conjunction which contains
     * the atomic constraint of kind {@code constraintCode} with {@code this} as the first argument
     * and {@code bool} as the second argument and each atomic constraint in {@code this.constraint}.
     * @param bool the second argument of the atomic constraint.
     * @param constraintCode a code for the kind of atomic constraint to create
     * @return the constraint conjunction created which is of the form
     * atomic_constraint({@code this}, {@code bool}) ^ {@code this.constraint}.
     */
    private @NotNull
    ConstraintClass
    boolConstraint(@NotNull Boolean bool, int constraintCode) {
        Objects.requireNonNull(bool);

        ConstraintClass cc = new ConstraintClass();
        cc.add(new AConstraint(constraintCode, this, bool));
        cc.addAll(this.constraint);

        assert cc != null;
        return cc;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// EXPRESSION METHODS ///////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Creates and returns a boolean logical variable which is the result of the expression {@code this} and {@code other}.
     * The result of the expression contains a constraint conjunction which is the conjunction of {@code this.constraint}
     * and {@code other.constraint} with the atomic constraint <strong>result = this AND other</strong>.
     * @param other The second argument of the and expression.
     * @return a boolean logical variable which is the result of the expression {@code this} and {@code other}.
     */
    public @NotNull BoolLVar
    and(@NotNull BoolLVar other) {
        Objects.requireNonNull(other);

        BoolLVar result = boolExpr(other, Environment.andBoolCode);
        assert result != null;
        return result;
    }

    /**
     * Creates and returns a boolean logical variable which is the result of the expression {@code this} or {@code other}.
     * The result of the expression contains a constraint conjunction which is the conjunction of {@code this.constraint}
     * and {@code other.constraint} with the atomic constraint <strong>result = this || other</strong>.
     * @param other The second argument of the or expression.
     * @return a boolean logical variable which is the result of the expression {@code this} or {@code other}.
     */
    public @NotNull BoolLVar
    or(@NotNull BoolLVar other) {
        Objects.requireNonNull(other);

        BoolLVar result = boolExpr(other, Environment.orBoolCode);
        assert result != null;
        return result;
    }

    /**
     * Creates and returns a boolean logical variable which is the result of the expression {@code this} implies {@code other}.
     * The result of the expression contains a constraint conjunction which is the conjunction of {@code this.constraint}
     * and {@code other.constraint} with the atomic constraint <strong>result = this implies other</strong>.
     * @param other The second argument of the implication.
     * @return a boolean logical variable which is the result of the expression {@code this} implies {@code other}.
     */
    public @NotNull BoolLVar
    implies(@NotNull BoolLVar other) {
        Objects.requireNonNull(other);

        BoolLVar result = boolExpr(other, Environment.impliesBoolCode);
        assert result != null;
        return result;
    }

    /**
     * Creates and returns a boolean logical variable which is the result of the expression {@code this} iff {@code other}.
     * The result of the expression contains a constraint conjunction which is the conjunction of {@code this.constraint}
     * and {@code other.constraint} with the atomic constraint <strong>result = this iff other</strong>.
     * @param other The second argument of the iff.
     * @return a boolean logical variable which is the result of the expression {@code this} iff {@code other}
     */
    public @NotNull BoolLVar
    iff(@NotNull BoolLVar other) {
        Objects.requireNonNull(other);

        BoolLVar result = boolExpr(other, Environment.iffBoolCode);
        assert result != null;
        return result;
    }

    /**
     * Creates and returns a boolean logical variable which is the result of the expression not({@code this}) .
     * The result of the expression contains the constraint conjunction {@code this.constraint} ^ not({@code this})
     * @return a boolean logical variable which is the result of the expression not({@code this}).
     */
    public @NotNull BoolLVar
    not() {
        BoolLVar result = boolExpr(Environment.notBoolCode);
        assert result != null;
        return result;
    }

    /**
     * Tests whether the value is true or not.
     * @return {@code true} if the variable is initialized with value true, {@code false} otherwise.
     */
    public boolean
    isTrue() {
        return isBound() && getValue();
    }

    /**
     * Tests whether the value is false or not.
     * @return {@code true} if the variable is initialized with value false, {@code false} otherwise.
     */
    public boolean
    isFalse() {
        return isBound() && !getValue();
    }

    /**
     * Creates and returns a boolean logical variable which is the result of the expression denoted by {@code constraintCode}.
     * The result of the expression contains the constraint conjunction {@code this.constraint} in conjunction with
     * the atomic constraint denoted by {@code constraintCode}.
     *
     * @param constraintCode integer defining the kind of constraint to bind to the logical boolean variable
     * @return a boolean logical variable which is the result of the expression denoted by {@code constraintCode}.
     */
    private @NotNull BoolLVar
    boolExpr(int constraintCode) {
        BoolLVar boolLVar = new BoolLVar();
        boolLVar.constraint.add(new AConstraint(constraintCode, boolLVar, this));
        boolLVar.constraint.addAll(this.constraint);

        assert boolLVar != null;
        return boolLVar;
    }

    /**
     * Creates and returns a boolean logical variable which is the result of the expression denoted by {@code constraintCode}.
     * The result of the expression contains a constraint conjunction which is the conjunction of {@code this.constraint}
     * and {@code other.constraint} with an atomic constraint with the kind denoted by {@code constraintCode}.
     * @param other The second argument of the atomic constraint to create.
     * @param constraintCode integer defining the kind of constraint to bind to the logical boolean variables.
     * @return a boolean logical variable which is the result of the expression denoted by {@code constraintCode}.
     */
    private @NotNull BoolLVar
    boolExpr(@NotNull BoolLVar other, int constraintCode) {
        Objects.requireNonNull(other);

        BoolLVar boolLVar = new BoolLVar();
        boolLVar.constraint.add(new AConstraint(constraintCode, boolLVar, this, other));
        boolLVar.constraint.addAll(other.constraint);
        boolLVar.constraint.addAll(this.constraint);

        assert boolLVar != null;
        return boolLVar;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// LABELING METHODS /////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs and returns a new constraint conjunction which tells the solver to label the object.
     * @return a new constraint conjunction which tells the solver to label the object.
     */
    public @NotNull
    ConstraintClass
    label() {
        ConstraintClass result = label(new LabelingOptions());
        assert result != null;
        return result;
    }
    /**
     * Constructs and returns a new constraint conjunction which tells the solver to label the object using the given heuristic.
     * @param boolHeuristic the heuristic to use when labeling the object.
     * @return a new constraint conjunction which tells the solver to label the object using the given heuristic.
     */
    public @NotNull
    ConstraintClass
    label(@NotNull BoolHeuristic boolHeuristic) {
        Objects.requireNonNull(boolHeuristic);

        LabelingOptions labelingOptions = new LabelingOptions();
        labelingOptions.valueForBoolLVar = boolHeuristic;
        ConstraintClass result = label(labelingOptions);
        assert result != null;
        return result;
    }

    /**
     * Constructs and returns a new constraint conjunction which tells the solver to label the object using the given labeling options.
     * @param labelingOptions the labeling options to use when labeling the object.
     * @return a new constraint conjunction which tells the solver to label the object using the given labeling options.
     */
    public @NotNull
    ConstraintClass
    label(@NotNull LabelingOptions labelingOptions) {
        Objects.requireNonNull(labelingOptions);

        ConstraintClass constraint = new ConstraintClass();
        constraint.add(new AConstraint(Environment.labelCode, this, labelingOptions));
        constraint.addAll(this.constraint);

        assert constraint != null;
        return constraint;
    }

}
