package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.FloatDomain;
import com.exigen.ie.constrainer.FloatVar;

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

/*
* Changes:
* 02.20.03 Constrainer.FLOAT_PRECISION is added by SV
*/
/**
 * An implementation the domain for the FloatVar.
 */
public class FloatDomainImpl implements FloatDomain
{
  private FloatVar      _variable;
  private double        _initial_min;
  private double        _initial_max;
  private double        _min;
  private double        _max;

  public FloatDomainImpl(FloatVar var, double min, double max) {
    _variable = var;
    _initial_min = min;
    _initial_max = max;
    _min = min;
    _max = max;
  }
  public Constrainer constrainer () {
    return _variable.constrainer ();
  }
  public boolean contains(double value) {
    if (value < _min - Constrainer.FLOAT_PRECISION || value > _max + Constrainer.FLOAT_PRECISION) {
      return false;
    }
    return true;
  }
  public void forceMax (double M) { _max = M; }
  public void forceMin (double m) { _min = m; }
  public double max () { return _max; }
  public double min () { return _min; }
  public boolean setMax (double M) throws Failure {
    if (M > _max) {
      return false;
    }
    if (M < _min  - Constrainer.FLOAT_PRECISION) {
      constrainer ().fail ("FloatDomainImpl setMax");
    }
    _variable.addUndo ();
    _max = M;
    return true;
  }
  public boolean setMin (double m) throws Failure {
    if (m < _min) {
      return false;
    }
    if (m > _max + Constrainer.FLOAT_PRECISION) {
      constrainer ().fail ("FloatDomainImpl setMin");
    }
    _variable.addUndo ();
    _min = m;
    return true;
  }
  public boolean setValue (double value) throws Failure {
    if (!contains (value)) {
      constrainer().fail("attempt to set invalid value for "+_variable);
    }
    if(Math.abs (_max - _min) < Constrainer.FLOAT_PRECISION) {
      return false;
    }
    _variable.addUndo ();
    _min = value;
    _max = value;
    return true;
  }
  public double size () { return _max - _min; }
  public void variable (FloatVar var) { _variable = var; }
  /**
   * Returns a String representation of this object.
   * @return a String representation of this object.
   */
  public String toString () {
    return  "[" + _min + ".." + _max + (_variable.bound () ? ( "(" + (_min + _max) / 2 + ")" ) : "" ) + "]";
  }
}