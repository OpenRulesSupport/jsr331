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
 * An interface for the domain of the constrained floating-point variable.
 */
public interface FloatDomain
{
  /**
   * Returns a constrainer that owns this domain.
   */
  public Constrainer constrainer();

  /**
   * Returns true if this domain contains the value.
   */
  public boolean contains(double value);

  /**
   * Returns the largest value in this domain.
   */
  public double max();

  /**
   * Returns the smallest value in this domain.
   */
  public double min();

  /**
   * Sets the maximum value for this domain.
   *
   * @throws Failure if domain becomes empty.
   */
  public boolean setMax(double M) throws Failure;

  /**
   * Sets the minimum value for this domain.
   *
   * @throws Failure if domain becomes empty.
   */
  public boolean setMin(double m) throws Failure;

  /**
   * Sets this domain to the damain containing only the value.
   *
   * @throws Failure if domain becomes empty.
   */
  public boolean setValue(double value) throws Failure;

  /**
   * Returns the size (<code>max-min</code>) of this domain.
   */
  public double size();

  /**
   * Sets the variable that have this domain.
   */
  public void variable(FloatVar var);

  /**
   * Undo helper: sets the maximum value for this domain.
   */
  public void forceMax(double M);

  /**
   * Undo helper: sets the minimum value for this domain.
   */
  public void forceMin(double m);

} // ~FloatDomain
