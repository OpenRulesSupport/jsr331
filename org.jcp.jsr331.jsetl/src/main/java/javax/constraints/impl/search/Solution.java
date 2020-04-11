package javax.constraints.impl.search;

import java.util.Iterator;
import java.util.Vector;

import javax.constraints.impl.VarSet;
import javax.constraints.impl.search.SearchStrategy;
import javax.constraints.impl.search.Solver;

import JSetL.Failure;
import JSetL.SetLVar;

/**
 * Implements the JSR331 solution extending the common 
 * implementation BasicSolution. The implementation is base on the solver 
 * JSetL. 
 * 
 * <p>To support the implementation of set variables a new array of
 * ResultSet as result was added to the class, like the BasicSolution do for
 * integer and real variables.
 * 
 * <p>ResultSet is a new class that represent the set result of the
 * solution.
 * 
 * @author Fabio Biselli
 *
 */
public class Solution extends BasicSolution {
	
	ResultSet[] setResults;

	/**
	 * Build a new Solution calling the super constructor that create
	 * integer and real results, than create the own set results bounds to
	 * the variables in the specified search strategies.
	 * 
	 * @param solver the solver which the solution is bound
	 * @param solutionNumber the number of the solution.
	 * 
	 * @throws Failure a failure
	 */
	public Solution(Solver solver, int solutionNumber) throws Failure {
		super(solver, solutionNumber);
		Vector<javax.constraints.SearchStrategy> searchStrategies = 
			((AbstractSolver)solver).getSearchStrategies();
		Vector<javax.constraints.VarSet> strategyVars = 
			new Vector<javax.constraints.VarSet>();
		for (javax.constraints.SearchStrategy strategy : searchStrategies) {
			javax.constraints.VarSet[] vars = ((SearchStrategy) strategy).getVarSet();
			if (vars == null)
				return;
			for (javax.constraints.VarSet var : vars) {
				if (!strategyVars.contains(var))
					strategyVars.add(var);
			}
		}
		setResults = new ResultSet[strategyVars.size()];
		Iterator<javax.constraints.VarSet> iter = strategyVars.iterator();
		int i = 0;
		while(iter.hasNext()) {
			javax.constraints.VarSet var = iter.next();
			setResults[i++] = createResult(var);
		}	
	}

	/**
	 * Build a new ResultSet from a given VarSet.
	 * 
	 * @param var the set variable.
	 * 
	 * @return the new ResultSet.
	 */
	private ResultSet createResult(javax.constraints.VarSet var) {
		ResultSet result = new ResultSet();
		if(var.isBound())
			result.bound = true;
		else result.bound = false;
		result.var = (SetLVar) ((VarSet) var).getImpl();
		return result;
	}
	
	public String toString() {
		String result = super.toString();//new StringBuffer();
		StringBuffer buf = new StringBuffer(result);
		if (setResults == null)
			return result;
		for(int i=0; i < setResults.length; i++) {
			buf.append("\n\t "+ setResults[i].getName()+" = " 
					+ setResults[i].toString());
		}
		return buf.toString();
	}
	
	public void log() {
		getProblem().log(toString());
	}

	/**
	 * Auxiliary class for Solution. It override the class 
	 * BasicSolution.ResultSet.
	 * 
	 * @author Fabio Biselli
	 *
	 */
	class ResultSet {
		SetLVar var;
		Boolean bound;
		
		public String toString() {
			return var.toString();
		}
		
		public String getName() {
			return var.getName();
		}
	}
	
}
