/**
 *  XmulYeqC.java 
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
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraint X * Y #= C
 * 
 * Boundary consistency is used.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class XmulYeqC extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = muleqC;

	int C;

	boolean firstConsistency = true;

	Variable X, Y;

	boolean xSquare = false;

	/**
	 * It constructs constraint X * Y = C.
	 * @param x variable x.
	 * @param y variable y.
	 * @param c constant c.
	 */
	public XmulYeqC(Variable x, Variable y, int c) {
		numberId = IdNumber++;
		numberArgs = 2;
		X = x;
		Y = y;
		C = c;
		xSquare = (X == Y) ? true : false;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(3);

		Variables.add(X);
		Variables.add(Y);

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
    public void consistency (Store S) {

	if (xSquare)  // X^2 = C
	    while ( S.newPropagation ) {
		S.newPropagation = false;

		int XMin=Constants.MaxInt, XMax=Constants.MinInt;
		
		if ( C < 0 )
		    S.throwFailException(this);

		XMin = (int)Math.round( Math.ceil( Math.sqrt((double)C) ));
		XMax = (int)Math.round( Math.floor( Math.sqrt((double)C) ));

		if ( XMin > XMax )
		    S.throwFailException(this);

		Domain dom = new IntervalDomain(XMin, XMax);
		dom.addDom(-XMax, -XMin);

		X.domain.in(S.level, X, dom);

	    }
	else    // X*Y=Z
	    while ( S.newPropagation ) {
		S.newPropagation = false;

		int XMin=Constants.MaxInt, XMax=Constants.MinInt, 
		    YMin=Constants.MaxInt, YMax=Constants.MinInt;
		int mul;
		float div1, div2;

		// Bounds for X
		if (Y.min() <= -1 && Y.max() >= 1) {
		    XMin = Math.min(-C, C);
		    XMax = Math.max(-C, C);
		}
		else {
		    if (Y.min() != 0) {
			div1 = (float)C/(float)Y.min();
		    }
		    else {
			div1 = Constants.MinInt;
			div2 = Constants.MaxInt;
		    }
		    if (Y.max() != 0) {
			div2 = (float)C/(float)Y.max();
		    }
		    else {
			div1 = Constants.MinInt;
			div2 = Constants.MaxInt;
		    }
		    float min = Math.min(div1,div2);
		    float max = Math.max(div1,div2);
		    XMin = (int)Math.round( Math.ceil( min) );
		    XMax = (int)Math.round( Math.floor( max ));
		}
// 		System.out.println(X + " :: " + XMin + ".." + XMax);
		X.domain.in(S.level, X, XMin, XMax);
	    
		// Bounds for Y
		if (X.min() <= -1 && X.max() >= 1) {
		    YMin = Math.min(-C, C);
		    YMax = Math.max(-C, C);
		}
		else {
		    if (X.min() != 0) {
			div1 = (float)C/(float)X.min();
		    }
		    else {
			div1 = Constants.MinInt;
			div2 = Constants.MaxInt;
		    }
		    if (X.max() != 0) {
			div2 = (float)C/(float)X.max();
		    }
		    else {
			div1 = Constants.MinInt;
			div2 = Constants.MaxInt;
		    }
		    float min = Math.min(div1,div2);
		    float max = Math.max(div1,div2);
		    YMin = (int)Math.round( Math.ceil( min) );
		    YMax = (int)Math.round( Math.floor( max ));
		}
// 		System.out.println(Y + " :: " + YMin + ".." + YMax);
		Y.domain.in(S.level, Y, YMin, YMax);

		// Bounds for C
		int ZMin=Constants.MaxInt, ZMax=Constants.MinInt;
//  		mul = X.min() * Y.min();
		mul = multiply(X.min(), Y.min());
		if (mul < ZMin) ZMin = mul;
		if (mul > ZMax) ZMax = mul;
// 		mul = X.min() * Y.max();
		mul = multiply(X.min(), Y.max());
		if (mul < ZMin) ZMin = mul;
		if (mul > ZMax) ZMax = mul;
// 		mul = X.max() * Y.min();
		mul = multiply(X.max(), Y.min());
		if (mul < ZMin) ZMin = mul;
		if (mul > ZMax) ZMax = mul;
// 		mul = X.max() * Y.max();
		mul = multiply(X.max(),Y.max());
		if (mul < ZMin) ZMin = mul;
		if (mul > ZMax) ZMax = mul;

// 		System.out.println(X + ", " + Y);
// 		System.out.println(C + " :: " + ZMin + ".." + ZMax);
		if ( C < ZMin || C > ZMax  )
		    S.throwFailException(this);

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
		predicate.setAttribute("name", Constants.id_muleqC
				+ (new Integer(C)).toString());

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText("int " + X.id() + " int " + Y.id());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");
		org.jdom.Element functional = new org.jdom.Element("functional");
		functional.setText("eq(mul(" + X.id() + "," + Y.id() + "),"
				+ (new Integer(C)).toString() + ")");

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
			return Constants.ANY;
			
			
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
			return id_muleqC + numberId;
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

			if (X.singleton()) {
				if (C % X.value() == 0)
					Y.domain.inComplement(store.level, Y, C / X.value());
			} else if (Y.singleton()) {
				if (C % Y.value() == 0)
					X.domain.inComplement(store.level, X, C / Y.value());
			}
		}
	}

	@Override
	public boolean notSatisfied() {
		Domain Xdom = X.dom(), Ydom = Y.dom();
		return (Xdom.max() * Ydom.max() < C || Xdom.min() * Ydom.min() > C);
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
		Domain Xdom = X.dom(), Ydom = Y.dom();

		return (Xdom.singleton() && Ydom.singleton() && (Xdom.min()
				* Ydom.min() == C));

	}

	@Override
	public String toString() {

		return id() + " : XmulYeqC(" + X + ", " + Y + ", " + C + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");

		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", "3");
		constraint.setAttribute("scope", X.id() + " " + Y.id());
		constraint.setAttribute("reference", Constants.id_muleqC
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
			Y.weight++;
		}
	}	
	
}
