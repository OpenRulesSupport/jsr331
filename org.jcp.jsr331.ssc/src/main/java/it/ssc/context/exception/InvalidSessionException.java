package it.ssc.context.exception;

import  it.ssc.i18n.RB;

public class InvalidSessionException  extends Exception  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidSessionException ()  {
		super( RB.getString("it.ssc.context.exception.InvalidSessionException.msg1"));
	}
}
