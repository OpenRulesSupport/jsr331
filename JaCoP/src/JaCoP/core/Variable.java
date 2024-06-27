/**
 *  Variable.java 
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
 * Defines a variable and related operations on it.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class Variable implements Backtrackable {

	static final int ANY = Constants.ANY;

	static final int BOUND = Constants.BOUND;

	static final boolean debug = false;

	static final int GROUND = Constants.GROUND;

	/**
	 * It is a counter to indicate number of created variables.
	 */
	static int idNumber = 0;

	/**
	 * It stores pointer to a current domain, which has stamp equal to store
	 * stamp.
	 */
	public Domain domain;

	/**
	 * Id string of the variable.
	 */
	public String id;

	/**
	 * It specifies the index at which it is stored in Store. 
	 */
	
	public int index = -1;

	/**
	 * It specifies the current weight of the variable.
	 */
	
	public int weight = 1;
	
	/**
	 * Each variable is created in a store. This attribute represents the store
	 * in which this variable was created.
	 */
	public Store store;

	/**
	 * No parameter, explicit, empty constructor for subclasses.
	 */
	public Variable() {
	}

	/**
	 * This constructor creates a variable with empty domain (standard
	 * IntervalDomain domain), automatically generated name, and empty attached
	 * constraint list.
	 * @param store store in which the variable is created.
	 */
	public Variable(Store store) {
		this(store, store.getVariableIdPrefix() + idNumber++,
				new IntervalDomain(5));
	}

	/**
	 * This constructor creates a variable with a domain between min..max, 
	 * automatically generated name, and empty attached constraint list.
	 * @param store store in which the variable is created.
	 * @param min the minimum value of the domain.
	 * @param max the maximum value of the domain.
	 */
	public Variable(Store store, int min, int max) {
		this(store, store.getVariableIdPrefix() + idNumber++,
				new IntervalDomain(min, max));
	}

	/**
	 * This constructor creates a variable with an empty domain (standard
	 * IntervalDomain domain), the specified name, and an empty attached
	 * constraint list. 
	 * 
	 * @param store store in which the variable is created.
	 * @param name the name for the variable being created.
	 */
	public Variable(Store store, String name) {
		this(store, name, new IntervalDomain(5));
	}

	/**
	 * It creates a variable in a given store, with a given name and 
	 * a given domain.
	 * @param store store in which the variable is created.
	 * @param name the name for the variable being created.
	 * @param dom the domain of the variable being created.
	 */
	public Variable(Store store, String name, Domain dom) {
		dom.searchConstraints = new ArrayList<Constraint>();
		dom.modelConstraints = new Constraint[3][];
		dom.modelConstraintsToEvaluate = new int[3];
		dom.modelConstraintsToEvaluate[0] = 0;
		dom.modelConstraintsToEvaluate[1] = 0;
		dom.modelConstraintsToEvaluate[2] = 0;
		id = name;
		domain = dom;
		domain.stamp = 0;
		index = store.putVariable(this);
		this.store = store;
	}

	/*
	protected Variable(Store store, String name, IntervalDomain dom) {
		dom.searchConstraints = new ArrayList<Constraint>();
		dom.modelConstraints = new Constraint[3][];
		dom.modelConstraintsToEvaluate = new int[3];
		dom.modelConstraintsToEvaluate[0] = 0;
		dom.modelConstraintsToEvaluate[1] = 0;
		dom.modelConstraintsToEvaluate[2] = 0;
		id = name;
		domain = dom;
		domain.stamp = 0;
		index = store.putVariable(this);
		this.store = store;
	}
   */
	
	/**
	 * This constructor creates a variable in a given store, with 
	 * the domain specified by min..max and with the given name.
	 * @param store the store in which the variable is created.
	 * @param name the name of the variable being created.
	 * @param min the minimum value of the variables domain.
	 * @param max the maximum value of the variables domain.
	 */
	public Variable(Store store, String name, int min, int max) {
		this(store, name, new IntervalDomain(min, max));

	}

    /**
	 * It is possible to add the domain of variable. It should be used with
	 * care, only right after variable was created and before it is used in
	 * constraints or search.
	 * @param dom the added domain. 
	 */

	public void addDom(Domain dom) {
		domain.addDom(dom);
	}

	/**
	 * It is possible to add the domain of variable. It should be used with
	 * care, only right after variable was created and before it is used in
	 * constraints or search. Current implementation requires domains being
	 * added in the increasing order (e.g. 1..5 before 9..10).
	 * @param min the left bound of the interval being added.
	 * @param max the right bound of the interval being added.
	 */

	public void addDom(int min, int max) {
		domain.addDom(min, max);
	}

	/**
	 * This function returns current domain of the variable.
	 * @return the domain of the variable.
	 */

	public Domain dom() {
		return domain;
	}

	/**
	 * It checks if the domains of variables are equal.
	 * @param var the variable to which current variable is compared to.
	 * @return true if both variables have the same domain.
	 */
	public boolean eq(Variable var) {
		return domain.eq(var.dom());
	}

	/**
	 * It returns the size of the current domain.
	 * @return the size of the variables domain.
	 */

	public int getSize() {
		return domain.getSize();
	}

	/**
	 * This function returns variable id.
	 * @return the id of the variable.
	 */
	public String id() {
		return id;
	}

	/**
	 * This function returns the index of variable in store array.
	 * @return the index of the variable. 
	 */
	public int index() {
		return index;
	}

	/**
	 * It checks if the domain is empty.
	 * @return true if variable domain is empty.
	 */

	public boolean isEmpty() {
		return domain.isEmpty();
	}

	/**
	 * This function returns current maximal value in the domain of the
	 * variable.
	 * @return the maximum value belonging to the domain.
	 */

	public int max() {
		return domain.max();
	}

	/**
	 * This function returns current minimal value in the domain of the
	 * variable.
	 * @return the minimum value beloning to the domain. 
	 */
	public int min() {
		return domain.min();
	}

	/**
	 * It registers constraint with current variable, so anytime this variable
	 * is changed the constraint is reevaluated.
	 * @param c the constraint being attached to this variable.
	 */

	public void putConstraint(Constraint c) {
		putModelConstraint(c, ANY);
	}

	/**
	 * It registers constraint with current variable, so anytime this variable
	 * is changed the constraint is reevaluated. Pruning events constants from 0
	 * to n, where n is the strongest pruning event.
	 * @param c the constraint which is being attached to the variable.
	 * @param pruningEvent type of the event which must occur to trigger the execution of the consistency function.
	 */

	public void putModelConstraint(Constraint c, int pruningEvent) {

		// If variable is a singleton then it will not be put in the model.
		// It will be put in the queue and evaluated only once in the queue. 
		// If constraint is consistent for a singleton then it will remain 
		// consistent from the point of view of this variable.
		if (singleton())
			return;

		// if Event is NONE then constraint is not being attached, it will 
		// be only evaluated once, as after imposition it is being put in the constraint 
		// queue.
		
		if (pruningEvent == Constants.NONE) {
			return;
		}

		domain.putModelConstraint(store.level, this, c, pruningEvent);

		store.recordChange(this);

	}

	/**
	 * It registers constraint with current variable, so always when this variable
	 * is changed the constraint is reevaluated.
	 * @param c the constraint which is added as a search constraint. 
	 */

	public void putSearchConstraint(Constraint c) {

		if (singleton())
			return;

		domain.putSearchConstraint(store.level, this, c);

		store.recordChange(this);

	}

	/**
	 * It returns the values which have been removed at current store level. It does
	 * _not_ return the recent pruning in between the calls to that function.
	 * @return difference between the current level and the one before it.
	 */
	public Domain recentDomainPruning() {

		return domain.recentDomainPruning(store.level);

	}

	/**
	 * It detaches constraint from the current variable, so change in variable
	 * will not cause constraint reevaluation. It is only removed from the 
	 * current level onwards. Removing current level at later stage will 
	 * automatically re-attached the constraint to the variable. 
	 * 
	 * @param c the constraint being detached from the variable.
	 */

	public void removeConstraint(Constraint c) {

		if (singleton())
			return;

		int i = domain.searchConstraintsToEvaluate - 1;
		for (; i >= 0; i--)
			if (domain.searchConstraints.get(i) == c)
				domain.removeSearchConstraint(store.level, this, i, c);

		if (i == -1)
			domain.removeModelConstraint(store.level, this, c);

		store.recordChange(this);

	}

	/**
	 * It is possible to set the domain of variable. It should be used with
	 * care, only right after variable was created and before it is used in
	 * constraints or search.
	 * @param dom domain to which the current variable domain is set to. 
	 */

	public void setDomain(Domain dom) {
		domain.setDomain(dom);
	}

	/**
	 * It is possible to set the domain of variable. It should be used with
	 * care, only right after variable was created and before it is used in
	 * constraints or search.
	 * @param min the left bound of the interval used to set this variable domain to.
	 * @param max the right bound of the interval used to set this variable domain to.
	 */

	public void setDomain(int min, int max) {
		domain.setDomain(min, max);
	}

	/**
	 * It checks if the domain contains only one value.
	 * @return true if the variable domain is a singleton, false otherwise.
	 */

	public boolean singleton() {
		return domain.singleton();
	}

	/**
	 * It checks if the domain contains only one value equal to c.
	 * @param val value to which we compare the singleton of the variable.
	 * @return true if a variable domain is singleton and it is equal to the specified value.
	 */

	public boolean singleton(int val) {
		return domain.singleton(val);
	}

	/**
	 * It returns current number of constraints which are associated with
	 * variable and are not yet satisfied.
	 * @return number of constraints attached to the variable.
	 */
	public int sizeConstraints() {
		return domain.sizeConstraints();
	}

	/**
	 * It returns all constraints which are associated with variable, even the
	 * ones which are already satisfied.
	 * @return number of constraints attached at the earliest level of the variable.
	 */
	public int sizeConstraintsOriginal() {
		return domain.sizeConstraintsOriginal();
	}

	/**
	 * It returns current number of constraints which are associated with
	 * variable and are not yet satisfied.
	 * @return number of attached search constraints.
	 */
	public int sizeSearchConstraints() {
		return domain.searchConstraintsToEvaluate;
	}

	/**
	 * This function returns stamp of the current domain of variable. It is
	 * equal or smaller to the stamp of store. Larger difference indicates that
	 * variable has been changed for a longer time.
	 * @return level for which the most recent changes have been applied to.
	 */

	public int level() {
		return domain.stamp;
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer(id);
		
		if (domain.singleton())
			result.append("=");
		else
			result.append("::");
			
		result.append(domain);
		return result.toString();
		
	}

	/**
	 * It returns the string representation of the variable using the full representation
	 * of the domain. 
	 * @return string representation.
	 */
	public String toStringFull() {
		
		StringBuffer result = new StringBuffer(id);
		result.append(domain.toStringFull());
		return result.toString();
		
	}

	/**
	 * It produces xml representation of a variable.
	 * @return XML element describing the variable.
	 */

	public org.jdom.Element toXML() {

		org.jdom.Element variable = new org.jdom.Element("variable");

		variable.setAttribute("id", id);

		variable.addContent(domain.toXML());

		return variable;
	}

	/**
	 * This function returns current value in the domain of the variable. If
	 * current domain of variable is not singleton then warning is printed and
	 * minimal value is returned.
	 * @return the value to which the variable has been grounded to.
	 */

	public int value() {
				
		assert singleton() : "Request for a value of not grounded variable " + this;

		// if (!singleton())
		//	Thread.dumpStack();
				
		return domain.min();
	}

	/**
	 * It informs the variable that its variable has changed according to the specified event.
	 * @param event the type of the change (GROUND, BOUND, ANY).
	 */
	public void domainHasChanged(int event) {
				
		assert ((event == ANY && !singleton()) || 
				(event == BOUND && !singleton()) ||
				(event == GROUND && singleton())) : "Wrong event generated";

		store.addChanged(this, event);

	}

	@Override
	public void remove(int removedLevel) {
		domain.removeLevel(removedLevel, this);
	}

}
