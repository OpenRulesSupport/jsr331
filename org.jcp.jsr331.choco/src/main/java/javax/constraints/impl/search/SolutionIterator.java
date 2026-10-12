package javax.constraints.impl.search;

import javax.constraints.Solution;

/** Iterates through Choco's native solution stream. */
public class SolutionIterator implements javax.constraints.SolutionIterator {
    private final Solver solver;
    private Solution next;
    private int solutionNumber;
    private boolean started;

    public SolutionIterator(javax.constraints.Solver solver) {
        this.solver = (Solver) solver;
    }

    @Override
    public boolean hasNext() {
        next = null;
        if (!started) {
            solver.getChocoSolver().reset();
            solver.configureSearch();
            started = true;
        }
        if (solver.getChocoSolver().solve()) {
            next = new BasicSolution(solver, ++solutionNumber);
            return true;
        }
        return false;
    }

    @Override
    public Solution next() {
        if (next == null) throw new IllegalStateException("Call hasNext() before next()");
        return next;
    }
}
