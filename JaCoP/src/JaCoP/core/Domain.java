/**
 *  Domain.java 
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
import JaCoP.constraints.Constraint;

/**
 * Defines a Domain and related operations on it.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public abstract class Domain implements Constants {

	/**
	 * It specifies the index of an array of constraints, which are evaluated
	 * upon ANY event.
	 */

	static final int ANY = Constants.ANY;

	/**
	 * It specifies the index of an array of constraints, which are evaluated
	 * upon BOUND event.
	 */

	static final int BOUND = Constants.BOUND;

	/**
	 * It specifies the index of an array of constraints, which are evaluated
	 * upon GROUND event.
	 */

	static final int GROUND = Constants.GROUND;

	/**
	 * Unique identifier for an interval domain type.
	 */

	public static final int IntervalDomainID = 0;
	
	/**
	 * Unique identifier for a bound domain type.
	 */

	public static final int BoundDomainID = 1;
	
	/**
	 * An exception used if failure encountered in functions in();
	 */

	static final FailException failException = new FailException();

	/**
	 * It specifies constraints which are attached to current domain, each array
	 * has different pruning event.
	 */

	public Constraint modelConstraints[][];

	/**
	 * It specifies the first position of a constraint which is satisfied. All
	 * constraints at earlier positions are not satisfied yet.
	 */

	public int[] modelConstraintsToEvaluate;

	/**
	 * It specifies constraints which are attached to current domain.
	 */

	public ArrayList<Constraint> searchConstraints;

	/**
	 * It specifies if the vector of constraints were cloned (if it was not
	 * cloned then the same vector is reused across domains with different
	 * stamps. Only reading actions are allowed on not cloned vector of
	 * constraints.
	 */

	public boolean searchConstraintsCloned;

	/**
	 * It specifies the position of the last constraint which is still not yet
	 * satisfied.
	 */

	public int searchConstraintsToEvaluate;

	/**
	 * It specifies the level of this domain, which specifies at which store
	 * level it was created and used. The domain is only valid (used) at a store
	 * level equal domain stamp.
	 */

	public int stamp;

	/**
	 * It adds interval of values to the domain.
	 * @param i Interval which needs to be added to the domain.
	 */

	public abstract void addDom(Interval i);

	/**
	 * It adds values as specified by the parameter to the domain.
	 * @param domain Domain which needs to be added to the domain.
	 */

	public abstract void addDom(Domain domain);

	/**
	 * It adds all values between min and max to the domain.
	 * @param min the left bound of the interval being added.
	 * @param max the right bound of the interval being added.
	 */

	public abstract void addDom(int min, int max);

	/**
	 * Checks if two domains intersect.
	 * @param domain the domain for which intersection is checked.
	 * @return true if domains are intersecting.
	 */

	public abstract boolean isIntersecting(Domain domain);

	/**
	 * It checks if interval min..max intersects with current domain.
	 * @param min the left bound of the interval.
	 * @param max the right bound of the interval.
	 * @return true if domain intersects with the specified interval.
	 */

	public abstract boolean isIntersecting(int min, int max);

	/**
	 * It removes all elements.
	 */

	public abstract void clear();

	/**
	 * It clones the domain object, only data responsible for encoding domain
	 * values is cloned. All other fields must be set separately.
	 * @return return a clone of the domain. It aims at getting domain of the proper class type. 
	 */

	public abstract Domain cloneLight();

	/**
	 * It clones the domain object.
	 */

	@Override
	public abstract Domain clone();

	/**
	 * It specifies if the current domain contains the domain given as a
	 * parameter.
	 * @param domain for which we check if it is contained in the current domain.
	 * @return true if the supplied domain is cover by this domain.
	 */

	public abstract boolean contains(Domain domain);

	/**
	 * It creates a complement of a domain.
	 * @return it returns the complement of this domain.
	 */

	public abstract Domain complement();

	/**
	 * It returns all the constraints attached currently to the domain.
	 * @return an array of constraints currently attached to the domain.
	 */
	public ArrayList<Constraint> constraints() {

		ArrayList<Constraint> result = new ArrayList<Constraint>();

		result.addAll(searchConstraints);

		if (modelConstraints != null)
			for (int i = GROUND; i <= ANY; i++) {
				for (int j = modelConstraintsToEvaluate[i]; j >= 0; j--)
					if (modelConstraints[i] != null)
						if (j < modelConstraints[i].length)
							result.add(modelConstraints[i][j]);
			}

		return result;
	}

	/**
	 * It checks if value belongs to the domain.
	 * @param value which is checked if it exists in the domain.
	 * @return true if value belongs to the domain.
	 */

	public abstract boolean contains(int value);

	/**
	 * It gives next value in the domain from the given one (lexigraphical
	 * ordering). If no value can be found then returns the same value.
	 * @param value it specifies the value after which a next value has to be found.
	 * @return next value after the specified one which belong to this domain.
	 */

	public abstract int nextValue(int value);

	/**
	 * It gives previous value in the domain from the given one (lexigraphical
	 * ordering). If no value can be found then returns the same value.
	 * @param value before which a value is seeked for.
	 * @return it returns the value before the one specified as a parameter.
	 */
	
	public abstract int previousValue(int value);

	/**
	 * It returns value enumeration of the domain values.
	 * @return valueEnumeration which can be used to enumerate one by one value from this domain.
	 */

	public abstract ValueEnumeration valueEnumeration();

	/**
	 * It returns interval enumeration of the domain values.
	 * @return intervalEnumeration which can be used to enumerate intervals in this domain.
	 */

	public abstract IntervalEnumeration intervalEnumeration();

	/**
	 * It checks if the domain is equal to the supplied domain.
	 * @param domain against which the equivalence test is performed.
	 * @return true if suppled domain has the same elements as this domain. 
	 */

	public abstract boolean eq(Domain domain);

	/**
	 * It returns the size of the domain.
	 * @return number of elements in this domain.
	 */

	public abstract int getSize();

	/**
	 * It intersects current domain with the one given as a parameter.
	 * @param dom domain with which the intersection needs to be computed.
	 * @return the intersection between supplied domain and this domain.
	 */

	public abstract Domain intersect(Domain dom);

	/**
	 * In intersects current domain with the interval min..max.
	 * @param min the left bound of the interval (inclusive)
	 * @param max the right bound of the interval (inclusive)
	 * @return the intersection between the specified interval and this domain.
	 */

	public abstract Domain intersect(int min, int max);

	/**
	 * It intersects with the domain which s a complement of value. 
	 * @param value the value for which the complement is computed
	 * @return the domain which does not contain specified value.
	 */

	public abstract Domain subtract(int value);

	/**
	 * It returns true if given domain is empty.
	 * @return true if the given domain is empty.
	 */

	public abstract boolean isEmpty();

	/**
	 * It returns the maximum value in a domain.
	 * @return the largest value present in the domain.
	 */

	public abstract int max();

	/**
	 * It returns the minimum value in a domain.
	 * @return the smallest value present in the domain.
	 */
	public abstract int min();

	/**
	 * It removes a constraint from a domain, it should only be called by
	 * removeConstraint function of Variable object. It is called for example in a
	 * situation when a constraint is satisfied. 
	 * @param storeLevel specifies the current level of the store, from which it should be removed.
	 * @param var specifies variable for which the constraint is being removed.
	 * @param C the constraint which is being removed.
	 */

	public abstract void removeModelConstraint(int storeLevel, Variable var,
			Constraint C);

	/**
	 * It removes a constraint from a domain, it should only be called by
	 * removeConstraint function of Variable object.
	 * @param storeLevel specifies the current level of the store, from which it should be removed.
	 * @param var specifies variable for which the constraint is being removed.
	 * @param position specifies the position of the removed constraint.
	 * @param C the constraint which is being removed.
	 */
	public abstract void removeSearchConstraint(int storeLevel, Variable var,
												int position, Constraint C);

	/**
	 * @return it returns the array containing search constraints (the ones imposed after setting up the model).
	 */
	public ArrayList<Constraint> searchConstraints() {
		return searchConstraints;
	}

	/**
	 * It sets the domain to the specified domain.
	 * @param domain the domain from which this domain takes all elements.
	 */

	public abstract void setDomain(Domain domain);

	/**
	 * It sets this domain to contain exactly all values between min and max.
	 * @param min the left bound of the interval (inclusive).
	 * @param max the right bound of the interval (inclusive).
	 */

	public abstract void setDomain(int min, int max);

	/**
	 * It sets the stamp of the domain.
	 * 
	 * @param stamp defines the time stamp of the domain.
	 */

	public void setStamp(int stamp) {
		this.stamp = stamp;
	}

	/**
	 * It returns true if given domain has only one element.
	 * @return true if the domain contains only one element.
	 */
	public abstract boolean singleton();

	/**
	 * It returns true if given domain has only one element equal c.
	 * @param c the value to which the only element should be equal to.
	 * @return true if the domain contains only one element c.
	 */

	public abstract boolean singleton(int c);

	/**
	 * It returns the number of constraints
	 * @return the number of constraints attached to this domain.
	 */

	public int noConstraints() {
		return searchConstraintsToEvaluate 
				+ modelConstraintsToEvaluate[GROUND]
				+ modelConstraintsToEvaluate[BOUND]
				+ modelConstraintsToEvaluate[ANY];
	}

	/**
	 * It returns number of search constraints.
	 * @return the number of search constraints.
	 */

	public int noSearchConstraints() {
		return searchConstraintsToEvaluate;
	}

	/**
	 * It returns the stamp of the domain.
	 * @return the level of the domain.
	 */
	public int stamp() {
		return stamp;
	}

	/**
	 * It subtracts domain from current domain and returns the result.
	 * @param domain the domain which is subtracted from this domain.
	 * @return the result of the subtraction.
	 */

	public abstract Domain subtract(Domain domain);

	/**
	 * It subtracts interval min..max.
	 * @param min the left bound of the interval (inclusive).
	 * @param max the right bound of the interval (inclusive).
	 * @return the result of the subtraction.
	 */

	public abstract Domain subtract(int min, int max);

	/**
	 * It computes union of the supplied domain with this domain.
	 * @param domain the domain for which the union is computed.
	 * @return the union of this domain with the supplied one.
	 */

	public abstract Domain union(Domain domain);

	/**
	 * It computes union of this domain and the interval.
	 * @param min the left bound of the interval (inclusive).
	 * @param max the right bound of the interval (inclusive).
	 * @return the union of this domain and the interval.
	 */

	public abstract Domain union(int min, int max);

	/**
	 * It computes union of this domain and value. 
	 * 
	 * @param value it specifies the value which is being added.
	 * @return domain which is a union of this one and the value.
	 */

	public abstract Domain union(int value);

	/**
	 * It returns string description of the domain (only values in the domain).
	 */

	@Override
	public abstract String toString();

	/**
	 * It returns string description of the constraints attached to the domain.
	 * @return the string description.
	 */

	public abstract String toStringConstraints();

	/**
	 * It returns complete string description containing all relevant
	 * information about the domain.
	 * @return complete description of the domain.
	 */

	public abstract String toStringFull();

	/**
	 * It produces xml representation of a domain.
	 * @return an XML element describing the domain, according to XCSP format definition.
	 */

	public abstract org.jdom.Element toXML();
	
	/**
	 * It produces an domain element from XML description.
	 * 
	 * @param domainElement XML element describing the domain.
	 */
	public abstract void fromXML(org.jdom.Element domainElement);

	/**
	 * It updates the domain according to the minimum value and stamp value. It
	 * informs the variable of a change if it occurred.
	 * @param storeLevel level of the store at which the update occurs.
	 * @param var variable for which this domain is used.
	 * @param min the minimum value to which the domain is updated.
	 */

	public abstract void inMin(int storeLevel, Variable var, int min);

	/**
	 * It updates the domain according to the maximum value and stamp value. It
	 * informs the variable of a change if it occurred.
	 * @param storeLevel level of the store at which the update occurs.
	 * @param var variable for which this domain is used.
	 * @param max the maximum value to which the domain is updated.
	 */

	public abstract void inMax(int storeLevel, Variable var, int max);

	/**
	 * It updates the domain to have values only within the interval min..max.
	 * The type of update is decided by the value of stamp. It informs the
	 * variable of a change if it occurred.
	 * @param storeLevel level of the store at which the update occurs.
	 * @param var variable for which this domain is used.
	 * @param min the minimum value to which the domain is updated.
	 * @param max the maximum value to which the domain is updated.
	 */

	public abstract void in(int storeLevel, Variable var, int min, int max);

	/**
	 * It updates the domain to have values only within the domain. The type of
	 * update is decided by the value of stamp. It informs the variable of a
	 * change if it occurred.
	 * @param storeLevel level of the store at which the update occurs.
	 * @param var variable for which this domain is used.
	 * @param domain the domain according to which the domain is updated.
	 */

	public abstract void in(int storeLevel, Variable var, Domain domain);

	/**
	 * It updates the domain to not contain the value complement. It informs the
	 * variable of a change if it occurred.
	 * @param storeLevel level of the store at which the update occurs.
	 * @param var variable for which this domain is used.
	 * @param complement value which is removed from the domain if it belonged to the domain.
	 */

	public abstract void inComplement(int storeLevel, Variable var, int complement);

	/**
	 * It updates the domain so it does not contain the supplied interval. It informs
	 * the variable of a change if it occurred.
	 * @param storeLevel level of the store at which the update occurs.
	 * @param var variable for which this domain is used.
	 * @param min the left bound of the interval (inclusive).
	 * @param max the right bound of the interval (inclusive).
	 */

	public abstract void inComplement(int storeLevel, Variable var, int min,
			int max);

	/**
	 * It returns number of intervals required to represent this domain.
	 * @return the number of intervals in the domain.
	 */
	public abstract int noIntervals();

	/**
	 * It returns required interval.
	 * @param position the position of the interval.
	 * @return the interval, or null if the required interval does not exist.
	 */
	public abstract Interval getInterval(int position);

	/**
	 * It updates the domain to contain the elements as specifed by the domain,
	 * which is shifted. E.g. {1..4} + 3 = 4..7
	 * @param storeLevel level of the store at which the update occurs.
	 * @param var variable for which this domain is used.
	 * @param domain the domain according to which the domain is updated.
	 * @param shift the shift which is used to shift the domain supplied as argument.
	 */

	public abstract void inShift(int storeLevel, Variable var, Domain domain,
			int shift);

	/**
	 * It removes the specified level. This function may re-instantiate
	 * the old copy of the domain (previous value) or recover from changes done at stamp
	 * level to get the previous value at level lower at provided level.
	 * @param level the level which is being removed.
	 * @param var the variable to which this domain belonged to.
	 */

	public abstract void removeLevel(int level, Variable var);

	/**
	 * It returns an unique identifier of the domain.
	 * @return it returns an integer id of the domain.
	 */

	public abstract int domainID();

	/**
	 * It specifies if the domain type is more suited to representing sparse
	 * domain.
	 * @return true if sparse, false otherwise.
	 */

	public abstract boolean isSparseRepresentation();

	/**
	 * It specifies if domain is a finite domain of numeric values (integers).
	 * @return true if domains contains numeric values.
	 */

	public abstract boolean isNumeric();

	/**
	 * It returns the left most element of the given interval.
	 * @param intervalNo the interval number.
	 * @return the left bound of the specified interval.
	 */

	public abstract int leftElement(int intervalNo);

	/**
	 * It returns the right most element of the given interval.
	 * @param intervalNo the interval number.
	 * @return the right bound of the specified interval.
	 */

	public abstract int rightElement(int intervalNo);

	/**
	 * It adds a constraint to a domain, it should only be called by
	 * putConstraint function of Variable object. putConstraint function from
	 * Variable must make a copy of a list of model constraints if vector was not
	 * cloned.
	 * @param storeLevel the level at which the model constraint is to be added.
	 * @param var variable to which the constraint is attached to.
	 * @param C the constraint which is being attached to a variable.
	 * @param pruningEvent the type of the prunning event required to check the consistency of the attached constraint.
	 */

	public abstract void putModelConstraint(int storeLevel, Variable var,
			Constraint C, int pruningEvent);

	/**
	 * It adds a constraint to a domain, it should only be called by
	 * putConstraint function of Variable object. putConstraint function from
	 * Variable must make a copy of a list of search constraints if vector was not
	 * cloned.
	 * @param storeLevel the level at which the search constraint is to be added.
	 * @param var variable to which the constraint is attached to.
	 * @param C the constraint which is being attached to a variable.
		 */

	public abstract void putSearchConstraint(int storeLevel, Variable var,
			Constraint C);

	/**
	 * It returns the values which have been removed at current store level.
	 * @param currentStoreLevel the current store level.
	 * @return emptyDomain if domain did not change at current level, or the set of values which have been removed at current level.
	 */

	public abstract Domain recentDomainPruning(int currentStoreLevel);

	/**
	 * It returns all constraints which are associated with variable, even the
	 * ones which are already satisfied.
	 * @return the number of constraint attached to this domain. 
	 */

	public int sizeConstraints() {
		return (modelConstraintsToEvaluate[0] + 
				modelConstraintsToEvaluate[1] + 
				modelConstraintsToEvaluate[2]);
	}

	/**
	 * It returns all constraints which are associated with variable, even the
	 * ones which are already satisfied.
	 * @return the number of constraints attached to the original domain of the variable associated with this domain.
	 */
	public abstract int sizeConstraintsOriginal();

}
