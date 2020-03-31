package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Undo;

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
 * An implementation of the undoable bit field.
 */
public final class UndoableBits extends UndoableImpl
{
  private boolean[]     _bits;
  private int           _min;

  public UndoableBits(Constrainer c, int min, int max)
  {
    super(c);
    int size = max - min +1;
    _bits = new boolean[size];
    for(int j=0; j < size; j++)
    {
      _bits[j] = false;
    }
    _min = min;
  }

  public UndoableBits(UndoableBits oldbits)
  {
    super(oldbits.constrainer());
    boolean[] bits = oldbits.bits();
    _bits = new boolean[bits.length];
    for(int j=0; j < bits.length; j++)
    {
      _bits[j] = bits[j];
    }
    _min = oldbits.min();
    object(oldbits.object());
  }

  boolean[] bits()
  {
    return _bits;
  }

  int size(boolean flag)
  {
    int s = 0;
    for(int j=0; j < _bits.length; j++)
    {
      if (_bits[j] == flag)
      s++;
    }
    return s;
  }

  public boolean bit(int value)
  {
    return _bits[value - _min];
  }

  public void bit(int value, boolean b)
  {
    _bits[value - _min] = b;
  }

  public int min()
  {
    return _min;
  }

  public int max()
  {
    return _min + _bits.length - 1;
  }

  public Undo createUndo()
  {
    return null; //stub!
    //return new UndoBits(this,value);
    //return (Undo)constrainer().undoBitsFactory().getElement();
  }

  public String toString()
  {
    String s = "bits("+_min+";"+max()+"):";
    for(int i=0; i < _bits.length; i++)
     s += " "+(_min+i)+(_bits[i]?"=1":"=0");
    return s;
  }

} // ~UndoableBits
