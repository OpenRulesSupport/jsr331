/**
 *  Sol.java 
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

import java.util.HashMap;
import java.util.Set;

import javax.constraints.Problem;
import javax.constraints.Solver;
import javax.constraints.VarString;
import javax.constraints.impl.AbstractProblem;

import JaCoP.core.Domain;
import JaCoP.core.IntDomain;
import JaCoP.core.IntVar;
import JaCoP.core.Store;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.SolutionListener;

/**
 * @author Radoslaw Szymanek
 *
 */

public class Sol implements javax.constraints.Solution {
	
	HashMap<String, Domain> values;
	
	Problem problem;
	
	public Sol(Problem problem) {
		values = new HashMap<String, Domain>();
		this.problem = problem;
	}

	public void addValues(IntVar[] vars, Domain[] doms) {
		
		for (int i = 0; i < vars.length; i++)
			values.put(vars[i].id(), doms[i]);
		
	} 
	
	public Sol(Problem problem, DepthFirstSearch<IntVar> search, Store store, boolean found) {
		
		this.problem = problem;
		
		if (found) {
			boolean wasRecordingSolutions = search.getSolutionListener().isRecordingSolutions();

			SolutionListener<?> solutionListener = search.getSolutionListener();

			int solNo = 0;

			if (solutionListener.solutionsNo() != 0)
				solNo = solutionListener.solutionsNo() - 1;
			else
				solNo = 0;

			Domain[] vals;

			if (wasRecordingSolutions)
				vals = solutionListener.getSolution(solNo);
			else
				vals = solutionListener.getSolutions()[0];

			IntVar[] vars = (IntVar[]) solutionListener.getVariables();
			values = new HashMap<String, Domain>();

			for (int i = 0; i < vals.length; i++)
				values.put(vars[i].id(), vals[i]);

			while (search.childSearches != null) {

				int match = -1;

				int currentChildSearch = 0;
				for (; currentChildSearch < search.childSearches.length
				&& match == -1; currentChildSearch++)
					match = search.childSearches[currentChildSearch].getSolutionListener().findSolutionMatchingParent(solNo);

				if (match == -1)
					assert false;
				else
					solNo = match;

				currentChildSearch--;
				solutionListener = search.childSearches[currentChildSearch].getSolutionListener();

				if (wasRecordingSolutions)
					vals = solutionListener.getSolution(solNo);
				else
					vals = solutionListener.getSolutions()[0];

				vars = (IntVar[]) solutionListener.getVariables();

				for (int i = 0; i < vals.length; i++)
					values.put(vars[i].id(), vals[i]);

				search = (DepthFirstSearch<IntVar>) search.childSearches[currentChildSearch];

			}

		}
		
	}
	
	@Override
	public int getMax(String name) {
		return ((IntDomain)values.get(name)).max();
	}

//	@Override
	public int getMax(int i) {
		assert false;
		return 0;
	}

	@Override
	public int getMin(String name) {
		return ((IntDomain)values.get(name)).min();
	}

//	@Override
	public int getMin(int i) {
		assert false;
		return 0;
	}

	@Override
	public int getNumberOfVarReals() {
		assert false;
		return 0;
	}

	@Override
	public int getNumberOfVarSets() {
		assert false;
		return 0;
	}

	@Override
	public int getNumberOfVars() {
		assert false;
		return 0;
	}

	@Override
	public Problem getProblem() {

		return problem;
		
	}

	@Override
	public double getValueReal(String name) {
		assert false;
		return 0;
	}

//	@Override
	public double getRealValue(int i) {
		assert false;
		return 0;
	}

	@Override
	public int getSolutionNumber() {
		assert false;
		return 0;
	}

	@Override
	public Solver getSolver() {
		assert false;
		return null;
	}

	@Override
	public int getValue(String name) {

		try {
			return ((IntDomain)values.get(name)).value();
		}
		catch(java.lang.NullPointerException ex) {
			System.out.println("Variable " + name + " not found in the solution ");
			System.out.println( values );
			throw ex;
		}
		
	}

//	@Override
	public int getValue(int i) {
		assert false;
		return 0;
	}

	@Override
	public boolean isBound(String name) {
		return ((IntDomain)values.get(name)).singleton();
	}

//	@Override
	public boolean isBound(int i) {
		assert false;
		return false;
	}

	@Override
	public boolean isBound() {
		assert false;
		return false;
	}
	
	public Set<Integer> getValueSet(String name){
		assert false;
		return null;
	}

	@Override
	public void log() {
		
		problem.log(toString());
		
	}
	
	public void log(int variablesPerLine) {
		log();
	}
	
	public String toString() {
		
		StringBuffer result = new StringBuffer("[");
		
		for (String name : values.keySet()) {
			result.append(name);
			result.append("=");
			result.append(values.get(name));
			result.append(", ");
		}
		
		result.replace(result.length() - 2, result.length(), "]");
		
		return result.toString();
			
	}
	
	@Override
	public void setSolutionNumber(int number) {
		assert false;
	}

	@Override
	public int getAt(String name) {
		return getValue(name);
	}
	
	@Override
	public void save() {
		assert false; // JF
	}
	
	public String getValueString(String name) {
		int intValue= getValue(name);
		AbstractProblem p = (AbstractProblem)getProblem();
		VarString varString = p.getVarString(name);
		return varString.getValue(intValue);
	}

}
