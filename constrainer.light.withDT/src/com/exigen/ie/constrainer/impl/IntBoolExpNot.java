package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
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

/**
 * An implementation of the expression: <code>(!IntBoolExp)</code>.
 * This implementation doesn't have an internal state.
 */
public final class IntBoolExpNot extends IntBoolExpImpl
{
  private IntBoolExp _opposite;
  private Observer _observer;

  class ObserverBoolExpNot extends Observer
  {
    public int subscriberMask()
    {
      return MIN | MAX;
    }

    public void update(Subject subject, EventOfInterest interest)
    throws Failure
    {
      // opposite is false
      if(interest.isMaxEvent())
      {
        notifyObservers(IntBoolVarImpl.IntEventBoolTrue.the);
      }
      else
      {
        notifyObservers(IntBoolVarImpl.IntEventBoolFalse.the);
      }
    }

    public Object master()
    {
      return IntBoolExpNot.this;
    }

  }

  public IntBoolExpNot(IntBoolExp opposite)
  {
    super(opposite.constrainer());
    _opposite = opposite;

    if(constrainer().showInternalNames())
    {
      _name = "!"+opposite.name();
    }

    _observer=new ObserverBoolExpNot();
    _opposite.attachObserver(_observer);
  }

  public int min()
  {
    return 1 - _opposite.max();
  }

  public int max()
  {
    return 1 - _opposite.min();
  }

  public void setMin(int min) throws Failure
  {
    _opposite.setMax(1 - min);
  }

  public void setMax(int max) throws Failure
  {
    _opposite.setMin(1 - max);
  }

  public IntBoolExp not()
  {
    return _opposite;
  }

} // ~IntBoolExpNot

/*
/**
 * An implementation of the expression: <code>(!IntBoolExp)</code>.
 * This implementation has an internal state.
 *
final class IntBoolExpNot extends IntBoolExpForSubject
{
  private IntBoolExp _opposite;
  private Observer _observer;

  class ObserverBoolExpNot extends Observer
  {
    public int subscriberMask()
    {
      return MIN | MAX;
    }

    public void update(Subject subject, EventOfInterest interest)
    throws Failure
    {
      // opposite is false
      if(interest.isMaxEvent())
      {
        setDomainMin(1);
      }
      else
      {
        setDomainMax(0);
      }
    }

    public Object master()
    {
      return IntBoolExpNot.this;
    }

  }

  public IntBoolExpNot(IntBoolExp opposite)
  {
    super(opposite.constrainer());
    _opposite = opposite;

    if(constrainer().showInternalNames())
    {
      _name = "!"+opposite.name();
    }
    setDomainMinMaxSafe();

    _observer=new ObserverBoolExpNot();
    _opposite.attachObserver(_observer);
  }

  public boolean isSubjectTrue()
  {
    return _opposite.isFalse();
  }

  public boolean isSubjectFalse()
  {
    return _opposite.isTrue();
  }

  protected void setSubjectTrue() throws Failure
  {
    _opposite.setFalse();
  }

  protected void setSubjectFalse() throws Failure
  {
    _opposite.setTrue();
  }

  public IntBoolExp not()
  {
    return _opposite;
  }

} // ~IntBoolExpNot
*/
