/**
 *  FSMTransition.java 
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jdom.Element;

import JaCoP.core.Domain;

/**
 * @author Radoslaw Szymanek
 * @version 2.4
 */

public class FSMTransition {

	/**
	 * It specifies the domain associated with the transition. 
	 */
	public Domain domain;

	
	/**
	 * It specifies the successor state we arrive to after taking the transition.
	 */
	public FSMState successor;
	
	/**
	 * It constructs a finite machine state transition.
	 * @param domain the domain which triggers the transition.
	 * @param state the successor state reached by a transition.
	 */
	public FSMTransition(Domain domain, FSMState state) {	
		this.domain = domain;
		this.successor = state;
	}
	
	
	/**
	 * It performs a clone of a transition with copying the attributes too.
	 * @param states a list of states which have been already copied.
	 * @return the transition clone.
	 */
	public FSMTransition deepClone(HashSet<FSMState> states) {
				
		return new FSMTransition(domain, successor.deepClone(states));
	
	}

	@Override
	public int hashCode() {
		return successor.id;
	}	
	
	@Override
	public boolean equals(Object o) {

		if (o == null)
			return false;
		
		if (o == this)
			return true;
		
		FSMTransition compareTo = (FSMTransition) o;
		
		if (compareTo.successor.equals(successor) &&
			compareTo.domain.eq(domain))
			return true;
		
		return false;
		
	}

	@Override
	public String toString() {
		return successor.toString() + "@" + domain.toString();
	}
	
	/**
	 * It returns an XML element describing the transition of Finite State Machine. It
	 * 
	 * 
	 * @return XML element describing the transition.
	 */
	public org.jdom.Element toXML() {
	
		org.jdom.Element transition = new org.jdom.Element("transition");
		transition.setAttribute("successorId", String.valueOf(successor.id) );
		
		org.jdom.Element domainElement = domain.toXML();
		transition.addContent(domainElement);
		
		return transition;
	}


	/**
	 * It completes creating transition from XML element. It uses the mapping of ids to states
	 * of finite state machine.
	 * @param transitionElement an XML element describing the transition.
	 * @param mapping the mapping containing all states of finite state machine.
	 * @return transition of finite state machine.
	 */
	@SuppressWarnings("unchecked")
	public static FSMTransition fromXML(Element transitionElement,
			                            HashMap<Integer, FSMState> mapping) {
		FSMState successor = mapping.get( Integer.valueOf( transitionElement.getAttributeValue("successorId") ) );
		org.jdom.Element domainElement = ((List<org.jdom.Element>)transitionElement.getChildren()).get(0);
		Domain domain = null;
		try {
			domain = ((Domain)(Class.forName( domainElement.getName() ).newInstance()));
		} catch (InstantiationException e) {
			assert false : "Domain description within fsm is not possible to instantiate";
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			assert false : "Domain description within fsm is not possible to access";
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			assert false : "Domain description within fsm is not possible to find";
			e.printStackTrace();
		}
		domain.fromXML(domainElement);
		return new FSMTransition(domain, successor);
	}

}
