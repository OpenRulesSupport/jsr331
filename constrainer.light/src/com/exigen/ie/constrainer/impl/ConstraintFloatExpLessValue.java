package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.Constraint;
import com.exigen.ie.constrainer.ConstraintImpl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.FloatEvent;
import com.exigen.ie.constrainer.FloatExp;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.IntBoolExp;
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

//
//: ConstraintFloatExpLessValue.java
//
/**
 * An implementation of the constraint: <code>FloatExp <= value</code>.
 */
public final class ConstraintFloatExpLessValue extends ConstraintImpl
{
  // PRIVATE MEMBERS
  private FloatExp	_exp;
  private double    _value;
  private Constraint _opposite;

  public ConstraintFloatExpLessValue(FloatExp exp, double value)
  {
    super(exp.constrainer(),"");//exp.name()+"<="+value);
    _name = "";
    if (constrainer().showInternalNames())
      _name = exp.name()+"<="+value;
    _exp = exp;
    _value = value;
    _opposite = null;
  }

  /*
  resetValue(double v) throws Failure
  {
    _value = v;
    _exp.setMax(_value);
  }
  */
  public void resetValue(double v)
  {
    _value = v;
  }

  public Goal execute() throws Failure
  {
    class ObserverFloatLessValue extends Observer
    {
      public void update(Subject exp, EventOfInterest interest)	throws Failure
      {
        //Debug.on();Debug.print("ObserverFloatLessValue: "+interest);Debug.off();
        FloatEvent event = (FloatEvent)interest;
        if (FloatCalc.gt (event.min (), _value)) {
          exp.constrainer().fail("from ObserverFloatLessValue");
        }

        _exp.setMax(_value);
      }

      public int subscriberMask()
      {
         return EventOfInterest.VALUE | EventOfInterest.MIN;
      }

      public String toString()
      {
        return "ObserverFloatLessValue";
      }

      public Object master()
      {
        return ConstraintFloatExpLessValue.this;
      }

    } //~ ObserverFloatLessValue

    _exp.setMax(_value); // may fail
    _exp.attachObserver(new ObserverFloatLessValue());
    return null;
  }

  public Constraint opposite()
  {
    if (_opposite == null)
      _opposite = new ConstraintFloatExpMoreValue(_exp,_value);
    return _opposite;
  }

  public boolean isLinear(){
    return _exp.isLinear();
  }

  public IntBoolExp toIntBoolExp(){
    return _exp.le(_value);
  }

  public String toString()
  {
    return _exp+"<="+_value;
  }

} //~ ConstraintFloatExpLessValue
