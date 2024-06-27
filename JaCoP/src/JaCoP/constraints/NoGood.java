/**
 *  NoGood.java 
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * NoGood constraints implements a constraint which disallows given combination
 * of values for given variables. NoGoods are special constraints as they can be
 * only triggered only when all variables except one are grounded and equal to
 * disallow values. This allows efficient implementation based on watched
 * literals idea from SAT community.
 * 
 * Do not be fooled by watched literals, if you add thousands of no-goods then
 * traversing even 1/10 of them if they are watched by variable which has been 
 * grounded can slow down search considerably. 
 * 
 * NoGoods constraints are imposed at all levels once added. Do not use in 
 * subsearches, as it will not take into account the assignments performed in
 * master search.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class NoGood extends PrimitiveConstraint implements Constants {

	static int IdNumber = 1;

	final static short type = noGoodConstr;

	Variable list[];

	int values[];

	Variable firstWatch;

	int firstValue;

	Variable secondWatch;

	int secondValue;

	final static boolean debug = false;

	/**
	 * It creates a no-good constraint.
	 * @param variables the scope of the constraint.
	 * @param values no-good values which all-together assignment to variables within constraint scope is a no-good.
	 */
	public NoGood(ArrayList<? extends Variable> variables, ArrayList<Integer> values) {

		queueIndex = 0;
		
		assert (variables.size() == values.size()) : "\nLength of two vectors different in NoGood";
		
		numberId = IdNumber++;

		list = new Variable[variables.size()];
		this.values = new int[values.size()];

		variables.toArray(list);
				
		numberArgs += variables.size();
				
		int i = 0;
		for (int val : values) {
			this.values[i++] = val;
		}
		
	}

	/**
	 * It creates a no-good constraint.
	 * @param variables the scope of the constraint.
	 * @param values no-good values which all-together assignment to variables within constraint scope is a no-good.
	 */
	public NoGood(Variable[] variables, int[] values) {

		queueIndex = 0;
		
		assert (variables.length == values.length) : "\nLength of two vectors different in NoGood";
		
		numberId = IdNumber++;
		list = new Variable[variables.length];
		this.values = new int[values.length];

		System.arraycopy(variables, 0, list, 0, variables.length);
		
		numberArgs += variables.length;
				
		System.arraycopy(values, 0, this.values, 0, values.length);
		
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(list.length);

		for (Variable v : list)
			Variables.add(v);

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {

		store.newPropagation = false;

		if (debug)
			System.out.println("Start " + this);

		if (firstWatch == secondWatch) {
			// Special case, when NoGood was one variable no-good
			// or there was no two not singleton variables to be
			// watched.

			if (debug)
				System.out
						.println("Special cases of noGood constraints have occured");

			if (list.length == 1) {

				firstWatch.dom().inComplement(store.level, firstWatch,
						firstValue);

				// store.in(firstWatch, Domain.domain.complement(firstValue));
				return;
			} else {
				// check if it still active no-good
				for (int i = 0; i < list.length; i++)
					if (list[i].getSize() == 1 && list[i].value() != values[i])
						return;

				// if variable is not singleton (even if it was at imposition
				// time)
				// sanity check, just in case, but if this code is executed than
				// mostly improper use of no-goods has been performed.
				for (int i = 0; i < list.length; i++)
					if (list[i].getSize() != 1 && list[i] != firstWatch) {
						throw new JaCoPException(
								"The NoGood learnt for one model is used in different model (model created across many store levels)");
					}

				firstWatch.dom().inComplement(store.level, firstWatch,
						firstValue);
				// store.in(firstWatch, Domain.domain.complement(firstValue));
				return;
			}

		}

		// no good satisfied
		if (firstWatch.getSize() == 1 && firstWatch.value() != firstValue)
			return;

		// no good satisfied
		if (secondWatch.getSize() == 1 && secondWatch.value() != secondValue)
			return;

		if (firstWatch.getSize() == 1 || secondWatch.getSize() == 1)
			for (int i = 0; i < list.length; i++)
				if (list[i].singleton() && !list[i].singleton(values[i]))
					return;

		if (firstWatch.getSize() == 1) {

			boolean found = false;
			// new watched variable needs to be found
			for (int i = 0; i < list.length; i++)
				if (list[i] != secondWatch && list[i].getSize() != 1) {

					store.deregisterWatchedLiteralConstraint(firstWatch,
							this);

					firstWatch = list[i];
					firstValue = values[i];

					store.registerWatchedLiteralConstraint(firstWatch,
							this);

					found = true;
				}

			if (!found) {
				// no new watch found, can propagate.

				secondWatch.dom().inComplement(store.level, secondWatch,
						secondValue);

				// store.in(secondWatch, Domain.domain.complement(secondValue));
				if (debug)
					System.out.println(secondWatch);
				
				return;
				
			}
		}

		if (secondWatch.getSize() == 1) {
			// new watched variable needs to be found

			boolean found = false;

			for (int i = 0; i < list.length; i++)
				if (list[i] != firstWatch && list[i].getSize() != 1) {

					store.deregisterWatchedLiteralConstraint(secondWatch, this);

					secondWatch = list[i];
					secondValue = values[i];

					store.registerWatchedLiteralConstraint(secondWatch, this);

					found = true;
				}

			if (!found) {
				// no new watch found, can propagate.

				firstWatch.dom().inComplement(store.level, firstWatch,
						firstValue);

				// store.in(firstWatch, Domain.domain.complement(firstValue));
				if (debug)
					System.out.println(firstWatch);
			}

		}

		if (debug)
			System.out.println("End" + this);
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
			return Constants.GROUND;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_noGood + numberId;
	}

	// registers the constraint in the constraint store
	// using watched literals functionality.
	@Override
	public void impose(Store store) {

		if (store.watchedConstraints == null)
			store.watchedConstraints = new HashMap<Variable, HashSet<Constraint>>();

		if (list.length == 1) {

			// No good is of form XneqC.
			firstWatch = secondWatch = list[0];
			firstValue = values[0];
			store.registerWatchedLiteralConstraint(firstWatch, this);

			// To obtain immediate pruning when consistency is called
			store.addChanged(this);
		} else {

			int i = 0;
			// Find any two variables and attach a no-good to it.
			for (int j = 0; j < list.length; j++) {
				Variable v = list[j];
				if (v.getSize() != 1 && i < 2) {
					if (i == 0) {
						firstWatch = v;
						firstValue = values[j];
					} else {
						secondWatch = v;
						secondValue = values[j];
					}
					i++;
				}
			}

			if (i < 2) {
				// No good is of form XneqC, as there are no two variables which
				// are not singletons.

				// No good is already satisfied and it is ignored.
				for (int j = 0; j < list.length; j++)
					if (list[i].getSize() == 1 && list[i].value() != values[i])
						return;

				// All values match, so no good is at the moment equivalent to
				// one-variable no-good.

				secondWatch = firstWatch;
				secondValue = firstValue;
				store.registerWatchedLiteralConstraint(firstWatch, this);

				// To obtain immediate pruning when consistency is called
				store.addChanged(this);
			} else {
				store.registerWatchedLiteralConstraint(firstWatch, this);
				store.registerWatchedLiteralConstraint(secondWatch, this);
			}
		}

	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	/**
	 * This function does nothing as constraints can not be removed for a given
	 * level. In addition, watched literals mechanism makes sure that constraint
	 * is not put in the queue when it can not propagate.
	 */
	@Override
	public void removeConstraint() {

		// This function does not do anything on purpose.
		// if constraint is removed from variable then it is removed for all
		// levels.
		// This is not how this function is being used, as constraint is removed
		// only on level on which it is satisfied.

	}

	/**
	 * First time consistency function does pruning is the last time it will be
	 * called. In addition, nogood constraints are not attached in a normal way
	 * to variable. Therefore this function will not be called. It returns false
	 * by default.
	 */

	@Override
	public boolean satisfied() {
		// Again this is watched constraint and constraint takes care in other
		// way if it is satisfied.
		return false;
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : noGood([");

		for (int i = 0; i < list.length; i++) {
			if (list[i] == firstWatch || list[i] == secondWatch)
				result.append("@");
			result.append(list[i]);
			if (i < list.length - 1)
				result.append(", ");
		}
		result.append("], [");
		
		for (int i = 0; i < values.length; i++) {
			result.append(values[i]);
			if (i < values.length - 1)
				result.append(", ");
		}
		result.append("] )");
		return result.toString();
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_noGood);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		for (int i = 0; i < list.length; i++)
			scopeVars.add(list[i]);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));


		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	
				
		org.jdom.Element term = new org.jdom.Element("noGood");
		constraint.addContent(term);

		org.jdom.Element tList = new org.jdom.Element("list");

		StringBuffer tString = new StringBuffer();
		for (int i = 0; i < list.length; i++)
			tString.append( list[i].id() ).append( " " );

		tList.setText(tString.toString().trim());

		constraint.addContent(tList);

		org.jdom.Element wList = new org.jdom.Element("values");

		StringBuffer wString = new StringBuffer();
		for (int i = 0; i < values.length; i++)
			wString.append( String.valueOf(values[i]) ).append( " " );

		wList.setText(wString.toString().trim());

		constraint.addContent(wList);

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	/**
	 * It constructs a constraint from XCSP description.
	 * @param constraint an XML element describing the constraint.
	 * @param store the constraint store in which context the constraint is created.
	 * @return created constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		

		String list = constraint.getChild("list").getText();
		String values = constraint.getChild("values").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(list);
		String[] valuesNames = pattern.split(values);

		ArrayList<Variable> x = new ArrayList<Variable>();

		for (String n : varsNames)
			x.add(store.findVariable(n));

		ArrayList<Integer> w = new ArrayList<Integer>();

		for (String n : valuesNames)
			w.add(Integer.valueOf(n));

		return new NoGood(x, w);
	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			for (Variable v : list) v.weight++;
		}
	}	
	
    @Override
	public boolean notSatisfied() {
		
		boolean result = true;
		for (int i = list.length - 1; i >= 0 && result; i--)
				result &= list[i].singleton(values[i]);
			
		return result;
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
			return Constants.GROUND;
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
	public int getNotConsistencyPruningEvent(Variable var) {
		// If notConsistency function mode
			if (notConsistencyPruningEvents != null) {
				Integer possibleEvent = notConsistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.GROUND;
	}

	@Override
	public void notConsistency(Store store) {
		
		for (int j = 0; j < list.length; j++)
			list[j].domain.in(store.level, list[j], values[j], values[j]);
				
	}	
	
}
