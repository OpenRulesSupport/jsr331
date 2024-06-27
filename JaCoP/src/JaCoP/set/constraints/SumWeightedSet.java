/**
 *  SumWeightedSet.java 
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
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;
import JaCoP.set.core.SetDomain;
import JaCoP.set.core.Set;

/**
 * It computes a weighted sum of the elements in the domain of the given set variable. 
 * The sum must be equal to the specified sum variable. The number of possible elements in 
 * the domain of the set variable must match the length of the weights array. 
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm
 * @version 2.4
 */

public class SumWeightedSet extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = sumWeightSet;

	Variable varSum, varSD;

	HashMap<Integer,Integer> elementWeights;

	//Cardinality variable might speed up search.
	//	Variable Cardinality;
	/**
	 * It constructs an weighted sum constraint to restrict the domain of the variables S and totalweight
	 * so that the sum of the weights belonging to the elements in S is equal to totalweight. 
	 * If weights are not submitted then this constraint will work as a sum instead.    
	 * @param S 
	 * @param weights 
	 * @param totalweight 
	 */
	public SumWeightedSet(Variable S, int[] weights, Variable totalweight) {
		if(totalweight.domain.domainID() == SetDomain.SetDomainID || totalweight.domain.domainID() == Set.SetID)
			throw new JaCoPException("Set or SetDomain is not allowed as domain for the totalweight.");
		numberId = IdNumber++;
		numberArgs = 1;
		varSum = totalweight;
		varSD = S;
		boolean empty = false;
		if(S.domain.domainID() == SetDomain.SetDomainID){
			SetDomain sd = (SetDomain) S.domain;
			if(weights.length == 0){
				weights = new int[sd.lub.card()];
				empty = true;
			}
			else if(sd.lub.card() != weights.length)
				throw new JaCoPException("The number of elements in LUB must be equal to the number of weights.");
			elementWeights = new HashMap<Integer,Integer>(weights.length);
			ValueEnumeration ve = sd.lub.valueEnumeration();
			int element;
			int index = 0;
			while(ve.hasMoreElements()){
				element = ve.nextElement();
				if(empty)
					elementWeights.put(element, element);
				else
					elementWeights.put(element, weights[index]);
				index++;			
			}

		} else {
			throw new JaCoPException("This constraint should only be used with SetDomain variable as first argument.");
		}

	}


	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(2);

		Variables.add(varSum);
		Variables.add(varSD);

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}
	@Override
	public void consistency(Store store) {
		int glbSum = 0;
		int lubSum = 0;

		SetDomain newSD = (SetDomain)varSD.domain.cloneLight();
		Set glb = newSD.glb.cloneLight();
		Set lub = newSD.lub.cloneLight();
		Set ground = lub.subtract(glb);
		Set notInDom = new Set();

		ValueEnumeration ve = glb.valueEnumeration();
		while(ve.hasMoreElements()){
			glbSum += elementWeights.get(ve.nextElement());
		}
		lubSum = glbSum;
		ve = ground.valueEnumeration();
		Integer el, weight;
		while(ve.hasMoreElements()){
			el = ve.nextElement();
			weight = elementWeights.get(el);
			if(varSum.max() < (glbSum + weight))
				notInDom.addDom(el);
			else
				lubSum += weight;
		}
		newSD.lub = newSD.lub.subtract(notInDom);

		store.newPropagation = false;
		varSum.domain.in(store.level, varSum, glbSum, lubSum);
		varSD.domain.in(store.level, varSD, newSD);

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
			return id_sumWeightSet + numberId;
	}

	@Override
	public void impose(Store store) {
		varSum.putModelConstraint(this, getConsistencyPruningEvent(varSum));
		varSD.putModelConstraint(this, getConsistencyPruningEvent(varSD));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		varSum.removeConstraint(this);
		varSD.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		if(!varSum.singleton())
			return false;
		if(varSD.singleton()){
			ValueEnumeration ve = ((SetDomain)varSD.domain).glb.valueEnumeration();
			int sum = 0;
			while(ve.hasMoreElements()){
				sum += elementWeights.get(ve.nextElement());
			}
			return varSum.min() == sum;
		}else{
			return false;
		}		
	}

	@Override
	public String toString() {
		String ret = id() + " : SumWeightedSet(" + varSD + ", < ";
		Integer weight;
		for ( Integer el : elementWeights.keySet()){
			weight = elementWeights.get(el);
			ret += "<"+el+","+weight+"> ";
		}
		ret +=">, ";
		if(varSum.singleton())
			return ret + varSum.min()+" )";
		else
			return ret + varSum.dom()+" )";
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for SumWeightedSet for Sets not implemented.");

	}

	@Override
	public short type() {
		return type;
	}


	@Override
	public void increaseWeight() {
		varSD.weight++;
		varSum.weight++;
	}	

}
