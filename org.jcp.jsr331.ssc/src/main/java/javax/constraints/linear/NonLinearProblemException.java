package javax.constraints.linear;

/**
 * Exception thrown when a problem is found to be non-linear while building the LP model
 *
 */
public class NonLinearProblemException extends RuntimeException {
	
	public NonLinearProblemException(String text) {
		super("NonLinearProblemException: " + text);
	}
}
