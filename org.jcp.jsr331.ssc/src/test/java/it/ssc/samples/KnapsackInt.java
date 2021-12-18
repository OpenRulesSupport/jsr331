package it.ssc.samples;

import java.util.ArrayList;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.MILP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;

public class KnapsackInt {
	
	ArrayList<Constraint> constraints;
	LinearObjectiveFunction objective;
	
	public void define() throws Exception {
//	    1G + 2S + 3B <= 25
//	    Cost: 15G + 10S + 18B
//      int itemValue[] = { 15, 10, 18 };
	    
        double A[][] = { 
            { 1.0, 2.0, 3.0 }, 
            { 1.0, 0.0, 0.0 }, 
            { 0.0, 1.0, 0.0 }, 
            { 0.0, 0.0, 1.0 } 
        };
        double b[] = { 25.0, 20.0, 30.0, 40.0 };
        double c[] = { 15.0, 10.0, 18.0 };
        
        ConsType[] myrel = { ConsType.LE, ConsType.LE, ConsType.LE, ConsType.INT };
        
        objective = new LinearObjectiveFunction(c, GoalType.MAX);
        
        constraints = new ArrayList<Constraint>();
        for (int i = 0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], myrel[i], b[i]));
        }
	}

	// === Problem Resolution
	public void solve() throws Exception {
        
        MILP milp = new MILP(objective,constraints);
        SolutionType solutionType = milp.resolve();

        if (solutionType == SolutionType.OPTIMUM) {
            Solution solution = milp.getSolution();
            for (Variable var : solution.getVariables()) {
                SscLogger.log("Variable name :" + var.getName() + " value:" + var.getValue());
            }
            SscLogger.log("Best value:" + solution.getOptimumValue());
        }
//        if(solutionType == SolutionType.OPTIMUM) { 
//            Solution sol=milp.getSolution();
//            Solution sol_relax=milp.getRelaxedSolution();
//            Variable[] var_int=sol.getVariables();
//            Variable[] var_relax=sol_relax.getVariables();
//            for(int _i=0; _i< var_int.length;_i++) {
//                SscLogger.log("Variable name :"+var_int[_i].getName() + " value:"+var_int[_i].getValue()+ 
//                              " ["+var_relax[_i].getValue()+"]");
//            }
//            SscLogger.log("o.f. value:"+sol.getOptimumValue() +" ["+sol_relax.getOptimumValue()+"]"); 
//        }
	}

	public static void main(String[] args) throws Exception {
		KnapsackInt problem = new KnapsackInt();
		problem.define();
		problem.solve();
	}

}

