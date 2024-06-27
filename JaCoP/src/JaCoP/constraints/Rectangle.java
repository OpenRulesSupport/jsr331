/**
 *  Rectangle.java 
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

import JaCoP.core.Domain;
import JaCoP.core.Variable;

/**
 * Defines a recatngle used in the diffn constraint.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

class Rectangle {

	int dim;

	Variable[] length;

	Variable[] origin;

	public Rectangle(ArrayList<? extends Variable> R) {
		dim = R.size() / 2;
		origin = new Variable[dim];
		length = new Variable[dim];
		for (int i = 0; i < dim; i++) {
			origin[i] = R.get(i);
			length[i] = R.get(i + dim);
		}
	}

	public Rectangle(Variable[] R) {
		dim = R.length / 2;
		origin = new Variable[dim];
		length = new Variable[dim];
		for (int i = 0; i < dim; i++) {
			origin[i] = R[i];
			length[i] = R[i + dim];
		}
	}

	int dim() {
		return dim;
	}

	public boolean domOverlap(Rectangle R) {
		boolean overlap = true;
		int min1, max1, min2, max2;
		int i = 0;
		while (overlap && i < dim) {
			Domain originIdom = origin[i].dom();
			Domain ROriginIdom = R.origin[i].dom();
			min1 = originIdom.min();
			max1 = originIdom.max() + length[i].max();
			min2 = ROriginIdom.min();
			max2 = ROriginIdom.max() + R.length[i].max();
			overlap = overlap && intervalOverlap(min1, max1, min2, max2);
			i++;
		}
		return overlap;
	}

	boolean intervalOverlap(int min1, int max1, int min2, int max2) {
		return !(min1 >= max2 || max1 <= min2);
	}

	Variable length(int i) {
		return length[i];
	}

	public int maxLevel() {
		int level = 0;
		int i = 0;
		while (i < dim) {
			int originStamp = origin[i].level(), lengthStamp = length[i]
					.level();
			if (level < originStamp)
				level = originStamp;
			if (level < lengthStamp)
				level = lengthStamp;
			i++;
		}
		return level;
	}

	long minArea() {
		long area = 1;
		for (int i = 0; i < dim; i++)
			area *= length[i].min();
		return area;
	}

	public boolean minLengthEq0() {
		boolean use = false;

		int i = 0;
		while (!use && i < dim) {
			use = (length[i].min() == 0);
			i++;
		}
		return use;
	}

	public boolean minUse(int SelDimension, IntRectangle U) {
		boolean use = true;
		int start, stop;

		int i = 0, j = 0;
		while (use && i < dim) {
			if (i != SelDimension) {
				Domain originIdom = origin[i].dom();
				start = originIdom.max();
				stop = originIdom.min() + length[i].min();
				if (start < stop) {
					U.add(start, stop - start);
					j++;
				} else
					use = false;
			} else {
				U.add(-1, -1);
			}
			i++;
		}
		return use;
	}

	public boolean minUse(IntRectangle U) {
		boolean use = true;
		int start, stop;

		int i = 0, j = 0;
		while (use && i < dim) {
			Domain originI = origin[i].dom();
			start = originI.max();
			stop = originI.min() + length[i].min();
			if (start < stop) {
				U.add(start, stop - start);
				j++;
			} else
				use = false;
			i++;
		}
		return use;
	}

	Variable origin(int i) {
		return origin[i];
	}

	public boolean settled() {
		boolean sat = true;
		int i = 0;
		while (sat && i < dim) {
			sat = sat && origin[i].singleton() && length[i].singleton();
			i++;
		}
		return sat;
	}

	@Override
	public String toString() {
		String S = "[";
		for (int i = 0; i < dim; i++) {
			S += origin[i] + ", ";
		}
		for (int i = 0; i < dim; i++) {
			S += length[i];
			if (i < dim - 1)
				S += ", ";
		}
		S += "]";
		return S;
	}

	public org.jdom.Element toXML() {

		org.jdom.Element rectangle = new org.jdom.Element("rectangle");

		rectangle.setAttribute("dimension", String.valueOf(dim));

		for (int i = 0; i < dim; i++) {

			rectangle.setAttribute("origin" + i, origin[i].id());
			rectangle.setAttribute("length" + i, length[i].id());

		}

		return rectangle;

	}

}
