package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.Constraint;
import com.exigen.ie.constrainer.ConstraintImpl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.IntBoolExp;
import com.exigen.ie.constrainer.IntExp;
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
 * An implementation of the constraint: <code>IntExp > value</code>.
 */
public final class ConstraintExpMoreValue extends ConstraintImpl
{
  // PRIVATE MEMBERS
  private IntExp		 _exp;
  private int			   _value;
  private Constraint _opposite;

  class ObserverMoreValue extends Observer
  {
    public void update(Subject exp, EventOfInterest interest)	throws Failure
    {
      //Debug.on();Debug.print("ObserverMoreValue: "+interest);Debug.off();
      IntEvent event = (IntEvent)interest;
      if ( event.max() <= _value)
      {
        exp.constrainer().fail("from ObserverMoreValue");
      }

      _exp.setMin(_value + 1);
    }

    public int subscriberMask()
    {
       return EventOfInterest.VALUE | EventOfInterest.MAX;
    }

    public String toString()
    {
      return "ObserverMoreValue";
    }

    public Object master()
    {
      return ConstraintExpMoreValue.this;
    }

  } //~ ObserverMoreValue

  public ConstraintExpMoreValue(IntExp exp, int value)
  {
    super(exp.constrainer());

    if(constrainer().showInternalNames())
    {
      _name = "("+exp.name()+">"+value+")";
    }

    _exp = exp;
    _value = value;
    _opposite = null;
  }

  public Goal execute() throws Failure
  {
    _exp.setMin(_value+1); // may fail
    _exp.attachObserver(new ObserverMoreValue());
    return null;
  }

  public Constraint opposite()
  {
    if (_opposite == null)
      _opposite = new ConstraintExpLessValue(_exp,_value+1);
    return _opposite;
  }

  public boolean isLinear(){
    return _exp.isLinear();
  }

  public IntBoolExp toIntBoolExp(){
    return _exp.gt(_value);
  }

  public String toString()
  {
    return _exp+">"+_value;
  }

} //eof ConstraintExpMoreValue
