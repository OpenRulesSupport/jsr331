/**
 *  Alldifferent.java 
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

import javax.constraints.ConsistencyLevel;
import javax.constraints.impl.Constraint;

import JaCoP.constraints.Alldiff;
import JaCoP.constraints.Alldifferent;
import JaCoP.constraints.Alldistinct;
import JaCoP.core.IntVar;

/**
 * @author Radoslaw Szymanek
 *
 */

public class AllDifferent extends Constraint {

	IntVar[] variables;
	
	ConsistencyLevel current;
	
	public AllDifferent(javax.constraints.Var[] vars) {
	
		super(vars[0].getProblem());
		
		variables = new IntVar[vars.length];		
		for (int i = 0; i < vars.length; i++)
			variables[i] = (IntVar)vars[i].getImpl();

	    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[1];
        array[0] = new Alldiff(variables);
    
		setImpl(array);
		current = ConsistencyLevel.BOUND;
		
	}
	
	public void post(ConsistencyLevel strength) {

		if (strength == ConsistencyLevel.DOMAIN && current != strength) {
		    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[1];
	        array[0] = new Alldistinct(variables);
	    
			setImpl(array);

		}
		
		if (strength == ConsistencyLevel.BOUND && current != strength) {
		    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[1];
	        array[0] = new Alldiff(variables);	    
			setImpl(array);
		}
		
		if (strength == ConsistencyLevel.VALUE && current != strength) {
		    JaCoP.constraints.Constraint[] array = new JaCoP.constraints.Constraint[1];
	        array[0] = new Alldifferent(variables);
			setImpl(array);
		}
	
		post();
	}
		
}
