package javax.constraints;

/**
 * This is a common interface for selection of a constrained
 * variable from an array of variables. Used inside search strategies.
 */

public interface VarSelector {
	
	/**
	 * Returns a type of this selector
	 * @return a type of this selector
	 */
	public VarSelectorType getType();
	
	/**
	 * Return a search strategy with which this selector is associated
	 * @return a search strategy with which this selector is associated
	 */
	public SearchStrategy getSearchStrategy();

	/**
	 * Returns the array of constrained variables upon which this selector was defined.
	 * @return the array of constrained variables upon which
	 * this selector was defined.
	 */
	public Var[] getVars();

	/**
	 * Returns the index of the selected variable in the array of constrained
	 * variables upon which this selector was defined. If no
	 * variables to select, it returns -1;
	 * @return the index of the selected variable in the array of constrained 
	 *         variables upon which this selector was defined. Returns -1 if
	 *         there are no variables to select.
	 */
	public int select();

}
