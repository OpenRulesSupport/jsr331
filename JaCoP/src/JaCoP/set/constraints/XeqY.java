/**
 *  XeqY.java 
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
import JaCoP.set.core.Set;

/**
 * It creates an equality constraint to make sure that two set variables
 * have the same value. 
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm
 * @version 2.4
 */

public class XeqY extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = XeqYSet;

	Variable xVar, yVar;

	/**
	 * It constructs an XeqY constraint to restrict the domain of the variables.
	 * @param x variable x restricted to be equal to y.
	 * @param y variable y restricted to be equal to x.
	 */
	public XeqY(Variable x, Variable y) {
		numberId = IdNumber++;
		numberArgs = 1;
		if(x.domain.domainID() == Set.SetID)
			this.xVar = new Variable(x.store,"XeqY_x_"+numberId, new SetDomain((Set)x.dom(),(Set)x.dom()));
		else
			this.xVar = x;
		if(y.domain.domainID() == Set.SetID)
			this.yVar = new Variable(y.store,"XeqY_y_"+numberId, new SetDomain((Set)y.dom(),(Set)y.dom()));
		else
			this.yVar = y;
		if(x.domain.domainID() != SetDomain.SetDomainID ||  y.domain.domainID() != SetDomain.SetDomainID){
			throw new JaCoPException("This constraint should only be used with sets or setDomains.");
		}

	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(2);

		variables.add(xVar);
		variables.add(yVar);
		return variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		while (store.newPropagation) {
			store.newPropagation = false;
			xVar.domain.in(store.level, xVar, yVar.dom()); //.cloneLight());
			yVar.domain.in(store.level, yVar, xVar.dom()); //.cloneLight());
		}
	}

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {
		return null;
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {

		if (notConsistencyPruningEvents != null) {
			Integer possibleEvent = notConsistencyPruningEvents.get(var);
			if (possibleEvent != null)
				return possibleEvent;
		}
		return Constants.ANY;
	}

	@Override
	public int getNotConsistencyPruningEvent(Variable var) {

		if (notConsistencyPruningEvents != null) {
			Integer possibleEvent = notConsistencyPruningEvents.get(var);
			if (possibleEvent != null)
				return possibleEvent;
		}
		return Constants.GROUND;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_XeqYSet + numberId;
	}

	@Override
	public void impose(Store store) {
		xVar.putModelConstraint(this, getConsistencyPruningEvent(xVar));
		yVar.putModelConstraint(this, getConsistencyPruningEvent(yVar));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void notConsistency(Store store) {
	    if(xVar.singleton() && yVar.singleton() && ((SetDomain)xVar.dom()).glb.eq(((SetDomain)yVar.dom()).glb))
			xVar.store.throwFailException(this);
	}

	@Override
	public boolean notSatisfied() {
		SetDomain x = (SetDomain) xVar.domain;
		SetDomain y = (SetDomain) yVar.domain;
		if(!x.lub.contains(y.glb) || !y.lub.contains(x.glb))
			return true;
		if(xVar.singleton() && yVar.singleton() && !((SetDomain)xVar.domain).glb.eq(((SetDomain)yVar.domain).glb)){
			return true;
		}
		return false;
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		xVar.removeConstraint(this);
		yVar.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		if(xVar.singleton() && yVar.singleton() && ((SetDomain)xVar.domain).glb.eq(((SetDomain)yVar.domain).glb)){
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
		return id() + " : XeqY(" + xVar + ", " + yVar + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for XeqY for Sets not implemented.");

	}

	@Override
	public short type() {
		return type;
	}


	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			xVar.weight++;
			yVar.weight++;
		}
	}	

}
