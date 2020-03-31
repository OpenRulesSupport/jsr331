//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  331
// 
// Specification
// 
//=============================================
package javax.constraints;

/**
 * This is an interface that allows a user to search and to iterate through multiple solutions.
 * The expected use:
 * <br>
 * <pre>
 * SolutionIterator iter = solver.solutionIterator();
 * while(iter.hasNext()) {
 *   Solution solution = iter.next();
 *     ...
 * }
 * 
 * The method next() should not be called without checking that hasNext() returned true.
 *
 * </pre>
 */

public interface SolutionIterator {

	/**
	 * Returns true if the iteration has more solutions.
	 * In other words, returns true if next would return an element
	 * rather than throwing an exception.
	 * @return true if the iteration has more solutions
	 */
	public boolean hasNext();

	/**
	 * Returns the next solution
	 * @return the next solution
	 * @throws RuntimeException "Cannot use SolutionIterator.next() before checking that hasNext() returned true"
	 */
	public Solution next();

}
