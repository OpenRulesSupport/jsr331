//================================================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1 
// 
// CONSTRAINER-BASED REFERENCE IMPLEMENTATION
//
// Copyright (c) Cork Constraint Computation Centre, 2010
// University College Cork, Cork, Ireland, www.4c.ucc.ie
// Constrainer is copyrighted by Exigen Group, USA.
// 
//================================================================
package javax.constraints.impl;

import javax.constraints.Problem;
import javax.constraints.extra.AbstractReversible;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.UndoableInt;

/**
 * This class implements reversible integers that
 * automatically restore their values when a solver backtracks.
 *
 */

public class Reversible extends AbstractReversible  {
	
	Constrainer getConstrainer() {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		return p.getConstrainer();
	}
	
	public Reversible(Problem problem, int value) {
		this(problem,"",value);
	}
	
	public Reversible(Problem problem, String name, int value) {
		super(problem,name,value);
		setImpl(getConstrainer().addUndoableInt(value));
	}
	
	public int getValue() {
		UndoableInt undoableInt = (UndoableInt)getImpl();
		return undoableInt.value();
	}
	
	public void setValue(int value) {
		UndoableInt undoableInt = (UndoableInt)getImpl();
		undoableInt.setValue(value);
	}
	
}
