/**
 *  IfThenElse.java 
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

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraint if constraint1 then constraint2 else constraint3
 * 
 * Boundary consistency is used.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class IfThenElse extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = ifthenelse;

	PrimitiveConstraint Cond, Then, Else;

	// imposed variable to manifest that constraint has been imposed (top-level)
	// constraint
	boolean imposed = false;

	Store store;

	/**
	 * It creates ifthenelse constraint.
	 * @param cond the condition of the constraint.
	 * @param then the condition which must be true if the constraint condition is true.
	 * @param elseC the condition which must be true if the constraint condition is not true.
	 */
	// Constructors
	public IfThenElse(PrimitiveConstraint cond, PrimitiveConstraint then,
			PrimitiveConstraint elseC) {
		numberId = IdNumber++;
		numberArgs = 2;
		Cond = cond;
		Then = then;
		Else = elseC;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(1);

		for (Variable V : Cond.arguments())
			Variables.add(V);

		for (Variable V : Then.arguments())
			Variables.add(V);

		for (Variable V : Else.arguments())
			Variables.add(V);

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {

		store.newPropagation = false;
		if (Cond.satisfied()) {
			store.newPropagation = true;
			Then.consistency(store);
		} else if (Cond.notSatisfied()) {
			store.newPropagation = true;
			Else.consistency(store);
		}

		if (imposed) {
			if (Then.notSatisfied()) {
				store.newPropagation = true;
				Cond.notConsistency(store);
			}

			if (Else.notSatisfied()) {
				store.newPropagation = true;
				Cond.consistency(store);
			}
		}

	}

	@Override
	public boolean notSatisfied() {
		return (Cond.satisfied() && Then.notSatisfied())
				|| (Cond.notSatisfied() && Else.notSatisfied());
	}

	@Override
	public void notConsistency(Store S) {

		S.newPropagation = false;

		if (Cond.notSatisfied()) {

			Else.notConsistency(S);

		}

		// It has to be two different if statements as
		// ! notSatisfied() is not equivalent to satisfied().

		if (Cond.satisfied()) {

			Then.notConsistency(S);

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
		}
		// If notConsistency function mode
		else {
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
		}

		int eventAcross = -1;

		if (Cond.arguments().contains(var)) {
			int event = Cond.getNestedPruningEvent(var, true);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Cond.arguments().contains(var)) {
			int event = Cond.getNestedPruningEvent(var, false);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Then.arguments().contains(var)) {
			int event = Then.getNestedPruningEvent(var, true);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Then.arguments().contains(var)) {
			int event = Then.getNestedPruningEvent(var, false);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Else.arguments().contains(var)) {
			int event = Else.getNestedPruningEvent(var, true);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Else.arguments().contains(var)) {
			int event = Else.getNestedPruningEvent(var, false);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (eventAcross == -1)
			return Constants.NONE;
		else
			return eventAcross;

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
			
			int eventAcross = -1;

			if (Cond.arguments().contains(var)) {
				int event = Cond.getNestedPruningEvent(var, true);
				if (event > eventAcross)
					eventAcross = event;
			}

			if (Cond.arguments().contains(var)) {
				int event = Cond.getNestedPruningEvent(var, false);
				if (event > eventAcross)
					eventAcross = event;
			}

			if (Then.arguments().contains(var)) {
				int event = Then.getNestedPruningEvent(var, true);
				if (event > eventAcross)
					eventAcross = event;
			}

			if (Then.arguments().contains(var)) {
				int event = Then.getNestedPruningEvent(var, false);
				if (event > eventAcross)
					eventAcross = event;
			}

			if (Else.arguments().contains(var)) {
				int event = Else.getNestedPruningEvent(var, true);
				if (event > eventAcross)
					eventAcross = event;
			}

			if (Else.arguments().contains(var)) {
				int event = Else.getNestedPruningEvent(var, false);
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

		if (Cond.arguments().contains(var)) {
			int event = Cond.getNestedPruningEvent(var, true);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Cond.arguments().contains(var)) {
			int event = Cond.getNestedPruningEvent(var, false);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Then.arguments().contains(var)) {
			int event = Then.getNestedPruningEvent(var, true);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Then.arguments().contains(var)) {
			int event = Then.getNestedPruningEvent(var, false);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Else.arguments().contains(var)) {
			int event = Else.getNestedPruningEvent(var, true);
			if (event > eventAcross)
				eventAcross = event;
		}

		if (Else.arguments().contains(var)) {
			int event = Else.getNestedPruningEvent(var, false);
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
			return id_ifthenelse + numberId;
	}

	@Override
	public void impose(Store store) {

		this.store = store;

		for (Variable V : Cond.arguments())
			V.putModelConstraint(this, getConsistencyPruningEvent(V));

		for (Variable V : Then.arguments())
			V.putModelConstraint(this, getConsistencyPruningEvent(V));

		for (Variable V : Else.arguments())
			V.putModelConstraint(this, getConsistencyPruningEvent(V));

		store.addChanged(this);
		store.countConstraint();

		imposed = true;
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {

		for (Variable V : Cond.arguments())
			V.removeConstraint(this);

		for (Variable V : Then.arguments())
			V.removeConstraint(this);

		for (Variable V : Else.arguments())
			V.removeConstraint(this);

	}

	@Override
	public boolean satisfied() {

		if (imposed) {

			if (Cond.satisfied()) {
				this.removeConstraint();
				store.impose(Then);
				return false;
			}

			if (Cond.notSatisfied()) {
				this.removeConstraint();
				store.impose(Else);
				return false;
			}

		}

		return (Cond.satisfied() && Then.satisfied())
				|| (Cond.notSatisfied() && Else.satisfied());

	}

	@Override
	public String toString() {

		StringBuffer result = new StringBuffer( id() );
		result.append(" : IfThenElse(").append( Cond ).append( ", " );
		result.append(Then).append( ", " ).append( Else ).append( " )" );
		
		return result.toString();
		
	}

	@Override
	public org.jdom.Element toXML() {
		
		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("id", id() );
		constraint.setAttribute("type", id_ifthenelse);

		org.jdom.Element primitiveConstraint = new org.jdom.Element("cond");
		
		org.jdom.Element condElement = Cond.toXML();
		org.jdom.Element predicateDescription = Cond.getPredicateDescriptionXML();
		
		primitiveConstraint.addContent(condElement);
		
		if (predicateDescription != null) 
			primitiveConstraint.addContent(predicateDescription);
		
		constraint.addContent(primitiveConstraint);

		primitiveConstraint = new org.jdom.Element("then");
		
		org.jdom.Element thenElement = Then.toXML();
		predicateDescription = Then.getPredicateDescriptionXML();
		
		primitiveConstraint.addContent(thenElement);
		
		if (predicateDescription != null)
			primitiveConstraint.addContent(predicateDescription);		
		
		constraint.addContent(primitiveConstraint);

		primitiveConstraint = new org.jdom.Element("else");
		
		org.jdom.Element elseElement = Else.toXML();
		predicateDescription = Else.getPredicateDescriptionXML();
		
		primitiveConstraint.addContent(elseElement);
		
		if (predicateDescription != null)
			primitiveConstraint.addContent(predicateDescription);		
		
		constraint.addContent(primitiveConstraint);
		
		return constraint;
		

	}

	@Override
	public short type() {
		return type;
	}

	/**
	 * It constructs a constraint from XCSP description.
	 * @param constraint an XML element describing the constraint.
	 * @param store the constraint store in which context the constraint is created.
	 * @return created constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
	
		Element condElement = constraint.getChild("cond");
		
		Element predicateCond = condElement.getChild("predicate");
		
		PrimitiveConstraint cond = null;
		
		if (predicateCond != null) {
			Element constraintCond = condElement.getChild("constraint");
		
			String parametersCond = predicateCond.getChild("parameters").getText();
			String functionalExpressionCond = predicateCond.getChild("expression")
						.getChild("functional").getText();
		
			String scopeCond = constraintCond.getAttributeValue("scope");	
		
			String constraintCondParameters = null;
		
			if (constraintCond.getChild("parameters") != null)
					constraintCondParameters = constraintCond.getChild("parameters")
							.getText();
		
			if (constraintCondParameters == null)
					cond = (new Predicate(scopeCond, parametersCond, 
							functionalExpressionCond, store)).getConstraint(store);
			else
					cond = (new Predicate(constraintCondParameters, parametersCond,
									  functionalExpressionCond, store)).getConstraint(store);
	
		}
		else {
			// PrimitiveConstraints, which do not have a predicate representation
			// e.g. IfThen itself.
			
			Element constraintCond = condElement.getChild("constraint");
			String reference = constraintCond.getAttributeValue("reference");
			
			if (reference.equals(Constants.id_ifthen))
				cond = (PrimitiveConstraint)IfThen.fromXML(constraintCond, store);
			
			if (reference.equals(Constants.id_ifthenelse))
				cond = (PrimitiveConstraint)IfThenElse.fromXML(constraintCond, store);
			
		}
		
		Element thenElement = constraint.getChild("then");
				
		Element predicateThen = thenElement.getChild("predicate");
		PrimitiveConstraint then = null;
		
		if (predicateThen != null) {
			Element constraintThen = thenElement.getChild("constraint");
		
			String parametersThen = predicateThen.getChild("parameters").getText();
			String functionalExpressionThen = predicateThen.getChild("expression")
					.getChild("functional").getText();
		
			String scopeThen = constraintThen.getAttributeValue("scope");	
		
			String constraintThenParameters = null;
		
			if (constraintThen.getChild("parameters") != null)
					constraintThenParameters = constraintThen.getChild("parameters")
							.getText();

		
			if (constraintThenParameters == null)
				then = (new Predicate(scopeThen, parametersThen, 
								functionalExpressionThen, store)).getConstraint(store);
			else
				then = (new Predicate(constraintThenParameters, parametersThen,
									  functionalExpressionThen, store)).getConstraint(store);
		
		}
		else {
	
			// PrimitiveConstraints, which do not have a predicate representation
			// e.g. IfThen itself.
			
			Element constraintThen = thenElement.getChild("constraint");
			String reference = constraintThen.getAttributeValue("reference");
			
			if (reference.equals(Constants.id_ifthen))
				then = (PrimitiveConstraint)IfThen.fromXML(constraintThen, store);
			
			if (reference.equals(Constants.id_ifthenelse))
				then = (PrimitiveConstraint)IfThenElse.fromXML(constraintThen, store);
			
			
		}
		
		Element elseElement = constraint.getChild("else");
		
		Element predicateElse = thenElement.getChild("predicate");
		PrimitiveConstraint _else = null;
		
		if (predicateElse != null) {
			Element constraintElse = elseElement.getChild("constraint");
		
			String parametersElse = predicateElse.getChild("parameters").getText();
			String functionalExpressionElse = predicateElse.getChild("expression")
					.getChild("functional").getText();
		
			String scopeElse = constraintElse.getAttributeValue("scope");	
		
			String constraintElseParameters = null;
		
			if (constraintElse.getChild("parameters") != null)
					constraintElseParameters = constraintElse.getChild("parameters")
							.getText();

		
			if (constraintElseParameters == null)
				_else = (new Predicate(scopeElse, parametersElse, 
								functionalExpressionElse, store)).getConstraint(store);
			else
				_else = (new Predicate(constraintElseParameters, parametersElse,
									  functionalExpressionElse, store)).getConstraint(store);
		
		}
		else {
	
			// PrimitiveConstraints, which do not have a predicate representation
			// e.g. IfThen itself.
			
			Element constraintElse = elseElement.getChild("constraint");
			String reference = constraintElse.getAttributeValue("reference");
			
			if (reference.equals(Constants.id_ifthen))
				_else = (PrimitiveConstraint)IfThen.fromXML(constraintElse, store);
			
			if (reference.equals(Constants.id_ifthenelse))
				_else = (PrimitiveConstraint)IfThenElse.fromXML(constraintElse, store);
			
			
		}
		return new IfThenElse(cond, then, _else);	
	
	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			Cond.increaseWeight();
			Then.increaseWeight();
			Else.increaseWeight();
			}
	}

}
