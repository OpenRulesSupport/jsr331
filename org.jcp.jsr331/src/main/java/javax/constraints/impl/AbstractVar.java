//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl;

/**
 * This is a based class for constrained integer variables. 
 * It is abstract and missing methods should be implemented 
 * by concrete reference implementations.
 *
 */

import javax.constraints.Constraint;
import javax.constraints.DomainType;
import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.VarReal;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

abstract public class AbstractVar extends AbstractConstrainedVariable implements Var {

	DomainType domainType;

	public AbstractVar(Problem problem) {
		super(problem);
	}

	public AbstractVar(Problem problem, String name) {
		super(problem, name);
	}


	/**
	 * @return domainType
	 */
	public DomainType getDomainType() {
		return domainType;
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
	 * @return int that is a value of this instantiated variable
	 * @throws RuntimeException
	 *             if this variable is not instantiated
	 */
	public int getValue() {
		if (!isBound())
			throw new java.lang.RuntimeException(
					"Attempt to getValue of non instantiated Var variable "
							+ getName());
		return getMin();
	}

	/**
	 * @return a string representing the initial domain of the variable
	 */
	public String getInitialDomain() {
		return "[" + getMin() + ";" + getMax() + "]";
	}

	/**
	 * Provides the default implementation as return getMax() - getMin() + 1;
	 * This method is better to be redefined to take into consideration an
	 * actual domain implementations.
	 * 
	 * @return the current number of values inside the domain of this variable
	 */
	public int getDomainSize() {
		return getMax() - getMin() + 1;
	}
	
	/**
	 * @return this - value
	 */
	public Var minus(int value) {
		return plus(-value);
	}

	/**
	 * @return this - var
	 */
	public Var minus(Var var) {
		return plus(var.multiply(-1));
	}

	/**
	 * @return this / value
	 */
	public Var divide(int value) {
		if (value == 0) {
			throw new RuntimeException("Attemt to use Var.div(0)");
		} else if (value == 1) {
			return this;
		} else if (value == -1) {
			return this.negative();
		} else {
			int min = getMin() * value;
			int max = getMax() * value;
			if (value < 0) {
				min = max;
				max = min;
			}
			Var result = getProblem().variable("", min, max);
//			this.eq(result.mul(value)).post();
			getProblem().post(this, "=", result.multiply(value));
			return result;
		}
	}
	
	/**
	 * @return this / var
	 */
	public Var divide(Var var) throws Exception {
		if (var.isBound() && var.getValue() == 0) {
			throw new Exception("Divisor " + var + "is instantiated by 0");
		} 
		
		Var result = getProblem().variable("",Integer.MIN_VALUE+1,Integer.MAX_VALUE-1); // JF  corrected MAX to MIN
//		this.eq(result.mul(var)).post();
		getProblem().post(this, "=", result.multiply(var));
		return result;
	}

	/**
	 * @return remainder of this/value: mod(this,value)
	 */
	public Var mod(int value) {
		Var quotient = this.divide(value);
		return this.minus(quotient.multiply(value));
	}
	
//	/**
//	 * Returns a number of constraints that constrain this variable
//	 * @return the number or -1
//	 */
//	public int getNumberOfConstraints() {
//		AbstractProblem p = (AbstractProblem)getProblem();
//		p.notImplementedException("Var's method getNumberOfConstraints");
//		return -1;
//	}



	public String toString() {
		if (getMin() == getMax())
			return getName() + "[" + getMin() + "]";
		else
			return getName() + "[" + getMin() + ".." + getMax() + "]";
	}

	/**
	 * @return -this
	 */
	public javax.constraints.Var negative() {
		return this.multiply(-1);
	}
	
	/**
	 * Returns a new Var that is constrained to be the product of this variable
	 *         and itself, i.e. this * this.
	 * @return a Var that represents: this * this.
	 */
	public Var sqr() {
		return this.multiply(this);
	}
	
	/**
	 * Returns a new Var that is constrained to be this variable raised to the power of the given value,
	 *         i.e. this^value.
	 * @param value the given value.
	 * @return a Var that represents: this^value.
	 */
	public Var power(int value) {
		Var var = this;
		for(int i = 1; i < value; i++){
			var = (Var)var.multiply(this);
		}
		return var;
	}
	
	/*
	 * Returns a VarReal variable equals to the current integer constrained
	 */
	public VarReal asReal() {
		throw new RuntimeException("There is no implementation for the method asReal()");
	}

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
		throw new RuntimeException("There is no implementation for the Var method addPropagator");
	}
	
	public int compareTo(Var var) {
		//return 1;
		throw new RuntimeException("The Java method compareTo should not be used for constrained variables");
	}
	
	//====
	public boolean contains(int value) {
		throw new RuntimeException("There is no implementation for the Var method contains(int value)");
	}

	public Var plus(int value) {
		throw new RuntimeException("There is no implementation for the Var method plus(int value)");
	}

	public Var plus(Var var) {
		throw new RuntimeException("There is no implementation for the Var method plus(Var var)");
	}
	
	public Var multiply(int value) {
		throw new RuntimeException("There is no implementation for the Var method multiply(int value)");
	}

	@Override
	public Var multiply(Var var) {
		throw new RuntimeException("There is no implementation for the Var method multiply(Var var)");
	}
	
	/**
	 * Returns a new Var that is constrained to be a "percent" of this variable.
	 * 
	 * @param percent
	 *            the given percent.
	 * @return a Var that represents: percent of var.
	 */
	public Var percent(int percent) {
		return this.multiply(percent).divide(100);
	}

	@Override
	public Var abs() {
		throw new RuntimeException("There is no implementation for the Var method abs()");
	}


}
