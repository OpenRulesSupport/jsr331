package javax.constraints.impl.search;

import java.util.ArrayList;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Objective;
import javax.constraints.OptimizationStrategy;
import javax.constraints.Problem;
import javax.constraints.ProblemState;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.VarSet;
import javax.constraints.impl.LinearIntConstraint;
import javax.constraints.impl.search.AbstractSolver;
import javax.constraints.impl.search.StrategyLog;
import javax.constraints.linear.NotImplementedException;

public class Solver extends AbstractSolver {
    
    double[][] matrix;
    double[] rhs;
    double[] costCoefficients;
	
	public Solver() {
		this(null);
	}
	
	public Solver(Problem problem) {
		super(problem);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)problem;
		ArrayList<Var> intVars = p.getVarArray();
		ArrayList<VarReal> realVars = p.getVarRealArray();
		
		ArrayList<ConstrainedVariable> allVars = new ArrayList<ConstrainedVariable>(intVars.size()+realVars.size());
		
		for(LinearIntConstraint c : p.getIntegerConstraints() ) {
		    Var[] vars = c.getVars();
		    
		}
	}
	
	public Solution findOptimalSolution(Objective objectiveDirection, Var objectiveVar) {
//        File file = generateMpsFile(objectiveDirection,objectiveVar);
//        int timeoutMilliSeconds = getTimeLimitGlobal();
//        if (timeoutMilliSeconds > 0)
//            return solve(file,timeoutMilliSeconds);
//        else
//            return solve(file);
	    return null;
    }
    
    public Solution findOptimalSolution(Objective objectiveDirection, VarReal objectiveVar) {
//        File file = generateMpsFile(objectiveDirection,objectiveVar);
//        int timeoutMilliSeconds = getTimeLimitGlobal();
//        if (timeoutMilliSeconds > 0)
//            return solve(file,timeoutMilliSeconds);
//        else
//            return solve(file);
        return null;
    }
    
    public Solution findOptimalSolution(Objective objective, Var objectiveVar, OptimizationStrategy optStrategy) {
        return findOptimalSolution(objective, objectiveVar);
    } 
	

//	@Override
//	public Solution findOptimalSolution(Objective objective, AbstractConstrainedVariable objectiveVar) {
//		return super.findOptimalSolution(objective, objectiveVar);
//	}

	@Override
	public void trace(Var var) {
		throw new NotImplementedException("trace(AbstractConstrainedVariable var)");
	}

	@Override
	public void trace(Var[] vars) {
		throw new NotImplementedException("trace(AbstractConstrainedVariable[] vars)");
	}

	@Override
	public void trace(VarSet setVar) {
		throw new NotImplementedException("trace(VarSet setVar)");
	}

	@Override
	public SearchStrategy newSearchStrategy() {
		String msg = "newSearchStrategy() - not required by linear solvers";
		//throw new NotImplementedException(msg);
		log(msg);
		return new StrategyLog(this, "LogStrategy");
	}

	@Override
	public Solution findSolution(ProblemState restoreOrNot) {
		throw new NotImplementedException("findSolution(ProblemState restoreOrNot)");
	}

	@Override
	public boolean applySolution(Solution solution) {
		throw new NotImplementedException("applySolution(Solution solution)");
	}

}
