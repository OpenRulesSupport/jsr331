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

import com.exigen.ie.constrainer.IntBoolExp;

/**
 * This constraint states the implication: if c1 then c2 for two
 * constraints c1 and c2. In means: if c1 is satisfied, then c2 should
 * also be satisfied.
 * 
 */

public class IfThen extends Constraint {

	public IfThen(javax.constraints.Constraint c1, javax.constraints.Constraint c2) {
		super(c1.getProblem());
		com.exigen.ie.constrainer.Constraint myC1 = (com.exigen.ie.constrainer.Constraint)c1.getImpl();
		com.exigen.ie.constrainer.Constraint myC2 = (com.exigen.ie.constrainer.Constraint)c2.getImpl();
		IntBoolExp b1 = myC1.toIntBoolExp();
		if (b1 == null)
			throw new RuntimeException("Constraint " + c1.getName()
					+ " cannot be used inside ifThen");
		IntBoolExp b2 = myC2.toIntBoolExp();
		if (b2 == null)
			throw new RuntimeException("Constraint " + c2.getName()
					+ " cannot be used inside ifThen");
		setImpl(myC1.constrainer().addConstraint(b1.implies(b2)));
		setName("ifThen");
	}
}
