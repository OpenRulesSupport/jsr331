package javax.constraints.linear.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import javax.constraints.Objective;
import javax.constraints.Var;
import javax.constraints.VarReal;

public class LinearSolver extends javax.constraints.linear.LinearSolver {

	static public final String JSR331_LINEAR_SOLVER_VERSION = "SCIP v.3.0.0 with SoPlex 1.7.0";

	public LinearSolver() {
	}

	public String getCommanLine() {
		String exe = System.getProperty(LP_SOLVER_EXE);
		if (exe == null) {
			exe = "scip";
		}
		String options = System.getProperty(LP_SOLVER_OPTIONS);
		if (options == null) {
			options = "";
		}
		return exe + " -c " + "\"read " + getInputFilename() + " " + options 
				+ " optimize write solution " + getOutputFilename() + " quit\"";
	}

	public String getVersion() {
		return JSR331_LINEAR_SOLVER_VERSION;
	}

	/**
	 * GLPK minimizes by default
	 */
	public Objective getDefaultOptimizationObjective() {
		return Objective.MINIMIZE;
	}

	/*
	 * Reads an output file and produces an array that is parallel to the array
	 * of all variables
	 * 
	 * solution status: optimal solution found 
	 * objective value: 	  -261972.299479167 
	 * take-3 154 (obj:0) 
	 * take-7 912.356770833333 (obj:0)
	 * take-8 333.25 (obj:0) 
	 * take-10 6505.5 (obj:0) 
	 * take-11 1180.32552083333 (obj:0) 
	 * costFunc 261972.299479167 (obj:-1)
	 */
	public int[] readResultValues() {

		BufferedReader reader = null;
		try {
			// Extract rules only
			reader = new BufferedReader(new FileReader(getOutputFilename()));
			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
			// skip 2 lines
			for (int i = 0; i < 2; i++) {
				String line = reader.readLine();
				if (line.indexOf("infeasible") > 0 )
					return null;
			}
			int n = 0;
			Var[] vars = problem.getVars();
			if (vars != null)
				n += vars.length;
			VarReal[] varReals = problem.getVarReals();
			if (varReals != null)
				n += varReals.length;
			int[] values = new int[n];
			for (int i = 0; i < n; i++) {
				values[i] = 0; // why 0? Because solution variables with value 0 not shown... 
			}
			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;
				String name = line.substring(0, 9);
				String value = line.substring(10, 53);
				int index = indexOfVariable(name.trim());
				if (index >= 0)
					values[index] = (int) Double.parseDouble(value);
				else {
					String msg = "Error in org.jcp.jsr331.linear.scip: "
							+ "Unknown variable " + name
							+ " among resulting values";
					log(msg);
					throw new RuntimeException(msg);
				}
			}
			return values;
		} catch (Exception ex) {
			log("Error during reading the file " + getOutputFilename());
			ex.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null) {
					// flush and close both "input" and its underlying FileReader
					reader.close();
				}
			} catch (Exception ex) {
				log("Error during closing the file " + getOutputFilename());
				ex.printStackTrace();
				return null;
			}
		}
	}
	
	public HashMap<String, String> readResults() {
		HashMap<String, String> results = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			// Extract rules only
			reader = new BufferedReader(new FileReader(getOutputFilename()));
			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
			// skip 2 lines
			for (int i = 0; i < 2; i++) {
				String line = reader.readLine();
				if (line.indexOf("infeasible") > 0 )
					return null;
			}
			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;
				String name = line.substring(0, 9);
				String value = line.substring(10, 53);
				results.put(name.trim(), value.trim());
			}
			return results;
		} catch (Exception ex) {
			log("Error during reading the file " + getOutputFilename());
			ex.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null) {
					// flush and close both "input" and its underlying FileReader
					reader.close();
				}
			} catch (Exception ex) {
				log("Error during closing the file " + getOutputFilename());
				ex.printStackTrace();
				return null;
			}
		}
	}

}
