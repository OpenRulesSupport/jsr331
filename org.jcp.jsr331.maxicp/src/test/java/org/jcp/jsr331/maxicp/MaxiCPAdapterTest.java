package org.jcp.jsr331.maxicp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Var;

import org.junit.Test;

public class MaxiCPAdapterTest {
    @Test
    public void solvesLinearAllDifferentModel() {
        Problem p = ProblemFactory.newProblem("linear");
        Var x = p.variable("x", 0, 5);
        Var y = p.variable("y", 0, 5);
        p.post(x.plus(y), "=", 5);
        p.postAllDifferent(new Var[] {x, y});

        Solution solution = p.getSolver().findSolution();

        assertNotNull(solution);
        assertEquals(5, solution.getValue("x") + solution.getValue("y"));
        assertTrue(solution.getValue("x") != solution.getValue("y"));
    }

    @Test
    public void supportsSparseDomainsElementAndLogic() {
        Problem p = ProblemFactory.newProblem("element");
        Var index = p.variable("index", new int[] {0, 2});
        Var value = p.variable("value", 10, 30);
        p.postElement(new int[] {10, 20, 30}, index, "=", value);
        p.linear(index, "=", 0).or(p.linear(value, "=", 30)).post();

        Solution solution = p.getSolver().findSolution();

        assertNotNull(solution);
        assertTrue(solution.getValue("value") == 10 || solution.getValue("value") == 30);
    }

    @Test
    public void enumeratesAndOptimizes() {
        Problem enumeration = ProblemFactory.newProblem("enumeration");
        Var x = enumeration.variable("x", 0, 2);
        Solution[] solutions = enumeration.getSolver().findAllSolutions();
        assertEquals(3, solutions.length);

        Problem optimization = ProblemFactory.newProblem("optimization");
        Var a = optimization.variable("a", 0, 5);
        Var b = optimization.variable("b", 0, 5);
        Var total = optimization.add("total", a.plus(b));
        optimization.post(total, ">=", 6);
        Solution optimum = optimization.getSolver()
                .findOptimalSolution(Objective.MINIMIZE, total);
        assertNotNull(optimum);
        assertEquals(6, optimum.getValue("total"));
    }
}
