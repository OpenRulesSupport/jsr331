package javax.constraints.impl.search;

import java.util.ArrayList;
import java.util.List;

import javax.constraints.Solution;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;

/** Enumerates CP-SAT solutions once and exposes them through JSR 331 iteration. */
public class SolutionIterator implements javax.constraints.SolutionIterator {
    private final Solver solver;
    private final List<long[]> values = new ArrayList<>();
    private boolean searched;
    private int position;
    private Solution next;

    public SolutionIterator(javax.constraints.Solver solver) {
        this.solver = (Solver) solver;
    }

    @Override
    public boolean hasNext() {
        if (!searched) {
            enumerate();
            searched = true;
        }
        next = null;
        if (position >= values.size()) {
            return false;
        }
        solver.assignValues(values.get(position));
        next = new BasicSolution(solver, position + 1);
        solver.getOrToolsProblem().clearSolutionValues();
        position++;
        return true;
    }

    @Override
    public Solution next() {
        if (next == null) {
            throw new IllegalStateException("Call hasNext() before next()");
        }
        return next;
    }

    private void enumerate() {
        CpModel working = solver.createWorkingModel();
        List<javax.constraints.impl.Var> variables =
                solver.getOrToolsProblem().getAllVariables();
        IntVar[] nativeVariables = new IntVar[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            nativeVariables[i] = working.getIntVarFromProtoIndex(
                    variables.get(i).getCpVar().getIndex());
        }

        int limit = solver.getTimeLimitGlobal() > 0
                ? solver.getTimeLimitGlobal() : solver.getTimeLimit();
        CpSolver nativeSolver = solver.createNativeSolver(
                limit, true, solver.hasFixedSearch());
        int maximum = solver.getMaxNumberOfSolutions();
        nativeSolver.solve(working, new CpSolverSolutionCallback() {
            @Override
            public void onSolutionCallback() {
                long[] solution = new long[nativeVariables.length];
                for (int i = 0; i < nativeVariables.length; i++) {
                    solution[i] = value(nativeVariables[i]);
                }
                values.add(solution);
                if (maximum > 0 && values.size() >= maximum) {
                    stopSearch();
                }
            }
        });
    }
}
