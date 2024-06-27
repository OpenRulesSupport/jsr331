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
import javax.rules.admin.RuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleAdministrator;
import javax.rules.RuleServiceProvider;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import junit.framework.*;
import org.w3c.dom.Element;

/**
 * Test the javax.rules.admin.RuleExecutionSetProvider class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRuleExecutionSetProvider}
 * <ul>
 * <li>Create Instance
 * <li>RuleExecutionSet form a Document element
 * <li>RuleExecutionSet form a uri
 * </ul>
 * </ul>
 * <b>Note:</b><br>
 * See the TCK documentation for more information on the rule execution
 * set definition that is used for testing this provider API. The
 * actual loading of the rule execution set is handled by the
 * TestCaseUtil class file.
 * 
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.admin.RuleExecutionSetProvider
 * @see TestCaseUtil#getRuleExecutionSetDocumentElement
 */
public class RuleExecutionSetProviderTest extends TestCase 
{
	/** RuleExecutionSetProviderTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public RuleExecutionSetProviderTest(String name)
	{
		super(name);
	}

	/** Initialize the RuleExecutionSetProviderTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the RuleExecutionSetProviderTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.admin.RuleExecutionSetProvider.
	 * Get the rule engine vendor specific implementation of the
	 * RuleExecutionSetProvider via the RuleServiceProvider
	 * specified in the tck.conf configuration file. Get the
	 * RuleAdministrator from this service provider which will have a
	 * reference to a RuleExecutionSetProvider. The test will
	 * continue with constructing a RuleExecutionSet from an
	 * String (uri) and a DOM Document element.
	 * In both cases the "tck_res_1.xml" rule execution set definition
	 * will be used as input.
	 * 
	 * <p>
	 * <b>Description:</b><br>
	 * <ul>
	 * <li>Create Instance
	 * <ul>
	 * <li>Fail: If any errors occur when trying to get a reference to
	 * the RuleExecutionSetProvider.
	 * <li>Succeed: If a reference to an instance of the
	 * RuleExecutionSetProvider is successfully obtained.
	 * </ul>
	 * <li>Document element
	 * <ul>
	 * <li>Fail: If any errors occur during the creation of a
	 * RuleExecutionSet from a DOM Document element.
	 * <li>Succeed: If a RuleExecutionSet can successfully be created
	 * from a DOM Document element.
	 * </ul>
	 * <li>String (uri) (currently disabled, as the TCK does not know the
     * format of the rule engine's URIs). review DCS - this should be externalized.
	 * <ul>
	 * <li>Fail: If any errors occur during the creation of a
	 * RuleExecutionSet from a URI.
	 * <li>Succeed: If a RuleExecutionSet can successfully be created
	 * from a URI.
	 * </ul>
	 * </ul>
	 * @see TestCaseUtil#getRuleExecutionSetDocumentElement
	 */
	public void testRuleExecutionSetProvider()
	{
		try {
			// Get the RuleServiceProvider
			RuleServiceProvider serviceProvider =
				TestCaseUtil.getRuleServiceProvider("ResTest");
			assertNotNull("[RuleExecutionSetProviderTest] " +
						  "RuleServiceProvider not found." , serviceProvider);

			// Get the RuleAdministrator
			RuleAdministrator ruleAdministrator =
				serviceProvider.getRuleAdministrator();
			assertNotNull("[RuleExecutionSetProviderTest] " +
						  "RuleAdministrator not found.", ruleAdministrator);

			// Get the RuleExecutionSetProvider
			RuleExecutionSetProvider provider = ruleAdministrator.getRuleExecutionSetProvider(null);
			assertNotNull("[RuleExecutionSetProviderTest] ", provider);

			// Now test the API.
			RuleExecutionSet res = null;

			Element docElement =
					TestCaseUtil.getRuleExecutionSetDocumentElement("tck_res_1.xml");
			res = provider.createRuleExecutionSet(docElement, null);
			assertNotNull("[RuleExecutionSetProviderTest] " +
				"Could not created RuleExecutionSet from a DOM Document element", res);

			String uri = "" + TestCaseUtil.getRuleExecutionSetLocation() +
						 "/tck_res_1.xml";
			
            // review DCS - I have removed this test, as the TCK cannot
            // know what the format of the rule engine's URIs is.
            // we should load the URI from an external file or pluggable-Factory
//			res = provider.createRuleExecutionSet(uri, null);
//			assertNotNull("[RuleExecutionSetProviderTest] " +
//				 "Could not created RuleExecutionSet from the URI " +
//						  uri, res);
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
