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
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.RuleSession;
import javax.rules.StatefulRuleSession;
import javax.rules.StatelessRuleSession;
import java.io.*;
import java.util.*;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.RuleRuntime class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRuleRuntime}
 * <ul>
 * <li>Retrieve RuleRuntime
 * <li>Registration
 * <li>Create Stateless Rule session
 * <li>Create Stateful Rule session
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.RuleRuntime
 */
public class RuleRuntimeTest extends TestCase 
{
  /** RuleRuntimeTest constructor.
   * This is the default constructor for a JUnit TestCase.
   *
   * @param name The name of this test.
   * 
   * @see junit.framework.TestCase
   */
  public RuleRuntimeTest(String name)
  {
    super(name);
  }

  /** Initialize the RuleRuntimeTest.
   *
   * @see junit.framework.TestCase#setUp
   */
  public void setUp()
  {
  }

  /** Cleanup the RuleRuntimeTest.
   *
   * @see junit.framework.TestCase#tearDown
   */
  public void tearDown()
  {
  }

  /** Test the compliance for javax.rules.RuleRuntime.
   * This test will get the rule service provider and the
   * corresponding rule administrator. Both the stateless
   * (tck_res_1.xml) and stateful (tck_res_2.xml) rule execution sets
   * will be registered. The RuleRuntime is requested from the rule
   * service provider and the RuleRuntime interface API definition is
   * tested.
   * <p>
   * <b>Description:</b><br>
   * <ul>
   * <li>Retrieve RuleRuntime
   * <ul>
   * <li>Fail: If no RuleRuntime instance is returned by the rule
   * service provider.
   * <li>Succeed: If a RuleRuntime instance is returned by the rule
   * service provider.
   * </ul>
   * <li>Registration
   * <ul>
   * <li>Fail: If the registered stateless and stateful rule
   * execution sets are not part of the retrieved registration list.
   * <li>Succeed: If the registered rule execution sets are retrieved.
   * </ul>
   * <li>Create Stateless Rule session
   * <ul>
   * <li>Fail: If any error occurs when creating a stateless rule
   * session.
   * <li>Succeed: If a rule session can successfully be created and
   * is an instance of StatelessRuleSession.
   * </ul>
   * <li>Create Stateful Rule session
   * <ul>
   * <li>Fail: If any error occurs when creating a stateful rule
   * session.
   * <li>Succeed: If a rule session can successfully be created and
   * is an instance of StatefulRuleSession.
   * </ul>
   * </ul>
   *
   * @see TestCaseUtil#getRuleExecutionSetLocation
   * @see TestCaseUtil#getRuleServiceProvider
   */
  public void testRuleRuntime()
  {
    try {
      String statelessUri = "tck_res_1.xml";
      String statefulUri = "tck_res_2.xml";
		
      String location = TestCaseUtil.getRuleExecutionSetLocation();
		
				// Get the RuleServiceProvider
      RuleServiceProvider serviceProvider =
	TestCaseUtil.getRuleServiceProvider("ruleRuntimeTest");
      assertNotNull("[RuleRuntimeTest] " +
		    "RuleServiceProvider not found.", serviceProvider);

      // Get the RuleAdministrator
      RuleAdministrator ruleAdministrator =
	serviceProvider.getRuleAdministrator();
      assertNotNull("[RuleRuntimeTest] " +
		    "RuleAdministrator not found.", ruleAdministrator);

      // Get an input stream for the stateless test XML rule
      // execution  set.
      // Try to load the files from the "rule-execution-set-location". 
      InputStream inStream = new FileInputStream(location +
						 "/" + statelessUri);
      assertNotNull("[RuleRuntimeTest] " +
		    "Input stream for " + location + "/" +
		    statelessUri + " could not be created.", inStream);

      // parse the ruleset from the XML document
      RuleExecutionSet res =
	ruleAdministrator.getLocalRuleExecutionSetProvider( null ).
	createRuleExecutionSet( inStream, null );
      assertNotNull("[RuleRuntimeTest] " +
		    "RuleExecutionSet " + statelessUri +
		    " could not be created.", res);
      inStream.close();

      // register the RuleExecutionSet
      ruleAdministrator.registerRuleExecutionSet(statelessUri,
						 res, null );

      // Get an input stream for the stateful test XML rule
      // execution  set.
      // Try to load the files from the "rule-execution-set-location". 
      inStream = new FileInputStream(location +
				     "/" + statefulUri);
      assertNotNull("[RuleRuntimeTest] " +
		    "Input stream for " + location + "/" + statefulUri +
		    " could not be created.", inStream);

      // parse the ruleset from the XML document
      res = ruleAdministrator.getLocalRuleExecutionSetProvider( null ).
	createRuleExecutionSet( inStream, null );
      assertNotNull("[RuleRuntimeTest] " +
		    "RuleExecutionSet " + statefulUri +
		    " could not be created.", res);
      inStream.close();

      // register the RuleExecutionSet
      ruleAdministrator.registerRuleExecutionSet(statefulUri,
						 res, null );

      // create a RuleRuntime
      RuleRuntime ruleRuntime = serviceProvider.getRuleRuntime();
      assertNotNull("[RuleRuntimeTest] " +
		    "RuleRuntime not created." , ruleRuntime);

      // So we finally have the RuleRuntime, now let's test it.
      List registrations = ruleRuntime.getRegistrations();
      // Check that we retrieved registrations.
      assertNotNull("[RuleRuntimeTest] " +
		    "No registrations found.", registrations);

      // We should have at least two registrations. However other
      // test runs might have created registrations as well, so
      // test for at least 2.
      assertTrue("[RuleRuntimeTest] " +
		 "Not all registrations found.",
		 (2 <= registrations.size()));

      // Check that we got the once that we registered.
      // i.e. statelessUri and statefulUri.
      Iterator itr = registrations.iterator();

      int countRegistrations = 0;

      while (itr.hasNext()) {
	Object next = itr.next();
	assertTrue("Iterator element instanceof String",
		   (next instanceof String));
	String name = (String)next;

	if (statelessUri.equals(name))
	  ++countRegistrations;
				
	if (statefulUri.equals(name))
	  ++countRegistrations;
      }
      // The count should be exactly 2 now, since we counted only
      // the once we registered.
      assertEquals("[RuleRuntimeTest] " +
		   "Not all registrations found.",
		   2, countRegistrations);

      RuleSession ruleSession = null;

      // Create and check a stateless rule session.
      ruleSession = ruleRuntime.
	createRuleSession(statelessUri, null,
			  RuleRuntime.STATELESS_SESSION_TYPE);
      assertNotNull("[RuleRuntimeTest] " +
		    "Stateless rule session could not be created.",
		    ruleSession);
      assertTrue("[RuleRuntimeTest] " +
		 "Rule session is not of Stateless type.",
		 ((ruleSession instanceof StatelessRuleSession)));


      // Create and check a stateful rule session.
      ruleSession = ruleRuntime.createRuleSession(statefulUri, null,
						  RuleRuntime.STATEFUL_SESSION_TYPE);
      assertNotNull("[RuleRuntimeTest] " +
		    "Stateful rule session could not be created.",
		    ruleSession);
      assertTrue("[RuleRuntimeTest] " +
		 "Rule session is not of Stateful type.",
		 ((ruleSession instanceof StatefulRuleSession)));
    }
    catch (Exception e) {
      TestCaseUtil.processTestException(e,this,"testRuleRuntime");
    }
  }
}
