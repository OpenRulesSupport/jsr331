package org.jscp.jsr331.test;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.SolutionIterator;
import javax.constraints.Solver;
import javax.constraints.Var;

/** Dependency-free integration checks; run with assertions enabled or directly via main. */
public final class Choco6SmokeTest {
    private Choco6SmokeTest() { }

    public static void main(String[] args) {
        testCoreConstraintsAndIteration();
        testOptimization();
        testArithmeticExpressions();
        System.out.println("Choco 6 adapter smoke tests passed");
    }

    private static void testCoreConstraintsAndIteration() {
        Problem p = ProblemFactory.newProblem("core");
        Var x = p.variable("x", 0, 2);
        Var y = p.variable("y", 0, 2);
        p.postAllDifferent(new Var[]{x, y});
        p.post(new Var[]{x, y}, "=", 2);
        p.postElement(new int[]{10, 20, 30}, x, "=", 10);
        p.postCardinality(new Var[]{x, y}, 0, "=", 1);

        Solver solver = p.getSolver();
        Solution solution = solver.findSolution();
        require(solution != null, "Expected a solution");
        require(solution.getValue("x") == 0, "element constraint was not enforced");
        require(solution.getValue("y") == 2, "linear/allDifferent constraints were not enforced");

        Problem q = ProblemFactory.newProblem("iterator");
        q.variable("v", 1, 3);
        SolutionIterator iterator = q.getSolver().solutionIterator();
        int count = 0;
        while (iterator.hasNext()) { iterator.next(); count++; }
        require(count == 3, "Expected 3 iterator solutions, got " + count);
    }

    private static void testOptimization() {
        Problem p = ProblemFactory.newProblem("optimization");
        Var x = p.variable("x", 0, 10);
        Var y = p.variable("y", 0, 10);
        p.post(new Var[]{x, y}, "<=", 12);
        Solution solution = p.getSolver().findOptimalSolution(Objective.MAXIMIZE, x.plus(y));
        require(solution != null, "Expected an optimal solution");
        require(solution.getValue("x") + solution.getValue("y") == 12,
                "Expected objective value 12");
    }

    private static void testArithmeticExpressions() {
        Problem p = ProblemFactory.newProblem("arithmetic");
        Var x = p.variable("x", -4, 4);
        p.post(x.abs(), "=", 3);
        p.post(x.multiply(2), "<", 0);
        Solution solution = p.getSolver().findSolution();
        require(solution != null && solution.getValue("x") == -3,
                "abs/multiply expressions were not enforced");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
