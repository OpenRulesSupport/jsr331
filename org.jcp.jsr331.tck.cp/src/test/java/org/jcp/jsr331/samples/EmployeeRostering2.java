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
Full-time employees work 5 consecutive days and earn $100 per day. 
Part-time employees work 2 consecutive days and earn $150 per day.
Question:
What is the minimal weekly staffing cost you can achieve while meeting 
the required staffing levels?
*/

public class EmployeeRostering2 {
	
    static Constraint all(Constraint c1,Constraint c2,Constraint c3,Constraint c4,
    		              Constraint c5,Constraint c6,Constraint c7) {
    	return c1.and(c2).and(c3).and(c4).and(c5).and(c6).and(c7);
    }

	public static void main(String[] args) {
		Problem p = ProblemFactory.newProblem("EmployeeRostering2");
		// Define FT and PT variables
		int maxFT = 14;
		int maxPT = 4;
		int days = 7; 
		int[] demand = { 5,8,9,10,16,18,12 };
		
		VarMatrix ft = p.variableMatrix("FT", 0, 1, maxFT, days);
		VarMatrix pt = p.variableMatrix("PT", 0, 1, maxPT, days);
		
		p.log("Post daily demand constraints");
		for (int i = 0; i < days; i++) {
			Var[] ftColumn = ft.column(i);
			Var dayFT = p.sum(ftColumn);
			Var[] ptColumn = pt.column(i);
			Var dayPT = p.sum(ptColumn);
			p.post(dayFT.plus(dayPT),"=",demand[i]); 
			
			if (demand[i] == (maxFT+maxPT)) {
				for (int j = 0; j < ftColumn.length; j++) {
					p.post(ftColumn[j],"=",1);
				}
				for (int j = 0; j < ptColumn.length; j++) {
					p.post(ptColumn[j],"=",1);
				}
			}
			
			/*
			// how to avoid symmetry?
			String sign = "<=";
			if (i >= 7)
				sign = ">=";
			for (int j = 1; j < ftColumn.length; j++) {
				p.post(ftColumn[j-1],sign,ftColumn[j]);
			}
			for (int j = 1; j < ptColumn.length; j++) {
				p.post(ptColumn[j-1],sign,ptColumn[j]);
			}
			*/
		}
		
		p.log("Post consecutive days constraints per employee");
		Constraint monOff,monOn,tueOff,tueOn,wedOff,wedOn,
		           thuOff,thuOn,friOff,friOn,satOff,satOn,sunOff,sunOn;
		p.log("FULL-TIME");
		/*
		 Mon Tue Wed Thu Fri Sat Sun 
		  x   x   x   x   x   -   -
		  -   x   x   x   x   x   -
		  -   -   x   x   x   x   x
		  x   -   -   x   x   x   x
		  x   x   -   -   x   x   x
		  x   x   x   -   -   x   x
		  x   x   x   x   -   -   x
		 */
		for (int i = 0; i < maxFT; i++) {
			Var[] vars = ft.row(i);
			monOff = p.linear(vars[0],"=",0); // 0 - Mon
			monOn = p.linear(vars[0],"=",1);
			tueOff = p.linear(vars[1],"=",0); 
			tueOn = p.linear(vars[1],"=",1);
			wedOff = p.linear(vars[2],"=",0); 
			wedOn = p.linear(vars[2],"=",1);
			thuOff = p.linear(vars[3],"=",0); 
			thuOn = p.linear(vars[3],"=",1);
			friOff = p.linear(vars[4],"=",0); 
			friOn = p.linear(vars[4],"=",1);
			satOff = p.linear(vars[5],"=",0); 
			satOn = p.linear(vars[5],"=",1);
			sunOff = p.linear(vars[6],"=",0);
			sunOn = p.linear(vars[6],"=",1);
			
			p.post(all(monOn,tueOn,wedOn,thuOn,friOn, satOff, sunOff).or(
				   all(monOff,tueOn,wedOn,thuOn,friOn,satOn,sunOff).or(
				   all(monOff,tueOff,wedOn,thuOn,friOn,satOn,sunOn).or(
				   all(monOn,tueOff,wedOff,thuOn,friOn,satOn,sunOn).or(	   
				   all(monOn,tueOn,wedOff,thuOff,friOn,satOn,sunOn).or(
				   all(monOn,tueOn,wedOn,thuOff,friOff,satOn,sunOn).or(
				   all(monOn,tueOn,wedOn,thuOn,friOff,satOff,sunOn))))))));
		}
		/*
		p.log("PART-TIME");
		/*
		 Mon Tue Wed Thu Fri Sat Sun 
		  -   -   -   -   -   *   *
		  *   -   -   -   -   -   *
		  *   *   -   -   -   -   -
		  -   *   *   -   -   -   -
		  -   -   *   *   -   -   -
		  -   -   -   *   *   -   -
		  -   -   -   -   *   *   -
		 */
		for (int i = 0; i < maxPT; i++) {
			Var[] vars = pt.row(i);
			monOff = p.linear(vars[0],"=",0); // 0 - Mon
			monOn = p.linear(vars[0],"=",1);
			tueOff = p.linear(vars[1],"=",0); 
			tueOn = p.linear(vars[1],"=",1);
			wedOff = p.linear(vars[2],"=",0); 
			wedOn = p.linear(vars[2],"=",1);
			thuOff = p.linear(vars[3],"=",0); 
			thuOn = p.linear(vars[3],"=",1);
			friOff = p.linear(vars[4],"=",0); 
			friOn = p.linear(vars[4],"=",1);
			satOff = p.linear(vars[5],"=",0); 
			satOn = p.linear(vars[5],"=",1);
			sunOff = p.linear(vars[6],"=",0);
			sunOn = p.linear(vars[6],"=",1);

			p.post(all(monOff,tueOff,wedOff,thuOff,friOff, satOn, sunOn).or(
					   all(monOn,tueOff,wedOff,thuOff,friOff,satOff,sunOn).or(
					   all(monOn,tueOn,wedOff,thuOff,friOff,satOff,sunOff).or(
					   all(monOff,tueOn,wedOn,thuOff,friOff,satOff,sunOff).or(	   
					   all(monOff,tueOff,wedOn,thuOn,friOff,satOff,sunOff).or(
					   all(monOff,tueOff,wedOff,thuOn,friOn,satOff,sunOff).or(
					   all(monOff,tueOff,wedOff,thuOff,friOn,satOn,sunOff))))))));
		}
		
		p.log("Define costs");
		Var[] ftVars = ft.flat();
		int[] ftCosts = new int[ftVars.length];
		for (int i = 0; i < ftCosts.length; i++) {
			ftCosts[i] = 100;
		}
		Var ftCost = p.scalProd(ftCosts, ftVars);
		
		Var[] ptVars = pt.flat();
		int[] ptCosts = new int[ptVars.length];
		for (int i = 0; i < ptCosts.length; i++) {
			ptCosts[i] = 150;
		}
		Var ptCost = p.scalProd(ptCosts, ptVars);
		Var totalCost = p.sum(ftCost,ptCost);
		p.add("TotalCost",totalCost);
		
		// Solution search
		Solver solver = p.getSolver();
		p.log("Find a Solution...");
		Solution solution = solver.findSolution();
		
//		p.log("Find Optimal Solution..."); // too many symmetries in  the model to prove that the first solution is optimal
//		ArrayList<Var> decisionVars = p.variableList();
//		decisionVars.add(ftVars); 
//		decisionVars.add(ptVars); 
//		solver.getSearchStrategy().setVars(decisionVars);
//		solver.traceSolutions(true);
//		//solver.addStrategyLogVariables();
//		//solver.trace(totalCost);
//		Solution solution = solver.findOptimalSolution(totalCost);
		if (solution != null) {
			solution.log();
			p.log("  FULL-TIME EMPLOYEES");
			p.log("  M  T  W  T  F  S  S");
			p.log(ft.toString());
			p.log("  PART-TIME EMPLOYEES");
			p.log("  M  T  W  T  F  S  S");
			p.log(pt.toString());
		}
		else
			p.log("No solutions");
		solver.logStats();
	}
}

/*
  FULL-TIME EMPLOYEES
  M  T  W  T  F  S  S
  0  0  1  1  1  1  1
  0  0  1  1  1  1  1
  0  0  1  1  1  1  1
  0  0  1  1  1  1  1
  0  0  1  1  1  1  1
  0  1  1  1  1  1  0
  0  1  1  1  1  1  0
  0  1  1  1  1  1  0
  0  1  1  1  1  1  0
  1  0  0  1  1  1  1
  1  1  0  0  1  1  1
  1  1  0  0  1  1  1
  1  1  0  0  1  1  1
  1  1  0  0  1  1  1

  PART-TIME EMPLOYEES
  M  T  W  T  F  S  S
  0  0  0  0  0  1  1
  0  0  0  0  0  1  1
  0  0  0  0  1  1  0
  0  0  0  0  1  1  0

Mon: 5 FT (1 Tue-Wed off, 4 Wed-Thu off). Total daily cost 5*$100=$500 
Tue: 8 FT (4 Sun-Mon off, 4 Wed-Thu off). Total daily cost 8*$100=$800 
Wed: 9 FT (4 Sun-Mon off; 5 Mon-Tue off). Total daily cost 9*$100=$900 
Thu: 10 FT (4 Sun-Mon off; 5 Mon-Tue off; 1 Tue-Wed off). Total daily cost: 10*$100=$1000 
Fri: 14 FT+2 PT (Sun-Thu off): total staffing 16. Total daily cost: 14*$100+2*$150=$1700 
Sat: 14 FT+4 PT (2 PT Mon-Fri off; and 2 PT Sun-Thu off): total staffing 18. Total daily cost: 14*$100+4*$150=$2000 
Sun: 10 FT+2 PT (2 PT Mon-Fri off): total staffing 12. Total daily cost: 10*$100+2*$150=$1300. 
Thus, total minimal weekly cost $8,200.
*/