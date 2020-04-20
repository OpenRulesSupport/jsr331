package javax.constraints.impl.constraint;

import java.util.ArrayList;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;
import javax.constraints.impl.Var;

import jsetl.ConstraintClass;
import jsetl.IntLVar;

/**
 * Represents a classic set of constraints over integer logical variables, the 
 * linear constraints (e.g. X less 3 or X = Y).
 * 
 * @author Fabio Biselli
 *
 */
public class Linear extends Constraint {
	
	/**
	 * Auxiliary vars.
	 */
	ArrayList<jsetl.IntLVar> variables;
	
	private static final int OPER_EQ = 1, OPER_UNKNOWN = 0, OPER_NEQ = 2, 
	OPER_LT = 3, OPER_LEQ = 4, OPER_GT = 5, OPER_GEQ = 6;

	/**
	 * Build a new ConstraintClass that constrain the variable
	 * <code>var1</code> to the integer variable <code>var2</code> with the 
	 * operator <code>oper</code>.
	 * 
	 * @param var1 the first integer variable
	 * @param var2 the second integer variable
	 * @param oper the string representing the operator 
	 */
	public Linear(
			javax.constraints.Var var1, 
			String oper, 
			javax.constraints.Var var2) {
		super(var1.getProblem());
		IntLVar v = ((Var) var1).getIntLVar();
		IntLVar value = ((Var) var2).getIntLVar();
		switch(getOperator(oper)) {
		case 1: {
			// Case = "equals".
			ConstraintClass constraint = v.eq(value);
			setImpl(constraint);
		} break;
		case 2: {
			// Case != "not equals".
			ConstraintClass constraint = v.neq(value);
			setImpl(constraint);
		} break;
		case 3: {
			// Case < "less".
			ConstraintClass constraint = v.lt(value);
			setImpl(constraint);
		} break;
		case 4: {
			// Case <= "less equals".
			ConstraintClass constraint = v.le(value);
			setImpl(constraint);
		} break;
		case 5: {
			// Case > "greater".
			ConstraintClass constraint = v.gt(value);
			setImpl(constraint);
		} break;
		case 6: {
			// Case >= "greater equals".
			ConstraintClass constraint = v.ge(value);
			setImpl(constraint);
		} break;
		default: throw new UnsupportedOperationException();
		}
		variables = new ArrayList<jsetl.IntLVar>();
		variables.add(v);
		variables.add(value);
	}
	
	/**
	 * Build a new ConstraintClass that constrain the variable given to
	 * the integer <code>value</code> with the operator <code>oper</code>.
	 * 
	 * @param var the integer variable
	 * @param value the integer value
	 * @param oper the string representing the operator 
	 */
	public Linear(
			javax.constraints.Var var, 
			String oper, 
			int value) {
		super(var.getProblem());
		IntLVar v = ((Var) var).getIntLVar();
		switch(getOperator(oper)) {
		case 1: {
			// Case = "equals".
			ConstraintClass constraint = v.eq(value);
			setImpl(constraint);
		} break;
		case 2: {
			// Case != "not equals".
			ConstraintClass constraint = v.neq(value);
			setImpl(constraint);
		} break;
		case 3: {
			// Case < "less".
			ConstraintClass constraint = v.lt(value);
			setImpl(constraint);
		} break;
		case 4: {
			// Case <= "less equals".
			ConstraintClass constraint = v.le(value);
			setImpl(constraint);
		} break;
		case 5: {
			// Case > "greater".
			ConstraintClass constraint = v.gt(value);
			setImpl(constraint);
		} break;
		case 6: {
			// Case >= "greater equals".
			ConstraintClass constraint = v.ge(value);
			setImpl(constraint);
		} break;
		default: throw new UnsupportedOperationException();
		}
		variables = new ArrayList<jsetl.IntLVar>();
		variables.add(v);
	}
	
	/**
	 * Build a new ConstraintClass based on the linear expression that is the
	 * sum of the constrained variables <code>vars[i]</code> multiplied for 
	 * the integer coefficient <code>array[i]</code>, constrained to the given 
	 * <code>value</code> with the operator <code>oper</code>.
	 * 
	 *  <code>array[0]*vars[0] + array[1]*vars[1] + ... + 
	 *  array[n]*vars[n] 
	 *  oper value</code>
	 *  The constraint will not be added to the problem.
	 *  
	 *  @param array the coefficient of the linear expression
	 *  @param vars the array of integer variables
	 *  @param oper the string representing the operator 
	 *  @param value the integer value.
	 */
	public Linear(
			int[] array, 
			javax.constraints.Var[] vars, 
			String oper, 
			int value) {
		super(vars[0].getProblem());
		if (array.length != vars.length 
				|| array.length == 0)
			throw new RuntimeException(
				"Coefficent and variable length must be equal and not zero.");
		IntLVar[] intVars = new IntLVar[array.length];
		intVars[0] = 
			((Var)  vars[0]).getIntLVar().mul(
					array[0]);
		for (int i = 1; i < array.length; i++) {
			IntLVar tmp = 
				((Var) vars[i]).getIntLVar().mul(
						array[i]);
			intVars[i] = intVars[i-1].sum(tmp);
		}
		IntLVar v = intVars[vars.length-1];
		switch(getOperator(oper)) {
		case 1: {
			// Case = "equals".
			ConstraintClass constraint = v.eq(value);
			setImpl(constraint);
		} break;
		case 2: {
			// Case != "not equals".
			ConstraintClass constraint = v.neq(value);
			setImpl(constraint);
		} break;
		case 3: {
			// Case < "less".
			ConstraintClass constraint = v.lt(value);
			setImpl(constraint);
		} break;
		case 4: {
			// Case <= "less equals".
			ConstraintClass constraint = v.le(value);
			setImpl(constraint);
		} break;
		case 5: {
			// Case > "greater".
			ConstraintClass constraint = v.gt(value);
			setImpl(constraint);
		} break;
		case 6: {
			// Case >= "greater equals".
			ConstraintClass constraint = v.ge(value);
			setImpl(constraint);
		} break;
		default: throw new UnsupportedOperationException();
		}
		variables = new ArrayList<jsetl.IntLVar>();
		variables.add(v);
		for (javax.constraints.Var var : vars)
			variables.add((IntLVar) var.getImpl());	
	}
	
	/**
	 * Build a new ConstraintClass based on the linear expression that is the
	 * sum of the constrained variables <code>vars[i]</code> multiplied for 
	 * the integer coefficient <code>array[i]</code>, constrained to the given
	 * integer variable <code>var</code> with the operator <code>oper</code>.
	 * 
	 *  <code>array[0]*vars[0] + array[1]*vars[1] + ... + array[n]*vars[n] 
	 *  oper var</code>
	 *  The constraint will not be added to the problem.
	 *  
	 *  @param array the coefficient of the linear expression
	 *  @param vars the array of integer variables
	 *  @param oper the string representing the operator 
	 *  @param var the integer variable.
	 */
	public Linear(
			int[] array, 
			javax.constraints.Var[] vars, 
			String oper, 
			javax.constraints.Var var) {
		super(vars[0].getProblem());
		if (array.length != vars.length 
				|| array.length == 0)
			throw new RuntimeException(
				"Coefficent and variable length must be equal and not zero.");
		IntLVar[] intVars = new IntLVar[array.length];
		intVars[0] = 
			((Var)  vars[0]).getIntLVar().mul(
					array[0]);
		for (int i = 1; i < array.length; i++) {
			IntLVar tmp = 
				((Var) vars[i]).getIntLVar().mul(
						array[i]);
			intVars[i] = intVars[i-1].sum(tmp);
		}
		IntLVar v = intVars[vars.length-1];
		IntLVar value = ((Var) var).getIntLVar();
		switch(getOperator(oper)) {
		case 1: {
			// Case = "equals".
			ConstraintClass constraint = v.eq(value);
			setImpl(constraint);
		} break;
		case 2: {
			// Case != "not equals".
			ConstraintClass constraint = v.neq(value);
			setImpl(constraint);
		} break;
		case 3: {
			// Case < "less".
			ConstraintClass constraint = v.lt(value);
			setImpl(constraint);
		} break;
		case 4: {
			// Case <= "less equals".
			ConstraintClass constraint = v.le(value);
			setImpl(constraint);
		} break;
		case 5: {
			// Case > "greater".
			ConstraintClass constraint = v.gt(value);
			setImpl(constraint);
		} break;
		case 6: {
			// Case >= "greater equals".
			ConstraintClass constraint = v.ge(value);
			setImpl(constraint);
		} break;
		default: throw new UnsupportedOperationException();
		}
		variables = new ArrayList<jsetl.IntLVar>();
		variables.add(v);
		variables.add(value);
		for (javax.constraints.Var x : vars)
			variables.add((IntLVar) x.getImpl());
	}
	
	public void post() {
		if (variables.size() != 0) {
			jsetl.IntLVar[] array = new jsetl.IntLVar[variables.size()];
			for (int i = 0; i < array.length; i++) {
				array[i] = variables.get(i);
			}
			for (jsetl.IntLVar var : array) 
				((Problem) getProblem()).addAuxVariable(var);
			((Problem) getProblem()).post(this);
		}
	}

	/**
	 * Auxiliary method that handle the string representing operators and
	 * return an identifier value.
	 * 
	 * @param oper the string representing the operator 
	 * (<, <=, >, >=, = and !=).
	 * 
	 * @return an identifier for the operator. 
	 */
	private int getOperator(String oper) {
		if (oper == "=") return OPER_EQ;
		else if (oper == "!=") return OPER_NEQ;
		else if (oper == "<") return OPER_LT;
		else if (oper == "<=") return OPER_LEQ;
		else if (oper == ">") return OPER_GT;
		else if (oper == ">=") return OPER_GEQ;
		return OPER_UNKNOWN;
	}
	
	
	
}
