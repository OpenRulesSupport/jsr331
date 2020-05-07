package javax.constraints.impl.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemState;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarSet;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.converter.Converter;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.expression.Expression;

/**
 * An implementation of the interface "SolverClass" by extending the common class AbstractSolver
 */
public class Solver extends AbstractSolver {
    public Sat4jSolver sat4jSolver;
    protected int solutionNumber = 0;
    
    public Solver(Problem problem) {
        super(problem);
        sat4jSolver = new Sat4jSolver(_getProblem());
    }

    private javax.constraints.impl.Problem _getProblem() {
        return (javax.constraints.impl.Problem)getProblem();
    }

    private void addExpression(Expression c) throws SugarException {
        javax.constraints.impl.Problem p = _getProblem();
        Converter.INCREMENTAL_PROPAGATION = false;
        p.sugarConverter.convert(c);
        Converter.INCREMENTAL_PROPAGATION = true;
    }

    private Solution _getSolution(ProblemState restoreOrNot) {
        javax.constraints.impl.Problem p = _getProblem();
        for (javax.constraints.impl.Var v : p.getVariables()) {
            IntegerVariable x = v._getImpl();
            v.setSolutionValue(x.getValue());
        }
        Solution solution = new BasicSolution(this, 0);
        if (restoreOrNot == ProblemState.RESTORE)
            _restore();
        return solution;
    }

    private void _restore() {
        javax.constraints.impl.Problem p = _getProblem();
        for (javax.constraints.impl.Var v : p.getVariables()) {
            v.setSolutionValue(null);
        }
    }
    
    private int _getValue(Var v) {
        return ((javax.constraints.impl.Var)v)._getImpl().getValue();
    }
    
    /**
     * This methods returns a new default search strategy 
     * @return a new default search strategy
     */
    @Override
    public javax.constraints.SearchStrategy newSearchStrategy() {
        SearchStrategy strategy = new javax.constraints.impl.search.SearchStrategy(this);
        strategy.setVars(getProblem().getVars());
        return strategy;
    }

    /**
     * This method attempts to find a solution of the problem, for which the solver was defined. 
     * It uses the search strategy defined by the method setSearchStrategy(). 
     * It returns the found solution (if any) or null. It also saves the solution and makes it 
     * available through the method getSolution(). 
     * If a solution is not found, the problem state is restored 
     * to that of before the invocation of this method.
     * If a solution is found, the problem state will be restored only if the parameter
     * "restore" is true. Otherwise all problem variables will be instantiated with the solution
     * values.
     *
     * The search can be limited by time, number of failed attempts, etc.
     *
     * @param restoreOrNot defines if the problem state should be restored after a solution is found
     * @return a Solution if the search is successful or null 
     */
    @Override
    public Solution findSolution(ProblemState restoreOrNot) {
        try {
            // addStrategyLogVariables();
            Solution solution = null;
            solutionNumber = 0;
            sat4jSolver.init();
            if (sat4jSolver.encode() && sat4jSolver.find()) {
                solution = _getSolution(restoreOrNot);
                solutionNumber++;
                solution.setSolutionNumber(solutionNumber);
            }
            sat4jSolver.commit();
            return solution;
        } catch (SugarException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public Solution findNextSolution() {
        try {
            javax.constraints.impl.Problem p = _getProblem();
            List<Expression> xs = new ArrayList<Expression>();
            xs.add(Expression.OR);
            for (Var v : getProblem().getVars()) {
                int a = _getValue(v);
                xs.add(p.toExpr(v).ne(a));
            }
            addExpression(Expression.create(xs));
            sat4jSolver.encodeDelta();
            boolean result = sat4jSolver.find();
            Solution solution = null;
            if (result) {
                solution = _getSolution(ProblemState.DO_NOT_RESTORE);
                solutionNumber++;
                solution.setSolutionNumber(solutionNumber);
            } else {
                _restore();
                sat4jSolver.cancel();
            }
            return solution;
        } catch (SugarException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * This method attempts to find the solution that minimizes/maximizes the objective variable.
     * It uses the search strategy defined by the method setSearchStrategy(strategy). 
     * The optimization process can be also controlled by:
     * <ul>
     * <li> OptimizationTolarance that is a difference between optimal solutions - see setOptimizationTolarance()
     * <li> MaxNumberOfSolutions that is the total number of considered solutions - may be limited by the method
     * setMaxNumberOfSolutions()
     * <li> TotalTimeLimit that is the number of seconds allocated for the entire optimization process.
     * </ul>
     * <br> At the same time the time for one iteration inside
     * optimization loop (a search of one solution) can be also limited by the use of the
     * special type of search strategy. 
     * <br> The problem state after the execution of this method is always restored. All variables
     * that were added to the problems (plus the objectiveVar) will have their assigned values 
     * saved inside the optimal solution. 
     * 
     * @param objective Objective.MINIMIZE or Objective.MAXIMIZE
     * @param objectiveVar the variable that is being minimized/maximized
     * @return Solution if a solution is found,
     *         null if there are no solutions.
     */
    @Override
    public Solution findOptimalSolution(Objective objective, Var objectiveVar) {
        try {
            // addStrategyLogVariables();
            getProblem().add(objectiveVar);
            addObjective(objectiveVar);
            Solution solution = null;
            solutionNumber = 0;
            int lb = objectiveVar.getMin();
            int ub = objectiveVar.getMax();
            sat4jSolver.init();
            boolean result = sat4jSolver.encode() && sat4jSolver.find();
            sat4jSolver.commit();
            if (result) {
                solution = _getSolution(ProblemState.DO_NOT_RESTORE);
                if (objective == Objective.MINIMIZE) {
                    ub = _getValue(objectiveVar); 
                    while (lb < ub) {
                        int mid = (lb + ub) / 2;
                        if (findOptBound(objectiveVar, lb, mid)) {
                            solution = _getSolution(ProblemState.DO_NOT_RESTORE);
                            ub = _getValue(objectiveVar);
                        } else {
                            lb = mid + 1;
                        }
                    }
                } else {
                    lb = _getValue(objectiveVar); 
                    while (lb < ub) {
                        int mid = (lb + ub + 1) / 2;
                        if (findOptBound(objectiveVar, mid, ub)) {
                            solution = _getSolution(ProblemState.DO_NOT_RESTORE);
                            lb = _getValue(objectiveVar);
                        } else {
                            ub = mid - 1;
                        }
                    }
                }
            }
            if (solution != null) {
                solutionNumber++;
                solution.setSolutionNumber(solutionNumber);
            }
            _restore();
            sat4jSolver.cancel();
            return solution;
        } catch (SugarException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    private boolean findOptBound(Var objectiveVar, int lb, int ub) throws SugarException, IOException {
        javax.constraints.impl.Problem p = _getProblem();
        sat4jSolver.cancel();
        Expression v = p.toExpr(objectiveVar);
        addExpression(v.ge(lb).and(v.le(ub)));
        sat4jSolver.encodeDelta();
        return sat4jSolver.find();
    }
    
    /**
     * Creates a solution iterator that allows a user to search and navigate
     * through multiple solutions.
     * 
     * @return a solution iterator
     */
    @Override
    public SolutionIterator solutionIterator() {
        return new SolutionIterator(this);
    }
    
    @Override
    public boolean applySolution(Solution solution) {
        // TODO JSR331 Implementation
        log("applySolution is not supported");
        return false;
    }

    @Override
    public void trace(Var var) {
        // TODO JSR331 Implementation
        log("trace is not supported");
    }

    @Override
    public void trace(Var[] vars) {
        // TODO JSR331 Implementation
        log("trace is not supported");
    }

    @Override
    public void trace(VarSet setVar) {
        // TODO JSR331 Implementation
        log("trace is not supported");
    }

    /**
     * If flag is true, all failures will be traced (logged)
     * 
     * @param flag boolean
     */
    @Override
    public void traceFailures(boolean flag) {
        // TODO JSR331 Implementation
        if (flag)
            log("traceFailures is not supported");
    }

    @Override
    public void logStats() {
        log("*** Execution Profile ***");
        long occupied_memory = Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory();
        log("Occupied memory: " + occupied_memory);
        long executionTime = System.currentTimeMillis() - getSolverStartTime();
        log("Execution time: " + executionTime + " msec");
    }
}
