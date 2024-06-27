/**
 *  QCP.java 
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import JaCoP.constraints.Alldistinct;
import JaCoP.constraints.Constraint;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMiddle;
import JaCoP.search.IndomainMin;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.Shaving;
import JaCoP.search.SimpleSelect;
import JaCoP.search.SmallestDomain;
import JaCoP.search.TransformExtensional;

/**
 * 
 * @author Radoslaw Szymanek
 * @version alfa
 */

public class QCP extends Example {

	// It uses correct InputOrder tie breaking (lex)

	/**
	 * It specifies the file containing the description of the problem.
	 */
	public String filename = "./ExamplesJaCoP/psqwh-25-235-0081.pls";
	                         
	/**
	 * It contains constraints which can be used to guide shaving. 
	 */
	public ArrayList<Constraint> shavingConstraints = new ArrayList<Constraint>();
	

	
	/**
	 * It contains the order of the QCP being solved.
	 */
	public int n = 0;
	
	@Override
	public void model() {

		String lines[] = new String[100];
		
		/* read from file args[0] or qcp.txt */
		try {

			BufferedReader in = new BufferedReader(new FileReader(filename));
			String str;

			while ((str = in.readLine()) != null) {
				lines[n] = str;
				n++;
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("I can not find file " + filename);
		} catch (IOException e) {
			System.err.println("Something is wrong with file" + filename);
		}

		n = n - 1;
		/* Creating constraint store */
		int numbers[][] = new int[n][n];

		// Transforms strings into ints
		for (int i = 1; i < n + 1; i++) {
			Pattern pat = Pattern.compile(" ");
			String[] result = pat.split(lines[i]);

			int current = 0;
			for (int j = 0; j < result.length; j++)
				try {
					int currentNo = new Integer(result[j]);
					numbers[i - 1][current++] = currentNo;
				} catch (Exception ex) {

				}
		}

		store = new Store();
		store.queueNo = 4;
		
		vars = new ArrayList<Variable>();
		
		// Get problem size n from second program argument.
		FDV[][] x = new FDV[n][n];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				if (numbers[i][j] == -1) {
					x[i][j] = new FDV(store, "x" + i + j, 0, n - 1);
					vars.add(x[i][j]);
				}
				else
					x[i][j] = new FDV(store, "x" + i + j, numbers[i][j],
							numbers[i][j]);
					vars.add(x[i][j]);
			}

		// Create variables and state constraints.
		for (int i = 0; i < n; i++) {
			Constraint cx = new Alldistinct(x[i]);
			
			store.impose(cx);
			shavingConstraints.add(cx);
			
			FDV[] y = new FDV[n];
			for (int j = 0; j < n; j++)
				y[j] = x[j][i];

			Constraint cy = new Alldistinct(y);
			store.impose(cy);
			shavingConstraints.add(cy);

		}
		
	}
	
	/**
	 * It performs search with shaving guided by constraints.
	 * 
	 * @return true if there is a solution, false otherwise.
	 */
	public boolean searchWithShaving() {
	
		Shaving shaving = new Shaving();
		shaving.setStore(store);
		shaving.quickShave = true;

		for (Constraint c : shavingConstraints)
			shaving.addShavingConstraint(c);

		long begin = System.currentTimeMillis();

		search = new DepthFirstSearch();
		search.setPrintInfo(true);
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), null, new IndomainMiddle());

		search.setConsistencyListener(shaving);
		search.setExitChildListener(shaving);

		boolean result = search.labeling(store, select);

		long end = System.currentTimeMillis();

		System.out.println("Number of milliseconds " + (end - begin));
		System.out.println("Ratio "	+ (shaving.successes * 100 / (shaving.successes + shaving.failures)));
		
		return result;
	
	}

	/**
	 * It transforms part of the problem into an extensional costraint to 
	 * improve propagation and search process.
	 * 
	 * @return true if there is a solution, false otherwise.
	 */
	public boolean searchAllTransform() {
		
		long T1, T2, T;
		T1 = System.currentTimeMillis();

		TransformExtensional transform = new TransformExtensional();
			
		store.consistency();

		for (int i = 7; i < 16; i++)
			for (int j = 14; j < 22; j++)
				if (!vars.get(i*n+j).singleton())
					transform.variablesTransformationScope.add(vars.get(i*n+j));
		
		System.out.println(transform.variablesTransformationScope);
		
		SelectChoicePoint select = new SimpleSelect(vars.toArray(new Variable[1]), new SmallestDomain(),
													new IndomainMin());

		search = new DepthFirstSearch();
		search.getSolutionListener().searchAll(true);
		search.getSolutionListener().recordSolutions(true);
		
		search.setInitializeListener(transform);
		transform.solutionLimit = 50000;
		
		boolean result = search.labeling(store, select);

		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");

		return result;
		
	}		
	
	/**
	 * It executes the program which solves the QCP in multiple different ways.
	 * @param args the first argument is the name of the file containing the problem.
	 */
	public static void main(String[] args) {
		
		QCP example = new QCP();
		
		if (args.length > 0)
			example.filename = args[0];	
	
		example.model();
		
		if (example.searchSmallestDomain(false))
			System.out.print(" Solution(s) found ");

		example = new QCP();
		
		if (args.length > 0)
			example.filename = args[0];	
	
		example.model();
		
		if (example.searchWithRestarts())
			System.out.print(" Solution(s) found ");
		
		example = new QCP();
		
		
		if (args.length > 0)
			example.filename = args[0];	
		
		example.model();
		
		if (example.searchWithShaving())
			System.out.print(" Solution(s) found ");
		
		example = new QCP();
		
		if (args.length > 0)
			example.filename = args[0];	
		
		example.model();
		
		if (example.searchAllTransform())
			System.out.print(" Solution(s) found ");
		
		example = new QCP();
		
		if (args.length > 0)
			example.filename = args[0];	
		
		example.model();
		example.store.variableWeightManagement = true;
		
		if (example.searchWeightedDegree())
			System.out.print(" Solution(s) found ");
			
	}
	
}
