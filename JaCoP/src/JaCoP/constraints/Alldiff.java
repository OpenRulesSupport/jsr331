/**
 *  Alldiff.java 
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;
import org.jdom.Element;
import JaCoP.core.Constants;
import JaCoP.core.Store;
import JaCoP.core.TimeStamp;
import JaCoP.core.Variable;


/**
 * Alldiff constraint assures that all FDVs has different values. It uses bounds
 * consistency technique as described in the paper by J.-F. Puget, "A fast
 * algorithm for the bound consistency of alldiff constraints", in Proceedings
 * of the Fifteenth National Conference on Artificial Intelligence (AAAI '98),
 * 1998. It implements the method with time complexity O(n^2). Before using
 * bounds consistency it calls consistency method from Alldifferent constraint.
 * 
 * It extends basic functionality of Alldifferent constraint.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Alldiff extends Alldifferent implements Constants {

	// it stores the store locally so all the private functions which 
	// are part of the consistency function can throw failure exception
	// without passing store argument every time their function is called.
	Store store;

	int[] min, max, u;

	Comparator<Variable> maxVariable = new VariablemaxComparator<Variable>();

	Comparator<Variable> minVariable = new VariableminComparator<Variable>();

	protected Variable[] listAlldiff;	
	/**
	 * It constructs the alldiff constraint for the supplied variable.
	 * @param variables variables which are constrained to take different values.
	 */
	public Alldiff(ArrayList<? extends Variable> variables) {
		queueIndex = 1;
		numberId = IdNumber++;
		listAlldiff = new Variable[variables.size()];
		list = new Variable[listAlldiff.length];
		listAlldiff = variables.toArray(listAlldiff);
		list = variables.toArray(list);
		numberArgs = (short) variables.size();
		min = new int[variables.size()];
		max = new int[variables.size()];
		u = new int[variables.size()];
	}

	/**
	 * It constructs the alldiff constraint for the supplied variable.
	 * @param variables variables which are constrained to take different values.
	 */
	public Alldiff(Variable[] variables) {
		queueIndex = 1;
		numberId = IdNumber++;
		listAlldiff = new Variable[variables.length];
		list = new Variable[variables.length];
		
		for (int i = 0; i < variables.length; i++) {
			listAlldiff[i] = variables[i];
			list[i] = variables[i];
			numberArgs++;
		}

		min = new int[variables.length];
		max = new int[variables.length];
		u = new int[variables.length];
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {

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
			return  id_alldiff + numberId;
	
	}

	@Override
	public void impose(Store store) {
		int level = store.level;

		int pos = 0;
		positionMapping = new HashMap<Variable, Integer>();
		
		for (Variable v : listAlldiff) {
			positionMapping.put(v, pos++);
			v.putModelConstraint(this, getConsistencyPruningEvent(v));
			queueVariable(level, v);
		}
		grounded = new TimeStamp<Integer>(store, 0);
		
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void consistency(Store store) {
		
		this.store = store;

		if (store.currentQueue == queueIndex) {	
			
			while (!variableQueue.isEmpty()) {
				
				LinkedHashSet<Variable> fdvs = variableQueue;
				variableQueue = new LinkedHashSet<Variable>();
			
				for (Variable Q : fdvs)
					if (Q.singleton()) {
						int qPos = positionMapping.get(Q);
						int groundPos = grounded.value();
						if (qPos > groundPos) {
							list[qPos] = list[groundPos];
							list[groundPos] = Q;
							positionMapping.put(Q, groundPos);
							positionMapping.put(list[qPos], qPos);
							grounded.update(++groundPos);
							for (int i = groundPos; i < list.length; i++)
								list[i].domain.inComplement(store.level, list[i], Q.min());
						}
						else if (qPos == groundPos) {
							grounded.update(++groundPos);
							for (int i = groundPos; i < list.length; i++)
								list[i].domain.inComplement(store.level, list[i], Q.min());
						}
					
					}
			}

			if (queueIndex + 1 < store.queueNo) {
				store.changed[queueIndex + 1].add(this);
				return;
			}

		}

		maxPass();
		minPass();

	}

	@Override
	public void removeLevel(int level) {
	}

	void maxPass() {
		
		Arrays.sort(listAlldiff, maxVariable);

		for (int i = 0; i < listAlldiff.length; i++) {
			min[i] = listAlldiff[i].min();
			max[i] = listAlldiff[i].max();
		}

		for (int i = 0; i < listAlldiff.length; i++)
			insertMax(i);
	}

	void minPass() {
		
		Arrays.sort(listAlldiff, minVariable);

		for (int i = 0; i < listAlldiff.length; i++) {
			min[i] = listAlldiff[i].min();
			max[i] = listAlldiff[i].max();
		}

		for (int i = 0; i < listAlldiff.length; i++)
			insertMin(i);
	}

	void insertMax(int i) {
		u[i] = min[i];

		int bestMin = MaxInt + 1;
		for (int j = 0; j < i; j++) {
			if (min[j] < min[i]) {
				u[j]++;
				if (u[j] > max[i])
					store.throwFailException(this);
				if (u[j] == max[i] && min[j] < bestMin)
					bestMin = min[j];
			} else
				u[i]++;
		}
		if (u[i] > max[i])
			store.throwFailException(this);
		if (u[i] == max[i] && min[i] < bestMin)
			bestMin = min[i];

		if (bestMin <= MaxInt)
			incrMin(bestMin, max[i], i);
	}

	void incrMin(int a, int b, int i) {
		for (int j = i + 1; j < min.length; j++)
			if (min[j] >= a) {
				listAlldiff[j].domain.inMin(store.level, listAlldiff[j], b + 1);
			}
	}

	void insertMin(int i) {
		u[i] = max[i];

		int bestMax = MinInt - 1;
		for (int j = 0; j < i; j++) {
			if (max[j] > max[i]) {
				u[j]--;
				if (u[j] < min[i])
					store.throwFailException(this);
				if (u[j] == min[i] && max[j] > bestMax)
					bestMax = max[j];
			} else
				u[i]--;
		}
		if (u[i] < min[i])
			store.throwFailException(this);
		if (u[i] == min[i] && max[i] > bestMax)
			bestMax = max[i];

		if (bestMax >= MinInt)
			decrMax(min[i], bestMax, i);
	}

	void decrMax(int a, int b, int i) {
		for (int j = i + 1; j < max.length; j++)
			if (max[j] <= b) {
				listAlldiff[j].domain.inMax(store.level, listAlldiff[j], a - 1);
			}
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		result.append(" : alldiff([");
		
		for (int i = 0; i < listAlldiff.length; i++) {
			result.append(listAlldiff[i]);
			if (i < listAlldiff.length - 1)
				result.append(", ");
		}
		
		result.append("])");
		
		return result.toString();

	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id());
		constraint.setAttribute("flavour", "alldiff");
		constraint.setAttribute("arity", String.valueOf(listAlldiff.length));
		constraint.setAttribute("reference", "global:allDifferent");

		StringBuffer scope = new StringBuffer();
		for (int i = 0; i < listAlldiff.length - 1; i++)
			scope.append(  listAlldiff[i].id() + " " );
		
		scope.append(listAlldiff[listAlldiff.length - 1]);
		constraint.setAttribute("scope", scope.toString());
		
		return constraint;

	}

	/**
	 * Predicate description of the constraint.
	 * @return null as there is no predicate description of this constraint.
	 */
	public org.jdom.Element getPredicateXML() {
		return null;
	}


    static public Constraint fromXML(Element constraint, Store store) {

		Pattern pattern = Pattern.compile(" ");
		String scope = constraint.getAttributeValue("scope");
		int arity = Integer.valueOf(constraint.getAttributeValue("arity"));
		
		String[] varNames = pattern.split(scope);
		Variable[] vars = new Variable[arity];

		int no = -1;
		for (String n : varNames) {

			no++;
			vars[no] = store.findVariable(n);

		}

		return new Alldiff(vars);

	}

    class VariablemaxComparator<T extends Variable> implements Comparator<T> {

		VariablemaxComparator() {
		}

		public int compare(T o1, T o2) {
			return (o1.max() - o2.max());
		}
	}

	class VariableminComparator<T extends Variable> implements Comparator<T> {

		VariableminComparator() {
		}

		public int compare(T o1, T o2) {
			return (o2.min() - o1.min());
		}
	}
	
}

