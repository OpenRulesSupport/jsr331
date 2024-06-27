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

public class Pricing3 {

	public static void main(String[] argv) {
		try {
			int totalPrice = 4745;
			int[] prices = { 0,185,190,195,200,205,210,220,225,230,235,240,245,250,255,260,265,270,275,280,285 };
			int numberOfPrices = prices.length;
			int[] quantities = { 0,4,4,4,4,4,4,4,4,4,4,3,3,3,3,3,3,3,3,3,3 };
			
			Problem p = ProblemFactory.newProblem("PricingProblem0");
			
			int priceMax = 0;
			for (int i = 0; i < prices.length; i++) {
				if (prices[i] > priceMax)
					priceMax = prices[i];
			}
			Var[] priceVars = p.variableArray("price", 0, priceMax, numberOfPrices);
			
			int numberOfItems = 33;
			int[] coefficients = new int[numberOfPrices];
			for (int i = 0; i < numberOfPrices; i++) {
				coefficients[i] = numberOfItems;
			}
			
			Var totalPriceVar = p.scalProd(coefficients,priceVars);
			p.add("TotalPriceVar",totalPriceVar);
			p.post(totalPriceVar, "=", totalPrice);
			
			// objective
			Var totalRevenue = p.scalProd(quantities, priceVars);
			p.add("totalRevenue",totalRevenue);
			
			p.log(p.getVars());
			Solver solver = p.getSolver();
			solver.traceSolutions(true);
			Solution solution = solver.findOptimalSolution(Objective.MAXIMIZE, totalRevenue);
			if (solution == null)
				p.log("No solutions");
			else
				solution.log();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

