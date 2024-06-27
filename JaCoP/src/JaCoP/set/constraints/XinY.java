/**
 *  XinY.java 
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
import JaCoP.core.Interval;
import JaCoP.core.IntervalDomain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.set.core.SetDomain;
import JaCoP.set.core.Set;

/**
 * It creates a constraint that makes sure that the domain of X is included
 * in the set value of the Y set variable. X can be a finite domain variable 
 * or a set variable.
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm
 * @version 2.4
 */

public class XinY extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = XinYSet;

	Variable x, y;

	boolean SETDOMAIN = false;
	boolean SET = false;
	boolean FDV_BOUND = false;
	boolean FDV_INTERVAL = false;

	/**
	 * It constructs an XinY constraint to restrict the domain of the variables X and Y.
	 * @param x variable x that is restriction to be a subset of y.
	 * @param y variable that is restricted to contain x.
	 */
	public XinY(Variable x,Variable y) {
		numberId = IdNumber++;
		numberArgs = 1;
		this.x = x;
		this.y = y;
		if(y.domain.domainID() != SetDomain.SetDomainID){
			throw new JaCoPException("This constraint should only be used with setDomain as domain for y.");
		}
		if(x.domain.domainID() == SetDomain.SetDomainID)
			SETDOMAIN = true;
		else if(x.domain.domainID() == Set.SetID)
			SET = true;
		else if(x.domain.domainID() == BoundDomain.BoundDomainID)
			FDV_BOUND = true;	
		else if(x.domain.domainID() == IntervalDomain.IntervalDomainID)
			FDV_INTERVAL = true;
		else
			throw new JaCoPException("Incorrect domain for x.");
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(2);

		variables.add(x);
		variables.add(y);

		return variables;
	}

	@Override
	public void removeLevel(int level) {
	}
	@Override
	public void consistency(Store store) {
		if(y.domain.isEmpty())
			store.throwFailException(this);
		if(SETDOMAIN){
			SetDomain s1 = (SetDomain) x.dom().cloneLight();
			SetDomain s2 = (SetDomain) y.dom().cloneLight();
			s1.lub = s1.lub.intersect(s2.lub);
			s2.glb = s2.glb.union(s1.glb);
			store.newPropagation = false;
			x.domain.in(store.level, x, s1); 
			y.domain.in(store.level, y, s2);
		}else if(SET){
			SetDomain sd = (SetDomain) y.dom().cloneLight();
			sd.glb = sd.glb.union((Set)x.domain);
			y.domain.in(store.level, y, sd);
		}else if(FDV_BOUND){
			SetDomain sd = (SetDomain) y.dom().cloneLight();
			BoundDomain bd = (BoundDomain) x.dom().cloneLight();
			while(!sd.lub.contains(bd.min())){
				bd = (BoundDomain) bd.subtract(bd.min());
			}
			while(!sd.lub.contains(bd.max())){
				bd = (BoundDomain) bd.subtract(bd.max());
			}
			x.domain.in(store.level, x, bd);
			if(bd.singleton()){
				sd.glb.addDom(bd.min());
				y.domain.in(store.level, y, sd);
			}
		}else if(FDV_INTERVAL){
			SetDomain sd = (SetDomain) y.dom().cloneLight();
			IntervalDomain id = (IntervalDomain) x.domain.cloneLight();
			IntervalDomain lub = new IntervalDomain(sd.lub.intervals.length);
			for(Interval i : sd.lub.intervals){
				if(i == null)
					break;
				lub.addDom(i);
			}

			IntervalDomain new_x = (IntervalDomain)id.subtract(id.subtract(lub));
			x.domain.in(store.level, x, new_x);

			if(id.singleton()){
				sd.glb.addDom(id.min());
				y.domain.in(store.level, y, sd);
			}
		}else{
			throw new JaCoPException("Incorrect domain for x.");
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
	public int getNotConsistencyPruningEvent(Variable var) {

		// If notConsistency function mode
		if (notConsistencyPruningEvents != null) {
			Integer possibleEvent = notConsistencyPruningEvents.get(var);
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
			return id_XinYSet + numberId;
	}

	@Override
	public void impose(Store store) {
		x.putModelConstraint(this, getConsistencyPruningEvent(x));
		y.putModelConstraint(this, getConsistencyPruningEvent(y));

		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void notConsistency(Store store) {
		if(SETDOMAIN){
			if(((SetDomain)y.domain).glb.contains(((SetDomain)x.domain).lub))
				store.throwFailException(this);
		}else if(SET){
			if(((SetDomain)y.domain).glb.contains((Set)x.domain))
				store.throwFailException(this);
		}else if(x.singleton()){
			SetDomain new_y = (SetDomain)y.domain.cloneLight();
			new_y.lub = new_y.lub.subtract(x.domain.min());
			y.domain.in(store.level, y, new_y);			
		}else{
			Set s;
			if(FDV_BOUND){
				s = new Set(x.domain.min(),x.domain.max());
			}else if(FDV_INTERVAL){
				IntervalDomain id = (IntervalDomain) x.dom();
				s = new Set(id.size);
				for(Interval i : id.intervals){
					if(i == null)
						break;
					s.addDom(i);
				}
			}else
				s = new Set();
			if(((SetDomain)y.domain).glb.contains(s))
				store.throwFailException(this);

		}

	}

	@Override
	public boolean notSatisfied() {
		if(SETDOMAIN)
			return (!((SetDomain) y.dom()).lub.contains(((SetDomain) x.dom()).lub) );
		else if(SET)
			return !((SetDomain) y.dom()).lub.contains((Set) x.dom());
		else if(FDV_BOUND)
			return !((SetDomain) y.domain).lub.isIntersecting(new Set(x.domain.min(),x.domain.max()));
		else if(FDV_INTERVAL){
			IntervalDomain id = (IntervalDomain) x.dom();
			Set s = new Set(id.size);
			for(Interval i : id.intervals){
				if(i == null)
					break;
				s.addDom(i);
			}
			return !((SetDomain) y.dom()).lub.isIntersecting(s);

		}else
			throw new JaCoPException("Incorrect domain for x.");


	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		x.removeConstraint(this);
		y.removeConstraint(this);

	}

	@Override
	public boolean satisfied() {
		if(SETDOMAIN)
			return ((SetDomain) y.dom()).glb.contains(((SetDomain) x.dom()).lub);
		else if(SET)
			return ((SetDomain) y.dom()).glb.contains((Set) x.dom());
		else if(FDV_BOUND){
			Set s = new Set(x.domain.min(), x.domain.max());
			return ((SetDomain) y.dom()).glb.contains(s);
		}else if(FDV_INTERVAL){
			IntervalDomain id = (IntervalDomain) x.dom();
			Set s = new Set(id.size);
			for(Interval i : id.intervals){
				if(i == null)
					break;
				s.addDom(i);
			}
			return ((SetDomain) y.dom()).glb.contains(s);
		}else
			throw new JaCoPException("Incorrect domain for x.");

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
		return id() + " : XinY(" + x + ", " + y + " )";
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for XinYSet not implemented.");

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			x.weight++;
			y.weight++;
		}
	}	

}
