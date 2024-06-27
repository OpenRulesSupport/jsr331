/**
 *  FSMState.java 
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

package JaCoP.util.fsm;

import java.util.*;

import org.jdom.Element;

/**
 * @author Radoslaw Szymanek
 * @version 2.4
 */

public class FSMState {	
	
	/**
	 * Id of the state. There can be multiple copies of the same state with the same id.
	 */
	public int id;
	
	/**
	 * It specifies the list of transitions  outgoing from this state.
	 */
	public HashSet<FSMTransition> transitions;
	

	/**
	 * It creates a state with id equl to the number of instances FSMState created.
	 */
	public FSMState(){
		this.id = FSM.stateId++;
		transitions = new  HashSet<FSMTransition>();
	}
		
	
	/**
	 * It creates a state with an id as the id specified by a supplied state.
	 * @param a state from which id is taken while creating this state.
	 */
	public FSMState(FSMState a) {
		this.id = a.id;
		transitions = new  HashSet<FSMTransition>();
	}
	
	/**
	 * Performing deep clone unless this state has already a state with
	 * the same id in the array of states.
	 * @param states it contains the states which do not need to be created, only reused.
	 * @return a deep clone of the current state.
	 */
	public FSMState deepClone(HashSet<FSMState> states)
	{
		
		// replace by HashSet contains check.
		FSMState newFSM = null;
		for (FSMState s : states)
			if (s.id == this.id)
				newFSM = s;
		if (newFSM != null) return newFSM;
		
		newFSM = new FSMState(this);
		states.add(newFSM);
		for(FSMTransition t : this.transitions)
			newFSM.transitions.add(t.deepClone(states));
		return newFSM;
	}
	
	/**
	 * It adds transition to the list of transitions from 
	 * this state.
	 * @param transition the transition being added.
	 */
	public void addTransition(FSMTransition transition) {
		transitions.add(transition);
	}
	
	@Override
	public boolean equals(Object o) {
		return this.id == ((FSMState)o).id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public String toString() {
		return "state_"+String.valueOf(id);		
	}

	/**
	 * It creates an XML element describing the state. 
	 * 
	 * @return it returns an XML description of the state.
	 */
	public org.jdom.Element toXML() {
	
		org.jdom.Element state = new org.jdom.Element("state");
	
		state.setAttribute("id", Integer.toString(id));
		
		for (FSMTransition transition : transitions) {
			state.addContent(transition.toXML());
		}
		
		return state;
	}

	/**
	 * It creates a state partially filled with information from XML element
	 * describing the state. 
	 * @param state an XML element describing the state.
	 * @return state with only state id set.
	 */
	public static FSMState fromXML(Element state) {
		
		FSMState result = new FSMState();
		result.id = Integer.valueOf( state.getAttributeValue("id") );
		return result;
	}
	
	/**
	 * It concludes creating a state from XML element.
	 * 
	 * @param state an XML element describing the state.
	 * @param mapping the mapping of integer ids onto states.
	 */
	@SuppressWarnings("unchecked")
	public void fromXML(Element state, HashMap<Integer, FSMState> mapping) {
		
		for (org.jdom.Element transitionElement : (List<org.jdom.Element>) state.getChildren()) {
		
			FSMTransition transition = FSMTransition.fromXML(transitionElement, mapping);
			transitions.add(transition);
		}
	}
	
}
