/**
 *  EqBool.java 
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
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * If all x's are equal to each other then result variable is equal 1. Otherwise, result variable 
 * is equal to zero. It restricts the domains of all variables to be either 0 or 1.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class EqBool extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;
	final static short type = eqbool;

	Variable [] x;
	Variable result;

	/**
	 * It constructs eqBool. 
	 * 
	 * @param x list of x's which must all be equal to the same value to make result equal 1.
	 * @param result variable which is equal 0 if x's contain different values. 
	 */
	public EqBool(Variable [] x, Variable result) {

		numberId = IdNumber++;
		numberArgs = (short)(x.length + 1);
		this.x = x;
		this.result = result;
		assert ( checkInvariants() == null) : checkInvariants();

	}

	/**
	 * It constructs eqBool. 
	 * 
	 * @param x list of x's which must all be equal to the same value to make result equal 1.
	 * @param result variable which is equal 0 if x's contain different values. 
	 */
	public EqBool(ArrayList<Variable> x, Variable result) {
	
		numberId = IdNumber++;
		numberArgs = (short)(x.size()+1);

		this.x = new Variable[x.size()];

		for (int i = 0; i < this.x.length; i++)
			this.x[i] = x.get(i);
		
		this.result = result;
		
		assert ( checkInvariants() == null) : checkInvariants();

	}

	/**
	 * It checks invariants required by the constraint. Namely that
	 * boolean variables have boolean domain. 
	 * 
	 * @return the string describing the violation of the invariant, null otherwise.
	 */
	public String checkInvariants() {

		for (Variable var : x)
			if (var.min() < 0 || var.max() > 1)
				return "Variable " + var + " does not have boolean domain";
		
		return null;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(x.length + 1);

		variables.add(result);
		for (int i = 0; i < x.length; i++)
			variables.add(x[i]);
		return variables;
	}

	@Override
	public void removeLevel(int level) {
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
			return Constants.GROUND;
		}
	}


	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_eqbool + numberId;
	}

	// registers the constraint in the constraint store
	@Override
	public void impose(Store store) {

		result.putModelConstraint(this, getConsistencyPruningEvent(result));

		for (Variable V : x)
			V.putModelConstraint(this, getConsistencyPruningEvent(V));

		store.addChanged(this);
		store.countConstraint();

	}

	public void consistency(Store store) {

	//	while (store.newPropagation) {

			int x1 = 0, x0 = 0, index_01 = 0;

			store.newPropagation = false;

			for (int i = 0; i < x.length; i++) {
				if (x[i].min() == 1) x1++;
				else
					if (x[i].max() == 0) x0++;
					else
						index_01 = i;
			}

			if (result.min() == 1) {
			
				if (x0 > 0)
					for (int i=0; i<x.length; i++)
						x[i].domain.in(store.level, x[i], 0, 0);
				if (x1 > 0)
					for (int i=0; i<x.length; i++)
						x[i].domain.in(store.level, x[i], 1, 1);
				
			}
			else {
				if (result.max() == 0) {
					if (x0 == 0 && x1 == x.length - 1)
						x[index_01].domain.in(store.level, x[index_01], 0, 0);
					if (x1 == 0 && x0 == x.length - 1)
						x[index_01].domain.in(store.level, x[index_01], 1, 1);
				}
			}
			
			if (x0 > 0 && x1 > 0)
				result.domain.in(store.level, result, 0, 0);
			
			if (x0 == x.length || x1 == x.length) 
				result.domain.in(store.level, result, 1, 1);
			
	//	}
		
	}

	@Override
	public void notConsistency(Store store) {

		while (store.newPropagation) {

			int x1 = 0, x0 = 0, index_01 = 0;

			store.newPropagation = false;

			for (int i = 0; i < x.length; i++) {
				if (x[i].min() == 1) x1++;
				else
					if (x[i].max() == 0) x0++;
					else
						index_01 = i;
			}

			if (result.min() == 1) {

				if (x0 == 0 && x1 == x.length - 1)
					x[index_01].domain.in(store.level, x[index_01], 0, 0);
				if (x1 == 0 && x0 == x.length - 1)
					x[index_01].domain.in(store.level, x[index_01], 1, 1);

			}
			else {
				if (result.max() == 0) {
					if (x0 > 0)
						for (int i=0; i<x.length; i++)
							x[i].domain.in(store.level, x[i], 0, 0);
					if (x1 > 0)
						for (int i=0; i<x.length; i++)
							x[i].domain.in(store.level, x[i], 1, 1);
				}
			}
			
			if (x0 > 0 && x1 > 0)
				result.domain.in(store.level, result, 1, 1);
			
			if (x0 == x.length || x1 == x.length) 
				result.domain.in(store.level, result, 0, 0);

		}
	}

	@Override
	public boolean satisfied() {

		if (result.max() == 0) {

			int x1 = 0, x0 = 0;

			for (int i = 0; i < x.length; i++) {
				
				if (x[i].min() == 1) x1++;
				else if (x[i].max() == 0) x0++;
				
				if (x0 > 0 && x1 > 0)
					return true;
			}
			
			return false;

		}
		else {
			
			if (result.min() == 1) {

				for (int i = 0; i < x.length - 1; i++)
					if (!x[i].singleton() || !x[i+1].singleton() || x[i].value() != x[i+1].value())
						return false;					

				return true;
			}
			
		}

		return false;
		
	}

	@Override
	public boolean notSatisfied() {

		if (result.max() == 0) {

			int x1 = 0, x0 = 0;

			for (int i = 0; i < x.length; i++) {
				
				if (x[i].min() == 1) x1++;
				else if (x[i].max() == 0) x0++;
				
				if (x0 > 0 && x1 > 0)
					return false;
			}
			
			if (x0 == x.length || x1 == x.length)
				return true;

		}
		else {
			
			if (result.min() == 1) {

				int x1 = 0, x0 = 0;

				for (int i = 0; i < x.length; i++) {
					
					if (x[i].min() == 1) x1++;
					else if (x[i].max() == 0) x0++;
					
					if (x0 > 0 && x1 > 0)
						return true;
				}

				return false;
			}
			
		}

		return false;


	}


	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		result.removeConstraint(this);
		for (int i = 0; i < x.length; i++) {
			x[i].removeConstraint(this);
		}
	}

	@Override
	public String toString() {

		StringBuffer resultString = new StringBuffer( id() );

		resultString.append(" : eqBool( ");
		for (int i = 0; i < x.length; i++) {
			resultString.append( x[i] );
			if (i < x.length - 1)
				resultString.append(", ");
		}
		resultString.append(", ");
		resultString.append(result);
		resultString.append(")");
		return resultString.toString();
	}

	ArrayList<Constraint> constraints;

	@Override
	public ArrayList<Constraint> decompose(Store store) {

		constraints = new ArrayList<Constraint>();

		PrimitiveConstraint [] eqConstraints = new PrimitiveConstraint[x.length];

		IntervalDomain booleanDom = new IntervalDomain(0, 1);

		for (int i = 0; i < eqConstraints.length - 1; i++) {
			eqConstraints[0] = new XeqY(x[i], x[i+1]);
			constraints.add(new In(x[i], booleanDom));
		}

		constraints.add( new In(result, booleanDom));

		constraints.add( new Eq(new And(eqConstraints), new XeqC(result, 1)) );

		return constraints;
	}

	@Override
	public void imposeDecomposition(Store store) {

		if (constraints == null)
			decompose(store);

		for (Constraint c : constraints)
			store.impose(c, queueIndex);

	}


	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_eqbool);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(result);
		for (int i = 0; i < x.length; i++)
			scopeVars.add(x[i]);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));


		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );

		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	

		org.jdom.Element eqBoolEl = new org.jdom.Element("eqbool");
		eqBoolEl.setText(result.id());
		constraint.addContent(eqBoolEl);

		org.jdom.Element xList = new org.jdom.Element("x");

		StringBuffer xString = new StringBuffer();

		for (int i = 0; i < x.length; i++)
			xString.append( x[i].id() ).append( " " );

		xList.setText(xString.toString().trim());

		constraint.addContent(xList);

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

		String list = constraint.getChild("x").getText();
		String result = constraint.getChild("eqbool").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(list);

		ArrayList<Variable> z = new ArrayList<Variable>();

		for (String n : varsNames)
			z.add(store.findVariable(n));

		return new EqBool(z, store.findVariable(result));

	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			result.weight++;
			for (Variable v : x) v.weight++;
		}
	}

}
