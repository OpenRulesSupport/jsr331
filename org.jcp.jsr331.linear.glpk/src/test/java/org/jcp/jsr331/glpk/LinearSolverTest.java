package org.jcp.jsr331.glpk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;

import org.junit.Test;

public class LinearSolverTest {

    @Test
    public void reportsGlpkVersion() {
        Problem problem = ProblemFactory.newProblem("version");
        javax.constraints.linear.LinearSolver solver =
                (javax.constraints.linear.LinearSolver) problem.getSolver();

        assertTrue(solver.getVersion().startsWith("GLPK 4.65"));
    }

    @Test
    public void maximizesIntegerModel() {
        Problem problem = ProblemFactory.newProblem("integer-max");
        Var x = problem.variable("x", 0, 4);
        Var y = problem.variable("y", 0, 4);
        problem.post(new int[] {2, 1}, new Var[] {x, y}, "<=", 7);
        Var objective = problem.scalProd("objective", new int[] {1, 1}, new Var[] {x, y});

        Solution solution = problem.getSolver()
                .findOptimalSolution(Objective.MAXIMIZE, objective);

        assertNotNull(solution);
        assertEquals(5, solution.getValue("objective"));
        assertEquals(5, solution.getValue("x") + solution.getValue("y"));
        assertTrue(2 * solution.getValue("x") + solution.getValue("y") <= 7);
    }

    @Test
    public void minimizesContinuousModel() {
        Problem problem = ProblemFactory.newProblem("continuous-min");
        VarReal x = problem.variableReal("x", 0.0, 10.0);
        VarReal y = problem.variableReal("y", 0.0, 10.0);
        problem.post(new double[] {1.0, 1.0}, new VarReal[] {x, y}, ">=", 5.0);
        VarReal objective = problem.scalProd(
                new double[] {2.0, 1.0}, new VarReal[] {x, y});
        objective.setName("objective");

        Solution solution = problem.getSolver()
                .findOptimalSolution(Objective.MINIMIZE, objective);

        assertNotNull(solution);
        assertEquals(0.0, solution.getValueReal("x"), 1.0e-7);
        assertEquals(5.0, solution.getValueReal("y"), 1.0e-7);
        assertEquals(5.0, solution.getValueReal("objective"), 1.0e-7);
    }

    @Test
    public void solvesMixedIntegerContinuousModel() {
        Problem problem = ProblemFactory.newProblem("mixed-model");
        Var x = problem.variable("x", 0, 4);
        VarReal y = problem.variableReal("y", 0.0, 10.0);
        ConstrainedVariable[] variables = new ConstrainedVariable[] {x, y};
        problem.post(new double[] {1.0, 1.0}, variables, ">=", 4.5);
        VarReal objective = problem.variableReal("objective", 0.0, 100.0);
        problem.post(new double[] {1.0, 2.0}, variables, "=", objective);

        Solution solution = problem.getSolver()
                .findOptimalSolution(Objective.MINIMIZE, objective);

        assertNotNull(solution);
        assertEquals(4, solution.getValue("x"));
        assertEquals(0.5, solution.getValueReal("y"), 1.0e-7);
        assertEquals(5.0, solution.getValueReal("objective"), 1.0e-7);
    }

    @Test
    public void reportsInfeasibleModel() {
        Problem problem = ProblemFactory.newProblem("infeasible");
        Var x = problem.variable("x", 0, 10);
        problem.post(x, "<=", 2);
        problem.post(x, ">=", 3);

        Solution solution = problem.getSolver()
                .findOptimalSolution(Objective.MINIMIZE, x);

        assertNull(solution);
    }

    @Test
    public void handlesStrictIntegralConstraint() {
        Problem problem = ProblemFactory.newProblem("strict-integral");
        Var x = problem.variable("x", 0, 10);
        problem.post(x, "<", 4);

        Solution solution = problem.getSolver()
                .findOptimalSolution(Objective.MAXIMIZE, x);

        assertNotNull(solution);
        assertEquals(3, solution.getValue("x"));
    }

    @Test
    public void solvesImportedMpsModel() throws IOException {
        Problem problem = ProblemFactory.newProblem("imported-mps");
        problem.variableReal("X", 0.0, 10.0);
        Path model = Files.createTempFile("jsr331-glpk-", ".mps");
        try {
            Files.writeString(model, """
                    NAME          IMPORTED
                    ROWS
                     N  COST
                     G  LIMIT
                    COLUMNS
                        X         COST      1
                        X         LIMIT     1
                    RHS
                        RHS1      LIMIT     3
                    BOUNDS
                     LO BND1      X         0
                     UP BND1      X         10
                    ENDATA
                    """);

            javax.constraints.linear.LinearSolver solver =
                    (javax.constraints.linear.LinearSolver) problem.getSolver();
            Solution solution = solver.solve(model.toFile());

            assertNotNull(solution);
            assertEquals(3.0, solution.getValueReal("X"), 1.0e-7);
        } finally {
            Files.deleteIfExists(model);
        }
    }
}
