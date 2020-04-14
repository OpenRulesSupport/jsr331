package org.jcp.jsr331.samples;

//===============================================
//J A V A  C O M M U N I T Y  P R O C E S S
//
//J S R  3 3 1
//
//TestXYZ Compatibility Kit
//
//================================================

import javax.constraints.*;

/***************************************************************************
 * \
 * 
 * ------------ Uzbekian sales man met Problem -----------
 * 
 * An Uzbekian sales man met five traders who live in 5 different cities.
 * 
 * Traders: Abdulhamid, Kurban,Ruza, Sharaf, Usman
 * 
 * Cities: Bukhara, Fergana, Kokand, Samarkand, Tashkent
 * 
 * Find the order in which he visited the cities with the following info: > He
 * met Ruza before Sharaf after visiting Samarkand, > He reached Fergana after
 * visiting Samarkand followed by other two cities, > The third trader he met
 * was Tashkent, > Immediately after his visit to Bukhara, he met Abdulhamid >
 * He reached Kokand after visiting the city of Kurban followed by other two
 * cities
 * 
 * @author Rajmahendra Hegde
 * 
 * 
 *         \
 ***************************************************************************/
public class UzbekianPuzzle {
	
	Problem p = ProblemFactory.newProblem("Uzbekian Salesman Problem");

	private String sCities[];
	private String sTraders[];

	private int cBukhara = 0, cFergana = 1, cKokand = 2, cSamarkand = 3,
			cTashkent = 4; // Cities
	private int tAbdulhamid = 0, tKurban = 1, tRuza = 2, tSharaf = 3,
			tUsman = 4; // Traders

	Var cities[];
	Var traders[];

	public UzbekianPuzzle() {
		sCities = new String[] { "Bukhara", "Fergana", "Kokand", "Samarkand",
				"Tashkent" };
		sTraders = new String[] { "Abdulhamid", "Kurban", "Ruza", "Sharaf",
				"Usman" };
		cities = new Var[sCities.length];
		traders = new Var[sTraders.length];
	}

	private void define() {

		cities[cBukhara]   = p.variable(sCities[cBukhara], 1, 5);
		cities[cFergana]   = p.variable(sCities[cFergana], 1, 5);
		cities[cKokand]    = p.variable(sCities[cKokand], 1, 5);
		cities[cSamarkand] = p.variable(sCities[cSamarkand], 1, 5);
		cities[cTashkent]  = p.variable(sCities[cTashkent], 1, 5);

		traders[tAbdulhamid] = p.variable(sTraders[tAbdulhamid], 1, 5);
		traders[tKurban]     = p.variable(sTraders[tKurban], 1, 5);
		traders[tRuza]       = p.variable(sTraders[tRuza], 1, 5);
		traders[tSharaf]     = p.variable(sTraders[tSharaf], 1, 5);
		traders[tUsman]      = p.variable(sTraders[tUsman], 1, 5);
	}

	private void postConstraints() {

		// He met Ruza before Sharaf after visiting Samarkand,
		p.post(traders[tRuza], "<", traders[tSharaf]);
		p.post(traders[tRuza], ">", cities[cSamarkand]);

		// He reached Fergana after visiting Samarkand followed by other two
		// cities,
		p.post(cities[cFergana], "=", cities[cSamarkand].plus(2));

		// The third trader he met was Tashkent,
		p.post(cities[cTashkent], "=", 3);

		// Immediately after his visit to Bukhara, he met Abdulhamid
		p.post(traders[tAbdulhamid], "=", cities[cBukhara].plus(1));

		// He reached Kokand after visiting the city of Kurban followed by other
		// two cities
		p.post(cities[cKokand], "=", traders[tKurban].plus(2));

		p.postAllDiff(cities);
		p.postAllDiff(traders);
	}

	private void solve() {
		Solver solver = p.getSolver();
		Solution solution = solver.findSolution();
		if (solution != null) {
			solution.log();
		} else
			p.log("No Solutions");
		solver.logStats();

	}

	public void findSolutionForProblem() {
		define();
		postConstraints();
		solve();
	}

	public static void main(String[] args) {
		UzbekianPuzzle theProblem = new UzbekianPuzzle();
		theProblem.findSolutionForProblem();
	}
}
