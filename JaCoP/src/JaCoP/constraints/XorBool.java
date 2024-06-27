/**
 *  XorBool.java
 *   
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
import java.util.HashSet;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraint ( X xor Y ) <=> Z.
 * 
 * X | Y | Z
 * 0   0   0
 * 0   1   1
 * 1   0   1
 * 1   1   0
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class XorBool extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = xorbool;

	Variable X, Y, Z;

	/** It constructs constraint (X xor Y ) <=> Z.
	 * @param x variable x.
	 * @param y variable y.
	 * @param z variable z.
	 */
	public XorBool(Variable x, Variable y, Variable z) {

		numberId = IdNumber++;
		numberArgs = 3;
		this.X = x;
		this.Y = y;
		this.Z = z;
		
		assert ( checkInvariants() == null) : checkInvariants();

	}

	/**
	 * It checks invariants required by the constraint. Namely that
	 * boolean variables have boolean domain. 
	 * 
	 * @return the string describing the violation of the invariant, null otherwise.
	 */
	public String checkInvariants() {

		if (X.min() < 0 || X.max() > 1)
			return "Variable " + X + " does not have boolean domain";

		if (Y.min() < 0 || Y.max() > 1)
			return "Variable " + Y + " does not have boolean domain";

		if (Z.min() < 0 || Z.max() > 1)
			return "Variable " + Z + " does not have boolean domain";

		return null;
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

// 			if (Z.singleton()) {
				
				if (Z.max() == 0) {
					X.domain.in(store.level, X, Y.domain);
					Y.domain.in(store.level, Y, X.domain);
				} else if (Z.min() == 1) {
					if (Y.singleton())
						X.domain.inComplement(store.level, X, Y.value() );
					if (X.singleton())
						Y.domain.inComplement(store.level, Y, X.value() );					
				}
				
// 			}

// 			if (X.singleton()) {

				if (X.max() == 0) {
					Z.domain.in(store.level, Z, Y.domain);
					Y.domain.in(store.level, Y, Z.domain);				
				} else if (X.min() == 1) {
					if (Y.singleton())
						Z.domain.inComplement(store.level, Z, Y.value() );
					if (Z.singleton())
						Y.domain.inComplement(store.level, Y, Z.value() );
				}
// 			}
			
// 			if (Y.singleton()) {
				
				if (Y.max() == 0) {
					Z.domain.in(store.level, Z, X.domain);
					X.domain.in(store.level, X, Z.domain);				
				} else if (Y.min() == 1) {
					if (X.singleton())
						Z.domain.inComplement(store.level, Z, X.value() );
					if (Z.singleton())
						X.domain.inComplement(store.level, X, Z.value() );
				}
				
// 			}
			
			
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
		return null;
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
			return id_xorbool + numberId;
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

			if (Z.singleton()) {
				
				if (Z.max() == 0) {
					if (Y.singleton())
						X.domain.inComplement(store.level, X, Y.value() );
					if (X.singleton())
						Y.domain.inComplement(store.level, Y, X.value() );					
				}
				
				if (Z.min() == 1) {
					X.domain.in(store.level, X, Y.domain);
					Y.domain.in(store.level, Y, X.domain);
				}
				
			}

			if (X.singleton()) {

				if (X.max() == 0) {
					if (Y.singleton())
						Z.domain.inComplement(store.level, Z, Y.value() );
					if (Z.singleton())
						Y.domain.inComplement(store.level, Y, Z.value() );
				}
			
				if (X.min() == 1) {
					Z.domain.in(store.level, Z, Y.domain);
					Y.domain.in(store.level, Y, Z.domain);				
				}
			}
			
			if (Y.singleton()) {
				
				if (Y.max() == 0) {
					if (X.singleton())
						Z.domain.inComplement(store.level, Z, X.value() );
					if (Z.singleton())
						X.domain.inComplement(store.level, X, Z.value() );
				}
				
				if (Y.min() == 1) {
					Z.domain.in(store.level, Z, Y.domain);
					Y.domain.in(store.level, Y, Z.domain);				
				}
				
			}

		}
	}

	@Override
	public boolean notSatisfied() {
		
		if (!X.singleton())
			return false;
		if (!Z.singleton())
			return false;
		if (!Y.singleton())
			return false;
		
		int sum = X.value() + Y.value() + Z.value();
		
		if (sum == 1 || sum == 3)
			return true;

		return false;
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

		if (!X.singleton())
			return false;
		if (!Z.singleton())
			return false;
		if (!Y.singleton())
			return false;
		
		int sum = X.value() + Y.value() + Z.value();
		
		if (sum == 0 || sum == 2)
			return true;

		return false;

	}

	@Override
	public String toString() {

		return id() + " : XorBool( (" + X + ", " + Y + ") <=> " + Z + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_xorbool);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(X);
		scopeVars.add(Y);
		scopeVars.add(Z);
		
		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() + " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	
				
		org.jdom.Element xEl = new org.jdom.Element("x");
		xEl.setText(X.id());
		constraint.addContent(xEl);

		org.jdom.Element yEl = new org.jdom.Element("y");
		yEl.setText(Y.id());
		constraint.addContent(yEl);

		org.jdom.Element zEl = new org.jdom.Element("z");
		zEl.setText(Z.id());
		constraint.addContent(zEl);
		
		return constraint;

	}

	/**
	 * It constructs a constraint from XCSP description.
	 * @param constraint an XML element describing the constraint.
	 * @param store the constraint store in which context the constraint is created.
	 * @return created constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		
		String x = constraint.getChild("x").getText();
		String y = constraint.getChild("y").getText();
		String z = constraint.getChild("z").getText();

		return new XorBool(store.findVariable(x), store.findVariable(y), store.findVariable(z));
		
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
