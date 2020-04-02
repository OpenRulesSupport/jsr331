package javax.constraints.linear;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import javax.constraints.Objective;
import javax.constraints.OptimizationStrategy;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.impl.AbstractConstrainedVariable;
import javax.constraints.impl.search.Solver;
import javax.management.RuntimeErrorException;

abstract public class LinearSolverReal extends Solver {
	
	public static String LP_SOLVER_EXE = "LP_SOLVER_EXE";
	public static String LP_SOLVER_OPTIONS = "LP_SOLVER_OPTIONS";
	public static String OUTPUT_FOLDER = "results/";
	
	public LinearSolverReal() {  
		super();
	}
	
	public Solution findOptimalSolution(Objective objectiveDirection, Var objectiveVar) {
		File file = generateMpsFile(objectiveDirection,objectiveVar);
		return solve(file);
	}
	
	public Solution findOptimalSolution(Objective objectiveDirection, VarReal objectiveVar) {
		File file = generateMpsFile(objectiveDirection,objectiveVar);
		return solve(file);
	}
	
	public Solution findOptimalSolution(Objective objective, Var objectiveVar, OptimizationStrategy optStrategy) {
		return findOptimalSolution(objective, objectiveVar);
	} 
	
	/**
	 * 
	 * @return Objective.MINIMIZE or Objective.MAXIMIZE based on a particular solver
	 */
	abstract public Objective getDefaultOptimizationObjective(); 
	
	public File generateMpsFile(Objective objectiveDirection, Var objectiveVar) {
		javax.constraints.impl.Problem problem = 
				(javax.constraints.impl.Problem)getProblem();
		problem.add(objectiveVar);
		return generateMpsFile(objectiveDirection, (AbstractConstrainedVariable)objectiveVar);
	}
	
	public File generateMpsFile(Objective objectiveDirection, VarReal objectiveVar) {
		javax.constraints.impl.Problem problem = 
				(javax.constraints.impl.Problem)getProblem();
		problem.add(objectiveVar);
		return generateMpsFile(objectiveDirection, (AbstractConstrainedVariable)objectiveVar);
	}
	
	public File generateMpsFile(Objective objectiveDirection, AbstractConstrainedVariable objectiveVar) {
		javax.constraints.impl.Problem problem = 
				(javax.constraints.impl.Problem)getProblem();
		int direction = 1; // default
		if (!objectiveDirection.equals(getDefaultOptimizationObjective())) {
			direction = -1;
		}
		
		File file = new File(getInputFilename());
		MpsGenerator mpsGenerator = 
				new MpsGenerator(problem, direction, objectiveVar,file);
		mpsGenerator.setIntegerVariablesOnly(true);
		mpsGenerator.generate();
		return file;
	}
	
	public String getInputFilename() {
		return OUTPUT_FOLDER + getProblem().getName() + ".mps";
	}
	
	public String getOutputFilename() {
		return OUTPUT_FOLDER + getProblem().getName() + ".sol";
	}
	
	public String getLogFilename() {
		return getOutputFilename() + ".log"; 
	}
	
	protected String preProcess() {
		return null;
	}
	
	/**
	 * 
	 * @param file
	 * @return solution or null
	 */
	public Solution solve(File file) {
		
		String inputfile = preProcess();

		String command = getCommanLine();
		try {
			// log("Executed command: "+command);
			StreamGobbler.execute(command, inputfile, getLogFilename());
			// Runtime rt = Runtime.getRuntime();
			// Process proc = rt.exec(command);
			// //int exitVal = proc.waitFor();
			// //log("Process exit value: " + exitVal);
		} catch (Throwable t) {
			log("Cannot execute command: " + command);
			t.printStackTrace();
			throw new RuntimeException(t);
		}
		
//		int[] values = readResultValues();
//		if (values != null && values.length > 0) {
//			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
//			AbstractConstrainedVariable[] vars = problem.getVars();
//			for (int i = 0; i < values.length; i++) {
//				javax.constraints.impl.AbstractConstrainedVariable var = (javax.constraints.impl.AbstractConstrainedVariable) vars[i];
//				var.setValue(values[i]);
//			}
//			Solution solution = new javax.constraints.impl.search.Solution(
//						this, 1);
//			return solution;
//			
//		} 
//		else
//			return null;
		
		HashMap<String,String> results = readResults();
		if (results != null) {
			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
			String name = "";
			try {
				Var[] vars = problem.getVars();
				if (vars != null)
				for (int i = 0; i < vars.length; i++) {
					javax.constraints.impl.Var var = (javax.constraints.impl.Var) vars[i];
					name = var.getName();
					String stringValue = results.get(name);
					if (stringValue != null) {
						int value = (int) Double.parseDouble(stringValue);
						var.setValue(value);
					}
					else {
						var.setValue(0); // linear solvers do not display zero values in solutions
					}
				}
				VarReal[] varReals = problem.getVarReals();
				if (varReals != null)
				for (int i = 0; i < varReals.length; i++) {
					javax.constraints.impl.VarReal varReal = (javax.constraints.impl.VarReal) varReals[i];
					name = varReal.getName();
					String stringValue = results.get(name);
					if (stringValue != null) {
						double value = Double.parseDouble(stringValue);
						varReal.setValue(value);
					}
					else {
						varReal.setValue(0.0);
					}
				}
			} catch (Exception e) {
				String msg = "Unknown variable " + name + " while reading results in org.jcp.jsr331.linear";
				log(msg);
				throw new RuntimeException(msg);
			}
			Solution solution = new javax.constraints.impl.search.Solution(this, 1);
			return solution;
		} 
		else
			return null;
	}
	
	public int indexOfVariable(String name) {
		javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
		Var[] vars = problem.getVars();
		if (vars != null)
		for (int i = 0; i < vars.length; i++) {
			javax.constraints.impl.AbstractConstrainedVariable abstractConstrainedVariable = (javax.constraints.impl.AbstractConstrainedVariable) vars[i];
			if (name.equals(abstractConstrainedVariable.getName()))
				return i;
		}
		return -1;
	}
	
	abstract public String getVersion(); 
	
	abstract public String getCommanLine(); 
	
	/**
	 * Reads an output file and produces a HashMap which elements contain pairs:
	 * - String varName
	 * - String value
	 * @return a HashMap with the resulting values for all problem variables or null
	 */
	abstract public HashMap<String, String> readResults();
	
//	/**
//	 * Reads an output file and produces an array that is parallel to the array
//	 * of all variables
//	 * @return integer array of resulting values for all problem variables
//	 */
//	abstract public int[] readResultValues();
	
	public static String[] split(String line) {
		StringBuffer buf = new StringBuffer();
		line = line.trim();
		// skip spaces
		int nSpaces = 0; 
		for(int i=0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (Character.isWhitespace(c)) {
				if (nSpaces > 0)
					continue;
				else {
					nSpaces = 1;
					buf.append(" ");
				}
			}
			else {
				buf.append(c);
				nSpaces = 0;
			}
		}
		String result = buf.toString();
		return result.split(" ");
	}

}
