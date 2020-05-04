package it.ssc.context;


import java.sql.SQLException;
import java.util.ArrayList;

import it.ssc.context.exception.InvalidSessionException;
import it.ssc.io.DirectoryNotFound;
import it.ssc.library.exception.InvalidLibraryException;


/**
 * Questa classe permette di accedere al constesto (ambiente di esecuzione) del sistema SSC. <br>
 * 
 * Nel particolare, attraverso il Contesto  si generano principalmente le sessioni 
 * di lavoro SSC. Partendo da una sessione di lavoro SSC si possono effettuare diverse operazione : 
 * allocazione di librerie, creazione di un'area  di  work, attivazione di  
 * eventuali connessioni a database esterni (viste come librerie), manipolazione ed elaborazione dei dati.<br>
 * Tutte queste risorse esterne (librerie di dati o DB esterni), una volta referenziate in una sessione di lavoro, 
 * sono sempre e solo riconducibili alla singola sessione di lavoro che le ha allocate. 
 * Alcune proprieta' di ogni sessione di lavoro sono impostate configurando il file config.xml. 
 * In questo file di configurazione possono essere preallocate delle librerie, effettuate connessioni al db, 
 * definito il path dell'area di work, etc. 
 * <br/><br/>
 * <font color="red">
 * Nel file config.xml mettere un tag per la gestione della versione <version >  o 
 * come attributo <config version="1.2"> . Il file config.xml deve soddisfare un DTD o 
 * uno schema. 
 * </font>
 * 
 * 
 * @author Stefano Scarioli
 * @version 1.0
 */

public class Context {
	
	private Context() {
		
	}
	
	/**
	 * E' l'oggetto che ha la responsabilita' della gestione delle diverse sessioni
	 */
	private static SessionsManager session_manager;
	private static String pathFileConfig;
	
	/**
	 * E' l'oggetto che contiene la corrente configurazione  utilizzata per le nuove sessioni.
	 * Inizialmente, se non modificata, e' la configurazione di default. Nel momento in cui viene 
	 * utilizzato per costruire una sessione di lavoro, il Config utilizzato nella sessione di lavoro
	 * e' un clone di quello di partenza, per cui le sue caratteristiche non possono essere modificate. 
	 */
	private static Config config;
	
	static {
		session_manager=new SessionsManager(); 
	}
	
	
	
	/**
	 * Crea una nuova sessione SSC partendo dalla configurazione di default. 
	 * 
	 * @return Una nuova  sessione SSC di lavoro
	 * @throws InvalidLibraryException 
	 * @throws DirectoryNotFound 
	 * @throws InvalidSessionException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */

	public synchronized static Session createNewSession() throws DirectoryNotFound, InvalidLibraryException, InvalidSessionException, ClassNotFoundException, SQLException {
		return session_manager.getNewSession(getConfig().clone());
	} 
 
	/**
	 * Permette di recuperare una sessione SSC precedentemente creata tramite il suo ID.
	 * Se tale sessione non esiste ritorna null;
	 * 
	 * @param id della sessione 
	 * @return una sessione SSC con tale ID o null se tale sessione non esiste. 
	 * @throws InvalidSessionException 
	 */
	
	public synchronized static Session getSessionById(int id) throws InvalidSessionException {
		return session_manager.getSessionById(id);
	}
  
	

	/**
	 * Restituisce sempre il corrente Config di contesto. Se questo non e' stato modificato 
	 * risulta il config di default. 
	 * 
	 * @return l'oggetto Config corrente del contesto 
	 */
	
	public static Config getConfig() {
		if(Context.config==null && pathFileConfig==null)  Context.config = new ConfigIMPL();
		if(Context.config==null && pathFileConfig!=null)  Context.config = new ConfigIMPL(pathFileConfig);
		return Context.config;
	}
	
	public String getPathFileConfig() {
		return pathFileConfig;
	}

	/**
	 * Setta il path del file xml con i parametri necessari per la configurazione del sistema. 
	 * Va effettuato prima di richiamare qualsiasi altro metodo della classe. Altrimenti la 
	 * configurazione sara' quella con i parametri di default. 
	 * 
	 * @param pathFileConfig
	 */
	
	public static void setPathFileConfig(String pathFileConfig) {
		Context.pathFileConfig = pathFileConfig;
	}
	
	public static ArrayList<Session> getListOpenSession() {
		return session_manager.getListOpenSession();
	}

}