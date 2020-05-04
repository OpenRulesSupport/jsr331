package it.ssc.ref;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import it.ssc.context.Config;
import it.ssc.log.SscLogger;
import it.ssc.metadata.FieldInterface;
import it.ssc.parser.InputDichiarationInfo;
import it.ssc.parser.ParserInformatString;
import it.ssc.parser.exception.InvalidInformatStringException;



/**
 * L'oggetto <code>InputDataTextFile</code> permette di creare  un  riferimento ad un
 * file di dati esterno. Attraverso questo  oggetto  si definisce anche  il tracciato
 * record (informat input) dei  dati  da  leggere da tale sorgente dati  e in
 * quali tipi di dati (int, double, etc)  verranno  memorizzati tali informazioni.
 * Sara' poi compito dei passi di datastep effettuare materialmente l'azione di
 * lettura dei dati e/o scrittura su un'altro tipo di stream, ma la definizione del  
 * tracciato record e il nome dei campi e il file da leggere, oltre alla suo path,  
 * vengono definiti in questa classe
 * <br>
 * <br>
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * @since version 1.0      
 */

public class InputFile implements Input , Cloneable {
	
	private static final Logger logger=SscLogger.getLogger();

	private static final TYPE_REF type_ref = TYPE_REF.REF_FILE;
	private InputDichiarationInfo  info_input;
	private File file;
	private Pattern separator;
	private String token_missing;
	
	{
		separator=Pattern.compile("\\s+");
		token_missing=Config.TOKEN_MISSING;
	}

	/**
	 * @param path_file
	 *            Nome del file con path completo da referenziare
	 * 
	 *  @throws IOException Nel caso ci siano problemi di scrittura 
	 */
	public InputFile(String path_file)  throws java.io.IOException {
		this.file=new File(path_file);
	}

	public InputFile(File file)  throws java.io.IOException {
		this.file=file;
	}
	/**
	 * Permette di specificare il formato di input da passare come parametro .
	 * Gia a questo livello possiamo pensare di effettuare il parsing della stringa 
	 * di informats per recuperare il formato di lettura 
	 * 
	 * @param informat_string formato di lettura 
	 */
	
	public void setInputFormat(String informat_string) throws InvalidInformatStringException {
		/*Creare un oggetto ParseInformatString che andra a popolare    */
		/*un oggetto con tutte le informazioni per effettuare la parte */
		/*di input di un passo di step*/
		
		ParserInformatString parse_string=new ParserInformatString();
		parse_string.parser(informat_string);
		info_input = parse_string.createInputDichiarationInfo(); 
	}
	
		
	/**
	 * 
	 * @return 	Ritorna il tipo di riferimento (TYPE_REF.REF_FILE)
	 */

	public TYPE_REF getTypeRef() {
		return type_ref;
	}

	public int getColumnCount() {
		return info_input.getColumnCount();
	}

	public String getColumnName(int index_column) {
		return info_input.getColumnName(index_column);
	}

	public FieldInterface getField(int index_column) {
		return info_input.getField(index_column);
	}
	
	public InputDichiarationInfo getInputDichiarationInfo() {
		return info_input;
	}
	
	public File getFile()  {
		return file;
	}
	
	public Pattern getSeparator() {
		return separator;
	}
	
	public void renameVarToLoad(String new_name,String old_name) throws Exception {
		info_input.renameInputVarIn(new_name,old_name);
	}

	public InputFile setSeparator(Pattern separator) {
		this.separator = separator;
		return this;
	}
	
	public InputFile setSeparator(Character separator) {
		this.separator = Pattern.compile(separator.toString());
		return this;
	}
	
	public void setMissingValue(String input_missing) {
		//if(token_miss==null) throw new InvalidStringForInputMissing("ERRORE ! La stringa identificativa dei token di input da considerare mancanti e a null ");
		this.token_missing=input_missing;
	}
	
	public String getMissingValue() {
		return this.token_missing;
	}
	
	//non ha effetti, in quanto non carica nulla in memoria 
	public void close() {
		
	}
	
	
	
	public InputFile clone() {
		InputFile clone=null;
		try {
			clone=(InputFile)super.clone();
			clone.file=new File(this.file.toURI());
			clone.info_input=this.info_input.clone();
			
		} 
		catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE," Clonazione InputDataTextFile",e);
		}
		return clone;
	}
}