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
import javax.rules.ConfigurationException;
import javax.rules.RuleException;

// internal imports

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.ConfigurationException class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testConfigurationException}
 * <ul>
 * <li>Instance Creation
 * <li>Class Hierarchy
 * <li>Exception Wrapping
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.ConfigurationException
 */
public class ConfigurationExceptionTest extends TestCase 
{
	/** ConfigurationExceptionTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public ConfigurationExceptionTest(String name)
	{
		super(name);
	}

	/** Initialize the ConfigurationExceptionTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the ConfigurationExceptionTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.ConfigurationException.
	 * 
	 * <p>
	 * <b>Description:</b><br>
	 * Create two instances of the ConfigurationException. The first
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
	 * <li>Fail: If ConfigurationException cannot be created by any of
	 * the JSR specified constructor.
	 * <li>Succeed: If the exception can successfully be created.
	 * </ul>
	 * <li>Class Hierarchy
	 * <ul>
	 * <li>Fail: If superclass is not a RuleException
	 * <li>Succeed: If the exception is instance of RuleException.
	 * </ul>
 	 * <li>Exception Wrapping
	 * <ul>
	 * <li>Fail: If any other than the the original
	 * ConfigurationException is unwrapped.
	 * <li>Succeed: If exception can successfully be unwrapped.
	 * </ul>
	 * </ul>
	 */
	public void testConfigurationException()
    {
		ConfigurationException ce1 = null;
		ConfigurationException ce2 = null;

		try {
			ce1 = new ConfigurationException(
				"jsr94-test-configuration-exception");
			ce2 = new ConfigurationException(
				"jsr94-test-embedded-configuration-exception", ce1);

			// A ConfigurationException must be a subclass of a
			// RuleException.
			assertTrue("[ConfigurationExceptionTest] ",
					   (ce1 instanceof RuleException));

			// Throw it.
			throw ce2;
		}
		catch (ConfigurationException ex) {
			// Catch it.
			String s = ex.getMessage();
			Throwable t = ex.getCause();
			
			// The cause of the exception should be ce1.
			assertEquals("[ConfigurationExceptionTest] ", t, ce1);

			// The thrown exception should be ce2.
			assertEquals("[ConfigurationExceptionTest] ", ex, ce2);
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
    }
}
