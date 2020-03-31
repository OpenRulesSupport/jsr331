package com.exigen.ie.constrainer.impl;
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
 * An implementation of the constraint: <code>IntExp1 == IntExp2 + offset</code>.
 */
public final class ConstraintExpEqualsExp extends ConstraintImpl
{
  // PRIVATE MEMBERS
  private IntExp	_exp1;
  private IntExp	_exp2;
  private int		  _offset;

  public ConstraintExpEqualsExp(IntExp exp1, IntExp exp2)
  {
    this(exp1,exp2,0);
  }

  /**
   * exp1 == exp2 + offset
   */
  public ConstraintExpEqualsExp(IntExp exp1, IntExp exp2, int offset)
  {
    super(exp1.constrainer());

    _exp1 = exp1;
    _exp2 = exp2;
    _offset = offset;

    if (constrainer().showInternalNames())
    {
      if(offset == 0)
        _name = "(" + exp1.name() + "==" + exp2.name() + ")";
      else if(offset>0)
        _name = "(" + exp1.name() + "==" + exp2.name() + "+" + offset + ")";
      else
        _name = "(" + exp1.name() + "==" + exp2.name() + offset + ")";
    }

  }

  public Goal execute() throws Failure
  {
    class ObserverEqual extends Observer
    {
      public void update(Subject exp, EventOfInterest interest)
        throws Failure
      {
        //Debug.on();Debug.print("ObserverEqual: "+_name+" Event: "+interest);Debug.off();
        IntEvent event = (IntEvent)interest;
        if (event.isRemoveEvent())
        {
          int max = event.numberOfRemoves();
          for(int i =0; i < max; ++i)
          {
            if (exp == _exp1)
              _exp2.removeValue(event.removed(i)-_offset);
            else
            if (exp == _exp2)
              _exp1.removeValue(event.removed(i)+_offset);
          }
        }

        else
        {
          minmax();
        }
      }

      public int subscriberMask()
      {
         return EventOfInterest.MINMAX | EventOfInterest.REMOVE;
      }

      public String toString()
      {
        return _name+"(MinMax)";
      }

      public Object master()
      {
        return ConstraintExpEqualsExp.this;
      }


    } //~ ObserverEqual

    minmax(); // may fail
    ObserverEqual observer = new ObserverEqual();
    _exp1.attachObserver(observer);
    _exp2.attachObserver(observer);
    return null;
  }

  public void minmax() throws Failure
  {
    _exp1.setMax(_exp2.max()+_offset); // may fail
    _exp2.setMax(_exp1.max()-_offset); // may fail
    _exp1.setMin(_exp2.min()+_offset); // may fail
    _exp2.setMin(_exp1.min()-_offset); // may fail
  }

  public boolean isLinear(){
    return (_exp1.isLinear() && _exp2.isLinear());
  }

  public IntBoolExp toIntBoolExp(){
    return _exp1.eq(_exp2.add(_offset));
  }

  public String toString()
  {
    return _exp1+"=="+_exp2;
  }

} //~ ConstraintExpEqualsExp
