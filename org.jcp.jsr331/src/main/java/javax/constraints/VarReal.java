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
 * This interface represents constrained real variables.
 */

public interface VarReal extends ConstrainedVariable {
	
	/**
	 * Returns true if the domain of the variable contains only one value.
	 * @return true if the domain of the variable contains only one value.
	 */
	public boolean isBound();

	/**
	 * Returns the current minimum value in the domain of this variable.
	 * @return the current minimum value in the domain of this variable.
	 */
	public double getMin();

	/**
	 * Returns the current maximum value in the domain of this variable.
	 * @return the current maximum value in the domain of this variable.
	 */
	public double getMax();

	/**
	 * Returns the value to which this variable has been instantiated,
	 *         throws a RuntimeException if the variable is not instantiated.
	 * @return the value of this instantiated variable.
	 * @throws RuntimeException if this variable is not instantiated.
	 */
	public double getValue();

	/**
	 * Removes a range of values from the domain of this variable,
	 *         throws an exception if unsuccessful.
	 * @param min the minimum value of the range to be removed from the domain of this variable.
	 * @param max the maximum value of the range to be removed from the domain of this variable.
	 * @throws Exception if the value is not present in the domain of this variable.
	 */
	public void removeRange(double min, double max) throws Exception;

	/**
	 * Returns a string representation of the initial domain of this variable.
	 * @return a string representation of the initial domain of this variable.
	 */
	public String getInitialDomain();

//	/**
//	 * Returns the current size of the domain of this variable.
//	 * @return the current size of the domain of this variable.
//	 */
//	public double getDomainSize();

	/**
	 * Adds and returns a new VarReal that is constrained to be the sum of this variable
	 *         and the given value, i.e. this + value.
	 * @param value the given value.
	 * @return a VarReal that represents: this + value.
	 */
	public VarReal plus(double value);

	/**
	 * Adds and returns a new VarReal that is constrained to be the sum of this variable
	 *         and the given variable, i.e. this + var.
	 * @param var the given variable.
	 * @return a VarReal that represents: this + var.
	 */
	public VarReal plus(VarReal var);

	/**
	 * Adds and returns a new VarReal that is constrained to be the sum of this variable
	 *         and the given value, i.e. this + value.
	 * @param value the given value.
	 * @return a VarReal that represents: this + value.
	 */
	public VarReal plus(int value);

	/**
	 * Adds and returns a new VarReal that is constrained to be the sum of this variable
	 *         and the given variable, i.e. this + var.
	 * @param var the given variable.
	 * @return a VarReal that represents: this + var.
	 */
	public VarReal plus(Var var);

	/**
	 * Adds and returns a new VarReal that is constrained to be the difference of this variable
	 *         and the given value, i.e. this - value.
	 * @param value the given value.
	 * @return a VarReal that represents: this - value.
	 */
	public VarReal minus(double value);

	/**
	 * Adds and returns a new VarReal that is constrained to be the difference of this variable
	 *         and the given variable, i.e. this - var.
	 * @param var the given variable.
	 * @return a VarReal that represents: this - var.
	 */
	public VarReal minus(VarReal var);

	/**
	 * Adds and returns a new VarReal that is constrained to be the difference of this variable
	 *         and the given value, i.e. this - value.
	 * @param value the given value.
	 * @return a VarReal that represents: this - value.
	 */
	public VarReal minus(int value);

	/**
	 * Adds and returns a new VarReal that is constrained to be the difference of this variable
	 *         and the given variable, i.e. this - var.
	 * @param var the given variable.
	 * @return a VarReal that represents: this - var.
	 */
	public VarReal minus(Var var);

	/**
	 * Adds and returns a new VarReal that is constrained to be the product of this variable
	 *         and the given value, i.e. this * value.
	 * @param value the given value.
	 * @return a VarReal that represents: this * value.
	 */
	public VarReal multiply(double value);

	/**
	 * Adds and returns a new VarReal that is constrained to be the product of this variable
	 *         and the given variable, i.e. this * var.
	 * @param var the given variable.
	 * @return a VarReal that represents: this * var.
	 */
	public VarReal multiply(VarReal var);

	/**
	 * Adds and returns a new VarReal that is constrained to be the product of this variable
	 *         and the given value, i.e. this * value.
	 * @param value the given value.
	 * @return a VarReal that represents: this * value.
	 */
	public VarReal multiply(int value);

	/**
	 * Adds and returns a new VarReal that is constrained to be the product of this variable
	 *         and the given variable, i.e. this * var.
	 * @param var the given variable.
	 * @return a VarReal that represents: this * var.
	 */
	public VarReal multiply(Var var);

	/**
	 * Adds and returns a new VarReal that is constrained to be the division of this variable
	 *         by the given value, i.e. this / value.
	 * @param value the given value.
	 * @return a VarReal that represents: this / value.
	 */
	public VarReal divide(double value);

	/**
	 * Adds and returns a new VarReal that is constrained to be the division of this variable
	 *         by the given variable, i.e. this / var.
	 * @param var the given variable.
	 * @return a VarReal that represents: this / var.
	 * @throws Exception if dividing by zero.
	 */
	public VarReal divide(VarReal var) throws Exception;

	/**
	 * Adds and returns a new VarReal that is constrained to be the division of this variable
	 *         by the given value, i.e. this / value.
	 * @param value the given value.
	 * @return a VarReal that represents: this / value.
	 */
	public VarReal divide(int value);

	/**
	 * Adds and returns a new VarReal that is constrained to be the division of this variable
	 *         by the given variable, i.e. this / var.
	 * @param var the given variable.
	 * @return a VarReal that represents: this / var.
	 * @throws Exception if dividing by zero.
	 */
	public VarReal divide(Var var) throws Exception;

	/**
	 * Adds and returns a new VarReal that is constrained to be the negation of this variable, i.e. 0 - this.
	 * @return a VarReal that represents: 0 - this.
	 */
	public VarReal negative();

	/**
	 * Adds and returns a new VarReal that is constrained to be the absolute value of this variable.
	 * @return a VarReal that represents the absolute value of this variable.
	 */
	public VarReal abs();

	/**
	 * Adds and returns a new VarReal that is constrained to be the product of this variable
	 *         and itself, i.e. this * this.
	 * @return a VarReal that represents: this * this.
	 */
	public VarReal sqr();

	/**
	 * Adds and returns a new VarReal that is constrained to be this variable raised to the power of the given value,
	 *         i.e. this^value.
	 * @param value the given value.
	 * @return a VarReal that represents: this^value.
	 */
	public VarReal power(double value);

	/**
	 * Adds and returns a new VarReal that is constrained to be this variable raised to the power of the given value,
	 *         i.e. this^value.
	 * @param value the given value.
	 * @return a VarReal that represents: this^value.
	 */
	public VarReal power(int value);

//	/**
//	 * Adds and returns a new VarReal that is constrained to be this variable raised to the power of the given variable,
//	 *         i.e. this^var.
//	 * @param var the given variable.
//	 * @return a VarReal that represents: this^var.
//	 */
//	public VarReal power(Var var);
//
//	/**
//	 * Adds and returns a new VarReal that is constrained to be this variable raised to the power of the given real variable,
//	 *         i.e. this^var.
//	 * @param var the given real variable.
//	 * @return a VarReal that represents: this^var.
//	 */
//	public VarReal power(VarReal var);

}
