/**
 *  FurnitureMoving.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2000-2008 Hakan Kjellerstrand and Radoslaw Szymanek
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

import JaCoP.constraints.Sum;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XlteqY;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.constraints.Cumulative;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;
import JaCoP.search.SmallestDomain;

import java.util.*;

/**
  *
  * @author Hakan Kjellerstrand (hakank@bonetmail.com) and Radoslaw Szymanek
  *
  * Problem from Marriott & Stuckey: 'Programming with constraints', page 112f
  *
  * Feature: testing cumulative.
  *
  * Other models of this problem:
  *   Choco (version 1): http://www.hakank.org/constraints/FurnitureMoving.java 
  *   MiniZinc: http://www.hakank.org/minizinc/furniture_moving.mzn
  *
  *
  * Also see http://www.hakank.org/JaCoP/
  * 
  */

public class FurnitureMoving extends Example {
	
	private static final boolean generateAll = true;

    FDV [] starts;
    FDV [] endTimes;
    
	@Override
	public void model() {
		
		store = new Store();

		FDV numPersons = new FDV(store, "numPersons", 2, 5); // will be minimized
		FDV maxTime    = new FDV(store, "maxTime", 60,60);

		// Start times
		FDV Sp = new FDV(store, "Sp", 0, 60); // Piano
		FDV Sc = new FDV(store, "Sc", 0, 60); // Chair 
		FDV Sb = new FDV(store, "Sb", 0, 60); // Bed
		FDV St = new FDV(store, "St", 0, 60); // Table
		FDV sumStartTimes = new FDV(store, "SumStartTimes", 0, 1000);

		starts = new FDV[4];
		starts[0] = Sp;		starts[1] = Sc;		starts[2] = Sb;		starts[3] = St;
		
		store.impose(new Sum(starts, sumStartTimes));

		FDV[] durations     = new FDV[4];
		FDV[] resources     = new FDV[4];
		endTimes      		= new FDV[4];
		
		int durationsInts[] = {30,10,15,15}; // duration of task
		int resourcesInts[] = {3,1,3,2};     // resources: num persons required for each task
		for (int i = 0; i < durationsInts.length; i++) {
			// converts to FDV
			durations[i] = new FDV(store, "dur_"+i, durationsInts[i], durationsInts[i]);
			// converts to FDV
			resources[i] = new FDV(store, "res_"+i, resourcesInts[i], resourcesInts[i]);

			// all tasks must be finished in 60 minutes
			endTimes[i]  = new FDV(store, "end_"+i, 0, 120);
			store.impose(new XplusYeqZ(starts[i], durations[i], endTimes[i]));
			store.impose(new XlteqY(endTimes[i], maxTime));
			
	    }	

	    store.impose(new Cumulative(starts, durations, resources, numPersons));

	    
	    if (generateAll) {
	    	// generate all optimal solutions
	    	store.impose(new XeqC(numPersons, 3));
	    }
	    
	    vars = new ArrayList<Variable>();
	    
	    for(FDV s: starts) 
	    	vars.add(s);

	    for(FDV e: endTimes) 
	        vars.add(e);

	    vars.add(numPersons);     
	    
	    cost = numPersons;
	        
	}


	/**
	 * It executes the program which solves this logic puzzle.
	 * @param args
	 */
	public static void main(String args[]) {

		long T1, T2, T;
		T1 = System.currentTimeMillis();

		FurnitureMoving example = new FurnitureMoving();
		example.model();
		
		example.searchSpecific();
		
		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");
	}
	
	
    /**
     * It specifies search for that logic puzzle. 
     */
    public void searchSpecific() {

        SelectChoicePoint select = new SimpleSelect (vars.toArray(new Variable[1]),
                                                     new SmallestDomain(),
                                                     new IndomainMin ());
        
        Search label = new DepthFirstSearch ();
        label.getSolutionListener().searchAll(true);
        label.getSolutionListener().recordSolutions(true);

        boolean result;
        if (generateAll) {
            // Generate all optimal solutions. 
            // Note: Gives null pointer exception when searchAll(true)
            result = label.labeling(store, select); 
        } else {
            // minimize over numPersons
            result = label.labeling(store, select, cost); 
        }

        Variable[] variables = label.getSolutionListener().getVariables();
        for(int i = 0; i < variables.length; i++) {
            System.out.println("Variable " + i + " " + variables[i]);
        }

        if(result) {
            
            label.printAllSolutions();

            System.out.println("\nNumber of persons needed: " + cost.value());
            System.out.println("Piano: " + starts[0].value() + ".." + endTimes[0].value() + "\n" +
                               "Chair: " + starts[1].value() + ".." + endTimes[1].value() + "\n" +
                               "Bed  : " + starts[2].value() + ".." + endTimes[2].value() + "\n" + 
                               "Table: " + starts[3].value() + ".." + endTimes[3].value());

                               
            
        } // end if result

    } // end main
	
	
} // end class FurnitureMoving
