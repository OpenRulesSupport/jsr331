/**
 *  Task.java 
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

import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Variable;

/**
 * Represents tasks for cumulative constraint
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

class Task {

	Variable start, dur, res;

	Task(Variable Start, Variable Duration, Variable ResourceUsage) {
		start = Start;
		dur = Duration;
		res = ResourceUsage;
	}

	long areaMax() {
		return dur.max() * res.max();
	}

	long areaMin() {
		return dur.min() * res.min();
	}

	Domain Compl() {
		Domain sDom = start.dom();
		Domain dDom = dur.dom();
		return new IntervalDomain(sDom.min() + dDom.min(), sDom.max()
				+ dDom.max());
	}

	Domain Completion() {
		Domain sDom = start.dom();
		int dDomMin = dur.dom().min();
		return new IntervalDomain(sDom.min() + dDomMin, sDom.max() + dDomMin);
	}

	Variable Dur() {
		return dur;
	}

	int ECT() {
		return start.min() + dur.min();
	}

	int EST() {
		return start.min();
	}

	int LaCT() {
		return start.max() + dur.max();
	}

	int LCT() {
		return start.max() + dur.min();
	}

	int LST() {
		return start.max();
	}

	boolean minUse(IntTask t) {
		int lst, ect;
		Domain sDom = start.dom();

		lst = sDom.max();
		ect = sDom.min() + dur.min();
		if (lst < ect) {
			t.start = lst;
			t.stop = ect;
			return true;
		} else
			return false;
	}

	Variable Res() {
		return res;
	}

	Variable Start() {
		return start;
	}

	@Override
	public String toString() {
		return "[" + start + ", " + dur + ", " + res + "]";
	}

	public org.jdom.Element toXML() {

		org.jdom.Element task = new org.jdom.Element("task");

		task.setAttribute("start", start.id());
		task.setAttribute("duration", dur.id());
		task.setAttribute("resource", res.id());

		return task;

	}

}
