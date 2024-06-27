/**
 *  XmulYeqZ.java 
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
 * Constraint X * Y #= Z
 * 
 * Boundary consistency is used.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class XmulYeqZ extends Constraint implements Constants {

    static int IdNumber = 1;

    final static short type = mul;

    Variable X, Y, Z;

    boolean xSquare = false;

    /**
     * It constructs a constraint X * Y = Z.
     * @param x variable x.
     * @param y variable y.
     * @param z variable z.
     */
    public XmulYeqZ(Variable x, Variable y, Variable z) {
	numberId = IdNumber++;
	numberArgs = 3;
	X = x;
	Y = y;
	Z = z;
	xSquare = (X == Y) ? true : false;
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
	public void consistency (Store S) {

	if (xSquare)  // X^2 = Z
	    while ( S.newPropagation ) {
		S.newPropagation = false;

		int XMin=Constants.MaxInt, XMax=Constants.MinInt;

		Z.domain.in(S.level, Z, 0, MaxInt);

		XMin = toInt( Math.round( Math.ceil( Math.sqrt((double)Z.min()) )) );
		XMax = toInt( Math.round( Math.floor( Math.sqrt((double)Z.max()) )) );

		if ( XMin > XMax )
		    S.throwFailException(Z);

		Domain dom = new IntervalDomain(XMin, XMax);
		dom.addDom(-XMax, -XMin);

		X.domain.in(S.level, X, dom);

	    }
	else    // X*Y=Z
	    while ( S.newPropagation ) {
		S.newPropagation = false;

		int XMin=Constants.MaxInt, XMax=Constants.MinInt, 
		    YMin=Constants.MaxInt, YMax=Constants.MinInt;
		int ZMin=Constants.MaxInt, ZMax=Constants.MinInt;
		int mul;
		float div1, div2, div3, div4;

		boolean ZcontaintsZero = Z.min() <=0 && Z.max() >= 0;
		boolean YcontaintsZero = Y.min() <=0 && Y.max() >= 0;

		// Bounds for X
		if ( !(ZcontaintsZero && YcontaintsZero) ) {// if Z and Y contains 0 then X can have any values!!!
		    if (Y.min() <= -1 && Y.max() >= 1) {
			XMin = Math.min(Math.min(-Z.min(), Z.min()), Math.min(-Z.max(), Z.max()));
			XMax = -XMin; //Math.max(Math.max(-Z.min(), Z.min()), Math.max(-Z.max(), Z.max()));
		    }
		    else {
			if (Y.min() != 0) {
			    div1 = (float)Z.min()/(float)Y.min();
			    div2 = (float)Z.max()/(float)Y.min();
			}
			else {
			    div1 = (float)Constants.MinInt;
			    div2 = (float)Constants.MaxInt;
			}
			if (Y.max() != 0) {
			    div3 = (float)Z.min()/(float)Y.max();
			    div4 = (float)Z.max()/(float)Y.max();
			}
			else {
			    div3 = (float)Constants.MinInt;
			    div4 = (float)Constants.MaxInt;
			}
			float min = Math.min(Math.min(div1,div2), Math.min(div3,div4));
			float max = Math.max(Math.max(div1,div2), Math.max(div3,div4));
			XMin = (int)Math.round( Math.ceil( min) );
			XMax = (int)Math.round( Math.floor( max ));
		    }

		    // 		    System.out.println(X + " :: " + XMin+".."+XMax);
		    if (XMin > XMax) 
			S.throwFailException(this);
		    
		    X.domain.in(S.level, X, XMin, XMax);
		}

		boolean XcontaintsZero = X.min() <=0 && X.max() >= 0;

		// Bounds for Y
		if ( !(ZcontaintsZero && XcontaintsZero) ) {// if Z and X contains 0 then Y can have any values!!!
		    if (X.min() <= -1 && X.max() >= 1) {
			YMin = Math.min(Math.min(-Z.min(), Z.min()), Math.min(-Z.max(), Z.max()));
			YMax = -YMin; //Math.max(Math.max(-Z.min(), Z.min()), Math.max(-Z.max(), Z.max()));
		    }
		    else {
			if (X.min() != 0) {
			    div1 = (float)Z.min()/(float)X.min();
			    div2 = (float)Z.max()/(float)X.min();
			}
			else {
			    div1 = (float)Constants.MinInt;
			    div2 = (float)Constants.MaxInt;
			}
			if (X.max() != 0) {
			    div3 = (float)Z.min()/(float)X.max();
			    div4 = (float)Z.max()/(float)X.max();
			}
			else {
			    div3 = (float)Constants.MinInt;
			    div4 = (float)Constants.MaxInt;
			}
			float min = Math.min(Math.min(div1,div2), Math.min(div3,div4));
			float max = Math.max(Math.max(div1,div2), Math.max(div3,div4));
			YMin = (int)Math.round( Math.ceil( min) );
			YMax = (int)Math.round( Math.floor( max ));
		    }
		    
		    if (YMin > YMax)
			S.throwFailException(this);

		    Y.domain.in(S.level, Y, YMin, YMax);
		}

		// Bounds for Z
// 		mul = X.min() * Y.min();
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
		Z.domain.in(S.level, Z, ZMin, ZMax);
	    }
    }

    @Override
	public org.jdom.Element getPredicateDescriptionXML() {

	org.jdom.Element predicate = new org.jdom.Element("predicate");
	predicate.setAttribute("name", Constants.id_mul);

	org.jdom.Element parameters = new org.jdom.Element("parameters");
	parameters.setText("int X int Y int Z");

	predicate.addContent(parameters);

	org.jdom.Element expression = new org.jdom.Element("expression");
	org.jdom.Element functional = new org.jdom.Element("functional");
	functional.setText("eq(mul(X,Y),Z)");

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
	    return id_mul + numberId;
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
	    && Xdom.min() * Ydom.min() == Zdom.min();
    }

    @Override
	public String toString() {

	return id() + " : XmulYeqZ(" + X + ", " + Y + ", " + Z + " )";
    }

    @Override
	public org.jdom.Element toXML() {

	org.jdom.Element constraint = new org.jdom.Element("constraint");

	constraint.setAttribute("name", id() );
	constraint.setAttribute("arity", "3");
	constraint.setAttribute("scope", X.id() + " " + Y.id() + " " + Z.id());
	constraint.setAttribute("reference", Constants.id_mul);

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
