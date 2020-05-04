package it.ssc.io;

public class DirectoryNotFound extends java.io.IOException {
	
	private static final long serialVersionUID = 1L;

	public DirectoryNotFound(String message) {
		super(message);
	}
}
