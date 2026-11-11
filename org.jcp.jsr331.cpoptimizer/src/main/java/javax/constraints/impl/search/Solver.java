package javax.constraints.impl.search;

import java.util.ArrayList;
import java.util.List;

import javax.constraints.Objective;
import javax.constraints.ProblemState;
import javax.constraints.Solution;
import javax.constraints.SolutionIterator;
import javax.constraints.Var;
import javax.constraints.VarSet;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloObjective;
import ilog.cp.IloCP;
import ilog.cp.IloSearchPhase;

/** JSR 331 solver facade over IBM ILOG CP Optimizer. */
public class Solver extends AbstractSolver {
    private final javax.constraints.impl.Problem problem;
    private final IloCP cp;

    public Solver(javax.constraints.Problem problem) {
        super(problem);
        this.problem = (javax.constraints.impl.Problem) problem;
        cp = this.problem.getCp();
    }

    @Override public SearchStrategy newSearchStrategy() { return new SearchStrategy(this); }

    public boolean propagate() { return findSolution(ProblemState.RESTORE) != null; }

    @Override
    public Solution findSolution(ProblemState state) {
        setSolverStartTime(System.currentTimeMillis());
        setTimeLimitExceeded(false);
        try {
            configureSearch();
            if (!cp.solve()) {
                problem.clearSolutionValues();
                return null;
            }
            assignValues();
            Solution solution = new BasicSolution(this, 1);
            if (state == ProblemState.RESTORE) problem.clearSolutionValues();
            return solution;
        } catch (IloException exception) {
            throw javax.constraints.impl.Problem.failure("solve the model", exception);
        }
    }

    @Override
    public Solution findOptimalSolution(Objective objective, Var objectiveVariable) {
        setSolverStartTime(System.currentTimeMillis());
        setTimeLimitExceeded(false);
        addObjective(objectiveVariable);
        IloObjective nativeObjective = null;
        try {
            IloIntVar expression = ((javax.constraints.impl.Var) objectiveVariable).getCpVar();
            nativeObjective = objective == Objective.MAXIMIZE
                    ? cp.maximize(expression) : cp.minimize(expression);
            cp.add(nativeObjective);
            configureSearch();
            if (!cp.solve()) {
                problem.clearSolutionValues();
                return null;
            }
            assignValues();
            Solution solution = new BasicSolution(this, 1);
            problem.clearSolutionValues();
            return solution;
        } catch (IloException exception) {
            throw javax.constraints.impl.Problem.failure("optimize the model", exception);
        } finally {
            if (nativeObjective != null) {
                try { cp.remove(nativeObjective); }
                catch (IloException ignored) { }
            }
        }
    }

    void configureSearch() throws IloException {
        cp.resetParameterValues();
        int limit = getTimeLimitGlobal() > 0 ? getTimeLimitGlobal() : getTimeLimit();
        if (limit > 0) cp.setParameter(IloCP.DoubleParam.TimeLimit, limit / 1000.0);
        List<IloSearchPhase> phases = new ArrayList<>();
        for (javax.constraints.SearchStrategy strategy : getSearchStrategies()) {
            if (strategy instanceof SearchStrategy cpStrategy) {
                IloSearchPhase phase = cpStrategy.createPhase(cp);
                if (phase != null) phases.add(phase);
            }
        }
        if (phases.isEmpty()) cp.setSearchPhases();
        else cp.setSearchPhases(phases.toArray(new IloSearchPhase[0]));
    }

    void assignValues() {
        for (javax.constraints.impl.Var variable : problem.getAllVariables())
            variable.setSolutionValue(cp.getIntValue(variable.getCpVar()));
    }

    void assignValues(long[] values) {
        int index = 0;
        for (javax.constraints.impl.Var variable : problem.getAllVariables())
            variable.setSolutionValue(values[index++]);
    }

    IloCP getCp() { return cp; }
    javax.constraints.impl.Problem getCpProblem() { return problem; }

    @Override public SolutionIterator solutionIterator() {
        return new javax.constraints.impl.search.SolutionIterator(this);
    }

    @Override
    public boolean applySolution(Solution solution) {
        problem.clearSolutionValues();
        try {
            Var[] variables = problem.getVars();
            if (variables != null) {
                for (Var variable : variables) {
                    Var target = problem.getVar(variable.getName());
                    if (target instanceof javax.constraints.impl.Var nativeVariable)
                        nativeVariable.setSolutionValue(solution.getValue(variable.getName()));
                }
            }
            return true;
        } catch (RuntimeException exception) {
            problem.clearSolutionValues();
            return false;
        }
    }

    @Override public void trace(Var variable) { }
    @Override public void trace(Var[] variables) { }
    @Override public void trace(VarSet variable) { }
    @Override public void traceFailures(boolean flag) { }

    @Override
    public void logStats() {
        try {
            problem.log("CP Optimizer: solutions=" + cp.getInfo(IloCP.IntInfo.NumberOfSolutions)
                    + ", branches=" + cp.getInfo(IloCP.IntInfo.NumberOfBranches)
                    + ", failures=" + cp.getInfo(IloCP.IntInfo.NumberOfFails));
        } catch (IloException exception) {
            problem.error("Unable to read CP Optimizer statistics: " + exception.getMessage());
        }
    }
}
