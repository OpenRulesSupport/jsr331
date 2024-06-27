/**
 *  AbsXeqY.java 
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

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Interval;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraints |X| #= Y
 * 
 * Domain consistency is used.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class AbsXeqY extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	static final short type = abseq;

	static final boolean debugAll = false;

	boolean firstConsistencyCheck = true;
	
	int firstConsistencyLevel;
	
	/**
	 * It contains variable x.
	 */
	public Variable X;
	
	/**
	 * It contains variable y.
	 */
	public Variable Y;

	/**
	 * It constructs |X| = Y constraints.
	 * @param x variable X
	 * @param y variable Y
	 */
	public AbsXeqY(Variable x, Variable y) {
		numberId = IdNumber++;
		numberArgs = 2;
		X = x;
		Y = y;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(2);

		Variables.add(X);
		Variables.add(Y);
		return Variables;
	}

	@Override
	public void removeLevel(int level) {
		if (level == firstConsistencyLevel) 
			firstConsistencyCheck = true;
	}

	@Override
	public void consistency(Store store) {

		if (firstConsistencyCheck) {
			Y.domain.inMin(store.level, Y, 0);
			firstConsistencyCheck = false;
			firstConsistencyLevel = store.level;
		}

		while (store.newPropagation) {

			if (debugAll)
				System.out.println("X " + X + " Y " + Y);

			if (X.domain.domainID() == Domain.IntervalDomainID
					&& Y.domain.domainID() == Domain.IntervalDomainID) {

				IntervalDomain xDom = (IntervalDomain) X.domain;
				IntervalDomain yDom1 = new IntervalDomain(xDom.size + 1);

				int i = 0;
				Interval[] intervals = xDom.intervals;
				for (; i < xDom.size; i++)
					if (intervals[i].max > 0)
						break;

				int j = i;
				if (j == xDom.size)
					j--;

				for (; j >= 0; j--)
					if (intervals[j].max <= 0)
						yDom1.addDom(-intervals[j].max, -intervals[j].min);

				if (i < xDom.size && intervals[i].min < 0
						&& intervals[i].max > 0) {

					if (-intervals[i].min > intervals[i].max)
						yDom1.addDom(0, -intervals[i].min);
					else
						yDom1.addDom(0, intervals[i].max);

				}

				IntervalDomain yDom = new IntervalDomain(xDom.size + 1);

				for (; i < xDom.size; i++)
					yDom.addDom(intervals[i]);

				yDom.addDom(yDom1);

				if (debugAll)
					System.out.println("new Ydom " + yDom);

				// @todo, test more the change from yDom1 to yDom.
				Y.domain.in(store.level, Y, yDom);

				xDom = new IntervalDomain(xDom.size + 1);
				yDom = (IntervalDomain) Y.domain;

				for (i = yDom.size - 1; i >= 0; i--)
					xDom.addDom(-yDom.intervals[i].max, -yDom.intervals[i].min);

				xDom.addDom(yDom);

				if (debugAll)
					System.out.println("new Xdom " + xDom);

				X.domain.in(store.level, X, xDom);

				store.newPropagation = false;
				
				continue;
			}

			assert false;
		}
	}

	@Override
	public int getNestedPruningEvent(Variable var, boolean mode) {

		// If consistency function mode
		if (mode) {
			if (consistencyPruningEvents != null) {
				Integer possibleEvent = consistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.ANY;
		}
		// If notConsistency function mode
		else {
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.GROUND;
		}
	}

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {

		org.jdom.Element predicate = new org.jdom.Element("predicate");
		predicate.setAttribute("name", Constants.id_abseq);

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText("int " + X.id() + " int " + Y.id());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");
		org.jdom.Element functional = new org.jdom.Element("functional");
		functional.setText("eq(abs(" + X.id() + ")," + Y.id() + ")");

		expression.addContent(functional);

		predicate.addContent(expression);

		return predicate;
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {

		// consistency function mode
		if (consistencyPruningEvents != null) {
			Integer possibleEvent = consistencyPruningEvents.get(var);
			if (possibleEvent != null)
				return possibleEvent;
		}
		return Constants.ANY;

	}

		
	@Override
	public int getNotConsistencyPruningEvent(Variable var) {
		// notConsistency function mode
		if (notConsistencyPruningEvents != null) {
			Integer possibleEvent = notConsistencyPruningEvents.get(var);
			if (possibleEvent != null)
				return possibleEvent;
		}
		return Constants.GROUND;
	}

	@Override
	public String id() {
		
		if (id != null)
			return id;
		else
			return id_abseq + numberId;
	
	}

	@Override
	public void impose(Store store) {
		X.putModelConstraint(this, getConsistencyPruningEvent(X));
		Y.putModelConstraint(this, getConsistencyPruningEvent(Y));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void notConsistency(Store store) {

		while (store.newPropagation) {
			store.newPropagation = false;
			if (Y.singleton()) {

				X.domain.inComplement(store.level, X, Y.value());
				X.domain.inComplement(store.level, X, -Y.value());

			}
			if (X.singleton()) {

				if (X.value() >= 0)
					Y.domain.inComplement(store.level, Y, X.value());
				else
					Y.domain.inComplement(store.level, Y, -X.value());
			}
		}
	}

	@Override
	public boolean notSatisfied() {

		Domain xDom = X.domain;
		Domain yDom = Y.domain;
		int xSize = xDom.noIntervals();
		for (int i = 0; i < xSize; i++) {

			int right = xDom.rightElement(i);

			if (right <= 0) {
				if (yDom.isIntersecting(-right, -xDom.leftElement(i)))
					return false;
			} else {

				int left = xDom.leftElement(i);
				if (left >= 0) {
					if (yDom.isIntersecting(left, right))
						return false;
				} else {

					if (yDom.isIntersecting(0, -left))
						return false;
					if (yDom.isIntersecting(0, right))
						return false;
				}

			}

		}

		return true;

	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		X.removeConstraint(this);
		Y.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		return X.singleton() && Y.singleton()
				&& (X.min() == Y.min() || -X.min() == Y.min());
	}


	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : absXeqY(").append(X).append(", ").append(Y).append(" )");
		
		return result.toString();
				
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");

		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", "2");
		constraint.setAttribute("scope", X.id() + " " + Y.id());
		constraint.setAttribute("reference", Constants.id_abseq);

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			X.weight++;
			Y.weight++;
		}
	}

}
