/**
 *  Xor.java 
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
import JaCoP.core.Domain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.util.SimpleHashSet;

/**
 * Xor constraints - xor("constraint", B).
 * 
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Xor extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = xor;

	Variable B;

	PrimitiveConstraint C;

	/**
	 * It constructs a xor constraint.
	 * @param c constraint c.
	 * @param b boolean variable b.
	 */
	public Xor(PrimitiveConstraint c, Variable b) {
		if (b.min() >= 0 && b.max() <= 1) {
			numberId = IdNumber++;
			numberArgs = 2;
			C = c;
			B = b;
		} else {
			String s = new String(
					"\nVariable variable in xor constraint nust have domain 0..1");
			throw new JaCoPException(s);
		}
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(1);

		Variables.add(B);

		Variables.addAll(C.arguments());

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {

		// Does not need to loop on newPropagation since
		// the constraint C loops itself
		store.newPropagation = false;
		if (B.max() == 0) { // C must be false
			store.newPropagation = true;
			C.consistency(store);
		} else if (B.min() == 1) { // C must be true
			store.newPropagation = true;
			C.notConsistency(store);
		} else if (C.satisfied()) {
			store.newPropagation = true;
			B.domain.in(store.level, B, 0, 0);
		} else if (C.notSatisfied()) {
			store.newPropagation = true;
			B.domain.in(store.level, B, 1, 1);
		}
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
		functionalString.append("xor(");

		org.jdom.Element insideConstraint = C.getPredicateDescriptionXML();

		functionalString.append(insideConstraint.getChild("expression")
				.getChildText("functional"));

		functionalString.append("," + B.id() + ")");

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
			
			if (var == B)
				return Constants.GROUND;
			else {
				
				int eventAcross = -1;
				
				if (C.arguments().contains(var)) {
					int event = C.getNestedPruningEvent(var, true);
					if (event > eventAcross)
						eventAcross = event;
				}

				if (C.arguments().contains(var)) {
					int event = C.getNestedPruningEvent(var, false);
					if (event > eventAcross)
						eventAcross = event;
				}

				if (eventAcross == -1)
					return Constants.NONE;
				else
					return eventAcross;				
			}
	}

	
	@Override
	public int getNotConsistencyPruningEvent(Variable var) {

		// If notConsistency function mode
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			if (var == B)
				return Constants.GROUND;
			else {
				
				int eventAcross = -1;
				
				if (C.arguments().contains(var)) {
					int event = C.getNestedPruningEvent(var, true);
					if (event > eventAcross)
						eventAcross = event;
				}

				if (C.arguments().contains(var)) {
					int event = C.getNestedPruningEvent(var, false);
					if (event > eventAcross)
						eventAcross = event;
				}

				if (eventAcross == -1)
					return Constants.NONE;
				else
					return eventAcross;				
			}
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_xor + numberId;
	}

	@Override
	public void impose(Store store) {

		SimpleHashSet<Variable> variables = new SimpleHashSet<Variable>();

		variables.add(B);

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
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {

		B.removeConstraint(this);

		for (Variable V : C.arguments())
			V.removeConstraint(this);

	}

	@Override
	public boolean satisfied() {
		Domain Bdom = B.dom();
		return (Bdom.min() == 0 && C.satisfied())
				|| (Bdom.max() == 1 && C.notSatisfied());
	}

	@Override
	public String toString() {

		return id() + " : Xor(" + C + ", " + B + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("id", id() );

		HashSet<Variable> parametersVariables = new HashSet<Variable>();

		parametersVariables.addAll(C.arguments());
		parametersVariables.add(B);

		StringBuffer parString = new StringBuffer();

		for (Variable v : parametersVariables)
			parString.append(v.id()).append(" ");

		constraint.setAttribute("scope", parString.toString().trim());
		constraint.setAttribute("reference", "predicate4" + id() );

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

    @Override
	public void notConsistency(Store store) {

		// Does not need to loop on newPropagation since
		// the constraint C loops itself
		store.newPropagation = false;
		if (B.max() == 0) { // C must be false
			store.newPropagation = true;
			C.notConsistency(store);
		} else if (B.min() == 1) { // C must be true
			store.newPropagation = true;
			C.consistency(store);
		} else if (C.satisfied()) {
			store.newPropagation = true;
			B.domain.in(store.level, B, 1, 1);
		} else if (C.notSatisfied()) {
			store.newPropagation = true;
			B.domain.in(store.level, B, 0, 0);
		}
	}

	@Override
	public boolean notSatisfied() {
		Domain Bdom = B.dom();
		return (Bdom.min() == 1 && C.satisfied())
				|| (Bdom.max() == 0 && C.notSatisfied());
	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			B.weight++;
			C.increaseWeight();
		}
	}

}
