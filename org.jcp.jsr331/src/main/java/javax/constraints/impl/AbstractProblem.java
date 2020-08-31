//=============================================
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Constraint;
import javax.constraints.DomainType;
import javax.constraints.Oper;
import javax.constraints.Problem;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.VarMatrix;
import javax.constraints.VarReal;
import javax.constraints.VarSet;
import javax.constraints.VarString;
import javax.constraints.extra.Reversible;
import javax.constraints.impl.constraint.ConstraintFalse;
import javax.constraints.impl.constraint.ConstraintGlobalCardinality;
import javax.constraints.impl.constraint.ConstraintMax;
import javax.constraints.impl.constraint.ConstraintMin;
import javax.constraints.impl.constraint.ConstraintTrue;
import javax.constraints.impl.search.goal.Goal;

import javax.constraints.Probability;

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
 * @see Var
 * @see Constraint
 * @see Goal
 * 
 */

abstract public class AbstractProblem implements Problem {

	/**
	 * @return JSR331 API version number
	 */
	public String getAPIVersion() {
		return JSR331_VERSION;
	}
	
	/**
	 * @return JSR331 Implementation version number
	 */
	abstract public String getImplVersion();
	
	
	private HashMap<String, Oper> operators;

	String name;
	Object object;
	ArrayList<Var> vars;
	ArrayList<VarReal> varReals;
	ArrayList<VarBool> varBools;
	HashMap<String,Var[]> varArrays;
	ArrayList<Constraint> constraints;
	Solver	solver;
	DomainType domainType;
	// saved state
	ArrayList<Var> savedVars;
	ArrayList<VarBool> savedVarBools;
	HashMap<String,Var[]> savedVarArrays;
	ArrayList<Constraint> savedConstraints;
	HashMap<String,VarMatrix> varMatrixs;
	ArrayList<VarString> varStrings;
	ArrayList<Var> constraintViolations;
	
	static protected double REAL_PRECISION = 1.0e-6; // TODO move to VarRealI 

	public AbstractProblem(String name) {
		this.name = name;
		log(getAPIVersion());
		log(getImplVersion());
		vars = new ArrayList<Var>();
		varReals = new ArrayList<VarReal>();
		varBools = new ArrayList<VarBool>();
		constraints = new ArrayList<Constraint>();
		varArrays = new HashMap<String,Var[]>();
		varMatrixs = new HashMap<String, VarMatrix>();
		varStrings = new ArrayList<VarString>();
		constraintViolations = new ArrayList<Var>();
		solver = null;
		operators = new HashMap<String, Oper>();
		operators.put("=", 	Oper.EQ);
		operators.put("==", Oper.EQ);
		operators.put("Is", Oper.EQ);
		operators.put("is", Oper.EQ);
		operators.put("!=", Oper.NEQ);
		operators.put("Is Not", Oper.NEQ);
		operators.put("is not", Oper.NEQ);
		operators.put("<", 	Oper.LT);
		operators.put("<=", Oper.LE);
		operators.put(">", 	Oper.GT);
		operators.put(">=", Oper.GE);
	}

	/**
	 * 
	 * @return the problem name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Sets the problem name
	 * 
	 * @param name
	 */
	public final void setName(String name) {
		this.name = name;
	}
	
	public final DomainType getDomainType() {
		return domainType;
	}

	public final void setDomainType(DomainType domainType) {
		this.domainType = domainType;
	}

	/**
	 * Adds a Var variable "var" to the problem, and returns the newly added
	 * Var.
	 * 
	 * @param var
	 *            the variable to add to the problem.
	 * @return the Var variable added to the problem.
	 */
	public Var add(Var var) {
		add(var.getName(),var);
		return var;
	}
	
	/**
	 * Adds a VarBool variable "var" to the problem, and returns the newly added
	 * Var.
	 * 
	 * @param varBool
	 *            the boolean variable to add to the problem.
	 * @return the VarBool variable added to the problem.
	 */
	public VarBool add(VarBool varBool) {
		varBools.add(varBool);
		return varBool;
	}
	
	/**
	 * Removes a Var variable by "name".
	 *
	 * @param varName the name of the variable to be removed from the problem.
	 */
	public void remove(String varName) {
		Var var = getVar(varName);
		if (var != null) {
			boolean result = vars.remove(var);
			if (!result)
				result = varReals.remove(var);
			if (!result)
				result = varBools.remove(var);
			// TO DO the same for varSets
		}
	}

	/**
	 * Gives the Var variable "var" a new name "name", adds it to the problem,
	 * and returns the newly added Var.
	 * 
	 * @param name
	 *            the new name for var.
	 * @param var
	 *            the variable to add to the problem.
	 * @return the Var variable added to the problem.
	 */
	public Var add(String name, Var var) {
		Var oldVar = getVar(name);
		if (oldVar != null) {
			//log("Replacing variable " + oldVar + " with " + var + " using the name " + name);
			vars.remove(oldVar);
		}
		vars.add(var);
		var.setName(name);
		return var;
	}

	/**
	 * Adds a VarReal variable "var" to the problem, and returns the newly added
	 * VarReal.
	 * 
	 * @param var
	 *            the real variable to add to the problem.
	 * @return the VarReal variable added to the problem.
	 */
	public VarReal add(VarReal var) {
		return add(var.getName(),var);
	}
	
	/**
	 * Gives the VarReal variable "var" a new name "name", adds it to the
	 * problem, and returns the newly added VarReal.
	 * 
	 * @param name
	 *            the new name for var.
	 * @param var
	 *            the real variable to add to the problem.
	 * @return the VarReal variable added to the problem.
	 */
	public VarReal add(String name, VarReal var) {
		VarReal oldVar = getVarReal(name);
		if (oldVar != null)
			varReals.remove(oldVar);
		varReals.add(var);
		var.setName(name);
		return var;
	}

	/**
	 * Creates a Var variable based on the symbolicExpression, adds this
	 * variable to the problem, and returns the newly added Var.
	 * 
	 * If the returned Var is null, there is either an error inside the
	 * symbolicExpression or there is no concrete solver implementation of this
	 * method. In either case, the constraint will not have been added to the
	 * problem.
	 * 
	 * @param symbolicExpression
	 *            a string that defines a constrained expression that uses
	 *            already defined variables.
	 * @return the Var variable created and added to the problem, or null if
	 *         there is an error inside the symbolicExpression or if this method
	 *         is not defined by a concrete solver implementation.
	 */
	public Var var(String name, String symbolicExpression) {
		error("The implementation does not support symbolic Var expressions like:\n"
				+ symbolicExpression);
		return null;
	}
	
	/**
	 * Creates a Constraint based on the symbolicExpression such as "x*y-z=3*r", 
	 * adds this constraint to the problem, and returns the newly added Constraint.
	 * It is assumed that all variables in the expression were previously created under names
	 * used within this expression.
	 *
	 * If the returned Constraint is null, there is either an error inside the symbolicExpression
	 * or there is no concrete solver implementation of this method. In either case, the
	 * constraint will not have been added to the problem.
	 *
	 * @param name the name for a new constraint  
	 * @param symbolicExpression
	 *            a string that defines a constrained expression that uses
	 *            already defined variables, e.g. "x*y-z=3*r"
	 * @return the constraint created and added to the problem, or null if there is an error inside the
	 *         symbolicExpression or if this method is not defined by a concrete
	 *         solver implementation.
	 */
	public Constraint post(String name, String symbolicExpression) {
		error("The implementation does not support symbolic constraints like:\n"
				+ symbolicExpression);
		return null;
	}

	public Var variable(String name, int min, int max) {
		Var var = createVariable(name, min, max);
		add(var);
		return var;
	}
	
	abstract public Var createVariable(String name, int min, int max);
	
	/**
	 * Creates a VarReal with the name "name" and domain [min;max] of the default domain type, 
	 * and returns the newly added VarReal
	 *
	 * @param name the name for the new VarReal.
	 * @param min the minimum value in the domain for the new VarReal.
	 * @param max the maximum value in the domain for the new VarReal.
	 * @return the VarReal variable created and added to the problem.
	 */
	public VarReal createVariableReal(String name, double min, double max) {
		error("Problem's method createVariableReal(String name, double min, double max) is not implemented");
		return null;
	}
	
	/**
	 * Similar to variable(String name, int min, int max) but also has one more parameter 
	 * that defines the default domain type
	 *
	 * @param name the name for the new Var
	 * @param min the minimum value in the domain for the new Var
	 * @param max the maximum value in the domain for the new Var
	 * @param domainType {@link DomainType}
	 * @return the Var variable created and added to the problem
	 */
	public Var variable(String name, int min, int max, DomainType domainType) {
		DomainType current = getDomainType();
		setDomainType(domainType);
		Var var = variable(name,min,max);
		setDomainType(current);
		return var;
	}
	
	public Var variable(int min, int max) {
		Var var = variable("noname",min,max);
		remove("noname");
		var.setName("");
		return var;
	}

	
	/**
	 * Creates a VarReal with the name "name" and domain [min;max], adds this
	 * variable to the problem, and returns the newly added VarReal.
	 * 
	 * @param name
	 *            the name for the new VarReal.
	 * 
	 * @param min
	 *            the minimum value in the domain for the new VarReal.
	 * 
	 * @param max
	 *            the maximum value in the domain for the new VarReal.
	 * 
	 * @return the VarReal variable created and added to the problem.
	 */
	public VarReal variableReal(String name, double min, double max) {
		VarReal var = createVariableReal(name, min, max);
		add(var);
		return var;
	}

	/**
	 * Creates a VarReal with the name "name" and default domain
	 * [Double.MIN_VALUE+1;Double.MAX_VALUE-1], adds this variable to the problem, and
	 * returns the newly added VarReal.
	 * 
	 * @param name
	 *            the name for the new VarReal.
	 * 
	 * @return the VarReal variable created and added to the problem.
	 */
	public VarReal variableReal(String name) {
		return variableReal(name,Double.MIN_VALUE+1,Double.MAX_VALUE-1);
	}

//	/**
//	 * Creates a BasicVarString with the name "name" and domain "values", adds this
//	 * variable to the problem and returns the newly added BasicVarString.
//	 * 
//	 * @param name
//	 *            the name for the new Var.
//	 * @param values
//	 *            an array of string values defining the domain of the new
//	 *            BasicVarString.
//	 * @return the BasicVarString variable created and added to the problem.
//	 */
//	BasicVarString addVarString(String name, String[] values) {
//		// TODO
//		return null;
//	}

	/**
	 * Creates a constrained set variable VarSet with the name "name" and domain
	 * of integer values from "min" to "max" inclusive.
	 * 
	 * This interface show how to create a new constrained set variable which,
	 * when bound, is equal to a set of elements. The domain of a constrained
	 * set variable is a set of Sets that consist of regular integers.The
	 * cardinality of a set constrained variable is an integer constrained
	 * variable.
	 * 
	 * @param name
	 * @param min
	 * @param max
	 * @return a new VarSet variable
	 */
	public VarSet variableSet(String name, int min, int max) throws Exception {
		VarSet var = new BasicVarSet(this, min, max, name);
		var.setEmpty(false); 
		return var;
	}

	/**
	 * Creates a constrained set variable VarSet with the name "name" and which
	 * domain consists of integer values from the array "value".
	 * 
	 * @param name
	 * @param values
	 * @return the VarSet variable created and added to the problem.
	 */
	public VarSet variableSet(String name, int[] values) throws Exception {
		VarSet var = new BasicVarSet(this, values, name);
		var.setEmpty(false); 
		return var;
	}
	
	/**
	 * Creates a constrained set variable VarSet with the name "name" and which
	 * domain consists of integer values from the Set "set".
	 * 
	 * @param name
	 * @param set
	 * @return the VarSet variable created and added to the problem.
	 */
	public VarSet variableSet(String name, Set set) throws Exception {
		VarSet var = new BasicVarSet(this, set, name);
		var.setEmpty(false); 
		return var;
	}

//	/**
//	 * Synonym for addVarString(String name, String[] values);
//	 * 
//	 * @see AbstractProblem#addVarString(String, String[])
//	 */
//	public BasicVarString addString(String name, String[] values) {
//		// TODO
//		return null;
//	}
	
	/**
	 * Adds an array of Var variables with the name "name" and domains
	 * [min;max] of domain type "type", adds this array of variables to the
	 * problem, and returns the newly added Var array. The "size" parameter
	 * specifies how many Var variables are contained in this new array.
	 * 
	 * @param name
	 *            the name for the new Var array.
	 * @param min
	 *            the minimum value in the domains for the new Vars in the new
	 *            Var array.
	 * @param max
	 *            the maximum value in the domain for the new Vars in the new
	 *            Var array.
	 * @param size
	 *            the number of Vars in the new Var array.
	 * 
	 * 
	 * @return the array of Var variables created and added to the problem.
	 */
	public Var[] variableArray(String name, int min, int max, int size) {
		Var[] array = new Var[size];
		for (int i = 0; i < size; i++) {
			String iName = (name == null || name.isEmpty()) ? "" : (name + "-" + i);
			array[i] = variable(iName, min, max);
		}
		return array;
	}
	

	/**
	 * Gives the array of Var variables "vars" a new name "name", adds the array
	 * of variables to the problem, and returns the newly added Var array.
	 * 
	 * @param name
	 *            the new name for the array "vars".
	 * @param vars
	 *            the array of Var variables to add to the problem.
	 * 
	 * @return the array of Var variables added to the problem.
	 */
	public Var[] addVarArray(String name, Var[] vars) {
		varArrays.put(name,vars);
		return vars;
	}

	/**
	 * Creates an array of Var variables with name "arrayName". The variables to
	 * be contained in the array must have already been added to the problem,
	 * and their names are specified in "varNames".
	 * 
	 * @param arrayName
	 *            the name for the new Var array.
	 * @param varNames
	 *            an array of names of already added variables, which are to
	 *            form the new array.
	 * 
	 * @return the array of Var variables created and added to the problem.
	 */
	public Var[] addVarArray(String arrayName, String[] varNames){
		int n = varNames.length;
		Var[] array = new Var[n];
		for (int i = 0; i < n; i++)
			array[i] = getVar(varNames[i]);
		varArrays.put(arrayName,array);
		return array;
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
		int max = Integer.MIN_VALUE+1;
		int min = Integer.MAX_VALUE-1;
		for (int i = 0; i < domain.length; i++) {
			int v = domain[i];
			if (min > v)
				min = v;
			if (max < v)
				max = v;
		}
		Var var = variable( name, min, max );
		
		// var.setDomainType(DomainType.DOMAIN_MIN_MAX); // added Nov 25, 2015

		if(domain.length <= Math.abs(max - min)) {
			int counter = 1;
			AbstractVar abstractVar = (AbstractVar)var;
			for (int i = min+1; i < max; i++) {
				if(domain[counter] != i){
					try{
						post(abstractVar,"!=",i);
					}catch (Exception e) {
						log("value " + i + "can not be removed from " + var);
					}
				}
				else
					counter++;
			}
		}
		return var;
	}
	
	public Var variable(int[] domain) {
		return variable("",domain);
	}
	
	/**
	 * Creates a boolean constrained variable with the name "name" and adds
	 * this variable to the problem, and returns the newly added VarBool.
	 * @param name the name for the new Var.
	 * @return the Var variable created and added to the problem.
	 */
	abstract public VarBool variableBool(String name);
	
//	/**
//	 * Adds a Var variable with the name "name" and domain
//	 * [Problem.INT_MIN;Problem.INT_MAX] of default domain type, adds this
//	 * variable to the problem and returns the newly added Var.
//	 * 
//	 * @param name
//	 *            the name for the new Var.
//	 * @return the Var variable created and added to the problem.
//	 */
//	public Var addVar(String name) {
//		return var(name, Integer.MIN_VALUE+1, Integer.MAX_VALUE-1);
//	}

	/**
	 * Returns an array containing the Var variables previously added to the
	 * problem.
	 * 
	 * @return an array containing the Var variables previously added to the
	 *         problem.
	 */
	public Var[] getVars() {
		if (vars.isEmpty())
			return null;
		Var[] array = new Var[vars.size()];
		Iterator<Var> iterator= vars.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			array[i++] = iterator.next();
		}
		return array;
	}
	
	public ArrayList<Var> getVarArray() {
	    return vars;
	}
	
	public ArrayList<VarReal> getVarRealArray() {
        return varReals;
    }
	
	/**
	 * Returns an array containing the VarBool variables previously added to the problem.
	 * @return an array containing the VarBool variables previously added to the problem.
	 */
	public VarBool[] getVarBools() {
		if (varBools.isEmpty())
			return null;
		VarBool[] array = new VarBool[varBools.size()];
		Iterator<VarBool> iterator= varBools.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			array[i++] = iterator.next();
		}
		return array;
		
	}
	
	/**
	 * Creates a bool var not added to the problem.
	 * @return the Var variable created 
	 */
	public VarBool variableBool() {
		VarBool var = variableBool("noname");
		remove("noname");
		var.setName("");
		return var;
	}

	/**
	 * Returns an array containing the VarReal variables previously added to the
	 * problem.
	 * 
	 * @return an array containing the VarReal variables previously added to the
	 *         problem.
	 */
	public VarReal[] getVarReals() {
		if (varReals.isEmpty())
			return null;
		VarReal[] array = new VarReal[varReals.size()];
		Iterator<VarReal> iterator= varReals.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			array[i++] = iterator.next();
		}
		return array;
	}
	
	/**
	 * Returns an array containing the VarSet variables previously added to the
	 * problem.
	 * 
	 * @return an array containing the VarSet variables previously added to the
	 *         problem.
	 */
	public VarReal[] getVarSets() {
		// TODO
		return null;
	}

	/**
	 * Returns the Var variable with the name "name", or null if no such
	 * variable exists in the problem.
	 * 
	 * @param name
	 *            the name of the desired variable.
	 * @return the Var variable with the name "name", or null if no such
	 *         variable exists in the problem.
	 */
	public Var getVar(String name) {
		if (name == null)
			return null;
		Iterator<Var> iterator= vars.iterator();
		while (iterator.hasNext()) {
			Var var = iterator.next();
			if (name.equals(var.getName()))
				return var;
		}
		//error("Cannot find Var "+name);
		return null;
	}

	/**
	 * Returns the VarReal variable with the name "name", or null if no such
	 * variable exists in the problem.
	 * 
	 * @param name
	 *            the name of the desired variable.
	 * @return the VarReal variable with the name "name", or null if no such
	 *         variable exists in the problem.
	 */
	public VarReal getVarReal(String name) {
		if (name == null)
			return null;
		Iterator<VarReal> iterator= varReals.iterator();
		while (iterator.hasNext()) {
			VarReal var = iterator.next();
			if (name.equals(var.getName()))
				return var;
		}
		//error("Cannot find Var "+name);
		return null;
	}

	/**
	 * Returns the array of Var variables with the name "name", or null if no
	 * such array exists in the problem.
	 * 
	 * @param name
	 *            the name of the desired array of Var variables.
	 * @return the array of Var variables with the name "name", or null if no
	 *         such array exists in the problem.
	 */
	public Var[] getVarArray(String name) {
		return varArrays.get(name);
	}

	/**
	 * Creates a 2-dimensional array of Var variables, with dimensions
	 * "size"x"size". The array has name "name" and each Var in the array has
	 * domain[min;max] of default domain type. The array is added to the
	 * problem, and then the array is returned.
	 * 
	 * If "name" is a non-null string "X" then each element [i,j] can be found
	 * by using Problem method getInt("X[i,j]").
	 * 
	 * The default domain type is Var.DOMAIN_SMALL, but the chosen
	 * implementation may override this default with its own default.
	 * 
	 * @param min
	 *            the minimum value in the domains for the new Vars in the new
	 *            Var array.
	 * @param max
	 *            the maximum value in the domain for the new Vars in the new
	 *            Var array.
	 * @param size
	 *            the dimensions of the new 2-dimensional array. The array
	 *            contains size*size elements.
	 * 
	 * @return the array of Var variables created and added to the problem.
	 */
	public Var[] varSquare(String name, int min, int max, int size) {
		Var[] array = new Var[size * size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				String iName = (name == null) ? "" : name + "[" + i + "," + j
						+ "]";
				array[i * size + j] = variable(iName, min, max);
			}
		}
		if (name != null)
			varArrays.put(name, array);
		return array;
	}

	/**
	 * Adds a Constraint "constraint" to the problem, then returns the
	 * constraint. The Constraint is not posted.
	 * 
	 * @param constraint
	 *            the constraint to add to the problem.
	 * @return the Constraint added to the problem.
	 */
	public Constraint add(Constraint constraint) {
		return add(constraint.getName(),constraint);
	}

	/**
	 * Gives a Constraint "constraint" a new name "name", adds the Constraint
	 * to the problem and returns the newly added Constraint. The Constraint
	 * is not posted.
	 * 
	 * @param name
	 *            the new name for the Constraint.
	 * @param constraint
	 *            the Constraint to add to the problem
	 * 
	 * @return the Constraint added to the problem.
	 */
	public Constraint add(String name, Constraint constraint) {
		constraint.setName(name);
		constraints.add(constraint);
		return constraint;
	}

	/**
	 * Returns an array of Objects containing the Constraints previously added
	 * to the problem.
	 * 
	 * @return an array of Objects containing the Constraints previously added
	 *         to the problem.
	 */
	public Constraint[] getConstraints() {
//		if (constraints.size() == 0)
//			return null;
		Constraint[] array = new Constraint[constraints.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = constraints.get(i);
		}
		return array;
	}
	
	public final Constraint getConstraint(String name) {
		if (constraints.size() == 0)
			return null;
		for (int i = 0; i < constraints.size(); i++) {
			if (name.endsWith(constraints.get(i).getName()))
				return constraints.get(i);
		}
		return null;
	}
	
	protected Oper stringToOper(String oper) { 
		Oper op = operators.get(oper);
		if (op == null)
			throw new RuntimeException("Invalid Operator " + oper);
		return op;
	}
	
	/**
	 * Returns a constrained integer variable that is an element of the integer "array"
	 * with index defined as another constrained integer variable "indexVar". When
	 * index is bound to the value i, the value of the resulting variable is
	 * array[i]. More generally, the domain of the returned variable is the set of
	 * values array[i] where the i are in the domain of index.
	 *
	 * @param array an array of integers
	 * @param indexVar a constrained integer variable whose domain serves as an index into
	 *            the array.
	 * @return a constrained integer variable whose domain is the set of values array[i] where each i
	 *         is in the domain of "indexVar".
	 */
	public Var element(int[] array, Var indexVar) {
		Var elemVar = variable("_element_",array);
		postElement(array, indexVar, "=", elemVar);
		return elemVar;
	}
	
	/**
	 * Returns a constrained integer variable that is an element of the "array" of variables
	 * with index defined as another constrained integer variable "indexVar". When
	 * index is bound to the value i, the value of the resulting variable is
	 * array[i]. More generally, the domain of the returned variable is the set of
	 * values array[i] where the i are in the domain of index.
	 *
	 * @param array an array of Var variables.
	 * @param indexVar a constrained integer variable whose domain serves as an index into
	 *            the array.
	 * @return a constrained integer variable whose domain is the set of values array[i] where each i
	 *         is in the domain of "indexVar".
	 */
	public Var element(Var[] array, Var indexVar) {
		int min = array[0].getMin();
		int max = array[0].getMax();
		for (int i = 1; i < array.length; i++) {
			Var var = array[i];
			if (min > var.getMin())
				min = var.getMin();
			if (max < var.getMax())
				max = var.getMax();
		}
		Var elemVar = variable("_element_",min,max);
		postElement(array, indexVar, "=", elemVar);
		return elemVar;
	}
	
	public Var element(ArrayList<Var> list, Var indexVar) {
		Var[] array = (Var[])list.toArray(new Var[list.size()]);
		return element(array,indexVar);
	}
	
	
	/**
	 * Creates a set variable that corresponds to sets[index]
	 * @param sets
	 * @param indexVar
	 * @return a set variable
	 * @throws Exception
	 */
	public VarSet element(Set<Integer>[] sets, Var indexVar) throws Exception {
		error("Method element(Set[] sets, Var indexVar) is not implemented");
		return null;
	}
	
	/**
	 * Creates and posts a constraint an "AND" Constraint. The Constraint "AND" is satisfied if both of the
	 * Constraints "c1" and "c2" are satisfied. The Constraint "AND" is not satisfied
	 * if at least one of the Constraints "c1" or "c2" are not satisfied.
	 * @param c1 a Constraint
	 * @param c2 a Constraint  
	 * @return a Constraint "AND" between the Constraints "c1" and "c2".
	 */
	public Constraint postAnd(Constraint c1, Constraint c2) {
		Constraint and = c1.and(c2);
		and.post();
		return and;
	}
	
	/**
	 * Creates and posts a constraint an "OR" Constraint. The Constraint "OR" is satisfied if 
	 * at least one of constraints "c1" or "c2" are satisfied. The Constraint "OR" is not satisfied
	 * if both constraints "c1" or "c2" are not satisfied.
	 * @param c1 a Constraint
	 * @param c2 a Constraint  
	 * @return a Constraint "OR" between the Constraints "c1" and "c2".
	 */
	public Constraint postOr(Constraint c1, Constraint c2) {
		Constraint or = c1.or(c2);
		or.post();
		return or;
	}
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(int[] array, Var[] vars, String oper, Var var) {
		Var scalProd = scalProd(array,vars);
		return post(scalProd,oper,var);
	}
	
	@Deprecated
	public Constraint post(int[] array, Var[] vars, String oper, int value) {
		Var scalProd = scalProd(array,vars);
		return post(scalProd,oper,value);
	}
	
	/**
	 * Creates and posts a constraint: sum of vars 'oper' value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(Var[] vars, String oper, int value) {
		Var sum = sum(vars);
		return post(sum,oper,value);
	}
	
	/**
	 * Creates and posts a constraint: sum of vars "oper" var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(Var[] vars, String oper, Var var) {
		Var sum = sum(vars);
		return post(sum,oper,var);
	}
	
	
	/**
	 * Creates and posts a constraint: (var1 + var2) "oper" value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(Var var1, Var var2, String oper, int value) {
		Var sum = var1.plus(var2);
		return post(sum,oper,value);
	}
	
	/**
	 * Creates and posts a constraint: (var1 + var2) "oper" var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(Var var1, Var var2, String oper, Var var) {
		Var sum = var1.plus(var2);
		return post(sum,oper,var);
	}
	
	@Deprecated
	public Constraint post(int[] array, ArrayList<Var> vars, String oper, int value) {
		Var[] varArray = (Var[])vars.toArray(new Var[vars.size()]);
		return post(array,varArray,oper,value);
	}
	
	@Deprecated
	public Constraint post(int[] array, ArrayList<Var> vars, String oper, Var var) {
		Var[] varArray = (Var[])vars.toArray(new Var[vars.size()]);
		return post(array,varArray,oper,var);
	}
		
//	public Constraint postLinear(int[] array, Var[] vars, String oper, int value) {
//		return post(array,vars,oper,value);
//	}
	
//	public Constraint postLinear(int[] array, Var[] vars, String oper, Var var) {
//		return post(array,vars,oper,var);
//	}
	
//	public Constraint postLinear(int[] array, ArrayList<Var> vars, String oper, Var var) {
//		Var[] arrayVars = vars.toArray();
//		return postLinear(array,arrayVars,oper,var);
//	}
	
//	public Constraint postLinear(int[] array, ArrayList<Var> vars, String oper, int value) {
//		Var[] arrayVars = vars.toArray();
//		return postLinear(array,arrayVars,oper,value);
//	}
	
//	public Constraint postLinear(Var[] vars, String oper, int value) {
//		return post(vars,oper,value);
//	}
	
//	public Constraint postLinear(Var[] vars, String oper, Var var) {
//		return post(vars,oper,var);
//	}
	
//	public Constraint postLinear(Var var, String oper, int value) {
//		return post(var,oper,value);
//	}
	
//	public Constraint postLinear(Var var1, String oper, Var var2) {
//		return post(var1, oper, var2);
//	}
	
	
	/**
	 * Creates and posts a constraint: var "oper" value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(VarReal var, String oper, int value) {
		error("Problem's method post(VarReal var, String oper, int value) is not implemented");
		return null;
	}
	
	/**
	 * Creates and posts a constraint: var "oper" value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(VarReal var, String oper, double value) {
		error("Problem's method post(VarReal var, String oper, double value) is not implemented");
		return null;
	}
	
//	/**
//	 * Synonym for constraint(VarReal var, String oper, int value)
//	 */
//	public Constraint postLinear(VarReal var, String oper, int value) {
//		return post(var,oper,value);
//	}
		
	/**
	 * Creates and posts a constraint: var1 "oper" var2
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(VarReal var1, String oper, VarReal var2) {
		error("Problem's method post(VarReal var1, String oper, VarReal var2) is not implemented");
		return null;
	}
	
	/**
	 * Creates and posts a constraint: sum of vars "oper" double value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(VarReal[] vars, String oper, double value) {
		double[] array = new double[vars.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = 1.0;
		}
		return post(array,vars,oper,value);
	}
	
	/**
	 * Creates and posts a constraint: sum of vars "oper" var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(VarReal[] vars, String oper, VarReal var) {
		double[] array = new double[vars.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = 1.0;
		}
		return post(array,vars,oper,var);
	}
	
	/**
	 * Creates and posts a constraint: var1 "oper" var2
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(VarReal var1, String oper, Var var2) {
		error("Problem's method post(VarReal var1, String oper, Var var2) is not implemented");
		return null;
	}
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' value
	 * for an "array: of real coefficients to be multiplied by an array "vars"
	 * of integer constrained variables 
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(double[] coefficients, Var[] vars, String oper, double value) {
		error("Problem's method post(double[] array, Var[] vars, String oper, double value) is not implemented");
		return null;
	} 
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(double[] array, VarReal[] vars, String oper, VarReal var) {
		error("Problem's method post(double[] array, VarReal[] vars, String oper, VarReal var) is not implemented");
		return null;
	}
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(double[] array, VarReal[] vars, String oper, double value) {
		error("Problem's method post(double[] array, VarReal[] vars, String oper, double value) is not implemented");
		return null;
	}
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(double[] array, ConstrainedVariable[] vars, String oper, ConstrainedVariable var) {
		error("Problem's method post(double[] array, ConstrainedVariable[] vars, String oper, VarReal var) is not implemented");
		return null;
	}
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(double[] array, ConstrainedVariable[] vars, String oper, double value) {
		error("Problem's method post(double[] array, ConstrainedVariable[] vars, String oper, double value) is not implemented");
		return null;
	}
	
	/**
	 * Creates and posts a constraint: var1 "oper" var2
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(Var var1, String oper, VarReal var2) {
		error("Problem's method post(Var var1, String oper, VarReal var2) is not implemented");
		return null;
	}
	
	/**
	 * This constraint is a synonym for constraintAllDifferent
	 */
	public Constraint postAllDiff(Var[] vars) {
		return postAllDifferent(vars);
	}
	
	public Constraint postAllDifferent(ArrayList<Var> vars) {
		Var[] array = new Var[vars.size()];
		int i = 0;
		for(Var var : vars)
			array[i++] = var;
		return postAllDifferent(array);
	}
	
	public Constraint postAllDiff(ArrayList<Var> vars) {
		return postAllDifferent(vars);
	}
	
//	public Constraint postAllDifferent(ArrayList<Var> vars) {
//		return postAllDifferent(vars);
//	}
	
	public Constraint postAllDifferent(java.util.List vars) {
		Var[] array = new Var[vars.size()];
		int i = 0;
		for(Object var : vars)
			array[i++] = (Var)var;
		return postAllDifferent(array);
	}
	
	public Constraint allDiff(ArrayList<Var> vars) {
		return postAllDifferent(vars);
	}
	
	/**
	 * Creates and posts a new Constraint stating that all of the elements of
	 * the array of variables "vars" must take different values from each other.
	 * 
	 * @param vars
	 *            the array of Vars which must all take different values.
	 * @return the all-different Constraint on the array of Vars.
	 */
	public Constraint postAllDifferent(Var[] vars) {
		Constraint c = allDiff(vars);
		c.post();
		return c;
	}
	
	/**
	 * Posts allDiff constraint for an array of VarString variables
	 */
	public Constraint postAllDiff(VarString[] varStrings) {
		Var[] vars = new Var[varStrings.length];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = varStrings[i].getInt();
		}
		return postAllDiff(vars);
	}
	
	/**
	 * Creates a constraint that states: 
	 * the integer constrained variable "var" should take at least one value from the "array" of integers 
	 * @param array an array of integer values
	 * @param var an integer constrained variable
	 * @return a constraint
	 */
	public Constraint isOneOfConstraint(int[] array, Var var) {
		Var sum;
		if (array.length == 1)
			sum = linear(var,"=",array[0]).asBool();
		else {
			VarBool[] bools = new VarBool[array.length];
			for (int i = 0; i < array.length; i++) {
				bools[i] = linear(var,"=",array[i]).asBool();
			}
			sum = sum(bools);
		}
		return linear(sum, ">=", 1);
	}
	
	/**
	 * Creates a constraint that states: 
	 * the string constrained variable "var" should take at least one value from the "array" of strings 
	 * @param array an array of String values
	 * @param var an string constrained variable
	 * @return a constraint
	 */
	public Constraint isOneOfConstraint(String[] array, VarString var) {
		Var sum;
		if (array.length == 1)
			sum = linear(var,"=",array[0]).asBool();
		else {
			VarBool[] bools = new VarBool[array.length];
			for (int i = 0; i < array.length; i++) {
				bools[i] = linear(var,"=",array[i]).asBool();
			}
			sum = sum(bools);
		}
		return linear(sum, ">=", 1);
	}
	
	/**
	 * Creates a constraint that states: 
	 * the integer constrained variable "var" should not take any one value from the "array" of integers 
	 * @param array an array of integer values
	 * @param var an integer constrained variable
	 * @return a constraint
	 */
	public Constraint isNotOneOfConstraint(int[] array, Var var) {
		Var sum;
		if (array.length == 1)
			sum = linear(var,"=",array[0]).asBool();
		else {
			VarBool[] bools = new VarBool[array.length];
			for (int i = 0; i < array.length; i++) {
				bools[i] = linear(var,"=",array[i]).asBool();
			}
			sum = sum(bools);
		}
		return linear(sum, "=", 0);
	}
	
	/**
	 * Creates a constraint that states: 
	 * the string constrained variable "var" should not take any value from the "array" of strings 
	 * @param array an array of String values
	 * @param var an string constrained variable
	 * @return a constraint
	 */
	public Constraint isNotOneOfConstraint(String[] array, VarString var) {
		Var sum;
		if (array.length == 1)
			sum = linear(var,"=",array[0]).asBool();
		else {
			VarBool[] bools = new VarBool[array.length];
			for (int i = 0; i < array.length; i++) {
				bools[i] = linear(var,"=",array[i]).asBool();
			}
			sum = sum(bools);
		}
		return linear(sum, "=", 0);
	}
	
	/**
	 * This method takes a constraint's implementation and uses its own 
	 * implementation-specific post-method. 
	 * @throws RuntimeException if a failure happened during the posting
	 */
	abstract public void post(Constraint constraint);

//	/**
//	 * Attempts to posts all constraints added to the problem. The individual
//	 * "post"s are done by concrete implementation and can throw exceptions if a
//	 * failure occurs during posting. If any constraint fails to post the
//	 * process is halted and this method returns <b>false</b>. If all
//	 * constraints are posted successfully this returns <b>true</b>.
//	 * 
//	 * @return <b>true</b> if all constraints are posted successfully;
//	 *         <b>false</b> if at least one constraint failed to be posted.
//	 */
//	public boolean postConstraints() {
//		try {
//			for (int i = 0; i < constraints.size(); i++) {
//				constraints.get(i).post();
//			}
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}


	/**
	 * Returns an "AND" Constraint. The Constraint "AND" is satisfied if all
	 * of the Constraints in the array "array" are satisfied. The Constraint
	 * "AND" is not satisfied if at least one of the Constraints in "array" is
	 * not satisfied.
	 * 
	 * @param array
	 *            the array of constraints forming the new "AND" Constraint.
	 * @return a Constraint "AND" between all the Constraints in the array
	 *         "array".
	 */
	public Constraint and(Constraint[] array) {
		Constraint result = array[0];
		for (int i = 1; i < array.length; i++) {
			result = result.and(array[i]);
		}
		return result;
	}
	
	/**
	 * Creates and posts a constraint that states the implication: if constraint1 then constraint2.
	 * In other words, if this constraint is satisfied, then constraint "c"
	 * should also be satisfied.
	 *
	 * @param constraint1  If-Constraint in the implication.
	 * @param constraint2  Then-Constraint in the implication.
	 * @return a Constraint that means: if constraint1 then constraint2
	 */
	public Constraint postIfThen(Constraint constraint1, Constraint constraint2) {
		Constraint implies = constraint1.implies(constraint2);
		implies.post();
		return implies;
	}
	
	/**
	 * Returns a Constraint that states the implication: 
	 * "if boolean variable var1 is true then boolean variable var2 is also true"
	 *
	 * @param var1 the VarBool
	 * @param var2 the VarBool
	 * @return a Constraint that means "if var1 then var2"
	 */
	public Constraint postIfThen(VarBool var1, VarBool var2) {
		Constraint c1 = linear(var1,"=",1);
		Constraint c2 = linear(var2,"=",1);
		return postIfThen(c1,c2);
	}


//	/**
//	 * Returns a Constraint that is satisfied if and only if the Constraint
//	 * "c" is not satisfied.
//	 * 
//	 * @param c
//	 *            the constraint we desire the opposite of.
//	 * @return a Constraint that is satisfied if and only if the Constraint
//	 *         "c" is not satisfied.
//	 */
//	public Constraint negation(Constraint c) {
//		return ifThen(c, new ConstraintFalse(this));
//	}
	
//=================================================
// Problem resolution methods
//=================================================
	/**
	 * Returns an instance of a Solver associated with this problem. 
	 * If a solver is not defined yet, creates a new Solver and associates it with the problem.
	 * @return a solver
	 */
	public Solver getSolver() {
		if (!isSolverCreated())
			solver = createSolver();
		return solver;
	}
	
	/**
	 * Associates a "solver" with the problem
	 * making it available through the method "getSolver()".
	 * @param solver
	 */
	public void setSolver(Solver solver) {
		this.solver = solver;
	}
	
	public boolean isSolverCreated() {
		return solver != null;
	}
	
	abstract protected Solver createSolver();
		

	/**
	 * Logs the String "text" and then all integer constrained variables added
	 * to the problem.
	 * 
	 * @param text
	 *            the text to log prior to logging all the integer constrained
	 *            variables added to the problem.
	 */
	public void log(String text, Var[] vars) {
		if (text != null)
			log(text);
		log(vars);
	}

	/**
	 * Logs the integer constrained variables contained in the array "vars".
	 * 
	 * @param vars
	 *            the array of Vars to log.
	 */
	public void log(Var[] vars) {
		//log(getId() + " vars#: " + vars.length);
		if (vars != null)
		for (int i = 0; i < vars.length; i++) {
			Var var = vars[i];
			log("Var[" + i + "]: " + var.toString());
		}
	}
	
	public void log(ArrayList<Var> vars) {
		if (vars != null)
		for (int i = 0; i < vars.size(); i++) {
			Var var = vars.get(i);
			log("ArrayList<Var>(" + i + "): " + var.toString());
		}
	}

	/**
	 * Logs the String "text" and then all real constrained variables contained
	 * in the array "vars".
	 * 
	 * @param text
	 *            the text to log prior to logging all the real constrained
	 *            variables added to the problem.
	 */
	public void log(String text, VarReal[] vars){
		if (text != null)
			log(text);
		log(vars);
	}

	/**
	 * Logs the real constrained variables contained in the array "vars".
	 * 
	 * @param vars
	 *            the array of VarReals to log.
	 */
	public void log(VarReal[] vars) {
		//log(getId() + " varReals#: " + vars.length);
		if (vars != null)
		for (int i = 0; i < vars.length; i++) {
			VarReal var = vars[i];
			log("VarReal[" + i + "]: " + var.toString());
		}
	}

	/**
	 * Logs the String "text" and then all constrained set variables contained
	 * in the array "vars".
	 * 
	 * @param text
	 *            the text to log prior to logging all the constrained set
	 *            variables added to the problem.
	 */
	public void log(String text, VarSet[] vars) {
		// TODO
	}

	/**
	 * Logs the constrained set variables contained in the array "vars".
	 * 
	 * @param vars
	 *            the array of VarSets to log.
	 */
	public void log(VarSet[] vars) {
		//TODO
	}

	/**
	 * Logs all Constraints contained in the array "constraints".
	 */
	public void log(Constraint[] constraints) {
		// log(getId() + " constraints#: " + getConstraints().length);
		if (constraints != null)
		for (int i = 0; i < constraints.length; i++) {
			Constraint ct = constraints[i];
			log("Constraint[" + i + "]: " + ct.getName());
		}
	}

	/**
	 * Log the String parameter "text" 
	 */
	abstract public void log(String text);
	
	/**
	 * Log the String parameter "text" 
	 */
	abstract public void debug(String text);

	/**
	 * Log the String parameter "text" 
	 */
	abstract public void error(String text);

	//==================================
	// Global constraints
	//==================================

//	abstract public Constraint allDifferent(Var[] vars);
	
	
	/**
	 * This method creates a new cardinality constraint 
	 *  cardinality(vars,cardVar) 'oper'  value.  
	 * Here cardinality(vars,cardVar) denotes a constrained integer 
	 * variable that is equal to the number of those elements in the 
	 * array "vars" that are bound to the value of the variable "cardVar"
	 * when it is instantiated.  
	 * For example, if oper is "=" it means that the variable 
	 * cardinality(vars,cardValue) must be equal to the  var.  
	 * This constraint does NOT assume a creation of an intermediate 
	 * variable "cardinality(vars,cardValue)".
	 */
	public Constraint postCardinality(Var[] vars, Var cardVar, String oper, Var var) {
		VarBool[] boolVars = new VarBool[vars.length];
		for (int i = 0; i < boolVars.length; i++) {
			boolVars[i] = linear(vars[i],"=",cardVar).asBool();
		}
		return post(boolVars, oper, var);
	}
	
	/**
	 * This method is similar to the one above but instead of var 
	 * it uses "value"
	 */
	public Constraint postCardinality(Var[] vars, Var cardVar, String oper, int value) {
		VarBool[] boolVars = new VarBool[vars.length];
		for (int i = 0; i < boolVars.length; i++) {
			boolVars[i] = linear(vars[i],"=",cardVar).asBool();
		}
		return post(boolVars, oper, value);
	}
	
	public Constraint postCardinality(ArrayList<Var> vars, int cardValue, String oper, int value) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return postCardinality(array, cardValue, oper, value);
	}
	
	public Constraint postCardinality(ArrayList<Var> vars, int cardValue, String oper, Var var) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return postCardinality(array, cardValue, oper, var);
	}
	
	public Constraint postCardinality(ArrayList<Var> vars, Var cardVar, String oper, int value) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return postCardinality(array, cardVar, oper, value);
	}
	
	public Constraint postCardinality(ArrayList<Var> vars, Var cardVar, String oper, Var var) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return postCardinality(array, cardVar, oper, var);
	}
	
	public Constraint postGlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars) {
		Constraint c = new ConstraintGlobalCardinality(vars, values, cardinalityVars);
		c.post();
		return c;
	}
	
	public Constraint postGlobalCardinality(ArrayList<Var> vars, int[] values, Var[] cardinalityVars) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return postGlobalCardinality(array, values, cardinalityVars);
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
	public Constraint postGlobalCardinality(Var[] vars, int[] values, int[] cardMin, int[] cardMax) {
		int min = cardMin[0];
		int max = cardMax[0];
		for(int i = 0; i < cardMin.length; i++){
			if(cardMin[i] > cardMax[i]) {
				throw new RuntimeException("GlobalCardinality error: cardMin["+i+"] <= cardMax["+i+"]");
			}
			if (cardMin[i] < min)
				min = cardMin[i];
			if (cardMax[i] > max)
				max = cardMax[i];
		}
		Var[] cardinalityVars = variableArray(name, min, max, values.length);
		Constraint c = postGlobalCardinality(vars,values,cardinalityVars);
		for (int i = 0; i < cardinalityVars.length; i++) {
			post(cardinalityVars[i],">=",cardMin[i]);
			post(cardinalityVars[i],"<=",cardMax[i]);
		}
		return c;	
	}
	
	public Constraint postGlobalCardinality(ArrayList<Var> vars, int[] values, int[] cardMin, int[] cardMax) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return postGlobalCardinality(array, values, cardMin, cardMax);
	}
	

	
	/**
	 * Creates and posts a constraint: max(vars) "oper" var
	 * where max(vars) is a maximal variable in the array "vars" 
	 * when they are all instantiated.
	 * @param vars
	 * @param oper
	 * @param var
	 */
	public Constraint postMax(Var[] vars, String oper, Var var) {
		Constraint c = new ConstraintMax(vars, oper, var);
		c.post();
		return c;
	}
	
	/**
	 * Creates and posts a constraint: max(vars) "oper" value
	 * where max(vars) is a maximal variable in the array "vars" 
	 * when they are all instantiated.
	 * @param vars
	 * @param oper
	 * @param value
	 */
	public Constraint postMax(Var[] vars, String oper, int value) {
		Constraint c = new ConstraintMax(vars, oper, value);
		c.post();
		return c;
	}
	
	/**
	 * Creates and posts a constraint: min(vars) "oper" var
	 * where max(vars) is a minimal variable in the array "vars" 
	 * when they are all instantiated.
	 * @param vars
	 * @param oper
	 * @param var
	 */
	public Constraint postMin(Var[] vars, String oper, Var var) {
		Constraint c = new ConstraintMin(vars, oper, var);
		c.post();
		return c;
	}
	
	/**
	 * Creates and posts a constraint: min(vars) "oper" value
	 * where max(vars) is a minimal variable in the array "vars" 
	 * when they are all instantiated.
	 * @param vars
	 * @param oper
	 * @param value
	 */
	public Constraint postMin(Var[] vars, String oper, int value) {
		Constraint c = new ConstraintMin(vars, oper, value);
		c.post();
		return c;
	}

	/**
	 * Returns a constrained integer variable that is equal to the minimal
	 * variable in the array "arrayOfVariables" when they are all instantiated.
	 * 
	 * @param vars
	 *            the array of variables from which we desire the minimal
	 *            variable.
	 * @return a constrained integer variable that is equal to the minimal
	 *         variable in the array "arrayOfVariables" when they are all
	 *         instantiated.
	 */
	public Var min(Var[] vars) {
		int m = vars[0].getMin(); // the smallest minimum
		int M = vars[0].getMax(); // the smallest maximum
		for (int i = 1; i < vars.length; i++) {
			Var var = vars[i];
			int mini = var.getMin();
			int maxi = var.getMax();
			if (m > mini)
				m = mini;
			if (M > maxi)
				M = maxi;
		}
		Var minVar = createVariable("_min_", m, M); // not added to teh problem
		Var[] equalities = new Var[vars.length];
		for (int i = 0; i < vars.length; i++) {
			Var var = vars[i];
			post(minVar,"<=",var);
			equalities[i] = linear(minVar,"=",vars[i]).asBool();
		}
		post(equalities, ">=", 1);
		//Var indexVar = variable("", 0, vars.length - 1);
		//result.eq(elementAt(arrayOfVariables, indexVar)).post();
		//postElement(vars, indexVar, "=", minVar);
		return minVar;
	}

	/**
	 * Returns a constrained integer variable that is equal to the maximal
	 * variable in the array "arrayOfVariables" when they are all instantiated.
	 * 
	 * @param vars
	 *            the array of variables from which we desire the maximal
	 *            variable.
	 * @return a constrained integer variable that is equal to the maximal
	 *         variable in the array "arrayOfVariables" when they are all
	 *         instantiated.
	 */
	public Var max(Var[] vars) {
		int m = vars[0].getMin(); // the largest minimum
		int M = vars[0].getMax(); // the largest maximum
		for (int i = 1; i < vars.length; i++) {
			Var var = vars[i];
			int mini = var.getMin();
			int maxi = var.getMax();
			if (m < mini)
				m = mini;
			if (M < maxi)
				M = maxi;
		}
		Var maxVar = createVariable("_max_",m, M);// do not add to teh problem
		Var[] equalities = new Var[vars.length];
		for (int i = 0; i < vars.length; i++) {
			Var var = vars[i];
			post(maxVar,">=",var);
			equalities[i] = linear(maxVar,"=",vars[i]).asBool();
		}
		post(equalities, ">=", 1);
		//Var indexVar = variable("", 0, vars.length - 1);
		//result.eq(elementAt(vars, indexVar)).post();
		//postElement(vars, indexVar, "=", maxVar);
		return maxVar;
	}
	
	public Var max(ArrayList<Var> vars) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return max(array);
	}
	
	public Var min(ArrayList<Var> vars) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return min(array);
	}
	
	/**
	 * @return minimum of two variables: var1 and var2
	 */
	public Var min(Var var1, Var var2) {
		try {
			int minVal = (var1.getMin() < var2.getMin() ? var1.getMin() : var2.getMin());
			int maxVal = (var1.getMax() < var2.getMax() ? var1.getMax() : var2.getMax());

			Var min = variable("_min_", minVal, maxVal);
			remove("_min_"); // necessary to create without adding
			post(min, "<=", var1);
			post(min, "<=", var2);

			Var eq1 = linear(min, "=", var1).asBool();
			Var eq2 = linear(min, "=", var2).asBool();
			post(eq1.plus(eq2), "!=", 0).post();
			return min;
		} catch (Exception e) {
			log("Failure to create min(var1,var2)");
			return null;
		}
	}

	/**
	 * @return maximum of two variables: var1 and var2
	 */
	public Var max(Var var1, Var var2) {
		try {
			int minVal = (var1.getMin() > var2.getMin() ? var1.getMin() : var2.getMin());
			int maxVal = (var1.getMax() > var2.getMax() ? var1.getMax() : var2.getMax());

			Var max = variable("_max_", minVal, maxVal);
			remove("_max_"); // necessary to create without adding

			post(max, ">=", var1);
			post(max, ">=", var2);

			Var eq1 = linear(max, "=", var1).asBool();
			Var eq2 = linear(max, "=", var2).asBool();
			post(eq1.plus(eq2), "!=", 0).post();
			return max;
		} catch (Exception e) {
			log("Failure to create max(var1,var2)");
			return null;
		}
	}

	
	/**
	 * Returns a constrained integer variable that is constrained to be 
	 * the sum of the variables in the array "vars".
	 * @param vars the array of variables from which we desire the sum.
	 * @return a constrained integer variable that is equal to the sum of the variables in the array
	 * 	       "var".
	 */
	public Var sum(Var[] vars) {
		if (vars.length==0) {
			log("Attempt to find a sum of an empty array");
			return variable(0,0);
		}
		int min = 0;
		int max = 0;
		for (int i = 0; i < vars.length; i++) {
			min += vars[i].getMin();
			max += vars[i].getMax();
		}
		AbstractProblem p = (AbstractProblem) vars[0].getProblem();
		Var sumVar = p.variable("_sum_", min, max);
		p.post(vars, "=", sumVar);
		p.remove("_sum_");
		return sumVar;
	}
	
	/**
	 * Returns a constrained integer variable called "name" that is constrained to be 
	 * the sum of the variables in the array "vars".
	 * @param vars the array of variables from which we desire the sum.
	 * @return a constrained integer variable that is equal to the sum of the variables in the array
	 * 	       "var".
	 */
	public Var sum(String name, Var[] vars) {
		if (vars.length==0) {
			log("Attempt to find a sum of an empty array");
			return variable(0,0);
		}
		int min = 0;
		int max = 0;
		for (int i = 0; i < vars.length; i++) {
			min += vars[i].getMin();
			max += vars[i].getMax();
		}
		AbstractProblem p = (AbstractProblem) vars[0].getProblem();
		Var sumVar = p.variable(name, min, max);
		p.post(vars, "=", sumVar);
		return sumVar;
	}
	
	/**
	 * Returns a constrained integer variable that is constrained to be 
	 * the sum of the variables var1 and var2.
	 * @param var1, var2 - variables which we desire to sum
	 * @return a constrained integer variable that is equal to the sum of the variables var1 and var2
	 */
	public Var sum(Var var1, Var var2) {
		return sum(new Var[] { var1, var2 });
	}
	
	/**
	 * Returns a constrained integer variable that is constrained to be 
	 * the sum of the variables var1, var2, and var3.
	 * @param var1, var2, var3 - variables which we desire to sum
	 * @return a constrained integer variable that is equal to the sum of the variables var1, var2, and var3
	 */
	public Var sum(Var var1, Var var2, Var var3) {
		return sum(new Var[] { var1, var2, var3 });
	}
	
	/**
	 * Returns a constrained integer variable that is constrained to be 
	 * the sum of the variables in the ArrayList of Var "vars".
	 * @param vars the list of variables from which we desire the sum.
	 * @return a constrained integer variable that is equal to the sum of the variables in the array
	 * 	       "var".
	 */
	public Var sum(ArrayList<Var> vars){
		if (vars.size()==0) {
			log("Attempt to find a sum of an empty array");
			return variable(0,0);
		}
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return sum(array);
//		int min = 0;
//		int max = 0;
//		for (int i = 0; i < vars.size(); i++) {
//			min += vars.get(i).getMin();
//			max += vars.get(i).getMax();
//		}
//		AbstractProblem p = (AbstractProblem) vars.get(0).getProblem();
//		Var sumVar = p.variable("_sum_", min, max);
//		p.remove("_sum_");
//		p.post(vars, "=", sumVar);
//		return sumVar;
	}
	
	/**
	 * Returns a constrained real variable that is constrained to be 
	 * the sum of the variables in the array "vars".
	 * @param vars the array of variables from which we desire the sum.
	 * @return a constrained integer variable that is equal to the sum of the variables in the array
	 * 	       "var".
	 */
	public VarReal sum(VarReal[] vars) {
		double min = 0;
		double max = 0;
		for (int i = 0; i < vars.length; i++) {
			min += vars[i].getMin();
			max += vars[i].getMax();
		}
		AbstractProblem p = (AbstractProblem) vars[0].getProblem();
		VarReal sumVar = p.variableReal("_sum_", min, max);
		p.remove("_sum_");
		p.post(vars, "=", sumVar); 
		return sumVar;
	}
	
	/**
	 * Returns a constrained real variable that is constrained to be 
	 * the sum of the variables var1 and var2.
	 * @param var1, var2 - variables which we desire to sum
	 * @return a constrained integer variable that is equal to the sum of the variables var1 and var2
	 */
	public VarReal sum(VarReal var1, VarReal var2) {
		return sum(new VarReal[] { var1, var2 });
	}
	
	/**
	 * Returns a constrained real variable that is constrained to be 
	 * the sum of the variables var1, var2, and var3.
	 * @param var1, var2, var3 - variables which we desire to sum
	 * @return a constrained integer variable that is equal to the sum of the variables var1, var2, and var3
	 */
	public VarReal sum(VarReal var1, VarReal var2, VarReal var3) {
		return sum(new VarReal[] { var1, var2, var3 });
	}
	
//	/**
//	 * Returns a constrained variable equal to the scalar product of an array of values "arrayOfValues"
//	 *         and an array of variables "arrayOfVariables".
//	 * @param arrayOfValues the array of values.
//	 * @param arrayOfVariables the array of variables.
//	 * @return a constrained variable equal to the scalar product of an array of values "arrayOfValues"
//	 *         and an array of variables "arrayOfVariables".
//	 */
//	abstract public Var scalProd(int[] arrayOfValues, Var[] arrayOfVariables);
	
	/**
	 * Sets the precision for VarReals
	 * 
	 * @param value
	 *            the new precision for VarReals
	 */
	public void setRealPrecision(double value) {
		REAL_PRECISION = value;
	}

	/**
	 * Returns the precision for VarReals
	 * 
	 * @return the precision for VarReals
	 */
	public double getRealPrecision() {
		return REAL_PRECISION;
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
	public Reversible addReversible(String name, int value) {
		throw new RuntimeException("This RI does not support Reversible integers");
	}
	
	/**
	 * Creates and posts a constraint: arrayOfSets[indexVar] 'oper' setVar 
	 * Here "arrayOfSets[indexVar]" denotes a constrained set variable, which domain
	 * consists of sets of integers arrayOfSets[i] where i is within domain of the "indexVar".
	 * When indexVar is bound to the value v, the value of the resulting variable is
	 * arrayOfSets[v]. The operator "oper" defines the type of relationships between  
	 * "arrayOfSets[indexVar]" and "setVar"
	 *
	 * @param arrayOfSets an array of integer sets.
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the "arrayOfSets".
	 * @throws RuntimeException if the posting fails 
	 * @return a newly created constraint
	 */
	public Constraint postElement(Set[] arrayOfSets, Var indexVar, String oper, VarSet var) {
		notImplementedException("Constraint postElement is not implemented on Sets");
		return null;
	}
	
	/**
	 * Returns the constant constraint that always will fail when it is posted or executed.
	 * @return the False Constraint 
	 */
	public Constraint getFalseConstraint() {
		return new ConstraintFalse(this);
	}
	
	/**
	 * Returns the constant constraint that always succeeds when it is posted or executed.
	 * @return the True Constraint 
	 */
	public Constraint getTrueConstraint() {
		return new ConstraintTrue(this);
	}
	
	public void notImplementedException(String feature) {
		String msg = feature + " is not implemented by " + getImplVersion();
		log(msg);
		throw new RuntimeException(msg);
	}
	
	
	public void add(VarString var) {
		varStrings.add(var);
	}

	public VarString[] getVarStrings() {
		if (varStrings.isEmpty())
			return null;
		VarString[] array = new VarString[varStrings.size()];
		Iterator<VarString> iterator = varStrings.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			array[i++] = iterator.next();
		}
		return array;
	}

	public VarString getVarString(String name) {
		if (varStrings.isEmpty())
			return null;
		Iterator<VarString> iterator = varStrings.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			VarString var = iterator.next();
			if (name.equals(var.getName()))
				return var;
		}
		return null;
	}

	public VarString variableString(String name, String[] allStrings) {
		VarString varString = new BasicVarString(this, name, allStrings);
		add(varString);
		return varString;
	}

	public Constraint post(VarString var, String oper, String value) {
		int index = var.getIndex(value);
		if (index < 0)
			throw new RuntimeException("ERROR: " + var.getName() + " " + oper + " " + value +
					": value " + value + " is expected to be inside variable domain " + var.getInitialDomain());
		return post(var.getInt(), oper, index);
	}

	public Constraint linear(VarString var, String oper, String value) {
		int index = var.getIndex(value);
		if (index < 0)
			throw new RuntimeException("ERROR: " + var.getName() + " " + oper + " " + value +
					": value " + value + " is expected to be inside variable domain " + var.getInitialDomain());
		return linear(var.getInt(), oper, index);
	}
	
	public Constraint linear(VarString var1, String oper, VarString var2) {
		return linear(var1.getInt(), oper, var2.getInt());
	}

	public Constraint post(VarString var1, String oper, VarString var2) {
		// if (!var1.hasSameDomain(var2))
		// throw new RuntimeException("Constraint " + getName() +
		// ".eq(" + var1.getName() + ") works only with the same domains");
		return post(var1.getInt(), oper, var2.getInt());
	}
	
	public Constraint post(ArrayList<Var> vars, String oper, int value) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return post(array,oper,value);
	}
	
	public Constraint post(ArrayList<Var> vars, String oper, Var var) {
		Var[] array = (Var[])vars.toArray(new Var[vars.size()]);
		return post(array,oper,var);
	}
	
	/**
	 * Creates and posts a constraint: var "oper" scalProd(arrayOfValues,arrayOfVariables)
	 * @param var the constraint variable
	 * @param arrayOfValues the array of values.
	 * @param arrayOfVariables the array of variables.
	 * @return a constraint 
	 */
	public Constraint postScalProd(Var var, String oper, int[] arrayOfValues, Var[] arrayOfVariables) {
		return post(var,oper,scalProd(arrayOfValues,arrayOfVariables));
	}
	
	public Var scalProd(int[] arrayOfValues, ArrayList<Var> vars) {
		Var[] arrayOfVariables = (Var[])vars.toArray(new Var[vars.size()]);
		return scalProd(arrayOfValues, arrayOfVariables);
	}
	
	public Var scalProd(String name, int[] arrayOfValues, Var[] arrayOfVariables) {
		Var scalProd = scalProd(arrayOfValues, arrayOfVariables);
		add(name,scalProd);
		return scalProd;
	}
	
	/**
	 * Returns a real constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 * @param arrayOfValues the array of values.
	 * @param arrayOfVariables the array of variables.
	 * @return a constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 */
	public VarReal scalProd(double[] arrayOfValues, VarReal[] arrayOfVariables) {
		error("Problem's method scalProd(double[] arrayOfValues, VarReal[] arrayOfVariables) is not implemented");
		return null;
	}
	
	public VarReal scalProd(double[] arrayOfValues, ConstrainedVariable[] arrayOfVariables) {
		error("Problem's method scalProd(double[] arrayOfValues, ConstrainedVariable[] arrayOfVariables) is not implemented");
		return null;
	}
	
//	public ArrayList<Var> variableList() {
//		return new ArrayList<Var>();
//	}
	
	public VarMatrix variableMatrix(String name, int min, int max, int rows,
			int columns) {
		VarMatrix matrix = new BasicVarMatrix(this, name, min, max, rows, columns);
		varMatrixs.put(name, matrix);
		return matrix;
	}

	public VarMatrix getVarMatrix(String name) {
		return varMatrixs.get(name);
	}
	
	public void addConstraintWithProbability(String name, Constraint c, Probability probability) {
		int weight = probability.getValue();
		Var constraintVar = c.asBool();
		Var violation = variableBool(name);
		Constraint zero = linear(constraintVar,"=",0);
		Constraint one = linear(constraintVar,"=",1);
		Constraint violationZero = linear(violation,"=",0);
		Constraint violationOne = linear(violation,"=",1);
		postIfThen(zero,violationOne);
		postIfThen(one,violationZero);
		constraintViolations.add(violation.multiply(weight));
	}
	
	/*
	 * Returns a Var that is equal to the sum of all weighted violations of all
	 * constraints posted with some Probability. Returns null if there are no such constraints. 
	 */
	public Var getTotalConstraintViolation() {
		if (!areThereProbabilityConstraints()) {
			log("There are no constraint postings with probabilities");
			return variable("stub for TotalConstraintViolation",0,0);
		}
		Var sum = sum(constraintViolations);
		sum.setName("TotalConstraintViolation");
		return sum;
	}
	
	/*
	 * @returns true if there are constraints posted with a probability between VER_LOW and VERY_HIGH
	 * @returns false otherwise
	 */
	public boolean areThereProbabilityConstraints() {
		return constraintViolations.size() > 0; 
	}
	
}
