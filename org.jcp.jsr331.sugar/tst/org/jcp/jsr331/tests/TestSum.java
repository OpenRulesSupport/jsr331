//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package org.jcp.jsr331.tests;


import javax.constraints.ProblemFactory;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.ValueSelectorType;
import javax.constraints.Problem;
import javax.constraints.impl.search.selectors.ValueSelectorMax;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestSum extends TestCase {
	
	Problem p;
	Var sum; 

	public TestSum(String name) {
		super(name);
	}

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestSum.class));
	}
	
	public void defineProblem() {
		p = ProblemFactory.newProblem("Test");
		Var[] vars = p.variableArray("vars", 0, 10, 10);

		int sumMin = 0;
		int sumMax = 0;
		for (int i = 0; i < vars.length; i++) {
			if (i%2 == 0)
				p.post(vars[i],">",i);
			else
				p.post(vars[i],"<",i);
			sumMin += vars[i].getMin();
			sumMax += vars[i].getMax();
		}
		sum = p.variable("sum",sumMin,sumMax);
		p.post(vars, "=", sum);
	}

	public void testOne() {
		defineProblem();
		p.log("TestSum: findSolution");
		Solver solver = p.getSolver();
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setValueSelectorType(ValueSelectorType.MAX);
		Solution solution = solver.findSolution();
		assertNotNull(solution);
		solution.log();
		//assertEquals(solution.getValue("sum"),70);
	}
	
//	public void testAll() {
//		defineProblem();
//		problem.log("TestSum: findAllSolution");
//		Solver solver = problem.getSolver();
//		int N = 10;
//		solver.setMaxNumberOfSolutions(N);
//		Solution[] solutions = solver.findAllSolutions();
//		assertNotNull(solutions);
//		assertEquals(solutions.length,N);
//		for (int i = 0; i < solutions.length; i++) 
//			solutions[i].log();
//	}
	
	public void testOptimal() {
		defineProblem();
		p.log("TestSum: findOptimalSolution");
		Solver solver = p.getSolver();
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setValueSelector(new ValueSelectorMax());
		Solution solution = solver.findOptimalSolution(sum);
		if (solution != null)
			solution.log();
		assertNotNull(solution);
		assertEquals(solution.getValue("sum"),25);
	}
}