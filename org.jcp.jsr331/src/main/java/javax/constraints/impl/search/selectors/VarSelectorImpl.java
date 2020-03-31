package javax.constraints.impl.search.selectors;

import javax.constraints.SearchStrategy;
import javax.constraints.Var;
import javax.constraints.VarSelector;

/**
 * This abstract defines an interface for the selection of the constrained
 * variable from a given array of variables. 
 */

abstract public class VarSelectorImpl implements VarSelector {
	
	SearchStrategy strategy;

	/**
	 * Constructor from SearchStrategy
	 */
	public VarSelectorImpl(SearchStrategy strategy) {
		this.strategy = strategy;
	}
	
	/**
	 * Return a search strategy with which this selector is associated
	 * @return
	 */
	public final SearchStrategy getSearchStrategy() {
		return strategy;
	}

	final public Var[] getVars() {
		return strategy.getVars();
	}

	/**
	 * Returns the index of the selected variable in the array of constrained
	 * variables passed to the selector as a constructor parameter. If no
	 * variables to select, it returns -1;
	 */
	abstract public int select();

}
