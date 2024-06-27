package org.jcp.jsr331.samples;

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
		p.post(monFT.plus(monPT),"=",5);
		p.post(tueFT.plus(tuePT),"=",8);
		p.post(wedFT.plus(wedPT),"=",9);
		p.post(thuFT.plus(thuPT),"=",10);
		p.post(friFT.plus(friPT),"=",16);
		p.post(satFT.plus(satPT),"=",18);
		p.post(sunFT.plus(sunPT),"=",12);
		
		// Define costs		
		int[] costs = {100,150,100,150,100,150,100,150,100,150,100,150,100,150};
		Var[] vars = {monFT,monPT,tueFT,tuePT,wedFT,wedPT,thuFT,thuPT,friFT,friPT,satFT,satPT,sunFT,sunPT};
		Var totalCost = p.scalProd(costs, vars);
		p.add("TotalCost",totalCost);
		
		// Find Optimal Solution
		Solver solver = p.getSolver();
		solver.traceSolutions(true);
		Solution s = solver.findOptimalSolution(totalCost);
		if (s != null) {
			s.log();
			// Print solution
			p.log("    M  T  W   T   F   S   S");
			p.log("FT  " + s.getValue("MonFT") + "  " + s.getValue("TueFT") 
				  + "  " + s.getValue("WedFT") + "  "
				  +	s.getValue("ThuFT") + "  " + s.getValue("FriFT") + 
				  "  " + s.getValue("SatFT") + "  " + s.getValue("SunFT"));
			p.log("PT  " + s.getValue("MonPT") + "  " + s.getValue("TuePT") 
				  + "  " + s.getValue("WedPT") + "   "
				  +	s.getValue("ThuPT") + "   " + s.getValue("FriPT") 
				  + "   " + s.getValue("SatPT") + "   " + s.getValue("SunPT"));
		}
		else
			p.log("No solutions");
		solver.logStats();
	}
}
