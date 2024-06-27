package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;

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
 * An implementation of the IntVar with tracing capabilities.
 */
public class IntVarImplTrace extends IntVarImpl
{
  int _trace_flags = 0;

  public IntVarImplTrace(Constrainer constrainer, int min, int max, String name, int type, int trace_flags)
  {
    super(constrainer, min, max, name, type);
    _trace_flags = trace_flags;

  }


  public void removeValue(int value) throws Failure
  {
    if ((_trace_flags & TRACE_REMOVE) != 0)
      System.out.println("++++ Remove:" + value + " from " + this + " ... ");
    super.removeValue(value);
    if ((_trace_flags & TRACE_REMOVE) != 0)
      System.out.println("---- Remove:" + value + " from " + this + " ... ");
  }

  public void setMin(int min) throws Failure
  {
    if ((_trace_flags & TRACE_MIN) != 0)
      System.out.println("++++ setMIN:" + min + "  in  " + this + " ... ");
    super.setMin(min);
    if ((_trace_flags & TRACE_MIN) != 0)
      System.out.println("---- setMIN:" + min + "  in  " + this + " ... ");
  }

  public void setMax(int max) throws Failure
  {
    if ((_trace_flags & TRACE_MAX) != 0)
      System.out.println("++++ setMAX:" + max + "  in  " + this + " ... ");
    super.setMax(max);
    if ((_trace_flags & TRACE_MAX) != 0)
      System.out.println("---- setMAX:" + max + "  in  " + this + " ... ");
  }


   public void forceMin(int val)
   {
    if ((_trace_flags & TRACE_MIN) != 0)
      System.out.println("++++ forceMIN:" + val + "  in  " + this + " ... ");
    super.forceMin(val);
    if ((_trace_flags & TRACE_MIN) != 0)
      System.out.println("---- forceMIN:" + val + "  in  " + this + " ... ");
   }

   public void forceMax(int val)
   {
    if ((_trace_flags & TRACE_MAX) != 0)
      System.out.println("++++ forceMAX:" + val + "  in  " + this + " ... ");
    super.forceMax(val);
    if ((_trace_flags & TRACE_MAX) != 0)
      System.out.println("---- forceMAX:" + val + "  in  " + this + " ... ");
   }




  public void setValue(int value) throws Failure
  {
    if ((_trace_flags & TRACE_VALUE) != 0)
      System.out.println("++++ setVal:" + value + "  in  " + this + " ... ");
    super.setValue(value);
    if ((_trace_flags & TRACE_VALUE) != 0)
      System.out.println("---- setVal:" + value + "  in  " + this + " ... ");
  }


  public String toString()
  {
//    if ((_trace_flags & TRACE_HISTORY) != 0)
//		  return super.toString() + _history;
    return super.toString();
  }

}
