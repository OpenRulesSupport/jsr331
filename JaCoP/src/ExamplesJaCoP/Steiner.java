/**
 *  Steiner.java 
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

import JaCoP.constraints.Reified;
import JaCoP.constraints.Sum;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.set.constraints.Card;
import JaCoP.set.constraints.EInS;
import JaCoP.set.constraints.XintersectYeqZ;
import JaCoP.set.core.SetDomain;
import JaCoP.set.search.MaxCardDiff;
import JaCoP.set.search.SetSimpleSelect;
import JaCoP.set.search.SetSimpleSolutionListener;

/**
 * It models and solves Steiner problem.
 *
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 1.7
 */

public class Steiner extends Example {

	public int n;

	Variable[] vs;

	/**
	 * It executes the program which solves this Steiner problem.
	 * @param args
	 */
	public static void main(String args[]) {

		Steiner example = new Steiner();
		example.n = 9;
		example.model();

		example.search();

	}
	
	// n=7, solutions=151200
	public void model () {

		int t = n*(n-1)/6;
		System.out.println("Steiner problem with n="+n+" and T="+t);
		int r = n%6;

		if (r==1 || r==3) {

			store = new Store();

			vars = new ArrayList<Variable>();
			Variable[] s = new Variable[t];

			for (int i = 0; i < t; i++) {
				s[i] = new Variable(store, "s"+i, new SetDomain(1, n));
				vars.add(s[i]);
				store.impose( new Card(s[i], 3));
			}

			for (int i=0; i<t; i++)
				for (int j=i+1; j<t; j++) {
					Variable temp = new Variable(store, "temp"+i+","+j, new SetDomain(1, n));
					store.impose(new XintersectYeqZ(s[i],s[j],temp));
					store.impose( new Card(temp, 0, 1));
				}

			// implied constraints to get better pruning
			for (int i=1; i<=n; i++) {
				Variable[] b = new Variable[t];
				for (int j=0; j<t; j++) {
					b[j] = new Variable(store, "b"+i+","+j, 0, 1);
					store.impose( new Reified(new EInS(i, s[j]), b[j]));
				}
				Variable sum = new Variable(store, "sum_"+i, (n-1)/2, (n-1)/2);
				store.impose(new Sum(b, sum));
			}

		}

	}

	public boolean search() {

		long T1, T2, T;
		T1 = System.currentTimeMillis();

		int r = n % 6;

		if (r==1 || r==3) {

			boolean result = store.consistency();
			System.out.println("*** consistency = " + result);

			Search label = new DepthFirstSearch();

			SelectChoicePoint select = new SetSimpleSelect(vars.toArray(new Variable[vars.size()]), 
					new MaxCardDiff(), 
					new IndomainMin());

			label.setSolutionListener(new SetSimpleSolutionListener());
			label.getSolutionListener().searchAll(false);
			label.getSolutionListener().recordSolutions(false);

			result = label.labeling(store, select);

			if (result) {
				System.out.println("*** Yes");
				label.getSolutionListener().printAllSolutions();
			}
			else
				System.out.println("*** No");

			T2 = System.currentTimeMillis();
			T = T2 - T1;
			System.out.println("\n\t*** Execution time = "+ T + " ms");
			return result;
		}
		else {
			System.out.println("Problem has no solution");
			return false;
		}
		
	}

}
