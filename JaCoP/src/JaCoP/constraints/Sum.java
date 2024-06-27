/**
 *  Sum.java 
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
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Store;
import JaCoP.core.TimeStamp;
import JaCoP.core.Variable;

/**
 * Sum constraint implements the summation over several Variable's . It provides
 * the sum from all Variable's on the list.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class Sum extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = sumConstr;

	Variable list[];

	Variable sum;

	/**
	 * The sum of grounded variables.
	 */
	private TimeStamp<Integer> sumGrounded;
	
	/**
	 * The position for the next grounded variable.
	 */
	private TimeStamp<Integer> nextGroundedPosition;	
	
	/**
	 * It creates a sum constraints which sums all variables and makes it equal to variable sum.
	 * @param variables variables being summed up.
	 * @param sum the sum variable.
	 */

	public Sum(ArrayList<? extends Variable> variables, Variable sum) {

		queueIndex = 1;
		numberId = IdNumber++;
		this.sum = sum;
		list = variables.toArray(new Variable[variables.size()]);
		numberArgs += list.length;
	}

	/**
	 * It constructs sum constraint which sums all variables and makes it equal to variable sum.
	 * @param variables
	 * @param sum
	 */
	public Sum(Variable[] variables, Variable sum) {
		
		queueIndex = 1;
		numberId = IdNumber++;
		this.sum = sum;
		list = new Variable[variables.length];
		System.arraycopy(variables, 0, list, 0, variables.length);
		numberArgs += variables.length;		
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> args = new ArrayList<Variable>(list.length + 1);

		args.add(sum);

		for (Variable v : list)
			args.add(v);

		return args;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		
		while (store.newPropagation) {

			int pointer = nextGroundedPosition.value();

			int lMin = sumGrounded.value();
			int lMax = lMin;
			
			int sumJustGrounded = 0;

			for (int i = pointer; i < list.length; i++) {
				Domain currentDomain = list[i].domain;
				
				if (currentDomain.singleton()) {
					
					if (pointer < i) {
						Variable grounded = list[i];
						list[i] = list[pointer];
						list[pointer] = grounded;
					}
				
					pointer++;
					sumJustGrounded += currentDomain.min();
					continue;
				}
				
				lMin += currentDomain.min();
				lMax += currentDomain.max();
			}

			nextGroundedPosition.update(pointer);
			sumGrounded.update( sumGrounded.value() + sumJustGrounded );
			
			lMin += sumJustGrounded;
			lMax += sumJustGrounded;
			
			boolean needAdaptMin = false;
			boolean needAdaptMax = false;

			if (sum.min() > lMin)
				needAdaptMin = true;

			if (sum.max() < lMax)
				needAdaptMax = true;

			sum.domain.in(store.level, sum, lMin, lMax);

			store.newPropagation = false;
			
			int min = sum.min() - lMax;
			int max = sum.max() - lMin;

			if (needAdaptMin && !needAdaptMax)
				for (int i = pointer; i < list.length; i++) {
					Variable v = list[i];
					v.domain.inMin(store.level, v, min + v.max());
				}

			if (!needAdaptMin && needAdaptMax)
				for (int i = pointer; i < list.length; i++) {
					Variable v = list[i];
					v.domain.inMax(store.level, v, max + v.min());
				}

			if (needAdaptMin && needAdaptMax)
				for (int i = pointer; i < list.length; i++) {
					Variable v = list[i];
					v.domain.in(store.level, v, min + v.max(), max + v.min());
				}

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
			return Constants.BOUND;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_sum + numberId;
	}

	// registers the constraint in the constraint store
	@Override
	public void impose(Store store) {

		sumGrounded = new TimeStamp<Integer>(store, 0);
		nextGroundedPosition = new TimeStamp<Integer>(store, 0);
		
		sum.putModelConstraint(this, getConsistencyPruningEvent(sum));
		
		for (int i = 0; i < list.length; i++) {
			list[i].putModelConstraint(this, getConsistencyPruningEvent(list[i]));
		}
		
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void queueVariable(int level, Variable V) {
		
	}

	@Override
	public void removeConstraint() {
		sum.removeConstraint(this);
		for (Variable v : list)
			v.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		boolean sat = sum.singleton();
		
		int i = list.length - 1, sumAll = 0;
		
		while (sat && i >= 0) {
			sat = list[i].singleton();
			i--;
		}
		if (sat) {
			for (Variable v : list)
				sumAll += v.min();
		}
		return (sat && sumAll == sum.min());
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		result.append(" : sum( [");

		for (int i = 0; i < list.length; i++) {
			result.append(list[i]);
			if (i < list.length - 1)
				result.append(", ");
		}
		result.append("], ").append(sum).append(" )");
		
		return result.toString();
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_sum);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(sum);
		for (int i = 0; i < list.length; i++)
			scopeVars.add(list[i]);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));


		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	
		
		org.jdom.Element term = new org.jdom.Element("sum");
		term.setText(sum.id());
		constraint.addContent(term);

		org.jdom.Element tList = new org.jdom.Element("list");

		StringBuffer tString = new StringBuffer();
		for (int i = 0; i < list.length; i++)
			tString.append( list[i].id() ).append( " " );

		tList.setText(tString.toString().trim());

		constraint.addContent(tList);

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	
	/**
	 * It creates a constraint from XCSP description.
	 * @param constraint an XML description of the constraint.
	 * @param store the constraint store in which context the constraint is being created.
	 * @return created constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		 
		String list = constraint.getChild("list").getText();
		String sum = constraint.getChild("sum").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(list);

		ArrayList<Variable> x = new ArrayList<Variable>();

		for (String n : varsNames)
			x.add(store.findVariable(n));

		return new Sum(x, store.findVariable(sum));
		
	}

	@Override
	public Constraint getGuideConstraint() {
	
		Variable proposedVariable = getGuideVariable();
		if (proposedVariable != null)
			return new XeqC(proposedVariable, guideValue);
		else
			return null;
	}

	@Override
	public int getGuideValue() {
		return guideValue; 
	}

	int guideValue = 0;
	
	
	@Override
	public Variable getGuideVariable() {
		
		int regret = 1;
		Variable proposedVariable = null;

		for (Variable v : list) {

			Domain listDom = v.dom();

			if (v.singleton())
				continue;

			int currentRegret = listDom.nextValue(listDom.min()) - listDom.min();
			
			if (currentRegret > regret) {
				regret = currentRegret;
				proposedVariable = v;
				guideValue = listDom.min();
			}

			currentRegret = listDom.max() - listDom.previousValue(listDom.max());
			
			if (currentRegret > regret) {
				regret = currentRegret;
				proposedVariable = v;
				guideValue = listDom.max();
			}
			
		}

		return proposedVariable;
		
	}


	@Override
	public void supplyGuideFeedback(boolean feedback) {
	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			sum.weight++;
			for (Variable v : list) v.weight++;
		}
	}
	
}
