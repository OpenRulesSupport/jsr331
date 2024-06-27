/**
 *  Sequence.java 
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

import JaCoP.constraints.regular.Regular;
import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.util.fsm.FSM;
import JaCoP.util.fsm.FSMState;
import JaCoP.util.fsm.FSMTransition;

/**
 *
 * It constructs a Sequence constraint. 
 * 
 * @author Radoslaw Szymanek and Polina Makeeva
 * @version 2.4
 */

public class Sequence extends DecomposedConstraint {

	IntervalDomain set;
	int min;
	int max;
	int q;
	Variable[] x;
	ArrayList<Constraint> constraints;
	
	/**
	 * It creates a Sequence constraint. 
	 * 
	 * @param x variables which assignment is constrained by Sequence constraint. 
	 * @param set set of values which occurrence is counted within each sequence.
	 * @param q the length of the sequence
	 * @param min the minimal occurrences of values from set within a sequence.
	 * @param max the maximal occurrences of values from set within a sequence.
	 */
	public Sequence(Variable[] x, IntervalDomain set, int q, int min, int max) {
	
		this.min = min;
		this.max = max;
		this.x = x;
		this.set = set;
		this.q = q;
	}	

	@Override
	public void imposeDecomposition(Store store) {
			
		if (constraints == null)
			decompose(store);
		
		for (Constraint c : constraints)
			store.impose(c, queueIndex);
		
	}

	@Override
	public ArrayList<Constraint> decompose(Store store) {

		if (constraints != null)
			return constraints;
		
		Domain setComplement = new IntervalDomain();
		for (Variable var : x)
			setComplement.addDom(var.domain);
		setComplement = setComplement.subtract(set);
		
		FSM fsm  = new FSM();

		fsm.initState =  new FSMState();
		fsm.states.add(fsm.initState);
		
		HashMap<FSMState, Integer> mappingQuantity = new HashMap<FSMState, Integer>();
		HashMap<String, FSMState> mappingString = new HashMap<String, FSMState>();
		
		mappingQuantity.put(fsm.initState, 0);
		mappingString.put("", fsm.initState);
		
		for (int i = 0; i < q; i++) {
			HashMap<String, FSMState> mappingStringNext = new HashMap<String, FSMState>();
			
			for (String stateString : mappingString.keySet()) {
				
				FSMState state = mappingString.get(stateString);
				
				if (mappingQuantity.get(state) < max) {
					// transition 1 (within a set) is allowed
					FSMState nextState = new FSMState();
					state.addTransition(new FSMTransition(set, nextState));
					mappingStringNext.put(stateString + "1", nextState);
					mappingQuantity.put(nextState, mappingQuantity.get(state) + 1);
				}
				
				if (mappingQuantity.get(state) + (q-i) > min) {
					// transition 0 (outside set) is allowed
					FSMState nextState = new FSMState();
					state.addTransition(new FSMTransition(setComplement, nextState));
					mappingStringNext.put(stateString + "0", nextState);
					mappingQuantity.put(nextState, mappingQuantity.get(state) );
				}
			}
			
			fsm.states.addAll( mappingString.values() );
			mappingString = mappingStringNext;
			
		}
		
		fsm.states.addAll( mappingString.values() );
		fsm.finalStates.addAll( mappingString.values() );
		
		for (String description : mappingString.keySet() ) {
			
			String one = description.substring(1) + "1";
			
			FSMState predecessor = mappingString.get(description);
			FSMState successor = mappingString.get(one);
			if (successor != null)
				predecessor.addTransition(new FSMTransition(set, successor));
			
			String zero = description.substring(1) + "0";
			successor = mappingString.get(zero);
			if (successor != null)
				predecessor.addTransition(new FSMTransition(setComplement, successor));
		}
				   
		fsm.resize();

		constraints = new ArrayList<Constraint>();
		constraints.add(new Regular(fsm, x));
		
		return constraints;
	}


}
