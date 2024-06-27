/**
 *  XplusYeqZ.java 
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
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraint X + Y #= Z
 * 
 * Boundary consistency is used.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class XplusYeqZ extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = add;

	Variable X, Y, Z;

	/** It constructs constraint X+Y=Z.
	 * @param x variable x.
	 * @param y variable y.
	 * @param z variable z.
	 */
	public XplusYeqZ(Variable x, Variable y, Variable z) {
		numberId = IdNumber++;
		numberArgs = 3;
		X = x;
		Y = y;
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

			if (X.singleton()) {

				// store.in(Y, Domain.domain.minus(Z.dom(), X.value()));
				// store.in(Z, Domain.domain.plus(Y.dom(), X.value()));

				// store.inShift(Y, Z.dom(), -X.value());

				Y.domain.inShift(store.level, Y, Z.domain, -X.value());
				Z.domain.inShift(store.level, Z, Y.domain, X.value());

				// store.inShift(Z, Y.dom(), X.value());

			} else if (Y.singleton()) {

				// store.in(X, Domain.domain.minus(Z.dom(), Y.value()));
				// store.in(Z, Domain.domain.plus(X.dom(), Y.value()));

				X.domain.inShift(store.level, X, Z.domain, -Y.value());
				// store.inShift(X, Z.dom(), -Y.value());
				Z.domain.inShift(store.level, Z, X.dom(), Y.value());
				// store.inShift(Z, X.dom(), Y.value());

			} else {
				X.domain.in(store.level, X, Z.min() - Y.max(), Z.max()
						- Y.min());
				// store.in(X, Z.min() - Y.max(), Z.max() - Y.min());
				Y.domain.in(store.level, Y, Z.min() - X.max(), Z.max()
						- X.min());
				// store.in(Y, Z.min() - X.max(), Z.max() - X.min());
				Z.domain.in(store.level, Z, X.min() + Y.min(), X.max()
						+ Y.max());
				// store.in(Z, X.min() + Y.min(), X.max() + Y.max());
			}

			// Propagation in between indexicals does not happen
			// often to actually pay off for not using temporary variables.
			// int Xmin = X.min(); int Xmax = X.max();
			// int Ymin = Y.min(); int Ymax = Y.max();
			// int Zmin = Z.min(); int Zmax = Z.max();

			// in(S, X, Zmin - Ymax, Zmax - Ymin);
			// in(S, Y, Zmin - Xmax, Zmax - Xmin);
			// in(S, Z, Xmin + Ymin, Xmax + Ymax);

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
			return Constants.GROUND;
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
		predicate.setAttribute("name", Constants.id_add);

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText("int " + X.id() + " int " + Y.id() + " int "
				+ Z.id());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");
		org.jdom.Element functional = new org.jdom.Element("functional");
		functional.setText("eq(add(" + X.id() + "," + Y.id() + ")," + Z.id()
				+ ")");

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
			// return Constants.ANY;
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
			return id_add + numberId;
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
				// int val = Z.min() - Y.min();
				// Domain ZYcompl = Domain.domain.complement(val);
				// store.in(X, ZYcompl);
				X.domain.inComplement(store.level, X, Z.min() - Y.min());
			}

			if (Z.singleton() && X.singleton()) {
				// int val = Z.min() - X.min();
				// Domain ZXcompl = Domain.domain.complement(val);
				// store.in(Y, ZXcompl);
				Y.domain.inComplement(store.level, Y, Z.min() - X.min());
			}

			if (X.singleton() && Y.singleton()) {
				// int val = X.min() + Y.min();
				// Domain XYcompl = Domain.domain.complement(val);
				// store.in(Z, XYcompl);
				Z.domain.inComplement(store.level, Z, X.min() + Y.min());
			}
		}
	}

	@Override
	public boolean notSatisfied() {
		Domain Xdom = X.dom(), Ydom = Y.dom(), Zdom = Z.dom();
		return (Xdom.max() + Ydom.max() < Zdom.min() || Xdom.min() + Ydom.min() > Zdom
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
		Domain Xdom = X.dom(), Ydom = Y.dom(), Zdom = Z.dom();
		return (Xdom.singleton() && Ydom.singleton() && Zdom.singleton() && Xdom
				.min()
				+ Ydom.min() == Zdom.min());
	}

	@Override
	public String toString() {

		return id() + " : XplusYeqZ(" + X + ", " + Y + ", " + Z + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");

		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", "3");
		constraint.setAttribute("scope", X.id() + " " + Y.id() + " " + Z.id());
		constraint.setAttribute("reference", Constants.id_add);

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
			Z.weight++;
		}
	}
	
}
