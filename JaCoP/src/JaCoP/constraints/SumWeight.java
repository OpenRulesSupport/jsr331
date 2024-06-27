/**
 *  SumWeight.java 
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Store;
import JaCoP.core.TimeStamp;
import JaCoP.core.Variable;

/**
 * SumWeight constraint implements the weighted summation over several
 * Variable's . It provides the weighted sum from all Variable's on the list.
 * The weights must be positive integers.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class SumWeight extends Constraint implements Constants {

    static int IdNumber = 1;

    final static short type = sumWeightConstr;

    Variable list[];

    Variable sum;

    int w[];

    /**
     * It constructs the constraint SumWeight. 
     * @param variables variables which are being multiplied by weights.
     * @param weights weight for each variable.
     * @param sum variable containing the sum of weighted variables.
     */
    public SumWeight(ArrayList<? extends Variable> variables,
		     ArrayList<Integer> weights, Variable sum) {

	queueIndex = 1;

	assert ( variables.size() == weights.size() ) : "\nLength of two vectors different in SumWeight";

	numberId = IdNumber++;
		
	HashMap<Variable, Integer> parameters = new HashMap<Variable, Integer>();
	this.sum = sum;
		
	for (int i = 0; i < variables.size(); i++) {
			
	    if (parameters.get(variables.get(i)) != null) {
		// variable ordered in the scope of the Sum Weight constraint.
		Integer coeff = parameters.get(variables.get(i));
		Integer sumOfCoeff = coeff + weights.get(i);
		parameters.put(variables.get(i), sumOfCoeff);
	    }
	    else
		parameters.put(variables.get(i), weights.get(i));
			
	}
		
	commonInitialization(parameters);

    }

    private void commonInitialization(HashMap<Variable, Integer> parameters) {

	assert ( parameters.get(sum) == null) : "Sum variable is used on both sides.";
		
	list = new Variable[parameters.size()];
	w = new int[parameters.size()];

	int i = 0;
	for (Variable var : parameters.keySet()) {
	    list[i] = var;
	    w[i] = parameters.get(var);
	    numberArgs++;
	    i++;
	}
		
    }

    /**
     * @param variables
     * @param weights
     * @param sum
     */
    public SumWeight(Variable[] variables, int[] weights, Variable sum) {
		
	queueIndex = 1;

	assert ( variables.length == weights.length ) : "\nLength of two vectors different in SumWeight";

	numberId = IdNumber++;
		
	this.sum = sum;

	HashMap<Variable, Integer> parameters = new HashMap<Variable, Integer>();
		
	for (int i = 0; i < variables.length; i++) {
			
	    if (parameters.get(variables[i]) != null) {
		// variable ordered in the scope of the Sum Weight constraint.
		Integer coeff = parameters.get(variables[i]);
		Integer sumOfCoeff = coeff + weights[i];
		parameters.put(variables[i], sumOfCoeff);
	    }
	    else
		parameters.put(variables[i], weights[i]);
			
	}
		
	commonInitialization(parameters);
		
		
    }

    @Override
	public ArrayList<Variable> arguments() {

	ArrayList<Variable> variables = new ArrayList<Variable>(list.length + 1);

	variables.add(sum);
		
	for (Variable v : list)
	    variables.add(v);

	return variables;
    }

    @Override
	public void removeLevel(int level) {
    }

	
    @Override
	public void removeLevelLate(int level) {
		
	backtrackHasOccured = true;
		

    }

	
    /**
     * The sum of grounded variables.
     */
    private TimeStamp<Integer> sumGrounded;
	
    /**
     * The position for the next grounded variable.
     */
    private TimeStamp<Integer> nextGroundedPosition;	

    @Override
	public void consistency(Store store) {

 	if (backtrackHasOccured) {

	    backtrackHasOccured = false;
			
	    int pointer = nextGroundedPosition.value();

	    lMin = sumGrounded.value();
	    lMax = lMin;

	    for (int i = pointer; i < list.length; i++) {
		Domain currentDomain = list[i].domain;

		assert (!currentDomain.singleton()) : "Singletons should occur in this part of the array";

// 		int mul1 = currentDomain.min() * w[i];
// 		int mul2 = currentDomain.max() * w[i];
		int mul1 = multiply(currentDomain.min(), w[i]);
		int mul2 = multiply(currentDomain.max(), w[i]);
		if (mul1 <= mul2) {
// 		    lMin += mul1;
		    lMin = add(lMin, mul1);
		    lMinArray[i] = mul1;
// 		    lMax += mul2;
		    lMax = add(lMax, mul2);
		    lMaxArray[i] = mul2;
		}
		else {

// 		    lMin += mul2;
		    lMin = add(lMin, mul2);
		    lMinArray[i] = mul2;
// 		    lMax += mul1;
		    lMax = add(lMax, mul1);
		    lMaxArray[i] = mul1;

 		}

		/*
		  if (currentDomain.singleton()) {
					
		  int pos = positionMaping.get(list[i]);

		  if (i < pointer)
		  return;

		  int value = list[i].min();

		  int weightGrounded = w[pos];

		  if (pointer < pos) {
		  Variable grounded = list[pos];
		  list[pos] = list[pointer];
		  list[pointer] = grounded;

		  positionMaping.put(list[i], i);
		  positionMaping.put(list[pointer], pointer);

		  int temp = lMinArray[pos];
		  lMinArray[pos] = lMinArray[pointer];
		  lMinArray[pointer] = temp;
							
		  temp = lMaxArray[pos];
		  lMaxArray[pos] = lMaxArray[pointer];
		  lMaxArray[pointer] = temp;
							
		  w[pos] = w[pointer];
		  w[pointer] = weightGrounded;

		  }

		  sumGrounded.update( sumGrounded.value() + value * weightGrounded );
						
		  pointer++;
		  nextGroundedPosition.update(pointer);
					
		  }
		*/
	    }
			
	}
		
	while (store.newPropagation) {

	    sum.domain.in(store.level, sum, lMin, lMax);

	    store.newPropagation = false;

// 	    int min = sum.min() - lMax;
// 	    int max = sum.max() - lMin;
	    int min = subtract(sum.min(), lMax);
	    int max = subtract(sum.max(), lMin);

	    int pointer1 = nextGroundedPosition.value();

	    for (int i = pointer1; i < list.length; i++) {

		if (w[i] == 0)
		    continue;
				
		Variable v = list[i];

		/*
		  int mulMin, mulMax;
		  int mul1 = v.min() * w[i];
		  int mul2 = v.max() * w[i];
				
		  if (mul1 <= mul2) {
		  mulMin = mul1;
		  mulMax = mul2;
		  }
		  else {
		  mulMin = mul2;
		  mulMax = mul1;
		  }

		  float d1 = ((float)(min + mulMax) / w[i]);
		  float d2 = ((float)(max + mulMin) / w[i]);

		*/

		float d1 = ((float)(min + lMaxArray[i]) / w[i]);
		float d2 = ((float)(max + lMinArray[i]) / w[i]);

		int divMin, divMax;
		if (d1 <= d2) {
		    divMin = toInt( Math.round( Math.ceil ( d1 ) ) );
		    divMax = toInt( Math.round( Math.floor( d2 ) ) );
		}
		else {
		    divMin = toInt( Math.round( Math.ceil ( d2 ) ) );
		    divMax = toInt( Math.round( Math.floor( d1 ) ) );
		}
				
		if (divMin > divMax) 
		    store.throwFailException(this);

		v.domain.in(store.level, v, divMin, divMax);

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
	return Constants.BOUND;
    }

    @Override
	public String id() {
	if (id != null)
	    return id;
	else
	    return id_sumWeight + numberId;
    }

    @Override
	public void impose(Store store) {

	sumGrounded = new TimeStamp<Integer>(store, 0);
	nextGroundedPosition = new TimeStamp<Integer>(store, 0);
	positionMaping = new HashMap<Variable, Integer>();
		
	store.registerRemoveLevelLateListener(this);
		
	sum.putModelConstraint(this, getConsistencyPruningEvent(sum));
	for (Variable V : list)
	    V.putModelConstraint(this, getConsistencyPruningEvent(V));

	lMinArray = new int[list.length];
	lMaxArray = new int[list.length];
	lMin = 0;
	lMax = 0;

	for (int i = 0; i < list.length; i++) {
			
	    assert (positionMaping.get(list[i]) == null) : "The variable occurs twice in the list, not able to make a maping from the variable to its list index.";
			
	    positionMaping.put(list[i], new Integer(i));
	    queueVariable(store.level, list[i]);
	}
		
	store.addChanged(this);
	store.countConstraint();
    }

    int lMin;
	
    int lMax;

    int[] lMinArray;
	
    int[] lMaxArray;
	
    HashMap<Variable, Integer> positionMaping;
	
    boolean backtrackHasOccured = false;
	
    @Override
	public void queueVariable(int level, Variable V) {
	
	if (V == sum)
	    return;
		
	if (V.singleton()) {

	    int pointer = nextGroundedPosition.value();

	    int i = positionMaping.get(V);

	    if (i < pointer)
		return;

	    int value = V.min();

	    int sumJustGrounded = 0;

	    int weightGrounded = w[i];

	    if (pointer < i) {
		Variable grounded = list[i];
		list[i] = list[pointer];
		list[pointer] = grounded;

		positionMaping.put(list[i], i);
		positionMaping.put(list[pointer], pointer);

		int temp = lMinArray[i];
		lMinArray[i] = lMinArray[pointer];
		lMinArray[pointer] = temp;
				
		temp = lMaxArray[i];
		lMaxArray[i] = lMaxArray[pointer];
		lMaxArray[pointer] = temp;
				
		w[i] = w[pointer];
		w[pointer] = weightGrounded;

	    }

 	    sumJustGrounded = add(sumJustGrounded, multiply(value, weightGrounded));
// 	    sumJustGrounded += value * weightGrounded;

	    sumGrounded.update( sumGrounded.value() + sumJustGrounded );

	    lMin = add(lMin, sumJustGrounded - lMinArray[pointer]);
	    lMax = add(lMax, sumJustGrounded - lMaxArray[pointer]);
// 	    lMin += lMin, sumJustGrounded - lMinArray[pointer];
// 	    lMax += lMax, sumJustGrounded - lMaxArray[pointer];
	    lMinArray[pointer] = sumJustGrounded;
	    lMaxArray[pointer] = sumJustGrounded;
			
	    pointer++;
	    nextGroundedPosition.update(pointer);

	}

	else {

	    int i = positionMaping.get(V);

	    int mul1 = multiply(V.min(), w[i]);
	    int mul2 = multiply(V.max(), w[i]);
// 	    int mul1 = V.min() * w[i]);
// 	    int mul2 = V.max() * w[i]);
	    if (mul1 <= mul2) {

		lMin = add(lMin, mul1 - lMinArray[i]);
// 		lMin += mul1 - lMinArray[i];
		lMinArray[i] = mul1;

		lMax = add(lMax, mul2 - lMaxArray[i]);
// 		lMax += mul2 - lMaxArray[i];
		lMaxArray[i] = mul2;

	    }
	    else {

 		lMin = add(lMin, mul2 - lMinArray[i]);
// 		lMin += mul2 - lMinArray[i];
		lMinArray[i] = mul2;

		lMax = add(lMax, mul1 - lMaxArray[i]);
// 		lMax += mul1 - lMaxArray[i];
		lMaxArray[i] = mul1;

	    }


	}

    }

    @Override
	public void removeConstraint() {
	sum.removeConstraint(this);
	for (Variable v : list)
	    v.removeConstraint(this);
    }

    @Override
	public boolean satisfied() {
		
	if (!sum.singleton())
	    return false;
				
	if (nextGroundedPosition.value() != list.length)
	    return false;
		
	if (sumGrounded.value() != sum.value())
	    return false;
		
	return true;
		
	/*	boolean sat = sum.singleton();
		int i = 0;
		int sumAll = 0;
		while (sat && i < list.length) {
		sat = list[i].singleton();
		i++;
		}
		if (sat) {
		for (int j = 0; j < list.length; j++)
		sumAll += list[j].min() * w[j];
		}
		return (sat && sumAll == sum.min());
	*/
		
    }

    @Override
	public String toString() {
		
	StringBuffer result = new StringBuffer( id() );
	result.append(" : sumWeight( [ ");

	for (int i = 0; i < list.length; i++) {
	    result.append(list[i]);
	    if (i < list.length - 1)
		result.append(", ");
	}
	result.append("], [");
		
	for (int i = 0; i < w.length; i++) {
	    result.append( w[i] );
	    if (i < w.length - 1)
		result.append( ", " );
	}

	result.append( "], ").append(sum).append( " )" );
		
	return result.toString();
		
    }

    @Override
	public org.jdom.Element toXML() {

	org.jdom.Element constraint = new org.jdom.Element("constraint");
	constraint.setAttribute("name", id() );
	constraint.setAttribute("reference", id_sumWeight);

	HashSet<Variable> scopeVars = new HashSet<Variable>();

	scopeVars.add(sum);
	for (int i = 0; i < list.length; i++)
	    scopeVars.add(list[i]);

	constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

	StringBuffer scope = new StringBuffer();
	for (Variable var : scopeVars)
	    scope.append( var.id() ).append( " " );

	constraint.setAttribute("scope", scope.toString().trim());

	org.jdom.Element term = new org.jdom.Element("sum");
	term.setText(sum.id());
	constraint.addContent(term);

	org.jdom.Element tList = new org.jdom.Element("list");

	StringBuffer tString = new StringBuffer();
	for (int i = 0; i < list.length; i++)
	    tString.append( list[i].id() ).append( " " );

	tList.setText(tString.toString().trim());

	constraint.addContent(tList);

	org.jdom.Element wList = new org.jdom.Element("weights");

	StringBuffer wString = new StringBuffer("");
	for (int i = 0; i < w.length; i++)
	    wString.append( String.valueOf(w[i]) ).append( " " );

	wList.setText(wString.toString().trim());

	constraint.addContent(wList);

	return constraint;

    }

    @Override
	public short type() {
	return type;
    }
	
    /**
     * It creates a constraint from XCSP description.
     * @param constraint and XML element describing the constraint.
     * @param store the constraint store in which the constraint is being created.
     * @return created constraint.
     */
    static public Constraint fromXML(Element constraint, Store store) {
		
	String list = constraint.getChild("list").getText();
	String weights = constraint.getChild("weights").getText();
	String sum = constraint.getChild("sum").getText();

	Pattern pattern = Pattern.compile(" ");
	String[] varsNames = pattern.split(list);
	String[] weightsNames = pattern.split(weights);

	ArrayList<Variable> x = new ArrayList<Variable>();

	for (String n : varsNames)
	    x.add(store.findVariable(n));

	ArrayList<Integer> w = new ArrayList<Integer>();

	for (String n : weightsNames)
	    w.add(Integer.valueOf(n));

	return new SumWeight(x, w, store.findVariable(sum));
    }

    @Override
	public void increaseWeight() {
	if (increaseWeight) {
	    sum.weight++;
	    for (Variable v : list) v.weight++;
	}
    }

}
