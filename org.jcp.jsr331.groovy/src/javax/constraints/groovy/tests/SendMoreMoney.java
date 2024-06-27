package javax.constraints.groovy.tests;

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

public class SendMoreMoney {
	public static void main(String[] args) {

		Problem p = new Problem("SendMoreMoney");
		// define variables
		Var S = p.variable( "S",1, 9);
		Var E = p.variable( "E",0, 9);
		Var N = p.variable( "N",0, 9);
		Var D = p.variable( "D",0, 9);
		Var M = p.variable( "M",1, 9);
		Var O = p.variable( "O",0, 9);
		Var R = p.variable( "R",0, 9);
		Var Y = p.variable( "Y",0, 9);

		// Post "all different" constraint
		Var[] vars = new Var[] { S, E, N, D, M, O, R, Y };
		new AllDifferent(vars).post();
		
		// Define constraint SEND + MORE = MONEY 
		int coef[] = { 1000, 100, 10, 1, 1000, 100, 10, 1, -10000, -1000, -100, -10, -1 };
		Var[] sendmoremoney = new Var[] { S, E, N, D, M, O, R, E, M, O, N, E, Y};
		// p.linear(S*1000 + E*100 + N*10 + D + M*1000 + O*100 + R*10 + E, "=", M*10000 + O*1000 + N*100 + E*10 + Y).post();
		p.post(coef, sendmoremoney, "=", 0);
		
		
		// Problem Resolution
		Solution s = p.getSolver().findSolution();
		if (s == null)
			p.log("No Solutions");
		else {
			s.log();
			p.log("  "+s.getValue("S")+s.getValue("E")+s.getValue("N")+s.getValue("D"));
			p.log("+ "+s.getValue("M")+s.getValue("O")+s.getValue("R")+s.getValue("E"));
			p.log("=======");
			p.log(" "+s.getValue("M")+s.getValue("O")+s.getValue("N")+s.getValue("E")+s.getValue("Y"));
		}
	}
}
