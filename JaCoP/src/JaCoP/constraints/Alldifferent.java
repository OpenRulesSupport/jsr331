/**
 *  Alldifferent.java 
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

import JaCoP.constraints.Constraint;
import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.TimeStamp;
import JaCoP.core.Variable;

/**
 * Alldifferent constraint assures that all FDVs has differnet values. It uses
 * partial consistency technique.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Alldifferent extends Constraint implements Constants {

	static int IdNumber = 1;
	
	final static short type = alldifferent;
	
	protected Variable[] list;
	
	int stamp = 0;

	LinkedHashSet<Variable> variableQueue = new LinkedHashSet<Variable>();

	protected HashMap<Variable, Integer> positionMapping;
	
	protected TimeStamp<Integer> grounded;
	
	protected Alldifferent() {
	}

	/**
	 * It constructs the alldifferent constraint for the supplied variable.
	 * @param variables variables which are constrained to take different values.
	 */

	public Alldifferent(ArrayList<? extends Variable> variables) {

		numberId = IdNumber++;
		list = new Variable[variables.size()];
		list = variables.toArray(list);

		numberArgs = (short) variables.size();
	}

	/**
	 * It constructs the alldifferent constraint for the supplied variable.
	 * @param variables variables which are constrained to take different values.
	 */
	public Alldifferent(Variable[] variables) {

		numberId = IdNumber++;
		list = new Variable[variables.length];

		for (int i = 0; i < variables.length; i++) {
			list[i] = variables[i];
			numberArgs++;
		}

	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(list.length);

        Variables.addAll(Arrays.asList(list));

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {

		while (store.newPropagation) {
			store.newPropagation = false;

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
			return Constants.GROUND;
	}

	@Override
	public String id() {	
		if (id != null)
			return id;
		else
			return  id_alldifferent + numberId;
	}
	
	@Override
	public boolean satisfied() {
		 
		 for(int i = grounded.value(); i < list.length; i++)
			 if (!list[i].singleton())
				 return false;
		
		 HashSet<Integer> values = new HashSet<Integer>();

        for (Variable aList : list)
            if (!values.add(aList.value()))
                return false;

        return true;
		 
	 }

	 @SuppressWarnings("unused")
	private boolean satisfiedFullCheck(Store S) {
	 
		 	int i = 0;
	 
		 	IntervalDomain result = new IntervalDomain();
		 	
		 	while (i < list.length - 1) {
		 			
		 		if (list[i].domain.isIntersecting(result))
		 			return false;
		 			
		 		result.addDom(list[i].domain);
		 			
		 		i++;
		 	}
		 		
		 	return true;
		 	
	 }

	@Override
	public void impose(Store store) {
		int level = store.level;

		int pos = 0;
		positionMapping = new HashMap<Variable, Integer>();
		for (Variable v : list) {
			positionMapping.put(v, pos++);
			v.putModelConstraint(this, getConsistencyPruningEvent(v));
			queueVariable(level, v);
		}
		grounded = new TimeStamp<Integer>(store, 0);
		
		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void queueVariable(int level, Variable V) {
		variableQueue.add(V);
	}

	@Override
	public void removeConstraint() {
		for (Variable v : list)
			v.removeConstraint(this);
	}

	@SuppressWarnings("unused")
	private boolean satisfiedBound() {
		boolean sat = true;
		int i = 0;
		while (sat && i < list.length) {
			Domain vDom = list[i].dom();
			int vMin = vDom.min(), vMax = vDom.max();
			int j = i + 1;
			while (sat && j < list.length) {
				Domain ljDom = list[j].dom();
				sat = (vMin > ljDom.max() || vMax < ljDom.min());
				j++;
			}
			i++;
		}
		return sat;
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : alldifferent([");

		for (int i = 0; i < list.length; i++) {
			result.append(list[i]);
			if (i < list.length - 1)
				result.append(", ");
		}
		result.append("])");
		
		return result.toString();
		
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("flavour", "alldifferent");
		constraint.setAttribute("reference", "global:allDifferent");
		constraint.setAttribute("arity", String.valueOf(list.length));

		StringBuffer scope = new StringBuffer();
		for (int i = 0; i < list.length - 1; i++)
			scope.append( list[i].id() ).append(" ");
		
		scope.append(list[list.length - 1]);
		constraint.setAttribute("scope", scope.toString());

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	/**
	 * Function to read and create Alldifferent constraint from XML (XCSP description). 
	 * 
	 * @param constraint XML element describing the constraint.
	 * @param store constraint store in which context the constraint is being created.
	 * @return constraint itself.
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

		return new Alldifferent(vars);

	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			for (Variable v : list) 
				v.weight++;
		}
	}	

}
