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
 * This is a common interface for selection of a constrained
 * real variable from an array of real variables. Used inside search strategies.
 *
 */

public interface VarRealSelector {

	/**
	 * Returns the array of constrained real variables upon which this selector was defined.
	 * @return the array of constrained real variables upon which
	 * this selector was defined.
	 */
	public VarReal[] getVarReals();

	/**
	 * Returns the index of the selected variable in the array of constrained
	 * real variables upon which this selector was defined. If no
	 * real variables to select, it returns -1;
	 * @return the index of the selected real variable in the array of constrained 
	 *         real variables upon which this selector was defined. Returns -1 if
	 *         there are no real variables to select.
	 */
	public int select();

}