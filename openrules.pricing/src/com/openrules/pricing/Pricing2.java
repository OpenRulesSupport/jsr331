package com.openrules.pricing;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;

/**
 * <p>
 * Title: <b>Knapsack problem</b>
 * </p>
 * <p>
 * Description: We have a knapsack with a fixed capacity (an integer) and a
 * number of items. Each item has an associated weight (an integer) and an
 * associated value (another integer). The problem consists of filling the
 * knapsack without exceeding its capacity, while maximizing the overall value
 * of its contents
 * </p>
 */

public class Pricing2 {

	public static void main(String[] argv) {
			
		int[] prices = { 185,190,195,200,205,210,220,225,230,235,240,245,250,255,260,265,270,275,280,285 };
		int numberOfPrices = prices.length;
		int[] quantities = { 4,4,4,4,4,4,4,4,4,4,3,3,3,3,3,3,3,3,3,3 };
		
		int[] revenues = new int[numberOfPrices];
		for (int i = 0; i < numberOfPrices; i++) {
			revenues[i] = quantities[i]*prices[i];
		}
		
		int numberOfItems = 33;
		
		long start = System.currentTimeMillis();
		for (int totalPrice = 4745; totalPrice <= 7390; totalPrice += 500) {
			long start1 = System.currentTimeMillis();
			try {		
				Problem p = ProblemFactory.newProblem("PricingProblem-"+totalPrice);
				p.log("\ntotal price = "+totalPrice);
				Var[] vars = p.variableArray("var", 0, numberOfItems, numberOfPrices);
				
				Var totalPriceVar = p.scalProd(prices,vars);
				p.add("TotalPri",totalPriceVar);
				p.post(totalPriceVar, "=", totalPrice);
				
				Var totalRevenueVar = p.scalProd(revenues,vars);
				p.add("TotalRev",totalRevenueVar);
				
				//p.log(p.getVars());
				Solver solver = p.getSolver();
				solver.traceSolutions(true);
				solver.setMaxNumberOfSolutions(3);
				solver.traceSolutions(true);
				p.log("Maximize " + totalRevenueVar);
				Solution solution = solver.findOptimalSolution(Objective.MAXIMIZE, totalRevenueVar);
				if (solution == null)
					p.log("No solutions");
				else {
					//solution.log();
					p.log("Solution found in "+ (System.currentTimeMillis() - start1)	+ " ms");
					int revenue = 0;
					int price = 0;
					for (int j = 0; j < numberOfPrices; j++) {
						int value = solution.getValue("var-"+j);
						if (value != 0) {
							p.log("\titem=" + j + " price="+prices[j] + " qty=" + value);
							price += prices[j]*value;
							revenue += (prices[j]*quantities[j]*value);
						}
					}
					p.log("\tTotal Price=" + price + " Total Revenue="+revenue);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Total elapsed time for all runs: " + (System.currentTimeMillis() - start)	+ " ms");
	}
}

