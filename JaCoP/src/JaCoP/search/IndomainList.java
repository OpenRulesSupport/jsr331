/**
 *  IndomainList.java 
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

import JaCoP.core.Domain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Variable;

/**
 * IndomainHierarchical - implements enumeration method based on the selection
 * of the preferred indomain for each variable. The initial idea of having such
 * functionality was proposed by Ben Weiner.
 * 
 * @author Radoslaw Szymanek
 * @version 2.4
 */

public class IndomainList implements Indomain {

	private Indomain defIndomain;

	private int[] order;

	/**
	 * It creates an IndomainList heuristic for choosing the values.
	 * @param order the order of values used to decide which values goes first.
	 * @param defIndomain the default indomain used if some values are not specified by the order array.
	 */
	public IndomainList(int[] order, Indomain defIndomain) {

		this.order = new int[order.length];

		for (int i = 0; i < order.length; i++)
			this.order[i] = order[i];

		this.defIndomain = defIndomain;
	}

	public int indomain(Domain dom) throws JaCoPException {

		for (int next : order)
			if (dom.contains(next))
				return next;

		if (defIndomain == null)
			throw new JaCoPException();

		return defIndomain.indomain(dom);
		
	}

	/*
	 * @throws JaCoPException if no value can be returned since list does not
	 * contain a value which belongs to the domain and default indomain was not
	 * supplied.
	 */
	public int indomain(Variable V) throws JaCoPException {

		for (int next : order)
			if (V.domain.contains(next))
				return next;

		if (defIndomain == null)
			throw new JaCoPException();

		return defIndomain.indomain(V);
	}

}
