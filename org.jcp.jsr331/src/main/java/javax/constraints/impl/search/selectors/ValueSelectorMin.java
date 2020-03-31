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
 * This selector selects the smallest value from the domain of 
 * a constrained variable.
 */
public class ValueSelectorMin implements ValueSelector {
	
	public ValueSelectorType getType() {
		return ValueSelectorType.MIN;
	}

	public final int select(Var var) {
		return var.getMin();
	}

}
