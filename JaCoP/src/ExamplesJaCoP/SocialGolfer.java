/**
 *  SocialGolfer.java 
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
 * The class Run is used to run test programs for JaCoP package.
 * It is used for test purpose only.
 *
 * @author Krzysztof Kuchcinski
 * @version 1.0
 */

public class SocialGolfer extends Example {

	int weeks;

	int groups;

	int players;

	Variable[][] golferGroup;

	/**
	 * 
	 * It runs a number of social golfer problems.
	 * 
	 * @param args
	 */
	public static void main (String args[]) {

		SocialGolfer example = new SocialGolfer();

		// Solved
		example.setup(2,5,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(2,6,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(2,7,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(3,5,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(3,6,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(3,7,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(4,5,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(4,6,5); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(4,7,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(4,9,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(5,5,3); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(5,7,4); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(5,8,3); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(6,6,3); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(5,3,2); // weeks - groups - players in each group
		example.model();
		example.search();
		
		example.setup(4,3,3); // weeks - groups - players in each group
		example.model();
		example.search();
		
	}

	/**
	 * It sets the parameters for the model creation function. 
	 * 
	 * @param weeks
	 * @param groups
	 * @param players
	 */
	public void setup(int weeks, int groups, int players) {

		this.weeks = weeks;
		this.groups = groups;
		this.players = players;

	}

	public  void model() {

		int N = groups*players;
		int[] weights = new int[players];
		int p = 10; //players+1;
		weights[players-1] = 1;
		for (int i=players-2; i>=0; i--) {
			weights[i] = weights[i+1]*p;
		}

		System.out.println("Social golfer problem " + weeks+"-"+groups+"-"+players);
		store = new FDstore();
		golferGroup = new Variable[weeks][groups];

		vars = new ArrayList<Variable>();

		for (int i=0; i<weeks; i++)
			for (int j=0; j<groups; j++) {
				golferGroup[i][j] = new Variable(store, "g_"+i+"_"+j, new SetDomain(1, N));
				vars.add(golferGroup[i][j]);
				store.impose(new Card(golferGroup[i][j], players));
			}

		for (int i=0; i<weeks; i++) 
			for (int j=0; j<groups; j++) 
				for (int k=j+1; k<groups; k++) {
					store.impose(new DisjointSets(golferGroup[i][j], golferGroup[i][k]));
				}

		for (int i=0; i<weeks; i++) {
			Variable t = golferGroup[i][0];

			for (int j=1; j<groups; j++) {
				Variable r = new Variable(store, "r", new SetDomain(1, N));
				store.impose(new XunionYeqZ(t, golferGroup[i][j], r));
				t = r;
			}
			store.impose(new XinY(new Variable(store, "", new SetDomain(new Set(1,N), new Set(1,N))), t));
		}

		for (int i=0; i<weeks; i++)
			for (int j=i+1; j<weeks; j++) 
				if (i != j) 
					for (int k=0; k<groups; k++)
						for (int l=0; l<groups; l++) {
							Variable result = new Variable(store, "res"+i+j+k+l, new SetDomain(1,N));
							store.impose(new XintersectYeqZ(golferGroup[i][k], golferGroup[j][l], result));
							store.impose(new Card(result, 0, 1));
						}

		Variable[] v = new Variable[weeks];
		Variable[][] var = new Variable[weeks][players];
		for (int i=0; i<weeks; i++) {
			v[i] = new Variable(store, "v"+i, 0, 100000000);
			for (int j=0; j<players; j++)
				var[i][j] = new Variable(store, "var"+i+j, 1, N);
			store.impose(new Match(golferGroup[i][0], var[i]));
			store.impose(new SumWeight(var[i], weights, v[i]));
		}
		for (int i=0; i<weeks-1; i++)
			store.impose(new XlteqY(v[i], v[i+1]));

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
				new MinLubCard(),
				new MaxGlbCard(),
				new IndomainMin());

		label.setSolutionListener(new SetSimpleSolutionListener());
		label.getSolutionListener().searchAll(false);
		label.getSolutionListener().recordSolutions(false);

		result = label.labeling(store, select);

		if (result) {
			System.out.println("*** Yes");
			for (int i=0; i<weeks; i++) {
				for (int j=0; j<groups; j++) {
					System.out.print(golferGroup[i][j].dom()+" ");
				}
				System.out.println();
			}
		}
		else
			System.out.println("*** No");

		System.out.println( "ThreadCpuTime = " + (b.getThreadCpuTime(tread.getId()) - startCPU)/(long)1e+6 + "ms");
		System.out.println( "ThreadUserTime = " + (b.getThreadUserTime(tread.getId()) - startUser)/(long)1e+6 + "ms" );

		return result;
	}



}
