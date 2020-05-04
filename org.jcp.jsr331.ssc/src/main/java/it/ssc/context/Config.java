package it.ssc.context;

public interface Config {
	
	public static final String NAME_DIR_COMPILER="compiler";
	public static final String NAME_DIR_SORTING="sorting";
	public static final String NAME_DIR_WORK="ssc_work";
	public static final String NAME_DIR_DERBY="ssc_derby";
	public static final String ESTENSION_FILE_FMT = ".sscdati";
	public static final String ESTENSION_META_FMT = ".sscmeta";
	public static final String ESTENSION_COPY_TEMP_FMT = ".copytmp";
	public static final String TOKEN_MISSING = ".";
	public static final String NL = System.getProperty("line.separator");
	
	
	public String getPathWorkArea(); 
	
	/**
	 * Questa chiamata setta il path dell'area di work  per 
	 * le nuove sessioni SSC che verranno create a partire da tale configurazione.  
	 * Quelle create antecedentemente a tale chiamata mantengono la work 
	 * precedentemente definita. 
	 * 
	 * @param path_work
	 */
	public void setPathWorkArea(String path_work); 
	

	public String getPathLocalDb();
	
	/**
	 * Questa chiamata setta il path dove verra memorizzato l'area per il DB locale Derby per  
	 * le nuove sessioni SSC che verranno create a partire da tale configurazione.  
	 * Quelle create antecedentemente a tale chiamata mantengono il db nel path  
	 * precedentemente definita. 
	 * 
	 * @param path_work
	 */

	public void setPathLocalDb(String path_root_db_derby) ;
	
	public  String getPathFileConfig() ;
	
	public Config clone() ;

	
}
