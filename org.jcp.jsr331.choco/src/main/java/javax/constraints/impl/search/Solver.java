package javax.constraints.impl.search;

import java.util.ArrayList;
import java.util.List;

import javax.constraints.Objective;
import javax.constraints.ProblemState;
import javax.constraints.SearchStrategy.SearchStrategyType;
import javax.constraints.Solution;
import javax.constraints.SolutionIterator;
import javax.constraints.Var;
import javax.constraints.VarSet;

import org.chocosolver.solver.Cause;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;

/** JSR-331 solver facade over Choco Solver 6. */
public class Solver extends AbstractSolver {
    private final Model model;
    private final org.chocosolver.solver.Solver chocoSolver;

    public Solver(javax.constraints.Problem problem) {
        super(problem);
        model = ((javax.constraints.impl.Problem) problem).getChocoModel();
        chocoSolver = model.getSolver();
    }

    public org.chocosolver.solver.Solver getChocoSolver() { return chocoSolver; }

    @Override public SearchStrategy newSearchStrategy() { return new SearchStrategy(this); }

    public boolean propagate() {
        try { chocoSolver.propagate(); return true; }
        catch (ContradictionException e) { return false; }
    }

    @Override
    public Solution findSolution(ProblemState state) {
        setSolverStartTime(System.currentTimeMillis());
        chocoSolver.reset();
        configureSearch();
        if (!chocoSolver.solve()) return null;
        Solution solution = new BasicSolution(this, 1);
        if (state == ProblemState.RESTORE) chocoSolver.reset();
        return solution;
    }

    void configureSearch() {
        List<AbstractStrategy<?>> strategies = new ArrayList<AbstractStrategy<?>>();
        for (javax.constraints.SearchStrategy strategy : getSearchStrategies()) {
            if (strategy.getType() == SearchStrategyType.CUSTOM) continue;
            AbstractStrategy<?> nativeStrategy = ((SearchStrategy) strategy).getChocoStrategy();
            if (nativeStrategy != null) strategies.add(nativeStrategy);
        }
        if (strategies.isEmpty()) Search.defaultSearch(model);
        else chocoSolver.setSearch(strategies.toArray(new AbstractStrategy<?>[0]));
    }

    @Override
    public Solution findOptimalSolution(Objective objective, Var objectiveVar) {
        setSolverStartTime(System.currentTimeMillis());
        addObjective(objectiveVar);
        chocoSolver.reset();
        configureSearch();
        IntVar nativeObjective = ((javax.constraints.impl.Var) objectiveVar).getChocoVar();
        model.setObjective(objective == Objective.MAXIMIZE, nativeObjective);
        Solution best = null;
        int number = 0;
        while (chocoSolver.solve()) best = new BasicSolution(this, ++number);
        chocoSolver.reset();
        model.clearObjective();
        return best;
    }

    @Override public SolutionIterator solutionIterator() { return new javax.constraints.impl.search.SolutionIterator(this); }

    @Override
    public boolean applySolution(Solution solution) {
        chocoSolver.reset();
        int world = model.getEnvironment().getWorldIndex();
        model.getEnvironment().worldPush();
        try {
            Var[] vars = getProblem().getVars();
            if (vars != null) {
                for (Var var : vars) {
                    Var target = getProblem().getVar(var.getName());
                    if (target != null)
                        ((javax.constraints.impl.Var) target).getChocoVar().instantiateTo(
                                solution.getValue(var.getName()), Cause.Null);
                }
            }
            chocoSolver.propagate();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            model.getEnvironment().worldPopUntil(world);
            chocoSolver.reset();
        }
    }

    @Override public void trace(Var var) { }
    @Override public void trace(Var[] vars) { }
    @Override public void trace(VarSet setVar) { }
    @Override public void traceFailures(boolean flag) { }

    @Override public void logStats() { chocoSolver.printStatistics(); }
}
