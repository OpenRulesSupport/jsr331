package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.ConstrainerObjectImpl;
import com.exigen.ie.constrainer.Undo;
import com.exigen.ie.constrainer.Undoable;

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
 * An implementation of the Undoable interface for the undoable only once object.
 */
public abstract class UndoableOnceImpl extends ConstrainerObjectImpl implements Undoable
{
  protected boolean     _undone;

  /**
   * Constructor with a given constrainer and name.
   */
  public UndoableOnceImpl(Constrainer c, String name)
  {
    super(c,name);
    _undone = false;
  }

  /**
   * Constructor with a given constrainer.
   */
  public UndoableOnceImpl(Constrainer c)
  {
    this(c,"");
  }

  public final boolean undone()
  {
    return _undone;
    //return false;
  }

  public final void undone(boolean b)
  {
    _undone = b;
  }

  public final void restore()
  {
    _undone = false;
  }

  public void addUndo()
  {
    if (!_undone)
    {
      _undone = true;
      Undo undo_object = createUndo();
      undo_object.undoable(this);
      //Debug.on();Debug.print("add " + undo_object);Debug.off();
//      constrainer().addUndo(undo_object);
      constrainer().addUndo(undo_object,this);
    }
  }

} // ~UndoableOnceImpl
