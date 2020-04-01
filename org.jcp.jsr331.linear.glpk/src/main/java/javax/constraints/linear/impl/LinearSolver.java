package javax.constraints.linear.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.constraints.Objective;
import javax.constraints.Var;
import javax.constraints.VarReal;

public class LinearSolver extends javax.constraints.linear.LinearSolver {

	static public final String JSR331_LINEAR_SOLVER_VERSION = "GLPK v.4-65";
 
	public LinearSolver() {
	} 

	public String getCommanLine() {
		String exe = System.getProperty(LP_SOLVER_EXE);
		if (exe == null) {
			exe = "glpsol";
		}
		String options = System.getProperty(LP_SOLVER_OPTIONS);
		if (options == null) {
			options = "";
		}
		return exe + " " + options + " --model " + getInputFilename() + " --freemps -o " + getOutputFilename();
//		return "glpsol --model " + getInputFilename() + " --mps -w " + getOutputFilename();   
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
	 * The file created by the routine glpsol contains the following
	 * information: 
	 * m n 
	 * stat obj_val 
	 * r_val[1] 
	 * . . . 
	 * r_val[m] 
	 * c_val[1] 
	 * . . .
	 * c_val[n] 
	 * where: m is the number of rows (auxiliary variables); 
	 * n is the number of columns (structural variables); 
	 * stat is the solution status
	 * (GLP_UNDEF=1, GLP_FEAS=2, GLP_NOFEAS=4, or GLP_OPT=5); 
	 * obj_val is the objective value; 
	 * r_val[i], i = 1; : : : ;m, is the value of i-th row;
	 * c_val[j], j = 1; : : : ; n, is the value of j-th column.
	 */
//	public int[] readResultValues() {
//
//		BufferedReader reader = null;
//		try {
//			// Extract rules only
//			reader = new BufferedReader(new FileReader(getOutputFilename()));
//			String line = null; // not declared within while loop
//			// m n
//			// m is the number of rows (auxiliary variables)
//			// n is the number of columns (structural variables)
//			line = reader.readLine();
//			String[] two = line.split(" ");
//			int numberOfRows = Integer.parseInt(two[0]);
//			int numberOfColumns = Integer.parseInt(two[1]);
//
//			// stat obj_val
//			// stat is the solution status (GLP_UNDEF=1, GLP_FEAS=2,
//			// GLP_NOFEAS=4, or GLP_OPT=5);
//			// obj_val is the objective value;
//			line = reader.readLine(); 
//			two = line.split(" ");
//			int solutionStatus = Integer.parseInt(two[0]);
//			if (solutionStatus != 5) {
//				if (solutionStatus == 1)
//					log("Solution status: GLP_UNDEF - infeasible problem");
//				else if (solutionStatus == 4)
//					log("Solution status: GLP_NOFEAS");
//				else
//					log("GLPK Cannot find a solution");
//				return null;
//			}
//			// GLP_OPT=5
//			int objectiveValue = Integer.parseInt(two[1]);
//
//			// read rows: r_val[i], i = 1; : : : ;m, is the value of i-th row;
//			for (int i = 0; i < numberOfRows; i++) {
//				reader.readLine();
//			}
//			// read columns: c_val[j], j = 1; : : : ; n, is the value of j-th
//			// column
//			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
//			int[] values = new int[problem.getVars().length];
//			if (values.length != numberOfColumns) {
//				String msg = "Error in org.jcp.jsr331.linear.glpk: see LinearSolver: vars.length != numberOfColumns";
//				log(msg);
//				throw new RuntimeException(msg); 
//			}
//			for (int i = 0; i < numberOfColumns; i++) {
//				line = reader.readLine();
//				values[i] = Integer.parseInt(line);
//			}
//			return values;
//		} catch (Exception ex) {
//			String msg = "Infeasible problem.";
//			log(msg);
//			return null;
//		} finally {
//			try {
//				if (reader != null) {
//					// flush and close both "input" and its underlying
//					// FileReader
//					reader.close();
//				}
//			} catch (Exception ex) {
//				log("Error during closing the file " + getOutputFilename());
//				ex.printStackTrace();
//				return null;
//			}
//		}
//	}
	
	public HashMap<String, String> readResults() {
		HashMap<String, String> results = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			// Extract rules only
			reader = new BufferedReader(new FileReader(getOutputFilename()));
			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
			String line = null; 
//			Problem:    InsideOutsideProduction
//			Rows:       12
//			Columns:    12
//			Non-zeros:  30
//			Status:     OPTIMAL
//			Objective:  _OBJ_ = -420 (MINimum)
			line = reader.readLine(); // skip Problem name
			
			line = reader.readLine(); // Rows:       12
			String[] elements = split(line.substring(12));
			int numberOfRows = Integer.parseInt(elements[0]);
			
			line = reader.readLine(); // Columns:    12
			elements = split(line.substring(12));
			int numberOfColumns = Integer.parseInt(elements[0]);
			
			line = reader.readLine(); // skip Non-zeros:  30
			
			line = reader.readLine(); // Status:     OPTIMAL or INTEGER OPTIMAL
			log(line);
			if (line.indexOf("OPTIMAL") < 0) {
				log("Infeasible problem: " + line);
				return null;
			}
			
			line = reader.readLine(); // Objective:  _OBJ_ = -420 (MINimum)
		
			line = reader.readLine(); // skip empty line
//			   No.   Row name   St   Activity     Lower bound   Upper bound    Marginal
//			   ------ ------------ -- ------------- ------------- ------------- -------------
//			        1 _OBJ_        B           -420                             
//			        2 C1           NS             0             0             =             1 
			line = reader.readLine(); // skip row title
			line = reader.readLine(); // skip row ----
			for (int i = 0; i < numberOfRows; i++) {
				reader.readLine();// skip rows
			}
			
			line = reader.readLine(); // skip empty line
//			   No. Column name  St   Activity     Lower bound   Upper bound    Marginal
//			   ------ ------------ -- ------------- ------------- ------------- -------------
//			        1 P1Inside     NL             0             0           100           0.4 
//			        2 P2Inside     B             50             0           200 
//                  ...
			line = reader.readLine(); // skip column title
			line = reader.readLine(); // skip column ----
			for (int i = 0; i < numberOfColumns; i++) {
				line = reader.readLine(); // next column
				elements = split(line);
				String name = elements[1].trim();
				String value = elements[3].trim();
				results.put(name.trim(), value.trim());
			}
			
		} catch (Exception ex) {
			log("*** Error during reading the file " + getOutputFilename());
			ex.printStackTrace();
			return null;
		} finally {
			try {
				if (reader != null) {
					// flush and close both "input" and its underlying FileReader
					reader.close();
				}
			} catch (Exception ex) {
				log("*** Error during closing the file " + getOutputFilename());
				ex.printStackTrace();
				return null;
			}
		}
		return results;
	}
}
