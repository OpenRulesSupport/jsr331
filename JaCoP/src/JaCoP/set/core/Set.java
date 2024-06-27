/**
 *  Set.java 
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

import JaCoP.core.Constants;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Interval;
import JaCoP.core.JaCoPException;
import JaCoP.core.ValueEnumeration;

/**
 * Defines Set which consist of zero, one or several intervals.
 * 
 * 
 * @author Krzysztof Kuchcinski, Radoslaw Szymanek and Robert Åkemalm
 * @version 2.4
 */

public class Set extends IntervalDomain {

	/**
	 * The unique ID of the set used for fast reference/differentiation between different domain classes.
	 */
	public static final int SetID = 2;


	/**
	 * It creates an empty Set domain.
	 */
	public Set() {
	}

	/**
	 * An empty domain, so no constant creation of empty domains is required.
	 */

	static public Set emptySet = new Set();

	/**
	 * It creates an empty set, with at least specified number of places in
	 * an array list for intervals.
	 * 
	 * @param size defines the initial size of an array storing the intervals.
	 */
	public Set(int size) {
		intervals = new Interval[size];
		this.size = 0;
		searchConstraints = null;
		searchConstraintsToEvaluate = 0;
		previousDomain = null;
		searchConstraintsCloned = false;
	}

	/**
	 * It creates a set with all values between min and max.
	 * 
	 * @param min defines the left bound of a set.
	 * @param max defines the right bound of a set.
	 */

	public Set(int min, int max) {
		intervals = new Interval[5];
		searchConstraints = null;
		searchConstraintsToEvaluate = 0;
		previousDomain = null;
		searchConstraintsCloned = false;
		intervals[0] = new Interval(min, max);
		this.size = 1;
	}	

	/**
	 * Adds the intervals from another set to this set
	 * @param set defines a set that should be merged to this set
	 */
	public void addDom(Set set){
	    assert checkInvariants() == null : checkInvariants() ;
	    assert set.checkInvariants() == null : set.checkInvariants() ;

		Set extended = this.union(set);
		this.intervals = extended.intervals;
		this.size = extended.size;

		assert checkInvariants() == null : checkInvariants() ;
	}

	/**
	 * Adds an integer to the Set
	 * @param i defines the integer that should be included to the Set.
	 */
	public void addDom(int i){
		if(!this.contains(i)){
			this.addDom(new Set(i,i));
		}
	}

	/**
	 * Adds one interval to the set
	 * @param i defines the interval to add to the set
	 */
	@Override
	public void addDom(Interval i){
		this.addDom(new Set(i.min, i.max));
	}

	/**
	 * Adds a new interval to the set
	 * @param min defines the min-value for the interval to add
	 * @param max defines the max-value for the interval to add
	 */
	@Override
	public void addDom(int min, int max){
		this.addDom(new Set(min,max));
	}


	/**
	 * It checks whether this set intersects with the one supplied as an argument.
	 * @param set the set for which we check if it intersects with this set.
	 * @return true if this set intersects with set given as a parameter, false otherwise.
	 */
	public boolean isIntersecting(Set set) {
		return (!this.intersect(set).isEmpty());
	}

	/**
	 * It returns the minimum value from the set.
	 * If the Set is empty it throws a JaCoPException.
	 */

	@Override
	public int min() {

		if(size == 0){
			throw new JaCoPException("The set is empty, no value for min()");
		}
		assert checkInvariants() == null : checkInvariants() ;

		return intervals[0].min;

	}

	/**
	 * It computes the cardinality of the set. 
	 * 
	 * @return the cardinality of the set.
	 */
	public int card(){
		int card = 0;
		for(int i = 0 ; i < size ; i++){
			card += intervals[i].max - intervals[i].min + 1;
		}
		return card;
	}
	/**
	 * It clones the set.
	 */

	@Override
	public Set cloneLight() {
		if(this.isEmpty()){
			return new Set();
		}
		Set cloned = new Set();
		cloned.intervals = new Interval[this.intervals.length];
		System.arraycopy(intervals, 0, cloned.intervals, 0, size);
		cloned.size = size;
		return cloned;
	}

	/**
	 * Returns a new set with all elements shifted shift steps. It will not do anything for an empty set.
	 * @param shift the size of the shift step. 
	 * @return A new set shifted shift steps.
	 */

	public Set shift(int shift) {
		if(this.isEmpty()){
			return new Set();
		}
		Set cloned = new Set();
		cloned.intervals = new Interval[this.intervals.length];
		for(int i = 0 ; i < this.intervals.length ; i++){
			cloned.intervals[i] = new Interval(this.intervals[i].min()+shift,this.intervals[i].max()+shift);
		}
		cloned.size = size;
		return cloned;
	}

	/**
	 * Checks if the argument 'set' is a subset of this set.
	 * @param set
	 * @return true if this set contains the argument set else false.
	 */
	public boolean contains(Set set) {
		if(set.isEmpty())
			return true;
		if(this.isEmpty())
			return false;
		int i1 = 0;
		int i2 = 0;
		int thisMax,setMin;
		int setSize = set.size;
		while(true){
			thisMax = this.intervals[i1].max;
			setMin = set.intervals[i2].min;
			while (setMin > thisMax) {
				i1++;
				if (i1 == this.size)
					return false;
				thisMax = this.intervals[i1].max;
			}
			if (setMin < this.intervals[i1].min || set.intervals[i2].max > thisMax)
				return false;
			i2++;
			if (i2 == setSize)
				return true;
		}
	}

	/**
	 * Returns a set that is the complement of this set.
	 */
	public Set complement() {

		if (this.size == 0)
			return new Set(Constants.MinInt, Constants.MaxInt);

		assert checkInvariants() == null : checkInvariants() ;

		Set result = new Set(this.size + 1);
		if (min() != Constants.MinInt)
			result.addDom(new Interval(Constants.MinInt, intervals[0].min - 1));

		for (int i = 0; i < this.size - 1; i++)
			result.addDom(new Interval(intervals[i].max + 1, intervals[i + 1].min - 1));

		if (max() != Constants.MaxInt)
			result.addDom(new Interval(max() + 1, Constants.MaxInt));

		assert result.checkInvariants() == null : result.checkInvariants() ;

		return result;
	}

	/**
	 * It checks if the set is equal to the supplied set.
	 * 
	 * @param set the set for which equality with this set is checked.
	 * @return true if this set is equal to set provided as a parameter, false otherwise.
	 */
	public boolean eq(Set set) {

		assert checkInvariants() == null : checkInvariants() ;
		assert set.checkInvariants() == null : set.checkInvariants() ;

		boolean equal = true;
		int i = 0;

		if (this.size == set.size) {
			while (equal && i < this.size) {
				equal = this.intervals[i].eq(set.intervals[i]);
				i++;
			}
		} else
			equal = false;
		return equal;
	}

	/**
	 * It intersects current set with the one given as a parameter.
	 * 
	 * @param input the set for which the intersection with this set is computed. 
	 * @return the result of the intersection.
	 */
	public Set intersect(Set input) {

		assert checkInvariants() == null : checkInvariants() ;
		assert input.checkInvariants() == null : input.checkInvariants() ;

		if (input.isEmpty() || this.isEmpty())
			return emptySet;

		Set temp;

		if (this.size > input.size)
			temp = new Set(this.size);
		else
			temp = new Set(input.size);

		int pointer1 = 0;
		int pointer2 = 0;
		int size1 = this.size;
		int size2 = input.size;

		if (size1 == 0 || size2 == 0)
			return temp;

		Interval interval1 = intervals[pointer1];
		Interval interval2 = input.intervals[pointer2];

		while (true) {
			if (interval1.max < interval2.min) {
				pointer1++;
				if (pointer1 < size1) {
					interval1 = intervals[pointer1];
					continue;
				} else
					break;
			} else if (interval2.max < interval1.min) {
				pointer2++;
				if (pointer2 < size2) {
					interval2 = input.intervals[pointer2];
					continue;
				} else
					break;
			} else
				if (interval1.min <= interval2.min) {

					if (interval1.max <= interval2.max) {

						temp.addDom(interval2.min, interval1.max);
						pointer1++;
						if (pointer1 < size1) {
							interval1 = intervals[pointer1];
							continue;
						} else
							break;
					} else {
						temp.addDom(interval2.min, interval2.max);
						pointer2++;
						if (pointer2 < size2) {
							interval2 = input.intervals[pointer2];
							continue;
						} else
							break;
					}

				} else
				{
					if (interval2.max <= interval1.max) {
						temp.addDom(interval1.min, interval2.max);
						pointer2++;
						if (pointer2 < size2) {
							interval2 = input.intervals[pointer2];
							continue;
						} else
							break;
					} else {
						temp.addDom(interval1.min, interval1.max);
						pointer1++;
						if (pointer1 < size1) {
							interval1 = intervals[pointer1];
							continue;
						} else
							break;
					}
				}
		}

		assert temp.checkInvariants() == null : temp.checkInvariants() ;

		return temp;

	}

	/**
	 * It intersects current set with the set {min..max} and returns the result.
	 */
	public Set intersect(int min, int max) {

		assert checkInvariants() == null : checkInvariants() ;

		if (size == 0)
			return this;

		Set temp = new Set(size);
		int pointer1 = 0;
		int pointer2 = 0;

		Interval interval1 = intervals[pointer1];

		while (true) {
			if (interval1.max < min) {
				pointer1++;
				if (pointer1 < size) {
					interval1 = intervals[pointer1];
					continue;
				} else
					break;
			} else if (max < interval1.min) {
				break;
			} else
				if (interval1.min <= min) {

					if (interval1.max <= max) {

						temp.addDom(new Interval(min, interval1.max));
						pointer1++;
						if (pointer1 < size) {
							interval1 = intervals[pointer1];
							continue;
						} else
							break;
					} else {
						temp.addDom(new Interval(min, max));
						break;
					}

				} else
				{
					if (max <= interval1.max) {
						temp.addDom(new Interval(interval1.min, max));
						pointer2++;
						break;
					} else {
						temp.addDom(new Interval(interval1.min, interval1.max));
						pointer1++;
						if (pointer1 < size) {
							interval1 = intervals[pointer1];
							continue;
						} else
							break;
					}

				}
		}

		assert temp.checkInvariants() == null : temp.checkInvariants() ;

		return temp;

	}

	/**
	 * Returns the lexical ordering between the sets
	 * @param s the Set that should be lexically compared to this set
	 * @return -1 if s is greater than this set, 0 if s is equal to this set and else it returns 1.
	 */
	public int lex(Set s){
		ValueEnumeration ve = this.valueEnumeration();
		ValueEnumeration veS = s.valueEnumeration();

		int i,j;
		while(ve.hasMoreElements()){
			i = ve.nextElement();
			if(veS.hasMoreElements()){
				j = veS.nextElement();
				if(i<j)
					return -1;
				else if(j<i)
					return 1;
			}else{
				return 1;
			}
		}
		if(veS.hasMoreElements())
			return -1;
		return 0;
	}


	/**
	 * Returns the set without the value element.
	 */

	public Set subtract(int value) {

		assert checkInvariants() == null : checkInvariants() ;

		Set result = cloneLight();

		int pointer1 = 0;

		if (size == 0)
			return result;

		Interval interval1 = intervals[pointer1];

		while (true) {

			if (interval1.max < value) {
				pointer1++;
				if (pointer1 < size) {
					interval1 = intervals[pointer1];
					continue;
				} else
					break;
			}

			if (interval1.min > value) {
				break;
			} else {

				if (interval1.min != value) {

					int oldMax = interval1.max;
					// replace min..max with interval1.min..value-1
					result.intervals[pointer1] = new Interval(interval1.min,
							value - 1);
					pointer1++;

					if (value != oldMax) {
						result.addDom(value + 1, oldMax);
						pointer1++;
					}

				} else if (interval1.max != value) {
					result.intervals[pointer1] = new Interval(value + 1,
							interval1.max);
					pointer1++;
				} else {
					result.removeInterval(pointer1);
				}

				break;

			}

		}

		assert result.checkInvariants() == null : result.checkInvariants() ;

		return result;

	}

	/**
	 * It subtracts {min..max} from current set and returns the result.
	 */
	public Set subtract(int min, int max) {

		assert checkInvariants() == null : checkInvariants() ;

		if(min > max)
			throw new JaCoPException("Min is greater than max.");
		assert (min <= max);

		if (size == 0)
			return emptySet;

		int i1 = 0;
		Interval currentInterval1 = intervals[i1];

		Set result = new Set(intervals.length+1);

		while (true) {

			if (currentInterval1.max < min) {
				result.addDom(intervals[i1]);
				i1++;
				if (i1 == size)
					break;
				currentInterval1 = intervals[i1];
				continue;
			}

			if (max < currentInterval1.min) {
				break;
			}

			if (currentInterval1.min >= min) {


				if (currentInterval1.max <= max) {
					i1++;
					if (i1 == size)
						break;
					currentInterval1 = intervals[i1];
					continue;

				} else {

					// interval of dom2 ends before interval of dom1 ends
					// currentDomain2.max+1 .. currentDomain1.max
					// BUT next currentdomain2.min needs to be larger than
					// currentDomain1.max

					result.addDom(new Interval(max + 1, currentInterval1.max));

					i1++;
					break;
				}
			}
			// currentDomain1.min < min
			// currentDomain1.max >= min
			// max >= currentDomain1.min
			else {

				if (currentInterval1.max <= max) {

					result.addDom(new Interval(currentInterval1.min, min - 1));

					i1++;

					if (i1 == size)
						break;
					currentInterval1 = intervals[i1];
					// next intervals of the domain may be before max.
					continue;

				} else {

					// interval of min..max ends before interval of dom1 ends
					// max+1 .. currentDomain1.max

					result.addDom(currentInterval1.min, min - 1);
					result.addDom(max + 1, currentInterval1.max);

					i1++;
					break;
				}

			}
		}

		for (int i = i1; i < size; i++)
			result.addDom(intervals[i]);

		assert result.checkInvariants() == null : result.checkInvariants() ;

		return result;
	}

	/**
	 * It subtracts set from current set and returns the result.
	 * 
	 * @param set the set which is being subtracted from this set. 
	 * @return the result of the subtraction.
	 */

	public Set subtract(Set set) {

		assert checkInvariants() == null : checkInvariants() ;

		if (isEmpty())
			return emptySet;

		if (set.size == 0)
			return cloneLight();

		Set result = new Set();

		result.intervals = new Interval[size + 1];

		int i1 = 0;
		int i2 = 0;

		Interval currentDomain1 = intervals[i1];
		Interval currentDomain2 = set.intervals[i2];

		boolean minIncluded = false;

		int max2 = set.size;

		while (true) {

			if (currentDomain1.max < currentDomain2.min) {
				result.addDom(currentDomain1);
				i1++;
				if (i1 == size)
					break;
				currentDomain1 = intervals[i1];
				minIncluded = false;
				continue;
			}

			if (currentDomain2.max < currentDomain1.min) {
				i2++;
				if (i2 == max2)
					break;
				currentDomain2 = set.intervals[i2];
				continue;
			}

			if (currentDomain1.min >= currentDomain2.min) {

				if (currentDomain1.max <= currentDomain2.max) {
					// Skip current interval of i1 completely
					i1++;
					if (i1 == size)
						break;
					currentDomain1 = intervals[i1];
					minIncluded = false;
					continue;
				} else {

					// interval of dom2 ends before interval of dom1 ends
					// currentDomain2.max+1 .. currentDomain1.max
					// BUT next currentdomain2.min needs to be larger than
					// currentDomain1.max

					int oldMax = currentDomain2.max;
					i2++;
					if (i2 != max2)
						currentDomain2 = set.intervals[i2];

					if (i2 == max2 || currentDomain2.min > currentDomain1.max) {
						result.addDom(new Interval(oldMax + 1,currentDomain1.max));
						i1++;
						if (i1 == size)
							break;
						currentDomain1 = intervals[i1];
						minIncluded = false;

						if (i2 == max2)
							break;
					} else {

						result.addDom(new Interval(oldMax + 1,currentDomain2.min - 1));
						minIncluded = true;
					}

				}

			}
			// currentDomain1.min < currentDomain2.min)
			else {

				if (currentDomain1.max <= currentDomain2.max) {

					if (!minIncluded)
						if (currentDomain1.max >= currentDomain2.min)
							result.addDom(new Interval(currentDomain1.min,currentDomain2.min - 1));
						else
							result.addDom(new Interval(currentDomain1.min,currentDomain1.max));

					i1++;
					if (i1 == size)
						break;
					currentDomain1 = intervals[i1];
					minIncluded = false;
				} else {

					// interval of dom2 ends before interval of dom1 ends
					// currentDomain2.max+1 .. currentDomain1.max
					// BUT next currentdomain2.min needs to be larger than
					// currentDomain1.max

					if (!minIncluded) {
						result.addDom(new Interval(currentDomain1.min,currentDomain2.min - 1));
						minIncluded = true;
					}

					int oldMax = currentDomain2.max;
					i2++;
					if (i2 != max2)
						currentDomain2 = set.intervals[i2];

					if (i2 == max2 || currentDomain2.min > currentDomain1.max) {
						result.addDom(new Interval(oldMax + 1,currentDomain1.max));
						i1++;
						if (i1 == size)
							break;
						currentDomain1 = intervals[i1];
						minIncluded = false;

						if (i2 == max2)
							break;
					} else {

						// i1++;
						// if (i1 == size)
						// break;

						result.addDom(new Interval(oldMax + 1,currentDomain2.min - 1));

					}

				}

			}

		}

		while (i1 < size) {
			result.addDom(intervals[i1]);
			i1++;
		}

		assert result.checkInvariants() == null : result.checkInvariants() ;

		return result;

	}

	/**
	 * returns this set with the element value added. 
	 */
	public Set union(int value) {
		if (size == 0)
			return new Set(value, value);

		assert checkInvariants() == null : checkInvariants() ;

		Set result = new Set(size + 1);

		int i1 = 0;

		Interval currentInterval = intervals[i1];

		while (true) {

			if (currentInterval.max + 1 < value) {
				result.addDom(currentInterval);
				i1++;
				if (i1 == size) {
					result.addDom(new Interval(value, value));
					return result;
				}
				currentInterval = intervals[i1];
				continue;
			}
			else
				break;

		}

		if (value + 1 < currentInterval.min) {
			result.addDom(new Interval(value, value));
		} else {

			int tempMin = value, tempMax = value;

			if (currentInterval.min < value)
				tempMin = currentInterval.min;

			if (currentInterval.max > value)
				tempMax = currentInterval.max;

			if (i1 + 1 < size && tempMax + 1 == intervals[i1+1].min) {
				tempMax = intervals[i1+1].max;
				i1++;
			}

			result.addDom(new Interval(tempMin, tempMax));
			i1++;
		}

		if (i1 < size)
			for (; i1 < size; i1++)
				result.addDom(intervals[i1]);

		assert result.checkInvariants() == null : result.checkInvariants() ;

		return result;
	}

	/**
	 * It computes union of current set and an interval min..max;
	 */
	public Set union(int min, int max) {

		if (size == 0)
			return new Set(min, max);

		assert checkInvariants() == null : checkInvariants() ;

		Set result = new Set(size + 1);

		int i1 = 0;

		Interval currentInterval1 = intervals[i1];

		while (true) {

			// all intervals before and not glued (min..max) are included
			if (currentInterval1.max + 1 < min) {
				result.addDom(currentInterval1);
				i1++;
				if (i1 == size){
					result.addDom(new Interval(min,max));
					break;
				}
				currentInterval1 = intervals[i1];
				continue;
			}

			// currentInterval if after and not glued
			if (max + 1 < currentInterval1.min) {
				result.addDom(new Interval(min, max));
				break;
			}

			// current interval is glued or intersects with (min..max).

			int tempMin;

			if (currentInterval1.min < min)
				tempMin = currentInterval1.min;
			else
				tempMin = min;

			if (currentInterval1.max > max) {
				result.addDom(new Interval(tempMin, currentInterval1.max));
				i1++;
				break;
			}
			else {

				// (min..max) can cover multiple intervals.
				while (currentInterval1.max <= max) {
					i1++;
					if (i1 == size) {
						result.addDom(new Interval(tempMin, max));
						break;
					}
					currentInterval1 = intervals[i1];
				}

				// if current interval is glued or intersects with (min..max)
				if (max + 1 >= currentInterval1.min) {
					result.addDom(new Interval(tempMin, currentInterval1.max));
					i1++;
				}
				else 
					result.addDom(new Interval(tempMin, max));

				break;
			}
		}

		if (i1 < size)
			for (; i1 < size; i1++)
				result.addDom(intervals[i1]);

		assert result.checkInvariants() == null : result.checkInvariants() ;

		return result;		

	}

	/**
	 * It computes union of current set and the supplied set.
	 * @param set the set used in the union operation. 
	 * @return the union of this set and the provided set.
	 */
	public Set union(Set set) {
		if (set.isEmpty() && isEmpty())
			return new Set();
		if (set.isEmpty() && ! isEmpty())
			return this.cloneLight();
		if (! set.isEmpty() && isEmpty())
			return set.cloneLight();
		IntervalDomain id = (IntervalDomain) super.union(set);
		Set ret = new Set();
		ret.intervals = id.intervals;
		ret.size = id.size;

		assert ret.checkInvariants() == null : ret.checkInvariants() ;

		return ret;
	}	

	/**
	 * It sets the Set to the specified set.
	 * @param set the set used to change this set.
	 */
	public void setDomain(Set set) {

		assert checkInvariants() == null : checkInvariants() ;

		size = set.size;

		intervals = new Interval[set.intervals.length];
		System.arraycopy(set.intervals, 0, intervals, 0, size);

		assert checkInvariants() == null : checkInvariants() ;

		return;
	}


	@Override
	public String toString() {
		StringBuffer S = new StringBuffer("");

		if (!singleton()) {
			S.append("{");
			for (int e = 0; e < size; e++) {
				S.append(intervals[e]);
				if (e + 1 < size)
					S.append(", ");
			}
			S.append("}");
		} 
		else {
			S.append("{"); S.append(intervals[0]); S.append("}");
		}
		return S.toString();

	}

	/**
	 * returns a ValueEnumeration for the set.  
	 */
	@Override
	public ValueEnumeration valueEnumeration() {
		return new SetValueEnumeration(this);
	}

	/**
	 * It returns an unique identifier of the set-domain.
	 */
	@Override
	public int domainID() {
		return SetID;
	}

	/**
	 * It returns the number of elements greater than el.
	 * @param el the element from which counted elements must be greater than. 
	 * @return the number of elements which are greater than the provided element el.
	 */
	public int elementsGreaterThan(int el){
		int ret = -1;
		int value = el-1;
		while(value != el){
			value = el;
			el = nextValue(el);
			ret ++;
		}
		return ret;		
	}

	/**
	 * It returns the number of elements smaller than el.
	 * @param el the element from which counted elements must be smaller than.
	 * @return the number of elements which are smaller than the provided element el.
	 */
	public int elementsSmallerThan(int el){
		int ret = -1;
		int value = el-1;
		while(value != el){
			value = el;
			el = previousValue(el);
			ret ++;
		}
		return ret;		
	}

}
