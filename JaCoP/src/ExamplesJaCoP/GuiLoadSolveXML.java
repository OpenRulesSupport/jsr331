/**
 *  GuiLoadSolveXML.java 
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

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import JaCoP.core.FDstore;
import JaCoP.core.Variable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.IndomainMin;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;
import JaCoP.search.WeightedDegree;

/**
 * 
 * It allows reading XCSP files and finding a solution using 
 * simple weighted degree heuristic. 
 * 
 * @author Radoslaw Szymanek
 *
 */
public class GuiLoadSolveXML {

	/**
	 * 
	 * It reads an XML description of the CSP problem and solves it 
	 * using WeightedDegree variable ordering heuristic.
	 * 
	 * @param args the first parameter can specify the filename containing XML description of the CSP.
	 */
	public static void main(String args[]) {

		File selFile = null;

		if (args.length != 1) {

			// Create and set up the window.
			JFrame frame = new JFrame("FileChooserDemo");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			// Display the window.
			frame.pack();
			frame.setVisible(true);

			String filename = File.separator + "xml";
			JFileChooser fc = new JFileChooser(new File(filename));

			fc.showOpenDialog(frame);
			selFile = fc.getSelectedFile();

		}

		FDstore store = new FDstore();

		long T1, T2, T;
		T1 = System.currentTimeMillis();

		if (selFile == null)
			if (args.length == 2)
				store.fromXCSP2_0(args[0], args[1]);
			else
				store.fromXCSP2_0("", args[0]);
		else {
			store.fromXCSP2_0("", selFile.getPath());

		}

		ArrayList<Variable> fdvV = new ArrayList<Variable>();

		for (Variable v : store.vars)
			if (v != null)
				fdvV.add(v);

		T2 = System.currentTimeMillis();
		T = T2 - T1;

		System.out.println("\n\t*** Loading time = " + T + " ms");

		Search label = new DepthFirstSearch();

		System.out.println("Search has begun");

		T1 = System.currentTimeMillis();

		label.setTimeOut(1800);

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

		T2 = System.currentTimeMillis();
		T = T2 - T1;

		if (result)
			System.out.println("Variables : " + vars);
		else
			System.out.println("Failed to find any solution");
			
		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = " + T + " ms");

	}

}
