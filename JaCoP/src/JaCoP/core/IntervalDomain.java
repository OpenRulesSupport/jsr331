/**
 *  IntervalDomain.java 
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
import java.util.List;

import org.jdom.Element;
import JaCoP.constraints.Constraint;

//TODO, test default function which use sparse (dense) representation. Default code if
//domain is neither Interval nor Bound domain.

/**
 * Defines interval of numbers which is part of FDV definition which consist of
 * one or several intervals.
 * 
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class IntervalDomain extends Domain {

	/**
	 * The values of the domain are encoded as a list of intervals.
	 */

	public Interval intervals[];

	/**
	 * It specifies the previous domain which was used by this domain. The old
	 * domain is stored here and can be easily restored if necessary.
	 */

	public Domain previousDomain;

	/**
	 * It specifies number of intervals needed to encode the domain.
	 */

	public int size;

	/**
	 * Empty constructor, does not initialize anything.
	 */

	public IntervalDomain() {
	}

	/**
	 * An empty domain, so no constant creation of empty domains is required.
	 */

	static public IntervalDomain emptyDomain = new IntervalDomain();

	/**
	 * It creates an empty domain, with at least specified number of places in
	 * an array list for intervals.
	 * 
	 * @param size defines the initial size of an array storing the intervals.
	 */

	public IntervalDomain(int size) {
		intervals = new Interval[size];
		this.size = 0;
		searchConstraints = null;
		searchConstraintsToEvaluate = 0;
		previousDomain = null;
		searchConstraintsCloned = false;
	}

	/**
	 * It creates domain with all values between min and max.
	 * 
	 * @param min defines the left bound of a domain.
	 * @param max defines the right bound of a domain.
	 */

	public IntervalDomain(int min, int max) {
		intervals = new Interval[5];
		searchConstraints = null;
		searchConstraintsToEvaluate = 0;
		previousDomain = null;
		searchConstraintsCloned = false;
		intervals[0] = new Interval(min, max);
		this.size = 1;
	}

	/**
	 * It adds interval of values to the domain. It adds at the end without
	 * checks for the correctness of domain representation.
	 */

	@Override
	public void addDom(Interval i) {

		assert checkInvariants() == null : checkInvariants() ;
		
		if (size == intervals.length) {
			Interval[] oldIntervals = intervals;
			intervals = new Interval[oldIntervals.length + 5];
			System.arraycopy(oldIntervals, 0, intervals, 0, size);
		}

		intervals[size++] = i;
		
		assert checkInvariants() == null : checkInvariants() ;
		
	}

	/**
	 * It adds values as specified by the parameter to the domain. The 
	 * input parameter can not be an empty set.
	 */

	@Override
	public void addDom(Domain domain) {

		if (domain.domainID() == IntervalDomainID) {

			IntervalDomain d = (IntervalDomain) domain;

			assert checkInvariants() == null : checkInvariants() ;
			
			if (size == 0) {
				if (intervals == null || intervals.length < d.intervals.length)
					intervals = new Interval[d.intervals.length];

				System.arraycopy(d.intervals, 0, intervals, 0, d.size);
				size = d.size;

			} else
				for (int i = 0; i < d.size; i++)
					// can not use function add(Interval)
					addDom(d.intervals[i].min, d.intervals[i].max);
			
			assert checkInvariants() == null : checkInvariants() ;
			
			return;
		}

		if (domain.domainID() == BoundDomainID) {
			
			assert checkInvariants() == null : checkInvariants() ;
			
			addDom(domain.min(), domain.max());
			
			assert checkInvariants() == null : checkInvariants() ;
			
			return;
		}
		
		if (domain.isSparseRepresentation()) {
			
			ValueEnumeration enumer = domain.valueEnumeration();
			
			while (enumer.hasMoreElements()) {
				
				int next = enumer.nextElement();
				
				addDom(next, next);
			}
			
			return;
							
		}
		else {
			
			IntervalEnumeration enumer = domain.intervalEnumeration();
			
			while (enumer.hasMoreElements()) {
				
				Interval next = enumer.nextElement();
				
				addDom(next.min, next.max);
			}
			
			return;
			
			
		}
				
	}

	
	/**
	 * It adds all values between min and max to the domain.
	 */

	@Override
	public void addDom(int min, int max) {

		assert checkInvariants() == null : checkInvariants() ;
		
		if (size == 0) {

			intervals = new Interval[1];
			intervals[size++] = new Interval(min, max);

		} else {
			
			
			int i = 0;
			for (; i < size; i++) {
				// i - position of the interval which touches with or intersects with min..max
				if ((max + 1 >= intervals[i].min && max <= intervals[i].max+1) || 
					(min + 1 >= intervals[i].min && min <= intervals[i].max+1) ||
					(min <= intervals[i].min && intervals[i].max <= max ))
					break;
				if (max + 1 < intervals[i].min) {
					// interval is inserted at position i
					
					if (size == intervals.length) {
						// no empty intervals to fill in
						Interval[] oldIntervals = intervals;
						intervals = new Interval[intervals.length + 5];
						System.arraycopy(oldIntervals, 0, intervals, 0, size);
					}

					// empty intervals are available
					Interval temp = intervals[i];
					intervals[i] = new Interval(min, max);

					int t = size;
					while (t > i) {
						intervals[t] = intervals[t - 1];
						t--;
					}
					intervals[i + 1] = temp;
					size++;

					assert checkInvariants() == null : checkInvariants() ;
					assert contains(min) : "The minimum was not added";
					assert contains(max) : "The maximum was not added";

					return;
				}
			}

			if (i == size) {
				
				if (size == intervals.length) {
					// no empty intervals to fill in
					Interval[] oldIntervals = intervals;
					intervals = new Interval[intervals.length + 5];
					System.arraycopy(oldIntervals, 0, intervals, 0, size);
				}
				
				intervals[size] = new Interval(min, max);
				size++;
				
				assert checkInvariants() == null : checkInvariants() ;
				assert contains(min) : "The minimum was not added";
				assert contains(max) : "The maximum was not added";

				return;
			}
			
			int newMin;
			// interval(min, max) intersects with current domain
			if (min < intervals[i].min) {
				newMin = min;
			} else {
				newMin = intervals[i].min;
			}
			
			int target = i;
			int newMax;

			while (target < size && max >= intervals[target].max)
				target++;

			if (target == size)
				newMax = max;
			else if (intervals[target].min > max + 1)
				newMax = max;
			else {
				newMax = intervals[target].max;
				target++;
			}

			intervals[i] = new Interval(newMin, newMax);

			while (target < size) {
				intervals[++i] = intervals[target++];
			}

			while (size > i + 1)
				intervals[--size] = null;
			
		}

		assert checkInvariants() == null : checkInvariants() ;
		assert contains(min) : "The minimum was not added";
		assert contains(max) : "The maximum was not added";
	}	
	
	
	/**
	 * Checks if two domains intersect.
	 */

	@Override
	public boolean isIntersecting(Domain domain) {

		if (domain.isEmpty())
			return false;
		
		if (domain.domainID() == IntervalDomainID) {

			assert checkInvariants() == null : checkInvariants() ;
			
			IntervalDomain intervalDomain = (IntervalDomain) domain;

			int pointer1 = 0;
			int pointer2 = 0;

			int size2 = intervalDomain.size;

			if (size == 0 || size2 == 0)
				return false;

			Interval interval1 = intervals[pointer1];
			Interval interval2 = intervalDomain.intervals[pointer2];

			while (true) {
				if (interval1.max < interval2.min) {
					pointer1++;
					if (pointer1 < size) {
						interval1 = intervals[pointer1];
						continue;
					} else
						break;
				} else if (interval2.max < interval1.min) {
					pointer2++;
					if (pointer2 < size2) {
						interval2 = intervalDomain.intervals[pointer2];
						continue;
					} else
						break;
				} else
					return true;
			}

			return false;

		}
		
		if (domain.domainID() == BoundDomainID) {
			
			assert checkInvariants() == null : checkInvariants() ;
			
			if (max() < domain.min() || domain.max() < min())
				return false;
			
			return true;
		}

		if (domain.isSparseRepresentation()) {
			
			ValueEnumeration enumer = domain.valueEnumeration();
			
			while (enumer.hasMoreElements()) {
				if (contains(enumer.nextElement()))
					return true;				
			}

			return false;
							
		}
		else {
			
			IntervalEnumeration enumer = domain.intervalEnumeration();
			
			while (enumer.hasMoreElements()) {
				
				Interval next = enumer.nextElement();
				if (this.isIntersecting(next.min, next.max))
					return true;
			}
			
			return false;
			
		}

	}

	@Override
	public boolean isIntersecting(int min, int max) {

		assert checkInvariants() == null : checkInvariants() ;
		
		int i = 0;
		for (; i < size && intervals[i].max < min; i++)
			;

		if (i == size || intervals[i].min > max)
			return false;
		else
			return true;

	}

	/**
	 * It removes all elements.
	 */

	@Override
	public void clear() {
		size = 0;
	}

	/**
	 * It clones the domain object, only data responsible for encoding domain
	 * values is cloned. All other fields must be set separately.
	 */

	@Override
	public IntervalDomain cloneLight() {

		assert checkInvariants() == null : checkInvariants() ;
		
		IntervalDomain cloned = new IntervalDomain();

		cloned.intervals = new Interval[this.intervals.length];

		System.arraycopy(intervals, 0, cloned.intervals, 0, size);

		cloned.size = size;

		return cloned;
	}

	/**
	 * It clones the domain object.
	 */

	@Override
	public IntervalDomain clone() {

		assert checkInvariants() == null : checkInvariants() ;
		
		IntervalDomain cloned = new IntervalDomain();

		cloned.intervals = new Interval[this.intervals.length];

		System.arraycopy(intervals, 0, cloned.intervals, 0, size);

		cloned.size = size;

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
	 * It specifies if the current domain contains the domain given as a
	 * parameter. It assumes that input parameter does not represent an
	 * empty domain.
	 */

	@Override
	public boolean contains(Domain domain) {
		
		assert checkInvariants() == null : checkInvariants() ;
		
        if (domain.domainID() == IntervalDomainID) {

                IntervalDomain dom2 = (IntervalDomain) domain;

        		assert dom2.checkInvariants() == null : dom2.checkInvariants() ;
        		
                int max2 = dom2.size;

                int i1 = 0;
                int i2 = 0;

                if (max2 == 0)
                        return true;

                Interval interval1 = intervals[0];
                Interval interval2 = dom2.intervals[0];

                while(true) {

                        while (interval2.min > interval1.max) {

                                i1++;

                                if (i1 == size)
                                        return false;

                                interval1 = intervals[i1];

                        }

                        if (interval2.min < interval1.min
                                        || interval2.max > interval1.max)
                                return false;

                        i2++;

                        if (i2 == max2)
                                return true;

                        interval2 = dom2.intervals[i2];

                }

        }
		
        if (domain.domainID() == BoundDomainID) {

        	int i= 0;
        	int min = domain.min();
        
        	for (; i < size && intervals[i].max < min; i++);
        	
        	if (i == size)
        		return false;
        	
        	if (intervals[i].min <= min && intervals[i].max >= domain.max())
        		return true;
        		
        	return false;
        	
        }
        
		if (domain.isSparseRepresentation()) {
			
			ValueEnumeration enumer = domain.valueEnumeration();
			
			while (enumer.hasMoreElements()) {
				if (!contains(enumer.nextElement()))
					return false;				
			}

			return true;
							
		}
		else {
								
			   int max2 = domain.noIntervals();

               int i1 = 0;
               int i2 = 0;

               if (max2 == 0)
                       return true;

               Interval interval1 = intervals[0];
               Interval interval2 = domain.getInterval(0);
               
               while(true) {

                       while (interval2.min > interval1.max) {

                               i1++;

                               if (i1 == size)
                                       return false;

                               interval1 = intervals[i1];

                       }

                       if (interval2.min < interval1.min
                                       || interval2.max > interval1.max)
                               return false;

                       i2++;

                       if (i2 == max2)
                               return true;

                       interval2 = domain.getInterval(i2);
               }
			
		}
        
	}	
	

	/**
	 * It creates a complement of a domain.
	 */

	@Override
	public Domain complement() {

		if (size == 0)
			return new IntervalDomain(Constants.MinInt, Constants.MaxInt);

		assert checkInvariants() == null : checkInvariants() ;
		
		IntervalDomain result = new IntervalDomain(size + 1);
		if (min() != Constants.MinInt)
			result.addDom(new Interval(Constants.MinInt, intervals[0].min - 1));

		for (int i = 0; i < size - 1; i++)
			result.addDom(new Interval(intervals[i].max + 1,
					intervals[i + 1].min - 1));

		if (max() != Constants.MaxInt)
			result.addDom(new Interval(max() + 1, Constants.MaxInt));

		assert result.checkInvariants() == null : result.checkInvariants() ;
		return result;

	}

	/**
	 * It checks if value belongs to the domain.
	 */
	@Override
	public boolean contains(int value) {

		assert checkInvariants() == null : checkInvariants() ;
		
		for (int m = 0; m < size; m++) {
			Interval i = intervals[m];
			if (i.max >= value)
				if (value >= i.min)
					return true;
		}

		return false;

	}

	/**
	 * It gives next value in the domain from the given value (lexigraphical
	 * ordering). The provided value does not have to belong to the domain. 
	 * If no value can be found then returns the same value.
	 */
	@Override
	public int nextValue(int value) {

		assert checkInvariants() == null : checkInvariants() ;
		
		for (int m = 0; m < size; m++) {
			Interval i = intervals[m];
			if (i.max > value)
				if (value >= i.min - 1)
					return value + 1;
				else
					return i.min;
		}

		return value;

	}

	/**
	 * It divides the domain. E.g. (5..9, 11..13) / 3 = 2..4.
	 * @param div divider.
	 * @return domain after division.
	 */
	public Domain divide(int div) {

		assert checkInvariants() == null : checkInvariants() ;
		
		IntervalDomain temp = new IntervalDomain(size);

		int newMin;
		int newMax;

		boolean current = false;
		int currentMin = -1;
		int currentMax = -1;

		for (int m = 0; m < size; m++) {

			Interval I1 = intervals[m];

			newMin = div(I1.min, div);
			newMax = I1.max / div;

			if (current != false && currentMax + 1 == newMin) {
				currentMax = newMax;
				continue;
			}

			if (newMin <= newMax) {

				if (current) {
					temp.addDom(currentMin, currentMax);
				}

				current = true;

				currentMin = newMin;
				currentMax = newMax;
			}
		}

		assert temp.checkInvariants() == null : temp.checkInvariants() ;
		return temp;

	}

	private int div(int A, int B) {
		int Div, Rem;

		Div = A / B;
		Rem = A % B;
		return (Rem > 0) ? Div + 1 : Div;
	}

	/**
	 * It returns value enumeration of the domain values.
	 */

	@Override
	public ValueEnumeration valueEnumeration() {
		return new IntervalDomainValueEnumeration(this);
	}

	/**
	 * It returns interval enumeration of the domain values.
	 */
	@Override
	public IntervalEnumeration intervalEnumeration() {
		return new IntervalDomainIntervalEnumeration(this);
	}

	/**
	 * It checks if the domain is equal to the supplied domain.
	 */
	@Override
	public boolean eq(Domain domain) {

		assert checkInvariants() == null : checkInvariants() ;
		
		if (domain.domainID() == IntervalDomainID) {

			IntervalDomain intervalDomain = (IntervalDomain) domain;

			assert intervalDomain.checkInvariants() == null : intervalDomain.checkInvariants() ;
			
			boolean equal = true;
			int i = 0;

			if (size == intervalDomain.size) {
				while (equal && i < size) {
					equal = intervals[i].eq(intervalDomain.intervals[i]);
					i++;
				}
			} else
				equal = false;

			return equal;

		}

		if (domain.domainID() == BoundDomainID) {
			
			if (size == 0 && domain.isEmpty())
				return true;
			
			if (size != 1)
				return false;
			
			if (intervals[0].min == domain.min() &&
				intervals[0].max == domain.max())
				return true;
			
			return false;
			
		}

		// Uses default dense and sparse assumptions to compute the function as
		// efficiently as possible.
		
		if (domain.isSparseRepresentation()) {	
			
			boolean equal = true;

			if (this.getSize() == domain.getSize()) {
				
				ValueEnumeration enumer1 = domain.valueEnumeration();
				ValueEnumeration enumer2 = this.valueEnumeration();
				
				while (equal && enumer1.hasMoreElements()) {
					equal = ( enumer1.nextElement() == enumer2.nextElement() );
				}
			} else
				equal = false;

			return equal;
			
		}
		else {
			
			boolean equal = true;
			int i = 0;

			if (this.getSize() == domain.getSize()) {
				while (equal && i < size) {
					equal = intervals[i].eq(domain.getInterval(i));
					i++;
				}
			} else
				equal = false;

			return equal;
			
		}
		
	}

	/**
	 * It returns the size of the domain.
	 */
	@Override
	public int getSize() {

		assert checkInvariants() == null : checkInvariants() ;
		
		int n = 0;

		for (int i = 0; i < size; i++)
			n = n + intervals[i].max - intervals[i].min + 1;

		return n;
	}

	/**
	 * It interesects current domain with the one given as a parameter.
	 */
	@Override
	public Domain intersect(Domain domain) {

		assert checkInvariants() == null : checkInvariants() ;
		
		if (domain.isEmpty())
			return emptyDomain;
		
		if (domain.domainID() == IntervalDomainID) {

			IntervalDomain input = (IntervalDomain) domain;

			assert input.checkInvariants() == null : input.checkInvariants() ;
			
			IntervalDomain temp;

			if (size > input.size)
				temp = new IntervalDomain(size);
			else
				temp = new IntervalDomain(input.size);

			int pointer1 = 0;
			int pointer2 = 0;

			int size1 = size;
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
				// interval1.max >= interval2.min
				// interval2.max >= interval1.min
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
				// interval1.max >= interval2.min
				// interval2.max >= interval1.min
				// interval1.min > interval2.min
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

		if (domain.domainID() == BoundDomainID) {
				
			int min = domain.min();
			int max = domain.max();
			
			IntervalDomain temp = new IntervalDomain(size);

			int pointer1 = 0;

			if (size == 0)
				return emptyDomain;

			Interval interval1 = intervals[pointer1];
			
			while (true) {
				if (interval1.max < min) {
					pointer1++;
					if (pointer1 < size) {
						interval1 = intervals[pointer1];
						continue;
					} else
						break;
				} else 
					if (max < interval1.min) 
						break;
					else
				// interval1.max >= min
				// max >= interval1.min
				if (interval1.min <= min) {

					if (interval1.max <= max) {

						temp.addDom(min, interval1.max);
						pointer1++;
						if (pointer1 < size) {
							interval1 = intervals[pointer1];
							continue;
						} else
							break;
					} else {
						temp.addDom(min, max);
						break;
					}

				} else
				// interval1.max >= min
				// max >= interval1.min
				// interval1.min > min
				{
					if (max <= interval1.max) {
						temp.addDom(interval1.min, max);
						break;
					} else {
						temp.addDom(interval1.min, interval1.max);
						pointer1++;
						if (pointer1 < size) {
							interval1 = intervals[pointer1];
							continue;
						} else
							break;
					}

				}
			}

			return temp;

		}		
		
		if (domain.isSparseRepresentation()) {	
			
			Domain temp = null;
			
			try {
				temp = domain.getClass().newInstance();
			}
			catch(Exception ex) {
				System.out.println(ex.getMessage());
			}

			ValueEnumeration enumer = domain.valueEnumeration();
			
			while (enumer.hasMoreElements()) {
				
				int next = enumer.nextElement();
				
				if (this.contains(next))
					temp.addDom(next, next);
			}
			
			return temp;
			
		}
		else {
			
			IntervalDomain temp = new IntervalDomain(size);

			int pointer1 = 0;
			int pointer2 = 0;

			int size1 = size;

			if (size1 == 0 || domain.noIntervals() == 0)
				return temp;

			Interval interval1 = intervals[pointer1];
			Interval interval2 = domain.getInterval(pointer2);

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
					if (pointer2 < domain.noIntervals()) {
						interval2 = domain.getInterval(pointer2);
						continue;
					}
					else
						break;
				} else
				// interval1.max >= interval2.min
				// interval2.max >= interval1.min
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
						if (pointer2 < domain.noIntervals()) {
							interval2 = domain.getInterval(pointer2);
							continue;
						}
						else
							break;
					}

				} else
				// interval1.max >= interval2.min
				// interval2.max >= interval1.min
				// interval1.min > interval2.min
				{
					if (interval2.max <= interval1.max) {
						temp.addDom(interval1.min, interval2.max);
						pointer2++;
						if (pointer2 < domain.noIntervals()) {
							interval2 = domain.getInterval(pointer2);
							continue;
						}
						else
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
			
			return temp;
			
		}
		
	}

	/**
	 * In intersects current domain with the domain min..max.
	 */

	@Override
	public Domain intersect(int min, int max) {

		assert checkInvariants() == null : checkInvariants() ;
		
		IntervalDomain temp = new IntervalDomain(size);

		if (size == 0)
			return this;

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
			// interval1.max >= interval2.min
			// interval2.max >= interval1.min
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
			// interval1.max >= interval2.min
			// interval2.max >= interval1.min
			// interval1.min > interval2.min
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

		assert checkInvariants() == null : checkInvariants() ;
		assert temp.checkInvariants() == null : temp.checkInvariants() ;
		
		return temp;

	}

	@Override
	public Domain subtract(int value) {

		assert checkInvariants() == null : checkInvariants() ;
		
		IntervalDomain result = cloneLight();

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
						// add domain value+1..oldMax
						result.addDom(value + 1, oldMax);
						pointer1++;
					}

				} else if (interval1.max != value) {
					// replace value..max with value+1..interval1.max
					result.intervals[pointer1] = new Interval(value + 1,
							interval1.max);
					pointer1++;
				} else {
					result.removeInterval(pointer1);
				}

				break;

			}

		}

		assert checkInvariants() == null : checkInvariants() ;
		assert result.checkInvariants() == null : result.checkInvariants() ;
		return result;

	}

	/**
	 * It returns true if given domain is empty.
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * It returns the maximum value in a domain.
	 */
	@Override
	public int max() {

		assert checkInvariants() == null : checkInvariants() ;
		
		assert size != 0;

		return intervals[size - 1].max;

	}

	/**
	 * It returns the minimum value in a domain.
	 */

	@Override
	public int min() {

		assert checkInvariants() == null : checkInvariants() ;
		
		assert size != 0;

		return intervals[0].min;

	}

	/**
	 * {1..4} * 6 = {6, 12, 18, 24}
	 * @param mul the multiplier constant.
	 * @return the domain after multiplication.
	 */

	public Domain multiply(int mul) {

		assert mul != 0;

		IntervalDomain temp = new IntervalDomain(getSize() * mul);

		if (mul > 0) {

			for (int m = 0; m < size; m++) {
				Interval I1 = intervals[m];
				for (int i = I1.min; i <= I1.max; i++) {
					int value = i * mul;
					temp.addDom(new Interval(value, value));
				}
			}
			
			assert temp.checkInvariants() == null : temp.checkInvariants() ;
			return temp;

		} else {

			for (int m = size - 1; m >= 0; m--) {
				Interval I1 = intervals[m];
				for (int i = I1.max; i >= I1.min; i--) {
					int value = i * mul;
					temp.addDom(new Interval(value, value));
				}
			}

			assert temp.checkInvariants() == null : temp.checkInvariants() ;
			return temp;

		}

	}

	/**
	 * It removes the counter-th interval from the domain.
	 * @param position it specifies the position of the removed interval.
	 */

	public void removeInterval(int position) {

		assert checkInvariants() == null : checkInvariants() ;
		
		assert position < size;
		assert position >= 0;

		size--;

		while (position < size) {
			intervals[position] = intervals[position + 1];
			position++;
		}

		assert checkInvariants() == null : checkInvariants() ;
		
	}

	/**
	 * It sets the domain to the specified domain.
	 */

	@Override
	public void setDomain(Domain domain) {

		assert checkInvariants() == null : checkInvariants() ;
		
		if (domain.domainID() == IntervalDomainID) {

			IntervalDomain intervalDomain = (IntervalDomain) domain;

			size = intervalDomain.size;

			intervals = new Interval[intervalDomain.intervals.length];
			System.arraycopy(intervalDomain.intervals, 0, intervals, 0, size);

			assert checkInvariants() == null : checkInvariants() ;
			return;
		}

		if (domain.domainID() == BoundDomainID) {
			
			size = 1;
			
			intervals = new Interval[1];
			intervals[0] = new Interval(domain.min(), domain.max());
			
			return;
		}
		
		if (domain.isSparseRepresentation()) {	
			
			this.clear();
			
			ValueEnumeration enumer = domain.valueEnumeration();
			
			while (enumer.hasMoreElements()) {
				
				int next = enumer.nextElement();
				
				if (this.contains(next))
					this.addDom(next, next);
			}			
		
			return;
			
		}
		else {

			this.clear();
			
			IntervalEnumeration enumer = domain.intervalEnumeration();
			
			while (enumer.hasMoreElements())				
				this.addDom(enumer.nextElement());
			
			return;
		}
		
	}

	/**
	 * It sets the domain to all values between min and max.
	 */
	@Override
	public void setDomain(int min, int max) {
		size = 1;
		intervals[0] = new Interval(min, max);
	}

	/**
	 * It returns true if given domain has only one element.
	 */
	@Override
	public boolean singleton() {
		return (size == 1 && intervals[0].min == intervals[0].max);
	}

	/**
	 * It returns true if given domain has only one element equal c.
	 */
	@Override
	public boolean singleton(int c) {
		assert checkInvariants() == null : checkInvariants() ;
		return (size == 1 && intervals[0].min == c && c == intervals[0].max);
	}

	/**
	 * It subtracts domain from current domain and returns the result.
	 */

	@Override
	public Domain subtract(Domain domain) {

		assert checkInvariants() == null : checkInvariants() ;

		if (isEmpty())
			return Store.emptyDomain;
		
	  	if (domain.domainID() == IntervalDomainID) {

			IntervalDomain intervalDomain = (IntervalDomain) domain;

			assert intervalDomain.checkInvariants() == null : intervalDomain.checkInvariants() ;
			
			if (intervalDomain.size == 0)
				return cloneLight();

			IntervalDomain result = new IntervalDomain();

			result.intervals = new Interval[size + 1];

			int i1 = 0;
			int i2 = 0;

			Interval currentDomain1 = intervals[i1];
			Interval currentDomain2 = intervalDomain.intervals[i2];
	         
			boolean minIncluded = false;

			int max2 = intervalDomain.size;

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
					currentDomain2 = intervalDomain.intervals[i2];
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
							currentDomain2 = intervalDomain.intervals[i2];

						if (i2 == max2
								|| currentDomain2.min > currentDomain1.max) {
							result.addDom(new Interval(oldMax + 1,
									currentDomain1.max));
							i1++;
							if (i1 == size)
								break;
							currentDomain1 = intervals[i1];
							minIncluded = false;

							if (i2 == max2)
								break;
						} else {

							result.addDom(new Interval(oldMax + 1,
									currentDomain2.min - 1));
							minIncluded = true;
						}

					}

				}
				// currentDomain1.min < currentDomain2.min)
				else {

					if (currentDomain1.max <= currentDomain2.max) {

						if (!minIncluded)
							if (currentDomain1.max >= currentDomain2.min)
								result.addDom(new Interval(currentDomain1.min,
										currentDomain2.min - 1));
							else
								result.addDom(new Interval(currentDomain1.min,
										currentDomain1.max));

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
							result.addDom(new Interval(currentDomain1.min,
									currentDomain2.min - 1));
							minIncluded = true;
						}

						int oldMax = currentDomain2.max;
						i2++;
						if (i2 != max2)
							currentDomain2 = intervalDomain.intervals[i2];

						if (i2 == max2
								|| currentDomain2.min > currentDomain1.max) {
							result.addDom(new Interval(oldMax + 1,
									currentDomain1.max));
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

							result.addDom(new Interval(oldMax + 1,
									currentDomain2.min - 1));

						}

					}

				}

			}

			while (i1 < size) {
				result.addDom(intervals[i1]);
				i1++;
			}

			assert checkInvariants() == null : checkInvariants() ;
			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			return result;

		}
		
		if (domain.domainID() == BoundDomainID) {

			if (domain.isEmpty())
				return cloneLight();

			IntervalDomain result = new IntervalDomain();

			result.intervals = new Interval[size+1];

			int i1 = 0;

			Interval currentDomain1 = intervals[i1];
			
			int min = domain.min();
			int max = domain.max();
				        
			while (true) {

				if (currentDomain1.max < min) {
					result.addDom(currentDomain1);
					i1++;
					if (i1 == size)
						break;
					currentDomain1 = intervals[i1];
					continue;
				}

				if (max < currentDomain1.min) {
					break;
				}

				if (currentDomain1.min >= min) {

					if (currentDomain1.max <= max) {
						// Skip current interval of i1 completely
						i1++;
						if (i1 == size)
							break;
						currentDomain1 = intervals[i1];
						continue;
					} else {

						// interval (min, max) ends before interval of dom1 ends
						result.addDom(new Interval(max + 1, currentDomain1.max));
						
						i1++;
						if (i1 == size)
							break;
						currentDomain1 = intervals[i1];
					}

				}
				// currentDomain1.min < min)
				else {

					result.addDom(new Interval(currentDomain1.min, min - 1));
					
					if (currentDomain1.max > max) {

						result.addDom(new Interval(max + 1,
									  currentDomain1.max));

					}
					
					i1++;
					if (i1 == size)
						break;
					currentDomain1 = intervals[i1];
				}
				
			}

			while (i1 < size) {
				result.addDom(intervals[i1]);
				i1++;
			}

			assert checkInvariants() == null : checkInvariants() ;
			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			return result;

		}		
		
		if (domain.isSparseRepresentation()) {	
			
			Domain result = this.cloneLight();
			
			ValueEnumeration enumer = domain.valueEnumeration();
			
			while (enumer.hasMoreElements()) {

				int next = enumer.nextElement();
				result.subtract(next, next);
				
			}

			return result;
			
		}
		else {
			
			if (domain.noIntervals() == 0)
				return cloneLight();

			IntervalDomain result = new IntervalDomain();

			result.intervals = new Interval[size + 1];

			int i1 = 0;
			int i2 = 0;

			Interval currentDomain1 = intervals[i1];
			Interval currentDomain2 = domain.getInterval(i2);
			
			boolean minIncluded = false;

			int max2 = domain.noIntervals();
			
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
					currentDomain2 = domain.getInterval(i2);
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
							currentDomain2 = domain.getInterval(i2);

						if (i2 == max2
								|| currentDomain2.min > currentDomain1.max) {
							result.addDom(new Interval(oldMax + 1,
									currentDomain1.max));
							i1++;
							if (i1 == size)
								break;
							currentDomain1 = intervals[i1];
							minIncluded = false;

							if (i2 == max2)
								break;
						} else {

							result.addDom(new Interval(oldMax + 1,
									currentDomain2.min - 1));
							minIncluded = true;
						}

					}

				}
				// currentDomain1.min < currentDomain2.min)
				else {

					if (currentDomain1.max <= currentDomain2.max) {

						if (!minIncluded)
							if (currentDomain1.max >= currentDomain2.min)
								result.addDom(new Interval(currentDomain1.min,
										currentDomain2.min - 1));
							else
								result.addDom(new Interval(currentDomain1.min,
										currentDomain1.max));

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
							result.addDom(new Interval(currentDomain1.min,
									currentDomain2.min - 1));
							minIncluded = true;
						}

						int oldMax = currentDomain2.max;
						i2++;
						if (i2 != max2)
							currentDomain2 = domain.getInterval(i2);

						if (i2 == max2
								|| currentDomain2.min > currentDomain1.max) {
							result.addDom(new Interval(oldMax + 1,
									currentDomain1.max));
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

							result.addDom(new Interval(oldMax + 1,
									currentDomain2.min - 1));

						}

					}

				}

			}

			while (i1 < size) {
				result.addDom(intervals[i1]);
				i1++;
			}

			assert checkInvariants() == null : checkInvariants() ;
			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			return result;

		}

	}

	/**
	 * It subtracts min..max from current domain and returns the result.
	 */

	
	
	@Override
	public IntervalDomain subtract(int min, int max) {

		assert checkInvariants() == null : checkInvariants() ;
		
		assert (min <= max);

		if (size == 0)
			return emptyDomain;

		// interval under the analysis
		int i1 = 0;
		// place for next interval in subtracted domain
		Interval currentInterval1 = intervals[i1];

		IntervalDomain result = new IntervalDomain(intervals.length+1);
		
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

				// currentDomain1.max >= min
				// max >= currentDomain1.min

				if (currentInterval1.max <= max) {
					// Skip current interval of i1 completely
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

		assert checkInvariants() == null : checkInvariants() ;
		assert result.checkInvariants() == null : result.checkInvariants() ;
		return result;
	}

	/**
	 * It computes union of dom1 from dom2 and returns the result.
	 */
	@Override
	public Domain union(Domain domain) {

		assert checkInvariants() == null : checkInvariants() ;
		
		if (domain.domainID() == IntervalDomainID) {

			IntervalDomain intervalDomain = (IntervalDomain) domain;

			assert intervalDomain.checkInvariants() == null : intervalDomain.checkInvariants() ;
			
			if (intervalDomain.size == 0) {

				IntervalDomain result = cloneLight();

				return result;
			}

			if (size == 0) {

				Domain result = intervalDomain.cloneLight();

				return result;
			}

			IntervalDomain result = new IntervalDomain(size
					+ intervalDomain.size);

			int i1 = 0;
			int i2 = 0;

			Interval currentDomain1 = intervals[i1];
			Interval currentDomain2 = intervalDomain.intervals[i2];

			int max1 = size;
			int max2 = intervalDomain.size;

			while (true) {

				if (currentDomain1.max + 1 < currentDomain2.min) {
					result.addDom(new Interval(currentDomain1.min,
							currentDomain1.max));
					i1++;
					if (i1 == max1)
						break;
					currentDomain1 = intervals[i1];
					continue;
				}

				if (currentDomain2.max + 1 < currentDomain1.min) {
					result.addDom(new Interval(currentDomain2.min,
							currentDomain2.max));
					i2++;
					if (i2 == max2)
						break;
					currentDomain2 = intervalDomain.intervals[i2];
					continue;
				}

				// if (currentDomain1.max > currentDomain2.min ||
				// currentDomain2.max > currentDomain1.min) {

				int min;

				if (currentDomain1.min < currentDomain2.min)
					min = currentDomain1.min;
				else
					min = currentDomain2.min;

				while ((currentDomain1.max + 1 >= currentDomain2.min && currentDomain1.min <= currentDomain2.min)
						|| (currentDomain2.max + 1 >= currentDomain1.min && currentDomain2.min <= currentDomain1.min)) {

					if (currentDomain1.max <= currentDomain2.max) {
						i1++;
						if (i1 == max1)
							break;
						currentDomain1 = intervals[i1];
						continue;
					}

					if (currentDomain2.max < currentDomain1.max) {
						i2++;
						if (i2 == max2)
							break;
						currentDomain2 = intervalDomain.intervals[i2];
						continue;
					}

				}

				if (i1 == max1) {

					while (currentDomain2.max <= currentDomain1.max) {
						i2++;
						if (i2 == max2)
							break;
						currentDomain2 = intervalDomain.intervals[i2];
					}

					if (currentDomain1.max <= currentDomain2.max
							&& currentDomain1.max + 1 >= currentDomain2.min) {
						result.addDom(new Interval(min, currentDomain2.max));
						i2++;
					} else {
						result.addDom(new Interval(min, currentDomain1.max));
					}
					break;
				}

				if (i2 == max2) {

					while (currentDomain1.max <= currentDomain2.max) {
						i1++;
						if (i1 == max1)
							break;
						currentDomain1 = intervals[i1];
					}

					if (currentDomain2.max <= currentDomain1.max
							&& currentDomain2.max + 1 >= currentDomain1.min) {
						result.addDom(new Interval(min, currentDomain1.max));
						i1++;
					} else {
						result.addDom(new Interval(min, currentDomain2.max));
					}
					break;
				}
				
				if (currentDomain1.max < currentDomain2.max) {
					result.addDom(new Interval(min, currentDomain1.max));
					i1++;
					if (i1 == max1)
						break;
					currentDomain1 = intervals[i1];
					continue;
				} else {
					result.addDom(new Interval(min, currentDomain2.max));
					i2++;
					if (i2 == max2)
						break;
					currentDomain2 = intervalDomain.intervals[i2];
					continue;
				}

			}

			if (i1 < max1)
				for (; i1 < max1; i1++)
					result.addDom(intervals[i1]);

			if (i2 < max2)
				for (; i2 < max2; i2++)
					result.addDom(intervalDomain.intervals[i2]);

			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			return result;

		}

		
		if (domain.domainID() == BoundDomainID) {

			if (domain.isEmpty())
				return cloneLight();
			
			int min = domain.min();
			int max = domain.max();
							
			if (size == 0)
				return new IntervalDomain(min, max);

			IntervalDomain result = new IntervalDomain(size + 1);

			int i1 = 0;

			Interval currentInterval1 = intervals[i1];

			while (true) {

				// all intervals before and not glued (min..max) are included
				if (currentInterval1.max + 1 < min) {
					result.addDom(currentInterval1);
					i1++;
					if (i1 == size)
						break;
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

			assert checkInvariants() == null : checkInvariants() ;
			assert result.checkInvariants() == null : result.checkInvariants() ;
			return result;		
		
		}
		
		if (domain.isSparseRepresentation()) {	
			
			IntervalDomain result = this.cloneLight();
			
			ValueEnumeration enumer = domain.valueEnumeration();
			
			while(enumer.hasMoreElements()) { 
				int next = enumer.nextElement();
				result.addDom(next, next);
			}
			
			return result;
			
		}
		else {
			//TODO, work with dense domain
			
			if (domain.noIntervals() == 0)
				return cloneLight();

			if (size == 0)
				return domain.cloneLight();

			IntervalDomain result = new IntervalDomain(size);

			int i1 = 0;
			int i2 = 0;

			Interval currentDomain1 = intervals[i1];
			Interval currentDomain2 = domain.getInterval(i2);

			int max1 = size;
//			int max2 = intervalDomain.size;

			while (true) {

				if (currentDomain1.max + 1 < currentDomain2.min) {
					result.addDom(new Interval(currentDomain1.min,
							currentDomain1.max));
					i1++;
					if (i1 == max1)
						break;
					currentDomain1 = intervals[i1];
					continue;
				}

				if (currentDomain2.max + 1 < currentDomain1.min) {
					result.addDom(new Interval(currentDomain2.min,
							currentDomain2.max));
					i2++;
					if (i2 < domain.noIntervals()) {
						currentDomain2 = domain.getInterval(i2);
						continue;
					}
					else
						break;
				}

				// if (currentDomain1.max > currentDomain2.min ||
				// currentDomain2.max > currentDomain1.min) {

				int min;

				if (currentDomain1.min < currentDomain2.min)
					min = currentDomain1.min;
				else
					min = currentDomain2.min;

				while ((currentDomain1.max + 1 >= currentDomain2.min && currentDomain1.min <= currentDomain2.min)
						|| (currentDomain2.max + 1 >= currentDomain1.min && currentDomain2.min <= currentDomain1.min)) {

					if (currentDomain1.max <= currentDomain2.max) {
						i1++;
						if (i1 == max1)
							break;
						currentDomain1 = intervals[i1];
						continue;
					}

					if (currentDomain2.max < currentDomain1.max) {
						i2++;
						if (i2 < domain.noIntervals()) {
							currentDomain2 = domain.getInterval(i2);
							continue;
						}
						else
							break;
					}

				}

				if (i1 == max1) {

					while (currentDomain2.max <= currentDomain1.max) {
						i2++;
						if (i2 < domain.noIntervals()) {
							currentDomain2 = domain.getInterval(i2);
							continue;
						}
						else
							break;
					}

					if (currentDomain1.max <= currentDomain2.max
							&& currentDomain1.max + 1 >= currentDomain2.min) {
						result.addDom(new Interval(min, currentDomain2.max));
						i2++;
					} else {
						result.addDom(new Interval(min, currentDomain1.max));
					}
					break;
				}

				if (domain.noIntervals() == i2) {

					while (currentDomain1.max <= currentDomain2.max) {
						i1++;
						if (i1 == max1)
							break;
						currentDomain1 = intervals[i1];
					}

					if (currentDomain2.max <= currentDomain1.max
							&& currentDomain2.max + 1 >= currentDomain1.min) {
						result.addDom(new Interval(min, currentDomain1.max));
						i1++;
					} else {
						result.addDom(new Interval(min, currentDomain2.max));
					}
					break;
				}
				
				if (currentDomain1.max < currentDomain2.max) {
					result.addDom(new Interval(min, currentDomain1.max));
					i1++;
					if (i1 == max1)
						break;
					currentDomain1 = intervals[i1];
					continue;
				} else {
					result.addDom(new Interval(min, currentDomain2.max));
					i2++;
					if (i2 < domain.noIntervals()) {
						currentDomain2 = domain.getInterval(i2);
						continue;
					}
					else
						break;
				}

			}

			if (i1 < max1)
				for (; i1 < max1; i1++)
					result.addDom(intervals[i1]);

			for (; i2 < domain.noIntervals(); i2++ )
				result.addDom(domain.getInterval(i2));
				
			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			return result;
			
			
		}
		
	}

	/**
	 * It computes union of current domain and an interval min..max;
	 */
	@Override
	public Domain union(int min, int max) {

		if (size == 0)
			return new IntervalDomain(min, max);

		assert checkInvariants() == null : checkInvariants() ;
		
		IntervalDomain result = new IntervalDomain(size + 1);

		int i1 = 0;

		Interval currentInterval1 = intervals[i1];

		while (true) {

			// all intervals before and not glued (min..max) are included
			if (currentInterval1.max + 1 < min) {
				result.addDom(currentInterval1);
				i1++;
				if (i1 == size) {
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

		assert checkInvariants() == null : checkInvariants() ;
		assert result.checkInvariants() == null : result.checkInvariants() ;
		return result;		
	
	}


	/**
	 * It computes union of dom1 and value and returns the result.
	 */

	//TODO, write Junit tests.
	
	@Override
	public Domain union(int value) {

		if (size == 0)
			return new IntervalDomain(value, value);

		assert checkInvariants() == null : checkInvariants() ;
		
		IntervalDomain result = new IntervalDomain(size + 1);

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

		assert checkInvariants() == null : checkInvariants() ;
		assert result.checkInvariants() == null : result.checkInvariants() ;
		return result;

	}

	/**
	 * It returns string description of the domain (only values in the domain).
	 */
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
			S.append("} ");
		} else
			S.append(intervals[0]);

		return S.toString();

	}

	/**
	 * It returns string description of the constraints attached to the domain.
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
	 * It returns complete string description containing all relevant
	 * information.
	 */
	@Override
	public String toStringFull() {

		StringBuffer S = new StringBuffer("");

		Domain domain = this;

		do {
			if (!domain.singleton()) {
				S.append("{");

				for (int e = 0; e < size; e++) {
					S.append(intervals[e]);
					if (e + 1 < size)
						S.append(", ");
				}

				S.append("} ").append("(").append(domain.stamp()).append(") ");
			} else
				S.append(intervals[0]).append("(").append(
						String.valueOf(domain.stamp())).append(") ");

			S.append("constraints: ");

			for (Iterator<Constraint> e = domain.searchConstraints.iterator(); e
					.hasNext();)
				S.append(e.next());

			if (domain.domainID() == IntervalDomainID) {

				IntervalDomain dom = (IntervalDomain) domain;
				domain = dom.previousDomain;

			} else {
				break;
			}

		} while (domain != null);

		return S.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void fromXML(org.jdom.Element domainElement) {
		
		for (org.jdom.Element intervalElement : (List<org.jdom.Element>) domainElement.getChildren()) {
			
			Interval interval = Interval.fromXML(intervalElement);
			this.addDom(interval.min, interval.max);
		}
	}
	/**
	 * It produces xml representation of a domain.
	 */
	@Override
	public Element toXML() {

		org.jdom.Element domain = new org.jdom.Element("JaCoP.core.IntervalDomain");

		for (int i = 0; i < size; i++)
			domain.addContent(intervals[i].toXML());
		
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

	/**
	 * It updates the domain according to the minimum value and stamp value. It
	 * informs the variable of a change if it occurred.
	 */
	@Override
	public void inMin(int storeLevel, Variable var, int min) {

		assert checkInvariants() == null : checkInvariants() ;
		
		if (min > intervals[size - 1].max)
			throw failException;

		if (min <= intervals[0].min)
			return;

		if (stamp == storeLevel) {

			int pointer = 0;

			while (intervals[pointer].max < min)
				pointer++;

			int i = 0;
			if (intervals[pointer].min < min) {
				intervals[0] = new Interval(min, intervals[pointer].max);
				pointer++;
				i++;
			}

			for (; pointer < size; i++, pointer++)
				intervals[i] = intervals[pointer];
			// intervals[pointer] = null;

			size = i;

			assert checkInvariants() == null : checkInvariants() ;
			
			if (singleton()) {
				var.domainHasChanged(GROUND);
				return;
			} else {
				var.domainHasChanged(BOUND);
				return;
			}

		} else {

			assert stamp < storeLevel;

			IntervalDomain result = new IntervalDomain(size + 1);
			int pointer = 0;

			// pointer is always smaller than size as domains intersect
			while (intervals[pointer].max < min)
				pointer++;

			if (intervals[pointer].min < min) {
				result.addDom(new Interval(min, intervals[pointer++].max));
			}

			for (; pointer < size; pointer++) {
				result.addDom(intervals[pointer]);
			}

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;

			assert checkInvariants() == null : checkInvariants() ;
			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			if (result.singleton()) {
				var.domainHasChanged(GROUND);
				return;
			} else {
				var.domainHasChanged(BOUND);
				return;
			}
		}

	}

	/**
	 * It updates the domain according to the maximum value and stamp value. It
	 * informs the variable of a change if it occurred.
	 */

	@Override
	public void inMax(int storeLevel, Variable var, int max) {

		assert checkInvariants() == null : checkInvariants() ;
		
		if (max < intervals[0].min)
			throw failException;

		int currentMax = intervals[size - 1].max;

		if (max >= currentMax)
			return;

		int pointer = size - 1;

		if (stamp == storeLevel) {

			while (intervals[pointer].min > max) {
				// intervals[pointer] = null;
				pointer--;
			}

			if (intervals[pointer].max > max)
				intervals[pointer] = new Interval(intervals[pointer].min, max);

			size = pointer + 1;

			assert checkInvariants() == null : checkInvariants() ;
			
			if (singleton()) {
				var.domainHasChanged(GROUND);
				return;
			} else {
				var.domainHasChanged(BOUND);
				return;
			}

		} else {

			assert stamp < storeLevel;

			while (intervals[pointer].min > max) {
				pointer--;
			}

			IntervalDomain result = new IntervalDomain(pointer + 1);

			for (int i = 0; i < pointer; i++)
				result.addDom(intervals[i]);

			if (intervals[pointer].max > max)
				result.addDom(new Interval(intervals[pointer].min, max));
			else
				result.addDom(intervals[pointer]);

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;

			assert result.checkInvariants() == null : result.checkInvariants() ;
			assert checkInvariants() == null : checkInvariants() ;
			
			if (result.singleton()) {
				var.domainHasChanged(GROUND);
				return;
			} else {
				var.domainHasChanged(BOUND);
				return;
			}

		}

	}

	/**
	 * It updates the domain to have values only within the interval min..max.
	 * The type of update is decided by the value of stamp. It informs the
	 * variable of a change if it occurred.
	 */
	@Override
	public void in(int storeLevel, Variable var, int min, int max) {

		assert checkInvariants() == null : checkInvariants() ;
		
		assert (min <= max) : "Min value greater than max value " + min + " > " + max;

		if (max < intervals[0].min)
			throw failException;

		int currentMax = intervals[size - 1].max;
		if (min > currentMax)
			throw failException;

		if (min <= intervals[0].min && max >= currentMax)
			return;

		IntervalDomain result = new IntervalDomain(size + 1);
		int pointer = 0;

		// pointer is always smaller than size as domains intersect
		while (intervals[pointer].max < min)
			pointer++;

		if (intervals[pointer].min > max)
			throw failException;

		if (intervals[pointer].min >= min)
			if (intervals[pointer].max <= max)
				result.addDom(intervals[pointer]);
			else
				result.addDom(new Interval(intervals[pointer].min, max));
		else if (intervals[pointer].max <= max)
			result.addDom(new Interval(min, intervals[pointer].max));
		else
			result.addDom(new Interval(min, max));

		pointer++;

		while (pointer < size)
			if (intervals[pointer].max <= max)
				result.addDom(intervals[pointer++]);
			else
				break;

		if (pointer < size)
			if (intervals[pointer].min <= max)
				result.addDom(new Interval(intervals[pointer].min, max));

		if (stamp == storeLevel) {

			// Copy all intervals
			if (result.size <= intervals.length)
				System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
			else {
				intervals = new Interval[result.size];
				System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
			}

			size = result.size;

		} else {

			assert stamp < storeLevel;

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;

		}

		assert checkInvariants() == null : checkInvariants() ;
		assert result.checkInvariants() == null : result.checkInvariants() ;
		
		if (result.singleton()) {
			var.domainHasChanged(GROUND);
			return;
		} else {
			var.domainHasChanged(BOUND);
			return;
		}

	}

	/**
	 * It updates the domain to have values only within the domain. The type of
	 * update is decided by the value of stamp. It informs the variable of a
	 * change if it occurred.
	 */
	@Override
	public void in(int storeLevel, Variable var, Domain domain) {

		// System.out.println(var.domain + " " + domain);

		assert checkInvariants() == null : checkInvariants() ;
		
		assert this.stamp <= storeLevel;

		if (domain.domainID() == IntervalDomainID) {

			IntervalDomain input = (IntervalDomain) domain;

			assert input.checkInvariants() == null : input.checkInvariants() ;
			
			if (input.size == 0)
				throw failException;

			assert size != 0;

			int pointer1 = 0;
			int pointer2 = 0;

			Interval inputIntervals[] = input.intervals;
			int inputSize = input.size;
			// Chance for no event
			while (pointer2 < inputSize
					&& inputIntervals[pointer2].max < intervals[pointer1].min)
				pointer2++;

			if (pointer2 == inputSize)
				throw failException;

			// traverse within while loop until certain that change will occur
			while (intervals[pointer1].min >= inputIntervals[pointer2].min
					&& intervals[pointer1].max <= inputIntervals[pointer2].max
					&& ++pointer1 < size) {

				while (intervals[pointer1].max > inputIntervals[pointer2].max
						&& ++pointer2 < inputSize)
					;

				if (pointer2 == inputSize)
					break;
			}

			// no change
			if (pointer1 == size)
				return;

			IntervalDomain result = new IntervalDomain(this.size);
			int temp = 0;
			// add all common intervals to result as indicated by progress of
			// the previous loop
			while (temp < pointer1)
				result.addDom(intervals[temp++]);

			pointer2 = 0;

			int interval1Min = intervals[pointer1].min;
			int interval1Max = intervals[pointer1].max;
			int interval2Min = inputIntervals[pointer2].min;
			int interval2Max = inputIntervals[pointer2].max;

			while (true) {

				if (interval1Max < interval2Min) {
					pointer1++;
					if (pointer1 < size) {
						interval1Min = intervals[pointer1].min;
						interval1Max = intervals[pointer1].max;
						continue;
					} else
						break;
				} else if (interval2Max < interval1Min) {
					pointer2++;
					if (pointer2 < inputSize) {
						interval2Min = inputIntervals[pointer2].min;
						interval2Max = inputIntervals[pointer2].max;
						continue;
					} else
						break;
				} else
				// interval1Max >= interval2Min
				// interval2Max >= interval1Min
				if (interval1Min <= interval2Min) {

					if (interval1Max <= interval2Max) {
						result.addDom(new Interval(interval2Min, interval1Max));

						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					} else {
						result.addDom(inputIntervals[pointer2]);
						pointer2++;

						if (pointer2 < inputSize) {
							interval2Min = inputIntervals[pointer2].min;
							interval2Max = inputIntervals[pointer2].max;
							continue;
						} else
							break;
					}

				} else
				// interval1Max >= interval2Min
				// interval2Max >= interval1Min
				// interval1Min > interval2Min
				{
					if (interval2Max <= interval1Max) {
						result.addDom(new Interval(interval1Min, interval2Max));

						if (interval2Max >= interval1Max) {
							pointer1++;
							if (pointer1 < size) {
								interval1Min = intervals[pointer1].min;
								interval1Max = intervals[pointer1].max;
							} else
								break;
						}

						pointer2++;
						if (pointer2 < inputSize) {
							interval2Min = inputIntervals[pointer2].min;
							interval2Max = inputIntervals[pointer2].max;
							continue;
						} else
							break;
					} else {
						result.addDom(intervals[pointer1]);
						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					}

				}
			}

			if (result.isEmpty())
				throw failException;

			int returnedEvent = ANY;

			assert checkInvariants() == null : checkInvariants() ;
			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			if (result.singleton())
				returnedEvent = GROUND;
			else if (result.min() > min() || result.max() < max())
				returnedEvent = BOUND;

			if (stamp == storeLevel) {

				// Copy all intervals
				if (result.size <= intervals.length)
					System.arraycopy(result.intervals, 0, intervals, 0,
							result.size);
				else {
					intervals = new Interval[result.size];
					System.arraycopy(result.intervals, 0, intervals, 0,
							result.size);
				}

				size = result.size;

			} else {

				assert stamp < storeLevel;

				result.modelConstraints = modelConstraints;
				result.searchConstraints = searchConstraints;
				result.stamp = storeLevel;
				result.previousDomain = this;
				result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
				result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
				var.domain = result;

			}

			assert checkInvariants() == null : checkInvariants() ;
			
			var.domainHasChanged(returnedEvent);
			return;

		}

		
		if (domain.domainID() == BoundDomainID) {

			if (domain.isEmpty())
				throw failException;
			
			assert size != 0;
			
			in(storeLevel, var, domain.min(), domain.max());
			
			return;
			
		}		
		
		if (domain.isSparseRepresentation()) {	

			if (previousDomain.isSparseRepresentation()) {
				
				// Sparse domain was also used at the current level
				
				IntervalDomain result = new IntervalDomain();
								
				ValueEnumeration enumer = domain.valueEnumeration();
				
				while (enumer.hasMoreElements()) {
					int next = enumer.nextElement();
					if (this.contains(next))
						result.addDom(next, next);
					
				}
				
				if (result.isEmpty())
					throw failException;

				int returnedEvent = ANY;

				assert checkInvariants() == null : checkInvariants() ;
				
				if (result.singleton())
					returnedEvent = GROUND;
				else if (result.min() > min() || result.max() < max())
					returnedEvent = BOUND;

				if (stamp == storeLevel) {

					// Copy all intervals
					if (result.size <= intervals.length)
						System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
					else {
						intervals = new Interval[result.size];
						System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
					}

					size = result.size;

				} else {

					assert stamp < storeLevel;

					result.modelConstraints = modelConstraints;
					result.searchConstraints = searchConstraints;
					result.stamp = storeLevel;
					result.previousDomain = this;
					result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
					result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
					var.domain = result;

				}

				assert checkInvariants() == null : checkInvariants() ;
				
				var.domainHasChanged(returnedEvent);
				return;							
				
			}
			else {
				
				// Dense domain is used to specify the domain.
				
				if (domain.getSize() == 0)
					throw failException;

				assert size != 0;

				int pointer1 = 0;
				int pointer2 = 0;

//				Interval inputIntervals[] = input.intervals;
				int inputSize = domain.noIntervals();
				
				// Chance for no event
				while (pointer2 < inputSize
						&& domain.getInterval(pointer2).max < intervals[pointer1].min)
					pointer2++;

				if (pointer2 == inputSize)
					throw failException;

				// traverse within while loop until certain that change will occur
				while (intervals[pointer1].min >= domain.getInterval(pointer2).min
						&& intervals[pointer1].max <= domain.getInterval(pointer2).max
						&& ++pointer1 < size) {

					while (intervals[pointer1].max > domain.getInterval(pointer2).max
							&& ++pointer2 < inputSize)
						;

					if (pointer2 == inputSize)
						break;
				}

				// no change
				if (pointer1 == size)
					return;

				IntervalDomain result = new IntervalDomain(this.size);
				int temp = 0;
				// add all common intervals to result as indicated by progress of
				// the previous loop
				while (temp < pointer1)
					result.addDom(intervals[temp++]);

				pointer2 = 0;

				int interval1Min = intervals[pointer1].min;
				int interval1Max = intervals[pointer1].max;
				int interval2Min = domain.getInterval(pointer2).min;
				int interval2Max = domain.getInterval(pointer2).max;

				while (true) {

					if (interval1Max < interval2Min) {
						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					} else if (interval2Max < interval1Min) {
						pointer2++;
						if (pointer2 < inputSize) {
							interval2Min = domain.getInterval(pointer2).min;
							interval2Max = domain.getInterval(pointer2).max;
							continue;
						} else
							break;
					} else
					// interval1Max >= interval2Min
					// interval2Max >= interval1Min
					if (interval1Min <= interval2Min) {

						if (interval1Max <= interval2Max) {
							result.addDom(new Interval(interval2Min, interval1Max));

							pointer1++;
							if (pointer1 < size) {
								interval1Min = intervals[pointer1].min;
								interval1Max = intervals[pointer1].max;
								continue;
							} else
								break;
						} else {
							result.addDom(domain.getInterval(pointer2));
							pointer2++;

							if (pointer2 < inputSize) {
								interval2Min = domain.getInterval(pointer2).min;
								interval2Max = domain.getInterval(pointer2).max;
								continue;
							} else
								break;
						}

					} else
					// interval1Max >= interval2Min
					// interval2Max >= interval1Min
					// interval1Min > interval2Min
					{
						if (interval2Max <= interval1Max) {
							result.addDom(new Interval(interval1Min, interval2Max));

							if (interval2Max >= interval1Max) {
								pointer1++;
								if (pointer1 < size) {
									interval1Min = intervals[pointer1].min;
									interval1Max = intervals[pointer1].max;
								} else
									break;
							}

							pointer2++;
							if (pointer2 < inputSize) {
								interval2Min = domain.getInterval(pointer2).min;
								interval2Max = domain.getInterval(pointer2).max;
								continue;
							} else
								break;
						} else {
							result.addDom(intervals[pointer1]);
							pointer1++;
							if (pointer1 < size) {
								interval1Min = intervals[pointer1].min;
								interval1Max = intervals[pointer1].max;
								continue;
							} else
								break;
						}

					}
				}

				if (result.isEmpty())
					throw failException;

				int returnedEvent = ANY;

				assert checkInvariants() == null : checkInvariants() ;
				assert result.checkInvariants() == null : result.checkInvariants() ;
				
				if (result.singleton())
					returnedEvent = GROUND;
				else if (result.min() > min() || result.max() < max())
					returnedEvent = BOUND;

				if (stamp == storeLevel) {

					// Copy all intervals
					if (result.size <= intervals.length)
						System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
					else {
						intervals = new Interval[result.size];
						System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
					}

					size = result.size;

				} else {

					assert stamp < storeLevel;

					result.modelConstraints = modelConstraints;
					result.searchConstraints = searchConstraints;
					result.stamp = storeLevel;
					result.previousDomain = this;
					result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
					result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
					var.domain = result;

				}

				assert checkInvariants() == null : checkInvariants() ;
				
				var.domainHasChanged(returnedEvent);
				return;
				
				
			}
				
			
		}
		else {
					
			if (domain.getSize() == 0)
				throw failException;

			int pointer1 = 0;
			int pointer2 = 0;

//			Interval domain.getInterval(] = input.intervals;
			int inputSize = domain.noIntervals();
				
			// Chance for no event
			while (pointer2 < inputSize
					&& domain.getInterval(pointer2).max < intervals[pointer1].min)
				pointer2++;
			
			if (pointer2 == inputSize)
				throw failException;

			// traverse within while loop until certain that change will occur
			while (intervals[pointer1].min >= domain.getInterval(pointer2).min
					&& intervals[pointer1].max <= domain.getInterval(pointer2).max
					&& ++pointer1 < size) {

				while (intervals[pointer1].max > domain.getInterval(pointer2).max
						&& ++pointer2 < inputSize)
					;

				if (pointer2 == inputSize)
					break;
			}

			// no change
			if (pointer1 == size)
				return;

			IntervalDomain result = new IntervalDomain(this.size);
			int temp = 0;
			// add all common intervals to result as indicated by progress of
			// the previous loop
			while (temp < pointer1)
				result.addDom(intervals[temp++]);

			pointer2 = 0;

			int interval1Min = intervals[pointer1].min;
			int interval1Max = intervals[pointer1].max;
			int interval2Min = domain.getInterval(pointer2).min;
			int interval2Max = domain.getInterval(pointer2).max;

			while (true) {

				if (interval1Max < interval2Min) {
					pointer1++;
					if (pointer1 < size) {
						interval1Min = intervals[pointer1].min;
						interval1Max = intervals[pointer1].max;
						continue;
					} else
						break;
				} else if (interval2Max < interval1Min) {
					pointer2++;
					if (pointer2 < inputSize) {
						interval2Min = domain.getInterval(pointer2).min;
						interval2Max = domain.getInterval(pointer2).max;
						continue;
					} else
						break;
				} else
				// interval1Max >= interval2Min
				// interval2Max >= interval1Min
				if (interval1Min <= interval2Min) {

					if (interval1Max <= interval2Max) {
						result.addDom(new Interval(interval2Min, interval1Max));

						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					} else {
						result.addDom(domain.getInterval(pointer2));
						pointer2++;

						if (pointer2 < inputSize) {
							interval2Min = domain.getInterval(pointer2).min;
							interval2Max = domain.getInterval(pointer2).max;
							continue;
						} else
							break;
					}

				} else
				// interval1Max >= interval2Min
				// interval2Max >= interval1Min
				// interval1Min > interval2Min
				{
					if (interval2Max <= interval1Max) {
						result.addDom(new Interval(interval1Min, interval2Max));

						if (interval2Max >= interval1Max) {
							pointer1++;
							if (pointer1 < size) {
								interval1Min = intervals[pointer1].min;
								interval1Max = intervals[pointer1].max;
							} else
								break;
						}

						pointer2++;
						if (pointer2 < inputSize) {
							interval2Min = domain.getInterval(pointer2).min;
							interval2Max = domain.getInterval(pointer2).max;
							continue;
						} else
							break;
					} else {
						result.addDom(intervals[pointer1]);
						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					}

				}
			}

			if (result.isEmpty())
				throw failException;

			int returnedEvent = ANY;

			assert checkInvariants() == null : checkInvariants() ;
			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			if (result.singleton())
				returnedEvent = GROUND;
			else if (result.min() > min() || result.max() < max())
				returnedEvent = BOUND;

			if (stamp == storeLevel) {

				// Copy all intervals
				if (result.size <= intervals.length)
					System.arraycopy(result.intervals, 0, intervals, 0,
							result.size);
				else {
					intervals = new Interval[result.size];
					System.arraycopy(result.intervals, 0, intervals, 0,
							result.size);
				}

				size = result.size;

			} else {

				assert stamp < storeLevel;

				result.modelConstraints = modelConstraints;
				result.searchConstraints = searchConstraints;
				result.stamp = storeLevel;
				result.previousDomain = this;
				result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
				result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
				var.domain = result;

			}

			assert checkInvariants() == null : checkInvariants() ;
			
			var.domainHasChanged(returnedEvent);
			return;			
			
			
			
		}
		
	}

	/**
	 * It returns the number intervals into which this domain is split.
	 */

	@Override
	public int noIntervals() {

		return size;

	}

	/**
	 * It specifies the position of the interval which contains specified value. 
	 * @param value value for which an interval containing it is searched.
	 * @return the position of the interval containing the specified value.
	 */
	public int intervalNo(int value) {

		for (int i = 0; i < size; i++)
			if (intervals[i].min > value)
				continue;
			else if (intervals[i].max < value)
				continue;
			else
				return i;

		return -1;

	}

	@Override
	public Interval getInterval(int position) {

		assert (position < size);

		return intervals[position];
	}

	/**
	 * It updates the domain to not contain the value complement. It informs the
	 * variable of a change if it occurred.
	 */
	@Override
	public void inComplement(int storeLevel, Variable var, int complement) {

		assert checkInvariants() == null : checkInvariants() ;
		
		//	System.out.println(var.domain + " " + complement);

		int counter = intervalNo(complement);

		if (counter == -1)
			return;

		if (storeLevel == stamp) {

			if (intervals[counter].min == complement) {

				if (intervals[counter].max != complement) {

					intervals[counter] = new Interval(complement + 1,
							intervals[counter].max);

					assert checkInvariants() == null : checkInvariants() ;
					
					if (singleton()) {
						var.domainHasChanged(GROUND);
						return;
					}

					if (counter == 0) {
						var.domainHasChanged(BOUND);
						return;
					} else {
						var.domainHasChanged(ANY);
						return;
					}
				} else {
					// if domain like this 1..3, 5, 7..10, and 5 being removed.

					if (singleton(complement))
						throw failException;

					for (int i = counter; i < size - 1; i++)
						intervals[i] = intervals[i + 1];

					size--;

					assert checkInvariants() == null : checkInvariants() ;
					
					if (singleton()) {
						var.domainHasChanged(GROUND);
						return;
					}

					// below size, instead of size-1 as size has been
					// just decreased, e.g. domain like 1..3, 5 and 5
					// being removed.

					if (counter == 0 || counter == size) {
						var.domainHasChanged(BOUND);
						return;
					} else {
						var.domainHasChanged(ANY);
						return;
					}

				}
			}

			if (intervals[counter].max == complement) {

				// domain like this 1..3, 5, 7..10, and 5 being
				// removed taken care of above.

				intervals[counter] = new Interval(intervals[counter].min,
						complement - 1);

				assert checkInvariants() == null : checkInvariants() ;
				
				if (singleton()) {
					var.domainHasChanged(GROUND);
					return;
				}

				if (counter == size - 1) {
					var.domainHasChanged(BOUND);
					return;
				} else {
					var.domainHasChanged(ANY);
					return;
				}
			}

			if (size + 1 < intervals.length) {
				for (int i = size; i > counter + 1; i--)
					intervals[i] = intervals[i - 1];
			} else {
				Interval[] updatedIntervals = new Interval[size + 1];
				System.arraycopy(intervals, 0, updatedIntervals, 0,
								counter + 1);
				System.arraycopy(intervals, counter, updatedIntervals,
						counter + 1, size - counter);
				intervals = updatedIntervals;
			}

			int max = intervals[counter].max;
			intervals[counter] = new Interval(intervals[counter].min,
					complement - 1);
			intervals[counter + 1] = new Interval(complement + 1, max);

			// One interval has been split, size increased by one.
			size++;

			assert checkInvariants() == null : checkInvariants() ;
			
			var.domainHasChanged(ANY);
			return;
			
		} else {

			if (singleton(complement))
				throw failException;

			assert storeLevel > stamp;

			IntervalDomain result = new IntervalDomain(this.size + 1);

			// variable obtains new domain, current one (this) becomes
			// previousDomain

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;

			if (intervals[counter].min == complement) {

				if (intervals[counter].max != complement) {

					System.arraycopy(intervals, 0, result.intervals, 0, size);

					result.intervals[counter] = new Interval(complement + 1,
							result.intervals[counter].max);

					result.size = size;

					assert result.checkInvariants() == null : result.checkInvariants() ;
					assert checkInvariants() == null : checkInvariants() ;
					
					if (result.singleton()) {
						var.domainHasChanged(GROUND);
						return;
					}

					if (counter == 0) {
						var.domainHasChanged(BOUND);
						return;
					} else {
						var.domainHasChanged(ANY);
						return;
					}
				} else {
					// if domain like this 1..3, 5, 7..10, and 5 being removed.
					System.arraycopy(intervals, 0, result.intervals, 0,
									counter);

					System.arraycopy(intervals, counter + 1, result.intervals,
							counter, size - counter - 1);

					result.size = size - 1;

					assert result.checkInvariants() == null : result.checkInvariants() ;
					
					if (result.singleton()) {
						var.domainHasChanged(GROUND);
						return;
					}

					if (counter == 0 || counter == size - 1) {
						var.domainHasChanged(BOUND);
						return;
					} else {
						var.domainHasChanged(ANY);
						return;
					}

				}
			}

			if (intervals[counter].max == complement) {

				// domain like this 1..3, 5, 7..10, and 5 being removed taken
				// care of above.

				System.arraycopy(intervals, 0, result.intervals, 0, size);

				result.intervals[counter] = new Interval(
						result.intervals[counter].min, complement - 1);

				result.size = size;

				assert checkInvariants() == null : checkInvariants() ;
				assert result.checkInvariants() == null : result.checkInvariants() ;
				
				if (result.singleton()) {
					var.domainHasChanged(GROUND);
					return;
				}
				if (counter == size - 1) {
					var.domainHasChanged(BOUND);
					return;
				} else {
					var.domainHasChanged(ANY);
					return;
				}
			}

			// if domain like this 1..3 and value 2 being removed, or
			// 1..3, 5..7, 10..20, and value 6 being removed.

			// length of result is by default one longer than size of this.

			if (size != 1) {
				System
						.arraycopy(intervals, 0, result.intervals, 0,
								counter + 1);
				System.arraycopy(intervals, counter, result.intervals,
						counter + 1, size - counter);
			}

			int max = intervals[counter].max;
			result.intervals[counter] = new Interval(intervals[counter].min,
					complement - 1);
			result.intervals[counter + 1] = new Interval(complement + 1, max);

			result.size = size + 1;

			/*
			 * result.modelConstraints = modelConstraints;
			 * result.searchConstraints = searchConstraints; result.stamp =
			 * storeLevel; result.previousDomain = this;
			 * result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			 * result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			 * var.domain = result;
			 */

			var.domainHasChanged(ANY);
			return;

		}

	}

	// TODO check and test inComplement below.

	@Override
	public void inComplement(int storeLevel, Variable var, int min, int max) {
		
		assert checkInvariants() == null : checkInvariants() ;
		
		if (intervals[0].min > max || intervals[size - 1].max < min)
			return;

		int counter = 0;

		while (intervals[counter].max < min)
			counter++;

		if (intervals[counter].min > max)
			return;

		if (min <= min() && max >= max())
			throw failException;

		if (storeLevel == stamp) {

			int noRemoved = 0;

			if (intervals[counter].min < min) {
				// intervals[counter].min..min-1

				if (intervals[counter].max > max) {
					// max+1..intervals[counter].max
					if (size < intervals.length) {
						// copy elements to make one hole for new interval

						for (int i = size; i > counter; i--)
							intervals[i] = intervals[i - 1];

						intervals[counter + 1] = new Interval(max + 1,
								intervals[counter].max);
						intervals[counter] = new Interval(
								intervals[counter].min, min - 1);

						size++;

						assert checkInvariants() == null : checkInvariants() ;
						
						var.domainHasChanged(ANY);

					} else {
						// create new array and copy

						Interval[] oldIntervals = intervals;
						intervals = new Interval[oldIntervals.length + 5];

						if (counter > 0)
							System.arraycopy(oldIntervals, 0, intervals, 0,
									counter);

						System.arraycopy(oldIntervals, counter + 1, intervals,
								counter + 2, size - counter - 1);

						intervals[counter + 1] = new Interval(max + 1,
								oldIntervals[counter].max);
						intervals[counter] = new Interval(
								oldIntervals[counter].min, min - 1);

						size++;

						assert checkInvariants() == null : checkInvariants() ;
						
						var.domainHasChanged(ANY);
					}
				} else {
					// intervals[counter].max <= max
					// intervals[counter].min..min-1

					intervals[counter] = new Interval(intervals[counter].min,
							min - 1);

					int position = ++counter;

					while (position < size && intervals[position].max <= max) {
						position++;
						noRemoved++;
					}

					if (noRemoved > 0) {

						for (int i = counter; i + noRemoved < size; i++)
							intervals[i] = intervals[i + noRemoved];
					
					}

					size -= noRemoved;

					if (counter < size && intervals[counter].min <= max)
						intervals[counter] = new Interval(max + 1,
								intervals[counter].max);

					assert checkInvariants() == null : checkInvariants() ;

					if (var.singleton())
						var.domainHasChanged(GROUND);
					else
						if (max() > max)
							var.domainHasChanged(BOUND);
						else
							var.domainHasChanged(ANY);
					return;
				}

			} else {
				// intervals[counter].min >= min
				if (intervals[counter].max > max) {
					// max+1..intervals[counter].max

					intervals[counter] = new Interval(max + 1,
							intervals[counter].max);

					assert checkInvariants() == null : checkInvariants() ;
					
					if (singleton()) {
						var.domainHasChanged(GROUND);
						return;
					}
					if (counter == 0)
						var.domainHasChanged(BOUND);
					else
						var.domainHasChanged(ANY);
					return;

				} else {
					// intervals[counter] is removed

					int position = counter;

					while (position < size && intervals[position].max <= max) {
						position++;
						noRemoved++;
					}

					for (int i = counter; i + noRemoved < size; i++)
						intervals[i] = intervals[i + noRemoved];

					size -= noRemoved;

					if (counter < size && intervals[counter].min <= max)
						intervals[counter] = new Interval(max + 1,
								intervals[counter].max);

					assert checkInvariants() == null : checkInvariants() ;
					
					if (singleton()) {
						var.domainHasChanged(GROUND);
						return;
					}
					if (counter == 0)
						var.domainHasChanged(BOUND);
					else
						var.domainHasChanged(ANY);
					return;

				}

			}

		} else {

			assert storeLevel > stamp;

			IntervalDomain result = new IntervalDomain(this.size + 1);

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			result.size = size;
			var.domain = result;

			int noRemoved = 0;

			for (int i = 0; i < counter; i++)
				result.intervals[i] = intervals[i];

			if (intervals[counter].min < min) {
				// intervals[counter].min..min-1

				if (intervals[counter].max > max) {
					// max+1..intervals[counter].max
					// copy elements to make one hole for new interval

					for (int i = size; i > counter; i--)
						result.intervals[i] = intervals[i - 1];

					result.intervals[counter + 1] = new Interval(max + 1,
							intervals[counter].max);
					result.intervals[counter] = new Interval(
							intervals[counter].min, min - 1);

					result.size++;

					assert result.checkInvariants() == null : result.checkInvariants() ;
					assert checkInvariants() == null : checkInvariants() ;
					
					var.domainHasChanged(ANY);

				} else {

					result.intervals[counter] = new Interval(
							intervals[counter].min, min - 1);

					int position = ++counter;

					while (position < size && intervals[position].max <= max) {
						position++;
						noRemoved++;
					}

					for (int i = counter; i + noRemoved < size; i++)
						result.intervals[i] = intervals[i + noRemoved];

					if (counter + noRemoved < size && intervals[counter+noRemoved].min <= max)
						result.intervals[counter] = new Interval(max + 1, intervals[counter+noRemoved].max);
					
					result.size -= noRemoved;

					assert checkInvariants() == null : checkInvariants() ;
					assert result.checkInvariants() == null : result.checkInvariants() ;
					
					if (var.singleton())
						var.domainHasChanged(GROUND);
					else
						if (max() > max)
							var.domainHasChanged(BOUND);
						else
							var.domainHasChanged(ANY);
					
					return;
				}

			} else {

				if (intervals[counter].max > max) {
					// max+1..intervals[counter].max

					for (int i = counter + 1; i < size; i++)
						result.intervals[i] = intervals[i];

					result.intervals[counter] = new Interval(max + 1,
							intervals[counter].max);

					assert checkInvariants() == null : checkInvariants() ;
					assert result.checkInvariants() == null : result.checkInvariants() ;
					
					if (result.singleton()) {
						var.domainHasChanged(GROUND);
						return;
					}
					if (counter == 0)
						var.domainHasChanged(BOUND);
					else
						var.domainHasChanged(ANY);
					return;

				} else {
					// intervals[counter] is removed

					int position = counter;

					while (position < size && intervals[position].max <= max) {
						position++;
						noRemoved++;
					}

					for (int i = counter; i + noRemoved < size; i++)
						result.intervals[i] = intervals[i + noRemoved];

					result.size -= noRemoved;
					
			//		if (counter < size && intervals[counter].min <= max)
			//			result.intervals[counter] = new Interval(max + 1,
			//					intervals[counter].max);

					if (counter + noRemoved < size && intervals[counter+noRemoved].min <= max)
						result.intervals[counter] = new Interval(max + 1, intervals[counter+noRemoved].max);
					
					assert checkInvariants() == null : checkInvariants() ;
					assert result.checkInvariants() == null : result.checkInvariants() ;

					if (var.singleton())
						var.domainHasChanged(GROUND);
					else
						if (max() >= max || min <= min())
							var.domainHasChanged(BOUND);
						else
							var.domainHasChanged(ANY);
					return;

				}

			}

		}

	}

	/**
	 * It updates the domain to contain the elements as specifed by the domain,
	 * which is shifted. E.g. {1..4} + 3 = 4..7
	 */
	@Override
	public void inShift(int storeLevel, Variable var, Domain domain, int shift) {

		assert checkInvariants() == null : checkInvariants() ;
		assert this.stamp <= storeLevel;

		if (domain.domainID() == IntervalDomainID) {

			IntervalDomain input = (IntervalDomain) domain;

			if (input.size == 0)
				throw failException;

			assert size != 0;

			int pointer1 = 0;
			int pointer2 = 0;
			int inputSize = input.size;

			Interval[] inputIntervals = input.intervals;
			
			// Chance for no event
			// traverse within while loop until certain that change will occur

			while (pointer2 < inputSize
					&& inputIntervals[pointer2].max + shift < intervals[pointer1].min)
				pointer2++;

			if (pointer2 == inputSize)
				throw failException;

			while (intervals[pointer1].min >= inputIntervals[pointer2].min
					+ shift
					&& intervals[pointer1].max <= inputIntervals[pointer2].max
							+ shift && ++pointer1 < size) {

				while (intervals[pointer1].max > inputIntervals[pointer2].max
						+ shift
						&& ++pointer2 < input.size)
					;

				if (pointer2 == input.size)
					break;
			}

			// no change
			if (pointer1 == size)
				return;

			IntervalDomain result = new IntervalDomain(size);
			pointer2 = 0;

			// add all common intervals to result as indicated by progress of
			// the previous loop
			while (pointer2 < pointer1)
				result.addDom(intervals[pointer2++]);

			pointer2 = 0;

			int interval1Min = intervals[pointer1].min;
			int interval1Max = intervals[pointer1].max;
			int interval2Min = inputIntervals[pointer2].min + shift;
			int interval2Max = inputIntervals[pointer2].max + shift;
			while (true) {

				if (interval1Max < interval2Min) {
					pointer1++;
					if (pointer1 < size) {
						interval1Min = intervals[pointer1].min;
						interval1Max = intervals[pointer1].max;
						continue;
					} else
						break;
				} else if (interval2Max < interval1Min) {
					pointer2++;
					if (pointer2 < inputSize) {
						interval2Min = inputIntervals[pointer2].min + shift;
						interval2Max = inputIntervals[pointer2].max + shift;
						continue;
					} else
						break;
				} else
				// interval1Max >= interval2Min
				// interval2Max >= interval1Min
				if (interval1Min <= interval2Min) {

					if (interval1Max <= interval2Max) {
						result.addDom(new Interval(interval2Min, interval1Max));

						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					} else {
						result.addDom(new Interval(inputIntervals[pointer2].min
								+ shift, inputIntervals[pointer2].max + shift));

						pointer2++;

						if (pointer2 < inputSize) {
							interval2Min = inputIntervals[pointer2].min + shift;
							interval2Max = inputIntervals[pointer2].max + shift;
							continue;
						} else
							break;
					}

				} else
				// interval1Max >= interval2Min
				// interval2Max >= interval1Min
				// interval1Min > interval2Min
				{
					if (interval2Max <= interval1Max) {
						result.addDom(new Interval(interval1Min, interval2Max));

						if (interval2Max >= interval1Max) {
							pointer1++;
							if (pointer1 < size) {
								// shift has been removed.
								interval1Min = intervals[pointer1].min;
								interval1Max = intervals[pointer1].max;
							} else
								break;
						}

						pointer2++;
						if (pointer2 < inputSize) {
							interval2Min = inputIntervals[pointer2].min + shift;
							interval2Max = inputIntervals[pointer2].max + shift;
							continue;
						} else
							break;
					} else {
						result.addDom(intervals[pointer1]);
						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					}

				}
			}

			if (result.isEmpty())
				throw failException;

			assert checkInvariants() == null : checkInvariants() ;
			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			int returnedEvent = ANY;

			if (result.singleton()) {
				returnedEvent = GROUND;
			} else if (result.min() > min() || result.max() < max()) {
				returnedEvent = BOUND;
			}

			if (stamp == storeLevel) {

				// Copy all intervals
				if (result.size <= intervals.length)
					System.arraycopy(result.intervals, 0, intervals, 0,
							result.size);
				else {
					intervals = new Interval[result.size];
					System.arraycopy(result.intervals, 0, intervals, 0,
							result.size);
				}

				size = result.size;

			} else {

				assert stamp < storeLevel;

				result.modelConstraints = modelConstraints;
				result.searchConstraints = searchConstraints;
				result.stamp = storeLevel;
				result.previousDomain = this;
				result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
				result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
				var.domain = result;

			}

			var.domainHasChanged(returnedEvent);
			return;

		}

		if (domain.domainID() == BoundDomainID) {
			
			if (domain.isEmpty())
				throw failException;
			
			in(storeLevel, var, domain.min()+shift, domain.max()+shift);
			return;
			
		}
		
		if (domain.isSparseRepresentation()) {	

			if (previousDomain.isSparseRepresentation()) {
				
				// Sparse domain was also used at the current level
				
				IntervalDomain result = new IntervalDomain();
								
				ValueEnumeration enumer = domain.valueEnumeration();
				
				while (enumer.hasMoreElements()) {
					int next = enumer.nextElement() + shift;
					if (this.contains(next))
						result.addDom(next, next);
					
				}
				
				if (result.isEmpty())
					throw failException;

				int returnedEvent = ANY;

				assert checkInvariants() == null : checkInvariants() ;
				
				if (result.singleton())
					returnedEvent = GROUND;
				else if (result.min() > min() || result.max() < max())
					returnedEvent = BOUND;

				if (stamp == storeLevel) {

					// Copy all intervals
					if (result.size <= intervals.length)
						System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
					else {
						intervals = new Interval[result.size];
						System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
					}

					size = result.size;

				} else {

					assert stamp < storeLevel;

					result.modelConstraints = modelConstraints;
					result.searchConstraints = searchConstraints;
					result.stamp = storeLevel;
					result.previousDomain = this;
					result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
					result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
					var.domain = result;

				}

				assert checkInvariants() == null : checkInvariants() ;
				
				var.domainHasChanged(returnedEvent);
				return;							
				
			}
			else {
				
				// Dense domain is used to specify the domain.
				
				if (domain.getSize() == 0)
					throw failException;

				assert size != 0;

				int pointer1 = 0;
				int pointer2 = 0;

//				Interval inputIntervals[] = input.intervals;
				int inputSize = domain.noIntervals();
				
				// Chance for no event
				while (pointer2 < inputSize
						&& domain.getInterval(pointer2).max+shift < intervals[pointer1].min+shift)
					pointer2++;

				if (pointer2 == inputSize)
					throw failException;

				// traverse within while loop until certain that change will occur
				while (intervals[pointer1].min >= domain.getInterval(pointer2).min+shift
						&& intervals[pointer1].max <= domain.getInterval(pointer2).max+shift
						&& ++pointer1 < size) {

					while (intervals[pointer1].max > domain.getInterval(pointer2).max+shift
							&& ++pointer2 < inputSize)
						;

					if (pointer2 == inputSize)
						break;
				}

				// no change
				if (pointer1 == size)
					return;

				IntervalDomain result = new IntervalDomain(this.size);
				int temp = 0;
				// add all common intervals to result as indicated by progress of
				// the previous loop
				while (temp < pointer1)
					result.addDom(intervals[temp++]);

				pointer2 = 0;

				int interval1Min = intervals[pointer1].min;
				int interval1Max = intervals[pointer1].max;
				int interval2Min = domain.getInterval(pointer2).min+shift;
				int interval2Max = domain.getInterval(pointer2).max+shift;

				while (true) {

					if (interval1Max < interval2Min) {
						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					} else if (interval2Max < interval1Min) {
						pointer2++;
						if (pointer2 < inputSize) {
							interval2Min = domain.getInterval(pointer2).min+shift;
							interval2Max = domain.getInterval(pointer2).max+shift;
							continue;
						} else
							break;
					} else
					// interval1Max >= interval2Min
					// interval2Max >= interval1Min
					if (interval1Min <= interval2Min) {

						if (interval1Max <= interval2Max) {
							result.addDom(new Interval(interval2Min, interval1Max));

							pointer1++;
							if (pointer1 < size) {
								interval1Min = intervals[pointer1].min;
								interval1Max = intervals[pointer1].max;
								continue;
							} else
								break;
						} else {
							result.addDom(new Interval(domain.getInterval(pointer2).min+shift, 
													   domain.getInterval(pointer2).max+shift));
							pointer2++;

							if (pointer2 < inputSize) {
								interval2Min = domain.getInterval(pointer2).min+shift;
								interval2Max = domain.getInterval(pointer2).max+shift;
								continue;
							} else
								break;
						}

					} else
					// interval1Max >= interval2Min
					// interval2Max >= interval1Min
					// interval1Min > interval2Min
					{
						if (interval2Max <= interval1Max) {
							result.addDom(new Interval(interval1Min, interval2Max));

							if (interval2Max >= interval1Max) {
								pointer1++;
								if (pointer1 < size) {
									interval1Min = intervals[pointer1].min;
									interval1Max = intervals[pointer1].max;
								} else
									break;
							}

							pointer2++;
							if (pointer2 < inputSize) {
								interval2Min = domain.getInterval(pointer2).min+shift;
								interval2Max = domain.getInterval(pointer2).max+shift;
								continue;
							} else
								break;
						} else {
							result.addDom(intervals[pointer1]);
							pointer1++;
							if (pointer1 < size) {
								interval1Min = intervals[pointer1].min;
								interval1Max = intervals[pointer1].max;
								continue;
							} else
								break;
						}

					}
				}

				if (result.isEmpty())
					throw failException;

				int returnedEvent = ANY;

				assert checkInvariants() == null : checkInvariants() ;
				assert result.checkInvariants() == null : result.checkInvariants() ;
				
				if (result.singleton())
					returnedEvent = GROUND;
				else if (result.min() > min() || result.max() < max())
					returnedEvent = BOUND;

				if (stamp == storeLevel) {

					// Copy all intervals
					if (result.size <= intervals.length)
						System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
					else {
						intervals = new Interval[result.size];
						System.arraycopy(result.intervals, 0, intervals, 0,
								result.size);
					}

					size = result.size;

				} else {

					assert stamp < storeLevel;

					result.modelConstraints = modelConstraints;
					result.searchConstraints = searchConstraints;
					result.stamp = storeLevel;
					result.previousDomain = this;
					result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
					result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
					var.domain = result;

				}

				assert checkInvariants() == null : checkInvariants() ;
				
				var.domainHasChanged(returnedEvent);
				return;
				
				
			}
				
			
		}
		else {
					
			if (domain.getSize() == 0)
				throw failException;

			int pointer1 = 0;
			int pointer2 = 0;

//			Interval domain.getInterval(] = input.intervals;
			int inputSize = domain.noIntervals();
				
			// Chance for no event
			while (pointer2 < inputSize
					&& domain.getInterval(pointer2).max+shift < intervals[pointer1].min)
				pointer2++;
			
			if (pointer2 == inputSize)
				throw failException;

			// traverse within while loop until certain that change will occur
			while (intervals[pointer1].min >= domain.getInterval(pointer2).min+shift
					&& intervals[pointer1].max <= domain.getInterval(pointer2).max+shift
					&& ++pointer1 < size) {

				while (intervals[pointer1].max > domain.getInterval(pointer2).max+shift
						&& ++pointer2 < inputSize)
					;

				if (pointer2 == inputSize)
					break;
			}

			// no change
			if (pointer1 == size)
				return;

			IntervalDomain result = new IntervalDomain(this.size);
			int temp = 0;
			// add all common intervals to result as indicated by progress of
			// the previous loop
			while (temp < pointer1)
				result.addDom(intervals[temp++]);

			pointer2 = 0;

			int interval1Min = intervals[pointer1].min;
			int interval1Max = intervals[pointer1].max;
			int interval2Min = domain.getInterval(pointer2).min+shift;
			int interval2Max = domain.getInterval(pointer2).max+shift;

			while (true) {

				if (interval1Max < interval2Min) {
					pointer1++;
					if (pointer1 < size) {
						interval1Min = intervals[pointer1].min;
						interval1Max = intervals[pointer1].max;
						continue;
					} else
						break;
				} else if (interval2Max < interval1Min) {
					pointer2++;
					if (pointer2 < inputSize) {
						interval2Min = domain.getInterval(pointer2).min+shift;
						interval2Max = domain.getInterval(pointer2).max+shift;
						continue;
					} else
						break;
				} else
				// interval1Max >= interval2Min
				// interval2Max >= interval1Min
				if (interval1Min <= interval2Min) {

					if (interval1Max <= interval2Max) {
						result.addDom(new Interval(interval2Min, interval1Max));

						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					} else {
						result.addDom(new Interval(domain.getInterval(pointer2).min+shift, 
												   domain.getInterval(pointer2).max+shift));
						pointer2++;

						if (pointer2 < inputSize) {
							interval2Min = domain.getInterval(pointer2).min+shift;
							interval2Max = domain.getInterval(pointer2).max+shift;
							continue;
						} else
							break;
					}

				} else
				// interval1Max >= interval2Min
				// interval2Max >= interval1Min
				// interval1Min > interval2Min
				{
					if (interval2Max <= interval1Max) {
						result.addDom(new Interval(interval1Min, interval2Max));

						if (interval2Max >= interval1Max) {
							pointer1++;
							if (pointer1 < size) {
								interval1Min = intervals[pointer1].min;
								interval1Max = intervals[pointer1].max;
							} else
								break;
						}

						pointer2++;
						if (pointer2 < inputSize) {
							interval2Min = domain.getInterval(pointer2).min+shift;
							interval2Max = domain.getInterval(pointer2).max+shift;
							continue;
						} else
							break;
					} else {
						result.addDom(intervals[pointer1]);
						pointer1++;
						if (pointer1 < size) {
							interval1Min = intervals[pointer1].min;
							interval1Max = intervals[pointer1].max;
							continue;
						} else
							break;
					}

				}
			}

			if (result.isEmpty())
				throw failException;

			int returnedEvent = ANY;

			assert checkInvariants() == null : checkInvariants() ;
			assert result.checkInvariants() == null : result.checkInvariants() ;
			
			if (result.singleton())
				returnedEvent = GROUND;
			else if (result.min() > min() || result.max() < max())
				returnedEvent = BOUND;

			if (stamp == storeLevel) {

				// Copy all intervals
				if (result.size <= intervals.length)
					System.arraycopy(result.intervals, 0, intervals, 0,
							result.size);
				else {
					intervals = new Interval[result.size];
					System.arraycopy(result.intervals, 0, intervals, 0,
							result.size);
				}

				size = result.size;

			} else {

				assert stamp < storeLevel;

				result.modelConstraints = modelConstraints;
				result.searchConstraints = searchConstraints;
				result.stamp = storeLevel;
				result.previousDomain = this;
				result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
				result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
				var.domain = result;

			}

			assert checkInvariants() == null : checkInvariants() ;
			
			var.domainHasChanged(returnedEvent);
			return;			
			
			
			
		}
		
	}

	/**
	 * It returns an unique identifier of the domain.
	 */
	@Override
	public int domainID() {
		return IntervalDomainID;
	}

	/**
	 * It specifies if the domain type is more suited to representing sparse
	 * domain.
	 */
	@Override
	public boolean isSparseRepresentation() {
		return false;
	}

	/**
	 * It specifies if domain is a finite domain of numeric values (integers).
	 */
	@Override
	public boolean isNumeric() {
		return true;
	}

	/**
	 * It returns the left most element of the given interval.
	 */
	@Override
	public int leftElement(int intervalNo) {

		assert (intervalNo < size);
		return intervals[intervalNo].min;
	}

	/**
	 * It returns the left most element of the given interval.
	 */
	@Override
	public int rightElement(int intervalNo) {

		assert (intervalNo < size);
		return intervals[intervalNo].max;
	}

	/**
	 * It removes a level of a domain. If domain is represented as a list of
	 * domains, the domain pointer within variable will be updated.
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
	 * It adds a constraint to a domain, it should only be called by
	 * putConstraint function of Variable object. putConstraint function from
	 * Variable must make a copy of a vector of constraints if vector was not
	 * cloned.
	 */

	@Override
	public void putModelConstraint(int storeLevel, Variable var, Constraint C,
			int pruningEvent) {

		if (stamp < storeLevel) {

			IntervalDomain result = this.cloneLight();

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

				IntervalDomain result = this.cloneLight();

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
	 * It removes a constraint from a domain, it should only be called by
	 * removeConstraint function of Variable object.
	 * @param storeLevel the current level of the store.
	 * @param var the variable for which the constraint is being removed.
	 * @param C the constraint being removed.
	 */

	public void removeSearchConstraint(int storeLevel, Variable var,
			Constraint C) {

		if (stamp < storeLevel) {

			IntervalDomain result = this.cloneLight();

			result.modelConstraints = modelConstraints;
			result.searchConstraints = searchConstraints;
			result.stamp = storeLevel;
			result.previousDomain = this;
			result.modelConstraintsToEvaluate = modelConstraintsToEvaluate;
			result.searchConstraintsToEvaluate = searchConstraintsToEvaluate;
			var.domain = result;

			result.removeSearchConstraint(storeLevel, var, C);
			return;
		}

		assert (stamp == storeLevel);

		int i = 0;

		// TODO , improve by using interval find function.

		while (i < searchConstraintsToEvaluate) {
			if (searchConstraints.get(i) == C) {

				searchConstraints.set(i, searchConstraints
						.get(searchConstraintsToEvaluate - 1));
				searchConstraints.set(searchConstraintsToEvaluate - 1, C);
				searchConstraintsToEvaluate--;

				break;
			}
			i++;
		}
	}

	/**
	 * It removes a constraint from a domain, it should only be called by
	 * removeConstraint function of Variable object.
	 */

	@Override
	public void removeSearchConstraint(int storeLevel, Variable var,
			int position, Constraint C) {

		if (stamp < storeLevel) {

			IntervalDomain result = this.cloneLight();

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

		assert (searchConstraints.get(position) == C) : "Position of the removed constraint not specified properly";
		
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

			IntervalDomain result = this.cloneLight();

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
	public Domain recentDomainPruning(int storeLevel) {

		if (previousDomain == null)
			return emptyDomain;

		if (stamp < storeLevel)
			return emptyDomain;

		return previousDomain.subtract(this);

	}

	/**
	 * It returns all constraints which are associated with variable, even the
	 * ones which are already satisfied.
	 */

	@Override
	public int sizeConstraintsOriginal() {

		Domain domain = this;

		while (domain.domainID() == IntervalDomainID) {

			IntervalDomain dom = (IntervalDomain) domain;

			if (dom.previousDomain != null)
				domain = dom.previousDomain;
			else
				break;
		}
	
		if (domain.domainID() == IntervalDomainID)
			return (domain.modelConstraintsToEvaluate[0]
					+ domain.modelConstraintsToEvaluate[1] + domain.modelConstraintsToEvaluate[2]);
		else
			return domain.sizeConstraintsOriginal();

	}

	@Override
	public int previousValue(int value) {
		
		for (int m = size - 1; m >= 0; m--) {

			Interval i = intervals[m];
			
			// the max of previous interval is the seeked value.
			if (i.min >= value)
				continue;
			
			if (i.max >= value && i.min < value)
				return value - 1;
			
			// the value is equal 
			if (i.max < value)
				return i.max;
				
		}

		return value;		

	}
	
	
	/**
	 * It is a function to check if the object is in consistent state. 
	 * @return String describing the violated invariant, null if no invariant is violated.
	 */
	public String checkInvariants() {
		
		if (size == 0)
			return null;

		for (int i = 0; i < size; i++)
			if (this.intervals[i] == null)
				return "size of the domain is not set up properly";
		
		if (this.intervals[0].min > this.intervals[size-1].max)
			return "Min value is larger than max value " + this;
		
		for (int i = 0; i < size; i++)
			if (this.intervals[i].min > this.intervals[i].max )
				return "One of the intervals not properly build. Min value is larger than max value " 
				+ this;
		
		for (int i = 0; i < size - 1; i++)
			if (this.intervals[i].max + 1 == this.intervals[i+1].min )
				return "Two consequtive intervals should be merged. Improper representation" + 
				this;
		
		//Fine, all invariants hold.
		return null;
		
	}	

}
