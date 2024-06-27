package com.exigen.ie.constrainer;

///////////////////////////////////////////////////////////////////////////////
/*
 * Copyright Exigen Group 1998, 1999, 2000, 2002
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
 * An implementation of the IntVarSelector interface
 * that selects the unbound variable with the minimal minimum.
 *
 * @see IntVarSelector
 * @see IntVarSelectorFirstUnbound
 * @see IntVarSelectorMinSize
 * @see GoalGenerate
 */
public class IntVarSelectorMinMin implements IntVarSelector
{
  private IntExpArray _intvars;

  /**
   * Constructor from the IntExpArray.
   */
  public IntVarSelectorMinMin(IntExpArray intvars)
  {
   _intvars = intvars;
  }

 /**
  * Selects the unbound variable with the smallest domain.
  * If there are two variables with the same size,
  * it will select the variable with the minimal minimum.
  * If all variables are bound, returns -1.
  */
  public int select()
  {
    int max_size = -1;
    int min_min = Integer.MAX_VALUE;
    int min_index = -1;
    int size = _intvars.size();
    for(int i=0; i < size; i++)
    {
      IntVar vari = (IntVar)_intvars.elementAt(i);
      if (!vari.bound())
      {
        if (min_index==-1 || min_min >= vari.min())
        {
          if (min_min > vari.min())// || max_size < vari.size())
          {
            max_size = vari.size();
            min_min = vari.min();
            min_index = i;
          }
        }
      }
    }
    return min_index;
  }

} // ~IntVarSelectorMinMin
