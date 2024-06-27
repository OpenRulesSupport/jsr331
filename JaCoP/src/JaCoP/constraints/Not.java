/**
 *  Not.java 
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
 * Constraint "not costraint"
 * 
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Not extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = not;

	PrimitiveConstraint C;
	
	/**
	 * It constructs not constraint.
	 * @param c primitive constraint which is being negated.
	 */
	public Not(PrimitiveConstraint c) {
		numberId = IdNumber++;
		C = c;
		numberArgs += c.numberArgs();
	}

	@Override
	public ArrayList<Variable> arguments() {

		return C.arguments();

	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		C.notConsistency(store);
	}

	@Override
	public int getNestedPruningEvent(Variable var, boolean mode) {

		return getConsistencyPruningEvent(var);

	}

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {

		org.jdom.Element predicate = new org.jdom.Element("predicate");
		predicate.setAttribute("name", "predicate4" + id() );

		StringBuffer parString = new StringBuffer();

		for (Variable v : C.arguments())
			parString.append("int ").append(v.id()).append(" ");

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText(parString.toString().trim());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");

		org.jdom.Element functional = new org.jdom.Element("functional");

		StringBuffer functionalString = new StringBuffer();
		functionalString.append("not(");

		org.jdom.Element insideConstraint = C.getPredicateDescriptionXML();

		functionalString.append(insideConstraint.getChild("expression")
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
			return C.getNestedPruningEvent(var, false);
	}

		
	@Override
	public int getNotConsistencyPruningEvent(Variable var) {
		
		// If notConsistency function mode
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return C.getNestedPruningEvent(var, true);
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_not + numberId;
	}

	@Override
	public void impose(Store store) {

		SimpleHashSet<Variable> variables = new SimpleHashSet<Variable>();

		for (Variable V : C.arguments())
			variables.add(V);

		while (!variables.isEmpty()) {
			Variable V = variables.removeFirst();
			V.putModelConstraint(this, getConsistencyPruningEvent(V));
		}

		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void notConsistency(Store store) {
		C.consistency(store);
	}

	@Override
	public boolean notSatisfied() {
		return C.satisfied();
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {

		for (Variable V : C.arguments())
			V.removeConstraint(this);

	}

	@Override
	public boolean satisfied() {
		return C.notSatisfied();
	}

	@Override
	public String toString() {
		return id() + " : Not( " + C + ")";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("id", id() );

		HashSet<Variable> parametersVariables = new HashSet<Variable>();

		parametersVariables.addAll(C.arguments());

		StringBuffer parString = new StringBuffer();

		for (Variable v : parametersVariables)
			parString.append(v.id()).append(" ");

		constraint.setAttribute("scope", parString.toString().trim());
		constraint.setAttribute("reference", "predicate4" + id());

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}
	
	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			C.increaseWeight();
		}
	}

}
