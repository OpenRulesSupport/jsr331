//================================================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1 
// 
// CONSTRAINER-BASED REFERENCE IMPLEMENTATION
//
// Copyright (c) Cork Constraint Computation Centre, 2010
// University College Cork, Cork, Ireland, www.4c.ucc.ie
// Constrainer is copyrighted by Exigen Group, USA.
// 
//================================================================
package javax.constraints.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Constraint;
import javax.constraints.DomainType;
import javax.constraints.Oper;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.VarReal;
import javax.constraints.VarSet;
import javax.constraints.extra.ConstraintElementAtOnSets;
import javax.constraints.impl.constraint.AllDifferent;
import javax.constraints.impl.constraint.Cardinality;
import javax.constraints.impl.constraint.Element;
import javax.constraints.impl.constraint.GlobalCardinality;
import javax.constraints.impl.constraint.Linear;

import org.slf4j.LoggerFactory;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.ConstraintConst;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.FloatExp;
import com.exigen.ie.constrainer.FloatExpArray;
import com.exigen.ie.constrainer.FloatVar;
import com.exigen.ie.constrainer.IntBoolExp;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;

/**
 * Problem - a placeholder for all components used to represent and resolve a
 * constraint satisfaction problem (Problem). A Problem usually consists of two parts:
 * <ol>
 * <li>Problem Definition: contains all constrained objects and constraints that
 * define and control relationships between constrained objects
 * <li>Problem Resolution: contains search algorithm (goals) that allows to
 * solve the problem
 * </ol>
 * 
 * The decision variables are represented in form of Java objects which may use
 * the predefined constrained variables types such as Var, VarReal, VarSet.
 * 
 * The constraints themselves are objects inherited from a generic class
 * Constraint. The interface covers major binary and global constraints
 * required for the practical CP programming. It is possible to define and add
 * new constraints.
 * 
 * <p>
 * To find the problem solutions, javax.constraints uses search algorithms
 * presented using objects of the predefined type Goal. Goals are building
 * blocks to define different search algorithms.
 * 
 */

public class Problem extends AbstractProblem { 
	
	/**
	 * CP API Reference Implementation with Constrainer - release version number
	 */
	static public final String JSR331_CONSTRAINER_VERSION = "JSR-331 Implementation based on Constrainer 5.4.1";
	
	//public static org.apache.commons.logging.Log logger = LogFactory.getLog("javax.constraints");
	private static org.slf4j.Logger logger = LoggerFactory.getLogger("javax.constraints");
	
	/**
	 * @return JSR331 Implementation version number
	 */
	public String getImplVersion() {
		return JSR331_CONSTRAINER_VERSION;
	}

	Constrainer constrainer;
	
	Constraint falseConstraint;
	Constraint trueConstraint;
	
	public Problem() {
		this("");
	}

	public Problem(String id) {
		super(id);
		constrainer = new Constrainer(id);
		falseConstraint = null;
		trueConstraint = null;
		setDomainType(DomainType.DOMAIN_SMALL);
		//log(getImplVersion());
		if (!id.isEmpty())
			log("Problem: " + id);
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

	public final Constrainer getConstrainer() {
		return constrainer;
	}

	public final void setConstrainer(Constrainer constrainer) {
		this.constrainer = constrainer;
	}

	/**
	 * Creates a Var with the name "name" and domain [min;max] of domain type
	 * "type", and returns the newly added
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
	public javax.constraints.Var createVariable(String name, int min, int max) {
		javax.constraints.Var var = new javax.constraints.impl.Var(this, name, min, max);
		return var;
	}
	
	/**
	 * Creates a boolean constrained variable with the name "name" and adds
	 * this variable to the problem, and returns the newly added VarBool.
	 * @param name the name for the new Var.
	 * @return the Var variable created and added to the problem.
	 */
	public VarBool variableBool(String name) {
		javax.constraints.VarBool varBool = new javax.constraints.impl.VarBool(this, name);
		this.add(varBool);
		return varBool;
	}
	
	/**
	 * This method takes a constraint's implementation and uses its own 
	 * RI-specific post-method. 
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post(Constraint constraint) {
		try {
			com.exigen.ie.constrainer.Constraint myConstraint = 
				(com.exigen.ie.constrainer.Constraint)constraint.getImpl();
			if (myConstraint != null)
				myConstraint.post();
		} catch (Failure failure) {
			RuntimeException rte = new RuntimeException(constraint.getName(),failure);
			throw rte;
		}
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
	public javax.constraints.extra.Reversible addReversible(String name, int value) {
		Reversible rev = new Reversible(this, name, value);
		return rev;
	}
	
	/**
	 * Posts and Returns a constraint: var "oper" value
	 */
	public Constraint post(Var var, String oper, int value) {
		Constraint c = add(new Linear(var, oper, value));
		c.post();
		return c;
	}
	
	public Constraint linear(Var var, String oper, int value) {
		return add(new Linear(var, oper, value));
	}
	
	/**
	 * Posts and Returns a constraint: var1 "oper" var2
	 */
	public Constraint post(Var var1, String oper, Var var2) {
		Constraint c = add(new Linear(var1, oper, var2));
		c.post();
		return c;
	}
	
	public Constraint linear(Var var1, String oper, Var var2) {
		return add(new Linear(var1, oper, var2));
	}

//	public Constraint post(int[] array, Var[] vars, String oper, int value) {
//		Constraint c = add(new Linear(array, vars, oper, value));
//		c.post();
//		return c;
//	}
//	
//	public Constraint post(int[] array, Var[] vars, String oper, Var var) {
//		Constraint c = add(new Linear(array, vars, oper, var));
//		c.post();
//		return c;
//	}
//	
//	public Constraint post(Var[] vars, String oper, int value) {
//		Constraint c = add(new Linear(vars, oper, value));
//		c.post();
//		return c;
//	}
//	
//	public Constraint post(Var[] vars, String oper, Var var) {
//		Constraint c = add(new Linear(vars, oper, var));
//		c.post();
//		return c;
//	}
	
	// These two methods are needed for GoalAssignValue only
	public Constraint constraintVarEqValue(Var var,int value) {
		return new Linear(var,"=",value);
	}
	public Constraint constraintVarNeqValue(Var var,int value) {
		return new Linear(var,"!=",value);
	}
	
	public Constraint postElement(int[] array, Var indexVar, String oper, int value) {
		Constraint c = add(new Element(array, indexVar, oper, value));
		c.post();
		return c;
	}
	
	public Constraint postElement(int[] array, Var indexVar, String oper, Var var) {
		Constraint c = add(new Element(array, indexVar, oper, var));
		c.post();
		return c;
	}
	
	public Constraint postElement(Var[] array, Var indexVar, String oper, int value) {
		Constraint c = add(new Element(array, indexVar, oper, value));
		c.post();
		return c;
	}
	
	public Constraint postElement(Var[] array, Var indexVar, String oper, Var var) {
		Constraint c = add(new Element(array, indexVar, oper, var));
		c.post();
		return c;
	}
	
	/**
	 * Returns a constrained integer variable that is an element of the "array"
	 * with index defined as another constrained integer variable "index". When
	 * index is bound to the value i, the value of the resulting variable is
	 * array[i]. More generally, the domain of the returned variable is the set
	 * of values array[i] where the i are in the domain of index.
	 * 
	 * @param array
	 *            an array of ints.
	 * @param index
	 *            a constrained integer variable whose domain serves as an index
	 *            into the array.
	 * @return a constrained integer variable whose domain is the set of values
	 *         array[i] where each i is in the domain of "index".
	 */
//	public javax.constraints.Var elementAt(int[] array, javax.constraints.Var indexVar) {
////		if (indexVar.getMin() > array.length-1 || indexVar.getMax() < 0)
////			throw new RuntimeException("elementAt: invalid index variable");
////
////		Var elementVar = addVar("_elementVar_",array);
////		remove("_elementVar_");
//		ConstraintElementAt c = new ConstraintElementAt(array, indexVar);
//		c.post();
//		return c.getElementVar();
//	}

	/**
	 * Returns a constrained integer variable that is an element of the "array"
	 * with index defined as another constrained integer variable "indexVar".
	 * When index is bound to the value i, the value of the resulting variable
	 * is array[i]. More generally, the domain of the returned variable is the
	 * set of values array[i] where the i are in the domain of index.
	 * 
	 * @param arrayVar
	 *            an array of Var variables.
	 * @param index
	 *            a constrained integer variable whose domain serves as an index
	 *            into the array.
	 * @return a constrained integer variable whose domain is the set of values
	 *         array[i] where each i is in the domain of "indexVar".
	 */
//	public javax.constraints.Var elementAt(javax.constraints.Var[] arrayVar, 
//										   javax.constraints.Var indexVar) {
////		if (indexVar.getMin() > arrayVar.length-1 || indexVar.getMax() < 0)
////			throw new RuntimeException("elementAt: invalid index variable");
////		// Create elementVar
////		int min = arrayVar[0].getMin();
////		int max = arrayVar[0].getMax();
////		for (int i = 1; i < arrayVar.length; i++) {
////			IntExp exp = (IntExp)arrayVar[i].getImpl();
////			if (min > exp.min())
////				min = exp.min();
////			if (max < exp.max())
////				max = exp.max();
////		}
////		Var elementVar = addVar("_elementVar_",min,max);
////		remove("_elementVar_");
//		// Create and post ConstraintElementAt constraint
//		ConstraintElementAt c = new ConstraintElementAt(arrayVar, indexVar);
//		c.post();
//		return c.getElementVar();
//	}
	
	/**
	 * Creates and posts a constraint: arrayOfSets[indexVar] 'oper' setVar 
	 * Here "arrayOfSets[indexVar]" denotes a constrained set variable, which domain
	 * consists of sets of integers arrayOfSets[i] where i is within domain of the "indexVar".
	 * When indexVar is bound to the value v, the value of the resulting variable is
	 * arrayOfSets[v]. The operator 'oper' defines the type of relationships between  
	 * "arrayOfSets[indexVar]" and "setVar"
	 *
	 * @param arrayOfSets an array of integer sets.
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the "arrayOfSets".
	 * @throws RuntimeException if the posting fails 
	 * @return a newly created constraint
	 */
	public Constraint postElement(Set[] arrayOfSets, Var indexVar, String oper, VarSet setVar) {
		Constraint c = new ConstraintElementAtOnSets(setVar, arrayOfSets, indexVar);
		c.post();
		return c;
	}
	
//	/**
//	 * This method is defined for Set[] similarly to the "elementAt" for int[].
//	 * It returns a constrained set variable that is an element of the array of
//	 * sets with index defined as a constrained integer variable "indexVar".
//	 * When index is bound to the value i, the value of the resulting variable
//	 * is a constaint set sets[i].
//	 * 
//	 * @param sets
//	 *            - an array of the regular Java Set(s)
//	 * @param index
//	 *            - a constrained integer variable whose domain serves as an
//	 *            index into the array of sets.
//	 * @return a constrained integer variable
//	 */
//	public javax.constraints.VarSet elementAt(Set[] sets, javax.constraints.Var indexVar) throws Exception {
//		// TODO
//		return null;
//	}

	/**
	 * Returns a constrained integer variable that is equal to the sum of the
	 * variables in the array "arrayOfVariables".
	 * 
	 * @param vars
	 *            the array of variables from which we desire the sum.
	 * @return a constrained integer variable that is equal to the sum of the
	 *         variables in the array "vars".
	 */
	public javax.constraints.Var sum(javax.constraints.Var[] vars) {
			Problem problem = (Problem) vars[0].getProblem();
			Constrainer constrainer = problem.getConstrainer();
			IntExpArray intvars = new IntExpArray(constrainer, vars.length);
			for (int i = 0; i < vars.length; i++) {
				IntExp cvar = (IntExp) vars[i].getImpl();
				intvars.set(cvar, i);
			}
			IntExp sum = constrainer.sum(intvars);
			return new javax.constraints.impl.Var(this,sum); 
	}

//	/**
//	 * Returns a constrained real variable that is equal to the sum of the real
//	 * variables in the array "arrayOfVariables".
//	 * 
//	 * @param arrayOfVariables
//	 *            the array of real variables from which we desire the sum.
//	 * @return a constrained real variable that is equal to the sum of the real
//	 *         variables in the array "arrayOfVariables".
//	 */
//	public javax.constraints.VarReal sum(javax.constraints.VarReal[] arrayOfVariables) {
//		// TODO
//		return null;
//	}

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
	public javax.constraints.Var scalProd(int[] values, javax.constraints.Var[] vars) {
//		ScalProd c = new ScalProd(values, vars);
//		c.post();
//		return c.getScalProdVar();
		
//		int min = 0;
//		int max = 0;
//		for (int i = 0; i < vars.length; i++) {
//			min += vars[i].getMin()*values[i];
//			max += vars[i].getMax()*values[i];
//		}
//		AbstractProblem p = (AbstractProblem) vars[0].getProblem();
//		Var scalProdVar = new javax.constraints.impl.Var(p, "scalProd", min, max);
//		new Linear(values,vars, "=", scalProdVar).post();
//		return scalProdVar;
		
		Constrainer constrainer = getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer, vars.length);
		for (int i = 0; i < vars.length; i++) {
			IntExp cvar = (IntExp) vars[i].getImpl();
			intvars.set(cvar, i);
		}
		IntExp scalProd = constrainer.scalarProduct(intvars, values);
		return new javax.constraints.impl.Var(this,scalProd);
	}
	
	public javax.constraints.VarReal scalProd(double[] values, ConstrainedVariable[] vars) {
		
		Constrainer constrainer = getConstrainer();
		FloatExpArray exps = new FloatExpArray(constrainer, vars.length);
		for (int i = 0; i < vars.length; i++) {
			ConstrainedVariable var = vars[i];
			Object constrainerVar = var.getImpl();
			if (var instanceof VarReal) {
				FloatVar floatVar = (FloatVar)constrainerVar;
				exps.set(floatVar, i);
			}
			else
			if (var instanceof Var) {
				IntVar intVar = (IntVar)constrainerVar;
				exps.set(intVar.asFloat(), i);
			}
		}
		FloatExp scalProd = constrainer.scalarProduct(exps, values);
		return new javax.constraints.impl.VarReal(this,scalProd);
	}
	
//	/**
//	 * Returns a constrained real variable equal to the scalar product of an array of real variables "arrayOfVariables"
//	 *         and an array of real values "arrayOfValues".
//	 * @param arrayOfVariables the array of real variables.
//	 * @param arrayOfValues the array of real values.
//	 * @return a constrained real variable equal to the scalar product of an array of real variables "arrayOfVariables"
//	 *         and an array of real values "arrayOfValues".
//	 */
//	public VarReal scalProd(double[] arrayOfValues, VarReal[] arrayOfVariables) {
//		// TODO
//		return null;
//	}


//	/**
//	 * Returns an "AND" Constraint. The Constraint "AND" is satisfied if both
//	 * of the Constraints "c1" and "c2" are satisfied. The Constraint "AND" is
//	 * not satisfied if at least one of the Constraints "c1" or "c2" is not
//	 * satisfied.
//	 * 
//	 * @param c1
//	 *            the first Constraint which is part of the new "AND"
//	 *            Constraint.
//	 * @param c2
//	 *            the other Constraint which is part of the new "AND"
//	 *            Constraint.
//	 * @return a Constraint "AND" between the Constraints "c1" and "c2".
//	 */
//	public Constraint and(Constraint c1, Constraint c2) {
//		return new ConstraintAndOr("And", c1, c2);
//	}


//	/**
//	 * Returns an "OR" Constraint. The Constraint "OR" is satisfied if either
//	 * of the Constraints "c1" and "c2" is satisfied. The Constraint "OR" is
//	 * not satisfied if both of the Constraints "c1" and "c2" are not satisfied.
//	 * 
//	 * @param c1
//	 *            the first Constraint which is part of the new "OR"
//	 *            Constraint.
//	 * @param c2
//	 *            the other Constraint which is part of the new "OR"
//	 *            Constraint.
//	 * @return a Constraint "OR" between the Constraints "c1" and "c2".
//	 */
//	public Constraint or(Constraint c1, Constraint c2) {
//		return new ConstraintAndOr("Or", c1, c2);
//	}


	/***************************************************************************
	 * Global constraints
	 **************************************************************************/
	
	/**
	 * Creates a new Constraint (without posting) stating that all of the elements of
	 * the array of variables "vars" must take different values from each other.
	 * 
	 * @param vars
	 *            the array of Vars which must all take different values.
	 * @return the all-different Constraint on the array of Vars.
	 */
	public Constraint allDiff(javax.constraints.Var[] vars) {
		Constraint c = new AllDifferent(vars);
		return c;
	}
	
	/**
	 * This method creates and returns a new cardinality constraint 
	 * such as cardinality(vars,cardValue)  less than  value.  
	 * Here cardinality(vars,cardValue) denotes a constrained integer 
	 * variable that is equal to the number of those elements in the 
	 * array "vars" that are bound to the "cardValue".  
	 * For example, if oper is "less" it means that the variable 
	 * cardinality(vars,cardValue) must be less than the  value.  
	 * This constraint does NOT assume a creation of an intermediate 
	 * variable "cardinality(vars,cardValue)".
	 * @param vars array of vars
	 * @param cardValue cardValue
	 * @param oper operator
	 * @param value value
	 * @return constraint a newly created cardinality constraint 
	 */
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, int value) {
		Constraint c = add(new Cardinality(vars, cardValue, oper, value));
		c.post();
		return c;
	}
	
	/**
	 * This method is similar to the one above but instead of value 
	 * the cardinality(vars,cardValue) is being constrained by var.
	 */
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, Var var) {
		Constraint c = add(new Cardinality(vars, cardValue, oper, var));
		c.post();
		return c;
	}
	
	/**
	 * This method is similar to the one above but instead of cardValue 
	 * it uses "cardVar"
	 */
	public Constraint postCardinality(Var[] vars, Var cardVar, String oper, Var var) {
		Constraint c = add(new Cardinality(vars, cardVar, oper, var));
		c.post();
		return c;
	}
	
	/**
	 * This method is similar to the one above but instead of var 
	 * it uses "value"
	 */
	public Constraint postCardinality(Var[] vars, Var cardVar, String oper, int value) {
		Constraint c = add(new Cardinality(vars, cardVar, oper, value));
		c.post();
		return c;
	}
	
	public Constraint postGlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars) {
		Constraint c = add(new GlobalCardinality(vars,cardinalityVars,values));
		c.post();
		return c;
	}
	
	public Constraint constraintGlobalCardinality(Var[] vars, Var[] cardinalityVars) {
		Constraint c = add(new GlobalCardinality(vars,cardinalityVars));
		c.post();
		return c;
	}
	
	public Constraint constraintGlobalCardinality(Var[] vars, Var[] cardinalityVars, Var[] valueVars) {
		Constraint c = add(new GlobalCardinality(vars,cardinalityVars,valueVars));
		c.post();
		return c;
	}
	

	public Solver createSolver() {
		return new javax.constraints.impl.search.Solver(this);
	}
	
	/**
	 * This method associates a custom Propagator with an "event"
	 * related to changes in the domain of a constrained variable "var". It
	 * forces the solver to keep an eye on these events and invoke the
	 * Propagator "propagator" when these events actually occur. When such events
	 * occur, the propagate() method of "propagator" will be executed.
	 * 
	 * @param propagator
	 *            the Propagator we wish to associate with events on the
	 *            variable.
	 * @param var
	 *            the constrained variable we wish to add an Propagator to.
	 * @param event
	 *            the events that will trigger the invocation of the
	 *            Propagator.
	 */
//	public void subscribeTo(Propagator propagator, Var var, PropagationEvent myEvent) {
//		int event = -1;
//		switch (myEvent) {
//			case VALUE:
//				event = EventOfInterest.VALUE;
//				break;
//			case MIN:
//				event = EventOfInterest.MIN;
//				break;
//			case MAX:
//				event = EventOfInterest.MAX;
//				break;
//			case REMOVE:
//				event = EventOfInterest.REMOVE;
//				break;
//			case RANGE:
//				event = EventOfInterest.MINMAX;
//				break;
//			case ANY:
//				event = EventOfInterest.ALL;
//		}
//		if (event == -1)
//			throw new RuntimeException("ERROR: unknown event "+myEvent);
//		else {
//			ConstrainerPropagator observer = new ConstrainerPropagator(propagator, myEvent);
//			((IntExp)var.getImpl()).attachObserver(observer);
//		}
//	}


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
	 * @see storeToXML
	 */
	public void loadFromXML(InputStream in) throws Exception {
		// TODO
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
	public void storeToXML(OutputStream os, String comment) throws Exception {
		// TODO
	}
	
	public void defineConstraintImpl(Constraint constraint, IntExp constrainerVar, String oper, Var var) { 
		IntBoolExp exp;
		IntExp cvar = (IntExp) var.getImpl();
		Oper op = stringToOper(oper);
		switch (op) {
		case EQ:
			exp = constrainerVar.eq(cvar);
			break;
		case NEQ:
			exp = constrainerVar.ne(cvar);
			break;
		case GT:
			exp = constrainerVar.gt(cvar);
			break;
		case GE:
			exp = constrainerVar.ge(cvar);
			break;
		case LT:
			exp = constrainerVar.lt(cvar);
			break;
		case LE:
			exp = constrainerVar.le(cvar);
			break;
		default:
			throw new RuntimeException("Invalid Oper " + oper + 
					" in the constraint "+ constraint.getName());
		}
		constraint.setImpl(constrainerVar.constrainer().addConstraint(exp));
	}
	
	public void defineConstraintImpl(Constraint constraint, IntExp constrainerVar, String oper, int value) { 
		IntBoolExp exp;
		Oper op = stringToOper(oper);
		switch (op) {
		case EQ:
			exp = constrainerVar.eq(value);
			break;
		case NEQ:
			exp = constrainerVar.ne(value);
			break;
		case GT:
			exp = constrainerVar.gt(value);
			break;
		case GE:
			exp = constrainerVar.ge(value);
			break;
		case LT:
			exp = constrainerVar.lt(value);
			break;
		case LE:
			exp = constrainerVar.le(value);
			break;
		default:
			throw new RuntimeException("Invalid Oper " + oper + 
					" in the constraint "+ constraint.getName());
		}
		constraint.setImpl(constrainerVar.constrainer().addConstraint(exp));
	}

	public IntExpArray getExpArray(Var[] vars) {
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray cVars = new IntExpArray(constrainer, vars.length);
		for (int i = 0; i < vars.length; i++) {
			IntExp exp = (IntExp) vars[i].getImpl();
			cVars.set(exp, i);
		}
		return cVars;
	}
	
	/**
	 * Creates a set variable that corresponds to sets[indexVar]
	 * @param sets a set of Integers
	 * @param indexVar an index variable
	 * @return a set variable
	 * @throws Exception if any error
	 */
	public VarSet element(Set<Integer>[] sets, Var indexVar) throws Exception {
		Set union = new HashSet();
		for (int i = 0; i < sets.length; i++) {
			Iterator iter = sets[i].iterator();
			while (iter.hasNext()) {
				Integer valueObj = (Integer) iter.next();
				union.add(valueObj);
			}
		}
		int[] values = new int[union.size()];
		Iterator iter2 = union.iterator();
		int u = 0;
		while (iter2.hasNext()) {
			values[u++] = ((Integer)iter2.next()).intValue();
		}
		VarSet setVar = variableSet("indexVarSet for "+indexVar.getName(), values);
		Constraint c = new ConstraintElementAtOnSets(setVar,sets,indexVar);
		c.post();
		return setVar;
	}
	
	/**
	 * Returns the constant constraint that always will fail when it is posted or executed.
	 * @return the False Constraint 
	 */
	public Constraint getFalseConstraint() {
		if (falseConstraint == null) {
			com.exigen.ie.constrainer.Constraint myConstraint = new ConstraintConst(constrainer, false);
			falseConstraint = new javax.constraints.impl.Constraint(this, myConstraint);
		}
		return falseConstraint;
	}
	
	/**
	 * Returns the constant constraint that always succeeds when it is posted or executed.
	 * @return the True Constraint 
	 */
	public Constraint getTrueConstraint() {
		if (trueConstraint == null) {
			com.exigen.ie.constrainer.Constraint myConstraint = new ConstraintConst(constrainer, false);
			trueConstraint = new javax.constraints.impl.Constraint(this, myConstraint);
		}
		return trueConstraint;
	}
}
