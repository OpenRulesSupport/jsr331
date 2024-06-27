/**
 *  Alldistinct.java 
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
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.util.SimpleArrayList;
import JaCoP.util.SimpleHashSet;
import JaCoP.core.Store;
import JaCoP.core.TimeStamp;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;

/**
 * Alldistinct constraint assures that all FDVs have different values.
 * 
 * This implementation is based on Regin paper. It uses slightly modified
 * Hopcroft-Karp algorithm to compute maximum matching. The value graph is
 * analysed and Tarjan algorithm for finding strongly connected components is
 * used. Maximum matching and Value Graph is stored as TimeStamp Mutable
 * variables to minimize recomputation. Value graph is expensive in terms of
 * memory usage. Use this constraint with care. One variable with domain
 * 0..1000000 will make it use few MB already and kill the efficiency.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class Alldistinct extends Constraint implements Constants {

	/* @todo implement in alldistinct remark, that only variable 
	 * with domain of size smaller equal n (number 
	 * of variables) can contribute to any pruning. */
	
	static final boolean debugAll = false;

	static final boolean debugPruning = false;

	static int idNumber = 1;

	final static short type = alldistinct;

	boolean backtrackOccured = true;

	/**
	 * It counts the number of executions of the consistency function. 
	 */
	public int consistencyChecks = 0;
	
	/**
	 * It computes how many times did consistency execution has been 
	 * re-executed due to narrowing event at the end of the consistency 
	 * function.
	 */
	public int fullConsistencyPassesWithNarrowingEvent = 0;

	// Any variable which matched edge ends up deleted is added to this
	// structure to obtain a new matched edge
	LinkedHashSet<Variable> freeVariables = new LinkedHashSet<Variable>();

	// Any variable which matched edge ends up deleted is added to this
	// structure to obtain a new matched edge
	protected ArrayList<Variable> freeVariablesAtFailure = new ArrayList<Variable>();

	// failure (inconsistency) discovered during imposition
	boolean impositionFailure = false;

	// each fdv has a matched value in maximal matching
	// this can change from consistency execution to consistency execution
	// any maximum matching is good for analysis.
	// However if no matched is removed then previously computed matching
	// can be directly used.
	// If a matched edge was removed then the remains of maximum matching
	// are used to compute a new maximum matching.
	IdentityHashMap<Variable, TimeStamp<Integer>> matching;

	boolean maximumMatchingNotRecomputed = true;

	// Important global variables for visitTarjan and revisitTarjan
	// Probably vn can be replaced by n.
	int n;

	TimeStamp<Integer> nStamp;

	boolean permutationConsistency = true;

	// Until pointer stampValues it stores all values still in domain of
	// at least one variable
	Integer potentialFreeValues[];

	// Represents for each Variable a scc to which it belongs.
	// This can change from a lot from matching to matching.
	// Variable may belong to different components given different matching.
	// Only if old maximum matching is used than the old components numbers can
	// be reused.
	IdentityHashMap<Variable, Integer> scc;

	IdentityHashMap<Variable, TimeStamp<Integer>> sccStamp;

	// All grounded variables are not taken into account, they have
	// their consistent value and can be simply omitted in any kind of
	// analysis.
	TimeStamp<Integer> stampNotGroundedVariables;

	// Stores how many variables were reached by free values. for
	// efficiency purposes. If equal number of variables where reached
	// then previously then we can stop doing reachability analysis.
	TimeStamp<Integer> stampReachability;

	// stamps specify the position of the last fdv which posses given integer
	// it decrease with increase of the store level.
	HashMap<Integer, TimeStamp<Integer>> stamps;

	// Variables for revisited Tarjan scc algorithm Reuse of scc
	// numbers previously computed, is only possible when matching is
	// not changed, since then any change can only split component
	// (components stay the same within the same matching). For Golomb
	// problem size 9, matching recomputed 50% of the time consistency
	// called. It is very important that this stamp is used at the
	// begining of the (re)computation of both visited and revisited
	// Tarjan algorithm.

	// For discovery of situation when number of values is equal
	// to number of variables, which means that there is no free
	// values
	// It also can say when to stop looking for free values since
	// it is easy to compute number of free values
	// "stampValues.value() - x.length"
	TimeStamp<Integer> stampValues;

	Store store;

	// Stores index for values in array potentialFreeValues it speeds
	// up significantly the swap operation when a value is not free
	// anymore and needs to be moved at the end of potentialFreeValues
	// array.
	HashMap<Integer, Integer> valueIndex;

	// valueMapVariable specifies which Variable posses given integer
	HashMap<Integer, SimpleArrayList<Variable>> valueMapVariable;

	LinkedHashSet<Variable> variableQueue = new LinkedHashSet<Variable>();

	int vn;

	Variable[] x;

	/**
	 * It constructs an alldistinct constraint.
	 * @param Variables arraylist of variables.
	 */
	public Alldistinct(ArrayList<? extends Variable> Variables) {

		commonInitialization(Variables);

	}

	/**
	 * It constructs an alldistinct constraint. 
	 * @param Variables an array of variables.
	 */
	public Alldistinct(Variable[] Variables) {

		ArrayList<Variable> temp = new ArrayList<Variable>();

		for (int i = 0; i < Variables.length; i++)
			temp.add(Variables[i]);

		commonInitialization(temp);
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(x.length);

		for (Variable var : x)
			Variables.add(var);

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
		variableQueue = new LinkedHashSet<Variable>();
		backtrackOccured = true;
	}

	private void commonInitialization(ArrayList<? extends Variable> Variables) {

		queueIndex = 0;

		numberId = idNumber++;

		x = new Variable[Variables.size()];

		for (int i = 0; i < Variables.size(); i++)
			x[i] = Variables.get(i);

		valueMapVariable = new HashMap<Integer, SimpleArrayList<Variable>>();
		stamps = new HashMap<Integer, TimeStamp<Integer>>();
		matching = new IdentityHashMap<Variable, TimeStamp<Integer>>();
		sccStamp = new IdentityHashMap<Variable, TimeStamp<Integer>>();

		Domain sum = new IntervalDomain(5);

		for (int i = 0; i < x.length; i++)
			sum.addDom(x[i].dom());

		// Each value in any variable domain will appear in a value graph
		// Therefore it is enough that one variable has a domain 0..1000000 to
		// create huge value graph making this constraint very ineffective
		int value = 0;
		SimpleArrayList<Variable> currentSimpleArrayList = null;

		potentialFreeValues = new Integer[sum.getSize()];

		valueIndex = new HashMap<Integer, Integer>(sum.getSize(), 0.5f);
		int m = 0;

		for (ValueEnumeration enumer = sum.valueEnumeration(); enumer
				.hasMoreElements();) {

			value = enumer.nextElement();
			Integer valueInteger = value;
			potentialFreeValues[m] = valueInteger;

			valueIndex.put(valueInteger, m);
			m++;

			currentSimpleArrayList = new SimpleArrayList<Variable>();
			for (int i = 0; i < Variables.size(); i++)
				if (x[i].domain.contains(value))
					currentSimpleArrayList.add(x[i]);
			valueMapVariable.put(valueInteger, currentSimpleArrayList);

		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void consistency(Store Store) {

		store = Store;

		if (impositionFailure)
			store.throwFailException(this);

		
		if (store.currentQueue == queueIndex) {

			LinkedHashSet<Variable> copy = (LinkedHashSet<Variable>) variableQueue.clone();

			for (Variable Q : copy) {
				if (Q.singleton()) {
					int qValue = Q.min();
					int lastNotGround = stampNotGroundedVariables.value();
					for (int i = 0; i <= lastNotGround; i++)
						if (x[i] != Q)
							x[i].domain.inComplement(store.level, x[i], qValue);
				}
			}

			if (queueIndex + 2 < store.queueNo) {
				store.changed[queueIndex + 2].add(this);
				return;
			}

		}
		
		consistencyChecks++;

		maximumMatchingNotRecomputed = true;

		if (stampValues.value() - 1 == stampNotGroundedVariables.value())
			permutationConsistency = true;
		else
			permutationConsistency = false;

		// Store all changed Variable variables locally
		LinkedHashSet<Variable> fdvs = variableQueue;

		if (debugAll) {
			System.out.println("Changed Variables " + variableQueue);
		}

		Domain Qdom = null;
		Integer zero = 0;
		SimpleArrayList<Variable> currentSimpleArrayList = null;
		TimeStamp<Integer> stamp = null;

		SimpleHashSet<Variable> singletons = new SimpleHashSet<Variable>();

		while (!variableQueue.isEmpty()) {

			variableQueue = new LinkedHashSet<Variable>();

			for (Variable Q : fdvs) {
				Qdom = Q.dom();
				if (Qdom.singleton()) {

					int qValue = Q.value();

					singletons.add(Q);

					int lastNotGroundedVariable = stampNotGroundedVariables
							.value();
					for (int i = 0; i <= lastNotGroundedVariable; i++)
						if (x[i] == Q) {
							x[i] = x[lastNotGroundedVariable];
							x[lastNotGroundedVariable] = Q;
							stampNotGroundedVariables
									.update(lastNotGroundedVariable - 1);
							break;
						}

					currentSimpleArrayList = valueMapVariable.get(qValue);

					// Timestamp variable which points to the position of
					// the last variable which still has qValue in its
					// domain
					stamp = stamps.get(qValue);

					int lastPosition = stamp.value();

					int positionV = currentSimpleArrayList.indexOf(Q);

					// It has to set position to variable which has
					// Qvalue in its domain to value 0 since only
					// one variable will have this value.
					stamp.update(zero);

					if (positionV > 0) {

						currentSimpleArrayList.setElementAt(
								currentSimpleArrayList.get(0), positionV);

						currentSimpleArrayList.setElementAt(Q, 0);
					}

					// All Variable which still had qValue in its domain
					// have this value removed
					// Domain complement = Domain.domain.complement(qValue);
					for (int c = 1; c <= lastPosition; c++)
						currentSimpleArrayList.get(c).domain.inComplement(
								store.level, currentSimpleArrayList.get(c),
								qValue);

					// Should be seperate from above loop since failure
					// in indexicals (in) will not clear variableQueue
					for (int c = 1; c <= lastPosition; c++)
						variableQueue.add(currentSimpleArrayList.get(c));
				}
			}
			fdvs.addAll(variableQueue);
		}

		variableQueue.clear();

		// If additional pruning has occured than re-execute consistency
		// algorithm
		boolean narrowingEvent = false;

		Iterator<Variable> iter = fdvs.iterator();

		if (debugAll) {
			System.out.println("Before");
			System.out.println("Mapping Value->Variable" + valueMapVariable);
			System.out.println("Stamps for size of Mapping Value->Variable"
					+ stamps);
			System.out.println("Maximum Matching " + matching);
		}

		for (; iter.hasNext();) {

			Variable V = iter.next();
			Domain vPrunedDomain = V.recentDomainPruning();

			if (debugAll) {
				System.out.println("Variable changed " + V);
				System.out.println("Pruned Domain " + vPrunedDomain);
			}

			if (!vPrunedDomain.isEmpty()) {

				// Check if any removed value was a edge in maximum matching
				Integer matchedValue = matching.get(V).value();

				// vPrunedDomain contains edge in maximum matching
				// this variable needs recomputation
				if (vPrunedDomain.contains(matchedValue))
					freeVariables.add(V);

				if (debugAll) {
					System.out.println(" V " + V + " matchedValue "
							+ matchedValue + " prunedDom " + vPrunedDomain
							+ "contains? "
							+ vPrunedDomain.contains(matchedValue));
				}

				for (ValueEnumeration enumer = vPrunedDomain.valueEnumeration(); enumer
						.hasMoreElements();) {

					int value = enumer.nextElement();
					Integer integerValue = value;

					currentSimpleArrayList = valueMapVariable.get(integerValue);

					stamp = stamps.get(integerValue);

					int lastPosition = stamp.value();

					int positionV = currentSimpleArrayList.indexOf(V,
							lastPosition);

					if (positionV == -1)
						continue;

					if (lastPosition > positionV) {

						stamp.update(lastPosition - 1);

						currentSimpleArrayList.setElementAt(
								currentSimpleArrayList.get(lastPosition),
								positionV);
						currentSimpleArrayList.setElementAt(V, lastPosition);

						continue;
					}

					if (lastPosition == positionV) {
						stamp.update(lastPosition - 1);

						if (lastPosition == 0) {

							// index of last existing value
							int stampValue = stampValues.value() - 1;
							// Move value to the position pointed by stampValue

							// indexDeletedValue is a current position of
							// deleted Value

							int indexDeletedValue = valueIndex
									.get(integerValue);

							if (indexDeletedValue < stampValue) {
								// Deleted value is NOT last in array of values
								// if last then no moving necessary

								// Update indexes in valueIndex hashtable
								valueIndex.put(
										potentialFreeValues[indexDeletedValue],
										stampValue);
								valueIndex.put(potentialFreeValues[stampValue],
										indexDeletedValue);

								// integerValue points to an integer from
								// potentialFreeValues
								// previous integerValue equals
								// potentialFreeValues[indexDeletedValue]
								integerValue = potentialFreeValues[indexDeletedValue];

								// Exchange values in potentialFreeValues array
								// use integerValue as swap
								potentialFreeValues[indexDeletedValue] = potentialFreeValues[stampValue];
								potentialFreeValues[stampValue] = integerValue;
							}
							// A value is not possible to be taken, decrease
							// number of values.
							stampValues.update(stampValues.value() - 1);

						}
					}
				}

			}

			else if (debugAll) {
				System.out
						.println("There was an Variable which was marked as changed"
								+ " but there is no difference in domain" + V);
				System.out.println("Most probably the result of current "
						+ " implementation of variableQueue signals");
			}

		}

		if (debugAll) {
			System.out.println("After");
			System.out.println("Mapping Value->Variable" + valueMapVariable);
			System.out.println("Stamps for size of Mapping Value->Variable"
					+ stamps);
		}

		if (debugAll) {
			System.out.println("Looking Maximum Matching ");
		}

		// Remove singletons from changed variables as no pruning
		// can be achieved for them.
		while (!singletons.isEmpty()) {
			Variable singleton = singletons.removeFirst();
			fdvs.remove(singleton);
			freeVariables.remove(singleton);
			Integer integerValue = singleton.value();
			matching.get(singleton).update(integerValue);

			// index of last existing value
			int stampValue = stampValues.value() - 1;
			// Move value to the position pointed by stampValue

			// indexDeletedValue is a current position of deleted Value

			int indexDeletedValue = valueIndex.get(integerValue);

			if (indexDeletedValue < stampValue) {
				// Deleted value is NOT last in array of values
				// if last then no moving necessary

				// Update indexes in valueIndex hashtable
				valueIndex.put(potentialFreeValues[indexDeletedValue],
						stampValue);
				valueIndex.put(potentialFreeValues[stampValue],
						indexDeletedValue);

				// integerValue points to an integer from potentialFreeValues
				// previous integerValue equals
				// potentialFreeValues[indexDeletedValue]
				integerValue = potentialFreeValues[indexDeletedValue];

				// Exchange values in potentialFreeValues array
				// use integerValue as swap
				potentialFreeValues[indexDeletedValue] = potentialFreeValues[stampValue];
				potentialFreeValues[stampValue] = integerValue;
			}
			// A value is not possible to be taken, decrease
			// number of values.
			stampValues.update(stampValues.value() - 1);
		}

		if (!freeVariables.isEmpty()) {

			if (!hopcroftKarpMaximumMatching()) {

				freeVariablesAtFailure = new ArrayList<Variable>(freeVariables);
				freeVariables.clear();
				variableQueue.clear();
				store.throwFailException((Constraint) this);
			}
			freeVariables.clear();
		} else {

			// Put all matched variables in valueMapVariable on the first
			// position
			// It is required during backtracking, old matching is reused
			// no need to recompute hopcroft algorithm but there is a need
			// to fix matching data structure.

			int lastNotGroundedVariable = stampNotGroundedVariables.value();
					
			Variable variable = null;
			Integer matchedValue;
			int positionMatched;

			for (int i = 0; i <= lastNotGroundedVariable; i++) {

				variable = x[i];
				
				matchedValue = matching.get(variable).value();
				currentSimpleArrayList = valueMapVariable.get(matchedValue);

				positionMatched = currentSimpleArrayList.indexOf(variable);
				if (positionMatched != 0) {

					currentSimpleArrayList.setElementAt(currentSimpleArrayList
							.get(0), positionMatched);

					currentSimpleArrayList.setElementAt(variable, 0);
				}
			}
		}

		if (debugAll) {
			System.out.println("Maximum Matching " + matching);
		}

		// Revisited Tarjan

		ArrayList<Variable> l = new ArrayList<Variable>();
		HashMap<Variable, Integer> dfsnum = new HashMap<Variable, Integer>();
		HashMap<Variable, Integer> low = new HashMap<Variable, Integer>();

		n = nStamp.value();

		int lastNotGroundedVariable = stampNotGroundedVariables.value();

		if (maximumMatchingNotRecomputed || permutationConsistency) {

			while (!fdvs.isEmpty()) {

				Variable changedVariable = fdvs.iterator().next();

				fdvs.remove(changedVariable);

				if (debugAll) {
					System.out.println("Tarjan start, changed variabled "
							+ changedVariable);
				}

				revisitTarjan(changedVariable, l, dfsnum, low, fdvs);

				if (debugAll) {
					System.out.println("Tarjan end");
				}

			}

			// important to keep n as large as number of the highest current
			// component
			nStamp.update(n + 1);

		} else {
			// New maximum matching may cause different scc for variables
			scc = new IdentityHashMap<Variable, Integer>();

			vn = nStamp.value();

			for (int i = 0; i <= lastNotGroundedVariable; i++) {

				if (debugAll) {
					System.out.println("Tarjan start, changed variabled "
							+ x[i]);
					System.out.println("Tarjan start, value mapping "
							+ valueMapVariable);
				}

				if (scc.get(x[i]) == null)
					visitTarjan(x[i], l, dfsnum, low);

				if (debugAll) {
					System.out.println("Tarjan end");
				}

			}

			if (debugAll) {
				System.out.println("Tarjan end state " + scc);
			}

			// Update stamps for new matching

			Variable next;
			for (Iterator<Variable> e = scc.keySet().iterator(); e.hasNext();) {
				next = (Variable) e.next();
				sccStamp.get(next).update(scc.get(next));
			}

			nStamp.update(vn + 1);

		}

		// Traverses the graph starting in free values and marks each variable
		// which is reachable from a free value

		// New approach
		// Use potentialFreeValues, create ordered list of values matched
		// each potential free value check against

		LinkedHashSet<Variable> variablesReachableFromFreeValues = new LinkedHashSet<Variable>(
				x.length, 0.50f);

		int stampValue = stampValues.value();

		int lastNotGroundedVariablePlusOne = lastNotGroundedVariable + 1;

		if (stampValue - lastNotGroundedVariablePlusOne > 0) {

			// if values available equal to number of variables not grounded
			// (plus one is due
			// to different representation) then no free values, so no need for
			// reachability analysis.

			HashSet<Integer> matchedValues = new HashSet<Integer>(x.length,
					0.50f);

			int noOfReachedVariablesLastTime = stampReachability.value();

			for (int i = 0; i <= lastNotGroundedVariable; i++)
				matchedValues.add(matching.get(x[i]).value());

			for (int i = 0; i < stampValue
					&& variablesReachableFromFreeValues.size() < noOfReachedVariablesLastTime
					&& variablesReachableFromFreeValues.size() != lastNotGroundedVariablePlusOne; i++)
				if (!matchedValues.contains(potentialFreeValues[i]))
					markReachableVariables(variablesReachableFromFreeValues,
							potentialFreeValues[i]);

			stampReachability.update(variablesReachableFromFreeValues.size());
		}

		if (debugAll) {
			System.out.println("All reached variables "
					+ variablesReachableFromFreeValues);

			System.out
					.println("Check for All NOT reached variables if there is an "
							+ " edge from matched variable to a different");
		}

		Variable variable = null;
		Integer matched;
		int variableComponentId;
		int lastPosition;
		Variable possibleDifferentComponentVariable;

		for (int j = 0; j <= lastNotGroundedVariable; j++) {

			variable = x[j];

			if (debugAll) {
				System.out.println("Variable " + variable + " is considered ");
			}

			if (!variablesReachableFromFreeValues.contains(variable)) {

				if (debugPruning) {
					System.out.println("Variable " + variable
							+ " is not reached by free values ");
				}

				variableComponentId = sccStamp.get(variable).value();

				matched = matching.get(variable).value();

				currentSimpleArrayList = valueMapVariable.get(matched);

				stamp = stamps.get(matched);

				lastPosition = stamp.value();

				if (debugAll)
					System.out
							.println("currentSimpleArrayList "
									+ currentSimpleArrayList + " stamp "
									+ lastPosition);

				// If permutation constraint
				// then above if is always true then this check can
				// reuse quite a lot of work required for other
				// pruning anyway
				// loop invariant is that variable is not singleton
				if (lastPosition == 0 && permutationConsistency) {

					if (debugPruning)
						System.out.println("Value " + matched
								+ " has only this variable possible "
								+ variable);

					// store.in(variable, matched, matched);
					variable.domain.in(store.level, variable, matched, matched);

					// The above pruning does not require execution of
					// consistency
					// function, neither update of any local structure of
					// alldistinct
					// constraint therefore it can be removed from
					// variableQueue.
					variableQueue.remove(variable);

				}

				for (int i = 0; i <= lastPosition; i++) {
					possibleDifferentComponentVariable = currentSimpleArrayList
							.get(i);
					if (variableComponentId != sccStamp.get(
							possibleDifferentComponentVariable).value()) {

						if (debugPruning) {

							System.out.println("\n\n\n\n\nVariable "
									+ possibleDifferentComponentVariable
									+ "can not take value " + matched
									+ "\n\n\n\n");
						}

						possibleDifferentComponentVariable.domain.inComplement(
								store.level,
								possibleDifferentComponentVariable, matched);

						narrowingEvent = true;

						// Required to keep the data structure consistent
						variableQueue.add(possibleDifferentComponentVariable);
						currentSimpleArrayList.set(i, currentSimpleArrayList
								.get(lastPosition));
						currentSimpleArrayList.set(lastPosition,
								possibleDifferentComponentVariable);

						lastPosition = lastPosition - 1;
						stamp.update(lastPosition);

					}
				}

			}
		}

		if (!narrowingEvent
				&& stampValues.value() - 1 == stampNotGroundedVariables.value()) {

			// Use Global Potential Free Values
			int sizePotentialFreeValues = stampValues.value();
			int currentlyUsedPotentialFreeValue = 0;

			Integer value;

			while (currentlyUsedPotentialFreeValue < sizePotentialFreeValues) {

				value = potentialFreeValues[currentlyUsedPotentialFreeValue];

				currentlyUsedPotentialFreeValue++;

				stamp = stamps.get(value);

				stampValue = stamp.value();

				if (stampValue == 0) {

					if (valueMapVariable.get(value).get(0).dom().getSize() > 1) {
						System.out
								.println("Transformation Alldistinct-Permutation and "
										+ "missing propagation ");

						valueMapVariable.get(value).get(0).domain.in(
								store.level,
								valueMapVariable.get(value).get(0), value,
								value);

						variableQueue.add(valueMapVariable.get(value).get(0));

						narrowingEvent = true;
					}

				}
			}
		}

		// moved from place below, so re-execution does not do unnecessary work
		backtrackOccured = false;

		if (narrowingEvent) {
			consistencyChecks--;
			fullConsistencyPassesWithNarrowingEvent++;
			consistency(store);				
		}
		

		Store.newPropagation = false;

		if (debugAll) {
			System.out.println("Consistency technique has finished execution ");
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
			return Constants.ANY;
	}

	// Right now accepts as input potential free values
	// Makes check if value is matched by variable and simply skip this case
	// It skips matched values at the begining of the path, but
	// it can not skip matched values after
	// potential freeValues, inside hopcroft algorithm, but outside it is free
	// Values

	private boolean hopcroftKarpMaximumMatching() {

		maximumMatchingNotRecomputed = false;

		boolean maximumMatchingFound = false;

		HashSet<Integer> nonFreeValues = new HashSet<Integer>();

		Integer matched;

		Variable variable = null;

		int lastNotGroundedVariable = stampNotGroundedVariables.value();

		for (int i = 0; i <= lastNotGroundedVariable; i++) {
			variable = x[i];

			// variable does not belong to freeVariables
			if (!freeVariables.contains(variable)) {

				matched = matching.get(variable).value();
				nonFreeValues.add(matched);

				if (backtrackOccured) {

					SimpleArrayList<Variable> currentSimpleArrayList = valueMapVariable
							.get(matched);

					// Correcting matching in ValueMapVariable for
					// notGroundedYetVariable.
					// This variable has not removed previously computed
					// matching
					// since last time this function was called

					int positionMatched = currentSimpleArrayList
							.indexOf(variable);
					if (positionMatched != 0) {

						currentSimpleArrayList.setElementAt(
								currentSimpleArrayList.get(0), positionMatched);

						currentSimpleArrayList.setElementAt(variable, 0);

					}
				}
			}

		}

		// Points at edge which was not yet used by Karp-Hopcroft algorithm
		HashMap<Integer, Integer> notYetUsedVariablePointer = new HashMap<Integer, Integer>();

		// Use Global Potential Free Values
		int sizePotentialFreeValues = stampValues.value();
		int currentlyUsedPotentialFreeValue = 0;

		Integer value;
		TimeStamp<Integer> stamp;
		int stampValue;

		while (currentlyUsedPotentialFreeValue < sizePotentialFreeValues) {

			value = potentialFreeValues[currentlyUsedPotentialFreeValue];

			currentlyUsedPotentialFreeValue++;

			stamp = stamps.get(value);

			stampValue = stamp.value();

			notYetUsedVariablePointer.put(value, stampValue);
		}

		while (!maximumMatchingFound) {

			ArrayList<LinkedList<Object>> allpaths = new ArrayList<LinkedList<Object>>();

			LinkedList<Object> path = new LinkedList<Object>();

			if (debugAll) {
				System.out.println("Non Free Values" + nonFreeValues);
			}

			HashSet<Integer> visitedValues = new HashSet<Integer>(
					valueMapVariable.size());
			HashSet<Variable> visitedVariables = new HashSet<Variable>(matching
					.size());

			// Very important since above it is also defined
			currentlyUsedPotentialFreeValue = 0;

			while (currentlyUsedPotentialFreeValue < sizePotentialFreeValues) {

				if (path.size() == 0) {
					// If last element from path is null - no path yet
					// then look for free value to start a path from
					while (currentlyUsedPotentialFreeValue < sizePotentialFreeValues) {
						Integer potentialTop = potentialFreeValues[currentlyUsedPotentialFreeValue];

						currentlyUsedPotentialFreeValue++;

						if (!nonFreeValues.contains(potentialTop)) {
							visitedValues.add(potentialTop);
							path.addLast(potentialTop);
							break;
						}
					}
				}

				if (debugAll)
					System.out.println("First element of the path " + path);

				if (path.size() == 0)
					// no possibility to start new path
					if (allpaths.size() == 0)
						// no path was found last execution
						// failed to find maximum matching
						return false;
					else
						// some paths were found re run algorithm
						break;

				// Get last element from path
				Integer top = (Integer) path.getLast();

				// Top contains last element of constructed path

				Variable first;

				while (true) { // Constructs the path
					// freeValue-...-freeVariable

					// Exit while loop if no addition to path can be done
					// no addition can be done if current pointer for
					// not yet used variable is larger than last possible
					// variable to be used.

					if (debugAll)
						System.out.println("Visited variables "
								+ visitedVariables);

					if (debugAll)
						System.out.println("Free variables " + freeVariables);

					if (debugAll)
						System.out.println("Values for last path element "
								+ valueMapVariable.get(top));

					// MAKE SURE you have increase level before worrying about
					// Null Pointer exception
					// in line below ;)).

					int notYetUsedVariable = notYetUsedVariablePointer.get(top);

					if (debugAll)
						System.out.println("notYetUsedVariable "
								+ notYetUsedVariable);

					if (notYetUsedVariable == -1)
						if (path.size() == 1)
							break;
						else {
							if (debugAll)
								System.out.println("Path to shorten " + path);
							path.removeLast();
							path.removeLast();
							if (debugAll)
								System.out.println("Shorten path" + path);
							top = (Integer) path.getLast();
							continue;
						}

					// Value has still some edges pointing at variables
					first = valueMapVariable.get(top).get(notYetUsedVariable);

					// Take any edge and mark it as used.
					notYetUsedVariablePointer.put(top, notYetUsedVariable - 1);

					if (!visitedVariables.contains(first)) {

						path.addLast(first);
						visitedVariables.add(first);

						if (debugAll)
							System.out.println("Current path " + path);

						// if first is free variable then path
						// freevalue-...-freevariable found
						if (freeVariables.contains(first))
							break;

						// variable is not free then matched value is pointed by
						// matching
						top = matching.get(first).value();
						path.addLast(top);
						visitedValues.add(top);
					}

					if (debugAll)
						System.out.println("Current path " + path);

				}

				// If path has even elements then it means that
				// freevalue-...-freevariable
				// path found
				if (path.size() % 2 == 0) {
					allpaths.add(path);
					path = new LinkedList<Object>();
				} else if (path.size() > 2) {
					// Value did not have any variables it could use to continue
					// path builing
					// Remove from path ....-variable-value (last variable and
					// value)
					path.removeLast();
					path.removeLast();
				} else {
					// Free Value yielded no path, try different free value
					path.removeLast();
				}

				// If number of paths is equal to number of free variables
				// this means that every free variables is visited and has its
				// path

				if (debugAll)
					System.out.println("Free variables " + freeVariables);

				if (debugAll)
					System.out.println("Allpaths " + allpaths);

				if (freeVariables.size() == allpaths.size()) {
					maximumMatchingFound = true;
					break;
				}

			}

			if (debugAll)
				System.out.println("Allpaths " + allpaths);

			if (allpaths.size() == 0)
				return false;

			// Use all paths to create better matching

			int allPathsSize = allpaths.size();

			for (int p = 0; p < allPathsSize; p++) {
				LinkedList<Object> freepath = allpaths.get(p);

				int freePathSize = freepath.size();

				for (int pos = 0; pos < freePathSize; pos = pos + 2) {
					Integer matchedValue = (Integer) freepath.get(pos);
					Variable matchedVariable = (Variable) freepath.get(pos + 1);

					if (!freeVariables.remove(matchedVariable))
						nonFreeValues.remove(matching.get(matchedVariable)
								.value());

					matching.get(matchedVariable).update(matchedValue);

					// Update valueMapVariable with new matched value

					SimpleArrayList<Variable> currentSimpleArrayList = valueMapVariable
							.get(matchedValue);
					int positionMatched = currentSimpleArrayList
							.indexOf(matchedVariable);
					if (positionMatched != 0) {

						currentSimpleArrayList.setElementAt(
								currentSimpleArrayList.get(0), positionMatched);
						currentSimpleArrayList.setElementAt(matchedVariable, 0);
					}

					nonFreeValues.add(matchedValue);

				}

			}

			if (!maximumMatchingFound) {

				// Use Global Potential Free Values
				sizePotentialFreeValues = stampValues.value();
				currentlyUsedPotentialFreeValue = 0;

				// Points at edge which was not yet used by Karp-Hopcroft
				// algorithm
				notYetUsedVariablePointer = new HashMap<Integer, Integer>(
						sizePotentialFreeValues);

				while (currentlyUsedPotentialFreeValue < sizePotentialFreeValues) {

					value = potentialFreeValues[currentlyUsedPotentialFreeValue];

					currentlyUsedPotentialFreeValue++;

					stamp = stamps.get(value);

					stampValue = stamp.value();

					notYetUsedVariablePointer.put(value, stampValue);
				}

			}

		}

		return true;
	}

	@Override
	public String id() {
		
		if (id != null)
			return id;
		else
			return  id_alldistinct + numberId;
		
	}

	@Override
	public void impose(Store store) {

		this.store = store;

		store.registerRemoveLevelListener(this);

		stampValues = new TimeStamp<Integer>(store, valueMapVariable.size());

		stampReachability = new TimeStamp<Integer>(store, x.length);

		nStamp = new TimeStamp<Integer>(store, 0);

		stampNotGroundedVariables = new TimeStamp<Integer>(store, x.length - 1);

		Integer zero = 0;

		for (Variable var : x) {
			var.putModelConstraint(this, getConsistencyPruningEvent(var));
			queueVariable(store.level, var);
			matching.put(var, new TimeStamp<Integer>(store, zero));
			sccStamp.put(var, new TimeStamp<Integer>(store, zero));
		}
		store.addChanged(this);
		store.countConstraint();

		Integer value = null;

		for (Iterator<Integer> e = valueMapVariable.keySet().iterator(); e
				.hasNext();) {
			value = e.next();
			stamps.put(value, new TimeStamp<Integer>(store, valueMapVariable.get(value).size() - 1));
		}

		// the initial maximum matching needs to be computed
		// search may return to this matching
		for (Variable var : x)
			freeVariables.add(var);

		LinkedHashSet<Variable> fdvs = new LinkedHashSet<Variable>(
				freeVariables);

		// If first invocation of hocroft matching algorithm fails just set
		// variable and quit
		if (!hopcroftKarpMaximumMatching()) {
			impositionFailure = true;
			return;
		}

		n = nStamp.value();

		ArrayList<Variable> l = new ArrayList<Variable>();
		HashMap<Variable, Integer> dfsnum = new HashMap<Variable, Integer>();
		HashMap<Variable, Integer> low = new HashMap<Variable, Integer>();

		while (!fdvs.isEmpty()) {

			Variable changedVariable = fdvs.iterator().next();

			fdvs.remove(changedVariable);

			revisitTarjan(changedVariable, l, dfsnum, low, fdvs);

		}

		nStamp.update(n + 1);

		if (debugAll) {
			System.out.println("Mapping Value->Variable" + valueMapVariable);
			System.out.println("Maximum Matching " + matching);
		}

		store.raiseLevelBeforeConsistency = true;

	}

	private void markReachableVariables(
			LinkedHashSet<Variable> variablesReachableFromFreeValues,
			Integer value) {

		if (debugAll) {
			System.out.println("Start mark reachable variables " + value);
		}

		SimpleArrayList<Variable> currentSimpleArrayList = valueMapVariable
				.get(value);

		TimeStamp<Integer> stamp = stamps.get(value);

		int lastPosition = stamp.value();

		Integer matched;

		// i counter has to be from zero since free paths can go from matched
		// edges
		for (int i = 0; i <= lastPosition; i++) {

			Variable reachableVariable = currentSimpleArrayList.get(i);

			if (variablesReachableFromFreeValues.contains(reachableVariable))
				continue;

			if (debugAll) {
				System.out.println("Variable " + reachableVariable
						+ " has been reached from value " + value);
			}

			matched = matching.get(reachableVariable).value();

			variablesReachableFromFreeValues.add(reachableVariable);

			markReachableVariables(variablesReachableFromFreeValues, matched);

		}

	}

	@Override
	public void queueVariable(int level, Variable V) {

		if (debugAll)
			System.out.println("Var " + V + V.recentDomainPruning());

		variableQueue.add(V);
	}

	@Override
	public void removeConstraint() {
		for (Variable var : x)
			var.removeConstraint(this);
	}

	private void revisitTarjan(Variable x, ArrayList<Variable> l,
			HashMap<Variable, Integer> dfsnum, HashMap<Variable, Integer> low,
			LinkedHashSet<Variable> fdvs) {

		Integer nInteger = n;

		dfsnum.put(x, nInteger);
		low.put(x, nInteger);
		n++;

		if (debugAll)
			System.out
					.println("Tarjan invocation : \nx " + x + "\nn " + n
							+ "\nl " + l + "\ndfsnum " + dfsnum + "\nlow "
							+ low + "\n");

		l.add(x);

		Integer matchedValue = matching.get(x).value();

		if (debugAll)
			System.out.println("Matched value " + matchedValue + " for " + x);

		SimpleArrayList<Variable> currentSimpleArrayList = valueMapVariable
				.get(matchedValue);

		if (debugAll)
			System.out.println("Mapped variables to Matched value "
					+ currentSimpleArrayList);

		TimeStamp<Integer> stamp = stamps.get(matchedValue);

		int lastPosition = stamp.value();

		if (debugAll)
			System.out.println("Last valid position for variables "
					+ lastPosition);

		int sccStampX = sccStamp.get(x).value();
		// first variable is matched value
		for (int i = 0; i <= lastPosition; i++) {

			Variable v = currentSimpleArrayList.get(i);

			if (sccStampX == sccStamp.get(v).value())
				if (dfsnum.get(v) == null) {

					revisitTarjan(v, l, dfsnum, low, fdvs);

					int lowv = low.get(v);

					if (low.get(x) > lowv)
						low.put(x, lowv);
				} else {

					if (debugAll)
						System.out.println("Part 2 : low " + x + "="
								+ low.get(x) + " dfsnum " + v + "="
								+ dfsnum.get(v));

					int dfsnumv = dfsnum.get(v);

					// If v was earlier visited and v belongs to stack then
					// update low number of x.
					if (dfsnumv < dfsnum.get(x))
						if (l.contains(v))
							if (low.get(x) > dfsnumv)
								low.put(x, dfsnumv);

				}
		}

		if (debugAll) {
			System.out.println("Invocation " + x + " Low values for it " + low);
			System.out.println("Invocation " + x + " Dfsnum values for it "
					+ dfsnum);
		}

		int lowx = low.get(x);

		if (lowx == dfsnum.get(x)) {

			if (debugAll)
				System.out.println("Component found  ");

			Variable component;

			while (true) {
				component = l.remove(l.size() - 1);

				if (debugAll)
					System.out.println("Component part  " + component + "id "
							+ lowx);

				sccStamp.get(component).update(lowx);
				fdvs.remove(component);

				if (component == x) {
					break;
				}
			}
		}

	}

	@Override
	public boolean satisfied() {

		// Possible to use this check, fast but not accurate
		// if (stampNotGroundedVariables.value() != -1)
		// return false;

		boolean sat = true;
		int i = 0;

		while (sat && i < x.length) {
			Domain vDom = x[i].dom();
			int vMin = vDom.min(), vMax = vDom.max();
			int j = 0;
			while (sat && j < x.length) {
				if (i != j) {
					Domain ljDom = x[j].dom();
					sat = (vMin > ljDom.max() || vMax < ljDom.min());
				}
				j++;
			}
			i++;
		}
		return sat;
	}

	@Override
	public String toString() {

		StringBuffer buf = new StringBuffer( id() );

		buf.append(" : alldistinct([");

		for (int i = 0; i < x.length; i++) {
			buf.append(x[i]);
			if (i < x.length - 1)
				buf.append(", ");
		}

		buf.append("]");
		return buf.toString();
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("flavour", "alldistinct");
		constraint.setAttribute("reference", "global:allDifferent");
		constraint.setAttribute("arity", String.valueOf(x.length));

		StringBuffer scope = new StringBuffer();
		for (int i = 0; i < x.length - 1; i++)
			scope.append(  x[i].id() + " " );
		
		scope.append(x[x.length - 1]);
		constraint.setAttribute("scope", scope.toString());

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	private void visitTarjan(Variable x, ArrayList<Variable> l,
			HashMap<Variable, Integer> dfsnum, HashMap<Variable, Integer> low) {

		Integer vnInteger = vn;
		dfsnum.put(x, vnInteger);
		low.put(x, vnInteger);
		vn++;

		if (debugAll)
			System.out
					.println("Tarjan invocation : \nx " + x + "\nn " + vn
							+ "\nl " + l + "\ndfsnum " + dfsnum + "\nlow "
							+ low + "\n");

		l.add(x);

		Integer matchedValue = matching.get(x).value();

		if (debugAll)
			System.out.println("Matched value " + matchedValue + " for " + x);

		SimpleArrayList<Variable> currentSimpleArrayList = valueMapVariable
				.get(matchedValue);

		if (debugAll)
			System.out.println("Mapped variables to Matched value "
					+ currentSimpleArrayList);

		TimeStamp<Integer> stamp = stamps.get(matchedValue);

		int lastPosition = stamp.value();

		if (debugAll)
			System.out.println("Last valid position for variables "
					+ lastPosition);

		Variable v;

		// first variable is matched value
		for (int i = 1; i <= lastPosition; i++) {

			v = currentSimpleArrayList.get(i);

			if (dfsnum.get(v) == null) {

				visitTarjan(v, l, dfsnum, low);

				int lowv = low.get(v);

				if (low.get(x) > lowv) {
					low.put(x, lowv);
				}

			} else {

				if (debugAll)
					System.out.println("Part 2 : low " + x + "=" + low.get(x)
							+ " dfsnum " + v + "=" + dfsnum.get(v));

				int dfsnumv = dfsnum.get(v);

				// If v was earlier visited and v belongs to stack then
				// update low number of x.
				if (dfsnumv < dfsnum.get(x))
					if (l.contains(v))
						if (low.get(x) > dfsnumv) {
							low.put(x, dfsnumv);
						}
			}
		}

		if (debugAll) {
			System.out.println("Invocation " + x + " Low values for it " + low);
			System.out.println("Invocation " + x + " Dfsnum values for it "
					+ dfsnum);
		}

		int lowx = low.get(x);

		if (lowx == dfsnum.get(x)) {

			if (debugAll)
				System.out.println("Component found  ");

			while (true) {
				Variable component = l.remove(l.size() - 1);

				if (debugAll)
					System.out.println("Component part  " + component);

				scc.put(component, lowx);

				if (component == x) {

					break;
				}
			}
		}

	}

	/**
	 * It converts and XML description of the constraint into actual constraint. 
	 * @param constraint XCSP based description
	 * @param store constraint store in which context the constraint is being created.
	 * @return alldistinct constraint created from XML element.
	 */
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

		return new Alldistinct(vars);

	}

	@Override
	public Constraint getGuideConstraint() {
		return new XeqC(guideVariable, guideValue);	
	}

	@Override
	public int getGuideValue() {
		return guideValue;
	}

	Variable guideVariable = null;
	int guideValue;
	boolean greedy = true;
	
	@Override
	public Variable getGuideVariable() {
		
		int minCurrentPruning = 1;
		int maxCurrentPruning = 100000;

		// Look at all variables with domain size two, and find the one with
		// best pruning

		guideVariable = null;
		
//		System.out.println("1. var " + guideVariable + " value " + guideValue);
		
		int lastNotGroundedVariable = stampNotGroundedVariables.value();
 
		for (int i = 0; i <= lastNotGroundedVariable; i++) {
			if (x[i].getSize() == 2) {

			    Integer firstValue = x[i].min();
			    Integer secondValue = x[i].max();

				// Evaluate recursively.
				int pruningFirstValue = estimatePruning(x[i], firstValue);

				if (pruningFirstValue >= minCurrentPruning) {

					int pruningSecondValue = estimatePruning(x[i], secondValue);

					if (pruningFirstValue < pruningSecondValue) {

						if (pruningFirstValue > minCurrentPruning) {

							// Lack of equal sign means greedy in propagation
							if (stamps.get(firstValue).value() < stamps.get(
									secondValue).value()
									|| (stamps.get(firstValue).value() == stamps
											.get(secondValue).value() && !greedy)) {
								// Value with lower number of variables has a
								// higher change to have this value
								guideVariable = x[i];
								guideValue = firstValue;

							} else {
								guideVariable = x[i];
								guideValue = secondValue;
							}

							minCurrentPruning = pruningFirstValue;
							maxCurrentPruning = pruningSecondValue;
						} else if (pruningFirstValue == minCurrentPruning
								&& pruningSecondValue > maxCurrentPruning) {

							// Lack of equal sign means greedy in propagation
							if (stamps.get(firstValue).value() < stamps.get(
									secondValue).value()
									|| (stamps.get(firstValue).value() == stamps
											.get(secondValue).value() && !greedy)) {
								// Value with lower number of variables has a
								// higher change to have this value

								guideVariable = x[i];
								guideValue = firstValue;
								
							} else {

								guideVariable = x[i];
								guideValue = secondValue;

							}
							maxCurrentPruning = pruningSecondValue;
						}
					} else {
						// FirstValuePruning > SecondValuePruning
						if (pruningSecondValue > minCurrentPruning) {
							
							// Lack of equal sign means no greedy in propagation
							// Equal sign means greedy in propagation
							if (stamps.get(firstValue).value() <= stamps.get(
									secondValue).value()
									|| (stamps.get(firstValue).value() == stamps
											.get(secondValue).value() && greedy)) {
								// Value with lower number of variables has a
								// higher change to have this value
								guideVariable = x[i];
								guideValue = firstValue;
							} else {

							    guideVariable = x[i];
							    guideValue = secondValue;

							}
							minCurrentPruning = pruningSecondValue;
							maxCurrentPruning = pruningFirstValue;
						} else if (pruningSecondValue == minCurrentPruning
								&& pruningFirstValue > maxCurrentPruning) {
							
							// Equal sign means greedy in propagation
							if (stamps.get(firstValue).value() <= stamps.get(
									secondValue).value()
									|| (stamps.get(firstValue).value() == stamps
											.get(secondValue).value() && greedy)) {
								// Value with lower number of variables has a
								// higher change to have this value
							    guideVariable = x[i];
							    guideValue = firstValue;
							} else {
							    guideVariable = x[i];
							    guideValue = secondValue;

							}
							maxCurrentPruning = pruningFirstValue;
						}

					}
				}
				firstValue = null;
				secondValue = null;
			}
		}

		
//		System.out.println("2. var " + guideVariable + " value " + guideValue);
		
		// Permutation only at this moment

		if (stampValues.value() - stampNotGroundedVariables.value() == 1) {

			// Use Global Potential Free Values
			int sizePotentialFreeValues = stampValues.value();
			int currentlyUsedPotentialFreeValue = 0;

			Integer value;
			TimeStamp<Integer> stamp;
			int stampValue;

			SimpleArrayList<Variable> currentSimpleArrayList = null;

			while (currentlyUsedPotentialFreeValue < sizePotentialFreeValues) {

				value = potentialFreeValues[currentlyUsedPotentialFreeValue];

				currentlyUsedPotentialFreeValue++;

				stamp = stamps.get(value);

				stampValue = stamp.value();

				// Value with two variables
				if (stampValue == 1) {

					currentSimpleArrayList = valueMapVariable.get(value);

					int pruningFirstVariable = estimatePruning(
							currentSimpleArrayList.get(0), value);

					if (pruningFirstVariable < minCurrentPruning)
						continue;

					int pruningSecondVariable = estimatePruning(
							currentSimpleArrayList.get(1), value);

					if (pruningSecondVariable < minCurrentPruning)
						continue;

					if (pruningFirstVariable < pruningSecondVariable) {

						if (pruningFirstVariable > minCurrentPruning) {

							// Equals sign means no greedy in propagation
							// Lack of equal sign means greedy in propagation
							if (currentSimpleArrayList.get(0).getSize() < currentSimpleArrayList
									.get(1).getSize()
									|| (currentSimpleArrayList.get(0).getSize() == currentSimpleArrayList
									    .get(1).getSize() && !greedy)) {

							    guideVariable = currentSimpleArrayList.get(0);
							    guideValue = value;

							} else {

							    guideVariable = currentSimpleArrayList.get(1);
							    guideValue = value;


							}
							minCurrentPruning = pruningFirstVariable;
							maxCurrentPruning = pruningSecondVariable;
						} else if (pruningFirstVariable == minCurrentPruning
								&& pruningSecondVariable > maxCurrentPruning) {
							// currentPruning.set(1, new
							// Integer(pruningSecondVariable));

							// Equals sign means no greedy in propagation in
							// case of tie break
							// Lack of equal sign means greedy in propagation
							if (currentSimpleArrayList.get(0).getSize() < currentSimpleArrayList
									.get(1).getSize()
									|| (currentSimpleArrayList.get(0).getSize() == currentSimpleArrayList
											.get(1).getSize() && !greedy)) {

							    guideVariable = currentSimpleArrayList.get(0);
							    guideValue = value;

							} else {

							    guideVariable = currentSimpleArrayList.get(1);
							    guideValue = value;

							}

							maxCurrentPruning = pruningSecondVariable;
						}

					} else {
						// PruningFirstVariable > PruningSecondVariable
						if (pruningSecondVariable > minCurrentPruning) {
							
							// Equal sign means greedy in case of tie break
							if (currentSimpleArrayList.get(0).getSize() <= currentSimpleArrayList
									.get(1).getSize()
									|| (currentSimpleArrayList.get(0).getSize() == currentSimpleArrayList
											.get(1).getSize() && greedy)) {

							    guideVariable = currentSimpleArrayList.get(0);
							    guideValue = value;
							} else {

							    guideVariable = currentSimpleArrayList.get(1);
							    guideValue = value;
							}

							minCurrentPruning = pruningSecondVariable;
							maxCurrentPruning = pruningFirstVariable;
						} else if (pruningSecondVariable == minCurrentPruning
								&& pruningFirstVariable > maxCurrentPruning) {
							
							// Equal sign means greedy in case of tie break
							if (currentSimpleArrayList.get(0).getSize() <= currentSimpleArrayList
									.get(1).getSize()
									|| (currentSimpleArrayList.get(0).getSize() == currentSimpleArrayList
											.get(1).getSize() && !greedy)) {

							    guideVariable = currentSimpleArrayList.get(0);
							    guideValue = value;
							
							} else {

							    guideVariable = currentSimpleArrayList.get(1);
							    guideValue = value;
							}
							maxCurrentPruning = pruningFirstVariable;
						}

					}

				}

			}

			value = null;
			stamp = null;
			currentSimpleArrayList = null;

		}
		
		// TODO, fix it, si does not return singleton variables.
		return guideVariable;
	}

    int estimatePruning(Variable x, Integer v) {

		ArrayList<Variable> exploredX = new ArrayList<Variable>();
		ArrayList<Integer> exploredV = new ArrayList<Integer>();

		int pruning = estimatePruningRecursive(x, v, exploredX, exploredV);

		SimpleArrayList<Variable> currentSimpleArrayList = null;
		Integer value = null;

		for (int i = 0; i < exploredV.size(); i++) {

			value = exploredV.get(i);
			currentSimpleArrayList = valueMapVariable.get(value);

			TimeStamp<Integer> stamp = stamps.get(value);

			int lastPosition = stamp.value();

			for (int j = 0; j <= lastPosition; j++)
				// Edge between j and value was not counted yet
				if (!exploredX.contains(currentSimpleArrayList.get(j)))
					pruning++;

			stamp = null;
		}

		currentSimpleArrayList = null;
		value = null;
		exploredX = null;
		exploredV = null;

		return pruning;
	}	
	
	int estimatePruningRecursive(Variable xVar, Integer v,
			ArrayList<Variable> exploredX, ArrayList<Integer> exploredV) {

		if (exploredX.contains(xVar))
			return 0;

		exploredX.add(xVar);
		exploredV.add(v);

		int pruning = 0;

		Domain xDom = xVar.dom();
		pruning = xDom.getSize() - 1;

		TimeStamp<Integer> stamp = null;
		SimpleArrayList<Variable> currentSimpleArrayList = null;
		ValueEnumeration enumer = xDom.valueEnumeration();
		
		// Permutation only
		if (stampValues.value() - stampNotGroundedVariables.value() == 1)
			for (int i = enumer.nextElement(); enumer.hasMoreElements(); i = enumer
					.nextElement()) {
				if (!exploredV.contains(i)) {
					Integer iInteger = i;

					stamp = stamps.get(iInteger);

					int lastPosition = stamp.value();

					// lastPosition == 0 means one variable, so check if there
					// is atmost one variable for value
					if (lastPosition < exploredX.size() + 1) {

						currentSimpleArrayList = valueMapVariable.get(iInteger);

						Variable singleVar = null;
						boolean single = true;

						for (int m = 0; m <= lastPosition; m++)
							if (!exploredX.contains(currentSimpleArrayList
									.get(m)))
								if (singleVar != null)
									singleVar = currentSimpleArrayList.get(m);
								else
									single = false;

						if (single && singleVar == null) {
							System.out.println(this);
							System.out.println("StampValues - 1 "
									+ (stampValues.value() - 1));
							System.out.println("Not grounded Var "
									+ stampNotGroundedVariables.value());

							int lastNotGroundedVariable = stampNotGroundedVariables
									.value();
							Variable variable = null;

							for (int l = 0; l <= lastNotGroundedVariable; l++) {
								variable = x[l];
								System.out.println("Stamp for " + variable
										+ " " + sccStamp.get(variable).value());
								System.out.println("Matching "
										+ matching.get(variable).value());

							}
						}

						if (single && singleVar != null)
							pruning += estimatePruningRecursive(singleVar,
									iInteger, exploredX, exploredV);

						singleVar = null;
					}
					iInteger = null;

				}
			}

		enumer = null;
		stamp = stamps.get(v);
		currentSimpleArrayList = valueMapVariable.get(v);

		int lastPosition = stamp.value();

		for (int i = 0; i <= lastPosition; i++) {
			Variable variable = currentSimpleArrayList.get(i);

			// checks if there is at most one value for variable
			if (!exploredX.contains(variable)
					&& variable.dom().getSize() < exploredV.size() + 2) {

				boolean single = true;
				Integer singleVal = null;
				
				for (ValueEnumeration enumerX = variable.dom().valueEnumeration(); enumerX.hasMoreElements();) {
						Integer next = enumerX.nextElement();

						if (!exploredV.contains(next))
							if (singleVal == null)
								singleVal = next;
							else
								single = false;
					}

				if (single)
					pruning += estimatePruningRecursive(variable, singleVal,
							exploredX, exploredV);
			}

		}

		stamp = null;
		return pruning;
	}	
	
	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			for (Variable v : x) v.weight++;
		}
	}
	
}
