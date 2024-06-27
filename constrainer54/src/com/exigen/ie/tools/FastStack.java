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
public final class FastStack implements Cloneable, java.io.Serializable
{

  static final int DEFAULT_CAPACITY = 10;

  Object[] m_data;
  int m_size;

  public FastStack()
  {
    this(DEFAULT_CAPACITY);
  }


  public FastStack(int capacity)
  {
    m_size = 0;
    if (capacity == 0)
      capacity = DEFAULT_CAPACITY;
    m_data = new Object[capacity];
  }

  public int size()
  {
    return m_size;
  }

  public void setSize(int newSize)
  {
    while(m_size > newSize)
    {
      m_data[--m_size] = null;
    }
  }


  public final boolean empty()
  {
    return m_size == 0;
  }

  public final void push(Object obj)
  {
    if (m_size == m_data.length)
      grow();

    m_data[m_size++] = obj;
  }

  public final Object pop()
  {
      Object o = m_data[--m_size];
      m_data[m_size] = null;
      return o;
  }

  public final Object peek()
  {
    return m_data[m_size - 1];
  }

  public void clear()
  {
    m_size = 0;
    m_data = new Object[m_data.length];
  }

  public Object clone() {
    try
    {
        FastStack v = (FastStack)super.clone();
        v.m_data = (Object[])m_data.clone();
//        v.m_data = new Object[m_data.length];
//        System.arraycopy(m_data, 0, v.m_data, 0, m_data.length);
        return v;
    } catch (CloneNotSupportedException e) {
        // this shouldn't happen, since we are Cloneable
        throw new InternalError();
    }
  }



  void grow()
  {
    Object[] old = m_data;

    m_data = new Object[m_data.length * 2];
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
