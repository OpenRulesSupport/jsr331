package org.jcp.jsr331.samples;

/*-------------------------------------------------------------
 Solve the puzzle:

   S E N D
 + M O R E
 ---------
 M O N E Y

 where different letters represent different digits.
 ------------------------------------------------------------ */

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Var;

public class SendMoreMoney1 {
	public static void main(String[] args) {

		Problem p = ProblemFactory.newProblem("SendMoreMoney");
		// define variables
		Var S = p.variable("S",1,9);
		Var E = p.variable("E",0,9);
		Var N = p.variable("N",0,9);
		Var D = p.variable("D",0,9);
		Var M = p.variable("M",1,9);
		Var O = p.variable("O",0,9);
		Var R = p.variable("R",0,9);
		Var Y = p.variable("Y",0,9);

		// Post "all different" constraint
		Var[] vars = new Var[] { S, E, N, D, M, O, R, Y };
		p.postAllDiff(vars);

		// Define expression SEND
		int coef1[] = { 1000, 100, 10, 1 };
		Var[] sendVars = { S, E, N, D };
		Var SEND = p.scalProd(coef1, sendVars);
		SEND.setName("SEND");
		// Define expression MORE
		Var[] moreVars = { M, O, R, E };
		Var MORE = p.scalProd(coef1, moreVars);
		MORE.setName("MORE");
		// Define expression MONEY
		Var[] moneyVars = { M, O, N, E, Y };
		int coef2[] = { 10000, 1000, 100, 10, 1 };
		Var MONEY = p.scalProd(coef2, moneyVars);
		MONEY.setName("MONEY");
		p.add(MONEY);
		// Post constraint SEND + MORE = MONEY
		p.post(SEND.plus(MORE),"=",MONEY);

		// Problem Resolution
		p.getSolver().findSolution();
		p.log("Solution: " + SEND + " + " + MORE + " = " + MONEY);
	}
}
