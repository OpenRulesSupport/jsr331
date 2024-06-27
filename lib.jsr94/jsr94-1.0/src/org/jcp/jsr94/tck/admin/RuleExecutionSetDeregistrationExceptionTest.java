/*
 * J A V A  C O M M U N I T Y  P R O C E S S
 *
 * J S R  9 4
 *
 * Test Compatability Kit
 *
 */
package org.jcp.jsr94.tck.admin;

// java imports
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleAdministrationException;

// internal imports

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.admin.RuleExecutionSetDeregistrationException
 * class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRuleExecutionSetUnregisterException}
 * <ul>
 * <li>Instance Creation
 * <li>Class Hierarchy
 * <li>Exception Wrapping
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.admin.RuleExecutionSetDeregistrationException
 */
public class RuleExecutionSetDeregistrationExceptionTest extends TestCase
{
	/** RuleExecutionSetDeregistrationExceptionTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 *
	 * @see junit.framework.TestCase
	 */
	public RuleExecutionSetDeregistrationExceptionTest(String name)
	{
		super(name);
	}

	/** Initialize the RuleExecutionSetDeregistrationExceptionTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the RuleExecutionSetDeregistrationExceptionTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for
	 * javax.rules.admin.RuleExecutionSetDeregistrationException.
	 *
	 * <p>
	 * <b>Description:</b><br>
	 * Create two instances of the RuleExecutionSetDeregistrationException
	 * class. The first
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
	 * <li>Fail: If RuleExecutionSetDeregistrationException cannot be created by any of
	 * the JSR specified constructor.
	 * <li>Succeed: If the exception can successfully be created.
	 * </ul>
	 * <li>Class Hierarchy
	 * <ul>
	 * <li>Fail: If superclass is not a RuleAdministrationException
	 * <li>Succeed: If the exception is instance of RuleAdministrationException.
	 * </ul>
 	 * <li>Exception Wrapping
	 * <ul>
	 * <li>Fail: If any other than the the original
	 * RuleExecutionSetDeregistrationException is unwrapped.
	 * <li>Succeed: If exception can successfully be unwrapped.
	 * </ul>
	 * </ul>
	 */
	public void testRuleExecutionSetUnregisterException()
	{
		RuleExecutionSetDeregistrationException re1 = null;
		RuleExecutionSetDeregistrationException re2 = null;

		try {
			re1 = new RuleExecutionSetDeregistrationException(
				"jsr94-test-res-unregister-exception");
			re2 = new RuleExecutionSetDeregistrationException(
				"jsr94-test-embedded-res-unregister-exception", re1);

            // A RuleExecutionSetDeregistrationException must be a subclass of a
			// RuleAdministrationException.
			assertTrue("[RuleExecutionSetDeregistrationExceptionTest] ",
					   (re1 instanceof RuleAdministrationException));

			// Throw it.
			throw re2;
		}
		catch (RuleExecutionSetDeregistrationException ex) {
			// Catch it.
			String s = ex.getMessage();
			Throwable t = ex.getCause();

			// The cause of the exception should be re1.
			assertEquals("[RuleExecutionSetDeregistrationExceptionTest]", t, re1);

			// The thrown exception should be re2.
			assertEquals("[RuleExecutionSetDeregistrationExceptionTest]", ex, re2);
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
}
