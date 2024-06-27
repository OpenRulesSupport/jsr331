package org.jcp.jsr331.linear.samples;

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

public class KnapsackProblem {

	public static void main(String[] argv) {
		try {
			int[] capacities = { 18209, 7692, 1333, 924, 26638, 61188, 13360 };
			int numberOfResources = capacities.length;
			int[] values = { 96, 76, 56, 11, 86, 10, 66, 86, 83, 12, 9, 81 };
			int numberOfItems = values.length;
			
			int[][] use = { 
					{ 19, 1, 10, 1, 1, 14, 152, 11, 1, 1, 1, 1 },
					{ 0, 4, 53, 0, 0, 80, 0, 4, 5, 0, 0, 0 },
					{ 4, 660, 3, 0, 30, 0, 3, 0, 4, 90, 0, 0 },
					{ 7, 0, 18, 6, 770, 330, 7, 0, 0, 6, 0, 0 },
					{ 0, 20, 0, 4, 52, 3, 0, 0, 0, 5, 4, 0 },
					{ 0, 0, 40, 70, 4, 63, 0, 0, 60, 0, 4, 0 },
					{ 0, 32, 0, 0, 0, 5, 0, 3, 0, 660, 0, 9 } 
			};

			Problem p = ProblemFactory.newProblem("KnapsackProblem");
			// variables
			int capacityMax = 0;
			for (int i = 0; i < capacities.length; i++) {
				if (capacities[i] > capacityMax)
					capacityMax = capacities[i];
			}
			Var[] take = p.variableArray("take", 0, capacityMax, numberOfItems);
			
			// capacity due constraints
			for (int r = 0; r < numberOfResources; r++) {
				p.post(use[r], take, "<=", capacities[r]);
			}
			
			// costFunction
			Var costFunc = p.scalProd(values, take);
			p.add("costFunc", costFunc);

			p.log(p.getVars());
			Solver solver = p.getSolver();
			Solution solution = solver.findOptimalSolution(Objective.MAXIMIZE,costFunc);
//			p.post(costFunc,">",261900);
//			Solution solution = solver.findSolution();
			if (solution == null) {
				System.out.println("The problem has no optimal solution!");
				return;
			}
			p.log("Optimized cost function value: " +
			solution.getValue("costFunc") );
			solution.log();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
Solve problem using GLPK v.4-47
MPS file generated in 22 msec
Execute command: glpsol --model .\KnapsackProblemWithSelectors.mps --mps -w .\KnapsackProblemWithSelectors.txt >.\KnapsackProblemWithSelectors.txt.log
Exit value: 0
Execution time: 234 msec
Optimized cost function value: 261922
Solution #1:
	 take-0[0] take-1[0] take-2[0] take-3[154] take-4[0] take-5[0] take-6[0] take-7[913] take-8[333]
	 take-9[0] take-10[6499] take-11[1180] costFunc[261922]
---	 
Solve problem using GUROBI v.5.0.1
MPS file generated in 4 msec
Execute command: gurobi_cl Threads=1 ResultFile=.\KnapsackProblemWithSelectors.sol .\KnapsackProblemWithSelectors.mps >.\KnapsackProblemWithSelectors.sol.log
Exit value: 0
Execution time: 49 msec
Optimized cost function value: 261922
Solution #1:
	 take-0[0] take-1[0] take-2[0] take-3[154] take-4[0] take-5[0] take-6[0] take-7[913] take-8[333]
	 take-9[0] take-10[6499] take-11[1180] costFunc[261922]
---
Solve problem using COIN v.1.3.3
MPS file generated in 5 msec
Execute command: clp .\KnapsackProblemWithSelectors.mps -maximize -dualsimplex -solution .\KnapsackProblemWithSelectors.sol >.\KnapsackProblemWithSelectors.sol.log
Exit value: 0
Execution time: 47 msec
Optimized cost function value: 261972
Solution #1:
	 take-0[0] take-1[0] take-2[0] take-3[154] take-4[0] take-5[0] take-6[0] take-7[912] take-8[333]
	 take-9[0] take-10[6505] take-11[1180] costFunc[261972]
 */ 
