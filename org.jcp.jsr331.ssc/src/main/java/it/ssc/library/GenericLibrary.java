package it.ssc.library;

import it.ssc.library.Library.TYPE_LIBRARY;
import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.ref.Input;

public abstract class GenericLibrary {
	
	protected TYPE_LIBRARY type;
	protected String name;
	protected String produtc_name;
	protected boolean is_close;
	protected RFactoryLibraries factrory_lib;
	
	public String getName() {
		return name;
	}
	
	public boolean isFmtLibrary()   {
		return (type==TYPE_LIBRARY.LIBRARY_SSC);
	}
	
	public boolean isDbLibrary() {
		return (type==TYPE_LIBRARY.LIBRARY_DB);
	}
	
	public Input getInputFromSQLQuery(String sql) throws Exception {
		throw new InvalidLibraryException("La libreria "+this.name+" non e una libreria di  DB."+
				"Le istruzioni SQL sono supportate da librerie che puntano a DATABASE");
	}
	
	protected void generateExceptionOfLibraryClose() throws InvalidLibraryException {
		if (this.is_close) {
			throw new InvalidLibraryException("ERRORE ! La libreria e' inutilizzabile in quanto precedentemente chiusa.");
		}
	}	
	
	public boolean isClose()  {
		return is_close;
	}
	
	public abstract void close() throws Exception;
	public abstract Input getInput(String name_table) throws Exception ;
	public abstract void emptyLibrary() throws InvalidLibraryException;

}
