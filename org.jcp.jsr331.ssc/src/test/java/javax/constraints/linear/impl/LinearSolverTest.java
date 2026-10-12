package javax.constraints.linear.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.constraints.Objective;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarReal;

import org.junit.Test;

public class LinearSolverTest {

    @Test
    public void solvesMixedIntegerProblem() {
        javax.constraints.impl.Problem problem = new javax.constraints.impl.Problem("mixed");
        Var x = problem.variable("x", 0, 4);
        VarReal y = problem.variableReal("y", 0.0, 10.0);
        problem.post(new double[] { 2.0, 1.0 },
                new javax.constraints.ConstrainedVariable[] { x, y }, "<=", 5.0);
        VarReal objective = problem.variableReal("objective", 0.0, 20.0);
        problem.post(new double[] { 3.0, 2.0, -1.0 },
                new javax.constraints.ConstrainedVariable[] { x, y, objective }, "=", 0.0);

        Solver solver = problem.getSolver();
        Solution solution = solver.findOptimalSolution(Objective.MAXIMIZE, objective);

        assertNotNull(solution);
        assertEquals(0, solution.getValue("x"));
        assertEquals(5.0, solution.getValueReal("y"), 1.0e-6);
    }

    @Test
    public void solvesContinuousProblem() {
        javax.constraints.impl.Problem problem = new javax.constraints.impl.Problem("continuous");
        VarReal x = problem.variableReal("x", 0.0, 10.0);
        VarReal y = problem.variableReal("y", 0.0, 10.0);
        problem.post(new double[] { 1.0, 1.0 }, new VarReal[] { x, y }, "<=", 6.0);
        VarReal objective = problem.scalProd(new double[] { 2.0, 1.0 }, new VarReal[] { x, y });

        Solution solution = problem.getSolver().findOptimalSolution(Objective.MAXIMIZE, objective);

        assertNotNull(solution);
        assertEquals(6.0, solution.getValueReal("x"), 1.0e-6);
        assertEquals(0.0, solution.getValueReal("y"), 1.0e-6);
    }
}
