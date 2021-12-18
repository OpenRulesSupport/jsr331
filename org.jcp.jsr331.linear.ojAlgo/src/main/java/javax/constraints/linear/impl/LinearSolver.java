package javax.constraints.linear.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.constraints.Objective;
//import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.linear.ComparableVariable;
import javax.constraints.impl.search.Solution;

//import org.ojalgo.TestUtils;
//import org.ojalgo.concurrent.ConcurrentUtils;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.MathProgSysModel;
import org.ojalgo.optimisation.Variable;
import org.ojalgo.optimisation.Optimisation.Result;
//import org.ojalgo.optimisation.OptimisationSolver;
//import org.ojalgo.optimisation.OptimisationSolver.Result;
//import org.ojalgo.optimisation.State;
//import org.ojalgo.optimisation.linear.mps.MathProgSysModel;


public class LinearSolver extends javax.constraints.linear.LinearSolver {
	
	static public final String JSR331_LINEAR_SOLVER_VERSION = "ojAlgo (Sep-2009)";
	
	public LinearSolver() {
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
    
	
	/**
	 * 
	 * @param file
	 * @return solution or null
	 */
	public Solution solve(File file) {
		
		javax.constraints.impl.Problem problem = 
				(javax.constraints.impl.Problem)getProblem();
				
		//MathProgSysModel mps = MathProgSysModel.makeFromFile(file);
		//MathProgSysModel mps = MathProgSysModel.make(file);
		//solve  
		//OptimisationSolver os = mps.getDefaultSolver();
		//Result rs = os.solve();
		//Result rs = mps.solve();
		
		//if( rs.getState() == State.INFEASIBLE ) {
		
		
		
		final ExpressionsBasedModel model = MathProgSysModel.make(file).getExpressionsBasedModel();
		if (!model.validate()) {
		    log("ExpressionsBasedModel failed to be validated");
            return null;
        }
		
		model.relax(true);
		 
		Result rs = model.minimise();
		if( !rs.getState().isFeasible() ) {
            log("Linear Solver Found No Solutions");
            return null; //no solution
        }

        Solution solution = new Solution(this, 1);
        
        rs.g

        for (Variable ojVar : model.getVariables()) {
                ojVar.relax();
                String name = ojVar.getName();
                BigDecimal decValue = ojVar.getValue();
                if (decValue == null)
                    continue;
                if (ojVar.isInteger()) {
                    solution.setValue(name, decValue.intValue());
                }
                else { // real?
                    solution.setValueReal(name, decValue.doubleValue());
                }
        }
		
		return solution;
	}
	
	
	public HashMap<String, String> readResults() {
		return null;
	}
	
	public String getCommanLine() {
		return null; // no command lines used
	} 
	

}
