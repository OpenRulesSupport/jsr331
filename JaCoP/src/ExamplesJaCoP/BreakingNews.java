/**
 *  BreakingNews.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2000-2008 Krzysztof Kuchcinski and Radoslaw Szymanek
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  Notwithstanding any other provision of this License, the copyright
 *  owners of this work supplement the terms of this License with terms
 *  prohibiting misrepresentation of the origin of this work and requiring
 *  that modified versions of this work be marked in reasonable ways as
 *  different from the original version. This supplement of the license
 *  terms is in accordance with Section 7 of GNU Affero General Public
 *  License version 3.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ExamplesJaCoP;

import java.util.ArrayList;
import JaCoP.constraints.Alldifferent;
import JaCoP.constraints.And;
import JaCoP.constraints.Or;
import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.constraints.XeqY;
import JaCoP.constraints.XneqY;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * @author Marcin Chrapek, Miroslaw Klos, and Radoslaw Szymanek
 * 
 * Logic Puzzle : Breaking News.
 *
 * The Daily Galaxy sent its four best reporters (Corey, Jimmy, Lois,
 * and Perry) to different locations (Bayonne, New Hope, Port Charles,
 * and South Amboy) to cover four breaking news events (30-pound baby,
 * blimp launching, skyscraper dedication, and beached whale). Their
 * editor is trying to remember where each of the reporters is. Can
 * you match the name of each reporter with the place he or she was
 * sent, and the event that each covered?
 *
 * 1. The 30-pound baby wasn't born in South Amboy or New Hope.
 *
 * 2. Jimmy didn't go to Port Charles.
 *
 * 3. The blimp launching and the skyscraper dedication were covered, in
 * some order, by Lois and the reporter who was sent to Port Charles.
 *
 * 4. South Amboy was not the site of either the beached whale or the
 * skyscraper dedication.
 *
 * 5. Bayonne is either the place that Corey went or the place where the
 * whale was beached, or both.
 *
 * Determine: Reporter -- Location -- Story
 *
 */

public class BreakingNews extends Example {

	@Override
	public void model() {

		store = new Store();
		vars = new ArrayList<Variable>();
		
		System.out.println("Program to solve Breaking News ");

		// String arrays with reporters names.
		String[] ReporterName = { "Corey", "Jimmy", "Lous", "Perry" };

		// Constant indexes to ease referring to variables denoting reporters.
		int /* iPerry = 0, */ iCorey = 1, iJimmy = 2, iLous = 3;
		
		// String arrays with locations names.
		String[] LocationName = { "Bayonne", "NewHope", "PortCharles",
				"SouthAmboy" };
		
		// Constant indexes to ease referring to variables denoting locations.
		int iBayonne = 0, iNewHope = 1, iPortCharles = 2, iSouthAmboy = 3;

		// String arrays with stories names.
		String[] StoryName = { "30pound", "blimp", "skyscraper", "beached" };

		// Constant indexes to ease referring to variables denoting stories.
		int i30pound = 0, iblimp = 1, iskyscraper = 2, ibeached = 3;

		// Arrays to store variables.

		Variable Reporter[] = new Variable[4];
		Variable Location[] = new Variable[4];
		Variable Story[] = new Variable[4];

		// All variables are created with domain 1..4. Variables from
		// different arrays with the same values denote the same person.
		for (int i = 0; i < 4; i++) {
			Reporter[i] = new Variable(store, ReporterName[i], 1, 4);
			Location[i] = new Variable(store, LocationName[i], 1, 4);
			Story[i] = new Variable(store, StoryName[i], 1, 4);
			vars.add(Reporter[i]); vars.add(Location[i]); vars.add(Story[i]);
		}

		// It is not possible that one person has two names, or
		// has been in two locations.

		store.impose(new Alldifferent(Reporter));
		store.impose(new Alldifferent(Location));
		store.impose(new Alldifferent(Story));

		// 1. The 30-pound baby wasn't born in South Amboy or New Hope.
		store.impose(new Or(new XneqY(Story[i30pound], Location[iNewHope]),
				new XneqY(Story[i30pound], Location[iSouthAmboy])));

		// 2. Jimmy didn't go to Port Charles.
		store.impose(new XneqY(Reporter[iJimmy], Location[iPortCharles]));

		// 3.The blimp launching and the skyscraper dedication were
		// covered, in some order, by Lois and the reporter who was
		// sent to Port Charles.

		store.impose(new Or(new And(new XeqY(Story[iblimp], Reporter[iLous]),
				new XeqY(Story[iskyscraper], Location[iPortCharles])), new And(
				new XeqY(Story[iblimp], Location[iPortCharles]), new XeqY(
						Story[iskyscraper], Reporter[iLous]))));

		// 4. South Amboy was not the site of either the beached whale
		// or the skyscraper dedication.
		store.impose(new Or(new XneqY(Location[iSouthAmboy], Story[ibeached]),
				new XneqY(Location[iSouthAmboy], Story[iskyscraper])));

		// 5. Bayonne is either the place that Corey went or the place
		// where the whale was beached, or both.
		PrimitiveConstraint orConstraint[] = new PrimitiveConstraint[3];
		orConstraint[0] = new XeqY(Location[iBayonne], Reporter[iCorey]);
		orConstraint[1] = new XeqY(Location[iBayonne], Story[ibeached]);
		orConstraint[2] = new And(new XeqY(Story[ibeached], Reporter[iCorey]), new XeqY(
				Reporter[iCorey], (Location[iBayonne])));

		store.impose(new Or(orConstraint));

	}
		
	/**
	 * It executes the program to solve this logic puzzle.
	 * @param args no arguments are read.
	 */
	public static void main(String args[]) {

		BreakingNews example = new BreakingNews();
		
		example.model();

		if (example.search())
			System.out.println("Solution(s) found");
	}	
	
}
