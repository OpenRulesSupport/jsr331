package com.exigen.ie.constrainer.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Constraint;
import com.exigen.ie.constrainer.EventOfInterest;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;
import com.exigen.ie.constrainer.GoalGenerate;
import com.exigen.ie.constrainer.IntBoolVar;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntSetVar;
import com.exigen.ie.constrainer.Observer;
import com.exigen.ie.constrainer.Subject;
import com.exigen.ie.constrainer.UndoImpl;
import com.exigen.ie.tools.Reusable;
import com.exigen.ie.tools.ReusableFactory;

public class IntSetVarImpl extends SubjectImpl implements IntSetVar
{

  private IntExpArray _set;
  private HashMap _values2index = new HashMap();
  private int _unboundsCounter;
  private IntSetVarImpl(Constrainer C){
    super(C);
  }

public IntSetVarImpl(Constrainer C, int[] array, String name)
{
  super(C, name);
  int size = array.length;
  _set = new IntExpArray(C, size);
  for (int i=0; i<size; i++){
    _set.set(C.addIntBoolVarInternal(name()+ "[" + array[i] + "]"), i);
    _set.get(i).attachObserver(new ElementsObserver(array[i]));
    _values2index.put(new Integer(array[i]), new Integer(i));
  }
  _unboundsCounter = size;
}


public IntSetVarImpl(Constrainer C, int[] array){
  this(C, array, "");
}

private IntBoolVar hasElem(int i){
  int idx = ((Integer)_values2index.get(new Integer(i))).intValue();
  return (IntBoolVar)_set.get(idx);
}

public boolean isPossible(int val){
  if (!_values2index.keySet().contains(new Integer(val)))
    return false;
  return  (hasElem(val).max() == 1) ? true : false;
}

public boolean bound(){
  for (int i=0; i<_set.size(); i++){
    if (! _set.get(i).bound())
      return false;
  }
  return true;
}

public Set value() throws Failure{
  if (!bound())
    constrainer().fail("Attempt to get value of the unbound variable "+this);
  return requiredSet();
}

public Set requiredSet(){
  java.util.HashSet values = new java.util.HashSet();
  Iterator iter = _values2index.keySet().iterator();
  while(iter.hasNext()){
    Integer curValue = (Integer)iter.next();
    if (hasElem(curValue.intValue()).min() == 1){
      values.add(curValue);
    }
  }
  /**@todo add emptiness chrecking*/
  return values;
}

/** TODO to be removed*/
public Set possibleSet(){
  java.util.HashSet values = new java.util.HashSet();
  Iterator iter = _values2index.keySet().iterator();
  while(iter.hasNext()){
    Integer curValue = (Integer)iter.next();
    if (hasElem(curValue.intValue()).max() == 1){
      values.add(curValue);
    }
  }
  /**@todo add emptiness chrecking*/
  return values;
}

public Constraint nullIntersectWith(IntSetVar anotherVar){
  return intersectionWith(anotherVar).cardinality().equals(0);
}

public IntSetVar intersectionWith(IntSetVar anotherSet){
  if (anotherSet instanceof IntSetVarImpl)
    return intersectionWith((IntSetVarImpl)anotherSet);
  else
    return anotherSet.intersectionWith(this);
}

public IntSetVar unionWith(IntSetVar anotherSet){
  if (anotherSet instanceof IntSetVarImpl)
    return unionWith((IntSetVarImpl)anotherSet);
  else
    return anotherSet.unionWith(this);
}

public IntSetVar intersectionWith(IntSetVarImpl anotherSet){

  Set values1 = anotherSet._values2index.keySet(),
      values2 = _values2index.keySet();

  int[] tmp = new int[values1.size()];

  Iterator iter = values1.iterator();
  int counter = 0;
  while(iter.hasNext()){
    Integer curValue = (Integer)iter.next();
    if (values2.contains(curValue)){
      tmp[counter++] = curValue.intValue();
    }
  }
  /**@todo add emptiness check*/

  int[] intersection = new int[counter];
  System.arraycopy(tmp, 0, intersection, 0, counter);
  IntSetVarImpl result = (IntSetVarImpl)constrainer().addIntSetVar(intersection);
  for (int i = 0; i<intersection.length; i++){
    int val = intersection[i];
    try{
      result.hasElem(val).equals(this.hasElem(val).and(anotherSet.hasElem(val))).execute();
    }catch(Failure f){/*it would be never thrown*/}
  }
  return result;
}

public IntSetVar unionWith(IntSetVarImpl anotherSet){
  Set values1 = _values2index.keySet(),
      values2 = anotherSet._values2index.keySet();

  int[] tmp = new int[values1.size() + values2.size()];
  int counter = 0;
  Iterator iter = values1.iterator();
  while(iter.hasNext()){
    tmp[counter++] = ((Integer)iter.next()).intValue();
  }
  iter = values2.iterator();
  while(iter.hasNext()){
    Integer curValue =(Integer)iter.next();
    if (!values1.contains(curValue))
    tmp[counter++] = (curValue).intValue();
  }

  int[] union = new int[counter];
  System.arraycopy(tmp, 0, union, 0, counter);

  IntSetVarImpl result = (IntSetVarImpl)constrainer().addIntSetVar(union);

  for (int i=0; i< union.length; i++){
    int val = union[i];
    if (values1.contains(new Integer(val))){
      if (values2.contains(new Integer(val))){
        try{
          result.hasElem(val).equals(hasElem(val).or(anotherSet.hasElem(val))).execute();
          }catch(Failure f){/*it would be never thrown*/}
      }
      else{
        try{result.hasElem(val).equals(hasElem(val)).execute();}
        catch(Failure f){/*it would be never thrown*/};
      }
    }
    else{
      try{result.hasElem(val).equals(anotherSet.hasElem(val)).execute();}
      catch(Failure f){/*it would be never thrown*/};
    }
  }
  return result;
}




public Goal generate(){
  return new GoalGenerate(_set);
}

public void propagate() throws Failure{}

public boolean possible(int value){
  return (hasElem(value).max() == 1);
}

public boolean required(int value){
  return (hasElem(value).min() == 1);
}

public boolean contains(Set anotherSet){
  if (!_values2index.keySet().containsAll(anotherSet))
    return false;
  Iterator iter = anotherSet.iterator();
  while(iter.hasNext()){
    int val = ((Integer)iter.next()).intValue();
    if (!possible(val))
      return false;
  }
  return true;
}

public void remove(int val) throws Failure{
  hasElem(val).setFalse();
}

public void require(int val) throws Failure{
  hasElem(val).setTrue();
}

class ElementsObserver extends Observer{
  private int _val;

  public ElementsObserver(int val){
    _val = val;
  }

  public void update(Subject exp, EventOfInterest event)
      throws Failure
  {
    constrainer().addUndo(UndoPossibleSetReduction.getUndo(IntSetVarImpl.this));
    _unboundsCounter--;
    IntEvent e = (IntEvent) event;
    int valueMask = 0;
    if (_unboundsCounter == 0){
      valueMask = IntSetEvent.IntSetEventConstants.VALUE;
    }
    if (e.max() == 1){
      //TRUE
      IntSetVarImpl.this.notifyObservers(IntSetEvent.getEvent(IntSetVarImpl.this,
          _val,
          IntSetEvent.IntSetEventConstants.REQUIRE|valueMask));
    }
    else{
      //FALSE
      IntSetVarImpl.this.notifyObservers(IntSetEvent.getEvent(IntSetVarImpl.this,
          _val,
          IntSetEvent.IntSetEventConstants.REMOVE|valueMask));
    }
  } // ~update()

  public int subscriberMask()
  {
    return IntEvent.Constants.ALL;
  }

  public Object master()
  {
    return IntSetVarImpl.this;
  }
}




public IntExp cardinality(){
  return _set.sum();
}

static public class UndoPossibleSetReduction extends UndoImpl{

  static ReusableFactory _factory = new ReusableFactory(){
      protected Reusable createNewElement(){
        return new UndoPossibleSetReduction();
      }
  };

  static UndoPossibleSetReduction getUndo(IntSetVarImpl var){
    UndoPossibleSetReduction undo = (UndoPossibleSetReduction) _factory.getElement();
    undo.undoable(var);
    return undo;
  }

  public String toString(){
    return "UndoPossibleSetReduction "+undoable();
  }

  public void undo(){
    IntSetVarImpl var = (IntSetVarImpl) undoable();
    var._unboundsCounter++;
    super.undo();
  }
}


}