package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.IntArray;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;
import com.exigen.ie.tools.Reusable;
import com.exigen.ie.tools.ReusableFactory;
import com.exigen.ie.tools.ReusableImpl;


/**
 * An implementation of the expression: <code>IntArray[IntExp]</code>.
 */
public final class IntExpElementAt extends IntExpImpl
{
  private IntArray    _ary;
  private IntExp	    _indexExp;

  private IntVar	    _index;
//  private IntVar      _element;
  private IntExp      _element;
  private Mapping     _m;

  /**
   * A mapping for removing values from the index and element.
   */
  interface Mapping
  {
    public void removeFromIndex(int idx) throws Failure;
    public void removeFromIndex(int min, int max) throws Failure;
    public void setValueFromIndex(int idx) throws Failure;

    public void removeFromElement(int value) throws Failure;
    public void removeFromElement(int min, int max) throws Failure;
    public void setValueFromElement(int value) throws Failure;

  }

/*==============================================================================
  Functional iterators
  ============================================================================*/
  /**
   * Finds idx from the _index where _ary[idx] == value.
   */
  static class FindValueIterator extends ReusableImpl implements IntExp.IntDomainIterator
  {
    static ReusableFactory _factory = new ReusableFactory()
    {
        protected Reusable createNewElement()
        {
          return new FindValueIterator();
        }
    };

    static FindValueIterator getIterator(IntExp index, IntArray ary, int value)
    {
      FindValueIterator it = (FindValueIterator) _factory.getElement();
      it.index = index;
      it.ary = ary;
      it.value = value;
      it.foundIndex = -1;
      return it;
    }

    IntExp    index;
    IntArray  ary;
    int       value;
    int       foundIndex;

    public boolean doSomethingOrStop(int idx) throws Failure
    {
      if(ary.elementAt(idx) == value)
      {
        foundIndex = idx;
        return false;
      }

      return true;
    }

  } // ~FindValueIterator

  /**
   * Remove all indexes from the _index where min LE _ary[idx] LE max.
   */
  static class RemoveFromElementIterator extends ReusableImpl implements IntExp.IntDomainIterator
  {
    static ReusableFactory _factory = new ReusableFactory()
    {
        protected Reusable createNewElement()
        {
          return new RemoveFromElementIterator();
        }
    };

    static RemoveFromElementIterator getIterator(IntExp index, IntArray ary, int min, int max)
    {
      RemoveFromElementIterator it = (RemoveFromElementIterator) _factory.getElement();
      it.index = index;
      it.ary = ary;
      it.min = min;
      it.max = max;
      return it;
    }

    IntExp index;
    IntArray ary;
    int min, max;

    public boolean doSomethingOrStop(int idx) throws Failure
    {
      int elementValue = ary.elementAt(idx);
      if(min <= elementValue && elementValue <= max)
        index.removeValue(idx);

      return true;
    }

  } // ~RemoveFromElementIterator

  /**
   * Remove all indexes from the _index where _ary[idx] != value.
   */
  static class SetValueFromElementIterator extends ReusableImpl implements IntExp.IntDomainIterator
  {
    static ReusableFactory _factory = new ReusableFactory()
    {
        protected Reusable createNewElement()
        {
          return new SetValueFromElementIterator();
        }
    };

    static SetValueFromElementIterator getIterator(IntExp index, IntArray ary, int value)
    {
      SetValueFromElementIterator it = (SetValueFromElementIterator) _factory.getElement();
      it.index = index;
      it.ary = ary;
      it.value = value;
      return it;
    }

    IntExp index;
    IntArray ary;
    int value;

    public boolean doSomethingOrStop(int idx) throws Failure
    {
      if(ary.elementAt(idx) != value)
        index.removeValue(idx);

      return true;
    }

  } // ~SetValueFromElementIterator
/*==============================================================================
  EOF Functional iterators
  ============================================================================*/

  static int findIndex(IntExp index, IntArray ary, int value)
  {
    FindValueIterator it = FindValueIterator.getIterator(index, ary, value);
    try{ index.iterateDomain(it); } catch(Failure f){}

    int foundIndex = it.foundIndex;

    it.free();

    return foundIndex;
  }

  static boolean indexHasValue(IntExp index, IntArray ary, int value)
  {
    return findIndex(index,ary,value) >= 0;
  }

  /**
   * An Mapping that scan index for a value.
   */
  static final class SimpleMapping implements Mapping
  {
    IntExp   _index;
    IntExp   _element;
    IntArray _ary;

    public SimpleMapping(IntExp index, IntExp element, IntArray ary)
    {
      _index = index;
      _element = element;
      _ary = ary;
    }

    public void removeFromIndex(int idx) throws Failure
    {
      int value = _ary.elementAt(idx);
      // Remove value from element if there are no more value in _ary[_index].
      if(!indexHasValue(_index,_ary,value))
        _element.removeValue(value);
    }

    public void removeFromIndex(int min, int max) throws Failure
    {
      for (int i = min; i <= max; ++i)
      {
        removeFromIndex(i);
      }
    }

    public void setValueFromIndex(int idx) throws Failure
    {
      int value = _ary.elementAt(idx);
      _element.setValue(value);
    }

    public void removeFromElement(int value) throws Failure
    {
      RemoveFromElementIterator it =
        RemoveFromElementIterator.getIterator(_index, _ary, value, value);
      _index.iterateDomain(it);
      it.free();
    }

    public void removeFromElement(int min, int max) throws Failure
    {
      RemoveFromElementIterator it =
        RemoveFromElementIterator.getIterator(_index, _ary, min, max);
      _index.iterateDomain(it);
      it.free();
    }
    public void setValueFromElement(int value) throws Failure
    {
      SetValueFromElementIterator it =
        SetValueFromElementIterator.getIterator(_index, _ary, value);
      _index.iterateDomain(it);
      it.free();
    }

  } // ~SimpleMapping

  /**
   * An Mapping that is one to one.
   */
  static final class OneToOneMapping implements Mapping
  {
    IntExp   _index;
    IntExp   _element;
    IntArray _ary;
    int      _element2index[];
    int      _elementMin;

    public OneToOneMapping(IntExp index, IntExp element, IntArray ary)
    {
      _index = index;
      _element = element;
      _ary = ary;

      createElement2Index();
    }

    void createElement2Index()
    {
      IntExp.IntDomainIterator it = new IntExp.IntDomainIterator()
      {
        public boolean doSomethingOrStop(int idx) throws Failure
        {
          int elementValue = _ary.elementAt(idx);
          _element2index[elementValue - _elementMin] = idx;
          return true;
        }
      };

      _elementMin = _element.min();
      int length = _element.max() - _elementMin + 1;
      _element2index = new int[length];

      for (int i=0; i<length; ++i)
      {
        _element2index[i] = -1;
      }

      try{_index.iterateDomain(it);} catch(Failure f) {}
    }

    public void removeFromIndex(int idx) throws Failure
    {
      int value = _ary.elementAt(idx);
      _element.removeValue(value);
    }

    public void removeFromIndex(int min, int max) throws Failure
    {
      for (int i = min; i <= max; ++i)
      {
        removeFromIndex(i);
      }
    }

    public void setValueFromIndex(int idx) throws Failure
    {
      int value = _ary.elementAt(idx);
      _element.setValue(value);
    }

    public void removeFromElement(int value) throws Failure
    {
      int idx = _element2index[value - _elementMin];
      _index.removeValue(idx);
    }

    public void removeFromElement(int min, int max) throws Failure
    {
      for (int i = min; i <= max; ++i)
      {
        int idx = _element2index[i - _elementMin];
        if(idx >= 0)
          _index.removeValue(idx);
      }
    }
    public void setValueFromElement(int value) throws Failure
    {
      int idx = _element2index[value - _elementMin];
      _index.setValue(idx);
    }

  } // ~OneToOneMapping

  class IndexObserver extends Observer
  {
    public void update(Subject exp, EventOfInterest event)
    throws Failure
    {
      IntEvent e = (IntEvent) event;
      int type = e.type();

      if ((type &  IntEvent.VALUE) != 0)
      {
        _m.setValueFromIndex(e.min());
      }
      else
      {
        if ((type &  IntEvent.MIN) != 0)
        {
          _m.removeFromIndex(e.oldmin(), e.min()-1);
        }
        if ((type &  IntEvent.MAX) != 0)
        {
          _m.removeFromIndex(e.max()+1, e.oldmax());
        }
        if ((type &  IntEvent.REMOVE) != 0)
        {
          int nRemoves = e.numberOfRemoves();
          int min = e.min();
          int max = e.max();
          for (int i = 0; i < nRemoves; ++i)
          {
            int removedValue = e.removed(i);
            if(min <= removedValue && removedValue <= max)
              _m.removeFromIndex(removedValue);
          }
        }
      }

    } // ~update()

    public int subscriberMask()
    {
      return IntEvent.ALL;
    }

    public Object master()
    {
      return IntExpElementAt.this;
    }

  } //~ IndexObserver

  class ElementObserver extends Observer
  {
    public void update(Subject exp, EventOfInterest event)
    throws Failure
    {
      IntEvent e = (IntEvent) event;
      int type = e.type();

      if ((type &  IntEvent.VALUE) != 0)
      {
        _m.setValueFromElement(e.min());
      }
      else
      {
        if ((type &  IntEvent.MIN) != 0)
        {
          _m.removeFromElement(e.oldmin(), e.min()-1);
        }
        if ((type &  IntEvent.MAX) != 0)
        {
          _m.removeFromElement(e.max()+1, e.oldmax());
        }
        if ((type &  IntEvent.REMOVE) != 0)
        {
          int nRemoves = e.numberOfRemoves();
          int min = e.min();
          int max = e.max();
          for (int i = 0; i < nRemoves; ++i)
          {
            int removedValue = e.removed(i);
            if(min <= removedValue && removedValue <= max)
              _m.removeFromElement(removedValue);
          }
        }
      }

    } // ~update()

    public int subscriberMask()
    {
      return IntEvent.ALL;
    }

    public Object master()
    {
      return IntExpElementAt.this;
    }

  } //~ ElementObserver


  public IntExpElementAt(IntArray ary, IntExp indexExp)
  {
    super(ary.constrainer());
    _ary = ary;
    _indexExp = indexExp;

    if(constrainer().showInternalNames())
    {
      _name = "" + _ary.name() + "[" + _indexExp.name() + "]";
    }

    try
    {
      createIndex();
      createElement();
      // Propagate events BEFORE attaching the observers.
      constrainer().propagate();
    }
    catch(Exception e)
    {
      throw new RuntimeException( "Invalid elementAt-expression: "+ary+"["+indexExp+"]. "
                                  + e.getClass().getName() + ": " + e.getMessage() );
    }

    createMapping();

    _index.attachObserver(new IndexObserver());
    _element.attachObserver(new ElementObserver());
  }

  /**
   * Returns element-domain as an array of different sorted values.
   */
  int[] elementDomain()
  {
    int[] values = new int[_index.size()];
    int dst = 0;
    for(int src=0; src < _ary.size(); ++src)
    {
      if(_index.contains(src))
      {
        values[dst++] = _ary.get(src);
      }
    }
    return IntCalc.differentSortedValues(values);
  }

  void createElement() throws Failure
  {
    int[] values = elementDomain();

    int min = values[0];
    int max = values[values.length-1];
    int size = values.length;
    int nHoles = (max-min+1) - size;

    if(nHoles < 10)
    {
      createElement1(values);
    }
    else
    {
      createElement2(values);
    }
  }

  void createElement1(int[] values) throws Failure
  {
//    int trace = IntVar.TRACE_ALL;
    int trace = 0;
    int min = values[0];
    int max = values[values.length-1];

    String name = "";
    if(constrainer().showInternalNames())
    {
      name = "element_"+_ary.name()+"["+_indexExp.name()+"]";
    }

    _element = constrainer().addIntVarTraceInternal(min, max,
                   name, IntVar.DOMAIN_BIT_FAST, trace);

    // Remove NOT-index values from _element
    for(int i = 0; i+1 < values.length; ++i)
    {
      for(int value = values[i]+1; value < values[i+1]-1; ++value)
      {
        _element.removeValue(value);
      }
    }

  }

  void createElement2(int[] values) throws Failure
  {
    String name = "";
    if(constrainer().showInternalNames())
    {
      name = "element_"+_ary.name()+"["+_indexExp.name()+"]";
    }

    _element = new IntExpEnum(constrainer(),values,name);
  }

  void createIndex() throws Failure
  {
    boolean effectiveIndexExp = (_indexExp instanceof IntVar) &&
                              ((IntVar)_indexExp).domainType() != IntVar.DOMAIN_PLAIN;
    int max = _ary.size()-1;
    if(effectiveIndexExp)
    {
      // Use _indexExp as _index.
      _index = (IntVar)_indexExp;
      _index.setMin(0);
      _index.setMax(max);
    }
    else
    {
      // Create _index as a new effective index and post constraint (_index == _indexExp).
//      int trace = IntVar.TRACE_ALL;
      int trace = 0;
      String name = "";
      if(constrainer().showInternalNames())
      {
        name = "index_"+_ary.name()+"["+_indexExp.name()+"]";
      }
      _index = constrainer().addIntVarTraceInternal(0, max,
                name, IntVar.DOMAIN_BIT_FAST, trace);

      // Sync _index and _indexExp
      for (int i = 0; i <= max; ++i)
      {
        if(!_indexExp.contains(i))
          _index.removeValue(i);
      }

      _index.equals(_indexExp).post();
    }
  }

  void createMapping()
  {
    int nHoles = (_element.max() - _element.min() +1) - _element.size();
    if(_index.size() == _element.size() && nHoles < 2000)
    {
      _m = new OneToOneMapping(_index, _element, _ary);
    }
    else
    {
      _m = new SimpleMapping(_index, _element, _ary);
    }
  }

  public void attachObserver(Observer observer)
  {
    super.attachObserver(observer);
    _element.attachObserver(observer);
  }

  public void reattachObserver(Observer observer)
  {
    super.reattachObserver(observer);
    _element.reattachObserver(observer);
  }

  public void detachObserver(Observer observer)
  {
    super.detachObserver(observer);
    _element.detachObserver(observer);
  }

  public boolean contains(int value)
  {
    return _element.contains(value);
  }

  public int max()
  {
    return _element.max();
  }

  public int min()
  {
    return _element.min();
  }

  public int size()
  {
    return _element.size();
  }

  public void setMax(int max) throws Failure
  {
    _element.setMax(max);
  }

  public void setMin(int min) throws Failure
  {
    _element.setMin(min);
  }

  public void setValue(int value) throws Failure
  {
    _element.setValue(value);
  }

  public void removeValue(int value) throws Failure
  {
    _element.removeValue(value);
  }

  public String domainToString()
  {
    return _element.domainToString();
  }

} // ~IntExpElementAt
