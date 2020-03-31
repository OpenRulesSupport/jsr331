package javax.constraints;

/**
 * VarString are a special type of constrained variables that are defined using
 * an array of string values.
 *
 */
public interface VarString extends ConstrainedVariable {
	
	/**
	 * Returns all possible strings from the domain of this variable.
	 * @return all possible strings from the domain of this variable.
	 */
	public String[] getAllStrings();
	
	/**
	 * Returns the underlying integer constrained variable.
	 * @return the underlying integer constrained variable.
	 */
	public Var getInt();
	
	/**
	 * Returns a String which is the value to which this variable has been instantiated,
	 *         throws a RuntimeException if the variable is not instantiated.
	 * @return a String which is the value of this instantiated variable.
	 * @throws RuntimeException if this variable is not instantiated.
	 */
	public String getValue();
	
	/**
	 * Returns a string with an index "index" from the domain of this variable
	 * @param index an integer index in the array getAlltrings()
	 * @return a String getAllStrings()[index]
	 * @throws RuntimeException if index is outside the domain
	 */
	public String getValue(int index);
	
	
	/**
	 * @return a string representing the initial domain of the variable
	 */
	public String getInitialDomain();
	
	/**
	 * 
	 * @return true if the variable is bound (domain size is 1)
	 */
	public boolean isBound();
	
	/**
	 * 
	 * @param var
	 * @return true if this variable has the same domain as "var" 
	 */
	public boolean hasSameDomain(VarString var);
	
	
	/**
	 * 
	 * @param string
	 * @return an integer index of the "string" inside the domain of this variable.
	 * @return -1 if "string" is outside of the domain of this variable.
	 */
	public int getIndex(String string);
	

}

