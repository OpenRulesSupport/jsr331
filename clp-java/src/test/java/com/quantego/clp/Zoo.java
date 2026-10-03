package com.quantego.clp;

import com.quantego.clp.CLP.STATUS;

public class Zoo {
	
	int seats[] = new int[] { 30, 40 };
	int[] costs = new int[] { 400, 500 };
	
	CLP solver;
	CLPVariable numberOf30Buses;
	CLPVariable numberOf40Buses;
	CLPExpression totalSeats;
	CLPObjective cost; 
	CLPConstraint constraint;
	
	    
    public Zoo() {
        solver = new CLP();
    }

    public void define() {

        // === Define Variable(s)
    	numberOf30Buses = solver.addVariable().name("NumberOf30Buses").lb(0).ub(11);
    	numberOf40Buses = solver.addVariable().name("numberOf40Buses").lb(0).ub(11);

		// === Post Constraint(s)
    	totalSeats = solver.createExpression().add(seats[0],numberOf30Buses).add(seats[1],numberOf40Buses);
    	constraint = totalSeats.geq(300).name("Seats>=300");

		// Cost
		//Cost = solver.addVariable().name("Cost");
        cost = solver.createExpression().add(costs[0],numberOf30Buses).add(costs[1],numberOf40Buses).asObjective();
	}

	// === Problem Resolution
	public void solve() {
	    STATUS status = solver.minimize();
        if (!status.equals(STATUS.OPTIMAL)) {
            System.out.println("CLP cannot find an optimal solution");
        }

        System.out.println("*** Optimal Solution ***");
        System.out.println("NumberOf30Buses = " + numberOf30Buses.getSolution());
        System.out.println("NumberOf40Buses = " + numberOf40Buses.getSolution());
        System.out.println(constraint.getSolution());
        System.out.println("Cost = " + solver.getObjectiveValue());
	}

	public static void main(String[] args) {
		System.out.println("*** Create CSP ***");
		Zoo problem = new Zoo();
		System.out.println("*** Define CSP ***");
		problem.define();
		System.out.println("*** Solve CSP ***");
		problem.solve();

	}

}
