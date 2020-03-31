//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//=============================================
package javax.constraints.impl.search.selectors;

import javax.constraints.ValueSelector;
import javax.constraints.ValueSelectorType;
import javax.constraints.Var;

/**
 * This selector selects one time the smallest and another time the largest
 * value from the domain of a constrained variable.
 */
public class ValueSelectorMinMaxAlternate implements ValueSelector {
	boolean minmax; // true=min false=max

	public ValueSelectorMinMaxAlternate() {
		minmax=true;
	}
	
	public ValueSelectorType getType() {
		return ValueSelectorType.MIN_MAX_ALTERNATE;
	}

	public final int select(Var var) {
		if (minmax) {
			minmax = false;
			return var.getMin();
		}
		else {
			minmax = true;
			return var.getMax();
		}
	}

}
