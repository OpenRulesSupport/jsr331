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
 * variable from a given array of variables by this criteria:
 * "min size of domain, random tie break"
 */

import javax.constraints.SearchStrategy;
import javax.constraints.Var;
import javax.constraints.VarSelectorType;

public class VarSelectorMinDomain extends AbstractVarSelector {
	
	public VarSelectorType getType() {
		return VarSelectorType.INPUT_ORDER;
	}

	/**
	 * Constructor from SearchStrategy strategy
	 */
	public VarSelectorMinDomain(SearchStrategy strategy) {
		super(strategy);
	}

	/**
	 * Returns the index of the selected variable:
	 * "min size of domain, random tie break".
	 * If no variables to select, it returns -1;
	 */
	public int select() {
		int minSize = Integer.MAX_VALUE;
	    int minIndex = -1;
		for(int i=0; i < getVars().length; i++) {
			Var var = getVars()[i];
			if (var.isBound())
				continue;
			int size = var.getDomainSize();
			if (minIndex == -1 || minSize > size) {
				minIndex = i;
				minSize = size;
				if (size == 2)
					break;
			}
			// TO DO add random tie break
		}
		return minIndex;
	}

}
