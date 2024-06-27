/**
 *  Examples.java 
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

import JaCoP.constraints.Constraint;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.search.CreditCalculator;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMiddle;
import JaCoP.search.IndomainMin;
import JaCoP.search.IndomainSimpleRandom;
import JaCoP.search.LDS;
import JaCoP.search.MaxRegret;
import JaCoP.search.MostConstrainedStatic;
import JaCoP.search.NoGoodsCollector;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.Shaving;
import JaCoP.search.SimpleSelect;
import JaCoP.search.SmallestDomain;
import JaCoP.search.SmallestMin;
import JaCoP.search.WeightedDegree;

import java.util.*;

/**
 * It is an abstract class to describe all necessary functions of any store.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public abstract class Example {

	/**
	 * It contains all variables used within a specific example.
	 */
	public ArrayList<Variable> vars;
	
	/**
	 * It specifies the cost function, null if no cost function is used.
	 */
	public Variable cost;
	
	/**
	 * It specifies the constraint store responsible for holding information 
	 * about constraints and variables.
	 */
	public Store store;
	
	/**
	 * It specifies the search procedure used by a given example.
	 */
	public Search search;	
	
	/**
	 * It specifies a standard way of modeling the problem.
	 */
	public abstract void model();
	
	/**
	 * It specifies simple search method based on input order and lexigraphical 
	 * ordering of values. 
	 * 
	 * @return true if there is a solution, false otherwise.
	 */
	public boolean search() {
		
		long T1, T2;
		T1 = System.currentTimeMillis();
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), null,
				new IndomainMin());

		search = new DepthFirstSearch();

		boolean result = search.labeling(store, select);

		if (result)
			store.print();

		T2 = System.currentTimeMillis();

		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
		
		System.out.println();
		System.out.print(search.getNodes() + "\t");
		System.out.print(search.getDecisions() + "\t");
		System.out.print(search.getWrongDecisions() + "\t");
		System.out.print(search.getBacktracks() + "\t");
		System.out.print(search.getMaximumDepth() + "\t");
		
		return result;
		
	}	

	/**
	 * It specifies simple search method based on input order and lexigraphical 
	 * ordering of values. It optimizes the solution by minimizing the cost function.
	 * 
	 * @return true if there is a solution, false otherwise.
	 */
	public boolean searchOptimal() {
		
		long T1, T2;
		T1 = System.currentTimeMillis();
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), null,
				new IndomainMin());

		search = new DepthFirstSearch();
		
		boolean result = search.labeling(store, select, cost);

		if (result)
			store.print();

		T2 = System.currentTimeMillis();

		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
		
		return result;
		
	}	
	
	
	/**
	 * It searches for all solutions with the optimal value.
	 * @return true if any optimal solution has been found.
	 */
	public boolean searchAllOptimal() {
		
		long T1, T2, T;
		T1 = System.currentTimeMillis();

		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), null,
													new IndomainMin());

		search = new DepthFirstSearch();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);

		boolean result = search.labeling(store, select, cost);

		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");

		return result;
		
	}
	
	/**
	 * It specifies simple search method based on smallest domain variable order 
	 * and lexigraphical ordering of values. 
	 * @param optimal it specifies if the search the optimal solution takes place.
	 * 
	 * @return true if there is a solution, false otherwise.
	 */

	public boolean searchSmallestDomain(boolean optimal) {
		
		long T1, T2;
		T1 = System.currentTimeMillis();
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), new SmallestDomain(),
				new IndomainMin());

		search = new DepthFirstSearch();

		boolean result = false;
		
		if (optimal) 
			search.labeling(store, select, cost);
		else
			search.labeling(store, select);

		System.out.println();
		System.out.print(search.getNodes() + "\t");
		System.out.print(search.getDecisions() + "\t");
		System.out.print(search.getWrongDecisions() + "\t");
		System.out.print(search.getBacktracks() + "\t");
		System.out.print(search.getMaximumDepth() + "\t");
		
		if (result)
			store.print();

		T2 = System.currentTimeMillis();

		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
		
		return result;
		
	}	
	
	/**
	 * It specifies simple search method based on weighted degree variable order 
	 * and lexigraphical ordering of values. This search method is rather general
	 * any problem good fit. It can be a good first trial to see if the model is 
	 * correct.
	 * 
	 * @return true if there is a solution, false otherwise.
	 */

	public boolean searchWeightedDegree() {
		
		long T1, T2;
		T1 = System.currentTimeMillis();
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), new WeightedDegree(),
				new IndomainMin());

		search = new DepthFirstSearch();

		boolean result = search.labeling(store, select);

		System.out.println();
		System.out.print(search.getNodes() + "\t");
		System.out.print(search.getDecisions() + "\t");
		System.out.print(search.getWrongDecisions() + "\t");
		System.out.print(search.getBacktracks() + "\t");
		System.out.print(search.getMaximumDepth() + "\t");
		
		if (result)
			store.print();

		T2 = System.currentTimeMillis();

		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");
		
		return result;
		
	}	

	/**
	 * It specifies simple search method based variable order which 
	 * takes into account the number of constraints attached to a variable 
	 * and lexigraphical ordering of values. 
	 * 
	 * @return true if there is a solution, false otherwise.
	 */

	public boolean searchMostConstrainedStatic() {
		
		search = new DepthFirstSearch();

		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]),
				new MostConstrainedStatic(), new IndomainMin());

		boolean result = search.labeling(store, select);

		System.out.println();
		System.out.print(search.getNodes() + "\t");
		System.out.print(search.getDecisions() + "\t");
		System.out.print(search.getWrongDecisions() + "\t");
		System.out.print(search.getBacktracks() + "\t");
		System.out.print(search.getMaximumDepth() + "\t");
		
		if (!result)
			System.out.println("**** No Solution ****");

		return result;
	}
	
	/**
	 * It specifies simple search method based on most constrained static and lexigraphical 
	 * ordering of values. It searches for all solutions.
	 * 
	 * @return true if there is a solution, false otherwise.
	 */

	public boolean searchAllAtOnce() {
		
		long T1, T2;
		T1 = System.currentTimeMillis();		
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]),
				new MostConstrainedStatic(), new IndomainMin());

		search = new DepthFirstSearch();
		
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		search.setAssignSolution(true);
		
		boolean result = search.labeling(store, select);

		T2 = System.currentTimeMillis();

		if (result) {
			System.out.println("Number of solutions " + search.getSolutionListener().solutionsNo());
			search.printAllSolutions();
		} 
		else
			System.out.println("Failed to find any solution");

		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");

		return result;
	}	
	
	
	/**
	 * It searches using an input order search with indomain based on middle value.
	 * @return true if there is a solution, false otherwise.
	 */
	public boolean searchMiddle() {

		long begin = System.currentTimeMillis();

		search = new DepthFirstSearch();

		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), 
													null, new IndomainMiddle());

		boolean result = search.labeling(store, select);

		long end = System.currentTimeMillis();

		System.out.println("Number of milliseconds " + (end - begin));

		return result;
	}

	/**
	 * It searches with shaving which is guided by supplied constraints.
	 * @param guidingShaving the array of constraints proposing shaving candidates.
	 * @param printInfo it specifies if that function should print any info.
	 * @return true if the solution was found, false otherwise.
	 */
	public boolean shavingSearch(ArrayList<Constraint> guidingShaving, boolean printInfo) {

		Shaving shaving = new Shaving();
		shaving.setStore(store);
		shaving.quickShave = true;

		for (Constraint c : guidingShaving)
			shaving.addShavingConstraint(c);

		long begin = System.currentTimeMillis();

		search = new DepthFirstSearch();
		search.setPrintInfo(printInfo);
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), null, new IndomainMiddle());

		search.setConsistencyListener(shaving);
		search.setExitChildListener(shaving);

		boolean result = search.labeling(store, select);

		long end = System.currentTimeMillis();

		if (printInfo) {
			System.out.println("Number of milliseconds " + (end - begin));
			System.out.println("Ratio "	+ (shaving.successes * 100 / (shaving.successes + shaving.failures)));

			if (result)
				store.print();
		}
		
		return result;

	}
	
	/**
	 * It conducts the search with restarts from which the no-goods are derived. 
	 * Every search contributes with new no-goods which are kept so eventually
	 * the search is complete (although can be very expensive to maintain explicitly
	 * all no-goods found during search). 
	 * @return true if there is a solution, false otherwise.
	 */
	public boolean searchWithRestarts() {
		
		// Input Order tie breaking
		boolean result = false;
		boolean timeout = true;
		
		int nodes = 0;
		int decisions = 0;
		int backtracks = 0;
		int wrongDecisions = 0;

		search = new DepthFirstSearch();

		NoGoodsCollector collector = new NoGoodsCollector();
		search.setExitChildListener(collector);
		search.setTimeOutListener(collector);
		search.setExitListener(collector);

		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), new SmallestDomain(),
				new IndomainSimpleRandom());
		
		while (timeout) {

			// search.setPrintInfo(false);
			search.setNodesOut(1000);

			result = search.labeling(store, select);
			timeout &= collector.timeOut;
			
			nodes += search.getNodes();
			decisions += search.getDecisions();
			wrongDecisions += search.getWrongDecisions();
			backtracks += search.getBacktracks();

			search = new DepthFirstSearch();
			collector = new NoGoodsCollector();
			search.setExitChildListener(collector);
			search.setTimeOutListener(collector);
			search.setExitListener(collector);

		}

		// System.out.println(search.noGoodsVariables);
		// System.out.println(search.noGoodsValues);
		// System.out.println(store.watchedConstraints);

		// store.toXCSP2_0();
		// store.finalizeXCSP2_0(-1, "./", "restarts.xml");

		System.out.println();
		System.out.print(nodes + "\t");
		System.out.print(decisions + "\t");
		System.out.print(wrongDecisions + "\t");
		System.out.print(backtracks + "\t");

		if (result)
			System.out.println(1);
		else
			System.out.println(0);

		return result;
	}
	
	/**
	 * It uses credit search to solve a problem.
	 * @param credits the number of credits available.
	 * @param backtracks the maximum number of backtracks used when a path exhausts its credits.
	 * @param maxDepth the maximum depth to which the credit distribution takes place.
	 * @return true if a solution was found, false otherwise.
	 */
	public boolean creditSearch(int credits, int backtracks, int maxDepth) {

		SelectChoicePoint select = new SimpleSelect(vars
				.toArray(new Variable[1]), new SmallestDomain(),
				new IndomainMin());

		CreditCalculator credit = new CreditCalculator(credits, backtracks, maxDepth);

		search = new DepthFirstSearch();

		if (search.getConsistencyListener() == null)
			search.setConsistencyListener(credit);
		else
			search.getConsistencyListener().setChildrenListeners(credit);

		search.setExitChildListener(credit);
		search.setTimeOutListener(credit);

		boolean result = search.labeling(store, select);

		store.print();

		System.out.print(search.getNodes() + "\t");
		System.out.print(search.getDecisions() + "\t");
		System.out.print(search.getWrongDecisions() + "\t");
		System.out.print(search.getBacktracks() + "\t");
		System.out.print(search.getMaximumDepth() + "\t");

		if (result)
			System.out.println(1);
		else
			System.out.println(0);

		return result;
	}

	
    /**
    *
    * It uses MaxRegret variable ordering heuristic to search for a solution.
    * @return true if there is a solution, false otherwise.
    *
    */
   public boolean searchWithMaxRegret() {

       SelectChoicePoint select = new SimpleSelect (vars.toArray(new Variable[0]),
                                                   	new MaxRegret(),
                                                   	new SmallestDomain(),
                                                    new IndomainMiddle());
       
       search = new DepthFirstSearch();
       search.getSolutionListener().searchAll(true);
       search.getSolutionListener().recordSolutions(true);
       
       boolean result = search.labeling(store, select);
       
	   return result;

   }
	
   /**
    * It searches for solution using Limited Discrepancy Search.
    * @param noDiscrepancy maximal number of discrepancies
    * @return true if the solution was found, false otherwise.
    */
   public boolean searchLDS(int noDiscrepancy) {
		
		search = new DepthFirstSearch();

		boolean result = false;

		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]),
				new SmallestDomain(), new IndomainMiddle());

		LDS lds = new LDS(noDiscrepancy);

		if (search.getExitChildListener() == null)
			search.setExitChildListener(lds);
		else
			search.getExitChildListener().setChildrenListeners(lds);

		// Execution time measurement
		long begin = System.currentTimeMillis();

		result = search.labeling(store, select);

		// Execution time measurement
		long end = System.currentTimeMillis();

		System.out.println("Number of milliseconds " + (end - begin));

		return result;
		
	}
   
   
 /**
  * It searches for optimal solution using max regret variable ordering
  * and indomain min for value ordering.
 * @return true if there is a solution, false otherwise.
 */
   public boolean searchMaxRegretOptimal() {
		
		long T1, T2, T;
		T1 = System.currentTimeMillis();

		search = new DepthFirstSearch();

		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), new MaxRegret(),
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
    * It searches using smallest domain variable ordering and 
    * indomain middle value ordering. 
    * @return true if there is a solution, false otherwise.
    */
   public boolean searchSmallestMiddle() {
		
		long begin = System.currentTimeMillis();

		boolean result = false;

		search = new DepthFirstSearch();

		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), new SmallestDomain(),
				new IndomainMiddle());

		result = search.labeling(store, select);

		long end = System.currentTimeMillis();


		System.out.println("Number of milliseconds " + (end - begin));
		
		return result;
		
	}
   
	/**
	 * It searches using Smallest Min variable ordering heuristic and indomainMin value
	 * ordering heuristic.
	 * 
	 * @return true if there is a solution, false otherwise.
	 */
	public boolean searchSmallestMin() { 

		long begin = System.currentTimeMillis();
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), new SmallestMin(),
				new IndomainMin());

		search = new DepthFirstSearch();
		
		boolean solution = search.labeling(store, select, cost);

		long end = System.currentTimeMillis();

		if (solution)
			store.print();
		else
			System.out.println("Failed to find any solution");

		System.out.println("Number of milliseconds " + (end - begin));

		return solution;
	
	}

	
	/**
	 * It conducts master-slave search. Both of them use input order variable ordering. 
	 * 
	 * @param masterVars it specifies the search variables used in master search.
	 * @param slaveVars it specifies the search variables used in slave search.
	 * 
	 * @return true if the solution exists, false otherwise.
	 */
	public boolean searchMasterSlave(ArrayList<Variable> masterVars, 
									 ArrayList<Variable> slaveVars) {

		long T1 = System.currentTimeMillis();

		boolean result = false;

		Search labelSlave = new DepthFirstSearch();
		SelectChoicePoint selectSlave = new SimpleSelect(slaveVars.toArray(new Variable[0]), null,
				new IndomainMin());
		labelSlave.setSelectChoicePoint(selectSlave);

		Search labelMaster = new DepthFirstSearch();
		SelectChoicePoint selectMaster = new SimpleSelect(masterVars.toArray(new Variable[0]), null,
				new IndomainMin());
		
		labelMaster.addChildSearch(labelSlave);

		search = labelMaster;
		
		result = labelMaster.labeling(store, selectMaster);

		if (result)
			System.out.println("Solution found");

		if (result)
			store.print();

		long T2 = System.currentTimeMillis();
		
		System.out.println("\n\t*** Execution time = " + (T2 - T1) + " ms");

		return result;
	}

	
	/**
	 * It returns the search used within an example.
	 * @return the search used within an example.
	 */
	public Search getSearch() {
		return search;
	}

	/**
	 * It specifies the constraint store used within an example.
	 * @return constraint store used within an example.
	 */
	public Store getStore() {
		return store;
	}

	/**
	 * It returns an array list of variables used to model the example.
	 * @return the array list of variables used to model the example.
	 */
	public ArrayList<Variable> getSearchVariables() {
		return vars;
	}	
	
	/**
	 * It saves current problem in XML. First parameter
	 * noSolutions specifies number of solutions. If number
	 * of solutions is not known then use value -1. Path can 
	 * contain strings like "./". 
	 * @param noSolutions it informs about the number of solution for this example.
	 * @param path it specifies the path to the file containing XCSP representation of the example.
	 * @param name it specifies the filename of the file containing XCSP representation of the example.
	 */
	public void toXML(int noSolutions, String path, String name) {
		store.toXCSP2_0();
		store.finalizeXCSP2_0(noSolutions, path, name);	
	}
	
}
