package com.exigen.ie.constrainer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite
{

  static final Class[] _testClasses =
  {
    IntExpMulExpTest.class,
    GoalFloatFastMinimizeTest.class,
  };


  public AllTests(String name)
  {
    super(name);
  }


  public static Test suite()
  {
    TestSuite suite = new TestSuite();
    for(int i = 0; i < _testClasses.length; ++i)
      suite.addTestSuite(_testClasses[i]);
    return suite;
  }


}
