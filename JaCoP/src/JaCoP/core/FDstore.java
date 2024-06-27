/**
 *  FDStore.java 
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

package JaCoP.core;

import JaCoP.core.Store;
import JaCoP.core.FDV;


/**
 * It is a Finite Domain store. It contains all created Variable as well imposed
 * constraints. It also stores all mutable variables, which value depends on
 * store level. It keeps track of constraints which require re-evaluation.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class FDstore extends Store {

	/**
	 * It creates a finite domain variable (FDV).
	 * @param min the left bound of the domain.
	 * @param max the right bound of the domain.
	 * @return FDV with the supplied domain.
	 */
	public FDV FDV(int min, int max) {

		FDV variable = new FDV(this, min, max);

		return variable;

	}

	/**
	 * It created a finite domain variable (FDV) with a given name and domain.
	 * @param name specifies the name of the variable.
	 * @param min the left bound of the domain.
	 * @param max the right bound of the domain.
	 * @return FDV with the given name and domain.
	 */
	public FDV FDV(String name, int min, int max) {

		FDV variable = new FDV(this, name, min, max);

		return variable;

	}

}
