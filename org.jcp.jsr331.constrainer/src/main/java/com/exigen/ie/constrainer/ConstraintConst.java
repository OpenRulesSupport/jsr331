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
 * An implementation of the trivial constraint for which its actual
 * meaning "true" or "false" is known during the construction.
 */
public final class ConstraintConst extends ConstraintImpl
{
  private boolean _flag;

  /**
   * Constructs the constraint.
   * @param flag the boolean value for this constraint known during the construction.
   */
  public ConstraintConst(Constrainer c, boolean flag)
  {
    super(c,"Const");
    _flag = flag;
  }

  public Goal execute() throws Failure
  {
    if (!_flag)
      constrainer().fail("Const");
    return null;
  }

  public Constraint opposite()
  {
     return new ConstraintConst(constrainer(),!_flag);
  }

  public boolean isLinear(){
    return true;
  }

  public IntBoolExp toIntBoolExp(){
    ExpressionFactory factory = constrainer().expressionFactory();
    Class clazz = IntBoolExpConst.class;
    Object[] args = new Object[]{constrainer(), new Boolean(_flag)};
    Class[] types = new Class[]{Constrainer.class, boolean.class};
    return (IntBoolExpConst) factory.getExpression(clazz, args, types);
  }

  public String toString()
  {
    return (_flag?"TRUE":"FALSE");
  }
}
