/**
 *  TSP.java 
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

import JaCoP.constraints.Circuit;
import JaCoP.constraints.Element;
import JaCoP.constraints.Sum;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.MaxRegret;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleMatrixSelect;
import JaCoP.search.SmallestDomain;

/**
 * 
 * It models Travelling Salesman Problem. 
 * 
 * @author Radoslaw Szymanek
 *
 */

public class TSP extends Example {

	FDV[][] varsMatrix;
	
	@Override
	public void model() {

		int noCities = 10;
		
		// Specifies distance between any two cities
		// 1000 - large value to remove possibility of self loop
		int[][] distance = { { 1000, 85, 110, 94, 71, 76, 25, 56, 94, 67 },
				{ 85, 1000, 26, 70, 62, 60, 63, 62, 70, 49 },
				{ 110, 26, 1000, 71, 87, 89, 88, 87, 93, 73 },
				{ 94, 70, 71, 1000, 121, 19, 82, 106, 124, 105 },
				{ 71, 62, 87, 121, 1000, 104, 53, 24, 8, 13 },
				{ 76, 60, 89, 19, 104, 1000, 65, 89, 108, 93 },
				{ 25, 63, 88, 82, 53, 65, 1000, 30, 57, 46 },
				{ 56, 62, 87, 106, 24, 89, 30, 1000, 23, 20 },
				{ 94, 70, 93, 124, 8, 108, 57, 23, 1000, 20 },
				{ 67, 49, 73, 105, 13, 93, 46, 20, 20, 1000 } };
	
		
		// Creating constraint store
		store = new Store();
		varsMatrix = new FDV[noCities][2];
		
		// Denotes a city to go to from
		// index city
		FDV[] cities = new FDV[noCities];
		// Denotes a cost of traveling between
		// index city and next city
		FDV[] costs = new FDV[noCities];

		for (int i = 0; i < cities.length; i++) {
			cities[i] = new FDV(store, "cities[" + (i + 1) + "]", 1,
					cities.length);
			costs[i] = new FDV(store, "costs[" + (i + 1) + "]", 0, 1000);
			varsMatrix[i][0] = costs[i];
			varsMatrix[i][1] = cities[i];
		}

		// Impose cuircuit constraint which makes sure
		// that array cities is a hamiltonian circuit
		store.impose(new Circuit(store, cities));

		// Computes a cost of traveling between ith city
		// and city[i]-th city
		for (int i = 0; i < cities.length; i++) {
			store.impose(new Element(cities[i], distance[i], costs[i]));
		}

		cost = new FDV(store, "Cost", 0, 100000);

		// Computes overall cost of traveling
		// simply sum of all costs
		store.impose(new Sum(costs, cost));

	}

	/**
    *
    * It uses MaxRegret variable ordering heuristic to search for a solution.
    * @return true if there is a solution, false otherwise.
    *
    */
   public boolean searchMaxRegretForMatrixOptimal() {

	   long T1, T2, T;
		T1 = System.currentTimeMillis();

		search = new DepthFirstSearch();
		
	   // pivot variable is at index 0.
       SelectChoicePoint select = new SimpleMatrixSelect (varsMatrix,
                                                   		  new MaxRegret(),
                                                   		  new SmallestDomain(),
                                                   		  new IndomainMin());
       
       boolean result = search.labeling(store, select, cost);

		T2 = System.currentTimeMillis();
		T = T2 - T1;

		if (result)
			System.out.println("Variables : " + vars);
		else
			System.out.println("Failed to find any solution");

		System.out.println("\n\t*** Execution time = " + T + " ms");

		return result;

   }
	/**
	 * It executes the program to solve this Travelling Salesman Problem. 
	 * @param args no argument is used.
	 */
	public static void main(String args[]) {

		TSP example = new TSP();
		
		example.model();

		if ( example.searchMaxRegretForMatrixOptimal())
			System.out.println("Solution(s) found");

	}	
	
}
