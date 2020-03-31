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
import javax.constraints.impl.AbstractVar;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntVar;

public class Var extends AbstractVar implements javax.constraints.Var {
	
	Constrainer getConstrainer() {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		return p.getConstrainer();
	}

	public Var(Problem problem, IntExp expression) {
		super(problem); //expression.name());
		setImpl(expression);
	}
	
	public Var(Problem problem, String name, int min, int max) {
		super(problem, name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		int myType = constrainerDomainType(p.getDomainType());
		try {
			setImpl(getConstrainer().addIntVar(min, max, name, myType));
			setMin(min);
			setMax(max);
		} catch (Exception e) {
			p.log("Invalid domain bounds for Var: [" + min + ";"	+ max + "]\n" + e);
		}
	}
	
	public Var(Problem problem, String name, int[] domain) {
		super(problem, name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)problem;
		int myType = constrainerDomainType(p.getDomainType());
		
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
			IntVar constranerVar = getConstrainer().addIntVar(min, max, name, myType);
			setImpl(constranerVar);
			setMin(min);
			setMax(max);
			if(domain.length <= Math.abs(max - min)) {
				int counter = 1;
				for (int i = min+1; i < max; i++) {
					if(domain[counter] != i){
						try{
							constranerVar.removeValue(i);
						}catch (Exception e) {
							p.log("value " + i + "can not be removed from " + constranerVar);
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
	
	int constrainerDomainType(DomainType type) {
		int myType;
		if (type == DomainType.DOMAIN_SMALL)
			myType = IntVar.DOMAIN_BIT_SMALL;
		else if (type == DomainType.DOMAIN_SPARSE)
			myType = IntVar.DOMAIN_PLAIN;
		else if (type == DomainType.DOMAIN_MIN_MAX)
			myType = IntVar.DOMAIN_PLAIN;
		else
			myType = IntVar.DOMAIN_BIT_SMALL;
		
		return myType;
	}
	
	/**
	 * Sets a new minimum for the domain of this variable. If min is less than
	 * the current min, a warning is produced and the setting is ignored. This
	 * method should be implemented by a concrete CP solver implementation.
	 * @param min integer
	 * @throws Exception if fails
	 */
	public final  void setMin(int min) throws Exception {
		IntExp myVar = (IntExp) getImpl();
		myVar.setMin(min);
	}

	/**
	 * @return current minimum for the domain of this variable
	 */
	public final int getMin() {
		IntExp myVar = (IntExp) getImpl();
		return myVar.min();
	}

	/**
	 * Removes a value from the domain of this variable. May throw an exception.
	 *
	 * @param value int
	 * @throws Exception if fails
	 */
	public final void removeValue(int value) throws Exception {
		IntExp myVar = (IntExp) getImpl();
		myVar.removeValue(value);
	}

	/**
	 * The default implementation is:
	 *   return getMax() - getMin() + 1;
	 * This method is better to be redefined to take into consideration an actual
	 * domain implementation.
	 * @return the current number of values inside the domain of this variable
	 */
	public final int getDomainSize() {
		IntExp myVar = (IntExp) getImpl();
		return myVar.size();
	}

	/**
	 * Sets a new maximum for the domain of this variable. If max is more than
	 * the current max, a warning is produced and the setting is ignored. This
	 * method should be implemented by a concrete CP solver implementation.
	 *
	 * @param max int
	 * @throws Exception if fails
	 */
	public final void setMax(int max) throws Exception {
		IntExp myVar = (IntExp) getImpl();
		myVar.setMax(max);
	}

	/**
	 * @return current maximum for the domain of this variable
	 */
	public final int getMax() {
		IntExp myVar = (IntExp) getImpl();
		return myVar.max();
	}

	/**
	 * @return true if the domain of the variable contains only one value
	 */
	public final boolean isBound() {
		IntExp myVar = (IntExp) getImpl();
		return myVar.bound();
	}
	
	/**
	 * Returns true if the domain of this variable contains the value.
	 * @return true if the value is in the domain of this variable
	 */
	public boolean contains(int value) {
		IntExp myVar = (IntExp) getImpl();
		return myVar.contains(value);
	}

	//=======================================
	// Arithmetic operators
	//=======================================
	
	/**
	 * @return this + value
	 */
	public javax.constraints.Var plus(int value) {
		IntExp myVar = (IntExp) getImpl();
		Var var = new Var(getProblem(), myVar.add(value));
		var.setName(getName()+"+"+value);
		return var;
	}

	/**
	 * @return this + var
	 */
	public javax.constraints.Var plus(javax.constraints.Var var) {
		IntExp myVar = (IntExp) getImpl();
		Var newVar = new Var(getProblem(),myVar.add((IntExp)var.getImpl()));
		newVar.setName(getName()+"+"+var.getName());
		return newVar;
	}

	/**
	 * @return this * value
	 */
	public javax.constraints.Var multiply(int value) {
		IntExp myVar = (IntExp) getImpl();
		return new Var(getProblem(),myVar.mul(value));
	}

	/**
	 * @return this * var
	 */
	public javax.constraints.Var multiply(javax.constraints.Var var) {
		IntExp myVar = (IntExp) getImpl();
		return new Var(getProblem(),myVar.mul((IntExp)var.getImpl()));
	}

	/**
	 * @return this / value
	 */
	public javax.constraints.Var divide(int value) {
		IntExp myVar = (IntExp) getImpl();
		return new Var(getProblem(),myVar.div(value));
	}

	/**
	 * @return this / var
	 */
	public javax.constraints.Var divide(javax.constraints.Var var) throws Exception {
		IntExp myVar = (IntExp) getImpl();
		return new Var(getProblem(),myVar.div((IntExp)var.getImpl()));
	}

	/**
	 * @return -this
	 */
	public javax.constraints.Var negative() {
		IntExp myVar = (IntExp) getImpl();
		return new Var(getProblem(),myVar.neg());
	}

	/**
	 * @return an absolute value of this
	 */
	public javax.constraints.Var abs() {
		IntExp myVar = (IntExp) getImpl();
		return new Var(getProblem(),myVar.abs());
	}

	/**
	 * @return this * this
	 */
	public javax.constraints.Var sqr() {
		IntExp myVar = (IntExp) getImpl();
		return new Var(getProblem(),myVar.sqr());
	}

	/**
	 * @return this in power of value where value more or equals to 0
	 */
	public javax.constraints.Var power(int value) {
		IntExp myVar = (IntExp) getImpl();
		return new Var(getProblem(),myVar.pow(value));
	}
	
//	/**
//	 * Returns a number of constraints that constrain this variable
//	 * @return integer or -1 if unknown
//	 */
//	public int getNumberOfConstraints() {
//		IntExp myVar = (IntExp) getImpl();
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
			IntExp exp = (IntExp)getImpl();
			exp.attachObserver(observer);
	}

}
