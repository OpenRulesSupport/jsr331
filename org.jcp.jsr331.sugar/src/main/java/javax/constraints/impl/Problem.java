package javax.constraints.impl;

import javax.constraints.impl.constraint.AllDifferent;
import javax.constraints.impl.constraint.Cardinality;
import javax.constraints.impl.constraint.Element;
import javax.constraints.impl.constraint.GlobalCardinality;
import javax.constraints.impl.constraint.Linear;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.converter.Converter;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.expression.Expression;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the interface "Problem"
 * 
 * @see jp.kobe_u.sugar.csp.CSP
 * @since 1.0
 * @version 1.0
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Problem extends AbstractProblem {
    /**
     * CP API Reference Implementation
     */
    private static final String JSR331_IMPLEMENTATION_VERSION =
            "JSR-331 Implementation based on Sugar version 2.1.3 and Sat4j version 2.3.5";

    //public static org.apache.commons.logging.Log logger =  LogFactory.getLog("javax.constraints");
    private static org.slf4j.Logger logger = LoggerFactory.getLogger("javax.constraints");

    private Set<Var> variables = new HashSet<Var>();
    public CSP sugarCSP;
    public Converter sugarConverter;
    private Constraint sugarFalse = null;
    private Constraint sugarTrue = null;
    
    public void addVarsToList(List<Expression> xs, javax.constraints.Var[] vars) {
        for (javax.constraints.Var v : vars) {
            IntegerVariable x = ((Var)v)._getImpl();
            xs.add(Expression.create(x.getName()));
        }
    }
    
    public Expression toExpr(javax.constraints.Var v) {
        IntegerVariable x = ((Var)v)._getImpl();
        return Expression.create(x.getName());
    }

    public Expression toExpr(javax.constraints.Var[] vars) {
        List<Expression> xs = new ArrayList<Expression>();
        addVarsToList(xs, vars);
        return Expression.create(xs);
    }

    public Expression toExpr(int[] array) {
        List<Expression> xs = new ArrayList<Expression>();
        for (int a : array)
            xs.add(Expression.create(a));
        return Expression.create(xs);
    }

    public Expression toSumExpr(javax.constraints.Var[] vars) {
        List<Expression> xs = new ArrayList<Expression>();
        xs.add(Expression.ADD);
        addVarsToList(xs, vars);
        return Expression.create(xs);
    }

    public Expression toSumExpr(int[] values, javax.constraints.Var[] vars) {
        List<Expression> xs = new ArrayList<Expression>();
        xs.add(Expression.ADD);
        for (int i = 0; i < vars.length; i++) {
            IntegerVariable x = ((Var)vars[i])._getImpl();
            xs.add(Expression.create(x.getName()).mul(values[i]));
        }
        return Expression.create(xs);
    }
    
    public Expression toExprComp(String comp) {
        if (comp.equals("="))
            return Expression.EQ;
        else if (comp.equals("!="))
            return Expression.NE;
        else if (comp.equals("<"))
            return Expression.LT;
        else if (comp.equals("<="))
            return Expression.LE;
        else if (comp.equals(">"))
            return Expression.GT;
        else if (comp.equals(">="))
            return Expression.GE;
        else
            throw new RuntimeException("Unknown comparison operator: " + comp);
    }

    public Expression toExpr(javax.constraints.Constraint c) {
        return ((Constraint)c)._getImpl();
    }

    public Var toVar(Expression ex) {
        try {
            IntegerVariable x = sugarConverter.toIntegerVariable(ex);
            Var v = new Var(this, x);
            return v;
        } catch (SugarException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @return JSR331 Implementation version number
     */
    public String getImplVersion() {
        return JSR331_IMPLEMENTATION_VERSION;
    }

    public Problem() {
        this("");
    }

    public Problem(String name) {
        super(name);
        if (! name.isEmpty())
            log("Problem: " + name);
        sugarCSP = new CSP();
        sugarConverter = new Converter(sugarCSP);
        solver = createSolver();
    }
    
    public void addVariable(Var v) {
        variables.add(v);
    }
    
    public Set<Var> getVariables() {
        return variables;
    }

    /**
     * Log the String parameter "text"
     */
    @Override
    public void log(String text) {
        logger.info(text);
    }

    /**
     * Log the String parameter "text"
     */
    @Override
    public void debug(String text) {
        logger.debug(text);
    }

    /**
     * Log the String parameter "text"
     */
    @Override
    public void error(String text) {
        logger.error(text);
    }

    /**
     * Creates a Var with the name "name" and domain [min;max] of domain type
     * "type", adds this variable to the problem, and returns the newly added
     * Var.
     *
     * @param name
     *            the name for the new Var.
     *
     * @param min
     *            the minimum value in the domain for the new Var.
     *
     * @param max
     *            the maximum value in the domain for the new Var.
     *
     * @return the Var variable created and added to the problem.
     */
    @Override
    public javax.constraints.Var createVariable(String name, int min, int max) {
        Var var = new Var(this, name, min, max);                                                                                       
        add(var);
        return var;
    }

    /**
     * Creates a Var with the name "name", and domain int[]
     *
     * @param name
     *            a string
     * @param domain an array of integers
     * @return the created variable
     */
    @Override
    public javax.constraints.Var variable(String name, int[] domain) {
        Var var = new Var(this, name, domain);
        add(var);
        return var;
    }

    /**
     * Creates a boolean constrained variable with the name "name" and adds
     * this variable to the problem, and returns the newly added VarBool.
     * @param name the name for the new Var.
     * @return the Var variable created and added to the problem.
     */
    @Override
    public javax.constraints.VarBool variableBool(String name) {
        VarBool varBool = new VarBool(this, name);
        add(varBool);
        return varBool;
    }

    /**
     * This method takes a constraint's implementation and uses its own
     * RI-specific post-method.
     * @throws RuntimeException if a failure happened during the posting
     */
    @Override
    public void post(javax.constraints.Constraint constraint) {
        try {
            Expression c = ((Constraint)constraint)._getImpl();
            sugarConverter.convert(c);
        } catch (SugarException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        constraint.post();
    }

    /**
     * Creates a Reversible integer with the name "name" and value "value"
     * and returns the newly added Reversible.
     *
     * @param name the name for the new reversible.
     *
     * @param value the initial value
     *
     * @return the reversible integer
     */
    @Override
    public javax.constraints.extra.Reversible addReversible(String name, int value) {
        Reversible rev = new Reversible(this, name, value);
        return rev;
    }

    /**
     * Posts and Returns a constraint: var "oper" value
     */
    @Override
    public javax.constraints.Constraint post(javax.constraints.Var var, String oper, int value) {
        javax.constraints.Constraint c = add(new Linear(var, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint linear(javax.constraints.Var var, String oper, int value) {
        return add(new Linear(var, oper, value));
    }

    /**
     * Posts and Returns a constraint: var1 "oper" var2
     */
    @Override
    public javax.constraints.Constraint post(javax.constraints.Var var1, String oper, javax.constraints.Var var2) {
        javax.constraints.Constraint c = add(new Linear(var1, oper, var2));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint linear(javax.constraints.Var var1, String oper, javax.constraints.Var var2) {
        return add(new Linear(var1, oper, var2));
    }

    @Override
    public javax.constraints.Constraint postElement(int[] array, javax.constraints.Var indexVar, String oper, int value) {
        javax.constraints.Constraint c = add(new Element(array, indexVar, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postElement(int[] array, javax.constraints.Var indexVar, String oper, javax.constraints.Var var) {
        javax.constraints.Constraint c = add(new Element(array, indexVar, oper, var));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postElement(javax.constraints.Var[] array, javax.constraints.Var indexVar, String oper, int value) {
        javax.constraints.Constraint c = add(new Element(array, indexVar, oper, value));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postElement(javax.constraints.Var[] array, javax.constraints.Var indexVar, String oper, javax.constraints.Var var) {
        javax.constraints.Constraint c = add(new Element(array, indexVar, oper, var));
        c.post();
        return c;
    }

    /**   
     * Returns a constrained integer variable that is equal to the sum of the
     * variables in the array "arrayOfVariables".
     *
     * @param vars
     *            the array of variables from which we desire the sum.
     * @return a constrained integer variable that is equal to the sum of the
     *         variables in the array "vars".
     */
    @Override
    public javax.constraints.Var sum(javax.constraints.Var[] vars) {
        Expression x = toSumExpr(vars);
        return toVar(x);
    }

    /**
     * Returns a constrained variable equal to the scalar product of an array of
     * values "arrayOfValues" and an array of variables "arrayOfVariables".
     *
     * @param values
     *            the array of values.
     * @param vars
     *            the array of variables.
     * @return a constrained variable equal to the scalar product of an array of
     *         values "arrayOfValues" and an array of variables
     *         "arrayOfVariables".
     */
    @Override
    public javax.constraints.Var scalProd(int[] values, javax.constraints.Var[] vars) {
        Expression x = toSumExpr(values, vars);
        return toVar(x);
    }

    /***************************************************************************
     * Global constraints
     **************************************************************************/

    /**
     * Creates (without posting) a new Constraint stating that all of the elements of
     * the array of variables "vars" must take different values from each other.
     *
     * @param vars
     *            the array of Vars which must all take different values.
     * @return the all-different Constraint on the array of Vars.
     */
    @Override
    public javax.constraints.Constraint allDiff(javax.constraints.Var[] vars) {
        javax.constraints.Constraint c = new AllDifferent(vars);
        return c;
    }

    /**
     * This method creates and returns a new cardinality constraint
     * such as cardinality(vars,cardValue) less than value.
     * Here cardinality(vars,cardValue) denotes a constrained integer
     * variable that is equal to the number of those elements in the
     * array "vars" that are bound to the "cardValue".
     * For example, if oper is "less" it means that the variable
     * cardinality(vars,cardValue) must be less than the  value .
     * This constraint does NOT assume a creation of an intermediate
     * variable "cardinality(vars,cardValue)".
     * @param vars array of vars
     * @param cardValue int
     * @param oper operator
     * @param value int
     * @return constraint
     */
    @Override
    public javax.constraints.Constraint postCardinality(javax.constraints.Var[] vars, int cardValue, String oper, int value) {
        javax.constraints.Constraint c = add(new Cardinality(vars, cardValue, oper, value));
        c.post();
        return c;
    }

    /**
     * This method is similar to the one above but instead of  value 
     * the  cardinality(vars,cardValue)  is being constrained by  var .
     */
    @Override
    public javax.constraints.Constraint postCardinality(javax.constraints.Var[] vars, int cardValue, String oper, javax.constraints.Var var) {
        javax.constraints.Constraint c = add(new Cardinality(vars, cardValue, oper, var));
        c.post();
        return c;
    }

    @Override
    public javax.constraints.Constraint postGlobalCardinality(javax.constraints.Var[] vars, int[] values, javax.constraints.Var[] cardinalityVars) {
        javax.constraints.Constraint c = add(new GlobalCardinality(vars, values, cardinalityVars));
        c.post();
        return c;
    }

    /**
     * For each index i the number of times the value "values[i]"
     * occurs in the array "vars" should be cardMin[i] and cardMax[i] (inclusive)
     * @param vars array of constrained integer variables
     * @param values array of integer values within domain of all vars
     * @param cardMin array of integers that serves as lower bounds for values[i]
     * @param cardMax array of integers that serves as upper bounds for values[i]
     * Note that arrays values, cardMin, and cardMax should have the same size
     * otherwise a RuntimeException will be thrown
     */
    @Override
    public javax.constraints.Constraint postGlobalCardinality(javax.constraints.Var[] vars, int[] values, int[] cardMin, int[] cardMax) {
        javax.constraints.Constraint c = add(new GlobalCardinality(vars, values, cardMin, cardMax));
        c.post();
        return c;
    }

    @Override
    protected javax.constraints.Solver createSolver() {
        return new javax.constraints.impl.search.Solver(this);
    }

    /**
     * Loads a Problem represented by the XML document on the specified input stream
     * into this instance of the Problem
     *
     * @param in
     *            the input stream from which to read the XML document.
     * @throws Exception
     *             if reading from the specified input stream results in an
     *             IOException or data on input stream does not constitute a
     *             valid XML document with the mandated document type.
     * @see #storeToXML
     */
    @Override
    public void loadFromXML(InputStream in) throws Exception {
        // TODO JSR331 Implementation
        throw new RuntimeException("loadFromXML is not supported");
    }

    /**
     * Emits an XML document representing this instance of the Problem.
     *
     * @param os
     *            the output stream on which to emit the XML document.
     * @param comment
     *            a description of the property list, or null if no comment is
     *            desired. If the specified comment is null then no comment will
     *            be stored in the document.
     * @throws Exception
     *             IOException - if writing to the specified output stream
     *             results in an IOException; NullPointerException - if os is
     *             null.
     */
    @Override
    public void storeToXML(OutputStream os, String comment) throws Exception {
        // TODO JSR331 Implementation
        throw new RuntimeException("storeToXML is not supported");
    }

    /**
     * Returns the constant constraint that always will fail when it is posted or executed.
     * @return the False Constraint
     */
    @Override
    public Constraint getFalseConstraint() {
        if (sugarFalse == null) {
            sugarFalse = new Constraint(this, "false");
            sugarFalse._setImpl(Expression.FALSE);
        }
        return sugarFalse;
    }

    /**
     * Returns the constant constraint that always succeeds when it is posted or executed.
     * @return the True Constraint
     */
    @Override
    public Constraint getTrueConstraint() {
        if (sugarTrue == null) {
            sugarTrue = new Constraint(this, "true");
            sugarTrue._setImpl(Expression.TRUE);
        }
        return sugarTrue;
    }
}
