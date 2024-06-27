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
 * A generic implementation of the Undoable interface.
 */
public abstract class UndoableImpl extends ConstrainerObjectImpl implements Undoable
{
  /**
   * Constructor with a given constrainer and name.
   */
  public UndoableImpl(Constrainer c, String name)
  {
    super(c,name);
  }

  /**
   * Constructor with a given constrainer.
   */
  public UndoableImpl(Constrainer c)
  {
    super(c);
  }

  public boolean undone() { return false; }

  public void undone(boolean b) {}

  public void addUndo()
  {
    Undo undo_object = createUndo();
    undo_object.undoable(this);
    //Debug.on();Debug.print("add " + undo_object);Debug.off();
    constrainer().addUndo(undo_object);
  }

} // ~UndoableImpl
