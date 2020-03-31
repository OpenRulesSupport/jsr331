package com.exigen.ie.constrainer.impl;

import java.util.HashMap;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.GoalGenerate;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.NonLinearExpression;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Exigen Group, Inc.</p>
 * @author Tseitlin Eugeny
 * @version 1.0
 */

public class TestIntExpOpposite extends TestCase {
  private Constrainer C = new Constrainer("TestIntExpOpposite");
  public TestIntExpOpposite(String name){super(name);}
  public static void main(String[] args) {
    TestRunner.run(new TestSuite(TestIntExpOpposite.class));
  }

  public void testIntExpOpposite(){
    IntVar intvar = C.addIntVar(-10,10);
    IntExpArray array = new IntExpArray(C, intvar);
    Goal goal = new GoalGenerate(array);
    try{
      IntExp exp = new IntExpOpposite(intvar);
      C.postConstraint(exp.eq(intvar));
      boolean flag = C.execute(goal);
      assertTrue(flag);
      assertEquals(exp.value(), intvar.value());
      assertEquals(0, intvar.value());
    }catch(Failure f){
      fail("test failed due to incorrect behaviour of IntVar.value()");
    }
  }

  public void testBound(){
    IntExpArray array = new IntExpArray(C, 10, -10, 10, "array");
    IntExpArray negArray = new IntExpArray(C, array.size());
    for (int i=0;i<array.size();i++){
      negArray.set(new IntExpOpposite(array.get(i)), i);
      if (i%3 == 0)
        try{
          array.get(i).setValue(i);
        }catch(Failure f){
          fail("test failed due to incorrect work of IntVar.setValue(int)");
        }
    }

    for (int i=0;i<array.size();i++){
      assertEquals(array.get(i).bound(), negArray.get(i).bound());
    }
  }

  public void testCalcCoeffs(){
    IntVar intvar = C.addIntVar(-10, 10, "intvar");
    IntExp oppexp = new IntExpOpposite(intvar.add(3));
    assertTrue(oppexp.isLinear());
    try{
      HashMap map = new HashMap();
      assertEquals(-3, oppexp.calcCoeffs(map), 0);
      assertEquals(-1, ((Double)map.get(intvar)).doubleValue(), 0);
    }catch(NonLinearExpression ex){fail("failed!!!");}
  }

  public void testContainsMinMax(){
    IntVar intvar = C.addIntVar(-15,5,"intvar", IntVar.DOMAIN_BIT_FAST);
    IntExp oppexp = new IntExpOpposite(intvar);
    int size = intvar.size();
    int minVal = intvar.min();
    int maxVal = intvar.max();
    try{
      //increasing minimum
      for (int i=minVal;i<maxVal;i++){
        intvar.setMin(i);
        C.propagate();

        assertEquals(intvar.min(), -oppexp.max());
        assertEquals(intvar.max(), -oppexp.min());
        assertEquals(intvar.size(), oppexp.size());

        for (int j=-minVal;j>-i;j--){
          assertTrue(!oppexp.contains(j));
        }
        for (int j=-maxVal;j<=-i;j++){
          assertTrue(oppexp.contains(j));
        }
      }

      //decreasing maximum
      intvar = C.addIntVar(minVal,maxVal,"intvar", IntVar.DOMAIN_BIT_FAST);
      oppexp = new IntExpOpposite(intvar);
      for (int i=0;i<maxVal-minVal;i++){
        int newMax = maxVal-i;
        intvar.setMax(newMax);
        C.propagate();

        assertEquals(intvar.min(), -oppexp.max());
        assertEquals(intvar.max(), -oppexp.min());
        assertEquals(intvar.size(), oppexp.size());

        for (int j=-maxVal;j<-newMax;j++){
          assertTrue("contains "+ j,!oppexp.contains(j));
        }
        for (int j=-minVal;j>=-newMax;j--){
          assertTrue("doesn't contain " + j,oppexp.contains(j));
        }
      }

      //successively removing values
      intvar = C.addIntVar(minVal,maxVal,"intvar", IntVar.DOMAIN_BIT_FAST);
      oppexp = new IntExpOpposite(intvar);
      for (int i=minVal;i<maxVal;i++){
        intvar.removeValue(i);
        C.propagate();

        assertEquals(intvar.min(), -oppexp.max());
        assertEquals(intvar.max(), -oppexp.min());
        assertEquals(intvar.size(), oppexp.size());

        for (int j=-minVal;j>=-i;j--){
          assertTrue(!oppexp.contains(j));
        }
        for (int j=-maxVal;j<-i;j++){
          assertTrue(oppexp.contains(j));
        }
      }

    }
    catch(Failure f){fail("test failed");}
  }

  public void testSetMinSetMaxRemove(){
    IntVar intvar = C.addIntVar(-15,5,"intvar", IntVar.DOMAIN_BIT_FAST);
    IntExp oppexp = new IntExpOpposite(intvar);
    int size = oppexp.size();
    int minVal = oppexp.min();
    int maxVal = oppexp.max();
    try{
      //increasing minimum
      for (int i=minVal;i<maxVal;i++){
        oppexp.setMin(i);
        C.propagate();

        assertEquals(intvar.min(), -oppexp.max());
        assertEquals(intvar.max(), -oppexp.min());
        assertEquals(intvar.size(), oppexp.size());

        for (int j=-minVal;j>-i;j--){
          assertTrue(!intvar.contains(j));
        }
        for (int j=-maxVal;j<=-i;j++){
          assertTrue(intvar.contains(j));
        }
      }

      //decreasing maximum
      intvar = C.addIntVar(-maxVal,-minVal,"intvar", IntVar.DOMAIN_BIT_FAST);
      oppexp = new IntExpOpposite(intvar);
      for (int i=0;i<maxVal-minVal;i++){
        int newMax = maxVal-i;
        oppexp.setMax(newMax);
        C.propagate();

        assertEquals(intvar.min(), -oppexp.max());
        assertEquals(intvar.max(), -oppexp.min());
        assertEquals(intvar.size(), oppexp.size());

        for (int j=-maxVal;j<-newMax;j++){
          assertTrue("contains "+ j,!intvar.contains(j));
        }
        for (int j=-minVal;j>=-newMax;j--){
          assertTrue("doesn't contain " + j,intvar.contains(j));
        }
      }

      //successively removing values
      intvar = C.addIntVar(-maxVal,-minVal,"intvar", IntVar.DOMAIN_BIT_FAST);
      oppexp = new IntExpOpposite(intvar);
      for (int i=minVal;i<maxVal;i++){
        oppexp.removeValue(i);
        C.propagate();

        assertEquals(intvar.min(), -oppexp.max());
        assertEquals(intvar.max(), -oppexp.min());
        assertEquals(intvar.size(), oppexp.size());

        for (int j=-minVal;j>=-i;j--){
          assertTrue(!intvar.contains(j));
        }
        for (int j=-maxVal;j<-i;j++){
          assertTrue(intvar.contains(j));
        }
      }

    }
    catch(Failure f){fail("test failed");}
  }


}