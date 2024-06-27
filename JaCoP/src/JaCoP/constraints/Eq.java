/**
 *  Eq.java 
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

import JaCoP.core.Constants;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.util.SimpleHashSet;

/**
 * Constraint "constraint1" #<=> "constraint2"
 * 
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Eq extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = eqConstr;

	PrimitiveConstraint c1, c2;


	/**
	 * It constructs equality constraint between two constraints.
	 * @param c1 the first constraint 
	 * @param c2 the second constraint
	 */
	public Eq(PrimitiveConstraint c1, PrimitiveConstraint c2) {
		numberId = IdNumber++;
		numberArgs = 2;
		this.c1 = c1;
		this.c2 = c2;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(1);

		variables.addAll(c1.arguments());
		variables.addAll(c2.arguments());
		
		return variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {

		store.newPropagation = false;
		// Does not need to loop on newPropagation
		if (c2.satisfied()) {
			store.newPropagation = true;
			c1.consistency(store);
		} else if (c2.notSatisfied()) {
			store.newPropagation = true;
			c1.notConsistency(store);
		}
		if (c1.satisfied()) {
			store.newPropagation = true;
			c2.consistency(store);
		} else if (c1.notSatisfied()) {
			store.newPropagation = true;
			c2.notConsistency(store);
		}
	}

	@Override
	public int getNestedPruningEvent(Variable var, boolean mode) {

		return getConsistencyPruningEvent(var);

	}

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {

		org.jdom.Element predicate = new org.jdom.Element("predicate");
		predicate.setAttribute("name", "predicate4" + id());

		HashSet<Variable> parametersVariables = new HashSet<Variable>();

		parametersVariables.addAll(c1.arguments());
		parametersVariables.addAll(c2.arguments());

		StringBuffer parString = new StringBuffer();

		for (Variable v : parametersVariables)
			parString.append("int ").append(v.id()).append(" ");

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText(parString.toString().trim());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");

		org.jdom.Element functional = new org.jdom.Element("functional");

		StringBuffer functionalString = new StringBuffer();
		functionalString.append("eq(");

		org.jdom.Element insideConstraintC1 = c1.getPredicateDescriptionXML();

		functionalString.append(insideConstraintC1.getChild("expression")
				.getChildText("functional"));

		functionalString.append(",");

		org.jdom.Element insideConstraintC2 = c2.getPredicateDescriptionXML();

		functionalString.append(insideConstraintC2.getChild("expression")
				.getChildText("functional"));

		functionalString.append(")");

		functional.setText(functionalString.toString());

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

			int eventAcross = -1;

			if (c1.arguments().contains(var)) {
				int event = c1.getNestedPruningEvent(var, true);
				if (event > eventAcross)
					eventAcross = event;
			}

			if (c1.arguments().contains(var)) {
				int event = c1.getNestedPruningEvent(var, false);
				if (event > eventAcross)
					eventAcross = event;
			}

			if (c2.arguments().contains(var)) {
				int event = c2.getNestedPruningEvent(var, true);
				if (event > eventAcross)
					eventAcross = event;
			}

			if (c2.arguments().contains(var)) {
				int event = c2.getNestedPruningEvent(var, false);
				if (event > eventAcross)
					eventAcross = event;
			}

			if (eventAcross == -1)
				return Constants.NONE;
			else
				return eventAcross;
			
			
		}

	@Override
	public int getNotConsistencyPruningEvent(Variable var) {
		
		// If notConsistency function mode
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}

		int eventAcross = -1;

		if (c1.arguments().contains(var)) {
			int event = c1.getNestedPruningEvent(var, true);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (c1.arguments().contains(var)) {
			int event = c1.getNestedPruningEvent(var, false);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (c2.arguments().contains(var)) {
			int event = c2.getNestedPruningEvent(var, true);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (c2.arguments().contains(var)) {
			int event = c2.getNestedPruningEvent(var, false);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (eventAcross == -1)
			return Constants.NONE;
		else
			return eventAcross;

	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_eqConstr + numberId;
	}

	@Override
	public void impose(Store store) {

		SimpleHashSet<Variable> variables = new SimpleHashSet<Variable>();

		for (Variable V : c1.arguments())
			variables.add(V);

		for (Variable V : c2.arguments())
			variables.add(V);

		while (!variables.isEmpty()) {
			Variable V = variables.removeFirst();
			V.putModelConstraint(this, getConsistencyPruningEvent(V));
		}

		store.addChanged(this);
		store.countConstraint(2);
	}

	@Override
	public void notConsistency(Store store) {

		store.newPropagation = false;
		if (c2.satisfied()) {
			store.newPropagation = true;
			c1.notConsistency(store);
		} else if (c2.notSatisfied()) {
			store.newPropagation = true;
			c1.consistency(store);
		}
		if (c1.satisfied()) {
			store.newPropagation = true;
			c2.notConsistency(store);
		} else if (c1.notSatisfied()) {
			store.newPropagation = true;
			c2.consistency(store);
		}
	}

	@Override
	public boolean notSatisfied() {
		return (c1.satisfied() && c2.notSatisfied())
				|| (c1.notSatisfied() && c2.satisfied());
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {

		for (Variable V : c1.arguments())
			V.removeConstraint(this);

		for (Variable V : c2.arguments())
			V.removeConstraint(this);

	}

	@Override
	public boolean satisfied() {
		return (c1.satisfied() && c2.satisfied())
				|| (c1.notSatisfied() && c2.notSatisfied());
	}

	@Override
	public String toString() {

		return id() + " : Eq(" + c1 + ", " + c2 + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("id", id() );

		HashSet<Variable> parametersVariables = new HashSet<Variable>();

		parametersVariables.addAll(c1.arguments());
		parametersVariables.addAll(c2.arguments());

		StringBuffer parString = new StringBuffer();

		for (Variable v : parametersVariables)
			parString.append(v.id()).append(" ");

		constraint.setAttribute("arity", String.valueOf(parametersVariables
				.size()));
		constraint.setAttribute("scope", parString.toString().trim());
		constraint.setAttribute("reference", "predicate4" + id() );

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			c1.increaseWeight();
			c2.increaseWeight();
		}
	}
	
}
