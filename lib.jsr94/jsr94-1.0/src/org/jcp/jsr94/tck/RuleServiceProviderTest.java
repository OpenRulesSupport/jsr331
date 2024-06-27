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
import javax.rules.RuleServiceProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.RuleRuntime;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;
import org.jcp.jsr94.tck.util.TestRuleServiceProvider;

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.RuleServiceProvider class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRuleServiceProvider}
 * <ul>
 * <li>Create Instance
 * <li>Retrieve RuleAdministrator
 * <li>Retrieve RuleRuntime
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.RuleServiceProvider
 */
public class RuleServiceProviderTest extends TestCase 
{
  /** RuleServiceProviderTest constructor.
   * This is the default constructor for a JUnit TestCase.
   *
   * @param name The name of this test.
   * 
   * @see junit.framework.TestCase
   */
  public RuleServiceProviderTest(String name)
  {
    super(name);
  }

  /** Initialize the RuleServiceProviderTest.
   *
   * @see junit.framework.TestCase#setUp
   */
  public void setUp()
  {
  }

  /** Cleanup the RuleServiceProviderTest.
   *
   * @see junit.framework.TestCase#tearDown
   */
  public void tearDown()
  {
  }

  /** Test the compliance for javax.rules.RuleServiceProvider.
   * This test will retrieve the rule engine vendor specific rule
   * service provider class name from the tck.conf configuration
   * file. An instance will be created via the static createInstance
   * method of the RuleServiceProvider class. The rule administrator
   * and rule runtime APIs will be tested.
   * <p>
   * <b>Description:</b><br>
   * <ul>
   * <li>Create Instance
   * <ul>
   * <li>Fail: If any exception occurs during the creation of the
   * rule service provider.
   * <li>Succeed: If the rule service provider can successfully be
   * instantiated.
   * </ul>
   * <li>Retrieve RuleAdministrator
   * <ul>
   * <li>Fail: If any exception occurs during the creation of the
   * rule service provider.
   * <li>Succeed: If the rule service provider can successfully be
   * instantiated.
   * </ul>
   * <li>Retrieve RuleRuntime
   * <ul>
   * <li>Fail: If any exception occurs during the creation of the
   * rule service provider.
   * <li>Succeed: If the rule service provider can successfully be
   * instantiated.
   * </ul>
   * </ul>
   *
   * @see TestCaseUtil#getRuleServiceProvider
   * @see TestRuleServiceProvider#createInstance
   */
  public void testRuleServiceProvider()
  {
    try {
      // Get the name of the vendor specific RuleServiceProvider.
      // as specified in the tck.conf configuration file.
      String providerName = TestCaseUtil.getRuleServiceProvider();
      assertNotNull("[RuleServiceProviderTest] " +
		    "No rule service provider name found.",
		    providerName);
      
      // Create the provider.
      Object obj = TestRuleServiceProvider.createInstance(providerName);

      // Check whether or not it is indeed a rule service
      // provider.
      assertNotNull("[RuleServiceProviderTest] " +
		    "Could not instantiate a Rule service provider.",
		    obj);
      assertTrue("[RuleServiceProviderTest] ",
		 (obj instanceof RuleServiceProvider));

      RuleServiceProvider provider = (RuleServiceProvider) obj;

      // Test the API.

      // Get the rule adminstrator.
      obj = provider.getRuleAdministrator();
			
      // Check whether or not it is indeed a rule administrator.
      assertNotNull("[RuleServiceProviderTest] " +
		    "Could not retrieve a rule administrator.", obj);
      assertTrue("[RuleServiceProviderTest] ",
		 (obj instanceof RuleAdministrator));
      
      // Get the rule runtime.
      obj = provider.getRuleRuntime();
      
      // Check whether or not it is indeed a rule runtime.
      assertNotNull("[RuleServiceProviderTest] " +
		    "Could not retrieve a rule runtime.", obj);
      assertTrue("[RuleServiceProviderTest] ",
		 (obj instanceof RuleRuntime));
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
