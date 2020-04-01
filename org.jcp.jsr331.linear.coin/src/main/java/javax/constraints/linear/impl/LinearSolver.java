package javax.constraints.linear.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import javax.constraints.Objective;

public class LinearSolver extends javax.constraints.linear.LinearSolver {

	static public final String JSR331_LINEAR_SOLVER_VERSION = "COIN v.1.8.0";

	public LinearSolver() {
	}

	public String getCommanLine() {
		String exe = System.getProperty(LP_SOLVER_EXE);
		if (exe == null) {
			exe = "clp";
			//exe = "c:/lp/coin/bin/clp";
		}
		String options = System.getProperty(LP_SOLVER_OPTIONS);
		if (options == null) {
			options = "-maximize -dualsimplex";
		}
		return exe + " " + getInputFilename() + " " + options + " -solution " + getOutputFilename();
//		return "clp " + getInputFilename() + " -maximize -dualsimplex -solution "+ getOutputFilename();
	}

	public String getVersion() {
		return JSR331_LINEAR_SOLVER_VERSION;
	}

	/**
	 * minimizes by default
	 */
	public Objective getDefaultOptimizationObjective() {
		return Objective.MAXIMIZE;
	}

	/*
	 * Reads an output file and produces an array that is parallel to the array
	 * of all variables
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
//				String value = line.substring(18,32);
//				values[n] = (int)Double.parseDouble(value);
//				n++;
//			}
//			if (n == 0) {
//				String msg = "infeasible problem.";
//				log(msg);
//				throw new RuntimeException(msg);
//			}
//			if (values.length != n) {
//				String msg = "Error in org.jcp.jsr331.linear.coin: " +
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
	
	public HashMap<String, String> readResultsNew() {
		if (isInfeasible())
			return null;
		HashMap<String, String> results = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			// Extract rules only
			reader = new BufferedReader(new FileReader(getOutputFilename()));
			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
			int n = 0;
			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (n == 0) { // infeasible or optimal
					if (line.startsWith("infeasible"))
						return null;
					n++;
					continue;
				}
				if (n == 1) { // skip "Objective value           -1715"
					n++;
					continue;
				}
//				log(line);
				String[] elements = split(line);
				String name = elements[1];
				String value = elements[2];
//				log("\t read " + name + " " + value);
				results.put(name,value);
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
	
	public boolean isInfeasible() {
		BufferedReader reader = null;
		try {
			// Extract rules only
			reader = new BufferedReader(new FileReader(getLogFilename()));
			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.contains("infeasible"))
						return true;
			}
			return false;
		} catch (Exception ex) {
			log("Error during reading the file " + getLogFilename());
			ex.printStackTrace();
			return true;
		} finally {
			try {
				if (reader != null) {
					// flush and close both "input" and its underlying FileReader
					reader.close();
				}
			} catch (Exception ex) {
				log("Error during closing the file " + getLogFilename());
				ex.printStackTrace();
				return true;
			}
		}
	}
	
	public HashMap<String, String> readResults() { // old version
		HashMap<String, String> results = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			// Extract rules only
			reader = new BufferedReader(new FileReader(getOutputFilename()));
			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;
				String[] elements = split(line);
				String name = elements[1];
				String value = elements[2];
				results.put(name,value);
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
