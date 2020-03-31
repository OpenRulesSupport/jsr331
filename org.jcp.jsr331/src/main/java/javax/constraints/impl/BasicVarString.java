package javax.constraints.impl;

import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarString;

/**
 * BasicVarString are a special type of constrained variables that are defined using
 * an array of string values.
 *
 */

public class BasicVarString extends AbstractConstrainedVariable implements VarString {
	
	String[] allStrings = null;
	Var myvar;

	public BasicVarString(Problem problem, String[] allStrings) {
		this(problem,"",allStrings);

	}

	public BasicVarString(Problem problem, String name, String[] allStrings) {
		super(problem, name);
		this.allStrings = allStrings;
		//javax.constraints.impl.AbstractProblem a problem = (javax.constraints.impl.AbstractProblem)problem;
		myvar = problem.variable(name,0, allStrings.length-1, javax.constraints.DomainType.DOMAIN_SMALL);
		//aproblem.remove(name);
		myvar.setObject(this); // we need this for a nice printing inside solution
	}

	/**
	 * Returns all possible strings from the domain of this variable.
	 * @return all possible strings from the domain of this variable.
	 */
	public String[] getAllStrings() {
		return allStrings;
	}

	/**
	 * Returns the underlying integer constrained variable.
	 * @return the underlying integer constrained variable.
	 */
	public Var getInt() {
		return myvar;
	}
	
	/**
	 * Returns a String which is the value to which this variable has been instantiated,
	 *         throws a RuntimeException if the variable is not instantiated.
	 * @return a String which is the value of this instantiated variable.
	 * @throws RuntimeException if this variable is not instantiated.
	 */
	public String getValue() {
		return allStrings[myvar.getValue()];
	}
	
	/**
	 * Returns a string with an index "index" from the domain of this variable
	 * @param index an integer index in the array getAlltrings()
	 * @return a String getAllStrings()[index]
	 * @throws RuntimeException if index is outside the domain
	 */
	public String getValue(int index) {
		return allStrings[index];
	}
	

	/**
	 * @return a string representing the initial domain of the variable
	 */
	public String getInitialDomain() {
		if (allStrings == null)
			return "";
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < allStrings.length; i++) {
			if (i>0)
				buf.append(" ");
			buf.append(allStrings[i]);
		}
		return buf.toString();
	}

	public boolean isBound() {
		return myvar.isBound();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getName() + "[");
		int n = 0;
		for (int i = 0; i < allStrings.length; i++) {
			if (myvar.contains(i)) {
				if (n > 0)
					buf.append(",");
				buf.append(allStrings[i]);
				n++;
			}
		}
		buf.append("]");
		return buf.toString();
	}

	public boolean hasSameDomain(VarString var) {
		if (allStrings.length == var.getAllStrings().length) {
			for (int i = 0; i < allStrings.length; i++) {
				if (!allStrings[i].equals(var.getAllStrings()[i]))
					return false;
			}
			return true;
		}
		return false;
	}

	public int getIndex(String string) {
		for (int i = 0; i < allStrings.length; i++) {
			if (allStrings[i].equals(string))
				return i;
		}
		return -1;
	}

	

	

	
//	/**
//	 * Returns a Constraint that represents that this variable must be equal to
//	 *         the given variable, i.e. this == var.
//	 * @param var the given variable.
//	 * @return a Constraint that represents: this == var.
//	 */
//	public Constraint eq(BasicVarString var);
//
//	/**
//	 * Returns a Constraint that represents that this variable must be equal to the given value
//	 *         i.e. this == value.
//	 * @param value the given value.
//	 * @return a Constraint that represents: this == value.
//	 */
//	public Constraint eq(String value);
//
//	/**
//	 * Returns a Constraint that represents that this variable must not be
//	 *         equal to the given variable, i.e. this != var.
//	 * @param var the given variable.
//	 * @return a Constraint that represents: this != var.
//	 */
//	public Constraint ne(BasicVarString var);
//
//	/**
//	 * Returns a Constraint that represents that this variable must not be
//	 *         equal to the given value, i.e. this != value.
//	 * @param value the given value.
//	 * @return a Constraint that represents: this != value.
//	 */
//	public Constraint ne(String value);

}
