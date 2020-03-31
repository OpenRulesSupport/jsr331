package com.exigen.ie.constrainer.impl;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.IntBoolExp;
import com.exigen.ie.constrainer.IntBoolExpConst;
import com.exigen.ie.constrainer.IntVar;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Exigen Group, Inc.</p>
 * @author Tseitlin Eugeny
 * @version 1.0
 */

public class TestIntBoolExpOr extends TestCase {
  private Constrainer C = new Constrainer("TestIntBoolExpOr");
  public TestIntBoolExpOr(String name){super(name);}
  public static void main(String[] args) {
    TestRunner.run(new TestSuite(TestIntBoolExpOr.class));
  }

  public void testAll(){
    IntBoolExp boolexp1 = new IntBoolExpConst(C, false),
               boolexp2 = new IntBoolExpConst(C, true);
    IntBoolExp andexp = new IntBoolExpOr(boolexp1, boolexp2);
    try{
      C.postConstraint(andexp);
    }catch(Failure f){fail("test failed");}

    boolexp1 = new IntBoolExpConst(C, true);
    boolexp2 = new IntBoolExpConst(C, false);
    andexp = new IntBoolExpOr(boolexp1, boolexp2);
    try{
      C.postConstraint(andexp);
    }catch(Failure f){fail("test failed");}

    boolexp1 = new IntBoolExpConst(C, false);
    boolexp2 = new IntBoolExpConst(C, false);
    andexp = new IntBoolExpOr(boolexp1, boolexp2);
    try{
      C.postConstraint(andexp);
      fail("test failed");
    }catch(Failure f){}

    boolexp1 = new IntBoolExpConst(C, true);
    boolexp2 = new IntBoolExpConst(C, true);
    andexp = new IntBoolExpOr(boolexp1, boolexp2);
    try{
      C.postConstraint(andexp);
    }catch(Failure f){fail("test failed");}

    IntVar intvar1 = C.addIntVar(0,10);
    IntVar intvar2 = C.addIntVar(0,10);

    boolexp1 = intvar1.gt(5);
    boolexp2 = intvar2.lt(5);
    andexp = new IntBoolExpOr(boolexp1, boolexp2);
    try{
      C.postConstraint(andexp);
    }catch(Failure f){fail("test failed");}

    assertEquals(0, intvar1.min());
    assertEquals(10, intvar1.max());
    assertEquals(0, intvar2.min());
    assertEquals(10, intvar2.max());

    try{
      intvar1.setMax(4);
      C.propagate();
      assertEquals(4, intvar2.max());
    }catch(Failure f){}
  }

}