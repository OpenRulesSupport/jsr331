package org.jcp.jsr331.samples;

/*
A manufacturer has to decide how much of each demanded product should be produced
internally and how much should be bought and resold. The company wants to minimize the
overall production cost of meeting the demand. <br>
Let's assume the company wants to produce 3 products that may consume 2 resources in the
company's factories. <br>
Here the problem data:</p>
<table border="1" width="100%" bordercolor="#000000">
<tr>
<td width="16%" align="center">Products</td>
<td width="16%" align="center">Demand</td>
<td width="17%" align="center">Inside Cost</td>
<td width="17%" align="center">Outside Cost</td>
<td width="17%" align="center">Resource-1 Consumption</td>
<td width="17%" align="center">Resource-2 Consumption</td>
</tr>
<tr>
<td width="16%" align="center">product-1</td>
<td width="16%" align="center">100</td>
<td width="17%" align="center">0.6</td>
<td width="17%" align="center">0.8</td>
<td width="17%" align="center">0.5</td>
<td width="17%" align="center">0.2</td>
</tr>
<tr>
<td width="16%" align="center">product-2</td>
<td width="16%" align="center">200</td>
<td width="17%" align="center">0.8</td>
<td width="17%" align="center">0.9</td>
<td width="17%" align="center">0.4</td>
<td width="17%" align="center">0.4</td>
</tr>
<tr>
<td width="16%" align="center">product-3</td>
<td width="16%" align="center">300</td>
<td width="17%" align="center">0.3</td>
<td width="17%" align="center">0.4</td>
<td width="17%" align="center">0.3</td>
<td width="17%" align="center">0.6</td>
</tr>
</table>
<p>Demand - customers' demand in units of each product. </p>
<p>Inside cost - cost of the product unit produced in the company's factories.</p>
<p>Outside cost - cost of the product unit bought from another company.</p>
<p>Products produced by inside production have to satisfy resource constraints; each
product consumes a certain amount of each resource.  The company manager must take
into account the capacity constraints:  resource-1 has capacity 20; resource-2 has
capacity 40.</p>
*/

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;

public class InsideOutsideProductionInteger {

	public InsideOutsideProductionInteger() {
	}

	public static void main(String[] args) {
		try {

			//products nomenclature
		    String[] products = {"P1", "P2", "P3"};
			//resources nomenclature
			String[] resources = {"R1", "R2"};
//			int KLUSKI=0, CAPPELINI = 1, FETTUCINI = 2;
//			int FLOUR = 0, EGGS = 1;
			//consumption matrix: consumption[i][j] is a consumption of resources[i] for products[j]
			int[][] consumption = {
//					{0.5, 0.4, 0.3},
//			        {0.2, 0.4, 0.6}
			        {5, 4, 13},
			        {2, 4, 18}
			};
			// insideCost[i] is a production cost of product[i]
			int[] insideCosts = {6, 8, 3};
			// outsideCost[i] is a cost of unit of product[i] being produced outside the company
			int[] outsideCosts = {8, 9, 4};
			// customers demands for product[i]
			int[] demand = {100, 200, 120};

			//amount of resources available
			int[] capacity = {20, 40};
			
			Problem csp = ProblemFactory.newProblem("InsideOutsideProduction");

			Var[] insideVars = new Var[] {
					csp.variable(products[0] + "Inside", 0, demand[0]),
					csp.variable(products[1] + "Inside", 0, demand[1]),
					csp.variable(products[2] + "Inside", 0, demand[2])
			};

			Var[] outsideVars = new Var[] {
					csp.variable(products[0] + "Outside", 0, demand[0]),
					csp.variable(products[1] + "Outside", 0, demand[1]),
					csp.variable(products[2] + "Outside", 0, demand[2])
			};

			//an objective function - the total cost of production
			Var insideCost = csp.scalProd(insideCosts, insideVars); 
			insideCost.setName("inCost");
		    csp.add(insideCost);
		    Var outsideCost = csp.scalProd(outsideCosts, outsideVars); 
			outsideCost.setName("outCost");
		    csp.add(outsideCost);
		    Var totalCost = csp.sum(insideCost,outsideCost);
		    totalCost.setName("TotalCost");
		    csp.add(totalCost); 

//		    //capacity constraints
//			for (int r=0; r < resources.length; r++){
//			      p.post(consumption[r],insideVars,"<=",capacity[r]);
//			}
			
	        //capacity constraints
            for (int r=0; r < resources.length; r++) {
                Var[] consumedResourceCapacities = new Var[products.length];
                for(int p = 0; p < products.length; p++) {
                    consumedResourceCapacities[p] = insideVars[p].multiply(consumption[r][p]);
                    consumedResourceCapacities[p].setName("consumedResourceCapacities["+r+"]["+p+"]");
                    csp.add(consumedResourceCapacities[p]);
                }
                Var resourceConsumption = csp.sum(consumedResourceCapacities);
                resourceConsumption.setName("resourceConsumption["+r+"]");
                csp.add(resourceConsumption);
                csp.post(resourceConsumption, "<=", capacity[r]);
            }

			//meeting customers demand constraints
			for (int d = 0; d < products.length; d++){
				Var sum = csp.sum(insideVars[d],outsideVars[d]);
				sum.setName("demand"+(d+1));
				csp.add(sum);
			    csp.post(sum,">=",demand[d]); 
			}

			csp.log(csp.getVars());
			csp.log(csp.getVarReals());
			Solver solver = csp.getSolver(); 
			Solution solution = solver.findOptimalSolution(Objective.MINIMIZE,totalCost);
			if (solution == null) {
				System.out.println("The problem has no solution!");
				return;
			}
			
			solution.log(6);
			csp.log("Optimized cost function value: "
					+ solution.getValue("TotalCost"));			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}