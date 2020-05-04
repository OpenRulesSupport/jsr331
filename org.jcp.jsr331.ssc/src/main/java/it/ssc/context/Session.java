package it.ssc.context;

import it.ssc.context.exception.InvalidSessionException;
import it.ssc.datasource.DataSource;
import it.ssc.io.DirectoryNotFound;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.ref.Input;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;
import it.ssc.step.SortStep;


/**
 * Questa interfaccia permette di accedere ai principali oggetti costituenti una
 * sessione di lavoro SSC. <br>
 * 
 * @author Stefano Scarioli
 * @version 1.0
 */

public interface Session {
	
	
	/**
	 * 
	 * Permette di recuperare l'identificativo della sessione SSC. 
	 * Ogni sessione di lavoro e' identificata 
	 * univocamente da un ID. <br>
	 * Questo metodo permette di recuperare tale identificativo univoco espresso
	 * come intero (relativo macchina virtuale java in cui e' in esecuzione ).
	 * 
	 * @return identificativo della sessione di lavoro
	 * @throws InvalidSessionException
	 */
	
	public int getId() throws InvalidSessionException;

	/**
	 * 
	 * Chiude la sessione di lavoro disallocando le librerie. Se le librerie
	 * rappresentano connessioni ai DB, queste connessioni vengono chiuse. La libreria di work 
	 * viene cancellata.
	 * 
	 * @throws Exception
	 */
	
	public void close() throws Exception;

	/**
	 * restituisce true se la sessione e' chiusa.
	 */
	
	public boolean isClose();
	
	/*
	 * Permette di recuperare un oggetto che implementa l'interfaccia FactoryFormats.<br>
	 * Questo oggetto consente di accedere alle funzionalita' per la gestione dei formati 
	 * relativi ad una singola sessione di lavoro. <br>
	 * 
	 * @throws InvalidSessionException
	 */

	//public FactoryFormats getFactoryFormats() throws InvalidSessionException;

	/**
	 * Permette di recuperare un oggetto che implementa l'interfaccia FactoryLibraries.<br>
	 * Tramite questo oggetto si accede alle funzionalita' per la gestione delle librerie relative ad una
	 * singola sessione di lavoro. <br>
	 * 
	 * @return un oggetto che implementa FactoryLibraries
	 * @throws InvalidSessionException
	 */

	public FactoryLibraries getFactoryLibraries() throws InvalidSessionException;
	
	
	
	/**
	 * Permette di recuperare un oggetto che implementa l'interfaccia FactorySteps.<br>
	 * Questo oggetto consente di accedere alle funzionalità per le creazione e gestione 
	 * dei passi di STEP per l'elaborazione dei dati  (DATASTEP,FILESTEP,etc)
	 * 
	 * @return un oggetto che impelemta FactorySteps
	 * @throws InvalidSessionException
	 */
	
	public FactorySteps getFactorySteps() throws InvalidSessionException;

	/**
	 * Aggiunge alla sessione una libreria con nome name_library che rappresenta 
	 * una connessione ad un database. Ritora un oggetto che e' un'interfaccia 
	 * alla libreria allocata
	 * 
	 * @param nome della libreria name_library
	 * @param una connessione ad un database 
	 * @return un oggetto che implementa LibraryInterface
	 * @throws Exception
	 */
	
	public Library addLibrary(String name_library,java.sql.Connection connect)  throws Exception ;
	
	/**
	 * 
	 * Aggiunge alla sessione una libreria nativa con nome name_library avente path specificato 
	 * Ritorna un oggetto che e' un'interfaccia alla libreria allocata
	 * 
	 * @param path_library
	 * @param name_library
	 * @return un oggetto che implementa LibraryInterface
	 * @throws InvalidSessionException
	 * @throws DirectoryNotFound
	 * @throws InvalidLibraryException
	 */
	
	public Library addLibrary(String path_library, String name_library)  throws  InvalidSessionException, DirectoryNotFound, InvalidLibraryException;
	
	
	/**
	 * A partire da una sorgente di input , crea il DataSource corrispondente. Attraverso un oggetto DataSource
	 * e' possibili recuperare per ciascun record i corrispettivi valori presenti nei relativi campi  
	 * 
	 * @param input_reference
	 * @return DataSource
	 * @throws Exception
	 * @see it.ssc.datasource.DataSource
	 * 
	 */
	
	public DataSource createDataSource(Input input_reference) throws Exception; 
	
	/**
	 * A partire da un nome di dataset, crea il DataSource corrispondente. Attraverso un oggetto DataSource
	 * e' possibili recuperare per ciascun record i corrispettivi valori presenti nei relativi campi  
	 * 
	 * @param name_input_dataset
	 * @return DataSource
	 * @throws Exception
	 */
	
	public DataSource createDataSource(String name_input_dataset) throws Exception; 
	
	/**
	 * 
	 * @param new_dataset
	 * @param name_input_dataset 
	 * @return DataSource
	 * @throws Exception
	 */
	
	public DataStep createDataStep(String new_dataset, String name_input_dataset) throws Exception; 
	
	/**
	 * 
	 * @param new_dataset
	 * @param input_reference
	 * @return DataStep
	 * @throws Exception
	 */
	
	public DataStep createDataStep(String new_dataset, Input input_reference) throws Exception; 
	
	/**
	 * 
	 * @param path_file
	 * @param lib_dot_idataset
	 * @return
	 * @throws Exception
	 */
	
	public FileStep createFileStep(String path_file, String lib_dot_idataset) throws Exception ;

	/**
	 * 
	 * @param path_file
	 * @param input_reference
	 * @return
	 * @throws Exception
	 */
	
	public FileStep createFileStep(String path_file,Input input_reference) throws Exception ;
	
	
	public SortStep createSortStep(String new_dataset, String name_input_dataset) throws Exception; 
	
	/**
	 * 
	 * @param new_dataset
	 * @param input_reference
	 * @return DataStep
	 * @throws Exception
	 */
	
	public SortStep createSortStep(String new_dataset, Input input_reference) throws Exception; 
	
	
	public Session startLocalDB()  throws Exception; 
	
	
	
	
	

}