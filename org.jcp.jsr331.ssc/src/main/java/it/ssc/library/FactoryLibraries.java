package it.ssc.library;

import it.ssc.context.exception.InvalidSessionException;
import it.ssc.io.DirectoryNotFound;
import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.library.exception.LibraryNotFoundException;


public interface FactoryLibraries {
	
	  public Library[] getLibraryList()  throws InvalidSessionException;
	  
	  public Library getLibraryWork()  throws InvalidSessionException;
	  
	  public Library addLibrary(String path_library, String name_library)  throws  InvalidSessionException, DirectoryNotFound, InvalidLibraryException;
	  
	  public void emptyWork() throws InvalidSessionException, InvalidLibraryException;
	  
	  public Library getLibrary(String name_library)  throws InvalidSessionException,LibraryNotFoundException;
	  
	  public Library addLibrary(String name_library,java.sql.Connection connection)  throws Exception;
	  
	  public boolean existLibrary(String name_library)  throws InvalidSessionException;
	  
}
