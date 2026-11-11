package javax.constraints.impl.search;

import java.util.ArrayList;
import java.util.List;

import javax.constraints.Solution;

import ilog.concert.IloException;
import ilog.cp.IloCP;

/** Enumerates CP Optimizer solutions through its native search stream. */
public class SolutionIterator implements javax.constraints.SolutionIterator {
    private final Solver solver;
    private final List<long[]> values = new ArrayList<>();
    private boolean searched;
    private int position;
    private Solution next;

    public SolutionIterator(javax.constraints.Solver solver) { this.solver = (Solver) solver; }

    @Override
    public boolean hasNext() {
        if (!searched) { enumerate(); searched = true; }
        next = null;
        if (position >= values.size()) return false;
        solver.assignValues(values.get(position));
        next = new BasicSolution(solver, position + 1);
        solver.getCpProblem().clearSolutionValues();
        position++;
        return true;
    }

    @Override
    public Solution next() {
        if (next == null) throw new IllegalStateException("Call hasNext() before next()");
        return next;
    }

    private void enumerate() {
        IloCP cp = solver.getCp();
        boolean searchStarted = false;
        try {
            solver.configureSearch();
            cp.startNewSearch();
            searchStarted = true;
            int maximum = solver.getMaxNumberOfSolutions();
            while (cp.next()) {
                List<javax.constraints.impl.Var> variables = solver.getCpProblem().getAllVariables();
                long[] solution = new long[variables.size()];
                for (int index = 0; index < variables.size(); index++)
                    solution[index] = cp.getIntValue(variables.get(index).getCpVar());
                values.add(solution);
                if (maximum > 0 && values.size() >= maximum) break;
            }
        } catch (IloException exception) {
            throw javax.constraints.impl.Problem.failure("enumerate solutions", exception);
        } finally {
            if (searchStarted) {
                try { cp.endSearch(); }
                catch (IloException ignored) { }
            }
        }
    }
}
