/**
 *  MinGlbCard.java 
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

package JaCoP.set.search;

import JaCoP.search.ComparatorVariable;
import JaCoP.set.core.SetDomain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Variable;

/**
 * Defines a minimum cardinality, of the greatest lowest bound, variable comparator. The variable with the minimum
 * cardinality for the greatest lower bound has the priority.
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm 
 * @version 2.4
 */

public class MinGlbCard implements ComparatorVariable {

	/**
	 * It constructs a minimum cardinality, of the greatest lowest bound, variable comparator.
	 */
	public MinGlbCard() {
	}

	/**
	 * Compares the cardinality of the variables glb to the float value.
	 */
	public int compare(float left, Variable var) {
		if(var.domain.domainID() != SetDomain.SetDomainID)
			throw new JaCoPException("This comparator should only be used for SetDomain variables.");
		SetDomain SD = (SetDomain) var.dom();

		int right = SD.glb.card();

		if (left < right)
			return 1;
		if (left > right)
			return -1;
		return 0;
	}

	/**
	 * Compares the cardinality of the variables glbs.
	 */
	public int compare(Variable leftVar, Variable rightVar) {
		if(leftVar.domain.domainID() != SetDomain.SetDomainID || rightVar.domain.domainID() != SetDomain.SetDomainID)
			throw new JaCoPException("This comparator should only be used for SetDomain variables.");
		SetDomain leftSD = (SetDomain) leftVar.dom();
		SetDomain rightSD = (SetDomain) rightVar.dom();

		int left = leftSD.glb.card();
		int right = rightSD.glb.card();

		if (left < right)
			return 1;
		if (left > right)
			return -1;
		return 0;
	}

	/**
	 * Returns the cardinality of the glb.
	 */
	public float metric(Variable var) {
		if(var.domain.domainID() != SetDomain.SetDomainID)
			throw new JaCoPException("This comparator should only be used for SetDomain variables.");
		SetDomain SD = (SetDomain) var.dom();
		return SD.glb.card();
	}

}
