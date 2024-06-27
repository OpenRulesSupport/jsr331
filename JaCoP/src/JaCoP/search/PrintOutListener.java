/**
 *  PrintOutListener.java 
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

package JaCoP.search;

/**
 * It is a simple example how it is possible to extend existing listeners to 
 * add your own functionality.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class PrintOutListener extends SimpleSolutionListener implements SolutionListener {

	/**
	 * It is executed right after consistency of the current search node. The
	 * return code specifies if the search should continue or exit.
	 */

	@Override
	public boolean executeAfterSolution(Search search, SelectChoicePoint select) {

		boolean parent = super.executeAfterSolution(search, select);
	

		StringBuffer buf = new StringBuffer("\n");

		if (search.getCostVariable() != null)
			buf.append("Solution cost " + search.getCostVariable() + "\n");
		
		if (noSolutions > 1) {
			buf.append("No of solutions : " + noSolutions);
			buf.append("\nLast Solution : [");
		} else
			buf.append("\nSolution : [");

		int solutionIndex = 0;
		
		if (recordSolutions)
			solutionIndex = noSolutions - 1;
		
		if (vars != null)
			for (int i = 0; i < vars.length; i++) {
				buf.append(vars[i].id()).append("=").append(
						solutions[solutionIndex][i]);
				if (i < vars.length - 1)
					buf.append(", ");
			}

		buf.append("]\n");

		System.out.println(buf.toString());
		
		return parent;
		
	}


}
