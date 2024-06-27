/**
 *  Solver.java 
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

package javax.constraints.impl.search;

import java.util.Vector;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemState;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.ValueSelector;
import javax.constraints.Var;
import javax.constraints.VarSelector;
import javax.constraints.VarSet;
import javax.constraints.ValueSelectorType;
import javax.constraints.VarSelectorType;

import JaCoP.core.Domain;
import JaCoP.core.IntVar;
import JaCoP.core.Store;
import JaCoP.search.ComparatorVariable;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.Indomain;
import JaCoP.search.IndomainMax;
import JaCoP.search.IndomainMin;
import JaCoP.search.MaxRegret;
import JaCoP.search.MostConstrainedStatic;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;
import JaCoP.search.SmallestDomain;
import JaCoP.search.SmallestMin;
import JaCoP.search.SolutionListener;

/**
 * @author Radoslaw Szymanek
 *
 */

public class Solver extends AbstractSolver {

	DepthFirstSearch<IntVar> search;
	Store store;
	
	public Solver(Problem problem, Store store) {
		super(problem);
		
		search = new DepthFirstSearch<IntVar>();
		this.store = store;

	}

	public Solver(Problem problem) {
		super(problem);
		
		search = new DepthFirstSearch<IntVar>();
		this.store = ((javax.constraints.impl.Problem)problem).store;

	}

	
	@Override
	public boolean applySolution(Solution solution) {
		
		assert false;
		return false;

	}

	@Override
	public Solution findOptimalSolution(Objective objective, Var objectiveVar) {
		return findOptimalSolution(objective, objectiveVar, ProblemState.RESTORE);
	}
	
	public Solution findOptimalSolution(Objective objective, Var objectiveVar, ProblemState restoreOrNot) {
		
		if (restoreOrNot == ProblemState.RESTORE) {
			search.setAssignSolution(false);
		}
		else {
			search.setAssignSolution(true);			
		}
		
		IntVar costVar;
		
		if (objective == Objective.MINIMIZE)
			costVar = (IntVar)objectiveVar.getImpl();
		else {
			Var negation = objectiveVar.multiply(-1);
			costVar = (IntVar)negation.getImpl();
		}
			
		int i = 0;
		Vector<SearchStrategy> basicSearches = this.getSearchStrategies();
		DepthFirstSearch<IntVar> current = search;
		current.setPrintInfo(false);
		SelectChoicePoint<IntVar> masterSelect = null;

		if (basicSearches.size() == 0) {
			basicSearches.add( this.newSearchStrategy() );
		}

		while (i < basicSearches.size()) {

			current.setCostVar( costVar );
			
			SearchStrategy next = basicSearches.get(i);
			Var[] vars = next.getVars();
			VarSelector varSelector = next.getVarSelector();
			ValueSelector valSelector = next.getValueSelector();
		
			VarSelectorType varSelectorType;
			
			if (varSelector != null ) varSelectorType = varSelector.getType();
			else {
				
				if (next instanceof SearchMethod)
					varSelectorType = ((SearchMethod)next).varSelector;
				else
					varSelectorType = VarSelectorType.MIN_DOMAIN;
				
			}
			
			ComparatorVariable<IntVar> varFirstComparator = null;
			ComparatorVariable<IntVar> varSecondComparator;
			
			switch (varSelectorType) {
			
			case MAX_REGRET : varFirstComparator = new MaxRegret<IntVar>(); break;
			
			case MIN_DOMAIN : varFirstComparator = new SmallestDomain<IntVar>(); break;
								
			case MIN_DOMAIN_MAX_DEGREE : varFirstComparator = new SmallestDomain<IntVar>();
										 varSecondComparator = new MostConstrainedStatic<IntVar>();
										 break;
										 
			case MIN_VALUE : varFirstComparator = new SmallestMin<IntVar>(); break;
				
			case MIN_DOMAIN_MIN_VALUE : varFirstComparator = new SmallestDomain<IntVar>();
			 varSecondComparator = new SmallestMin<IntVar>();
			 break;
			
			}
			
			ValueSelectorType valueSelectorType = valSelector.getType();
			
			Indomain<IntVar> result = new IndomainMin<IntVar>();
			
			switch (valueSelectorType) {
			
				case MAX : result = new IndomainMax<IntVar>(); break;
				
				case MIN : result = new IndomainMin<IntVar>(); break;

			}
			
			 IntVar[] intVars = new IntVar[vars.length];
			 for (int j = 0; j < intVars.length; j++) {
				 intVars[j] = (IntVar)vars[j].getImpl();
			 }

			 SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(intVars, varFirstComparator, result);

			 if (i == 0)
				 masterSelect = select;

			if (i > 0 ) {
				 
				 DepthFirstSearch<IntVar> nextSearch = new DepthFirstSearch<IntVar>();
				 
				 nextSearch.setMasterSearch(current);
				 nextSearch.setSelectChoicePoint(select);
				 
				 current.addChildSearch(nextSearch);
				 
				 current = nextSearch;
				 
			 }	 
			
			i++;
			
			current.setPrintInfo(false);

		}

		boolean result = search.labeling(store, masterSelect, costVar);		

		if (result)
			return new javax.constraints.impl.search.Sol(getProblem(), search, store, result);
		else
			return null;

		
	}

	
	@Override
	public Solution findSolution(ProblemState restoreOrNot) {

		if (restoreOrNot == ProblemState.RESTORE) {
			search.setAssignSolution(false);
		}
		else {
			search.setAssignSolution(true);			
		}
		
		int i = 0;
		Vector<SearchStrategy> basicSearches = this.getSearchStrategies();
		DepthFirstSearch<IntVar> current = search;
		current.setPrintInfo(false);
		
		SelectChoicePoint<IntVar> masterSelect = null;
		
		if (basicSearches.size() == 0) {
			basicSearches.add( this.newSearchStrategy() );
		}

		while (i < basicSearches.size()) {
		
			SearchStrategy next = basicSearches.get(i);
			Var[] vars = next.getVars();
			VarSelector varSelector = next.getVarSelector();
			ValueSelector valSelector = next.getValueSelector();
		
			VarSelectorType varSelectorType;
			
			if (varSelector != null ) varSelectorType = varSelector.getType();
			else {
				
				if (next instanceof SearchMethod)
					varSelectorType = ((SearchMethod)next).varSelector;
				else
					varSelectorType = VarSelectorType.MIN_DOMAIN;
				
			}
			ComparatorVariable<IntVar> varFirstComparator = null;
			ComparatorVariable<IntVar> varSecondComparator = null;
			
			switch (varSelectorType) {
			
			case MAX_REGRET : varFirstComparator = new MaxRegret<IntVar>(); break;
			
			case MIN_DOMAIN : varFirstComparator = new SmallestDomain<IntVar>(); break;
								
			case MIN_DOMAIN_MAX_DEGREE : varFirstComparator = new SmallestDomain<IntVar>();
										 varSecondComparator = new MostConstrainedStatic<IntVar>();
										 break;
										 
			case MIN_VALUE : varFirstComparator = new SmallestMin<IntVar>(); break;
				
			case MIN_DOMAIN_MIN_VALUE : varFirstComparator = new SmallestDomain<IntVar>();
			 varSecondComparator = new SmallestMin<IntVar>();
			 break;
			
			}
			
			ValueSelectorType valueSelectorType;
			
			
			if (valSelector != null) 
				valueSelectorType = valSelector.getType();
			else 
				valueSelectorType = ((SearchMethod)next).valSelector;
			
			Indomain<IntVar> result = new IndomainMin<IntVar>();
			
			switch (valueSelectorType) {
			
				case MAX : result = new IndomainMax<IntVar>(); break;
				
				case MIN : result = new IndomainMin<IntVar>(); break;

			}
			
			 IntVar[] intVars = new IntVar[vars.length];
			 for (int j = 0; j < intVars.length; j++) {
				 intVars[j] = (IntVar)vars[j].getImpl();
			 }

			 SelectChoicePoint<IntVar> select;
			 
			 if (varSecondComparator == null) select = new SimpleSelect<IntVar>(intVars, varFirstComparator, result);
			 else 
				 select = new SimpleSelect<IntVar>(intVars, varFirstComparator, varSecondComparator, result);
			 	 

			 if (i == 0)
				 masterSelect = select;

			if (i > 0 ) {
				 
				 DepthFirstSearch<IntVar> nextSearch = new DepthFirstSearch<IntVar>();
				 
				 nextSearch.setMasterSearch(current);
				 nextSearch.setSelectChoicePoint(select);
				 
				 current.addChildSearch(nextSearch);
				 
				 current = nextSearch;
				 
			 }	 
			
			i++;

			current.setPrintInfo(false);
			
		}

		boolean result = search.labeling(store, masterSelect);		

		if (result)
			return new javax.constraints.impl.search.Sol(getProblem(), search, store, result);
		else
			return null;
		
	}

	@Override
	public void setMaxNumberOfSolutions(int maxNumberOfSolutions) {
		this.maxNumberOfSolutions = maxNumberOfSolutions;
		search.getSolutionListener().setSolutionLimit(maxNumberOfSolutions);
	}

	@Override
	public Solution[] findAllSolutions() {
		
		search.setAssignSolution(false);
		
		int i = 0;
		Vector<SearchStrategy> basicSearches = this.getSearchStrategies();
		DepthFirstSearch<IntVar> current = search;
		current.setPrintInfo(false);
		SelectChoicePoint<IntVar> masterSelect = null;
		
		if (basicSearches.size() == 0) {
			basicSearches.add( this.newSearchStrategy() );
		}

		while (i < basicSearches.size()) {
		
			SearchStrategy next = basicSearches.get(i);
			Var[] vars = next.getVars();
			VarSelector varSelector = next.getVarSelector();
			ValueSelector valSelector = next.getValueSelector();
		
			VarSelectorType varSelectorType;
			
			if (varSelector != null ) varSelectorType = varSelector.getType();
			else {
				
				if (next instanceof SearchMethod)
					varSelectorType = ((SearchMethod)next).varSelector;
				else
					varSelectorType = VarSelectorType.MIN_DOMAIN;
				
			}
			
			ComparatorVariable<IntVar> varFirstComparator = null;
			ComparatorVariable<IntVar> varSecondComparator;
			
			switch (varSelectorType) {
			
			case MAX_REGRET : varFirstComparator = new MaxRegret<IntVar>(); break;
			
			case MIN_DOMAIN : varFirstComparator = new SmallestDomain<IntVar>(); break;
								
			case MIN_DOMAIN_MAX_DEGREE : varFirstComparator = new SmallestDomain<IntVar>();
										 varSecondComparator = new MostConstrainedStatic<IntVar>();
										 break;
										 
			case MIN_VALUE : varFirstComparator = new SmallestMin<IntVar>(); break;
				
			case MIN_DOMAIN_MIN_VALUE : varFirstComparator = new SmallestDomain<IntVar>();
			 varSecondComparator = new SmallestMin<IntVar>();
			 break;
			
			}
			
			ValueSelectorType valueSelectorType = valSelector.getType();
			
			Indomain<IntVar> result = new IndomainMin<IntVar>();
			
			switch (valueSelectorType) {
			
				case MAX : result = new IndomainMax<IntVar>(); break;
				
				case MIN : result = new IndomainMin<IntVar>(); break;

			}
			
			 IntVar[] intVars = new IntVar[vars.length];
			 for (int j = 0; j < intVars.length; j++) {
				 intVars[j] = (IntVar)vars[j].getImpl();
			 }

			 SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(intVars, varFirstComparator, result);

			 if (i == 0)
				 masterSelect = select;

			if (i > 0 ) {
				 
				 DepthFirstSearch<IntVar> nextSearch = new DepthFirstSearch<IntVar>();
				 
				 nextSearch.setMasterSearch(current);
				 nextSearch.setSelectChoicePoint(select);
				 
				 current.addChildSearch(nextSearch);
				 
				 current = nextSearch;
				 
			 }	 
			
			i++;
			
			current.getSolutionListener().searchAll(true);
			current.getSolutionListener().setSolutionLimit(maxNumberOfSolutions);
			current.getSolutionListener().recordSolutions(true);
			current.setPrintInfo(false);
			
		}

		boolean result = search.labeling(store, masterSelect);		

		if (result) {
			IntVar[] vars = search.getVariables();
			int noSol = current.getSolutionListener().solutionsNo();
			javax.constraints.impl.search.Sol[] solutions = new javax.constraints.impl.search.Sol[noSol];
			DepthFirstSearch last = current;
			for (int s = 0; s < noSol; s++) {
				solutions[s] = new javax.constraints.impl.search.Sol(getProblem());
				current = last;
				int parentSol = s;
				while (parentSol != -1 && current != null) {
					SolutionListener sl = current.getSolutionListener();
					Domain[] solution = sl.getSolution(s+1);
					solutions[s].addValues(current.getVariables(), solution);
					parentSol = sl.getParentSolution(s+1);
					current = (DepthFirstSearch)current.masterSearch;
				}
				
			}

			return solutions;
			
		}
		else
			return null;
	
	}

	
	@Override
	public SearchStrategy newSearchStrategy() {
		
		return new SearchMethod(this);

	}

	/**
	 * Returns a SearchStrategy that is currently used by the solver
	 * @return a SearchStrategy that is currently used by the solver to solve the problem
	 */
//	public SearchStrategy getSearchStrategy() {
//		
//	}

	
	@Override
	public void trace(Var var) {
		// @TODO implement it.
		assert false;
	}

	@Override
	public void trace(Var[] vars) {
		// @TODO implement it.
		assert false;
	}

	@Override
	public void trace(VarSet setVar) {
		// @TODO implement it.
		assert false;
	}

}
