package org.jcp.jsr331.samples;

import javax.constraints.*;

public class FeatureSelectionJSR331 {
    public static void main(String[] args) {
        Problem p = ProblemFactory.newProblem("Feature Selection");

        // Data
        int[] costs = {5000, 4000, 3000, 2000, 1000, 1000, 1000, 1000, 1000, 1000};
        int[] values = {7, 6, 5, 4, 3, 2, 1, 1, 1, 1};
        int budget = 10000;

        int wValue = 5;   // Weight for value
        int wCount = 5;   // Weight for quantity
        int n = costs.length;

        // Decision variables: x[i] = 1 if feature i is selected
        Var[] x = p.variableArray("x", 0, 1, n);

        p.post(p.scalProd(costs, x), "<=", budget);

        // Objective: weighted sum
        Var valueObjective = p.scalProd(values, x);
        Var quantityObjective = p.sum(x);
        Var balancedObjective = valueObjective.multiply(wValue).plus(quantityObjective.multiply(wCount));

        // Solve
        Solver solver = p.getSolver();
        Solution solution = solver.findOptimalSolution(Objective.MAXIMIZE, balancedObjective);

        if (solution != null) {
            System.out.println("Selected Features:");
            int totalCost = 0;
            int totalValue = 0;
            for (int i = 0; i < n; i++) {
                if (solution.getValue("x-" + i) == 1) {
                    System.out.println("Feature " + (i + 1) + 
                    		           " (Cost: " + costs[i] + ", Value: " + values[i] + ")");
                    totalCost += costs[i];
                    totalValue += values[i];
                }
            }
            System.out.println("Total Cost: " + totalCost);
            System.out.println("Total Value: " + totalValue);
        } else {
            System.out.println("No solution found.");
        }
    }
}

