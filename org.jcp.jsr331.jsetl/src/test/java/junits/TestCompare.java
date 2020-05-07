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


public class TestCompare extends TestCase {

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestCompare.class));
	}
	
	public void testExecute() {
		try {
			Problem p = ProblemFactory.newProblem("test");
			Var[] vars = p.variableArray("x", 0, 100, 10);

			for (int i = 0; i < vars.length; i++) {
				if (i%2 == 0)
					p.post(vars[i],"!=",31);
				p.post(vars[i],"<",60);
				p.post(vars[i],">",30);
				if (i < vars.length - 1) {
					Constraint c1 = p.linear(vars[i],"<=",32);
					Constraint c2 = p.linear(vars[i+1],">",40);
					c1.implies(c2).post();
				}
			}
			Solver solver = p.getSolver();
			Solution solution = solver.findSolution();
			assertNotNull(solution);
			solution.log();
		} catch (Exception e) {
			fail("TestCompare failed!");
		}
	}
}