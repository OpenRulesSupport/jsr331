/**
 *  XplusYplusCeqZ.java 
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
 * Constraints X + Y + C #= Z.
 * 
 * Boundary consistency is used.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class XplusYplusCeqZ extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = add3C;

	int C;

	Variable X, Y, Z;

	/**
	 * It constructs constraint X+Y+C=Z. 
	 * @param x variable X.
	 * @param y variable Y.
	 * @param c constant C.
	 * @param z variable Z.
	 */
	public XplusYplusCeqZ(Variable x, Variable y, int c, Variable z) {
		numberId = IdNumber++;
		X = x;
		Y = y;
		C = c;
		Z = z;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(3);

		Variables.add(X);
		Variables.add(Y);
		Variables.add(Z);
		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		while (store.newPropagation) {
			store.newPropagation = false;
			X.domain.in(store.level, X, Z.min() - Y.max() - C, Z.max()
					- Y.min() - C);
			// store.in(X, Z.min() - Y.max() - C, Z.max() - Y.min() - C);
			Y.domain.in(store.level, Y, Z.min() - X.max() - C, Z.max()
					- X.min() - C);
			// store.in(Y, Z.min() - X.max() - C, Z.max() - X.min() - C);
			Z.domain.in(store.level, Z, X.min() + Y.min() + C, X.max()
					+ Y.max() + C);
			// store.in(Z, X.min() + Y.min() + C, X.max() + Y.max() + C);
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
			return Constants.GROUND;
		}
	}

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {

		org.jdom.Element predicate = new org.jdom.Element("predicate");
		predicate.setAttribute("name", Constants.id_add3C + String.valueOf(C));

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText("int " + X.id() + " int " + Y.id() + " int "
				+ Z.id());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");
		org.jdom.Element functional = new org.jdom.Element("functional");
		functional.setText("eq(add(add(" + X.id() + "," + Y.id() + "),"
				+ String.valueOf(C) + ")," + Z.id() + ")");

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
			return Constants.GROUND;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_add3C + numberId;
	}

	@Override
	public void impose(Store store) {

		X.putModelConstraint(this, getConsistencyPruningEvent(X));
		Y.putModelConstraint(this, getConsistencyPruningEvent(Y));
		Z.putModelConstraint(this, getConsistencyPruningEvent(Z));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void notConsistency(Store store) {
		while (store.newPropagation) {
			store.newPropagation = false;
			if (Z.singleton() && Y.singleton()) {
				X.domain.inComplement(store.level, X, Z.min() - Y.min() - C);
				// int val = Z.min() - Y.min() - C;
				// Domain ZYCcompl = Domain.domain.complement(val);
				// store.in(X, ZYCcompl);
			}

			if (Z.singleton() && X.singleton()) {
				Y.domain.inComplement(store.level, Y, Z.min() - X.min() - C);
				// int val = Z.min() - X.min() - C;
				// Domain ZXCcompl = Domain.domain.complement(val);
				// store.in(Y, ZXCcompl);
			}

			if (X.singleton() && Y.singleton()) {
				Z.domain.inComplement(store.level, Z, X.min() + Y.min() + C);
				// int val = X.min() + Y.min() + C;
				// Domain XYCcompl = Domain.domain.complement(val);
				// store.in(Z, XYCcompl);
			}
		}
	}

	@Override
	public boolean notSatisfied() {
		return (X.max() + Y.max() + C < Z.min() || X.min() + Y.min() + C > Z
				.max());
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		X.removeConstraint(this);
		Y.removeConstraint(this);
		Z.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		return X.singleton() && Y.singleton() && Z.singleton()
				&& X.min() + Y.min() + C == Z.min();
	}

	@Override
	public String toString() {

		return id() + " : XplusYplusQeqZ(" + X + ", " + Y + ", " + ", " + C
				+ ", " + Z + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");

		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", "3");
		constraint.setAttribute("scope", X.id() + " " + Y.id() + " " + Z.id());
		constraint.setAttribute("reference", Constants.id_add3C + String.valueOf(C));

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	//TODO, predicate can not recover this constrait from the description. 
	// It will be transformed into two constraints X+Y=A, A + C = Z.	
	
	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			X.weight++;
			Y.weight++;
			Z.weight++;
		}
	}
	
}
