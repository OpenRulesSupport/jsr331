/**
 *  IntervalDomainIntervalEnumeration.java 
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


/**
 * Defines a methods for enumerating values contain in the intersal domain. It
 * implements the interface Enumeration.
 * 
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class IntervalDomainIntervalEnumeration extends IntervalEnumeration {

	IntervalDomain domain;

	Interval i = null;

	int intervalNo = -1;

	int maxIntervalNo;

	/**
	 * It creates an enumeration for a given interval domain.
	 * @param dom interval domain for which an enumeration is created.
	 */
	public IntervalDomainIntervalEnumeration(IntervalDomain dom) {
		domain = dom;
		maxIntervalNo = domain.size - 1;
	}

	@Override
	public boolean hasMoreElements() {
		return (intervalNo < maxIntervalNo);
	}

	@Override
	public Interval nextElement() {

		if (intervalNo < maxIntervalNo) {
			intervalNo++;
			return domain.intervals[intervalNo];
		}

		assert false;
		return null;
	}

}
