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
import javax.rules.ObjectFilter;

// internal imports
import org.jcp.jsr94.tck.util.TestObjectFilter;
import org.jcp.jsr94.tck.model.Customer;
import org.jcp.jsr94.tck.model.Invoice;

// external imports
import junit.framework.*;

/**
 * Test the javax.rules.ObjectFilter class.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Basic API tests. {@link #testObjectFilter}
 * <ul>
 * <li>Instance Creation
 * <li>Filtering
 * </ul>
 * </ul>
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.ObjectFilter
 */
public class ObjectFilterTest extends TestCase 
{
	/** ObjectFilterTest constructor.
	 * This is the default constructor for a JUnit TestCase.
	 *
	 * @param name The name of this test.
	 * 
	 * @see junit.framework.TestCase
	 */
	public ObjectFilterTest(String name)
	{
		super(name);
	}

	/** Initialize the ObjectFilterTest.
	 * 
	 * @see junit.framework.TestCase#setUp
	 */
	public void setUp()
	{
	}

	/** Cleanup the ObjectFilterTest.
	 *
	 * @see junit.framework.TestCase#tearDown
	 */
	public void tearDown()
	{
	}

	/** Test the compliance for javax.rules.ObjectFilter.
	 * This test is testing the basic functionality of the
	 * TestObjectFilter. This object filter will be used in rule
	 * session related tests.
	 * This test creates two filters one filter is a Customer filter
	 * the other one an Invoice filter.
	 * Both Customer and Invoice objects will be passed to both filters
	 * and the results are checked.
	 * 
	 * <p>
	 * <b>Description:</b><br>
	 * <ul>
	 * <li>Instance Creation
	 * <ul>
	 * <li>Fail: If the TestObjectFilter cannot be created and/or does
	 * not implement the ObjectFilter interface.
	 * <li>Succeed: If the TestObjectFilter can successfully be created.
	 * </ul>
	 * <li>Filtering
	 * <ul>
	 * <li>Fail: If objects are returned that are not part of the
	 * filter's pattern.
	 * <li>Succeed: If only objects that are part of the filter's
	 * pattern are being returned.
	 * </ul>
	 * </ul>
	 * @see org.jcp.jsr94.tck.util.TestObjectFilter
	 * @see org.jcp.jsr94.tck.model.Customer
	 * @see org.jcp.jsr94.tck.model.Invoice
	 */
	public void testObjectFilter()
	{
		try {
			ObjectFilter customerFilter = new TestObjectFilter(TestObjectFilter.CUSTOMER_FILTER);
			ObjectFilter invoiceFilter = new TestObjectFilter(TestObjectFilter.INVOICE_FILTER);
			assertNotNull("[ObjectFilterTest] ",
						  customerFilter);
			assertNotNull("[ObjectFilterTest] ", invoiceFilter);

			Customer c = new Customer("test customer");
			Invoice i = new Invoice("test invoice");

			Object obj = null;

			// We should get the customer back.
			obj = customerFilter.filter(c);
			assertNotNull("[ObjectFilterTest] ", obj);
			assertTrue("[ObjectFilterTest] ", (obj instanceof Customer));
			assertEquals("[ObjectFilterTest] ", c, obj);

			// The invoice should be filtered out.
			obj = customerFilter.filter(i);
			assertNull("[ObjectFilterTest] ", obj);


			// We should get the invoice back.
			obj = invoiceFilter.filter(i);
			assertNotNull("[ObjectFilterTest] ", obj);
			assertTrue("[ObjectFilterTest] ", (obj instanceof Invoice));
			assertEquals("[ObjectFilterTest] ", i, obj);

			// The customer should be filtered out.
			obj = invoiceFilter.filter(c);
			assertNull("[ObjectFilterTest] ", obj);
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
