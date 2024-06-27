package com.exigen.ie.constrainer;

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
 * A generic implementation of a ConstrainerObject interface.
 */
public class ConstrainerObjectImpl implements ConstrainerObject
{
  protected Constrainer _constrainer;
  private Object        _object;
  protected String      _name;

  /**
   * Constructor with a given constrainer and name.
   */
  public ConstrainerObjectImpl(Constrainer c, String name)
  {
    _constrainer = c;
    _object = null;
    if (c.showInternalNames())
      _name = name;
    else
      _name = "";
  }

  public ConstrainerObjectImpl(Constrainer c)
  {
    this(c,"");
  }

  final public Constrainer constrainer()
  {
    return _constrainer;
  }

  final public String name()
  {
    return _name;
  }

  public void name(String name)
  {
    _name = name;
  }

  /**
   * Sets name and manages symbolic context for this object.
   */
  protected void symbolicName(String name)
  {
//    String oldName = _name;
    _name = name;
//    try
//    {
//      constrainer().symbolicContext().renameVar(oldName,name,this);
//    }
//    catch(Exception e)
//    {
//      constrainer().out().println(e.getMessage());
//    }
  }

  public Object object()
  {
    return _object;
  }

  public void object(Object o)
  {
    _object = o;
  }

  /**
   * This method aborts the program execution. It prints the "msg" and displays
   * the stack trace. Used to display "impossible" errors.
   */
  public void abort(String msg)
  {
    Constrainer.abort(msg);
  }

  /**
   * Returns a String representation of this object.
   * @return a String representation of this object.
   */
  public String toString()
  {
    return _name;
  }
} // ~ConstrainerObjectImpl
