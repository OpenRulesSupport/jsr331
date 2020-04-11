//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |   (..)  |                           *
//*         |_  J||L _|        CHOCO solver       *
//*                                               *
//*    Choco is a java library for constraint     *
//*    satisfaction problems (CSP), constraint    *
//*    programming (CP) and explanation-based     *
//*    constraint solving (e-CP). It is built     *
//*    on a event-based propagation mechanism     *
//*    with backtrackable structures.             *
//*                                               *
//*    Choco is an open-source software,          *
//*    distributed under a BSD licence            *
//*    and hosted by sourceforge.net              *
//*                                               *
//*    + website : http://choco.emn.fr            *
//*    + support : choco@emn.fr                   *
//*                                               *
//*    Copyright (C) F. Laburthe,                 *
//*                  N. Jussien    1999-2009      *
//* * * * * * * * * * * * * * * * * * * * * * * * *
package javax.constraints.impl.search;

/**
 * This is a simple implementation of the SolutionIterator interface 
 * that allows a user to search and to iterate through multiple solutions.
 * The expected use:
 * <br>
 * <pre>
 * SolutionIterator iter = solver.solutionIterator();
 * while(iter.hasNext()) {
 *   Solution solution = iter.next();
 *     ...
 * }
 *
 * </pre>
 */

import javax.constraints.Solution;
import javax.constraints.impl.search.Solver;
import javax.constraints.Var;
import javax.constraints.ProblemState;
import javax.constraints.impl.constraint.ConstraintNotAllEqual;

public class SolutionIterator implements javax.constraints.SolutionIterator {
	
	Solver solver;
	Solution solution;
	int solutionNumber;
	boolean noSolutions;
	
	public SolutionIterator(javax.constraints.Solver solver) {
		this.solver = (javax.constraints.impl.search.Solver)solver;
		solution = null;
		noSolutions = false;
		solutionNumber = 0;
	}

	@Override
	public boolean hasNext() {
		if (noSolutions)
			return false;
		solution = null;
		if (solutionNumber == 0) {
			solution = solver.findSolution();
		}
		else {
			if (solver.getChocoSolver().nextSolution()) {
				solution = new BasicSolution(solver,solutionNumber+1);
			}
		}
		if (solution != null) {
			solutionNumber++;
			return true;
		}
		else 
			return false;
	}

	@Override
	public Solution next() {
		if (solution == null)
			throw new RuntimeException("Cannot use SolutionIterator.next() before checking the hasNext() returned true");
		return solution;
	}

}
