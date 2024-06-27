package org.jcp.jsr331.linear.samples;

/**
 * <p>Description: (The description was taken from Ilog's OPL Studio manual) 
 * An oil company manufactures three types of gasoline: super, regular, and diesel.
 * Each type of gasoline is produced by blending three types of crude oil: crude1, crude2, and crude3.
 * The gasoline must satisfy some quality criteria with respect to their lead content and their octane ratings,
 * thus constraining the possible blendings. The company must also satisfy its customer demand,
 * which is 3,000 barrels a day of super, 2,000 of regular, and 1,000 of diesel. The company can
 * purchase 5,000 barrels of each type of crude oil per day and can process at most 14,000 barrels a day.
 * In addition, the company has the option of advertising a gasoline, in which case the demand for this type
 * of gasoline increases by ten barrels for every dollar spent. Finally, it costs four dollars
 * to transform a barrel of oil into a barrel of gasoline 
 * </p>
 */

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarReal;

public class BlendingProblemReal {

	public BlendingProblemReal() {
	}

	public static void main(String[] args) {
		try {

//			String oilTypes[] = { "Crude1", "Crude2", "Crude3" };
//			String gasTypes[] = { "Super", "Regular", "Diesel" };
			String oilTypes[] = { "Crd1", "Crd2", "Crd3" };
			String gasTypes[] = { "Sup", "Reg", "Die" };
			int demands[] = { 3000, 2000, 1000 };
			int capacities[] = { 5000, 5000, 5000 };
			int gasOctane[] = { 10, 8, 6 };
			int oilOctane[] = { 12, 6, 8 };
			int gasLead[] = { 1, 2, 1 };
			double oilLead[] = {0.5,2,3};
			//int oilLead[] = { 0, 2, 3 };
			int gasPrices[] = { 70, 60, 50 };
			int oilPrices[] = { 45, 35, 25 };
			int maxProduction = 14000;
			int prodCost = 4;
			
			Problem p = ProblemFactory.newProblem("BlendingProblemReal");
			// VarReal[][] blendsTable = new VarReal[3][3];
			Var[][] blendsTable = new Var[3][3];

			// It would be 9 variables corresponding to the appropriate blending
			// values
			int N = oilTypes.length * gasTypes.length;
			Var[] blends = new Var[N];
			int[] income = new int[N];
			for (int i = 0; i < oilTypes.length; i++) {
				for (int j = 0; j < gasTypes.length; j++) {
					Var var = p.variable(oilTypes[i] + "-" + gasTypes[j], 0,
							maxProduction);
					blendsTable[i][j] = var;
					blends[gasTypes.length * i + j] = var;
					income[gasTypes.length * i + j] = gasPrices[j]
							- oilPrices[i] - prodCost;
				}
			}
			// and three variables corresponding to the amount of gasoline
			// consumed for advertisement purposes.
			Var[] adv = new Var[gasTypes.length];
			for (int i = 0; i < gasTypes.length; i++) {
				Var var = p.variable("adv[" + gasTypes[i] + "]", 0,
						maxProduction);
				adv[i] = var;
			}

			// cost function to be maximized (pure income)
			int[] coef1 = new int[income.length + adv.length];
			Var[] vars1 = new Var[income.length + adv.length];
			int n = 0;
			for (int i = 0; i < income.length; i++) {
				coef1[n] = income[i];
				vars1[n] = blends[i];
				n++;
			}
			for (int i = 0; i < adv.length; i++) {
				coef1[n] = -1;
				vars1[n] = adv[i];
				n++;
			}
			// Var costFunc = p.scalProd(income, blends).minus(p.sum(adv));
			Var costFunc = p.scalProd(coef1, vars1);
			p.add("costFunc", costFunc);
			// csp.setObjectiveReal(costFunc);

			// demand constraints
			for (int j = 0; j < gasTypes.length; j++) {
				int[] coef2 = new int[oilTypes.length + 1];
				Var[] vars2 = new Var[oilTypes.length + 1];
				n = 0;
				for (int i = 0; i < oilTypes.length; i++) {
					coef2[n] = 1;
					vars2[n] = blendsTable[i][j];
					n++;
				}
				coef2[n] = -10;
				vars2[n] = adv[j];
				p.post(coef2, vars2, "=", demands[j]);
			}

			// purchase limitation
			for (int i = 0; i < oilTypes.length; i++) {
				Var[] tmp = new Var[gasTypes.length];
				for (int j = 0; j < gasTypes.length; j++) {
					tmp[j] = blendsTable[i][j];
				}
				p.post(tmp, "<=", capacities[i]);
			}

			// quality constraints:
			for (int j = 0; j < gasTypes.length; j++) {
				Var[] tmp = new Var[oilTypes.length];
				int[] octaneDiff = new int[oilTypes.length];
				//int[] leadDiff = new int[oilTypes.length];
				double[] leadDiff = new double[oilTypes.length];
				for (int i = 0; i < oilTypes.length; i++) {
					tmp[i] = blendsTable[i][j];
					octaneDiff[i] = oilOctane[i] - gasOctane[j];
					leadDiff[i] = oilLead[i] - gasLead[j];
				}
				// 1. by octane rating
				Var octaneRating = p.scalProd(octaneDiff, tmp);
				p.post(octaneRating, ">=", 0);
				// 2. by leading content
				VarReal leadContent = p.scalProd(leadDiff, tmp); 
				p.post(leadContent, "<=", 0.0);    
			}
			// capacity limitation
			p.post(blends, "<=", maxProduction);

			// Solution solution = prob.maximize();
			p.log(p.getVars());
			p.log(p.getVarReals());   
			Solver solver = p.getSolver();
			Solution solution = solver.findOptimalSolution(Objective.MAXIMIZE,
					costFunc);
			if (solution == null) {
				System.out.println("The problem has no solution!");
				return;
			}
			p.log("Optimized cost function value: "
					+ solution.getValue("costFunc"));
			solution.log();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
 Optimized cost function value: 287750
REAL COSTS: 
Scip
Solution #1:
	 Crd1-Sup[2000] Crd1-Reg[2330] Crd1-Die[670] Crd2-Sup[1000] Crd2-Reg[3675] Crd2-Die[325] Crd3-Sup[0] Crd3-Reg[3495] Crd3-Die[5]
	 adv[Sup][0] adv[Reg][750] adv[Die][0] costFunc[287750]
	 
INTEGER COSTS:	 
Solve problem using ojAlgo (Sep-2009)
MPS file generated in 4 msec
Optimized cost function value: 323199
Solution #1:
	 Crd1-Sup[2400] Crd1-Reg[2100] Crd1-Die[500] Crd2-Sup[799] Crd2-Reg[2699] Crd2-Die[500] Crd3-Sup[799] Crd3-Reg[4200] Crd3-Die[0]
	 adv[Sup][99] adv[Reg][699] adv[Die][0] costFunc[323199]
----	 
Solve problem using GLPK v.4-47
MPS file generated in 4 msec
Execute command: glpsol --model .\BlendingProblem.mps --mps -w .\BlendingProblem.txt >.\BlendingProblem.txt.log
Exit value: 0
Execution time: 45 msec
Optimized cost function value: 323200
Solution #1:
	 Crd1-Sup[2850] Crd1-Reg[1650] Crd1-Die[500] Crd2-Sup[950] Crd2-Reg[3300] Crd2-Die[500] Crd3-Sup[950] Crd3-Reg[3300] Crd3-Die[0]
	 adv[Sup][175] adv[Reg][625] adv[Die][0] costFunc[323200]
---
Solve problem using GUROBI v.5.0.1
MPS file generated in 8 msec
Execute command: gurobi_cl Threads=1 ResultFile=.\BlendingProblem.sol .\BlendingProblem.mps >.\BlendingProblem.sol.log
Exit value: 0
Execution time: 74 msec
Optimized cost function value: 323200
Solution #1:
	 Crd1-Sup[2534] Crd1-Reg[1800] Crd1-Die[666] Crd2-Sup[397] Crd2-Reg[3600] Crd2-Die[2] Crd3-Sup[1068] Crd3-Reg[3600] Crd3-Die[331]
	 adv[Sup][100] adv[Reg][700] adv[Die][0] costFunc[323200]
---
Solve problem using COIN v.1.3.3
MPS file generated in 3 msec
Execute command: clp .\BlendingProblem.mps -maximize -dualsimplex -solution .\BlendingProblem.sol >.\BlendingProblem.sol.log
Exit value: 0
Execution time: 31 msec
Optimized cost function value: 323200
Solution #1:
	 Crd1-Sup[2400] Crd1-Reg[2100] Crd1-Die[500] Crd2-Sup[800] Crd2-Reg[2700] Crd2-Die[500] Crd3-Sup[800] Crd3-Reg[4200] Crd3-Die[0]
	 adv[Sup][100] adv[Reg][700] adv[Die][0] costFunc[323200]
	 
 */