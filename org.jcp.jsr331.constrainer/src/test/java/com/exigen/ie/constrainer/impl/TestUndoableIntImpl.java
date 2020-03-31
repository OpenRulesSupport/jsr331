package com.exigen.ie.constrainer.impl;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Undo;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Exigen Group, Inc.</p>
 * @author unascribed
 * @version 1.0
 */

public class TestUndoableIntImpl extends TestCase {
  private Constrainer C = new Constrainer("TestUndoableIntImpl");
  public TestUndoableIntImpl(String name) {super(name);}
  public static void main(String[] args) {
    TestRunner.run(new TestSuite(TestUndoableIntImpl.class));
  }

  public void testCreateUndo(){
    UndoableIntImpl undoableInt = new UndoableIntImpl(C, 10);
    Undo[] stages = new Undo[100];
    for (int i=1;i<=100;i++){
      stages[i-1] = undoableInt.createUndo();
      stages[i-1].undoable(undoableInt);
      undoableInt.setValue(10+i);
      assertEquals(10+i, undoableInt.value());
    }

    //ascending order
    for (int i=0;i<100;i++){
      stages[i].undo();
      assertEquals(10+i, undoableInt.value());
    }

    //descending order
    for (int i=99;i>=0;i--){
      stages[i].undo();
      assertEquals(10+i, undoableInt.value());
    }
  }

}