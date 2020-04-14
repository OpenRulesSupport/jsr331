//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package org.jcp.jsr331.samples;

import javax.constraints.ProblemFactory;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.VarSet;
import javax.constraints.Problem;

public class FlightCrews {

	static final String[] names = { "Tom", "David", "Jeremy", "Ron", "Joe",
			"Bill", "Fred", "Bob", "Mario", "Ed", "Carol", "Janet", "Tracy",
			"Marilyn", "Carolyn", "Cathy", "Inez", "Jean", "Heather", "Juliet" };

	static final int Tom = 0, David = 1, Jeremy = 2, Ron = 3, Joe = 4,
			Bill = 5, Fred = 6, Bob = 7, Mario = 8, Ed = 9, Carol = 10,
			Janet = 11, Tracy = 12, Marilyn = 13, Carolyn = 14, Cathy = 15,
			Inez = 16, Jean = 17, Heather = 18, Juliet = 19;

	static final int iNbMembers = 0, iStewards = 1, iHostesses = 2,
			iFrench = 3, iSpanish = 4, iGerman = 5;

	static int[] Staff = { Tom, David, Jeremy, Ron, Joe, Bill, Fred, Bob,
			Mario, Ed, Carol, Janet, Tracy, Marilyn, Carolyn, Cathy, Inez,
			Jean, Heather, Juliet };


	public static void main(String[] args) {
		try {
			Problem csp = ProblemFactory.newProblem("FlightCrew");
			VarSet Stewards = csp.variableSet("Stewards", new int[] { Tom, David,
					Jeremy, Ron, Joe, Bill, Fred, Bob, Mario, Ed });

			VarSet Hostesses = csp.variableSet("Hostesses", new int[] { Carol,
					Janet, Tracy, Marilyn, Carolyn, Cathy, Inez, Jean, Heather,
					Juliet });

			VarSet French = csp.variableSet("French", new int[] { Inez, Bill,
					Jean, Juliet });
			VarSet German = csp.variableSet("German", new int[] { Tom, Jeremy,
					Mario, Cathy, Juliet });
			VarSet Spanish = csp.variableSet("Spanish", new int[] { Bill, Fred,
					Joe, Mario, Marilyn, Inez, Heather });

			Stewards.require(Tom);
			Stewards.require(Bill);
			csp.post(Stewards.getCardinality(),">",3);
			Stewards.intersection(French).setEmpty(true);
			Hostesses.intersection(German).setEmpty(true);
			csp.post(Hostesses.getCardinality(),">",2);
			Spanish.intersection(German).setEmpty(true);
//			csp.post(French.getCardinality(),">",3);
//			csp.post(German.getCardinality(),">",2);

			VarSet[] setVars = new VarSet[] { Stewards, Hostesses, French, German, Spanish };
			for (int i = 0; i < setVars.length; i++) {
				System.out.println(setVars[i].toString());
			}

			Solver solver = csp.getSolver();
			solver.logStats();
			//solver.setTimeLimit(milliseconds); // mills
			SearchStrategy strategy = solver.getSearchStrategy();
			strategy.setVars(setVars); 
	
			// One solution
			Solution solution = solver.findSolution();
			if (solution == null)
				System.out.println("Solution for crews task wasn't found");
			else {
				solution.log();
				for (int i = 0; i < setVars.length; i++) {
					System.out.println(setVars[i].toString());
				}
			}
		} catch (Exception e) {
			System.err.println("Problem is over-constrained: errors during constraints posting");
			e.printStackTrace();
		}

	}

}
