//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.extra;

import javax.constraints.Problem;
import javax.constraints.impl.CommonBase;

/**
 * This class implements reversible integers that
 * automatically restore their values when a solver backtracks.
 *
 */

abstract public class AbstractReversible extends CommonBase implements javax.constraints.extra.Reversible  {
	
	public AbstractReversible(Problem problem, int value) {
		this(problem,"",value);
	}
	
	public AbstractReversible(Problem problem, String name, int value) {
		super(problem,name);
	}
	
	abstract public int getValue();
	
	abstract public void setValue(int value);
	
	public String toString() {
		return getName()+"["+getValue()+"]";
	}

}
