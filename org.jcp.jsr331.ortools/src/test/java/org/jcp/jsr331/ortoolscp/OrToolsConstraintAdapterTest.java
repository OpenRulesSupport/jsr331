package org.jcp.jsr331.ortoolscp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
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
import javax.constraints.VarSelectorType;

import org.junit.Test;

public class OrToolsConstraintAdapterTest {

    @Test
    public void reportsOrToolsCpSatVersion() {
        Problem problem = new javax.constraints.impl.Problem("version");

        assertTrue(problem.getImplVersion().contains("OR-Tools CP-SAT 9.15."));
    }

    @Test
    public void solvesLinearAndAllDifferentConstraints() {
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

    @Test
    public void usesZeroBasedIndexesForIntegerAndVariableElements() {
        Problem problem = new javax.constraints.impl.Problem("element");
        Var index = problem.variable("index", 0, 2);
        Var constantValue = problem.element(new int[] {4, 7, 9}, index);
        constantValue.setName("constantValue");
        Var a = problem.variable("a", 10, 10);
        Var b = problem.variable("b", 20, 20);
        Var c = problem.variable("c", 30, 30);
        Var variableValue = problem.element(new Var[] {a, b, c}, index);
        variableValue.setName("variableValue");
        problem.post(index, "=", 0);

        Solution solution = problem.getSolver().findSolution();

        assertNotNull(solution);
        assertEquals(4, solution.getValue("constantValue"));
        assertEquals(10, solution.getValue("variableValue"));
    }

    @Test
    public void supportsLogicalAndReifiedConstraints() {
        Problem problem = new javax.constraints.impl.Problem("logic");
        Var x = problem.variable("x", 0, 2);
        Var y = problem.variable("y", 0, 2);
        Constraint xIsOne = problem.linear(x, "=", 1);
        Constraint yIsTwo = problem.linear(y, "=", 2);
        Var truth = xIsOne.asBool();
        truth.setName("xIsOne");
        problem.add(truth);
        problem.postIfThen(xIsOne, yIsTwo);
        xIsOne.post();
        yIsTwo.negation().or(problem.linear(y, "=", 2)).post();

        Solution solution = problem.getSolver().findSolution();

        assertNotNull(solution);
        assertEquals(1, solution.getValue("x"));
        assertEquals(2, solution.getValue("y"));
        assertEquals(1, solution.getValue("xIsOne"));
    }

    @Test
    public void supportsCardinalityAndClosedGlobalCardinality() {
        Problem problem = new javax.constraints.impl.Problem("cardinality");
        Var[] variables = problem.variableArray("v", 0, 2, 3);
        Var[] cardinalities = {
            problem.variable("zeros", 1, 1),
            problem.variable("ones", 1, 1),
            problem.variable("twos", 1, 1)
        };
        problem.postGlobalCardinality(variables, new int[] {0, 1, 2}, cardinalities);
        problem.postCardinality(variables, 0, "=", 1);

        Solution solution = problem.getSolver().findSolution();

        assertNotNull(solution);
        assertNotEquals(solution.getValue("v-0"), solution.getValue("v-1"));
        assertNotEquals(solution.getValue("v-0"), solution.getValue("v-2"));
        assertNotEquals(solution.getValue("v-1"), solution.getValue("v-2"));
    }

    @Test
    public void supportsSelfReferentialGlobalCardinality() {
        Problem problem = new javax.constraints.impl.Problem("magic-sequence");
        Var[] sequence = problem.variableArray("sequence", 0, 3, 4);

        problem.postGlobalCardinality(sequence, new int[] {0, 1, 2, 3}, sequence);
        problem.post(new int[] {0, 1, 2, 3}, sequence, "=", 4);

        javax.constraints.Solver solver = problem.getSolver();
        solver.getSearchStrategy().setVars(sequence);
        solver.getSearchStrategy().setVarSelectorType(VarSelectorType.MIN_DOMAIN);
        Solution solution = solver.findSolution();
        assertNotNull(solution);
        assertArrayEquals(new int[] {1, 2, 1, 0}, new int[] {
                solution.getValue("sequence-0"),
                solution.getValue("sequence-1"),
                solution.getValue("sequence-2"),
                solution.getValue("sequence-3")
        });
    }

    @Test
    public void supportsArithmeticExpressionsAndOptimization() {
        Problem arithmetic = new javax.constraints.impl.Problem("arithmetic");
        Var x = arithmetic.variable("x", -4, 4);
        arithmetic.post(x.abs(), "=", 3);
        arithmetic.post(x.multiply(2), "<", 0);

        Solution arithmeticSolution = arithmetic.getSolver().findSolution();

        assertNotNull(arithmeticSolution);
        assertEquals(-3, arithmeticSolution.getValue("x"));

        Problem optimization = new javax.constraints.impl.Problem("optimization");
        Var a = optimization.variable("a", 0, 10);
        Var b = optimization.variable("b", 0, 10);
        optimization.post(new Var[] {a, b}, "<=", 12);
        Var objective = a.plus(b);
        objective.setName("objective");

        Solution optimal = optimization.getSolver()
                .findOptimalSolution(Objective.MAXIMIZE, objective);

        assertNotNull(optimal);
        assertEquals(12, optimal.getValue("objective"));
        assertEquals(12, optimal.getValue("a") + optimal.getValue("b"));
    }

    @Test
    public void honorsSparseDomainsAndMutableBounds() throws Exception {
        Problem problem = new javax.constraints.impl.Problem("domains");
        Var x = problem.variable("x", new int[] {1, 3, 5, 7});
        javax.constraints.impl.Var nativeVariable = (javax.constraints.impl.Var) x;
        nativeVariable.removeValue(3);
        nativeVariable.setMin(2);
        nativeVariable.setMax(6);

        Solution solution = problem.getSolver().findSolution();

        assertNotNull(solution);
        assertEquals(5, solution.getValue("x"));
    }

    @Test
    public void reportsInfeasibleProblems() {
        Problem problem = new javax.constraints.impl.Problem("infeasible");
        Var x = problem.variable("x", 0, 2);
        problem.post(x, "=", 0);
        problem.post(x, "=", 1);

        assertNull(problem.getSolver().findSolution());
    }

    @Test
    public void iteratesAllSolutions() {
        Problem problem = new javax.constraints.impl.Problem("iteration");
        problem.variable("value", 1, 3);
        SolutionIterator iterator = problem.getSolver().solutionIterator();
        int count = 0;
        int total = 0;
        while (iterator.hasNext()) {
            Solution solution = iterator.next();
            count++;
            total += solution.getValue("value");
        }

        assertEquals(3, count);
        assertEquals(6, total);
    }
}
