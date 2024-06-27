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
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleAdministrator;
import javax.rules.RuleServiceProvider;

import java.io.Reader;
import java.io.InputStream;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.admin.LocalRuleExecutionSetProvider class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testLocalRuleExecutionSetProvider}
 * <ul>
 * <li>Create Instance
 * <li>InputStream
 * <li>Reader
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
 * @see javax.rules.admin.LocalRuleExecutionSetProvider
 * @see TestCaseUtil#getRuleExecutionSetInputStream
 * @see TestCaseUtil#getRuleExecutionSetReader
 */
public class LocalRuleExecutionSetProviderTest extends TestCase 
{
	/** LocalRuleExecutionSetProviderTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public LocalRuleExecutionSetProviderTest(String name)
	{
		super(name);
	}

	/** Initialize the LocalRuleExecutionSetProviderTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the LocalRuleExecutionSetProviderTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.admin.LocalRuleExecutionSetProvider.
	 * Get the rule engine vendor specific implementation of the
	 * LocalRuleExecutionSetProvider via the RuleServiceProvider
	 * specified in the tck.conf configuration file. Get the
	 * RuleAdministrator from this service provider which will have a
	 * reference to a LocalRuleExecutionSetProvider. The test will
	 * continue with constructing a RuleExecutionSet from an
	 * InputStream and a Reader. In both cases the "tck_res_1.xml" rule
	 * execution set definition will be used as input.
	 * <p>
	 * <b>Description:</b><br>
	 * <ul>
	 * <li>Create Instance
	 * <ul>
	 * <li>Fail: If any errors occur when trying to get a reference to
	 * the LocalRuleExecutionSetProvider.
	 * <li>Succeed: If a reference to an instance of the
	 * LocalRuleExecutionSetProvider is successfully obtained.
	 * </ul>
	 * <li>InputStream
	 * <ul>
	 * <li>Fail: If any errors occur during the creation of a
	 * RuleExecutionSet from an InputStream.
	 * <li>Succeed: If a RuleExecutionSet can successfully be created
	 * from an InputStream.
	 * </ul>
	 * <li>Reader
	 * <ul>
	 * <li>Fail: If any errors occur during the creation of a
	 * RuleExecutionSet from a Reader.
	 * <li>Succeed: If a RuleExecutionSet can successfully be created
	 * from a Reader.
	 * </ul>
	 * </ul>
	 * @see TestCaseUtil#getRuleExecutionSetInputStream
	 * @see TestCaseUtil#getRuleExecutionSetReader
	 */
	public void testLocalRuleExecutionSetProvider() throws Exception
	{
            // Get the RuleServiceProvider
            RuleServiceProvider serviceProvider =
                TestCaseUtil.getRuleServiceProvider("LocalResTest");
            assertNotNull("[localRuleExecutionSetProviderTest] " +
                          "RuleServiceProvider not found." ,
                          serviceProvider);
            
            // Get the RuleAdministrator
            RuleAdministrator ruleAdministrator =
                serviceProvider.getRuleAdministrator();
            assertNotNull("[localRuleExecutionSetProviderTest] " +
                          "RuleAdministrator not found.", ruleAdministrator);
            
            // Get the LocalRuleExecutionSetProvider
            LocalRuleExecutionSetProvider localProvider = ruleAdministrator.getLocalRuleExecutionSetProvider(null);
            assertNotNull("[localRuleExecutionSetProviderTest] ",
                          localProvider);
            
            // Now test the API.
            RuleExecutionSet res = null;
            
            InputStream inStream = TestCaseUtil.getRuleExecutionSetInputStream("tck_res_1.xml");
            res = localProvider.createRuleExecutionSet(inStream, null);
            assertNotNull("[localRuleExecutionSetProviderTest] " +
                          "Could not created RuleExecutionSet from an InputStream", res);
            
            Reader reader = TestCaseUtil.getRuleExecutionSetReader("tck_res_1.xml");
            res = localProvider.createRuleExecutionSet(reader, null);
            assertNotNull("[localRuleExecutionSetProviderTest] " +
                          "Could not created RuleExecutionSet from a Reader", res);
        }
}
