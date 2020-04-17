//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//=============================================
package javax.constraints.scheduler.impl;

import java.security.GeneralSecurityException;

/**
 * This class creates a selector that selects an unbound constrained
 * variable from a given array of variables by this criteria:
 * "min size of domain, random tie break"
 */

import javax.constraints.SearchStrategy;
import javax.constraints.Var;
import javax.constraints.VarSelectorType;
import javax.constraints.impl.search.selectors.AbstractVarSelector;
import javax.constraints.scheduler.Resource;

public class VarSelectorMinCost extends AbstractVarSelector {
	
	public VarSelectorType getType() {
		return VarSelectorType.CUSTOM;
	}

	/**
	 * Constructor from SearchStrategy strategy
	 */
	public VarSelectorMinCost(SearchStrategy strategy) {
		super(strategy);
	}

	/**
	 * Returns the index of the selected variable:
	 * "min cost of the associated resource".
	 * If no variables to select, it returns -1;
	 */
	public int select() {
		
		int minCost = Integer.MAX_VALUE;
	    int minIndex = -1;
		for(int i=0; i < getVars().length; i++) {
			Var var = getVars()[i];
			if (var.isBound())
				continue;
			Resource resource = (Resource)var.getObject();
			if (resource == null)
				return i;
			int cost = resource.getCost();
			if (minIndex == -1 || minCost > cost) {
				minIndex = i;
				minCost = cost;
			}
		}
		return minIndex;
	}

}
