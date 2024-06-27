/**
 *  XmulCeqZ.java 
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
 * Constraint X * C #= Z
 * 
 * Boundary consistency is used.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class XmulCeqZ extends Constraint implements Constants {

	//@todo Make this constraint PrimitiveConstraint.
	
	static int IdNumber = 1;

	final static short type = mulC;

	int C;

	Variable X, Z;

	/**
	 * It constructs a constraint X * C = Z.
	 * @param x variable x.
	 * @param c constant c.
	 * @param z variable z.
	 */
	public XmulCeqZ(Variable x, int c, Variable z) {

		numberId = IdNumber++;
		numberArgs = 3;
		X = x;
		C = c;
		Z = z;
	}

	@Override
	public ArrayList<Variable> arguments() {
		ArrayList<Variable> args = new ArrayList<Variable>(2);

		args.add(X);
		args.add(Z);
		return args;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
    public void consistency (Store store) {
	int XMin, XMax;

	if (C != 0)
	    while ( store.newPropagation ) {
		store.newPropagation = false;

		// Bounds for X
		float d1 = (float)Z.min()/C;
		float d2 = (float)Z.max()/C;

		if (d1 <= d2) {
		    XMin = toInt( Math.round( Math.ceil( d1) ) );
		    XMax = toInt( Math.round( Math.floor( d2 )) );
		}
		else {
		    XMin = toInt( Math.round( Math.ceil( d2) ) );
		    XMax = toInt( Math.round( Math.floor( d1 )) );
		}

		if (XMin > XMax) 
		    store.throwFailException(this);

		X.domain.in(store.level, X, XMin, XMax);

		// Bounds for Z
		int ZMin, ZMax;
// 		int mul1 = X.min() * C;
// 		int mul2 = X.max() * C;
		int mul1 = multiply(X.min(), C);
		int mul2 = multiply(X.max(), C);
		if (mul1 <= mul2) {
		    ZMin = mul1;
		    ZMax = mul2;
		}
		else {
		    ZMin = mul2;
		    ZMax = mul1;
		}
		Z.domain.in(store.level, Z, ZMin, ZMax);
	    }
	else
	    Z.domain.in(store.level, Z, 0, 0);
    }

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {

		org.jdom.Element predicate = new org.jdom.Element("predicate");
		predicate.setAttribute("name", Constants.id_mulC
				+ (new Integer(C)).toString());

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText("int X int Z");

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");
		org.jdom.Element functional = new org.jdom.Element("functional");
		functional.setText("eq(mul(X," + (new Integer(C)).toString() + "),Z)");

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
			return id_mulC + numberId;
	}

	@Override
	public void impose(Store store) {
		X.putModelConstraint(this, getConsistencyPruningEvent(X));
		Z.putModelConstraint(this, getConsistencyPruningEvent(Z));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		X.removeConstraint(this);
		Z.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		Domain Xdom = X.dom(), Zdom = Z.dom();
		return Xdom.singleton() && Zdom.singleton()
				&& Xdom.min() * C == Zdom.min();
	}

	@Override
	public String toString() {

		return id() + " : XmulCeqZ(" + X + ", " + C + ", " + Z + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");

		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", "2");
		constraint.setAttribute("scope", X.id() + " " + Z.id());
		constraint.setAttribute("reference", Constants.id_mulC
				+ (new Integer(C)).toString());

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
			Z.weight++;
		}
	}
	
}
