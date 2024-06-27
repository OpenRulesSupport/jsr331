/**
 *  BoundDomain.java 
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

package JaCoP.core;

import java.util.ArrayList;
import java.util.Iterator;

import org.jdom.Element;

import JaCoP.constraints.Constraint;

/**
 * Defines interval of numbers which is part of FDV definition which consist of
 * one or several intervals.
 * 
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class BoundDomain extends Domain {

	
	/**
	 * The minimal value of the domain.
	 */

	public int min;
	
	/**
	 * The maximal value of the domain.
	 */
	
	public int max;

	/**
	 * It specifies the previous domain which was used by this domain. The old
	 * domain is stored here and can be easily restored if neccessary.
	 */

	public Domain previousDomain;	
	
	
	/**
	 * It predefines empty domain so there is no need to constantly create it when
	 * needed.
	 */
	static public BoundDomain emptyDomain = new BoundDomain();
	
	
	/**
	 * It is a constructor which will create an empty Bound domain. An empty domain
	 * has minimum larger than maximum.
	 */
	public BoundDomain() {
		min = 1;
		max = -1;
	}
	
	/** Creates a new instance of BoundDomain. It requires min to be smaller
	 * or equal to max.
	 * @param min it specifies the left bound of the BoundDomain (inclusive).  
	 * @param max it specifies the right bound of the BoundDomain (inclusive).
	 */
	public BoundDomain(int min, int max) {
		
		assert min <= max;
		
		this.min = min;
		this.max = max;
		
		searchConstraints = null;
		searchConstraintsToEvaluate = 0;
		previousDomain = null;
		searchConstraintsCloned = false;
	
	}


	/**
	 * @param i  
	 * 
	 */
	@Override
	public void addDom(Interval i) {
		
		if (min < max) {
			if (i.min < min)
				min = i.min;
		
			if (i.max > max)
				max = i.max;
		}
		else {
			
			min = i.min;
			max = i.max;
		}
		
	}

	/**
	 * 
	 */
	@Override
	public void addDom(Domain domain) {
		
		if (min < max) {
			if (domain.min() < min)
				min = domain.min();
		
			if (domain.max() > max)
				max = domain.max();
		}
		else {
			
			min = domain.min();
			max = domain.max();
			
		}
	}

	@Override
	public void addDom(int min, int max) {

		if (this.min < this.max) {
			if (this.min < min)
				this.min = min;
		
			if (this.max > max)
				this.max = max;
		}
		else {
			this.min = min;
			this.max = max;
		}
	}

	@Override
	public void clear() {
		min = 1;
		max = -1;
	}

	@Override
	public BoundDomain clone() {
		BoundDomain cloned = new BoundDomain(min, max);

		cloned.stamp = stamp;
		cloned.previousDomain = previousDomain;

		cloned.searchConstraints = searchConstraints;
		cloned.searchConstraintsToEvaluate = searchConstraintsToEvaluate;

		cloned.modelConstraints = modelConstraints;
		cloned.modelConstraintsToEvaluate = modelConstraintsToEvaluate;

		cloned.searchConstraintsCloned = searchConstraintsCloned;

		return cloned;	
	}

	@Override
	public BoundDomain cloneLight() {
		return new BoundDomain(min, max);
	}

	@Override
	public Domain complement() {
		
		if (min == Constants.MinInt) {
			
			if (max == Constants.MaxInt)
				return new BoundDomain();
			
			return new BoundDomain(max+1, Constants.MaxInt);
		}
		
		if (max == Constants.MaxInt) {
			
			return new BoundDomain(Constants.MinInt, min-1);
			
		}
		
		IntervalDomain complement = new IntervalDomain();
		complement.addDom(Constants.MinInt, min-1);
		complement.addDom(max+1, Constants.MaxInt);
		
		return complement;
	}

	@Override
	public boolean contains(Domain domain) {
		
		if ( min <= domain.min() && max >= domain.max())
			return true;
		return false;
	}

	@Override
	public boolean contains(int value) {
		if (min <= value && max >= value)
			return true;
		return false;
	}

	/**
	 * It divides the domain by a given constant.
	 * @param div the constant by which the domain should be divided.
	 * @return the domain obtained by dividing this domain by a given constant.
	 */
	public Domain divide(int div) {
		return new BoundDomain(div(min, div), max / div);
	}

	private int div(int A, int B) {
		int Div, Rem;

		Div = A / B;
		Rem = A % B;
		return (Rem > 0) ? Div + 1 : Div;
	}
	
	@Override
	public int domainID() {
		return BoundDomainID;
	}

	@Override
	public boolean eq(Domain domain) {
		
		if (min == domain.min() && max == domain.max() && 
			(max - min + 1) == domain.getSize())
			return true;
		
		return false;
	}

	@Override
	public Interval getInterval(int position) {
		if (position == 0)
			return new Interval(min, max);

		return null;
	}

	@Override
	public int getSize() {
		return max - min + 1;
	}

	@Override
	public void in(int storeLevel, Variable var, int min, int max) {

		assert (min <= max);

		if (this.max < min || this.min > max)
			throw failException;

		if (min <= this.min && max >= this.max)
			return;
		
		if (stamp == storeLevel) {

			if (this.min < min )
				this.min = min;
			
			if (this.max > max)
				this.max = max;

			if (this.min == this.max) 
				var.domainHasChanged(GROUND);
			else
				var.domainHasChanged(BOUND);
		
			return;
			
		} else {

			assert stamp < storeLevel;
			
			BoundDomain result;
			
			if (this.min < min )
				if (this.max > max) 
					result = new BoundDomain(min, max);
				else
					result = new BoundDomain(min, this.max);
			else
				// case this.min, this.max means no change which is handled above.
				result = new BoundDomain(this.min, max);
			
			
			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;
			
			if (result.singleton()) {
				var.domainHasChanged(GROUND);
				return;
			} else {
				var.domainHasChanged(BOUND);
				return;
			}

		}
		
	}

	@Override
	public void in(int storeLevel, Variable var, Domain domain) {

		in(storeLevel, var, domain.min(), domain.max());

	}

	@Override
	public void inComplement(int storeLevel, Variable var, int complement) {

		if (this.max == this.min && this.max == complement)
			throw failException;

		// Can not be removed without changing the code below.
		if (complement != this.min && complement != this.max)
			return;
		
		if (stamp == storeLevel) {

			if (this.min == complement )
				this.min++;
			// Assumes that check that complement must be equal to one of the bounds is 
			// done above.
			else
				this.max--;

			if (this.min == this.max) 
				var.domainHasChanged(GROUND);
			else
				var.domainHasChanged(BOUND);
		
			return;
			
		} else {

			assert stamp < storeLevel;
			
			BoundDomain result;
			
			if (this.min == complement )
				result = new BoundDomain(this.min+1, this.max);
			else
				result = new BoundDomain(this.min, this.max-1);			
			
			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;
			
			if (result.singleton()) {
				var.domainHasChanged(GROUND);
				return;
			} else {
				var.domainHasChanged(BOUND);
				return;
			}

		}		
		
	}

	@Override
	public void inComplement(int storeLevel, Variable var, int min, int max) {
		
		assert (min <= max);
		
		// all elements are removed so fail.
		if (this.min >= min && this.max <= max)
			throw failException;

		// Can not be removed without changing the code below.
		// none of the elements are removed can ignore the call.
		if (max < this.min || this.max < min)
			return;
		
		// For bound domain, creating holes in the domain not possible.
		if (min > this.min && max < this.max)
			return;
		
		if (stamp == storeLevel) {

			if (max < this.max)
				this.min = max + 1;
			else 
				this.max = min - 1;
			
			if (this.min == this.max) 
				var.domainHasChanged(GROUND);
			else
				var.domainHasChanged(BOUND);
		
			return;
			
		} else {

			assert stamp < storeLevel;
			
			BoundDomain result;
			
			if (max < this.max)
				result = new BoundDomain(max + 1, this.max);
			else 
				result = new BoundDomain(this.min, min - 1);
						
			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;
			
			if (result.singleton()) {
				var.domainHasChanged(GROUND);
				return;
			} else {
				var.domainHasChanged(BOUND);
				return;
			}

		}		
		
	}

	@Override
	public void inMax(int storeLevel, Variable var, int max) {

		if (this.min > max)
			throw failException;

		//If removed the code below has to change.
		if (max >= this.max)
			return;
		
		if (stamp == storeLevel) {
			
			this.max = max;

			if (this.min == this.max) 
				var.domainHasChanged(GROUND);
			else
				var.domainHasChanged(BOUND);
		
			return;
			
		} else {

			assert stamp < storeLevel;
			
			BoundDomain result = new BoundDomain(min, max);						
			
			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;
			
			if (result.singleton()) {
				var.domainHasChanged(GROUND);
				return;
			} else {
				var.domainHasChanged(BOUND);
				return;
			}

		}		

	}

	@Override
	public void inMin(int storeLevel, Variable var, int min) {
		
		if (this.max < min)
			throw failException;

		if (min <= this.min)
			return;
		
		if (stamp == storeLevel) {

			this.min = min;
			
			if (this.min == this.max) 
				var.domainHasChanged(GROUND);
			else
				var.domainHasChanged(BOUND);
		
			return;
			
		} else {

			assert stamp < storeLevel;
			
			BoundDomain result = new BoundDomain(min, this.max);			
			
			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;
			
			if (result.singleton()) {
				var.domainHasChanged(GROUND);
				return;
			} else {
				var.domainHasChanged(BOUND);
				return;
			}

		}		
	}

	@Override
	public void inShift(int storeLevel, Variable var, Domain domain, int shift) {
		in(storeLevel, var, domain.min()+shift, domain.max()+shift);
	}

	@Override
	public Domain intersect(Domain dom) {
		
		int inputMin = dom.min();
		int inputMax = dom.max();
		
		if (inputMin > this.max || inputMax < this.min)
			return emptyDomain;
		
		if (inputMin >= this.min) // inputMin..
			if (inputMax <= this.max) // inputMin..inputMax
				return new BoundDomain(inputMin, inputMax);
			else // inputMin..max
				return new BoundDomain(inputMin, this.max);
		else // min..
			if (inputMax <= this.max) // min..inputMax
				return new BoundDomain(this.min, inputMax);
			else // min..max
				return new BoundDomain(this.min, this.max);
	}

	@Override
	public Domain intersect(int min, int max) {

		if (min > this.max || max < this.min)
			return emptyDomain;
		
		if (min >= this.min) // inputMin..
			if (max <= this.max) // inputMin..inputMax
				return new BoundDomain(min, max);
			else // inputMin..max
				return new BoundDomain(min, this.max);
		else // min..
			if (max <= this.max) // min..inputMax
				return new BoundDomain(this.min, max);
			else // min..max
				return new BoundDomain(this.min, this.max);
	
	
	}

	@Override
	public Domain subtract(int value) {
		
		if (this.max == this.min && this.max == value)
			return emptyDomain;

		// Can not be removed without changing the code below.
		if (value != this.min && value != this.max)
			return new BoundDomain(this.min, this.max);
		
		if (this.min == value )
			return new BoundDomain(this.min + 1, this.max);
		else
			return new BoundDomain(this.min, this.max - 1);
		
	}

	@Override
	public IntervalEnumeration intervalEnumeration() {
		return new BoundDomainIntervalEnumeration(this.min, this.max);
	}

	@Override
	public boolean isEmpty() {
		return min > max;
	}

	@Override
	public boolean isIntersecting(Domain domain) {
				
		if (domain.min() > this.max || domain.max() < this.min)
			return false;
		
		return true;
		
	}

	@Override
	public boolean isIntersecting(int min, int max) {
		
		if (min > this.max || max < this.min)
			return false;
		
		return true;		

	}

	@Override
	public boolean isNumeric() {
		return	true;
	}

	@Override
	public boolean isSparseRepresentation() {
		return false;
	}

	@Override
	public int leftElement(int intervalNo) {
		assert intervalNo == 0;
		return this.min; 
	}

	@Override
	public int max() {
		return this.max;
	}

	@Override
	public int min() {
		return this.min;
	}

	/**
	 * It multiplies the domain by a given constant.
	 * @param mul a factor by which the domain is being multiplied.
	 * @return Domain created by multiplication of this domain.
	 */
	public Domain multiply(int mul) {
		return new BoundDomain(this.min*mul, this.max*mul);
	}

	@Override
	public int nextValue(int value) {
		if (value < this.min)
			return min;
		if (value < this.max)
			return value + 1;
		
		return value;
	}

	@Override
	public int noIntervals() {
		return 1;
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

			BoundDomain result = this.cloneLight();

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

				BoundDomain result = this.cloneLight();

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

	@Override
	public Domain recentDomainPruning(int storeLevel) {
		if (previousDomain == null)
			return emptyDomain;

		if (stamp < storeLevel)
			return emptyDomain;

		return previousDomain.subtract(this);		
	}

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

			BoundDomain result = this.cloneLight();

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

			BoundDomain result = this.cloneLight();

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

		int pruningEvent = GROUND;

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

		pruningEvent = BOUND;

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

		pruningEvent = ANY;

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
	
	@Override
	public int rightElement(int intervalNo) {
		assert (intervalNo == 0);
		return max;
	}

	@Override
	public void setDomain(Domain domain) {
		
		if (domain.domainID() == BoundDomainID) {

			BoundDomain boundDomain = (BoundDomain) domain;

			this.min = boundDomain.min();
			this.max = boundDomain.max();
			
			return;
		}

		setDomain(domain.min(), domain.max());

	}

	@Override
	public void setDomain(int min, int max) {
		
		assert (min <= max);
		
		this.min = min;
		this.max = max;
		
	}

	@Override
	public boolean singleton() {
		return (min==max);
	}

	@Override
	public boolean singleton(int c) {
		return (min == c && max == c);
	}

	@Override
	public int sizeConstraintsOriginal() {
		
		Domain domain = this;

		while (domain.domainID() == BoundDomainID) {

			BoundDomain dom = (BoundDomain) domain;

			if (dom.previousDomain != null)
				domain = dom.previousDomain;
			else
				break;
		}

		if (domain.domainID() == BoundDomainID)
			return (domain.modelConstraintsToEvaluate[0]
					+ domain.modelConstraintsToEvaluate[1] + domain.modelConstraintsToEvaluate[2]);
		else
			return domain.sizeConstraintsOriginal();		
		
	}

	@Override
	public Domain subtract(Domain domain) {
		
		int inputMin = domain.min();
		int inputMax = domain.max();
		
		if (inputMin <= this.min && inputMax >= this.max)
			return emptyDomain;

		if (this.min < inputMin && inputMax < this.max)
			return new BoundDomain(this.min, this.max);
		
		if (inputMin > this.min)
			return new BoundDomain(this.min, inputMin - 1);
		
		if (inputMax < this.max)
			return new BoundDomain(inputMax + 1, this.max);

		assert false;
		return null;
	}

	
	@Override
	public BoundDomain subtract(int min, int max) {
		
		if (min <= this.min && max >= this.max)
			return emptyDomain;

		if (this.min < min && max < this.max)
			return new BoundDomain(this.min, this.max);
		
		if (min > this.min)
			return new BoundDomain(this.min, min - 1);
		
		if (max < this.max)
			return new BoundDomain(max + 1, this.max);

		assert false;
		return null;		

	}

	@Override
	public String toString() {
		
		if (min < max)
			return "{" + min + ".." + max + "}";
		else if ( min == max)
			return String.valueOf(min);
		else
			return "{}";
		
	}

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

	@Override
	public String toStringFull() {
		
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
		
	}

	@Override
	public void fromXML(org.jdom.Element domainElement) {
		
		min = Integer.valueOf( domainElement.getAttributeValue("min") );
		max = Integer.valueOf( domainElement.getAttributeValue("max") );

	}
	
	@Override
	public Element toXML() {
		
		org.jdom.Element domain = new org.jdom.Element("JaCoP.core.BoundDomain");

		domain.setAttribute("min", String.valueOf(min));
		domain.setAttribute("max", String.valueOf(max));
		
		if (modelConstraints != null) {
			if (modelConstraints[GROUND] != null)
				for (int i = 0; i < modelConstraintsToEvaluate[GROUND]; i++)
					if (modelConstraints[GROUND][i] != null) {

						org.jdom.Element constraint = new org.jdom.Element(
						"constraint");

						constraint.setAttribute("id", modelConstraints[GROUND][i]
						                                                       .id());
						constraint.setAttribute("event", "ground");
						domain.addContent(constraint);
					}

			if (modelConstraints[BOUND] != null)
				for (int i = 0; i < modelConstraintsToEvaluate[BOUND]; i++)
					if (modelConstraints[BOUND][i] != null) {

						org.jdom.Element constraint = new org.jdom.Element(
						"constraint");
						constraint.setAttribute("id", modelConstraints[BOUND][i]
						                                                      .id());
						constraint.setAttribute("event", "bound");
						domain.addContent(constraint);
					}

			if (modelConstraints[ANY] != null)
				for (int i = 0; i < modelConstraintsToEvaluate[ANY]; i++)
					if (modelConstraints[ANY][i] != null) {

						org.jdom.Element constraint = new org.jdom.Element(
						"constraint");
						constraint
						.setAttribute("id", modelConstraints[ANY][i].id());
						constraint.setAttribute("event", "any");
						domain.addContent(constraint);
					}

		}
		
		for (int i = 0; i < searchConstraintsToEvaluate; i++) {

			org.jdom.Element constraint = new org.jdom.Element("constraint");
			constraint.setAttribute("id", searchConstraints.get(i).id());
			domain.addContent(constraint);

		}

		domain.setAttribute("stamp", String.valueOf(stamp));

		if (previousDomain != null)
			domain.addContent(previousDomain.toXML());

		return domain;		
	}

	@Override
	public Domain union(Domain domain) {
		
		int min = domain.min();
		int max = domain.max();
		
		if (min < this.min) // min..
			if (this.max < max) // min..max
				return new BoundDomain(min, max);
			else // min..this.max
				return new BoundDomain(min, this.max);
		else // this.min..
			if (this.max < max) // this.min..max
				return new BoundDomain(this.min, max);
			else // this.min..this.max
				return new BoundDomain(this.min, this.max);

	}

	@Override
	public Domain union(int min, int max) {
		
		if (min < this.min) // min..
			if (this.max < max) // min..max
				return new BoundDomain(min, max);
			else // min..this.max
				return new BoundDomain(min, this.max);
		else // this.min..
			if (this.max < max) // this.min..max
				return new BoundDomain(this.min, max);
			else // this.min..this.max
				return new BoundDomain(this.min, this.max);		
	}

	@Override
	public Domain union(int value) {
		
		if (value < this.min)
			return new BoundDomain(value, this.max);
		
		if (value > this.max)
			return new BoundDomain(this.min, value);
		
		return new BoundDomain(this.min, this.max);
	}

	@Override
	public ValueEnumeration valueEnumeration() {
		return new BoundDomainValueEnumeration(this);
	}

	@Override
	public int previousValue(int value) {
		
		if (value > this.min)
			return value-1;
		
		return value;
	}
	
	/**
	 * @return It returns the information about the first invariant which does not hold or null otherwise. 
	 */
	public String checkInvariants() {
		
		if (this.min > this.max)
			return "Min value is larger than max value ";
		
		//Fine, all invariants hold.
		return null;
		
	}

}
