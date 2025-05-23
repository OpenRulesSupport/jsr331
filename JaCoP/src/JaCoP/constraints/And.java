/**
 *  And.java 
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
import java.util.Hashtable;

import JaCoP.core.Constants;
import JaCoP.util.SimpleHashSet;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Constraint c1 /\ c2 ... /\ cn
 * 
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class And extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = and;

	PrimitiveConstraint C[];

	Hashtable<Variable, Integer> pruningEvents;

	/**
	 * It constructs an And constraint based on primitive constraints. The 
	 * constraint is satisfied if all constraints are satisfied.
	 * @param c arraylist of constraints
	 */
	public And(ArrayList<PrimitiveConstraint> c) {
		
		this.queueIndex = 1;
		numberId = IdNumber++;
		
		C = new PrimitiveConstraint[c.size()];
		
		int i = 0;
		
		for (PrimitiveConstraint cc : c) {
			numberArgs += cc.numberArgs();
			C[i++] = cc;
		}
	}

	/**
	 * It constructs a simple And constraint based on two primitive constraints.
	 * @param c1 the first primitive constraint
	 * @param c2 the second primitive constraint
	 */
	public And(PrimitiveConstraint c1, PrimitiveConstraint c2) {
		
		numberId = IdNumber++;
		
		C = new PrimitiveConstraint[2];
		
		numberArgs += c1.numberArgs();
		
		C[0] = c1;
		
		numberArgs += c2.numberArgs();
		
		C[1] = c2;
	}

	/**
	 * It constructs an And constraint over an array of primitive constraints.
	 * @param c an array of primitive constraints constituting the And constraint.
	 */
	public And(PrimitiveConstraint[] c) {
		
		this.queueIndex = 1;
		numberId = IdNumber++;
		C = new PrimitiveConstraint[c.length];
		for (int i = 0; i < c.length; i++) {
			numberArgs += c[i].numberArgs();
			C[i] = c[i];
		}
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>();

		for (Constraint cc : C)
			for (Variable V : cc.arguments())
				Variables.add(V);

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {

		boolean newProp = true;
		int propNumber = 0;

		while (newProp) {
			newProp = false;

			for (Constraint cc : C) {
				store.newPropagation = true;

				cc.consistency(store);

				newProp = newProp || store.newPropagation;
				if (store.newPropagation)
					propNumber++;
			}
		}
		store.newPropagation = (propNumber > 0) ? true : false;
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

		for (int i = 0; i < C.length; i++)
			parametersVariables.addAll(C[i].arguments());

		StringBuffer parString = new StringBuffer();

		for (Variable v : parametersVariables)
			parString.append("int ").append(v.id()).append(" ");

		org.jdom.Element parameters = new org.jdom.Element("parameters");
		parameters.setText(parString.toString().trim());

		predicate.addContent(parameters);

		org.jdom.Element expression = new org.jdom.Element("expression");

		org.jdom.Element functional = new org.jdom.Element("functional");

		StringBuffer functionalString = new StringBuffer();
		functionalString.append("and(");

		for (int i = 0; i < C.length; i++) {
			org.jdom.Element insideConstraint = C[i]
					.getPredicateDescriptionXML();

			functionalString.append(insideConstraint.getChild("expression")
					.getChildText("functional"));

			if (i < C.length - 1)
				functionalString.append(",");
		}

		functionalString.append(")");

		functional.setText(functionalString.toString());

		expression.addContent(functional);

		predicate.addContent(expression);

		return predicate;

	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {

		// If consistency function mode
			if (pruningEvents != null) {
				Integer possibleEvent = pruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}

			int eventAcross = -1;

			for (int i = 0; i < C.length; i++) {
				if (C[i].arguments().contains(var)) {
					int event = C[i].getNestedPruningEvent(var, true);
					if (event > eventAcross)
						eventAcross = event;
				}
			}

			if (eventAcross == -1)
				return Constants.NONE;
			else
				return eventAcross;

		}
	
	@Override
	public int getNotConsistencyPruningEvent(Variable var) {

			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}

			int eventAcross = -1;

			for (int i = 0; i < C.length; i++) {
				if (C[i].arguments().contains(var)) {
					int event = C[i].getNestedPruningEvent(var, false);
					if (event > eventAcross)
						eventAcross = event;
				}
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
			return  id_and + numberId;
	}

	@Override
	public void impose(Store store) {

		SimpleHashSet<Variable> variables = new SimpleHashSet<Variable>();

		for (int i = 0; i < C.length; i++)
			for (Variable V : C[i].arguments())
				variables.add(V);

		while (!variables.isEmpty()) {
			Variable V = variables.removeFirst();
			V.putModelConstraint(this, getConsistencyPruningEvent(V));
		}

		store.addChanged(this);
		store.countConstraint(C.length);
	}

	@Override
	public void notConsistency(Store store) {
		int numberSat = 0, numberNotSat = 0;
		int j = 0;

		store.newPropagation = false;

		int i = 0;
		while (numberSat == 0 && i < C.length) {
			if (C[i].notSatisfied())
				numberSat++;
			else {
				if (C[i].satisfied())
					numberNotSat++;
				else
					j = i;
			}
			i++;
		}

		if (numberSat == 0) {
			if (numberNotSat == C.length - 1) {
				store.newPropagation = true;
				C[j].notConsistency(store);
			} else if (numberNotSat == C.length)
				store.throwFailException(this);
		}
	}

	@Override
	public boolean notSatisfied() {
		boolean notSat = false;

		int i = 0;
		while (!notSat && i < C.length) {
			notSat = notSat || C[i].notSatisfied();
			i++;
		}
		return notSat;
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {

		for (Constraint cc : C)
			for (Variable V : cc.arguments())
				V.removeConstraint(this);

	}

	@Override
	public boolean satisfied() {
		boolean sat = true;

		int i = 0;
		while (sat && i < C.length) {
			sat = sat && C[i].satisfied();
			i++;
		}
		return sat;
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : And(");
		
		for (int i = 0; i < C.length; i++) {
			result.append( C[i] );
			if (i == C.length - 1)
				result.append(",");
		}
		return result.toString();
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("id", id() );

		HashSet<Variable> parametersVariables = new HashSet<Variable>();

		for (int i = 0; i < C.length; i++)
			parametersVariables.addAll(C[i].arguments());

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
			for (Constraint c : C) c.increaseWeight();
		}
	}
}
