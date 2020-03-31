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
 * real variable from a given array of real variables. It selects the unbound 
 * real variable with the smallest minimum.
 */

import javax.constraints.VarReal;

public class VarRealSelectorMinMin extends VarRealSelectorImpl {

	/**
	 * Constructor from the VarReal[]
	 */
	public VarRealSelectorMinMin(VarReal[] vars) {
		super(vars);
	}

	/**
	 *  Selects the unbound variable with the smallest minimum.
	 *  If no variables to select, it returns -1;
	 */
	public int select() {
		double minMin = Double.MAX_VALUE;
	    int minIndex = -1;
		for(int i=0; i < getVarReals().length; i++) {
			VarReal var = getVarReals()[i];
			if (var.isBound())
				continue;
			double min = var.getMin();
			if (minIndex == -1 || minMin > min) {
					minIndex = i;
					minMin =  min;
			}
		}
		return minIndex;
	}

}