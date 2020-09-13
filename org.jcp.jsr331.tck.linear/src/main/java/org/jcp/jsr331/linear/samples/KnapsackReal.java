package org.jcp.jsr331.linear.samples;

//===============================================
//J A V A  C O M M U N I T Y  P R O C E S S
//
//J S R  3 3 1
//
//TestXYZ Compatibility Kit
//
//================================================

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

import javax.constraints.*;

public class KnapsackReal {
	
	Problem p = ProblemFactory.newProblem("KnapsackReal");
	
	double itemSize[] = { 1, 2, 3 };
    double itemValue[] = { 15, 10, 5 };
    double itemCount[] = { 20, 30, 40 };
    final int knapsackSize = 25;
	    
    public void define() {

        // === Define Variable(s)
        VarReal G = p.variableReal("G",0,itemCount[0]);
        VarReal S = p.variableReal("S", 0, itemCount[1]);
        VarReal B = p.variableReal("B", 0, itemCount[2]);
        VarReal[] vars = new VarReal[] { G, S, B };

        // === Post Constraint(s)
        // 1. 1G + 2S + 3B <= 25
        VarReal scalProd = p.scalProd(itemSize, vars);
        p.add("ScalProd",scalProd);
        p.post(scalProd, "<=", knapsackSize);

        // 2. Cost: 15G + 10S + 5B
        VarReal cost = p.scalProd(itemValue, vars);
        p.add("cost",cost);

    }

	// === Problem Resolution
	public void solve() {
		Solver solver = p.getSolver();
		Solution s = solver.findOptimalSolution(Objective.MAXIMIZE, p.getVarReal("cost"));
		if (s == null)
			p.log("Unable to derive a solution.");
		else {
			p.log("*** Optimal Solution ***");
			p.log("Gold   = " + s.getValueReal("G")); 
			p.log("Silver = " + s.getValueReal("S"));
			p.log("Bronze = " + s.getValueReal("B"));
			p.log("Maximum Profit = " + s.getValueReal("cost"));
		}
		solver.logStats();

	}

	public static void main(String[] args) {

		KnapsackReal problem = new KnapsackReal();
		problem.define();
		problem.solve();

	}

}

