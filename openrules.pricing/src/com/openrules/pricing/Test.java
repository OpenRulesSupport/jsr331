package com.openrules.pricing;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;

/**
 * <p>
 * Title: <b>Pricing problem</b>
 * </p>
 * <p>
 * Description: We have a set of items that were previously sold under certain conditions:
 * price, quantity, total price of similar items in the set. The problem consists of finding 
 * optimal price combinations while maximizing the overall revenue
 * </p>
 */

public class Test {
	
	public static void main(String[] argv) {
		long start = System.currentTimeMillis();
		int[] prices = { 185,190,195,200,205,210,220,225,230,235,240,245,250,255,260,265,270,275,280,285 };
		int[] quantities = { 4,4,1,4,4,5,4,4,4,2,3,3,3,3,3,3,2,3,3,3 };
		int numberOfItems = 33;
		int totalPrice = 6245; 
		
//		int[] prices = { 185,190,195 };
//		int[] quantities = { 4,3,5};
//		int numberOfItems = 3;
//		int totalPrice = 570; 
		
//		int[] prices = { 185,195 };
//		int[] quantities = { 4,5};
//		int numberOfItems = 3;
//		int totalPrice = 380; 
		
		PricingItem[] items = new PricingItem[numberOfItems];
		for (int i = 0; i < numberOfItems; i++) {
			items[i] = new PricingItem(i,prices,quantities);
		}
		
		
		System.out.println("\ntotal price = "+totalPrice);
		PricingSolver pricingSolver = new PricingSolver(totalPrice,prices,items);
//		pricingSolver.setTimeLimitForOneSolutionMills(6000);
		PricingSolution solution = pricingSolver.solve();
		if (solution != null)
			System.out.println("Solution Found "); // + solution.toString());
		System.out.println("Total elapsed time for all runs: " + (System.currentTimeMillis() - start)	+ " ms");
	}
		
}

