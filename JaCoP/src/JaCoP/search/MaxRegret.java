/**
 *  MaxRegret.java 
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
import JaCoP.core.Constants;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;

/**
 * Defines a MaxRegret comparator for Variables.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class MaxRegret implements ComparatorVariable {

	/**
	 * It constructs MaxRegret comparator.
	 */
	public MaxRegret() {
	}

	public int compare(float ldiff, Variable var) {

		ValueEnumeration rEnum = var.domain.valueEnumeration();

		int rmin = rEnum.nextElement();
		int rminNext = 0;
		if (rEnum.hasMoreElements())
			rminNext = rEnum.nextElement();
		else
			rminNext = Constants.MaxInt;

		int rdiff = rminNext - rmin;

		if (ldiff > rdiff)
			return 1;
		if (ldiff < rdiff)
			return -1;
		return 0;

	}

	public int compare(Variable left, Variable right) {

		ValueEnumeration lEnum = left.domain.valueEnumeration();

		int lmin = lEnum.nextElement();
		int lminNext = 0;
		if (lEnum.hasMoreElements())
			lminNext = lEnum.nextElement();
		else
			lminNext = Constants.MaxInt;

		int ldiff = lminNext - lmin;

		ValueEnumeration rEnum = right.domain.valueEnumeration();

		int rmin = rEnum.nextElement();
		int rminNext = 0;
		if (rEnum.hasMoreElements())
			rminNext = rEnum.nextElement();
		else
			rminNext = Constants.MaxInt;

		int rdiff = rminNext - rmin;

		if (ldiff > rdiff)
			return 1;
		if (ldiff < rdiff)
			return -1;
		return 0;

	}

	public float metric(Variable o) {

		ValueEnumeration oEnum = o.domain.valueEnumeration();

		int omin = oEnum.nextElement();
		int ominNext = 0;
		if (oEnum.hasMoreElements())
			ominNext = oEnum.nextElement();
		else
			ominNext = Constants.MaxInt;

		return (ominNext - omin);

	}

}
