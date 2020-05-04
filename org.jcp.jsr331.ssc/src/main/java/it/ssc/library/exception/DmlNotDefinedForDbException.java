package it.ssc.library.exception;

public class DmlNotDefinedForDbException extends Exception { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DmlNotDefinedForDbException(String message) {
		super(message);
	}

}
