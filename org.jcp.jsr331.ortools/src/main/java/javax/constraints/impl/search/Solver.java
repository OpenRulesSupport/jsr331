package javax.constraints.impl.search;

import javax.constraints.Objective;
import javax.constraints.ProblemState;
import javax.constraints.Solution;
import javax.constraints.SolutionIterator;
import javax.constraints.Var;
import javax.constraints.VarSet;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.SatParameters.SearchBranching;

/** JSR 331 solver facade over OR-Tools CP-SAT. */
public class Solver extends AbstractSolver {
    private final javax.constraints.impl.Problem problem;
    private CpSolver lastSolver;

    public Solver(javax.constraints.Problem problem) {
        super(problem);
        this.problem = (javax.constraints.impl.Problem) problem;
    }

    @Override
    public SearchStrategy newSearchStrategy() {
        return new SearchStrategy(this);
    }

    public boolean propagate() {
        CpModel working = createWorkingModel();
        CpSolver nativeSolver = createNativeSolver(getTimeLimit(), false, false);
        CpSolverStatus status = nativeSolver.solve(working);
        problem.clearSolutionValues();
        return isSolution(status);
    }

    @Override
    public Solution findSolution(ProblemState state) {
        setSolverStartTime(System.currentTimeMillis());
        setTimeLimitExceeded(false);
        CpModel working = createWorkingModel();
        CpSolver nativeSolver = createNativeSolver(getTimeLimit(), false, hasFixedSearch());
        CpSolverStatus status = nativeSolver.solve(working);
        lastSolver = nativeSolver;
        if (!isSolution(status)) {
            setTimeLimitExceeded(status == CpSolverStatus.UNKNOWN && getTimeLimit() > 0);
            problem.clearSolutionValues();
            return null;
        }

        assignValues(nativeSolver, working);
        Solution solution = new BasicSolution(this, 1);
        if (state == ProblemState.RESTORE) {
            problem.clearSolutionValues();
        }
        return solution;
    }

    @Override
    public Solution findOptimalSolution(Objective objective, Var objectiveVariable) {
        setSolverStartTime(System.currentTimeMillis());
        setTimeLimitExceeded(false);
        addObjective(objectiveVariable);
        CpModel working = createWorkingModel();
        IntVar nativeObjective = working.getIntVarFromProtoIndex(
                ((javax.constraints.impl.Var) objectiveVariable).getCpVar().getIndex());
        if (objective == Objective.MAXIMIZE) {
            working.maximize(nativeObjective);
        } else {
            working.minimize(nativeObjective);
        }

        int limit = getTimeLimitGlobal() > 0 ? getTimeLimitGlobal() : getTimeLimit();
        CpSolver nativeSolver = createNativeSolver(limit, false, hasFixedSearch());
        CpSolverStatus status = nativeSolver.solve(working);
        lastSolver = nativeSolver;
        if (!isSolution(status)) {
            setTimeLimitExceeded(status == CpSolverStatus.UNKNOWN && limit > 0);
            problem.clearSolutionValues();
            return null;
        }

        assignValues(nativeSolver, working);
        Solution solution = new BasicSolution(this, 1);
        problem.clearSolutionValues();
        return solution;
    }

    CpModel createWorkingModel() {
        CpModel working = problem.getCpModel().getClone();
        for (javax.constraints.SearchStrategy strategy : getSearchStrategies()) {
            if (strategy instanceof SearchStrategy nativeStrategy) {
                nativeStrategy.apply(working);
            }
        }
        return working;
    }

    CpSolver createNativeSolver(int timeLimitMillis, boolean enumerate, boolean fixedSearch) {
        CpSolver nativeSolver = new CpSolver();
        nativeSolver.getParameters().setLogSearchProgress(false).setLogToStdout(false);
        if (timeLimitMillis > 0) {
            nativeSolver.getParameters().setMaxTimeInSeconds(timeLimitMillis / 1000.0);
        }
        if (enumerate) {
            nativeSolver.getParameters()
                    .setEnumerateAllSolutions(true)
                    .setNumSearchWorkers(1);
        }
        if (fixedSearch) {
            nativeSolver.getParameters()
                    .setSearchBranching(SearchBranching.FIXED_SEARCH)
                    .setCpModelPresolve(false);
        }
        return nativeSolver;
    }

    boolean hasFixedSearch() {
        for (javax.constraints.SearchStrategy strategy : getSearchStrategies()) {
            if (strategy.getType() != javax.constraints.SearchStrategy.SearchStrategyType.CUSTOM
                    && strategy.getVars() != null && strategy.getVars().length > 0) {
                return true;
            }
        }
        return false;
    }

    void assignValues(CpSolver nativeSolver, CpModel working) {
        for (javax.constraints.impl.Var variable : problem.getAllVariables()) {
            IntVar nativeVariable = working.getIntVarFromProtoIndex(
                    variable.getCpVar().getIndex());
            variable.setSolutionValue(nativeSolver.value(nativeVariable));
        }
    }

    void assignValues(long[] values) {
        int index = 0;
        for (javax.constraints.impl.Var variable : problem.getAllVariables()) {
            variable.setSolutionValue(values[index++]);
        }
    }

    javax.constraints.impl.Problem getOrToolsProblem() {
        return problem;
    }

    private static boolean isSolution(CpSolverStatus status) {
        return status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL;
    }

    @Override
    public SolutionIterator solutionIterator() {
        return new javax.constraints.impl.search.SolutionIterator(this);
    }

    @Override
    public boolean applySolution(Solution solution) {
        problem.clearSolutionValues();
        try {
            Var[] solutionVariables = problem.getVars();
            if (solutionVariables == null) {
                return true;
            }
            for (Var variable : solutionVariables) {
                javax.constraints.Var target = problem.getVar(variable.getName());
                if (target instanceof javax.constraints.impl.Var nativeVariable) {
                    nativeVariable.setSolutionValue(solution.getValue(variable.getName()));
                }
            }
            return true;
        } catch (RuntimeException failure) {
            problem.clearSolutionValues();
            return false;
        }
    }

    @Override
    public void trace(Var variable) {
    }

    @Override
    public void trace(Var[] variables) {
    }

    @Override
    public void trace(VarSet variable) {
    }

    @Override
    public void traceFailures(boolean flag) {
    }

    @Override
    public void logStats() {
        if (lastSolver != null) {
            problem.log(lastSolver.responseStats());
        }
    }
}
