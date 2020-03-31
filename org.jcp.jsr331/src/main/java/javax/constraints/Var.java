//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints;

/**
 * This interface represents constrained integer variables, the most frequently
 * used type of constrained objects.
 * 
 */

public interface Var extends ConstrainedVariable {

	/**
	 * Returns the current minimum value in the domain of this variable.
	 * 
	 * @return the current minimum value in the domain of this variable.
	 */
	public int getMin();

	/**
	 * Returns the current maximum value in the domain of this variable.
	 * 
	 * @return the current maximum value in the domain of this variable.
	 */
	public int getMax();

	/**
	 * Returns the value to which this variable has been instantiated, throws a
	 * RuntimeException if the variable is not instantiated.
	 * 
	 * @return the value of this instantiated variable.
	 * @throws RuntimeException
	 *             if this variable is not instantiated.
	 */
	public int getValue();

	/**
	 * Returns true if the domain of the variable contains only one value.
	 * 
	 * @return true if the domain of the variable contains only one value.
	 */
	public boolean isBound();

	/**
	 * Returns a string representation of the initial domain of this variable.
	 * 
	 * @return a string representation of the initial domain of this variable.
	 */
	public String getInitialDomain();

	/**
	 * Returns true if the domain of this variable contains the value.
	 * @return true if the value is in the domain of this variable
	 */
	public boolean contains(int value);

//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * equal to the given variable, i.e. this == var.
//	 * 
//	 * @param var
//	 *            the given variable.
//	 * @return a Constraint that represents: this == var.
//	 */
//	public Constraint eq(Var var);
//	
//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * equal to the given value i.e. this == value.
//	 * 
//	 * @param value
//	 *            the given value.
//	 * @return a Constraint that represents: this == value.
//	 */
//	public Constraint eq(int value);
//
//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * less than or equal to the given variable, i.e. this <= var.
//	 * 
//	 * @param var
//	 *            the given variable.
//	 * @return a Constraint that represents: this <= var.
//	 */
//	public Constraint le(Var var);
//
//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * less than or equal to the given value, i.e. this <= value.
//	 * 
//	 * @param value
//	 *            the given value.
//	 * @return a Constraint that represents: this <= value.
//	 */
//	public Constraint le(int value);
//
//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * less than the given variable, i.e. this < var.
//	 * 
//	 * @param var
//	 *            the given variable.
//	 * @return a Constraint that represents: this < var.
//	 */
//	public Constraint lt(Var var);
//
//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * less than the given value, i.e. this < value.
//	 * 
//	 * @param value
//	 *            the given value.
//	 * @return a Constraint that represents: this < value.
//	 */
//	public Constraint lt(int value);
//
//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * greater than or equal to the given variable, i.e. this >= var.
//	 * 
//	 * @param var
//	 *            the given variable.
//	 * @return a Constraint that represents: this >= var.
//	 */
//	public Constraint ge(Var var);
//
//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * greater than or equal to the given value, i.e. this >= value.
//	 * 
//	 * @param value
//	 *            the given value.
//	 * @return a Constraint that represents: this >= value.
//	 */
//	public Constraint ge(int value);
//
//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * greater than the given variable, i.e. this >= var.
//	 * 
//	 * @param var
//	 *            the given variable.
//	 * @return a Constraint that represents: this >= var.
//	 */
//	public Constraint gt(Var var);
//
//	/**
//	 * Returns a Constraint that represents that this variable must be
//	 * greater than the given value, i.e. this > value.
//	 * 
//	 * @param value
//	 *            the given value.
//	 * @return a Constraint that represents: this > value.
//	 */
//	public Constraint gt(int value);
//
//	/**
//	 * Returns a Constraint that represents that this variable must not
//	 * be equal to the given variable, i.e. this != var.
//	 * 
//	 * @param var
//	 *            the given variable.
//	 * @return a Constraint that represents: this != var.
//	 */
//	public Constraint neq(Var var);
//
//	/**
//	 * Returns a Constraint that represents that this variable must not
//	 * be equal to the given value, i.e. this != value.
//	 * 
//	 * @param value
//	 *            the given value.
//	 * @return a Constraint that represents: this != value.
//	 */
//	public Constraint neq(int value);

	/**
	 * Returns a new Var that is constrained to be the sum of this
	 * variable and the given value, i.e. this + value.
	 * 
	 * @param value
	 *            the given value.
	 * @return a Var that represents: this + value.
	 */
	public Var plus(int value);

	/**
	 * Returns a new Var that is constrained to be the sum of this
	 * variable and the given variable, i.e. this + var.
	 * 
	 * @param var
	 *            the given variable.
	 * @return a Var that represents: this + var.
	 */
	public Var plus(Var var);
	

	/**
	 * Returns a new Var that is constrained to be the difference of
	 * this variable and the given value, i.e. this - value.
	 * 
	 * @param value
	 *            the given value.
	 * @return a Var that represents: this - value.
	 */
	public Var minus(int value);

	/**
	 * Returns a new Var that is constrained to be the difference of
	 * this variable and the given variable, i.e. this - var.
	 * 
	 * @param var
	 *            the given variable.
	 * @return a Var that represents: this - var.
	 */
	public Var minus(Var var);

	/**
	 * Returns a new Var that is constrained to be the product of this
	 * variable and the given value, i.e. this * value.
	 * 
	 * @param value
	 *            the given value.
	 * @return a Var that represents: this * value.
	 */
	public Var multiply(int value);

	/**
	 * Returns a new Var that is constrained to be the product of this
	 * variable and the given variable, i.e. this * var.
	 * 
	 * @param var
	 *            the given variable.
	 * @return a Var that represents: this * var.
	 */
	public Var multiply(Var var);

	/**
	 * Returns a new Var that is constrained to be the division of this
	 * variable by the given value, i.e. this / value.
	 * 
	 * @param value
	 *            the given value.
	 * @return a Var that represents: this / value.
	 */
	public Var divide(int value);

	/**
	 * Returns a new Var that is constrained to be the division of this
	 * variable by the given variable, i.e. this / var.
	 * 
	 * @param var
	 *            the given variable.
	 * @return a Var that represents: this / var.
	 * @throws Exception
	 *             if dividing by zero.
	 */
	public Var divide(Var var) throws Exception;
	
	/**
	 * Returns a new Var that is constrained to be a "percent" of this variable.
	 * 
	 * @param percent the given percent.
	 * @return a Var that represents: percent of var.
	 */
	public Var percent(int percent);

	/**
	 * Returns a new Var that is constrained to be the remainder after
	 * performing integer division of this variable by the given value, i.e.
	 * mod(this,value).
	 * 
	 * @param value
	 *            the given value.
	 * @return a Var that represents: mod(this,value).
	 */
	public Var mod(int value);

	 /**
	 * Returns a new Var that is constrained to be the negation of
	 this variable, i.e. 0 - this.
	 * @return a Var that represents: 0 - this.
	 */
	 public Var negative();

	/**
	 * Returns a new Var that is constrained to be the absolute value
	 * of this variable.
	 * 
	 * @return a Var that represents the absolute value of this variable.
	 */
	public Var abs();

	/**
	 * Returns a new Var that is constrained to be the product of this
	 * variable and itself, i.e. this * this.
	 * 
	 * @return a Var that represents: this * this.
	 */
	public Var sqr();

	/**
	 * Returns a new Var that is constrained to be this variable raised
	 * to the power of the given value, i.e. this**value.
	 * 
	 * @param value
	 *            the given value.
	 * @return a Var that represents: this**value.
	 */
	public Var power(int value);

	/**
	 * Returns the current number of values inside the domain of this variable.
	 * 
	 * @return the current number of values inside the domain of this variable.
	 */
	public int getDomainSize();

	/**
	 * Returns the type of this variable's domain.
	 * 
	 * @return the type of this variable's domain.
	 */
	public DomainType getDomainType();
	
	/**
	 * Changes the domain type of this variable. Setting domain type
	 * AFTER a variable was created allows to take into consideration posted constraints
	 * and other problem state specific information.
	 * @param type
	 */
	public void setDomainType(DomainType type); 
	
	/**
	 * @return a VarReal variable equals to the current integer constrained
	 */
	public VarReal asReal();

	// /**
	// * Returns a Constraint that represents that this variable must
	// be equal to the given value
	// * i.e. this == value.
	// * @param value the given value.
	// * @return a Constraint that represents: this == value.
	// */
	// public Constraint eq(double value);

	// /**
	// * returns a Constraint that represents that this variable must
	// be equal to the real variable
	// * i.e. this == var.
	// * @param var the given real variable.
	// * @return a Constraint that represents: this == var.
	// */
	// public Constraint eq(VarReal var);

	// /**
	// * returns a Constraint that represents that this variable must
	// not be equal to the given value
	// * i.e. this != value.
	// * @param value the given value.
	// * @return a Constraint that represents: this != value.
	// */
	// public Constraint neq(double value);

	// /**
	// * returns a Constraint that represents that this variable must
	// not be equal to the real variable
	// * i.e. this != var.
	// * @param var the given real variable.
	// * @return a Constraint that represents: this != var.
	// */
	// public Constraint neq(VarReal var);

	// /**
	// * returns a new VarReal that is constrained to be the sum of
	// this variable
	// * and the given value, i.e. this + value.
	// * @param value the given value.
	// * @return a VarReal that represents: this + value.
	// */
	// public VarReal add(double value);

	// /**
	// * returns a new VarReal that is constrained to be the sum of
	// this variable
	// * and the given real variable, i.e. this + var.
	// * @param var the given real variable.
	// * @return a VarReal that represents: this + var.
	// */
	// public VarReal add(VarReal var);

	// /**
	// * returns a new VarReal that is constrained to be the difference
	// of this variable
	// * and the given value, i.e. this - value.
	// * @param value the given value.
	// * @return a VarReal that represents: this - value.
	// */
	// public VarReal sub(double value);

	// /**
	// * returns a new VarReal that is constrained to be the difference
	// of this variable
	// * and the given real variable, i.e. this - var.
	// * @param var the given real variable.
	// * @return a VarReal that represents: this - var.
	// */
	// public VarReal sub(VarReal var);

	// /**
	// * returns a new VarReal that is constrained to be the division
	// of this variable
	// * by the given value, i.e. this / value.
	// * @param value the given value.
	// * @return a VarReal that represents: this / value.
	// */
	// public VarReal div(double value);

	// /**
	// * returns a Constraint that represents that this variable must
	// be greater than
	// * or equal to the given real variable, i.e. this >= var.
	// * @param var the given real variable.
	// * @return a Constraint that represents: this >= var.
	// */
	// public Constraint ge(VarReal var);

	// /**
	// * returns a Constraint that represents that this variable must
	// be greater than
	// * or equal to the given real value, i.e. this >= value.
	// * @param value the given real value.
	// * @return a Constraint that represents: this >= value.
	// */
	// public Constraint ge(double value);

	// /**
	// * returns a Constraint that represents that this variable must
	// be greater than
	// * the given real variable, i.e. this >= var.
	// * @param var the given real variable.
	// * @return a Constraint that represents: this >= var.
	// */
	// public Constraint gt(VarReal var);

	// /**
	// * Returns a Constraint that represents that this variable must
	// be greater than
	// * the given real value, i.e. this > value.
	// * @param value the given real value.
	// * @return a Constraint that represents: this > value.
	// */
	// public Constraint gt(double value);

	// /**
	// * Returns a Constraint that represents that this variable must
	// be less than or
	// * equal to the given real variable, i.e. this <= var.
	// * @param var the given real variable.
	// * @return a Constraint that represents: this <= var.
	// */
	// public Constraint le(VarReal var);

	// /**
	// * Returns a Constraint that represents that this variable must
	// be less than or
	// * equal to the given real value, i.e. this <= value.
	// * @param value the given real value.
	// * @return a Constraint that represents: this <= value.
	// */
	// public Constraint le(double value);

	// /**
	// * Returns a Constraint that represents that this variable must
	// be less than
	// * the given real variable, i.e. this < var.
	// * @param var the given real variable.
	// * @return a Constraint that represents: this < var.
	// */
	// public Constraint lt(VarReal var);

	// /**
	// * Returns a Constraint that represents that this variable must
	// be less than
	// * the given real value, i.e. this < value.
	// * @param value the given real value.
	// * @return a Constraint that represents: this < value.
	// */
	// public Constraint lt(double value);

	// /**
	// * Returns a new VarReal that is constrained to be this variable
	// raised to the power of the given real value,
	// * i.e. this^value.
	// * @param value the given real value.
	// * @return a Var that represents: this^value.
	// */
	// public VarReal pow(double value);
	
//	public int compareTo(Var var);

}
