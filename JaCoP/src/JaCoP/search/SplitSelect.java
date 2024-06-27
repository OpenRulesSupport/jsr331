/**
 *  SplitSelect.java 
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

import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.constraints.XltC;
import JaCoP.constraints.XlteqC;
import JaCoP.core.Variable;

/**
 * It is simple and customizable selector of decisions (constraints) which will
 * be enforced by search. However, it does not use X=c as a search decision 
 * but rather X <= c (potentially splitting the domain), unless c is equal to 
 * the maximal value in the domain of X then the constraint X < c is used.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class SplitSelect extends SimpleSelect {

	/**
	 * The constructor to create a simple choice select mechanism.
	 * @param variables variables upon which the choice points are created.
	 * @param varSelect the variable comparator to choose the variable.
	 * @param indomain the value heuristic to choose a value for a given variable.
	 */
	public SplitSelect(Variable[] variables, 
					   ComparatorVariable varSelect,
					   Indomain indomain) {
		super(variables, varSelect, indomain);
	//	assert (indomain.getClass() != IndomainMax.class) : "Using Indomain max inside SplitSelect will result in non-termination";
	}

	/**
	 * It constructs a simple selection mechanism for choice points.
	 * @param variables variables used as basis of the choice point.
	 * @param varSelect the main variable comparator.
	 * @param tieBreakerVarSelect secondary variable comparator employed if the first one gives the same metric.
	 * @param indomain the heuristic to choose value assigned to a chosen variable.
	 */
	public SplitSelect(Variable[] variables, 
					   ComparatorVariable varSelect,
					   ComparatorVariable tieBreakerVarSelect, 
					   Indomain indomain) {
		super(variables, varSelect, tieBreakerVarSelect, indomain);
	//	assert (indomain.getClass() != IndomainMax.class) : "Using Indomain max inside SplitSelect will result in non-termination";
	}
	
	@Override 
	public Variable getChoiceVariable(int index) {
		return null;
	}
	
	@Override
	public PrimitiveConstraint getChoiceConstraint(int index) {
		
		Variable var = super.getChoiceVariable(index);
		if (var == null)
			return null;
		
		int value = super.getChoiceValue();
		
	//	assert (var.max() != value) : "Indomain has chosen the maximum value and split select will not be able to split the interval causing search to loop.";
	// 	return new XlteqC(var, value);

		if (var.max() == value)
			return new XltC(var, value);
		else
			return new XlteqC(var, value);
	}
	
}
