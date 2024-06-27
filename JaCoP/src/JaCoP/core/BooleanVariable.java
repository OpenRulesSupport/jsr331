/**
 *  BooleanVariable.java 
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

public class BooleanVariable extends Variable {

	/**
	 * No parameter, explicit, empty constructor for subclasses.
	 */
	public BooleanVariable() {
	}

	/**
	 * This constructor creates a variable with empty domain (standard FD
	 * domain), automatically generated name, and empty attached constraint
	 * list.
	 * @param store It specifies the store in which boolean variable should be created.
	 */
	public BooleanVariable(Store store) {
		this(store, store.getVariableIdPrefix() + idNumber,
				new BoundDomain(0, 1));
	}

	/**
	 * Boolean variable constructor. 
	 * @param store It specifies the store in which boolean variable should be created.
	 * @param name It specifies the id of the variable.
	 */
	public BooleanVariable(Store store, String name) {
		this(store, name, new BoundDomain(0, 1));

	}

// 	protected BooleanVariable(Store store, String name, BoundDomain dom) {
 	public BooleanVariable(Store store, String name, BoundDomain dom) {

		dom.searchConstraints = new ArrayList<Constraint>();
		dom.modelConstraints = new Constraint[3][];
		dom.modelConstraintsToEvaluate = new int[3];
		dom.modelConstraintsToEvaluate[0] = 0;
		dom.modelConstraintsToEvaluate[1] = 0;
		dom.modelConstraintsToEvaluate[2] = 0;
		id = name;
		domain = dom;
		domain.stamp = 0;
		this.store = store;

		if (store.pointer4GroundedBooleanVariables == null) {
			store.pointer4GroundedBooleanVariables = new TimeStamp<Integer>(
					store, 0);
			// Boolean Time stamp will be updated manually by store.
			store.timeStamps.remove(store.pointer4GroundedBooleanVariables);
			store.changeHistory4BooleanVariables = new BooleanVariable[100];
		}

	}

    /**
	 * It is turned-off to avoid any mis-use. It does not perform any function.
	 */
	@Override
	public void addDom(Domain dom) {
		assert(false) : "Do not attempt to add domains to boolean variables. Use constructors only.";
	}

	/**
	 * It is turned-off to avoid any mis-use. It does not perform any function.
	 */
	@Override
	public void addDom(int min, int max) {
		assert(false) : "Do not attempt to add domains to boolean variables. Use constructors only.";
	}


	/**
	 * It registers constraint with current variable, so anytime this variable
	 * is changed the constraint is reevaluated.
	 * @param C Constraint which is being attached to a boolean variable.
	 */
	@Override
	public void putConstraint(Constraint C) {

		putModelConstraint(C, GROUND);

	}

	/**
	 * It registers constraint with current variable, so anytime this variable
	 * is changed the constraint is reevaluated. Pruning event is ignored as all
	 * are evaluated with GROUND event, since any change to Boolean Variable
	 * makes it ground.
	 * @param C - constraint being attached to a variable.
	 * @param pruningEvent - Only NONE and GROUND events are considered. By default GROUND event is used.
	 * 
	 */
	@Override
	public void putModelConstraint(Constraint C, int pruningEvent) {

		if (singleton())
			return;

		if (pruningEvent == Constants.NONE) {
			return;
		}

		domain.putModelConstraint(store.level, this, C, GROUND);

		store.recordBooleanChange(this);

	}

	/**
	 * It registers constraint with current variable, so anytime this variable
	 * is changed the constraint is reevaluated.
	 * @param C It specifies the constraint which is being added. 
	 */
	@Override
	public void putSearchConstraint(Constraint C) {

		if (singleton())
			return;

		domain.putSearchConstraint(store.level, this, C);
		store.recordBooleanChange(this);

	}

	/**
	 * It unregisters constraint with current variable, so change in variable
	 * will not cause constraint reevaluation.
	 * @param constraint it specifies the constraint which is no longer attached to a variable.
	 */
	@Override
	public void removeConstraint(Constraint constraint) {

		if (singleton())
			return;

		int i = domain.searchConstraintsToEvaluate - 1;
		for (; i >= 0; i--)
			if (domain.searchConstraints.get(i) == constraint)
				domain.removeSearchConstraint(store.level, this, i, constraint);

		if (i == -1)
			domain.removeModelConstraint(store.level, this, constraint);

		store.recordBooleanChange(this);

	}

	/**
	 * It is turned-off to avoid any mis-use. It does not perform any function.
	 * @param d - the domain which is added.
	 */	
	@Override
	public void setDomain(Domain d) {
		assert (false) : "Do not attempt to add domains to boolean variables. Use constructors only.";
	}

	/**
	 * It returns current number of constraints which are associated with
	 * variable and are not yet satisfied.
	 * @return the number of constraints currently attached to this variable.
	 */
	@Override
	public int sizeConstraints() {
		return domain.sizeConstraints();
	}

	/**
	 * It returns all constraints which are associated with variable, even the
	 * ones which are already satisfied.
	 * @return the number of constraints originally attached to this variable.
	 */
	@Override
	public int sizeConstraintsOriginal() {
		return domain.sizeConstraintsOriginal();
	}

	/**
	 * It returns current number of constraints which are associated with
	 * a boolean variable and are not yet satisfied.
	 * @return the number of constraints.
	 */
	@Override
	public int sizeSearchConstraints() {
		return domain.searchConstraintsToEvaluate;
	}

	/**
	 * @return it returns the string description of the boolean variable.
	 * 
	 */
	@Override
	public String toString() {
		if (domain.singleton())
			return id + "=" + domain;
		else
			return id + "::" + domain;		
	}

	/**
	 * @return It returns elaborate string description of the boolean variable and all the components of its domain. 
	 * 
	 */
	@Override
	public String toStringFull() {
		return id + domain.toStringFull();
	}

	/**
	 * It produces xml representation of a variable.
	 * @return It returns XML element describing the variable.
	 */
	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element variable = new org.jdom.Element("variable");

		variable.setAttribute("id", id);

		variable.addContent(domain.toXML());

		return variable;
	}

}
