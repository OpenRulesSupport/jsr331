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

package com.exigen.ie.tools;

// "implements serializable"  was added by Eugeny Tseitlin 18.06.2003
public final class FastVectorDouble implements Cloneable, java.io.Serializable
{

  static final int DEFAULT_CAPACITY = 10;

  static int max_size = 0;

  double[] m_data;
  int m_size;

  public FastVectorDouble()
  {
    this(DEFAULT_CAPACITY);
  }


  public FastVectorDouble(int capacity)
  {
    m_size = 0;
    if (capacity == 0)
      capacity = DEFAULT_CAPACITY;
    m_data = new double[capacity];
  }

  public FastVectorDouble(double[] c)
  {
    this(c, 0, c.length-1);
  }

  public FastVectorDouble(double[] c, int fromIndex, int toIndex)
  {
    this(Math.max(0, toIndex-fromIndex+1));
    if(m_size>0)
      System.arraycopy(m_data,fromIndex, m_data,0, m_size);
  }

  public final boolean isEmpty()
  {
    return m_size == 0;
  }

  public int size()
  {
    return m_size;
  }

  public double elementAt(int i)
  {
    return m_data[i];
  }

  public final void add(double val)
  {
    if (m_size == m_data.length)
      grow();

    m_data[m_size++] = val;
  }

  public final void addElement(double val)
  {
    if (m_size == m_data.length)
      grow();

    m_data[m_size++] = val;
  }

  public Object clone() {
    try
    {
        FastVectorDouble v = (FastVectorDouble)super.clone();
        v.m_data = (double[])m_data.clone();
//        v.m_data = new double[m_data.length];
//        System.arraycopy(m_data, 0, v.m_data, 0, m_data.length);
        return v;
    } catch (CloneNotSupportedException e) {
        // this shouldn't happen, since we are Cloneable
        throw new InternalError();
    }
  }



  public void insertElementAt(double val, int index) {
    if (m_size == m_data.length)
      grow();

    System.arraycopy(m_data, index, m_data, index + 1, m_size - index);
    m_data[index] = val;
    m_size++;
  }



  public final double removeLast()
  {
    return m_data[--m_size];
  }

  public final double peek()
  {
    return m_data[m_size - 1];
  }

  public void cutSize(int new_size)
  {
    m_size = new_size;
  }


  public void clear()
  {
    m_size = 0;
//    m_data = new Object[m_data.length];
  }

  public double firstElement()
  {
    return m_data[0];
  }

  public double lastElement()
  {
    return m_data[m_size - 1];
  }

  public void removeElementAt(int index)
  {
    int j = m_size - index - 1;
    if (j > 0) {
        System.arraycopy(m_data, index + 1, m_data, index, j);
    }
    m_size--;
//  	m_data[m_size] = null; /* to let gc do its work */
  }



  void grow()
  {
    double[] old = m_data;

    m_data = new double[m_data.length * 2];
    System.arraycopy(old, 0, m_data, 0, m_size);
  }

  public String toString()
  {
  StringBuffer buf = new StringBuffer();
  buf.append("[");
  int maxIndex = m_size - 1;
  for (int i = 0; i <= maxIndex; i++) {
      buf.append(String.valueOf(m_data[i]));
      if (i < maxIndex)
    buf.append(", ");
  }
  buf.append("]");
  return buf.toString();
  }

}
