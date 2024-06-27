/*
 * J A V A  C O M M U N I T Y  P R O C E S S
 *
 * J S R  9 4
 *
 * Test Compatability Kit
 *
 */
package org.jcp.jsr94.tck;

import javax.rules.RuleServiceProviderManager;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleRuntime;
import javax.rules.admin.RuleAdministrator;
import javax.rules.ConfigurationException;
import org.jcp.jsr94.tck.util.TestCaseUtil;
import junit.framework.*;
import java.net.*;
import java.util.*;

/**
 * Tests the {@link javax.rules.RuleServiceProviderManager}
 * and {@link javax.rules.RuleServiceProvider} classes. This
 * test complements the others by creating a class loader
 * to test the methods that have a ClassLoader in their signature.
 * @version 1.0
 * @since JSR-94 1.0
 * @see javax.rules.RuleServiceProviderManager
 * @see javax.rules.RuleServiceProvider
 */
public class ClassLoaderTest extends TestCase 
{
  /** RuleServiceProviderManagerTest constructor.
   * This is the default constructor for a JUnit TestCase.
   *
   * @param name The name of this test.
   * 
   * @see junit.framework.TestCase
   */
  public ClassLoaderTest(String name)
    {
      super(name);
    }

  /** Initialize the RuleServiceProviderManagerTest.
   *
   * @see junit.framework.TestCase#setUp
   */
  public void setUp()
    {
    }

  /** Cleanup the RuleServiceProviderManagerTest.
   *
   * @see junit.framework.TestCase#tearDown
   */
  public void tearDown()
    {
    }

  public void testRuleServiceProviderManager()
    {
      Vector vect = TestCaseUtil.getRuleServiceProviderJarURLs();
      assertNotNull("[ClassLoaderTest] " +
		    "No jar file name for the rule service provider.",
		    vect);
      assertTrue("[ClassLoaderTest] " +
		 "No jar file name for the rule service provider.",
		 vect.size() > 0);

      int size = vect.size();
      URL[] urls = new URL[size];

      for (int i = 0; i < size; i++) {
	try {
	  URL url = new URL((String)vect.elementAt(i));
	  urls[i] = url;
	}
	catch(MalformedURLException ex) {
	  fail(ex.getMessage());
	}
      }

      try {
	// Register the provider.
        String uri = (String)vect.elementAt(0);
	RuleServiceProvider provider = TestCaseUtil.getRuleServiceProvider(uri,urls);

	// Retrieve the provider
	Object object = RuleServiceProviderManager.getRuleServiceProvider(uri);
	assertEquals("[RuleServiceProviderManagerTest] ",
		     provider.getClass(), object.getClass());
	
	useProvider(provider);

	// Deregister the provider.
	RuleServiceProviderManager.deregisterRuleServiceProvider(uri);	
	try {
	  object = RuleServiceProviderManager.getRuleServiceProvider(uri);
	  assertNull("[RuleServiceProviderManagerTest] " +
		     "Deregistered provider still in list of registrations.",
		     object);
	}
	catch(ConfigurationException ce) {
				// Do nothing, we expected this exception. The
				// RuleServiceProviderManager will throw a
				// ConfigurationException when a call to
				// getRuleServiceProvider is made on an unknown uri.
				// Since we unregistered the provider, we should get
				// this exception.
	}
      }
      catch (Exception e) {
	fail(e.getMessage());
      }
    }

  private void useProvider(RuleServiceProvider provider)
  {
    try {

      // Get the rule adminstrator.
      RuleAdministrator admin = provider.getRuleAdministrator();
			
      // Check whether or not it is indeed a rule administrator.
      assertNotNull("[RuleServiceProviderTest] " +
		    "Could not retrieve a rule administrator.", admin);
      
      // Get the rule runtime.
      RuleRuntime runtime = provider.getRuleRuntime();
      
      // Check whether or not it is indeed a rule runtime.
      assertNotNull("[RuleServiceProviderTest] " +
		    "Could not retrieve a rule runtime.", runtime);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
