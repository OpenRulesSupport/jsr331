/**
 *  Min.java 
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
import JaCoP.core.Domain;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Min constraint implements the minimum/2 constraint. It provides the minimum
 * varable from all FD varaibles on the list.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Min extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = minConstr;

	Variable list[];

	Variable min;

	/**
	 * It constructs min constraint.
	 * @param min variable denoting the minimal value
	 * @param variables the array of variables for which the minimal value is imposed.
	 */
	public Min(Variable min, ArrayList<? extends Variable> variables) {

		queueIndex = 1;
		numberId = IdNumber++;
		this.min = min;
		list = new Variable[variables.size()];
		int i = 0;
		for (Variable e : variables) {
			list[i] = e;
			i++;
			numberArgs++;
		}
	}

	/**
	 * It constructs min constraint.
	 * @param min variable denoting the minimal value
	 * @param variables the array of variables for which the minimal value is imposed.
	 */
	public Min(Variable min, Variable[] variables) {

		queueIndex = 1;
		numberId = IdNumber++;
		this.min = min;
		list = new Variable[variables.length];
		for (int i = 0; i < variables.length; i++) {
			list[i] = variables[i];
			numberArgs++;
		}
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(list.length + 1);

		Variables.add(min);
		for (int i = 0; i < list.length; i++)
			Variables.add(list[i]);
		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		int MIN = MaxInt, MAX = MaxInt;
		Variable V;
		Domain Vdom;

		//@todo keep one variable with the smallest value as watched variable
		// only check for other support if that smallest value is no longer part
		// of the variable domain. 
		
		while (store.newPropagation) {
			store.newPropagation = false;

			// @todo, optimize, if there is no change on min.min() then
			// the below inMin does not have to be executed.
			
			int minMin = min.min();
			for (int i = 0; i < list.length; i++) {
				V = list[i];

				V.domain.inMin(store.level, V, minMin);

				Vdom = V.dom();
				int VdomMin = Vdom.min(), VdomMax = Vdom.max();
				MIN = (MIN < VdomMin) ? MIN : VdomMin;
				MAX = (MAX < VdomMax) ? MAX : VdomMax;
			}

			min.domain.in(store.level, min, MIN, MAX);

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
	public String id() {
		if (id != null)
			return id;
		else
			return id_min + numberId;
	}

	// registers the constraint in the constraint store
	@Override
	public void impose(Store store) {

		min.putModelConstraint(this, getConsistencyPruningEvent(min));

		for (Variable V : list)
			V.putModelConstraint(this, getConsistencyPruningEvent(V));

		store.addChanged(this);
		store.countConstraint();

	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		min.removeConstraint(this);
		for (int i = 0; i < list.length; i++) {
			list[i].removeConstraint(this);
		}
	}

	@Override
	public boolean satisfied() {
		boolean sat = min.singleton();
		int MIN = min.max();
		int i = 0, eq = 0;
		while (sat && i < list.length) {
			if (list[i].singleton() && list[i].value() == MIN)
				eq++;
			sat = list[i].min() >= MIN;
			i++;
		}
		return sat && eq > 0;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : min( ");
		for (int i = 0; i < list.length; i++) {
			result.append( list[i] );
			if (i < list.length - 1)
				result.append(", ");
		}
		result.append(")");
		return result.toString();
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_min);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(min);
		for (int i = 0; i < list.length; i++)
			scopeVars.add(list[i]);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));


		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() + " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	
				
		org.jdom.Element minEl = new org.jdom.Element("min");
		minEl.setText(min.id());
		constraint.addContent(minEl);

		org.jdom.Element xList = new org.jdom.Element("list");

		StringBuffer xString = new StringBuffer();
		for (int i = 0; i < list.length; i++)
			xString.append( list[i].id() ).append( " " );

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
		
		String list = constraint.getChild("list").getText();
		String min = constraint.getChild("min").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(list);

		ArrayList<Variable> x = new ArrayList<Variable>();

		for (String n : varsNames)
			x.add(store.findVariable(n));

		return new Min(store.findVariable(min), x);
		
	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			min.weight++;
			for (Variable v : list) v.weight++;
		}
	}
	
}
