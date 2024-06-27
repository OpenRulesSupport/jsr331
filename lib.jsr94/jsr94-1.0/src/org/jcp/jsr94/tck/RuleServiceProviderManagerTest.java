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
import javax.rules.RuleServiceProviderManager;
import javax.rules.ConfigurationException;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.RuleServiceProviderManager class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRuleServiceProviderManager}
 * <ul>
 * <li>RuleServiceProvider Property specification
 * <li>Load the RuleServiceProvider class.
 * <li>Register the RuleServiceProvider class.
 * <li>Deregister the RuleServiceProvider class.
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.RuleServiceProviderManager
 */
public class RuleServiceProviderManagerTest extends TestCase 
{
	/** RuleServiceProviderManagerTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public RuleServiceProviderManagerTest(String name)
	{
		super(name);
	}

	/** Initialize the RuleServiceProviderManagerTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the RuleServiceProviderManagerTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.RuleServiceProviderManager.
	 * 
	 * <p>
	 * <b>Description:</b><br>
	 * Test the basic functioning of the RuleServiceProviderManager.<br>
	 * The rule engine vendor specific rule service provider should be
	 * specified in the "tck.conf" configuration file.<br>
	 * The following tests will be performed:<br>
	 * <ul>
	 * <li>Property specification
	 * <ul>
	 * <li>Fail: If the "rule-service-provider" configuration setting
	 * has not been set.
	 * <li>Succeed: When the javax.rules.ruleServiceProvider is not
	 * null.
	 * </ul>
	 * <li>Load the RuleServiceProvider class.
	 * <ul>
	 * <li>Fail: If the specified class cannot be found.
	 * <li>Succeed: If the specified class can successfull be loaded.
	 * </ul>
	 * <li>Register the RuleServiceProvider class.
	 * <ul>
	 * <li>Fail: If an exception occurs during registration.
	 * <li>Succeed: If the specified provider can be successfully
	 * retrieved from the RuleServiceProviderManager.
	 * </ul>
	 * <li>Deregister the RuleServiceProvider class.
	 * <ul>
	 * <li>Fail: If the rule service provider can still be found.
	 * <li>Succeed: If the specified provider has been successfully
	 * removed from the RuleServiceProviderManager.
	 * </ul>
	 * </ul>
	 * @see TestCaseUtil#getRuleServiceProvider
	 */
	public void testRuleServiceProviderManager()
	{
		// The rule engine vendor specific rule service provider should
		// be specified in the tck.conf configuration file.
		// Use the TestCaseUtil utility class to retrieve this setting.
		String ruleServiceProvider = TestCaseUtil.getRuleServiceProvider();

		// Fail this test if no provider has been specified.
		if (ruleServiceProvider == null) {
			fail("No setting for rule-service-provider found. Check you tck.conf configuration file.");
		}

		// Load the provider.
		Class ruleServiceProviderClass = null;
		try {
			ruleServiceProviderClass = Class.forName(ruleServiceProvider);
		}
		catch (ClassNotFoundException e) {
			fail(e.getMessage());
		}
		assertNotNull("[RuleServiceProviderManagerTest] ",
					  ruleServiceProviderClass);

		try {
			// Register the provider.
			RuleServiceProviderManager.registerRuleServiceProvider("test",
				ruleServiceProviderClass);
			// Retrieve the provider
			Object object = RuleServiceProviderManager.getRuleServiceProvider(
				"test");
			assertEquals("[RuleServiceProviderManagerTest] ",
						 ruleServiceProviderClass, object.getClass());

			// Deregister the provider.
			RuleServiceProviderManager.deregisterRuleServiceProvider("test");

			try {
				// Retrieve the provider
				object = RuleServiceProviderManager.getRuleServiceProvider(
					"test");
				assertNull("[RuleServiceProviderManagerTest] " +
					  "Deregistered provider still in list of registrations.",
					   object);
			}
			catch(ConfigurationException ce) {
				// Do nothing, we expected this exception. The
				// RuleServiceProviderManager will throw a
				// ConfigurationException when a call to
				// getRuleServiceProvider is made on an unknown uri.
				// Since we deregistered the provider, we should get
				// this exception.
			}
			
		}
		catch (ConfigurationException e) {
			fail(e.getMessage());
		}
	}
}
