package com.exigen.ie.constrainer.impl;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.GoalGenerate;
import com.exigen.ie.constrainer.IntExpArray;

public class TestConstraintAllDiff extends TestCase {
  private Constrainer C = new Constrainer("TestConstraintAllDiff");

  static private boolean isAllDiff(int[] arr){
    for (int i=0;i<arr.length-1;i++){
      for (int j=i+1;j<arr.length;j++){
        if (arr[i] == arr[j])
          return false;
      }
    }
    return true;
  }

  public TestConstraintAllDiff(String name) {super(name);}
  public static void main(String[] args) {
    TestRunner.run(new TestSuite(TestConstraintAllDiff.class));
  }

  public void testExecute(){
    try{
      IntExpArray array1 = new IntExpArray(C, 10, 0, 9, "array1");
      C.postConstraint(new ConstraintAllDiff(array1));
      boolean flag = C.execute(new GoalGenerate(array1));
      assertTrue(flag);
      int[] vals = new int[array1.size()];
      for (int i=0;i<vals.length;i++){
        vals[i] = array1.get(i).value();
      }
      assertTrue("There are entries with equal values",isAllDiff(vals));

      // the number of elements in the array is greater then the domain size
      IntExpArray array2 = new IntExpArray(C, 5, 0, 3, "array2");

      C.postConstraint(new ConstraintAllDiff(array2));
      flag = C.execute(new GoalGenerate(array2));
      assertTrue(!flag);

    }
    catch(Failure f){
      fail("test failed!");
    }

  }
}