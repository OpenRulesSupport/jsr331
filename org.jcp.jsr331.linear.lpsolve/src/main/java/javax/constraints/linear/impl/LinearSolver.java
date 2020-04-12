package javax.constraints.linear.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import javax.constraints.Objective;

public class LinearSolver extends javax.constraints.linear.LinearSolver {

	static public final String JSR331_LINEAR_SOLVER_VERSION = "LpSolve v.5.5.2";

	public LinearSolver() {
	}

	public String getCommanLine() {
		String exe = System.getProperty(LP_SOLVER_EXE);
		if (exe == null) {
			exe = "lp_solve";
		}
		String options = System.getProperty(LP_SOLVER_OPTIONS);
		if (options == null) {
			options = "";
		}
		return exe + " -min " + options + " -fmps " + getInputFilename() + " -S2";
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

//	/*
//	 * Reads an output file and produces an array that is parallel to the array
//	 * of all variables
//	 * -----
//	 * 
//	 * Value of objective function: -261972.29948053
//	 * 
//	 * Actual values of the variables:
//	 * 
//	 * take-0                          0
//	 * take-1                          0
//	 * ... 
//	 * take-11                   1180.33
//	 * costFunc                   261972
//	 */
//	public int[] readResultValues() {
//
//		BufferedReader reader = null;
//		try {
//			// Extract rules only
//			reader = new BufferedReader(new FileReader(getLogFilename()));
//			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
//			int[] values = new int[problem.getVars().length];
//			int n = 0;
//			// Skip 4 lines
//			for (int i = 0; i < 4; i++) {
//				String line = reader.readLine();
//			}
//			for(;;) {
//				String line = reader.readLine();
//				if (line == null)
//					break;
//				String value = line.substring(10,33);
//				values[n] = (int)Double.parseDouble(value);
//				n++;
//			}
//			if (n == 0) {
//				String msg = "infeasible problem.";
//				log(msg);
//				throw new RuntimeException(msg);
//			}
//			if (values.length != n) {
//				String msg = "Error in org.jcp.jsr331.linear.lpsolve: " +
//			        "Number of variables " + n + 
//			        " is not equal to the number of resulting values " + values.length;
//				log(msg);
//				throw new RuntimeException(msg);
//			}
//			return values;
//		} catch (Exception ex) {
//			return null;
//		} finally {
//			try {
//				if (reader != null) {
//					// flush and close both "input" and its underlying FileReader
//					reader.close();
//				}
//			} catch (Exception ex) {
//				log("Error during closing the file " + getOutputFilename());
//				ex.printStackTrace();
//				return null;
//			}
//		}
//	}

	/*
	 * Reads an output file and produces a hash map of all variables and their values
	 * -----
	 * 
	 * Value of objective function: -261972.29948053
	 * 
	 * Actual values of the variables:
	 * 
	 * take-0                          0
	 * take-1                          0
	 * ... 
	 * take-11                   1180.33
	 * costFunc                   261972
	 */
	public HashMap<String, String> readResults() {
		HashMap<String, String> results = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(getLogFilename()));
			// Skip 4 lines
			for (int i = 0; i < 4; i++) {
				String line = reader.readLine();
			}
			int n = 0;
			for (;;) {
				String line = reader.readLine();
				while(line != null && line.startsWith("#")) {
					//log(line);
					line = reader.readLine();
				}
				if (line == null)
					break;
				String[] elements = split(line);
				String name = elements[0];
				String value = elements[1];
				results.put(name,value);
				n++;
			}
			if (n == 0) {
				String msg = "Infeasible problem.";
				log(msg);
				return null;
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
