/**
 *  In.java 
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

import java.util.*;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraints X in FiniteDomain
 * 
 * Domain consistecny is used.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class In extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = in;

	Domain Dom, DomComplement;

	Variable X;

	/**
	 * It constructs an In constraint to restrict the domain of the variable.
	 * @param x variable x for which the restriction is applied.
	 * @param dom the domain to which the variables domain is restricted.
	 */
	public In(Variable x, Domain dom) {
		numberId = IdNumber++;
		numberArgs = 1;
		X = x;
		Dom = dom;
		DomComplement = Dom.complement();
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(1);

		Variables.add(X);
		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		store.newPropagation = false;
		X.domain.in(store.level, X, Dom);
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
			return Constants.NONE;		
		}
	
	@Override
	public int getNotConsistencyPruningEvent(Variable var) {

		// If notConsistency function mode
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.NONE;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_in + numberId;
	}

	@Override
	public void impose(Store store) {
		X.putModelConstraint(this, getConsistencyPruningEvent(X));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void notConsistency(Store store) {
		store.newPropagation = false;
		X.domain.in(store.level, X, DomComplement);
	}

	@Override
	public boolean notSatisfied() {
	    return !X.domain.isIntersecting(Dom);
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		X.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		return X.singleton() && Dom.contains(X.domain);
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
			return Constants.ANY;
		}
		// If notConsistency function mode
		else {
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.ANY;
		}
	}


	@Override
	public String toString() {
		return id() + " : In(" + X + ", " + Dom + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("id", id() );
		constraint.setAttribute("type", id_in);

		org.jdom.Element domain = Dom.toXML();
		constraint.addContent(domain);

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public Constraint getGuideConstraint() {
		return new XeqC(X, X.min());
	}

	@Override
	public int getGuideValue() {
		return X.min();
	}

	@Override
	public Variable getGuideVariable() {
		return X;
	}

	@Override
	public void supplyGuideFeedback(boolean feedback) {
	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			X.weight++;
		}
	}	
	
}
