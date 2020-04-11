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
 * An implementation of the Constraint "GlobalCardinality".
 * 
 */

import choco.Choco;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Var;
import javax.constraints.impl.Problem;



public class GlobalCardinality extends javax.constraints.impl.Constraint {

	/**
	 * For each index i the number of times the value "values[i]" 
	 * occurs in the array "vars" should be cardMin[i] and cardMax[i] (inclusive) 
	 * @param vars array of constrained integer variables
	 * @param values array of integer values within domain of all vars
	 * @param cardMin array of integers that serves as lower bounds for values[i]
	 * @param cardMax array of integers that serves as upper bounds for values[i]
	 * Note that arrays values, cardMin, and cardMax should have the same size 
	 * otherwise a RuntimeException will be thrown
	 */
	public GlobalCardinality(Var[] vars, int[] values, int[] cardMin, int[] cardMax) {
		super(vars[0].getProblem());
		if (cardMin.length != values.length || cardMax.length != values.length) {
			throw new RuntimeException("GlobalCardinality error: arrays values, cardMin and cardMax do not have same size");
		}
		int min = cardMin[0];
		int max = cardMax[0];
		for(int i = 0; i < cardMin.length; i++){
			if(cardMin[i] > cardMax[i]) {
				throw new ModelException("GlobalCardinality error: cardMin["+i+"] <= cardMax["+i+"]");
			}
			if (cardMin[i] < min)
				min = cardMin[i];
			if (cardMax[i] > max)
				max = cardMax[i];
		}
		Problem p = (Problem) getProblem();
		IntegerVariable[] chocoVars = p.createIntVarArray(vars);
		// Choco assumes that integers within array values are sorted from min to max without "holes" ??
		Constraint chocoConstraint = Choco.globalCardinality(chocoVars, min, max, cardMin, cardMax);
		setImpl(chocoConstraint);
	}

	/**
     * @param vars array of constrained integer variables
     * @param values array of integer values within domain of all vars
     * @param cardinalityVars array of cardinality variables
     */
    public GlobalCardinality(Var[] vars, int[] values, Var[] cardinalityVars) {
		super(vars[0].getProblem());
        // We assume that the array of values is sorted in increasing order
        int offset = values[0];
        //TODO: add controls on parameters
		Problem p = (Problem) getProblem();
		IntegerVariable[] chocoVars = p.createIntVarArray(vars);
        IntegerVariable[] chocoCardinalityVars = p.createIntVarArray(cardinalityVars);
		// Choco assumes that integers within array values are sorted from min to max without "holes" ??
        // => yes, holes must be expressed using external constraints
        // => it has been patched in the current version
        Constraint chocoConstraint = Choco.globalCardinality(chocoVars, values, chocoCardinalityVars);
		setImpl(chocoConstraint);
	}

    /**
     * @param vars array of constrained integer variables
     * @param cardinalityVars array of cardinality variables
     */
    public GlobalCardinality(Var[] vars, Var[] cardinalityVars) {
		super(vars[0].getProblem());
        // We assume that the array of values is sorted in increasing order
        int offset = 0;
        //TODO: add controls on parameters
		Problem p = (Problem) getProblem();
		IntegerVariable[] chocoVars = p.createIntVarArray(vars);
        IntegerVariable[] chocoCardinalityVars = p.createIntVarArray(cardinalityVars);
		// Choco assumes that integers within array values are sorted from min to max without "holes" ??
        // => yes, holes must be expressed using external constraints
		Constraint chocoConstraint = Choco.globalCardinality(chocoVars, chocoCardinalityVars, offset);
		setImpl(chocoConstraint);
	}

	
	/**
	 * This method is used to post the constraint. Additionally to post() 
	 * this methods specifies a particular level of consistency that will
	 * be selected by an implementation to control the propagation strength of
	 * this constraint. If this method is not overloaded by an implementation, it will work as a post(). 
	 * @param consistencyLevel consistency level
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post(ConsistencyLevel consistencyLevel) {
		String option = "cp:ac"; // default
		if (consistencyLevel.equals(ConsistencyLevel.BOUND)) {
			option = "cp:bc";
		}
		Problem p = (Problem)getProblem();
		try {
			Constraint constraint = (Constraint) getImpl();
            constraint.addOption(option);
			p.addChocoConstraint(constraint);
		} catch (Exception e) {
			String msg = "Failure to post constraint: " + getName();
			getProblem().log(msg);
			throw new RuntimeException(msg);
		}
	}

	public void post() {
		String option = "cp:ac"; // default
		Problem p = (Problem)getProblem();
		try {
			Constraint constraint = (Constraint) getImpl();
            constraint.addOption(option);
			p.addChocoConstraint(constraint);
		} catch (Exception e) {
			String msg = "Failure to post constraint: " + getName();
			getProblem().log(msg);
			throw new RuntimeException(msg);
		}
    }
	
}
