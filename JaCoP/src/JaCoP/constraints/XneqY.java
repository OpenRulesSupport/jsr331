/**
 *  XneqY.java 
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
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraints X #\= Y
 * 
 * Domain consistency is used.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class XneqY extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = nEq;

	Variable X, Y;

	/**
	 * It constructs X != Y constraint.
	 * @param x variable x.
	 * @param y variable y.
	 */
	public XneqY(Variable x, Variable y) {
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
	}

	@Override
	public void consistency(Store store) {

			if (Y.singleton())
				X.domain.inComplement(store.level, X, Y.min());

			if (X.singleton())
				Y.domain.inComplement(store.level, Y, X.min());
		
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
		predicate.setAttribute("name", Constants.id_nEq);

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText("int " + X.id() + " int " + Y.id());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");
		org.jdom.Element functional = new org.jdom.Element("functional");
		functional.setText("ne(" + X.id() + "," + Y.id() + ")");

		expression.addContent(functional);

		predicate.addContent(expression);

		return predicate;
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {

		// If consistency function mode
			if (consistencyPruningEvents != null) {
				Integer possibleEvent = consistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.GROUND;
		}

	@Override
	public int getNotConsistencyPruningEvent(Variable var) {

	// If notConsistency function mode
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.ANY;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_nEq + numberId;
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

			X.domain.in(store.level, X, Y.domain);
			store.newPropagation = false;
			Y.domain.in(store.level, Y, X.domain);
		}
	}

	@Override
	public boolean notSatisfied() {
		return X.singleton() && Y.singleton() && X.min() == Y.min();
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
		return !X.domain.isIntersecting(Y.domain);
	}

	@Override
	public String toString() {
		return id() + " : XneqY(" + X + ", " + Y + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");

		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", "2");
		constraint.setAttribute("scope", X.id() + " " + Y.id());
		constraint.setAttribute("reference", id_nEq);

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
