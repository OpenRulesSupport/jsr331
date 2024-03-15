package org.jcp.jsr331.samples;

import javax.constraints.Var;
import javax.constraints.impl.AbstractConstraint;

/**
 * This constraint represents the following solver specific non-equality
 * constraints:
 * <ul>
 * <li>var1 != var2
 * <li>var != value
 * <ul>
 * */

public class ConstraintVarNeValue extends AbstractConstraint {

	Var var;
	int value;

	// Propagator handler;

	/**
	 * Constructs the constraint with the specified variables and constant.
	 * 
	 * @param var
	 *            the Var variable
	 * @param value
	 *            the search constant used in the inequality.
	 */
	public ConstraintVarNeValue(Var var, int value) {
		super(var.getProblem(), "ConstraintVarNeValue");
		this.var = var;
		this.value = value;
		// handler = new MyPropagator();
	}

	@Override
	public void post() {
		try {
			System.out.println("ConstraintVarNeValue before remove: "+var + " domainsize="+var.getDomainSize());
			getProblem().post(var,"!=",value);
			System.out.println("ConstraintVarNeValue after remove: "+var);
		} catch (Exception e) {
			System.out.println("ConstraintVarNeValue: value "+value + " is not in domain of " + var);
			throw new RuntimeException(e);
		}
	}

	// /**
	// * reacts on any event
	// */
	// class MyPropagator extends PropagatorI {
	// @Override
	// public void propagate(PropagationEvent event) throws Exception{
	// if (var.getValue() == value)
	// throw new Exception("Failure for constraint " + getName());
	// }
	// }

	public String toString() {
		return var.toString() + " != " + value;
	}

}
