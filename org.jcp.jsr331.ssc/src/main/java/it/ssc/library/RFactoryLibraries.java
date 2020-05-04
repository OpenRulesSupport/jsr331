package it.ssc.library;

import it.ssc.context.SessionIPRIV; 
import it.ssc.context.Config;
import it.ssc.context.exception.InvalidSessionException;
import it.ssc.io.DirectoryNotFound;
import it.ssc.io.UtilFile;
import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.library.exception.LibraryNotFoundException;
import it.ssc.log.SscLogger;
import it.ssc.i18n.RB;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RFactoryLibraries implements  FactoryLibraries {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private HashMap<String,PLibraryInterface> list_library;
	private SessionIPRIV parent_session;
	private String path_work;
	private String path_compiler;
	private String path_sorting;
	
	
	public RFactoryLibraries (SessionIPRIV parent_session) throws DirectoryNotFound, InvalidLibraryException, InvalidSessionException, ClassNotFoundException, SQLException {
		this.parent_session=parent_session; 
		this.list_library=new HashMap<String,PLibraryInterface>(); 
		
		//Aggiungo libreria di work
		String parth_root=this.parent_session.getConfig().getPathWorkArea();
		String parth_root_work=creteDirectoryPrincipal(parth_root,Config.NAME_DIR_WORK);
		this.path_work=getRandomNameWork(parth_root_work);
		this.list_library.put(Library.NAME_LIBRARY_WORK, new FmtLibrary(Library.NAME_LIBRARY_WORK,path_work,this));
		
		logger.log(Level.INFO,RB.getString("it.ssc.library.RFactoryLibraries.msg1")+" "+
				   FmtLibrary.NAME_LIBRARY_WORK+" "+ path_work);
		this.path_compiler=creteDirectoryInnerWork(Config.NAME_DIR_COMPILER);
		this.path_sorting=creteDirectoryInnerWork(Config.NAME_DIR_SORTING);
		
	}
	
	public void startLocalDB()  throws Exception {
		parent_session.generateExceptionOfSessionClose(); 
		String db_root=this.parent_session.getConfig().getPathLocalDb();
		String db_root_derby=creteDirectoryPrincipal(db_root,Config.NAME_DIR_DERBY); 
		logger.log(Level.INFO,"Path per il DB LOCALE: "+ db_root_derby);
		Connection connection_derby= createConnectionEmbeddedDbDerby(db_root_derby);
		this.list_library.put(Library.NAME_LIBRARY_DERBY, new DbLibrary(Library.NAME_LIBRARY_DERBY,connection_derby,this));
	}
	
	public String getPathCompiler() throws DirectoryNotFound {
		return UtilFile.getPathDirWithSeparatorFinal(path_compiler);
	}
	
	public String getPathSorting() throws DirectoryNotFound {
		return UtilFile.getPathDirWithSeparatorFinal(path_sorting);
	}
	
	
	private static String creteDirectoryPrincipal(String path_root,String name_dir) throws DirectoryNotFound {
	    String new_path=UtilFile.getPathDirWithSeparatorFinal(path_root);
		File file = new File(new_path + name_dir + File.separator);
		if(!file.exists()) { 
			if(!file.mkdir()) {
				throw new DirectoryNotFound("ERRORE ! Impossibile creare "+file.getAbsolutePath());
			}
		}
		return file.getAbsolutePath();
	}
	
	
	private Connection createConnectionEmbeddedDbDerby(String path_db) throws SQLException, ClassNotFoundException {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection conn = DriverManager.getConnection("jdbc:derby:"+path_db+ File.separator+"LOCALDB;create=true");
		return conn;
	}
		
	
	
	private String creteDirectoryInnerWork(String name_dir) throws DirectoryNotFound {
	    String new_path=UtilFile.getPathDirWithSeparatorFinal(path_work);
		File file = new File(new_path + name_dir + File.separator);
		if(!file.exists()) { 
			if(!file.mkdir()) {
				throw new DirectoryNotFound("ERRORE ! Impossibile creare "+file.getAbsolutePath());
			}
		}
		return file.getAbsolutePath();
	}
	
	/**
	 * 
	 * @return Un oggetto dal quale accedere alle librerie di una sessione FMT
	 * @throws InvalidSessionException
	 */

	public Library[] getLibraryList() throws InvalidSessionException {
		parent_session.generateExceptionOfSessionClose(); 
		Library[] appo=new Library[list_library.size()];
		return list_library.values().toArray(appo);
	}

	/**
	 * 
	 * @return Un oggetto dal quale accedere alle proprieta' della libreria di
	 *         work
	 * @throws InvalidSessionException 
	 * 
	 */

	public Library getLibraryWork() throws InvalidSessionException {
		parent_session.generateExceptionOfSessionClose();
		return list_library.get(Library.NAME_LIBRARY_WORK);
	}
	
	
	/**
	 * 
	 * @return Un oggetto dal quale accedere alle proprieta' della libreria di
	 *         work
	 * @throws InvalidSessionException 
	 * 
	 */

	public Library getLibraryLocalDb() throws InvalidSessionException {
		parent_session.generateExceptionOfSessionClose();
		return list_library.get(Library.NAME_LIBRARY_DERBY);
	}
	
	

	/**
	 * Alloca una nuova libreria per la sessione FMT corrente
	 * 
	 * @param path_library
	 *            Path fisico della libreria
	 * @param name_library
	 *            Nome della libreria. Il nome puo essere costituito solo dai
	 *            caratteri [a-z][A-Z][0-9][_] , non puo contenere punti, o
	 *            caratteri particolari, come '$','%','&','/','(',')' etc... Una
	 *            libreria deve sempre iniziare con un carattere alfabetico.
	 * 
	 * @return un riferimento alla libreria creata
	 * @throws InvalidSessionException
	 * @throws InvalidLibraryException
	 * @throws DirectoryNotFound
	 * @throws InvalidNameDataset
	 */

	  public Library addLibrary(String name_library, String path_library) throws InvalidSessionException, InvalidLibraryException, DirectoryNotFound, InvalidLibraryException {
		  parent_session.generateExceptionOfSessionClose();
		  name_library=name_library.toUpperCase();
		  if(list_library.containsKey(name_library)) {
			throw new  InvalidLibraryException("ERRORE. Libreria gia assegnata :" +name_library);
		  }
		  
		  FmtLibrary library=new FmtLibrary(name_library,path_library,this);
		  list_library.put(name_library, library);
		  logger.log(Level.INFO,RB.getString("it.ssc.library.RFactoryLibraries.msg1")+" " +name_library+" "+ path_library);
		  return library;
	  }
	  
	  // Da implementare per uso user
	  
	  public void emptyWork() throws InvalidSessionException, InvalidLibraryException {
		  parent_session.generateExceptionOfSessionClose();
		  list_library.get(FmtLibrary.NAME_LIBRARY_WORK).emptyLibrary();
	  }
	  
	  
	  public Library getLibrary(String name_library) throws InvalidSessionException, LibraryNotFoundException {
		  parent_session.generateExceptionOfSessionClose();
		  name_library=name_library.toUpperCase();
		  if(list_library.containsKey(name_library))  {
			  return list_library.get(name_library);
		  }
		  else {
			  throw new LibraryNotFoundException(name_library);
		  }
	  }

	  
	public void removeLibraryFromList(String name_library) throws Exception {
		parent_session.generateExceptionOfSessionClose();
		name_library = name_library.toUpperCase();
		list_library.remove(name_library);
	}
	  
	  public Library addLibrary(String name_library,java.sql.Connection conect ) throws InvalidSessionException, InvalidLibraryException, SQLException  {
		  parent_session.generateExceptionOfSessionClose();
		  name_library=name_library.toUpperCase();
		 
		  if(list_library.containsKey(name_library)) {
			throw new  InvalidLibraryException("ERRORE. Libreria gia assegnata :" +name_library);
		  }
		  
		  DbLibrary library=new DbLibrary(name_library,conect,this);
		  list_library.put(name_library, library);
		  logger.log(Level.INFO,"Assegnata libreria "+name_library+" ");
		  return library;
		  
	  }
	 
	  
	public boolean existLibrary(String name_library) throws InvalidSessionException {
		parent_session.generateExceptionOfSessionClose();
		name_library = name_library.toUpperCase();
		return list_library.containsKey(name_library);
	}

	/**
	 * Essendo questo metodo non nell'interfaccia pubblica ma richiamato solo se si chiude la sessione
	 * e' initile ripulire la lista delle librerie 
	 * 
	 * @throws Exception
	 */
	public void closeAllLibrary() throws Exception {
		parent_session.generateExceptionOfSessionClose();
		for (PLibraryInterface lib : list_library.values()) {
			lib.closeLibraryForTerminateSession();
		}
	}
	  
	private static synchronized String getRandomNameWork(String path_work_root) throws DirectoryNotFound {
		String path_work;
	    File file;
		do {
			Random ra = new Random(new Date().getTime());
			path_work=UtilFile.getPathDirWithSeparatorFinal(path_work_root)+"work_" + Math.abs(ra.nextInt());
			file=new File(path_work);
		}	
		while(file.exists());
		if(!file.mkdir()) throw new DirectoryNotFound("ERRORE ! Impossibile creare "+file.getAbsolutePath());;
		return path_work;
	}
}
