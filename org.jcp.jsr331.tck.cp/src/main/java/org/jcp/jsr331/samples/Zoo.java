package org.jcp.jsr331.samples;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;


/*
    Problem "Zoo, Buses, and Kids"	
	300 kids need to travel to the London zoo. 
	The school may rent 40 seats and 30 seats buses for 500 and 400  . 
	How many buses of each to minimize cost? 

 */

public class Zoo {
    
    Problem csp;  
    
    public Zoo() {
        csp = ProblemFactory.newProblem("Zoo");
    }


	public void define() {

		Var numberOf30Buses = csp.variable("Number Of 30 seats buses", 0, 30);
		Var numberOf40Buses = csp.variable("Number Of 40 seats buses", 0, 30);
		int[] seats = new int[] { 30, 40 };
		Var[] vars = new Var[] { numberOf30Buses, numberOf40Buses };
		Var totalNumberOfSeats = csp.scalProd("Total number of seats", seats, vars);
		csp.post(totalNumberOfSeats, ">=", 300);
		int[] costs = new int[] { 400, 500 };
		Var totalCost = csp.scalProd("Total cost", costs, vars);
		csp.add(totalCost);
	}
	
	public void solve() { 
        csp.log("=== SOLVE:");
        Solver solver = csp.getSolver(); 
        Solution solution = solver.findOptimalSolution(Objective.MINIMIZE, csp.getVar("Total cost")); 
        if (solution != null)
            solution.log();
        else
            csp.log("No Solutions");
        solver.logStats();
    }

	public static void main(String[] args) {
		Zoo problem = new Zoo();
		problem.define();
		problem.solve();
	}
}
