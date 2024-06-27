/**
 *  Langford.java 
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

import JaCoP.constraints.Alldiff;
import JaCoP.constraints.Alldistinct;
import JaCoP.constraints.Assignment;
import JaCoP.constraints.Constraint;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.core.BoundDomain;
import JaCoP.core.FDV;
import JaCoP.core.FDstore;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * 
 * @author Radoslaw Szymanek
 * @version 2.4
 */

public class Langford extends Example {

	static int n = 3;
	static int m = 17;

	@Override
	public void model() {

		store = new Store();
		vars = new ArrayList<Variable>();
		
		// Get problem size n from second program argument.
		FDV[] x = new FDV[n * m];

		for (int i = 0; i < n * m; i++) {
			x[i] = new FDV(store, "x" + i, 1, m * n);
			vars.add(x[i]);
		}

		for (int i = 0; i + 1 < n; i++) {
			for (int j = 0; j < m; j++) {

				store.impose(new XplusCeqZ(x[i * m + j], (j + 2), x[(i + 1) * m
						+ j]));

			}
		}

		Constraint cx = new Alldistinct(x);
		store.impose(cx);
		
	}

	/**
	 * It uses BoundDomain for all variables.
	 */
	public void modelBound() {

		store = new Store();
		vars = new ArrayList<Variable>();
		
		// Get problem size n from second program argument.
		Variable[] x = new Variable[n * m];

		for (int i = 0; i < n * m; i++) {
			x[i] = new Variable(store, "x" + i, new BoundDomain(1, m * n) );
			vars.add(x[i]);
		}

		for (int i = 0; i + 1 < n; i++) {
			for (int j = 0; j < m; j++) {
				store.impose(new XplusCeqZ(x[i * m + j], (j + 2), x[(i + 1) * m	+ j]));
			}
		}

		Constraint cx = new Alldiff(x);
		store.impose(cx);
		
	}
	
	
	/**
	 * It uses the dual model.
	 */
	public void modelDual() {

		store = new FDstore();
		vars = new ArrayList<Variable>();
		
		FDV[] x = new FDV[n * m];

		for (int i = 0; i < n * m; i++) {
			x[i] = new FDV(store, "x" + i, 0, m * n - 1);
			vars.add(x[i]);
		}

		for (int i = 0; i + 1 < n; i++) {
			for (int j = 0; j < m; j++) {

				store.impose(new XplusCeqZ(x[i * m + j], (j + 2), x[(i + 1) * m + j]));

			}
		}

		Constraint cx = new Alldistinct(x);
		store.impose(cx);

		FDV[] d = new FDV[n * m];

		for (int i = 0; i < n * m; i++) {
			d[i] = new FDV(store, "d" + i, 0, m * n - 1);
			vars.add(d[i]);
		}

		store.impose(new Assignment(x, d));
				
	}
	
	/**
	 * It executes the program to solve the Langford problem.
	 * It is possible to specify two parameters. If no 
	 * parameter is used then default values for n and m are used.
	 * 
	 * @param args the first parameter denotes n, the second parameter denotes m.
	 */
	public static void main(String args[]) {

		Langford example = new Langford();
		if (args.length > 1) {
			Langford.n = new Integer(args[0]);
			Langford.m = new Integer(args[1]);
		}	
		
		example.model();

		if (example.search())
			System.out.println("Solution(s) found");
		
		Langford exampleBound = new Langford();
		if (args.length > 1) {
			Langford.n = new Integer(args[0]);
			Langford.m = new Integer(args[1]);
		}
		
		exampleBound.modelBound();

		if (exampleBound.search())
			System.out.println("Solution(s) found");		
		
		
		Langford exampleDual = new Langford();
		if (args.length > 1) {
			Langford.n = new Integer(args[0]);
			Langford.m = new Integer(args[1]);
		}
		exampleDual.modelDual();
		
		if (exampleDual.search())
			System.out.println("Solution(s) found");
		
	}	
		
	
}
