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
package javax.constraints.impl.constraint;


import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;

/**
 * This is one of the most popular Constraint that
 * states that all of the elements within the array of variables "vars" 
 * must take different values from each other.
 * The RI should overload the method "defineNativeImpl" and 
 * may add its own additional constructors.
 */

public class AllDifferent extends Constraint {

	public AllDifferent(Var[] vars) {
		super(vars[0].getProblem(),"AllDiff");
		Problem problem = (Problem) getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer,vars.length);
		for(int i=0; i<vars.length; i++) {
			IntExp var = (IntExp)vars[i].getImpl();
			intvars.set(var, i);
		}

		com.exigen.ie.constrainer.Constraint allDiff =
			new com.exigen.ie.constrainer.impl.ConstraintAllDiff(intvars);
		setImpl(constrainer.addConstraint(allDiff));
	}
}
