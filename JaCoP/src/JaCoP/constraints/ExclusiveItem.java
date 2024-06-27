/**
 *  ExclusiveItem.java 
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

import JaCoP.core.Variable;

/**
 * Defines a basic structure for exsclusive rectangles used in
 * DisjointConditional
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

class ExclusiveItem {

	Variable cond;

	int i1, i2;

	ExclusiveItem(int a, int b, Variable c) {
		i1 = a;
		i2 = b;
		cond = c;
	}

	Variable condition() {
		return cond;
	}

	int index1() {
		return i1;
	}

	int index2() {
		return i2;
	}

	@Override
	public String toString() {
		return "[" + i1 + ", " + i2 + ", " + cond + "]";
	}

	public org.jdom.Element toXML() {

		org.jdom.Element exclusion = new org.jdom.Element("exclusion");

		exclusion.setAttribute("val1", String.valueOf(i1));
		exclusion.setAttribute("val2", String.valueOf(i2));
		exclusion.setAttribute("var", cond.id());

		return exclusion;
	}

}
