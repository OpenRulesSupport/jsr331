package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.FloatExp;
import com.exigen.ie.constrainer.FloatVar;
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
 * An implementation of the expression: <code>pow(FloatExp,value)</code>.
 * <pre>
 * Assumptions:
 *  - Value is not integer and LT 0.
 *  - Exp GE 0.
 * </pre>
 * In this case pow(x,value) is monotonic by x.
 *  <p>
 *  Examples:
 *  <p>
 *  <code>FloatVar var = constrainer.addFloatVar(min,max,name);</code>
 *  <p>
 *  <code>FloatExp exp = var.pow(3.1);</code>
 *  
 */
public final class FloatExpPowValue extends FloatExpImpl
{
  private FloatExp	         _exp;
  private double             _value;
  private FloatVar           _result;
  private ExpressionObserver _observer;

  static final private int[] event_map = { MIN | MAX, MIN,
                                           MIN | MAX, MAX,
                                           VALUE, VALUE,
                                           REMOVE, REMOVE
                                          };


  class FloatExpPowValueObserver extends ExpressionObserver
  {

    FloatExpPowValueObserver()
    {
      super(event_map);
    }


    public void update(Subject exp, EventOfInterest event)
      throws Failure
    {
//      FloatEvent e = (FloatEvent) event;

      _result.setMin(calc_min());
      _result.setMax(calc_max());
    }


    public String toString()
    {
      return "FloatExpPowValueObserver: "+"pow("+_exp+","+_value+")";
    }

    public Object master()
    {
      return FloatExpPowValue.this;
    }
  } //~ FloatExpPowValueObserver

  public FloatExpPowValue(FloatExp exp, double value)
  {
    super(exp.constrainer(),"");//"pow("+exp.name()"+","+value+")");
    _exp = exp;
    _value = value;
    _observer = new FloatExpPowValueObserver();
    _exp.attachObserver(_observer);
    int trace = 0;
    _result = constrainer().addFloatVarTraceInternal(calc_min(), calc_max(), "pow", trace);
  }

  double calc_min()
  {
    return Math.pow(_exp.min(),_value);
  }

  double calc_max()
  {
    return Math.pow(_exp.max(),_value);
  }

  public void onMaskChange()
  {
    _observer.publish(publisherMask(),_exp);
  }

  public void attachObserver(Observer observer)
  {
    super.attachObserver(observer);
    _result.attachObserver(observer);
  }

  public void reattachObserver(Observer observer)
  {
    super.reattachObserver(observer);
    _result.reattachObserver(observer);
  }

  public void detachObserver(Observer observer)
  {
    super.detachObserver(observer);
    _result.detachObserver(observer);
  }

  public double max()
  {
    return _result.max();
  }

  public double min()
  {
    return _result.min();
  }

  public void setMax(double max) throws Failure
  {
    if(max < 0)
      constrainer().fail("max < 0");

    double expMax = FloatCalc.solve_pow(max,_value);
    _exp.setMax(expMax);
  }

  public void setMin(double min) throws Failure
  {
    if (min <= 0)
      return;

    double expMin = FloatCalc.solve_pow(min,_value);
    _exp.setMin(expMin);
  }

  public void setValue(double value) throws Failure
  {
    if(value <= 0)
      constrainer().fail("value <= 0");

    double expV = FloatCalc.solve_pow(value,_value);
    _exp.setValue(expV);
  }

  public String toString()
  {
    return "pow(" + _exp + "," + _value + ")" + domainToString();
  }

} // ~FloatExpPowValue
