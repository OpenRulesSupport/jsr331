/**
 *  Gates.java 
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

import JaCoP.constraints.ExtensionalSupportVA;
import JaCoP.core.BooleanVariable;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.MostConstrainedStatic;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;

/**
 * The class Run is used to run test programs for JaCoP package. It is used for
 * test purpose only.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version alfa
 */

public class Gates extends Example {

	@Override
	public void model() {

		store = new Store();
		vars = new ArrayList<Variable>();

		BooleanVariable a = new BooleanVariable(store, "a");
		BooleanVariable b = new BooleanVariable(store, "b");
		BooleanVariable c = new BooleanVariable(store, "c");
		BooleanVariable sum = new BooleanVariable(store, "sum");
		BooleanVariable carry = new BooleanVariable(store, "carry");
		vars.add(a); vars.add(b); vars.add(c); vars.add(sum); vars.add(carry);
		
		BooleanVariable nca = new BooleanVariable(store, "nca");

		BooleanVariable[] t = new BooleanVariable[2];
		for (int i = 0; i < t.length; i++)
			t[i] = new BooleanVariable(store);

		// sum part
		xor(c, nca, sum);
		xor(a, b, nca);

		// carry part
		or(t[0], t[1], carry);
		and(c, nca, t[1]);
		and(a, b, t[0]);

		System.out.println("\nBooleanVariable store size: " + store.size()
				+ "\nNumber of constraints: " + store.numberConstraints());

	}

	/**
	 * It imposes an extensional constraint enforcing an and relationship
	 * between two input parameters and an output parameter.
	 * @param in1 the first input parameter.
	 * @param in2 the second input parameter.
	 * @param out the output parameter.
	 */
	public void and(BooleanVariable in1, BooleanVariable in2,
					BooleanVariable out) {

		int[][] tuples = { { 0, 0, 0 }, { 0, 1, 0 }, { 1, 0, 0 }, { 1, 1, 1 } };

		store.impose(new ExtensionalSupportVA(new BooleanVariable[] { in1, in2,
				out }, tuples));

	}

	/**
	 * It imposes an extensional constraint enforcing an or relationship
	 * between two input parameters and an output parameter.
	 * @param in1 the first input parameter.
	 * @param in2 the second input parameter.
	 * @param out the output parameter.
	 */
	public void or(BooleanVariable in1, BooleanVariable in2, BooleanVariable out) {

		int[][] tuples = { { 0, 0, 0 }, { 0, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 } };

		store.impose(new ExtensionalSupportVA(new BooleanVariable[] { in1, in2,
				out }, tuples));
	}

	/**
	 * It imposes an extensional constraint enforcing an xor relationship
	 * between two input parameters and an output parameter.
	 * @param in1 the first input parameter.
	 * @param in2 the second input parameter.
	 * @param out the output parameter.
	 */
	public void xor(BooleanVariable in1, BooleanVariable in2, BooleanVariable out) {

		int[][] tuples = { { 0, 0, 0 }, { 0, 1, 1 }, { 1, 0, 1 }, { 1, 1, 0 } };

		store.impose(new ExtensionalSupportVA(new BooleanVariable[] { in1, in2,
				out }, tuples));
	}

	/**
	 * It imposes an extensional constraint enforcing an not relationship
	 * between input parameter and an output parameter.
	 * @param in the first input parameter.
	 * @param out the output parameter.
	 */
	public void not(BooleanVariable in, BooleanVariable out) {

		int[][] tuples = { { 0, 1 }, { 1, 0 } };

		store.impose(new ExtensionalSupportVA(new BooleanVariable[] { in, out },
				tuples));
	}


	/**
	 * It executes a program to solve gates problems.
	 * @param args
	 */
	public static void main(String args[]) {

		long T1, T2, T;
		T1 = System.currentTimeMillis();

		Gates example = new Gates();
		example.model();

		if ( example.searchSpecific() )
			System.out.println("Solution found.");

		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");
	}


	/**
	 * It provides a specific search with extensive printout of the result.
	 * @return true if there is a solution, false otherwise.
	 */
	public boolean searchSpecific() {
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]),
													new MostConstrainedStatic(), 
													new IndomainMin());

		search = new DepthFirstSearch();

		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);

		boolean searchResult = search.labeling(store, select);

		if (searchResult) {
			System.out.println("\nYes");
			int[][] solutions = new int[search.getSolutionListener()
					.solutionsNo()][];
			for (int i = 1; i <= solutions.length; i++)
				solutions[i - 1] = search.getSolution(i);

			System.out.println("\nAll solutions:\n");
			for (Variable v : vars)
				System.out.print(v.id() + "\t");
			System.out.println("\n-------------------------------------");
			for (int j = 0; j < solutions.length; j++) {
				for (int i = 0; i < solutions[0].length; i++)
					System.out.print(solutions[j][i] + "\t");
				System.out.println();
			}
		} else
			System.out.println("\nNo");
		
		return searchResult;

	}	
	
}
