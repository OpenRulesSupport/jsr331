/**
 *  ArchFriends.java 
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
import JaCoP.constraints.Not;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XeqY;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * 
 * @author Adam Plonka, Piotr Ogrodzki, and Radoslaw Szymanek
 * 
 * Logic Puzzle

 * Title       : Arch Friends
 * Author      : Mark T. Zegarelli
 * Publication : Dell Logic Puzzles
 * Issue       : April, 1998
 * Page        : 7
 * Stars       : 1

 * Description : 

 * Harriet, upon returning from the mall, is happily describing her
 * four shoe purchases to her friend Aurora. Aurora just loves the four
 * different kinds of shoes that Harriet bought (ecru espadrilles,
 * fuchsia flats, purple pumps, and suede sandals), but Harriet can't
 * recall at which different store (Foot Farm, Heels in a Handcart, The
 * Shoe Palace, or Tootsies) she got each pair. Can you help these two
 * figure out the order in which Harriet bought each pair of shoes, and
 * where she bought each?
 */

public class ArchFriends extends Example {
	
	@Override
	public void model() {

		vars = new ArrayList<Variable>();
		store = new Store();

		System.out.println("Program to solve ArchFriends problem ");

		// Declaration of constants (names, variables' indexes

		String[] shoeNames = { "EcruEspadrilles", "FuchsiaFlats",
				"PurplePumps", "SuedeSandals" };

		int /* iEcruEspadrilles = 0, */ iFuchsiaFlats = 1, iPurplePumps = 2, iSuedeSandals = 3;

		String[] shopNames = { "FootFarm", "HeelsInAHandcart", "TheShoePalace",
				"Tootsies" };

		int iFootFarm = 0, iHeelsInAHandcart = 1, iTheShoePalace = 2, iTootsies = 3;

		// Variables shoe and shop

		Variable shoe[] = new Variable[4];
		Variable shop[] = new Variable[4];

		// Each variable has a domain 1..4 as there are four different
		// shoes and shops. Values 1 to 4 within variables shoe
		// denote the order in which the shoes were bought.

		for (int i = 0; i < 4; i++) {
			shoe[i] = new Variable(store, shoeNames[i], 1, 4);
			shop[i] = new Variable(store, shopNames[i], 1, 4);
		}

		for (Variable v : shoe) vars.add(v);
		for (Variable v : shop) vars.add(v);
		
		
		// Each shoe, shop have to have a unique identifier.
		store.impose(new Alldifferent(shoe));
		store.impose(new Alldifferent(shop));

		// Constraints given in the problem description.

		// 1. Harriet bought fuchsia flats at Heels in a Handcart.
		store.impose(new XeqY(shoe[iFuchsiaFlats], shop[iHeelsInAHandcart]));

		// 2.The store she visited just after buying her purple pumps
		// was not Tootsies.

		// Nested constraint by applying constraint Not to constraint XplusCeqZ
		store.impose(new Not(new XplusCeqZ(shoe[iPurplePumps], 1,
				shop[iTootsies])));

		// 3. The Foot Farm was Harriet's second stop.
		store.impose(new XeqC(shop[iFootFarm], 2));

		// 4. Two stops after leaving The Shoe Place, Harriet
		// bought her suede sandals.
		store.impose(new XplusCeqZ(shop[iTheShoePalace], 2,
			         shoe[iSuedeSandals]));

	}

	/**
	 * It executes the program to solve the logic puzzle.
	 * @param args no arguments are read.
	 */
	public static void main(String args[]) {

		ArchFriends example = new ArchFriends();
		
		example.model();

		if (example.searchAllAtOnce())
			System.out.println("Solution(s) found");
		
	}

}
