package com.quantego.clp;

import com.quantego.clp.CLP.STATUS;

/***************************************************************************\
 * 
 * ------------  Knapsack Problem -----------
 * 
 * The knapsack problem or rucksack problem is a problem in combinatorial 
 * optimization: Given a set of items, each with a weight and a value, 
 * determine the number of each item to include in a collection so that 
 * the total weight is less than or equal to a given limit and the total 
 * value is as large as possible. It derives its name from the problem 
 * faced by someone who is constrained by a fixed-size knapsack and must 
 * fill it with the most useful items.
 *                                       -- Wikipedia
 * 
 * Knapsack Max Unit : 10
 * 
 * Items   Weight Value   Total Items
 * ---------------------------------
 * Gold    1      15       20
 * Silver  2      10       30
 * Bronze  3      5        40
 * 
 * 
 *      1G +  2S + 3B <= 25  // to find total Weight
 *     15G + 10S + 5B  =  P  // to find total Profit
 * 
 * 
 \***************************************************************************/

public class Knapsack {
	
	int itemSize[] = { 1, 2, 3 };
	int itemValue[] = { 15, 10, 5 };
	int itemCount[] = { 20, 30, 40 };
	int knapsackSize = 25;
	
	CLP solver;
	CLPVariable G;
	CLPVariable S;
	CLPVariable B;
	CLPVariable Cost;   
	    
    public Knapsack() {
        solver = new CLP();
    }

    public void define() {

        // === Define Variable(s)
        G = solver.addVariable().name("G").lb(0).ub(itemCount[0]);
        S = solver.addVariable().name("S").lb(0).ub(itemCount[1]);
        B = solver.addVariable().name("B").lb(0).ub(itemCount[2]);

		// === Post Constraint(s)
		// 1G + 2S + 3B <= 25
		solver.createExpression().add(itemSize[0],G).add(itemSize[1],S).add(itemSize[2],B).leq(knapsackSize);

		// Cost: 15G + 10S + 5B
		//Cost = solver.addVariable().name("Cost");
		solver.createExpression().add(itemValue[0],G).add(itemValue[1],S).add(itemValue[2],B).asObjective();
	}

	// === Problem Resolution
	public void solve() {
	    STATUS status = solver.maximize();
        if (!status.equals(STATUS.OPTIMAL)) {
            System.out.println("CLP cannot find an optimal solution");
        }

        System.out.println("*** Optimal Solution ***");
        System.out.println("Gold = " + G.getSolution());
        System.out.println("Silver = " + S.getSolution());
        System.out.println("Bronze = " + B.getSolution());
        System.out.println("Maximum Profit = " + solver.getObjectiveValue());
	}

	public static void main(String[] args) {

		Knapsack problem = new Knapsack();
		problem.define();
		problem.solve();

	}

}
