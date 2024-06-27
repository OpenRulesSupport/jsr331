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
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.rules.ObjectFilter;
import javax.rules.RuleServiceProvider;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import junit.framework.TestCase;

import org.jcp.jsr94.tck.util.TestCaseUtil;
import org.jcp.jsr94.tck.util.TestObjectFilter;

/**
 * Test the javax.rules.admin.RuleExecutionSet class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRuleExecutionSet}
 * <ul>
 * <li>Create Instance (with and without properties)
 * <li>Get/Set properties
 * <li>Get/Set object filters
 * <li>Retrieve Rules from the RuleExecutionSet
 * </ul>
 * </ul>
 * <b>Note:</b><br>
 * The tck_res_1.xml rule execution set is used for testing. For more
 * information on this rule execution set, see the TCK documentation.
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.admin.RuleExecutionSet
 * @see TestCaseUtil#getRuleExecutionSetInputStream
 */
public class RuleExecutionSetTest extends TestCase 
{
	/** RuleExecutionSetTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public RuleExecutionSetTest(String name)
	{
		super(name);
	}

	/** Initialize the RuleExecutionSetTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the RuleExecutionSetTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.admin.RuleExecutionSet.
	 * Get a RuleAdministrator from a RuleServiceProvider and get the
	 * LocalRuleExecutionSetProvider from the Administrator. Create a
	 * RuleExecutionSet via the LocalRuleExecutionSetProvider. An input
	 * stream to the tck_res_1.xml rule execution set is used to
	 * construct the rule execution set. Test whether or not properties
	 * can be set and retrieved, object filters can be set and
	 * retrieved. Iterate through the rules of this rule execution set.
	 * 
	 * <p>
	 * <b>Description:</b><br>
	 * <ul>
	 * <li>Create Instance (with and without properties)
	 * <ul>
	 * <li>Fail: If any errors occur during the creation of the rule
	 * execution set.
	 * <li>Succeed: If an instance of a RuleExecutionSet could
	 * successfully be created and the specified properties are
	 * available.
	 * </ul>
	 * <li>Get/Set properties
	 * <ul>
	 * <li>Fail: If the properties could not be set or retrieved.
	 * <li>Succeed: If all the properties can successfully be set and
	 * retrieved.
	 * </ul>
	 * <li>Get/Set object filters
	 * <ul>
	 * <li>Fail: If a failure occurs during the set or get of an
	 * ObjectFilter.
	 * <li>Succeed: If an ObjectFilter can successfully be set and
	 * retrieved.
	 * </ul>
	 * <li>Retrieve Rules from the RuleExecutionSet
	 * <ul>
	 * <li>Fail: If no rules are found.
	 * <li>Succeed: If at least one rule definition can be found.
	 * </ul>
	 * </ul>
	 */
  public void testRuleExecutionSet()
  {
    try {
      // Get the RuleServiceProvider
      RuleServiceProvider serviceProvider =
	TestCaseUtil.getRuleServiceProvider("RuleExecutionSetTest");
      assertNotNull("[RuleExecutionSetTest] " +
		    "RuleServiceProvider not found." , serviceProvider);

      // Get the RuleAdministrator
      RuleAdministrator ruleAdministrator =
	serviceProvider.getRuleAdministrator();
      assertNotNull("[RuleExecutionSetTest] " +
		    "RuleAdministrator not found.", ruleAdministrator);

      // Test the LocalRuleExecutionSetProvider API
      LocalRuleExecutionSetProvider localProvider =
	ruleAdministrator.getLocalRuleExecutionSetProvider(null);
      assertNotNull("[RuleExecutionSetTest]", localProvider);

      InputStream inStream =
	TestCaseUtil.getRuleExecutionSetInputStream("tck_res_1.xml");

      // Create an object filter.
      ObjectFilter filter = new TestObjectFilter(
						 TestObjectFilter.CUSTOMER_FILTER);
			
      // Create some properties.
      HashMap creationMap = new HashMap();

      // Create the RuleExecutionSet with the properties.
      RuleExecutionSet res = localProvider.createRuleExecutionSet(
								  inStream, creationMap);

      // set a property on the RuleExecutionSet
      res.setProperty( "objectFilter", filter);

      inStream.close();

      // Test the basics.
      assertNotNull("[RuleExecutionSetTest] " +
		    "RuleExecutionSet could not be created.", res);
      // Do we need to have a description ?
      assertNotNull("[RuleExecutionSetTest] " +
		    "No description found.", res.getDescription());
      assertNotNull("[RuleExecutionSetTest] " +
		    "No name found.", res.getName());
			
      // Test whether we can get the objectFilter back. The
      // object filter was specified as a property during
      // creation.
      Object obj = res.getProperty("objectFilter");
      assertNotNull("[RuleExecutionSetTest] " +
		    "Could not retrieve property", obj);
      assertEquals("[RuleExecutionSetTest]", filter, obj);

      // Test setting additional properties
      res.setProperty("additionalProperty", filter);
      obj = res.getProperty("additionalProperty");
      assertNotNull("[RuleExecutionSetTest] " +
		    "Could not retrieve property", obj);
      assertEquals("[RuleExecutionSetTest] ", filter, obj);

      // Test setting a default object filter
      res.setDefaultObjectFilter(filter.getClass().getName());
      String objectFilter = res.getDefaultObjectFilter();
      assertNotNull("[RuleExecutionSetTest] " +
		    "Could not retrieve object filter", objectFilter);
      assertEquals("[RuleExecutionSetTest] ",
		   filter.getClass().getName(), objectFilter);

      // Test the getRules.
      List rules = res.getRules();
      assertNotNull("[RuleExecutionSetTest] " +
		    "No rules found.", rules);
      assertTrue("[RuleExecutionSetTest] " ,
		 (0 < rules.size())); // At least one rule.
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
    
  }
}
