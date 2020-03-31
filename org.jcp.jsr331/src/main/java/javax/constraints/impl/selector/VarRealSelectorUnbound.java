//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//=============================================
package javax.constraints.impl.selector;

/**
 * This class creates a selector that selects the first unbound constrained
 * real variable from a given array of real variables.
 */

import javax.constraints.VarReal;

public class VarRealSelectorUnbound extends VarRealSelectorImpl {

	/**
	 * Constructor from the VarReal[]
	 */
	public VarRealSelectorUnbound(VarReal[] vars) {
		super(vars);
	}

	/**
	 * Returns the index of the selected real variable in the array of constrained
	 * real variables passed to the selector as a constructor parameter. If no
	 * real variables to select, it returns -1;
	 */
	public int select() {

		for(int i=0; i < getVarReals().length; i++) {
			VarReal var = getVarReals()[i];
			if (!var.isBound())
				return i;
		}
		return -1;
	}

}
