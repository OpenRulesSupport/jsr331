//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.search;

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
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.VarString;
import javax.constraints.impl.AbstractProblem;
import javax.constraints.VarString;

public class Solution implements javax.constraints.Solution {
	Solver solver;
	int solutionNumber;
	ResultInt[] intResults = null;
	ResultReal[] realResults = null;

	public Solution(Solver solver, int solutionNumber) {
		this.solver = solver;
		this.solutionNumber = solutionNumber;
		Var[] vars = getProblem().getVars();
		if (vars != null) {
			intResults = new ResultInt[vars.length];
			for (int i = 0; i < intResults.length; i++) {
				intResults[i] = createResultInt(vars[i]);
			}
		}

		VarReal[] varReals = getProblem().getVarReals();
		if (varReals != null) {
			realResults = new ResultReal[varReals.length];
			for (int i = 0; i < realResults.length; i++) {
				realResults[i] = createResultReal(varReals[i]);
			}
		}
	}

	public Solver getSolver() {
		return solver;
	}

	public Problem getProblem() {
		return solver.getProblem();
	}

	public int getSolutionNumber() {
		return solutionNumber;
	}

	public void setSolutionNumber(int number) {
		solutionNumber = number;
	}

	/**
	 * Returns the number of decision integer variables in the solution
	 * 
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
	 * 
	 * @return the number of decision integer variables in the solution
	 */
	public int getNumberOfVarReals() {
		if (realResults != null)
			return realResults.length;
		else
			return 0;
	}

	public int getNumberOfVarSets() {
		return 0;
	}

	public int getValue(String name) {
		int i = getIndexOfInt(name);
		//if (intResults[i].bound)
			return intResults[i].value;
		//throw new RuntimeException("variable " + name + " is not bound");
	}
	
//	public String getValueString(String name){
//		VarString varString = getProblem().getVarString(name);
//		int i = getValue(name);
//		return varString.getValue(i);
//	}

	/**
	 * A synonym for getValue(name). Used by Groovy to overload the operator
	 * solution["Name"]
	 * 
	 * @param name a name of the variable
	 * @return a found value of the variable
	 */
	public int getAt(String name) {
		return getValue(name);
	}

	public double getValueReal(String name) {
		int i = getIndexOfReal(name);
		//if (realResults[i].bound)
			return ((realResults[i].min + realResults[i].max) / 2);
		//throw new RuntimeException("real variable " + name + " is not bound");
	}
	
	public Set<Integer> getValueSet(String name) {
		throw new RuntimeException("Linear solver does not deal with set variables");
	}

	public int getMin(String name) {
		int i = getIndexOfInt(name);
		return intResults[i].min;
	}

	public int getMax(String name) {
		int i = getIndexOfInt(name);
		return intResults[i].max;
	}

	public boolean isBound(String name) {
		int i = getIndexOfInt(name);
		return intResults[i].bound;
	}

	private int getIndexOfInt(String name) {
		if (intResults != null)
		for (int i = 0; i < intResults.length; i++) {
			if (name.equals(intResults[i].varName))
				return i;
		}
		throw new RuntimeException("Integer variable " + name + " is not found in Solution");
	}

	private int getIndexOfReal(String name) {
		if (realResults != null)
		for (int i = 0; i < realResults.length; i++) {
			if (name.equals(realResults[i].varName))
				return i;
		}
		throw new RuntimeException("Real variable " + name + " is not found in Solution");
	}

	public boolean isBound() {
		for (int i = 0; i < intResults.length; i++) {
			if (!intResults[i].bound)
				return false;
		}
		// // TODO for real and set
		return true;
	}

	int varPerLine = 8;

	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (intResults != null)
		for (int i = 0; i < intResults.length; i++) {
			if (i > 0 && i % (varPerLine) == 0)
				buf.append("\n\t");
			buf.append(" " + intResults[i].toString());
		}
		if (realResults != null)
		for (int i = 0; i < realResults.length; i++) {
			if (i > 0 && i % (varPerLine) == 0)
				buf.append("\n\t");
			buf.append(" " + realResults[i].toString());
		}
		return "Solution #" + solutionNumber + ":\n\t" + buf.toString();
	}

	public void log() {
		varPerLine = 9;
		getProblem().log(toString());
	}

	/**
	 * Logs the solution in the information log. Logs only "varPerLine"
	 * variables per line. By default varPerLine = 9.
	 */
	public void log(int varPerLinePar) {
		varPerLine = varPerLinePar;
		getProblem().log(toString());
	}

	/**
	 * Logs integer variables of the solution in the information log
	 */
	public void logVars() {
		if (intResults != null)
		for (int i = 0; i < intResults.length; i++) {
			getProblem().log(intResults[i].toString());
		}
	}

	/**
	 * Logs real variables of the solution in the information log
	 */
	public void logVarReals() {
		if (realResults != null)
		for (int i = 0; i < realResults.length; i++) {
			getProblem().log(realResults[i].toString());
		}
	}

	ResultInt createResultInt(Var var) {
		ResultInt result = new ResultInt();
		result.varName = var.getName();
		if (var.isBound()) {
			result.value = var.getValue();
			result.min = result.value;
			result.max = result.value;
			result.bound = true;
		} else {
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

		public String toString() {
			if (bound) {
				return varName + "[" + value + "]";
			} else
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

	ResultReal createResultReal(VarReal var) {
		ResultReal result = new ResultReal();
		result.varName = var.getName();
		if (var.isBound()) {
			result.value = var.getValue();
			result.min = result.value;
			result.max = result.value;
			result.bound = true;
		} else {
			result.min = var.getMin();
			result.max = var.getMax();
			result.bound = false;
		}
		return result;
	}

	class ResultReal {
		String varName;
		double value;
		double min;
		double max;
		boolean bound;

		public String toString() {
			if (bound) {
				return varName + "[" + value + "]";
			} else
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
	
	public String getValueString(String name) {
		int intValue= getValue(name);
		AbstractProblem p = (AbstractProblem)getProblem();
		VarString varString = p.getVarString(name);
		return varString.getValue(intValue);
	}

}
