/**
 *  Max.java 
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
 * Max constraint implements the Maximum/2 constraint. It provides the maximum
 * varable from all FD varaibles on the list.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Max extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = maxConstr;

	Variable list[];

	Variable max;

	/**
	 * It constructs max constraint.
	 * @param max variable denoting the maximum value
	 * @param variables the array of variables for which the maximum value is imposed.
	 */
	public Max(Variable max, ArrayList<? extends Variable> variables) {

		queueIndex = 1;
		numberId = IdNumber++;
		this.max = max;
		list = new Variable[variables.size()];
		int i = 0;
		for (Variable e : variables) {
			list[i] = e;
			i++;
			numberArgs++;
		}
	}

	/**
	 * It constructs max constraint.
	 * @param max variable denoting the maximum value
	 * @param variables the array of variables for which the maximum value is imposed.
	 */
	public Max(Variable max, Variable[] variables) {

		queueIndex = 1;
		numberId = IdNumber++;
		this.max = max;
		list = new Variable[variables.length];
		for (int i = 0; i < variables.length; i++) {
			list[i] = variables[i];
			numberArgs++;
		}
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(list.length + 1);

		Variables.add(max);
		for (int i = 0; i < list.length; i++)
			Variables.add(list[i]);
		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		int MIN = MinInt, MAX = MinInt;
		Variable V;
		Domain Vdom;

		while (store.newPropagation) {
			store.newPropagation = false;

			// @todo, optimize, if there is no change on min.min() then
			// the below inMin does not have to be executed.

			int maxMax = max.max();
			for (int i = 0; i < list.length; i++) {
				V = list[i];

				V.domain.inMax(store.level, V, maxMax);

				Vdom = V.dom();
				int VdomMin = Vdom.min(), VdomMax = Vdom.max();
				MIN = (MIN > VdomMin) ? MIN : VdomMin;
				MAX = (MAX > VdomMax) ? MAX : VdomMax;
			}

			max.domain.in(store.level, max, MIN, MAX);

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
			return id_max + numberId;
	}

	// registers the constraint in the constraint store
	@Override
	public void impose(Store store) {

		max.putModelConstraint(this, getConsistencyPruningEvent(max));

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
		max.removeConstraint(this);
		for (int i = 0; i < list.length; i++) {
			list[i].removeConstraint(this);
		}
	}

	@Override
	public boolean satisfied() {

		boolean sat = max.singleton();
		int MAX = max.min();
		int i = 0, eq = 0;
		while (sat && i < list.length) {
			if (list[i].singleton() && list[i].value() == MAX)
				eq++;
			sat = list[i].max() <= MAX;
			i++;
		}
		return sat && eq > 0;
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : max( ");
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
		constraint.setAttribute("reference", id_max);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(max);
		for (int i = 0; i < list.length; i++)
			scopeVars.add(list[i]);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));


		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	
				
		org.jdom.Element maxEl = new org.jdom.Element("max");
		maxEl.setText(max.id());
		constraint.addContent(maxEl);

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
		String max = constraint.getChild("max").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(list);

		ArrayList<Variable> x = new ArrayList<Variable>();

		for (String n : varsNames)
			x.add(store.findVariable(n));

		return new Max(store.findVariable(max), x);
		
	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			max.weight++;
			for (Variable v : list) v.weight++;
		}
	}
}
