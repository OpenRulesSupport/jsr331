/**
 *  Match.java 
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
import JaCoP.core.Domain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;
import JaCoP.set.core.SetDomain;
import JaCoP.set.core.Set;

/**
 * This constraint matches the elements of the given set variable
 * onto a list of finite domain variables. 
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm
 * @version 2.4
 */

public class Match extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = matchSet;

	Variable varS1;
	Variable[] list;

	/**
	 * It constructs a match constraint to restrict the domains of the variable s1 and the variables in an 
	 * array FDVs.
	 * 
	 * @param s1 variable that is restricted to have the same elements as the FDVs.
	 * @param fdvs variable array that is restricted to have the same elements as s1.
	 */

	public Match(Variable s1, Variable[] fdvs) {
		numberId = IdNumber++;
		numberArgs = 1;
		varS1 = s1;
		list = new Variable[fdvs.length]; 
		System.arraycopy(fdvs, 0, list, 0, fdvs.length);

		if(s1.domain.domainID() != SetDomain.SetDomainID ){
			throw new JaCoPException("This constraint should only be used with s1 as a setDomain variable.");
		}
		
		for(Variable v : fdvs)
			if(v.domain.domainID() == SetDomain.SetDomainID ||v.domain.domainID() == Set.SetID){
				throw new JaCoPException("This constraint should not be used with set or setDomain variables as last argument.");
			}
		s1.store.impose(new Card(varS1,list.length));
		for(int i = 0 ; i < list.length - 1 ; i++){
			s1.store.impose(new XgtY(list[i+1],list[i]));
		}
		for(int i = 0 ; i < list.length ; i++){
			s1.store.impose(new XinY(list[i],varS1));
		}
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(list.length +1);

		Variables.add(varS1);
		for(Variable fdv : list)
			Variables.add(fdv);

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		store.newPropagation = false;
		if(((SetDomain)varS1.domain).glb.card() == list.length){
			ValueEnumeration ve = ((SetDomain)varS1.domain).glb.valueEnumeration();
			int el;
			for(int i = 0 ; i < list.length ; i++){
				el = ve.nextElement();
				list[i].domain.in(store.level, list[i], el, el);
			}
			SetDomain new_s1 = (SetDomain)varS1.domain.cloneLight();
			new_s1.lub = new_s1.glb.cloneLight();
			varS1.domain.in(store.level, varS1,new_s1);
		}else if(((SetDomain)varS1.domain).lub.card() == list.length){
			ValueEnumeration ve = ((SetDomain)varS1.domain).lub.valueEnumeration();
			int el;
			for(int i = 0 ; i < list.length ; i++){
				el = ve.nextElement();
				list[i].domain.in(store.level, list[i], el,el);
			}
			SetDomain new_s1 = (SetDomain)varS1.domain.cloneLight();
			new_s1.glb = new_s1.lub.cloneLight();
			varS1.domain.in(store.level, varS1,new_s1);
		}else{
			Domain[] fdvs = new Domain[list.length];
			SetDomain sd = (SetDomain) varS1.domain.cloneLight();
			Set new_glb = sd.glb.cloneLight();
			Set new_lub = sd.lub.cloneLight();
			for(int i = 0 ; i < list.length ; i++){
				fdvs[i] = list[i].domain.cloneLight();
				if(fdvs[i].singleton())
					new_glb.addDom(fdvs[i].min());
				new_lub.addDom(fdvs[i]);
			}


			Set lub = ((SetDomain)varS1.domain).lub.cloneLight();
			int min_a,max_a,min_b,max_b;
			int l = fdvs.length;
			fdvs[0] = fdvs[0].intersect(lub);
			if(fdvs[0].isEmpty())
				store.throwFailException(this);

			for(int i = 1 ; i < l ; i++){
				fdvs[i] = fdvs[i].intersect(lub);
				if(fdvs[i].isEmpty() || fdvs[i-1].isEmpty())
					store.throwFailException(this);
				min_a = Math.max(fdvs[i-1].min()+1, fdvs[i].min());
				max_a = Math.max(fdvs[i-1].max()+1, fdvs[i].max());
				fdvs[i] = fdvs[i].intersect(min_a,max_a);
				if(fdvs[l-i].isEmpty() || fdvs[l-i-1].isEmpty())
					store.throwFailException(this);
				min_b = Math.min(fdvs[l-i-1].min(), fdvs[l-i].min()-1);
				max_b = Math.min(fdvs[l-i-1].max(), fdvs[l-i].max()-1);
				fdvs[l-i-1] = fdvs[l-i-1].intersect(min_b,max_b);
				if(fdvs[i-1].singleton() && i != 1){
					if(((SetDomain)varS1.domain).lub.elementsSmallerThan(fdvs[i-1].min()) == i-1){
						ValueEnumeration ve = ((SetDomain)varS1.domain).lub.valueEnumeration();
						int val;
						for(int j = 0 ; j< i-1 ; j ++){
							val = ve.nextElement();
							fdvs[j] = fdvs[j].intersect(val,val);
						}
					}
				}
			}
			((SetDomain)varS1.domain).in(store.level, varS1, new_glb,new_lub);
			for(int i = 0; i < fdvs.length ; i++){
				list[i].domain.in(store.level, list[i], fdvs[i]);
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
			return id_matchSet + numberId;
	}

	@Override
	public void impose(Store store) {
		varS1.putModelConstraint(this, getConsistencyPruningEvent(varS1));
		for(Variable fdv : list)
		    fdv.putModelConstraint(this, getConsistencyPruningEvent(fdv));
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		varS1.removeConstraint(this);
		for(Variable fdv :list)
			fdv.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		if(((SetDomain)varS1.domain).glb.card() == list.length && varS1.singleton()){
			ValueEnumeration ve = ((SetDomain)varS1.domain).glb.valueEnumeration();
			for(int i = 0 ; i < list.length ; i++){
				if(!list[i].singleton())
					return false;
				if(ve.nextElement() != list[i].min())
					return false;
			}
			return true;
		}
		return false;
	}



	@Override
	public String toString() {
		String ret = id() + " : Match(" + varS1 + ", [ ";
		for(Variable fdv : list)
			ret += fdv + " ";
		ret +="] )";
		return ret;
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for Match not implemented.");

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public void increaseWeight() {
		varS1.weight++;
		for(Variable fdv : list)
			fdv.weight++;
	}	

}
