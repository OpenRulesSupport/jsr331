/**
 *  FSM.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2008 Polina Maakeva and Radoslaw Szymanek
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

package JaCoP.util.fsm;

import java.util.*;

import org.jdom.Element;

import JaCoP.core.Domain;
import JaCoP.core.Interval;
import JaCoP.core.IntervalDomain;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;
import JaCoP.util.MDD;

/**
 * Deterministic Finite Acyclic graph.
 * 
 * @author Polina Makeeva and Radoslaw Szymanek
 * @version 2.4
 */

public class FSM {

	/**
	 * It specifies number of states created in DFA class.
	 */
	public static int stateId = 0;
		
	/**
	 * It specifies the intial state of DFA.
	 */
	public FSMState initState;
		
	/**
	 * It specifies final states of DFA.
	 */
	public HashSet<FSMState> finalStates;
		
	/**
	 * It specifies all states including the initial one and final ones.
	 */
	public HashSet<FSMState> states;
	
	/**
	 * It creates a Finite State Machine used by Regular constraint constructor.
	 */
	public FSM() {
		
		finalStates = new HashSet<FSMState>();	
		states = new HashSet<FSMState>();
	
	}
	
	
	/**
	 * It computes a union of two Finite State Machines.
	 * @param other the other FSM which is used in the union computation.
	 * @return the resulting FSM.
	 */
	public FSM union(FSM other) {
		
		FSM result = new FSM();
		
		result.initState = new FSMState();
		
		result.states.add(result.initState);
		
		for (FSMTransition t : initState.transitions) {
			
			FSMState addState = t.successor.deepClone(result.states);
		
			result.initState.addTransition(new FSMTransition(t.domain, addState));
		
		}
		
		for (FSMState f : finalStates)
			result.finalStates.add(f.deepClone(result.states));
		
		
		for (FSMTransition t :other.initState.transitions) {
			FSMState addState = t.successor.deepClone(result.states);
			result.initState.addTransition(new FSMTransition(t.domain,addState));
		}
		
		for (FSMState f : other.finalStates)
			result.finalStates.add(f.deepClone(result.states));
		
		return result;
	}

	
	/**
	 * It does concatenation of two FSM. 
	 * @param other the FSM with which the concatenation takes place.
	 * @return the resulting FSM.
	 */
	public FSM concatenation(FSM other) {
		
		FSM result = new FSM();
		
		boolean otherIsStar = other.finalStates.size() == 1 && 
							  other.finalStates.contains(other.initState);
		
		result.initState = initState.deepClone(result.states);
		
		for (FSMState f : finalStates)	{
		
			FSMState ff = f.deepClone(result.states); 
			
			for (FSMTransition t : other.initState.transitions) {

				FSMState addState = t.successor.deepClone(result.states);
				ff.addTransition(new FSMTransition(t.domain,addState));
				
				if (otherIsStar) {
					for (FSMState s : result.states) {
						for (FSMTransition ts : s.transitions) {
							if (ts.successor.id == other.initState.id) {
							   ts.successor = ff;	
							}
						}
					}	
					result.states.remove(other.initState);
				}
			}
		}
		
		if (!otherIsStar)	
			for (FSMState f : other.finalStates)
				result.finalStates.add(f.deepClone(result.states));
		else
			for (FSMState f : finalStates)
				result.finalStates.add(f.deepClone(result.states));
			
		
		return result;
	}
	

	
	/**
	 * It performs star operation on this FSM.
	 * @return the resulting FSM.
	 */
	public FSM star() {
		
		FSM result = new FSM();
		
		result.initState = new FSMState(initState);
		
		result.states.add(result.initState);
		
		ArrayList<FSMState> set = new ArrayList<FSMState>();
		
		set.add(result.initState);
		
		int length = 1;
		FSMState s = null;
		
		for (int i = 0; i < length; i++) {
			s = set.get(i);
			FSMState orgS = getState(s.id);
			for (FSMTransition t : orgS.transitions){
				if (!finalStates.contains(t.successor))	{
					FSMState suc = result.getState(t.successor.id);
					if (suc == null) {
						suc = new FSMState(t.successor);	
						result.states.add(suc);
						set.add(suc);
						length = length + 1;
					}
					s.addTransition(new FSMTransition(t.domain, suc));
				}
				else
					s.addTransition(new FSMTransition(t.domain, result.initState));
				
			}
		}	
		result.finalStates.add(result.initState);
		
		return result;
	}
	
	
	
	/**
	 * It gets state of a given id.
	 * @param id the id of the searched state.
	 * @return the state of FSM with a given id.
	 */
	public FSMState getState(int id) {
		
		for (FSMState s : this.states)
			if (s.id == id) return s;
		
		return null;
	}
	
	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer("Init state : ");
		
		result.append( initState.id ).append("\n");
		
		for (FSMState s : states) {
			
			result.append( s.id ).append("\n");
			
			for (FSMTransition t : s.transitions)	
				result.append( "-" ).append( t.domain ).append( "> " ).append(t.successor.id ).append("\n");	
			
		}
		
		result.append( "\n" );
		result.append( "Final states :" );
		
		for (FSMState s : finalStates)
			result.append(s.id).append(", ");

		result.append("\n");

		return result.toString();
			
	}
	
	/**
	 * It resizes the Finite State Machine. All states get a new
	 * id between 0..n-1, where n is the number of states.
	 */
	public void resize() {
		
		HashSet<FSMState> finalStates = new HashSet<FSMState>();
		HashSet<FSMState> states = new HashSet<FSMState>();

		int id = 0;

		for (FSMState s : this.states) {
			s.id = id++;
		}

		finalStates.addAll(this.finalStates);
		states.addAll(this.states);
		
		this.finalStates = finalStates;
		this.states = states;
	}
	
	
	/**
	 * It creates an array of tuples representing this Regular context.
 	 * It generates only the tuples which are allowed in the current context of the store.
	 * 
	 * @param vars variables in which context a list of tuples is created.
	 * @return an array of tuples.
	 */
	public int[][] transformIntoTuples(Variable[] vars) {
		
		int levels = vars.length;
		int stateNumber = this.states.size();
	
		//The array that keep all possible states with arcs and their domains
		//If the domain is empty then the arc doesn't exist
		IntervalDomain[][][] outarc = new IntervalDomain[levels+1][stateNumber][stateNumber];
		
		//Reachable region of the graph
		HashSet<FSMState> reachable = new HashSet<FSMState>();
		//Temporal variable for reachable region  
		HashSet<FSMState> tmp = new HashSet<FSMState>();
	
		//The id's of the states are renamed to make the future graph in latex look pretty
		int level = 0;

		resize();
		FSMState array[] = new FSMState[stateNumber];
		for (FSMState s : this.states)
			array[s.id] = s;
		
		//----- compute the reachable region of the graph -----
		
		//Start with initial state
		reachable.add(this.initState);
		
		while (level < levels)
		{	
			//prepare tmp set of reachable states in the next level
			tmp.clear();
			//For each state reached until now
			for (FSMState s : reachable)
				//watch it's edges
				for (FSMTransition t : s.transitions) {
					//prepare the set of values of this edge
					Domain dom = t.domain.intersect(vars[level].dom());
					outarc[level][s.id][t.successor.id] = (IntervalDomain)dom;
					
					/* If the edge is not empty them add the state to tmp set
					 * check that such states wasn't previously added.
					 * 
					 * If the level is the last, then check whether the state 
					 * belongs to the set of accepted states 
					 */
					
					if (dom.getSize() > 0)
						if (level < levels - 1)
							tmp.add(t.successor);
						else if ( finalStates.contains(t.successor) )
								tmp.add(t.successor);

//					if (dom.getSize() > 0 && !tmp.contains(t.succesor))
//						if (level < levels -1) tmp.push(t.succesor); 
//						else if (this.finalStates.contains(t.succesor)) tmp.push(t.succesor);
				}
			//copy the tmp set of states into reachable region
			reachable.clear();
			reachable.addAll(tmp);
			//got to next level
			level++;
		}
		
		
		/* ----- delete the paths of the graph that doesn't reach the accepted state -----
		 * 
		 * 
		 *  by calculating the reachable region of the graph starting from the accepted 
		 *  states and following the edges in the opposite direction.
		 */
			
		while(level > 0) {
			tmp.clear();

			for (int i = 0; i < stateNumber; i++)
				for (int j = 0; j < stateNumber; j++)
				if (outarc[level-1][j][i] != null && outarc[level-1][j][i].getSize() > 0)
					if (!reachable.contains(array[i])) 
						outarc[level-1][j][i].clear();
					else
						tmp.add(array[j]);
			
			reachable.clear();
			reachable.addAll(tmp);
			level--;
		}
		
		IntervalDomain dom;
	    int[] tuple = new int[levels];
	    ArrayList<int[] > result = new ArrayList<int[]>();
	    
		for (int i = 0; i < stateNumber; i++)
			for (int j = 0; j < stateNumber; j++)
				if (outarc[0][i][j] != null && outarc[0][i][j].getSize() > 0) {
					dom = outarc[0][i][j];
					for (int h=0; h < dom.size; h++)	{
						Interval inv = (dom).intervals[h];
						//for each interval of val
						if (inv != null)
							//For each value of the interval
							for (int v = inv.min(); v <= inv.max(); v++) {
								tuple[0] = v;
								recursiveCall(j, 1, stateNumber, outarc, tuple, result);	 
							}
					}			
				}
					
		return result.toArray(new int[result.size()][]);
	
	}

	// Recursive function used to create a list of tuples.
	private void recursiveCall(int prevSuc, 
							   int level, 
							   int stateNumber, 
							   IntervalDomain[][][] outarc, 
							   int[] tuple, 
							   ArrayList<int[] > tuples) {
		
		if (level == tuple.length) {
			tuples.add( tuple.clone() );
			return;
		}
		
		IntervalDomain dom;
		
		for (int i = 0; i < stateNumber; i++) 
			if (outarc[level][prevSuc][i] != null && outarc[level][prevSuc][i].getSize() > 0) {
				dom = outarc[level][prevSuc][i];
				
				for (int h = 0; h < dom.size; h++)	{
				
					Interval inv = (dom).intervals[h];
					
					if (inv != null)
						//For each value of the interval
						for (int v = inv.min(); v <= inv.max(); v++) {
							tuple[level] = v;
							recursiveCall(i, level+1, stateNumber, outarc, tuple, tuples);	 
						}
				}
				
			}
				
		
	}
	
	
	/**
	 * It generates one by one tuples allowed by a Regular constraint, which are added
	 * to the MDD being built. After all tuples are added MDD is being reduced. The standard
	 * MDD creating procedure employed in paper presenting MDD based extensional constraint.
	 * It generates only the tuples which are allowed in the current context of the store.
	 * 
	 * @param vars variables in which context MDD is being created from Regular constraint. 
	 * @return MDD representing the same constraint as Regular.
	 */
	public MDD transformIntoMDD(Variable[] vars) {
		
		MDD result = new MDD(vars);
		
		int levels = vars.length;
		int stateNumber = this.states.size();
	
		//The array that keep all possible states with arcs and their domains
		//If the domain is empty then the arc doesn't exist
		IntervalDomain[][][] outarc = new IntervalDomain[levels + 1][stateNumber][stateNumber];
		
		//Reachable region of the graph
		HashSet<FSMState> reachable = new HashSet<FSMState>();
		//Temporal variable for reachable region  
		HashSet<FSMState> tmp = new HashSet<FSMState>();
		
		//The id's of the states are renamed to make the future graph in latex look pretty
		int level = 0;
		
		resize();
		FSMState array[] = new FSMState[stateNumber];
		for (FSMState s:this.states) {
			array[s.id] = s;
		}
		
		//----- compute the reachable region of the graph -----
		
		//Start with initial state
		reachable.add(this.initState);
		
		while (level < levels)
		{	
			//prepare tmp set of reachable states in the next level
			tmp.clear();
			//For each state reached until now
			for (FSMState s : reachable)
				//watch it's edges
				for (FSMTransition t : s.transitions)
				{
					//prepare the set of values of this edge
					Domain dom = t.domain.intersect(vars[level].dom());
					outarc[level][s.id][t.successor.id] = (IntervalDomain)dom;
					
					/* If the edge is not empty them add the state to tmp set
					 * check that such states wasn't previously added.
					 * 
					 * If the level is the last, then check whether the state 
					 * belongs to the set of accepted states 
					 */
					
					if (dom.getSize() > 0)
						if (level < levels - 1)
							tmp.add(t.successor);
						else if ( finalStates.contains(t.successor) )
								tmp.add(t.successor);
							
					//if (dom.getSize() > 0 && !tmp.contains(t.succesor))
					//	if (level < levels -1) tmp.push(t.succesor); 
					//	else if (this.finalStates.contains(t.succesor)) tmp.push(t.succesor);
				}
			
			//copy the tmp set of states into reachable region
			reachable.clear();
			reachable.addAll(tmp);
			//got to next level
			level++;
		}
		
		
		/* ----- delete the paths of the graph that doesn't reach the accepted state -----
		 * 
		 * 
		 *  by calculating the reachable region of the graph starting from the accepted 
		 *  states and following the edges in the opposite direction.
		 */
			
		while(level > 0) {
			
			tmp.clear();

			for (int i = 0; i < stateNumber; i++)
				for (int j = 0; j < stateNumber; j++)
				if (outarc[level-1][j][i] != null && outarc[level-1][j][i].getSize() > 0)
					if (!reachable.contains(array[i])) 
						outarc[level-1][j][i].clear();
					else
						tmp.add(array[j]);						
			
			reachable.clear();
			reachable.addAll(tmp);
			level--;
			
		}
		
		IntervalDomain dom;
	    int[] tuple = new int[levels];

	    // Part exploring all tuples and adding one by one to MDD.
		for (int i = 0; i < stateNumber; i++)
			for (int j = 0; j < stateNumber; j++)
				// for level 0 (first variable in the tuple)
				if (outarc[0][i][j] != null && outarc[0][i][j].getSize() > 0) {
					dom = outarc[0][i][j];
					for (int h = 0; h < dom.size; h++)	{
						Interval inv = dom.intervals[h];
						//for each interval of val
						if (inv != null)
							//For each value of the interval
							for (int v = inv.min(); v <= inv.max(); v++) {
								tuple[0] = v;
								recursiveCall(j, 1, stateNumber, outarc, tuple, result);	 
							}
					}						
				}
					
		result.reduce();
		return result;
		
	}

	// It recursively creates
	private void recursiveCall(int prevSuc, 
							  int level, 
							  int stateNumber, 
							  IntervalDomain[][][] outarc, 
							  int[] tuple, 
							  MDD result) {
				
		if (level == tuple.length) {
			// it adds a tuple to an MDD.
			result.addTuple(tuple);
			return;
		}
		
		IntervalDomain dom;
		
		for (int i = 0; i < stateNumber; i++) 
			if (outarc[level][prevSuc][i] != null  && outarc[level][prevSuc][i].getSize() > 0) {

				dom = outarc[level][prevSuc][i];
				
				for (int h = 0; h < dom.size; h++)	{
					
					Interval inv = (dom).intervals[h];
					
					if (inv != null)
						//For each value of the interval
						for (int v = inv.min(); v <= inv.max(); v++) {
							tuple[level] = v;
							recursiveCall(i, level+1, stateNumber, outarc, tuple, result);	 
						}
				}	
			}
				
	}

	
	
	/**
	 * It generates one by one tuples allowed by a Regular constraint, which are added
	 * to the MDD being built. After all tuples are added MDD is being reduced. The standard
	 * MDD creating procedure employed in paper presenting MDD based extensional constraint.
	 * It generates only the tuples which are allowed in the current context of the store.
	 * 
	 * @param vars variables in which context MDD is being created from Regular constraint. 
	 * @return MDD representing the same constraint as Regular.
	 */
	public MDD transformDirectlyIntoMDD(Variable[] vars) {
		
		MDD result = new MDD(vars);
		
		int levels = vars.length;
		int stateNumber = this.states.size();
	
		//The array that keep all possible states with arcs and their domains
		//If the domain is empty then the arc doesn't exist
		IntervalDomain[][][] outarc = new IntervalDomain[levels + 1][stateNumber][stateNumber];
		
		//Reachable region of the graph
		HashSet<FSMState> reachable = new HashSet<FSMState>();
		//Temporal variable for reachable region  
		HashSet<FSMState> tmp = new HashSet<FSMState>();
		
		//The id's of the states are renamed to make the future graph in latex look pretty
		int level = 0;
		
		resize();
		FSMState array[] = new FSMState[stateNumber];
		for (FSMState s:this.states) {
			array[s.id] = s;
		}
		
		//----- compute the reachable region of the graph -----
		
		//Start with initial state
		reachable.add(this.initState);
		
		while (level < levels) {
			
			//prepare tmp set of reachable states in the next level
			tmp.clear();
			//For each state reached until now
			for (FSMState s : reachable)
				//watch it's edges
				for (FSMTransition t : s.transitions) {
					//prepare the set of values of this edge
					Domain dom = t.domain.intersect(vars[level].dom());
					outarc[level][s.id][t.successor.id] = ( IntervalDomain ) dom;
					
					/* If the edge is not empty them add the state to tmp set
					 * check that such states wasn't previously added.
					 * 
					 * If the level is the last, then check whether the state 
					 * belongs to the set of accepted states 
					 */
					
					if (dom.getSize() > 0)
						if (level < levels - 1)
							tmp.add(t.successor);
						else if ( finalStates.contains(t.successor) )
								tmp.add(t.successor);
							
					//if (dom.getSize() > 0 && !tmp.contains(t.succesor))
					//	if (level < levels -1) tmp.push(t.succesor); 
					//	else if (this.finalStates.contains(t.succesor)) tmp.push(t.succesor);
				}
			
			//copy the tmp set of states into reachable region
			reachable.clear();
			reachable.addAll(tmp);
			//got to next level
			level++;
		}
		
		
		/* ----- delete the paths of the graph that doesn't reach the accepted state -----
		 * 
		 * 
		 *  by calculating the reachable region of the graph starting from the accepted 
		 *  states and following the edges in the opposite direction.
		 */
			
		while(level > 0) {
			
			tmp.clear();

			for (int i = 0; i < stateNumber; i++)
				for (int j = 0; j < stateNumber; j++)
				if (outarc[level-1][j][i] != null && outarc[level-1][j][i].getSize() > 0)
					if (!reachable.contains(array[i])) 
						outarc[level-1][j][i].clear();
					else
						tmp.add(array[j]);						
			
			reachable.clear();
			reachable.addAll(tmp);
			level--;
			
		}

		int [] positions = new int[(vars.length+1)*stateNumber];
		
		positions[ initState.id ] = 0;
		// not needed as constructor is already doing it.
		//result.freePosition += vars[0].getSize();
		
	    // Part exploring all tuples and adding one by one to MDD.
		for (int l = 0; l < vars.length; l++)
			for (int i = 0; i < stateNumber; i++)
				for (int j = 0; j < stateNumber; j++)
					// for level 0 (first variable in the tuple)
					if (outarc[l][i][j] != null && outarc[l][i][j].getSize() > 0) {
					
						// There is an arc from state i to state j at level 0.
						ValueEnumeration enumer = outarc[l][i][j].valueEnumeration();
					
						for (; enumer.hasMoreElements();) {
						
							int nextElement = enumer.nextElement();
				
							int indexOfValue = result.findPosition(nextElement, result.views[l].indexToValue);
							
							if (positions[l*stateNumber+i] == 0 && !(l == 0 && i == initState.id) ) {
								positions[l*stateNumber+i] = result.freePosition;
								result.freePosition += vars[l].getSize();
								result.freePosition += result.domainLimits[l];
							}
							
							if (positions[(l+1)*stateNumber+j] == 0) {
								positions[(l+1)*stateNumber+j] = result.freePosition;
								if (l+1 < vars.length)
									result.freePosition += result.domainLimits[l+1];
								//else {
								//	result.ensureSize(result.freePosition + 1);
								//	result.diagram[result.freePosition] = MDD.TERMINAL;
								//	result.freePosition += 1;
								//}	
							}
							
							if (l+1 < vars.length) {
								result.ensureSize(positions[l*stateNumber+i] + indexOfValue + 1);
								result.diagram[ positions[l*stateNumber+i] + indexOfValue ] = positions[(l+1)*stateNumber+j];
							}
							else {
								result.ensureSize(positions[l*stateNumber+i] + indexOfValue + 1);
								result.diagram[ positions[l*stateNumber+i] + indexOfValue ] = MDD.TERMINAL;
							}
						}
					}

		/** @TODO Check the correctness of this translation */
		return result;
		
	}


	/**
	 * It allows to create a finite state machine from xml description. 
	 * 
	 * @param fsmElement the xml description of finite state machine.
	 * 
	 * @return the finite state machine created from xml description.
	 */
	@SuppressWarnings("unchecked")
	public static FSM fromXML(Element fsmElement) {
	
		FSM result = new FSM();
		
		HashMap<Integer, FSMState> mappingId = new HashMap<Integer, FSMState>();
		
		for (org.jdom.Element stateElement : (List<org.jdom.Element>) fsmElement.getChildren()) {

			if (stateElement.getAttributeValue("type").equals("init")) {
				result.initState = FSMState.fromXML(stateElement);
				mappingId.put(result.initState.id, result.initState);
				continue;
			}
			
			if (stateElement.getAttributeValue("type").equals("final")) {
				FSMState state = FSMState.fromXML(stateElement);
				mappingId.put(state.id, state);
				result.finalStates.add( state );
				continue;
			}
			
			FSMState state = FSMState.fromXML(stateElement);
			mappingId.put(state.id, state);
			result.states.add( state );
			
		}

		result.states.addAll(result.finalStates);
		result.states.add(result.initState);
		
		for (org.jdom.Element stateElement : (List<org.jdom.Element>) fsmElement.getChildren()) {
			int stateId = Integer.valueOf( stateElement.getAttributeValue("id") );
			mappingId.get( stateId ).fromXML(stateElement, mappingId);
		}

		return result;
	}


	/**
	 * It creates an XML description of FSM.
	 * @return XML description.
	 */
	public Element toXML() {
		
		org.jdom.Element fsm = new org.jdom.Element("fsm");
		
		org.jdom.Element state = initState.toXML();
		state.setAttribute("type", "init");
		
		fsm.addContent(state);
		
		for (FSMState finalState : finalStates) {
			state = finalState.toXML();
			state.setAttribute("type", "final");
			fsm.addContent(state);
		}
		
		for (FSMState intermediateState : states) {
			
			if (finalStates.contains(intermediateState))
				continue;
			
			if (intermediateState == initState)
				continue;
			
			state = intermediateState.toXML();
			state.setAttribute("type", "intermediate");
			fsm.addContent(state);
			
		}
		
		return fsm;
	}
	
}
