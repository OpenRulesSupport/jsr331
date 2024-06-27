/**
 *  Distance2.java 
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
import java.util.Hashtable;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Interval;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraint |X - Y| #= Z
 * 
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class Distance extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = distance;

	boolean firstConsistencyCheck = false;

	Variable X, Y, Z;

	Hashtable<Variable, Integer> pruningEvents;

	int firstConsistencyLevel;

	// Constructors
	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Distance(Variable x, Variable y, Variable z) {
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
		if (level == firstConsistencyLevel)
			firstConsistencyCheck = true;
	}

	@Override
	public void consistency(Store store) {

		if (firstConsistencyCheck) {
			Z.domain.inMin(store.level, Z, 0);
			firstConsistencyCheck = false;
			firstConsistencyLevel = store.level;
		}

		while (store.newPropagation) {
			store.newPropagation = false;

			if (X.singleton()) {

				int xValue = X.value();
				// |X - Y| = Z

				Domain yDom = Y.dom();
				int ySize = yDom.noIntervals();

				IntervalDomain tempPlus4Z = new IntervalDomain(ySize);

				for (int i = ySize - 1; i >= 0; i--)
					if (xValue >= yDom.rightElement(i))
						tempPlus4Z.addDom(new Interval(xValue
								- yDom.rightElement(i), xValue
								- yDom.leftElement(i)));
					else if (xValue >= yDom.leftElement(i))
						tempPlus4Z.addDom(new Interval(0, xValue
								- yDom.leftElement(i)));

				IntervalDomain tempMinus4Z = new IntervalDomain(ySize);

				for (int i = 0; i < ySize; i++)
					if (xValue <= yDom.leftElement(i))
						tempMinus4Z.addDom(new Interval(-xValue
								+ yDom.leftElement(i), -xValue
								+ yDom.rightElement(i)));
					else if (xValue <= yDom.rightElement(i))
						tempMinus4Z.addDom(new Interval(0, -xValue
								+ yDom.rightElement(i)));

				tempPlus4Z.addDom(tempMinus4Z);
				Z.domain.in(store.level, Z, tempPlus4Z);

				// If Y changes Z then only if Z changes Y we execute
				// consistency again.
				store.newPropagation = false;

				// |X - Y| = Z

				Domain zDom = Z.dom();
				int zSize = Z.domain.noIntervals();

				IntervalDomain temp = new IntervalDomain(zSize);

				for (int i = zSize - 1; i >= 0; i--)
					temp.addDom(new Interval(-zDom.rightElement(i), -zDom
							.leftElement(i)));

				temp.addDom(zDom);

				Y.domain.inShift(store.level, Y, temp, xValue);

			} else {

				// X not singleton
				if (Y.singleton()) {

					int yValue = Y.value();
					// |X - Y| = Z

					Domain xDom = X.dom();
					int xSize = X.domain.noIntervals();

					IntervalDomain temp4PlusZ = new IntervalDomain(xSize);
					IntervalDomain temp4MinusZ = new IntervalDomain(xSize);

					for (int i = 0; i < xSize; i++)
						if (xDom.leftElement(i) - yValue > 0)
							temp4PlusZ.addDom(new Interval(xDom.leftElement(i)
									- yValue, xDom.rightElement(i) - yValue));
						else if (xDom.rightElement(i) - yValue > 0)
							temp4PlusZ.addDom(0, xDom.rightElement(i) - yValue);

					for (int i = xSize - 1; i >= 0; i--)
						if (xDom.rightElement(i) - yValue < 0)
							temp4MinusZ.addDom(new Interval(-xDom
									.rightElement(i)
									+ yValue, -xDom.leftElement(i) + yValue));
						else if (xDom.leftElement(i) - yValue < 0)
							temp4MinusZ.addDom(0, -xDom.leftElement(i) + yValue);

					temp4PlusZ.addDom(temp4MinusZ);
					Z.domain.in(store.level, Z, temp4PlusZ);

					// If X changes Z then only if Z changes X we execute
					// consistency again.
					store.newPropagation = false;

					// Y.singleton()
					// |X - Y| = Z

					Domain zDom = Z.dom();
					int zSize = zDom.noIntervals();

					IntervalDomain temp = new IntervalDomain(zSize);

					for (int i = zSize - 1; i >= 0; i--)
						temp.addDom(new Interval(-zDom.rightElement(i), -zDom
								.leftElement(i)));

					temp.addDom(zDom);

					X.domain.inShift(store.level, X, temp, yValue);

				} else {

					// X and Y not singleton

					if (Z.singleton()) {

						// Z is singleton
						int zValue = Z.value();

						Domain xDom = X.dom();
						int xSize = xDom.noIntervals();

						IntervalDomain tempPlusC = new IntervalDomain(xSize);
						IntervalDomain tempMinusC = new IntervalDomain(xSize);

						for (int i = 0; i < xSize; i++) {
							tempPlusC.addDom(new Interval(xDom.leftElement(i)
									+ zValue, xDom.rightElement(i) + zValue));
							tempMinusC.addDom(new Interval(xDom.leftElement(i)
									- zValue, xDom.rightElement(i) - zValue));
						}

						tempPlusC.addDom(tempMinusC);

						Y.domain.in(store.level, Y, tempPlusC);

						// If X changes Y then only if Y changes X we execute
						// consistency again.
						store.newPropagation = false;

						Domain yDom = Y.dom();
						int ySize = yDom.noIntervals();

						tempPlusC = new IntervalDomain(ySize);
						tempMinusC = new IntervalDomain(ySize);

						for (int i = 0; i < ySize; i++) {
							tempPlusC.addDom(new Interval(yDom.leftElement(i)
									+ zValue, yDom.rightElement(i) + zValue));
							tempMinusC.addDom(new Interval(yDom.leftElement(i)
									- zValue, yDom.rightElement(i) - zValue));
						}

						tempPlusC.addDom(tempMinusC);
						X.domain.in(store.level, X, tempPlusC);
						
					} else {
						// None is singleton

						// Y - X = Z
						IntervalDomain Xdom1 = new IntervalDomain(Y.min()
								- Z.max(), Y.max() - Z.min());
						// X - Y = Z
						Xdom1.addDom(Y.min() + Z.min(), Y.max() + Z.max());

						X.domain.in(store.level, X, Xdom1);

						store.newPropagation = false;

						// Y - X = Z

						IntervalDomain Ydom1 = new IntervalDomain(X.min()
								+ Z.min(), X.max() + Z.max());
						// X - Y = Z
						Ydom1.addDom(X.min() - Z.max(), X.max() - Z.min());

						Y.domain.in(store.level, Y, Ydom1);

						// Y - X = Z
						IntervalDomain Zdom1 = new IntervalDomain(Y.min()
								- X.max(), Y.max() - X.min());
						// X - Y = Z
						Zdom1.addDom(X.min() - Y.max(), X.max() - Y.min());

						Z.domain.in(store.level, Z, Zdom1);

					}

				}

			}

		}

	}

	@Override
	public int getNestedPruningEvent(Variable var, boolean mode) {

		// If consistency function mode
		if (mode) {
			if (pruningEvents != null) {
				Integer possibleEvent = pruningEvents.get(var);
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
		predicate.setAttribute("name", Constants.id_distance);

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText("int X int Y int Z");

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");
		org.jdom.Element functional = new org.jdom.Element("functional");
		functional.setText("eq(abs(X,Y),Z)");

		expression.addContent(functional);

		predicate.addContent(expression);

		return predicate;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return  id_distance + numberId;
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
		return (Xdom.singleton() && Ydom.singleton() && Zdom.singleton() && java.lang.Math
				.abs(Xdom.min() - Ydom.min()) == Zdom.min());
	}

	@Override
	public String toString() {

		return id() + " : Distance(" + X + ", " + Y + ", " + Z + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");

		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", "3");
		constraint.setAttribute("scope", X.id() + " " + Y.id() + " " + Z.id());
		constraint.setAttribute("reference", Constants.id_distance);

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {

		// If consistency function mode
			if (pruningEvents != null) {
				Integer possibleEvent = pruningEvents.get(var);
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
	public boolean notSatisfied() {

		Domain Xdom = X.dom(), Ydom = Y.dom(), Zdom = Z.dom();
		return (Xdom.singleton() && Ydom.singleton() && Zdom.singleton() && !(java.lang.Math
				.abs(Xdom.min() - Ydom.min()) == Zdom.min()));

	}

	@Override
	public void notConsistency(Store store) {

		while (store.newPropagation) {
			store.newPropagation = false;

			if (X.singleton()) {

				if (Z.singleton()) {

					// |X - Y| = Z
					// X - Y = Z => Y = X - Z
					// -X + Y = Z => Y = X + Z

					// Domain first = Domain.domain.minus(X.dom(), Z.dom());

					Y.domain.inComplement(store.level, Y, X.value() - Z.value());
					Y.domain.inComplement(store.level, Y, X.value() + Z.value());
					
				} else if (Y.singleton()) {

					Z.domain.inComplement(store.level, Z, X.value() - Y.value());
					Z.domain.inComplement(store.level, X, Y.value() - X.value());

				}

			} else if (Z.singleton() && Y.singleton()) {

				// |X - Y| = Z
				// -X + Y = Z => X = Y - Z, Y = X + Z
				// X - Y = Z => X = Y + Z, Y = X - Z

				X.domain.inComplement(store.level, X, Y.value() - Z.value());
				X.domain.inComplement(store.level, X, Y.value() + Z.value());

			}

		}

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
