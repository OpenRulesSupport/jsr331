
package com.exigen.ie.constrainer.impl;
import java.util.Map;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Domain;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.GoalInstantiate;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.NonLinearExpression;
import com.exigen.ie.constrainer.Undo;
import com.exigen.ie.constrainer.Undoable;
import com.exigen.ie.tools.Reusable;
import com.exigen.ie.tools.ReusableFactory;
//
//: IntVarImpl.java
//
/**
 * A generic implementation of the IntVar interface.
 */
public class IntVarImpl extends IntExpImpl implements IntVar
{
  private Domain		         _domain;
  private IntDomainHistory _history;

  public IntVarImpl(Constrainer constrainer)
  {
    this(constrainer,1000000);
  }

  public IntVarImpl(Constrainer constrainer, int max)
  {
    this(constrainer,0,max);
  }

  public IntVarImpl(Constrainer constrainer, int min, int max)
  {
    this(constrainer,min,max,"var");
  }

  public IntVarImpl(Constrainer constrainer, int min, int max, String name)
  {
    super(constrainer,name);
    _domain = new DomainBits(this,min,max);

    _history = new IntDomainHistory(this);
  }


  public IntVarImpl(Constrainer constrainer, int min, int max, String name, int domain_type)
  {
    super(constrainer,name);

    int size = max - min +1;

    switch(domain_type)
    {
      case DOMAIN_PLAIN:
        _domain = new DomainImpl(this,min,max);
        break;
      case DOMAIN_BIT_FAST:
        _domain = new DomainBits(this,min,max);
        break;
      case DOMAIN_BIT_SMALL:
        _domain = new DomainBits2(this,min,max);
        break;
      case DOMAIN_DEFAULT:
        if (size < 16)
          _domain = new DomainBits(this,min,max);
        else if (size < 128)
          _domain = new DomainBits2(this,min,max);
        else
          _domain = new DomainImpl(this,min,max);
        break;
      }

    _history = new IntDomainHistory(this);
  }


  public IntVarImpl(Constrainer constrainer, String name)
  {
    this(constrainer,0,1000000,name);
  }

  public int domainType()
  {
    return _domain.type();
  }

  public IntDomainHistory history()
  {
    return _history;
  }


  public void propagate() throws Failure
  {
    _history.propagate();
  }

  public boolean contains(int value)
  {
    return _domain.contains(value);
  }

  public String domainToString()
  {
    return _domain.toString();
  }

  public Goal instantiate()
  {
    return new GoalInstantiate(this);
  }

  public int max()
  {
    return _domain.max();
  }

  public int min()
  {
    return _domain.min();
  }


  public void setMax(int max) throws Failure
  {
    if (_domain.setMax(max))
    {
      _history.setMax(max());
      addToPropagationQueue();
    }
  }

  public void setMin(int min) throws Failure
  {
    if (_domain.setMin(min))
    {
      _history.setMin(min());
      addToPropagationQueue();
    }
  }

  public void setValue(int value) throws Failure
  {
    setMin(value);
    setMax(value);
  }

 // removes only min and max value (leaves holes)
  public void removeValue(int value) throws Failure
  {
    if (_domain.removeValue(value))
    {
      _history.setMin(_domain.min());
      _history.setMax(_domain.max());
      _history.remove(value);
      addToPropagationQueue();
    }
  }
  protected void removeRangeInternal(int min, int max) throws Failure {
    if (_domain.removeRange(min, max)) {
      _history.setMin(_domain.min());
      _history.setMax(_domain.max());
      _history.remove(min, max);
      addToPropagationQueue();
    }
  }

  public int size()
  {
    return _domain.size();
  }

  public int value() throws Failure
  {
    if (!bound())
      constrainer().fail("Attempt to get value of the unbound variable "+this);
    return _domain.min();
  }

  public Undo createUndo()
  {
    _history.saveUndo();
    return UndoIntVarImpl.getIntVarUndo();
  }

   public void forceSize(int val)
   {
      _domain.forceSize(val);
   }

   public void forceMin(int val)
   {
      _domain.forceMin(val);
   }

   public void forceMax(int val)
   {
      _domain.forceMax(val);
   }

   public void forceInsert(int val)
   {
      _domain.forceInsert(val);
   }

  public void iterateDomain(IntExp.IntDomainIterator it) throws Failure
  {
    _domain.iterateDomain(it);
  }

  public boolean isLinear(){
    return true;
  }

  public double calcCoeffs(Map map, double factor) throws NonLinearExpression{
    if (bound())
      return max()*factor;

    Double coef = (Double)map.get(this);
    if (coef == null){
      map.put(this, new Double(factor));
    }
    else{
      map.put(this, new Double(factor + coef.doubleValue()));
    }
    return 0;
  }


  /**
   * Undo Class for IntVar.
   */
  static final class UndoIntVarImpl extends UndoSubject
  {

    static ReusableFactory _factory = new ReusableFactory()
    {
        protected Reusable createNewElement()
        {
          return new UndoIntVarImpl();
        }

    };

    static UndoIntVarImpl getIntVarUndo()
    {
      return (UndoIntVarImpl) _factory.getElement();
    }

    int _history_index;

    public void undoable(Undoable u)
    {
      super.undoable(u);
      IntVarImpl intvar = (IntVarImpl) u;
      _history_index = intvar.history().currentIndex();
  //    System.out.println("++ SAVE: " + intvar + "index:" + _history_index);

   }

    public void undo()
    {
        IntVarImpl intvar = (IntVarImpl) undoable();
  //      System.out.println("++ Undo: " + intvar);
        intvar.history().restore(_history_index);
        super.undo();
  //      System.out.println("-- Undo: " + intvar);
    }

    /**
     * Returns a String representation of this object.
     * @return a String representation of this object.
     */
    public String toString()
    {
      return "UndoIntVar "+undoable();
    }

  } // ~UndoIntVarImpl

} // ~IntVarImpl
