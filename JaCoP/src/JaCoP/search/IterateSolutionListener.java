/**
 *  IterateSolutionListener.java 
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

package JaCoP.search;

import java.util.ArrayList;

import JaCoP.constraints.NoGood;
import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * @bug It allows iterating through solutions, by searching them in batches. This functionality
 * is still buggy ;(. In some cases it may return the same solution multiple times, moreover
 * if the batch is relatively small (all the solution within a batch are repeated) it may never
 * finish execution. It works more often for batches of size 1 than larger. Possibly using
 * NoGood for only last search (including last solution) is not sufficient.
 *
 * 
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class IterateSolutionListener extends SimpleSolutionListener implements ExitChildListener, 
							InitializeListener, ExitListener, ConsistencyListener {

	ArrayList<ArrayList<Variable>> noGoodsVariables;

	ArrayList<ArrayList<Integer>> noGoodsValues;

	/**
	 * It is set to true if the sufficient number of solutions has been found and search
	 * is being terminated.
	 */
	public boolean searchDone = false;

	ExitChildListener[] exitChildListeners;

	InitializeListener[] initializeListeners;

	ExitListener[] exitListeners;
	
	ConsistencyListener[] consistencyListeners;

	@Override
	public boolean executeAfterSolution(Search search, SelectChoicePoint select) {
		
		searchDone = super.executeAfterSolution(search, select);
		
		if (searchDone) {
			
			noGoodsVariables = new ArrayList<ArrayList<Variable>>();
			noGoodsValues = new ArrayList<ArrayList<Integer>>();
			
		}
		
		return searchDone;
	}

	
	/**
	 * It is executed after exiting left child. Status specifies if the solution
	 * is found or not. The return parameter specifies if the search should
	 * continue according to its course or be forced to exit the parent node of
	 * the left child.
	 */

	public boolean leftChild(Variable var, int value, boolean status) {

		if (searchDone) {
		
			for (ArrayList<Variable> noGood : noGoodsVariables)
				noGood.add(var);

			for (ArrayList<Integer> noGood : noGoodsValues)
				noGood.add(value);

			if (exitChildListeners != null)
				for (int i = 0; i < exitChildListeners.length; i++)
					exitChildListeners[i].leftChild(var, value, status);

			return false;
			
		} else {
			if (exitChildListeners == null)
				return true;
			else {
				boolean code = false;
				for (int i = 0; i < exitChildListeners.length; i++)
					code |= exitChildListeners[i].leftChild(var, value, status);
				return code;
			}
		}
	}

	public boolean leftChild(PrimitiveConstraint choice, boolean status) {
		if (exitChildListeners == null)
			return true;
		else {
			boolean code = false;
			for (int i = 0; i < exitChildListeners.length; i++)
				code |= exitChildListeners[i].leftChild(choice, status);
			return code;
		}
	}

	public void rightChild(Variable var, int value, boolean status) {

		if (searchDone) {
			
			ArrayList<Variable> newNoGoodVar = new ArrayList<Variable>();
			newNoGoodVar.add(var);
			ArrayList<Integer> newNoGoodVal = new ArrayList<Integer>();
			newNoGoodVal.add(value);

			noGoodsVariables.add(newNoGoodVar);
			noGoodsValues.add(newNoGoodVal);
		}

		if (exitChildListeners != null)
			for (int i = 0; i < exitChildListeners.length; i++)
				exitChildListeners[i].rightChild(var, value, status);

	}

    public void rightChild(PrimitiveConstraint choice, boolean status) {
		if (exitChildListeners != null)
			for (int i = 0; i < exitChildListeners.length; i++)
				exitChildListeners[i].rightChild(choice, status);
		return;
	}

    /**
	 * It disallows the last solution so it is not found again.
	 * @param store
	 */
	public void disallowLastSolution(Store store) {

		if (searchDone) {
			for (int i = 0; i < noGoodsVariables.size(); i++)
				store.impose(new NoGood(noGoodsVariables.get(i), noGoodsValues
						.get(i)));

		}

	}

	public void setChildrenListeners(ExitChildListener[] children) {
		exitChildListeners = children;
	}

	public void setChildrenListeners(ExitListener[] children) {

		exitListeners = children;
	}

	public void setChildrenListeners(InitializeListener[] children) {

		initializeListeners = children;

	}

	public void setChildrenListeners(InitializeListener child) {
		initializeListeners = new InitializeListener[1];
		initializeListeners[0] = child;
	}

	public void setChildrenListeners(ExitListener child) {
		exitListeners = new ExitListener[1];
		exitListeners[0] = child;
	}

	public void setChildrenListeners(ExitChildListener child) {
		exitChildListeners = new ExitChildListener[1];
		exitChildListeners[0] = child;
	}

	@Override
	public String toString() {
		
		if (noGoodsVariables != null ) {
			StringBuffer sb = new StringBuffer(noGoodsVariables.toString());
			sb.append(noGoodsValues.toString());
			return sb.toString(); 
		}
		else return "[]";
		
	}

	ArrayList<NoGood> noGoods = new ArrayList<NoGood>();
	 		
	public void executedAtInitialize(Store store) {
		
		boolean consistent = store.consistency();
		
		//TODO, deal with below in cleaner manner.
		assert (consistent) : "Problem for which you seek solutions is inconsistent";
		
		store.setLevel(store.level + 1);
		
		if (searchDone) {
			
			for (int i = 0; i < noGoodsVariables.size(); i++)
				noGoods.add(new NoGood(noGoodsVariables.get(i), noGoodsValues
						.get(i)));
			
			if (recordSolutions)
				noGoods.add(new NoGood(vars, solutions[noSolutions - 1]));
			else
				noGoods.add(new NoGood(vars, solutions[0]));			
				
		}

		noSolutions = 0;
				
		searchDone = false;
		
	}


	public void executedAtExit(Store store, int solutionsNo) {
	
		noGoods.clear();
		
		store.removeLevel(store.level);
		store.setLevel(store.level - 1);
		
	}
	
	/**
	 * It does all the work to make itself listener of the search. It 
	 * becomes a SolutionListener, ExitChildListener, InitializeListener, 
	 * and ConsistencyListener. 
	 * @param search search to which it is supposed to listen to.
	 */
	public void hookUp(Search search) {
		
		if (search.getSolutionListener() == null ||
			search.getSolutionListener() instanceof SimpleSolutionListener )
			search.setSolutionListener( this );
		else
			search.getSolutionListener().setChildrenListeners( this );
		
		if ( search.getExitChildListener() == null)
			search.setExitChildListener( this );
		else
			search.getExitChildListener().setChildrenListeners( this );

		if ( search.getExitListener() == null)
			search.setExitListener( this );
		else
			search.getExitListener().setChildrenListeners( this );

		if ( search.getInitializeListener() == null)
			search.setInitializeListener( this );
		else
			search.getInitializeListener().setChildrenListeners( this );
		
		if ( search.getConsistencyListener() == null)
			search.setConsistencyListener( this );
		else
			search.getConsistencyListener().setChildrenListeners( this );
		
	}


	public boolean executeAfterConsistency(boolean consistent) {
	
		for (NoGood ng : noGoods)
			if (ng.notSatisfied())
				return false;
					
		return true && consistent;

	}

	
	public void setChildrenListeners(ConsistencyListener[] children) {
		consistencyListeners = children;
	}

	public void setChildrenListeners(ConsistencyListener child) {
		consistencyListeners = new ConsistencyListener[1];
		consistencyListeners[0] = child;
	}
	
}
