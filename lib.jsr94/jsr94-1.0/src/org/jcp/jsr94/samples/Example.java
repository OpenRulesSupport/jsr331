/*
 * J A V A  C O M M U N I T Y  P R O C E S S
 *
 * J S R  9 4
 *
 * Reference Implementation
 *
 */
package org.jcp.jsr94.samples;

// java imports
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.rules.Handle;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatefulRuleSession;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

// external imports
import org.jcp.jsr94.tck.model.Customer;
import org.jcp.jsr94.tck.model.Invoice;

/**
 * This class implement a simple example using a rule execution set
 * from the Test Compatibility Kit.
 *
 * See for the TCK for more information on the object model and the
 * rule execution set.
 *
 * This example requires the following jar file to be present on your
 * classpath:
 * jsr94.jar
 * jsr94-ri.jar
 * jsr94-tck.jar
 * xerces.jar
 * jess.jar (The reference implementation)
 *
 * To run this example execute the following command in the lib
 * directory of the jsr94 distribution:
 * java -jar jsr94-example.jar
 */
public class Example
{
	// The rule service provider uri as defined by the reference
	// implementation.
	private static final String RULE_SERVICE_PROVIDER = "org.jcp.jsr94.jess";

	/**
	 * Main entry point.
	 */
	public static void main( String[] args )
	{
		try
		{
			// Load the rule service provider of the reference
			// implementation.
			// Loading this class will automatically register this
			// provider with the provider manager.
			Class.forName( "org.jcp.jsr94.jess.RuleServiceProviderImpl" );

			// Get the rule service provider from the provider manager.
			RuleServiceProvider serviceProvider = RuleServiceProviderManager.getRuleServiceProvider( RULE_SERVICE_PROVIDER );

			// get the RuleAdministrator
			RuleAdministrator ruleAdministrator = serviceProvider.getRuleAdministrator();
			System.out.println("\nAdministration API\n");
			System.out.println( "Acquired RuleAdministrator: " +
								ruleAdministrator );		   

			// get an input stream to a test XML ruleset
			// This rule execution set is part of the TCK.
			InputStream inStream = org.jcp.jsr94.tck.model.Customer.class.getResourceAsStream( "/org/jcp/jsr94/tck/tck_res_1.xml" );
			System.out.println("Acquired InputStream to RI tck_res_1.xml: " +
							   inStream );

			// parse the ruleset from the XML document
			RuleExecutionSet res1 = ruleAdministrator.getLocalRuleExecutionSetProvider( null ).createRuleExecutionSet( inStream, null );
			inStream.close();
			System.out.println( "Loaded RuleExecutionSet: " + res1);

			// register the RuleExecutionSet
			String uri = res1.getName();
			ruleAdministrator.registerRuleExecutionSet(uri, res1, null );
			System.out.println( "Bound RuleExecutionSet to URI: " + uri);



			// Get a RuleRuntime and invoke the rule engine.
			System.out.println( "\nRuntime API\n" );

			RuleRuntime ruleRuntime = serviceProvider.getRuleRuntime();
			System.out.println( "Acquired RuleRuntime: " + ruleRuntime );

			// create a StatelessRuleSession 
			StatelessRuleSession statelessRuleSession = 
				(StatelessRuleSession) ruleRuntime.createRuleSession(uri,
				new HashMap(), RuleRuntime.STATELESS_SESSION_TYPE);

			System.out.println( "Got Stateless Rule Session: " +
								statelessRuleSession );

			// call executeRules with some input objects

			// Create a Customer as specified by the TCK documentation.
			Customer inputCustomer = new Customer("test");
			inputCustomer.setCreditLimit(5000);

			// Create an Invoice as specified by the TCK documentation.
			Invoice inputInvoice = new Invoice("Invoice 1");
			inputInvoice.setAmount(2000);

			// Create a input list.
			List input = new ArrayList();
			input.add(inputCustomer);
			input.add(inputInvoice);

			// Print the input.
			System.out.println("Calling rule session with the following data");
			System.out.println("Customer credit limit input: " +
							   inputCustomer.getCreditLimit());
			System.out.println(inputInvoice.getDescription() +
							   " amount: " + inputInvoice.getAmount() +
							   " status: " + inputInvoice.getStatus());

			// Execute the rules without a filter.
			List results = statelessRuleSession.executeRules(input);

			System.out.println( "Called executeRules on Stateless Rule Session: " + statelessRuleSession );

			System.out.println( "Result of calling executeRules: " +
								results.size() + " results." );

			// Loop over the results.
			Iterator itr = results.iterator();
			while(itr.hasNext()) {
				Object obj = itr.next();
				if (obj instanceof Customer)
					System.out.println("Customer credit limit result: " +
									   ((Customer) obj).getCreditLimit());
				if (obj instanceof Invoice)
					System.out.println(((Invoice) obj).getDescription() +
									   " amount: " + ((Invoice) obj).getAmount() +
									   " status: " + ((Invoice) obj).getStatus());
			}

			// Release the session.
			statelessRuleSession.release();
			System.out.println( "Released Stateless Rule Session." );
			System.out.println();

			// create a StatefulRuleSession 
			StatefulRuleSession statefulRuleSession = 
				(StatefulRuleSession) ruleRuntime.createRuleSession( uri, 
				new HashMap(), 
				RuleRuntime.STATEFUL_SESSION_TYPE );

			System.out.println( "Got Stateful Rule Session: " + statefulRuleSession );
			// Add another Invoice.
			Invoice inputInvoice2 = new Invoice("Invoice 2");
			inputInvoice2.setAmount(1750);
			input.add(inputInvoice2);
			System.out.println("Calling rule session with the following data");
			System.out.println("Customer credit limit input: " +
							   inputCustomer.getCreditLimit());
			System.out.println(inputInvoice.getDescription() +
							   " amount: " + inputInvoice.getAmount() +
							   " status: " + inputInvoice.getStatus());
			System.out.println(inputInvoice2.getDescription() +
							   " amount: " + inputInvoice2.getAmount() +
							   " status: " + inputInvoice2.getStatus());

			// add an Object to the statefulRuleSession
			statefulRuleSession.addObjects( input );
			System.out.println( "Called addObject on Stateful Rule Session: "
								+ statefulRuleSession );

			statefulRuleSession.executeRules();
			System.out.println( "Called executeRules" );

			// extract the Objects from the statefulRuleSession
			results = statefulRuleSession.getObjects();

			System.out.println( "Result of calling getObjects: " +
								results.size() + " results." );


			// Loop over the results.
			itr = results.iterator();
			while(itr.hasNext()) {
				Object obj = itr.next();
				if (obj instanceof Customer)
					System.out.println("Customer credit limit result: " +
									   ((Customer) obj).getCreditLimit());
				if (obj instanceof Invoice)
					System.out.println(((Invoice) obj).getDescription() +
									   " amount: " + ((Invoice) obj).getAmount() +
									   " status: " + ((Invoice) obj).getStatus());
			}

			// release the statefulRuleSession
			statefulRuleSession.release();
			System.out.println( "Released Stateful Rule Session." );
			System.out.println();

		}
		catch (NoClassDefFoundError e)
		{
			if (e.getMessage().indexOf("JessException") != -1)
			{
				System.err.println("Error: The reference implementation Jess could not be found.");
			}
			else
			{
				System.err.println("Error: " + e.getMessage());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


