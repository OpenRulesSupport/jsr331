/*
 * J A V A  C O	M M U N	I T Y  P R O C E S S
 *
 * J S R  9 4
 *
 * Test	Compatability Kit
 *
 */
package	org.jcp.jsr94.tck.admin;

// java	imports
import javax.rules.admin.RuleAdministrator;
import javax.rules.RuleServiceProvider;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSetProvider;

import java.io.InputStream;

// internal imports
import	org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import junit.framework.*;

/**
 * Test	the javax.rules.admin.RuleAdministrator	class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRuleAdministrator}
 * <ul>
 * <li>Create Instance
 * <li>Get the LocalRuleExecutionSetProvider
 * <li>Get the RuleExecutionSetProvider
 * <li>Register and Deregister a RuleExecutionSet.
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see	javax.rules.admin.RuleAdministrator
 */
public class RuleAdministratorTest extends TestCase
{
	/** RuleAdministratorTest constructor.
	 * This	is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name	of this	test.
	 *
	 * @see	junit.framework.TestCase
	 */
	public RuleAdministratorTest(String name)
	{
		super(name);
	}

	/** Initialize the RuleAdministratorTest.
	 *
	 * @see	junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the	RuleAdministratorTest.
	 *
	 * @see	junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance	for javax.rules.admin.RuleAdministrator.
	 * Get a RuleAdministrator from a RuleServiceProvider. Get the
	 * RuleExecutionSetProvider as well as the
	 * LocalRuleExecutionSetProvider. Create a
	 * RuleExecutionSet via the LocalRuleExecutionSetProvider and try
	 * to register and deregister the RuleExecutionSet. An input stream
	 * to the tck_res_1.xml rule execution set is used to construct the
	 * rule execution set.
	 * <p>
	 * <b>Description:</b><br>
	 * <ul>
	 * <li>Create Instance
	 * <ul>
	 * <li>Fail: If a RuleAdministrator instance cannot be retrieved.
	 * <li>Succeed: If the RuleAdminstrator can successfully be
	 * retrieved from the RuleServiveProvider.
	 * </ul>
	 * <li>Get the LocalRuleExecutionSetProvider
	 * <ul>
	 * <li>Fail: If any error occurs while retrieving the
	 * LocalRuleExecutionSetProvider.
	 * <li>Succeed: If a non null LocalRuleExecutionSetProvider is
	 * retrieved.
	 * </ul>
	 * <li>Get the RuleExecutionSetProvider
	 * <ul>
	 * <li>Fail: If any error occurs while retrieving the
	 * RuleExecutionSetProvider.
	 * <li>Succeed: If a non null RuleExecutionSetProvider is
	 * retrieved.
	 * </ul>
	 * <li>Register and Deregister a RuleExecutionSet.
	 * <ul>
	 * <li>Fail: If any error occurs during the registration and
	 * deregistration of a rule execution set.
	 * <li>Succeed: If a rule execution set can succesfully be
	 * registered and deregistered.
	 * </ul>
	 * </ul>
	 * @see TestCaseUtil#getRuleServiceProvider
	 * @see TestCaseUtil#getRuleExecutionSetInputStream
	 */
	public void testRuleAdministrator()
	{
		try {
			// Get the RuleServiceProvider
			RuleServiceProvider serviceProvider = TestCaseUtil.getRuleServiceProvider("ruleAdministratorTest");
			assertNotNull("[RuleAdministratorTest] " +
						  "RuleServiceProvider not found." , serviceProvider);

			// Get the RuleAdministrator
			RuleAdministrator ruleAdministrator =
				serviceProvider.getRuleAdministrator();
			assertNotNull("[RuleAdministratorTest] " +
						  "RuleAdministrator not found.", ruleAdministrator);

			// Test the LocalRuleExecutionSetProvider API
			LocalRuleExecutionSetProvider localProvider = ruleAdministrator.getLocalRuleExecutionSetProvider(null);
			assertNotNull("[RuleAdministratorTest] ", localProvider);

			// Test the RuleExecutionSetProvider API
			RuleExecutionSetProvider provider = ruleAdministrator.getRuleExecutionSetProvider(null);
			assertNotNull("[RuleAdministratorTest] ", provider);

			InputStream inStream = TestCaseUtil.getRuleExecutionSetInputStream("tck_res_1.xml");
			// Create the rule execution set.
			RuleExecutionSet res = localProvider.createRuleExecutionSet(inStream, null);
			
			assertNotNull("[RuleAdministratorTest] " +
						  "RuleExecutionSet could not be created.", res);
			inStream.close();


			// register the RuleExecutionSet
			ruleAdministrator.registerRuleExecutionSet("testAdmin", res, null);
			// deregister the RuleExecutionSet
			ruleAdministrator.deregisterRuleExecutionSet("testAdmin", null);

		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
