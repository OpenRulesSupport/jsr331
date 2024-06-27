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
import javax.rules.admin.Rule;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.RuleServiceProvider;
import javax.rules.admin.RuleExecutionSet;

import java.util.List;
import java.io.InputStream;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.admin.Rule class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRule}
 * <ul>
 * <li>Create Instance
 * <li>Get / Set properties
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.admin.Rule
 */
public class RuleTest extends TestCase 
{
	/** RuleTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public RuleTest(String name)
	{
		super(name);
	}

	/** Initialize the RuleTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the RuleTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.admin.Rule.
	 * A RuleExecutionSet for tck_res_1.xml will be created using the
	 * LocalRuleExecutionSetProvider. The list of rules from this rule
	 * execution set should at least contain 1 rule (as described in
	 * the TCK documentation). Additional test for setting properties
	 * on the rule will be performed.
	 * <p>
	 * <b>Description:</b><br>
	 * <ul>
	 * <li>Create Instance
	 * <ul>
	 * <li>Fail: If any error occurs or no rules can be found in the
	 * tck_res_1.xml rule execution set. 
	 * <li>Succeed: If rules can be retrieved from the rule execution
	 * set and their name and description are set.
	 * </ul>
	 * <li>Get / Set properties
	 * <ul>
	 * <li>Fail: If the added properties can not be retrieved. 
	 * <li>Succeed: If the properties added to this rule can
	 * successfully be retrieved.
	 * </ul>
	 * </ul>
	 */
	public void testRule()
	{
		try {
			// Get the RuleServiceProvider
			RuleServiceProvider serviceProvider =
				TestCaseUtil.getRuleServiceProvider("RuleExecutionSetTest");
			assertNotNull("[RuleTest] " +
						  "RuleServiceProvider not found." ,
						  serviceProvider);

			// Get the RuleAdministrator
			RuleAdministrator ruleAdministrator =
				serviceProvider.getRuleAdministrator();
			assertNotNull("[RuleTest] " +
						  "RuleAdministrator not found.", ruleAdministrator);

			// Test the LocalRuleExecutionSetProvider API
			LocalRuleExecutionSetProvider localProvider =
				ruleAdministrator.getLocalRuleExecutionSetProvider(null);
			assertNotNull("[RuleTest] ", localProvider);

			InputStream inStream =
								  TestCaseUtil.getRuleExecutionSetInputStream("tck_res_1.xml");

			// Create the RuleExecutionSet.
			RuleExecutionSet res = localProvider.createRuleExecutionSet(
				inStream, null);

			inStream.close();

			// Get the rules.
			List ruleList = res.getRules();
			assertNotNull("[RuleTest] No rules found.", ruleList);
			assertTrue("[RuleTest] ",
					   (0 < ruleList.size())); // At least one rule.

			Rule[] rules = (Rule[]) ruleList.toArray(new Rule[0]);
			Rule rule = rules[0];

			// Test the name and description
			assertNotNull("[RuleTest] No rule name.", rule.getName());
			assertNotNull("[RuleTest] No rule description",
						  rule.getDescription());

			// Test the properties.
			rule.setProperty("ruleProperty", ruleList);
			Object obj = rule.getProperty("ruleProperty");
			assertNotNull("[RuleTest] " +
						  "Rule property could not be retrieved.", obj);
			assertEquals("[RuleTest] ",obj, ruleList);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
