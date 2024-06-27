/**
 *  WeightedDegree.java 
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

import JaCoP.search.ComparatorVariable;
import JaCoP.core.Variable;

/**
 * 
 * Defines a WeightedDegree comparator for Variables. Every time a constraint
 * failure is encountered all variables within the scope of that constraints
 * have increased weight. The comparator will choose the variable with the 
 * highest weight divided by its size.
 * 
 * 
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * 
 * @version 2.4
 * 
 */

public class WeightedDegree implements ComparatorVariable {

	public int compare(float left, Variable var) {

		float right = ((float) var.weight) / ((float) var.getSize());

		if (left > right)

			return 1;

		if (left < right)

			return -1;

		return 0;

	}

	public int compare(Variable leftVar, Variable rightVar) {

		float left = ((float) leftVar.weight) / ((float) leftVar.getSize());

		float right = ((float) rightVar.weight) / ((float) rightVar.getSize());

		if (left > right)

			return 1;

		if (left < right)

			return -1;

		return 0;

	}

	public float metric(Variable var) {

		return ((float) var.weight) / ((float) var.getSize());

	}

}
