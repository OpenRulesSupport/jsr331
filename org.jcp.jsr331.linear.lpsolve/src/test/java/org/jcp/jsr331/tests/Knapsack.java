package org.jcp.jsr331.tests;

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

public class Knapsack {
	
	Problem p = ProblemFactory.newProblem("Knapsack");
	
	int itemSize[] = { 1, 2, 3 };
	int itemValue[] = { 15, 10, 5 };
	final int knapsackSize = 25;

	public void define() {

		// === Define Variable(s)
		Var G = p.variable("G", 0, 20);
		Var S = p.variable("S", 0, 30);
		Var B = p.variable("B", 0, 40);
		Var[] vars = new Var[] { G, S, B };

		// === Post Constraint(s)
		// 1. 1G + 2S + 3B <= 25
		Var scalProd = p.scalProd(itemSize, vars);
		p.post(scalProd, "<=", knapsackSize);

		// 2. Cost: 15G + 10S + 5B
		Var cost = p.scalProd(itemValue, vars);
		p.add("cost",cost);

	}

	// === Problem Resolution
	public void solve() {
		// --- Get the Solver
		Solver solver = p.getSolver();

		// --- Find the Solution

		/*
		 * // --- Find all Solutions Solution[] solutions =
		 * solver.findAllSolutions(); for (int i = 0; i < solutions.length; i++)
		 * { solutions[i].log(); }
		 */

		// --- Find Exact Solution
		Solution s = solver.findOptimalSolution(Objective.MAXIMIZE, p.getVar("cost"));
		if (s == null)
			p.log("Unable to derive a solution.");
		else {
			p.log("*** Optimal Solution ***");
			p.log("Gold = " + s.getValue("G"));
			p.log("Silver = " + s.getValue("S"));
			p.log("Bronze = " + s.getValue("B"));
			p.log("Maximum Profit = " + s.getValue("cost"));
		}
		// --- Display Stats Log (optional)
		solver.logStats();

	}

	public static void main(String[] args) {

		Knapsack problem = new Knapsack();
		problem.define();
		problem.solve();

	}

}
