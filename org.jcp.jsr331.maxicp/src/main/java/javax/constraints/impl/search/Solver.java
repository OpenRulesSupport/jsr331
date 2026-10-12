package javax.constraints.impl.search;

import java.util.Vector;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.constraints.ProblemState;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarSet;

import org.maxicp.cp.CPFactory;
import org.maxicp.cp.engine.core.CPIntVar;
import org.maxicp.cp.engine.core.CPSolver;
import org.maxicp.search.DFSearch;
import org.maxicp.search.SearchStatistics;
import org.maxicp.util.exception.InconsistencyException;

/** JSR-331 depth-first-search bridge for MaxiCP. */
public class Solver extends AbstractSolver {
    private final CPSolver maxiCP;
    private SearchStatistics lastStatistics;

    public Solver(javax.constraints.Problem problem) {
        super(problem);
        maxiCP = ((javax.constraints.impl.Problem) problem).getMaxiCPSolver();
    }

    public CPSolver getMaxiCPSolver() { return maxiCP; }

    @Override
    public javax.constraints.SearchStrategy newSearchStrategy() {
        return new javax.constraints.impl.search.SearchStrategy(this);
    }

    @Override
    public Solution findSolution(ProblemState restoreOrNot) {
        setTimeLimitStart();
        final Solution[] found = new Solution[1];
        DFSearch search = CPFactory.makeDfs(maxiCP, branching());
        search.onSolution(() -> {
            if (found[0] == null) found[0] = new BasicSolution(this, 1);
        });
        Predicate<SearchStatistics> stop = stats ->
                stats.numberOfSolutions() >= 1 || checkTimeLimit();
        try {
            lastStatistics = search.solve(stop);
        } catch (InconsistencyException inconsistent) {
            return null;
        }
        setTimeLimitExceeded(found[0] == null && checkTimeLimit());
        if (found[0] != null && restoreOrNot == ProblemState.DO_NOT_RESTORE)
            applySolution(found[0]);
        return found[0];
    }

    private Supplier<Runnable[]> branching() {
        Vector<javax.constraints.SearchStrategy> strategies = getSearchStrategies();
        return () -> {
            for (javax.constraints.SearchStrategy strategy : strategies) {
                Var selected = selectVariable(strategy);
                if (selected != null) {
                    CPIntVar variable = ((javax.constraints.impl.Var) selected).getMaxiCPVar();
                    int value = selectValue(strategy, selected);
                    return new Runnable[] {
                        () -> { variable.fix(value); maxiCP.fixPoint(); },
                        () -> { variable.remove(value); maxiCP.fixPoint(); }
                    };
                }
            }
            return new Runnable[0];
        };
    }

    private Var selectVariable(javax.constraints.SearchStrategy strategy) {
        Var[] vars = strategy.getVars();
        if (vars == null) return null;
        if (strategy.getVarSelector() != null) {
            int index = strategy.getVarSelector().select();
            if (index >= 0 && index < vars.length && !vars[index].isBound())
                return vars[index];
        }
        Var best = null;
        for (Var var : vars) {
            if (!var.isBound() && (best == null
                    || var.getDomainSize() < best.getDomainSize()))
                best = var;
        }
        return best;
    }

    private int selectValue(javax.constraints.SearchStrategy strategy, Var var) {
        if (strategy.getValueSelector() != null)
            return strategy.getValueSelector().select(var);
        return var.getMin();
    }

    @Override
    public boolean applySolution(Solution solution) {
        if (solution == null) return false;
        try {
            for (javax.constraints.SearchStrategy strategy : getSearchStrategies()) {
                Var[] vars = strategy.getVars();
                if (vars == null) continue;
                for (Var var : vars) {
                    maxiCP.post(CPFactory.eq(
                        ((javax.constraints.impl.Var) var).getMaxiCPVar(),
                        solution.getValue(var.getName())));
                }
            }
            return true;
        } catch (RuntimeException failure) {
            return false;
        }
    }

    @Override public void trace(Var var) { }
    @Override public void trace(Var[] vars) { }
    @Override public void trace(VarSet setVar) { }
    @Override public void traceFailures(boolean flag) { }

    @Override
    public void logStats() {
        log("*** MaxiCP Execution Profile ***");
        if (lastStatistics != null) log(lastStatistics.toString());
        super.logStats();
    }
}
