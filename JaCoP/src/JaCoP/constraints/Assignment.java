/**
 *  Assignment.java 
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
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.ValueEnumeration;
import JaCoP.core.Variable;

/**
 * Assignment constraint implements facility to improve channeling constraints
 * between dual viewpoints of permutation models.
 * 
 * @author Radoslaw Szymanek
 * @version 2.4
 */

public class Assignment extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = assignmentConstr;

	Variable d[];

	HashMap<Variable, Integer> ds;

	boolean firstConsistencyCheck = true;
	int firstConsistencyLevel;

	// int min = 0;
	
	int shiftX = 0;
	
	int shiftD = 0;

	LinkedHashSet<Variable> variableQueue = new LinkedHashSet<Variable>();

	Variable x[];

	HashMap<Variable, Integer> xs;

	/**
	 * It constructs an Assignment constraint with shift equal 0. It
	 * enforces relation - d[x[j]] = i and x[d[i]] = j.
	 * @param xs arraylist of x variables
	 * @param ds arraylist of d variables
	 */
	public Assignment(ArrayList<? extends Variable> xs,
			ArrayList<? extends Variable> ds) {

		numberId = IdNumber++;

		x = new Variable[xs.size()];
		int i = 0;
		for (Variable e : xs) {
			x[i] = e;
			i++;
			numberArgs++;
		}

		d = new Variable[ds.size()];
		i = 0;
		for (Variable e : ds) {
			d[i] = e;
			i++;
			numberArgs++;
		}

		commonInitialization();
	}
	
	/**
	 * It enforces the relationship x[d[i]-min]=i+min and
	 * d[x[i]-min]=i+min. 
	 * @param xs arraylist of variables x
	 * @param ds arraylist of variables d
	 * @param min shift
	 */
	public Assignment(ArrayList<? extends Variable> xs,
					  ArrayList<? extends Variable> ds, 
					  int min) {

		numberId = IdNumber++;

		this.shiftX = min;
		this.shiftD = min;
		x = new Variable[xs.size()];
		int i = 0;
		for (Variable e : xs) {
			x[i] = e;
			i++;
			numberArgs++;
		}

		d = new Variable[ds.size()];
		i = 0;
		for (Variable e : ds) {
			d[i] = e;
			i++;
			numberArgs++;
		}

		commonInitialization();
	}

	
	
	/**
	 * It enforces the relationship x[d[i]-shiftX]=i+shiftD and
	 * d[x[i]-shiftD]=i+shiftX. 
	 * @param xs arraylist of variables x
	 * @param ds arraylist of variables d
	 * @param shiftX 
	 * @param shiftD 
	 */
	public Assignment(ArrayList<? extends Variable> xs,
					  ArrayList<? extends Variable> ds, 
					  int shiftX, 
					  int shiftD) {

		numberId = IdNumber++;

		this.shiftX = shiftX;
		this.shiftD = shiftD;
		x = new Variable[xs.size()];
		int i = 0;
		for (Variable e : xs) {
			x[i] = e;
			i++;
			numberArgs++;
		}

		d = new Variable[ds.size()];
		i = 0;
		for (Variable e : ds) {
			d[i] = e;
			i++;
			numberArgs++;
		}

		commonInitialization();
	}

	/**
	 * It constructs an Assignment constraint with shift equal 0. It
	 * enforces relation - d[x[i]] = i and x[d[i]] = i.
	 * @param xs array of x variables
	 * @param ds array of d variables
	 */
	public Assignment(Variable[] xs, Variable[] ds) {

		numberId = IdNumber++;

		x = new Variable[xs.length];
		for (int i = 0; i < xs.length; i++) {
			x[i] = xs[i];
			numberArgs++;
		}

		d = new Variable[ds.length];
		for (int i = 0; i < ds.length; i++) {
			d[i] = ds[i];
			numberArgs++;
		}

		commonInitialization();
	}

	/**
	 * It enforces the relationship x[d[i]-min]=i+min and
	 * d[x[i]-min]=i+min. 
	 * @param xs array of variables x
	 * @param ds array of variables d
	 * @param min shift
	 */
	public Assignment(Variable[] xs, Variable[] ds, int min) {

		numberId = IdNumber++;

		this.shiftX = min;
		this.shiftD = min;
		x = new Variable[xs.length];
		for (int i = 0; i < xs.length; i++) {
			x[i] = xs[i];
			numberArgs++;
		}

		d = new Variable[ds.length];
		for (int i = 0; i < ds.length; i++) {
			d[i] = ds[i];
			numberArgs++;
		}

		commonInitialization();
	}

	
	/**
	 * It enforces the relationship x[d[i]-shiftX]=i+shiftD and
	 * d[x[i]-shiftD]=i+shiftX. 
	 * @param xs array of variables x
	 * @param ds array of variables d
	 * @param shiftX a shift of indexes in X array.
	 * @param shiftD a shift of indexes in D array.
	 */
	public Assignment(Variable[] xs, Variable[] ds, int shiftX, int shiftD) {

		numberId = IdNumber++;

		this.shiftX = shiftX;
		this.shiftD = shiftD;
		x = new Variable[xs.length];
		for (int i = 0; i < xs.length; i++) {
			x[i] = xs[i];
			numberArgs++;
		}

		d = new Variable[ds.length];
		for (int i = 0; i < ds.length; i++) {
			d[i] = ds[i];
			numberArgs++;
		}

		commonInitialization();
	}
	
	
	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(
				x.length * 2 + 1);

		for (int i = 0; i < x.length; i++) {
			Variables.add(x[i]);
			Variables.add(d[i]);
		}

		return Variables;
	}

	@Override
	public void removeLevel(int level) {
		variableQueue = new LinkedHashSet<Variable>();
		if (level == firstConsistencyLevel)
			firstConsistencyCheck = true;
	}

	protected void commonInitialization() {

		this.queueIndex = 1;
		
		xs = new HashMap<Variable, Integer>(x.length * 2);
		ds = new HashMap<Variable, Integer>(d.length * 2);

		for (int i = 0; i < x.length; i++) {
			xs.put(x[i], i + shiftX);
			ds.put(d[i], i + shiftD);
		}

	}

	IntervalDomain rangeX;
	IntervalDomain rangeD;

	@Override
	public void consistency(Store store) {

		if (firstConsistencyCheck) {

			rangeX = new IntervalDomain(0 + shiftX, x.length - 1
					+ shiftX);

			rangeD = new IntervalDomain(0 + shiftD, x.length - 1
					+ shiftD);

			for (int i = 0; i < x.length; i++) {

				Domain alreadyRemoved = rangeD.subtract(x[i].domain);

				x[i].domain.in(store.level, x[i], shiftD, x.length - 1 + shiftD);
				
				if (!alreadyRemoved.isEmpty())
					for (ValueEnumeration enumer = alreadyRemoved
							.valueEnumeration(); enumer.hasMoreElements();) {

						int xValue = enumer.nextElement();

						d[xValue - shiftD].domain.inComplement(store.level,
								d[xValue - shiftD], i + shiftX);

					}

				if (x[i].singleton()) {
					int position = x[i].value() - shiftD;
					d[position].domain.in(store.level, d[position], i + shiftX, i
							+ shiftX);
				}

			}

			for (int i = 0; i < d.length; i++) {

				Domain alreadyRemoved = rangeX.subtract(d[i].domain);

				d[i].domain.in(store.level, d[i], shiftX, x.length - 1 + shiftX);

				if (!alreadyRemoved.isEmpty())
					for (ValueEnumeration enumer = alreadyRemoved
							.valueEnumeration(); enumer.hasMoreElements();) {

						int dValue = enumer.nextElement();

						x[dValue - shiftX].domain.inComplement(store.level,
								x[dValue - shiftX], i + shiftD);

					}

				if (d[i].singleton()) {

					x[d[i].value() - shiftX].domain.in(store.level, x[d[i].value()
							- shiftX], i + shiftD, i + shiftD);
				}

			}

			firstConsistencyCheck = false;
			firstConsistencyLevel = store.level;
			
		}

		while (!variableQueue.isEmpty()) {

			LinkedHashSet<Variable> fdvs = variableQueue;

			variableQueue = new LinkedHashSet<Variable>();

			for (Variable V : fdvs) {

				Domain vPrunedDomain = V.recentDomainPruning();

				if (!vPrunedDomain.isEmpty()) {

					Integer position = xs.get(V);
					if (position == null) {
						// d variable has been changed
						position = ds.get(V);

						vPrunedDomain = vPrunedDomain.intersect(rangeX);
						
						if (vPrunedDomain.isEmpty())
							continue;

						for (ValueEnumeration enumer = vPrunedDomain
								.valueEnumeration(); enumer.hasMoreElements();) {

							int dValue = enumer.nextElement() - shiftX;

							if (dValue >= 0 && dValue < x.length)
								x[dValue].domain.inComplement(store.level,
										x[dValue], position);
						}

						if (V.singleton())
							x[V.value() - shiftX].domain.in(store.level, x[V.value() - shiftX], position, position);

					} else {
						// x variable has been changed
						
						vPrunedDomain = vPrunedDomain.intersect(rangeD);
						
						if (vPrunedDomain.isEmpty())
							continue;

						for (ValueEnumeration enumer = vPrunedDomain
								.valueEnumeration(); enumer.hasMoreElements();) {

							int xValue = enumer.nextElement() - shiftD;

							if (xValue >= 0 && xValue < d.length)
								d[xValue].domain.inComplement(store.level,
										d[xValue], position);

							if (V.singleton())
								d[V.value() - shiftD].domain.in(store.level, d[V.value() - shiftD], position, position);

						}

					}

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
			return Constants.ANY;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_assignment + numberId;
	}

	// registers the constraint in the constraint store
	@Override
	public void impose(Store store) {

		store.registerRemoveLevelListener(this);

		for (int i = 0; i < x.length; i++) {
			x[i].putModelConstraint(this, getConsistencyPruningEvent(x[i]));
			d[i].putModelConstraint(this, getConsistencyPruningEvent(d[i]));
		}

		store.addChanged(this);
		store.countConstraint();

		store.raiseLevelBeforeConsistency = true;

	}

	@Override
	public void queueVariable(int level, Variable V) {

		variableQueue.add(V);
	}

	@Override
	public void removeConstraint() {

		for (int i = 0; i < x.length; i++)
			x[i].removeConstraint(this);

		for (int i = 0; i < d.length; i++)
			d[i].removeConstraint(this);

	}

	@Override
	public boolean satisfied() {

		int i = 0;
		while (i < x.length) {
			if (!x[i].singleton())
				return false;
			if (!d[i].singleton())
				return false;
			i++;
		}

		return true;

	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : assignment([");
		
		for (int i = 0; i < x.length; i++) {
			result.append(x[i]);
			if (i < x.length - 1)
				result.append(", ");
		}
		result.append("], [");
		
		for (int i = 0; i < d.length; i++) {
			result.append(d[i]);
			if (i < d.length - 1)
				result.append(", ");
		}
		result.append("])");

		return result.toString();
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_assignment);
		constraint.setAttribute("arity", String.valueOf(x.length + d.length));

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		for (int i = 0; i < x.length; i++)
			scopeVars.add(x[i]);
		for (int i = 0; i < d.length; i++)
			scopeVars.add(d[i]);

		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	

	
		org.jdom.Element xList = new org.jdom.Element("xlist");	
		StringBuffer xString = new StringBuffer();
		for (int i = 0; i < x.length - 1; i++)
			xString.append(  x[i].id() + " " );
		xString.append(x[x.length - 1]);
		xList.setText(xString.toString());		

		org.jdom.Element dList = new org.jdom.Element("dlist");

		StringBuffer dString = new StringBuffer();
		for (int i = 0; i < d.length - 1; i++)
			dString.append(  d[i].id() ).append( " " );
		dString.append(d[d.length - 1]);
		dList.setText(dString.toString());		
		
		org.jdom.Element shiftElX = new org.jdom.Element("shiftX");
		shiftElX.setText(String.valueOf(shiftX));

		org.jdom.Element shiftElD = new org.jdom.Element("shiftD");
		shiftElD.setText(String.valueOf(shiftD));

		constraint.addContent(xList);
		constraint.addContent(dList);
		constraint.addContent(shiftElX);
		constraint.addContent(shiftElD);

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	
	/**
	 * It constructs the constraint from XML element.
	 * @param constraint an XCSP description of the constraint.
	 * @param store the store in which the context the constraint is created.
	 * @return created constraint from XCSP description.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		
		String xList = constraint.getChild("xlist").getText();
		String dList = constraint.getChild("dlist").getText();
		int shiftX = Integer.valueOf(constraint.getChild("shiftX").getText());
		int shiftD = Integer.valueOf(constraint.getChild("shiftD").getText());

		Pattern pattern = Pattern.compile(" ");
		String[] xVarsNames = pattern.split(xList);
		String[] dVarsNames = pattern.split(dList);

		ArrayList<Variable> x = new ArrayList<Variable>();
		ArrayList<Variable> d = new ArrayList<Variable>();

		for (String n : xVarsNames)
			x.add(store.findVariable(n));

		for (String n : dVarsNames)
			d.add(store.findVariable(n));
		
		return new Assignment(x, d, shiftX, shiftD);

	}

    @Override
	public void increaseWeight() {
		if (increaseWeight) {
			for (Variable v : x) v.weight++;
			for (Variable v : d) v.weight++;
		}
	}

}
