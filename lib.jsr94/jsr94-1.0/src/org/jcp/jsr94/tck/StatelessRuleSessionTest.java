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
import javax.rules.StatelessRuleSession;
import javax.rules.ObjectFilter;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;
import org.jcp.jsr94.tck.util.TestObjectFilter;
import org.jcp.jsr94.tck.model.Customer;
import org.jcp.jsr94.tck.model.Invoice;

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.StatelessRuleSession class.
 * <p>
 * The definition of the rule execution set can be found in the
 * "tck_res_1.xml" file.<br>
 * This RuleExecutionSet will be invoked by the TCK in a stateless
 * manner.<br>
 * The rule execution set must have support for the following business
 * object model:
 * <ul>
 * <li>Customer Class.<br>
 * The Customer business object is a simple business object that
 * contains a name and credit limit property. The definition of this
 * class can be found in {@link org.jcp.jsr94.tck.model.Customer}
 * <li>Invoice Class.<br>
 * The Invoice business object is a simple business object that
 * contains a description, amount, and status property. The definition
 * of this class can be found in {@link org.jcp.jsr94.tck.model.Invoice}
 * </ul>
 * <p>
 * The rule execution set has the following definition:
 * <ul>
 * <li>Support Customer and Invoice business objects.
 * <li>Defines 1 logical rule.<br>
 * Rule1:<br>
 * If the credit limit of the customer is greater than the amount of
 * the  invoice and the status of the invoice is unpaid then
 * decrement the credit limit with the amount of the invoice and
 * set the status of the invoice to "paid".
 * </ul>
 * <p>
 * <b>Note:</b><br>
 * Additional physical rules may be defined to accomplish the
 * requirements mentioned above.<p>
 * The rule execution set has the following semantics:
 * <ul>
 * <li>Input: <br>
 * A Customer with a credit limit of 5000.<br>
 * An Invoice with an amount of 2000.<br>
 * </ul>
 * The rule execution should produce the following output:
 * <ul>
 * <li>The credit limit of the customer is 3000
 * <li>The status of the invoice is paid.
 * </ul>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testStatelessRuleSession}
 * <ul>
 * <li>Create stateless rule session
 * <li>Execute Rules and verify results
 * <li>Execute Rules with object filtering
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.StatelessRuleSession
 */
public class StatelessRuleSessionTest extends TestCase 
{
  /** StatelessRuleSessionTest constructor.
   * This is the default constructor for a JUnit TestCase.
   *
   * @param name The name of this test.
   * 
   * @see junit.framework.TestCase
   */
  public StatelessRuleSessionTest(String name)
  {
    super(name);
  }

  /** Initialize the StatelessRuleSessionTest.
   *
   * @see junit.framework.TestCase#setUp
   */
  public void setUp()
  {
  }

  /** Cleanup the StatelessRuleSessionTest.
   *
   * @see junit.framework.TestCase#tearDown
   */
  public void tearDown()
  {
  }

  /** Test the compliance for javax.rules.StatelessRuleSession.
   * Create a stateless rule session and execute the rules on a
   * Customer and Invoice object. The execution of the rule will be
   * invoked with an without an object filter. The object filter is
   * the TestObjectFilter of the TCK.
   * 
   * <p>
   * <b>Description:</b><br>
   * <ul>
   * <li>Create stateless rule session
   * <ul>
   * <li>Fail: If any exception occurs while creating the stateless
   * rule session
   * <li>Succeed: If the stateless rule session can successfully be
   * created. 
   * </ul>
   * <li>Execute Rules and verify results
   * <ul>
   * <li>Fail: If any error occurs during execution of the rules or
   * if the results do not match the expected output.
   * <li>Succeed: If the execute rules produced the expected output.
   * </ul>
   * <li>Execute Rules with object filtering
   * <ul>
   * <li>Fail: If any error occurs during the execution of the rules
   * or if the results to not match the expected output.
   * <li>Succeed: If the execution of the rules produced the correct
   * set of output objects after filtering.
   * </ul>
   * </ul>
   * @see TestCaseUtil#getStatelessRuleSession
   * @see TestObjectFilter
   * @see Customer
   * @see Invoice
   */
  public void testStatelessRuleSession()
  {
    try {
      StatelessRuleSession ruleSession = TestCaseUtil.getStatelessRuleSession("stateless", "tck_res_1.xml");
      assertNotNull("[StatelessRuleSessionTest] ", ruleSession);
      
      // Create a Customer as specified by the TCK documentation.
      Customer inputCustomer = new Customer("test");
      inputCustomer.setCreditLimit(5000);
      
      // Create an Invoice as specified by the TCK documentation.
      Invoice inputInvoice = new Invoice("test");
      inputInvoice.setAmount(2000);

      // Create a input list.
      List input = new ArrayList();
      input.add(inputCustomer);
      input.add(inputInvoice);

      // Execute the rules without a filter.
      List results = ruleSession.executeRules(input);

      // Check the results.
      assertNotNull("[StatelessRuleSessionTest] " +
		    "No results found.", results);
      assertTrue("[StatelessRuleSessionTest] ", (2 == results.size()));

      Iterator itr = results.iterator();

      Customer resultCustomer = null;
      Invoice resultInvoice = null;
			
      while(itr.hasNext()) {
	Object obj = itr.next();
	if (obj instanceof Customer)
	  resultCustomer = (Customer) obj;
	if (obj instanceof Invoice)
	  resultInvoice = (Invoice) obj;
      }

      // We should have a customer and an invoice.
      assertNotNull("[StatelessRuleSessionTest] " +
		    "Customer not found in result set.", resultCustomer);
      assertNotNull("[StatelessRuleSessionTest] " +
		    "Invoice not found in result set.", resultInvoice);

      // Verify the results (although technically speaking we
      // shouldn't verify of the rule engine works.
      assertEquals("[StatelessRuleSessionTest] ",
		   3000, resultCustomer.getCreditLimit());
      assertEquals("[StatelessRuleSessionTest] ",
		   "paid", resultInvoice.getStatus());
      
      
      // ================= With ObjectFilter for Customer =======
      // Create a Customer as specified by the TCK documentation.
      inputCustomer = new Customer("test");
      inputCustomer.setCreditLimit(5000);
      
      // Create an Invoice as specified by the TCK documentation.
      inputInvoice = new Invoice("test");
      inputInvoice.setAmount(2000);

      // Create a input list.
      input = new ArrayList();
      input.add(inputCustomer);
      input.add(inputInvoice);

      // Create the object filter.
      ObjectFilter customerFilter = new TestObjectFilter(TestObjectFilter.CUSTOMER_FILTER);
      // Execute the rules without a filter.
      results = ruleSession.executeRules(input, customerFilter);

      // Check the results.
      assertNotNull("[StatelessRuleSessionTest] " +
		    "No results found.", results);
      // We should only have the customer.
      assertEquals("[StatelessRuleSessionTest] ",
		   1, results.size());

      itr = results.iterator();

      resultCustomer = null;
      resultInvoice = null;

      while(itr.hasNext()) {
	Object obj = itr.next();
	if (obj instanceof Customer)
	  resultCustomer = (Customer) obj;
	if (obj instanceof Invoice)
	  resultInvoice = (Invoice) obj;
      }

      // We should only have a customer.
      assertNotNull("[StatelessRuleSessionTest] " +
		    "Customer not found in result set.", resultCustomer);
      assertNull("[StatelessRuleSessionTest] "+
		 "Invoice found in result set.", resultInvoice);			
      
      // Release the session.
      ruleSession.release();
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
