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

public class Pricing1 {

	public static void main(String[] argv) {
		try {
			int totalPrice = 6105;
			int[] prices = { 0,185,190,195,200,205,210,220,225,230,235,240,245,250,255,260,265,270,275,280,285 };
			int numberOfPrices = prices.length;
			int[] quantities = { 0,4,4,4,4,4,4,4,4,4,4,3,3,3,3,3,3,3,3,3,3 };
			
			Problem p = ProblemFactory.newProblem("PricingProblem0");
			
			int numberOfItems = 33;
			Var[] vars = p.variableArray("var", 0, numberOfItems, numberOfPrices);
			int[] coefficients = new int[numberOfPrices];
			for (int i = 0; i < numberOfPrices; i++) {
				coefficients[i] = numberOfItems;
			}
			
			Var totalPriceVar = p.scalProd(prices,vars);
			p.add("TotalPriceVar",totalPriceVar);
			p.post(totalPriceVar, "=", totalPrice);
			
			
			p.log(p.getVars());
			Solver solver = p.getSolver();
			solver.traceSolutions(true);
			solver.setMaxNumberOfSolutions(3);
			Solution[] solutions = solver.findAllSolutions();
			for (int i = 0; i < solutions.length; i++) {
				Solution s = solutions[i];
				//s.log();
				p.log("Solution " + (i+1));
				for (int j = 0; j < numberOfPrices; j++) {
					int value = s.getValue("var-"+j);
					if (value != 0)
						p.log("\tprice="+prices[j] + " qty=" + value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

