package it.ssc.context;

import it.ssc.i18n.RB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.ssc.context.exception.InvalidSessionException;
import it.ssc.io.DirectoryNotFound;
import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.log.SscLogger;

class SessionsManager  {
	
	private static final Logger logger=SscLogger.getLogger();
	
	/**
	 * contiene la lista delle sessioni attive. Indicizzate per ID.
	 * 
	 */

	private HashMap<Integer,SessionIMPL> listSession;
	
	/**
	 * Costruttore 
	 */
	SessionsManager() { 
		listSession=new HashMap<Integer,SessionIMPL> ();
	}
	
	/**
	 * Crea una nuova sessione, la inserisce nella lista delle sessioni attive, e la restituisce 
	 * al chiamante
	 * 
	 * @param config
	 * @return una nuova sessione FMT 
	 * @throws DirectoryNotFound
	 * @throws InvalidLibraryException
	 * @throws InvalidSessionException
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	Session getNewSession(Config config) throws DirectoryNotFound, InvalidLibraryException, InvalidSessionException, ClassNotFoundException, SQLException {
		Integer new_id=getNewId();
		SessionIMPL session=new SessionIMPL(new_id,config);
		listSession.put(new_id, session);
		return session;
	}

	/**
	 * Recupera la session con ID specificato, se chiusa ritorna Exception , se non esiste ritorna 
	 * null
	 * 
	 * @param id della sessione
	 * @return la sessione 
	 * @throws InvalidSessionException
	 */

	Session getSessionById(Integer id) throws InvalidSessionException { 
		SessionIPRIV session=listSession.get(id);
		if(session==null) logger.log(Level.WARNING,
				 					 RB.getString("it.ssc.context.SessionsManager.msg1")+id); 
		else if(session.isClose()) session.generateExceptionOfSessionClose();
		return session;
	}
	
	public ArrayList<Session> getListOpenSession() {
		ArrayList<Session> listSessionOpen=new ArrayList<Session>();
		Collection<SessionIMPL>  collectionSession= listSession.values();
		for(Session session:collectionSession)  {
			if(!session.isClose()) listSessionOpen.add(session);
		}
		return listSessionOpen;
	}
	
	/**
	 * Permette di creare ID univoci non utilizzati per identificare altre sessioni
	 * 
	 * @return id intero inutilizzato 
	 */
	
	private Integer getNewId() {
		Integer id;
		do {
			Random ra = new Random(new Date().getTime());
			id = Math.abs(ra.nextInt());
		} 
		while (listSession.containsKey(id));
		return id;
	}
}
