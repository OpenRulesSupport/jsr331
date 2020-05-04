package it.ssc.context;

import it.ssc.context.exception.InvalidSessionException;
import it.ssc.io.DirectoryNotFound;

/*Interfaccia privata e di utilizzo del sistema */

public interface SessionIPRIV extends Session {
	
	public Config getConfig() throws InvalidSessionException;
	public String getPathCompiler() throws InvalidSessionException, DirectoryNotFound;
	public String getPathSorting() throws InvalidSessionException, DirectoryNotFound;
	public void generateExceptionOfSessionClose() throws InvalidSessionException;

}
