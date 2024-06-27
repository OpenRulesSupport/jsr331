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
import javax.rules.Handle;
import javax.rules.StatefulRuleSession;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import junit.framework.*;

/**
 * Tests the compliance of {@link javax.rules.Handle} implementation classes.
 * This class performs the following test scenario:
 * <ul>
 * <li> An object is added to a stateful rule session and a handle is obtained.
 *      That object has defined properly the <code>equals</code> and
 *      <code>hashCode</code> methods.
 * <li> The obtained handle must be an instance of <code>Handle</code>.
 * <li> The rule session then must contain the handle.
 * <li> The object returned by {@link StatefulRuleSession#getObject(Handle)} must be
 *      equals to the initial object.
 * <li> The object is changed to be another object of the same class.
 * <li> The rule session is updated with the handle containing the new object.
 * <li> The rule session must again contain the handle.
 * <li> The object returned by {@link StatefulRuleSession#getObject(Handle)} must be
 *      equals to this new object.
 * <li> The handle is removed from the rule session.
 * <li> The rule session must not contain the handle.
 * </ul>
 * <b>Note:</b><br>
 * This test uses a RuleSetExecution definition that is stored in the
 * tck_res_2.xml. You have to change this file accoding to your rule
 * execution set definition. The mainline TCK documentation describes
 * the contents of this rule execution set definition.

 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.Handle
 */
public class HandleTest extends TestCase 
{
  // Stateful rule session.
  private StatefulRuleSession ruleSession;
	
  /** HandleTest constructor.
   * This is the default constructor for a JUnit TestCase.
   *
   * @param name The name of this test.
   * 
   * @see junit.framework.TestCase
   */
  public HandleTest(String name)
  {
    super(name);
  }

  /** Initialize the HandleTest.
   * Initializes a stateful rule ression. This test uses the
   * tck_res_2.xml RuleExecutionSet definition file.
   * @see junit.framework.TestCase#setUp
   */
  public void setUp()
  {
    try {
      ruleSession = TestCaseUtil.getStatefulRuleSession("handleTest",
							"tck_res_2.xml");
    }
    catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /** Cleanup the HandleTest.
   *
   * @see junit.framework.TestCase#tearDown
   */
  public void tearDown()
  {
    ruleSession = null;
  }

  /**
   * A test for <code>javax.rules.Handle</code> implementation classes.
   */
  public void testHandle() throws Exception {
    Object object = "handle";

      Handle handle = ruleSession.addObject(object);
      assertTrue("[HandleTest] instanceof Handle",
		 (handle instanceof Handle));

      assertTrue("[HandleTest] containsObject",
		 ruleSession.containsObject(handle));

      Object newobject = ruleSession.getObject(handle);
      assertTrue("[HandleTest] calling equals",
		 object.equals(newobject));

      newobject = "new handle";
      ruleSession.updateObject(handle,newobject);

      assertTrue("[HandleTest] containsObject after update",
		 ruleSession.containsObject(handle));

      assertTrue("[HandleTest] calling equals after update",
		 newobject.equals(ruleSession.getObject(handle)));

      ruleSession.removeObject(handle);

      assertTrue("[HandleTest] containsObject after remove",
		 !ruleSession.containsObject(handle));
  }
}
