package com.exigen.ie.constrainer.impl;
import java.util.Map;

import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.FloatExp;
import com.exigen.ie.constrainer.NonLinearExpression;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;
/**
 * An implementation of the expression: <code>(FloatExp == FloatExp + offset)</code>.
 */
public class IntBoolExpFloatEqExp extends IntBoolExpForSubject
{
  protected FloatExp _exp1, _exp2;
  protected double _offset;
  private Observer _observer;

  final class ObserverMinMax extends Observer
  {
    public int subscriberMask()
    {
      return MIN | MAX;
    }

    public void update(Subject exp, EventOfInterest interest)
                    throws Failure
    {
      setDomainMinMax();
    }

    public Object master()
    {
      return IntBoolExpFloatEqExp.this;
    }

  } // ~ObserverMinMax


  public IntBoolExpFloatEqExp(FloatExp exp1, FloatExp exp2)
  {
    this(exp1,exp2,0);
  }

  public IntBoolExpFloatEqExp(FloatExp exp1, FloatExp exp2, double offset)
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

    setDomainMinMaxSafe();

    _observer = new ObserverMinMax();
    _exp1.attachObserver(_observer);
    _exp2.attachObserver(_observer);
  }

  protected boolean isSubjectTrue()
  {
    // both are bound and equals
    try
    {
      return _exp1.bound() && _exp2.bound() &&
             FloatCalc.eq (_exp1.value(),_exp2.value() + _offset);
    }
    catch(Failure e)
    {
      return false; // should never happenned
    }
  }

  protected boolean isSubjectFalse()
  {
    // exp1 > exp2 || exp1 < exp2
    return
        FloatCalc.gt (_exp1.min (), _exp2.max () + _offset)
      ||
        FloatCalc.gt (_exp2.min () + _offset, _exp1.max ());
  }

  protected void setSubjectTrue() throws Failure
  {
    // exp1 == exp2
    _exp1.setMax(_exp2.max()+_offset);
    _exp2.setMax(_exp1.max()-_offset);
    _exp1.setMin(_exp2.min()+_offset);
    _exp2.setMin(_exp1.min()-_offset);
  }

  protected void setSubjectFalse() throws Failure
  {
    // exp1 != exp2 -> nothing to propagate for floats
  }

  public boolean isLinear(){
    return (_exp1.isLinear() && _exp2.isLinear());
  }

  public double calcCoeffs(Map map, double factor) throws NonLinearExpression{
    return (_exp2.sub(_exp1).add(_offset)).calcCoeffs(map, factor);
  }

} // ~IntBoolExpFloatEqExp
