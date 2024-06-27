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

import com.exigen.ie.tools.ReusableImpl;

/**
 * A generic implementation of the Undo interface.
 */
public class UndoImpl extends ReusableImpl implements Undo
{
  //private boolean    _undone_flag;
  protected Undoable _undoable;

  public void undoable(Undoable u)
  {
    _undoable = u;
  }

  public Undoable undoable()
  {
    return _undoable;
  }

  /*
  public void undone(boolean f)
  {
    _undone_flag = f;
  }

  public boolean undone()
  {
    return _undone_flag;
  }
  */

  public void undo()
  {
    free();
   /*
    try
    {
      _undoable.undone(_undone_flag);
      free();
    }
    catch(Exception e)
    {
      System.out.println("UNEXPECTED EXCEPTION during undo for "+this+": "+e);
    }
   */
  }

  /**
   * Returns a String representation of this object.
   * @return a String representation of this object.
   */
  public String toString()
  {
    return "UNDO";
  }

} // ~UndoImpl
