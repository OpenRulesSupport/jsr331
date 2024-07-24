//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.search;

import java.util.ArrayList;

/**
 * This class defines a solution to a Problem.
 * It includes all constrained variables that were added to the problem and are
 * instantiated using a Solver
 */

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.constraints.Problem;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.VarString;
import javax.constraints.impl.AbstractProblem;
import javax.constraints.VarString;

public class BasicSolution implements Solution {
	Solver 			solver;
	int 			solutionNumber;
	Var[]		 	vars; 
	ResultInt[] 	intResults;
	ResultReal[] 	realResults;
	ResultSet[] 	setResults;
	
	public BasicSolution(Solver solver, int solutionNumber) {
		this.solver = solver;
		this.solutionNumber = solutionNumber;
		
		Vector<SearchStrategy> searchStrategies = 
			((AbstractSolver)solver).getSearchStrategies();
		Vector<Var> allvars = new Vector<Var>();
		for (SearchStrategy strategy : searchStrategies) {
			Var[] strategyVars = strategy.getVars();
			for (Var var : strategyVars) {
				if (!allvars.contains(var))
					allvars.add(var);
			}
		}
		vars = new Var[allvars.size()];
		intResults = new ResultInt[vars.length];
		for (int i = 0; i < vars.length; i++) {
			Var var = allvars.get(i);
			vars[i] = var;
			intResults[i] = createResult(var);
		}
		
		// TODO: add loops for realResults and setResults
	}
	
	public Solver getSolver() {
		return solver;
	}
	
	public Problem getProblem() {
		return solver.getProblem();
	}
	
	public Var[] getVars() {
		return vars;
	}

//	public VarReal[] getVarReals()
//	{
//		return varReals;
//	}

//	public VarReal getVarReal(String name) {
//		for(int i=0; i < varReals.length; i++) {
//			VarReal varR = varReals[i];
//			if (name.equals(varR.getName()))
//				return varR;
//		}
//		return null;
//	}

//	public Var getVar(String name) {
//		for(int i=0; i < vars.length; i++) {
//			Var var = vars[i];
//			if (name.equals(var.getName()))
//				return var;
//		}
//		return null;
//	}

//	public VarSet getVarSet(String name)
//	{
//		for(int i=0; i < varSets.length; i++) {
//			VarSet var = varSets[i];
//			if (name.equals(var.getName()))
//				return var;
//		}
//		return null;
//	}

//	public VarSet[] getVarSets()
//	{
//		return varSets;
//	}

	public int getSolutionNumber() {
		return solutionNumber;
	}

	public void setSolutionNumber(int number) {
		solutionNumber = number;
	}

	/**
	 * Returns the number of decision integer variables in the solution
	 * @return the number of decision integer variables in the solution
	 */
	public int getNumberOfVars() {
		if (intResults != null)
			return intResults.length;
		else
			return 0;
	}
	
	/**
	 * Returns the number of decision integer variables in the solution
	 * @return the number of decision integer variables in the solution
	 */
	public int getNumberOfVarReals() {
		if (realResults != null)
			return realResults.length;
		else
			return 0;
	}
	
	/**
	 * Returns the number of decision integer variables in the solution
	 * @return the number of decision integer variables in the solution
	 */
	public int getNumberOfVarSets() {
		if (setResults != null)
			return setResults.length;
		else
			return 0;
	}
	
	public int getValue(String name){
		int i = getIndexOfInt(name);
		if(intResults[i].bound)
			return intResults[i].value;
		throw new RuntimeException("variable " + name + " is not bound");
	}
	
	/**
	 * A synonym for getValue(name). 
	 * Used by Groovy to overload the operator solution["Name"]
	 * @param name
	 * @return int
	 */
	public int getAt(String name) {
		return getValue(name);
	}
	
//	public int getValue(int i){
//		if(intResults[i].bound)
//			return intResults[i].value;
//		throw new RuntimeException("variable " + intResults[i].varName + " is not bound");
//	}
	
	
	public double getValueReal(String name){
		int i = getIndexOfReal(name);
		if(realResults[i].bound)
			return ( (realResults[i].min + realResults[i].max)/2 );
		throw new RuntimeException("real variable " + name + " is not bound");
	}
	
	/**
	 * Returns the String value of the solution String variable with the given name,
	 *         throws a RuntimeException if the string variable is not bound.
	 * @param name the name of the desired solution string variable .
	 * @return the value of solution string variable with the given name,
	 *         assuming that the string variable is bound.
	 * @throws RuntimeException if the solution string variable is not bound.
	 */
	public String getValueString(String name) {
		int intValue= getValue(name);
		AbstractProblem p = (AbstractProblem)getProblem();
		VarString varString = p.getVarString(name);
		return varString.getValue(intValue);
	}
	
	public Set<Integer> getValueSet(String name){
		int i = getIndexOfSet(name);
		if(setResults[i].bound)
			return setResults[i].value;
		throw new RuntimeException("Set variable " + name + " is not bound");
	}
	
//	public double getRealValue(int i){
//		if(realResults[i].bound)
//			return ( (realResults[i].min + realResults[i].max)/2 );
//		throw new RuntimeException("real variable " + realResults[i].varName + " is not bound");
//	}

	public int getMin(int i){
		return intResults[i].min;
	}
	
	public int getMin(String name){
		int i = getIndexOfInt(name);
		return intResults[i].min;
	}

	public int getMax(int i){
		return intResults[i].max;
	}
	
	public int getMax(String name){
		int i = getIndexOfInt(name);
		return intResults[i].max;
	}

//	public boolean isBound(int i){
//		return intResults[i].bound;
//	}
	
	public boolean isBound(String name){
		int i = getIndexOfInt(name);
		return intResults[i].bound;
	}

	private int getIndexOfInt(String name){
		if (intResults != null)
		for (int i = 0; i < intResults.length; i++) {
			if(name.equals(intResults[i].varName))
				return i;
		}
		throw new RuntimeException("ERROR: variable " + name + " not found in Solution");
	}
	
	public double getMinReal(int i){
		return realResults[i].min;
	}
	
	public double getMaxReal(int i){
		return realResults[i].max;
	}

	private int getIndexOfReal(String name){
		if (realResults != null)
		for (int i = 0; i < realResults.length; i++) {
			if(name.equals(realResults[i].varName))
				return i;
		}
		throw new RuntimeException("ERROR: real variable " + name + " not found in Solution");
	}
	
	private int getIndexOfSet(String name){
		if (setResults != null)
		for (int i = 0; i < setResults.length; i++) {
			if(name.equals(setResults[i].varName))
				return i;
		}
		throw new RuntimeException("ERROR: set variable " + name + " not found in Solution");
	}

	public boolean isBound() {
		for(int i=0; i < intResults.length; i++) {
			if (!intResults[i].bound)
				return false;
		}
//		// TODO for real and set
		return true;
	}
		
	public void save() {
		if (intResults != null)
		for (int i = 0; i < intResults.length; i++) {
			intResults[i].save();
		}
		if (realResults != null)
		for (int i = 0; i < realResults.length; i++) {
			realResults[i].save();
		}
	}

	static int varPerLine = 9;
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (intResults != null)
		for(int i=0; i < intResults.length; i++) {
			if (i>0 && i%(varPerLine) == 0)
				buf.append("\n\t");
			buf.append(" " + intResults[i].toString());
		}
		return "Solution #"+solutionNumber+":\n\t"+ buf.toString();
	}

	public void log() {
		getProblem().log(toString());
	}
	
	/**
	 * Logs the solution in the information log. Logs only "varPerLine" variables per line.
	 * By default varPerLine = 8.
	 */
	public void log(int varPerLinePar) {
		varPerLine = varPerLinePar;
		log();
	}

	/**
	 * Logs integer variables of the solution in the information log
	 */
	public void logVars() {
		for (int i = 0; i < intResults.length; i++) {
			getProblem().log(intResults[i].toString());
		}
	}

	/**
	 * Logs real variables of the solution in the information log
	 */
	public void logVarReals() {
		for (int i = 0; i < realResults.length; i++) {
			getProblem().log(realResults[i].toString());
		}
	}

	/**
	 * Logs set variables of the solution in the information log
	 */
	public void logVarSets() {
		for (int i = 0; i < setResults.length; i++) {
			getProblem().log(setResults[i].toString());
		}
	}
	
	ResultInt createResult(Var var) {
		ResultInt result = new ResultInt();
		result.varName = var.getName();
		if (var.isBound()) {
			result.value = var.getValue();
			result.min = result.value;
			result.max = result.value;
			result.bound = true;
		}
		else {
			result.min = var.getMin();
			result.max = var.getMax();
			result.bound = false;
		}
		return result;
	}
	
	class ResultInt {
		String varName;
		int value;
		int min;
		int max;
		boolean bound;
		
		public String toString(){
			if (bound) {
					return varName + "[" + value + "]";
			}
			else
				return varName + "[" + min + ".." + max + "]";
		}
		
		public void save() {
			Problem p = getProblem();
			Var var = p.getVar(varName);
			if (var != null) {
				p.post(var,">=",min);
				p.post(var,"<=",max);
			}
		}
	}
	
	
	class ResultReal {
		String varName;
		double value;
		double min;
		double max;
		boolean bound;
		
		public String toString(){
			if (bound) {
					return varName + "[" + value + "]";
			}
			else
				return varName + "[" + min + ".." + max + "]";
		}
		
		public void save() {
			Problem p = getProblem();
			VarReal var = p.getVarReal(varName);
			if (var != null) {
				p.post(var,">=",min);
				p.post(var,"<=",max);
			}
		}
	}
	
	class ResultSet {
		String varName;
		Set value;
		Set possible;
		Set required;
		boolean bound;
		
		public String toString(){
			return varName;
		}
	}
}
