package com.exigen.ie.constrainer.lpsolver;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Exigen Group, Inc.</p>
 * @author unascribed
 * @version 1.0
 */

public class TestConstrainerLP extends TestCase{

  public TestConstrainerLP(String name) {super(name);}

  public static void main(String[] args) {
    TestRunner.run(new TestSuite(TestConstrainerLP.class));
  }

  public void testVoid(){}
}