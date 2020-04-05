//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  331
// 
// Specification
// 
//=============================================
package javax.constraints;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.constraints.impl.BasicVarMatrix;
import javax.constraints.impl.BasicVarString;

/**
 * A constraint satisfaction problem Problem can be defined in terms of constrained 
 * variables and constraints. The standard will cover the following types of Constrained 
 * Variables:
 * <ul>
 * <li>Var - Integer Variables
 * <li>VarBool - Boolean Variables
 * <li>VarReal - Real (floating point) Variables
 * <li>VarSet - Set Variables.
 * </ul>
 * <p>The standard will cover the following  Constraints:
 * <ul>
 * <li> Basic arithmetic and logical constraints
 * <li> Global constraints (defined on collections of variables)
 * <li> Logical combinations of constraints.
 * </ul>
 * 
 * <p>Problem Definition is completely independent of the Problem Resolution (class Solver).
 * </p> 
 * 
 * @see ConstrainedVariable
 * @see Var
 * @see Constraint
 * @see Solver
 */

public interface Problem {

	static final String JSR331_VERSION = "JSR-331 \"Constraint Programming API\" Release 2.0.1";
	
	/**
	 * Returns JSR331 API version number
	 * @return JSR331 API version number
	 */
	public String getAPIVersion();
	
	/**
	 * Returns JSR331 Implementation version number
	 * @return JSR331 Implementation version number
	 */
	public String getImplVersion();

	/**
	 * Returns the problem name
	 * @return the problem name
	 */
	public String getName();

	/**
	 * Sets the problem name
	 * @param name
	 */
	public void setName(String name);

	/**
	 * Adds a Var variable "var" to the problem, and returns the newly added Var.
	 *
	 * @param var the variable to add to the problem.
	 * @return the Var variable added to the problem.
	 */
	public Var add(Var var);
	
	/**
	 * Adds a VarBool variable "var" to the problem, and returns the newly added VarBool.
	 *
	 * @param var the booolean constrained variable to add to the problem.
	 * @return the VarBool variable added to the problem.
	 */
	public VarBool add(VarBool var);
	
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
	public Var add(String name, Var var);

//	/**
//	 * Removes a Var variable by "name" from arrays getVars(), getVarReals(), and getVarSets().
//	 *
//	 * @param varName the name of the variable to be removed from the problem.
//	 */
//	public void remove(String varName);

	/**
	 * Adds a VarReal variable "var" to the problem, and returns the newly added VarReal.
	 *
	 * @param var the real variable to add to the problem.
	 * @return the VarReal variable added to the problem.
	 */
	public VarReal add(VarReal var);

//	/**
//	 * Creates a Var variable based on the symbolicExpression such as "x*y-z", adds this
//	 * variable to the problem, and returns the newly added Var.
//	 *
//	 * If the returned Var is null, there is either an error inside the symbolicExpression
//	 * or there is no concrete solver implementation of this method. In either case, the
//	 * constraint will not have been added to the problem.
//	 *
//	 * @param name the name for a new var  
//	 * @param symbolicExpression
//	 *            a string that defines a constrained expression that uses
//	 *            already defined variables, e.g. "x+2*y"
//	 * @return the Var variable created and added to the problem, or null if there is an error inside the
//	 *         symbolicExpression or if this method is not defined by a concrete
//	 *         solver implementation.
//	 */
//	public Var var(String name, String symbolicExpression);
	
	/**
	 * Creates a Constraint based on the symbolicExpression such as "x*y-z = 3*r", 
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
	 *            already defined variables, e.g. "x*y-z = 3*r"
	 * @return the constraint created and added to the problem, or null if there is an error inside the
	 *         symbolicExpression or if this method is not defined by a concrete
	 *         solver implementation.
	 */
	public Constraint post(String name, String symbolicExpression);
	
	/**
	 * Sets the domain type that will be used as the default for subsequent creation
	 * of variables using addVar-methods 
	 * @param type
	 */
	public void setDomainType(DomainType type);
	
//	/**
//	 * @return a domain type for Var
//	 */
//	public DomainType getDomainType();

	/**
	 * Creates a Var with the name "name" and domain [min;max] of the default domain type, 
	 * and returns the newly added Var.
	 *
	 * The variable will be created with the default domain type or 
	 * by the latest call of the method setDomainType().
	 *
	 * @param name the name for the new Var.
	 * @param min the minimum value in the domain for the new Var.
	 * @param max the maximum value in the domain for the new Var.
	 * @return the Var variable created and added to the problem.
	 */
	Var createVariable(String name, int min, int max);
	
	/**
	 * Creates a VarReal with the name "name" and domain [min;max] of the default domain type, 
	 * and returns the newly added VarReal
	 *
	 * @param name the name for the new VarReal.
	 * @param min the minimum value in the domain for the new VarReal.
	 * @param max the maximum value in the domain for the new VarReal.
	 * @return the VarReal variable created and added to the problem.
	 */
	VarReal createVariableReal(String name, double min, double max);
	
	/**
	 * Creates a Var by calling createVariable(name,min,max) and 
	 * adds this variable to the problem.
	 *
	 * The variable will be created with the default domain type or 
	 * by the latest call of the method setDomainType().
	 *
	 * @param name the name for the new Var.
	 * @param min the minimum value in the domain for the new Var.
	 * @param max the maximum value in the domain for the new Var.
	 * @return the Var variable created and added to the problem.
	 */
	public Var variable(String name, int min, int max);
	
	/**
	 * Similar to variable(String name, int min, int max) but also has one more parameter 
	 * that defines the default domain type. .
	 *
	 * @param name the name for the new Var.
	 * @param min the minimum value in the domain for the new Var.
	 * @param max the maximum value in the domain for the new Var.
	 * @param domainType {@link DomainType}
	 * @return the Var variable created and added to the problem.
	 */
	public Var variable(String name, int min, int max, DomainType domainType);
	
	/**
	 * Creates a Var with domain [min;max]. The variable will be created 
	 * with the default domain type or by the latest call of the method setDomainType(). 
	 * This variable has an empty name and is not added to the problem.
	 * 
	 * @param min the minimum value in the domain for the new Var.
	 * @param max the maximum value in the domain for the new Var.
	 * @return the Var created variable
	 */
	public Var variable(int min, int max);
	
	/**
	 * Creates a boolean constrained variable with the name "name" and adds
	 * this variable to the problem, and returns the newly added VarBool.
	 * @param name the name for the new Var.
	 * @return the Var variable created and added to the problem.
	 */
	public VarBool variableBool(String name);
	
	/**
	 * Creates a boolean constrained variable without name.
	 * This variable is not added to the problem,
	 * @return the Var variable created
	 */
	public VarBool variableBool();
	
//	/**
//	 * Creates a Reversible integer with the name "name" and value "value"
//	 * and returns the newly added Reversible.
//	 *
//	 * @param name the name for the new reversible.
//	 *
//	 * @param value the initial value
//	 *
//	 * @return the reversible integer
//	 */
//	public Reversible addReversible(String name, int value);


	/**
	 * Sets the precision for VarReals
	 * @param value the new precision for VarReals
	 */
	public void setRealPrecision(double value);

	/**
	 * Returns the precision for VarReals
	 * @return the precision for VarReals
	 */
	public double getRealPrecision();

	/**
	 * Creates a VarReal with the name "name" and domain [min;max], adds
	 * this variable to the problem, and returns the newly added VarReal.
	 *
	 * @param name the name for the new VarReal.
	 *
	 * @param min the minimum value in the domain for the new VarReal.
	 *
	 * @param max the maximum value in the domain for the new VarReal.
	 *
	 * @return the VarReal variable created and added to the problem.
	 */
	public VarReal variableReal(String name, double min, double max);

	/**
	 * Creates a VarReal with the name "name" and default domain [Problem.REAL_MIN;Problem.REAL_MAX], adds
	 * this variable to the problem, and returns the newly added VarReal.
	 *
	 * @param name the name for the new VarReal.
	 *
	 * @return the VarReal variable created and added to the problem.
	 */
	public VarReal variableReal(String name);

//	/**
//	 * Creates a BasicVarString with the name "name" and domain "values", adds
//	 * this variable to the problem and returns the newly added BasicVarString.
//	 *
//	 * @param name the name for the new Var.
//	 * @param values an array of string values defining the domain of the new BasicVarString.
//	 * @return the BasicVarString variable created and added to the problem.
//	 */
//	 BasicVarString addVarString(String name, String[] values);

	/**
	 * Creates a constrained set variable VarSet with the name "name"
	 * and domain of integer values from "min" to "max" inclusive.
	 *
	 * This interface show how to create a new constrained set variable
	 * which, when bound, is equal to a set of elements. The domain
	 * of a constrained set variable is a set of Sets that consist of
	 * regular integers.The cardinality of a set constrained variable is an integer
	 * constrained variable.
	 *
	 * @param name
	 * @param min
	 * @param max
	 * @return a new VarSet variable
	 */
	public VarSet variableSet(String name, int min, int max) throws Exception;

	/**
	 * Creates a constrained set variable VarSet with the name "name"
	 * and which domain consists of integer values from the array "value".
	 * @param name
	 * @param values
	 * @return the VarSet variable created and added to the problem.
	 */
	public VarSet variableSet(String name, int[] values) throws Exception;
	
	/**
	 * Creates a constrained set variable VarSet with the name "name" and which
	 * domain consists of integer values from the Set "set".
	 * 
	 * @param name
	 * @return the VarSet variable created and added to the problem.
	 */
	public VarSet variableSet(String name, Set set) throws Exception;

	/**
	 * Creates an array of Var variables with the name "name" and domains [min;max], adds
	 * this array of variables to the problem, and returns the newly added Var array. The "size" parameter specifies
	 * how many Var variables are contained in this new array.
	 *
	 * The variables will be created with the default domain type or as defined 
	 * by the latest call of the method setDomainType().
	 * @param name the name for the new Var array.
	 * @param min the minimum value in the domains for the new Vars in the new Var array.
	 * @param max the maximum value in the domain for the new Vars in the new Var array.
	 * @param size the number of Vars in the new Var array.
	 *
	 * @return the array of Var variables created and added to the problem.
	 */
	public Var[] variableArray(String name, int min, int max, int size);
	
//	public ArrayList<Var> variableList();
	

//	/**
//	 * Creates an array of Var variables with name "arrayName". The variables to be contained in the array must
//	 * have already been added to the problem, and their names are specified in "varNames".
//	 * @param arrayName the name for the new Var array.
//	 * @param varNames an array of names of already added variables, which are to form the new array.
//	 *
//	 * @return the array of Var variables created and added to the problem.
//	 */
//	public Var[] addVarArray(String arrayName, String[] varNames);

	/**
	 * Creates a Var with the name "name" and domain containing the values in the array "domain"
	 * of the default domain type, adds this variable to the problem, and returns the newly added Var.
	 *
	 * The variable will be created with the default domain type or 
	 * by the latest call of the method setDomainType().
	 * @param name the name for the new Var.
	 * @param domain an array specifying the values in the domain for the new Var.
	 * @return the Var variable created and added to the problem.
	 */
	public Var variable(String name, int[] domain);
	
	/**
	 * An equivalent of var("",domain)
	 * @param domain an array specifying the values in the domain for the new Var.
	 * @return the Var variable created and added to the problem.
	 */
	public Var variable(int[] domain);

//	/**
//	 * Creates a new Var variable "name" with domain [Integer.MIN_VALUE+1;Integer.MAX_VALUE-1]
//	 * of default domain type, adds this variable to the problem and returns the newly added Var.
//	 *
//	 * @param name the name for a new var
//	 * @return the Var variable created and added to the problem.
//	 */
//	public Var addVar(String name);

	/**
	 * Returns an array containing the Var variables previously added to the problem.
	 * @return an array containing the Var variables previously added to the problem.
	 */
	public Var[] getVars();
	
	/**
	 * Returns an array containing the VarBool variables previously added to the problem.
	 * @return an array containing the VarBool variables previously added to the problem.
	 */
	public VarBool[] getVarBools();

	/**
	 * Returns an array containing the VarReal variables previously added to the problem.
	 * @return an array containing the VarReal variables previously added to the problem.
	 */
	public VarReal[] getVarReals();
	
	/**
	 * Returns an array containing the VarSet variables previously added to the problem.
	 * @return an array containing the VarSet variables previously added to the problem.
	 */
	public VarReal[] getVarSets();

	/**
	 * Returns the Var variable with the name "name", or null if no such variable exists in the problem.
	 * @param name the name of the desired variable.
	 * @return the Var variable with the name "name", or null if no such variable exists in the problem.
	 */
	public Var getVar(String name);
	
	/**
	 * Returns the Constraint the name "name", or null if no such constraints exists in the problem.
	 * @param name the name of the desired constraint.
	 * @return the Constraint the name "name", or null if no such constraints exists in the problem.
	 */
	public Constraint getConstraint(String name);
	
	/**
	 * Returns the constant constraint that always will fail when it is posted or executed.
	 * @return the False Constraint 
	 */
	public Constraint getFalseConstraint();
	
	/**
	 * Returns the constant constraint that always succeeds when it is posted or executed.
	 * @return the True Constraint 
	 */
	public Constraint getTrueConstraint();

	/**
	 * Returns the VarReal variable with the name "name", or null if no such variable exists in the problem.
	 * @param name the name of the desired variable.
	 * @return the VarReal variable with the name "name", or null if no such variable exists in the problem.
	 */
	public VarReal getVarReal(String name);

	/**
	 * Returns the array of Var variables with the name "name", or null if no such array exists in the problem.
	 * @param name the name of the desired array of Var variables.
	 * @return the array of Var variables with the name "name", or null if no such array exists in the problem.
	 */
	public Var[] getVarArray(String name);

//	/**
//	 * Creates a 2-dimensional array of Var variables, with dimensions "size"x"size". The array has
//	 * name "name" and each Var in the array has domain[min;max] of default domain type. The array is added
//	 * to the problem, and then the array is returned.
//	 *
//	 * If "name" is a non-null string "X" then
//	 * each element [i,j] can be found by using Problem method
//	 * getInt("X[i,j]").
//	 *
//	 * The default domain type is Constant.DOMAIN_SMALL, but the chosen implementation may
//	 * override this default with its own default.
//	 * @param min the minimum value in the domains for the new Vars in the new Var array.
//	 * @param max the maximum value in the domain for the new Vars in the new Var array.
//	 * @param size the dimensions of the new 2-dimensional array. The array contains size*size elements.
//	 *
//	 * @return the array of Var variables created and added to the problem.
//	 */
//	public Var[] addVarSquare(String name, int min, int max, int size);

	/**
	 * Adds a Constraint "constraint" to the problem, then returns the constraint.
	 * The Constraint is not posted.
	 *
	 * @param constraint the constraint to add to the problem.
	 * @return the Constraint added to the problem.
	 */
	public Constraint add(Constraint constraint);

//	/**
//	 * Gives a Constraint "constraint" a new name "name", adds the Constraint to the problem
//	 * and returns the newly added Constraint. The Constraint is not posted.
//	 * @param name the new name for the Constraint.
//	 * @param constraint the Constraint to add to the problem
//	 *
//	 * @return the Constraint added to the problem.
//	 */
//	public Constraint add(String name, Constraint constraint);

	/**
	 * Returns an array of Constraints previously added to the problem.
	 * @return an array of Constraints previously added to the problem.
	 */
	public Constraint[] getConstraints();

//	/**
//	 * Returns a Constraint that states the implication: if c1 then
//	 * c2 for two Constraints c1 and c2. In other words, if c1 is satisfied, then c2
//	 * should also be satisfied.
//	 *
//	 * @param c1 the first Constraint in the implication.
//	 * @param c2 the second Constraint in the implication.
//	 *
//	 * @return a Constraint that means c1 => c2 (if c1 then c2).
//	 *
//	 */
//	public Constraint ifThen(Constraint c1, Constraint c2);

	/**
	 * Creates and posts a new constraint: array[indexVar] 'oper' value 
	 * Here "array[indexVar]" denotes a constrained integer variable, which domain
	 * consists of integers array[i] where i is within domain of the "indexVar".
	 * When indexVar is bound to the value v, the value of the resulting variable is
	 * array[v]. The operator "oper" defines the type of relationships between  
	 * "array[indexVar]" and "value"
	 *
	 * @param array an array of integers.
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the "array".
	 * @throws RuntimeException if the posting fails 
	 * @return a newly created constraint
	 */
	public Constraint postElement(int[] array, Var indexVar, String oper, int value);
	
	/**
	 * Creates and posts a constraint: array[indexVar] 'oper' var 
	 * Here "array[indexVar]" denotes a constrained integer variable, which domain
	 * consists of integers array[i] where i is within domain of the "indexVar".
	 * When indexVar is bound to the value v, the value of the resulting variable is
	 * array[v]. The operator "oper" defines the type of relationships between  
	 * "array[indexVar]" and "var"
	 *
	 * @param array an array of integers.
	 * @param indexVar a constrained integer variable whose domain serves as 
	 * an index into the "array".
	 * @throws RuntimeException if the posting fails 
	 * @return a newly created constraint
	 */
	public Constraint postElement(int[] array, Var indexVar, String oper, Var var);
	
	/**
	 * Posts a constraint: vars[indexVar] 'oper' value 
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint postElement(Var[] vars, Var indexVar, String oper, int value);
	
	/**
	 * Posts a new constraint: vars[indexVar] 'oper' var 
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint postElement(Var[] vars, Var indexVar, String oper, Var var);
	
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
	public Constraint postElement(Set[] arrayOfSets, Var indexVar, String oper, VarSet var);
	
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(int[] array, Var[] vars, String oper, int value);
	
	@Deprecated
	public Constraint post(int[] array, ArrayList<Var> vars, String oper, int value);
	
//	/**
//	 * Creates and posts a constraint: array*vars 'oper' value
//	 * for an "array: of real coefficients to be multiplied by an array "vars"
//	 * of integer constrained variables 
//	 * @throws RuntimeException if the posting fails
//	 * @return a newly created constraint
//	 */
//	public Constraint post(double[] coefficients, Var[] vars, String oper, double value);
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(int[] array, Var[] vars, String oper, Var var);
	
	/**
	 * Creates and posts a constraint: sum of vars 'oper' value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(Var[] vars, String oper, int value);
	
	/**
	 * Creates and posts a constraint: sum of vars 'oper' value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(ArrayList<Var> vars, String oper, int value);
	
	/**
	 * Creates and posts a constraint: sum of vars "oper" var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(Var[] vars, String oper, Var var);
	
	/**
	 * Creates and posts a constraint: sum of vars "oper" var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(ArrayList<Var> vars, String oper, Var var);
	
	/**
	 * Creates and posts a constraint: (var1 + var2) "oper" value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(Var var1, Var var2, String oper, int value);
	
	/**
	 * Creates and posts a constraint: (var1 + var2) "oper" var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(Var var1, Var var2, String oper, Var var);
	
	/**
	 * Creates and posts a constraint: var "oper" value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(Var var, String oper, int value);
	
	/**
	 * Creates and posts a constraint: var1 "oper" var2
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(Var var1, String oper, Var var2);

	/**
	 * Creates a constraint: var "oper" value without posting
	 * @return a newly created constraint
	 */
	public Constraint linear(Var var, String oper, int value);
	
	/**
	 * Creates a constraint: var1 "oper" var2 without posting
	 * @return a newly created constraint
	 */
	public Constraint linear(Var var1, String oper, Var var2);
	
	/**
	 * Creates and posts a constraint: var "oper" integer value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(VarReal var, String oper, int value);
	
	/**
	 * Creates and posts a constraint: var "oper" double value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(VarReal var, String oper, double value);
	
	/**
	 * Creates and posts a constraint: var1 "oper" var2
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(VarReal var1, String oper, VarReal var2);
	
	/**
	 * Creates and posts a constraint: sum of vars "oper" double value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(VarReal[] vars, String oper, double value);
	
	/**
	 * Creates and posts a constraint: sum of vars "oper" var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(VarReal[] vars, String oper, VarReal var);
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(double[] array, VarReal[] vars, String oper, VarReal var);
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(double[] array, VarReal[] vars, String oper, double value);
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' var
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated 
	public Constraint post(double[] array, ConstrainedVariable[] vars, String oper, ConstrainedVariable var);
	
	/**
	 * Creates and posts a constraint: array*vars 'oper' value
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	@Deprecated
	public Constraint post(double[] array, ConstrainedVariable[] vars, String oper, double value);
	
	/**
	 * Creates and posts a constraint: var1 "oper" var2
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(VarReal var1, String oper, Var var2);
	
	/**
	 * Creates and posts a constraint: var1 "oper" var2
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint post(Var var1, String oper, VarReal var2);
	
	
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
	public Var element(int[] array, Var indexVar);
	
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
	public Var element(Var[] array, Var indexVar);
	
	public Var element(ArrayList<Var> list, Var indexVar);
	
	/**
	 * Creates a set variable that corresponds to sets[index]
	 * @param sets an array of Set objects
	 * @param indexVar a constrained integer variable
	 * @return a set variable that corresponds to sets[index]
	 * @throws Exception
	 */
	public VarSet element(Set<Integer>[] sets, Var indexVar) throws Exception;
	

//	/**
//	 * This method is defined for Set[] similarly to the "elementAt" for int[].
//	 * It returns a constrained set variable that is an element of the array of sets
//	 * with index defined as a constrained integer variable "indexVar". When
//	 * index is bound to the value i, the value of the resulting variable is a constaint set
//	 * sets[i].
//	 *
//	 * @param sets - an array of the regular Java Set(s)
//	 * @param indexVar - a constrained integer variable whose domain serves
//	 * as an index into the array of sets.
//	 * @return a constrained integer variable
//	 */
//	public VarSet elementAt(Set[] sets, Var indexVar) throws Exception;

	/**
	 * Returns a constrained integer variable that is equal to the minimal variable in the array
	 *         "arrayOfVariables" when they are all instantiated.
	 * @param arrayOfVariables the array of variables from which we desire the minimal variable.
	 * @return a constrained integer variable that is equal to the minimal variable in the array
	 * 	       "arrayOfVariables" when they are all instantiated.
	 */
	public Var min(Var[] arrayOfVariables);
	
	public Var min(ArrayList<Var> listOfVariables);
	
	/**
	 * Returns a constrained integer variable that is equal to the minimal of two variablea
	 * var1 or var 2 when they are both instantiated.
	 * @param var1 integer variable
	 * @param var2 integer variable
	 * @return a constrained integer variable that is equal to the minimal of two variablea
	 * var1 or var 2 when they are both instantiated.
	 */
	public Var min(Var var1,Var var2);

	/**
	 * Returns a constrained integer variable that is equal to the maximal variable in the array
	 *         "arrayOfVariables" when they are all instantiated.
	 * @param arrayOfVariables the array of variables from which we desire the maximal variable.
	 * @return a constrained integer variable that is equal to the maximal variable in the array
	 * 	       "arrayOfVariables" when they are all instantiated.
	 */
	public Var max(Var[] arrayOfVariables);
	
	public Var max(ArrayList<Var> listOfVariables);
	
	/**
	 * Returns a constrained integer variable that is equal to the maximal of two variablea
	 * var1 or var 2 when they are both instantiated.
	 * @param var1 integer variable
	 * @param var2 integer variable
	 * @return a constrained integer variable that is equal to the maximal of two variablea
	 * var1 or var 2 when they are both instantiated.
	 */
	public Var max(Var var1,Var var2);

	/**
	 * Returns a constrained integer variable that is constrained to be 
	 * the sum of the variables in the array "vars".
	 * @param vars the array of variables from which we desire the sum.
	 * @return a constrained integer variable that is equal to the sum of the variables in the array
	 * 	       "var".
	 */
	public Var sum(Var[] vars);
	
	/**
	 * Returns a constrained integer variable called "name" that is constrained to be 
	 * the sum of the variables in the array "vars".
	 * @param vars the array of variables from which we desire the sum.
	 * @return a constrained integer variable that is equal to the sum of the variables in the array
	 * 	       "var".
	 */
	public Var sum(String name, Var[] vars);
	
	/**
	 * Returns a constrained integer variable that is constrained to be 
	 * the sum of the variables in the ArrayList of Var "vars".
	 * @param vars the list of variables from which we desire the sum.
	 * @return a constrained integer variable that is equal to the sum of the variables in the array
	 * 	       "var".
	 */
	public Var sum(ArrayList<Var> vars);
	
	/**
	 * Returns a constrained integer variable that is constrained to be 
	 * the sum of the variables var1 and var2.
	 * @param var1, var2 - variables which we desire to sum
	 * @return a constrained integer variable that is equal to the sum of the variables var1 and var2
	 */
	public Var sum(Var var1, Var var2);
	
	/**
	 * Returns a constrained integer variable that is constrained to be 
	 * the sum of the variables var1, var2, and var3.
	 * @param var1, var2, var3 - variables which we desire to sum
	 * @return a constrained integer variable that is equal to the sum of the variables var1, var2, and var3
	 */
	public Var sum(Var var1, Var var2, Var var3);
	
	/**
	 * Returns a constrained integer variable that is constrained to be 
	 * the sum of the variables in the array "vars".
	 * @param vars the array of variables from which we desire the sum.
	 * @return a constrained integer variable that is equal to the sum of the variables in the array
	 * 	       "var".
	 */
	public VarReal sum(VarReal[] vars);
	
	/**
	 * Returns a constrained real variable that is constrained to be 
	 * the sum of the variables var1 and var2.
	 * @param var1, var2 - variables which we desire to sum
	 * @return a constrained integer variable that is equal to the sum of the variables var1 and var2
	 */
	public VarReal sum(VarReal var1, VarReal var2);
	
	/**
	 * Returns a constrained real variable that is constrained to be 
	 * the sum of the variables var1, var2, and var3.
	 * @param var1, var2, var3 - variables which we desire to sum
	 * @return a constrained integer variable that is equal to the sum of the variables var1, var2, and var3
	 */
	public VarReal sum(VarReal var1, VarReal var2, VarReal var3);

	/**
	 * Returns an integer constrained variable called "name" 
	 * equal to the scalar product of an array of values "arrayOfValues"
	 * and an array of variables "arrayOfVariables".
	 * @param arrayOfValues the array of values.
	 * @param arrayOfVariables the array of variables.
	 * @return a constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 */
	public Var scalProd(String name, int[] arrayOfValues, Var[] arrayOfVariables);
	
	/**
	 * Returns an integer constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 * @param arrayOfValues the array of values.
	 * @param arrayOfVariables the array of variables.
	 * @return a constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 */
	public Var scalProd(int[] arrayOfValues, Var[] arrayOfVariables);
	
	/**
	 * Creates and posts a constraint: var "oper" scalProd(arrayOfValues,arrayOfVariables)
	 * @param var the constraint variable
	 * @param oper 
	 * @param arrayOfValues the array of values.
	 * @param arrayOfVariables the array of variables.
	 * @return a constraint 
	 */
	public Constraint postScalProd(Var var, String oper, int[] arrayOfValues, Var[] arrayOfVariables);
	
	public Var scalProd(int[] arrayOfValues, ArrayList<Var> listOfVariables);
	
	/**
	 * Returns a real constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 * @param arrayOfValues the array of values.
	 * @param arrayOfVariables the array of variables.
	 * @return a constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 */
	public VarReal scalProd(double[] arrayOfValues, VarReal[] arrayOfVariables);
	
	/**
	 * Returns a real constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 * @param arrayOfValues the array of values.
	 * @param arrayOfVariables the array of variables.
	 * @return a constrained variable equal to the scalar product of an array of values "arrayOfValues"
	 *         and an array of variables "arrayOfVariables".
	 */
	public VarReal scalProd(double[] arrayOfValues, ConstrainedVariable[] arrayOfVariables);


	/**
	 * Posts the constraint "c". Throws a RuntimeException if a failure happens
	 * during the posting.
	 * @param c the constraint to post.
	 * @throws RuntimeException if the posting fails.
	 */
	public void post(Constraint c);

//	/**
//	 * Attempts to posts all constraints added to the problem. The
//	 * individual "post"s are done by concrete implementation and can throw exceptions
//	 * if a failure occurs during posting. If any constraint fails to post the process
//	 * is halted and this method returns <b>false</b>. If all constraints are posted successfully
//	 * this returns <b>true</b>.
//	 *
//	 * @return <b>true</b> if all constraints are posted successfully; <b>false</b>
//	 *         if at least one constraint failed to be posted.
//	 */
//	public boolean postConstraints();


//	/**
//	 * Returns an "AND" Constraint. The Constraint "AND" is satisfied if both of the
//	 * 		   Constraints "c1" and "c2" are satisfied. The Constraint "AND" is not satisfied
//	 * 		   if at least one of the Constraints "c1" or "c2" is not satisfied.
//	 * @param c1 the first Constraint which is part of the new "AND" Constraint.
//	 * @param c2 the other Constraint which is part of the new "AND" Constraint.
//	 * @return a Constraint "AND" between the Constraints "c1" and "c2".
//	 */
//	public Constraint and(Constraint c1, Constraint c2);

//	/**
//	 * Returns an "AND" Constraint. The Constraint "AND" is satisfied if all of the
//	 * 		   Constraints in the array "array" are satisfied. The Constraint "AND" is not satisfied
//	 * 		   if at least one of the Constraints in "array" is not satisfied.
//	 * @param array the array of constraints forming the new "AND" Constraint.
//	 * @return a Constraint "AND" between all the Constraints in the array "array".
//	 */
//	public Constraint and(Constraint[] array);

//	/**
//	 * Returns an "OR" Constraint. The Constraint "OR" is satisfied if either of the
//	 * 		   Constraints "c1" and "c2" is satisfied. The Constraint "OR" is not satisfied
//	 * 		   if both of the Constraints "c1" and "c2" are not satisfied.
//	 * @param c1 the first Constraint which is part of the new "OR" Constraint.
//	 * @param c2 the other Constraint which is part of the new "OR" Constraint.
//	 * @return a Constraint "OR" between the Constraints "c1" and "c2".
//	 */
//	public Constraint or(Constraint c1, Constraint c2);

//	/**
//	 * Returns a Constraint that is satisfied if and only if the Constraint "c" is not satisfied.
//	 * @param c the constraint we desire the opposite of.
//	 * @return a Constraint that is satisfied if and only if the Constraint "c" is not satisfied.
//	 */
//	public Constraint negation(Constraint c);

	/**
	 * Returns an instance of a Solver associated with this problem. 
	 * If a solver is not defined yet, creates a new Solver and associates it with the problem.
	 * @return a solver
	 */
	public Solver getSolver();
	
	/**
	 * Associates a "solver" with the problem
	 * making it available through the method "getSolver()".
	 * @param solver
	 */
	public void setSolver(Solver solver);
	
	/**
	 * Logs the integer constrained variables contained in the array "vars".
	 * 
	 * @param vars the array of Vars to log.
	 */
	public void log(Var[] vars);
	
	/**
	 * Logs the real constrained variables contained in the array "vars".
	 * 
	 * @param vars the array of VarReals to log.
	 */
	public void log(VarReal[] vars);
	
	public void log(ArrayList<Var> vars);

//	/**
//	 * Logs the String "text" and then all integer constrained variables added to the
//	 * problem.
//	 *
//	 * @param text the text to log prior to logging all the integer constrained
//	 *             variables added to the problem.
//	 */
//	public void log(String text,Var[] vars);

//	/**
//	 * Logs the String "text" and then all real constrained variables
//	 * contained in the array "vars".
//	 *
//	 * @param text the text to log prior to logging all the real constrained
//	 *             variables added to the problem.
//	 */
//	public void log(String text,VarReal[] vars);
//
//	/**
//	 * Logs the String "text" and then all constrained set variables
//	 * contained in the array "vars".
//	 * @param text the text to log prior to logging all the constrained
//	 *             set variables added to the problem.
//	 */
//	public void log(String text,VarSet[] vars);

//	/**
//	 * Logs (displays) all Constraints contained in the array "constraints"
//	 */
//	public void log(Constraint[] constraints);

	/**
	 * Logs (displays) the String parameter "text"
	 */
	public void log(String text);

//	/***************************************************************************
//	 * Global constraints
//	 **************************************************************************/

	/**
	 * Creates and posts a new Constraint stating that all of the elements of
	 * the array of variables "vars" must take different values from each other.
	 * @param vars the array of Vars which must all take different values.
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint postAllDifferent(Var[] vars);
	
	
	/**
	 * Similar to postAllDifferent for variables saved in the ArrayList vars
	 */
	public Constraint postAllDifferent(ArrayList<Var> vars);
	
//	public Constraint postAllDifferent(List vars);
	
	/**
	 * This constraint is a synonym for postAllDifferent
	 */
	public Constraint postAllDiff(Var[] vars);
	
	/**
	 * This method create a new Constraint stating that all of the elements of
	 * the array of variables "vars" must take different values from each other.
	 * To have an effect the constraint should be posted using Constraint's methods
	 * post() or post(ConsistencyLevel consistencyLevel)
	 * @param vars the array of Vars which must all take different values.
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint allDiff(Var[] vars);
	
	/**
	 * This method create a new Constraint stating that all of the elements of
	 * the list of variables "vars" must take different values from each other.
	 * To have an effect the constraint should be posted using Constraint's methods
	 * post() or post(ConsistencyLevel consistencyLevel)
	 * @param vars the ArrayList of Var which must all take different values.
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint allDiff(ArrayList<Var> vars);
	
	/**
	 * Similar to postAllDiff for variables saved in the ArrayList vars
	 */
	public Constraint postAllDiff(java.util.ArrayList<Var> vars);
	
	/**
	 * Posts allDiff constraint for an array of VarString variables
	 */
	public Constraint postAllDiff(VarString[] varStrings);
	
	/**
	 * This method creates and posts a new cardinality constraint 
	 *  cardinality(vars,cardValue) 'oper' value.  
	 * Here cardinality(vars,cardValue) denotes a constrained integer 
	 * variable that is equal to the number of those elements in the 
	 * array "vars" that are bound to the "cardValue".  
	 * For example, if oper is "=" it means that the variable 
	 * cardinality(vars,cardValue) must be equal to the  value.  
	 * This constraint does NOT assume a creation of an intermediate 
	 * variable "cardinality(vars,cardValue)".
	 * @throws RuntimeException if the posting fails
	 * @return a newly created constraint
	 */
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, int value);
	
	public Constraint postCardinality(ArrayList<Var> vars, int cardValue, String oper, int value);
	
	/**
	 * This method is similar to the one above but instead of value 
	 * the cardinality(vars,cardValue) is being constrained by var.
	 */
	public Constraint postCardinality(Var[] vars, int cardValue, String oper, Var var);
	
	public Constraint postCardinality(ArrayList<Var> vars, int cardValue, String oper, Var var);
	
	/**
	 * This method is similar to the one above but instead of cardValue 
	 * it uses "cardVar"
	 */
	public Constraint postCardinality(Var[] vars, Var cardVar, String oper, Var var);
	
	public Constraint postCardinality(ArrayList<Var> vars, Var cardVar, String oper, Var var);
	
	/**
	 * This method is similar to the one above but instead of var 
	 * it uses "value"
	 */
	public Constraint postCardinality(Var[] vars, Var cardVar, String oper, int value);
	
	public Constraint postCardinality(ArrayList<Var> vars, Var cardVar, String oper, int value);


//	/**
//	 * Creates ans posts a new global cardinality constraint.
//	 * This method counts the numbers of occurrences of integer values from a given array "values" 
//	 * among the constrained variables from a given array "vars" and associates 
//	 * these numbers with a given array cardinalityVars.  More precisely, 
//	 * for each i from 0 to (values.lengh-1),  cardinalityVars[i] is equal to 
//	 * the number of occurrences of values[i] in the array "vars".  
//	 * The arrays cardinalityVars and "values" should have the same size. 
//	 *
//	 * @param vars the array of Vars
//	 * @param cardinalityVars the array of Vars.
//	 * @param values the array of values whose cardinality of occurrences
//	 * in "vars" is in consideration.
//	 * @return constraint that sets relationships between "vars and "cardinalityVars" 
//	 * as they are related to "values"
//	 * @throws RuntimeException if arrays cardinalityVars and "values" have different sizes.
//	 * @return a newly created constraint
//	 */
//	public Constraint constraintGlobalCardinality(Var[] vars, Var[] cardinalityVars, int[] values);
	
	
	/**
	 * Creates and posts a new global cardinality constraint that states:
	 * <p>
	 * For each index i the number of times the value "values[i]" occurs 
	 * in the array "vars" is exactly "cardinalityVars[i]"
	 * </p> 
	 * @param vars array of constrained integer variables
	 * @param cardinalityVars array of constrained integer variables (cardinality variables)
	 * @param values array of integer values
	 * @throws RuntimeException if arrays cardinalityVars and "values" have different sizes.
	 * @return a newly created constraint
	 */
	public Constraint postGlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars);
	
	public Constraint postGlobalCardinality(ArrayList<Var> vars, int[] values, Var[] cardinalityVars);
	
//	/**
//	 * This method is similar to the constraintGlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars)
//	 * where the array values is fixed to an array of integers from 0 to (cardinalityVars.length  1).  
//	 *
//	 * @param vars the array of Vars
//	 * @param cardinalityVars the array of Vars.
//	 * @return constraint that sets relationships between "vars and "cardinalityVars" 
//	 * such that "cardinalityVars[i]" is equal to a number of occurrences of the value "i" 
//	 * among the constrained variables from "vars" 
//	 * @throws RuntimeException if the posting fails
//	 * @return a newly created constraint
//	 */
//	public Constraint constraintGlobalCardinality(Var[] vars, Var[] cardinalityVars);
	
	/**
	 * Creates and posts a new global cardinality constraint that states:
	 * <p>
	 * For each index i the number of times the value "values[i]" 
	 * occurs in the array "vars" should be between cardMin[i] and cardMax[i] (inclusive) 
	 * </p>
	 * @param vars array of constrained integer variables
	 * @param values array of integer values within domain of all vars
	 * @param cardMin array of integers that serves as lower bounds for values[i]
	 * @param cardMax array of integers that serves as upper bounds for values[i]
	 * Note that arrays values, cardMin, and cardMax should have the same size 
	 * otherwise a RuntimeException will be thrown
	 */
	public Constraint postGlobalCardinality(Var[] vars, int[] values, int[] cardMin, int[] cardMax);
	
	public Constraint postGlobalCardinality(ArrayList<Var> vars, int[] values, int[] cardMin, int[] cardMax);
	
//	/**
//	 * Creates and posts a new global cardinality constraint.
//	 * This method counts the numbers of occurrences of integer values that 
//	 * may instantiate variables of a given array "valueVars" among the constrained 
//	 * variables from a given array "vars" and associates these numbers with 
//	 * a given array cardinalityVars.  More precisely, for each i from 0 to 
//	 * (valueVars.lengh-1),  cardinalityVars[i] is equal to the number of 
//	 * occurrences of valueVars[i] in the array "vars".  
//	 * The arrays cardinalityVars and "valueVarss" should have the same size  
//	 * otherwise a RuntimeException will be thrown. 
//	 *
//	 * @param vars the array of Vars
//	 * @param cardinalityVars the array of Vars.
//	 * @param valueVars the array of values whose cardinality of occurrences
//	 * in "vars" is in consideration.
//	 * @return constraint that sets relationships between "vars and "cardinalityVars" 
//	 * as they are related to "values"
//	 * @throws RuntimeException if arrays cardinalityVars and "values" have different sizes.
//	 * @throws RuntimeException if the posting fails
//	 * @return a newly created constraint
//	 */
//	public Constraint constraintGlobalCardinality(Var[] vars, Var[] cardinalityVars, Var[] valueVars);
	
	/**
	 * Creates a constraint that states: 
	 * the integer constrained variable "var" should take at least one value from the "array" of integers 
	 * @param array an array of integer values
	 * @param var an integer constrained variable
	 * @return a constraint
	 */
	public Constraint isOneOfConstraint(int[] array, Var var);
	
	/**
	 * Creates a constraint that states: 
	 * the string constrained variable "var" should take at least one value from the "array" of strings 
	 * @param array an array of String values
	 * @param var an string constrained variable
	 * @return a constraint
	 */
	public Constraint isOneOfConstraint(String[] array, VarString var);
	
	/**
	 * Creates a constraint that states: 
	 * the integer constrained variable "var" should not take any one value from the "array" of integers 
	 * @param array an array of integer values
	 * @param var an integer constrained variable
	 * @return a constraint
	 */
	public Constraint isNotOneOfConstraint(int[] array, Var var);
	
	/**
	 * Creates a constraint that states: 
	 * the string constrained variable "var" should not take any value from the "array" of strings 
	 * @param array an array of String values
	 * @param var an string constrained variable
	 * @return a constraint
	 */
	public Constraint isNotOneOfConstraint(String[] array, VarString var);
	
	/**
	 * Creates and posts a constraint that states the implication: if constraint1 then constraint2.
	 * In other words, if this constraint1 is satisfied, then constraint2
	 * should also be satisfied.
	 *
	 * @param constraint1  If-Constraint in the implication.
	 * @param constraint2  Then-Constraint in the implication.
	 * @return a Constraint that means: if constraint1 then constraint2
	 */
	public Constraint postIfThen(Constraint constraint1, Constraint constraint2);
	
	/**
	 * Returns a Constraint that states the implication: 
	 * "if boolean variable var1 is true then boolean variable var2 is also true"
	 *
	 * @param var1 the VarBool
	 * @param var2 the VarBool
	 * @return a Constraint that means "if var1 then var2"
	 */
	public Constraint postIfThen(VarBool var1, VarBool var2);
	
	/*
	 * @returns true if there are constraints posted with a probability between VER_LOW and VERY_HIGH
	 * @returns false otherwise
	 */
	public boolean areThereProbabilityConstraints(); 
	
	/*
	 * Returns a Var that is equal to the sum of all weighted violations of all
	 * constraints posted with some Probability. Returns null if there are no such constraints. 
	 */
	public Var getTotalConstraintViolation();
	
	/**
	 * Creates and posts a constraint an "AND" Constraint. The Constraint "AND" is satisfied if both of the
	 * Constraints "c1" and "c2" are satisfied. The Constraint "AND" is not satisfied
	 * if at least one of the Constraints "c1" or "c2" are not satisfied.
	 * @param c1 a Constraint
	 * @param c2 a Constraint  
	 * @return a Constraint "AND" between the Constraints "c1" and "c2".
	 */
	public Constraint postAnd(Constraint c1, Constraint c2);
	
	/**
	 * Creates and posts a constraint an "OR" Constraint. The Constraint "OR" is satisfied if 
	 * at least one of constraints "c1" or "c2" are satisfied. The Constraint "OR" is not satisfied
	 * if both constraints "c1" or "c2" are not satisfied.
	 * @param c1 a Constraint
	 * @param c2 a Constraint  
	 * @return a Constraint "OR" between the Constraints "c1" and "c2".
	 */
	public Constraint postOr(Constraint c1, Constraint c2);

	/**
	 * Creates and posts a constraint: max(vars) "oper" var
	 * where max(vars) is a maximal variable in the array "vars" 
	 * when they are all instantiated.
	 * @param vars
	 * @param oper
	 * @param var
	 */
	public Constraint postMax(Var[] vars, String oper, Var var);
	
	/**
	 * Creates and posts a constraint: max(vars) "oper" value
	 * where max(vars) is a maximal variable in the array "vars" 
	 * when they are all instantiated.
	 * @param vars
	 * @param oper
	 * @param value
	 */
	public Constraint postMax(Var[] vars, String oper, int value);
	
	/**
	 * Creates and posts a constraint: min(vars) "oper" var
	 * where max(vars) is a minimal variable in the array "vars" 
	 * when they are all instantiated.
	 * @param vars
	 * @param oper
	 * @param var
	 */
	public Constraint postMin(Var[] vars, String oper, Var var);
	
	/**
	 * Creates and posts a constraint: min(vars) "oper" value
	 * where max(vars) is a minimal variable in the array "vars" 
	 * when they are all instantiated.
	 * @param vars
	 * @param oper
	 * @param value
	 */
	public Constraint postMin(Var[] vars, String oper, int value);

	/**
	 * Loads a Problem represented by the XML document on the specified
	 * input stream into this instance of the Problem
	 * @param in the input stream from which to read the XML document.
	 * @throws Exception if reading from the specified input stream
	 * results in an IOException or data on input stream does not
	 * constitute a valid XML document with the mandated document type.
	 */
	public void loadFromXML(InputStream in) throws Exception;

	/**
	 * Emits an XML document representing this instance of the Problem.
	 * @param os the output stream on which to emit the XML document.
	 * @param comment a description of the property list, or null if no
	 * comment is desired. If the specified comment is null then no
	 * comment will be stored in the document.
	 * @throws Exception IOException - if writing to the specified
	 * output stream results in an IOException; NullPointerException -
	 * if os is null.
	 */
	public void storeToXML(OutputStream os, String comment) throws Exception;
	
	// VarMatrix methods
	public VarMatrix variableMatrix(String name, int min, int max, int rows,
			int columns);

	public VarMatrix getVarMatrix(String name);
	
	// VarString methods
	public void add(VarString var);

	public VarString[] getVarStrings();

	public VarString getVarString(String name);

	public VarString variableString(String name, String[] allStrings);

	public Constraint post(VarString var, String oper, String value);

	public Constraint linear(VarString var, String oper, String value);
	
	public Constraint linear(VarString var1, String oper, VarString var2);

	public Constraint post(VarString var1, String oper, VarString var2);
	
	
	
}
