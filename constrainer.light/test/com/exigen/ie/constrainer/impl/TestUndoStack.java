package com.exigen.ie.constrainer.impl;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
import com.exigen.ie.constrainer.IntVar;
import com.exigen.ie.constrainer.Undo;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Exigen Group, Inc.</p>
 * @author Tseitlin Eugeny
 * @version 1.0
 */

public class TestUndoStack extends TestCase {
  private Constrainer C = new Constrainer("TestUndoStack");
  public TestUndoStack(String name) {super(name);}
  public static void main(String[] args) {
    TestRunner.run(new TestSuite(TestUndoStack.class));
  }

  public void testBackTrack(){
    UndoStack undos = new UndoStack();
    IntExpArray array =  new IntExpArray(C, 10);
    TestUtils.TestObserver[] observers = new TestUtils.TestObserver[array.size()];
    UndoableIntImpl[] undoableInts = new UndoableIntImpl[array.size()];

    //initialization
    for (int i=0; i<array.size(); i++){
      array.set(C.addIntVar(0, 9, IntVar.DOMAIN_BIT_FAST), i);
      observers[i] = TestUtils.createTestObserver();
      array.get(i).attachObserver(observers[i]);
      undoableInts[i] = new UndoableIntImpl(C, i);
    }

    //saving
    for (int i=0;i<array.size();i++){
      Undo undo = array.get(i).createUndo();
      undo.undoable(array.get(i));
      undos.pushUndo(undo);

      undo = SubjectImpl.UndoAttachObserver.getUndo(array.get(i), observers[i]);
      undos.pushUndo(undo);

      undo = undoableInts[i].createUndo();
      undo.undoable(undoableInts[i]);
      undos.pushUndo(undo);
    }

    //making changes
    for (int i=0;i<array.size();i++){
      IntExp exp = array.get(i);
      try{
        exp.setMin(i);
      }catch(Failure f){fail("test failed");}
      undoableInts[i].setValue(-i);
    }

    //check whether the changes have taken effect
    for (int i=0;i<array.size();i++){
      assertTrue(TestUtils.contains(array.get(i).observers(), observers[i]));
      assertEquals(i, array.get(i).min());
      assertEquals(-i, undoableInts[i].value());
    }

    //restoring the original state
    int size = undos.size();

    for (int j = 3; j<size; j+=3){
      undos.backtrack(size-j);
      //check the appropriate changes have been backtracked
      int counter = array.size()-j/3;
      assertEquals(0, array.get(counter).min());
      assertEquals(counter, undoableInts[counter].value());
      //check whether an appropriate observer has been detached
      assertTrue(!TestUtils.contains(array.get(counter).observers(), observers[counter]));

      //changes backtracking hasn't taken place for other variables
      for (int i=0;i<counter;i++){
        assertTrue(TestUtils.contains(array.get(i).observers(), observers[i]));
        assertEquals(i, array.get(i).min());
        assertEquals(-i, undoableInts[i].value());
      }
    }

  }

}