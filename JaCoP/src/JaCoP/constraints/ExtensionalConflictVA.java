/**
 *  ExtensionalConflictVA.java 
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
import java.util.LinkedHashSet;
import java.util.PriorityQueue;

import JaCoP.core.Constants;
import JaCoP.core.Store;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;

/**
 * Extensional constraint assures that none of the tuples explicitly given is enforced in the
 * relation.
 * 
 * This implementation tries to balance the usage of memory versus time
 * efficiency.
 * 
 * @author Radoslaw Szymanek
 * @version 2.4
 */

public class ExtensionalConflictVA extends Constraint implements Constants {

	int [] tuple;
	
	/**
	 * It seeks support tuple for a given variable and its value.
	 * @param varPosition variable for which the support is seeked.
	 * @param value value of the variable for which support is seeked.
	 * @return support tuple supporting varPosition-value pair.
	 */
	public int[] seekSupportVA(int varPosition, int value) {

		if (debugAll)
			System.out.println("Seeking support for " + x[varPosition]
					+ " and value " + value);

		int[] t = tuple;
		int pos = findPosition(value, values[varPosition]);
		
		if (pos == -1)
			return setFirstValid(varPosition, value);
		
		try {
			if (supports[varPosition][pos] != null)
				System.arraycopy(supports[varPosition][pos], 0, t, 0, x.length);
			else
				t = setFirstValid(varPosition, value);
		}
		catch (Exception ex) {
			t = setFirstValid(varPosition, value);
		}	
		
		assert ( t != null) : " First valid tuple can not be null ";
		
		int invalidPosition = -1;
		
		int [][] tuplesVarValue = tuples[varPosition][pos];
		int [] lastofsequenceVarValue = lastofsequence[varPosition][pos];
		
		while (true) {
			// find if t is disallowed  
			
			int position = isDisallowed(varPosition, value, t);
			if (position == -1) {
				recordSupport(varPosition, value, t);
				return t;
			}
			
			// finds the last of sequence of disallowed tuples from the 
			// convex.
			
			if (lastofsequenceVarValue[position] != position)
				System.arraycopy(tuplesVarValue[lastofsequenceVarValue[position]], 0, t, 0, x.length);
			
			invalidPosition = seekInvalidPosition(t);
			
			if (invalidPosition == -1) {
				int i = x.length - 1;
				for (; i >= 0; i--) {
					if (i != varPosition) 
						if (t[i] == x[i].max())
							t[i] = x[i].min();
						else {
							t[i] = x[i].domain.nextValue(t[i]);
							break;
						}		
				}
				if (i == -1)
					return null;
			}
			else {
				// setNextValidPart
				// t = setNextValid(varPosition, value, t, invalidPosition);
				for (int i = invalidPosition + 1; i < x.length; i++)
					if (i != varPosition)
						t[i] = x[i].min();
				boolean cont = false;
				for (int i = invalidPosition; i >= 0; i--)
					if (i != varPosition)
						if (t[i] >= x[i].max())
							t[i] = x[i].min();
						else {
							t[i] = x[i].domain.nextValue(t[i]);
							cont = true;
							break;
						}
				if (!cont)
					return null;
				}
		}

	}

	int [][][] lastofsequence;

	int [][][] supports;
	
	private void recordSupport(int varPosition, int value, int[] t) {
		
		int pos = findPosition(value, values[varPosition]);
		
		int [][] supports4variable = supports[varPosition];

		if (supports4variable == null) {
			supports4variable = new int[values[varPosition].length][];
			supports4variable[pos] = new int [x.length];
			System.arraycopy(t, 0, supports4variable[pos], 0, x.length);
		}
		else {
			if (supports4variable[pos] == null)
				supports4variable[pos] = new int [x.length];
			System.arraycopy(t, 0, supports4variable[pos], 0, x.length);
		}
		
	}

	/**
	 * It computes the first valid tuple given restriction that a variable will
	 * be equal to a given value.
 	 * @param varPosition the position of the variable.
	 * @param value the value of the variable.
	 * @return the smallest valid tuple supporting varPosition-value pair.
	 */
	public int[] setFirstValid(int varPosition, int value) {

		int t[] = tuple;

		int noVars = x.length;
		for (int i = 0; i < noVars; i++)
			t[i] = x[i].min();

		t[varPosition] = value;

		return t;
	}

	/**
	 * It returns the position of disallowed tuple in the array of tuples for a given variable-value pair.
	 * @param varPosition variable for which we search for the forbidden tuple.
	 * @param value value for which we search for the forbidden tuple.
	 * @param t tuple which we check for forbidness.
	 * @return position of the forbidden tuple, -1 if it is not forbidden.
	 */
	public int isDisallowed(int varPosition, int value, int[] t) {

		if (debugAll)
			System.out.println("variable" + x[varPosition] + " position "
					+ varPosition + " value " + value);

		int[][] tuplesForGivenVariableValuePair = tuples[varPosition][findPosition(
				value, values[varPosition])];

		int left = 0;
		int right = tuplesForGivenVariableValuePair.length - 1;
		
		int position = (left + right) >> 1;

		while (!(left + 1 >= right)) {

			if (smaller(t, tuplesForGivenVariableValuePair[position])) {
				right = position;
			} else {
				left = position;
			}

			position = (left + right) >> 1;

		}

		if (left != right) {
			if (equal(t, tuplesForGivenVariableValuePair[left])) {
				return left;
			} 
		
			if (equal(t, tuplesForGivenVariableValuePair[right])) {
				return right;
			}
		}
		else {
			
			if (equal(t, tuplesForGivenVariableValuePair[left]))
				return left;
			
		}
		
		return -1;
	}

    /**
     * It finds the position at which the tuple is invalid. The value is not in the domain 
     * of the corresponding variable.
     * @param t tuple being check for in-validity
     * @return the position in the tuple at which the corresponding variable does not contain the value used by tuple, -1 if no invalid position exists.
     */
    public int seekInvalidPosition(int[] t) {

		int noVars = x.length;
		for (int i = 0; i < noVars; i++)
			if (!x[i].domain.contains(t[i]))
				return i;
		return -1;
	}

	static final boolean debugAll = false;

	static final boolean debugPruning = false;

	static int idNumber = 1;

	final static short type = extSupport;

	int numberTuples = 0;

	Store store;

	/**
	 * It represents tuples which are supports for each of the variables. The
	 * first index denotes variable index. The second index denotes value index.
	 * The third index denotes tuple.
	 */

	int[][][][] tuples;

	private int[][] tuplesFromConstructor;

	/**
	 * It represents values which are supported for a variable.
	 */
	int[][] values;

	LinkedHashSet<Variable> variableQueue = new LinkedHashSet<Variable>();

	/**
	 * It stores variables within this extensional constraint, order does
	 * matter.
	 */

	Variable[] x;

	/**
	 * The constructor does not create local copy of tuples array. Any changes
	 * to this array will reflect on constraint behavior. Most probably
	 * incorrect as other data structures will not change accordingly.
	 * @param variables the scope of the extensional conflict constraint.
	 * @param tuples the conflict (forbidden) tuples for that constraint.
	 */

	public ExtensionalConflictVA(ArrayList<? extends Variable> variables,
			int[][] tuples) {

		this(variables.toArray(new Variable[variables.size()]), tuples);

	}

	/**
	 * Constructor stores reference to tuples until imposition, any changes to
	 * tuples parameter will be reflected in the constraint behavior. Changes to
	 * tuples should not performed under any circumstances. The tuples array is
	 * not copied to save memory and time.
	 * @param variables 
	 * @param tuples 
	 */

	public ExtensionalConflictVA(Variable[] variables, int[][] tuples) {

		this.x = new Variable[variables.length];
		supports = new int[variables.length][][];
		tuple = new int[variables.length];
		
		for (int i = 0; i < variables.length; i++)
			x[i] = variables[i];

		tuplesFromConstructor = tuples;

		numberId = idNumber++;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(x.length + 1);

		for (int i = 0; i < x.length; i++) {
			Variables.add(x[i]);
		}

		return Variables;
	}

	/**
	 * It puts back tuples which have lost their support status at the level
	 * which is being removed.
	 */

	@Override
	public void removeLevel(int level) {
		
		// backtracking has occurred (removeLevel) therefore
		// restart tuples can not be reused.
		supports = new int[x.length][][];	
		variableQueue = new LinkedHashSet<Variable>();
		
	}

	@Override
	public void consistency(Store store) {

		if (debugAll)
			System.out.println("Begin " + this);

		boolean pruned = true;

		while (pruned) {

			pruned = false;
			// For each variable
			for (int varPosition = 0; varPosition < x.length; varPosition++) {
				// for each value

				for (ValueEnumeration enumer = x[varPosition].domain
						.valueEnumeration(); enumer.hasMoreElements();) {

					int value = enumer.nextElement();

					if (debugAll)
						System.out.println("Seeking support for "
								+ x[varPosition] + " and value " + value);
					int[] t = seekSupportVA(varPosition, value);

					if (debugAll)
						System.out.println("Found support?" + !(t == null));

					if (t == null) {
						x[varPosition].domain.inComplement(store.level,
								x[varPosition], value);
						// store.inComplement(x[varPosition], value);
						pruned = true;
					}

				}
			}
		}

		if (debugAll)
			System.out.println("End " + this);
	}

	protected int findPosition(int value, int[] values) {

		int left = 0;
		int right = values.length - 1;

		int position = (left + right) >> 1;

		if (debugAll) {
			System.out.println("Looking for " + value);
			for (int v : values)
				System.out.print("val " + v);
			System.out.println("");
		}

		while (!(left + 1 >= right)) {

			if (debugAll)
				System.out.println("left " + left + " right " + right
						+ " position " + position);

			if (values[position] > value) {
				right = position;
			} else {
				left = position;
			}

			position = (left + right) >> 1;

		}

		if (values[left] == value)
			return left;

		if (values[right] == value)
			return right;

		return -1;

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
			return Constants.ANY;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_extCon + numberId;
	}

	@Override
	public void impose(Store store) {
	
		store.registerRemoveLevelListener(this);

		for (int i = 0; i < x.length; i++) {
			x[i].putModelConstraint(this, getConsistencyPruningEvent(x[i]));
		}

		store.addChanged(this);
		store.countConstraint();

		this.store = store;

		if (debugAll) {
			for (Variable var : x)
				System.out.println("Variable " + var);
		}

		// TO DO, adjust (even simplify) all internal data structures
		// to current domains of variables.
		// filter which ignores all tuples which already are not supports.

		boolean[] stillConflict = new boolean[tuplesFromConstructor.length];

		int noConflicts = 0;

		int[][] supportCount = new int[x.length][];

		int i = 0;

		for (int[] t : tuplesFromConstructor) {

			stillConflict[i] = true;

			int j = 0;

			if (debugAll) {
				System.out.print("conflict for analysis[");
				for (int val : t)
					System.out.print(val + " ");
				System.out.println("]");
			}

			for (int val : t) {

				// if (debugAll) {
				// System.out.print("Checking " + x[j]);
				// System.out.print(" " + val);
				// System.out.println(Domain.domain.contains(x[j].dom(), val));
				// }

				if (!x[j].dom().contains(val)) {
					// if (!Domain.domain.contains(x[j].dom(), val)) {
					stillConflict[i] = false;
					break;
				}

				j++;
			}

			if (stillConflict[i])
				noConflicts++;

			if (debugAll) {
				if (!stillConflict[i]) {
					System.out.print("Not support [");
					for (int val : t)
						System.out.print(val + " ");
					System.out.println("]");
				}
			}

			i++;

		}

		if (debugAll) {
			System.out.println("No. still conflicts " + noConflicts);
		}

		int[][] temp4Shrinking = new int[noConflicts][];

		i = 0;
		int k = 0;

		for (int[] t : tuplesFromConstructor) {

			if (stillConflict[k]) {
				temp4Shrinking[i] = t;
				i++;

				if (debugAll) {
					System.out.print("Still support [");
					for (int val : t)
						System.out.print(val + " ");
					System.out.println("]");
				}

			}

			k++;

		}

		// Only still conflicts are kept.

		tuplesFromConstructor = temp4Shrinking;

		numberTuples = tuplesFromConstructor.length;

		// TO DO, just store parameters for later use in impose
		// function, move all code below to impose function.


		this.tuples = new int[x.length][][][];
		this.values = new int[x.length][];

		lastofsequence = new int[x.length][][];
	
		for (i = 0; i < x.length; i++) {
			
			HashMap<Integer, Integer> val = new HashMap<Integer, Integer>();

			for (int[] t : tuplesFromConstructor) {

				Integer value = t[i];
				Integer key = val.get(value);

				if (key == null)
					val.put(value, 1);
				else
					val.put(value, key + 1);
			}

			if (debugAll)
				System.out.println("values " + val.keySet());

			PriorityQueue<Integer> sortedVal = new PriorityQueue<Integer>(val
					.keySet());

			if (debugAll)
				System.out.println("Sorted val size " + sortedVal.size());

			values[i] = new int[sortedVal.size()];
			supportCount[i] = new int[sortedVal.size()];
			this.tuples[i] = new int[sortedVal.size()][][];

			if (debugAll)
				System.out.println("values length " + values[i].length);

			for (int j = 0; j < values[i].length; j++) {

				if (debugAll)
					System.out.println("sortedVal " + sortedVal);

				values[i][j] = sortedVal.poll();
				supportCount[i][j] = val.get(new Integer(values[i][j]));
				this.tuples[i][j] = new int[supportCount[i][j]][];
			}

			int m = 0;
			for (int[] t : tuplesFromConstructor) {

				int value = t[i];
				int position = findPosition(value, values[i]);

				this.tuples[i][position][--supportCount[i][position]] = t;
				m++;

			}

			// @todo, check & improve sorting functionality (possibly reuse existing sorting functionality).
			for (int j = 0; j < tuples[i].length; j++)
				store.sortTuplesWithin(tuples[i][j]);

			lastofsequence[i] = new int[tuples[i].length][];
			
			// compute lastOfSequence for each i,j and tuple.
			// i - for each variable
			for (int j = 0; j < tuples[i].length; j++) { // for each value 
				lastofsequence[i][j] = new int [tuples[i][j].length];
				for (int l = 0; l < tuples[i][j].length; l++) // for each tuple
						lastofsequence[i][j][l] = computeLastOfSequence(tuples[i][j], i, l);
			}

		}

		tuplesFromConstructor = null;

		store.raiseLevelBeforeConsistency = true;

	}
	
	private int computeLastOfSequence(int[][] is, int posVar, int l) {
		
		int[] t = tuple;
		
		System.arraycopy(is[l], 0, t, 0, x.length);
		
		while (l + 1 < is.length) {

			for (int i = x.length - 1; i >= 0; i--)
				if (i != posVar)
					if (t[i] >= x[i].max())
						t[i] = x[i].min();
					else {
						t[i] = x[i].domain.nextValue(t[i]);
						break;
					}
			
			if (!equal(is[l+1], t))
				return l;
			else {

				System.arraycopy(is[++l], 0, t, 0, x.length);				

			}
				
		}
			
		return l;
	}

	@Override
	public void queueVariable(int level, Variable V) {

		if (debugAll)
			System.out.println("Var " + V + V.recentDomainPruning());

		variableQueue.add(V);
	}

	@Override
	public void removeConstraint() {

		for (int i = 0; i < x.length; i++)
			x[i].removeConstraint(this);

	}

	@Override
	public boolean satisfied() {

		int i = 0;
		while (i < x.length) {
			if (!x[i].singleton())
				return false;
			i++;
		}

		return true;

	}

	boolean smaller(int[] tuple1, int[] tuple2) {

		int arity = tuple1.length;
		for (int i = 0; i < arity && tuple1[i] <= tuple2[i]; i++)
			if (tuple1[i] < tuple2[i])
				return true;

		return false;

	}

	boolean equal(int[] tuple1, int[] tuple2) {

		int arity = tuple1.length;
		for (int i = 0; i < arity; i++)
			if (tuple1[i] != tuple2[i])
				return false;

		return true;

	}

	@Override
	public String toString() {

		StringBuffer tupleString = new StringBuffer();

		tupleString.append(id() );
		tupleString.append("(");

		for (int i = 0; i < x.length; i++) {
			tupleString.append(x[i].toString());
			if (i + 1 < x.length)
				tupleString.append(" ");
		}

		tupleString.append(")");

		if (tuplesFromConstructor != null) {

			int[][] subset = tuplesFromConstructor;

			for (int p1 = 0; p1 < subset.length; p1++)
				for (int p2 = subset.length - 1; p2 > p1; p2--)
					if (smaller(subset[p2], subset[p2 - 1])) {
						int[] temp = subset[p2];
						subset[p2] = subset[p2 - 1];
						subset[p2 - 1] = temp;
					}

			for (int p1 = 0; p1 < subset.length; p1++) {
				for (int p2 = 0; p2 < subset[p1].length; p2++) {
					
					tupleString.append(subset[p1][p2]);
					
					if (p2 != subset[p1].length - 1)
						tupleString.append(" ");
				
				}

				if (p1 != subset.length - 1)
					tupleString.append("|");
			}

			tupleString.append(")");
			return tupleString.toString();

		}

		return tupleString.toString();

	}

	/**
	 * XCSP 2.0 format uses relations elements to specify table constraints. Therefore
	 * this function produces a relation xml element, which can be used to specify the
	 * constraint entirely.
	 */
	
	@Override
	public org.jdom.Element toXML() {

		// @todo improve sorting of the tuples, refactor into a separate function.
		
		org.jdom.Element constraint = new org.jdom.Element("relation");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("arity", (new Integer(x.length)).toString());

		constraint.setAttribute("semantics", "conflicts");

		StringBuffer tupleString = new StringBuffer();

		int noTuples = 0;
		
		for (int i = 0; i < tuples[0].length; i++) {

			int[][] subset = tuples[0][i];

			noTuples += subset.length;
			
			// Sorting the tuples required for XCSP 2.0

			for (int p1 = 0; p1 < subset.length; p1++)
				for (int p2 = subset.length - 1; p2 > p1; p2--)
					if (smaller(subset[p2], subset[p2 - 1])) {
						int[] temp = subset[p2];
						subset[p2] = subset[p2 - 1];
						subset[p2 - 1] = temp;
					}

			for (int p1 = 0; p1 < subset.length; p1++) {
				for (int p2 = 0; p2 < subset[p1].length; p2++) {
					tupleString
							.append((new Integer(subset[p1][p2])).toString());
					if (p2 != subset[p1].length - 1)
						tupleString.append(" ");
				}

				if (i != tuples[0].length - 1 || p1 != subset.length - 1)
					tupleString.append("|");
			}

		}

		constraint.setAttribute("nbTuples", (new Integer(noTuples))
				.toString());
		constraint.setText(tupleString.toString());

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}
	
	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			for (Variable v : x) v.weight++;
		}
	}

}
