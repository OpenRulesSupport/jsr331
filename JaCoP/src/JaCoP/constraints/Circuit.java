/**
 *  Circuit.java 
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
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.MutableVar;
import JaCoP.core.Store;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;

/**
 * Circuit constraint assures that all variables build a Hamiltonian
 * circuit. Value of every variable x[i] points to the next variable in 
 * the circuit. Variables create one circuit. 
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Circuit extends Alldiff implements Constants {

	final static short type = circuit;

	int chainLength = 0;

	boolean firstConsistencyCheck = true;

	MutableVar graph[];

	int idd = 0;

	int sccLength = 0;

	int[] val;

	static int IdNumber = 0;
	
	Hashtable<Variable, Integer> valueIndex = new Hashtable<Variable, Integer>();

	int firstConsistencyLevel;

	/**
	 * It constructs a circuit constraint.
	 * @param store store in which the constraint is being created.
	 * @param variables variables which must form a circuit.
	 */
	public Circuit(Store store, ArrayList<? extends Variable> variables) {

		super(variables);

		Alldiff.IdNumber--;
		numberId = IdNumber++;
		
		int i = 0;
		for (Variable v : variables)
			valueIndex.put(v, i++);

		graph = new CircuitVar[list.length];
		for (int j = 0; j < graph.length; j++)
			graph[j] = new CircuitVar(store, 0, 0);
		val = new int[list.length];

	}

	/**
	 * It constructs a circuit constraint.
	 * @param store store in which the constraint is being created.
	 * @param variables variables which must form a circuit.
	 */
	public Circuit(Store store, Variable[] variables) {
		super(variables);

		Alldiff.IdNumber--;
		numberId = IdNumber++;

		int i = 0;
		for (Variable v : variables)
			valueIndex.put(v, i++);

		graph = new CircuitVar[list.length];
		for (int j = 0; j < graph.length; j++)
			graph[j] = new CircuitVar(store, 0, 0);
		val = new int[list.length];
	}

	@Override
	public void consistency(Store store) {

		if (firstConsistencyCheck) {
			for (int i = 0; i < listAlldiff.length; i++) {
				listAlldiff[i].domain.in(store.level, listAlldiff[i], 1, listAlldiff.length);
				listAlldiff[i].domain.inComplement(store.level, listAlldiff[i], i + 1);
			}
			firstConsistencyCheck = false;
			firstConsistencyLevel = store.level;
		}

		while (store.newPropagation) {
			store.newPropagation = false;

			LinkedHashSet<Variable> fdvs = variableQueue;
			variableQueue = new LinkedHashSet<Variable>();
			
			alldifferent(store, fdvs);

			oneCircuit(store, fdvs);

		}
		sccs(store); // strongly connected components
	}

	void alldifferent(Store store, LinkedHashSet<Variable> fdvs) {

		for (Variable Q : fdvs) {
			if (Q.singleton()) {
				for (Variable V : listAlldiff)
					if (V != Q) 
						V.domain.inComplement(store.level, V, Q.min());
			}
		}	
		
	}	
	
	int firstNode(int current) {
		int start = current;
		int first;
		do {
			first = ((CircuitVarValue) graph[current - 1].value()).previous;
			if (first != 0 && first != start) {
				current = first;
				chainLength++;
			}
		} while (first != 0 && first != start);
		return current;
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {

		// If consistency function mode
		if (consistencyPruningEvents != null) {
				Integer possibleEvent = consistencyPruningEvents.get(var);
				if (possibleEvent != null)
					return possibleEvent;
			}
			return Constants.ANY;
	}

	// registers the constraint in the constraint store
	@Override
	public void impose(Store store) {
		super.impose(store);
	}

	int lastNode(Store S, int current) {
		int start = current;
		int last;
		do {
			last = ((CircuitVarValue) graph[current - 1].value()).next;
			if (last != 0) {
				current = last;
				if (++chainLength > graph.length)
					S.throwFailException(this);
            }
		} while (last != 0 && last != start);
		if (last == current)
			chainLength = 0;
		return current;
	}

	void oneCircuit(Store store, LinkedHashSet<Variable> fdvs) {
		Domain Qdom;

		for (Variable Q : fdvs) {
			Qdom = Q.dom();
			if (Qdom.singleton()) {
				updateChains(Q);
				int Qmin = Qdom.min();

				chainLength = 0;
				int lastInChain = lastNode(store, Qmin);
				int firstInChain = firstNode(Qmin);
				if (chainLength < listAlldiff.length - 1) {
					listAlldiff[lastInChain - 1].domain.inComplement(store.level,
							listAlldiff[lastInChain - 1], firstInChain);
				}
				
			}
		}
	}


	// @todo, what if there is a small circuit ending with zero, it is not consistent but can be satisfied.
	// redesign satisfied function since the implementation of alldiff has changed.
	@Override
	public boolean satisfied() {
		
		if (grounded.value() != listAlldiff.length)
			return false;
		
		boolean sat = super.satisfied(); // alldifferent

		if (sat) {
			int i = 0;
			int no = 0;
			do {
				i = list[i].min() - 1;
				no++;
			} while (no < listAlldiff.length && i != 0);
			if (no != listAlldiff.length || i != 0)
				return false;
		}
		return sat;
	}

	void sccs(Store store) {

		for (int i = 0; i < val.length; i++)
			val[i] = 0;
		idd = 0;

		sccLength = 0;
		visit(0, store);

	}

	// --- Strongly Connected Conmponents

	// Uses Trajan's algorithm to find strongly connected components
	// if found strongly connected component is shorter than the
	// Hamiltonian circuit length fail is enforced (one is unable to
	// to build a circuit. Based on the algorithm from the book
	// Robert Sedgewick, Algorithms, 1988, p. 482.

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		result.append(" : circuit([");
		
		for (int i = 0; i < list.length; i++) {
			result.append(list[i]);
			if (i < list.length - 1)
				result.append(", ");
		}
		result.append("])");
		
		return result.toString();
	}

	@Override
	public void removeLevel(int level) {
		if (firstConsistencyLevel == level)
			firstConsistencyCheck = true;
	}
	
	
	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return  id_circuit + numberId;
	}
	
	// --- Strongly Connected Conmponents

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_circuit);
		constraint.setAttribute("arity", String.valueOf(list.length));

		StringBuffer scope = new StringBuffer();
		for (int i = 0; i < list.length - 1; i++)
			scope.append(  list[i].id() ).append( " " );
		
		scope.append(list[list.length - 1]);
		constraint.setAttribute("scope", scope.toString());

		return constraint;

	}

	void updateChains(Variable v) {

		int i = valueIndex.get(v);
		int vMin = v.min();

		graph[i].update(new CircuitVarValue(vMin, ((CircuitVarValue) graph[i]
				.value()).previous));
		int j = vMin;
		graph[j - 1].update(new CircuitVarValue(((CircuitVarValue) graph[j - 1]
				.value()).next, i + 1));
	}

	int visit(int k, Store S) {

		int m, min = 0, t;
		idd++;
		val[k] = idd;
		min = idd;
		sccLength++;
		for (ValueEnumeration e = list[k].dom().valueEnumeration(); e
				.hasMoreElements();) {
			t = e.nextElement() - 1;
			if (val[t] == 0)
				m = visit(t, S);
			else
				m = val[t];
			if (m < min)
				min = m;
		}
		if (min == val[k]) {
			if (sccLength != list.length && sccLength != 0) {
				// the scc is shorter than all nodes in the circuit constraints
				sccLength = 0;
				S.throwFailException(this);
			}
			sccLength = 0;
		}
		return min;
	}

	static public Constraint fromXML(Element constraint, Store store) {
		
		String scope = constraint.getAttributeValue("scope");
		
		Pattern pattern = Pattern.compile(" ");	
		String[] varNames = pattern.split(scope);
		
		ArrayList<Variable> vars = new ArrayList<Variable>();

		for (String n : varNames)
			vars.add(store.findVariable(n));

		return new Circuit(store, vars);		
		
	}
	
}
