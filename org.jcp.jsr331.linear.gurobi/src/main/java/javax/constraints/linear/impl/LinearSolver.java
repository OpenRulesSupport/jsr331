package javax.constraints.linear.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import javax.constraints.Objective;

public class LinearSolver extends javax.constraints.linear.LinearSolver {

	static public final String JSR331_LINEAR_SOLVER_VERSION = "GUROBI v.5.0.1";

	public LinearSolver() {
	}

	public String getCommanLine() {
		String exe = System.getProperty(LP_SOLVER_EXE);
		if (exe == null) {
			exe = "gurobi_cl";
		}
		String options = System.getProperty(LP_SOLVER_OPTIONS);
		if (options == null) {
			options = "Threads=1";
		}
		return exe + " " + options + " ResultFile=" + getOutputFilename() + " " + getInputFilename();
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
//	 * 
//	 * A Gurobi solution (SOL) file is used to output a solution vector. 
//	 * It can be written (using GRBwrite, for example) whenever a solution is available.
//	 * The file consists of variable-value pairs, each on its own line. 
//	 * The file contains one line for each variable in the model. 
//	 * The following is a simple example:
//	 * # Solution file
//	 * x  1.0
//	 * y  0.5
//	 * z  0.2
//	 */
//	public int[] readResultValues() {
//
//		BufferedReader reader = null;
//		try {
//			// Extract rules only
//			reader = new BufferedReader(new FileReader(getOutputFilename()));
//			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
//			int[] values = new int[problem.getVars().length];
//			int n = 0;
//			for(;;) {
//				String line = reader.readLine();
//				while(line != null && line.startsWith("#")) {
//					//log(line);
//					line = reader.readLine();
//				}
//				if (line == null)
//					break;
//				String[] two = line.split(" ");
//				String name = two[0];
//				values[n] = (int)Double.parseDouble(two[1]);
//				n++;
//			}
//			if (n == 0) {
//				String msg = "Infeasible problem.";
//				log(msg);
//				throw new RuntimeException(msg);
//			}
//			if (values.length != n) {
//				String msg = "Error in org.jcp.jsr331.linear.gurobi: " +
//			        "Number of variable is not equal to the number of resulting values";
//				log(msg);
//				throw new RuntimeException(msg);
//			}
//			return values;
//		} catch (Exception ex) {
//			//log("Error during reading the file " + getOutputFilename());
//			//ex.printStackTrace();
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
	 * 
	 * A Gurobi solution (SOL) file is used to output a solution vector. 
	 * It can be written (using GRBwrite, for example) whenever a solution is available.
	 * The file consists of variable-value pairs, each on its own line. 
	 * The file contains one line for each variable in the model. 
	 * The following is a simple example:
	 * # Solution file
	 * x  1.0
	 * y  0.5
	 * z  0.2
	 */
	public HashMap<String, String> readResults() {
		HashMap<String, String> results = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			// Extract rules only
			reader = new BufferedReader(new FileReader(getOutputFilename()));
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
