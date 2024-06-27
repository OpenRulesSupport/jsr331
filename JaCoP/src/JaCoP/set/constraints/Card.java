/**
 *  Card.java 
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
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.set.core.SetDomain;
import JaCoP.set.core.Set;

/**
 * The cardinality constraint.
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm
 * @version 2.4
 */

public class Card extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = cardSet;

	Variable x;

	Variable c;

	/**
	 * It constructs a card constraint to restrict the domain of the variable X
	 * 
	 * @param x variable that is restricted to have the cardinality c.
	 * @param c domain for the cardinality variable.
	 */
	public Card(Variable x, Domain c) {
		numberId = IdNumber++;
		numberArgs = 1;
		this.x = x;
		this.c = new Variable(x.store, "cardVar_"+numberId, c);

		if(x.domain.domainID() != SetDomain.SetDomainID || c.domainID() == SetDomain.SetDomainID || c.domainID()==Set.SetID ) {
			throw new JaCoPException("This constraint should only be used with setDomain variables and cardinality with finite domain.");
		}

	}

	/**
	 * It constructs an card constraint to restrict the domain of the variable X
	 * 
	 * @param x variable that is restricted to have the cardinality c.
	 * @param c the value accepted for the cardinality of the set variable.
	 */
	public Card(Variable x, int c) {
		numberId = IdNumber++;
		numberArgs = 1;
		this.x = x;
		this.c = new Variable(x.store, "cardVar_" + numberId, new BoundDomain(c,c) );

		if(x.domain.domainID() != SetDomain.SetDomainID ){
			throw new JaCoPException("This constraint should only be used with setDomain.");
		}

	}

	/**
	 * It constructs an card constraint to restrict the domain of the variable X
	 * 
	 * @param x variable that is restricted to have the cardinality [m,n].
	 * @param min the minimum value possible for the cardinality of x.
	 * @param max the maximum value possible for the cardinality of x.
	 */
	public Card(Variable x, int min, int max) {
		numberId = IdNumber++;
		numberArgs = 1;
		this.x = x;
		this.c = new Variable(x.store, "cardVar_" + numberId, new BoundDomain(min, max));

		if(x.domain.domainID() != SetDomain.SetDomainID ){
			throw new JaCoPException("This constraint should only be used with setDomain.");
		}

	}

	/**
	 * It constructs an card constraint to restrict the domain of the variable x.
	 * 
	 * @param x variable that is restricted to have the cardinality c.
	 * @param c the variable specifying possible values for cardinality of x.
	 */
	public Card(Variable x, Variable c) {
		numberId = IdNumber++;
		numberArgs = 1;
		this.x = x;
		this.c = c;
		if(x.domain.domainID() != SetDomain.SetDomainID || c.domain.domainID()==SetDomain.SetDomainID ||c.domain.domainID()==Set.SetID){
			throw new JaCoPException("This constraint should only be used with setDomain and cardinality as finite domain.");
		}

	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(2);

		variables.add(x);
		variables.add(c);

		return variables;
	}

	@Override
	public void removeLevel(int level) {}

	@Override
	public void consistency(Store store) {
		SetDomain s1 = (SetDomain) x.dom().cloneLight();
		Domain card = c.dom().cloneLight();
		int min,max;
		//T12
		min = Math.max(s1.glb.card(),card.min());
		max = Math.min(s1.lub.card(),card.max());
		if (min > max)
			store.throwFailException(this);
		//T13 else //T14
		if(s1.glb.card() == card.max()) {
			s1.lub = s1.glb;
		} else if(s1.lub.card() == card.min()){
			s1.glb = s1.lub;
		}

		store.newPropagation = false;
		x.domain.in(store.level, x, s1);
		store.newPropagation = false;
		c.domain.in(store.level, c, min, max); 
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
			return id_cardSet + numberId;
	}

	@Override
	public void impose(Store store) {
		x.putModelConstraint(this,getConsistencyPruningEvent(x));
		c.putModelConstraint(this,getConsistencyPruningEvent(c));

		store.addChanged(this);
		store.countConstraint();
	}


	@Override
	public void queueVariable(int level, Variable V) {}

	@Override
	public void removeConstraint() {
		x.removeConstraint(this);
		c.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		return (x.singleton() && c.singleton() && ((SetDomain)x.domain).card().eq(c.dom()));
	}


	@Override
	public String toString() {
		if(c.singleton())
			return id() + " : card(" + x + ", " + c.min()+" )";
		else
			return id() + " : card(" + x + ", " + c.dom()+" )";
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for Card not implemented.");

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public void increaseWeight() {
		x.weight++;
		c.weight++;
	}	

}
