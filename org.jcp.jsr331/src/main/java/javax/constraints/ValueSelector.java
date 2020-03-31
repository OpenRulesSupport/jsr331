//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints;

/**
 * This is a common interface for selection of a value from the domain of a constrained variable.
 * Used inside search strategies.
 */
public interface ValueSelector {

	public int select(Var var);
	
	public ValueSelectorType getType();

}
