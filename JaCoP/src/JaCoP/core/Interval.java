/**
 *  Interval.java 
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
 * Defines interval of numbers which is part of FDV definition which consist of
 * one or several intervals.
 * 
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public final class Interval implements Constants {

	/**
	 * It specifies the maximal value in the interval.
	 */
	public final int max;

	/**
	 * It specifies the minimal value in the interval.
	 */
	public final int min;

	/**
	 * It creates the largest possible interval.
	 */
	public Interval() {
		min = MinInt;
		max = MaxInt;
	}

	/**
	 * It creates an interval with a given minimum and maximal value.
	 * @param min the minimal value in the interval (the left bound). 
	 * @param max the maximal value in the interval (the right bound).
	 */
	public Interval(int min, int max) {

		assert (min <= max) : "min value " + min + 
							  " is larger than max value " + max;

		this.min = min;
		this.max = max;

	}

	@Override
	public Object clone() {
		return new Interval(min, max);
	}

	/**
	 * It checks equality between intervals.
	 * @param interval the inerval to which the comparison is made.
	 * @return true if an input interval is equal to this one.
	 */
	public boolean eq(Interval interval) {
		return min == interval.min && max == interval.max;
	}

	/**
	 * It returns the right bound of the interval (maximum value).
	 * @return the maximal value from the interval.
	 */
	public int max() {
		return max;
	}

	/**
	 * It returns the left range of the interval (minimum value).
	 * @return the minimal value from the interval.
	 */
	public int min() {
		return min;
	}

	/**
	 * It checks if an intervals contains only one value (singleton).
	 * @return true if domain has only one value.
	 */
	public boolean singleton() {
		return (min == max);
	}

	/**
	 * It checks if an intervals contains only value c.
	 * @param c integer value to which the singleton is compared to.
	 * @return true if variable has a singleton domain and it is equal to value c.
	 */

	public boolean singleton(int c) {
		return (min == max && min == c);
	}

	@Override
	public String toString() {
		String string = "" + min;
		if (max != min)
			string += ".." + max;
		return string;
	}

	/**
	 * It returns an xml representation of an interval.
	 * @return XML element representing an interval.
	 */
	public org.jdom.Element toXML() {

		org.jdom.Element interval = new org.jdom.Element("interval");

		interval.setAttribute("min", String.valueOf(min));
		interval.setAttribute("max", String.valueOf(max));

		return interval;

	}
	
	
	/**
	 * It creates an interval from XML element describing the interval.
	 * 
	 * @param intervalElement an XML element for this interval.
	 * @return interval as described by XML element.
	 */
	public static Interval fromXML(org.jdom.Element intervalElement) {
		
		int min = Integer.valueOf( intervalElement.getAttributeValue("min") );
		int max = Integer.valueOf( intervalElement.getAttributeValue("max") );
		
		return new Interval(min, max);
	}

}
