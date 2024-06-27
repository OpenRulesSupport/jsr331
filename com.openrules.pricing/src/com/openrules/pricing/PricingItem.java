package com.openrules.pricing;

import java.util.Arrays;

import javax.constraints.Problem;
import javax.constraints.Solution;
import javax.constraints.Var;

public class PricingItem {
	int number;
	int[] prices;
	int[] quantities;
	int selectedPriceIndex;
	
	// Temporary variables
	Problem problem;
	Var[] selectedPriceVars; // one var [0..1] for each price
	Var sumOfSelectedPriceVars;
	Var revenueVar;
	Var priceVar;
	
	public PricingItem(int number, int[] prices, int[] quantities) {
		this.number = number;
		//this.prices = prices;
		this.prices = new int[prices.length];
		this.quantities = new int[quantities.length];
		for (int i = 0; i < prices.length; i++) {
			if (quantities[i] < 0) {
				this.quantities[i] = 0;
			}
			else {
				this.quantities[i] = quantities[i];
			}
			this.prices[i] = prices[i];
		}
		
	}

	public int getNumber() {
		return number;
	}

	public void init(Problem problem) {
		this.problem = problem;
		selectedPriceIndex = -1;
		selectedPriceVars = new Var[prices.length];//problem.variableArray("i" + number + "p", 0, 1, getPrices().length);
		for (int p = 0; p < prices.length; p++) {
			String name = ""+number+"p"+p;
			if (quantities[p] == 0) {
				selectedPriceVars[p] = problem.variable(0, 0); // to make sure that at least one real price is selected
			}
			else {
				selectedPriceVars[p] = problem.variable(0, 1);
			}	
			problem.add(name,selectedPriceVars[p]);
		}
		sumOfSelectedPriceVars = problem.sum("Sum"+number,selectedPriceVars);  
		problem.post(sumOfSelectedPriceVars,"=",1); // one and only one price should be selected for the item
		
		int[] revenues = new int[prices.length];
		for (int i = 0; i < prices.length; i++) {
			if (quantities[i] <= 0) {
				revenues[i] = 0;
			}
			else {
				revenues[i] = quantities[i]*prices[i];
			}
		}
		
		
		revenueVar = problem.scalProd("Rev"+number,revenues,selectedPriceVars);
		priceVar = problem.scalProd("Price"+number, prices, selectedPriceVars);
		//problem.log(this.toString());
	}
	
	public Var getPriceVar() {
		return priceVar;
	}
	
	public Var getSelectedPriceVar(int priceIndex) {
		return selectedPriceVars[priceIndex];
	}

	public int[] getPrices() {
		return prices;
	}

	public void setPrices(int[] prices) {
		this.prices = prices;
	}

	public int[] getQuantities() {
		return quantities;
	}

	public void setQuantities(int[] quantities) {
		this.quantities = quantities;
	}

	public Var[] getSelectedPriceVars() {
		return selectedPriceVars;
	}

	public void setSelectedPriceVars(Var[] priceVars) {
		this.selectedPriceVars = priceVars;
	}

	public Var getRevenueVar() {
		return revenueVar;
	}
	
	public boolean saveSolution(Solution solution) {
		for (int p = 0; p < prices.length; p++) {
			String name = selectedPriceVars[p].getName();
			if (solution.getValue(name) == 1) {
				if (quantities[p] >= 0)
					selectedPriceIndex = p;
				else 
					selectedPriceIndex = -1;
				return true;
			}
		}
		problem.log("== Not priced " + toString());
		return false;
	}
	
	public int getRecommendedPrice() {
		if (selectedPriceIndex < 0)
			return 0;
		return prices[selectedPriceIndex];
	}
	
	public int getSelectedPriceIndex() {
		return selectedPriceIndex;
	}

	public void setSelectedPriceIndex(int selectedPriceIndex) {
		this.selectedPriceIndex = selectedPriceIndex;
	}

	public void log() {
		String text = "Item "+number; 
		if (selectedPriceIndex < 0)
			text += " - no price selected";
		else {
			text += " selectedPrice=" + prices[selectedPriceIndex]
					+ " qty=" + quantities[selectedPriceIndex]
				+ " revenue=" + prices[selectedPriceIndex]*quantities[selectedPriceIndex]
				+ " selectedPriceIndex="+selectedPriceIndex;
		}
		problem.log(text);
	}

	@Override
	public String toString() {
		return "PricingItem [number=" + number + ", prices="
				+ Arrays.toString(prices) + ", quantities="
				+ Arrays.toString(quantities) + ", selectedPriceIndex="
				+ selectedPriceIndex 
				+ ", sumOfSelectedPriceVars=" + sumOfSelectedPriceVars
				+ ", revenueVar=" + revenueVar + "]"
				+ ", selectedPriceVars=" + Arrays.toString(selectedPriceVars);
	}

}
