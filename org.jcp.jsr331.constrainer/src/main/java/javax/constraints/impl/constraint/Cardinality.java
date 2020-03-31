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
 * This constraint defines a new constrained integer variable "cardinality"
 * that is equal to the number of elements of the
 * array of variables "vars" which are bound to the "value" or "var" .
 *
 */

public class Cardinality extends Constraint {
	
	static final String name = "Cardinality";
	
	/**
	 * Example: cardinality(vars,cardValue) less than var
	 * @param vars array of variables
     * @param cardValue an cardinality integer value
     * @param oper an operator
     * @param var an integer variable
	 */
	public Cardinality(Var[] vars, int cardValue, String oper, Var var) {
		super(vars[0].getProblem(),name);
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray cVars = problem.getExpArray(vars);
		try {
			IntExp cardinality = constrainer.cardinality(cVars,cardValue);
			problem.defineConstraintImpl(this, cardinality, oper, var);
		} catch (Exception f) {
			throw new RuntimeException(
					"Failure to create constraint "+name);
		}
	}
	
	/**
	 * Example: cardinality(vars,cardVar) less than var
	 * @param vars array of variables
     * @param cardVar an cardinality variable
     * @param oper an operator
     * @param var an integer variable
	 */
	public Cardinality(Var[] vars, Var cardVar, String oper, Var var) {
		super(vars[0].getProblem(),name);
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray cVars = problem.getExpArray(vars);
		try {
			IntExp cVar = (IntExp) cardVar.getImpl();
			IntExp cardinality = constrainer.cardinality(cVars,cVar);
			problem.defineConstraintImpl(this, cardinality, oper, var);
		} catch (Exception f) {
			throw new RuntimeException(
					"Failure to create constraint "+name);
		}
	}
	
	/**
	 * Example: cardinality(vars,cardValue) less than value
	 * @param vars array of variables
	 * @param cardValue an cardinality integer value
	 * @param oper an operator
	 * @param value an integer value
	 */
	public Cardinality(Var[] vars, int cardValue, String oper, int value) {
		super(vars[0].getProblem(),name);
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray cVars = problem.getExpArray(vars);
		try {
			IntExp cardinality = constrainer.cardinality(cVars,cardValue);
			problem.defineConstraintImpl(this, cardinality, oper, value);
		} catch (Exception f) {
			throw new RuntimeException(
					"Failure to create constraint "+name);
		}
	}
	
	/**
	 * Example: cardinality(vars,cardVar) less than value
	 * @param vars array of variables
     * @param cardVar an cardinality integer variable
     * @param oper an operator
     * @param value an integer value
	 */
	public Cardinality(Var[] vars, Var cardVar, String oper, int value) {
		super(vars[0].getProblem(),name);
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray cVars = problem.getExpArray(vars);
		try {
			IntExp cVar = (IntExp) cardVar.getImpl();
			IntExp cardinality = constrainer.cardinality(cVars,cVar);
			problem.defineConstraintImpl(this, cardinality, oper, value);
		} catch (Exception f) {
			throw new RuntimeException(
					"Failure to create constraint "+name);
		}
	}
		
}
