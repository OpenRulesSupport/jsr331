//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |   (..)  |                           *
//*         |_  J||L _|        CHOCO solver       *
//*                                               *
//*    Choco is a java library for constraint     *
//*    satisfaction problems (CSP), constraint    *
//*    programming (CP) and explanation-based     *
//*    constraint solving (e-CP). It is built     *
//*    on a event-based propagation mechanism     *
//*    with backtrackable structures.             *
//*                                               *
//*    Choco is an open-source software,          *
//*    distributed under a BSD licence            *
//*    and hosted by sourceforge.net              *
//*                                               *
//*    + website : http://choco.emn.fr            *
//*    + support : choco@emn.fr                   *
//*                                               *
//*    Copyright (C) F. Laburthe,                 *
//*                  N. Jussien    1999-2009      *
//* * * * * * * * * * * * * * * * * * * * * * * * *
package javax.constraints.impl.constraint;

/**
 * An implementation of the Constraint "And"
 * @author J.Feldman
 */

import javax.constraints.impl.Constraint;

import choco.Choco;


public class And extends Constraint {

	public And(javax.constraints.Constraint c1, javax.constraints.Constraint c2) {
		super(c1.getProblem()); 
		choco.kernel.model.constraints.Constraint cc1 = 
			((javax.constraints.impl.Constraint)c1).getChocoConstraint();
		choco.kernel.model.constraints.Constraint cc2 = 
			((javax.constraints.impl.Constraint)c2).getChocoConstraint();
		choco.kernel.model.constraints.Constraint and = Choco.and(cc1,cc2);
		setImpl(and);
	}

}
