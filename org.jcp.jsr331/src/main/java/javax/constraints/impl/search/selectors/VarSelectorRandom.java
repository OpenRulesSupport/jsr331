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
 * This class creates a selector that randomly selects an unbound constrained
 * variable from a given array of variables.
 */

import java.util.Random;

import javax.constraints.SearchStrategy;
import javax.constraints.VarSelectorType;

public class VarSelectorRandom extends AbstractVarSelector {
	
	Random rn;
	
	public VarSelectorType getType() {
		return VarSelectorType.RANDOM;
	}

	/**
	 * Constructor from the Var[]
	 */
	public VarSelectorRandom(SearchStrategy strategy) {
		super(strategy);
		rn = new Random();
	}

	/**
	 * Returns the index of a randomly selected variable.
	 * If no variables to select, it returns -1;
	 */
	public int select() {
		int[] unboundIndexes = new int[getVars().length];
	    int n = 0;
		for(int i=0; i < getVars().length; i++) {
			if (getVars()[i].isBound())
				continue;
			unboundIndexes[n++] = i;
		}
		if (n==0)
			return -1;
		
		if (n==1)
			return unboundIndexes[0];
		else {
			int randomIndex = rn.nextInt(n); // return a value from 0 to n-1
			return unboundIndexes[randomIndex];
		}
	}

}
