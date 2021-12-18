package javax.constraints.linear;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.UUID;

import javax.constraints.Objective;
import javax.constraints.OptimizationStrategy;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.impl.AbstractConstrainedVariable;
import javax.constraints.impl.search.Solver;

abstract public class LinearSolver extends Solver {
	
	public static String LP_SOLVER_EXE = "LP_SOLVER_EXE";
	public static String LP_SOLVER_OPTIONS = "LP_SOLVER_OPTIONS";
	public static String OUTPUT_FOLDER = "results/";
	
	private String correlationID = UUID.randomUUID().toString();
	
	public LinearSolver() {  
		super();
	}
	
	public Solution findOptimalSolution(Objective objectiveDirection, Var objectiveVar) {
		File file = generateMpsFile(objectiveDirection,objectiveVar);
		int timeoutMilliSeconds = getTimeLimitGlobal();
		if (timeoutMilliSeconds > 0)
			return solve(file,timeoutMilliSeconds);
		else
			return solve(file);
	}
	
	public Solution findOptimalSolution(Objective objectiveDirection, VarReal objectiveVar) {
		File file = generateMpsFile(objectiveDirection,objectiveVar);
		int timeoutMilliSeconds = getTimeLimitGlobal();
		if (timeoutMilliSeconds > 0)
			return solve(file,timeoutMilliSeconds);
		else
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
	
	public String uniqueName(String ext) {
	    File file = new File("/tmp");
        File outFolder = new File(file,OUTPUT_FOLDER);
        if ( !outFolder.exists() ) {
            outFolder.mkdirs();
        }
	    return new File(outFolder,getProblem().getName()+"_"+correlationID + ext).getAbsolutePath();
	}
	
	public String getInputFilename() {
	    
		//return OUTPUT_FOLDER, + uniqueName() + ".mps";
        return uniqueName(".mps");
	}
	
	public String getOutputFilename() {
		return uniqueName(".sol");
	}
	
	public String getLogFilename() {
		return uniqueName(".log"); 
	}
	
	protected String preProcess() {
		return null;
	}
	
	public Solution solve(File file) {
		return solve(file,-1); // no timeout
	}
	
	/**
	 * 
	 * @param file a file
	 * @param timeoutMilliSeconds timeout in milliseconds
	 * @return solution or null
	 */
	public Solution solve(File file, int timeoutMilliSeconds) {
		
		String inputfile = preProcess();

		String command = getCommanLine();
		try {
			// log("Executed command: "+command);
			boolean result = StreamGobbler.execute(command, inputfile, getLogFilename(), timeoutMilliSeconds);
			// Runtime rt = Runtime.getRuntime();
			// Process proc = rt.exec(command);
			// //int exitVal = proc.waitFor();
			// //log("Process exit value: " + exitVal);
			if (result == false)
				return null;
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
		
		try {
            FileReader reader = new FileReader(getOutputFilename());
        } catch (FileNotFoundException e1) {
            log("This solver cannot find a solution");
            return null;
        }
		HashMap<String,String> results = readResults();
		if (results != null) {
			javax.constraints.impl.Problem problem = (javax.constraints.impl.Problem) getProblem();
			//String name = "";
			String id = "";
			try {
				Var[] vars = problem.getVars();
				if (vars != null)
				for (int i = 0; i < vars.length; i++) {
					javax.constraints.impl.Var var = (javax.constraints.impl.Var) vars[i];
					//name = var.getName();
					//String stringValue = results.get(name);
					id = var.getId();
					String stringValue = results.get(id);
//					log("Save result: var id="+id + " value="+stringValue);
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
					//name = varReal.getName();
					//String stringValue = results.get(name);
					id = varReal.getId();
					String stringValue = results.get(id);
					if (stringValue != null) {
						double value = Double.parseDouble(stringValue);
						varReal.setValue(value);
					}
					else {
						varReal.setValue(0.0);
					}
				}
			} catch (Exception e) {
				String msg = "Unknown variable " + id + " while reading results in org.jcp.jsr331.linear";
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
