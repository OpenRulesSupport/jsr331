/**
 *  Gardner.java 
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

import JaCoP.core.*;
import JaCoP.constraints.*;
import JaCoP.search.*;
import JaCoP.set.core.*;
import JaCoP.set.constraints.*;
import JaCoP.set.search.*;

/**
 * It specifies a simple Gardner problem which use set functionality from JaCoP. 
 *
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 1.0
 */

public class Gardner extends Example {

	/**
	 * It executes the program which solves this gardner problem.
	 * @param args
	 */
	public static void main(String args[]) {

		Gardner example = new Gardner();
		example.model();

		example.search();

	}

	public void model() {

		int num_days = 35;
		int num_persons_per_meeting = 3;
		int persons = 15;

		System.out.println("Gardner dinner problem ");
		store = new FDstore();

		Variable[] days = new Variable[num_days];
		
		for (int i = 0; i < days.length; i++)
			days[i] = new Variable(store, "days[" + i + "]", new SetDomain(1, persons));

	    vars = new ArrayList<Variable>();
	    
	    for(Variable d: days) 
	    	vars.add(d);

		// all_different(days)
		for (int i = 0; i < days.length - 1; i++)
			for (int j = i + 1; j < days.length; j++)
				store.impose(new Not(new JaCoP.set.constraints.XeqY(days[i], days[j])));

		// card(days[i]) = num_persons_per_meeting
		for (int i = 0; i < days.length; i++)
			store.impose(new Card(days[i], num_persons_per_meeting));

		for (int i = 0; i < days.length - 1; i++)
			for (int j = i + 1; j < days.length; j++) {
				Variable intersect = new Variable(store, "intersect" + i + "-" + j, 
												  new SetDomain(1, persons));
				store.impose(new XintersectYeqZ(days[i], days[j], intersect));
				Variable card = new Variable(store, 0, 1);
				store.impose(new Card(intersect, card));
			}

		System.out.println( "\nVariable store size: "+ store.size()+
				"\nNumber of constraints: " + store.numberConstraints()
		);

	}
	
	public boolean search() {

		Thread tread = java.lang.Thread.currentThread();
		java.lang.management.ThreadMXBean b = java.lang.management.ManagementFactory.getThreadMXBean();

		long startCPU = b.getThreadCpuTime(tread.getId());
		long startUser = b.getThreadUserTime(tread.getId());

		boolean result = store.consistency();
		System.out.println("*** consistency = " + result);

		Search label = new DepthFirstSearch();

		SelectChoicePoint select = new SetSimpleSelect(vars.toArray(new Variable[vars.size()]), 
				null,
				new IndomainMin());

		label.setSolutionListener(new SetSimpleSolutionListener());
		label.getSolutionListener().searchAll(false);
		label.getSolutionListener().recordSolutions(false);

		result = label.labeling(store, select);

		if (result) {
			System.out.println("*** Yes");
			for (int i=0; i< vars.size(); i++) {
				System.out.println(vars.get(i));
			}
		}
		else
			System.out.println("*** No");


		System.out.println( "ThreadCpuTime = " + (b.getThreadCpuTime(tread.getId()) - startCPU)/(long)1e+6 + "ms");
		System.out.println( "ThreadUserTime = " + (b.getThreadUserTime(tread.getId()) - startUser)/(long)1e+6 + "ms" );
		
		return result;

	}

}
