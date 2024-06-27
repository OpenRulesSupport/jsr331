/**
 *  ElementVariable.java 
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
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;

/**
 * ElementVariable constraint defines a relation 
 * variables[index - shift ] = value.
 * The first element of the list has by default an index equal to one.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class ElementVariable extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = elementVar;

	boolean firstConsistencyCheck = true;
	int firstConsistencyLevel;
	
	Store currentStore = null;

	Variable index, val;

	boolean indexChanged = false;

	int indexOffset = 0;

	Domain indexRange;

	Variable list[];

	ElementVariable(Variable Index, ArrayList<? extends Variable> V,
			Variable Val) {

		numberId = IdNumber++;
		index = Index;
		val = Val;
		numberArgs = (short) (numberArgs + 2);
		list = new Variable[V.size()];
		int j = 0;
		for (Variable e : V) {
			list[j] = e;
			numberArgs++;
			j++;
		}

		indexRange = new IntervalDomain(1 + indexOffset, list.length + indexOffset);

	}

	ElementVariable(Variable Index, ArrayList<? extends Variable> V,
			Variable Val, int shift) {
		
		indexOffset = shift;

		numberId = IdNumber++;
		index = Index;
		val = Val;
		numberArgs = (short) (numberArgs + 2);
		list = new Variable[V.size()];
		int j = 0;
		for (Variable e : V) {
			list[j] = e;
			numberArgs++;
			j++;
		}

		indexRange = new IntervalDomain(1 + indexOffset, list.length + indexOffset);

	}

	ElementVariable(Variable Index, Variable[] VariableArray, Variable Val) {

		numberId = IdNumber++;
		index = Index;
		val = Val;
		numberArgs = (short) (numberArgs + 2);
		list = new Variable[VariableArray.length];
		for (int i = 0; i < VariableArray.length; i++) {
			list[i] = VariableArray[i];
			numberArgs++;
		}

		indexRange = new IntervalDomain(1 + indexOffset, list.length + indexOffset);

	}

	ElementVariable(Variable Index, Variable[] VariableArray, Variable Val,
			int shift) {
		indexOffset = shift;
		numberId = IdNumber++;
		index = Index;
		val = Val;
		numberArgs = (short) (numberArgs + 2);
		list = new Variable[VariableArray.length];

		for (int i = 0; i < VariableArray.length; i++) {
			list[i] = VariableArray[i];
			numberArgs++;
		}

		indexRange = new IntervalDomain(1 + indexOffset, list.length + indexOffset);

	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(list.length + 2);

		variables.add(index);
		variables.add(val);

		for (Variable v : list)
			variables.add(v);

		return variables;
	}

	@Override
	public void removeLevel(int level) {
		if (level == firstConsistencyLevel)
			firstConsistencyCheck = true;
	}

	@Override
	public void consistency(Store store) {
		
		int size = list.length;
		
		Domain IndexDom = new IntervalDomain(5);
		Domain ValDom = new IntervalDomain(5);

		if (firstConsistencyCheck) {
			index.domain.in(store.level, index, indexRange);
			firstConsistencyCheck = false;
			firstConsistencyLevel = store.level;
			
		} else {

			if (indexChanged)
				for (ValueEnumeration e = index.recentDomainPruning().intersect(indexRange).valueEnumeration(); 
					 e.hasMoreElements();) {
					
					list[e.nextElement() - 1 - indexOffset].removeConstraint(this);

				}

			indexChanged = false;

		}
		
		while (store.newPropagation) {
			store.newPropagation = false;

			IndexDom.clear();
			ValDom.clear();
			Domain indexDom = index.dom();

			if (!val.singleton()) {

				for (ValueEnumeration e = indexDom.valueEnumeration(); e.hasMoreElements();)
					ValDom.addDom(list[e.nextElement() - 1 - indexOffset].dom());

				val.domain.in(store.level, val, ValDom);

			}

			Domain valDom = val.dom();
			for (int i = 0; i < size; i++) {

				if (!valDom.isIntersecting(list[i].domain)) {

					index.domain.inComplement(store.level, index, i + 1
							+ indexOffset);
					
					list[i].removeConstraint(this);
				}

			}

			if (index.singleton()) {
				
				Variable el = list[index.min() - 1 - indexOffset];
				el.domain.in(store.level, el, val.domain);
				val.domain.in(store.level, val, el.dom());
			}
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
			return Constants.ANY;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_elementVar + numberId;
	}

	@Override
	public void impose(Store store) {

		index.putModelConstraint(this, getConsistencyPruningEvent(index));
		val.putModelConstraint(this, getConsistencyPruningEvent(val));

		for (Variable v : list)
			v.putModelConstraint(this, getConsistencyPruningEvent(v));

		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void queueVariable(int level, Variable V) {

		if (V == index)
			indexChanged = true;
	}

	@Override
	public void removeConstraint() {
		index.removeConstraint(this);
		val.removeConstraint(this);
		for (Variable v : list)
			v.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		boolean sat = val.singleton();
		if (sat) {
			int v = val.min();
			ValueEnumeration e = index.domain.valueEnumeration();
			while (sat && e.hasMoreElements()) {
				Variable fdv = list[e.nextElement() - 1 - indexOffset];
				sat = fdv.singleton() && fdv.min() == v;
			}
		}
		return sat;
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : elementVariable").append("( ").append(index).append(", [");
		
		for (int i = 0; i < list.length; i++) {
			result.append( list[i] );
			
			if (i < list.length - 1)
				result.append(", ");
		}
		
		result.append("], ").append(val).append(" )");

		return result.toString();
		
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_elementVar);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(index);
		scopeVars.add(val);
		
		for (int i = 0; i < list.length; i++) {
			scopeVars.add(list[i]);
		}

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(var.id()).append(" ");
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	
		
		org.jdom.Element indexEl = new org.jdom.Element("index");
		indexEl.setText(index.id());
		constraint.addContent(indexEl);

		org.jdom.Element valueEl = new org.jdom.Element("value");
		valueEl.setText(val.id());
		constraint.addContent(valueEl);

		org.jdom.Element xList = new org.jdom.Element("list");

		StringBuffer xBuffer = new StringBuffer();
		for (int i = 0; i < list.length; i++)
			xBuffer.append( list[i].id() ).append(" ");

		xList.setText(xBuffer.toString().trim());

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
	 * @param store the constraint in which context the constraint is being created.
	 * @return created constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		
		String index = constraint.getChild("index").getText();
		String value = constraint.getChild("value").getText();
		String list = constraint.getChild("list").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(list);
		
		ArrayList<Variable> x = new ArrayList<Variable>();
		for (String n : varsNames)
			x.add(store.findVariable(n));

		return new ElementVariable(store.findVariable(index), x, store.findVariable(value));

	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			index.weight++;
			val.weight++;
			for (Variable v : list) v.weight++;
		}
	}

}
