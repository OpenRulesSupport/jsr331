/**
 *  DecomposedConstraint.java 
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

package JaCoP.constraints;

import java.util.ArrayList;

import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Standard unified interface/abstract class for constraints, which can only be decomposed. 
 * Defines how to construct a constraint out of other constraints.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public abstract class DecomposedConstraint {
	
	/**
	 * It specifies the queue (index), which is used to record that constraint
	 * needs to be re-evaluated.
	 */

	public int queueIndex = 0;

	/**
	 * It imposes the constraint in a given store.
	 * @param store the constraint store to which the constraint is imposed to.
	 */
	public abstract void imposeDecomposition(Store store);

	/**
	 * It imposes the constraint and adjusts the queue index.
	 * @param store the constraint store to which the constraint is imposed to.
	 * @param queueIndex the index of the queue in the store it is assigned to.
	 */

	public void imposeDecomposition(Store store, int queueIndex) {

		assert ( queueIndex < store.queueNo ) 
			: "Constraint queue number larger than permitted by store.";

		this.queueIndex = queueIndex;
		
		imposeDecomposition(store);
	
	}
	
	/**
	 * It returns an array list of constraint which are used to decompose this 
	 * constraint. It actually creates a decomposition (possibly also creating
	 * variables), but it does not impose the constraint.
	 * @param store the constraint store in which context the decomposition takes place.
	 * 
	 * @return an array list of constraints used to decompose this constraint.
	 */
	public abstract ArrayList<Constraint> decompose(Store store);
	
	
	/**
	 * @return null if no auxiliary variables were created, otherwise a list with variables.
	 */
	public ArrayList<Variable> auxiliaryVariables() { return null; }

}
