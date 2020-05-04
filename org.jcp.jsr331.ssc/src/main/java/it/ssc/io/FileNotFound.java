package it.ssc.io;


public class FileNotFound extends java.io.IOException {
	
	private static final long serialVersionUID = 1L;

	public FileNotFound(String message) {
		super(message);
	}
}