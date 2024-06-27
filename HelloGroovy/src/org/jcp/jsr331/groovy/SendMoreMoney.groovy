package org.jcp.jsr331.groovy;

/*-------------------------------------------------------------
 Solve the puzzle:

   S E N D
 + M O R E
 ---------
 M O N E Y

 where different letters represent different digits.
 ------------------------------------------------------------ */

import javax.constraints.impl.Problem
import javax.constraints.impl.Var

// I'd like to be able to write 10*R instead of R*10
//class extend java.lang.Number {
//	  Var multiply(Var var) {
//	    return var.multiply(this);
//	  }
//}

public class SendMoreMoney {
	public static void main(String[] args) {

		Problem p = new Problem("SendMoreMoney");
		// define variables
		def S = p.variable("S",1, 9)
		def E = p.variable("E",0, 9)
		def N = p.variable("N",0, 9)
		def D = p.variable("D",0, 9)
		def M = p.variable("M",1, 9)
		def O = p.variable("O",0, 9)
		def R = p.variable("R",0, 9)
		def Y = p.variable("Y",0, 9)

		// Post "all different" constraint
		Var[] vars = [ S, E, N, D, M, O, R, Y ]
		p.postAllDiff(vars)
		
		// Define constraint SEND + MORE = MONEY 
		p.post( 
			(  (S*1000 + E*100 + N*10 + D)  		//   SEND
			 + (M*1000 + O*100 + R*10 + E)),  		// + MORE
			"=", 									// =
			(M*10000 + O*1000 + N*100 + E*10 + Y)) 	//  MONEY
		
		// Problem Resolution
		def s = p.getSolver().findSolution();
		if (s == null)
			p.log "No Solutions"
		else {
			s.log()
			s.dump()
			p.log "  "+s["S"]+s["E"]+s["N"]+s["D"]
			p.log "+ "+s["M"]+s["O"]+s["R"]+s["E"]
			p.log "======="
			p.log " "+s["M"]+s["O"]+s["N"]+s["E"]+s["Y"]
		}
	}
}
