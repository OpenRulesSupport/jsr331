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


import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import choco.Choco;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * An implementation of the Constraint "Cardinality"
 * @author J.Feldman
 */

public class Cardinality extends Constraint {
	
	static final String name = "Cardinality";
	
	/**
	 * Example: cardinality(vars,cardValue) less than var
	 * Here  cardinality(vars,cardValue)  denotes a constrained integer 
	 * variable that is equal to the number of those elements in the 
	 * array "vars" that are bound to the "cardValue".
	 * @param vars array of variables
     * @param cardValue cardinality value
     * @param oper operator
     * @param var integer variable 
	 */
	public Cardinality(Var[] vars, int cardValue, String oper, Var var) {
		super(vars[0].getProblem(),name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();
		IntegerVariable[] intVars = p.createIntVarArray(vars);
		IntegerVariable cardVar = Choco.makeIntVar("cardVar", 0, vars.length);
		p.addChocoConstraint(Choco.occurrence(cardValue, cardVar, intVars)); 
		javax.constraints.impl.Var cVar = (javax.constraints.impl.Var)var;
		choco.kernel.model.constraints.Constraint card = p.compare(cardVar,oper,cVar.getChocoVar());
		setImpl(card);
	}
	
	/**
	 * Example: cardinality(vars,cardVar) less than var
	 * Here  cardinality(vars,cardVar)  denotes a constrained integer 
	 * variable that is equal to the number of those elements in the 
	 * array "vars" that are bound to the value of the variable "cardVar"
	 * when it is instantiated.
	 * @param vars array of variables
	 * @param cardVar cardinality variable
	 * @param oper operator
	 * @param var variable
	 */
	public Cardinality(Var[] vars, Var cardVar, String oper, Var var) {
		super(vars[0].getProblem(),name);
		Problem problem = (Problem) vars[0].getProblem();
		problem.notImplementedException("Cardinality(Var[] vars, Var cardVar, String oper, Var var)");
	}
	
	/**
	 * Example: cardinality(vars,cardValue) less than value
	 * @param vars array of variables
	 * @param cardValue  cardinality value
	 * @param oper operator
	 * @param value integer value
	 */
	public Cardinality(Var[] vars, int cardValue, String oper, int value) {
		super(vars[0].getProblem(),name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();
		IntegerVariable[] intVars = p.createIntVarArray(vars);
		IntegerVariable cardVar = Choco.makeIntVar("cardVar", 0, vars.length);
		p.addChocoConstraint(Choco.occurrence(cardValue, cardVar, intVars)); 
		choco.kernel.model.constraints.Constraint card = p.compare(cardVar,oper,value);
		setImpl(card);
	}
	
	/**
     * Example: cardinality(vars,cardValue) less than value
     * @param vars array of variables
     * @param cardVar  cardinality variable
     * @param oper operator
     * @param value integer value
     */
	public Cardinality(Var[] vars, Var cardVar, String oper, int value) {
		super(vars[0].getProblem(),name);
		Problem problem = (Problem) vars[0].getProblem();
		problem.notImplementedException("Cardinality(Var[] vars, Var cardVar, String oper, int value)");
	}
		
}
