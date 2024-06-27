/**
 *  Values.java 
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

import java.util.*;
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.IntervalDomainValueEnumeration;

/**
 * Constraint Values counts number of different values on a list of Variables.
 * 
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * 
 * @version 2.4
 */

public class Values extends Constraint implements Constants {

	String id;
	
	static int IdNumber = 1;
	final static short type = val;
	
	Variable count;
	Variable[] list;
	
	byte numberArgs;

	Comparator<Variable> minFDV = new FDVminimumComparator<Variable>();

	static final boolean debug = false;

	/**
	 * It constructs Values constraint.
	 * 
	 * @param list list of variables for which different values are being counted.
	 * @param count specifies the number of different values in the list. 
	 */
	public Values(Variable[] list, Variable count) {
		this.queueIndex = 1;
		id = id_val + IdNumber;
		IdNumber++;
		numberArgs = 1;
		this.count = count;
		this.list = new Variable[list.length];
		for (int i = 0; i < list.length; i++) {
			this.list[i] = list[i];
			numberArgs++;
		}
	}

	/**
	 * It constructs Values constraint.
	 * 
	 * @param list list of variables for which different values are being counted.
	 * @param count specifies the number of different values in the list. 
	 */
	public Values(ArrayList<Variable> list, Variable count) {
		this.queueIndex = 1;
		id = id_val + IdNumber;
		IdNumber++;
		numberArgs = 1;
		this.count = count;
		this.list = new Variable[list.size()];
		for (int i = 0; i < list.size(); i++) {
			this.list[i] = list.get(i);
			numberArgs++;
		}
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_val + numberId;
	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public int numberArgs() {
		return numberArgs;
	}

	@Override
	public void impose(Store store) {
		count.putConstraint(this);
		for (Variable v : list)
			v.putConstraint(this);
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void consistency(Store store) {

		while (store.newPropagation) {
			
			store.newPropagation = false;

			Arrays.sort(list, minFDV);
			
			if (debug)
				System.out.println("Sorted : \n" + this);

			int minNumberDifferent = 1, minimumMax = list[0].max();

			ArrayList<HashSet<Integer>> graph = new ArrayList<HashSet<Integer>>();
			int numberSingleton = 0;
			Domain singletonValues = new IntervalDomain();

			for (Variable v : list) {

				// compute information for pruning list of Variables
				if (v.singleton()) {
					numberSingleton++;
					singletonValues.addDom(v.min(), v.min());
				}

				// compute minimal value for count
				if (v.min() > minimumMax) {
					minNumberDifferent++;
					minimumMax = v.max();
				}
				if (v.max() < minimumMax)
					minimumMax = v.max();

				// build bi-partite graph for computing maximal value for count
				HashSet<Integer> nodeConnections = new HashSet<Integer>();
				for (IntervalDomainValueEnumeration e = new IntervalDomainValueEnumeration(
						(IntervalDomain) v.dom()); e.hasMoreElements();) {
					nodeConnections.add(e.nextElement());
				}
				graph.add(nodeConnections);
			}

			// compute maximal value for count
			int maxNumberDifferent = bipartiteGraphMatching(graph);

			if (debug)
				System.out.println("Minimum number of different values = "
						+ minNumberDifferent);
			if (debug)
				System.out.println("Maximum number of different values = "
						+ maxNumberDifferent);

			count.domain.in(store.level, count, minNumberDifferent,
					maxNumberDifferent);

			if (debug)
				System.out.println("Number singleton values = "
						+ numberSingleton + " Values = " + singletonValues);

			if (count.max() == singletonValues.getSize()
					&& numberSingleton < list.length) {
				for (Variable v : list)
					if (!v.singleton())
						v.domain.in(store.level, v, singletonValues);
			} else {
				int diffMin = count.min() - singletonValues.getSize(), diffSingleton = list.length
						- numberSingleton;
				if (diffMin == diffSingleton)
					for (Variable v : list)
						if (!v.singleton())
							v.domain.in(store.level, v, singletonValues
									.complement());
			}
		}
	}

	@Override
	public boolean satisfied() {
		boolean sat = true;
		int i = 0;
		if (count.singleton())
			while (sat && i < list.length) {
				sat = list[i].singleton();
				i++;
			}
		else
			return false;
		return sat;
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> FDVs = new ArrayList<Variable>(2);

		FDVs.add(count);
		for (Variable v : list)
			FDVs.add(v);
		return FDVs;
	}

	@Override
	public void removeConstraint() {
		count.removeConstraint(this);
		for (Variable v : list)
			v.removeConstraint(this);
	}

	@Override
	public String toString() {

		StringBuffer result = new StringBuffer ( id () );
		result.append(" : Values([");
		for (int i = 0; i < list.length; i++) {
			if (i < list.length - 1)
				result.append( list[i] ).append( ", " );
			else
				result.append( list[i] );
		}
		result.append( "], " ).append( count ).append( " )" );
		return result.toString();
	}

	
	@SuppressWarnings("unchecked")
	int bipartiteGraphMatching(ArrayList<HashSet<Integer>> graph) {

		int u = 0;

		HashMap<Integer, Integer> matching = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> reverseMatching = new HashMap<Integer, Integer>();

		// Gready matching that creates initial matching
		u = 0;
		for (HashSet<Integer> s : graph) {
			for (Integer v : s) {
				if (!matching.containsValue(v)) {
					matching.put(u, v);
					reverseMatching.put(v, u);
					break;
				}
			}
			u++;
		}

		HashSet<Integer> values = new HashSet<Integer>();
		for (HashSet<Integer> s : graph)
			for (Integer v : s)
				values.add(v);

		u = 0;
		HashMap<Integer, Integer> valuesMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> valuesKeyMap = new HashMap<Integer, Integer>();
		for (Integer v : values) {
			valuesMap.put(v, u);
			valuesKeyMap.put(u, v);
			u++;
		}

		int uLength = graph.size();
		int vLength = values.size();
		int sink = 1 + uLength + vLength;

		HashSet[] g = new HashSet[1 + uLength + vLength];
		boolean done = false;
		byte[] b = new byte[g.length];
		Stack<Integer> stack = new Stack<Integer>();

		while (!done) {

			// Create G
			HashSet<Integer> nextNodesForSource = new HashSet<Integer>();
			u = 0;
			for (HashSet<Integer> hg : graph) {
				HashSet<Integer> next = new HashSet<Integer>();
				for (Integer v : hg)
					if (matching.containsKey(u)) {
						if (matching.get(u) != v)
							next.add(valuesMap.get(v) + uLength + 1);
					} else {
						next.add(valuesMap.get(v) + uLength + 1);
						nextNodesForSource.add(u + 1);
					}

				g[u + 1] = next;
				u++;
			}
			g[0] = nextNodesForSource;

			for (Integer v : values) {
				u = 1 + uLength + valuesMap.get(v);
				HashSet<Integer> next = new HashSet<Integer>();
				if (!reverseMatching.containsKey(v)) {
					next.add(sink);
					g[u] = next;
				} else {
					next.add(reverseMatching.get(v) + 1);
					g[u] = next;
				}
			}

			for (int i = 0; i < b.length; i++)
				b[i] = 0;

			done = true;

			// Alternating paths
			stack.push(0);
			while (!stack.empty()) {
				int top = stack.peek();
				while (g[top].size() > 0) {
					
					Iterator nextNode = g[top].iterator();
					
					int first = (Integer) nextNode.next();
					
					if (debug)
						System.out.println("PUSH " + first);
					// remove edge (TOP, FIRST) from G
					
					if (debug) 
						System.out.println("Checking edge (" + top + ", " + first + ")");
					
					g[top].remove(first);
					if (first == sink)
						while (stack.size() >= 2) {
							int v = stack.pop();
							u = stack.pop();
							int vKey = valuesKeyMap.get(v - 1 - uLength);
							if (debug) 
								System.out.println("("+u+", "+v+")" + "("+ (int)(u-1) + ", " + vKey + ")");
							matching.remove(u - 1);
							matching.put(u - 1, vKey);
							reverseMatching.remove(vKey);
							reverseMatching.put(vKey, u - 1);
							done = false;
							if (debug)
								System.out.println("Improved matching " + matching);
						}
					else if (b[first] == 0) {
						b[first] = 1;
						stack.push(first);
						top = first;
					}
				}
				stack.pop();
			}
		}

		if (debug) {
		
			System.out.println("Final graph G");
		
			for (HashSet s : g)
				System.out.println(u++ + ":" + s);
			
			System.out.println("Final matching (u, v): " + matching);
		}
		
		return matching.size();
	}

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
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_val);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add( count );
		for (int i = 0; i < list.length; i++)
			scopeVars.add(list[i]);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );

		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	

		org.jdom.Element term = new org.jdom.Element("count");
		term.setText(count.id());
		constraint.addContent(term);

		org.jdom.Element tList = new org.jdom.Element("list");

		StringBuffer tString = new StringBuffer();
		for (int i = 0; i < list.length; i++)
			tString.append( list[i].id() ).append( " " );

		tList.setText(tString.toString().trim());

		constraint.addContent(tList);

		return constraint;

	}

	/**
	 * It creates a constraint from XCSP description.
	 * @param constraint an XML description of the constraint.
	 * @param store the constraint store in which context the constraint is being created.
	 * @return created constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		 
		String list = constraint.getChild("list").getText();
		String count = constraint.getChild("count").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(list);

		ArrayList<Variable> x = new ArrayList<Variable>();

		for (String n : varsNames)
			x.add(store.findVariable(n));

		return new Values(x, store.findVariable(count));
		
	}	
	
	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			count.weight++;
			for (Variable v : list)
				v.weight++;
		}
	}
}

class FDVminimumComparator<T extends Variable> 
			implements Comparator<T> {

	FDVminimumComparator() {
	}

	public int compare(T o1, T o2) {
		return (o1.min() - o2.min());
	}
	
}
