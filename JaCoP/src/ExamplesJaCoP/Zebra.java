/**
 *  Zebra.java 
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
import JaCoP.constraints.Eq;
import JaCoP.constraints.Not;
import JaCoP.constraints.Or;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XeqY;
import JaCoP.constraints.XneqY;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * 
 * It models and solves logic puzzle.
 * 
 * @author Radoslaw Szymanek
 * 
 * It was given at The German Institute of Logical Thinking in Berlin, 1981. And 98% FAILED.
 *
 *
 * 
 * Conditions
 *
 *  1. There are five houses.
 *  2. Each house has it's own unique color.
 *  3. All homeowners are of different nationalities.
 *  4. They all have different pets.
 *  5. They all drink different drinks.
 *  6. They all smoke different cigarettes.
 *  7. The Englishman lives in the red House.
 *  8. The Swede has a dog.
 *  9. The Dane drinks Tea.
 * 10. The green house is on the left side of the white house.
 * 11. In the green house they drink coffee.
 * 12. The man who smokes Pall Mall has birds.
 * 13. In the yellow house they smoke Dunhill.
 * 14. In the middle house they drink milk.
 * 15. The Norwegian lives in the First house.
 * 16. The man who smokes Blend lives next door to the house with cats.
 * 17. In the house next to the house with a horse, they smoke Dunhill.
 * 18. The man who smokes Blue Master drinks beer.
 * 19. The German smokes Prince.
 * 20. The Norwegian lives next to the blue house.
 * 21. They drink water in the house next to where they smoke Blend.
 * 22. Who owns the Zebra? 
 *
 *
 */

public class Zebra extends Example {

	
	@Override
	public void model() {

		store = new Store();
		vars = new ArrayList<Variable>();
		
		System.out.println("Program to solve Zebra problem ");

		String[] colorNames = { "red", "green", "white", "yellow", "blue" };
		int ired = 0, igreen = 1, iwhite = 2, iyellow = 3, iblue = 4;
		String[] nationalityNames = { "english", "spaniard", "japanese",
				"italian", "norwegian" };
		int ienglish = 0, ispaniard = 1, ijapanese = 2, iitalian = 3, inorwegian = 4;

		String[] petNames = { "dog", "snails", "fox", "horse", "zebra" };
		int idog = 0, isnails = 1, ifox = 2, ihorse = 3 /*, izebra = 4 */;
		String[] professionNames = { "painter", "sculptor", "diplomat",
				"violinist", "doctor" };
		int ipainter = 0, isculptor = 1, idiplomat = 2, iviolinist = 3, idoctor = 4;
		String[] drinkNames = { "tea", "coffee", "milk", "juice", "water" };
		int itea = 0, icoffee = 1, imilk = 2, ijuice = 3 /* , iwater = 4 */;

		FDV color[] = new FDV[5];
		FDV nationality[] = new FDV[5];
		FDV drink[] = new FDV[5];
		FDV pet[] = new FDV[5];
		FDV profession[] = new FDV[5];

		for (int i = 0; i < 5; i++) {
			color[i] = new FDV(store, colorNames[i], 1, 5);
			pet[i] = new FDV(store, petNames[i], 1, 5);
			drink[i] = new FDV(store, drinkNames[i], 1, 5);
			nationality[i] = new FDV(store, nationalityNames[i], 1, 5);
			profession[i] = new FDV(store, professionNames[i], 1, 5);
			vars.add(color[i]); vars.add(nationality[i]); vars.add(pet[i]);
			vars.add(drink[i]); vars.add(profession[i]);
		}

		store.impose(new Alldifferent(color));
		store.impose(new Alldifferent(pet));
		store.impose(new Alldifferent(drink));
		store.impose(new Alldifferent(nationality));
		store.impose(new Alldifferent(profession));

		// S1 to S10
		store.impose(new XeqY(nationality[ienglish], color[ired]));
		store.impose(new XeqY(nationality[ispaniard], pet[idog]));
		store.impose(new XeqY(nationality[ijapanese], profession[ipainter]));
		store.impose(new XeqY(nationality[iitalian], drink[itea]));
		store.impose(new XeqC(nationality[inorwegian], 1));
		store.impose(new XeqY(color[igreen], drink[icoffee]));
		store.impose(new XplusCeqZ(color[iwhite], 1, color[igreen]));
		store.impose(new XeqY(profession[isculptor], pet[isnails]));
		store.impose(new XeqY(profession[idiplomat], color[iyellow]));
		store.impose(new XeqC(drink[imilk], 3));

		// S11
		store.impose(new XneqY(nationality[inorwegian], color[iblue]));

		store.impose(new Eq(new XplusCeqZ(nationality[inorwegian], 1,
				color[iblue]), new Not(new XplusCeqZ(color[iblue], 1,
				nationality[inorwegian]))));

		// Using reified constraints
		// FDV binary[] = new FDV[2];
		// binary[0] = new FDV(store, "binary1", 0, 1);
		// binary[1] = new FDV(store, "binary2", 0, 1);

		// store.impose(new Reified(new XplusCeqZ(nationality[inorwegian],
		// 1, color[iblue]),
		// binary[0]));

		// store.impose(new Reified(new XplusCeqZ(color[iblue], 1,
		// nationality[inorwegian]),
		// binary[1]));
		// store.impose(new XneqY(binary[0], binary[1]));

		// Using Or constraint
		// store.impose(new Or(new XplusCeqZ(nationality[inorwegian],
		// 1, color[iblue]),
		// new XplusCeqZ(color[iblue],
		// 1, nationality[inorwegian])));

		// S12
		store.impose(new XeqY(profession[iviolinist], drink[ijuice]));

		// S13
		store.impose(new XneqY(pet[ifox], profession[idoctor]));
		store.impose(new Or(new XplusCeqZ(pet[ifox], 1, profession[idoctor]),
				new XplusCeqZ(profession[idoctor], 1, pet[ifox])));

		// S14

		store.impose(new XneqY(pet[ihorse], profession[idiplomat]));
		// store.impose(new Or(new XplusCeqZ(pet[ihorse], 1,
		// profession[idiplomat]),
		// new XplusCeqZ(profession[idiplomat], 1,
		// pet[ihorse])));

		FDV distance3 = new FDV(store, "distance3", -1, 1);
		store.impose(new XplusYeqZ(distance3, pet[ihorse],
				profession[idiplomat]));

		vars.add(distance3);

	}
	
	
	/**
	 * It executes the program to solve this simple logic puzzle.
	 * 
	 * @param args no argument is used.
	 */
	public static void main(String args[]) {

		Zebra example = new Zebra();
		
		example.model();

		if (example.searchMostConstrainedStatic())
			System.out.println("Solution(s) found");
		
	}			
	
	
}
