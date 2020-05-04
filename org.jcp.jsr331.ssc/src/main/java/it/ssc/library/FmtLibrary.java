package it.ssc.library;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.ssc.context.Config;
import it.ssc.dataset.exception.InvalidNameDataset;
import it.ssc.io.DirectoryNotFound;
import it.ssc.io.UtilFile;
import it.ssc.library.exception.DatasetExistingException;
import it.ssc.library.exception.DatasetNotFoundException;
import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.library.exception.RenameDatasetException;
import it.ssc.log.SscLogger;
import it.ssc.ref.Input;
import it.ssc.ref.InputRefFmt;
import it.ssc.util.TestValidName;

public class FmtLibrary extends GenericLibrary  implements PLibraryInterface {
	
	private static final Logger logger=SscLogger.getLogger();
	private String path;
	
	FmtLibrary(String name,String path,RFactoryLibraries fact_lib) throws DirectoryNotFound, InvalidLibraryException {
		
		if(!UtilFile.existDirectory(path))  {
			throw new DirectoryNotFound("ERRORE. Directory non trovata:"+path);
		}
		if(!TestValidName.isValidNameLibrary(name))  {
			throw new InvalidLibraryException("ERRORE. Nome libreria non corrretto :"+name+ ", usare i caratteri consentiti.");
		}
		this.path=path;
		this.name=name;
		this.factrory_lib=fact_lib;
		this.type=TYPE_LIBRARY.LIBRARY_SSC;
		this.produtc_name=PRODUCT_NAME.SSC_STAT.getValue();
	}
	
	public String getUrl()  {
		return getAbsolutePath();
	}
		
	public String getAbsolutePath() {
		return path;
	}
	
	
	public Input getInput(String name_table) throws Exception  {
		this.generateExceptionOfLibraryClose();
		return new InputRefFmt(this,name_table);
	}
	
	public String[] getListTable(String[] type_table_no_applicable) throws InvalidLibraryException  {
		return  this.getListTable();
	}
	
	public String[] getListTable() throws InvalidLibraryException  {
		this.generateExceptionOfLibraryClose();
		
		File dir_lib=new File(path);
		ArrayList<String> list_file_fmt=new ArrayList<String>();
		for (File file_lib: dir_lib.listFiles() ) {
			String path=file_lib.getAbsolutePath();
			String path_file_meta=null;
			if(path.endsWith(Config.ESTENSION_FILE_FMT ))  {
				path_file_meta=path.replace(Config.ESTENSION_FILE_FMT, Config.ESTENSION_META_FMT);
				File file_meta=new File(path_file_meta);
				if(file_meta.exists()) {
					String name_table=file_meta.getName().replace(Config.ESTENSION_META_FMT,"");
					list_file_fmt.add(name_table);
				}	
			}
		}	
		
		String[] list_table=new String[list_file_fmt.size()];
		return list_file_fmt.toArray(list_table);
	}
	
	public boolean existTable (String name_table) throws InvalidLibraryException   { 
		this.generateExceptionOfLibraryClose();
		for(String name_table_fmt:this.getListTable()) {
			if(name_table_fmt.equals(name_table))  { 
				return true;
			}
		}
		return false;
	}
	

	public void close() throws Exception {
		if(this.is_close) return;
		if(!name.equals(NAME_LIBRARY_WORK))  this.factrory_lib.removeLibraryFromList(this.name);
		this.is_close=true;
	}
	
	public void closeLibraryForTerminateSession()  {
		if(this.is_close) return;
		if(name.equals(NAME_LIBRARY_WORK)) deletePhificalLibrary();
		this.is_close=true;
	}
	
	private void deletePhificalLibrary() {
		File dir_lib = new File(path);
		for (File file_lib : dir_lib.listFiles()) {
			file_lib.delete();
		}
		dir_lib.delete();
	}
	
	public void renameTable(String new_name,String old_name) throws Exception {
		this.generateExceptionOfLibraryClose();
		
		boolean test_name_ds=TestValidName.isValidNameDataset(new_name);
		if(!test_name_ds) throw new InvalidNameDataset(new_name);
		
		if(!this.existTable(old_name)) {
			throw new DatasetNotFoundException("ERRORE ! la tabella "+old_name+" non esiste.");
		}
		
		if(this.existTable(new_name)) {
			throw new DatasetExistingException("ERRORE ! il nome della tabella "+new_name+"  e' utilizzato  .");
		}
		
		File file_dati=new File(UtilFile.getPathDirWithSeparatorFinal(path)+old_name+Config.ESTENSION_FILE_FMT );
		File file_meta=new File(UtilFile.getPathDirWithSeparatorFinal(path)+old_name+Config.ESTENSION_META_FMT );
		
	
		File new_file_dati=new File(UtilFile.getPathDirWithSeparatorFinal(path)+new_name+Config.ESTENSION_FILE_FMT );
		File new_file_meta=new File(UtilFile.getPathDirWithSeparatorFinal(path)+new_name+Config.ESTENSION_META_FMT );
		
		if(new_file_dati.exists()  )  {
			throw new DatasetExistingException("ERRORE ! il  file "+new_file_dati.getAbsolutePath()+"  esiste gia'.");
		}
		if(new_file_meta.exists()  )  {
			throw new DatasetExistingException("ERRORE ! il  file "+new_file_meta.getAbsolutePath()+"  esiste gia'.");
		}
		
		if(!file_dati.renameTo(new_file_dati)) {
			throw new RenameDatasetException("ERRORE ! Impossibile rinominare "+file_dati.getAbsolutePath());
		}
		
		if(!file_meta.renameTo(new_file_meta)) {
			throw new RenameDatasetException("ERRORE ! Impossibile rinominare "+file_meta.getAbsolutePath());
		}
		logger.log(Level.INFO, "Il dataset "+this.name+"."+old_name +" e' stato rinominato in "+this.name+"."+new_name);
		
	}
	
	public void emptyLibrary() throws InvalidLibraryException {
		this.generateExceptionOfLibraryClose();
		File dir_lib=new File(path);
		for (File file_lib: dir_lib.listFiles() ) {
			String name=file_lib.getName();
			if(name.toLowerCase().endsWith(Config.ESTENSION_FILE_FMT )  || 
			   name.toLowerCase().endsWith(Config.ESTENSION_META_FMT )  ||	
			   name.toLowerCase().endsWith(Config.ESTENSION_COPY_TEMP_FMT )  )  {
				file_lib.delete();
			}
		}	
	}
	
	public void dropTable(String name_table) throws InvalidLibraryException, DatasetNotFoundException, DirectoryNotFound {
		this.generateExceptionOfLibraryClose();
		if(existTable(name_table)) {
			File file_dati=new File(UtilFile.getPathDirWithSeparatorFinal(path)+name_table+Config.ESTENSION_FILE_FMT );
			File file_meta=new File(UtilFile.getPathDirWithSeparatorFinal(path)+name_table+Config.ESTENSION_META_FMT );
			file_dati.delete();
			file_meta.delete();
		}
		else {
			throw new DatasetNotFoundException("ERRORE ! Il dataset "+name+"."+name_table+" non e' stato trovato");
		}
		logger.log(Level.INFO,"Il dataset "+name+"."+name_table+" e' stato eliminato");
	}
	
	public int executeSQLUpdate(String sql) throws SQLException, InvalidLibraryException {
		throw new InvalidLibraryException(
				"ERRORE ! La libreria "	+ this.name + " non e' una libreria relativa ad un database." +
				"Le istruzioni di Insert, Update, Delete e DDL sono consentite solo su RDBMS esterni (ORACLE, DB,Etc). "+
				"Operazione non consentita.");
	}
}
