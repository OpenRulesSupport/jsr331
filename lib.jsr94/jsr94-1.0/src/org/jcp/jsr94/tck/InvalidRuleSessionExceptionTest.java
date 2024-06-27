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
import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleExecutionException;

// internal imports

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.InvalidRuleSessionException class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testInvalidRuleSessionException}
 * <ul>
 * <li>Instance Creation
 * <li>Class Hierarchy
 * <li>Exception Wrapping
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.InvalidRuleSessionException
 */
public class InvalidRuleSessionExceptionTest extends TestCase 
{
	/** InvalidRuleSessionExceptionTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public InvalidRuleSessionExceptionTest(String name)
	{
		super(name);
	}

	/** Initialize the InvalidRuleSessionExceptionTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the InvalidRuleSessionExceptionTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.InvalidRuleSessionException.
	 * 
	 * <p>
	 * <b>Description:</b><br>
	 * Create two instances of the InvalidRuleSessionException. The first
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
	 * <li>Fail: If InvalidRuleSessionException cannot be created by any of
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
	 * InvalidRuleSessionException is unwrapped.
	 * <li>Succeed: If exception can successfully be unwrapped.
	 * </ul>
	 * </ul>
	 */
	public void testInvalidRuleSessionException()
    {
		InvalidRuleSessionException ie1 = null;
		InvalidRuleSessionException ie2 = null;

		try {
			ie1 = new InvalidRuleSessionException(
					"jsr94-test-invalid-rule-session-exception");
			ie2 = new InvalidRuleSessionException(
					"jsr94-test-embedded-invalid-rule-session-exception", ie1);

			// A InvalidRuleSessionException must be a subclass of a
			// RuleExecutionException.
			assertTrue("[InvalidRuleSessionExceptionTest] ",
					   (ie1 instanceof RuleExecutionException));

			// Throw it.
			throw ie2;
		}
		catch (InvalidRuleSessionException ex) {
			// Catch it.
			String s = ex.getMessage();
			Throwable t = ex.getCause();

			// The cause of the exception should be ie1.
			assertEquals("[InvalidRuleSessionExceptionTest] ", t, ie1);

			// The thrown exception should be ie2.
			assertEquals("[InvalidRuleSessionExceptionTest] ", ex, ie2);
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
    }
}
