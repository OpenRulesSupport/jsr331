package it.ssc.library;

import java.sql.SQLException;
import it.ssc.context.exception.InvalidSessionException;
import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.ref.Input;

/**
 * E' l'interfaccia che permette di accedere alle librerie allocate da una perticolare sessione 
 * di lavoro.  <br>
 * I nomi delle librerie non sono case sensitive.
 * 
 * @author Scarioli Stefano.
 *
 */

public interface Library {
	
	public static final String NAME_LIBRARY_WORK="WORK";
	public static final String NAME_LIBRARY_DERBY="LOCALDB";
	
	public enum TYPE_LIBRARY {LIBRARY_DB, LIBRARY_SSC};

	/**
	 * Restituisce il nome logico della libreria 
	 * @return name
	 */
	public String getName() ;  
	/**
	 * restituisce un riferimento di tipo InputDataInterface ad una tabella esistente. 
	 * 
	 * 
	 * @param name_table nome della tabella da referenziare
	 * @return un oggetto InputDataInterface
	 * @throws Exception se la tabella non esiste o se i suoi metadati non possono essere letti
	 */
	public Input getInput(String name_table) throws Exception;
	
	/**
	 * A fronte di una istruzione select SQL su una o piu' tabelle (tramite istruzione di join) 
	 * restituisce un riferimento di tipo InputDataInterface al resultset relativo alla query. 
	 * Questo tipo di referenziazione puo' essere ottenuto solo su librerie relative a DB , 
	 * ma non su librerie native 
	 * 
	 * @param sql : un'istruzione sql di interrogazione (select)
	 * @return un oggetto InputDataInterface
	 * @throws Exception se la tabella e non esiste o se la query e' errata 
	 */
	public Input getInputFromSQLQuery(String sql) throws Exception;
	
	/**
	 * Permette di recuperare in un array di Stringhe i nomi delle tabelle appartenenti 
	 * alla libreria . Se la libreria e' un DB le tabelle considerate sono del 
	 * tipo "TABLE","VIEW"
	 * 
	 * @return la lista di nomi delle tabelle della libreria 
	 * @throws InvalidLibraryException
	 */
	
	public String[] getListTable() throws Exception;
	
	
	/**
	 * 
	 *  Permette di recuperare in un array di Stringhe i nomi delle tabelle della 
	 *  libreria e appartenenti, nel caso di una libreria DB,  
	 *  a un determinato/i tipo di tabella (type_table[]); nel caso di una libreria 
	 *  nativa l'array passato non ha effetti. 
	 * 
	 * @param type_table nel caso di DB si possono specificare uno o piu' Stringhe nell'array con i seguenti valori : 
	 * "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". 
	 * 
	 * @return la lista di nomi delle tabelle della libreria  
	 * @throws Exception
	 */
	
	public String[] getListTable(String[] type_table) throws Exception;
	
	/**
	 * Verifica l'esistenza della tabella 
	 * 
	 * @param name_table
	 * @return vero se esiste la tabella con nome name_table
	 * @throws InvalidLibraryException
	 */
	
	public boolean existTable (String name_table) throws Exception;
	public boolean isFmtLibrary();
	public boolean isDbLibrary() ;
	public String getAbsolutePath() throws Exception; 
	public String getUrl() throws Exception; 
	
	/**
	 * Svuota la libreria di tutto il suo contenuto. Questo comando e' applicabile solo 
	 * alle librerie di tipo nativo. Su DB e' improprio eliminare un intero DATABASE. 
	 * Per cui sui DB esterni occorre utilizzare gli appropriati comandi SQL (DROP TABLE, ...) o il metodo dropTable(String name_table). 
	 * 
	 * @throws InvalidSessionException
	 * @throws InvalidLibraryException
	 */
	
	public void emptyLibrary() throws InvalidSessionException, InvalidLibraryException ;
	public void close() throws Exception;
	public void dropTable(String name_table) throws Exception;
	public void renameTable(String new_name,String old_name) throws Exception ;
	
	/**
	 * sql - an SQL Data Manipulation Language (DML) statement, such as 
	 * INSERT, UPDATE or DELETE; or an SQL statement that returns nothing, 
	 * such as a DDL statement. 
	 * 
	 * @param sql
	 * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing 
	 * @throws SQLException
	 * @throws InvalidLibraryException
	 */
	public int executeSQLUpdate(String sql) throws SQLException , InvalidLibraryException;
	
	
	public enum PRODUCT_NAME  {
		SSC_STAT("SSC_STAT"),    
		ORACLE("ORACLE"),    
		POSTGRESQL("POSTGRESQL"),
		DB2("DB2"),
		SAS("SAS");
	 
	    private String value;
	    PRODUCT_NAME(String value)  {
	      this.value=value;
	    }
	    public String getValue()  {
	      return value;
	    }
	}
}