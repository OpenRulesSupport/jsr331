package javax.constraints.impl.search;

import javax.constraints.Solution;

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
public class SolutionIterator implements javax.constraints.SolutionIterator {
    Solver solver;
    Solution solution;
    int solutionNumber;
    boolean noSolutions;
    
    public SolutionIterator(javax.constraints.Solver solver) {
        this.solver = (Solver)solver;
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
        } else {
            solution = solver.findNextSolution();
        }
        if (solution != null) {
            solutionNumber++;
            return true;
        } else 
            return false;
    }

    @Override
    public Solution next() {
        if (solution == null)
            throw new RuntimeException("Cannot use SolutionIterator.next() before checking the hasNext() returned true");
        return solution;
    }
}
