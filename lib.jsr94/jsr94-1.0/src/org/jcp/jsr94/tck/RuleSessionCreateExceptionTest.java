/*
 * J A V A  C O M M U N I T Y  P R O C E S S
 *
 * J S R  9 4
 *
 * Test Compatability Kit
 *
 */
package org.jcp.jsr94.tck;

// java imports
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleExecutionException;

// internal imports

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.RuleSessionCreateException class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRuleSessionCreateException}
 * <ul>
 * <li>Instance Creation
 * <li>Class Hierarchy
 * <li>Exception Wrapping
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.RuleSessionCreateException
 */
public class RuleSessionCreateExceptionTest extends TestCase 
{
	/** RuleSessionCreateExceptionTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public RuleSessionCreateExceptionTest(String name)
	{
		super(name);
	}

	/** Initialize the RuleSessionCreateExceptionTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the RuleSessionCreateExceptionTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.RuleSessionCreateException.
	 * 
	 * <p>
	 * <b>Description:</b><br>
	 * Create two instances of the RuleSessionCreateException class. The first
	 * instance will be constructed with an additonal message. The
	 * second instance will have its own message as well as wrap the
	 * first instance. The class hierarchy will be tested. The second
	 * exception will be thrown. The exception will be caught and the
	 * test will verify whether the exception can successfully be
	 * unwrapped.
	 * The following tests will be performed.
	 * <ul>
	 * <li>Instance Creation
	 * <ul>
	 * <li>Fail: If RuleSessionCreateException cannot be created by any of
	 * the JSR specified constructors.
	 * <li>Succeed: If the exception can successfully be created.
	 * </ul>
	 * <li>Class Hierarchy
	 * <ul>
	 * <li>Fail: If superclass is not a RuleExecutionException
	 * <li>Succeed: If the exception is instance of RuleExecutionException.
	 * </ul>
 	 * <li>Exception Wrapping
	 * <ul>
	 * <li>Fail: If any other than the the original
	 * RuleSessionCreateException is unwrapped.
	 * <li>Succeed: If exception can successfully be unwrapped.
	 * </ul>
	 * </ul>
	 */
	public void testRuleSessionCreateException()
	{
		RuleSessionCreateException re1 = null;
		RuleSessionCreateException re2 = null;

		try {
			re1 = new RuleSessionCreateException(
					 "jsr94-test-rule-session-create-exception");
			re2 = new RuleSessionCreateException(
					"jsr94-test-embedded-rule-session-create-exception", re1);

			// A RuleSessionCreateException must be a subclass of a
			// RuleExecutionException.
			assertTrue("[RuleSessionCreateExceptionTest] ",
					   (re1 instanceof RuleExecutionException));

			// Throw it.
			throw re2;
		}
		catch (RuleSessionCreateException ex) {
			// Catch it.
			String s = ex.getMessage();
			Throwable t = ex.getCause();

			// The cause of the exception should be re1.
			assertEquals("[RuleSessionCreateExceptionTest] ", t, re1);

			// The thrown exception should be re2.
			assertEquals("[RuleSessionCreateExceptionTest] ", ex, re2);
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
}
