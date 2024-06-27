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

public class PricingSolver {
	
	static public String VERSION = "** OpenRules Pricing Solver version 1.5.0 (build 03/02/2016) **";
	
	public static void main(String[] argv) {
		long start = System.currentTimeMillis();
		int[] prices = { 185,190,195,200,205,210,220,225,230,235,240,245,250,255,260,265,270,275,280,285 };
		int[] quantities = { 4,4,1,4,4,5,4,4,4,2,3,3,3,3,3,3,2,3,3,3 }; // added 0,-1 instead of 3,3
		int numberOfItems = 33;
		
		int totalRuns = 0;
		int totalSolutions = 0;
		
//		int[] prices = { 185,190,195 };
//		int[] quantities = { 4,3,5};
//		int numberOfItems = 3;
//		for (int totalPrice = 570; totalPrice <= 570; totalPrice += 500) {
		
		PricingItem[] items = new PricingItem[numberOfItems];
		for (int i = 0; i < numberOfItems; i++) {
			items[i] = new PricingItem(i,prices,quantities);
		}
		
//		for (int totalPrice = 4745; totalPrice <= 7390; totalPrice += 5) {
		for (int totalPrice = 4745; totalPrice <= 7390; totalPrice += 500) {
//		for (int totalPrice = 7210; totalPrice <= 7225; totalPrice += 5) {
			totalRuns++;
			System.out.println("\ntotal price = "+totalPrice);
			PricingSolver pricingSolver = new PricingSolver(totalPrice,prices,items);
			pricingSolver.setTimeLimitForOneSolutionMills(6000);
			if (pricingSolver.solve() != null)
				totalSolutions++;
		}
		System.out.println("Found " + totalSolutions + " out of " + totalRuns + " runs");
		System.out.println("Total elapsed time for all runs: " + (System.currentTimeMillis() - start)	+ " ms");
	}
	
	int[] prices;
	PricingItem[] items;
	int totalPrice;
	Problem p;
	boolean comments;
	int timeLimitForOneSolutionMills;
	
	
	public PricingSolver(int totalPrice, int[] prices, PricingItem[] items) {
		System.out.println(VERSION);
		this.prices = prices;
		this.items = items;
		this.totalPrice = totalPrice;
		comments = true;
		timeLimitForOneSolutionMills = -1; // no limits;
		
		long start = System.currentTimeMillis();
		try {
			p = ProblemFactory.newProblem("PricingProblem");
			Var[] revenueVars = new Var[items.length];
			for (int i = 0; i < items.length; i++) {
				items[i].init(p);
				revenueVars[i] = items[i].getRevenueVar();
			}
			
			// we may use the same maximal array of prices for all items 
			// but make quantities for absent prices equal to zero
//			Var[] vars = p.variableArray("var", 0, items.length-1, prices.length);
//			Var[] selectedPriceVars = new Var[prices.length];
//			Var[][] itemSelectedPriceVars = new Var[prices.length][items.length]; 
//			for (int priceIndex = 0; priceIndex < prices.length; priceIndex++) {
//				itemSelectedPriceVars[priceIndex] = new Var[items.length];
//				for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
//					itemSelectedPriceVars[priceIndex][itemIndex] = items[itemIndex].getSelectedPriceVar(priceIndex);
//				}
//				selectedPriceVars[priceIndex] = p.sum("Slct"+priceIndex,itemSelectedPriceVars[priceIndex]);
//			}
//			Var totalPriceVar = p.scalProd("TotalPri",prices,selectedPriceVars);
//			p.post(totalPriceVar, "=", totalPrice);
			
			Var[] priceVars = new Var[items.length];
			for (int i = 0; i < items.length; i++) {
				priceVars[i] = items[i].getPriceVar();
			}
			
			Var totalPriceVar = p.sum("TotalPri",priceVars);
			p.post(totalPriceVar, "=", totalPrice);
			
			p.sum("TotalRev",revenueVars);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (comments)
			p.log("Problem defined in "+ (System.currentTimeMillis() - start)	+ " ms");
	}
	
	public PricingSolution solve() {
		long start = System.currentTimeMillis();
		Solution solution = null;
		try {		
				//p.log(p.getVars());
				Solver solver = p.getSolver();
				Var totalRevenueVar = p.getVar("TotalRev");
				if (comments)
					p.log("Maximize " + totalRevenueVar);
				if (timeLimitForOneSolutionMills > 0)
					solver.setTimeLimitGlobal(timeLimitForOneSolutionMills);
				solution = solver.findOptimalSolution(Objective.MAXIMIZE, totalRevenueVar);
				if (solution == null && comments)
					p.log("No solutions");
				else {
					solution.log(8);
					int calcTotalPrice = 0;
					boolean success = true;
					for (int i = 0; i < items.length; i++) {
						PricingItem item = items[i];
						if (!item.saveSolution(solution)) // item is not priced
							success = false;
						else
							calcTotalPrice += item.getRecommendedPrice();
						if (comments)
							item.log(); 
						/*
						for (int j = 0; j < prices.length; j++) {
							Var selectedPriceVar = item.getSelectedPriceVar(j);
							String name = item.getNumber()+"p"+j;
							int value = solution.getValue(name);
							if (comments)
								p.log("\t\tprice="+prices[j]+" selected=" + value);
						}
						*/
					}
					if (success && calcTotalPrice == totalPrice) {
						int totalVarPrice = solution.getValue("TotalPri");
						int revenue = solution.getValue("TotalRev");
						if (comments)
							p.log("\tTotal Price=" + totalPrice + " TotalVarPrice=" + totalVarPrice + " calcTotalPrice=" + calcTotalPrice + " Total Revenue="+revenue);
						PricingSolution pricingSolution = new PricingSolution(totalPrice,items,revenue);
						if (comments)
							p.log("Problem solved in "+ (System.currentTimeMillis() - start)	+ " ms");
						return pricingSolution;
					}
					else {
						p.log("Infeasible Solution ignored.");
						return null;
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		p.log("Problem not solved in "+ (System.currentTimeMillis() - start)	+ " ms");
		return null;
	}

	public boolean isComments() {
		return comments;
	}

	public void setComments(boolean comments) {
		this.comments = comments;
	}

	public int getTimeLimitForOneSolutionMills() {
		return timeLimitForOneSolutionMills;
	}

	public void setTimeLimitForOneSolutionMills(int timeLimitForOneSolutionMills) {
		this.timeLimitForOneSolutionMills = timeLimitForOneSolutionMills;
	}
	
	

}

