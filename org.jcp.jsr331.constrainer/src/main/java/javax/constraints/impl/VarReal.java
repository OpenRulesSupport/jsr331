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
package javax.constraints.impl;

/**
 * This interface represents constrained integer variables, the most frequently
 * used type of constrained objects.
 *
 */

import javax.constraints.DomainType;
import javax.constraints.Problem;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;
import javax.constraints.impl.AbstractVarReal;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.FloatExp;

public class VarReal extends AbstractVarReal implements javax.constraints.VarReal {
	
	Constrainer getConstrainer() {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		return p.getConstrainer();
	}

	public VarReal(Problem problem, FloatExp expression) {
		super(problem); //expression.name());
		setImpl(expression);
	}
	
	public VarReal(Problem problem, String name, double min, double max) {
		super(problem, name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		try {
			setImpl(getConstrainer().addFloatVar(min, max, name));
			setMin(min);
			setMax(max);
		} catch (Exception e) {
			p.log("Invalid domain bounds for VarReal: [" + min + ";"	+ max + "]\n" + e);
		}
	}
	
	public final  void setMin(double min) throws Exception {
		FloatExp myVar = (FloatExp) getImpl();
		myVar.setMin(min);
	}

	public final double getMin() {
		FloatExp myVar = (FloatExp) getImpl();
		return myVar.min();
	} 

	public final void removeValue(double value) throws Exception {
		FloatExp myVar = (FloatExp) getImpl();
		myVar.removeRange(value,value);
	}

	public final double getDomainSize() {
		FloatExp myVar = (FloatExp) getImpl();
		return myVar.size();
	}

	public final void setMax(double max) throws Exception {
		FloatExp myVar = (FloatExp) getImpl();
		myVar.setMax(max);
	}

	/**
	 * @return current maximum for the domain of this variable
	 */
	public final double getMax() {
		FloatExp myVar = (FloatExp) getImpl();
		return myVar.max();
	}

	/**
	 * @return true if the domain of the variable contains only one value
	 */
	public final boolean isBound() {
		FloatExp myVar = (FloatExp) getImpl();
		return myVar.bound();
	}
	
	//=======================================
	// Arithmetic operators
	//=======================================
	
	/**
	 * @return this + value
	 */
	public VarReal plus(double value) {
		FloatExp myVar = (FloatExp) getImpl();
		return new VarReal(getProblem(), myVar.add(value));
	}

	/**
	 * @return this + var
	 */
	public VarReal plus(javax.constraints.Var var) {
		FloatExp myVar = (FloatExp) getImpl();
		return new VarReal(getProblem(),myVar.add((FloatExp)var.getImpl()));
	}

	/**
	 * @return this * value
	 */
	public VarReal multiply(double value) {
		FloatExp myVar = (FloatExp) getImpl();
		return new VarReal(getProblem(),myVar.mul(value));
	}

	/**
	 * @return this * var
	 */
	public VarReal multiply(javax.constraints.Var var) {
		FloatExp myVar = (FloatExp) getImpl();
		return new VarReal(getProblem(),myVar.mul((FloatExp)var.getImpl()));
	}

	/**
	 * @return this / value
	 */
	public VarReal divide(double value) {
		FloatExp myVar = (FloatExp) getImpl();
		return new VarReal(getProblem(),myVar.div(value));
	}

	/**
	 * @return this / var
	 */
	public VarReal divide(javax.constraints.Var var) throws Exception {
		FloatExp myVar = (FloatExp) getImpl();
		return new VarReal(getProblem(),myVar.div((FloatExp)var.getImpl()));
	}

	/**
	 * @return -this
	 */
	public VarReal negative() {
		FloatExp myVar = (FloatExp) getImpl();
		return new VarReal(getProblem(),myVar.neg());
	}

	/**
	 * @return an absolute value of this
	 */
	public VarReal abs() {
		FloatExp myVar = (FloatExp) getImpl();
		return new VarReal(getProblem(),myVar.abs());
	}


	/**
	 * @return this in power of value where value more or equals to 0
	 */
	public VarReal power(double value) {
		FloatExp myVar = (FloatExp) getImpl();
		try {
			return new VarReal(getProblem(),myVar.pow(value));
		} catch (Failure e) {
			throw new RuntimeException("Exception trying to power "+this+" using "+value,e);
		}
	}
	
//	/**
//	 * Returns a number of constraints that constrain this variable
//	 * @return integer or -1 if unknown
//	 */
//	public int getNumberOfConstraints() {
//		FloatExp myVar = (FloatExp) getImpl();
//		return myVar.observers().size();
//	}
	
	/**
	 * This method associates a custom Propagator with an "event"
	 * related to changes in the domain of a constrained variable "var". It
	 * forces the solver to keep an eye on these events and invoke the
	 * Propagator "propagator" when these events actually occur. When such events
	 * occur, the Propagator's method propagate() will be executed.
	 * 
	 * @param propagator
	 *            the Propagator we wish to associate with events on the
	 *            variable.
	 * @param event
	 *            the events that will trigger the invocation of the
	 *            Propagator.
	 */
	public void addPropagator(Propagator propagator, PropagationEvent event) {
			ConstrainerPropagator observer = new ConstrainerPropagator(propagator, event);
			FloatExp myVar = (FloatExp) getImpl();
			myVar.attachObserver(observer);
	}

}
