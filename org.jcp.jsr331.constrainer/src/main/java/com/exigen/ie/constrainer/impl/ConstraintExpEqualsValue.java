package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.Constraint;
import com.exigen.ie.constrainer.ConstraintImpl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.ExpressionFactory;
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
 * An implementation of the constraint: <code>IntExp == value</code>.
 */
public final class ConstraintExpEqualsValue extends ConstraintImpl
{
  // PRIVATE MEMBERS
  private IntExp	    _exp;
  private int	        _value;
  private Constraint  _opposite;

  public ConstraintExpEqualsValue(IntExp exp, int value)
  {
    super(exp.constrainer());

    if(constrainer().showInternalNames())
    {
      _name = "("+exp.name()+"=="+value+")";
    }

    _exp = exp;
    _value = value;
  }

  public Goal execute() throws Failure
  {
    class ObserverEqualValue extends Observer
    {
      public void update(Subject exp, EventOfInterest interest)	throws Failure
      {
        //Debug.on();Debug.print("ObserverEqualValue: "+interest);Debug.off();
        IntEvent event = (IntEvent)interest;
        if ( (event.isValueEvent() && event.min() != _value) ||
             (event.isMaxEvent() && event.max() < _value) ||
             (event.isMinEvent() && event.min() > _value))
        {
          exp.constrainer().fail("from ObserverEqualValue");
        }
        _exp.setValue(_value);
      }

      public int subscriberMask()
      {
         return EventOfInterest.VALUE | EventOfInterest.MIN | EventOfInterest.MAX;
      }

      public String toString()
      {
        return "ObserverEqualValue";
      }

      public Object master()
      {
        return ConstraintExpEqualsValue.this;
      }



    } //~ ObserverEqualValue

    _exp.setValue(_value); // may fail
    _exp.attachObserver(new ObserverEqualValue());
    return null;
  }

  public Constraint opposite()
  {
    if (_opposite == null)
      _opposite = new ConstraintExpNotValue(_exp,_value);
    return _opposite;
  }

  public boolean isLinear(){
    return _exp.isLinear();
  }

  public IntBoolExp toIntBoolExp(){
    ExpressionFactory factory = constrainer().expressionFactory();
    Class clazz = IntBoolExpEqValue.class;
    Object[] args = new Object[]{_exp, new Integer(_value)};
    Class[] types = new Class[]{IntExp.class, int.class};

    return (IntBoolExpEqValue) factory.getExpression(clazz, args, types);
  }

  public String toString()
  {
    return _exp+"="+_value;
  }

} //~ ConstraintExpEqualsValue
