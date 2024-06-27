/**
 *  Element.java 
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

package JaCoP.constraints;

import java.util.ArrayList;

import JaCoP.core.Constants;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Element constraint implements the element/4 constraint (both with integer
 * list and variables list). It defines a following relation 
 * variables[index  - shift] = value. The default shift value is equal to zero.
 * The first index in the variables list is equal to 1.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Element extends Constraint implements Constants {

	Constraint c = null;

	/**
	 * It constructs element constraint based on variables. The default shift value is equal 0.
	 * @param index index variable.
	 * @param variables list of variables.
	 * @param value variable to which index variable is equal to.
	 */
	public Element(Variable index, ArrayList<? extends Variable> variables, Variable value) {
		queueIndex = 1;
		c = new ElementVariable(index, variables, value);
	}

	/**
 	 * It constructs element constraint based on variables.
	 * @param index index variable.
	 * @param variables variables list.
	 * @param value value variable.
	 * @param shift shift by which the index value is moved to the left.
	 */
	public Element(Variable index, ArrayList<? extends Variable> variables,
			Variable value, int shift) {
		queueIndex = 1;
		c = new ElementVariable(index, variables, value, shift);
	}

	/**
	 * It constructs element constraint based on variables. The default shift value is equal 0.
	 * @param index index variable.
	 * @param values list of integers.
	 * @param value variable to which index variable is equal to.
	 */
	public Element(Variable index, int[] values, Variable value) {
		queueIndex = 0;
		c = new ElementInteger(index, values, value);
	}

	/**
 	 * It constructs element constraint based on variables.
	 * @param index index variable.
	 * @param values integer list.
	 * @param value value variable.
	 * @param shift shift by which the index value is moved to the left.
	 */
	public Element(Variable index, int[] values, Variable value, int shift) {
		queueIndex = 0;
		c = new ElementInteger(index, values, value, shift);
	}

	/**
	 * @param index
	 * @param variables
	 * @param value
	 */
	public Element(Variable index, Variable[] variables, Variable value) {
		queueIndex = 1;
		c = new ElementVariable(index, variables, value);
	}

	/**
 	 * It constructs element constraint based on variables.
	 * @param index index variable.
	 * @param variables variables list.
	 * @param value value variable.
	 * @param shift shift by which the index value is moved to the left.
	 */
	public Element(Variable index, 
				   Variable[] variables, 
				   Variable value,
				   int shift) {
		queueIndex = 1;
		c = new ElementVariable(index, variables, value, shift);
	}

	@Override
	public ArrayList<Variable> arguments() {

		return c.arguments();
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		c.consistency(store);
	}

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {
		return c.getPredicateDescriptionXML();
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {
		return c.getConsistencyPruningEvent(var);

	}

	@Override
	public String id() {
		return c.id();
	}

	@Override
	public void impose(Store store) {
		c.impose(store);
	}

	@Override
	public void queueVariable(int level, Variable V) {
		c.queueVariable(level, V);
	}

	@Override
	public void removeConstraint() {
		c.removeConstraint();
	}

	@Override
	public boolean satisfied() {
		return c.satisfied();
	}

	@Override
	public String toString() {
		return c.toString();
	}

	@Override
	public org.jdom.Element toXML() {
		return c.toXML();
	}

	@Override
	public short type() {
		return c.type();
	}

	@Override
	public Constraint getGuideConstraint() {
		return c.getGuideConstraint();
	}

	@Override
	public int getGuideValue() {
		return c.getGuideValue();
	}

	@Override
	public Variable getGuideVariable() {
		return c.getGuideVariable();
	}

	@Override
	public void supplyGuideFeedback(boolean feedback) {
		c.supplyGuideFeedback(feedback);
	}

	@Override
	public void increaseWeight() {
		c.increaseWeight();
	}

}
