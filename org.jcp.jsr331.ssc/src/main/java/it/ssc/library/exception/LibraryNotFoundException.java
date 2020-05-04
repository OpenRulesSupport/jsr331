package it.ssc.library.exception;

public class LibraryNotFoundException extends Exception { 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LibraryNotFoundException(String name_lib) {
		super("ERRORE. Lbreria non valida o inesistente o precedentemente chiusa:"+name_lib);
	}

}
