package javax.constraints.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.constraints.Problem;
import javax.constraints.Solution;
import javax.constraints.Var;

import org.junit.Test;

public class JacopAdapterTest {

	@Test
	public void reportsTheJaCoPVersion() {
		Problem problem = new javax.constraints.impl.Problem("version");

		assertTrue(problem.getImplVersion().contains("JaCoP 4.10.0"));
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
	public void usesZeroBasedIndexesForIntegerElements() {
		Problem problem = new javax.constraints.impl.Problem("element");
		Var index = problem.variable("index", 0, 2);
		Var value = problem.element(new int[] {4, 7, 9}, index);
		value.setName("value");
		problem.post(index, "=", 0);

		Solution solution = problem.getSolver().findSolution();

		assertNotNull(solution);
		assertEquals(4, solution.getValue("value"));
	}
}
