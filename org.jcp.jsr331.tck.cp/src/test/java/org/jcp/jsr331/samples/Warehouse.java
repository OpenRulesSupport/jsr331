package org.jcp.jsr331.samples;


import javax.constraints.*;

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
 * <partition>
 * The objective is to minimize total cost by determining for each warehouse
 * which stores should be suppled by it and what its capacity should be. The
 * total is then the sum of supply costs plus the costs of building each
 * warehouse.
 */

public class Warehouse {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");

	Var[] suppliers;
	Var[] open;
	Var[] costs;
	Var totalCost;

	public Warehouse(int nbStores, int nbSuppliers, int buildingCost,
			         int[] capacities, int[][] costMatrix) {
		try {
			suppliers = p.variableArray("supplier",0, nbSuppliers - 1, nbStores);
			open = p.variableArray("open",0, 1, nbSuppliers);
			costs = new Var[nbStores];
			int maxCost = 0;
			int maxSumCost = 0;
			for (int i = 0; i < nbStores; i++) {
				for (int j = 0; j < costMatrix[i].length; j++) {
					if (maxCost < costMatrix[i][j])
						maxCost = costMatrix[i][j];
				}
				costs[i] = p.variable("cost-"+i,0,maxCost);
				p.postElement(costMatrix[i], suppliers[i], "=", costs[i]);
				p.postElement(open, suppliers[i], "=", 1);
				maxSumCost += maxCost;
			}
			// cardinality constraint
			for (int j = 0; j < nbSuppliers; j++) {
				p.postCardinality(suppliers, j, "<=", capacities[j]);
			}
			// totalCost= sum(cost) + sum(open)*buildCost
			Var sumCost = p.sum(costs);
			sumCost.setName("sumCost");
			p.post(sumCost,"<=",maxSumCost);
			Var sumOpen = p.sum(open);
			sumOpen.setName("sumOpen");
			p.post(sumOpen,"<=",nbSuppliers);
			totalCost = sumOpen.multiply(buildingCost).plus(sumCost);
			totalCost.setName("TotalCost");
			p.add(totalCost);

		} catch (Exception e) {
			p.log("Error in prolem definition: " + e);
			throw new RuntimeException("Cannot create a problem");
		}
	}

	public Solution solve() {
		Solver solver = p.getSolver();
		
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
			p.log("NO Solutions");
			return;
		}
		solution.log();

		StringBuffer str = new StringBuffer();
		str.append("suppliers: ");
		for (int i = 0; i < suppliers.length; i++){
			int value = solution.getValue("supplier-"+i);
			str.append(value + " ");
		}
		p.log(str.toString());

		str = new StringBuffer();
		str.append("costs: ");
		for (int i = 0; i < costs.length; i++) {
			int value = solution.getValue("cost-"+i);
			str.append(value + " ");
		}
		p.log(str.toString());

		str = new StringBuffer();
		str.append("open: ");
		for (int i = 0; i < open.length; i++) {
			int value = solution.getValue("open-"+i);
			str.append(value + " ");
		}
		p.log(str.toString());

		p.log("TotalCost: " +solution.getValue("TotalCost"));
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
