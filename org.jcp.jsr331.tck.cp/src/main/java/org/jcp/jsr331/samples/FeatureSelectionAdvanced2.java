package org.jcp.jsr331.samples;

import javax.constraints.*;

public class FeatureSelectionAdvanced2 {
    public static void main(String[] args) {
        Problem p = ProblemFactory.newProblem("Feature Selection Advanced 2");

        // Data
        int[] costs = {5000, 4000, 3000, 2000, 1000, 1000, 1000, 1000, 1000, 1000};
        int costMin = 1000;
        int costMax = 5000+10;
        int[] values = {7, 6, 5, 4, 3, 2, 1, 1, 1, 1};
        int budget = 10000;

        int wValue = 2;   // Weight for value
        int wCount = 8;   // Weight for quantity
        int n = costs.length;

        // Decision variables: x[i] = 1 if feature i is selected
        Var[] x = p.variableArray("x", 0, 1, n);

        // Features 3 and 4 can be chosen only together
        p.post(x[2], "=", x[3]);
        

        // Feature 2 cannot combine with Feature 3
        p.post(x[1].plus(x[2]), "<=", 1);


        // Only one of the features 8, 9, and 10 can be selected. 
        // Otherwise, the cost of each of these features will be increased by 10%.
        Var[] costVars = p.variableArray("costVars", 0, costMax, costs.length);
        Constraint c1 = p.linear(x[7].plus(x[8]).plus(x[9]), "<=", 1);
        for(int i=0; i<costs.length; i++) {
        	if (i == 7 || i == 8 || i == 9) {
        		int increasedCost = (int)(costs[i]*1.1);
        		Constraint c2 = p.linear(costVars[i], "=", x[i].multiply(increasedCost));
        		p.postIfThen(c1, c2);
        	}
        	else  {
        		p.post(costVars[i], "=", x[i].multiply(costs[i]));
        	}
        }
        Var totalCostVar = p.sum("Total Cost", costVars);
        
        // Objective: weighted sum
        Var valueObjective = p.scalProd(values, x);
        Var quantityObjective = p.sum(x);
        Var balancedObjective = valueObjective.multiply(wValue).plus(quantityObjective.multiply(wCount));
        
        // If 5 or more features are selected, 5% discount is provided
        p.post(totalCostVar.percent(95), "<=", budget);
        p.post(valueObjective,">=", 15);
        

        // Solve
        Solver solver = p.getSolver();
        //Solution solution = solver.findOptimalSolution(Objective.MAXIMIZE, balancedObjective);
        Solution solution = solver.findOptimalSolution(Objective.MINIMIZE, totalCostVar);

        // Print results
        if (solution != null) {
            System.out.println("Selected Features:");
            int totalCost = 0;
            int totalValue = 0;
            int featureCount = 0;
            int count = 0;
            for (int i = 0; i < n; i++) {
                if (solution.getValue("x-" + i) == 1) {
                    System.out.println("Feature " + (i + 1) + 
                    		           " (Cost: " + costs[i] + ", Value: " + values[i] + ")");
                    totalCost += costs[i];
                    totalValue += values[i];
                    featureCount++;
                    count++;
                }
            }
            System.out.println("Total Cost: " + totalCost);
            System.out.println("Total Value: " + totalValue);
            System.out.println("Feature Count: " + featureCount);
        } else {
            System.out.println("No solution found.");
        }
    }
}

