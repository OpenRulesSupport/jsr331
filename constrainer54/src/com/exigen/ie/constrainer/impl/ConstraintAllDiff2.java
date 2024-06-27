package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Constraint;
import com.exigen.ie.constrainer.ConstraintImpl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.GoalAnd;
import com.exigen.ie.constrainer.GoalGenerate;
import com.exigen.ie.constrainer.GoalPrint;
import com.exigen.ie.constrainer.GoalPrintObject;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntValueSelector;
import com.exigen.ie.constrainer.IntValueSelectorMin;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.IntVarSelector;
import com.exigen.ie.constrainer.IntVarSelectorMinSize;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;

///////////////////////////////////////////////////////////////////////////////
/*
 * Copyright Exigen Group 1998, 1999, 2000
 * 320 Amboy Ave., Metuchen, NJ, 08840, USA, www.exigengroup.com
 *
 * The copyright to the computer program(s) herein
 * is the property of Exigen Group, USA. All rights reserved.
 * The program(s) may be used and/or copied only with
 * the written permission of Exigen Group
 * or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which
 * the program(s) have been supplied.
 */
///////////////////////////////////////////////////////////////////////////////

/**
 * An implementation of the constraint "All Different".
 *
 * Any two constrained integer expressions from the vector (parameter of this
 * constraint) should can not be instantiated with the same value.
 */
public final class ConstraintAllDiff2 extends ConstraintImpl
{
  // PRIVATE MEMBERS
  private IntExpArray	     _intvars;
  private IntVar _bit_var; // one bit for every possible values among all _intvars

  //////////////////////////////////////////////// inner class AllDiffObserver
  class AllDiffObserver extends Observer
  {
    public void update(Subject var, EventOfInterest interest)
        throws Failure
    {
      IntExp event_var = (IntExp)var;
      //Debug.on(); Debug.print("AllDiffObserver("+var+") "+interest+" "+_bits); Debug.off();
      int value = event_var.value();
      // check for failure
      if (!_bit_var.contains(value))
      {
        //Debug.on();Debug.print("diff fail: "+interest+_intvars+_bits);Debug.off();
        var.constrainer().fail("AllDiff"); //+_bits +" "+_intvars);
      }

      // set the bit
      int size = _intvars.size();
      _bit_var.removeValue(value);  //Debug.on();Debug.print(" "+this+":  "+_bits);Debug.off();

      // additional propagation
      for(int i=0; i<size; i++)
      {
        IntExp vari = (IntExp)_intvars.elementAt(i);
        if (vari != event_var)
        {
          //Debug.on();Debug.print("alldiff remove "+value+" from "+vari);Debug.off();
          vari.removeValue(value); // may fail
        }
      }
    }

     public int subscriberMask()
     {
       return  EventOfInterest.VALUE;
     }

     public String toString()
     {
        return "AllDiff";//+_intvars;
     }

    public Object master()
    {
      return ConstraintAllDiff2.this;
    }


  } //~ AllDiffObserver

  public ConstraintAllDiff2(IntExpArray intvars) throws Failure
  {
    super(intvars.constrainer(),"AllDiff");
    _intvars = intvars;

    int min = intvars.min();
    int max = intvars.max();


    _bit_var = constrainer().addIntVar(min,max + 1, IntVar.DOMAIN_BIT_FAST);
  }

  public Goal execute() throws Failure
  {
    // initial propagation
    int size = _intvars.size();
    boolean undo = false;
    for(int i=0; i<size; i++)
    {
      IntExp vari = (IntExp)_intvars.elementAt(i);
      if (vari.bound())
      {
        int value = vari.value();
        boolean bit = _bit_var.contains(value);
        // check for failure
        if (!bit)
        {
          constrainer().fail("Diff");
        }
        _bit_var.removeValue(value);
      }
    }

    // attach observers
    Observer value_observer = new AllDiffObserver();
    for(int i=0; i<size; i++)
    {
      IntExp vari = (IntExp)_intvars.elementAt(i);
      vari.attachObserver(value_observer);
    }

    return null;
  } // end of execute

  // TEST
  static void test1(String args[]) throws Exception
  {
    Constrainer C = new Constrainer("Test");

    IntExp x = C.addIntVar(0,10,"x",IntVar.DOMAIN_DEFAULT);
    IntExp y = C.addIntVar(2,10,"y",IntVar.DOMAIN_DEFAULT);
    IntExp z = C.addIntVar(0,10,"z",IntVar.DOMAIN_DEFAULT);

    int size = 3;
    IntExpArray vars = new IntExpArray(C, size);
    vars.set(x, 0);
    vars.set(y, 1);
    vars.set(z, 2);

    Constraint constraintAllDiff2 = new ConstraintAllDiff2(vars);
    constraintAllDiff2.execute();

    //IntExp cost = C.addIntVar(0,20,"cost");
    //Constraint sum = new ConstraintAddVector(vars,cost);
    //sum.execute();
    IntExp cost = C.sum(vars); cost.name("cost");

    //x.lessOrEqual(y).execute();
    x.mul(2).sub(cost).more(y).execute();

    C.traceChoicePoints(vars);
    //C.displayOnBacktrack(vars);
    C.traceFailures(vars);

    IntValueSelector value_selector = new IntValueSelectorMin();
    IntVarSelector var_selector = new IntVarSelectorMinSize(vars);
    Goal print = new GoalAnd(new GoalPrint(vars),new GoalPrintObject(C,cost));
    Goal solution = new GoalAnd(print,
                                new GoalGenerate(vars,var_selector,value_selector),
                                print);

    //if (!C.execute(new GoalMinimize(solution,cost)))
    if (!C.execute(solution))
      System.out.println("No solutions");
    System.out.println(x + " " + y + " " + " " + z + cost);
  }
} // ~ConstraintAllDiff2
