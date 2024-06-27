/**
 *  IndomainHierarchical.java 
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

import java.util.HashMap;

import JaCoP.core.Domain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Variable;

/**
 * IndomainHierarchical - implements enumeration method based on the selection
 * of the preferred indomain for each variable. The initial idea of having such
 * functionality was proposed by Ben Weiner.
 * 
 * @author Radoslaw Szymanek 
 * 
 * @version 2.4
 */

public class IndomainHierarchical implements Indomain {

	/**
	 * It defines the default indomain if there is no mapping provided.
	 */
	private Indomain defIndomain;

	/**
	 * It defines for each variable and indomain method which should be used.
	 */
	private HashMap<Variable, Indomain> hashmap;

	/**
	 * Constructor which specifies the mapping and default indomain to be used
	 * if mapping does not give specific indomain for some variables.
	 * @param hashmap a mapping from variable to indomain heuristic used.
	 * @param defIndomain default indomain used if hashmap does not contain an entry.
	 */

	public IndomainHierarchical(HashMap<? extends Variable, Indomain> hashmap,
			Indomain defIndomain) {

		this.hashmap = new HashMap<Variable, Indomain>(hashmap);
		this.defIndomain = defIndomain;

	}

	public int indomain(Domain dom) throws JaCoPException {

		assert (false) : "IndomainList uses association between"
			+ "variables and indomain functions. Using"
			+ "it with domain is not defined";
		
		throw new JaCoPException("IndomainList uses association between"
				+ "variables and indomain functions. Using"
				+ "it with domain is not defined");
	}

	/*
	 * @throws JaCoPException if no value can be returned since no selection
	 * mechanism is provided.
	 */
	public int indomain(Variable v) throws JaCoPException {
		if (hashmap.containsKey(v))
			return hashmap.get(v).indomain(v);
		else {
			if (defIndomain == null)
				throw new JaCoPException("Variable " + v
						+ " does not have any indomain"
						+ " associated and default indomain is not defined");
			return defIndomain.indomain(v);
		}
	}

}
