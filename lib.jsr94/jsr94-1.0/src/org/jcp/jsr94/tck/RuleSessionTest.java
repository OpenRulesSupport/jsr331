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
import javax.rules.RuleSession;
import javax.rules.RuleExecutionSetMetadata;
import javax.rules.RuleRuntime;


// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.RuleSession class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testRuleSession}
 * <ul>
 * <li>Retrieve RuleExecutionSetMetadata
 * <li>Verify RuleSession type
 * <li>Release the session
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.RuleSession
 */
public class RuleSessionTest extends TestCase 
{
	/** RuleSessionTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public RuleSessionTest(String name)
	{
		super(name);
	}

	/** Initialize the RuleSessionTest.
	 *
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the RuleSessionTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.RuleSession.
	 * This test will create a stateless and stateful rule session
	 * using the TestCaseUtil convenience methods. On each rule
	 * sesssion the following tests will be performed.
	 * <p>
	 * <b>Description:</b><br>
	 * <ul>
	 * <li>Retrieve RuleExecutionSetMetadata
	 * <ul>
	 * <li>Fail: If any error occurs when retrieving the metadata.
	 * <li>Succeed: If a non null RuleExecutionSetMetadata is retrieved.
	 * </ul>
	 * <li>Verify RuleSession type
	 * <ul>
	 * <li>Fail: If the session type does not correspond with the
	 * created session.
	 * <li>Succeed: If the session type is equal to the type specified
	 * during creation of the session.
	 * </ul>
	 * <li>Release the session
	 * <ul>
	 * <li>Fail: If any error occurs during the release of the session.
	 * <li>Succeed: If the session can successfully be released.
	 * </ul>
	 * </ul>
	 *
	 * @see TestCaseUtil#getStatelessRuleSession
	 * @see TestCaseUtil#getStatefulRuleSession
	 */
	public void testRuleSession()
	{
		try {
			RuleExecutionSetMetadata data = null;
			// Create a stateless session.
			RuleSession ruleSession =
					TestCaseUtil.getStatelessRuleSession("ruleSessionTest",
								"tck_res_1.xml");
			assertNotNull("[RuleSessionTest] ", ruleSession);

			// Retrieve the metadata.
			assertNotNull("[RuleSessionTest] ",
						  ruleSession.getRuleExecutionSetMetadata());
			
			// Check the rule session.
			assertTrue("[RuleSessionTest] ",
					   (ruleSession.getType() == RuleRuntime.STATELESS_SESSION_TYPE));

			// Let's release the session.
			ruleSession.release();

			// Create a stateful session.
			ruleSession = TestCaseUtil.getStatefulRuleSession(
								"ruleSessionTest",
								"tck_res_2.xml");
			assertNotNull("[RuleSessionTest] ", ruleSession);
			
			// Retrieve the metadata.
			assertNotNull("[RuleSessionTest] ",
						  ruleSession.getRuleExecutionSetMetadata());

			// Check the rule session.
			assertTrue("[RuleSessionTest] ",
					   (ruleSession.getType() == RuleRuntime.STATEFUL_SESSION_TYPE));
			// Let's release the session.
			ruleSession.release();
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		}

	}
}
