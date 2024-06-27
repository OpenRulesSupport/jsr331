package jsr331.biz;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006-2007
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/
/**
 * This class represents constrained integer variables, the most frequently used
 * type of constrained objects.
 *
 * @author JF
 *
 */

import javax.constraints.CSP;
import javax.constraints.Var;

final public class BizInt extends BizObject {

	private Var var;

	public BizInt(BizProblem csp) {
		this(csp, "int");
	}

	/**
	 * Constructs a var variable with the name "name " and domain
	 * [Integer.MIN_VALUE+1;Integer.MAX_VALUE-1]
	 *
	 * @param csp
	 *            a CorkProblem
	 * @param name
	 *            a String
	 */
	public BizInt(BizProblem csp, String name) {
		this(csp, name, Integer.MIN_VALUE+1, Integer.MAX_VALUE-1);
	}

	/**
	 * Constructs a BizInt variable with the name "name " and domain [min;max]
	 * and its underlying var variable
	 *
	 * @param problem
	 *            a BizProblem
	 * @param name a String
	 * @param min minimal int value of the variable's domain
	 * @param max maximal int value of the variable's domain
	 */
	public BizInt(BizProblem problem, String name, int min, int max) {
		super(problem, name);
		CSP csp = problem.getCsp();
		var = csp.addVar(name,min,max);
		saveDomain();
	}

	/**
	 * Constructs a BizInt variable with the name "name " based on
	 * already existing var variable
	 *
	 * @param problem
	 *            a BizProblem
	 * @param var a var
	 */
	public BizInt(BizProblem problem, Var var) {
		super(problem, var.getName());
		setVar(var);
	}

	public Var getVar() {
		return var;
	}

	public void setVar(Var var) {
		this.var = var;
	}

	/**
	 * This method should be defined by an adapter to a concrete solver
	 * implementation. It saves the current state of the object's domain
	 * as a String.
	 */
	@Override
	public void saveDomain() {
		setDomain(var.toString());
	}



	/**
	 * @return true if the domain of the variable contains only one value
	 */
	@Override
	public boolean isBound() {
		return var.isBound();
	}

	/**
	 * This method is used by a selected CP implementation to save the values
	 * of all variables after finding the problem solution
	 * @param value
	 */
	@Override
	public void saveValue() {
		setValue(""+var.getValue());
	}

	@Override
	public String toString() {

		return "name="+getName()+" var=" + var.toString() +
		" saved as="+getValue();
	}



}
