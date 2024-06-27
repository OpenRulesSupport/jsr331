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
import java.util.Vector;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;

// internal imports
import org.jcp.jsr94.tck.util.TestCaseUtil;

// external imports
import junit.framework.*;
import com.sun.tdk.signaturetest.SignatureTest;
import com.sun.javatest.Status;
import com.sun.javatest.Test;

/**
 * Signature test for the JSR 94.
 * The signature test checks whether an implementation is compliant
 * with the constructor, method and field signatures defined by the JSR.
 * Any modifications will be reported as errors.
 * <p>
 * This test reads the <code>tck.conf</code> configuration file. The
 * <code>&lt;rule-execution-set-location&gt;</code> tag is used to locate the
 * original signature file.
 * <p>
 * <b>Perfoms the following tests:</b><br>
 * <ul>
 * <li>Full API signature test for:
 * <ul>
 * <li> <code>javax.rules</code> package
 * <li> <code>javax.rules.admin</code> package
 * </ul>
 * </ul>
 * <p>
 * @version 1.0
 * @since JSR-94 1.0
 */
public class ApiSignatureTest extends TestCase 
{
  /**
   * ApiSignatureTest constructor.
   * This is the default constructor for a JUnit TestCase.
   *
   * @param name The name of this test.
   * @see junit.framework.TestCase
   */
  public ApiSignatureTest(String name)
  {
    super(name);
  }

  /** Initialize the ApiSignatureTest.
   * @see junit.framework.TestCase#setUp
   */
  public void setUp()
  {
  }

  /** Cleanup the ApiSignatureTest.
   *
   * @see junit.framework.TestCase#tearDown
   */
  public void tearDown()
  {
  }

  /** Test all API calls against the original definition.
   * 
   * <p>
   * <b>Description:</b><br>
   * The signature test will check whether or not the current test
   * run is using an unmodified API which is completely conforment
   * with the API as defined by the JSR.
   * The location of the signature file is defined by the
   * &lt;rule-execution-set-location&gt; in the tck.conf configuration file.
   * <ul>
   * <li>JSR 94 API Signatures
   * <ul>
   * <li>Fail: If any modification are detected.
   * <li>Succeed: If the api is compliant with the JSR.
   * </ul>
   * </ul>
   */
  public void testSignatures()
  {
    Status status = null;
    try {
	  String javaVersion = System.getProperty("java.version");
      Vector args = new Vector();
      // Get the location of the rule execution sets.
      // Currently we assume we can find the signature file here
      // as well.
      String dir = TestCaseUtil.getRuleExecutionSetLocation();
      args.add("-FileName");
	  if (javaVersion.startsWith("1.4")) {
		  args.add(dir + "/" + "jaxrules14.sig");
	  }
	  else {
		  args.add(dir + "/" + "jaxrules.sig");
	  }
      args.add("-Package");
      args.add("javax.rules");

      ByteArrayOutputStream os = new ByteArrayOutputStream(2000);
      PrintWriter pw = new PrintWriter(os);

      Test test = new SignatureTest();
      status = test.run((String[]) args.toArray(new String[0]),
			pw, null);

      pw.flush();
      // Print the results.
      System.out.println(os.toString());
      // Check whether we passed.
      assertTrue("[ApiSignatureTest] " + os.toString(),
		 status.isPassed());
      pw.close();
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
