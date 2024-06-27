/**
 *  DonaldGeraldRobert.java 
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
import JaCoP.constraints.SumWeight;
import JaCoP.constraints.XneqC;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/** 
 * 
 * It specifies the logic puzzle (cryptogram) which need to satisfy the following 
 * equation that DONALD+GERALD=ROBERT. The solution is provided on the right.
 * 
 * Donald 		  526485
 * Gerald =====> +197485
 * Robert         723970
 *
 * @author Radoslaw Szymanek
 */

public class DonaldGeraldRobert extends Example {

	@Override
	public void model() {

		System.out.println("Program to solve Donald+Gerald=Robert problem ");

		// Creating constraint store
		store = new Store();
		vars = new ArrayList<Variable>();

		FDV d = new FDV(store, "d", 0, 9);
		FDV o = new FDV(store, "d", 0, 9);
		FDV n = new FDV(store, "d", 0, 9);
		FDV a = new FDV(store, "d", 0, 9);
		FDV l = new FDV(store, "d", 0, 9);
		FDV g = new FDV(store, "d", 0, 9);
		FDV e = new FDV(store, "d", 0, 9);
		FDV r = new FDV(store, "d", 0, 9);
		FDV b = new FDV(store, "d", 0, 9);
		FDV t = new FDV(store, "d", 0, 9);
		
		FDV[] donald = {d, o, n, a, l, d};
		FDV[] gerald = {g, e, r, a, l, d};
		FDV[] robert = {r, o, b, e, r, t};

		FDV[] digits = {d, o, n, a, l, g, e, r, b, t};
		
		for (Variable v : digits)
			vars.add(v);
		
		// Imposing inequalities constraints between letters
		store.impose(new Alldifferent(digits));

		int[] weights = {100000, 10000, 1000, 100, 10, 1};
		
		FDV donaldValue = new FDV(store, "Donald", 0, 999999);
		FDV geraldValue = new FDV(store, "Gerald", 0, 999999);
		FDV robertValue = new FDV(store, "Robert", 0, 999999);

		// Constraints for getting value for words
		store.impose(new SumWeight(donald, weights, donaldValue));
		store.impose(new SumWeight(gerald, weights, geraldValue));
		store.impose(new SumWeight(robert, weights, robertValue));

		// Equation
		store.impose(new XplusYeqZ(donaldValue, geraldValue, robertValue));

		// Since T is D+D mod 10 then T must be even or 5,
		// additional reasoning.

		store.impose(new XneqC(robert[5], 1));
		store.impose(new XneqC(robert[5], 3));
		store.impose(new XneqC(robert[5], 5));
		store.impose(new XneqC(robert[5], 7));
		store.impose(new XneqC(robert[5], 9));

		// // First letter of every word can not equal 0
		store.impose(new XneqC(donald[0], 0));
		store.impose(new XneqC(gerald[0], 0));
		store.impose(new XneqC(robert[0], 0));

	}
	
	/**
	 * It executes the program to solve cryptogram puzzle
	 * DONALD+GERALD=ROBERT.
	 * @param args no argument is used.
	 */
	public static void main(String args[]) {

		DonaldGeraldRobert example = new DonaldGeraldRobert();
		
		example.model();
		
		if (example.searchSmallestDomain(false))
			System.out.println("Solution(s) found");
		
	}
	
}
