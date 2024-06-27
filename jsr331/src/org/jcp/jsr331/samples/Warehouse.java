package org.jcp.jsr331.samples;


import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.impl.Problem;

/**
 * Let's assume that a company plans to create a network of warehouses to supply
 * its existing stores. Let's suppose in addition that the company already has a
 * number of suitable sites for building warehouses and thus wants to know
 * whether or not to create a warehouse on each such site. For each site chosen,
 * the company wants to determine the optimal capacity for the warehouse. The
 * company considers the average merchandise turnover as identical from one
 * store to another. However, the distance among locations and the
 * transportation infrastructure both lead to varying transportation costs for
 * each pair consisting of a store and a warehouse.
 * <p>
 * The objective is to minimize total cost by determining for each warehouse
 * which stores should be suppled by it and what its capacity should be. The
 * total is then the sum of supply costs plus the costs of building each
 * warehouse.
 */

public class Warehouse extends Problem {

	Var[] suppliers;
	Var[] open;
	Var[] costs;
	Var totalCost;

	public Warehouse(int nbStores, int nbSuppliers, int buildingCost,
			         int[] capacities, int[][] costMatrix) {

		suppliers = new Var[nbStores];
		open = new Var[nbSuppliers];
		costs = new Var[nbStores]; 
		try {
			suppliers = variableArray("supplier",0, nbSuppliers - 1, nbStores);
			open = variableArray("open",0, 1, nbSuppliers);
			int maxCost = 0;
			int maxSumCost = 0;
			for (int i = 0; i < nbStores; i++) {
				for (int j = 0; j < costMatrix[i].length; j++) {
					if (maxCost < costMatrix[i][j])
						maxCost = costMatrix[i][j];
				}
				costs[i] = variable("cost-"+i,0,maxCost);
				postElement(costMatrix[i], suppliers[i], "=", costs[i]);
				postElement(open, suppliers[i], "=", 1).post();
				maxSumCost += maxCost;
			}
			// cardinality constraint
			for (int j = 0; j < nbSuppliers; j++) {
				postCardinality(suppliers, j, "<=", capacities[j]);
			}
			// totalCost= sum(cost) + sum(open)*buildCost
			Var sumCost = variable("sumCost",0, maxSumCost);
			post(costs,"=",sumCost).post();
			Var sumOpen = variable("sumOpen", 0, nbSuppliers);
			post(open, "=", sumOpen).post();
			totalCost = sumOpen.multiply(buildingCost).plus(sumCost);
			totalCost.setName("TotalCost");
			add(totalCost);

		} catch (Exception e) {
			log("Error in prolem definition: " + e);
			throw new RuntimeException("Cannot create a problem");
		}
	}

	public Solution solve() {
		Solver solver = getSolver();
		
		Var[] vars = new Var[suppliers.length+open.length+costs.length];
		int v = 0;
		for (int i = 0; i < open.length; i++) {
			vars[v++] = open[i];
		}
		for (int i = 0; i < suppliers.length; i++) {
			vars[v++] = suppliers[i];
		}
		for (int i = 0; i < costs.length; i++) {
			vars[v++] = costs[i];
		}
		solver.getSearchStrategy().setVars(vars);
		
		Solution solution = solver.findOptimalSolution(totalCost);
		return solution;
	}

	public void printSolution(Solution solution) {
		if (solution == null) {
			log("NO Solutions");
			return;
		}

		solution.log();

		log("suppliers");
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < suppliers.length; i++){
			//int value = solution.getValue("");
			str.append(suppliers[i].getValue() + " ");
		}
		log(str.toString());

		log("costs");
		str = new StringBuffer();
		for (int i = 0; i < costs.length; i++)
			str.append(costs[i].getValue() + " ");
		log(str.toString());

		log("open");
		str = new StringBuffer();
		for (int i = 0; i < open.length; i++)
			str.append(open[i].getValue() + " ");
		log(str.toString());

		log(totalCost.toString());
	}

	public static void main(String[] args) {

		long startMS = System.currentTimeMillis();

		// ========= Problem Definition ==================
		int nbStores = 10;
		int nbSuppliers = 5;
		int buildingCost = 30;
		int[] capacities = new int[] { 1, 4, 2, 1, 3 };
		int[][] costMatrix = new int[][] { { 20, 24, 11, 25, 30 },
				{ 28, 27, 82, 83, 74 }, { 74, 97, 71, 96, 70 },
				{ 2, 55, 73, 69, 61 }, { 46, 96, 59, 83, 4 },
				{ 42, 22, 29, 67, 59 }, { 1, 5, 73, 59, 56 },
				{ 10, 73, 13, 43, 96 }, { 93, 35, 63, 85, 46 },
				{ 47, 65, 55, 71, 95 } };

		Warehouse wh = new Warehouse(nbStores, nbSuppliers, buildingCost,
				capacities, costMatrix);

		// ========= Problem Resolution ==================
		Solution solution = wh.solve();

		wh.printSolution(solution);

		long finishMS = System.currentTimeMillis();
		System.out.println("Runtime: " + (finishMS - startMS) + " Millis");

	}

}
