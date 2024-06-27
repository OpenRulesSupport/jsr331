package org.jcp.jsr331.samples;

/*-------------------------------------------------------------
 Solve the puzzle:

   S E N D
 + M O R E
 ---------
 M O N E Y

 where different letters represent different digits.
 ------------------------------------------------------------ */

import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.impl.Problem;
import javax.constraints.impl.constraint.AllDifferent;

public class SendMoreMoney extends Problem {
	
	// Problem Definition
	public void define() {
		// define variables
		Var S = variable( "S",1, 9);
		Var E = variable( "E",0, 9);
		Var N = variable( "N",0, 9);
		Var D = variable( "D",0, 9);
		Var M = variable( "M",1, 9);
		Var O = variable( "O",0, 9);
		Var R = variable( "R",0, 9);
		Var Y = variable( "Y",0, 9);

		// Post "all different" constraint
		Var[] vars = new Var[] { S, E, N, D, M, O, R, Y };
		new AllDifferent(vars).post();
		
		// Define constraint SEND + MORE = MONEY 
		int coef[] = { 1000, 100, 10, 1, 1000, 100, 10, 1, -10000, -1000, -100, -10, -1 };
		Var[] sendmoremoney = new Var[] { S, E, N, D, M, O, R, E, M, O, N, E, Y};
		post(coef, sendmoremoney, "=", 0);
	}
		
	// Problem Resolution
	public void solve() {	
		Solution s = getSolver().findSolution();
		if (s == null)
			log("No Solutions");
		else {
			s.log();
			log("  "+s.getValue("S")+s.getValue("E")+s.getValue("N")+s.getValue("D"));
			log("+ "+s.getValue("M")+s.getValue("O")+s.getValue("R")+s.getValue("E"));
			log("=======");
			log(" "+s.getValue("M")+s.getValue("O")+s.getValue("N")+s.getValue("E")+s.getValue("Y"));
		}
		
	}
	
	public static void main(String[] args) {
		SendMoreMoney p = new SendMoreMoney();
		p.define();
		p.solve();
	}
}
