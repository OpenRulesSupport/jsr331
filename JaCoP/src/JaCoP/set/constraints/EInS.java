/**
 *  eIn.java 
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
import JaCoP.core.Constants;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.set.core.SetDomain;

/**
 * 
 * It constructs a constraint which makes sure that a given element is 
 * in the domain of the set variable.
 * 
 * @author Krzysztof Kuchcinski, Radoslaw Szymanek and Robert Åkemalm
 * @version 2.4
 */

public class EInS extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = inSet;

	Variable x;

	int element;

	/**
	 * It constructs an eInS constraint to restrict the domain of the variable.
	 * @param x variable x for which the restriction is applied.
	 * @param e the element that has to be a part of the variable domain.
	 */
	public EInS(int e, Variable x) {
		numberId = IdNumber++;
		numberArgs = 1;
		this.x = x;
		element = e;
		if(x.domain.domainID() != SetDomain.SetDomainID){
			throw new JaCoPException("This constraint should only be used with setDomain.");
		}

	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(1);

		variables.add(x);
		return variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {

		SetDomain sd = (SetDomain) x.dom().cloneLight();
		sd.glb.addDom(element);
		store.newPropagation = false;
		x.domain.in(store.level, x, sd); 
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
		return Constants.NONE;		
	}

	@Override
	public int getNotConsistencyPruningEvent(Variable var) {

		// If notConsistency function mode
		if (notConsistencyPruningEvents != null) {
			Integer possibleEvent = notConsistencyPruningEvents.get(var);
			if (possibleEvent != null)
				return possibleEvent;
		}
		return Constants.NONE;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_inSet + numberId;
	}

	@Override
	public void impose(Store store) {
		x.putModelConstraint(this, getConsistencyPruningEvent(x));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void notConsistency(Store store) {
		SetDomain sd = (SetDomain) x.dom().cloneLight();
		sd.lub = sd.lub.subtract(element);
		store.newPropagation = false;
		x.domain.in(store.level, x, sd);
	}

	@Override
	public boolean notSatisfied() {
		SetDomain sd = (SetDomain) x.dom();
		if(sd.lub.contains(element)){
			return false;
		}
		return true;
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		x.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		SetDomain sd = (SetDomain) x.dom();
		if(sd.glb.contains(element)){
			return true;
		}
		return false;
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
			return Constants.ANY;
		}
	}


	@Override
	public String toString() {
		return id() + " : eInS(" + element + ", " + x + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for inSet not implemented.");

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public void increaseWeight() {
		x.weight++;
	}	

}
