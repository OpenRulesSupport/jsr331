package it.ssc.log;

import java.util.logging.Level;

public class SscLevel extends Level {

	public static final Level LOG=new SscLevel("LOG",811);
	public static final Level NOTE=new SscLevel("NOTE",821);
	public static final Level TIME=new SscLevel("TIME",831);

	protected SscLevel(String arg0, int arg1) {
		super(arg0, arg1);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
