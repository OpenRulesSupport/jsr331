/**
 *  XplusYplusQgtC.java 
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
 * Constraint X + Y + Q #> C
 * 
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class XplusYplusQgtC extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = gtC2;

	int C;

	Variable X, Y, Q;

	/**
	 * It creates X+Y+Q>=C constraint.
	 * @param x variable x.
	 * @param y variable y.
	 * @param q variable q.
	 * @param c constant c.
	 */
	public XplusYplusQgtC(Variable x, Variable y, Variable q, int c) {
		numberId = IdNumber++;
		X = x;
		Y = y;
		Q = q;
		C = c;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(3);

		Variables.add(X);
		Variables.add(Y);
		Variables.add(Q);
		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		while (store.newPropagation) {
			store.newPropagation = false;
			X.domain.inMin(store.level, X, C - Y.max() - Q.max() + 1);
			// store.in(X, C - Y.max() - Q.max() + 1, MaxInt);
			Y.domain.inMin(store.level, Y, C - X.max() - Q.max() + 1);
			// store.in(Y, C - X.max() - Q.max() + 1, MaxInt);
			Q.domain.inMin(store.level, Q, C - X.max() - Y.max() + 1);
			// store.in(Q, C - X.max() - Y.max() + 1, MaxInt);
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
			return Constants.BOUND;
		}
		// If notConsistency function mode
		else {
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.BOUND;
		}
	}

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {

		org.jdom.Element predicate = new org.jdom.Element("predicate");
		predicate.setAttribute("name", Constants.id_gtC2 + String.valueOf(C));

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText("int " + X.id() + " int " + Y.id() + " int "
				+ Q.id());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");
		org.jdom.Element functional = new org.jdom.Element("functional");
		functional.setText("gt(add(add(" + X.id() + "," + Y.id() + "),"
				+ Q.id() + ")," + String.valueOf(C) + ")");

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
			return Constants.BOUND;
		}

	@Override
	public int getNotConsistencyPruningEvent(Variable var) {

	// If notConsistency function mode
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.BOUND;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_gtC2 + numberId;
	}

	@Override
	public void impose(Store store) {

		X.putModelConstraint(this, getConsistencyPruningEvent(X));
		Y.putModelConstraint(this, getConsistencyPruningEvent(Y));
		Q.putModelConstraint(this, getConsistencyPruningEvent(Q));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void notConsistency(Store store) {
		while (store.newPropagation) {
			store.newPropagation = false;
			X.domain.inMax(store.level, X, C - Y.min() - Q.min());
			Y.domain.inMax(store.level, Y, C - X.min() - Q.min());
			Q.domain.inMax(store.level, Q, C - X.min() - Y.min());
		}
	}

	@Override
	public boolean notSatisfied() {
		return X.max() + Y.max() + Q.max() <= C;
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		X.removeConstraint(this);
		Y.removeConstraint(this);
		Q.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		return X.min() + Y.min() + Q.min() > C;
	}

	@Override
	public String toString() {

		return id() + " : XplusYplusQgtC(" + X + ", " + Y + ", " + Q + ", " + C
				+ " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");

		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", "3");
		constraint.setAttribute("scope", X.id() + " " + Y.id() + " " + Q.id());
		constraint.setAttribute("reference", Constants.id_gtC2 + String.valueOf(C));

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public Constraint getGuideConstraint() {
		return null;
	}

	@Override
	public int getGuideValue() {
		return 0;
	}

	@Override
	public Variable getGuideVariable() {
		return null;
	}

	@Override
	public void supplyGuideFeedback(boolean feedback) {
	}

	//@todo predicate can not recover this constraint from the XCSP description. 
	// It will be transformed into two constraints X+Y=A, A + Q > C.	

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			X.weight++;
			Y.weight++;
			Q.weight++;
		}
	}
}
