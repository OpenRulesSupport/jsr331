//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |  �(..)  |                           *
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
 * An implementation of the interface "Constraint"
 * @author J.Feldman
 */

import choco.Choco;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;

import javax.constraints.Problem;
import javax.constraints.impl.constraint.And;
import javax.constraints.impl.constraint.IfThen;
import javax.constraints.impl.constraint.Neg;
import javax.constraints.impl.constraint.Or;


/**
 * The interface Constraint defines a generic interface for all
 * constraints that could be created and posted within the Problem.
 * Concrete constraints can be defined in two ways:
 * <ul>
 * <li> explicitly in the interface Problem such as "allDifferent"</li>
 * <li> implicitly inside constrained expressions such as sum(vars)
 * or var1.eq(var2). </li>
 * </ul>
 * <br>
 * The interface Problem includes only commonly used and de-facto standardized
 * global constraints in their most popular forms. This API assumes that every API
 * implementation will provide a library of global constraints
 * that implements the interface Constraint and follows the
 * guidelines of the Global Constraint Catalog - see http://www.emn.fr/x-info/sdemasse/gccat/index.html.
 * 
 */

public class Constraint extends AbstractConstraint { 
	
	public Constraint(Problem problem) {
		this(problem,"");
	}
	
	public Constraint(Problem problem, String name) {
		super(problem, name);
	}
	
	
	public Constraint(Problem problem, choco.kernel.model.constraints.Constraint myConstraint) {
		super(problem);
		setImpl(myConstraint);
	}

	/**
	 * This method is used in the problem definition to post the
	 * constraint. The constraint posting may do two things:
	 * 1) initial constraint propagation will be executed;
	 * 2) the constraint will be memorized and wait for notification events
	 * when the variables it controls are changed.
	 * The actual posting logic depends on an underlying CP solver.
	 * If the posting was unsuccessful, this method throws a runtime exception.
	 * A user may put constraint posting in a try-catch block to react to
	 * posting failures.
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post() {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
//		p.post(this);
		try {
			choco.kernel.model.constraints.Constraint constraint = 
				(choco.kernel.model.constraints.Constraint) getImpl();
			p.addChocoConstraint(constraint);
//			p.getChocoSolver().read(p.getChocoModel());
		} catch (Exception e) {
			String msg = "Failure to post constraint: " + getName();
			getProblem().log(msg);
			throw new RuntimeException(msg);
		}
	}

	public choco.kernel.model.constraints.Constraint getChocoConstraint() {
		return (choco.kernel.model.constraints.Constraint) getImpl();
	}
	/**
	 * This method returns a Var variable that is equal 1 if the constraint
	 * is satisfied and equals 0 if it is violated.
	 * @return Var with a domain [0;1]: a 1 value indicates the constraint is satisfied,
	 *                                  a 0 value indicates the constraint is violated.
	 */
	public javax.constraints.VarBool asBool() {
		if (getImpl() == null) {
			throw new RuntimeException("Constraint " + getName() + 
					" has no implementation for the method toVar()." +
					" It cannot be used in logical expressions.");
		}
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();	
		choco.kernel.model.constraints.Constraint constraint = getChocoConstraint();
		if (constraint == null) {
			String msg = "Failure to convert constraint " + getName() + " to VarBool. Not implemented.";
			getProblem().log(msg);
			throw new RuntimeException(msg);
		}
		Model m = p.getChocoModel();
		IntegerVariable boolVar = Choco.makeBooleanVar("boolVar");
		m.addConstraint(Choco.ifOnlyIf(Choco.eq(boolVar, 1), constraint));
//		p.getChocoSolver().read(m); //??
		VarBool var = new javax.constraints.impl.VarBool(getProblem(), boolVar);
		var.setName(getName()+".asBool");
		return var;
	}	
	
	/**
	 * Returns an "AND" Constraint. The Constraint "AND" is satisfied if both of the
	 * 		   Constraints "this" and "c" are satisfied. The Constraint "AND" is not satisfied
	 * 		   if at least one of the Constraints "this" or "c" is not satisfied.
	 * @param c the Constraint which is part of the new "AND" Constraint
	 * @return a Constraint "AND" between the Constraints "this" and "c2".
	 */
	public javax.constraints.Constraint and(javax.constraints.Constraint c) {
		return new And(this, c);
	}

	/**
	 * Returns an "OR" Constraint. The Constraint "OR" is satisfied if either of the
	 * 		   Constraints "this" and "c" is satisfied. The Constraint "OR" is not satisfied
	 * 		   if both of the Constraints "this" and "c" are not satisfied.
	 * @param c1 the Constraint which is part of the new "OR" Constraint
	 * @return a Constraint "OR" between the Constraints "this" and "c".
	 */
	public javax.constraints.Constraint or(javax.constraints.Constraint c) {
		return new Or(this, c);
	}

	/**
	 * Returns a Constraint that is satisfied if and only if this constraint is not satisfied.
	 * @return a Constraint that is satisfied if and only if this constraint is not satisfied.
	 */
	public javax.constraints.Constraint negation() {
		return new Neg(this);
	}
	
	/**
	 * Returns a Constraint that states the implication: this => c.
	 * In other words, if this constraint is satisfied, then constraint "c"
	 * should also be satisfied.
	 *
	 * @param c the Constraint in the implication.
	 * @return a Constraint that means this => c (if this then c).
	 *
	 */
	public javax.constraints.Constraint implies(javax.constraints.Constraint c) {
		return new IfThen(this,c);
	}
}
