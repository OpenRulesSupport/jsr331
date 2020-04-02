package javax.constraints.linear;

/**
 * Exception thrown when a method is not implemented 
 */
public class NotImplementedException extends RuntimeException {
	
	public NotImplementedException(String text) {
		super("NotImplementedException: " +text);
	}
}
