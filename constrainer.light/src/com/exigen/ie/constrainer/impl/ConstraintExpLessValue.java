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
 * An implementation of the constraint: <code>IntExp < value</code>.
 */
public final class ConstraintExpLessValue extends ConstraintImpl
{
  // PRIVATE MEMBERS
  private IntExp	_exp;
  private int	    _value;
  private Constraint _opposite;

  public ConstraintExpLessValue(IntExp exp, int value)
  {
    super(exp.constrainer());
    _exp = exp;
    _value = value;
    _opposite = null;

    if(constrainer().showInternalNames())
    {
      _name = "("+exp.name()+"<"+value+")";
    }
  }

  /*
  resetValue(int v) throws Failure
  {
    _value = v;
    _exp.setMax(_value-1);
  }
  */
  public void resetValue(int v)
  {
    _value = v;
  }

  class ObserverLessValue extends Observer
  {
    public void update(Subject exp, EventOfInterest interest)	throws Failure
    {
      //Debug.on();Debug.print("ObserverLessValue: "+interest);Debug.off();
      IntEvent event = (IntEvent)interest;
      if (event.min() >= _value)
      {
        exp.constrainer().fail("from ObserverLessValue");
      }

      _exp.setMax(_value - 1);
    }

    public int subscriberMask()
    {
       return EventOfInterest.VALUE | EventOfInterest.MIN;
    }

    public String toString()
    {
      return "ObserverLessValue";
    }

    public Object master()
    {
      return ConstraintExpLessValue.this;
    }

  } //~ ObserverLessValue

  public Goal execute() throws Failure
  {
    _exp.setMax(_value-1); // may fail
    _exp.attachObserver(new ObserverLessValue());
    return null;
  }

  public Constraint opposite()
  {
    if (_opposite == null)
      _opposite = new ConstraintExpMoreValue(_exp,_value-1);
    return _opposite;
  }

  public boolean isLinear(){
    return _exp.isLinear();
  }

  public IntBoolExp toIntBoolExp(){
    return _exp.lt(_value);
  }

  public String toString()
  {
    return _exp+"<"+_value;
  }

} //~ ConstraintExpLessValue
