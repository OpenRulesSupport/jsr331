/**
 *  AmongVar.java 
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

import java.util.*;
import java.util.regex.Pattern;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Interval;
import JaCoP.core.IntervalDomain;
import JaCoP.core.MutableDomain;
import JaCoP.core.MutableDomainValue;
import JaCoP.core.MutableVar;
import JaCoP.core.Store;
import JaCoP.core.TimeStamp;
import JaCoP.core.Variable;

/**
* Among constraint in its general form. It establishes the following
* relation. The given number N of X`s take values from the set
* specified by Y`s. 
* 
* This constraint significantly extends the algorithms presented in 
* the literature as it does not use the decomposition into simpler
* constraints. 
* 
* Therefore as a result, it provides stronger pruning methods without
* noticeable increase in the execution time. The large part of the 
* computation is reused across following executions of the consistency 
* function. The strength of propagation algorithm is incomporable to BC.
* 
* @author Polina Makeeva and Radoslaw Szymanek
* @version 2.4
*/

public class AmongVar extends Constraint implements Constants {
	
	/**
	 * It turns out printing debugging information.
	 */
	public static final boolean debugAll = false;
	
	
	/**
	 * Number of Among constraints created.
	 */
	public static int idNumber  = 1;
		
	//All variables attributes
	private Variable[] xVarList;
	private Variable[] yVarList;	
	private HashMap<Variable, Integer> xIndex;
	private HashMap<Variable, Integer> yIndex;
	private Variable N;
	
	//Derived variables
	private MutableVar lbS;
	private MutableVar futureLbS;
	
	private LinkedHashSet<Integer> variableQueueY = new LinkedHashSet<Integer>();
	
	//Time stamps
	private TimeStamp<Integer> lb0TS;
	private TimeStamp<Integer> ub0TS;
	
	private TimeStamp<Integer> yGrounded;
	private TimeStamp<Integer> xGrounded;
	
	
	/**
	 * It constructs an AmongVar constraint. 
	 * @param x the list of variables whose equality to other set of variables we count
	 * @param y the list of variable to which equality is counted.
	 * @param n how many variables from list x are equal to at least one variable from list y.
	 */
	public AmongVar(Variable[] x, Variable[] y, Variable n) {
	
		this.queueIndex = 1;
		
		numberId = idNumber ++;
		
		this.xVarList = new Variable[x.length];
		
		for (int i = 0; i < x.length; i++){
			xVarList[i] = x[i];
			numberArgs ++;
		}
		
		this.yVarList = new Variable[y.length];
		for (int i = 0; i < y.length; i++){
			yVarList[i] = y[i];
			numberArgs ++;
		}	
		
		this.N = n;	
	}

	@Override
	public ArrayList<Variable> arguments() {
		ArrayList<Variable> args = new ArrayList<Variable>(this.numberArgs-1);

		args.add(this.N);

        args.addAll(Arrays.asList(this.xVarList));

        args.addAll(Arrays.asList(this.yVarList));
		

		return args;
	}

	@Override
	public void removeLevel(int level) {
		this.variableQueueY.clear();
		
	}

	
	/**
	 * Is called when all y are grounded and amongForSet is equivalent to simple version of Among.
	 * @param store constraint store in which context that consistency function is being executed.
	 */
	public void consistencyForX(Store store) {
		
		Domain lbSDom = ((MutableDomainValue)lbS.value()).domain;
		
		int lb0 = lb0TS.value();
		int ub0 = ub0TS.value();
		
		Variable x, tmpX;
		
		boolean inLb = false;
		
		for (int i = lb0; i < ub0; i++) {
			x = xVarList[i];
			inLb = false;
			// watch the relation with lbS
			if (lbSDom.getSize() >0)
				if (lbSDom.contains(x.domain)) {		
					//put in the beginning  of the array
					if (i != lb0) {
						tmpX = xVarList[lb0];
						xVarList[lb0] = x;
						xVarList[i] = tmpX;
						xIndex.put(x, lb0);
						xIndex.put(tmpX, i);
					}
					lb0++;
					inLb = true;
					x.removeConstraint(this);						
				}	
			
			if (!inLb )
				if (!lbSDom.isIntersecting(x.domain)) {
					//X is not intersecting the domain of y
					//put at the end of the array
					if (i != ub0 - 1) {
						tmpX= xVarList[ub0-1];
						xVarList[ub0-1] = x;
						xVarList[i] = tmpX;
						xIndex.put(x, ub0-1);
						xIndex.put(tmpX, i);
					}
					ub0--;
					i--;
					x.removeConstraint(this);
				}
		}

		if (lb0 != lb0TS.value()) lb0TS.update(lb0);
		if (ub0 != ub0TS.value()) ub0TS.update(ub0);		
		
		if (debugAll) {
			System.out.println("-------------Consistency FOR X -------------");
			System.out.println("--LEVEL : " + store.level);
			System.out.println(this);
			System.out.println("--lbS = " +lb0);
			System.out.println("--ubS = " +ub0);
			System.out.println("------------");
		}
		
		int minN = Math.max(N.min(),lb0);
		int maxN = Math.min(N.max(), ub0);
		
		if (minN > maxN)
			throw Store.failException;
		
		N.domain.in(store.level, N, minN, maxN);
		
		if (debugAll)
			System.out.println("-- K =  " + lbSDom);
		
		if (N.domain.singleton()) {
			
			if (lb0 == N.min() && ( ub0) == N.min()) {
				this.removeConstraint();
				return;
			}
			
			if (lb0 == N.min()) {			
				for (int i = lb0; i < ub0; i++) {
					x = xVarList[i];	
						
					x.domain.in(store.level, x, x.domain.subtract(lbSDom));
					if (debugAll) System.out.println("-- " + x.id()+ " in " + x.domain);
				}
			}
		
			if ( ub0 == N.min()) {			
				for (int i = lb0; i < ub0; i++) {
					x = xVarList[i];
					x.domain.in(store.level, x, x.domain.intersect(lbSDom));
					if (debugAll)  
						System.out.println("-- " + x.id()+ " in " + x.domain);
				}				
			}
		}		
	}
	
	/**
	 * The number of x in lbsDom is equal to the number of X intersecting ubSDom.
	 * 
	 * 1) If there are not enough of y to cover future domain then fail
	 * 2) 
	 * 
	 * @param store
	 */
	public void consistencyWhen_LB0_EQ_UB0(Store store) {
		
		Domain lbSDom = ((MutableDomainValue) lbS.value()).domain;
		Domain futureDom =  ((MutableDomainValue) futureLbS.value()).domain;
		Variable y;
		int i;
		//number of grounded Y
		int yGround = yGrounded.value();
		//number of Y that can potentially cover some X
		int potentialCover = 0;
		for (i = yGround; i < yVarList.length; i++){
			y = yVarList[i];
			if (y.domain.isIntersecting(futureDom))
				potentialCover ++;
		}
		
		if (debugAll) {
			System.out.println("-------------Consistency when LB0 == UB0 -------------");
			System.out.println("--LEVEL : " + store.level);
			System.out.println("--lbSDom  = " +lbSDom);
			System.out.println("--futureDom  = " +futureDom);
			System.out.println("covered min " + yGround);
			System.out.println("left y that may play role" + potentialCover );
			System.out.println("------------");
		}
	
	
		if (potentialCover < futureDom.getSize()) {
			if (debugAll) 
				System.out.println("Fail beacuase there are not enough of y to cover x");
			throw Store.failException;
		}
			
		if (potentialCover == futureDom.getSize()) {
			if (debugAll) {
				System.out.println("if the number of y is just enough to cover future domain");
				System.out.println("than we can decrease their domain to future dom");
				System.out.println("and detauch those who are not intersecting the future dom");
			}
			
			for (i = yGround; i < yVarList.length; i++){
				y = yVarList[i];
				if (y.domain.isIntersecting(futureDom))
					y.domain.in(store.level, y, y.domain.intersect(futureDom));			
				else
					y.removeConstraint(this);
					
			}
		}
			
	}
	
	
	/**
	 * It is a function which makes Y consistent if all X's are grounded.
	 * @param store a constraint store in which context all prunings are executed.
	 */
	public void consistencyForY(Store store)
	{
	
		Domain K = new IntervalDomain();
		for (Variable x: xVarList)
			if (x.singleton())
				K = K.union(x.min());
			else {
				assert (false) : "consistencyForY is called without all X being grounded";
				return;
			}	
		
		Domain lbSDom = ((MutableDomainValue) lbS.value()).domain;
		Domain futureDomain = ((MutableDomainValue) futureLbS.value()).domain;		
		Domain U = null;
		
		if (lbSDom.getSize() > 0) {
			if (futureDomain.getSize() > 0) U = lbSDom.subtract(futureDomain);
			else U = lbSDom.clone();
		}
		else U = new IntervalDomain();
		
		if (debugAll) {
			System.out.println("-------------Consistency FOR Y -------------");
			System.out.println("--LEVEL : " + store.level);
			System.out.println(this);
			System.out.println("--x formed K = " +K);
			System.out.println("--y formed U = " +U);
			System.out.println("------------");
		}
		
		int yGr = this.yGrounded.value();
		int ub0 = this.ub0TS.value();
		Variable y;
		Variable x;
		
		//number of X that are already covered by some y
		int countCoverMin = 0;
		//Number of Y who are not playing role in covering x
		int noRoleY = 0;
		//Number of Y who might cover x that were not yet covered
		int potentialCover = 0;
		
		//Number of Y who already covering some x
		int alreadyCover = 0;
		
		//Number of disjoint Y who will cover x that were not yet covered
		int disjointCover = 0;
		
		for (int i = 0; i<ub0; i++) {
			x = this.xVarList[i];
			if (U.contains(x.value())) countCoverMin++;
		}
			
		for (int i = 0; i < yGr; i++) {
			y = yVarList[i];
			if (K.contains(y.domain))
				alreadyCover++;
			else
				noRoleY++;
		}
		
		Domain intersectK = new IntervalDomain();
		Domain disjoint = new IntervalDomain();
		for (int i = yGr; i < yVarList.length; i++) {
			y = yVarList[i];
			if (y.singleton()){
				if (K.contains(y.domain))
					alreadyCover++;
				else
					noRoleY++;		
			} else {
			
				intersectK = (y.domain.intersect(K)).subtract(U);
			
				if (intersectK.getSize()==0) 
					noRoleY++;
				else
					if (intersectK.getSize()==y.domain.getSize()) {
						potentialCover++;	
						if (!disjoint.isIntersecting(y.domain))	{
							disjointCover++;
							disjoint = disjoint.union(y.domain);
						}
					}				
					else
						potentialCover ++;		
			}
		}
		
		if (debugAll) { 
			System.out.println("--number of x already covered=       " +countCoverMin);
			System.out.println("--number of y that already cover x = " +alreadyCover);
			System.out.println("--number of no role y=               " +noRoleY);
			System.out.println("--number of potential cover y=       " +potentialCover);
			System.out.println("--min nb of y for disjoint cover=    " +disjointCover);
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}
		
		if (countCoverMin > N.max()) {
			if (debugAll) 
				System.out.println("........Fail because the number of covered X is bigger than N........");					
			throw Store.failException;
		}
		
		if ( noRoleY == (yVarList.length - alreadyCover )) {
			if (debugAll) 
				System.out.println("........N must be equal to " + countCoverMin);
			N.domain.in(store.level, N, countCoverMin, countCoverMin);
		}
		
		K = K.subtract(U);
		
		if ( (countCoverMin == N.min()) && N.singleton() ) {
			
			if (debugAll)  
				System.out.println("--K \\ U = " + K );
			
			for (int i=yGr; i < yVarList.length; i++)
			{
				y = yVarList[i];
				if (y.domain.isIntersecting(K)){
					y.domain.in(store.level, y, y.domain.subtract(K));
				}
			}
			return;
		}

		int mayLeftToCover = xVarList.length - ub0;
		for (int i = 0; i < ub0; i++) {
			x = xVarList[i];
			if (K.contains(x.min())) 
				mayLeftToCover ++;
		}

		if ( K.getSize() == mayLeftToCover) {
			N.domain.in(store.level, N,countCoverMin + disjointCover, countCoverMin+potentialCover);
		}
		
		if (N.singleton()) {
			if (potentialCover <= K.getSize() && mayLeftToCover == (N.min() - countCoverMin) && K.getSize() == mayLeftToCover) {
				for (int i = yGr; i < yVarList.length; i++) {
					y = yVarList[i];
					if (y.domain.isIntersecting(K)) {
						y.domain.in(store.level, y, K);
					}
				}
			}
			if ( potentialCover == N.min()- countCoverMin &&  K.getSize() == mayLeftToCover) 
				for (int i = yGr; i < yVarList.length; i++) {
					y = yVarList[i];
					if (y.domain.isIntersecting(K)) {
						y.domain.in(store.level, y, K);
					}
				}
		}
	}
		
	@Override
	public void consistency(Store store) {
				
		//pureUbs ubs usually equals to ubs \ lbs and is used upon calculating 
		//the lbV and ubV hashtables
		IntervalDomain pureUbs = new IntervalDomain();
		Hashtable<Integer,Integer> lbV = new Hashtable<Integer,Integer>();
		Hashtable<Integer,Integer> ubV = new Hashtable<Integer,Integer>();
		
		//Take the lbs domain computed on the previous level
		//it contain the Y values that will be or must be present in S domain
		Domain lbSDom  = ((MutableDomainValue)this.lbS.value()).domain;
		
		//Ubs domain must be recalculated on the each level because the 
		//shrinking of the Y domain will cause ubs's decrease
		Domain ubSDom  = null;
		if (lbSDom.getSize() > 0 ) 
			ubSDom = lbSDom.clone();
		else 
			ubSDom = new IntervalDomain();
		
		//Future domain contains the Y values that are not must be present in S
		//meaning that one (or more) Y must be grounded to such values
		Domain futureDom =  ((MutableDomainValue)this.futureLbS.value()).domain;
		
		//mustBeCoveredNow consists of the Y values that must be present in lbs 
		//but they were pruned out from some Y domain, and thus need a check-up
		IntervalDomain mustBeCoveredNow = new IntervalDomain();
		
		//we will count the number of new grounded Y in order to move such Y
		//in the beginning of the array and move the pointer. It helps to avoid 
		//some recalculations and give potential to fail if the number of ungrounded Y
		//is not enough to cover the future domain
		int countGY = 0;	
		Variable y;
		int lastIndex = yGrounded.value();
		IntervalDomain lbVubV = new IntervalDomain();
		
		boolean skipInitialLB0UB0calculation = false;

		boolean firstTimeWhileLoop = true;

		while (variableQueueY.size() > 0 || firstTimeWhileLoop) {
			
			//----------------------------------------------------------
			if (debugAll) {
				System.out.println("LEVEL : " + store.level);
				System.out.println(this);
			}
			//----------------------------------------------------------
					
			pureUbs.clear();
			//Construction of lbSDom
			while (variableQueueY.size() > 0) {
				for (Integer yi: this.variableQueueY) {
					y = this.yVarList[yi];
					if (y.singleton()) {
						if (debugAll) 
							System.out.println("New y " + y.id+ " was grouded to " + y.value());
						//Increase the lbSDom with grounded y
						lbSDom = lbSDom.union(y.domain);		
						if (futureDom.getSize() > 0) 
							futureDom = futureDom.subtract(y.value(), y.value());
						if  (((IntervalDomain)y.domain).previousDomain != null) {
							mustBeCoveredNow =(IntervalDomain) mustBeCoveredNow.union(((IntervalDomain)y.domain).previousDomain);
							if (! firstTimeWhileLoop) 
								pureUbs = (IntervalDomain)pureUbs.union(((IntervalDomain)y.domain).previousDomain );
						}
						countGY ++;			
						
						if (yi >= lastIndex) {
							if (yi != lastIndex) {
								int yInt = yi;
								Variable tmp = yVarList[lastIndex];
								yVarList[lastIndex] = y;
								yVarList[yInt] = tmp;
								yIndex.put(y, lastIndex);
								yIndex.put(tmp, yInt);
							}
							lastIndex ++;
						}															
					} else {
						if (! firstTimeWhileLoop)						
							if  (((IntervalDomain)y.domain).previousDomain != null)
								pureUbs = (IntervalDomain)pureUbs.union(((IntervalDomain)y.domain).previousDomain );
						if  (((IntervalDomain)y.domain).previousDomain != null)
							mustBeCoveredNow =(IntervalDomain) mustBeCoveredNow.union(((IntervalDomain)y.domain).previousDomain);
						}	
				}
				variableQueueY.clear();
				yGrounded.update(lastIndex);
				
				if (futureDom.getSize() > 0) 
					mustBeCoveredNow =(IntervalDomain) mustBeCoveredNow.intersect(futureDom);
				else 
					mustBeCoveredNow = (IntervalDomain)futureDom;
				
				//If there appeared the Y values that have a risk to stay ungrounded
				//we will count their cardinality and FAIL if its 0, ground some Y if it is 1
				if (mustBeCoveredNow.getSize() > 0) {	
					if (debugAll) 
						System.out.println("It appears that we must cover such values : "+ mustBeCoveredNow);
					int cardinalityV = 0;
					int last;
					Variable y_last;
					//Go though all the intervals of the domain
					
					Interval inv;
					for (int h = 0; h < mustBeCoveredNow.size; h++) {
						inv =  mustBeCoveredNow.intervals[h];
						//go through each value of the interval						
						for (int v = inv.min; v <= inv.max; v++) {								
						    cardinalityV =0;						   
							last=-1;
							//count the cardinality of v among Y
							for (int i = this.yGrounded.value(); i < this.yVarList.length; i++) {
								y = this.yVarList[i];
								
								if (y.singleton() && y.min() == v) {
									mustBeCoveredNow = (IntervalDomain)mustBeCoveredNow.subtract(v, v);
									cardinalityV = -1;
									break;
								}
								if (y.domain.contains(v)) {
									cardinalityV ++;
									last = i;
								}
							}
							if (cardinalityV == 0) {
								if (debugAll) 
									System.out.println("Cardinality of " +v+ " is 0 => FAIL ");
								throw Store.failException;
							}
							else if (cardinalityV == 1) {
								y_last = this.yVarList[last];
								if (debugAll) 
									System.out.println("Cardinality of " +v+ " is 1 => Groud " + y_last.id);
								
								if (last != lastIndex) {
									int yInt = last;
									Variable tmp = yVarList[lastIndex];
									yVarList[lastIndex] = y_last;
									yVarList[yInt] = tmp;
									yIndex.put(y_last, lastIndex);
									yIndex.put(tmp, yInt);
								}
								lastIndex ++; 
								y_last.domain.in(store.level, y_last, v,v);
								
								mustBeCoveredNow = (IntervalDomain)mustBeCoveredNow.subtract(v, v);
							}											
						}
					}
				}		
			}
			lbS.update(new MutableDomainValue(lbSDom));
			mustBeCoveredNow = new IntervalDomain();
			futureLbS.update(new MutableDomainValue(futureDom));
			
			if (debugAll) {
				System.out.println(countGY + " new y were grounded and " +(this.yGrounded.value()) + " in general");
				System.out.println("Future domain is " + futureDom);	
			}
			
			if ( this.yVarList.length - this.yGrounded.value() < futureDom.getSize()) {
				if (debugAll) 
					System.out.println("Fail because the number of not grounded y is not enough to cover future lbS domain");
				throw Store.failException;
			}
			if (this.yGrounded.value() == this.yVarList.length) {
				if (debugAll) 
					System.out.println("All Y were grounded, thus we can pass to simple ve rsion of Among contrians where GAC can be reached");
				consistencyForX(store);
				return;
			}
			
			for (Variable var: this.yVarList) {
				 ubSDom = (IntervalDomain)ubSDom.union(var.domain);							
			}	
			
			//----------------------------------------------------------
			if (debugAll) {
				System.out.println("lbS = " +lbSDom);
				System.out.println("ubS = " +ubSDom);
				System.out.println("--------");
			}
			//----------------------------------------------------------
			
			//Next part of consistency count occupy the X variables: count the lb0, glb0, ub0, lub0
			int lb0 = lb0TS.value();
			int ub0 = ub0TS.value();
			
			int glb0 = lb0;			
			
			// [x1, x2, x3, ... x_lb0] ..... [ x_ub0 .... x_n] 
			
			//lbSDOm and ubSDom are ready
			Variable x;
			Variable tmpX;
			if (!skipInitialLB0UB0calculation){
				for (int i = lb0; i < ub0; i++) {
					x = xVarList[i];
					//a) count the grounded x
					
					//b) watch the relation with lbS
					if (lbSDom.getSize() > 0)
						if (lbSDom.contains(x.domain)) {
							
							//put in the beginning  of the array
							if (i != lb0) {
								tmpX = xVarList[lb0];
								xVarList[lb0] = x;
								xVarList[i] = tmpX;
								xIndex.put(x, lb0);
								xIndex.put(tmpX, i);
							}
							lb0++;
							glb0 ++;
							
							x.removeConstraint(this);
							
						} else if (lbSDom.isIntersecting(x.domain)) {
								glb0 ++;						
						}	
			
									
					if (!ubSDom.isIntersecting(x.domain)) {
						//X is not intersecting the domain of y
						//put at the end of the array
						if (i != ub0 - 1) {
							tmpX= xVarList[ub0-1];
							xVarList[ub0-1] = x;
							xVarList[i] = tmpX;
							xIndex.put(x, ub0-1);
							xIndex.put(tmpX, i);
						}
						ub0--;
						i--;
						x.removeConstraint(this);
					}					
				
				}
			
				if (lb0 != lb0TS.value()) lb0TS.update(lb0);
				if (ub0 != ub0TS.value()) ub0TS.update(ub0);
			}
			
			if (this.satisfied()) return;
			
			if (debugAll) {
				System.out.println("--------");
				System.out.println("- lb0  = " + lb0);
				System.out.println("- glb0 = " + glb0);
				System.out.println("- ub0  = " + ub0);
				System.out.println(" domain of N " + N.domain + " is in [ " +  Math.max(N.min(),lb0) + ", " +  Math.min(N.max(), ub0) +" ]" );
				System.out.println("--------");
			}
					
			int minN = Math.max(N.min(),lb0);
			int maxN = Math.min(N.max(), ub0);
			
			if (minN > maxN)
				throw Store.failException;
			
			N.domain.in(store.level, N, minN, maxN);
			
			/*----------------------------------------------------------
			 Now we will enter the pruning N part
			 For each value of UBS we will calculate : 
			 lbV - lower border on N if v was included into S
			 weight - max number of X which can take value from S if v was included into S 
			 ubV - upper border on N if v as excluded from S
			 _____________________________________________________
			 lbV - lb0 gives min number of X which can take value from S if v was included into S
			
			 N must be in the Union[ (lbV - lb0 ), weight] and their combinations
			  
			
			*/
			int lbTmp =  0;
			int ubTmp =  0;
			int weight = 0;
			
			
			if (firstTimeWhileLoop){
				lbVubV = new IntervalDomain(lb0, glb0);
				lbVubV = (IntervalDomain)lbVubV.union(ub0);	
			}
			
			
			int LB = Integer.MAX_VALUE;
			int UB = Integer.MIN_VALUE;
			if (firstTimeWhileLoop) 
				pureUbs= (IntervalDomain)ubSDom.subtract(lbSDom);
			else
				pureUbs = (IntervalDomain)pureUbs.intersect(ubSDom.subtract(lbSDom));
			
			Interval inv;
			
	
			for (int h=0; h<(pureUbs).size; h++) {
				inv = (pureUbs).intervals[h];
				//for each interval of UBS
				if (inv!=null)
					//For each value of the interval
					for (int v = inv.min; v <= inv.max; v++) {
						lbTmp =lb0;
						weight =0;
						ubTmp =0;
						
						for (int i = 0; i < lb0; i++) {
							x = xVarList[i];
							if (ubSDom.subtract(v,v).isIntersecting(x.domain))
								ubTmp++;
						}
							
						
						for (int i = lb0; i < ub0; i++) {
							x = xVarList[i];
							//a) count the weight
							if (firstTimeWhileLoop)
								if (x.domain.contains(v)) weight++;
							//b)count the relation with lbS
							if (lbSDom.union(v).contains(x.domain))
								lbTmp++;
							//c)count the relation with ubs
							if (ubSDom.subtract(v,v).isIntersecting(x.domain))
								ubTmp++;
							
						}
						if (debugAll) System.out.println("--- lb[" + v + "] = " + lbTmp);
						if (debugAll) System.out.println("--- ub[" + v + "] = " + ubTmp);
						lbV.put(v,lbTmp);
						ubV.put(v,ubTmp);			
						if ( ubTmp > UB)
							UB = ubTmp;
						
						if ( lbTmp < LB)
							LB = lbTmp;
						
						if (debugAll) 
							if (firstTimeWhileLoop)
							System.out.println("--- weight[" + v + "] = " + weight);
						
						if (firstTimeWhileLoop)
							if ( weight != 0)
								if ( (!lbVubV.contains(N.domain) ) ) {
									int max = 0;
									int min = 0;
									lbTmp = lbTmp-lb0;
								
									if (lbVubV.getSize() > 0)
										for (Interval a : lbVubV.intervals)
										{
											if (a != null )
											{
												max = Math.min( weight+ a.max, ub0);
												min = Math.max( lbTmp+a.min,lb0);
										
												if ( min <= max) 
												{
													lbVubV = (IntervalDomain)lbVubV.union(min, max );									
													if (debugAll)
														if ( a != null ) 
															System.out.println(" >>>> " + a + " + [" +lbTmp + ", " +weight+  "] = [" +min + ", " + max+  " ]");
												}else
													if (debugAll)
														if ( a != null ) 
														System.out.println(" >>>> " + a + " + [" +lbTmp + ", " +weight+  "] = NOTHING");
																										
											}
										}								
								}		
					}			
			}
			
			if (debugAll) 	
				System.out.println(" Made up n domain = " + lbVubV);
	
		   if (firstTimeWhileLoop)
			   N.domain.in(store.level, N, lbVubV.intersect(N.domain));

		   else N.domain.in(store.level, N, Math.max(lb0, N.min()), Math.min(ub0, N.max()));
			   			
			boolean recalculateLB0 = false;
			boolean recalculateUB0 = false;
			
			pureUbs = (IntervalDomain)ubSDom.subtract(lbSDom);
			
			for (int h = 0; h < pureUbs.size; h++) {
				inv =  pureUbs.intervals[h];
				if (inv != null){
					
					for (int v = inv.min; v <= inv.max; v++) {
						if (debugAll) System.out.println(">>>>>>>>>>>>>>>>>>>" + v + "  ");
						if (ubV.get(v) < N.min()){
							if (debugAll) System.out.println(v + " must be be present in S");
							lbSDom = lbSDom.union(v);
							recalculateLB0 = true;
							
							int cardinalityV =0;
							int last = -1;
							Variable y_last = null;
							for (int i = this.yGrounded.value(); i < this.yVarList.length; i++) {
								y = this.yVarList[i];
								
								if (y.domain.contains(v)) {
									cardinalityV ++;
									last = i;
								}
							}
							
							
							if (cardinalityV == 1 )	{
								y_last = this.yVarList[last];
								if (!(y_last.singleton())) {
									y_last = this.yVarList[last];
									
									if (debugAll) 
										System.out.println("Only " + y_last.id + " can cover " + v + " so I ground it");
									
									mustBeCoveredNow = (IntervalDomain)mustBeCoveredNow.union(y_last.domain.subtract(v, v));
									
									int yInt = last;
									
									if (last != lastIndex) {
										Variable tmp = yVarList[lastIndex];
										yVarList[lastIndex] = y_last;
										yVarList[yInt] = tmp;
										yIndex.put(y_last, lastIndex);
										yIndex.put(tmp, yInt);
									}
									lastIndex ++; 
									
									y_last.domain.in(store.level, y_last, v,v);
									
								}
							}
							else {
								if (cardinalityV == 0) throw Store.failException; 
								futureDom = futureDom.union(v);
							}
							
							
						}
						
						if (lbV.get(v) > N.max()){
							ubSDom = ubSDom.subtract(v, v);
							recalculateUB0 = true;
						
							if (debugAll) System.out.println(v + " must be pruned out of all y");
							
							for (int i = yGrounded.value(); i < yVarList.length; i++) {
								y = yVarList[i];
								
								if (y.singleton()) { 
									if (y.value() == v) throw Store.failException;
								}
								else {
									y.domain.inComplement(store.level, y, v);
									if (y.singleton()){
										if (futureDom.getSize()>0) futureDom = futureDom.subtract(y.value(), y.value());
										lbSDom = lbSDom.union(y.value());
										recalculateLB0 = true;
										
										if (i != lastIndex)
										{
											variableQueueY.remove(i);
											int yInt = i;
											Variable tmp = yVarList[lastIndex];
											yVarList[lastIndex] = y;
											yVarList[yInt] = tmp;
											yIndex.put(y, lastIndex);
											variableQueueY.add(lastIndex);
											yIndex.put(tmp, yInt);
										}
										lastIndex ++; 
									}
								}
							}
						}				
					}
				}
			}
			
			if (debugAll)  System.out.println("Future domain is " + futureDom);
			this.futureLbS.update(new MutableDomainValue(futureDom));
			
			skipInitialLB0UB0calculation = false;
			if (recalculateLB0 || recalculateUB0)
			{
				skipInitialLB0UB0calculation = true;
				boolean inLb = false;
				for (int i = lb0; i < ub0; i++) {
					x = xVarList[i];
					inLb = false;
					
					// watch the relation with lbS
					if (lbSDom.getSize() >0)
						if (lbSDom.contains(x.domain))
						{		
							//put in the beginning  of the array
							if (i != lb0)
							{
								tmpX= this.xVarList[lb0];
								this.xVarList[lb0] = x;
								this.xVarList[i] = tmpX;
								this.xIndex.put(x, lb0);
								this.xIndex.put(tmpX, i);
								
							}
							lb0++;
							inLb = true;
							x.removeConstraint(this);						
						}	
					
					if (!inLb && recalculateUB0 )
						if (!ubSDom.isIntersecting(x.domain)){
							//X is not intersecting the domain of y
							//put at the end of the array
							if (i != ub0-1)
							{
								tmpX = xVarList[ub0-1];
								xVarList[ub0-1] = x;
								xVarList[i] = tmpX;
								xIndex.put(x, ub0-1);
								xIndex.put(tmpX, i);
							}
							ub0--;
							i--;
							x.removeConstraint(this);
						}
				}

				if (lb0 != lb0TS.value()) lb0TS.update(lb0);
				if (ub0 != ub0TS.value()) ub0TS.update(ub0);
			}
			
			lbS.update(new MutableDomainValue(lbSDom));
			
			if (debugAll)
				{
				System.out.println("lb(S) := lb(S) U {v | ub[v] < min(N) } = " +  lbSDom );
				System.out.println("ub(S) := ub(S) \\ {v | lb[v] > max(N) } = " +  ubSDom );
				System.out.println("(min(N) = max(N)) = " + N.singleton());
				System.out.println("- lb0  = " + lb0);
				System.out.println("- ub0  = " + ub0);
				System.out.println(" domain of N " + N.domain + " is in [ " +  Math.max(N.min(),lb0) + ", " +  Math.min(N.max(), ub0) +" ]" );
				
				}
			
			N.domain.in(store.level, N, Math.max(N.min(),lb0), Math.min(N.max(), ub0));
			
			if (lbSDom.getSize() > ubSDom.getSize()  || lbSDom.getSize() > yVarList.length   ) {
				if (debugAll) 
					System.out.println("........Fail because lbSDom.getSize() > ubSDom.getSize()  || lbSDom.getSize() > this.yVarList.length........");
				throw Store.failException;
			}
			
			
			if (N.singleton()){	
				if (lbSDom.getSize() > 0) {
					if (N.value() == lb0)
						for (int i = lb0; i < ub0; i++) {
							x = xVarList[i];
							x.domain.in(store.level, x, x.domain.subtract(lbSDom));
						}
				}
				if (N.value()==ub0)					
					for (int i = lb0; i < ub0; i++) {
						x = xVarList[i];
						x.domain.in(store.level, x, x.domain.intersect(ubSDom));							
					}		
			}
			
			if (xGrounded.value() == xVarList.length) {
				consistencyForY(store);
			}
			else 
				if (lb0 == ub0)
					consistencyWhen_LB0_EQ_UB0(store);
			
			if (satisfied() ) {
				removeConstraint();
				return;
			}
			
			if (debugAll) System.out.println(this);
				
			firstTimeWhileLoop = false;
		}
	}
	

	@Override
	public Element getPredicateDescriptionXML() {
		
		return null;
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {
//		 If consistency function mode
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
			return  id_amongVar + numberId;
	}

	@Override
	public void impose(Store store) {

		store.registerRemoveLevelListener(this);
		
		xIndex = new HashMap<Variable, Integer>();
		
		int i = 0;
		Variable y;
		Variable x;
		int gx = 0;
		
		
		for (i = 0; i < xVarList.length; i++) {
			x = xVarList[i];
			x.putConstraint(this);
			xIndex.put(x, i);
			if (x.singleton()) gx++;
		}
		
		yIndex = new HashMap<Variable, Integer>();
		
		for (i = 0; i < yVarList.length; i++) {
			y = yVarList[i];
			y.putConstraint(this);
			variableQueueY.add(i);
			yIndex.put(y, i);
		}
		
		N.putConstraint(this);
		
		lbS = new MutableDomain(store);
		futureLbS = new MutableDomain(store);
		
		lb0TS = new TimeStamp<Integer>(store, 0);
		ub0TS = new TimeStamp<Integer>(store, xVarList.length);
		
		xGrounded = new TimeStamp<Integer>(store, gx);
		yGrounded = new TimeStamp<Integer>(store, 0);
		
		store.addChanged(this);
		store.countConstraint();
		
		store.raiseLevelBeforeConsistency = true;
		
		
	}

	@Override
	public void queueVariable(int level, Variable V) {
		
		if (debugAll) {
			System.out.println(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");
			System.out.println("Var " + V + V.recentDomainPruning());
		}

		
		int i;
		for (i =0; i < this.yVarList.length; i++)
			if (this.yVarList[i] == V) {
				this.variableQueueY.add(i);
				return;
			}
				
		
		if (V != this.N) {
			//It can be only X
			if (V.singleton()) xGrounded.update( xGrounded.value() + 1);
		}
		
	}

	@Override
	public void removeConstraint() {
		if (debugAll) {
			System.out.println("............Finished with..............");
			System.out.println(this);
			System.out.println("..................................");
		}
		
		for (Variable var : xVarList)
			var.removeConstraint(this);
		
		for (Variable var : yVarList)
			var.removeConstraint(this);
		
		N.removeConstraint(this);
		
		this.variableQueueY.clear();
		
	}

	@Override
	public boolean satisfied() {
		
		if (N.singleton()) {
			
			int lb0 = lb0TS.value();
			int ub0 = ub0TS.value();
			
			boolean allYGrounded = (yGrounded.value() == yVarList.length);
			boolean allXGrounded = (xGrounded.value() == xVarList.length);
					
			if (allYGrounded) {
				if (N.value() == lb0 && lb0 == ub0) 
					return true;	    
			}
			
			if (allYGrounded && allXGrounded)
				assert (N.value() == lb0) : " Domain of N or value of timestamp LBoUTS was not maintenated properly";
		
		}
		return false;
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );

		for(Variable var : this.xVarList) {
			result.append("X variable ").append(var.id).append(" : ").append(var.domain);
			result.append(" 			among attached : ");
			result.append(var.domain.constraints().contains(this)).append(" \n");
		}
		
		for(Variable var : this.yVarList) {
			result.append("Y variable ").append(var.id).append(" : ").append(var.domain);
			result.append(" 			among attached : ");
			result.append(var.domain.constraints().contains(this)).append(" \n");
		}

		result.append("variable ").append(N.id).append(" : ").append( N.domain );
		result.append(" 			among attached : ");
		result.append(N.domain.constraints().contains(this)).append("\n");
			
		return result.toString();
		
	}

	@Override
	public Element toXML() {
		
		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_amongVar);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(N);
		for (int i = 0; i < xVarList.length; i++)
			scopeVars.add(xVarList[i]);
		for (int i = 0; i < yVarList.length; i++)
			scopeVars.add(yVarList[i]);

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append( var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));

		org.jdom.Element term = new org.jdom.Element("N");
		term.setText(N.id());
		constraint.addContent(term);

		org.jdom.Element xList = new org.jdom.Element("xlist");
		
		StringBuffer xString = new StringBuffer();
		for (int i = 0; i < xVarList.length - 1; i++)
			xString.append(  xVarList[i].id() ).append( " " );
		
		xString.append(xVarList[xVarList.length - 1]);
		
		
		xList.setText(xString.toString());

		constraint.addContent(xList);
		
		org.jdom.Element yList = new org.jdom.Element("ylist");
		
		StringBuffer yString = new StringBuffer();
		for (int i = 0; i < yVarList.length - 1; i++)
			yString.append(  yVarList[i].id() + " " );
		
		yString.append(yVarList[yVarList.length - 1]);
		
		yList.setText(yString.toString());

		constraint.addContent(yList);		
		
		return constraint;		
				
	}

	@Override
	public short type() {
		
		return amongVar;
	}

	/**
	 * It constructs a constraint from XML description.
	 * @param constraint it specifies an XCSP element representing a constraint.
	 * @param store constraint store in which context the constraint will be created.
	 * @return a constraint built from XCSP representation of the constraint.
	 */
	static public Constraint fromXML(Element constraint, Store store) {
		
		String xlist = constraint.getChild("xlist").getText();
		String ylist = constraint.getChild("ylist").getText();
		String N = constraint.getChild("N").getText();

		Pattern pattern = Pattern.compile(" ");
		String[] varsNames = pattern.split(xlist);

		Variable[] x = new Variable[varsNames.length];

		for (int i = 0; i < x.length; i++)
			x[i] = store.findVariable(varsNames[i]);

		pattern = Pattern.compile(" ");
		varsNames = pattern.split(ylist);

		Variable[] y = new Variable[varsNames.length];

		for (int i = 0; i < y.length; i++)
			y[i] = store.findVariable(varsNames[i]);		
		
		return new AmongVar(x, y, store.findVariable(N));
	}	

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			N.weight++;
			for (Variable v : xVarList) v.weight++;
			for (Variable v : yVarList) v.weight++;
		}
	}
	
}
