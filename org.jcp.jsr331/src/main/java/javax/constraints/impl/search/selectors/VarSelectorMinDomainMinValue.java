//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//=============================================
package javax.constraints.impl.search.selectors;

/**
 * This class creates a selector that selects an unbound constrained
 * variable from a given array of variables. It selects the unbound variable
 * with the smallest domain. If there are two variables with the same
 * smallest size, then the selector selects a variable with the smallest
 * minimum.
 */

import javax.constraints.SearchStrategy;
import javax.constraints.Var;
import javax.constraints.VarSelectorType;

public class VarSelectorMinDomainMinValue extends AbstractVarSelector {

	public VarSelectorMinDomainMinValue(SearchStrategy strategy) {
		super(strategy);
	}

	public VarSelectorType getType() {
		return VarSelectorType.MIN_DOMAIN_MIN_VALUE;
	}
	
	/**
	 *  Selects the unbound variable with the smallest domain.
	 *  If there are two variables with the same smallest size,
	 *  a variable with the smallest minimum is selected.
	 *  If no variables to select, it returns -1;
	 */
	public int select() {
		int minSize = Integer.MAX_VALUE;
		int minMin = Integer.MAX_VALUE;
	    int minIndex = -1;
		for(int i=0; i < getVars().length; i++) {
			Var var = getVars()[i];
			if (var.isBound())
				continue;
			int size = var.getDomainSize();
			if (minIndex == -1 || minSize >= size) {
				if (minSize > size || minMin > var.getMin()) {
					minIndex = i;
					minSize = size;
					minMin =  var.getMin();
//					if (size == 2) break;
				}
			}
		}
		return minIndex;
	}

}
