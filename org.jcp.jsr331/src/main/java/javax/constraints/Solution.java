//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  331
// 
// Specification
// 
//=============================================
package javax.constraints;

import java.util.Set;

/**
 * This interface defines a solution of a Problem.
 * It includes values for all constrained variables that were added to the problem
 * and were instantiated using a Solver.
 */

public interface Solution {

//	/**
//	 * Returns an array of the integer variables included in the solution.
//	 * @return an array of the integer variables included in the solution.
//	 */
//	public Var[] getVars();

//	/**
//	 * Returns an array of the real variables included in the solution.
//	 * @return an array of the real variables included in the solution.
//	 */
//	public VarReal[] getVarReals();

//	/**
//	 * Returns the solution real variable with the given name.
//	 * @param name the name of the desired solution real variable.
//	 * @return the solution real variable with the given name.
//	 */
//	public VarReal getVarReal(String name);

//	/**
//	 * Returns the solution variable with the given name.
//	 * @param name the name of the desired solution variable.
//	 * @return the solution variable with the given name.
//	 */
//	public VarSet getVarSet(String name);

//	/**
//	 * Returns an array of the integer variables included in the solution.
//	 * @return an array of the integer variables included in the solution.
//	 */
//	public VarSet[] getVarSets();

//	/**
//	 * Returns the solution variable with the given name.
//	 * @param name the name of the desired solution variable.
//	 * @return the solution variable with the given name.
//	 */
//	public Var getVar(String name);

	/**
	 * Returns the value of the solution variable with the given name,
	 *         throws a RuntimeException if the variable is not bound.
	 * @param name the name of the desired solution variable .
	 * @return the value of solution variable with the given name,
	 *         assuming that the variable is bound.
	 * @throws RuntimeException if the solution variable is not bound.
	 */
	public int getValue(String name);
	
	/**
	 * A synonym for getValue(name). 
	 * Used by Groovy to overload the operator solution["Name"]
	 * @param name
	 * @return the value of solution variable with the given name,
	 *         assuming that the variable is bound.
	 * @throws RuntimeException if the solution variable is not bound. 
	 */
	public int getAt(String name);
	
//	/**
//	 * Returns the value of the solution variable with the index i,
//	 *         throws a RuntimeException if the variable is not bound.
//	 * @param i the index of the desired solution variable .
//	 * @return the value of solution variable with the given name,
//	 *         assuming that the variable is bound.
//	 * @throws RuntimeException if the solution variable is not bound.
//	 */
//	public int getValue(int i);
	
	/**
	 * Returns the value of the sting solution variable with the given name,
	 *         throws a RuntimeException if the variable is not bound.
	 * @param name the name of the desired solution variable .
	 * @return the String from a solution variable with the given name,
	 *         assuming that the variable is bound.
	 * @throws RuntimeException if the solution variable is not bound.
	 */
	public String getValueString(String name);

	/**
	 * Returns the value of the solution real variable with the given name,
	 *         throws a RuntimeException if the real variable is not bound.
	 * @param name the name of the desired solution real variable .
	 * @return the value of solution real variable with the given name,
	 *         assuming that the real variable is bound.
	 * @throws RuntimeException if the solution real variable is not bound.
	 */
	public double getValueReal(String name);
	
//	/**
//	 * Returns the value of the solution real variable with the index i,
//	 *         throws a RuntimeException if the real variable is not bound.
//	 * @param i the index of the desired solution real variable .
//	 * @return the value of solution real variable with the given name,
//	 *         assuming that the real variable is bound.
//	 * @throws RuntimeException if the solution real variable is not bound.
//	 */
//	public double getRealValue(int i);
	
	/**
	 * Returns the value of the solution set variable with the given name,
	 *         throws a RuntimeException if the set variable is not bound.
	 * @param name the name of the desired solution set variable .
	 * @return the value of solution set variable with the given name,
	 *         assuming that the set variable is bound.
	 * @throws RuntimeException if the solution set variable is not bound.
	 */
	public Set<Integer> getValueSet(String name);

	/**
	 * Returns the number of decision integer variables in the solution
	 * @return the number of decision integer variables in the solution
	 */
	public int getNumberOfVars();
	
	/**
	 * Returns the number of decision real variables in the solution
	 * @return the number of decision real variables in the solution
	 */
	public int getNumberOfVarReals();
	
	/**
	 * Returns the number of decision set variables in the solution
	 * @return the number of decision set variables in the solution
	 */
	public int getNumberOfVarSets();
	
	/**
	 * Returns the minimum value in the domain of the solution variable with the given name.
	 * @param name the name of the desired solution variable.
	 * @return minimum value of the solution variable with the given name.
	 */
	public int getMin(String name);
	
//	/**
//	 * Returns the minimum value in the domain of the solution variable with the index i.
//	 * @param i the index of the desired solution variable.
//	 * @return minimum value of the solution variable with the given name.
//	 */
//	public int getMin(int i);

	/**
	 * Returns the maximum value in the domain of the solution variable with the given name.
	 * @param name the name of the desired solution variable.
	 * @return maximum value of the solution variable with the given name.
	 */
	public int getMax(String name);
	
//	/**
//	 * Returns the maximum value in the domain of the solution variable with index i.
//	 * @param i the index of the desired solution variable.
//	 * @return maximum value of the solution variable with the given name.
//	 */
//	public int getMax(int i);

	/**
	 * Returns true if the domain of the solution variable with the given name
	 *         is a single value, i.e. getMin(name) == getMax(name).
	 * @param name the name of the desired solution variable.
	 * @return true if the domain of the solution variable with the given name
	 *         is a single value.
	 */
	public boolean isBound(String name);
	
	/**
	 * Saves the found results back to the constrained variables included into the solution
	 */
	public void save();
	
//	/**
//	 * Returns true if the domain of the solution variable with the index i
//	 *         is a single value, i.e. getMin(name) == getMax(name).
//	 * @param i the index of the desired solution variable.
//	 * @return true if the domain of the solution variable with the given name
//	 *         is a single value.
//	 */
//	public boolean isBound(int i);

	/**
	 * Returns true if all the solution variables are bound, i.e. if isBound(name)
	 *         returns true for all solution variables.
	 * @return true if all the solutions variables are bound.
	 * @see Solution#isBound(String)
	 */
	public boolean isBound();

	/**
	 * Returns the number identifying this Solution.
	 * @return the number identifying this Solution.
	 */
	public int getSolutionNumber();

	/**
	 * Sets the number identifying this Solution.
	 * @param number the number identifying this Solution.
	 */
	public void setSolutionNumber(int number);

	/**
	 * Logs the solution in the information log
	 */
	public void log();
	
	/**
	 * Logs the solution in the information log. Logs only "varPerLine" variables per line.
	 * By default varPerLine = 8.
	 */
	public void log(int varPerLine);

//	/**
//	 * Logs integer variables of the solution in the information log
//	 */
//	public void logVars();
//
//	/**
//	 * Logs real variables of the solution in the information log
//	 */
//	public void logVarReals();
//
//	/**
//	 * Logs set variables of the solution in the information log
//	 */
//	public void logVarSets();

	/**
	 * Returns the solver that created this Solution.
	 * @return the solver that created this Solution.
	 */
	public Solver getSolver();

	/**
	 * Returns the problem that this Solution is a solution to.
	 * @return the problem that this Solution is a solution to.
	 */
	public Problem getProblem();

}
