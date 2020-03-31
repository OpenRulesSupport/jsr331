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
 * This class creates a selector that selects the first unbound constrained
 * variable from a given array of variables in order of definition.
 */

import javax.constraints.SearchStrategy;
import javax.constraints.Var;
import javax.constraints.VarSelectorType;

public class VarSelectorInputOrder extends AbstractVarSelector {
	
	public VarSelectorType getType() {
		return VarSelectorType.INPUT_ORDER;
	}

	/**
	 * Constructor from the Var[]
	 */
	public VarSelectorInputOrder(SearchStrategy strategy) {
		super(strategy);
	}

	/**
	 * Returns the index of the selected variable in the array of constrained
	 * variables in order of definition. If no variables to select, it returns -1;
	 */
	public int select() {

		for(int i=0; i < getVars().length; i++) {
			Var var = getVars()[i];
			if (!var.isBound())
				return i;
		}
		return -1;
	}

}
