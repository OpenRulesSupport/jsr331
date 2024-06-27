/**
 *  SetDomain.java 
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

package JaCoP.set.core;

import java.util.Iterator;

import org.jdom.Element;
import JaCoP.core.*;
import JaCoP.constraints.Constraint;
import JaCoP.set.core.Set;
import java.util.ArrayList;

/**
 * Defines a set interval determined by a least upper bound(lub) and a 
 * greatest lower bound(glb). The domain consist of zero, one or several sets.
 * 
 * 
 * @author Radoslaw Szymanek, Krzysztof Kuchcinski and Robert Åkemalm 
 * @version 2.4
 */

public class SetDomain extends Domain {

	/**
	 * It specifies an unique ID for the domain. 
	 */
	public static final int SetDomainID = 3;

	/**
	 * The greatest lower bound of the domain.
	 */

	public Set glb;

	/**
	 * The least upper bound of the domain.
	 */

	public Set lub;

	/**
	 * It specifies the previous domain which was used by this domain. The old
	 * domain is stored here and can be easily restored if necessary.
	 */

	public Domain previousDomain;	


	/**
	 * It predefines empty domain so there is no need to constantly create it when
	 * needed.
	 */
	static public SetDomain emptyDomain = new SetDomain();


	/**
	 * It is a constructor which will create an empty SetDomain. An empty SetDomain
	 * has a glb and a lub that is empty.
	 */
	public SetDomain() {
		this.glb = new Set();
		this.lub = new Set();
		searchConstraints = null;
		searchConstraintsToEvaluate = 0;
		previousDomain = null;
		searchConstraintsCloned = false;

	}

	/** Creates a new instance of SetDomain. It requires glb to be a subset of lub.
	 * @param glb it specifies the left bound of the SetDomain (inclusive).  
	 * @param lub it specifies the right bound of the setDomain (inclusive).
	 */
	public SetDomain(Set glb, Set lub) {

		if(!lub.contains(glb))
			throw new JaCoPException();
		this.glb = glb.cloneLight();
		this.lub = lub.cloneLight();

		searchConstraints = null;
		searchConstraintsToEvaluate = 0;
		previousDomain = null;
		searchConstraintsCloned = false;

	}

	/** 
	 * 
	 * It creates a new instance of SetDomain with glb empty and lub={e1..e2}
	 * @param e1 the minimum element of lub.
	 * @param e2 the maximum element of lub.
	 */
	public SetDomain(int e1, int e2) {

		this.glb = new Set();
		this.lub = new Set(e1, e2);

		searchConstraints = null;
		searchConstraintsToEvaluate = 0;
		previousDomain = null;
		searchConstraintsCloned = false;

	} 

	/**
	 * Adds an interval to the lub.
	 * @param i  The interval to be added to the lub.
	 */
	@Override
	public void addDom(Interval i) {
		Set s = new Set(1);
		s.addDom(i);
		this.addDom(s);
	}

	/**
	 * Adds a set to the domain.
	 */
	@Override
	public void addDom(Domain domain) {
		if(domain.domainID() == Set.SetID){
			Set s = (Set) domain;

			assert s.checkInvariants() == null : s.checkInvariants() ;

			this.lub = this.lub.union(s);
		}
	}

	/**
	 * Adds an interval [min..max] to the domain.
	 */
	@Override
	public void addDom(int min, int max) {
		this.addDom(new Set(min,max));
	}

	/**
	 * Returns the cardinality of the setDomain as [glb.card(), lub.card()] 
	 * @return The cardinality of the setDomain given as a boundDomain.
	 */
	public BoundDomain card(){
		return new BoundDomain(glb.card(),lub.card());
	}

	/**
	 * Sets the domain to an empty SetDomain.
	 */
	@Override
	public void clear() {
		glb = Set.emptySet;
		lub = Set.emptySet;
	}


	/**
	 * Clones the domain.
	 */
	@Override
	public SetDomain clone() {
		SetDomain cloned = new SetDomain(glb.cloneLight(), lub.cloneLight()); 
		cloned.stamp = stamp;
		cloned.previousDomain = previousDomain;

		cloned.searchConstraints = searchConstraints;
		cloned.searchConstraintsToEvaluate = searchConstraintsToEvaluate;

		cloned.modelConstraints = modelConstraints;
		cloned.modelConstraintsToEvaluate = modelConstraintsToEvaluate;

		cloned.searchConstraintsCloned = searchConstraintsCloned;

		return cloned;	
	}

	/**
	 * It clones the domain object, only data responsible for encoding domain
	 * values is cloned. All other fields must be set separately.
	 * @return return a clone of the domain. It aims at getting domain of the proper class type. 
	 */
	@Override
	public SetDomain cloneLight() {
		return new SetDomain(glb, lub); 
		// 		return new SetDomain(glb.cloneLight(), lub.cloneLight()); 
	}

	/**
	 * It creates a complement of a domain.
	 * @return it returns the complement of this domain.
	 */
	@Override
	public Domain complement() {
		return new SetDomain(this.lub.complement(),this.glb.complement());		
	}

	/**
	 * It checks if the supplied set or setDomain is a subset of this domain.
	 */
	@Override
	public boolean contains(Domain domain) {
		if(domain.domainID() == Set.SetID){
			Set s = (Set) domain;

			assert s.checkInvariants() == null : s.checkInvariants() ;

			if(this.lub.contains(s))
				return true;
			return false;
		}
		if(domain.domainID() == SetDomainID){
			SetDomain sd = (SetDomain) domain;
			assert sd.checkInvariants() == null : sd.checkInvariants() ;

			if(this.lub.contains(sd.lub))
				return true;
			return false;
		}	
		return false;
	}

	/**
	 * It checks if value belongs to the domain.
	 */
	@Override
	public boolean contains(int value) {
		return lub.contains(value);
	}

	/**
	 * It returns an unique identifier of the domain.
	 * @return it returns an integer id of the domain.
	 */
	@Override
	public int domainID() {
		return SetDomainID;
	}

	/**
	 * It checks if the domain is equal to the supplied domain.
	 * @param domain against which the equivalence test is performed.
	 * @return true if suppled domain has the same elements as this domain. 
	 */
	@Override
	public boolean eq(Domain domain) {
		if(domain.domainID() == SetDomainID){
			SetDomain sd = (SetDomain) domain;
			if(sd.glb.eq(this.glb) && sd.lub.eq(this.lub))
				return true;
			return false;
		}
		return false;
	}

	/**
	 * This function should not be used for SetDomain
	 */
	@Override
	public Interval getInterval(int position) {
		throw new JaCoPException("This function should not be used for SetDomain.");
	}

	/**
	 * Returns the number of elements in the domain.
	 */
	@Override
	public int getSize() {
		return (int) Math.pow(2, (lub.getSize() - glb.getSize()));
	}

	/**
	 * It returns the greatest lower bound of the domain.
	 * @return the greatest lower bound of the domain.
	 */
	public Set glb() {
		return this.glb;
	}

	/**
	 * function used by DepthFirstSearch to add an element to the domain.
	 */

	@Override
	public void in(int storeLevel, Variable var, int min, int max) {
		throw new JaCoPException("This function is not used for setDomain.");
	}

	/**
	 * This function is equivalent to in(int storeLevel, Variable var, int min, int max).
	 *
	 * @param storeLevel the level of the store at which the change occurrs.
	 * @param var the set variable for which the domain may change.
	 * @param glb the greatest lower bound of the domain. 
	 * @param lub the least upper bound of the domain.
	 */
	public void in(int storeLevel, Variable var, Set glb, Set lub) {

		if (!lub.contains(glb)){
			var.store.throwFailException(var);
		}

		if (stamp == storeLevel) {
			if (this.glb.contains(glb) && lub.contains(this.lub)){
				//   			    New domain is the same or "larger" than the old one; do nothing,
				//   			    do not re-evaluate constrained assigned to this variable
				return; 
			}

			this.glb = this.glb.union(glb);
			this.lub = this.lub.intersect(lub);

			if (this.lub.eq(this.glb)) 
				var.domainHasChanged(JaCoP.core.Constants.GROUND);
			else
				var.domainHasChanged(JaCoP.core.Constants.BOUND);

			return;

		} else {

			assert stamp < storeLevel;

			if (this.glb.contains(glb) && lub.contains(this.lub)){
				//   			    New domain the same as the old one; do nothing,
				//   			    do not reevaluate constrained assigned to this variable
				return; 
			}

			SetDomain result = new SetDomain(glb, lub);

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;

			if (result.singleton()) {
				var.domainHasChanged(JaCoP.core.Constants.GROUND);
				return;
			} else {
				var.domainHasChanged(JaCoP.core.Constants.BOUND);
				return;
			}

		}	
	}

	/**
	 * It updates the domain to have values only within the domain. The type of
	 * update is decided by the value of stamp. It informs the variable of a
	 * change if it occurred.
	 * @param storeLevel level of the store at which the update occurs.
	 * @param var variable for which this domain is used.
	 * @param domain the domain according to which the domain is updated.
	 */
	@Override
	public void in(int storeLevel, Variable var, Domain domain) {
		if(domain.domainID() == SetDomainID){
			SetDomain sd = (SetDomain) domain;
			in(storeLevel, var, sd.glb(), sd.lub());
		}else if(domain.domainID() == Set.SetID){
			Set s = (Set) domain;
			this.in(storeLevel, var, new Set(),s);
		}
	}

	/**
	 * Sets the variable to not contain the supplied value complement.
	 * If complement is an element in greatest lower bound a JaCoPException
	 * will be thrown. 
	 */
	@Override
	public void inComplement(int storeLevel, Variable var, int complement) {
		throw new JaCoPException("This function is not used for setDomain.");
	}

	/**
	 * not implemented.
	 */
	@Override
	public void inComplement(int storeLevel, Variable var, int min, int max) {
		throw new JaCoPException("This function is not used for setDomain.");
	}

	/**
	 * This function should not be used for SetDomain
	 */
	@Override
	public void inMax(int storeLevel, Variable var, int max) {
		throw new JaCoPException("This function should not be used for SetDomain.");
	}

	/**
	 * This function should not be used for SetDomain
	 */

	@Override
	public void inMin(int storeLevel, Variable var, int min) {
		throw new JaCoPException("This function should not be used for SetDomain.");
	}

	/**
	 * It updates the domain to contain the elements as specified by the domain,
	 * which is shifted. E.g. {{1..4}..{1..7}} + 3 = {{4..7}..{4..10}}
	 * @param storeLevel level of the store at which the update occurs.
	 * @param var variable for which this domain is used.
	 * @param domain the domain according to which the domain is updated.
	 * @param shift the shift which is used to shift the domain supplied as argument.
	 */
	@Override
	public void inShift(int storeLevel, Variable var, Domain domain, int shift) {
		if(domain.domainID() == SetDomainID){
			SetDomain sd = (SetDomain) domain;


		    var.store.throwFailException(var);

		    assert sd.checkInvariants() == null : sd.checkInvariants() ;

		    in(storeLevel, var, sd.glb().shift(shift), sd.glb.shift(shift));
		}
	}

	/**
	 * It intersects current domain with the one given as a parameter.
	 * @param domain domain with which the intersection needs to be computed.
	 * @return the intersection between supplied domain and this domain.
	 */
	@Override
	public Domain intersect(Domain domain) {
		if(domain.domainID() == SetDomainID){
			SetDomain sd = (SetDomain) domain;

			assert sd.checkInvariants() == null : sd.checkInvariants() ;

			Set lub_i = lub.intersect(sd.lub);
			if(lub_i.isEmpty())
				return emptyDomain;
			Set glb_i =glb.intersect(sd.glb); 
			return new SetDomain(glb_i,lub_i);
		}
		if(domain.domainID() == Set.SetID){
			Set s = (Set) domain;
			assert s.checkInvariants() == null : s.checkInvariants() ;

			Set lub_i = lub.intersect(s);
			if(lub_i.isEmpty())
				return emptyDomain;
			Set glb_i = glb.intersect(s); 
			return new SetDomain(glb_i,lub_i);
		}
		return emptyDomain;
	}

	/**
	 * It intersects current domain with the set {min..max}.
	 * @return the intersection between supplied set and this domain.
	 */
	@Override
	public Domain intersect(int min, int max) {
		Set s = lub.intersect(new Set(min,max));
		return new SetDomain(s,s);
	}

	@Override
	public IntervalEnumeration intervalEnumeration() {
		throw new JaCoPException("This function is not used for setDomain.");
	}

	/**
	 * It returns true if given domain is empty.
	 * @return true if the given domain is empty.
	 */
	@Override
	public boolean isEmpty() {
		if (glb.isEmpty() && lub.isEmpty())
			return true;
		return false;
	}

	/**
	 * It returns true if given domain intersects this domain.
	 * @return true if the given domain intersects this domain.
	 */
	@Override
	public boolean isIntersecting(Domain domain) {
		return !this.intersect(domain).isEmpty();
	}

	/**
	 * In intersects current domain with the interval min..max.
	 * @param min the left bound of the interval (inclusive)
	 * @param max the right bound of the interval (inclusive)
	 * @return the intersection between the specified interval and this domain.
	 */
	@Override
	public boolean isIntersecting(int min, int max) {
		return lub.isIntersecting(new Set(min,max));
	}

	/**
	 * A set is never numeric
	 * @return false
	 */
	@Override
	public boolean isNumeric() {
		return	false;
	}

	/**
	 * A set is not sparse
	 * @return false
	 */
	@Override
	public boolean isSparseRepresentation() {
		return false;
	}

	//returns the smallest element in the ground set lub\glb.
	/**
	 * This function is not used for setDomain.
	 */
	@Override
	public int leftElement(int intervalNo) {
		throw new JaCoPException("This function is not used for setDomain.");
		/*		assert intervalNo == 0;
		return this.min();
		 */ 
	}

	/**
	 * It returns the least upper bound of the domain.
	 * @return the least upper bound of the domain.
	 */
	public Set lub() {
		return this.lub;
	}

	@Override
	public int max() {
		throw new JaCoPException("max() should not be used for SetDomain.");
	}

	@Override
	public int min() {
		throw new JaCoPException("min() should not be used for SetDomain.");
	}

	/**
	 * This function should not be used for SetDomain.
	 */
	@Override
	public int nextValue(int value) {
		throw new JaCoPException("nextValue(int value) should not be used for SetDomain.");
	}

	/**
	 * The setDomain doesn't have any intervals.
	 */
	@Override
	public int noIntervals() {
		return 0;
	}

	/**
	 * It adds a constraint to a domain, it should only be called by
	 * putConstraint function of Variable object. putConstraint function from
	 * Variable must make a copy of a vector of constraints if vector was not
	 * cloned.
	 */
	@Override
	public void putModelConstraint(int storeLevel, Variable var, Constraint C,
			int pruningEvent) {


		if (stamp < storeLevel) {

			SetDomain result = this.cloneLight();

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;

			result.putModelConstraint(storeLevel, var, C, pruningEvent);
			return;
		}

		Constraint[] pruningEventConstraints = modelConstraints[pruningEvent];

		if (pruningEventConstraints != null) {

			boolean alreadyImposed = false;

			if (modelConstraintsToEvaluate[pruningEvent] > 0)
				for (int i = pruningEventConstraints.length - 1; i >= 0; i--)
					if (pruningEventConstraints[i] == C)
						alreadyImposed = true;

			int pruningConstraintsToEvaluate = modelConstraintsToEvaluate[pruningEvent];

			if (!alreadyImposed) {
				Constraint[] newPruningEventConstraints = new Constraint[pruningConstraintsToEvaluate + 1];

				System.arraycopy(pruningEventConstraints, 0,
						newPruningEventConstraints, 0,
						pruningConstraintsToEvaluate);
				newPruningEventConstraints[pruningConstraintsToEvaluate] = C;

				Constraint[][] newModelConstraints = new Constraint[3][];

				newModelConstraints[0] = modelConstraints[0];
				newModelConstraints[1] = modelConstraints[1];
				newModelConstraints[2] = modelConstraints[2];

				newModelConstraints[pruningEvent] = newPruningEventConstraints;

				modelConstraints = newModelConstraints;

				int[] newModelConstraintsToEvaluate = new int[3];

				newModelConstraintsToEvaluate[0] = modelConstraintsToEvaluate[0];
				newModelConstraintsToEvaluate[1] = modelConstraintsToEvaluate[1];
				newModelConstraintsToEvaluate[2] = modelConstraintsToEvaluate[2];

				newModelConstraintsToEvaluate[pruningEvent]++;

				modelConstraintsToEvaluate = newModelConstraintsToEvaluate;

			}

		} else {

			Constraint[] newPruningEventConstraints = new Constraint[1];

			newPruningEventConstraints[0] = C;

			Constraint[][] newModelConstraints = new Constraint[3][];

			newModelConstraints[0] = modelConstraints[0];
			newModelConstraints[1] = modelConstraints[1];
			newModelConstraints[2] = modelConstraints[2];

			newModelConstraints[pruningEvent] = newPruningEventConstraints;

			modelConstraints = newModelConstraints;

			int[] newModelConstraintsToEvaluate = new int[3];

			newModelConstraintsToEvaluate[0] = modelConstraintsToEvaluate[0];
			newModelConstraintsToEvaluate[1] = modelConstraintsToEvaluate[1];
			newModelConstraintsToEvaluate[2] = modelConstraintsToEvaluate[2];

			newModelConstraintsToEvaluate[pruningEvent] = 1;

			modelConstraintsToEvaluate = newModelConstraintsToEvaluate;

		}

	}

	/**
	 * It adds a constraint to a domain, it should only be called by
	 * putConstraint function of Variable object. putConstraint function from
	 * Variable must make a copy of a vector of constraints if vector was not
	 * cloned.
	 */
	@Override
	public void putSearchConstraint(int storeLevel, Variable var, Constraint C) {

		if (!searchConstraints.contains(C)) {

			if (stamp < storeLevel) {

				SetDomain result = this.cloneLight();

				result.modelConstraints = modelConstraints;

				result.searchConstraints = new ArrayList<Constraint>(
						searchConstraints.subList(0,
								searchConstraintsToEvaluate));
				result.searchConstraintsCloned = true;
				result.stamp = storeLevel;
				result.previousDomain = this;
				result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
				result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
				var.domain = result;

				result.putSearchConstraint(storeLevel, var, C);
				return;
			}

			if (searchConstraints.size() == searchConstraintsToEvaluate) {
				searchConstraints.add(C);
				searchConstraintsToEvaluate++;
			} else {
				// Exchange the first satisfied constraint with just added
				// constraint
				// Order of satisfied constraints is not preserved

				if (searchConstraintsCloned) {
					Constraint firstSatisfied = searchConstraints
					.get(searchConstraintsToEvaluate);
					searchConstraints.set(searchConstraintsToEvaluate, C);
					searchConstraints.add(firstSatisfied);
					searchConstraintsToEvaluate++;
				} else {
					searchConstraints = new ArrayList<Constraint>(
							searchConstraints.subList(0,
									searchConstraintsToEvaluate));
					searchConstraintsCloned = true;
					searchConstraints.add(C);
					searchConstraintsToEvaluate++;
				}
			}
		}

	}


	/**
	 * It returns the values which have been removed at current store level.
	 * @param storeLevel the current store level.
	 * @return emptyDomain if domain did not change at current level, or the set of values which have been removed at current level.
	 */
	@Override
	public Domain recentDomainPruning(int storeLevel) {
		if (previousDomain == null)
			return emptyDomain;

		if (stamp < storeLevel)
			return emptyDomain;

		return previousDomain.subtract(this);		
	}

	/**
	 * It removes the specified level. This function may re-instantiate
	 * the old copy of the domain (previous value) or recover from changes done at stamp
	 * level to get the previous value at level lower at provided level.
	 * @param level the level which is being removed.
	 * @param var the variable to which this domain belonged to.
	 */
	@Override
	public void removeLevel(int level, Variable var) {

		assert (this.stamp <= level);

		if (this.stamp == level) {

			if (Switches.trace)
				if (Switches.traceStore)
					if (Switches.traceLevelRemoval)
						System.out.println("Store level removed for variable "
								+ var + " and now is" + this.previousDomain);

			var.domain = this.previousDomain;
		}

		assert (var.domain.stamp < level);		
	}


	/**
	 * It removes a constraint from a domain, it should only be called by
	 * removeConstraint function of Variable object.
	 */
	@Override
	public void removeSearchConstraint(int storeLevel, Variable var,
			int position, Constraint C) {

		if (stamp < storeLevel) {

			SetDomain result = this.cloneLight();

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;

			result.removeSearchConstraint(storeLevel, var, position, C);
			return;
		}

		assert (stamp == storeLevel);

		if (position < searchConstraintsToEvaluate) {

			searchConstraints.set(position, searchConstraints
					.get(searchConstraintsToEvaluate - 1));
			searchConstraints.set(searchConstraintsToEvaluate - 1, C);
			searchConstraintsToEvaluate--;

		}

	}

	/**
	 * It removes a constraint from a domain, it should only be called by
	 * removeConstraint function of Variable object.
	 */
	@Override
	public void removeModelConstraint(int storeLevel, Variable var, Constraint C) {

		if (stamp < storeLevel) {

			SetDomain result = this.cloneLight();

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;

			result.removeModelConstraint(storeLevel, var, C);
			return;
		}

		int pruningEvent = JaCoP.core.Constants.GROUND;

		Constraint[] pruningEventConstraints = modelConstraints[pruningEvent];

		if (pruningEventConstraints != null) {

			boolean isImposed = false;

			int i;

			for (i = modelConstraintsToEvaluate[pruningEvent] - 1; i >= 0; i--)
				if (pruningEventConstraints[i] == C) {
					isImposed = true;
					break;
				}

			// int pruningConstraintsToEvaluate =
			// modelConstraintsToEvaluate[pruningEvent];

			if (isImposed) {

				if (i != modelConstraintsToEvaluate[pruningEvent] - 1) {

					modelConstraints[pruningEvent][i] = modelConstraints[pruningEvent][modelConstraintsToEvaluate[pruningEvent] - 1];

					modelConstraints[pruningEvent][modelConstraintsToEvaluate[pruningEvent] - 1] = C;
				}

				int[] newModelConstraintsToEvaluate = new int[3];

				newModelConstraintsToEvaluate[0] = modelConstraintsToEvaluate[0];
				newModelConstraintsToEvaluate[1] = modelConstraintsToEvaluate[1];
				newModelConstraintsToEvaluate[2] = modelConstraintsToEvaluate[2];

				newModelConstraintsToEvaluate[pruningEvent]--;

				modelConstraintsToEvaluate = newModelConstraintsToEvaluate;

				return;

			}

		}

		pruningEvent = JaCoP.core.Constants.BOUND;

		pruningEventConstraints = modelConstraints[pruningEvent];

		if (pruningEventConstraints != null) {

			boolean isImposed = false;

			int i;

			for (i = modelConstraintsToEvaluate[pruningEvent] - 1; i >= 0; i--)
				if (pruningEventConstraints[i] == C) {
					isImposed = true;
					break;
				}

			if (isImposed) {

				if (i != modelConstraintsToEvaluate[pruningEvent] - 1) {

					modelConstraints[pruningEvent][i] = modelConstraints[pruningEvent][modelConstraintsToEvaluate[pruningEvent] - 1];

					modelConstraints[pruningEvent][modelConstraintsToEvaluate[pruningEvent] - 1] = C;
				}

				int[] newModelConstraintsToEvaluate = new int[3];

				newModelConstraintsToEvaluate[0] = modelConstraintsToEvaluate[0];
				newModelConstraintsToEvaluate[1] = modelConstraintsToEvaluate[1];
				newModelConstraintsToEvaluate[2] = modelConstraintsToEvaluate[2];

				newModelConstraintsToEvaluate[pruningEvent]--;

				modelConstraintsToEvaluate = newModelConstraintsToEvaluate;

				return;

			}

		}

		pruningEvent = JaCoP.core.Constants.ANY;

		pruningEventConstraints = modelConstraints[pruningEvent];

		if (pruningEventConstraints != null) {

			boolean isImposed = false;

			int i;

			for (i = modelConstraintsToEvaluate[pruningEvent] - 1; i >= 0; i--)
				if (pruningEventConstraints[i] == C) {
					isImposed = true;
					break;
				}

			// int pruningConstraintsToEvaluate =
			// modelConstraintsToEvaluate[pruningEvent];

			if (isImposed) {

				if (i != modelConstraintsToEvaluate[pruningEvent] - 1) {

					modelConstraints[pruningEvent][i] = modelConstraints[pruningEvent][modelConstraintsToEvaluate[pruningEvent] - 1];

					modelConstraints[pruningEvent][modelConstraintsToEvaluate[pruningEvent] - 1] = C;
				}

				int[] newModelConstraintsToEvaluate = new int[3];

				newModelConstraintsToEvaluate[0] = modelConstraintsToEvaluate[0];
				newModelConstraintsToEvaluate[1] = modelConstraintsToEvaluate[1];
				newModelConstraintsToEvaluate[2] = modelConstraintsToEvaluate[2];

				newModelConstraintsToEvaluate[pruningEvent]--;

				modelConstraintsToEvaluate = newModelConstraintsToEvaluate;

			}

		}

	}
	//returns the largest element in the ground set lub\glb.
	/**
	 * This function is not used for setDomain.
	 */
	@Override
	public int rightElement(int intervalNo) {
		throw new JaCoPException("This function is not used for setDomain.");
		//		assert (intervalNo == 0);
		//		return max();
	}

	/**
	 * It sets the domain to the specified domain.
	 * @param domain the domain from which this domain takes all elements.
	 */
	@Override
	public void setDomain(Domain domain) {

		if (domain.domainID() == SetDomainID) {
			SetDomain sd = (SetDomain) domain;
			assert sd.checkInvariants() == null : sd.checkInvariants() ;
			this.glb = sd.glb;
			this.lub = sd.lub;
			return;
		}

		setDomain(domain.min(), domain.max());

	}

	/**
	 * It sets the domain to the the set {min..max}.
	 */
	@Override
	public void setDomain(int min, int max) {
		assert (min <= max);
		Set s = new Set(min,max);
		this.glb = s;
		this.lub = s;	
	}

	/**
	 * It returns true if given domain has only one set-element.
	 * @return true if the domain contains only one set-element.
	 */
	@Override
	public boolean singleton() {
		return (lub.eq(glb));
	}

	/**
	 * It returns true if given domain has only one set-element and this set-element only contains c.
	 * @return true if the domain contains only one set-element and this set-element only contains c.
	 */	
	@Override
	public boolean singleton(int c) {
		throw new JaCoPException("This function is not used for setDomain.");
	}


	/**
	 * It returns all constraints which are associated with variable, even the
	 * ones which are already satisfied.
	 * @return the number of constraints attached to the original domain of the variable associated with this domain.
	 */
	@Override
	public int sizeConstraintsOriginal() {

		Domain domain = this;

		while (domain.domainID() == SetDomainID) {

			SetDomain dom = (SetDomain) domain;

			if (dom.previousDomain != null)
				domain = dom.previousDomain;
			else
				break;
		}

		if (domain.domainID() == SetDomainID)
			return (domain.modelConstraintsToEvaluate[0]
			                                          + domain.modelConstraintsToEvaluate[1] + domain.modelConstraintsToEvaluate[2]);
		else
			return domain.sizeConstraintsOriginal();		

	}

	/**
	 * It subtracts domain from current domain and returns the result.
	 * @param domain the domain which is subtracted from this domain.
	 * @return the result of the subtraction.
	 */
	@Override
	public Domain subtract(Domain domain) {
		if (domain.domainID() == SetDomainID) {
			SetDomain sd = (SetDomain) domain;
			assert sd.checkInvariants() == null : sd.checkInvariants() ;
			Set glb_s = glb.subtract(sd.lub);
			Set lub_s = lub.subtract(sd.glb);
			return new SetDomain(glb_s,lub_s);
		}
		throw new JaCoPException("Domain not supported.");
	}

	/**
	 * It subtracts the set {min..max}.
	 * @param min the left bound of the set.
	 * @param max the right bound of the set.
	 * @return the result of the subtraction.
	 */
	@Override
	public SetDomain subtract(int min, int max) {
		Set lub_s = lub.subtract(min, max);
		Set glb_s = glb.subtract(min, max);
		return new SetDomain(glb_s,lub_s);
	}

	/**
	 * It subtracts the set {value}.
	 * @return the result of the subtraction.
	 */
	@Override
	public Domain subtract(int value) {
		SetDomain sd = this.cloneLight();
		sd.lub.subtract(value);
		sd.glb.subtract(value);
		return sd;
	}	


	/**
	 * It returns string description of the domain.
	 */
	@Override
	public String toString() {
		assert checkInvariants() == null : checkInvariants() ;
		if(this.glb.eq(this.lub))
			return glb.toString() ;
		else
			return "{" + glb.toString() + ".." + lub.toString() + "}";
	}

	/**
	 * It returns string description of the constraints attached to the domain.
	 * @return the string description.
	 */
	@Override
	public String toStringConstraints() {

		StringBuffer S = new StringBuffer("");

		for (Iterator<Constraint> e = searchConstraints.iterator(); e.hasNext();) {
			S.append(e.next().id());
			if (e.hasNext())
				S.append(", ");
		}

		return S.toString();		

	}

	/**
	 * not implemented.
	 */
	@Override
	public String toStringFull() {
		throw new JaCoPException("This function is not used for setDomain.");
		/*
		StringBuffer S = new StringBuffer("");

		Domain domain = this;

		do {
			if (!domain.singleton()) {
				S.append(toString()).append("(").append(domain.stamp()).append(") ");
			} else
				S.append(min).append("(").append(
						String.valueOf(domain.stamp())).append(") ");

			S.append("constraints: ");

			for (Iterator<Constraint> e = domain.searchConstraints.iterator(); e
					.hasNext();)
				S.append(e.next());

			if (domain.domainID() == IntervalDomainID) {

				IntervalDomain dom = (IntervalDomain) domain;
				domain = dom.previousDomain;

			} 
			else if (domain.domainID() == BoundDomainID) {

				BoundDomain dom = (BoundDomain) domain;
				domain = dom.previousDomain;

				}
			else {

				// Other type.
			}

		} while (domain != null);

		return S.toString();
		 */
	}

	/**
	 * not implemented.
	 */
	@Override
	public void fromXML(org.jdom.Element domainElement) {
		throw new JaCoPException("Function not implemented.");
	}

	/**
	 * not implemented.
	 */
	@Override
	public Element toXML() {
		throw new JaCoPException("Function not implemented.");
	}

	/**
	 * It computes union of the supplied domain with this domain.
	 * @param domain the domain for which the union is computed.
	 * @return the union of this domain with the supplied one.
	 */
	@Override
	public Domain union(Domain domain) {
		if (domain.domainID() == SetDomainID) {
			SetDomain sd = (SetDomain) domain;
			assert sd.checkInvariants() == null : sd.checkInvariants() ;

			Set glb_s = glb.intersect(sd.glb);
			Set lub_s = lub.union(sd.lub);
			return new SetDomain(glb_s,lub_s);
		}
		throw new JaCoPException("Domain not supported.");
	}

	/**
	 * It computes union of this domain and the interval.
	 * @param min the left bound of the interval (inclusive).
	 * @param max the right bound of the interval (inclusive).
	 * @return the union of this domain and the interval.
	 */

	@Override
	public Domain union(int min, int max) {
		assert max > min;
		Set glb_s = glb.union(min, max);
		Set lub_s = lub.union(min, max);
		return new SetDomain(glb_s,lub_s);
	}

	/**
	 * It computes union of this domain and value. 
	 * 
	 * @param value it specifies the value which is being added.
	 * @return domain which is a union of this one and the value.
	 */
	@Override
	public Domain union(int value) {
		Set glb_s = glb.union(value);
		Set lub_s = lub.union(value);
		return new SetDomain(glb_s,lub_s);
	}

	/**
	 * It returns value enumeration of the domain values.
	 * @return valueEnumeration which can be used to enumerate the sets of this domain one by one.
	 */
	@Override
	public ValueEnumeration valueEnumeration() {
		return new SetDomainValueEnumeration(this);
	}

	//Returns the previous integer from the ground set lub\glb.
	/**
	 * This function is not implemented.
	 */
	@Override
	public int previousValue(int value) {
		throw new JaCoPException("This function is not used for setDomain.");
		//		return this.lub.subtract(glb).previousValue(value);
	}

	/**
	 * @return It returns the information about the first invariant which does not hold or null otherwise. 
	 */
	public String checkInvariants() {
		if(!lub.contains(glb))
			return "Greatest lower bound is larger than least upper bound ";

		//Fine, all invariants hold.
		return null;

	}

}

