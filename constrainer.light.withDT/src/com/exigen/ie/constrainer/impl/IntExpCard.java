package com.exigen.ie.constrainer.impl;
import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;

/**
 * An implementation of the expression: <code>cardinality(IntExpArray)</code>.
 */
public final class IntExpCard extends IntExpImpl
{
  private IntExpArray   _vars;
  private IntVar        _possible_required;
  private IntVarImpl    _indexes;
  private int           _card_value;

  /**
   * This is an Observer for min/max events on _possible_required variable:
   * _possible_required.min() <= this.min() <= this.max() <= _possible_required.max()
   */

  class ObserverIndexes extends Observer
  {

    public void update(Subject var, EventOfInterest interest)
      throws Failure
    {
//      Debug.on();Debug.print("ObserverPossibleRequired "+interest);Debug.off();

      // any _possible event may produce only decrease in size
      _possible_required.setMax(_indexes.size() - 1);

    }

    public Object master()
    {
      return IntExpCard.this;
    }

    public int subscriberMask()
    {
      return EventOfInterest.MINMAX | EventOfInterest.REMOVE;
    }

    public String toString()
    {
      return "ObserverIndexes";
    }
  } //~ ObserverPossibleRequired


  public void setMin(int min) throws Failure
  {
    if (min == max())
      bindAll();
  }


  public void setMax(int max) throws Failure
  {
    if (max == min())
    {
      removeUnbounds();
    }
  }

  public void removeUnbounds() throws Failure
  {
    IntExp.IntDomainIterator it = new IntExp.IntDomainIterator()
    {
        public boolean doSomethingOrStop(int i) throws Failure
        {
          if (i == _vars.size())
            return true;
          IntExp exp = (IntExp) _vars.get(i);
          if (!exp.bound())
            exp.removeValue(_card_value);
          return true;
        }
    };

    _indexes.iterateDomain(it);
  }

  public void bindAll() throws Failure
  {
    IntExp.IntDomainIterator it = new IntExp.IntDomainIterator()
    {
        public boolean doSomethingOrStop(int i) throws Failure
        {
          if (i == _vars.size())
            return true;
          IntExp exp = (IntExp) _vars.get(i);
          exp.setValue(_card_value);
          return true;
        }
    };

    _indexes.iterateDomain(it);
  }


  public IntExpCard(Constrainer constrainer, IntExpArray vars, int card_value) throws Failure
  {
//		super(constrainer,0,vars.size(),"C" + card_value, IntVarImplTrace.TRACE_ALL);
    super(constrainer,"C" + card_value);
    _card_value = card_value;

//      int trace =  IntVarImplTrace.TRACE_ALL;
      int trace =  0;

    int size = vars.size();
    _vars = vars;

    _indexes = (IntVarImpl)constrainer().addIntVarTraceInternal(
          0, size ,"IX" + card_value,IntVar.DOMAIN_BIT_FAST, trace);


    int possible_instances = 0;
    int required_instances = 0;
    for(int i=0; i<size; i++)
    {
      IntExp exp = (IntExp)vars.get(i);
      if (exp.contains(card_value))
      {
        if (exp.bound())
          required_instances++;
      }
      else _indexes.removeValue(i);

    }

    _possible_required = constrainer().addIntVarTraceInternal(
        required_instances,_indexes.size() - 1, "PR" + card_value,IntVar.DOMAIN_PLAIN, trace);


    _indexes.attachObserver(new ObserverIndexes());

    //_possible_required.attachObserver(new ObserverPossibleRequired());
    //constrainer().trace(_possible_required);
    //constrainer().trace(this);

  //  this.attachObserver(new ObserverCardValue());

  }

  public int max()
  {
    return _possible_required.max();
  }

  public int min()
  {
    return _possible_required.min();
  }


  public void attachObserver(Observer observer)
  {
    super.attachObserver(observer);
    _possible_required.attachObserver(observer);
  }

  public void reattachObserver(Observer observer)
  {
    super.reattachObserver(observer);
    _possible_required.reattachObserver(observer);
  }

  public void detachObserver(Observer observer)
  {
    super.detachObserver(observer);
    _possible_required.detachObserver(observer);
  }


  public void removeIndex(int idx) throws Failure
  {
    _indexes.removeValue(idx);
  }

  public void addValueIndex(int idx) throws Failure
  {
    _possible_required.setMin(_possible_required.min() + 1);
  }

  public String toString()
  {
    return "(" + _possible_required  + ":" + _indexes + ")";
  }

  /*
    extending to 5.1.0
    added by S. Vanskov
  */
  int get_cardinality_value () {
    return _card_value;
  }
  /* EO additions */

} // ~IntExpCard
