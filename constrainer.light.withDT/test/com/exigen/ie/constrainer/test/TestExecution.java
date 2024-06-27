// ------------------------------------------------------------
// File: TestExecution.java
// Java Implementation of the Dummy  problem
// Copyright (C) 2000 by Exigen Group
// ------------------------------------------------------------

package com.exigen.ie.constrainer.test;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Constraint;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.GoalAnd;
import com.exigen.ie.constrainer.GoalDisplay;
import com.exigen.ie.constrainer.GoalFail;
import com.exigen.ie.constrainer.GoalOr;
import com.exigen.ie.constrainer.GoalPrintObject;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;

public class TestExecution
{
  public boolean run()
  {
    try
    {
      Constrainer C = new Constrainer("Test");
      C.showInternalNames(true);
      IntVar var = C.addIntVar(0,10,"var",IntVar.DOMAIN_DEFAULT);
      Goal g1 = new GoalFail(C);
      Goal g2 = new GoalDisplay(C,"Goal-2");
      Goal g12 = new GoalAnd(g1,g2);
      Goal g3 = new GoalFail(C);
      Goal g4 = new GoalDisplay(C,"Goal-4");
      Goal g34 = new GoalAnd(g3,g4);
      Goal g1234 = new GoalOr(g12,g34);
      //Goal g6 = new GoalFail(C);
      Goal g6 = new GoalDisplay(C,"Goal-6");
      Goal g5 = var.less(5);
      Goal g56 = new GoalAnd(g5,g6);
      Goal G = new GoalOr(g1234,g56);
      Goal print = new GoalPrintObject(C,var);
      C.traceChoicePoints(C);
      C.traceBacktracks(C);
      C.traceFailures(C);
      C.traceExecution();
      System.out.println(var);
      boolean restore = true;
      boolean rc = C.execute(new GoalAnd(G,print),restore);
      System.out.println(var);

      // test sum
      System.out.println("TEST SUM");
      C.traceOff();
      int trace = IntVar.TRACE_ALL;
      IntVar v1 = C.addIntVarTrace(1,10,"v1",IntVar.DOMAIN_DEFAULT, trace);
      IntVar v2 = C.addIntVarTrace(1,10,"v2",IntVar.DOMAIN_DEFAULT, trace);
      IntVar v3 = C.addIntVarTrace(1,100,"v3",IntVar.DOMAIN_DEFAULT, trace);
      Constraint c1 = v1.less(v2);
      Constraint c2 = v3.equals(v1.add(v2));
      IntExpArray array = new IntExpArray(C,v1,v2,v3);
      System.out.println("before post:"+array);
      c1.post();
      System.out.println("after v1<v2:"+array);
      c2.post();
      System.out.println("after v3==v1+v2"+array);

      /*
      IntExpArray array = new IntExpArray(C,v1,v2);
      array.sum().lessOrEqual(1).post();
      C.execute(new GoalGenerateAll(array));
      */
      return rc;
    }
    catch(Exception e)
    {
      System.out.println(e);
      return false;
    }
  }

  public static void main(String[] args)
  {
    TestExecution benchmark = new TestExecution();
    if (benchmark.run())
    {
      System.out.println("Successful run of "+benchmark);
    }
    else
    {
      System.out.println("Unsuccessful run of "+benchmark);
    }
  } //~ main

}
