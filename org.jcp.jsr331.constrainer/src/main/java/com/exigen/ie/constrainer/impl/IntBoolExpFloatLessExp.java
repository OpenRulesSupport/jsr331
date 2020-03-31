package com.exigen.ie.constrainer.impl;
import java.util.Map;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.FloatExp;
import com.exigen.ie.constrainer.NonLinearExpression;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;
/**
 * An implementation of the expression: <code>(FloatExp1 LE FloatExp2)</code>.
 */
public class IntBoolExpFloatLessExp extends IntBoolExpForSubject
{
  protected FloatExp _left, _right;
  protected double _offset;
  private Observer _observer;

  public IntBoolExpFloatLessExp(FloatExp left, FloatExp right, double offset){
    this(left, right, offset, left.constrainer(), "");
  }

  public IntBoolExpFloatLessExp(FloatExp left, FloatExp right)
  {
    this(left, right, 0, left.constrainer(), "");
  }

  class ObserverMinMax extends Observer
  {
    public int subscriberMask()
    {
      return MIN | MAX;
    }

    public void update(Subject subject, EventOfInterest interest)
                    throws Failure
    {
      setDomainMinMax();
    }

    public Object master()
    {
      return IntBoolExpFloatLessExp.this;
    }

  } // ~ObserverMinMax

  public IntBoolExpFloatLessExp(FloatExp left, FloatExp right, double offset, Constrainer c, String name)
  {
    super(c, name);
    _left = left;
    _right = right;
    _offset = offset;

    if(constrainer().showInternalNames())
    {
      _name = "("+left.name()+"||"+right.name()+")";
    }
    setDomainMinMaxSafe();

    _observer = new ObserverMinMax();
    _left.attachObserver(_observer);
    _right.attachObserver(_observer);
  }

  public boolean isSubjectTrue () {
    return FloatCalc.ge (_right.min ()+_offset, _left.max ());
  }
  public boolean isSubjectFalse () {
    return FloatCalc.gt (_left.min (), _right.max ()+_offset);
  }

  protected void setSubjectTrue() throws Failure
  {
    // left <= right + offset
    _left.setMax(_right.max()-_offset);
    _right.setMin(_left.min()+_offset);
  }

  protected void setSubjectFalse() throws Failure
  {
    // left >= right + offset
    _left.setMin(_right.min()+_offset);
    _right.setMax(_left.max()-_offset);
  }


  public boolean isLinear(){
    return (_left.isLinear() && _right.isLinear());
  }

  public double calcCoeffs(Map map, double factor) throws NonLinearExpression{
    return (_right.sub(_left).add(_offset)).calcCoeffs(map, factor);
  }

} // ~IntBoolExpFloatLessExp
