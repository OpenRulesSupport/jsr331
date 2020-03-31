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
 * This class creates a selector that selects an unbound constrained
 * real variable from a given array of real variables that has the maximal domain size.
 */

import javax.constraints.VarReal;

public class VarRealSelectorMaxSize extends VarRealSelectorImpl {

	/**
	 * Constructor from the VarReal[]
	 */
	public VarRealSelectorMaxSize(VarReal[] vars) {
		super(vars);
	}

	/**
	 * Returns the index of the unbound real variable with the largest domain.
	 * If no real variables to select, it returns -1;
	 */
	public int select() {
		double maxSize = Double.MIN_VALUE;
	    int maxIndex = -1;
		for(int i=0; i < getVarReals().length; i++) {
			VarReal var = getVarReals()[i];
			if (var.isBound())
				continue;
			double size = var.getMax() - var.getMin() + 1;
			if (maxIndex == -1 || maxSize < size) {
				maxIndex = i;
				maxSize = size;
			}
		}
		return maxIndex;
	}

}
