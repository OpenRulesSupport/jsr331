//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints.extra;

/**
 * This interface represents reversible integers.
 * They automatically restore their values when a solver backtracks.
 *
 */

public interface Reversible {
	
	public int getValue();
	public void setValue(int value);

}
