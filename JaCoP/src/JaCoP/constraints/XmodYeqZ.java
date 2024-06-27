/**
 *  XmodYeqZ.java 
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
 * Constraint X mod Y #= Z
 * 
 * Boundary consistency is used.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class XmodYeqZ extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = mul;

	Variable X, Y, Z;
	//     int reminderMin, reminderMax;

	/**
	 * It constructs a constraint X * Y = Z.
	 * @param x variable x.
	 * @param y variable y.
	 * @param z variable z.
	 */
	public XmodYeqZ(Variable x, Variable y, Variable z) {
		numberId = IdNumber++;
		numberArgs = 3;
		X = z;
		Y = y;
		Z = x;
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

		//   	System.out.println("*** " + Z + " mod " + Y + " = " + X);

		int resultMin=Constants.MinInt, resultMax=Constants.MaxInt;

		// X*Y=Z
		while ( S.newPropagation ) {
			S.newPropagation = false;

			int YMin=Constants.MaxInt, YMax=Constants.MinInt;
			int ZMin=Constants.MaxInt, ZMax=Constants.MinInt;
			int mul;
			float div1, div2, div3, div4;

			boolean ZcontaintsZero = Z.min() <=0 && Z.max() >= 0;
			boolean YcontaintsZero = Y.min() <=0 && Y.max() >= 0;

			if (Y.singleton(0))
				S.throwFailException(this);

			// Compute bounds for reminder 

			int reminderMin, reminderMax;

			if (Z.min() >= 0) {
				reminderMin = 0;
				reminderMax = Math.max(Math.abs(Y.min()), Math.abs(Y.max()))  - 1;
			}
			else if (Z.max() < 0) {
				reminderMax = 0;
				reminderMin = - Math.max(Math.abs(Y.min()), Math.abs(Y.max())) + 1;
			} 
			else {
				reminderMin = Math.min(Math.min(Y.min(),-Y.min()), Math.min(Y.max(),-Y.max())) + 1;
				reminderMax = Math.max(Math.max(Y.min(),-Y.min()), Math.max(Y.max(),-Y.max())) - 1;
			}

			//   	    System.out.println(X+" --> reminder :: " + reminderMin + ".." + reminderMax);	
			X.domain.in(S.level, X, reminderMin, reminderMax);


			// Bounds for result
			if ( ! (ZcontaintsZero && YcontaintsZero) ) {// if Z and Y contains 0 then X can have any values!!!
				int oldResultMin= resultMin, oldResultMax= resultMax; 
				if (Y.min() <= -1 && Y.max() >= 1) {
					resultMin = getMin(Z.min(), Z.max(), reminderMin, reminderMax); 
					resultMax = getMax(Z.min(), Z.max(), reminderMin, reminderMax);
				}
				else {
					if (Y.min() != 0) {
						div1 = (float)(Z.min() - X.max())/(float)Y.min();
						div2 = (float)(Z.max() - X.min())/(float)Y.min();
					}
					else {
						div1 = (float)Constants.MinInt;
						div2 = (float)Constants.MaxInt;
					}
					if (Y.max() != 0) {
						div3 = (float)(Z.min() - X.max())/(float)Y.max();
						div4 = (float)(Z.max() - X.min())/(float)Y.max();
					}
					else {
						div3 = (float)Constants.MinInt;
						div4 = (float)Constants.MaxInt;
					}
					float min = Math.min(Math.min(div1,div2), Math.min(div3,div4));
					float max = Math.max(Math.max(div1,div2), Math.max(div3,div4));
					resultMin = (int)Math.round( Math.ceil( min) );
					resultMax = (int)Math.round( Math.floor( max ));
				}

				if (resultMin > resultMax)
					S.throwFailException(this);

				if (oldResultMin != resultMin || oldResultMax != resultMax)
					S.newPropagation = true;
			}

			//  	    System.out.println("result = " + resultMin + ".." + resultMax);

			// 	    boolean XcontaintsZero = X.min() <=0 && X.max() >= 0;

			boolean resultContaintsZero = resultMin <=0 && resultMax >= 0;

			// Bounds for Y
			if ( !(ZcontaintsZero && resultContaintsZero) ) {// if Z and X contains 0 then Y can have any values!!!
				if ( resultMin <= -1 && resultMax >= 1) {
					YMin = getMin(Z.min(), Z.max(), reminderMin, reminderMax); 
					YMax = getMax(Z.min(), Z.max(), reminderMin, reminderMax); //Math.max(Math.max(-Z.min(), Z.min()), Math.max(-Z.max(), Z.max()));
				}
				else {
					if (resultMin != 0) {
						div1 = (float)(Z.min() - X.max())/(float)resultMin;
						div2 = (float)(Z.max() - X.min())/(float)resultMin;
					}
					else {
						div1 = (float)Constants.MinInt;
						div2 = (float)Constants.MaxInt;
					}
					if (resultMax != 0) {
						div3 = (float)(Z.min() - X.max())/(float)resultMax;
						div4 = (float)(Z.max() - X.min())/(float)resultMax;
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

				//   		System.out.println(Y + " :: " + YMin+".."+YMax);
				if (YMin > YMax)
					S.throwFailException(this);

				Y.domain.in(S.level, Y, YMin, YMax);
			}

			// Bounds for Z and reminder
			// 		mul = X.min() * Y.min();
			mul = multiply(resultMin, Y.min());
			if (mul < ZMin) ZMin = mul;
			if (mul > ZMax) ZMax = mul;
			// 		mul = X.min() * Y.max();
			mul = multiply(resultMin, Y.max());
			if (mul < ZMin) ZMin = mul;
			if (mul > ZMax) ZMax = mul;
			// 		mul = X.max() * Y.min();
			mul = multiply(resultMax, Y.min());
			if (mul < ZMin) ZMin = mul;
			if (mul > ZMax) ZMax = mul;
			// 		mul = X.max() * Y.max();
			mul = multiply(resultMax,Y.max());
			if (mul < ZMin) ZMin = mul;
			if (mul > ZMax) ZMax = mul;
			// x * y = z + r

			reminderMin = Z.min() - ZMax;
			reminderMax = Z.max() - ZMin;

			X.domain.in(S.level, X, reminderMin, reminderMax);

			//  	    System.out.println(Z + " :: " + ((int)(ZMin  + X.min())) +".."+ ((int)(ZMax + X.max())));
			Z.domain.in(S.level, Z, ZMin + X.min(), ZMax + X.max());

			assert checkSolution(resultMin, resultMax) == null : checkSolution(resultMin, resultMax) ;

		}
	}

	int getMin(int zMin, int zMax, int reminderMin, int reminderMax) {
		int minimal = JaCoP.core.Constants.MaxInt;

		minimal = (minimal > zMin - reminderMin) ? zMin - reminderMin : minimal;
		minimal = (minimal > -zMin - reminderMin) ? -zMin - reminderMin : minimal;
		minimal = (minimal > zMin + reminderMin) ? zMin + reminderMin : minimal;
		minimal = (minimal > -zMin + reminderMin) ? -zMin + reminderMin : minimal;
		minimal = (minimal > zMin - reminderMax) ? zMin - reminderMax : minimal;
		minimal = (minimal > -zMin - reminderMax) ? -zMin - reminderMax : minimal;
		minimal = (minimal > zMin + reminderMax) ? zMin + reminderMax : minimal;
		minimal = (minimal > -zMin + reminderMax) ? -zMin + reminderMax : minimal;

		minimal = (minimal > zMax - reminderMin) ? zMax - reminderMin : minimal;
		minimal = (minimal > -zMax - reminderMin) ? -zMax - reminderMin : minimal;
		minimal = (minimal > zMax + reminderMin) ? zMax + reminderMin : minimal;
		minimal = (minimal > -zMax + reminderMin) ? -zMax + reminderMin : minimal;
		minimal = (minimal > zMax - reminderMax) ? zMax - reminderMax : minimal;
		minimal = (minimal > -zMax - reminderMax) ? -zMax - reminderMax : minimal;
		minimal = (minimal > zMax + reminderMax) ? zMax + reminderMax : minimal;
		minimal = (minimal > -zMax + reminderMax) ? -zMax + reminderMax : minimal;

		return minimal;
	}

	int getMax(int zMin, int zMax, int reminderMin, int reminderMax) {
		int maximal = JaCoP.core.Constants.MinInt;

		maximal = (maximal < zMin - reminderMin) ? zMin - reminderMin : maximal;
		maximal = (maximal < -zMin - reminderMin) ? -zMin - reminderMin : maximal;
		maximal = (maximal < zMin + reminderMin) ? zMin + reminderMin : maximal;
		maximal = (maximal < -zMin + reminderMin) ? -zMin + reminderMin : maximal;
		maximal = (maximal < zMin - reminderMax) ? zMin - reminderMax : maximal;
		maximal = (maximal < -zMin - reminderMax) ? -zMin - reminderMax : maximal;
		maximal = (maximal < zMin + reminderMax) ? zMin + reminderMax : maximal;
		maximal = (maximal < -zMin + reminderMax) ? -zMin + reminderMax : maximal;

		maximal = (maximal < zMax - reminderMin) ? zMax - reminderMin : maximal;
		maximal = (maximal < -zMax - reminderMin) ? -zMax - reminderMin : maximal;
		maximal = (maximal < zMax + reminderMin) ? zMax + reminderMin : maximal;
		maximal = (maximal < -zMax + reminderMin) ? -zMax + reminderMin : maximal;
		maximal = (maximal < zMax - reminderMax) ? zMax - reminderMax : maximal;
		maximal = (maximal < -zMax - reminderMax) ? -zMax - reminderMax : maximal;
		maximal = (maximal < zMax + reminderMax) ? zMax + reminderMax : maximal;
		maximal = (maximal < -zMax + reminderMax) ? -zMax + reminderMax : maximal;

		return maximal;
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
		functional.setText("eq(mod(X,Y),Z)");

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
			return id_mod + numberId;
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

		return Xdom.singleton() && Ydom.singleton() && Zdom.singleton() &&
		( Ydom.min() * Xdom.min() <= Zdom.min() || Ydom.min() * Xdom.min() < Zdom.min() + Ydom.min());
	}

	@Override
	public String toString() {

		return id() + " : XmodYeqZ(" + X + ", " + Y + ", " + Z + " )";
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

	String checkSolution(int resultMin, int resultMax) {
		String result = null;

		if (X.singleton() && Y.singleton() && Z.singleton()) {
			result = "Operation mod does not hold " + Z + " mod " + Y + " = " + X + "(result "+resultMin+".."+resultMax;
			for (int i=resultMin; i<=resultMax; i++) {
				if ( i*Y.value() + X.value() == Z.value() )
					result = null;
			}
		}
		else 
			result = null;
		return result;
	}
}
