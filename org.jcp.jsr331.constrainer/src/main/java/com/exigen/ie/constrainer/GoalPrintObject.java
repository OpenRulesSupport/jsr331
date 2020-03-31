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
 * An implementation of a {@link Goal} that prints an object.
 */
public class GoalPrintObject extends GoalImpl
{
  private Object _object;
  /**
   * Creates a GoalPrintObject
   * @param constrainer A Constrainer
   * @param object An object to be printed
   */
  public GoalPrintObject(Constrainer constrainer, Object object)
  {
    super(constrainer, "Print");
    _object = object;
  }

  /**
   * Prints the object to System.out.
   */
  public Goal execute() throws Failure
  {
    System.out.print(_object);
    return null;
  }
}
