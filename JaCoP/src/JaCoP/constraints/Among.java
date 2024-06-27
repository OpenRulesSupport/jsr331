/**
 *  Among.java 
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

package JaCoP.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.TimeStamp;
import JaCoP.core.Variable;

/**
 * Among constraint in its simplest form. It establishes the following
 * relation. The given number N of X`s take values from the supplied set
 * of values Kset. 
 * 
 * This constraint implements a simple and polynomial algorithm to establish 
 * GAC as presented in different research papers. There are number of 
 * improvements (iterative execution, optimization of computational load upon 
 * backtracking) to improve the constraint further. 
 * 
 * @author Polina Makeeva and Radoslaw Szymanek
 * @version 2.4
 */

public class Among extends Constraint implements Constants {
	
	static final boolean debugAll = false;

	static int idNumber = 1;

	private Variable[] varList;

	private HashMap<Variable, Integer> position;
	
	// Given set of values for the variables
	private IntervalDomain Kset;

	private Variable N;

	// number if x that belongs to K (Kset)
	// As search progress this time stamp can only increase
	// because if X was in between lbS and ubS than
	// it can have the between (x intersects S <> empty and x doesn't belong to
	// S) values being shrinked.
	private TimeStamp<Integer> lowerBorder;

	// number of x who may still intersect K (Kset)
	private TimeStamp<Integer> upperBorder;

	LinkedHashSet<Variable> variableQueue = new LinkedHashSet<Variable>();

	/**
	 * It constructs an Among constraint.
	 * @param variables variables which are compared to Kset
	 * @param Kset set of integer values against which we check if variables are equal to.
	 * @param N number of possible variables equal to a value from Kset.
	 */
	public Among(Variable[] variables, IntervalDomain Kset, Variable N) {

		this.queueIndex = 1;
		
		numberId = idNumber++;

		varList = new Variable[variables.length];
		for (int i = 0; i < variables.length; i++) {
			varList[i] = variables[i];
			numberArgs++;
		}

		this.Kset = Kset;
		this.N = N;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> args = new ArrayList<Variable>(this.numberArgs - 1);

		args.add(this.N);

		for (Variable v : this.varList)
			args.add(v);

		return args;
	}

	@Override
	public void removeLevel(int level) {
		this.variableQueue.clear();

	}

	@Override
	public void consistency(Store store) {
		// ----------------------------------------------------------
		if (debugAll) {
			System.out.println("LEVEL : " + store.level);
			System.out.println(this);
		}
		// ----------------------------------------------------------

		int currentLB = this.lowerBorder.value();
		// Refer to the algorithm where ubS = n - |{ x | dom(x) intersect S =
		// empty set } |
		int currentUB = this.upperBorder.value();

		// For the variable that signaled the change of domain
		// Count those that entered lbS, or ubS
		for (Variable var : this.variableQueue) {
			if (this.Kset.contains(var.domain)) {

				int posVar = position.get(var);
				if (posVar != currentLB) {
					varList[posVar] = varList[currentLB];
					varList[currentLB] = var;
					position.put(var, currentLB);
					position.put(varList[posVar], posVar);
				}
				currentLB++;

				// If variable entered lb then it would stay there
				// and we can detach the constrain from it
				var.removeConstraint(this);

			}
			if (!this.Kset.isIntersecting(var.domain)) {
				
				int posVar = position.get(var);
				if (posVar != currentUB) {
					varList[posVar] = varList[currentUB-1];
					varList[currentUB-1] = var;
					position.put(var, currentUB-1);
					position.put(varList[posVar], posVar);
				}
				currentUB--;

				// If the variable entered not ub then it will stay there
				// and we can detach the constrain from it
				var.removeConstraint(this);

			}
		}

		variableQueue.clear();

		// ----------------------------------------------------------
		if (debugAll) {
			System.out.println("lbS = " + currentLB);
			System.out.println("ubS = " + currentUB);
			System.out.println(" domain of N " + N.domain + " is in [ "
					+ Math.max(N.min(), currentLB) + ", "
					+ Math.min(N.max(), currentUB) + " ]");
		}
		// ----------------------------------------------------------
		
		if (Math.max(N.min(), currentLB) > Math.min(N.max(), currentUB))
			throw Store.failException;
		
		N.domain.in(store.level, N, Math.max(N.min(), currentLB), Math.min(N.max(),
				currentUB));

		// Just in case LB or UB have changed.
		upperBorder.update(currentUB);
		lowerBorder.update(currentLB);

		if (currentLB == N.min() && N.domain.singleton()) {
			// If the number of X that belong to S is equal to N.value than we
			// have to subtract
			// the K set from the rest of x that do not belong to S
			for (int i = currentLB; i < currentUB; i++) {
				Variable var = varList[i];
				if (!this.Kset.contains(var.domain)) {
					if (debugAll) {
						System.out.println("lb >> The value before in of "
								+ var.id + ": " + var.domain);
						System.out.println("lb >> subtrack " + this.Kset);
						System.out.println("lb >> equals "
								+ var.domain.subtract(this.Kset));
					}
					var.domain.in(store.level, var, var.domain.subtract(this.Kset));
					var.removeConstraint(this);
					if (debugAll)
						System.out.println("lb >> The value after in of "
								+ var.id + ": " + var.domain);
				}
			}
			
			// since the constraint is satisfied UB is equal to LB. 
			upperBorder.update(currentLB);
			
			// The constrain became satisfied
			if (debugAll)
				System.out.println("Simple Among is satisfied");
		}

		if (currentUB == N.min() && N.domain.singleton()) {
			// If the number intersecting X is equal to desired number N than we
			// have
			// to intersect the domains of X with K set.
			for (int i = currentLB; i < currentUB; i++) {
				Variable var = varList[i];
				var.domain.in(store.level, var, Kset);
				var.removeConstraint(this);
			}
			
			// since the constraint is satisfied LB is equal to UB. 
			lowerBorder.update(currentUB);
			
			// The constrain became satisfied
			if (debugAll)
				System.out.println("Simple Among is satisfied");
		}

		if (debugAll)
			System.out.println(this);
	}

	/**
	 * No predicate description of Among constraint, so this function returns null.
	 * @return always null.
	 */
	@Override
	public Element getPredicateDescriptionXML() {
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
			return id_among + numberId;
	}

	@Override
	public void impose(Store store) {

		store.registerRemoveLevelListener(this);
		this.lowerBorder = new TimeStamp<Integer>(store, 0);
		this.upperBorder = new TimeStamp<Integer>(store, varList.length);

		int level = store.level;
		int pos = 0;
		position = new HashMap<Variable, Integer>();
		for (Variable var : varList) {
			position.put(var, pos);
			var.putConstraint(this);
			queueVariable(level, var);
			pos++;
		}

		store.addChanged(this);
		store.countConstraint();

	}

	@Override
	public void queueVariable(int level, Variable V) {
		if (debugAll)
			System.out.println("Var " + V + V.recentDomainPruning());

		variableQueue.add(V);
	}

	@Override
	public void removeConstraint() {
		for (Variable var : varList)
			var.removeConstraint(this);

		N.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {

		return (lowerBorder.value() == upperBorder.value() && N.min() == lowerBorder.value()
				&& N.singleton());

	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id () );
		
		result.append(" Among(");
		
		for(Variable var : this.varList)
			result.append("variable").append(var.id).append(" : ").append(var.domain).append(" ");

		result.append(")\n Kset : ").append(this.Kset).append("\n");
		result.append("variable ").append(N.id).append(" : ").append(N.domain).append(")\n");
		
		return result.toString();
	}


	
	@Override
	public Element toXML() {
		
		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_among);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(N);
		for (int i = 0; i < varList.length; i++)
			scopeVars.add(varList[i]);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));
		
		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));		
		

		org.jdom.Element term = new org.jdom.Element("N");
		term.setText(N.id());
		constraint.addContent(term);

		org.jdom.Element tList = new org.jdom.Element("list");
		
		StringBuffer tString = new StringBuffer();
		for (int i = 0; i < varList.length - 1; i++)
			tString.append(varList[i].id()).append( " " );
		
		tString.append(varList[varList.length - 1]);
		tList.setText(tString.toString());

		constraint.addContent(tList);

		org.jdom.Element set = new org.jdom.Element("Kset");

		String domainValues = Kset.toString();
		domainValues = domainValues.replace("{", " ");
		domainValues = domainValues.replace("}", " ");
		domainValues = domainValues.replace(",", " ");
		set.setText(domainValues.trim());
				
		constraint.addContent(set);

		return constraint;
		
	}

	/**
	 * It creates a constraint from XCSP description.
	 * @param constraint XML element describing the constraint.
	 * @param store store in which the constraint is created.
	 * @return constraint created from XML element.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		
		String list = constraint.getChild("list").getText();
		String set = constraint.getChild("kSet").getText();
		String N = constraint.getChild("N").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(list);

		Variable[] x = new Variable[varsNames.length];

		for (int i = 0; i < x.length; i++)
			x[i] = store.findVariable(varsNames[i]);

		if (debugAll)
			System.out.println("values " + set);

		String[] intervals = pattern.split(set);

		IntervalDomain Kset = new IntervalDomain(intervals.length);

		pattern = Pattern.compile("[.]+");

		for (String interval : intervals) {

			if (debugAll)
				System.out.println("interval " + interval);

			if (interval.equals(""))
				continue;

			String[] parts = pattern.split(interval);

			if (parts.length == 1)
				Kset.addDom(Integer.valueOf(parts[0].trim()), Integer
						.valueOf(parts[0].trim()));

			if (parts.length == 2)
				Kset.addDom(Integer.valueOf(parts[0].trim()), Integer
						.valueOf(parts[1].trim()));

		}
		
		return new Among(x, Kset, store.findVariable(N));
	}	
	
	@Override
	public short type() {
		return among;
	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			N.weight++;
			for (Variable v : varList) v.weight++;
		}
	}

}
