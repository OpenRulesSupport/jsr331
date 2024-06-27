/**
 *  GlobalCardinality.java 
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

package javax.constraints.impl.constraint;

import java.util.HashMap;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Var;
import JaCoP.core.IntVar;
import JaCoP.core.IntervalDomain;
import JaCoP.core.ValueEnumeration;

/**
 * @author Radoslaw Szymanek
 *
 */

public class GlobalCardinality extends javax.constraints.impl.Constraint {

	javax.constraints.impl.Problem p;

	/**
	 * For each index i the number of times the value "values[i]" 
	 * occurs in the array "vars" should be cardMin[i] and cardMax[i] (inclusive) 
	 * @param vars array of constrained integer variables
	 * @param values array of integer values within domain of all vars
	 * @param cardMin array of integers that serves as lower bounds for values[i]
	 * @param cardMax array of integers that serves as upper bounds for values[i]
	 * Note that arrays values, cardMin, and cardMax should have the same size 
	 * otherwise a RuntimeException will be thrown
	 */
	public GlobalCardinality(Var[] vars, int[] values, int[] cardMin, int[] cardMax){

		super(vars[0].getProblem());

		p = (javax.constraints.impl.Problem)vars[0].getProblem();

		HashMap<Integer, Integer> valuePositionMaping = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < values.length; i++)
			valuePositionMaping.put(values[i], i);
		
		IntervalDomain sum = new IntervalDomain();
		for (int i = 0; i < vars.length; i++) {
			sum.unionAdapt( ((IntVar)vars[i].getImpl()).domain);
		}
		
		IntVar[] variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

		IntVar[] counter = new IntVar[sum.getSize()]; 
		ValueEnumeration enumer = sum.valueEnumeration();
		int i = 0;
		while (enumer.hasMoreElements()) {
			int value = enumer.nextElement();
			Integer position = valuePositionMaping.get(value);
			if (position == null) {
				counter[i] = (IntVar)p.variable(0, vars.length).getImpl();
			}
			else {
				counter[i] = (IntVar)p.variable(cardMin[position], cardMax[position]).getImpl();				
			}
			i++;
		}
		
	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[1];
        array[0] = new JaCoP.constraints.GCC(variables, counter);

		setImpl(array);
		
	}

	
	/**
	 * This method is used to post the constraint. Additionally to post() 
	 * this methods specifies a particular level of consistency that will
	 * be selected by an implementation to control the propagation strength of
	 * this constraint. If this method is not overloaded by an implementation, it will work as a post(). 
	 * @param consistencyLevel
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post(ConsistencyLevel strength) {
		
		if (strength != ConsistencyLevel.BOUND) {
			p.log("Global cardinality constraint only implemented with Bound Consistency. Option " + strength + " ignored.");
		}
	
		p.post(this);

	}
	
}
