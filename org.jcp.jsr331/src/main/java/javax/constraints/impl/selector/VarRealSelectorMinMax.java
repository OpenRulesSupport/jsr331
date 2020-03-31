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
 * real variable from a given array of real variables. It selects the 
 * unbound real variable with the smallest maximum.
 */

import javax.constraints.VarReal;

public class VarRealSelectorMinMax extends VarRealSelectorImpl {

	/**
	 * Constructor from the VarReal[]
	 */
	public VarRealSelectorMinMax(VarReal[] vars) {
		super(vars);
	}

	/**
	 *  Selects the unbound real variable with the smallest maximum.
	 *  If no real variables to select, it returns -1;
	 */
	public int select() {
		double minMax = Double.MAX_VALUE;
	    int minIndex = -1;
		for(int i=0; i < getVarReals().length; i++) {
			VarReal var = getVarReals()[i];
			if (var.isBound())
				continue;
			double max = var.getMax();
			if (minIndex == -1 || minMax > max) {
					minIndex = i;
					minMax =  max;
			}
		}
		return minIndex;
	}

}
