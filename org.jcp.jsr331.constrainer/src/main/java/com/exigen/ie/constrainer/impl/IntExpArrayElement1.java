package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;
import com.exigen.ie.tools.Reusable;
import com.exigen.ie.tools.ReusableFactory;
import com.exigen.ie.tools.ReusableImpl;

/*
 * == Changed
 * March-2010: Error was found and fixed by Roman - see MaxTest.java
 * Test 3 triggered an ArrayIndexOutOfBoundsException
 * at IntExpArrayElement1:935, resulting in a
 * NullPointerException at Var:120.
 * "j<=max" replaced by "j<max"
 */

public class IntExpArrayElement1 extends IntExpImpl
{
  private final IntExpArray _ary;
  private AryElementsObserver[] _aryElementsObservers = null;
  private final IntExp _indexExp;
  private IntVar _index;
  private IntExp      _element;
  private Mapping     _m;

  /**
   * A mapping for removing values from the index and element.
   */
  interface Mapping
  {
    public void arrayElementRemove(int removedValue, int idx) throws Failure;
    public void arrayElementValue(int value, int idx) throws Failure;
    public void arrayElementMax(int oldmax, int max, int idx) throws Failure;
    public void arrayElementMin(int oldmin, int min, int idx) throws Failure;

    public void indexRemove(int removedValue) throws Failure;
    public void indexMax(int oldmax, int max) throws Failure;
    public void indexMin(int oldmin, int min) throws Failure;
    public void indexValue(int value) throws Failure;

    public void resultMin(int min) throws Failure;
    public void resultMax(int max) throws Failure;
    public void resultRemove(int removedValue) throws Failure;
    public void resultValue(int value) throws Failure;
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

    static FindValueIterator getIterator(IntExp index, IntExpArray ary, int value)
    {
      FindValueIterator it = (FindValueIterator) _factory.getElement();
      it.index = index;
      it.ary = ary;
      it.value = value;
      it.foundIndex = -1;
      return it;
    }

    IntExp    index;
    IntExpArray  ary;
    int       value;
    int       foundIndex;

    public boolean doSomethingOrStop(int idx) throws Failure
    {
      if(ary.elementAt(idx).contains(value))
      {
        foundIndex = idx;
        return false;
      }

      return true;
    }

  } // ~FindValueIterator

/**
 * make an exctract from IntExpArray based  on _index
 */
static class CopyElementsIterator extends ReusableImpl implements IntExp.IntDomainIterator{
  static ReusableFactory _factory = new ReusableFactory(){
    protected Reusable createNewElement(){
      return new CopyElementsIterator();
    }
  };

  static CopyElementsIterator getIterator(IntExp index, IntExpArray ary)
  {
    CopyElementsIterator iter = new CopyElementsIterator();
    iter._index = index;
    iter.source = ary;
    iter.extract = new IntExpArray(index.constrainer(), index.size());
    return iter;
  }

  IntExp _index;
  IntExpArray source;
  IntExpArray extract;
  int cnt = 0;

  public boolean doSomethingOrStop (int idx) throws Failure{
    extract.set(source.get(idx) ,cnt++);
    return true;
  }
}

/**
 * Remove all indexes from the _index where min GT max(_ary[idx]).
 */
static class RemoveFromElementMinIterator extends ReusableImpl implements IntExp.IntDomainIterator
{
  static ReusableFactory _factory = new ReusableFactory()
  {
      protected Reusable createNewElement()
      {
        return new RemoveFromElementMinIterator();
      }
  };

  static RemoveFromElementMinIterator getIterator(IntExp index, IntExpArray ary, int min)
  {
    RemoveFromElementMinIterator it = (RemoveFromElementMinIterator) _factory.getElement();
    it.index = index;
    it.ary = ary;
    it.min = min;
    return it;
  }

  IntExp index;
  IntExpArray ary;
  int min;

  public boolean doSomethingOrStop(int idx) throws Failure
  {
    int arrayElementMax = ary.elementAt(idx).max();
    if(min > arrayElementMax)
      index.removeValue(idx);
    return true;
  }
} // ~RemoveFromElementMinIterator

/**
 * Remove all indexes from the _index where max LT min(_ary[idx]).
 */
static class RemoveFromElementMaxIterator extends ReusableImpl implements IntExp.IntDomainIterator
{
  static ReusableFactory _factory = new ReusableFactory()
  {
      protected Reusable createNewElement()
      {
        return new RemoveFromElementMaxIterator();
      }
  };

  static RemoveFromElementMaxIterator getIterator(IntExp index, IntExpArray ary, int max)
  {
    RemoveFromElementMaxIterator it = (RemoveFromElementMaxIterator) _factory.getElement();
    it.index = index;
    it.ary = ary;
    it.max = max;
    return it;
  }

  IntExp index;
  IntExpArray ary;
  int max;

  public boolean doSomethingOrStop(int idx) throws Failure
  {
    int arrayElementMin = ary.elementAt(idx).min();
    if(max < arrayElementMin)
      index.removeValue(idx);
    return true;
  }
} // ~RemoveFromElementMaxIterator


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

  static SetValueFromElementIterator getIterator(IntExp index, IntExpArray ary, int value)
  {
    SetValueFromElementIterator it = (SetValueFromElementIterator) _factory.getElement();
    it.index = index;
    it.ary = ary;
    it.value = value;
    return it;
  }

  IntExp index;
  IntExpArray ary;
  int value;

  public boolean doSomethingOrStop(int idx) throws Failure
  {
    if(!ary.elementAt(idx).contains(value))
      index.removeValue(idx);
    return true;
  }

} // ~SetValueFromElementIterator

/**
 * finds  min(_ary[idx]) and max(_ary[idx]).
 */
static class FindMinMaxIterator extends ReusableImpl implements IntExp.IntDomainIterator
{
  static ReusableFactory _factory = new ReusableFactory()
  {
      protected Reusable createNewElement()
      {
        return new FindMinMaxIterator();
      }
  };

  IntExpArray ary;
  int min = Integer.MAX_VALUE;
  int max = -Integer.MAX_VALUE;
  IntExp index;

  static FindMinMaxIterator getIterator(IntExp index, IntExpArray ary)
  {
    FindMinMaxIterator it = (FindMinMaxIterator) _factory.getElement();
    it.index = index;
    it.ary = ary;
    return it;
  }

  public boolean doSomethingOrStop(int idx) throws Failure
  {
    if (ary.elementAt(idx).max() > max)
      max = ary.elementAt(idx).max();
    if (ary.elementAt(idx).min() < min)
      min = ary.elementAt(idx).min();
    return true;
  }
}
//~FindMinMaxIterator

/*==============================================================================
EOF Functional iterators
============================================================================*/

static int findIndex(IntExp index, IntExpArray ary, int value)
{
  FindValueIterator it = FindValueIterator.getIterator(index, ary, value);
  try{ index.iterateDomain(it); } catch(Failure f){}

  int foundIndex = it.foundIndex;

  it.free();

  return foundIndex;
}

static boolean indexHasValue(IntExp index, IntExpArray ary, int value)
{
  return findIndex(index,ary,value) >= 0;
}

static IntExpArray makeExtraction(IntExp index, IntExpArray ary){
  CopyElementsIterator iter = CopyElementsIterator.getIterator(index, ary);
  try{index.iterateDomain(iter);} catch(Failure f){}
  //iter.free();
  return iter.extract;
}
/**
 * An Mapping that scan index for a value.
 */
static class SimpleMapping implements Mapping
{
  IntExp   _index;
  IntExp   _element;
  IntExpArray _ary;
  AryElementsObserver[] _observers;

  public SimpleMapping(IntExp index, IntExp element, IntExpArray ary, AryElementsObserver[] observers)
  {
    _index = index;
    _element = element;
    _ary = ary;
    _observers = observers;
  }

  public void arrayElementValue(int value, int idx) throws Failure{
    if ( (value > _element.max()) || (value < _element.min()) )
        _index.removeValue(idx);
  }

  public void arrayElementMax(int oldmax, int max, int idx) throws Failure{
    if (_element.min() > max)
      _index.removeValue(idx);
  }

  public void arrayElementMin(int oldmin, int min, int idx) throws Failure{
    if (_element.max() < min)
      _index.removeValue(idx);
  }

  public void arrayElementRemove(int value, int idx) throws Failure{;}

  public void indexValue(int value) throws Failure
  {
    for (int i=0; i<_ary.size(); i++){
      if ( (_observers[i] != null) && (i!=value))
        _ary.get(value).detachObserver(_observers[i]);
    }
    _element.setMin(_ary.get(value).min());
    _element.setMax(_ary.get(value).max());
  }

  public void indexMax(int oldmax, int max) throws Failure{
    for (int i=oldmax; i>max; i--){
      if ( (_observers[i] != null) )
        _ary.get(i).detachObserver(_observers[i]);
    }
    updateResultDomainFromIndex();
  }

  public void indexMin(int oldmin, int min) throws Failure{
    for (int i=oldmin; i<min; i++){
      if ( (_observers[i] != null) )
        _ary.get(i).detachObserver(_observers[i]);
    }
    updateResultDomainFromIndex();
  }

  public void indexRemove(int value) throws Failure{
    _ary.get(value).detachObserver(_observers[value]);
  }

  public void updateResultDomainFromIndex() throws Failure
  {
    FindMinMaxIterator it = FindMinMaxIterator.getIterator(_index, _ary);
    _index.iterateDomain(it);
    _element.setMin(it.min);
    _element.setMax(it.max);
    it.free();
  }

  public void resultMin(int min) throws Failure
  {
    RemoveFromElementMinIterator it =
      RemoveFromElementMinIterator.getIterator(_index, _ary, min);
    _index.iterateDomain(it);
    it.free();
  }

  public void resultMax(int max) throws Failure
  {
    RemoveFromElementMaxIterator it =
      RemoveFromElementMaxIterator.getIterator(_index, _ary, max);
    _index.iterateDomain(it);
    it.free();
  }

  public void resultValue(int value) throws Failure
  {
    SetValueFromElementIterator it =
      SetValueFromElementIterator.getIterator(_index, _ary, value);
    _index.iterateDomain(it);
    it.free();
  }

  public void resultRemove(int value) throws Failure{;}

} // ~SimpleMapping

private static class ValueCounterIterator implements IntExp.IntDomainIterator{
  IntExpArray _ary;
  int _val;
  int cnt = 0;

  public ValueCounterIterator(IntExpArray array, int value){
    _ary = array;
    _val = value;
  }

  public boolean doSomethingOrStop(int idx) throws Failure{
    if (_ary.get(idx).contains(_val))
      cnt++;
    return true;
  }
}


static class AdvancedMapping implements Mapping{
  java.util.HashMap valueToArrayIdx = new java.util.HashMap();
  IntExp _index;
  IntExp _element;
  IntExpArray _ary;
  IntExpArray _valuesUsed; //How many times a particular value could be encountered within _ary[_index]
  //IntExp _indexMin;       //index corresponding to the element of _ary with min min()
  //IntExp _indexMax;
  IntExpArray copyOfAry;
  IntExp _copyOfIndex;
  AryElementsObserver[] _observers;

  public AdvancedMapping(IntExp index, IntExp element, IntExpArray ary, AryElementsObserver[] observers){
    _observers = observers;
    _index = index;
    _element = element;
    _ary = ary;

    createCountersArray();
    _copyOfIndex = createCopyOfIntVar((IntVar)_index);
    copyOfAry = createCopyOfIntExpArray(_ary);
   // index.constrainer().trace(_valuesUsed);
  }

  void updateResultDomainFromIndex() throws Failure
  {
    FindMinMaxIterator it = FindMinMaxIterator.getIterator(_index, _ary);
    _index.iterateDomain(it);
    _element.setMin(it.min);
    _element.setMax(it.max);
    it.free();
  }

  void createCountersArray(){
    int cnt = 0;
    int[] usage = new int[_element.size()];
    for (int i=_element.min(); i<=_element.max(); i++){
      if (_element.contains(i)){
        ValueCounterIterator iter = new ValueCounterIterator(_ary, i);
        try{_index.iterateDomain(iter);}catch(Failure f){};
        valueToArrayIdx.put(new Integer(i), new Integer(cnt));
        usage[cnt] = iter.cnt;
        cnt++;
      }
    }

    _valuesUsed = new IntExpArray(_index.constrainer(), cnt);
    for (int i=0; i<cnt; i++){
      _valuesUsed.set(_index.constrainer().addIntVar(0, usage[i]), i);
    }
  }

  static IntVar createCopyOfIntVar(IntVar var){
    int type = var.domainType();
    boolean remove = false;
    switch(type){
      case IntVar.DOMAIN_BIT_FAST:
      case IntVar.DOMAIN_BIT_SMALL:
        remove = true;
      case IntVar.DOMAIN_PLAIN :
      default :
        break;
    }
    IntVar copy = var.constrainer().addIntVar(var.min(), var.max(), type);
    if (remove){
      for (int i=var.min(); i<= var.max(); i++){
        if (! var.contains(i))
          try{copy.removeValue(i);}catch(Failure f){}
      }
    }
    return copy;
  }

  static IntExpArray createCopyOfIntExpArray(IntExpArray array){
    IntExpArray arrCopy = new IntExpArray(array.constrainer(), array.size());
    for (int i=0; i<array.size(); i++){
      IntVar varCopy;
      IntExp exp = array.get(i);
      if (exp instanceof IntVar)
        varCopy = createCopyOfIntVar((IntVar)exp);
      else
        varCopy = array.constrainer().addIntVar(exp.min(), exp.max(), IntVar.DOMAIN_PLAIN);
      arrCopy.set(varCopy, i);
    }
    return arrCopy;
  }

  void decreaseUsageCounter(int val) throws Failure{
    int idx = ((Integer)valueToArrayIdx.get(new Integer(val))).intValue();
    int oldMax = _valuesUsed.get(idx).max();
    if (oldMax == 1){
      _element.removeValue(val);
      _valuesUsed.get(idx).setMax(0);
    }
    else
      _valuesUsed.get(idx).setMax(oldMax-1);
  }

  void decreaseUsageCounter(int start, int end, int idx) throws Failure{
    IntVar var = (IntVar)copyOfAry.get(idx);
    for (int i=start; i<=end; i++){
      if (var.contains(i))
        decreaseUsageCounter(i);
    }
  }

  public void arrayElementMin(int oldmin, int min, int idx) throws Failure{
    if (min > _element.max())
      _index.removeValue(idx);
    else
      decreaseUsageCounter(oldmin, min-1, idx);
    IntVar varCopy = (IntVar)copyOfAry.get(idx);
    varCopy.setMin(min);
  }

  public void arrayElementMax(int oldmax, int max, int idx) throws Failure{
    if (max < _element.min())
      _index.removeValue(idx);
    else
      decreaseUsageCounter(max+1, oldmax, idx);
    IntVar varCopy = (IntVar)copyOfAry.get(idx);
    varCopy.setMax(max);
  }

  public void arrayElementRemove(int removedValue, int idx) throws Failure{
    decreaseUsageCounter(removedValue);
    IntVar varCopy = (IntVar)copyOfAry.get(idx);
    varCopy.removeValue(removedValue);
  }

  public void arrayElementValue(int value, int idx) throws Failure{
    IntVar varCopy = (IntVar)copyOfAry.get(idx);
    if ( (value < _element.min()) || (value > _element.max()) )
      _index.removeValue(idx);
    else{
      for (int i=varCopy.min(); i < value; i++){
        if (varCopy.contains(i))
          decreaseUsageCounter(i);
      }
      for (int i=varCopy.max(); i > value; i--){
        if (varCopy.contains(i))
          decreaseUsageCounter(i);
      }
    }
    varCopy.setValue(value);
  }

  void synchronizedUsageCounterWithIndex(int valueBeingSet){

  }

  void detachObservers(int start, int end){
    for (int i=start; i<=end; i++){
      if (_copyOfIndex.contains(i))
        _ary.get(i).detachObserver(_observers[i]);
    }
  }
/**/
  public void indexValue(int value) throws Failure{
    /*IntVar exp = (IntVar)copyOfAry.get(value);
    java.util.Iterator iter = valueToArrayIdx.keySet().iterator();
    while(iter.hasNext()){
      Integer tmp = (Integer)iter.next();
      int curValue = tmp.intValue();
      Integer idx = (Integer)valueToArrayIdx.get(tmp);
      if (_ary.get(value).contains(curValue)){
        _valuesUsed.get(idx.intValue()).setMax(1);
      }else{
        _valuesUsed.get(idx.intValue()).setMax(0);
      }
    }*/

    _element.setMin(_ary.get(value).min());
    _element.setMax(_ary.get(value).max());

    for (int i=0; i<_ary.size(); i++){
      if (_copyOfIndex.contains(i) && (i!=value))
        _ary.get(i).detachObserver(_observers[i]);
    }

    _copyOfIndex.setValue(value);
  }

  public void indexMax(int oldmax, int max) throws Failure{
    FindMinMaxIterator iter = FindMinMaxIterator.getIterator(_index, _ary);
    _index.iterateDomain(iter);
    if ( (iter.max < _element.max()) && (iter.min > _element.min()) ){
      _element.setMax(iter.max);
      _element.setMin(iter.min);
    }
    else{
      for (int i=oldmax; i>max; i--){
        if (_copyOfIndex.contains(i))
          decreaseUsageCounter(copyOfAry.get(i).min(), copyOfAry.get(i).max(), i);
      }
    }

    detachObservers(max+1, oldmax);
    _copyOfIndex.setMax(max);
  }/**/

  public void indexMin(int oldmin, int min) throws Failure{
    FindMinMaxIterator iter = FindMinMaxIterator.getIterator(_index, _ary);
    _index.iterateDomain(iter);
    if ( (iter.max < _element.max()) || (iter.min > _element.min()) ){
      _element.setMax(iter.max);
      _element.setMin(iter.min);
    }
    else{
      for (int i=oldmin; i<min; i++){
        if (_copyOfIndex.contains(i))
          decreaseUsageCounter(copyOfAry.get(i).min(), copyOfAry.get(i).max(), i);
      }
    }
    updateResultDomainFromIndex();
    detachObservers(oldmin, min-1);
    _copyOfIndex.setMin(min);
  }/**/

  public void indexRemove(int removedValue) throws Failure{
     if (_copyOfIndex.contains(removedValue))
       decreaseUsageCounter(copyOfAry.get(removedValue).min(),
                            copyOfAry.get(removedValue).max(),
                            removedValue);
     _ary.get(removedValue).detachObserver(_observers[removedValue]);
     _copyOfIndex.removeValue(removedValue);
  }/**/
  public void resultMin(int min) throws Failure
  {
    RemoveFromElementMinIterator it =
      RemoveFromElementMinIterator.getIterator(_index, _ary, min);
    _index.iterateDomain(it);
    it.free();
  }

  public void resultMax(int max) throws Failure
  {
    RemoveFromElementMaxIterator it =
      RemoveFromElementMaxIterator.getIterator(_index, _ary, max);
    _index.iterateDomain(it);
    it.free();
  }

  public void resultValue(int value) throws Failure
  {
    SetValueFromElementIterator it =
      SetValueFromElementIterator.getIterator(_index, _ary, value);
    _index.iterateDomain(it);
    it.free();
  }



  public void resultRemove(int value) throws Failure{;}

}//~AdvancedMapping

static class AdvancedMapping2 extends AdvancedMapping{
  public AdvancedMapping2(IntExp index,
                          IntExp element,
                          IntExpArray ary,
                          AryElementsObserver[] observers)
  {
    super(index, element, ary, observers);
  }

  /*public void indexValue(int value) throws Failure{
    for (int i=0; i<this._ary.size(); i++){
      if ( (_observers[i] != null) && (i!=value))
        this._ary.get(value).detachObserver(_observers[i]);
    }
    this._element.setMin(this._ary.get(value).min());
    this._element.setMax(this._ary.get(value).max());
  }*/

  public void indexMax(int oldmax, int max) throws Failure{
   updateResultDomainFromIndex();
   detachObservers(max+1, oldmax);
   _copyOfIndex.setMax(max);
  }

  public void indexMin(int oldmin, int min) throws Failure{
    updateResultDomainFromIndex();
    detachObservers(oldmin, min-1);
    _copyOfIndex.setMin(min);
  }

  public void indexRemove(int removedValue) throws Failure{
    updateResultDomainFromIndex();
    detachObservers(removedValue, removedValue);
    _copyOfIndex.removeValue(removedValue);
  }
} //~AdvancedMapping2

class IndexObserver extends Observer
{
  public void update(Subject exp, EventOfInterest event)
  throws Failure
  {
    IntEvent e = (IntEvent) event;
    int type = e.type();

    if ((type &  IntEvent.VALUE) != 0)
    {
      _m.indexValue(e.min());
    }
    else
    {
      if((type & IntEvent.MIN) != 0){
        _m.indexMin(e.oldmin(), e.min());
      }
      if((type & IntEvent.MAX) != 0)
        _m.indexMax(e.oldmax(), e.max());
      if ((type &  IntEvent.REMOVE) != 0)
        {
          int nRemoves = e.numberOfRemoves();
          int min = e.min();
          int max = e.max();
          for (int i = 0; i < nRemoves; ++i)
          {
            int removedValue = e.removed(i);
            if(min <= removedValue && removedValue <= max)
              _m.indexRemove(removedValue);
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
    return IntExpArrayElement1.this;
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
      _m.resultValue(e.min());
    }
    else
    {
      if ((type &  IntEvent.MIN) != 0)
      {
        _m.resultMin(e.min());
      }
      if ((type &  IntEvent.MAX) != 0)
      {
        _m.resultMax(e.max());
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
              _m.resultRemove(removedValue);
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
    return IntExpArrayElement1.this;
  }

} //~ ElementObserver

class AryElementsObserver extends Observer
{
  private final int idx;

  public AryElementsObserver(int id){
    idx = id;
  }

  public void update(Subject exp, EventOfInterest event)
      throws Failure
  {
    IntEvent e = (IntEvent) event;
    int type = e.type();

    if ((type &  IntEvent.VALUE) != 0)
    {
      int value = e.min();
      _m.arrayElementValue(value, idx);
      /*if ( (value > _element.max()) || (value < _element.min()) ){
        _index.removeValue(idx);
      }*/
    }
    else
    {
      if ((type &  IntEvent.MIN) != 0)
      {
        /*if (_element.max() < e.min())
          _index.removeValue(idx);*/
        _m.arrayElementMin(e.oldmin(), e.min(), idx);
      }
      if ((type &  IntEvent.MAX) != 0)
      {
        /*if (_element.min() > e.max())
          _index.removeValue(idx);*/
        _m.arrayElementMax(e.oldmax(), e.max(), idx);
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
              _m.arrayElementRemove(removedValue, idx);
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
    return IntExpArrayElement1.this;
  }
} //~ AryElementsObserver



public IntExpArrayElement1(IntExpArray ary, IntExp indexExp)
{
  super(ary.constrainer());
  _ary = ary;
  _indexExp = indexExp;

  _aryElementsObservers = new AryElementsObserver[ary.size()];

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

  for (int i=0;i<_ary.size();i++){
    if (_index.contains(i)){
      _aryElementsObservers[i] = new AryElementsObserver(i);
      _ary.get(i).attachObserver(_aryElementsObservers[i]);
    }
  }
}

/**
 * Returns element-domain as an array of different sorted values.
 */
int[] elementDomain()
{
  int arMin = _ary.min();
  int arMax = _ary.max();
  int[] values= new int[arMax-arMin+1];
  int valCounter = 0;

  class IntExpComparator implements java.util.Comparator{
    public int compare(Object a1, Object a2){

      if (((IntExp)a1).min() < ((IntExp)a2).min())
        return -1;
      if (((IntExp)a1).min() == ((IntExp)a2).min())
        return 0;
      return 1;
    }
  };

  IntExpArray tmp = makeExtraction(_index, _ary);
  tmp.sort(new IntExpComparator());

  for (int i=tmp.get(0).min(); i<=tmp.get(0).max(); i++){
    if (tmp.get(0).contains(i))
    values[valCounter++] = i;
  }

  for(int i=1; i < tmp.size(); i++)
  {
    IntExp curElem = tmp.get(i);
    int min = curElem.min();
    int max = curElem.max();
    if (min < values[valCounter-1])
      min = values[valCounter-1]+1;
    for (int j=min;j<max;j++){  //== Changed: j<=max replaced to j<max
        if (curElem.contains(j))
          values[valCounter++] = j;
    }
  }


  int[] valFinally = new int[valCounter];
  System.arraycopy(values, 0, valFinally, 0, valCounter);
  return valFinally;
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
  //_m = new SimpleMapping(_index, _element, _ary, _aryElementsObservers);
  _m = new AdvancedMapping2(_index, _element, _ary, _aryElementsObservers);
  /*int nHoles = (_element.max() - _element.min() +1) - _element.size();
  if(_index.size() == _element.size() && nHoles < 2000)
  {
    _m = new OneToOneMapping(_index, _element, _ary);
  }
  else
  {
    _m = new SimpleMapping(_index, _element, _ary);
  }*/
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
/*
static public void main(String[] args){
  try{
    Constrainer C = new Constrainer("");

    class PrintValues extends GoalImpl{
      IntExpArray _arr;
      int[] _vals;
      PrintValues(IntExpArray arr, int[] vals){super(arr.constrainer());_arr = arr; _vals = vals;}
      public Goal execute(){
        String s = " ";
        String space;
        for (int i=0; i<_arr.size(); i++){
          if (_arr.get(i).size() > 1){
            space = "     ";
            s = s + " " + _vals[i] + space;
          }
          else{
            space = "   ";
            s = s + _vals[i] + space;
          }

        }
        System.out.println("\n" + s + "\n" + _arr);
        return null;
      }
   }


    IntExpArray array = new IntExpArray(C, 9);
    for (int i=0;i<array.size();i++){
      array.set(C.addIntVar(i-2, i+2, IntVar.DOMAIN_BIT_FAST), i);
    }


    IntVar index = C.addIntVar(0, array.size(), "index",IntVar.DOMAIN_BIT_SMALL);
    IntExpArrayElement1 elem1 = new IntExpArrayElement1(array, index);
    AdvancedMapping mp = (AdvancedMapping)elem1._m;

    PrintValues prnVals = new PrintValues(mp._valuesUsed, new int[]{-2,-1,0,1,2,3,4,5,6,7,8,9,10});

    System.out.println("Started with:\n" + mp._valuesUsed + "\nand the domain was:\n" + elem1._element);

    for (int i = array.min(); i<=array.max(); i++){
      values = values+"    " + i + "    ";
    }
    array.get(0).removeRange(-20, -10);
    Goal print = new GoalAnd(prnVals,
                             new GoalPrint(new IntExpArray(C, elem1), "\ndomain: "));
    Goal g1 = new GoalAnd(array.get(8).less(8), print, new GoalFail(C));
    Goal g2 = new GoalAnd(new IntExpConst(C, -1).less(array.get(0)), print, new GoalFail(C));
    Goal goalOr = new GoalOr(g1, g2);
    C.execute(goalOr);

//    array.get(5).setMin(5);
    index.removeValue(4);
    C.propagate();
    C.execute(print);
  }
  catch(Failure f){;}
}*/
}
