package javax.constraints.impl;

import java.util.SortedSet;
import java.util.TreeSet;

import jp.kobe_u.sugar.csp.IntegerDomain;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the interface "Var"
 */
public class Var extends AbstractVar implements javax.constraints.Var {
    public static String VAR_PREFIX = "SVar_";
    public static int count = 0;
    
    private Integer solutionValue = null;

    public Var(javax.constraints.Problem problem, String name, int min, int max) {
        super(problem, name);
        Problem p = (Problem)getProblem();
        try {
            IntegerDomain d = IntegerDomain.create(min, max);
            count++;
            String varName = VAR_PREFIX + count + "_" + name;
            IntegerVariable x = new IntegerVariable(varName, d);
            p.sugarCSP.add(x);
            _setImpl(x);
            p.addVariable(this);
        } catch (Exception e) {
            p.log("Invalid domain bounds for Var: [" + min + ";" + max + "]\n" + e);
        }
    }

    public Var(javax.constraints.Problem problem, String name, int[] domain) {
        super(problem, name);
        Problem p = (Problem)getProblem();
        if (name == null || name.equals(""))
            throw new IllegalArgumentException("Null or empty name for Var");
        try {
            SortedSet<Integer> set = new TreeSet<Integer>();
            for (int a : domain)
                set.add(a);
            IntegerDomain d = IntegerDomain.create(set);
            count++;
            String varName = VAR_PREFIX + count + "_" + name;
            IntegerVariable x = new IntegerVariable(varName, d);
            p.sugarCSP.add(x);
            _setImpl(x);
            p.addVariable(this);
        } catch (Exception e) {
            p.log("Invalid domain bounds for Var\n" + e);
        }
    }
    
    public Var(javax.constraints.Problem problem, IntegerVariable x) {
        super(problem, x.getName());
        Problem p = (Problem)getProblem();
        _setImpl(x);
        p.addVariable(this);
    }
    
    public IntegerVariable _getImpl() {
        return (IntegerVariable)impl;
    }
    
    public void _setImpl(IntegerVariable x) {
        impl = x;
    }
    
    public Integer getSolutionValue() {
        return solutionValue;
    }

    public void setSolutionValue(Integer solutionValue) {
        this.solutionValue = solutionValue;
    }

    /**
     * Changes the domain type of this variable. Setting domain type
     * AFTER a variable was created allows to take into consideration
     * posted constraints and other problem state specific information.
     * To take advantage of such domain type as DOMAIN_AUTOMATIC
     * this method should be overloaded by an implementation solver
     * @param type DomainType
     */
    @Override
    public void setDomainType(javax.constraints.DomainType type) {
        // TODO JSR331 Implementation
        getProblem().log("setDomainType is not supported");
    }

    /**
     * Returns true if the domain of this variable contains the value.
     * @param value int
     * @return true if the value is in the domain of this variable
     */
    @Override
    public boolean contains(int value) {
        if (solutionValue != null)
            return solutionValue == value;
        IntegerVariable x = _getImpl();
        return x.getDomain().contains(value);
    }

    /**
     * Sets a new minimum for the domain of this variable. If min is less than
     * the current min, a warning is produced and the setting is ignored. This
     * method should be implemented by a concrete CP solver implementation.
     *
     * @param min int
     * @throws RuntimeException
     */
    public final void setMin(int min) throws Exception {
        // TODO JSR331 Implementation
        // Not used?
        new RuntimeException("setMin is not supported");
    }

    /**
     * @return current minimum for the domain of this variable
     */
    @Override
    public final int getMin() {
        if (solutionValue != null)
            return solutionValue;
        IntegerVariable x = _getImpl();
        return x.getDomain().getLowerBound();
    }

    /**
     * Removes a value from the domain of this variable. May throw an exception.
     *
     * @param value int
     * @throws RuntimeException
     */
    public final void removeValue(int value) throws Exception {
        // TODO JSR331 Implementation
        // Not used?
        new RuntimeException("removeValue is not supported");
    }

    /**
     * The default implementation is:
     *   return getMax() - getMin() + 1;
     * This method is better to be redefined to take into consideration an actual
     * domain implementation.
     * @return the current number of values inside the domain of this variable
     */
    @Override
    public final int getDomainSize() {
        if (solutionValue != null)
            return 1;
        IntegerVariable x = _getImpl();
        return x.getDomain().size();
    }

    /**
     * Sets a new maximum for the domain of this variable. If max is more than
     * the current max, a warning is produced and the setting is ignored. This
     * method should be implemented by a concrete CP solver implementation.
     *
     * @param max int
     * @throws RuntimeException
     */
    public final void setMax(int max) throws Exception {
        // TODO JSR331 Implementation
        // Not used?
        new RuntimeException("setMax is not supported");
    }

    /**
     * @return current maximum for the domain of this variable
     */
    @Override
    public final int getMax() {
        if (solutionValue != null)
            return solutionValue;
        IntegerVariable x = _getImpl();
        return x.getDomain().getUpperBound();
    }

    /**
     * @return true if the domain of the variable contains only one value
     */
    @Override
    public final boolean isBound() {
        if (solutionValue != null)
            return true;
        IntegerVariable x = _getImpl();
        return x.getDomain().size() == 1;
    }

    //=======================================
    // Arithmetic operators
    //=======================================

    /**
     * @return this + value
     */
    @Override
    public javax.constraints.Var plus(int value) {
        Problem p = (Problem)getProblem();
        Expression x = p.toExpr(this).add(value);
        return p.toVar(x);
    }

    /**
     * @return this + var
     */
    @Override
    public javax.constraints.Var plus(javax.constraints.Var var) {
        Problem p = (Problem)getProblem();
        Expression x = p.toExpr(this).add(p.toExpr((Var)var));
        return p.toVar(x);
    }

    /**
     * @return this * value
     */
    @Override
    public javax.constraints.Var multiply(int value) {
        Problem p = (Problem)getProblem();
        Expression x = p.toExpr(this).mul(value);
        return p.toVar(x);
    }

    /**
     * @return this * var
     */
    @Override
    public javax.constraints.Var multiply(javax.constraints.Var var) {
        Problem p = (Problem)getProblem();
        Expression x = p.toExpr(this).mul(p.toExpr((Var)var));
        return p.toVar(x);
    }

    /**
     * @return this / value
     */
    @Override
    public javax.constraints.Var divide(int value) {
        if (value == 0) {
            throw new IllegalArgumentException("div(Var var, int value): value == 0");
        } else if (value == 1) {
            return this;
        }
        Problem p = (Problem)getProblem();
        Expression x = p.toExpr(this).div(value);
        return p.toVar(x);
    }

    /**
     * @return this / var
     */
    @Override
    public javax.constraints.Var divide(javax.constraints.Var var) throws Exception {
        Problem p = (Problem)getProblem();
        Expression x = p.toExpr(this).div(p.toExpr((Var)var));
        return p.toVar(x);
    }

    /**
     * @return an absolute value of this
     */
    @Override
    public javax.constraints.Var abs() {
        Problem p = (Problem)getProblem();
        Expression x = p.toExpr(this).abs();
        return p.toVar(x);
    }

    /**
     * This method associates a custom Propagator with an "event"
     * related to changes in the domain of a constrained variable "var". It
     * forces the solver to keep an eye on these events and invoke the
     * Propagator "propagator" when these events actually occur. When such events
     * occur, the Propagator's method propagate() will be executed.
     *
     * @param propagator the Propagator we wish to associate with events on the variable.
     * @param event the events that will trigger the invocation of the Propagator.
     */
    @Override
    public void addPropagator(javax.constraints.extra.Propagator propagator, javax.constraints.extra.PropagationEvent event) {
        // TODO JSR331 Implementation
        throw new RuntimeException("addPropagator is not supported");
    }
}
