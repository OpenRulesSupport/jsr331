/**
 *  GCC.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2008 Jocelyne Lotfi and Radoslaw Szymanek
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import org.jdom.Element;

import JaCoP.core.BoundDomain;
import JaCoP.core.Constants;
import JaCoP.core.IntervalDomain;
import JaCoP.core.IntervalDomainValueEnumeration;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.TimeStamp;
import JaCoP.core.Variable;
import java.util.regex.Pattern;

/**
* GCC constraint counts the number of occurences of given 
* values in x variables. The counters are specified by y's.
* The occurence of all values in the domain of xs is counted. 
* 
* We would like to thank Irit Katriel for making the code of GCC in C she wrote
* available to us. 
* 
* @author Jocelyne Lotfi and Radoslaw Szymanek.
* 
* @version 2.4
*/

public class GCC extends Constraint implements Constants {

	/**
	 * @todo An improvement to increase the incrementality even further. 
	 * 
	 * 1. The first matching uses minimal values. Remember which minimal value has changed which 
	 * removed from the domain the value which was used in the matching. 
	 * Reuse from old matching 1 all values smaller than the minimal which has changed.
	 * 
	 * Similar principle applies to matching 2 (skip the positions (variables) until 
	 * the first index for which m1 did change or for which the m2 value is no longer in the
	 * domain.
	 * 
	 * 
	 * 2. Use IndexDomainView instead of local solution.
	 * 
	 * 
	 * 3. boolean variable first - is it only once in the consistency function? Then this functionality
	 * can be moved out of the while(newPropagation), if it should be executed every time consistency
	 * is executed then (it should be setup to true somewhere).
	 * 
	 * 
	 */
	
	boolean firstConsistencyCheck = true;
	boolean newProp;
	Variable v;

	static int idNumber = 1;
	
	final static short type = gcc;
	
	/**
	 * The array which stores the first computed matching, which may not take into account
	 * the lower bound of count variables.
	 */
	int [] match1;
	
	/**
	 * The array which stores the second computed matching, which may not take into account 
	 * the upper bound of count variables.
	 */
	int [] match2;
	
	/**
	 * The array which stores the third proper matching, constructed from the first one 
	 * and second one so both lower and upper bounds are respected. 
	 */
	int [] match3;
		
	int [] match1XOrder;
	int [] match2XOrder;
	int [] nbOfMatchPerY;

	int [] compOfY;

	XDomain[] xDomain;
	BoundDomain[] yDomain;	

	Variable[] xNodes;
	Variable[] yNodes;
	int xSize;
	int ySize; 

	ArrayDeque<Integer> S1;
	ArrayDeque<Component> S2;
	PriorityQueue<XDomain> pFirst, pSecond;
	PriorityQueue<Integer> pCount;
	CompareLowerBound compareLowerBound; 

	final static boolean debug = false;

	int[] domainHash;
	HashMap<Variable, Integer> pruningConsistencyEvents;

	HashMap<Variable, Integer> xNodesHash;
	HashSet<Variable> xVariableToChange;

	TimeStamp<Integer> stamp;
	int stampValue;
	int firstConsistencyLevel;

	/**It constructs global cardinality constraint.
	 * @param xNodes variables which values are counted.
	 * @param yNodes variables which count the values.
	 */
	public GCC(Variable[] xNodes, Variable[] yNodes) {

		this.queueIndex = 1;
		numberId = idNumber++;

		yNodes = removeZeroCounters(xNodes, yNodes);
		
		xSize = xNodes.length;
		ySize = yNodes.length;

		this.xNodes = new Variable[xSize];
		this.yNodes = new Variable[ySize];
		
		System.arraycopy(xNodes, 0, this.xNodes, 0, xSize);
		System.arraycopy(yNodes, 0, this.yNodes, 0, ySize);

		this.xDomain = new XDomain[xSize];
		this.yDomain = new BoundDomain[ySize];

		// rest of the init
		match1 = new int [xSize];
		match2 = new int [xSize];
		match3 = new int [xSize];
		match1XOrder = new int [xSize];
		match2XOrder = new int [xSize];

		nbOfMatchPerY = new int [ySize];
		compOfY = new int[ySize];

		S1 = new ArrayDeque<Integer>();
		S2 = new ArrayDeque<Component>();
		pFirst = new PriorityQueue<XDomain>(10, new SortPriorityMinOrder());
		pSecond = new PriorityQueue<XDomain>(10, new SortPriorityMinOrder());
		pCount = new PriorityQueue<Integer>(10, new SortPriorityMaxOrder());

		compareLowerBound = new CompareLowerBound();
		xNodesHash = new HashMap<Variable, Integer>();
		xVariableToChange = new HashSet<Variable>();

	}

	HashSet<Variable> zeroCounters;
	
	private Variable[] removeZeroCounters(Variable[] xNodes, Variable[] yNodes) {

		Variable[] result;
		
		// here I will put normalization
		IntervalDomain d = new IntervalDomain();

		for (int i = 0; i < xNodes.length; i++)
			d = (IntervalDomain)d.union(xNodes[i].domain);

		// I check the consistency of the x and y variable
		if (d.getSize() != yNodes.length &&  ( d.max() - d.min() + 1) != yNodes.length )
			// if there are more y variable than x variable there is a mistake of conseption
			// as the rest of y variables are 0 in any case. The problem is to know which y variable
			// should not be here. With normalization we assume that it is the last ones in the
			// list but it is an assumption, it's better to throw an exception there and let the
			// user determine what is correct.
			throw new JaCoPException("GCC failure : join domain of x variables doesn't cover all count variables");

		// no changes required
		if (d.getSize() == yNodes.length)
			return yNodes;
		
		// zero counters encountered.
		result = new Variable[d.getSize()];
		zeroCounters = new HashSet<Variable>();

		int i = 0;
		for (int k = d.min(); k <= d.max(); k++) 
			if (d.contains(k))
				result[i++] = yNodes[k-d.min()];
			else
				zeroCounters.add( yNodes[k-d.min()] );
			
		return result;
	}

	/**It constructs global cardinality constraint.
	 * @param xNodes variables which values are counted.
	 * @param yNodes variables which count the values.
	 */	
	public GCC(ArrayList<? extends Variable> xNodes, 
			   ArrayList<? extends Variable> yNodes) {

		this(xNodes.toArray(new Variable[xNodes.size()]),
			 yNodes.toArray(new Variable[yNodes.size()]));
		
	}	
	
	@Override
	public ArrayList<Variable> arguments() {
		
		ArrayList<Variable> variables = new ArrayList<Variable>();
		
		for (Variable x : this.xNodes) variables.add(x);
		for (Variable y : this.yNodes) variables.add(y);
		
		return variables;
		
	}

	@Override
	public void removeLevel(int level) {
		if (level == firstConsistencyLevel) 
			firstConsistencyCheck = true;
	}

	@Override
	public void consistency(Store store) {

		// the stamp is here to represent the number of x variable still
		// not singleton and that need to be pruned (the rest is set and 
		// doesn't need to be pass though the whole calculation of matching 
		// and SCC's). The same trick can not be apply to the y as the order
		// matter a lot. 

		// I take out all the xNodes that are singleton and I put them 
		// after the stamp value
		if ( firstConsistencyCheck ) {
		
			if (zeroCounters != null)
				for (Variable zeroCounter : zeroCounters)
					zeroCounter.domain.in(store.level, zeroCounter, 0, 0);
					
			int k = 0;
			
			stamp.update(xSize);
			
			while (k < stamp.value()) {
				if (xNodes[k].singleton()){
					stamp.update(stamp.value() - 1);
					putToTheEnd(xNodes, k);
				} else {
					// no incrementation if there is a modification in the
					// xNodes table. The variable in the i position is no more
					// the same and need to be check also.
					k++;
				}
			}
			firstConsistencyCheck = false;
			firstConsistencyLevel = store.level;
		}

		// no need to rerun the consistancy function as the reduction on x domains doesn't affect 
		// the matching and the y count is base on the matching and doesn't affect it. So
		// rerunning the constraint doesn't bring anything new. We can suppose that y counting 
		// achieve bound consistancy.
	
		while (store.newPropagation) {
			store.newPropagation = false;
			newProp = false;
			
			if (debug) {
				System.out.println("XNodes");
				for (int i = 0; i < xSize; i++)
					System.out.println(xNodes[i]);
				System.out.println("stamp before "+ stamp.value());
			}
						
			stampValue = stamp.value();
			
			if (debug) {
				System.out.println("stamp after "+ stampValue);
				System.out.println("XDomain");
			}
			// put in the xDomain all xNodes that are not singleton
			
			for (int i = 0; i < stampValue; i++) {
				xDomain[i].setDomain(findPosition(xNodes[i].min(), domainHash), 
								  findPosition(xNodes[i].max(), domainHash));
				
				xDomain[i].twin = xNodes[i];
				if (debug)
					System.out.println(xDomain[i]);
			}
			if (debug)
				System.out.println("YDomain");

			// put all yNodes in yDomain
			for (int i = 0; i < ySize; i++) {
				yDomain[i].setDomain(yNodes[i].min(), yNodes[i].max());

				if (debug)
					System.out.println(yDomain[i]);
			}

			if (debug) 
				System.out.println("take out singleton xNodes");

			// check all xNodes and if singleton change the yDomain value
			// to count down the xNode already link to this yNode
			for (int i = 0; i < xSize; i++) {
				if (xNodes[i].singleton()) {
					//Change, check.
					int value = findPosition(xNodes[i].value(), domainHash);
					if (yDomain[value].min > 0) {
						yDomain[value].min--;
					}
					yDomain[value].max--;
					if (yDomain[value].max < 0)
						throw Store.failException;
				}
			}

			if (debug) {
				System.out.println("pass in consistency");
				System.out.println("YDomain");
				for (int i = 0; i < ySize; i++)
					System.out.println(yDomain[i]);
			}

			sortXByDomainMin();

			FindGeneralizedMatching();
			SCCs();
			// I do the countConcistancy before the x pruning so the 
			// change in x variable doesn't affect the pruning of y variable
			countBoundConsistency(store);

			// do the pruning
			for (int j = 0; j < stampValue; j++) {

				assert((match3[j] >= 0)&& (match3[j]< ySize));
				assert((compOfY[match3[j]] >= 0) && (compOfY[match3[j]] <= ySize));

				int cutMin = xDomain[j].min;
				int cutMax = xDomain[j].max;

				if (debug)
					System.out.println("cutmax "+cutMax);

				while (compOfY[match3[j]] != compOfY[cutMin])
					cutMin++;

				while (compOfY[match3[j]] != compOfY[cutMax]) {
					cutMax--;
				}

				int id = xNodesHash.get(xDomain[j].twin);

				if (debug)
					System.out.println("do pruning ["+xNodes[id].min()+","+xNodes[id].max()+"] => ["+cutMin+","+cutMax+"]");

				xDomain[j].setDomain(cutMin, cutMax);
				v = xNodes[id];
				int hashMin = domainHash[cutMin];
				int hashMax = domainHash[cutMax];
				v.domain.in(store.level, v, hashMin, hashMax);
				if(!newProp && (v.max() != hashMax || v.min() != hashMin)){
					newProp = true;
				}
			}

			// check if the solution is still valid now the prunning done
			// useful if max bound reduction lead to a non-existing number and 
			// the prunning is so report to the next upper bound (not necessary fitting)
			for (int i = 0; i < xSize; i++) {
				if (xNodes[i].singleton()) {
					// Change, check.
					int value = findPosition(xNodes[i].value(), domainHash);
					yDomain[value].max--;
					if (yDomain[value].max < 0) {
						if (debug)
							System.out.println("failure in putting back yNodes domain");
						throw Store.failException;
					}
				}
			}
			store.newPropagation = newProp;
		}
	}

	@Override
	public Element getPredicateDescriptionXML() {
		return null;
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {
		// If consistency function mode
			if (pruningConsistencyEvents != null) {
				Integer possibleEvent = pruningConsistencyEvents.get(var);
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
			return id_GCC + numberId;
	}

	@Override
	public void impose(Store store) {

		stamp = new TimeStamp<Integer>(store, xSize);
		
		// first I will put all the xNodes in a hashTable to be able to use
		// it with the queueVariable function
		for (int i = 0; i < xSize; i++) 
			xNodesHash.put(xNodes[i], i);

		// here I will put normalization
		IntervalDomain d = new IntervalDomain();

		for (int i = 0; i < xSize; i++)
			d = (IntervalDomain)d.union(xNodes[i].domain);

		// I check the consistency of the x and y variable
		if (d.getSize() != ySize)
			// if there are more y variable than x variable there is a mistake of conseption
			// as the rest of y variables are 0 in any case. The problem is to know which y variable
			// should not be here. With normalization we assume that it is the last ones in the
			// list but it is an assumption, it's better to throw an exception there and let the
			// user determine what is correct.
			throw new JaCoPException("GCC failure : join domain of x variables doesn't cover all count variables");

		domainHash = new int[d.getSize()];
		IntervalDomainValueEnumeration venum = new IntervalDomainValueEnumeration(d);
		int i = 0;
		do {
			
			Integer j = venum.nextElement();
			domainHash[i++] = j;
			
		} while (venum.hasMoreElements());

		for (i = 0; i < xSize; i++)
			this.xDomain[i] = new XDomain(xNodes[i], findPosition(xNodes[i].min(), domainHash), 
										  findPosition(xNodes[i].max(), domainHash));

		for (i = 0; i < ySize; i++)
			this.yDomain[i] = new BoundDomain(yNodes[i].min(), yNodes[i].max());

		// add the events to the constraint
		for (i = 0 ; i < xSize; i++)
			xNodes[i].putModelConstraint(this, getConsistencyPruningEvent(xNodes[i]));

		for (i = 0; i < ySize; i++)
			yNodes[i].putModelConstraint(this, getConsistencyPruningEvent(yNodes[i]));

		store.addChanged(this);
		store.countConstraint();
	}

	@Override
	public void queueVariable(int level, Variable V) {
		if (debug)
			System.out.println("in queue variable "+V +" level "+ level);
		// if v is singleton and is an X variable
		if (V.singleton() && xNodesHash.containsKey(V)) {
			// if 
			if (xNodesHash.get(V) <= stamp.value()) {
				if (debug)
					System.out.println(" in xVariableToChange: " + V);
				stamp.update(stamp.value() - 1);
				putToTheEnd(xNodes, xNodesHash.get(V));
			}
		}
	}

	@Override
	public void removeConstraint() {
		for (Variable x : this.xNodes) x.removeConstraint(this);
		for (Variable y : this.yNodes) y.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		
		for(Variable x : xNodes){
			if(!x.singleton()){
				return false;
			}
		}
		
		for(Variable y : yNodes){
			if(!y.singleton()){
				return false;
			}
		}
		
		int count[] = new int[domainHash.length];
		
		for (Variable x : xNodes) {	
			int xValue = x.value();
			int position = 0;
			for (; position < count.length && domainHash[position] != xValue; position++);
			assert(position < count.length);
			count[position]++;
		}
		
		for (int i = 0; i < yNodes.length; i++)
			if (yNodes[i].value() != count[i])
				return false;
		
		return true;
	}

	@Override
	public String toString() {

		StringBuffer toString = new StringBuffer( id() );
		
		toString.append( " : GCC ([" );
		toString.append("assignement variables : ");
		for (int i = 0; i < xSize-1; i++)
			toString.append(xNodes[i].toString()).append( ", ");
		toString.append( xNodes[xSize - 1].toString() );
		toString.append( " count variables : " );
		for (int j = 0; j < ySize-1; j++)
			toString.append( yNodes[j].toString() ).append( ", " );
		toString.append( yNodes[ySize - 1].toString() ).append( "])" );
		
		return toString.toString();
		
	}

	@Override
	public Element toXML() {
		
		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_GCC);
		constraint.setAttribute("arity", String.valueOf(xNodes.length + yNodes.length));

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		for (int i = 0; i < xNodes.length; i++)
			scopeVars.add(xNodes[i]);
		for (int i = 0; i < yNodes.length; i++)
			scopeVars.add(yNodes[i]);


		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	
				
		org.jdom.Element xList = new org.jdom.Element("xlist");

		StringBuffer xString = new StringBuffer("");
		for (int i = 0; i < xNodes.length; i++)
			xString.append( xNodes[i].id() ).append( " " );

		xList.setText(xString.toString().trim());

		org.jdom.Element yList = new org.jdom.Element("ylist");

		StringBuffer dString = new StringBuffer("");
		for (int i = 0; i < yNodes.length; i++)
			dString.append( yNodes[i].id() ).append( " " );

		yList.setText(dString.toString().trim());

		constraint.addContent(xList);
		constraint.addContent(yList);

		return constraint;		
	}

	/**
	 * It constructs a constraint from XCSP description.
	 * @param constraint an XML element describing the constraint.
	 * @param store the constraint store in which context the constraint is created.
	 * @return created constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		
		String xList = constraint.getChild("xlist").getText();
		String yList = constraint.getChild("ylist").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] xVarsNames = pattern.split(xList);
		String[] yVarsNames = pattern.split(yList);

		ArrayList<Variable> x = new ArrayList<Variable>();
		ArrayList<Variable> y = new ArrayList<Variable>();

		for (String n : xVarsNames)
			x.add(store.findVariable(n));

		for (String n : yVarsNames)
			y.add(store.findVariable(n));
		
		return new GCC(x, y);

	}	
	
	@Override
	public short type() {
		return type;
	}

	// private methods

	//-----------------------FIND_GENERALIZED_MATCHING--------------------------------//
	private void FindGeneralizedMatching(){

		Arrays.fill(nbOfMatchPerY, 0);

		if (debug) {
			System.out.println("XDomain");
			for (int i = 0; i < stampValue; i++)
				System.out.println(i + " [" + xDomain[i].min+"-"+ xDomain[i].max +"]");
		}
		// first pass
		firstPass();

		// check we are in the good ranges for match1 and match1XOrder
		assert( checkFirstPass() );

		secondPass();

		assert ( checkSecondPass() );
		
		thirdPass();

		assert ( checkThirdPass() );
		
	}
	
	private boolean checkFirstPass() {
		
		for (int j = 0; j < stampValue ; j++) {
			assert(match1[j] >= 0);
			assert(match1[j] < ySize);
			assert(match1XOrder[j] >= 0);
			assert(match1XOrder[j] < stampValue);
		}
		
		return true;
	} 
	
	private boolean checkSecondPass() {
		
		for (int j = 1; j < stampValue; j++)
			assert(match2[match2XOrder[j]] >= match2[match2XOrder[j-1]]);
		
		return true;
	}
	
	private boolean checkThirdPass() {
		
		for (int j = 0; j < stampValue; j++) {
			assert(xDomain[j].min <= match3[j]);
			assert(xDomain[j].max >= match3[j]);
		}
		
		return true;
	}
	
	private void firstPass() {
		
		pFirst.clear();
		int j = 0;
		int xIndex = 0;
		int maxY;
		int match1XOrderIndex = 0;
		int top;

		for (int i = 0; i < ySize; i++){
			// first we add all the x which min domain is y
			while ((xIndex < stampValue) && xDomain[xIndex].min == i) {
				// add a new element with index to get it back after the good element 
				// and the max of the domain of xNode to sort them.
				xDomain[xIndex].index = xIndex;
				pFirst.add(xDomain[xIndex]);
				xIndex++;
			}
			int u = 0;
			// second we add the maximum of these xs possible to the current y
			maxY = yDomain[i].max;
			while (!pFirst.isEmpty() && u < maxY) {
				top = (pFirst.remove()).index; // index of the first element of pFirst
				match1[top] = i;
				u++;
				// match1XOrder gives the order in which xs where in the priority queue 
				// that sorted them by domain max by group of domain min.
				// That is there are first sorted by domain min and after by domain max.
				match1XOrder[match1XOrderIndex] = top;
				match1XOrderIndex++;

				if (xDomain[top].max < i) {
					if (debug)
						System.out.println("failure first pass");

					throw Store.failException;
					// it was checked that the min == i so max cannot be under min. Well, yes there are cases
					// where it's useful.
				}
				j++;
			}		
		}

		// do we have to check that the queue is empty ? If not it means that an x was not paired.
		// I add the test on the queue. It is possible if the maxY = 0 and is the last one visited
		// otherwise the element in the queue can be used by the next yNode.
		if (!pFirst.isEmpty()) {
		
			if (debug)
				System.out.println("failure the queue is not empty");

			throw Store.failException;
			
		}
		
		if (debug) {
		
			System.out.print("match1Xorder : ");
			for (int i = 0; i < match1XOrder.length; i++)
				System.out.print(match1XOrder[i] +" ");

			System.out.println("");
			System.out.print("match1 : ");

			for(int i = 0; i < match1.length; i++)
				System.out.print(match1[i] +" ");

			System.out.println("");
		}
		
	}
	
	private void secondPass(){

		pSecond.clear();
		int j = 0;
		int top;
		int xIndex = 0;
		int minY;
		int match2XOrderIndex = 0;
		int order;

		for (int i = 0; i < ySize; i++) {
			// I should iterate on the match1XOrder instead of the normal order
			// we take all the xs that where matched with yi in the first pass. 
			while ((xIndex < stampValue) && (match1[match1XOrder[xIndex]] == i)) {
				order = match1XOrder[xIndex];
				xDomain[order].index = order;
				pSecond.add(xDomain[order]);
				xIndex++;
			}
			minY = yDomain[i].min;
			for (int l = 0; l < minY; l++) {
				if (pSecond.isEmpty()) {
					// failure need to be expressed
					if (debug)
						System.out.println("failure second pass");

					throw Store.failException;
				}
				top = pSecond.remove().index;
				//change, check.
				match2[top] = i;

				match2XOrder[match2XOrderIndex] = top;
				match2XOrderIndex++;
				nbOfMatchPerY[i]++;
				j++;
			}
			while (!pSecond.isEmpty() && ((pSecond.element().max) < i+1)){
				top = pSecond.remove().index;
				// change, check.
				match2[top] = i;
				match2XOrder[match2XOrderIndex] = top;
				match2XOrderIndex++;
				nbOfMatchPerY[i]++;
				j++;
			}
		}
				
		
		if (debug) {
			
			System.out.print("match2Xorder : ");
			
			for(int i = 0; i < match2XOrder.length; i++)
				System.out.print(match2XOrder[i] +" ");

			System.out.println("");

			System.out.print("match2 : ");
			for (int i = 0; i < match2.length; i++)
				System.out.print(match2[i] +" ");

			System.out.println("");
		}
		
	}

	private void thirdPass(){

		int xIndex = stampValue - 1;
		int e;
		int x = 0;
				
		System.arraycopy(match2, 0, match3, 0, stampValue);
		
		for (int i = ySize - 1 ; i >= 0; i--) {
			while ((xIndex >= 0) && (match2[match2XOrder[xIndex]] > i))
				xIndex--;

			e = nbOfMatchPerY[i] - yDomain[i].max; // excess of y mates
			while (e > 0) {
				
				assert(match2[match2XOrder[xIndex]] == i);

				while (xIndex >= 0) {
					x = match2XOrder[xIndex];
					if (match1[x] == i)
						xIndex--;
					else
						break;
				}

				assert(match1[x] < i);
				assert(match2[x] == i);
				
				match3[x] = match1[x];
				nbOfMatchPerY[i]--;
				nbOfMatchPerY[match1[x]]++;
				xIndex--;
				e--;
			}
		}
		if (debug) {
			System.out.print("match3 : ");
			for (int i = 0; i < match3.length; i++)
				System.out.print(match3[i] +" ");
			System.out.println("");
		}
	}

	//--------------------------------SCCs-------------------------------------//

	private void SCCs(){

		int sccNb, C;
		int maxYReachedFromS, maxYReachesS;
		int minYReachedFromS, minYReachesS;
		int [] compReachesLeft = new int [ySize];
		int [] compReachesRight = new int [ySize];
		int [] yReachesLeft = new int [ySize];
		int [] yReachesRight = new int [ySize];

		for (int i = 0; i < ySize; i++)
			compOfY[i] = i;

		sccNb = SCCsWithoutS(compReachesLeft, compReachesRight, yReachesLeft, yReachesRight);
		// now compReaches(Left, Right) and compOfY contain the left and right most y per comp and to which comp a y belong
		if (debug) {
			System.out.println("sccNb : "+ sccNb);
			System.out.println("compReachesLeft ");
			for (int i = 0; i < compReachesLeft.length; i++)
				System.out.print(compReachesLeft[i] +" ");

			System.out.println("");
			System.out.println("compReachesRight ");
			for (int i = 0; i < compReachesRight.length; i++)
				System.out.print(compReachesRight[i] +" ");

			System.out.println("");
			System.out.println("compOfY ");
			for (int i = 0; i < compOfY.length; i++)
				System.out.print(compOfY[i] +" ");

			System.out.println("");
		}
		boolean [] reachedFromS = new boolean [sccNb];
		boolean [] reachesS = new boolean [sccNb];

		// reachedFromS and reachesS need to contain only value false. 
		// by default satisfied by Java, therefore the two lines below are not needed.
		// Arrays.fill(reachedFromS, false);
		// Arrays.fill(reachesS, false);

		// init reachedFromS and reachesS
		int comp;
		for (int i = 0; i < ySize; i++) {
			comp = compOfY[i];
			assert(comp >= 0);
			assert(comp <= sccNb);
			
			// if there are stictly more match to y than the minimum required
			// an edge exist from s to y
			if (yDomain[i].min < nbOfMatchPerY[i])
				reachedFromS[comp] = true;

			// if there are strictly less match to y than the maximum possible
			// an edge exist from y to s
			if(yDomain[i].max > nbOfMatchPerY[i])
				reachesS[comp] = true;
		}

		maxYReachedFromS = -1;
		maxYReachesS = -1;

		for (int i = 0; i < ySize; i++) {
			C = compOfY[i];
			
			assert(C >= 0);
			assert(C <= sccNb);
			
			// if the max y that can be reached is greater than the current y,
			// that the comp it belongs to can be reached from s.
			if (maxYReachedFromS >= i)
				reachedFromS[C] = true;

			// if it's reached we can extand the max y reached to the compReachesRight of the component
			if (reachedFromS[C])
				maxYReachedFromS = Math.max(maxYReachedFromS, compReachesRight[C]);

			// same in the other way : if the ReachesLeft of the comp is under the max y that 
			// reaches S this comp reaches S
			if (compReachesLeft[C] <= maxYReachesS)
				reachesS[C] = true;

			// if it's reachesS we can extand the max Y that reaches it to the current y.
			if (reachesS[C])
				maxYReachesS = Math.max(maxYReachesS, i);

		}

		// same as before but for minimum

		minYReachedFromS = ySize;
		minYReachesS = ySize;

		for (int i = ySize-1; i >= 0; i--) {
			C = compOfY[i];
			assert(C >= 0);
			assert(C <= sccNb);
			if (minYReachedFromS <= i)
				reachedFromS[C] = true;

			if (reachedFromS[C])
				minYReachedFromS = Math.min(minYReachedFromS, compReachesLeft[C]);

			if (compReachesRight[C] >= minYReachesS)
				reachesS[C] = true;

			if (reachesS[C])
				minYReachesS = Math.min(minYReachesS, i);

		}

		// merge all comp that are strongly connected through s and 
		// give this new comp a new number
		
		for (int i = 0; i < ySize; i++)
			
			if (reachesS[compOfY[i]] && reachedFromS[compOfY[i]])
				compOfY[i] = sccNb;

		if (debug) {
			System.out.println("compOfY after S ");
			for (int i = 0; i < compOfY.length; i++)
				System.out.print(compOfY[i] +" ");

			System.out.println("");
		}
	}

	private int SCCsWithoutS(int[] compReachesLeft, int[] compReachesRight, int[] yReachesLeft, int[] yReachesRight){

		Component C, C1;
		int sccNb = 0;
		S1.clear();
		S2.clear();

		ReachedFromY(yReachesLeft, yReachesRight);

		// init all componant as containing only one y and set these component
		// reachesLeft and reachesRight to y reachesLeft and Right
		for (int y = 0; y < ySize; y++) {
			compReachesLeft[y] = yReachesLeft[y];
			compReachesRight[y] = yReachesRight[y];
		}

		for (int y = 0; y < ySize; y++) {
			// set comp to (root, rightmostY, maxX)
			C = new Component(y, y, yReachesRight[y]);

			if (S2.isEmpty()) {
				S1.push(y);
				S2.push(C);
				continue;
			}

			// once S2 not empty
			// this first part treat the case we have a new component.
			// (c1.max < C.root)

			while ((!S2.isEmpty()) && ((S2.peek().maxX < C.root))) {
				compReachesLeft[sccNb] = ySize;
				compReachesRight[sccNb] = -1;

				assert(!S1.isEmpty());

				C1 = S2.pop();
				while (!S1.isEmpty() && S1.peek() >= C1.root && S1.peek() <= C1.rightmostY) {
					int popY;
					assert(!S1.isEmpty());
					popY = S1.pop();
					compOfY[popY] = sccNb;
					compReachesLeft[sccNb] = Math.min(compReachesLeft[sccNb], yReachesLeft[popY]);
					compReachesRight[sccNb] = Math.max(compReachesRight[sccNb], yReachesRight[popY]);
				}
				sccNb++;
			}
			
			assert (S2.isEmpty() || S2.peek().maxX >= C.root);

			// this second part treat the case the new c1 is in fact attainable by the current component

			while (!S2.isEmpty() && yReachesLeft[y] <= S2.peek().rightmostY) {
				assert(!S2.isEmpty());
				C1 = S2.pop();
				C.maxX = Math.max(C.maxX, C1.maxX);
				C.root = C1.root; // as they are taken in order from left to right c1.root is always < C.root
				C.rightmostY = y; // same remark
			}
			
			assert(S2.isEmpty() || ((yReachesLeft[y] > S2.peek().rightmostY) && (S2.peek().maxX >= C.root)));

			S1.push(y);
			S2.push(C);
		} // end for


		// for every component still on the pile update compOfY, compReachesLeft and Right, and sccNb
		while (!S2.isEmpty()) {
			assert(!S1.isEmpty());
			C = S2.pop();
			compReachesLeft[sccNb] = ySize;
			compReachesRight[sccNb] = -1;

			while (!S1.isEmpty() && S1.peek() >= C.root && S1.peek() <= C.rightmostY) {
				int y;
				assert(!S1.isEmpty());
				y = S1.pop();
				compOfY[y] = sccNb;
				compReachesLeft[sccNb] = Math.min(compReachesLeft[sccNb], yReachesLeft[y]);
				compReachesRight[sccNb] = Math.max(compReachesRight[sccNb], yReachesRight[y]);
			}
			sccNb++;
		}
		
		assert(S1.isEmpty() && S2.isEmpty());
		return sccNb;
	}

	private void ReachedFromY(int[] yReachesLeft, int[] yReachesRight) {


		for (int i = 0; i < ySize; i++) {
			yReachesLeft[i] = i;
			yReachesRight[i] = i;
		}

		int i; 
		// we check what is the minimum ymin and the maximum ymax reachable by y. 
		// For that we check every x linked to y to keep the minimal and maximal domain 
		// bondaries of these xs. 
		for (int j = 0; j < stampValue; j++) {
			i = match3[j];
			assert(i >= 0);
			assert(i < ySize);
			yReachesLeft[i] = Math.min(yReachesLeft[i], xDomain[j].min);
			yReachesRight[i] = Math.max(yReachesRight[i], xDomain[j].max);
		}
		if (debug) {
			System.out.println("yReachesLeft ");
			for(i = 0; i < yReachesLeft.length; i++)
				System.out.print(yReachesLeft[i] +" ");

			System.out.println("");
			System.out.println("yReachesRight ");
			for(i = 0; i < yReachesRight.length; i++)
				System.out.print(yReachesRight[i] +" ");

			System.out.println("");
		}
	}

	//------------------------USED_FOR_REDUCING_DOMAIN--------------------------//


	private void putToTheEnd(Variable[] list, int element ){
		// I swap the element with the last one before the stamp 
		// which have nothing to do in the no-more-seen variables
		Variable v1 = list[element];
		int stampValue = stamp.value();
		list[element] = list[stampValue];
		// update the index of the moved element which was behind the stamp value
		xNodesHash.put(list[stampValue], element);
		// and update the one put to the end
		xNodesHash.put(v1, stampValue);
		list[stampValue] = v1;
	}

	//---------------------------COUNT_BOUND_CONCISTENCY-----------------------//

	private void countBoundConsistency(Store store){
		int [] max_u = new int [ySize];
		Arrays.fill(max_u, ySize-1);

		int [] min_l = new int [ySize];
		Arrays.fill(min_l, 0);

		upperCount(max_u);
		lowerCount(min_l);

		if (debug) {
			System.out.println("max_u ");
			for (int i = 0; i < max_u.length; i++)
				System.out.print(max_u[i] +" ");

			System.out.println("");
			System.out.println("min_l ");
			for(int i = 0; i < min_l.length; i++)
				System.out.print(min_l[i] +" ");

			System.out.println("");
		}
		// do the pruning of the domain
		for (int i = 0; i < ySize; i++) {
			if (debug)
				System.out.println("do pruning ["+yNodes[i].min()+","+yNodes[i].max()+"] => ["+min_l[i]+","+max_u[i]+"]");

			if (yDomain[i].max != max_u[i] || yDomain[i].min != min_l[i])
				yDomain[i].setDomain(min_l[i], max_u[i]);
		}
		// add the rest of nodes not treated in this pass that was already singleton
		if (debug) 
			System.out.println("increase yDomain with xNodes singleton");

		for (int i = 0; i < xSize; i++) {
			if (xNodes[i].singleton()) {
				//Change, check.
				int value = findPosition(xNodes[i].value(), domainHash);
				yDomain[value].max++;
				yDomain[value].min++;
			}
		}
		if (debug)
			System.out.println("set yNodes");

		for (int i = 0; i < ySize; i++){
			yNodes[i].domain.in(store.level, yNodes[i], yDomain[i].min, yDomain[i].max);
			if (!newProp && (yNodes[i].max() != yDomain[i].max || yNodes[i].min() != yDomain[i].min)){
				newProp = true;

			}
		}
	}

	private void upperCount(int [] max_u){

		int xIndex, x;
		pCount.clear();
		xIndex = stampValue-1;
		for (int i = ySize-1; i >= 0 ; i--) {
			while (xIndex >= 0) {
				x = match2XOrder[xIndex];
				if (match2[x] == i) {
					pCount.add(match1[x]);
					xIndex--;
				}else
					break;
			}
			max_u[i] = Math.min(yDomain[i].max, pCount.size());
			for (int l = 0; l < yDomain[i].min; l++) {
				assert(!pCount.isEmpty());
				pCount.remove();
			}

			// well see how it works for the second part of the condition
			while (!pCount.isEmpty() && (pCount.peek() == i)) {
				pCount.remove();
			}
		}
	}
	private void lowerCount(int [] min_l){
		int xIndex, count, x;
		pCount.clear();
		xIndex = stampValue-1;
		for (int i = ySize-1; i >= 0; i--) {
			count = 0;
			while (xIndex >= 0) {
				x = match2XOrder[xIndex];
				if (match2[x] == i) {
					pCount.add(match1[x]);
					xIndex--;
				}else
					break;

			}
			
			for (int l = 0; l < yDomain[i].min; l++) {
				assert(!pCount.isEmpty());
				pCount.remove();
				count++;
			}
			
			while ((!pCount.isEmpty()) && (pCount.peek() == i)){
				pCount.remove();
				count++;
			}
			
			min_l[i] = count;
			while ((!pCount.isEmpty()) && (count < yDomain[i].max)){
				pCount.remove();
				count++;
			}
		}
	}

//	----------------------------SORT_AND_COMPARATOR--------------------------//

	private void sortXByDomainMin() {
		// I need to sort only the part concern, otherwise old values still after
		// the stamp value will interfer with the sorting
		Arrays.sort(xDomain, 0, stampValue, compareLowerBound);
	}

	// used in sortByDomainMin snd version
	private class CompareLowerBound implements Comparator<XDomain> {

		public int compare(XDomain o1, XDomain o2) {
			if (o1.min < o2.min)
				return -1;
			else
				if (o1.min > o2.min)
					return 1;				

			return 0;
		}
	}
	private class SortPriorityMinOrder implements Comparator<XDomain> {

		public int compare(XDomain o1, XDomain o2) {
			if (o1.max < o2.max)
				return -1;
			else
				if(o1.max > o2.max)
					return 1;

			return 0;
		}
	}

	private class SortPriorityMaxOrder implements Comparator<Integer> {

		public int compare(Integer e1, Integer e2) {
			return - e1.compareTo(e2);
		}
	}

	//-----------------------INNER CLASSES-----------------------------------//
	private class Component {

		int root;
		int rightmostY;
		int maxX;

		public Component(int root, int rightmostY, int maxX) {
			this.root = root;
			this.rightmostY = rightmostY;
			this.maxX = maxX;
		}
	}

	private class XDomain extends BoundDomain {
		Variable twin;
		int index;

		public XDomain(Variable twin, int min, int max) {
			super(min, max);
			this.twin = twin;
		}
	}

	@Override
	public void increaseWeight() {
		for (Variable x : xNodes) x.weight++;
		for (Variable y : yNodes) y.weight++;
	}
	
	protected int findPosition(int value, int[] values) {

		int left = 0;
		int right = values.length - 1;

		int position = (left + right) >> 1;

		if (debug) {
			System.out.println("Looking for " + value);
			for (int v : values)
				System.out.print("val " + v);
			System.out.println("");
		}

		while (!(left + 1 >= right)) {

			if (debug)
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
}
