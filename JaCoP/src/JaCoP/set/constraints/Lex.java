/**
 *  Lex.java 
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
 * It creates a lex constraint on two variables which are required to have
 * lex ordering between them.
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm
 * @version 2.4
 */

public class Lex extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = lexSet;

	Variable varS1, varS2;

	/**
	 * It constructs an Lexical ordering constraint to restrict the domain of the variables S1 and S2
	 * 
	 * @param s1 variable that is restricted to be less than s2 with lexical order.
	 * @param s2 variable that is restricted to be greater than s1 with lexical order.
	 */
	public Lex(Variable s1, Variable s2) {
		numberId = IdNumber++;
		numberArgs = 1;
		varS1 = s1;
		varS2 = s2;
		if(varS1.domain.domainID() == SetDomain.SetDomainID){
			if(varS2.domain.domainID() == SetDomain.SetDomainID){
			}else if(varS2.domain.domainID() == Set.SetID){
				varS2.domain.setDomain(new SetDomain((Set)varS2.domain.cloneLight(),(Set)varS2.domain.cloneLight()));
			}else{
				throw new JaCoPException("This constraint should only be used with set or setDomain variables.");			
			}
		}else if(varS1.domain.domainID() == Set.SetID){
			varS1.domain.setDomain(new SetDomain((Set)varS1.domain.cloneLight(),(Set)varS1.domain.cloneLight()));
			if(varS2.domain.domainID() == SetDomain.SetDomainID){
			}else if(varS2.domain.domainID() == Set.SetID){
				varS2.domain.setDomain(new SetDomain((Set)varS2.domain.cloneLight(),(Set)varS2.domain.cloneLight()));
			}else{
				throw new JaCoPException("This constraint should only be used with set or setDomain variables.");			
			}
		}else{
			throw new JaCoPException("This constraint should only be used with set or setDomain variables.");			
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
		SetDomain new_s1 = ((SetDomain)varS1.domain).cloneLight();
		SetDomain new_s2 = ((SetDomain)varS2.domain).cloneLight();

// 		if(new_s1.checkInvariants() != null || new_s2.checkInvariants() != null)
// 			varS1.store.throwFailException(this);

		Set s1_min,s1_max,s2_min,s2_max;
		if(varS2.isEmpty())
			varS2.store.throwFailException(this);
		if(!varS1.isEmpty()){
			if(new_s1.glb.isEmpty()){
				s1_min = new Set();
				s1_max = new Set(new_s1.lub.max(),new_s1.lub.max());
			}else if(new_s1.glb.lex(new_s1.lub) == -1){
				s1_min = new_s1.glb.cloneLight();
				s1_max = s1_min.cloneLight();
				s1_max.addDom(new_s1.lub.subtract(s1_min).max());

			}else if(new_s1.glb.lex(new_s1.lub) == 1){//s1.glb > s1.lub
				s1_min = new_s1.glb.cloneLight();
				s1_min.addDom(new_s1.lub.subtract(new_s1.glb).min());
				s1_max = new_s1.glb.cloneLight();
				s1_max.addDom(new_s1.lub.subtract(new_s1.glb).max());

			}else{
				s1_min = new_s1.glb.cloneLight();
				s1_max = new_s1.lub.cloneLight();
			}

			if(new_s2.glb.isEmpty()){
				s2_min = new Set();
				s2_max = new Set(new_s2.lub.max(),new_s2.lub.max());
			}else if(new_s2.glb.lex(new_s2.lub) == -1){
				s2_min = new_s2.glb.cloneLight();
				s2_max = s2_min.cloneLight();
				s2_max.addDom(new_s2.lub.subtract(s2_min).max());

			}else if(new_s2.glb.lex(new_s2.lub) == 1){//s2.glb > s2.lub
				s2_min = new_s2.glb.cloneLight();
				s2_min.addDom(new_s2.lub.subtract(new_s2.glb).min());
				s2_max = new_s2.glb.cloneLight();
				s2_max.addDom(new_s2.lub.subtract(new_s2.glb).max());

			}else{
				s2_min = new_s2.glb.cloneLight();
				s2_max = new_s2.lub.cloneLight();
			}

			if(s2_max.lex(s1_min) <= 0){
				varS2.store.throwFailException(this);
			}else if(s2_min.lex(s1_min) < 0 && s2_max.lex(s1_max) < 0){
				while(s1_min.lex(new_s2.lub) >= 0 && !new_s2.singleton()){
					new_s2.lub = new_s2.lub.subtract(new_s2.lub.subtract(new_s2.glb).min());
				}
			}else if(s2_min.lex(s1_min) > 0 && s2_max.lex(s1_max) > 0){
				while(s1_min.lex(new_s2.lub) >= 0 ){
					new_s2.lub = new_s2.lub.subtract(new_s2.lub.subtract(s2_min).min());
				}
			}
			if(!(s1_max.lex(s2_min) <= 0)){

// 				if(!varS1.domain.eq(new_s1) || !varS2.domain.eq(new_s2)){
// 					System.out.println("##############");
// 					System.out.println(varS1 + " "+new_s1);
// 					System.out.println(varS2 + " "+new_s2);
// 					System.out.println("##############\n");
// 				}

				store.newPropagation = false;
				varS1.domain.in(store.level, varS1, new_s1);
				varS2.domain.in(store.level, varS2, new_s2);
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
		return Constants.BOUND;		
	}


	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_lexSet + numberId;
	}

	@Override
	public void impose(Store store) {
		varS1.putModelConstraint(this, getConsistencyPruningEvent(varS1));
		varS2.putModelConstraint(this, getConsistencyPruningEvent(varS2));
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
		return (((SetDomain)varS1.domain).lub.lex(((SetDomain)varS2.domain).lub) < 0 && varS1.singleton() && varS2.singleton());
	}

	@Override
	public String toString() {
		return id() + " : Lex(" + varS1 + " '< "+ varS2+")";
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for Lex not implemented.");

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
