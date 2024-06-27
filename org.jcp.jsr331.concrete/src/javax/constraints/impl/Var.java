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
 * An implementation of the interface "Var"
 * @author J.Feldman
 */

import choco.Choco;
import choco.Options;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;

import javax.constraints.DomainType;
import javax.constraints.Problem;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;


public class Var extends AbstractVar implements javax.constraints.Var {

	Model getChocoModel() {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		return p.getChocoModel();
	}

	public Var(Problem problem, IntegerVariable expression) {
		super(problem,expression.toString());
		setImpl(expression);
//		getProblem().getChocoSolver().read(getChocoModel()); ??
	}

	public Var(Problem problem, String name, int min, int max) {
		super(problem, name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		String myType = chocoDomainType(p.getDomainType());
		try {
			IntegerVariable cVar = Choco.makeIntVar(name, min, max, myType);
			getChocoModel().addVariable(cVar);
			setImpl(cVar);
			setMin(min);
			setMax(max);
		} catch (Exception e) {
			p.log("Invalid domain bounds for Var: [" + min + ";"	+ max + "]\n" + e);
		}
	}

	public Var(Problem problem, String name, int[] domain) {
		super(problem, name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)problem;
		String myType = chocoDomainType(p.getDomainType());

		int max = Integer.MIN_VALUE+1;
		int min = Integer.MAX_VALUE-1;
		for (int i = 0; i < domain.length; i++) {
			int v = domain[i];
			if (min > v)
				min = v;
			if (max < v)
				max = v;
		}

		try {
			IntegerVariable cVar = Choco.makeIntVar(name, min, max, myType);
			getChocoModel().addVariable(cVar);
			setImpl(cVar);
			setMin(min);
			setMax(max);
			if(domain.length <= Math.abs(max - min)) {
				int counter = 1;
				for (int i = min+1; i < max; i++) {
					if(domain[counter] != i){
						try{
							cVar.removeVal(i);
						}catch (Exception e) {
							p.log("value " + i + "can not be removed from " + cVar);
						}
					}
					else
						counter++;
				}
			}
		} catch (Exception e) {
			p.log("Invalid domain bounds for Var: [" + min + ";"	+ max + "]\n" + e);
		}
	}

	static String chocoDomainType(DomainType type) {

		String chocoDomainType;
		if (type == DomainType.DOMAIN_SMALL)
			chocoDomainType = Options.V_ENUM; // "cp:enum";
		else if (type == DomainType.DOMAIN_SPARSE)
			chocoDomainType = Options.V_BLIST;
		else if (type == DomainType.DOMAIN_MIN_MAX)
			chocoDomainType = Options.V_BOUND; //"cp:bound";
//		else if (type == DomainType.AUTOMATIC)
//			.....
		else
			chocoDomainType = Options.NO_OPTION; //""=> let choco chooses
		return chocoDomainType;
	}

	/**
	 * Changes the domain type of this variable. Setting domain type
	 * AFTER a variable was created allows to take into consideration
	 * posted constraints and other problem state specific information.
	 * To take advantage of such domain type as DOMAIN_AUTOMATIC
	 * this method should be overloaded by an implementation solver
	 * @param type
	 */
	public void setDomainType(DomainType type) {
		if (domainType == type)
			return;
		// domainType = type;
	}

	/**
	 * Returns true if the domain of this variable contains the value.
	 * @return true if the value is in the domain of this variable
	 */
	public boolean contains(int value) {
		// TO DO
//		return getChocoVar().??
		if (value < getMin() || value > getMax())
			return false;
		return true;
	}

	/**
	 * Sets a new minimum for the domain of this variable. If min is less than
	 * the current min, a warning is produced and the setting is ignored. This
	 * method should be implemented by a concrete CP solver implementation.
	 *
	 * @param min
	 *            int
	 */
	public final  void setMin(int min) throws Exception {
		IntDomainVar var = getChocoDomainVar();
		if( var != null) {
			var.setInf(min);
		}
		else {
			getChocoVar().setLowB(min); // ??
		}
	}

	/**
	 * @return current minimum for the domain of this variable
	 */
	public final int getMin() {
		IntDomainVar var = getChocoDomainVar();
		if( var != null) {
			return var.getInf();
		}
		else {
			return getChocoVar().getLowB(); // ??
		}
	}

	/**
	 * Removes a value from the domain of this variable. May throw an exception.
	 *
	 * @param value
	 *            int
	 */
	public final void removeValue(int value) throws Exception {
		IntDomainVar var = getChocoDomainVar();
		if( var != null) {
			var.remVal(value);
		}
		else {
			getChocoVar().removeVal(value); // ??
		}
	}

	/**
	 * The default implementation is:
	 *   return getMax() - getMin() + 1;
	 * This method is better to be redefined to take into consideration an actual
	 * domain implementation.
	 * @return the current number of values inside the domain of this variable
	 */
	public final int getDomainSize() {
		IntDomainVar var = getChocoDomainVar();
		if( var != null) {
			return var.getDomainSize();
		}
		else {
			return getChocoVar().getDomainSize(); // ??
		}
	}

	/**
	 * Sets a new maximum for the domain of this variable. If max is more than
	 * the current max, a warning is produced and the setting is ignored. This
	 * method should be implemented by a concrete CP solver implementation.
	 *
	 * @param max
	 *            int
	 */
	public final void setMax(int max) throws Exception {
		IntDomainVar var = getChocoDomainVar();
		if( var != null) {
			var.setSup(max);
		}
		else {
			getChocoVar().setUppB(max); // ??
		}
	}

	/**
	 * @return current maximum for the domain of this variable
	 */
	public final int getMax() {
		IntDomainVar var = getChocoDomainVar();
		if( var != null) {
			return var.getSup();
		}
		else {
			return getChocoVar().getUppB();
		}
	}

	public final IntegerVariable getChocoVar() {
//		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		IntegerVariable cVar = (IntegerVariable)getImpl();
		return cVar;
	}

	public final IntDomainVar getChocoDomainVar() {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		if (p.isSolverCreated() == false)
			return null;
		IntegerVariable cVar = (IntegerVariable)getImpl();
		IntDomainVar domainVar = p.getChocoSolver().getVar(cVar);
		if (domainVar == null) {
			p.error("ERROR: Invalid attempt to get InDomainVar from Var " + getName());
		}
		return domainVar;
	}

	/**
	 * @return true if the domain of the variable contains only one value
	 */
	public final boolean isBound() {
		IntDomainVar var = getChocoDomainVar();
		if( var != null) {
			return var.isInstantiated();
		}
		else {
			return getMin() == getMax(); // ??
		}
	}

	//=======================================
	// Arithmetic operators
	//=======================================

	/**
	 * @return this + value
	 */
	public javax.constraints.Var plus(int value) {
//		int min = getMin()+value;
//		int max = getMax()+value;
		IntegerVariable sum = Choco.makeIntVar(getName()+"+"+value,Options.V_BOUND); //, min, max);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		p.addChocoConstraint(Choco.eq(sum, Choco.plus(getChocoVar(), value)));
		return new Var(getProblem(),sum);
	}

	/**
	 * @return this + var
	 */
	public javax.constraints.Var plus(javax.constraints.Var var) {
//		int min = getMin()+var.getMin();
//		int max = getMax()+var.getMax();
		IntegerVariable sum = Choco.makeIntVar(getName()+"+"+var.getName(),Options.V_BOUND); //, min, max);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		Var implVar = (Var)var;
		p.addChocoConstraint(Choco.eq(sum, Choco.plus(getChocoVar(), implVar.getChocoVar())));
		Var sumVar = new Var(getProblem(),sum);
		//p.linear(this, var, "=", sumVar).post();
		return sumVar;
	}

	/**
	 * @return this * value
	 */
	public javax.constraints.Var multiply(int value) {
//		int min = getMin() * value;
//		int max = getMax() * value;
//		int tmin;
//		if(min > max){
//			tmin = min;
//			min = max;
//			max = tmin;
//		}
		IntegerVariable product = Choco.makeIntVar("mul",Options.V_BOUND); //, min, max);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		p.addChocoConstraint(Choco.eq(product, Choco.mult(value, getChocoVar())));
		return new Var(getProblem(),product);
	}

	/**
	 * @return this * var
	 */
	public javax.constraints.Var multiply(javax.constraints.Var var) {
//		int min = Integer.MAX_VALUE;
//		int max = Integer.MIN_VALUE;
//		int[] values = new int[4];
//		values[0] = getMin()*var.getMax();
//		values[1] = getMax()*var.getMin();
//		values[2] = getMax()*var.getMax();
//		values[3] = getMin()*var.getMin();
//		for(int i = 0; i < values.length; i++){
//			if(values[i] < min)
//				min = values[i];
//			if(values[i] > max)
//				max = values[i];
//		}

		IntegerVariable product = Choco.makeIntVar("mul",Options.V_BOUND); //, min, max);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		p.addChocoConstraint(Choco.times(getChocoVar(), (IntegerVariable)var.getImpl(), product));
		return new Var(getProblem(),product);
	}

	/**
	 * @return this / value
	 */
	public javax.constraints.Var divide(int value) {
		if (value == 0) {
			throw new IllegalArgumentException("div(Var var, int value): value == 0");
		} else if (value == 1) {
			return this;
		} else {
//			int min, max;
//			if (value > 0) {
//				min = this.getMin()/value;
//				max = this.getMax()/value;
//			} else {
//				min = this.getMax()/value;
//				max = this.getMin()/value;
//			}
			IntegerVariable quotient = Choco.makeIntVar("quotient"); //, min, max);
			javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
			p.addChocoConstraint(Choco.eq(getChocoVar(), Choco.mult(value, quotient)));
			return new Var(getProblem(),quotient);
		}
	}

	/**
	 * @return this / var
	 */
	public javax.constraints.Var divide(javax.constraints.Var var) throws Exception {
//		int min = Integer.MAX_VALUE;
//		int max = Integer.MIN_VALUE;
//		int[] values = new int[4];
//		values[0] = getMin()/var.getMax();
//		values[1] = getMax()/var.getMin();
//		values[2] = getMax()/var.getMax();
//		values[3] = getMin()/var.getMin();
//		for(int i = 0; i < values.length; i++){
//			if(values[i] < min)
//				min = values[i];
//			if(values[i] > max)
//				max = values[i];
//		}

		IntegerVariable quotient = Choco.makeIntVar("quotient"); //, min, max);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		p.addChocoConstraint(Choco.times((IntegerVariable)var.getImpl(), quotient, getChocoVar()));
		return new Var(getProblem(),quotient);
	}


	/**
	 * @return an absolute value of this
	 */
	public javax.constraints.Var abs() {
		int maxVal = Math.max(Math.abs(getMin()), Math.abs(getMax()));
		IntegerVariable var = Choco.makeIntVar("var", 0, maxVal);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		p.addChocoConstraint(Choco.abs(var, getChocoVar()));
		return new Var(getProblem(),var);
	}


//	/**
//	 * Returns a number of constraints that constrain this variable
//	 * @return integer or -1 if unknown
//	 */
//	public int getNumberOfConstraints() {
//		return getChocoVar().getNbConstraint(getChocoModel());
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
	 * @param var
	 *            the constrained variable we wish to add an Propagator to.
	 * @param event
	 *            the events that will trigger the invocation of the
	 *            Propagator.
	 */
	public void addPropagator(Propagator propagator, PropagationEvent event) {
//			ChocoPropagator chocoPropagator = new ChocoPropagator(propagator, event);
//			getChocoVar.attachObserver(chocoPropagator);
//			chocoSolver.post(new ChocoObserver(var, handler, event));
	}

}
