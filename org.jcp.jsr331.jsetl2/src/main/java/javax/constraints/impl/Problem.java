package javax.constraints.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.constraints.impl.constraint.AllDifferent;
import javax.constraints.impl.constraint.Cardinality;
import javax.constraints.impl.constraint.Element;
import javax.constraints.impl.constraint.GlobalCardinality;
import javax.constraints.impl.constraint.Linear;
import javax.constraints.impl.search.Solver;

import org.slf4j.LoggerFactory;

import JSetL.IntLVar;
import JSetL.MultiInterval;


/**
 * This class implement the JSR331 problem
 * extending the common implementation AbstractProblem. The implementation is
 * based on the solver JSetL.
 * 
 * <p>The class contains an integer value <code>counter</code> that count the 
 * number of objects created without a name, or temporary variable. This 
 * counter is used to assign an unique name to objects and variables.</p>
 * 
 * @author Fabio Biselli
 *
 */
public class Problem extends AbstractProblem {
	
	//public static org.apache.commons.logging.Log logger = LogFactory.getLog("javax.constraints");
    private static org.slf4j.Logger logger = LoggerFactory.getLogger("javax.constraints");
	
	/**
	 * Problem integer set variables.
	 */
	ArrayList<VarSet> varSets;
	
	/**
	 * Problem constraints.
	 */
	ArrayList<JSetL.Constraint> constraints;
	
	/**
	 * Auxiliary vars.
	 */
	ArrayList<JSetL.IntLVar> auxIntLVar;
	
	/**
	 * Constants for Relationship Operator.
	 */
	private static final int OPER_EQ = 1, OPER_UNKNOWN = 0, OPER_NEQ = 2, 
		OPER_LT = 3, OPER_LEQ = 4, OPER_GT = 5, OPER_GEQ = 6;

	/**
	 * CP API Reference Implementation.
	 */
	private static final String JSR331_JSetL_Version = 
			"JSR-331 Implementation based on JSetL 2.3";
	
	/**
	 * Auxiliary counter for naming variables.
	 */
	private static int counter = 0;
	
	/**
	 * A flag that advice if an instance of the solver is already created.
	 */
	boolean solverReady = false;
	

	/**
	 * Build a new Problem with the name <code>name</code>.
	 * 
	 * @param name the name of the new Problem.
	 */
	public Problem(String name) {
		super(name);
		varSets = new ArrayList<VarSet>();
		constraints = new ArrayList<JSetL.Constraint>();
		auxIntLVar = new ArrayList<JSetL.IntLVar>();
	}
	
	/**
	 * Build a new Problem with an auto-generated name.
	 * 
	 */
	public Problem() {
		super("_P"+(counter++));
		varSets = new ArrayList<VarSet>();
		constraints = new ArrayList<JSetL.Constraint>();
		auxIntLVar = new ArrayList<JSetL.IntLVar>();
	}

	/**
	 * Auxiliary method that add a given JSetL constraint into the
	 * vector of saved JSetL constraints.
	 * @param constraint a constraint
	 * @return a constraint
	 */
	public JSetL.Constraint addJSetLConstraints(JSetL.Constraint constraint) {
		if (solverReady)
			((Solver) solver).addJSetLConstraint(constraint);
		constraints.add(constraint);
		return constraint;
	}
	
	/**
	 * Auxiliary method that return an array of JSetL constraints
	 * saved into the problem.
	 * 
	 * @return the array of JSetL constraints bound to <code>this</code> problem.
	 */
	public JSetL.Constraint[] getJSetLConstraints() {
		if (constraints.size() == 0)
			return null;
		JSetL.Constraint[] array = new JSetL.Constraint[constraints.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = constraints.get(i);
		}
		return array;
	}
	
	/**
	 * Create and post a new Constraint such as: <code> array[indexVar]
	 * oper value </code>.
	 * 
	 * @param array an array of integers
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the "array"
	 * @param value the integer value.
	 * 
	 * @throws RuntimeException if the posting fails 
	 * @return a newly created constraint
	 */
	public Constraint postElement(
			int[] array, 
			javax.constraints.Var indexVar, 
			String oper, 
			int value) {
		if (indexVar.getMin() > array.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		Element result = new Element(array, indexVar, oper, value);
		post(result);
		addAuxVariable(indexVar);
		return result;
	}

	/**
	 * Create and post a new Constraint such as: <code> array[indexVar]
	 * oper value </code>.
	 * 
	 * @param array an array of integers
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the "array"
	 * @param var the integer variable.
	 * 
	 * @throws RuntimeException if the posting fails 
	 * @return a newly created constraint
	 */
	public Constraint postElement(
			int[] array, 
			javax.constraints.Var indexVar, 
			String oper, 
			javax.constraints.Var var) {
		if (indexVar.getMin() > array.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		Element result = new Element(array, indexVar, oper, var);
		post(result);
		addAuxVariable(indexVar);
		addAuxVariable(var);
		return result;
	}

	/**
	 * Create and post a new Constraint such as: <code> vars[indexVar]
	 * oper value </code>.
	 * 
	 * @param vars an array of integer variables
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the array "vars"
	 * @param value the integer value.
	 * 
	 * @throws RuntimeException if the posting fails 
	 * @return a newly created constraint
	 */
	public Constraint postElement(
			javax.constraints.Var[] vars, 
			javax.constraints.Var indexVar, 
			String oper,
			int value) {
		if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		Element result = new Element(vars, indexVar, oper, value);
		post(result);
		addAuxVariable(indexVar);
		addAuxVariables(vars);
		return result;
	}

	/**
	 * Create and post a new Constraint such as: <code> vars[indexVar]
	 * oper var </code>.
	 * 
	 * @param vars an array of integer variables
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the array "vars"
	 * @param var the integer variable.
	 * 
	 * @throws RuntimeException if the posting fails 
	 * @return a newly created constraint
	 */
	public Constraint postElement(javax.constraints.Var[] vars, 
			javax.constraints.Var indexVar, String oper, 
			javax.constraints.Var var) {
		if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		Element result = new Element(vars, indexVar, oper, var);
		post(result);
		addAuxVariable(indexVar);
		addAuxVariable(var);
		addAuxVariables(vars);
		return result;
	}

	/**
	 * Build a new Constraint based on the linear expression that is the 
	 * sum of the constrained variables <code>vars[i]</code> multiplied for 
	 * the integer coefficient <code>array[i]</code>, constrained to the given 
	 * <code>value</code> with the operator <code>oper</code>.
	 * 
	 *  <code>array[0]*vars[0] + array[1]*vars[1] + ... + 
	 *  array[n]*vars[n] 
	 *  oper value</code>
	 *  
	 *  The constraint will be added to the problem.
	 *  
	 *  @param array the coefficient of the linear expression
	 *  @param vars the array of integer variables
	 *  @param oper the string representing the operator 
	 *  @param value the integer value.
	 *  
	 *  @return result the Contraint built.
	 */
	public Constraint post(
			int[] array, 
			javax.constraints.Var[] vars, 
			String oper, 
			int value) {
		Constraint result = linear(array, vars, oper, value);
		post(result);
		add(result);
		addAuxVariables(vars);
		return result;
	}

	/**
	 * Build a new Constraint based on the linear expression that is the 
	 * sum of the constrained variables <code>vars[i]</code> multiplied for 
	 * the integer coefficient <code>array[i]</code>, constrained to the given
	 * integer variable <code>var</code> with the operator <code>oper</code>.
	 * 
	 *  <code>array[0]*vars[0] + array[1]*vars[1] + ... + 
	 *  array[n]*vars[n]
	 *  oper var</code>
	 *  
	 *  The constraint will be added to the problem.
	 *  
	 *  @param array the coefficient of the linear expression
	 *  @param vars the array of integer variables
	 *  @param oper the string representing the operator 
	 *  @param var the integer variable.
	 *  
	 *  @return result the Contraint built.
	 */
	public Constraint post(
			int[] array, 
			javax.constraints.Var[] vars, 
			String oper, 
			javax.constraints.Var var) {
		Constraint result = linear(array, vars, oper, var);
		post(result);
		add(result);
		addAuxVariable(var);
		addAuxVariables(vars);
		return result;
	}

	/**
	 * Build a new Constraint based on the linear expression that is the
	 * sum of the constrained variables <code>vars[i]</code>, constrained to 
	 * the given integer <code>value</code> with the operator 
	 * <code>oper</code>.
	 * 
	 *  <code>vars[0] + vars[1] + ... + vars[n] oper 
	 *  value</code>
	 *  
	 *  The constraint will be added to the problem.
	 *  
	 * @param vars the array of integer variable
	 * @param oper the string representing the operator 
	 * @param value the integer value.
	 * 
	 * 	 *  @return result the Contraint built.
	 */
	public Constraint post(
			javax.constraints.Var[] vars, 
			String oper, 
			int value) {
		// The coefficient array.
		int coef[] = new int[vars.length];
		for (int i = 0; i < vars.length; i++)
			coef[i] = 1;
		Constraint result = linear(coef, vars, oper, value);
		post(result);
		add(result);
		addAuxVariables(vars);
		return result;
	}
	
	/**
	 * Build a new Constraint based on the linear expression that is the 
	 * sum of the constrained variables <code>vars[i]</code>, constrained to 
	 * the given integer variable <code>var</code> with the operator
	 * <code>oper</code>.
	 * 
	 *  <code>vars[0] + vars[1] + ... + vars[n] oper var</code>
     *  The constraint will be added to the problem.
	 *  
	 *  @param vars the array of integer variables
	 *  @param oper the string representing the operator 
	 *  @param var the integer variable.
	 *  
	 *  @return result the Contraint built.
	 */
	public Constraint post(
			javax.constraints.Var[] vars, 
			String oper, 
			javax.constraints.Var var) {
		int array[] = new int[vars.length];
		for (int i = 0; i < vars.length; i++)
			array[i] = 1;
		Constraint result = linear(array, vars, oper, var);
		post(result);
		add(result);
		addAuxVariables(vars);
		addAuxVariable(var);
		return result;
	}

	/**
	 * Build a new Constraint that constrain the variable given to
	 * the integer <code>value</code> with the operator <code>oper</code>,
	 * and add it to the problem.
	 * 
	 * @param var the integer variable
	 * @param value the integer value
	 * @param oper the string representing the operator 
	 *  
	 * @return result the Constraint built.
	 */
	public Constraint post(
			javax.constraints.Var var, 
			String oper, 
			int value) {
		Constraint result = linear(var, oper, value);
		post(result);
		add(result);
		addAuxVariable(var);
		return result;
	}

	/**
	 * Build a new JConstraint that constrain the given variable to
	 * another variable with the operator <code>oper</code>, and add it to
	 * the problem.
	 * 
	 * @param var1 the first integer variable
	 * @param var2 the second integer variable
	 * @param oper the string representing the operator 
	 *  
	 *  @return result the Constraint built.
	 */
	public Constraint post(
			javax.constraints.Var var1, 
			String oper, 
			javax.constraints.Var var2) {
		Constraint result = new Constraint(this);
		result = linear(var1, oper, var2);
		post(result);
		add(result);
		addAuxVariable(var1);
		addAuxVariable(var2);
		return result;
	}

	/**
	 * Build a new Constraint that constrain the variable given to
	 * the integer <code>value</code> with the operator <code>oper</code>,
	 * without adding it to the problem.
	 * 
	 * @param var the integer variable
	 * @param value the integer value
	 * @param oper the string representing the operator 
	 *  
	 *  @return result the Constraint built.
	 */
	public Constraint linear(
			javax.constraints.Var var, 
			String oper, 
			int value) {
		if (var == null)
			throw new RuntimeException("Parameters must not be null.");
		Linear result = new Linear(var, oper, value);
		return result;
	}

	/**
	 * Build a new Constraint that constrain the variable 
	 * <code>var1</code> to the integer variable <code>var2</code> with the 
	 * operator <code>oper</code>, without adding it to the problem.
	 * 
	 * @param var1 the first integer variable
	 * @param var2 the second integer variable
	 * @param oper the string representing the operator 
	 *  
	 *  @return result the Constraint built.
	 */
	public Constraint linear(
			javax.constraints.Var var1, 
			String oper, 
			javax.constraints.Var var2) {
		if (var1 == null || var2 == null)
			throw new RuntimeException("Parameters must not be null.");
		Linear result = new Linear(var1, oper, var2);
		return result;
	}
	
	/**
	 * Build a new Constraint based on the linear expression that is the 
	 * sum of the constrained variables <code>vars[i]</code> multiplied for 
	 * the integer coefficient <code>array[i]</code>, constrained to the given 
	 * <code>value</code> with the operator <code>oper</code>.
	 * 
	 *  <code>array[0]*vars[0] + array[1]*vars[1] + ... + array[n]*vars[n] oper value</code>
	 *  The constraint will not be added to the problem.
	 *  
	 *  @param array the coefficient of the linear expression
	 *  @param vars the array of integer variables
	 *  @param oper the string representing the operator 
	 *  @param value the integer value.
	 *  
	 *  @return the Constraint built.
	 */
	public Constraint linear(
			int[] array, 
			javax.constraints.Var[] vars, 
			String oper, 
			int value) {
		if (array.length != vars.length || array.length == 0)
			throw new RuntimeException(
				"Coefficent and variable length must be equal and not zero.");
		Var scalprod = (Var) scalProd(array, vars);
		Linear result = new Linear(scalprod, oper, value);
		return result;
	}
	
	/**
	 * Build a new Constraint based on the linear expression that is the 
	 * sum of the constrained variables <code>vars[i]</code> multiplied for 
	 * the integer coefficient <code>array[i]</code>, constrained to the given
	 * integer variable <code>var</code> with the operator <code>oper</code>.
	 * 
	 *  <code>array[0]*vars[0] + array[1]*vars[1] + ... + array[n]*vars[n] oper var</code>
	 *  The constraint will not be added to the problem.
	 *  
	 *  @param array the coefficient of the linear expression
	 *  @param vars the array of integer variables
	 *  @param oper the string representing the operator 
	 *  @param var the integer variable.
	 *  
	 *  @return result the Constraint built.
	 */
	public Constraint linear(int[] array, javax.constraints.Var[] vars, 
			String oper, javax.constraints.Var var) {
		if (array.length != vars.length || array.length == 0)
			throw new RuntimeException(
				"Coefficent and variable length must be equal and not zero.");
		Var scalprod = (Var) scalProd(array, vars);
		Linear result = new Linear(scalprod, oper, var);
		return result;
	}

	/**
	 * Build a new Var that is the scalar product of the variables
	 * <code>arrayOfVariables</code> with the coefficients 
	 * <code>arrayOfValues</code>.
	 * 
	 * @param arrayOfVariables the variables
	 * @param arrayOfValues the coefficients.
	 * 
	 * @return new Var.
	 */
	public Var scalProd(
			int[] arrayOfValues, 
			javax.constraints.Var[] arrayOfVariables) {
		if (arrayOfValues.length != arrayOfVariables.length 
				|| arrayOfValues.length == 0)
			throw new RuntimeException(
				"Coefficent and variable length must be equal and not zero.");
		IntLVar[] intVars = new IntLVar[arrayOfVariables.length];
		if (arrayOfValues[0] != 0) {
			intVars[0] = 
				((Var) arrayOfVariables[0]).getIntLVar().mul(
						arrayOfValues[0]);
		}
		else intVars[0] = new IntLVar(0,0);
		intVars[0].setName("_coef-0");
		for (int i = 1; i < arrayOfValues.length; i++) {
			if (arrayOfValues[i] !=0) {
				IntLVar tmp = new IntLVar(
						((Var) arrayOfVariables[i]).getIntLVar().mul(
								arrayOfValues[i]));
				intVars[i] = intVars[i-1].sum(tmp);
			}
			else intVars[i] = intVars[i-1];
			intVars[i].setName("_coef-" +i);
		}
		intVars[arrayOfVariables.length-1].setName(getFreshName());
		Var result = new Var(this, intVars[arrayOfVariables.length-1]);
		return result;
	}

	/**
	 * Build a new Constraint that constrain all variables in the array
	 * <code>vars</code> to be all different:
	 * <code>vars[0] != vars[1] != ...</code>
	 * Than add the constraint to the problem. 
	 * 
	 * @param vars the array of integer variable.
	 * 
	 * @return result the Constraint built.
	 */
	public Constraint postAllDifferent(javax.constraints.Var[] vars) {
		if (vars.length == 0)
			throw new RuntimeException("Variable array must not be empty.");
		Constraint result = new AllDifferent(vars);
		post(result);
		addAuxVariables(vars);
		return result;
	}

	/**
	 * Build a new Constraint that bind the cardinality of the variables
	 * of the given array of variables <code>vars</code> that satisfy the 
	 * constraint "oper <code>value</code>" to be equal the given
	 * integer <code>cardValue</code>. Where <code>oper</code> is a string 
	 * representing the commons constraint operator.
	 * 
	 * @param vars the array of integer variables
	 * @param cardValue the cardinality
	 * @param oper the string representing the operator 
	 * @param value the value that the variable must be constrained to.
	 * 
	 * @return a new Constraint.
	 */
	public Constraint postCardinality(
			javax.constraints.Var[] vars, 
			int cardValue, 
			String oper,
			int value) {
		if (vars == null)
			throw new RuntimeException("Array vars must not be null.");
		Cardinality result = new Cardinality(vars, cardValue, oper, value);
		post(result);
		addAuxVariables(vars);
		return result;
	}

	/**
	 * Build a new Constraint that bind the cardinality of the variables
	 * of the given array of variables <code>vars</code> that satisfy the 
	 * constraint "oper <code>value</code>" to be equal the given
	 * integer variable <code>cardValue</code>. Where <code>oper</code> is a 
	 * string representing the commons constraint operator.
	 * 
	 * @param vars the array of integer variables
	 * @param cardValue the cardinality
	 * @param oper the string representing the operator 
	 * @param value the value that the variable must be constrained to.
	 * 
	 * @return a new Constraint.
	 */
	public Constraint postCardinality(
			javax.constraints.Var[] vars, 
			javax.constraints.Var cardValue, 
			String oper, 
			int value) {
		if (vars == null)
			throw new RuntimeException("Array vars must not be null.");
		Cardinality result = new Cardinality(vars, cardValue, oper, value);
		post(result);
		addAuxVariables(vars);
		addAuxVariable(cardValue);
		return result;
	}


	/**
	 * Build a new Constraint that bind the cardinality of the variables
	 * of the given array of variables <code>vars</code> that satisfy the 
	 * constraint "oper <code>var</code>" to be equal the given
	 * integer <code>cardValue</code>. Where <code>oper</code> is a string 
	 * representing the commons constraint operator.
	 * 
	 * @param vars the array of integer variables.
	 * @param cardValue the cardinality
	 * @param oper the string representing the operator 
	 * @param var the integer variable that the variable must be 
	 * constrained to.
	 * 
	 * @return a new Constraint.
	 */
	public Constraint postCardinality(
			javax.constraints.Var[] vars,
			int cardValue, 
			String oper, 
			javax.constraints.Var var) {
		if (vars == null)
			throw new RuntimeException("Array vars must not be null.");
		Cardinality result = new Cardinality(vars, cardValue, oper, var);
		post(result);
		addAuxVariables(vars);
		addAuxVariable(var);
		return result;
	}
	
	/**
	 * Build and post a new Constraint that bind the cardinality of the variables
	 * of the given array of variables <code>vars</code> that satisfy the 
	 * constraint "oper <code>var</code>" to be equal the given
	 * integer variable <code>cardValue</code>. Where <code>oper</code> is a 
	 * string representing the commons constraint operator.
	 * 
	 * @param vars the array of integer variables
	 * @param cardValue the cardinality
	 * @param oper the string representing the operator 
	 * @param var the integer variable that the variable must be 
	 * constrained to.
	 * 
	 * @return a new Constraint.
	 */
	public Constraint postCardinality(
			javax.constraints.Var[] vars,
			javax.constraints.Var cardValue, 
			String oper, 
			Var var) {
		if (vars == null)
			throw new RuntimeException("Array vars must not be null.");
		Cardinality result = new Cardinality(vars, cardValue, oper, var);
		post(result);
		addAuxVariables(vars);
		addAuxVariable(cardValue);
		return result;
	}

	/**
	 * Build and post a new Constraint such as for each index i the number of time
	 * the values <code>value[i]</code> occurs in the array of variables
	 * <code>vars</code> is exactly <code>cardinalityVars</code>.
	 * 
	 * @param vars the array of integer variables
	 * @param values the integer values
	 * @param cardinalityVars the array of integer variables that represent 
	 * the cardinality the variable must be constrained to.
	 * 
	 * @return a new Constraint.
	 */
	public Constraint postGlobalCardinality(
			Var[] vars, 
			int[] values, 
			Var[] cardinalityVars) {
		Constraint c = new GlobalCardinality(vars, values, cardinalityVars);
		c.post();
		addAuxVariables(vars);
		addAuxVariables(cardinalityVars);
		return c;
	}
	
	/**
	 * For each index i the number of times the value "values[i]" 
	 * occurs in the array "vars" should be cardMin[i] and cardMax[i] (inclusive) 
	 * @param vars array of constrained integer variables
	 * @param values array of integer values within domain of all vars
	 * @param cardMin array of integers that serves as lower bounds for values[i]
	 * @param cardMax array of integers that serves as upper bounds for values[i]
	 * @return a constraint
	 * Note that arrays values, cardMin, and cardMax should have the same size 
	 * otherwise a RuntimeException will be thrown
	 */
	public Constraint postGlobalCardinality(
			Var[] vars, 
			int[] values, 
			int[] cardMin, 
			int[] cardMax) {
		Constraint c = new GlobalCardinality(vars, values, cardMin, cardMax);
		c.post();
		addAuxVariables(vars);
		return c;	
	}
	
	@Override
	public void loadFromXML(InputStream in) throws Exception {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void storeToXML(OutputStream os, String comment) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getImplVersion() {
		return JSR331_JSetL_Version;
	}

	/**
	 * Build a new constrained integer variable with the name 
	 * <code>name</code> and 
	 * domain <code>[min,max]</code>, than add it to the problem.
	 * 
	 * @param name the name of the new variable
	 * @param min the lower bound
	 * @param max the upper bound.
	 * 
	 * @return add(result) the JSetLVar built.
	 */
	public Var variable(String name, int min, int max) {
		IntLVar jSetLResult;
		if (min <= max) 
			jSetLResult = new IntLVar(name, min, max);
		else
			jSetLResult = new IntLVar(name, max, min);
		Var result = new Var(this, jSetLResult);
		return addVar(result);
	}

	/**
	 * Build a new constrained boolean variable with the name 
	 * <code>name</code> and 
	 * domain <code>[0,1]</code>, than add it to the problem.
	 * 
	 * @param name the name of the new variable.
	 * 
	 * @return add(result) the Var built.
	 */
	public VarBool variableBool(String name) {
		VarBool result = new VarBool(this, name);
		return addVarBool(result);
	}
	
	/**
	 * Auxiliary method for adding boolean variables into the problem. 
	 * Check if the
	 * given variable is already inserted, otherwise add it.
	 * 
	 * @param var the boolean variable to add.
	 * 
	 * @return the same variable. 
	 */
	private VarBool addVarBool(VarBool var) {
		javax.constraints.VarBool[] v = getVarBools();
		if (v == null) {
			add(var);
			return var;
		}
		for (int i = 0; i < v.length; i++)
			if(v[i].getName() == var.getName())
				return var;
		return (VarBool) add(var);
	}

	/**
	 * Post the constraint <code>constraint</code>.
	 * 
	 * @param constraint the JSR331 constraint to post.
	 */
	public void post(javax.constraints.Constraint constraint) {
		add(constraint);
		addJSetLConstraints((JSetL.Constraint) constraint.getImpl());
	}
	
	/**
	 * Creates a new Solver bound to the problem.
	 * 
	 * @return an instance of Solver class.
	 */
	protected Solver createSolver() {
		Solver result = new Solver(this);
		solverReady = true;
		return result;
	}
	
	/**
	 * Log the String parameter "text" using log4J info
	 */
	public void log(String text) {
		logger.info(text);
	}

	/**
	 * Log the String parameter "text" using log4J debug
	 */
	public void debug(String text) {
		logger.debug(text);
	}

	/**
	 * Log the String parameter "text" using log4J error
	 */
	public void error(String text) {
		logger.error(text);
	}

//	/**
//	 * Simple output method.
//	 */
//	public void log(String text) {
//		System.out.println(text);
//	}
//
//	@Override
//	public void debug(String text) {
//		throw new UnsupportedOperationException();
//	}
//
//	/**
//	 * Simple log error method.
//	 */
//	public void error(String text) {
//		System.out.println("Error: " + text);
//	}
	
	/**
	 * Auxiliary method that create a new variable name. The name, inside 
	 * the problem, must be unique to easy human reading, and debugging. 
	 * 
	 * @return a string of the form <code>_AuxJSetLVar-c</code> where c is 
	 * a progressive integer.
	 */
	public String getFreshName() {
		return "_AuxJSetLVar-" + (counter++);
	}
	
	/**
	 * Auxiliary method that handle the string representing operators and
	 * return an identifier value.
	 * 
	 * @param oper the string representing the operator 
	 * @return an identifier for the operator. 
	 */
	public int getOperator(String oper) {
		if (oper == "=") return OPER_EQ;
		else if (oper == "!=") return OPER_NEQ;
		else if (oper == "<") return OPER_LT;
		else if (oper == "<=") return OPER_LEQ;
		else if (oper == ">") return OPER_GT;
		else if (oper == ">=") return OPER_GEQ;
		return OPER_UNKNOWN;
	}

	/**
	 * Auxiliary method for adding variables into the problem. Check if the
	 * given variable is already inserted, otherwise add it.
	 * 
	 * @param var the variable to add.
	 * 
	 * @return var the same variable. 
	 */
	private Var addVar(Var var) {
		javax.constraints.Var[] v = getVars();
		if (v == null) {
			add(var);
			return var;
		}
		for (int i = 0; i < v.length; i++)
			if(v[i].getName() == var.getName())
				return var;
		return (Var) add(var);
	}
	
	/**
	 * Build a new Contraint that constrain all variables in the array
	 * <code>vars</code> to be all different:
	 * <code>vars[0] != vars[1] != ... != vars[n]</code>
	 * Without adding it to the problem. 
	 * 
	 * @param vars the array of integer variable.
	 * 
	 * @return result the Contraint built.
	 */
	public Constraint allDiff(javax.constraints.Var[] vars) {
		if (vars.length == 0)
			throw new RuntimeException("Variable array must not be empty.");
		IntLVar[] vec = new IntLVar[vars.length];
		for (int i = 0; i < vars.length; i++)
			vec[i] = ((Var) vars[i]).getIntLVar();
		JSetL.Constraint alldiff = IntLVar.allDifferent(vec);
		Constraint result = new Constraint(this,alldiff);
		addJSetLConstraints(alldiff);
		addAuxVariables(vars);
		return result;
	}
	
	/**
	 * Creates a constrained set variable VarSet with the name "name" and 
	 * domain of integer values from "min" to "max" inclusive.
	 * 
	 * @param name the name of the new set variable
	 * @param min the min value of the domain
	 * @param max the max value of the domain.
	 * @return a new VarSet variable.
	 */
	public VarSet variableSet(String name, int min, int max) {//throws Exception {
		MultiInterval ub = new MultiInterval(min, max);
		VarSet var = new VarSet(this, ub, name);
		add(var);
		return var;
	}
	
	/**
	 * Creates a constrained set variable VarSet with the name "name" 
	 * and which
	 * domain consists of integer values from the Set "set".
	 * 
	 * @param name the name of the new set variable
	 * @param set the domain of the new set variable.
	 * 
	 * @return the VarSet variable created and added to the problem.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public VarSet variableSet(String name, Set set)  {
		MultiInterval ub = new MultiInterval();
		ub.addAll(set);
		VarSet var = new VarSet(this, ub, name);
		add(var);
		return var;
	}
	
	/**
	 * Creates a constrained set variable VarSet with the name "name" and 
	 * which domain consists of integer values from the array "value".
	 * 
	 * @param name the name of the new variable created
	 * @param values the integer array representing the domain.
	 * 
	 * @return the VarSet variable created and added to the problem.
	 */
	public VarSet variableSet(String name, int[] values) {
		MultiInterval ub = new MultiInterval();
		for (int i = 0; i < values.length; i++)
			ub.add(values[i]);
		VarSet var = new VarSet(this, ub, name);
		add(var);
		return var;
	}

	/**
	 * Build a new constrained integer variable with the name 
	 * <code>name</code> and 
	 * domain <code>[min,max]</code>, without adding it to the problem.
	 * 
	 * @param name the name of the new variable created
	 * @param min the lower bound
	 * @param max the upper bound.
	 * 
	 * @return add(result) the Var built.
	 */
	public Var createVariable(String name, int min, int max) {
		IntLVar jSetLResult;
		if (min <= max) 
			jSetLResult = new IntLVar(name, min, max);
		else
			jSetLResult = new IntLVar(name, max, min);
		return new Var(this, jSetLResult);
	}

	/**
	 * Getter method for the set variables previously added to the
	 * problem.
	 * 
	 * @return the array of VarSets bound to the problem.
	 */
	public VarSet[] getSetVars() {
		if (varSets.isEmpty())
			return null;
		VarSet[] array = new VarSet[varSets.size()];
		Iterator<VarSet> iterator= varSets.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			array[i++] = iterator.next();
		}
		return array;
	}
	
	/**
	 * Adds a VarSet variable "var" to the problem, and returns the newly added
	 * VarSet.
	 * 
	 * @param var the variable to add to the problem.
	 * 
	 * @return the VarSet variable added to the problem.
	 */
	public VarSet add(VarSet var) {
		VarSet[] v = getSetVars();
		if (v == null) {
			add(var.getName(),var);
			return var;
		}
		else
			for (int i = 0; i < v.length; i++)
				if(v[i].getName() == var.getName())
					return var;
		return add(var.getName(),var);
	}
	
	/**
	 * Gives the VarSet variable "var" a new name "name", adds it to the 
	 * problem,
	 * and returns the newly added VarSet.
	 * 
	 * @param name the new name for var
	 * @param var the variable to add to the problem.
	 * 
	 * @return the VarSet variable added to the problem.
	 */
	public VarSet add(String name, VarSet var) {
		VarSet oldVar = getVarSet(name);
		if (oldVar != null)
			varSets.remove(oldVar);
		varSets.add(var);
		var.setName(name);
		return var;
	}
	
	/**
	 * Returns the VarSet variable with the name "name", or null if no such
	 * variable exists in the problem.
	 * 
	 * @param name the name of the desired variable.
	 * 
	 * @return the VarSet variable with the name "name", or null if no such
	 *         variable exists in the problem.
	 */
	public VarSet getVarSet(String name) {
		if (name == null)
			return null;
		Iterator<VarSet> iterator= varSets.iterator();
		while (iterator.hasNext()) {
			VarSet var = iterator.next();
			if (name.equals(var.getName()))
				return var;
		}
		return null;
	}
	
	/**
	 * Auxiliary method that add an auxiliary integer variable. Add a
	 * variable into the array if it's not a problem variable.
	 *  
	 * @param x the IntLVar to be added.
	 */
	public void addAuxVariable(JSetL.IntLVar x) {
		javax.constraints.Var[] v = getVars();
		if (v == null) {
			if(notContains(x))
				auxIntLVar.add(x);
			return;
		}
		for (int i = 0; i < v.length; i++) {
			int a = x.hashCode();
			int b = v[i].hashCode();
			if(a == b)
				return;
		}
		if(notContains(x))
			auxIntLVar.add(x);
	}

	/**
	 * Return <code>false</code> if the given IntLVar x is contained
	 * in the array of auxiliary integer variable of <code>this</code> problem.
	 * 
	 * @param x the integer variable to check.
	 */
	private boolean notContains(IntLVar x) {
		if (auxIntLVar.isEmpty())
			return true;
		IntLVar[] array = new IntLVar[auxIntLVar.size()];
		Iterator<IntLVar> iterator= auxIntLVar.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			array[i++] = iterator.next();
		}
		for (int j = 0; j < array.length; j++) {
			int a = x.hashCode();
			int b = array[j].hashCode();
			if (a == b)
				return false;
		}
		return true;
	}

	/**
	 * Add all elements of a given array of integer variables in the list of 
	 * auxiliary IntLVar of <code>this</code> problem.
	 * 
	 * @param vars the array of integer variables to be added.
	 */
	public void addAuxVariables(javax.constraints.Var[] vars) {
		if (vars == null)
			return;
		for (int i = 0; i < vars.length; i++)
			addAuxVariable((IntLVar) vars[i].getImpl());
	}
	
	/**
	 * Add an integer variable to the list of auxiliary IntLVar of <code>this</code> 
	 * problem.
	 * 
	 * @param var the integer variable to be added.
	 */
	public void addAuxVariable(javax.constraints.Var var) {
		addAuxVariable((IntLVar) var.getImpl());
	}

	/**
	 * Getter method for the auxiliary list of IntLVar of <code>this</code> problem.
	 * 
	 * @return the array of auxiliary IntLVar.
	 */
	public IntLVar[] getAuxIntLVar() {
		if (auxIntLVar.size() == 0)
			return null;
		JSetL.IntLVar[] array = new JSetL.IntLVar[auxIntLVar.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = auxIntLVar.get(i);
		}
		return array;
	}

}
