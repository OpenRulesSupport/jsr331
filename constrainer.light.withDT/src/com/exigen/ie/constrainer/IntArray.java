package com.exigen.ie.constrainer;
import java.util.Vector;

import com.exigen.ie.constrainer.impl.IntExpElementAt;

/**
 * It is an array-like container for the variables of type <p>IntExp</p>.
 * The essential difference between <p>IntArray</p> and <p>IntExpArray</p> is that the former
 * is able to use a variable of type <p>IntExp</p> as index.
 * If an "index" in an "array" is a constrained integer expression, then the
 * "array[index]" is also the constrained integer variable.
 * To get the access to the element of an array using constrained index one has to
 * use method {@link #elementAt(IntExp)}.
 */

public final class IntArray extends ConstrainerObjectImpl
{
  private int[] _data;

  /**
   * Returns a constrained integer variables that corresponds to such value
   * from this array which has index "ind_exp". For example,
   * <pre>
   *
   * IntArray costs = new IntArray(C, 50, 75, 35, 75);
   * IntVar index = C.addIntVar(1,2);
   * IntVar element = costs.elementAt(index); // could have values 75 or 35 only
   *
   * </pre>
   * For the sake of clarity, let's call A the invoking array. When idx_exp is bound to the
   * value i, the value of the resulting variable is A[i]. More generally, the domain of
   * the variable is the set of values A[i] where the i are in the domain of idx_exp.
   */
  public IntExp elementAt(IntExp idx_exp) //throws Failure
//  public IntVar elementAt(IntExp idx_exp) throws Failure // IntVar elementAt???
  {
    return new IntExpElementAt(this,idx_exp);
/*
    String name = "";
    if(constrainer().showInternalNames())
    {
      name = name() + "[" + idx_exp.name() + "]";
    }
    int domain = IntVar.DOMAIN_BIT_FAST;
    int trace = 0;//IntVar.TRACE_ALL;
    IntVar element_var = constrainer().addIntVarTrace(min(), max(), name, domain, trace);

    Constraint ce =  new ConstraintElement(this,idx_exp,element_var);
    ce.post();

    return element_var;
*/
  }

 /**
 * Returns idx'th element
 * @param idx element to return
 * @return idx'th element
 */
  public int elementAt(int idx)
  {
    return get(idx);
  }

  /**
   * @return Array of integers.
   */
  public int[] data()
  {
    return _data;
  }

  /**
   * Constructor from array.
   * @param c Current constrainer.
   * @param arr Array of integers.
   */
  public IntArray(Constrainer c, int[] arr)
  {
    this(c, arr.length);

    for(int i = 0; i < _data.length; ++i)
      _data[i] = arr[i];
  }

  /**
   * Constructor from vector.
   * @param c Current constrainer.
   * @param v Vector of Integers.
   */
  public IntArray(Constrainer c, Vector v)
  {
    this(c, v.size());

    for(int i = 0; i < _data.length; ++i)
      _data[i] = ((Integer) v.elementAt(i)).intValue();
  }

  /**
   * Constructor: known size, unknown elements values.
   * @param c Current constrainer.
   * @param size Size of array.
   */
  public IntArray(Constrainer c, int size)
  {
    super(c);
    _data = new int[size];
  }

  /**
   * Constructor: known size, unknown elements values.
   * @param c Current constrainer.
   * @param size Size of array.
   * @param name Symbolic name.
   */
  public IntArray(Constrainer c, int size, String name)
  {
    super(c,name);
    _data = new int[size];
  }

  /**
   * Constructor from 2 elements.
   */
  public IntArray(Constrainer c, int e0, int e1)
  {
    this(c, 2);

    _data[0] = e0;
    _data[1] = e1;

  }

  /**
   * Constructor from 3 elements.
   */
  public IntArray(Constrainer c, int e0, int e1, int e2)
  {
    this(c, 3);

    _data[0] = e0;
    _data[1] = e1;
    _data[2] = e2;

  }

  /**
   * Constructor from 4 elements.
   */
  public IntArray(Constrainer c, int e0, int e1, int e2, int e3)
  {
    this(c, 4);

    _data[0] = e0;
    _data[1] = e1;
    _data[2] = e2;
    _data[3] = e3;

  }

  /**
   * Constructor from 5 elements.
   */
  public IntArray(Constrainer c, int e0, int e1, int e2, int e3, int e4)
  {
    this(c, 5);

    _data[0] = e0;
    _data[1] = e1;
    _data[2] = e2;
    _data[3] = e3;
    _data[4] = e4;

  }

  /**
   * Constructor from 6 elements.
   */
  public IntArray(Constrainer c, int e0, int e1, int e2,
                                   int e3, int e4, int e5)
  {
    this(c, 6);

    _data[0] = e0;
    _data[1] = e1;
    _data[2] = e2;
    _data[3] = e3;
    _data[4] = e4;
    _data[5] = e5;

  }

  /**
   * Constructor from 7 elements.
   */
  public IntArray(Constrainer c, int e0, int e1, int e2,
                                   int e3, int e4, int e5,
                                   int e6)
  {
    this(c, 7);

    _data[0] = e0;
    _data[1] = e1;
    _data[2] = e2;
    _data[3] = e3;
    _data[4] = e4;
    _data[5] = e5;
    _data[6] = e6;

  }



  /**
   * Constructor from 8 elements.
   */
  public IntArray(Constrainer c, int e0, int e1, int e2,
                                   int e3, int e4, int e5,
                                   int e6, int e7)
  {
    this(c, 8);

    _data[0] = e0;
    _data[1] = e1;
    _data[2] = e2;
    _data[3] = e3;
    _data[4] = e4;
    _data[5] = e5;
    _data[6] = e6;
    _data[7] = e7;

  }

  /**
   * Constructor from 9 elements.
   */
  public IntArray(Constrainer c, int e0, int e1, int e2,
                                   int e3, int e4, int e5,
                                   int e6, int e7, int e8)
  {
    this(c, 9);

    _data[0] = e0;
    _data[1] = e1;
    _data[2] = e2;
    _data[3] = e3;
    _data[4] = e4;
    _data[5] = e5;
    _data[6] = e6;
    _data[7] = e7;
    _data[8] = e8;

  }

  /**
   * Returns size of the array
   * @return Size of the array.
   */
  public int size()
  {
    return _data.length;
  }

  /**
   * Returns arrays' minimal value.
   * @return Minimal value.
   */
  public int min()
  {
    int min = Integer.MAX_VALUE;
    for(int i = 0; i < _data.length; ++i)
    {
       int mini = _data[i];
       if (mini < min)
          min = mini;
    }
    return min;
  }

  /**
   * Returns arrays' maximal value.
   * @return Maximal value.
   */
  public int max()
  {
    int max = Integer.MIN_VALUE;
    for(int i = 0; i < _data.length; ++i)
    {
       int maxi = _data[i];
       if (maxi > max)
          max = maxi;
    }
    return max;
  }

  /**
   * Sets new value of element "idx"
   * @param value New value.
   * @param idx Element index.
   */
  public void set(int value, int idx)
  {
    _data[idx] = value;
  }

  /**
   * Return value of element with index "idx"
   * @param idx Element index.
   * @return Value of element with index "idx"
   */
  public int get(int idx)
  {
    return _data[idx];
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer();

    buf.append("[");

    for(int i= 0; i < _data.length; ++i)
    {
      if (i > 0)
        buf.append(" ");
      buf.append(_data[i]);
    }

    buf.append("]");

    return buf.toString();

  }

  /**
   * Sets new symbolic name.
   * @param name New name.
   */
  public void name(String name)
  {
    symbolicName(name);
  }
  /*
    extending to 5.1.0
    added by S. Vanskov
  */
  /**
   * The function returns subarray of the array consisting
   * of the given array elements which indeses more or
   * equal to <code> min_index </code> and more or equal to
   * <code> max_index </code>
   *
   * @param min_index index lower bound
   * @param max_index index upper bound
   *
   * @return subarray of array from <code> min_index </code>  to
   * <code> max_index </code>
   */
  IntArray subarray (int min_index, int max_index) {
    if (min_index > max_index) {
      return new IntArray (constrainer (), 0);
    }

    int []  sub_data = new int [max_index - min_index + 1];

    for (int i = min_index; i <= max_index; i++) {
      sub_data [i - min_index] = _data [i];
    }

    return new IntArray (constrainer (), sub_data);
  }
  /**
   * The function returns subarray of the array according to
   * the mask array.
   *
   * @param mask is the mask array
   *
   * @return the subarray according the mask array
   */
  IntArray subarrayByMask (boolean [] mask) {
    int a_size = Math.min (_data.length, mask.length);
    int b_size = 0;

    int i;
    for (i = 0; i < a_size; i++) {
      if (mask [i]) {
        b_size++;
      }
    }

    int [] sub_data = new int [b_size];
    int index = 0;

    for (i = 0; i < a_size; i++) {
      if (mask [i]) {
        sub_data [index] = _data [i];
      }
    }

    return new IntArray (constrainer (), sub_data);
  }
  /**
   * The function merges this array and the array provided as
   * parameter and returns the resulted array.
   *
   * @param array the array to be merged
   *
   * @return the merged array
   */
  IntArray merge (IntArray array) {
    int [] new_data = new int [this._data.length + array._data.length];
    int i;
    int index = 0;

    for (i = 0; i < _data.length; i++) {
      new_data [index] = _data [i];
      index ++;
    }

    for (i = 0; i < array._data.length; i++) {
      new_data [index] = array._data [i];
      index ++;
    }
    return new IntArray (constrainer (), new_data);
  }
  /* EO additions */
} // ~IntArray
