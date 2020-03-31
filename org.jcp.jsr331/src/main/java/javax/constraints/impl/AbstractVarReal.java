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
 * This is a based class for constrained real variables. 
 * It is abstract and missing methods should be implemented 
 * by concrete reference implementations.
 *
 */

import javax.constraints.Constraint;
import javax.constraints.DomainType;
import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

abstract public class AbstractVarReal extends AbstractConstrainedVariable implements VarReal {
	
	//DomainType domainType;

	public AbstractVarReal(Problem problem) {
		super(problem);
	}

	public AbstractVarReal(Problem problem, String name) {
		super(problem, name);
	}

	/**
	 * @return double that is a value of this instantiated variable
	 * @throws RuntimeException
	 *             if this variable is not instantiated
	 */
	public double getValue() {
		if (!isBound())
			throw new java.lang.RuntimeException(
					"Attempt to getValue of non instantiated VarReal variable "
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
	 * @return this - value
	 */
	public VarReal minus(double value) {
		return plus(-value);
	}

	/**
	 * @return this - value
	 */
	public VarReal minus(int value) {
		return plus(-value);
	}

	/**
	 * @return this - var
	 */
	public VarReal minus(Var var) {
		return plus(var.multiply(-1));
	}

	/**
	 * @return this / value
	 */
	public VarReal divide(double value) {
		if (value == 0) {
			throw new RuntimeException("Attemt to use Var.div(0)");
		} else if (value == 1) {
			return this;
		} else if (value == -1) {
			return this.negative();
		} else {
			double min = getMin() * value;
			double max = getMax() * value;
			if (value < 0) {
				min = max;
				max = min;
			}
			VarReal result = getProblem().variableReal("", min, max);
//			this.eq(result.mul(value)).post();
			getProblem().post(this, "=", result.multiply(value));
			return result;
		}
	}
	
	/**
	 * @return this / value
	 */
	public VarReal divide(int value) {
		return divide((double) value);
	}
	
	/**
	 * @return this / var
	 */
	public VarReal divide(VarReal var) throws Exception {
		if (var.isBound() && var.getValue() == 0) {
			throw new Exception("Divisor " + var + "is instantiated by 0");
		} 
		
		VarReal result = getProblem().variableReal("",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
//		this.eq(result.mul(var)).post();
		getProblem().post(this, "=", result.multiply(var));
		return result;
	}
	
//	/**
//	 * @return this / var
//	 */
//	public VarReal divide(Var var) throws Exception {
//		
//	}

	/**
	 * @return remainder of this/value: mod(this,value)
	 */
	public VarReal mod(int value) {
		VarReal quotient = this.divide(value);
		return this.minus(quotient.multiply(value));
	}
	
	public String toString() {
		if (isBound())
			return getName() + "[" + getMin() + "]";
		else
			return getName() + "[" + getMin() + " .. " + getMax() + "]";
	}

	/**
	 * @return -this
	 */
	public VarReal negative() {
		return this.multiply(-1);
	}
	
	/**
	 * Returns a new VarReal that is constrained to be the product of this variable
	 *         and itself, i.e. this * this.
	 * @return a VarReal that represents: this * this.
	 */
	public VarReal sqr() {
		return this.multiply(this);
	}
	
	/**
	 * Returns a new VarReal that is constrained to be this variable raised to the power of the given value,
	 *         i.e. this^value.
	 * @param value the given value.
	 * @return a VarReal that represents: this^value.
	 */
	public VarReal power(int value) {
		VarReal var = this;
		for(int i = 1; i < value; i++){
			var = (VarReal)var.multiply(this);
		}
		return var;
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
		throw new RuntimeException("There is no implementation for the VarReal method addPropagator");
	}
	
	public int compareTo(VarReal var) {
		//return 1;
		throw new RuntimeException("The Java method compareTo should not be used for constrained variables");
	}
	
	//====
		public boolean contains(double value) {
			return value >= getMin() && value <= getMax();
		}

		public VarReal plus(int value) {
			return plus((double)value);
		}

		public VarReal plus(VarReal var) {
			throw new RuntimeException("There is no implementation for the VarReal method plus(VarReal var)");
		}
		
		@Override
		public VarReal plus(double value) {
			throw new RuntimeException("There is no implementation for the VarReal method plus(double value)");
		}
	
		@Override
		public VarReal plus(Var var) {
			throw new RuntimeException("There is no implementation for the VarReal method plus(Var var)");
		}
		
		public VarReal multiply(double value) {
			throw new RuntimeException("There is no implementation for the VarReal method multiply(int value)");
		}
		
		public VarReal multiply(int value) {
			return multiply((double)value);
		}
		
		public VarReal multiply(Var var) {
			throw new RuntimeException("There is no implementation for the VarReal method multiply(Var var)");
		}

		@Override
		public VarReal multiply(VarReal var) {
			throw new RuntimeException("There is no implementation for the VarReal method multiply(VarReal var)");
		}

		@Override
		public VarReal abs() {
			throw new RuntimeException("There is no implementation for the VarReal method abs()");
		}
		
		@Override
		public void removeRange(double min, double max) throws Exception {
			throw new RuntimeException("There is no implementation for the VarReal method removeRange(double min, double max)");
		}
		
		@Override
		public VarReal minus(VarReal var) {
			throw new RuntimeException("There is no implementation for the VarReal method minus(VarReal var)");
		}

		@Override
		public VarReal divide(Var var) throws Exception {
			throw new RuntimeException("There is no implementation for the VarReal method divide(Var var)");
		}

		@Override
		public VarReal power(double value) {
			throw new RuntimeException("There is no implementation for the VarReal method power(double value)");
		}

}
