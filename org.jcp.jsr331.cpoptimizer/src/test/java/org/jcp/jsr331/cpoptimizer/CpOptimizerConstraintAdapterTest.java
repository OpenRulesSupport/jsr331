package org.jcp.jsr331.cpoptimizer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.constraints.Constraint;
import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.Solution;
import javax.constraints.SolutionIterator;
import javax.constraints.Var;

import org.junit.Test;

public class CpOptimizerConstraintAdapterTest {
    @Test public void reportsCpOptimizerVersion() {
        Problem problem = new javax.constraints.impl.Problem("version");
        assertTrue(problem.getImplVersion().contains("CP Optimizer 22.2"));
    }

    @Test public void solvesLinearAndAllDifferentConstraints() {
        Problem problem = new javax.constraints.impl.Problem("linear");
        Var x = problem.variable("x", 0, 3);
        Var y = problem.variable("y", 0, 3);
        problem.post(new Var[] {x, y}, "=", 3);
        problem.postAllDifferent(new Var[] {x, y});
        Solution solution = problem.getSolver().findSolution();
        assertNotNull(solution);
        assertEquals(3, solution.getValue("x") + solution.getValue("y"));
        assertNotEquals(solution.getValue("x"), solution.getValue("y"));
    }

    @Test public void supportsElementsLogicAndReification() {
        Problem problem = new javax.constraints.impl.Problem("expressions");
        Var index = problem.variable("index", 0, 2);
        Var value = problem.element(new int[] {4, 7, 9}, index);
        value.setName("value");
        Constraint indexIsOne = problem.linear(index, "=", 1);
        Var truth = indexIsOne.asBool();
        truth.setName("truth");
        problem.add(truth);
        indexIsOne.post();
        Solution solution = problem.getSolver().findSolution();
        assertNotNull(solution);
        assertEquals(7, solution.getValue("value"));
        assertEquals(1, solution.getValue("truth"));
    }

    @Test public void supportsCardinalityAndGlobalCardinality() {
        Problem problem = new javax.constraints.impl.Problem("cardinality");
        Var[] variables = problem.variableArray("v", 0, 2, 3);
        Var[] cardinalities = {
            problem.variable("zeros", 1, 1), problem.variable("ones", 1, 1),
            problem.variable("twos", 1, 1)
        };
        problem.postGlobalCardinality(variables, new int[] {0, 1, 2}, cardinalities);
        Solution solution = problem.getSolver().findSolution();
        assertNotNull(solution);
        assertNotEquals(solution.getValue("v-0"), solution.getValue("v-1"));
        assertNotEquals(solution.getValue("v-0"), solution.getValue("v-2"));
    }

    @Test public void supportsArithmeticAndOptimization() {
        Problem problem = new javax.constraints.impl.Problem("optimization");
        Var a = problem.variable("a", 0, 10);
        Var b = problem.variable("b", 0, 10);
        problem.post(new Var[] {a, b}, "<=", 12);
        Var objective = a.plus(b);
        objective.setName("objective");
        Solution solution = problem.getSolver().findOptimalSolution(Objective.MAXIMIZE, objective);
        assertNotNull(solution);
        assertEquals(12, solution.getValue("objective"));
        assertEquals(12, solution.getValue("a") + solution.getValue("b"));
    }

    @Test public void handlesSparseDomainsInfeasibilityAndIteration() throws Exception {
        Problem sparse = new javax.constraints.impl.Problem("sparse");
        Var x = sparse.variable("x", new int[] {1, 3, 5, 7});
        ((javax.constraints.impl.Var) x).removeValue(3);
        ((javax.constraints.impl.Var) x).setMin(2);
        ((javax.constraints.impl.Var) x).setMax(6);
        assertEquals(5, sparse.getSolver().findSolution().getValue("x"));

        Problem infeasible = new javax.constraints.impl.Problem("infeasible");
        Var impossible = infeasible.variable("x", 0, 2);
        infeasible.post(impossible, "=", 0);
        infeasible.post(impossible, "=", 1);
        assertNull(infeasible.getSolver().findSolution());

        Problem iteration = new javax.constraints.impl.Problem("iteration");
        iteration.variable("value", 1, 3);
        SolutionIterator iterator = iteration.getSolver().solutionIterator();
        int count = 0;
        int total = 0;
        while (iterator.hasNext()) { count++; total += iterator.next().getValue("value"); }
        assertArrayEquals(new int[] {3, 6}, new int[] {count, total});
    }

    @Test public void extractsUnconstrainedVariablesUsedByDefaultSearch() {
        Problem problem = new javax.constraints.impl.Problem("unconstrained-search-vars");
        Var constrained = problem.variable("constrained", 0, 2);
        problem.variableArray("unused", 0, 1, 3);
        problem.post(constrained, "=", 1);

        Solution solution = problem.getSolver().findSolution();

        assertNotNull(solution);
        assertEquals(1, solution.getValue("constrained"));
        assertEquals(0, solution.getValue("unused-0"));
    }
}
