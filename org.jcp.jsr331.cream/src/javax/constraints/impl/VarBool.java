//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |  °(..)  |                           *
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
package javax.constraints.impl;

/**
 * An implementation of the interface "VarBool"
 * @author J.Feldman
 */

import javax.constraints.Problem;

import choco.kernel.model.variables.integer.IntegerVariable;

public class VarBool extends Var implements javax.constraints.VarBool {
	
	public VarBool(Problem problem, String name) {
		super(problem,name,0,1);
	}
	
	public VarBool(Problem problem) {
		this(problem,"");
	}
	
	public VarBool(Problem problem, IntegerVariable expression) {
		super(problem,expression);
	}

}
