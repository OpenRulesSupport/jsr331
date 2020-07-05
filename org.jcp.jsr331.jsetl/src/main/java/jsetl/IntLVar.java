package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.NotValidDomainException;

import java.util.*;

/**
 * Objects of this class are logical variables whose values can only be integers.
 * These objects support common arithmetic constraints such as multiplication, module, labeling,...
 */
public class IntLVar extends LVar implements Visitable {

    /////////////////////////////////////////////////////////
    //////////////// STATIC METHODS /////////////////////////
    /////////////////////////////////////////////////////////

    /**
     * A constraint which labels each variable in {@code list} giving them subsequent values taken from their domains.
     *
     * @param list list of integer variables to label. None of them can be {@code null}.
     * @return the generated constraint.
     * @throws NullPointerException if some of the values in {@code list} is {@code null}.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull List<IntLVar> list) {
        Objects.requireNonNull(list);
        if(list.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        ConstraintClass result = label(new LabelingOptions(), list);
        assert result != null;
        return result;
    }

    /**
     * A constraint which labels each variable in {@code list} giving them subsequent values taken from their domains.
     * It uses the given labeling options.
     *
     * @param labelingOptions labeling options to use.
     * @param list list of integer variables to label. None of them can be {@code null}.
     * @return the generated constraint.
     * @throws NullPointerException if some of the values in {@code list} is {@code null}.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull LabelingOptions labelingOptions, @NotNull List<IntLVar> list) {
        Objects.requireNonNull(list);
        if(list.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");
        Objects.requireNonNull(labelingOptions);

        ArrayList<IntLVar> arrayList;
        if(list instanceof IntLVar)
            arrayList = (ArrayList<IntLVar>) list;
        else
            arrayList = new ArrayList<>(list);

        return new ConstraintClass(Environment.labelCode, arrayList, labelingOptions);
    }

    /**
     * A constraint which labels each variable in {@code intLVars} giving them subsequent values taken from their domains.
     *
     * @param intLVars array of integer variables to label. None of them can be {@code null}.
     * @return the generated constraint.
     * @throws NullPointerException if some of the values in {@code intLVars} is {@code null}.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull IntLVar... intLVars) {
        Objects.requireNonNull(intLVars);
        if(Arrays.stream(intLVars).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        LabelingOptions labelingOptions = new LabelingOptions();
        ConstraintClass result = label(labelingOptions, intLVars);
        assert result != null;
        return result;
    }

    /**
     * A constraint which labels each variable in {@code intLVars} giving them subsequent values taken from their domains.
     * It uses the given labeling options.
     *
     * @param labelingOptions labeling options to use.
     * @param intLVars array of integer variables to label. None of them can be {@code null}.
     * @return the generated constraint.
     * @throws NullPointerException if some of the values in {@code intLVars} is {@code null}.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull LabelingOptions labelingOptions, @NotNull IntLVar... intLVars) {
        ConstraintClass result = label(labelingOptions, Arrays.asList(intLVars));
        assert result != null;
        return result;
    }


    /////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS ///////////////////////////
    /////////////////////////////////////////////////////////

    /**
     * This variable is {@code true} if the variable is a dummy variable generated as
     * the result of an Integer Expression, {@code false} otherwise.
     */
    private boolean dummy = false;

    /**
     * Reference to the domain of the integer variable
     */
    private MultiInterval domain;

    /**
     * ConstraintClass conjunction associated to this variable
     */
    protected ConstraintClass constraint;


    /////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS ///////////////////////////
    /////////////////////////////////////////////////////////
    
    /**
     * Constructs a not initialized integer logical variable with a default name.
     */
    public 
    IntLVar() { 
        this(defaultName());
    }

    /**
     * Constructs a not initialized integer logical variable with the given name name.
     * @param name the name of the variable.
     */
    public 
    IntLVar(@NotNull String name) {
        this(name, MultiInterval.universe());
        assert name != null;
    }

    /**
     * Constructs a logical variable with an integer value and default name.
     * @param value the value of the logical variable.
     * @throws NotValidDomainException if {@code value} is not representable by {@code MultiInterval}.
     */
    public 
    IntLVar(@NotNull Integer value)
    throws NotValidDomainException {
        this(defaultName(), value);
        assert value != null;
    }

    /**
     * Constructs a logical variable with an integer value {@code value} and a specified name.
     * @param name the name of the variable.
     * @param value the value of the variable.
     * @throws NotValidDomainException if {@code value} is not representable by {@code MultiInterval}.
     */
    public 
    IntLVar(@NotNull String name, @NotNull Integer value)
    throws NotValidDomainException {
        this(name, new MultiInterval(value));

        assert name != null;
        assert value != null;

    }

    /**
     * Constructs a logical variable equal to the logical variable intLVar.
     * Using this constructor is equivalent to creating an unbound {@code IntLVar}
     * and posting and solving the constraint {@code this.eq(intLVar)}.
     * @param intLVar the logical variable to copy.
     */
    public 
    IntLVar(@NotNull IntLVar intLVar) {
        this(defaultName(), intLVar);

        assert intLVar != null;
    }

    /**
     * Constructs a logical variable equal to the logical variable {@code intLVar} and with a specified name.
     * Using this constructor is equivalent to creating an unbound {@code IntLVar}
     * and posting and solving the constraint {@code this.eq(intLVar)}.
     * @param name the name of the variable.
     * @param intLVar the logical variable to copy.
     */
     public 
     IntLVar(@NotNull String name, @NotNull IntLVar intLVar) {
         super(name, intLVar);
         
         assert name != null;
         assert intLVar != null;
         
         this.domain = intLVar.domain;
         this.constraint = intLVar.constraint;
     }

     /**
      * Constructs an (unbound) logical variable with domain [{@code glb, lub}] with default name.
      * @param glb greatest lower bound of the domain.
      * @param lub least upper bound of the domain.
      * @throws NotValidDomainException if [{@code glb, lub}] is empty.
      */
     public 
     IntLVar(@NotNull Integer glb, @NotNull Integer lub)
     throws NotValidDomainException { 
        this(defaultName(), glb, lub);
        assert glb != null;
        assert lub != null;
     }

     /**
      * Constructs an (unbound) logical variable with domain [glb,lub] and with glb specified name.
      * @param name name of the variable.
      * @param glb greatest lower bound of the domain.
      * @param lub least upper bound of the domain.
      * @throws NotValidDomainException if [{@code glb, lub}] is empty.
      */
     public 
     IntLVar(@NotNull String name, @NotNull Integer glb, @NotNull Integer lub)
     throws NotValidDomainException {
         this(name, new MultiInterval(glb, lub));
         assert name != null;
         assert glb != null;
         assert lub != null;
     }
     
     /**
      * Constructs an (unbound) logical variable with a given domain and default name.
      * @param domain domain of the variable.
      * @throws NotValidDomainException if {@code domain} is empty.
      */
     public 
     IntLVar(@NotNull MultiInterval domain)
     throws NotValidDomainException { 
        this(defaultName(), domain);
        assert domain != null;
     }

     /**
      * Constructs an (unbound) logical variable with given domain and with a specified name.
      * @param name name of the variable.
      * @param domain domain of the variable.
      * @throws NotValidDomainException if {@code domain} is empty.
      */
     public 
     IntLVar(@NotNull String name, @NotNull MultiInterval domain)
     throws NotValidDomainException {
         super(name);
         assert name != null;
         assert domain != null;

         if (domain.isEmpty())
             throw new NotValidDomainException();
         if (domain.isSingleton()) {
             this.initialized = true;
             this.val = domain.getGlb();
         }
         this.domain = domain;
         this.constraint = new ConstraintClass();
     }


    /////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS /////////////////////////
    /////////////////////////////////////////////////////////

    /**
     * Needed for the Visitor pattern.
     * @param visitor the visitor that wants to visit the object.
     * @return the result of the call {@code visitor.visit(this)}.
     */
    public @NotNull Object accept(@NotNull Visitor visitor){
        assert visitor != null;
        return visitor.visit(this);
    }

    /**
     * returns the value of this variable, if it is initialized, {@code null} otherwise.
     * @return the value of this variable.
     */
    @Override
    public @Nullable Integer
    getValue() { 
        return (Integer)super.getValue();
    }

    /**
     * Sets the name of this logical variable to the given name, then returns it.
     * @param name new name for the variable.
     * @return this variable.
     */
    @Override
    public @NotNull IntLVar
    setName(@NotNull String name) {
        Objects.requireNonNull(name);
        IntLVar result = (IntLVar)super.setName(name);
        assert result != null;
        assert result == this;
        return result;
    }

    /**
     * Sets the value of this logical variable then returns it.
     * @param value the new value for this variable.
     * @return this variable.
     * @throws NullPointerException if {@code value} is not an {@code Integer}.
     */
    @Override
    public @NotNull IntLVar setValue(@NotNull Object value) {
        Objects.requireNonNull(value);
        if(!(value instanceof Integer))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");
        super.setValue(value);
        this.domain = new MultiInterval((Integer)value);
        return this;
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
     * Sets the constraint conjunction of this variable to the parameter {@code constraint}.
     * @param constraint the new constraint conjunction.
     */
    protected void
    setConstraint(ConstraintClass constraint) {
        this.constraint = constraint;
     }

    /**
     * Getter for domain.
     * @return the domain of this variable.
     */
    public @NotNull MultiInterval
    getDomain() {
        MultiInterval result;
        if (this.equ == null)
            result = domain;
        else 
            result = ((IntLVar) this.equ).getDomain();
        assert result != null;
        return result;
    }

    /**
     * Setter for domain
     * @param domain new domain for this variable
     */
    protected void
    setDomain(@NotNull MultiInterval domain) {
        assert domain != null;
        if (this.equ == null) {
            this.initialized = false;
            this.domain = domain;
        } else 
            ((IntLVar) this.equ).setDomain(domain);
    }

    /**
     * Constructs and returns a (shallow) copy of this variable.
     * @return a (shallow) copy of this variable.
     */
     @Override
     public @NotNull IntLVar
     clone() {     
       if (this.equ == null) {
           IntLVar lvarClone = new IntLVar();
           lvarClone.initialized = this.initialized;
           lvarClone.name = this.name;
           lvarClone.equ  = null;
           lvarClone.val = this.val;
           lvarClone.constraint = this.constraint;
           lvarClone.domain = this.domain.clone();

           return lvarClone;
       }
       else 
           return ((IntLVar)this.equ).clone();
     }

    /**
     * Checks whether this variable is equal to the parameter {@code other}.
     * @param other the other variable.
     * @return {@code true} if {@code this} and {@code other} are equal, {@code false} otherwise.
     */
     @Override
     public boolean 
     equals(@Nullable Object other) {
         if(other == null)
             return false;
         if(other == this)
             return true;
         if(other instanceof IntLVar) {
             IntLVar intLVar = (IntLVar) other;
             if (equ != null)
                 return getEndOfEquChain().equals(intLVar);
             if (intLVar.equ != null)
                 return equals(intLVar.getEndOfEquChain());
             else
                 return super.equals(intLVar) && (this.domain).equals(intLVar.domain);
         }
         else if(other instanceof Integer){
             Integer integer = (Integer) other;
             return this.isBound() && this.getValue().equals(integer);
         }
         else
             return false;
     }

    /**
     *  Prints the name, the value and the domain of this variable to standard output.
     *  The format of the output is: <br>
     *      "variable_name = variable_value -- ConstraintClass: constraint" if the variable is bound to a value.
     *      If the domain has no restrictions the output will be
     *      "variable_name = unknown -- ConstraintClass: constraint".
     *      If the domain has a restriction on the upper bound and the domain is a single interval the output will be
     *      "variable_name = unknown, variable_name =< upper_bound -- ConstraintClass: constraint".
     *      If the domain has a restriction on the lower bound and the domain is a single interval the output will be
     *      "variable_name = unknown, variable_name >= lower_bound -- ConstraintClass: constraint".
     *      If the domain has restriction on both upper and lower bounds and the domain is a single interval the output will be
     *      "variable_name = unknown, lower_bound =< variable_name =< upper_bound -- ConstraintClass: constraint".
     *      Otherwise the output will be
     *      "variable_name = variable_vale -- Domain: domain -- ConstraintClass: constraint"
     *      The variable_value may be unknown.
     *      Examples:
     *      "_a = unknown, a =< 3 -- ConstraintClass: _a = _x + _N8 AND _N8 = _y - 1"
     *      "_b = unknown, b >= 3 -- ConstraintClass: _b = _x + _N8 AND _N8 = _y - 1"
     *      "_c = unknown, 3 =< _c =< 4 -- ConstraintClass: _c = _x + _N8 AND _N8 = _y - 1"
     *      "_d = unknown -- ConstraintClass: _d = _x + _N8 AND _N8 = _y - 1"
     *      "_e = 3 -- ConstraintClass: _e = _x + _N8 AND _N8 = _y - 1"
     *      "_f = unknown -- Domain: {-10..10, 20..40} -- ConstraintClass: _z = _x + _N8 AND _N8 = _y - 1"
     */
     @Override
     public void 
     output() {
        super.outputLine();
        if (!this.isBound()) {
            if(this.domain.getIntervals().size() == 1) {
                if (this.domain != null && !this.domain.isUniverse()) {
                    if (this.domain.getGlb().equals(MultiInterval.INF))
                        System.out.print(", _" + this.getName() + " =< " + this.domain.getLub());
                    else if (this.domain.getLub().equals((MultiInterval.SUP)))
                        System.out.print(", _" + this.getName() + " >= " + this.domain.getGlb());
                    else
                        System.out.print(", " + this.getDomain().getGlb() + " =< _" + this.getName() + " =< " + this.getDomain().getLub());
                }
                if (!this.constraint.isEmpty()) {
                    System.out.print(" -- ConstraintClass: ");
                    System.out.print(this.constraint);
                }
            }
            else{
                if (this.domain != null)
                    System.out.print(" -- Domain: " + this.getDomain());
                if (!this.constraint.isEmpty()) {
                    System.out.print(" -- ConstraintClass: ");
                    System.out.print(this.constraint);
                }
            }
        }
        System.out.println();
    }


    /////////////////////////////////////////////////////////
    //////////////// INTLVAR EXPRESSIONS METHODS ////////////
    /////////////////////////////////////////////////////////
    
    /**
     * Constructs and returns a logical variable which is the sum of {@code this} with the parameter {@code intLVar}.
     * @param intLVar the second parameter of the sum.
     * @return a logical variable which is the sum of {@code this} with the parameter {@code intLVar}.
     */
    public @NotNull IntLVar
    sum(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        IntLVar result = LvarExprGenExpr(intLVar, Environment.sumCode);

        assert result != null;
        return result;
    }

    /**
     * Constructs and returns a logical variable which is the sum of {@code this} with the parameter {@code integer}.
     * @param integer the second parameter of the sum.
     * @return a logical variable which is the sum of {@code this} with the parameter {@code integer}.
     */
    public @NotNull IntLVar
    sum(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        IntLVar result = LvarExprGenObj(integer, Environment.sumCode);
        assert result != null;
        return result;
    }

    /**
     * Constructs and returns a logical variable which is the subtraction of {@code this} with the parameter {@code intLVar}.
     * @param intLVar the second parameter of the subtraction.
     * @return a logical variable which is the subtraction of {@code this} with the parameter {@code intLVar}.
     */
    public @NotNull IntLVar
    sub(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        IntLVar result = LvarExprGenExpr(intLVar, Environment.subCode);
        assert result != null;
        return result;
    }

    /**
     * Constructs and returns a logical variable which is the subtraction of {@code this} with the parameter {@code integer}.
     * @param integer the second parameter of the subtraction.
     * @return a logical variable which is the subtraction of {@code this} with the parameter {@code integer}.
     */
    public @NotNull IntLVar
    sub(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        IntLVar result = LvarExprGenObj(integer, Environment.subCode);
        assert result != null;
        return result;
    }


    /**
     * Constructs and returns a logical variable which is the multiplication of {@code this} with the parameter {@code intLVar}.
     * @param intLVar the second parameter of the multiplication.
     * @return a logical variable which is the multiplication of {@code this} with the parameter {@code intLVar}.
     */
    public @NotNull IntLVar
    mul(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        IntLVar result = LvarExprGenExpr(intLVar, Environment.mulCode);
        assert result != null;
        return result;
    }

    /**
     * Constructs and returns a logical variable which is the multiplication of {@code this} with the parameter {@code integer}.
     * @param integer the second parameter of the multiplication.
     * @return a logical variable which is the multiplication of {@code this} with the parameter {@code integer}.
     */
    public @NotNull IntLVar
    mul(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        IntLVar result = LvarExprGenObj(integer, Environment.mulCode);
        assert result != null;
        return result;
    }


    /**
     * Constructs and returns a logical variable which is the (exact) division of {@code this} with the parameter {@code intLVar}.
     * @param intLVar the second parameter of the division.
     * @return a logical variable which is the division of {@code this} with the parameter {@code intLVar}.
     */
    public @NotNull IntLVar
    div(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        AConstraint newAconstraint = new AConstraint(Environment.neqCode, intLVar, 0);
        newAconstraint.setDefinitionConstraint(true);

        IntLVar module = LvarExprGenExpr(intLVar, Environment.divCode);
        module.constraint.add(newAconstraint);

        assert module != null;
        return module;
    }

    /**
     * Constructs and returns a logical variable which is the (exact) division of {@code this} with the parameter {@code integer}.
     * @param integer the second parameter of the division.
     * @return a logical variable which is the division of {@code this} with the parameter {@code integer}.
     */
    public @NotNull IntLVar
    div(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        IntLVar module = LvarExprGenObj(integer, Environment.divCode);

        if(integer == 0) {
            AConstraint newAconstraint = new AConstraint(Environment.neqCode, integer, 0);
            newAconstraint.setDefinitionConstraint(true);
            module.constraint.add(newAconstraint);
        }

        assert module != null;
        return module;
    }

    /**
     * Constructs and returns a logical variable which is the truncated division of {@code this} with the parameter {@code intLVar}
     * @param intLVar the second parameter of the truncated division
     * @return a logical variable which is the truncated division of {@code this} with the parameter {@code intLVar}
     */
    public @NotNull IntLVar
    truncDiv(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        IntLVar mod = this.mod(intLVar);
        IntLVar truncatedDivision = new IntLVar();
        truncatedDivision.constraint.addAll(this.eq(intLVar.mul(truncatedDivision).sum(mod)));

        assert truncatedDivision != null;
        return truncatedDivision;
    }

    /**
     * Constructs and returns a logical variable which is the truncated division of {@code this} with the parameter {@code integer}.
     * @param integer the second parameter of the truncated division.
     * @return a logical variable which is the truncated division of {@code this} with the parameter {@code integer}.
     */
    public @NotNull IntLVar
    truncDiv(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        IntLVar mod = this.mod(integer);
        IntLVar truncatedDivision = new IntLVar();
        truncatedDivision.constraint.addAll(this.eq(truncatedDivision.mul(integer).sum(mod)));
        return truncatedDivision;
    }

    /**
     * Constructs and returns a logical variable which is equal to {@code this} mod {@code intLVar}.
     * @param intLVar the second parameter of the module.
     * @return a logical variable which is the module of {@code this} with the parameter {@code intLVar}.
     */
    public @NotNull IntLVar
    mod(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);
    	
        AConstraint newAconstraint = new AConstraint(Environment.neqCode, intLVar, 0);
        newAconstraint.setDefinitionConstraint(true);

        IntLVar module = LvarExprGenExpr(intLVar, Environment.modCode);
        module.constraint.add(newAconstraint);

        assert module != null;
        return module;
    }

    /**
     * Constructs and returns a logical variable which is equal to {@code this} mod {@code integer}.
     * @param integer the second parameter of the module.
     * @return a logical variable which is the module of {@code this} with the parameter {@code integer}.
     */
    public @NotNull IntLVar
    mod(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        IntLVar module = LvarExprGenObj(integer, Environment.modCode);

        if(integer == 0) {
            AConstraint newAconstraint = new AConstraint(Environment.neqCode, integer, 0);
            newAconstraint.setDefinitionConstraint(true);
            module.constraint.add(newAconstraint);
        }

        assert module != null;
        return module;
    }

    /**
     * Constructs and returns a logical variable which is the absolute value of {@code this}.
     * @return a logical variable which is the absolute value of {@code this}.
     */
    public @NotNull IntLVar
    abs() {
        IntLVar y = LvarExprGenExpr(Environment.absCode);
        y.constraint.add(new AConstraint(Environment.geCode, y, 0));
        assert y != null;
        return y;
    }


    /////////////////////////////////////////////////////////
    //////////////// CONSTRAINT METHODS /////////////////////
    /////////////////////////////////////////////////////////

    /**
     * ConstraintClass of equality between {@code this} and {@code intLVar}.
     * @param intLVar second argument of equality.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "equality").
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        ConstraintClass result = ArithConstGenExpr(intLVar, Environment.eqCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass of equality between {@code this} and {@code integer}.
     * @param integer second argument of equality.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "equality").
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        ConstraintClass result = ArithConstGenObj(integer, Environment.eqCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass of inequality between {@code this} and {@code intLVar}.
     * @param intLVar second argument of inequality.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "inequality").
     */
    public @NotNull
    ConstraintClass
    neq(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        ConstraintClass result = ArithConstGenExpr(intLVar, Environment.neqCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass of inequality between {@code this} and {@code integer}.
     * @param integer second argument of inequality.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "inequality").
     */
    public @NotNull
    ConstraintClass
    neq(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        ConstraintClass result = ArithConstGenObj(integer, Environment.neqCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass which is satisfied if and only if {@code this} is less than or equal to {@code intLVar}.
     * @param intLVar second argument of the constraint.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "less than or equal").
     */
    public @NotNull
    ConstraintClass
    le(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        ConstraintClass result = ArithConstGenExpr(intLVar, Environment.leCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass which is satisfied if and only if {@code this} is less than or equal to {@code integer}.
     * @param integer second argument of the constraint.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "less than or equal").
     */
    public @NotNull
    ConstraintClass
    le(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        ConstraintClass result = ArithConstGenObj(integer, Environment.leCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass which is satisfied if and only if {@code this} is less than {@code intLVar}.
     * @param intLVar second argument of the constraint.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "less than").
     */
    public @NotNull
    ConstraintClass
    lt(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        ConstraintClass result = ArithConstGenExpr(intLVar, Environment.ltCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass which is satisfied if and only if {@code this} is less than {@code integer}.
     * @param integer second argument of the constraint.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "less than").
     */
    public @NotNull
    ConstraintClass
    lt(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        ConstraintClass result = ArithConstGenObj(integer, Environment.ltCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass which is satisfied if and only if {@code this} is greater than or equal to {@code intLVar}.
     * @param intLVar second argument of the constraint.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "greater than or equal").
     */
    public @NotNull
    ConstraintClass
    ge(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        ConstraintClass result = ArithConstGenExpr(intLVar, Environment.geCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass which is satisfied if and only if {@code this} is greater than or equal to {@code integer}.
     * @param integer second argument of the constraint.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "greater than or equal").
     */
    public @NotNull
    ConstraintClass
    ge(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        ConstraintClass result = ArithConstGenObj(integer, Environment.geCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass which is satisfied if and only if {@code this} is greater than {@code intLVar}.
     * @param intLVar second argument of the constraint.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "greater than").
     */
    public @NotNull
    ConstraintClass
    gt(@NotNull IntLVar intLVar) {
        Objects.requireNonNull(intLVar);

        ConstraintClass result = ArithConstGenExpr(intLVar, Environment.gtCode);
        assert result != null;
        return result;
    }

    /**
     * ConstraintClass which is satisfied if and only if {@code this} is greater than {@code integer}.
     * @param integer second argument of the constraint.
     * @return the generated constraint conjunction (consisting of a single atomic constraint of kind "greater than").
     */
    public @NotNull
    ConstraintClass
    gt(@NotNull Integer integer) {
        Objects.requireNonNull(integer);

        ConstraintClass result = ArithConstGenObj(integer, Environment.gtCode);
        assert result != null;
        return result;
    }

    /**
     * Creates a constraint which is satisfiable if and only if {@code this} is an element of {@code setLVar}.
     * @param setLVar a logical set, possibly uninitialized.
     * @return a constraint which is satisfiable if and only if {@code this} is an element of {@code setLVar}.
     */
    public @NotNull
    ConstraintClass
    in(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);

        ConstraintClass result = new ConstraintClass();
        AConstraint inAConstraint = new AConstraint(Environment.inCode, this, setLVar);
        result.add(inAConstraint);
        result.addAll(this.constraint);

        assert result != null;
        return result;
    }

    /**
     * Creates a constraint which is satisfiable if and only if {@code this} is an element of {@code multiInterval}.
     * @param multiInterval a multi-interval.
     * @return a constraint which is satisfiable if and only if {@code this} is an element of {@code multiInterval}.
     */
    public @NotNull
    ConstraintClass
    in(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);

        ConstraintClass result = this.dom(multiInterval);
        return result;
    }

    /**
     * Creates a constraint which is satisfiable if and only if {@code this} is not an element of {@code setLVar}.
     * @param setLVar a logical set, possibly uninitialized.
     * @return a constraint which is satisfiable if and only if {@code this} is not an element of {@code setLVar}.
     */
    public @NotNull
    ConstraintClass
    nin(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);

        ConstraintClass result = new ConstraintClass();
        AConstraint ninAConstraint = new AConstraint(Environment.ninCode, this, setLVar);
        result.add(ninAConstraint);
        result.addAll(this.constraint);

        assert result != null;
        return result;
    }

    /**
     * Creates a constraint which is satisfiable if and only if {@code this} is not an element of {@code multiInterval}.
     * @param multiInterval a multi-interval.
     * @return a constraint which is satisfiable if and only if {@code this} is not an element of {@code multiInterval}.
     */
    public @NotNull
    ConstraintClass
    nin(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);

        ConstraintClass result = this.ndom(multiInterval);
        assert result != null;
        return result;
    }

    /**
     * Generates glb constraint which is satisfiable if and only if {@code this} is in the interval {@code [glb,lub]}.
     * @param glb greatest lower bound of the interval.
     * @param lub least upper bound of the interval.
     * @return the generated constraint.
     * @throws NotValidDomainException if {@code [glb, lub]} is empty.
     */
    public @NotNull
    ConstraintClass
    dom(@NotNull Integer glb, @NotNull Integer lub) throws NotValidDomainException{
        Objects.requireNonNull(glb);
        Objects.requireNonNull(lub);

        MultiInterval multiInterval = new MultiInterval(glb, lub);
        if(multiInterval.isEmpty())
            throw new NotValidDomainException();
        ConstraintClass result = dom(multiInterval);
        assert result != null;
        return result;
    }

    /**
     * Generates a constraint which is satisfiable if and only if {@code this} is in the multi-interval {@code multiInterval}.
     * @param multiInterval the multi-interval.
     * @return the generated constraint.
     * @throws NotValidDomainException if {@code multiInterval} is empty.
     */
    public @NotNull
    ConstraintClass
    dom(@NotNull MultiInterval multiInterval) throws NotValidDomainException {
        Objects.requireNonNull(multiInterval);

        if(multiInterval.isEmpty())
            throw new NotValidDomainException();

        ConstraintClass result = new ConstraintClass();
        result.add(new AConstraint(Environment.domCode, this, multiInterval));
        result.addAll(this.constraint);

        assert result != null;
        return result;
    }

    /**
     * Generates a constraint which is satisfiable if and only if {@code this} is in the set {@code integers}.
     * @param integers the set of integers. It must not contain {@code null} values.
     * @return the generated constraint.
     * @throws NotValidDomainException if {@code integers} is empty.
     * @throws NullPointerException if {@code integers} contains {@code null} values.
     */
    public @NotNull
    ConstraintClass
    dom(@NotNull Set<Integer> integers) {
        Objects.requireNonNull(integers);
        if(integers.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        MultiInterval multiInterval = new MultiInterval(integers);
        if(multiInterval.isEmpty())
            throw new NotValidDomainException();

        ConstraintClass result = dom(multiInterval);
        assert result != null;
        return result;
    }


    /**
     * Generates glb constraint which is satisfiable if and only if {@code this} is not in the interval {@code [glb,lub]}.
     * @param glb greatest lower bound of the interval.
     * @param lub least upper bound of the interval.
     * @return the generated constraint.
     */
    public @NotNull
    ConstraintClass
    ndom(@NotNull Integer glb, @NotNull Integer lub) {
        Objects.requireNonNull(glb);
        Objects.requireNonNull(lub);

        MultiInterval first = new MultiInterval(MultiInterval.INF, glb - 1);
        MultiInterval second = new MultiInterval(lub + 1, MultiInterval.SUP);
        MultiInterval domain = first.union(second);

        ConstraintClass result = dom(domain);
        assert result != null;
        return result;
    }

    /**
     * Generates a constraint which is satisfiable if and only if {@code this} is not in the multi-interval {@code multiInterval}.
     * @param multiInterval the multi-interval.
     * @return the generated constraint.
     */
    public @NotNull
    ConstraintClass
    ndom(@NotNull MultiInterval multiInterval)
            throws NotValidDomainException {
        Objects.requireNonNull(multiInterval);

        ConstraintClass result = dom(multiInterval.complement());
        assert result != null;
        return result;
    }

    /**
     * A constraint which labels this variable giving it subsequent values taken from its domain.
     * @return the generated constraint.
     */
    public @NotNull
    ConstraintClass
    label() {
        ConstraintClass result = label(new LabelingOptions());
        assert result != null;
        return result;
    }

    /**
     * A constraint which labels this variable giving it subsequent values taken from its domain using the given heuristic.
     *
     * @param valHeuristic the heuristic to use when labeling.
     * @return the generated constraint.
     */
    public @NotNull
    ConstraintClass
    label(@NotNull ValHeuristic valHeuristic) {
        Objects.requireNonNull(valHeuristic);
        LabelingOptions lop = new LabelingOptions();
        lop.valueForIntLVar = valHeuristic;

        ConstraintClass result = label(lop);
        assert result != null;
        return result;
    }

    /**
     * A constraint which labels this variable giving it subsequent values taken from its domain using the given labeling options.
     *
     * @param labelingOptions labeling options to use.
     * @return the generated constraint.
     */
    public @NotNull
    ConstraintClass
    label(@NotNull LabelingOptions labelingOptions) {
        ConstraintClass result = new ConstraintClass();
        result.add(new AConstraint(Environment.labelCode, this, labelingOptions));
        result.addAll(this.constraint);
        assert result != null;
        return result;
    }

    /////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS //////////////////////
    /////////////////////////////////////////////////////////

    /**
     * Generates an integer variable which is the result of the single-argument constraint {@code constraintCode} with {@code this} as its argument.
     *
     * @param constraintCode code for the constraint.
     * @return the generated integer variable.
     */
    protected @NotNull IntLVar
    LvarExprGenExpr(int constraintCode) {
        IntLVar result = new IntLVar();
        result.setDummy(true);

        AConstraint newAConstraint = new AConstraint(constraintCode, result, this);
        newAConstraint.setDefinitionConstraint(true);
        
        result.constraint.add(newAConstraint);
        result.constraint.addAll(this.constraint);

        assert result != null;
        return result;
    }

    /**
     * Generates an integer variable which is the result of the two-argument constraint (expression) {@code constraintCode}
     * with {@code this} as its first argument and {@code second} as its second argument.
     *
     * @param second second argument of the constraint.
     * @param constraintCode code for the constraint.
     * @return the generated integer variable.
     */
    protected @NotNull IntLVar
    LvarExprGenExpr(@NotNull IntLVar second, int constraintCode) {
        assert second != null;

        IntLVar result = new IntLVar();
        result.setDummy(true);

        AConstraint newAConstraint = new AConstraint(constraintCode, result, this, second);
        newAConstraint.setDefinitionConstraint(true);
        		
        result.constraint.add(newAConstraint);
        result.constraint.addAll(second.constraint);
        result.constraint.addAll(this.constraint);

        assert result != null;
        return result;
    }

    /**
     * Generates an integer variable which is the result of the two-argument constraint {@code constraintCode}
     * with {@code this} as its first argument and {@code second} as its second argument.
     *
     * @param second second argument of the constraint.
     * @param constraintCode code for the constraint.
     * @return the generated integer variable.
     */
    protected @NotNull IntLVar
    LvarExprGenObj(@NotNull Integer second, int constraintCode) {
        assert second != null;

        IntLVar result = new IntLVar();
        result.setDummy(true);

        AConstraint newAConstraint = new AConstraint(constraintCode, result, this, second);
        newAConstraint.setDefinitionConstraint(true);
        
        result.constraint.add(newAConstraint);
        result.constraint.addAll(this.constraint);

        assert result != null;
        return result;
    }

    /**
     * Checks whether the variable is dummy or not.
     * @return{@code true} if the variable is dummy, i.e., it has been generated
     * as the result of an Integer Expression, {@code false} otherwise.
     * @author Andrea Fois
     */
    protected boolean isDummy(){
        return this.dummy;
    }

    /**
     * Makes the variable dummy or not.
     * @param dummy {@code true} if the variable is dummy, i.e., it has been generated
     * as the result of an Integer Expression, {@code false} otherwise.
     * @author Andrea Fois
     */
    protected void setDummy(boolean dummy){
        this.dummy = dummy;
    }

    /**
     * Generates an arithmetic constraint such as "X less than or equal to Y" with {@code this} as the first argument of the operation.
     * @param second the second parameter of the operation.
     * @param constraintCode code for the kind of constraint to create.
     * @return the generated arithmetic constraint.
     */
    private @NotNull
    ConstraintClass
    ArithConstGenExpr(@NotNull IntLVar second, int constraintCode) {
        Objects.requireNonNull(second);

        ConstraintClass result = new ConstraintClass();
        
        AConstraint newAConstraint = new AConstraint(constraintCode, this, second);

        result.add(newAConstraint);
        result.addAll(this.constraint);
        result.addAll(second.constraint);

        assert result != null;
        return result;
    }

    /**
     * Generates an arithmetic constraint such as "X less than or equal to Y" with {@code this} as the first argument of the operation.
     * @param second the second parameter of the operation.
     * @param constraintCode code for the kind of constraint to create.
     * @return the generated arithmetic constraint.
     */
    private @NotNull
    ConstraintClass
    ArithConstGenObj(@NotNull Integer second, int constraintCode) {
        Objects.requireNonNull(second);

        ConstraintClass result = new ConstraintClass();
        
        AConstraint newAConstraint = new AConstraint(constraintCode, this, second);

        result.add(newAConstraint);
        result.addAll(this.constraint);

        assert result != null;
        return result;
    }

}
