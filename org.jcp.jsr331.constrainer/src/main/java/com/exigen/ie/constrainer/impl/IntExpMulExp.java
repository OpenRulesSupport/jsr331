package com.exigen.ie.constrainer.impl;

import java.util.Map;

import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.NonLinearExpression;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;
//
//: IntExpMulExp.java
//
/**
 * An implementation of the expression: <code>(IntExp1 * IntExp2)</code>.
 */
public final class IntExpMulExp extends IntExpImpl
{
  private IntExp _exp1, _exp2;
  private Observer _observer;
  private IntVar _product;
  private IntExpMulExpCalc _calc;

  /**
   * Various calculations depending on exp1 and exp2 signs.
   */
  abstract class IntExpMulExpCalc
  {
    abstract public void setMin(int min) throws Failure;
    abstract public void setMax(int max) throws Failure;
    abstract public int min();
    abstract public int max();

    // May be overriden where min/max more optimal to calculate simultaneously.
    public void createProduct()
    {
      createProductVar(min(), max());
    }

    // May be overriden where min/max more optimal to calculate simultaneously.
    public void updateFromObserver() throws Failure
    {
      updateProductVar(min(), max());
    }
  }

  /**
   * Calculation for general exp1 and exp2.
   */
  final class CalcGeneral extends IntExpMulExpCalc
  {
    private IntExp _exp1, _exp2;

    public CalcGeneral(IntExp exp1, IntExp exp2)
    {
      _exp1 = exp1;
      _exp2 = exp2;
    }

    public int min()
    {
      return IntCalc.productMin(_exp1.min(),_exp1.max(),_exp2.min(),_exp2.max());
    }

    public int max()
    {
      return IntCalc.productMax(_exp1.min(),_exp1.max(),_exp2.min(),_exp2.max());
    }

    public void createProduct()
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      int min2 = _exp2.min();
      int max2 = _exp2.max();
      int min = IntCalc.productMin(min1,max1,min2,max2);
      int max = IntCalc.productMax(min1,max1,min2,max2);
      createProductVar(min, max);
    }

    public void updateFromObserver() throws Failure
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      int min2 = _exp2.min();
      int max2 = _exp2.max();
      int min = IntCalc.productMin(min1,max1,min2,max2);
      int max = IntCalc.productMax(min1,max1,min2,max2);
      updateProductVar(min, max);
    }

    public void setMax(int max) throws Failure
    {
      IntCalc.productSetMax(max, _exp1, _exp2);
      IntCalc.productSetMax(max, _exp2, _exp1);
    }

    public void setMin(int min) throws Failure
    {
      IntCalc.productSetMin(min, _exp1, _exp2);
      IntCalc.productSetMin(min, _exp2, _exp1);
    }

  } // ~CalcGeneral

  /**
   * Calculation for exp1 >= 0.
   */
  final class CalcP extends IntExpMulExpCalc
  {
    private IntExp _exp1, _exp2;

    public CalcP(IntExp exp1, IntExp exp2)
    {
      _exp1 = exp1;
      _exp2 = exp2;
    }

    public int min()
    {
      return IntCalc.productMinP(_exp1.min(),_exp1.max(),_exp2.min());
    }

    public int max()
    {
      return IntCalc.productMaxP(_exp1.min(),_exp1.max(),_exp2.max());
    }

    public void createProduct()
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      int min2 = _exp2.min();
      int max2 = _exp2.max();
      int min = IntCalc.productMinP(min1,max1,min2);
      int max = IntCalc.productMaxP(min1,max1,max2);
      createProductVar(min, max);
    }

    public void updateFromObserver() throws Failure
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      int min2 = _exp2.min();
      int max2 = _exp2.max();
      int min = IntCalc.productMinP(min1,max1,min2);
      int max = IntCalc.productMaxP(min1,max1,max2);
      updateProductVar(min, max);
    }

    public void setMax(int max) throws Failure
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      IntCalc.productSetMaxP(max, min1, max1, _exp2);
      IntCalc.productSetMax(max, _exp2, _exp1);
    }

    public void setMin(int min) throws Failure
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      IntCalc.productSetMinP(min, min1, max1, _exp2);
      IntCalc.productSetMin(min, _exp2, _exp1);
    }

  } // ~CalcP

  /**
   * Calculation for exp1 <= 0.
   */
  final class CalcN extends IntExpMulExpCalc
  {
    private IntExp _exp1, _exp2;

    public CalcN(IntExp exp1, IntExp exp2)
    {
      _exp1 = exp1;
      _exp2 = exp2;
    }

    public int min()
    {
      return IntCalc.productMinN(_exp1.min(),_exp1.max(),_exp2.max());
    }

    public int max()
    {
      return IntCalc.productMaxN(_exp1.min(),_exp1.max(),_exp2.min());
    }

    public void createProduct()
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      int min2 = _exp2.min();
      int max2 = _exp2.max();
      int min = IntCalc.productMinN(min1,max1,max2);
      int max = IntCalc.productMaxN(min1,max1,min2);
      createProductVar(min, max);
    }

    public void updateFromObserver() throws Failure
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      int min2 = _exp2.min();
      int max2 = _exp2.max();
      int min = IntCalc.productMinN(min1,max1,max2);
      int max = IntCalc.productMaxN(min1,max1,min2);
      updateProductVar(min, max);
    }

    public void setMax(int max) throws Failure
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      IntCalc.productSetMaxN(max, min1, max1, _exp2);
      IntCalc.productSetMax(max, _exp2, _exp1);
    }

    public void setMin(int min) throws Failure
    {
      int min1 = _exp1.min();
      int max1 = _exp1.max();
      IntCalc.productSetMinN(min, min1, max1, _exp2);
      IntCalc.productSetMin(min, _exp2, _exp1);
    }

  } // ~CalcN

  /**
   * Calculation for exp1 >= 0 && exp2 >= 0.
   */
  final class CalcPP extends IntExpMulExpCalc
  {

    private IntExp _exp1, _exp2;

    public CalcPP(IntExp exp1, IntExp exp2)
    {
      _exp1 = exp1;
      _exp2 = exp2;
    }

    public int min()
    {
      return _exp1.min()*_exp2.min();
    }

    public int max()
    {
      return _exp1.max()*_exp2.max();
    }

    public void setMax(int max) throws Failure
    {
      IntCalc.productSetMaxP(max, _exp1.min(), _exp1.max(), _exp2);
      IntCalc.productSetMaxP(max, _exp2.min(), _exp2.max(), _exp1);
    }

    public void setMin(int min) throws Failure
    {
      IntCalc.productSetMinP(min, _exp1.min(), _exp1.max(), _exp2);
      IntCalc.productSetMinP(min, _exp2.min(), _exp2.max(), _exp1);
    }

  } // ~CalcPP

  /**
   * Calculation for exp1 <= 0 && exp2 <= 0.
   */
  final class CalcNN extends IntExpMulExpCalc
  {
    private IntExp _exp1, _exp2;

    public CalcNN(IntExp exp1, IntExp exp2)
    {
      _exp1 = exp1;
      _exp2 = exp2;
    }

    public int min()
    {
      return _exp1.max()*_exp2.max();
    }

    public int max()
    {
      return _exp1.min()*_exp2.min();
    }

    public void setMax(int max) throws Failure
    {
      IntCalc.productSetMaxN(max, _exp1.min(), _exp1.max(), _exp2);
      IntCalc.productSetMaxN(max, _exp2.min(), _exp2.max(), _exp1);
    }

    public void setMin(int min) throws Failure
    {
      IntCalc.productSetMinN(min, _exp1.min(), _exp1.max(), _exp2);
      IntCalc.productSetMinN(min, _exp2.min(), _exp2.max(), _exp1);
    }

  } // ~CalcNN

  /**
   * Calculation for exp1 >= 0 && exp2 <= 0.
   */
  final class CalcPN extends IntExpMulExpCalc
  {
    private IntExp _exp1, _exp2;

    public CalcPN(IntExp exp1, IntExp exp2)
    {
      _exp1 = exp1;
      _exp2 = exp2;
    }

    public int min()
    {
      return _exp1.max()*_exp2.min();
    }

    public int max()
    {
      return _exp1.min()*_exp2.max();
    }

    public void setMax(int max) throws Failure
    {
      IntCalc.productSetMaxP(max, _exp1.min(), _exp1.max(), _exp2);
      IntCalc.productSetMaxN(max, _exp2.min(), _exp2.max(), _exp1);
    }

    public void setMin(int min) throws Failure
    {
      IntCalc.productSetMinP(min, _exp1.min(), _exp1.max(), _exp2);
      IntCalc.productSetMinN(min, _exp2.min(), _exp2.max(), _exp1);
    }

  } // ~CalcPN

  class IntExpMulExpObserver extends Observer
  {

    public void update(Subject subject, EventOfInterest event)
    throws Failure
    {
      _calc.updateFromObserver();
    }

    public String toString()
    {
      return "IntExpMulExpObserver: " + _exp1 + " x " + _exp2;
    }

    public Object master()
    {
      return IntExpMulExp.this;
    }

    public int subscriberMask()
    {
      return MIN | MAX | VALUE;
    }

  } //~ IntExpMulExpObserver


  public IntExpMulExp(IntExp exp1, IntExp exp2)
  {
    super(exp1.constrainer());
    _exp1 = exp1;
    _exp2 = exp2;

    if(constrainer().showInternalNames())
    {
      _name = "("+exp1.name()+"*"+exp2.name()+")";
    }

    createCalc();

    _calc.createProduct();

    _observer = new IntExpMulExpObserver();
    _exp1.attachObserver(_observer);
    _exp2.attachObserver(_observer);
  }

  void createProductVar(int min, int max)
  {
    int trace = 0;
    _product = constrainer().addIntVarTraceInternal(min, max, _name, IntVar.DOMAIN_PLAIN, trace);
  }

  void updateProductVar(int min, int max) throws Failure
  {
    _product.setMin(min);
    _product.setMax(max);
  }

  void createCalc()
  {
    // exp1 >= 0 -> P*
    if(_exp1.min() >= 0)
    {
      // exp2 >= 0 -> PP
      if(_exp2.min() >= 0)
      {
        _calc = new CalcPP(_exp1,_exp2);
      }
      // exp2 <= 0 -> PN
      else if(_exp2.max() <= 0)
      {
        _calc = new CalcPN(_exp1,_exp2);
      }
      // exp2 changes sign -> P*
      else
      {
        _calc = new CalcP(_exp1,_exp2);
      }
    }
    // exp1 <= 0 -> N*
    else if(_exp1.max() <= 0)
    {
      // exp2 >= 0 -> NP
      if(_exp2.min() >= 0)
      {
        _calc = new CalcPN(_exp2,_exp1); // NP
      }
      // exp2 <= 0 -> NN
      else if(_exp2.max() <= 0)
      {
        _calc = new CalcNN(_exp1,_exp2);
      }
      // exp2 changes sign -> N*
      else
      {
        _calc = new CalcN(_exp1,_exp2);
      }
    }
    // exp1 changes sign
    else
    {
      // exp2 >= 0 -> *P
      if(_exp2.min() >= 0)
      {
        _calc = new CalcP(_exp2,_exp1);
      }
      // exp2 <= 0 -> *N
      else if(_exp2.max() <= 0)
      {
        _calc = new CalcN(_exp2,_exp1);
      }
      // exp2 changes sign -> **
      else
      {
        _calc = new CalcGeneral(_exp1,_exp2);
      }
    }
  }

  public void name(String name)
  {
    super.name(name);
    _product.name(name);
  }

  public void attachObserver(Observer observer)
  {
    super.attachObserver(observer);
    _product.attachObserver(observer);
  }

  public void reattachObserver(Observer observer)
  {
    super.reattachObserver(observer);
    _product.reattachObserver(observer);
  }

  public void detachObserver(Observer observer)
  {
    super.detachObserver(observer);
    _product.detachObserver(observer);
  }

  public int max()
  {
    return _product.max();
  }

  public int min()
  {
    return _product.min();
  }

  public void setMax(int max) throws Failure
  {
//    System.out.println("setmax: " + max + " in "  + this);

    if (max >= max())
      return;

    _product.setMax(max);

//    if (max < min())
//    {
//      constrainer().fail("IntExpMulExp.setMax(): max < min()");
//    }

    _calc.setMax(max);
  }

  public void setMin(int min) throws Failure
  {
//    System.out.println("setmin: " + min + " in "  + this);

    if (min <= min())
      return;

    _product.setMin(min);

//   if (min > max())
//    {
//      constrainer().fail("IntExpMulExp.setMin(): min > max()");
//    }

    _calc.setMin(min);
  }

  public void setValue(int value) throws Failure
  {
    setMin(value);
    setMax(value);
  }

  public boolean isLinear(){
    if (! ( (_exp1.bound()) || (_exp2.bound()) ) )
      return false;
    return (_exp1.isLinear() && _exp2.isLinear());
  }

   public double calcCoeffs(Map map, double factor) throws NonLinearExpression{
    if (_exp1.bound())
      return _exp2.calcCoeffs(map,factor*_exp1.max());
    if (_exp2.bound())
      return _exp1.calcCoeffs(map, factor*_exp2.max());
    throw new NonLinearExpression(this);
  }

} // ~IntExpMulExp
