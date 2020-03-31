//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//=============================================
package javax.constraints.impl.selector;

import javax.constraints.VarReal;
import javax.constraints.VarRealSelector;


/**
 * This abstract class defines an interface for the selection of the constrained
 * real variable from a given array of real variables. 
 *
 */


abstract public class VarRealSelectorImpl implements VarRealSelector {

	protected VarReal[] vars;
	
	/**
	 * Constructor from the Var[]
	 */
	public VarRealSelectorImpl(VarReal[] vars) {
		this.vars = vars;
	}
	
	/**
	 * Returns the array of constrained real variables upon which this selector was defined.
	 * @return the array of constrained real variables upon which
	 * this selector was defined.
	 */
	public VarReal[] getVarReals()
	{
		return vars;
	}

	/**
	 * Returns the index of the selected variable in the array of constrained
	 * real variables upon which this selector was defined. If no
	 * real variables to select, it returns -1;
	 * @return the index of the selected real variable in the array of constrained 
	 *         real variables upon which this selector was defined. Returns -1 if
	 *         there are no real variables to select.
	 */
	abstract public int select();

}
