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
 * This interface represents application-specific actions that
 * can be added during the search and executed during backtracking.
 * For example, an application may draw a square during the search and
 * erase it (a reversible action) during backtracking.
 *
 */

public interface ReversibleAction {
	
	public boolean execute() throws Exception;

}
