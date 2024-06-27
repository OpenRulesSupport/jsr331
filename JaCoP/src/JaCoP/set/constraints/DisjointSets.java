/**
 *  DisjointSets.java 
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
 * The disjoint set constraint makes sure that two set variables
 * have disjoint set values.
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm
 * @version 2.4
 */

public class DisjointSets extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = disjointSets;

	Variable varS1, varS2;

	/**
	 * It constructs a disjontSet constraint to restrict the domains of the variables S1 and S2.
	 * 
	 * @param s1 variable that is restricted to not have any element in common with s2.
	 * @param s2 variable that is restricted to not have any element in common with s1.
	 */
	public DisjointSets(Variable s1, Variable s2) {
		numberId = IdNumber++;
		numberArgs = 1;
		varS1 = s1;
		varS2 = s2;
		if(s1.domain.domainID() != SetDomain.SetDomainID || s2.domain.domainID() != SetDomain.SetDomainID){
			throw new JaCoPException("This constraint should only be used with setDomain variables.");
		}

	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(2);

		variables.add(varS1);
		variables.add(varS2);

		return variables;
	}

	@Override
	public void removeLevel(int level) {
	}
	@Override
	public void consistency(Store store) {
		SetDomain s1 = (SetDomain) varS1.dom().cloneLight();
		SetDomain s2 = (SetDomain) varS2.dom().cloneLight();

		//T3
		s1.lub = s1.lub.subtract(s2.glb);
		//T4
		s2.lub = s2.lub.subtract(s1.glb);

		store.newPropagation = false;
		varS1.domain.in(store.level, varS1, s1);
		varS2.domain.in(store.level, varS2, s2); 
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
			return id_disjointSets + numberId;
	}
	@Override
	public void impose(Store store) {
		varS1.putModelConstraint(this,getConsistencyPruningEvent(varS1));
		varS2.putModelConstraint(this,getConsistencyPruningEvent(varS2));

		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		varS1.removeConstraint(this);
		varS2.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		return ((SetDomain)varS1.domain).lub.intersect(((SetDomain)varS2.domain).lub).isEmpty();
	}

	@Override
	public String toString() {
		return id() + " : DisjointSets(" + varS1 + ", " + varS2+" )";
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for DisjointSets not implemented.");

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public void increaseWeight() {
		varS1.weight++;
		varS2.weight++;
	}	

}
