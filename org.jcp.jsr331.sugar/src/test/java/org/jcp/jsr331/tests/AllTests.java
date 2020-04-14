//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package org.jcp.jsr331.tests;

import junit.framework.*;

/**
 * Run all the test suites in the TestXYZ Compatibility Kit. 
 */
public class AllTests {
	
	static final String name = "JSR 331 Compatibility TestXYZ";
	
	public static void main(String[] args) {
		TestResult result = junit.textui.TestRunner.run(suite());
		if (result.wasSuccessful())
			System.out.println(name + " was successful. Congratulations!\n");
		else {
			System.out.println(name + " was unsuccessful. Fix errors and try again.\n");
			assert(false);
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite(name);

		//suite.addTestSuite(TestSolutions.class);
		suite.addTestSuite(TestSum.class);
		suite.addTestSuite(TestCompare.class);
		suite.addTestSuite(TestIfThen.class);
		suite.addTestSuite(TestCardinality.class);
		//suite.addTestSuite(TestGlobalCardinality.class);
		//suite.addTestSuite(TestMagicSequence.class);
		suite.addTestSuite(TestMagicSquare.class);
		suite.addTestSuite(TestQueens.class);
		suite.addTestSuite(TestGraphColoring.class);
		suite.addTestSuite(TestBins.class);
		return suite;
	}
}
