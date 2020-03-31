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


import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import com.exigen.ie.constrainer.IntBoolExp;

public class Or extends Constraint {

	public Or(javax.constraints.Constraint c1, javax.constraints.Constraint c2) {
		super(c1.getProblem(),"or"); 
		Object myC1 = c1.getImpl();
		if (myC1 == null)
			error(c1);
		Object myC2 = c2.getImpl();
		if (myC2 == null)
			error(c2);
		IntBoolExp b1 = ((com.exigen.ie.constrainer.Constraint)myC1).toIntBoolExp();
		if (b1 == null)
			error(c1);
		IntBoolExp b2 = ((com.exigen.ie.constrainer.Constraint)myC2).toIntBoolExp();
		if (b2 == null)
			error(c2);
		IntBoolExp newConstraint;
		newConstraint = b1.or(b2);
		Problem p = (Problem)c1.getProblem();
		setImpl(p.getConstrainer().addConstraint(newConstraint));
	}
	
	void error(javax.constraints.Constraint c) {
		throw new RuntimeException("Constraint "+c.getName() + " cannot be used with constraint Or");
	}
}
