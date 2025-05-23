/**
 *  CircuitVarValue.java 
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

import JaCoP.core.MutableVarValue;

/**
 * Defines a current value of the CircuitVar and related operations on it.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

class CircuitVarValue implements MutableVarValue {

	int next = 0, previous = 0;

	CircuitVarValue nextCircuitVarValue = null;

	int stamp = 0;

	CircuitVarValue() {}

	CircuitVarValue(int n, int p) {
		next = n;
		previous = p;
	}

	@Override
	public Object clone() {

		CircuitVarValue val = new CircuitVarValue(next, previous);
		val.stamp = stamp;
		val.nextCircuitVarValue = nextCircuitVarValue;
		return val;
	}

	public MutableVarValue previous() {
		return nextCircuitVarValue;
	}

	public void setPrevious(MutableVarValue nn) {
		nextCircuitVarValue = (CircuitVarValue) nn;
	}

	public void setStamp(int stamp) {
		this.stamp = stamp;
	}

	void setValue(int n, int p) {
		next = n;
		previous = p;
	}

	public int stamp() {
		return stamp;
	}

	@Override
	public String toString() {
		return "[" + next + ", " + previous + "]";
	}
	
}
