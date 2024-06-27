/**
 *  IndomainMiddle.java 
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

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;

/**
 * IndomainMiddle - implements enumeration method based on the selection of the
 * middle value in the domain of FD variable and then right and left values.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class IndomainMiddle implements Indomain, Constants {

	/**
	 * It creates Indomain heuristic which chooses the middle value.
	 */
	public IndomainMiddle() {
	}

	public int indomain(Domain dom) {

		assert ( dom.domainID() != Domain.BoundDomainID);
		
		if (dom.domainID() == Domain.IntervalDomainID) {

			IntervalDomain domain = (IntervalDomain) dom;

			int dMin = domain.min(), dMax = domain.max();

			if (domain.singleton())
				return dMin;

			// right shift operator is a division by 2, more efficient
			int middle = dMin + ((dMax - dMin) >> 1);
			// int middle = Dmin + ((Dmax - Dmin) / 2);

			if (!domain.contains(middle)) {
				int iBefore = 0;
				int iAfter = domain.size - 1;

				for (; iBefore < domain.size
						&& domain.intervals[iBefore].max < middle; iBefore++)
					;

				for (; iAfter >= 0 && domain.intervals[iAfter].min > middle; iAfter--)
					;

				if (middle - domain.intervals[iBefore].max > domain.intervals[iAfter].min
						- middle)
					return domain.intervals[iAfter].min;
				else
					return domain.intervals[iBefore].max;

			} else
				return middle;

		}

		if (dom.isSparseRepresentation()) {
			
			int size = dom.getSize() / 2;
			
			ValueEnumeration enumer = dom.valueEnumeration();
			
			while (enumer.hasMoreElements() && size > 0) {
				enumer.nextElement();
				size--;
			}

			return enumer.nextElement();
		}
		else {
			
			int dMin = dom.min(), dMax = dom.max();

			if (dom.singleton())
				return dMin;

			// right shift operator is a division by 2, more efficient
			int middle = dMin + ((dMax - dMin) >> 1);
			// int middle = Dmin + ((Dmax - Dmin) / 2);

			if (!dom.contains(middle)) {
				int iBefore = 0;
				int iAfter = dom.noIntervals() - 1;
				
				for (; iBefore < dom.noIntervals()
						&& dom.getInterval(iBefore).max < middle; iBefore++)
					;

				for (; iAfter >= 0 && dom.getInterval(iAfter).min > middle; iAfter--)
					;

				if (middle - dom.getInterval(iBefore).max > dom.getInterval(iAfter).min 
						- middle)
					return dom.getInterval(iAfter).min;
				else
					return dom.getInterval(iBefore).max;
				
			} else
				return middle;
			
		}
		
	}

	/**
	 * It requires variable with FD type domain, as it uses
	 * indomainDirectionLeft to decide which left or right value to choose.
	 */

	public int indomain(Variable V) {

		if (V.domain.domainID() == Domain.IntervalDomainID) {

			IntervalDomain domain = (IntervalDomain) V.domain;

			int dMin = domain.min(), dMax = domain.max();

			if (domain.singleton())
				return dMin;

			// right shift operator is a division by 2, more efficient
			int middle = dMin + ((dMax - dMin) >> 1);
			// int middle = Dmin + ((Dmax - Dmin) / 2);

			if (!domain.contains(middle)) {
				int iBefore = 0;
				int iAfter = domain.size - 1;

				for (; iBefore < domain.size
						&& domain.intervals[iBefore].max < middle; iBefore++)
					;

				for (; iAfter >= 0 && domain.intervals[iAfter].min > middle; iAfter--)
					;

				if (middle - domain.intervals[iBefore].max > domain.intervals[iAfter].min
						- middle)
					return domain.intervals[iAfter].min;
				else
					return domain.intervals[iBefore].max;

			} else
				return middle;

		}

		assert false;

		return -1;

	}

}
