package javax.constraints.linear.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import javax.constraints.Objective;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.linear.ComparableVariable;

import org.ojalgo.concurrent.ConcurrentUtils;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.optimisation.OptimisationSolver;
import org.ojalgo.optimisation.OptimisationSolver.Result;
import org.ojalgo.optimisation.State;
import org.ojalgo.optimisation.linear.mps.MathProgSysModel;


public class OldLinearSolver extends javax.constraints.linear.LinearSolver {
	
	static public final String JSR331_LINEAR_SOLVER_VERSION = "ojAlgo (Sep-2009)";
	
	public OldLinearSolver() {
	}
	
	public String getVersion() {
		return JSR331_LINEAR_SOLVER_VERSION;
	}
	/**
	 * ojAlso minimizes by default
	 */
	public Objective getDefaultOptimizationObjective() {
		return Objective.MINIMIZE;
	}
	
	/**
	 * 
	 * @param file
	 * @return solution or null
	 */
	public Solution solve(File file) {
		
		javax.constraints.impl.Problem problem = 
				(javax.constraints.impl.Problem)getProblem();
				
		MathProgSysModel mps = MathProgSysModel.makeFromFile(file);
		//solve  
		OptimisationSolver os = mps.getDefaultSolver();
		Result rs = os.solve();
		
		if( rs.getState() == State.INFEASIBLE )
		{
			//log("Linear Solver Found No Solutions");
			return null; //no solution
		}

		BasicMatrix basicMatrix = rs.getSolution(); 
		
		//ojAlgo orders result variables lexicographically
		int numberOfVars = 0;
		Var[] vars = problem.getVars();
		if (vars != null) {
			numberOfVars = vars.length;
		}
		int numberOfVarReals = 0;
		VarReal[] varReals = problem.getVarReals();
		if (varReals != null) {
			numberOfVarReals = varReals.length;
		}
		ComparableVariable[] varsArray = new ComparableVariable[numberOfVars + numberOfVarReals];
		int n = 0;
		for (int i = 0; i < numberOfVars; i++) {
			javax.constraints.impl.Var var = (javax.constraints.impl.Var) vars[i];
			varsArray[n++] = new ComparableVariable(var);
		}
		for (int i = 0; i < numberOfVarReals; i++) {
			javax.constraints.impl.VarReal varReal = (javax.constraints.impl.VarReal) varReals[i];
			varsArray[n++] = new ComparableVariable(varReal);
		}
		java.util.Arrays.sort(varsArray);
	
		//assign the values from the solution to the variables
		for(int i = 0; i < varsArray.length; i++) {
			double value = basicMatrix.doubleValue(i, 0);
			ComparableVariable var = varsArray[i];
			if (var.isInteger()) {
				javax.constraints.impl.Var intVar = var.getVar();
				intVar.setValue((int)value);
			}
			if (var.isReal()) {
				javax.constraints.impl.VarReal realVar = var.getVarReal();
				realVar.setValue((int)value);
			}
		}
		Solution solution = new javax.constraints.linear.impl.search.Solution(this, 1);
		
		ConcurrentUtils.EXECUTOR.setCorePoolSize(0); // stop ojAlgo maintaining its default 1 thread
		
		return solution;
	}
	
//	public int[] readResultValues() {
//		return null; // no intermediate files are used
//	}
	
	public HashMap<String, String> readResults() {
		return null;
	}
	
	public String getCommanLine() {
		return null; // no command lines used
	} 
	

}
