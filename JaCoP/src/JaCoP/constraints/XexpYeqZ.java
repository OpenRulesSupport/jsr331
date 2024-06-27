/**
 *  XexpYeqZ.java 
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
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraint X ^ Y #= Z
 * 
 * Boundary consistecny is used.
 * 
 * @author Krzysztof Kuchcinski
 * @version 2.4
 */

public class XexpYeqZ extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = exp;

	Variable X, Y, Z;

	/**
	 * It constructs constraint X^Y=Z.
	 * @param x variable x.
	 * @param y variable y.
	 * @param z variable z.
	 */
	public XexpYeqZ(Variable x, Variable y, Variable z) {
		numberId = IdNumber++;
		numberArgs = 3;
		X = x;
		Y = y;
		Z = z;
		if (X.min() < 0 || Y.min() < 0 || Z.min() < 0) {
			String s = new String(
					"\nFDV's must be >= 0 in X ^ Y = Z constraint");
			throw new JaCoPException(s);
		}
	}

	double aLog(double a, double x) {
		return Math.log(x) / Math.log(a);
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> FDVs = new ArrayList<Variable>(3);

		FDVs.add(X);
		FDVs.add(Y);
		FDVs.add(Z);
		return FDVs;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		double XMin, XMax, YMin, YMax, ZMin, ZMax;
		while (store.newPropagation) {
			store.newPropagation = false;

			XMin = Math.max(Math.pow(Z.min(), 1.0 / Y.max()), X.min());
			XMax = Math.min(Math.pow(Z.max(), 1.0 / Y.min()), X.max());
			YMin = Math.max(aLog(X.max(), Z.min()), Y.min());
			YMax = Math.min(aLog(X.min(), Z.max()), Y.max());
			ZMin = Math.max(Math.pow(X.min(), Y.min()), Z.min());
			ZMax = Math.min(Math.pow(X.max(), Y.max()), Z.max());

			X.domain.in(store.level, X, (int) Math.floor(XMin), (int) Math
					.ceil(XMax));

			Y.domain.in(store.level, Y, (int) Math.floor(YMin), (int) Math
					.ceil(YMax));

			Z.domain.in(store.level, Z, (int) Math.floor(ZMin), (int) Math
					.ceil(ZMax));
		}

	}

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {

		org.jdom.Element predicate = new org.jdom.Element("predicate");
		predicate.setAttribute("name", Constants.id_exp);

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText("int " + X.id() + " int " + Y.id() + " int "
				+ Z.id());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");
		org.jdom.Element functional = new org.jdom.Element("functional");
		functional.setText("eq(pow(" + X.id() + "," + Y.id() + ")," + Z.id()
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
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_exp + numberId;
	}

	@Override
	public void impose(Store Store) {
		X.putModelConstraint(this, getConsistencyPruningEvent(X));
		Y.putModelConstraint(this, getConsistencyPruningEvent(Y));
		Z.putModelConstraint(this, getConsistencyPruningEvent(Z));
		Store.addChanged(this);
		Store.countConstraint();
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
		return Xdom.singleton() && Ydom.singleton() && Zdom.singleton()
				&& Math.pow(Xdom.min(), Ydom.min()) == Zdom.min();
	}

	@Override
	public String toString() {

		return id() + " : XexpYeqZ(" + X + ", " + Y + ", " + Z + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");

		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", "3");
		constraint.setAttribute("scope", X.id() + " " + Y.id() + " " + Z.id());
		constraint.setAttribute("reference", Constants.id_exp);

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
