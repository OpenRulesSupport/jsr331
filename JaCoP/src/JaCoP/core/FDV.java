/**
 *  FDV.java 
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

import java.util.ArrayList;
import JaCoP.constraints.Constraint;

/**
 * Defines a Finite Domain Variable (FDV) and related operations on it.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class FDV extends Variable {

	/**
	 * It creates a variable in a constraint store. This variable
	 * has a default name and an empty domain to start with.
	 * @param store store in which the variable is created.
	 */
	public FDV(Store store) {
		IntervalDomain dom = new IntervalDomain(5);
		dom.searchConstraints = new ArrayList<Constraint>();
		dom.modelConstraints = new Constraint[3][];
		dom.modelConstraintsToEvaluate = new int[3];
		dom.modelConstraintsToEvaluate[0] = 0;
		dom.modelConstraintsToEvaluate[1] = 0;
		dom.modelConstraintsToEvaluate[2] = 0;
		id = store.getVariableIdPrefix() + idNumber;
		idNumber++;
		domain = dom;
		domain.stamp = 0;
		index = store.putVariable(this);
		this.store = store;
	}

	/**
	 * It creates a variable in a given store and with the given
	 * domain but it automatically uses an internal names generator.
	 * @param store store in which the variable is created.
	 * @param min the minimal value in the variables domain.
	 * @param max the maximal value in the variables domain.
	 */
	public FDV(Store store, int min, int max) {
		IntervalDomain dom = new IntervalDomain(min, max);
		dom.searchConstraints = new ArrayList<Constraint>();
		dom.modelConstraints = new Constraint[3][];
		dom.modelConstraintsToEvaluate = new int[3];
		dom.modelConstraintsToEvaluate[0] = 0;
		dom.modelConstraintsToEvaluate[1] = 0;
		dom.modelConstraintsToEvaluate[2] = 0;
		id = store.getVariableIdPrefix() + idNumber;
		idNumber++;
		domain = dom;
		domain.stamp = 0;
		index = store.putVariable(this);
		this.store = store;
	}

	/**
	 * It creates a variable with a given name in a given store, but
	 * with an empty domain.
	 * @param store store in which the variable is created.
	 * @param name the name of the variable.
	 */
	public FDV(Store store, String name) {
		IntervalDomain dom = new IntervalDomain(5);
		dom.searchConstraints = new ArrayList<Constraint>();
		dom.modelConstraints = new Constraint[3][];
		dom.modelConstraintsToEvaluate = new int[3];
		dom.modelConstraintsToEvaluate[0] = 0;
		dom.modelConstraintsToEvaluate[1] = 0;
		dom.modelConstraintsToEvaluate[2] = 0;
		id = name;
		domain = dom;
		domain.stamp = 0;
		index = store.putVariable(this);
		this.store = store;
	}

	/**
	 * It creates a variable in a given store, with a given name,
	 * and domain consisting of an interval min..max.
	 * @param store store in which the variable is created.
	 * @param name the name of the variable being created.
	 * @param min the minimal value in the variables domain.
	 * @param max the maximal value in the variables domain.
	 */
	public FDV(Store store, String name, int min, int max) {
		IntervalDomain dom = new IntervalDomain(min, max);
		dom.searchConstraints = new ArrayList<Constraint>();
		dom.modelConstraints = new Constraint[3][];
		dom.modelConstraintsToEvaluate = new int[3];
		dom.modelConstraintsToEvaluate[0] = 0;
		dom.modelConstraintsToEvaluate[1] = 0;
		dom.modelConstraintsToEvaluate[2] = 0;
		id = name;
		domain = dom;
		domain.stamp = 0;
		index = store.putVariable(this);
		this.store = store;
	}

	
}
