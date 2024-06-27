/**
 *  ElementInteger.java 
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
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;

/**
 * ElementInteger constraint defines a following relation
 * values[index - shift] = value. 
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class ElementInteger extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = elementInteger;

	boolean firstConsistencyCheck = true;
	int firstConsistencyLevel;

	Variable index, val;

	int indexOffset = 0;

	int list[];

	Hashtable<Integer, Domain> mappingValuesToIndex = new Hashtable<Integer, Domain>();

	boolean indexHasChanged = true;
	boolean valueHasChanged = true;
	
	ElementInteger(Variable index, ArrayList<Integer> list, Variable value) {

		this(index, list, value, 0);
		
	}

	ElementInteger(Variable index, ArrayList<Integer> list, Variable value, int shift) {

		indexOffset = shift;

		numberId = IdNumber++;
		this.index = index;
		val = value;
		numberArgs = (short) (numberArgs + 2);
		this.list = new int[list.size()];
		int i = 0;
		for (Integer listElement : list) {
			this.list[i] = listElement;
			Domain oldFD = mappingValuesToIndex.get(listElement);
			if (oldFD == null)
				mappingValuesToIndex.put(listElement, new IntervalDomain(i + 1 + indexOffset, i + 1	+ indexOffset));
			else
				oldFD.addDom(i + 1 + indexOffset, i + 1 + indexOffset);
			numberArgs++;
			i++;
		}
		
	}

	ElementInteger(Variable index, int[] list, Variable value) {

		this(index, list, value, 0);
		
	}

	ElementInteger(Variable index, int[] list, Variable value, int shift) {
		
		indexOffset = shift;
		
		numberId = IdNumber++;
		this.index = index;
		val = value;
		numberArgs = (short) (numberArgs + 2);
		this.list = new int[list.length];
		
		for (int i = 0; i < list.length; i++) {
			
			Integer listElement = list[i];
			this.list[i] = list[i];
			
			Domain oldFD = mappingValuesToIndex.get(listElement);
			if (oldFD == null)
				mappingValuesToIndex.put(listElement, new IntervalDomain(i + 1 + indexOffset, i + 1	+ indexOffset));
			else
				oldFD.addDom(i + 1 + indexOffset, i + 1 + indexOffset);
			
		}
		
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(2);

		variables.add(index);
		variables.add(val);
		
		return variables;
		
	}

	@Override
	public void removeLevel(int level) {
		if (level == firstConsistencyLevel)
			firstConsistencyCheck = true;
	}

	@Override
	public void consistency(Store store) {

		Domain domIndex = new IntervalDomain(5);
		Domain domValue = new IntervalDomain(5);

		if (firstConsistencyCheck) {

			index.domain.in(store.level, index, 1 + indexOffset, list.length + indexOffset);
			firstConsistencyCheck = false;
			firstConsistencyLevel = store.level;

		}

		if (indexHasChanged) {

			indexHasChanged = false;
			Domain indexDom = index.dom();

			for (ValueEnumeration e = indexDom.valueEnumeration(); e.hasMoreElements();) {
				int valueOfElement = list[e.nextElement() - 1 - indexOffset];
				domValue.addDom(valueOfElement, valueOfElement);
			}

			val.domain.in(store.level, val, domValue);
		}

		if (valueHasChanged) {

			valueHasChanged = false;
			Domain valDom = val.dom();

			for (ValueEnumeration e = valDom.valueEnumeration(); e.hasMoreElements();) {
				Domain i = mappingValuesToIndex.get(new Integer(e.nextElement()));
				if (i != null)
					domIndex.addDom(i);
			}

			index.domain.in(store.level, index, domIndex);

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
			return id_elementINT+ numberId;
	}

	@Override
	public void impose(Store store) {
		index.putModelConstraint(this, getConsistencyPruningEvent(index));
		val.putModelConstraint(this, getConsistencyPruningEvent(val));
		store.addChanged(this);
		store.countConstraint();
	}

	int indexOf(int element) {
		boolean found = false;
		int i = 0;
		while (!found && i < list.length) {
			found = list[i] == element;
			i++;
		}
		return i - 1;
	}

	@Override
	public void queueVariable(int level, Variable V) {
		if (V == index)
			indexHasChanged = true;
		else
			valueHasChanged = true;
	}

	@Override
	public void removeConstraint() {
		index.removeConstraint(this);
		val.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		boolean sat = val.singleton();
		if (sat) {
			int v = val.min();
			ValueEnumeration e = index.domain.valueEnumeration();
			while (sat && e.hasMoreElements()) {
				sat = list[e.nextElement() - 1 - indexOffset] == v;
			}
		}
		return sat;
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : elementInteger").append("( ").append(index).append(", [");
		
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
		constraint.setAttribute("reference", id_elementINT);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(index);
		scopeVars.add(val);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

		StringBuffer scope = new StringBuffer();
		
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append(" ");
		
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
			xBuffer.append( String.valueOf(list[i]) ).append( " " );

		xList.setText(xBuffer.toString().trim());

		constraint.addContent(xList);

		return constraint;
	}

	@Override
	public short type() {
		return type;
	}

	/**
	 * It creates a constraint from XCSP description.
	 * @param constraint XML element describing the constraint.
	 * @param store constraint store in which context the constraint is created.
	 * @return the created constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		
		String index = constraint.getChild("index").getText();
		String value = constraint.getChild("value").getText();
		String list = constraint.getChild("list").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] valuesNames = pattern.split(list);

		int[] w = new int[valuesNames.length];

		int no = 0;
		for (String n : valuesNames)
			w[no++] = Integer.valueOf(n);

		return new ElementInteger(store.findVariable(index), w, store.findVariable(value));
		
	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			index.weight++;
			val.weight++;
		}
	}

}
