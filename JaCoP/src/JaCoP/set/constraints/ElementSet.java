/**
 *  ElementSet.java 
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

package JaCoP.set.constraints;

import java.util.*;
import JaCoP.constraints.*;
import JaCoP.core.BoundDomain;
import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;
import JaCoP.set.core.SetDomain;
import JaCoP.set.core.Set;

/**
 * It is an element constraint that make sures that 
 * the set variable value has a domain equal to 
 * the index-th element of the supplied list of sets.
 * 
 * By default, indexing starts from 1, if it is required
 * to be different (e.g. 0) than shift must be specified (-1). 
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm
 * @version 2.4
 */

public class ElementSet extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = elementSet;

	Variable index, value;

	Set[] list;

	int indexOffset = 0;	

	/**
	 * It constructs an elementSet constraint to restrict the domains of the variables index and value.
	 * 
	 * @param value variable that is restricted to have the same elements as list[index].
	 * @param list array of sets that contains possible values for variable value.
	 * @param index variable that is restricted to be the index of sets for which list[index] == value.
	 * @param shift the shift applied to the index variable.
	 */
	public ElementSet(Variable index, Set[] list,  Variable value, int shift) {
		numberId = IdNumber++;
		numberArgs = 1;
		this.index = index;
		this.value = value;
		this.indexOffset = shift;
		this.list = new Set[list.length]; 
		System.arraycopy(list, 0, this.list, 0, list.length);

		if(!(index.domain.domainID() == BoundDomain.BoundDomainID || index.domain.domainID() == IntervalDomain.IntervalDomainID)){
			throw new JaCoPException("This constraint should only be used with fdv as a finite domain variable.");
		}

		if(value.domain.domainID() != SetDomain.SetDomainID ){
			throw new JaCoPException("This constraint should only be used with sd as a setDomain variable.");
		}

	}

	/**
	 * It constructs an elementSet constraint to restrict the domains of the variables index and value.
	 * 
	 * @param value variable that is restricted to have the same elements as list[index].
	 * @param list array of sets that contains possible values for variable value.
	 * @param index variable that is restricted to be the index of sets for which list[index] == value.
	 */
	public ElementSet(Variable index, Set[] list,  Variable value) {
		this(index, list, value, 0);
	}


	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(2);

		variables.add(index);
		variables.add(value);

		return variables;
	}

	@Override
	public void removeLevel(int level) {
	}
	@Override
	public void consistency(Store store) {

		SetDomain old_sd = (SetDomain)value.domain; //.cloneLight();
		Domain old_index = index.domain; //.cloneLight();
		Domain new_index = new IntervalDomain(); //old_fdv.cloneLight();
		Set glb = old_sd.lub.cloneLight();
		Set lub = new Set();
		ValueEnumeration ve = old_index.valueEnumeration();
		int el = 0;
		while(ve.hasMoreElements()) {
			el = ve.nextElement() - 1 - indexOffset;
			if(el >= 0 ){
				if(el >= list.length)
					break;
				glb = glb.intersect(list[el]);
				lub.addDom(list[el]);
				if(old_sd.lub.contains(list[el]) && list[el].contains(old_sd.glb))
					new_index.addDom(el+1+indexOffset, el+1+indexOffset);
			}
		}

		store.newPropagation = false;
		((SetDomain)value.domain).in(store.level, value, glb, lub);
		index.domain.in(store.level, index, new_index);
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
			return id_elementSet + numberId;
	}

	@Override
	public void impose(Store store) {
		index.putModelConstraint(this,getConsistencyPruningEvent(index));
		value.putModelConstraint(this,getConsistencyPruningEvent(value));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void queueVariable(int level, Variable variable) {
	}

	@Override
	public void removeConstraint() {
		index.removeConstraint(this);
		value.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		return (value.singleton() && index.singleton() && list[index.domain.min()-1-indexOffset].eq(((SetDomain)value.domain).glb));
	}

	@Override
	public String toString() {

		StringBuilder result = new StringBuilder();

		result.append( id() ).append(" : ElementSet( ").append( index ).append(", [ ");

		for(Set s : list)
			result.append( s ).append(" ");

		result.append(" ], ").append( value ).append(" )"); 

		return result.toString();
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for ElementSet not implemented.");

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public void increaseWeight() {
		index.weight++;
		value.weight++;
	}	

}
