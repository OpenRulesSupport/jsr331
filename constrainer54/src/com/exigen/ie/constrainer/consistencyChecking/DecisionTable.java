package com.exigen.ie.constrainer.consistencyChecking;

/**
 * <p>Title: </p>
 * <p>Description: Interface for gaining access to the data of the Decision Table.</p>
 * <p>
 * Decision Table is represented as a matrix consisted of m columns - variables and n rows - rules.
 * Thus the entry with coordinates [i,j] is a constraint imposed by i'th rule on the j'th variable.
 * The conjunction of all the constraint situated in the same row called "Rule". Formally speaking,
 * rule is an area in the space of states which bounds are due to the constraints being part of it.
 * </p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
import com.exigen.ie.constrainer.IntBoolExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;

public interface DecisionTable {
  /**
   * @return an array of rules. Rule is the conjuction of all the constraints located in
   * the i'th row of the constraint matrix.
   * Note: All the values in the returned array should be not <b><code>null</code></b>.
   */
  public IntBoolExp [] getRules ();
  /**
   * @param The number of rule to be returned (actually it is the number of the appropriate
   * row from the data matrix).
   *
   * @return the conjunction of all the constraints from the i'th row.
   * Note: it should return not <b><code>null</code></b> for all decision table rows.
   */
  public IntBoolExp getRule (int i);
  /**
   * @return all variables.
   */
  public IntExpArray getVars ();
  /**
   * @param The number of variable to be returned.
   *
   * @return the i'th variable.
   */
  public IntVar getVar (int i);
  /**
   * @param the number of rule the constraint belongs to
   * @param the number of variable the constraint imposed on
   *
   * @return the constraint imposed by the i'th rule on the j'th variable
   * Note: may return nulls for some decision table entrys.
   */
  public IntBoolExp getEntry (int i, int j);
}