//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |   (..)  |                           *
//*         |_  J||L _|        CHOCO solver       *
//*                                               *
//*    Choco is a java library for constraint     *
//*    satisfaction problems (CSP), constraint    *
//*    programming (CP) and explanation-based     *
//*    constraint solving (e-CP). It is built     *
//*    on a event-based propagation mechanism     *
//*    with backtrackable structures.             *
//*                                               *
//*    Choco is an open-source software,          *
//*    distributed under a BSD licence            *
//*    and hosted by sourceforge.net              *
//*                                               *
//*    + website : http://choco.emn.fr            *
//*    + support : choco@emn.fr                   *
//*                                               *
//*    Copyright (C) F. Laburthe,                 *
//*                  N. Jussien    1999-2009      *
//* * * * * * * * * * * * * * * * * * * * * * * * *
package javax.constraints.impl;

/**
 * An implementation of the interface "Problem"
 * @author J.Feldman
 */

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.apache.commons.logging.LogFactory;

import javax.constraints.Constraint;
import javax.constraints.*;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.impl.constraint.*;
import java.io.InputStream;
import java.io.OutputStream;

public class Problem extends AbstractProblem {

	/**
	 * CP API Reference Implementation with Choco - release version number
	 */
	static public final String JSR331_CHOCO_VERSION = "JSR-331 Implementation based on CHOCO 2.1.5, build 2012.06.03";

	//public final static Logger LOGGER = ChocoLogging.getMainLogger();
	public static org.apache.commons.logging.Log logger = LogFactory.getLog("javax.constraints");

	Constraint falseConstraint;
	Constraint trueConstraint;

	/**
	 * @return JSR331 Implementation version number
	 */
	public String getImplVersion() {
		return JSR331_CHOCO_VERSION;
	}

	Model chocoModel;

	public Problem() {
		this("");
	}

	public Problem(String id) {
		super(id);
		chocoModel = new CPModel();
		chocoModel.setDefaultExpressionDecomposition(true); // !! recommended by Hadrien
		setDomainType(DomainType.DOMAIN_AUTOMATIC);
		ChocoLogging.setVerbosity(Verbosity.SILENT);
		//log(getImplVersion());
		if (!id.isEmpty())
			log("Problem: " + id);
	}

	/**
	 * Log the String parameter "text"
	 */
	public void log(String text) {
		//LOGGER.log(Level.INFO,text);
		logger.info(text);
	}

	/**
	 * Log the String parameter "text"
	 */
	public void debug(String text) {
		//LOGGER.log(Level.WARNING,text);
		logger.debug(text);
	}

	/**
	 * Log the String parameter "text"
	 */
	public void error(String text) {
		//LOGGER.log(Level.SEVERE,text);
		logger.error(text);
	}

	public final Model getChocoModel() {
		return chocoModel;
	}

	public final void setChocoModel(Model model) {
		this.chocoModel = model;
	}

	public CPSolver getChocoSolver() {
		javax.constraints.impl.search.Solver s =
			(javax.constraints.impl.search.Solver) getSolver();
		return s.getChocoSolver();
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
	public javax.constraints.Var createVariable(String name, int min, int max) {
		DomainType type = getDomainType();
		String chocoDomainType = javax.constraints.impl.Var.chocoDomainType(type);
		IntegerVariable cVar = Choco.makeIntVar(name, min, max, chocoDomainType);
		getChocoModel().addVariable(cVar);
		javax.constraints.impl.Var var = new javax.constraints.impl.Var(this, cVar);
		var.setName(name);
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
	public Var variable(String name, int[] domain) {
		DomainType type = getDomainType();
		String chocoDomainType = javax.constraints.impl.Var.chocoDomainType(type);
		IntegerVariable cVar = Choco.makeIntVar(name, domain, chocoDomainType);
		getChocoModel().addVariable(cVar);
		javax.constraints.impl.Var var = new javax.constraints.impl.Var(this, cVar);
//		chocoSolver.read(chocoModel); ??
//		try {
//			var.setMin(min);
//			var.setMax(max);
//		} catch (Exception e) {
//			error("Invalid domain bounds for Var: [" + min + ".."
//					+ max + "]\n" + e);
//		}
		add(var);
		var.setName(name);
		return var;
	}

	public void addChocoConstraint(choco.kernel.model.constraints.Constraint c) {
        if(!isSolverCreated()){
		    getChocoModel().addConstraint(c);
        }else{
            getChocoSolver().addConstraint(false, c);
        }
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

	public IntegerVariable[] createIntVarArray(javax.constraints.Var[] arrayOfVariables) {
		IntegerVariable[] cArray = new IntegerVariable[arrayOfVariables.length];
		for (int i = 0; i < arrayOfVariables.length; i++)
			cArray[i] = ((javax.constraints.impl.Var) arrayOfVariables[i]).getChocoVar();
		return cArray;
	}

	/**
	 * This method takes a constraint's implementation and uses its own
	 * RI-specific post-method.
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post(Constraint constraint) {
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
	public javax.constraints.extra.Reversible addReversible(String name, int value) {
		Reversible rev = new javax.constraints.impl.Reversible(this, name, value);
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
		IntegerVariable[] intVars = createIntVarArray(vars);
		IntegerExpressionVariable sum = Choco.sum(intVars); 
		int min = sum.getLowB();
		if (min < Choco.MIN_LOWER_BOUND)
			min = Choco.MIN_LOWER_BOUND;
		int max = sum.getUppB();
		if (max > Choco.MAX_UPPER_BOUND)
			max = Choco.MAX_UPPER_BOUND;
		IntegerVariable sumVar = Choco.makeIntVar("sum",min,max);
		choco.kernel.model.constraints.Constraint constraint = compare(sum,"=",sumVar);
		new javax.constraints.impl.Constraint(this,constraint).post();
		//addChocoConstraint(constraint);
		return new javax.constraints.impl.Var(this,sumVar);
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
		
		IntegerVariable[] intVars = createIntVarArray(vars);
		IntegerExpressionVariable scalar = Choco.scalar(intVars,values);
		int min = scalar.getLowB();
		if (min < Choco.MIN_LOWER_BOUND)
			min = Choco.MIN_LOWER_BOUND;
		int max = scalar.getUppB();
		if (max > Choco.MAX_UPPER_BOUND)
			max = Choco.MAX_UPPER_BOUND;
		IntegerVariable scalVar = Choco.makeIntVar("scalProd",min,max);
		choco.kernel.model.constraints.Constraint constraint = compare(scalar,"=",scalVar);
		new javax.constraints.impl.Constraint(this,constraint).post();
		//addChocoConstraint(constraint);
		return new javax.constraints.impl.Var(this,scalVar);
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
	 * Creates (without posting) a new Constraint stating that all of the elements of
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
	 * such as  cardinality(vars,cardValue)  less than  value .
	 * Here  cardinality(vars,cardValue)  denotes a constrained integer
	 * variable that is equal to the number of those elements in the
	 * array "vars" that are bound to the "cardValue".
	 * For example, if  oper  is "less then" it means that the variable
	 *  cardinality(vars,cardValue)  must be less than the   value .
	 * This constraint does NOT assume a creation of an intermediate
	 * variable "cardinality(vars,cardValue)".
	 * @param vars array of variables
	 * @param cardValue cardinality value
	 * @param oper operator
	 * @param value inetger value
	 * @return constraint
	 */
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, int value) {
		Constraint c = add(new Cardinality(vars, cardValue, oper, value));
		c.post();
		return c;
	}

	/**
	 * This method is similar to the one above but instead of  value 
	 * the  cardinality(vars,cardValue)  is being constrained by  var .
	 */
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, Var var) {
		Constraint c = add(new Cardinality(vars, cardValue, oper, var));
		c.post();
		return c;
	}


	public Constraint postGlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars) {
        Constraint c = add(new GlobalCardinality(vars,values, cardinalityVars));
		c.post();
		return c;
	}

	public Constraint postGlobalCardinality(Var[] vars, Var[] cardinalityVars) {
		Constraint c = add(new GlobalCardinality(vars,cardinalityVars));
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
	// or we may rely on the default decomposition within AbstractProblem
	public Constraint postGlobalCardinality(Var[] vars, int[] values, int[] cardMin, int[] cardMax) {
		Constraint c = add(new GlobalCardinality(vars,values,cardMin,cardMax));
		c.post();
		return c;
	}

	protected Solver createSolver() {
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
	 * @see #storeToXML
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


	public choco.kernel.model.constraints.Constraint compare(IntegerExpressionVariable var, String oper, int value) {
		Oper op = stringToOper(oper);
		switch (op) {
		case EQ:
			return Choco.eq(var,value);
		case NEQ:
			return Choco.neq(var,value);
		case GT:
			return Choco.gt(var,value);
		case GE:
			return Choco.geq(var,value);
		case LT:
			return Choco.lt(var,value);
		case LE:
			return Choco.leq(var,value);
		default:
			throw new RuntimeException("Invalid Oper " + oper);
		}
	}

	public choco.kernel.model.constraints.Constraint compare(IntegerExpressionVariable var1,
			                                                 String oper,
			                                                 IntegerExpressionVariable var2) {
		Oper op = stringToOper(oper);
		switch (op) {
		case EQ:
			return Choco.eq(var1,var2);
		case NEQ:
			return Choco.neq(var1,var2);
		case GT:
			return Choco.gt(var1,var2);
		case GE:
			return Choco.geq(var1,var2);
		case LT:
			return Choco.lt(var1,var2);
		case LE:
			return Choco.leq(var1,var2);
		default:
			throw new RuntimeException("Invalid Oper " + oper);
		}
	}

	/**
	 * Returns the constant constraint that always will fail when it is posted or executed.
	 * @return the False Constraint
	 */
	public Constraint getFalseConstraint() {
		if (falseConstraint == null) {
			falseConstraint = new javax.constraints.impl.Constraint(this, Choco.FALSE);
		}
		return falseConstraint;
	}

	/**
	 * Returns the constant constraint that always succeeds when it is posted or executed.
	 * @return the True Constraint
	 */
	public Constraint getTrueConstraint() {
		if (trueConstraint == null) {
			trueConstraint = new javax.constraints.impl.Constraint(this, Choco.TRUE);
		}
		return trueConstraint;
	}

//	/**
//	 * Log the String parameter "text" using log4j
//	 */
//	public void log(String text) {
//		LOGGER.info(text);
////		System.out.println(text); // ?? sync loggers
//	}

//	public IntExpArray getExpArray(Var[] vars) {
//		Problem problem = (Problem) vars[0].getProblem();
//		Constrainer constrainer = problem.getConstrainer();
//		IntExpArray cVars = new IntExpArray(constrainer, vars.length);
//		for (int i = 0; i < vars.length; i++) {
//			IntExp exp = (IntExp) vars[i].getImpl();
//			cVars.set(exp, i);
//		}
//		return cVars;
//	}
}
