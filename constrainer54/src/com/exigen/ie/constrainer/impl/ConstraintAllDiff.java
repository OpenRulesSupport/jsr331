package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.ConstraintImpl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
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

//
//: ConstraintAllDiff.java
//
/**
 * An implementation of the constraint "All Different".
 *
 * Any two constrained integer expressions from the array (parameter of this
 * constraint) should not be instantiated with the same value.
 */
public final class ConstraintAllDiff extends ConstraintImpl
{
  // PRIVATE MEMBERS
  private IntExpArray	     _intvars;
  private UndoableBits _bits; // one bit for every possible values among all _intvars

  //////////////////////////////////////////////// inner class AllDiffObserver
  class AllDiffObserver extends Observer
  {
    public void update(Subject var, EventOfInterest interest)
        throws Failure
    {
      IntExp event_var = (IntExp)var;
      //Debug.on(); Debug.print("AllDiffObserver("+var+") "+interest+" "+_bits); Debug.off();
      int size = _intvars.size();
      int value = event_var.value();
      boolean bit = _bits.bit(value);
      // check for failure
      if (bit)
      {
        //Debug.on();Debug.print("diff fail: "+interest+_intvars+_bits);Debug.off();
        var.constrainer().fail("AllDiff"); //+_bits +" "+_intvars);
      }

      // set the bit
      _bits.bit(value,true);  //Debug.on();Debug.print(" "+this+":  "+_bits);Debug.off();
      var.constrainer().addUndo(UndoBits.getUndo(_bits,value));

      // additional propagation

      IntExp[] data = _intvars.data();

      for(int i=0; i < size; i++)
      {
        IntExp vari = data[i];
        if (vari!=event_var)
        {
          //Debug.on();Debug.print("alldiff remove "+value+" from "+vari);Debug.off();
          vari.removeValue(value); // may fail
        }
      }
    }

     public int subscriberMask()
     {
       return  EventOfInterest.VALUE;
     }

     public String toString()
     {
        return "AllDiff";//+_intvars;
     }

      public Object master()
      {
        return ConstraintAllDiff.this;
      }


  } //~ AllDiffObserver

  ///////////////////////////////////// inner class AllDiffMinMaxObserver
  class AllDiffMinMaxObserver extends Observer
  {
    public void update(Subject var, EventOfInterest interest)
        throws Failure
    {
      int min = _bits.min();
      int max = _bits.max();
      //Debug.on();Debug.print("alldiff minmax "+interest+":  "+_bits);Debug.off();
      for(int i=0; i<_intvars.size(); i++)
      {
        IntExp vari = (IntExp)_intvars.elementAt(i);
        if (!vari.bound())
        {
          for(int v=min; v <=max; v++)
          {
            if (_bits.bit(v) && vari.contains(v))//(v==vari.min() || v==vari.max()))
            {
              //Debug.on();Debug.print("alldiff minmax remove "+v+" from "+vari);Debug.off();
              vari.removeValue(v); // may fail
              if (vari.bound())
                break;
            }
          }
        }
      }
    }

    public int subscriberMask()
    {
      return EventOfInterest.MINMAX;
    }

    public Object master()
    {
      return ConstraintAllDiff.this;
    }

  } //~ AllDiffMinMaxObserver

  public ConstraintAllDiff(IntExpArray intvars)
  {
    super(intvars.constrainer(),"AllDiff");
    _intvars = intvars;

    int min = intvars.min();
    int max = intvars.max();


    _bits = new UndoableBits(constrainer(), min,max);
    _bits.object(this);
  }

  void bits(UndoableBits b)
  {
    _bits = b;
  }

  UndoableBits bits()
  {
    return _bits;
  }

  public Goal execute() throws Failure
  {
    // initial propagation
    int size = _intvars.size();
    for(int i=0; i<size; i++)
    {
      IntExp vari = (IntExp)_intvars.elementAt(i);
      if (vari.bound())
      {
        int value = vari.value();
        boolean bit = _bits.bit(value);
        // check for failure
        if (bit)
        {
          constrainer().fail("Diff");
        }
        _bits.bit(value,true);
        constrainer().addUndo(UndoBits.getUndo(_bits,value));
      }
    }

    // attach observers
    Observer value_observer = new AllDiffObserver();
    Observer minmax_observer = new AllDiffMinMaxObserver();
    for(int i=0; i<size; i++)
    {
      IntExp vari = (IntExp)_intvars.elementAt(i);
      vari.attachObserver(value_observer);
    }

    return null;
  } // end of execute


} // ~ConstraintAllDiff
