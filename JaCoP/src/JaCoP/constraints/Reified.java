/**
 *  Reified.java 
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
import JaCoP.core.Domain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.util.SimpleHashSet;

/**
 * Reified constraints "constraint" #<=> B
 * 
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Reified extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = reified;

	Variable B;

	PrimitiveConstraint C;

	// Constructors
	/**
	 * It creates Reified constraint.
	 * @param c primitive constraint c.
	 * @param b boolean variable b.
	 */
	public Reified(PrimitiveConstraint c, Variable b) {
		
		if (b.min() >= 0 && b.max() <= 1) {
		
			numberId = IdNumber++;
			numberArgs = 2;
			C = c;
			B = b;
			
		} else {
			throw new JaCoPException("\nVariable variable in reified constraint nust have domain 0..1");
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
		// the reified constraint loops itself
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
			// store.in(B, 1, 1);
		} else if (C.notSatisfied()) {
			store.newPropagation = true;
			B.domain.in(store.level, B, 0, 0);
			// store.in(B, 0, 0);
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
			return id_reified + numberId;
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
		return (Bdom.min() == 1 && C.satisfied())
				|| (Bdom.max() == 0 && C.notSatisfied());
	}

	@Override
	public String toString() {

		return id() + " : Reified(" + C + ", " + B + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("id", id() );
		constraint.setAttribute("var", B.id());
		constraint.setAttribute("reference", id_reified);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(B);
		scopeVars.addAll(C.arguments());

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	
				
		org.jdom.Element primitiveConstraint = C.toXML();

		primitiveConstraint.addContent(C.getPredicateDescriptionXML());

		constraint.addContent(primitiveConstraint);

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	/**
	 * It creates a constraint from XCSP description.
	 * @param constraint XML element describing the constraint.
	 * @param store the constraint store in which context the constraint is being created.
	 * @return created constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		
		String internalConstraintParameters = null;

		if (constraint.getChild("constraint").getChild("parameters") != null)
			internalConstraintParameters = constraint.getChild(
					"constraint").getChild("parameters").getText();

		if (internalConstraintParameters == null)
			internalConstraintParameters = constraint.getChild(
					"constraint").getAttributeValue("scope");

		String internalConstraintPredicateParameters = constraint
				.getChild("constraint").getChild("predicate").getChild(
						"parameters").getText();
		String internalConstraintPredicateExpression = constraint
				.getChild("constraint").getChild("predicate").getChild(
						"expression").getChild("functional").getText();

		PrimitiveConstraint internalConstraint = (new Predicate(
				internalConstraintParameters,
				internalConstraintPredicateParameters,
				internalConstraintPredicateExpression, store))
				.getConstraint(store);

		return new Reified(internalConstraint, 
							store.findVariable(constraint.getAttributeValue("var")));
		
	}


    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			B.weight++;
			C.increaseWeight();
		}
	}	
	
}
