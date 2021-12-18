package org.jcp.jsr331.linear.samples;

import javax.constraints.*;

/* http://www.analytics-magazine.org/may-june-2012/586-thinking-analytically-is-this-the-way-to-run-a-popsicle-stand?goback=%2Egmr_83522%2Egde_83522_member_114735776
You are required to hire and set the weekly work schedule for your employees. 
The required levels for the week are as follows: 
Total employees required: 
   Monday = 5, 
   Tuesday = 8, 
   Wednesday = 9, 
   Thursday = 10, 
   Friday = 16, 
   Saturday = 18; 
   Sunday = 12.
Total employee pool size: 18 (14 full-time + 4 part-time) 
Assume the same staffing requirements continue week after week.
Full-time employees earn $100 per day. 
Part-time employees earn $150 per day.
Question:
What is the minimal weekly staffing cost you can achieve while meeting 
the required staffing levels?
*/

public class EmployeeRostering1 {

	public static void main(String[] args) {
		Problem p = ProblemFactory.newProblem("EmployeeRostering1");
		// Define FT and PT variables
		int maxFT = 14;
		int maxPT = 4;
		Var monFT = p.variable("MonFT", 0, maxFT);
		Var monPT = p.variable("MonPT", 0, maxPT);
		Var tueFT = p.variable("TueFT", 0, maxFT);
		Var tuePT = p.variable("TuePT", 0,maxPT);
		Var wedFT = p.variable("WedFT", 0, maxFT);
		Var wedPT = p.variable("WedPT", 0, maxPT);
		Var thuFT = p.variable("ThuFT", 0, maxFT);
		Var thuPT = p.variable("ThuPT", 0, maxPT);
		Var friFT = p.variable("FriFT", 0, maxFT);
		Var friPT = p.variable("FriPT", 0, maxPT);
		Var satFT = p.variable("SatFT", 0, maxFT);
		Var satPT = p.variable("SatPT", 0, maxPT);
		Var sunFT = p.variable("SunFT", 0, maxFT);
		Var sunPT = p.variable("SunPT", 0, maxPT);
		
		// Post daily constraints
		p.post(new Var[]{monFT,monPT},"=",5);
		p.post(new Var[]{tueFT,tuePT},"=",8);
		p.post(new Var[]{wedFT,wedPT},"=",9);
		p.post(new Var[]{thuFT,thuPT},"=",10);
		p.post(new Var[]{friFT,friPT},"=",16);
		p.post(new Var[]{satFT,satPT},"=",18);
		p.post(new Var[]{sunFT,sunPT},"=",12);
		
		// Define costs
		
		int[] costs = {100,150,100,150,100,150,100,150,100,150,100,150,100,150};
		Var[] vars = {monFT,monPT,tueFT,tuePT,wedFT,wedPT,thuFT,thuPT,friFT,friPT,satFT,satPT,sunFT,sunPT};
		Var totalCost = p.scalProd(costs, vars);
		p.add("Cost",totalCost);
		/*
		int costFT = 100;
		int costPT = 150;
		
		Var monCost = monFT.multiply(costFT).plus(monPT.multiply(costPT));
		Var tueCost = tueFT.multiply(costFT).plus(tuePT.multiply(costPT));
		Var wedCost = wedFT.multiply(costFT).plus(wedPT.multiply(costPT));
		Var thuCost = thuFT.multiply(costFT).plus(thuPT.multiply(costPT));
		Var friCost = friFT.multiply(costFT).plus(friPT.multiply(costPT));
		Var satCost = satFT.multiply(costFT).plus(satPT.multiply(costPT));
		Var sunCost = sunFT.multiply(costFT).plus(sunPT.multiply(costPT));
		
		Var[] costVars = {
			monCost,tueCost,wedCost,thuCost,friCost,satCost,sunCost
		};
		Var totalCost = p.sum(costVars);
		p.add("TotalCost",totalCost);
		*/
		// Find Optimal Solution
		Solver solver = p.getSolver();
		solver.traceSolutions(true);
		Solution s = solver.findOptimalSolution(totalCost);
		if (s != null) {
			s.log();
			// Print solution
			p.log("    M  T  W   T   F   S   S");
			p.log("FT  " + s.getValue("MonFT") + "  " + s.getValue("TueFT") + "  " + s.getValue("WedFT") + "  "
					  +	s.getValue("ThuFT") + "  " +	s.getValue("FriFT") + "  " + s.getValue("SatFT") + "  "  
					  +	s.getValue("SunFT"));
			p.log("PT  " + s.getValue("MonPT") + "  " + s.getValue("TuePT") + "  " + s.getValue("WedPT") + "   "
					  +	s.getValue("ThuPT") + "   " +	s.getValue("FriPT") + "   " + s.getValue("SatPT") + "   "  
					  +	s.getValue("SunPT"));
		}
		else
			p.log("No solutions");
		solver.logStats();
	}
}

/*
Solution #23:
	 MonFT[5] MonPT[0] TueFT[8] TuePT[0] WedFT[9] WedPT[0] ThuFT[10] ThuPT[0] FriFT[14]
	 FriPT[2] SatFT[14] SatPT[4] SunFT[12] SunPT[0] TotalCost[8100]
    M  T  W   T   F   S   S
FT  5  8  9  10  14  14  12
PT  0  0  0   0   2   4   0
*/