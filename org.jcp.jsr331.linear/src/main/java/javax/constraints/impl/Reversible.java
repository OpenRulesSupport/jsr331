//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
package javax.constraints.impl;

/**
 * This class implements reversible integers that
 * automatically restore their values when a solver backtracks.
 * 
 * @author J.Feldman
 * Modified by: 
 */

import javax.constraints.Problem;
import javax.constraints.extra.AbstractReversible;


public class Reversible extends AbstractReversible  {
	
	javax.constraints.impl.Problem getChocoProblem() {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		return p;
	}
	
	public Reversible(Problem problem, int value) {
		this(problem,"",value);
	}
	
	public Reversible(Problem problem, String name, int value) {
		super(problem,name,value);
		//setImpl(getChocoProblem().addUndoableInt(value)); ??
	}
	
	public int getValue() {
//		UndoableInt undoableInt = (UndoableInt)getImpl(); ??
//		return undoableInt.value();
		return -1;
	}
	
	public void setValue(int value) {
//		UndoableInt undoableInt = (UndoableInt)getImpl(); ??
//		undoableInt.setValue(value);
	}
	
}
