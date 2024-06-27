/**
 *  Count.java 
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
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Count constraint implements the counting over number of occurrences of value
 * Val on the list of FDV's. The number of occurrences is specified by finite
 * domain variable Counter
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.1
 */

public class Count extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = countConstr;

	Variable count;

	Variable list[];

	int val;

	/**
	 * It constructs a Count constraint. 
	 * @param val value which is counted
	 * @param variables variables which equality to val is counted.
	 * @param counter number of variables equal to val.
	 */
	public Count(int val, ArrayList<? extends Variable> variables, Variable counter) {

		this.queueIndex = 1;
		numberId = IdNumber++;
		this.val = val;
		count = counter;
		list = new Variable[variables.size()];
		variables.toArray(list);
		numberArgs = (short) (list.length + 1);
	}

	/**
	 * It constructs a Count constraint. 
	 * @param val value which is counted
	 * @param variables variables which equality to val is counted.
	 * @param counter number of variables equal to val.
	 */
	public Count(int val, Variable[] variables, Variable counter) {
		
		this.queueIndex = 1;
		numberId = IdNumber++;
		this.val = val;
		count = counter;
		list = new Variable[variables.length];
		for (int i = 0; i < variables.length; i++)
			list[i] = variables[i];
		numberArgs = (short) (list.length + 1);
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return  id_count + numberId;
	}

	@Override
	public short type() {
		return type;
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
			return Constants.ANY;
	}



	// registers the constraint in the constraint store
	@Override
	public void impose(Store store) {

		count.putConstraint(this);
		for (Variable V : list)
			V.putConstraint(this);

		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void consistency(Store store) {
		while (store.newPropagation) {
			store.newPropagation = false;

			int numberEq = 0, numberMayBe = 0;
			for (Variable v : list) {
				if (v.domain.contains(val))
					if (v.singleton())
						numberEq++;
					else
						numberMayBe++;
			}

			count.domain.in(store.level, count, numberEq, numberEq
					+ numberMayBe);

			if (numberMayBe == count.min() - numberEq)
				for (Variable v : list) {
					if (!v.singleton() && v.domain.contains(val))
						v.domain.in(store.level, v, val, val);
				}
			else if (numberEq == count.max())
				for (Variable v : list)
					if (!v.singleton() && v.domain.contains(val))
						v.domain.inComplement(store.level, v, val);
		
		}
	}

	@Override
	public boolean satisfied() {

		int countAll = 0;

		if (count.singleton(val)) {
			for (Variable v : list)
				if (v.singleton(val))
					countAll++;
			return (countAll == count.min());
		} else
			return false;
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> args = new ArrayList<Variable>(list.length + 1);

		args.add(count);

		for (Variable v : list)
			args.add(v);

		return args;
	}

	@Override
	public void removeConstraint() {
		count.removeConstraint(this);
		for (Variable v : list)
			v.removeConstraint(this);
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : count(").append( val ).append(",[");
		
		for (int i = 0; i < list.length; i++) {
			result.append( list[i] );
			if (i < list.length - 1)
				result.append(", ");
		}
		
		result.append("], ").append(count).append(" )");
		
		return result.toString();
		
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_count);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(count);
		for (int i = 0; i < list.length; i++)
			scopeVars.add(list[i]);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	

		org.jdom.Element term = new org.jdom.Element("count");
		term.setText(count.id());
		constraint.addContent(term);

		org.jdom.Element tList = new org.jdom.Element("list");

		StringBuffer tString = new StringBuffer();
		for (int i = 0; i < list.length - 1; i++)
			tString.append(  list[i].id() ).append( " " );
		
		tString.append(list[list.length - 1]);
		
		tList.setText(tString.toString());

		org.jdom.Element countValue = new org.jdom.Element("val");
		countValue.setText(String.valueOf(val));
		constraint.addContent(countValue);

		constraint.addContent(tList);

		return constraint;

	}
	
	/**
	 * It creates a constraint from XCSP description. 
	 * @param constraint an XML element describing the constraint.
	 * @param store the constraint store in which context the constraint is being created.
	 * @return the created constraint. 
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		
		String list = constraint.getChild("list").getText();
		String var = constraint.getChild("count").getText();

		int val = Integer.valueOf(constraint.getChild("val").getText());

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(list);

		ArrayList<Variable> x = new ArrayList<Variable>();

		for (String n : varsNames)
			x.add(store.findVariable(n));

		return new Count(val, x, store.findVariable(var));
	}
	
	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			count.weight++;
			for (Variable v : list) v.weight++;
		}
	}

}
