//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package junits;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import javax.constraints.*;

public class TestIfThen extends TestCase {
	
	public TestIfThen(String name) {
		super(name);
	}

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestIfThen.class));
	}

	public void test() {
		Problem p = ProblemFactory.newProblem("ifthen");
		Var a = p.variable("a",0, 10);
		Var b = p.variable("b",0, 10);

		Constraint c1 = p.linear(a,"=",3);
		Constraint c2 = p.linear(b,"=",7);
		c1.implies(c2).post();
		c1.post();
		
		Solver solver = p.getSolver();
		Solution solution = solver.findSolution();
		assertNotNull(solution);
		assertEquals(solution.getValue("a"),3);
		assertEquals(solution.getValue("b"),7);
		solution.log();
	}
}