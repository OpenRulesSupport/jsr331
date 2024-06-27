/**
 *  LoadSolveXML.java 
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

import JaCoP.core.FDstore;
import JaCoP.core.Variable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;
import JaCoP.search.WeightedDegree;

/**
 * @author Radoslaw Szymanek
 * 
 * Simple example of loading file containing XCSP based problem description and
 * applying weighted degree variable ordering search heuristic to solve the problem.
 * 
 */
public class LoadSolveXML {

	/**
	 * It loads given problem specified in XCSP format in a file given as first argument. 
	 * It uses weighted degree variable ordering heuristic to solve the problem.
	 * 
	 * @param args filename containing XCSP description of the problem.
	 */
	public static void main(String args[]) {

		if (args.length != 1) {
			System.out.println("Program has to be supplied with a parameter(s) - [path] filename");
			System.exit(-1);
		}

		FDstore store = new FDstore();

		long T1, T2, T;
		T1 = System.currentTimeMillis();

		if (args.length == 2)
			store.fromXCSP2_0(args[0], args[1]);
		else
			store.fromXCSP2_0("", args[0]);

		ArrayList<Variable> fdvV = new ArrayList<Variable>();

		for (Variable v : store.vars)
			if (v != null)
				fdvV.add(v);

		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Loading time = " + T + " ms");

		Search label = new DepthFirstSearch();

		Variable[] vars = new Variable[fdvV.size()];
		int i = 0;
		for (Variable v : fdvV)
			vars[i++] = v;

		SelectChoicePoint select = new SimpleSelect(vars, 
				new WeightedDegree(),
				new IndomainMin());

		((SimpleSelect)select).inputOrderTieBreaking = false;
		store.variableWeightManagement = true;
		
		boolean result = label.labeling(store, select);

		if (result)
			System.out.println("\nSolution has been found.");
		
		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");

	}

}
