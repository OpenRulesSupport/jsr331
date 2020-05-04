package it.ssc.ref;

import java.io.StringReader;
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
 * L'oggetto <code>InputString</code> permette di creare una sorgente dati da una 
 * stringa. Attraverso questo  oggetto  si definisce anche  il tracciato
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

public class InputString implements Input , Cloneable {

	private static final Logger logger=SscLogger.getLogger();
	
	private static final TYPE_REF type_ref = TYPE_REF.REF_STRING;
	private InputDichiarationInfo  info_input;
	
	private String data_string;
	private Pattern separator;
	private String token_missing;
	private int index_create_line=0;
	
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
	public InputString(String data_string) {
		this.data_string=data_string;
	}

	
	/**
	 * Permette di specificare il formato di input da passare come parametro .
	 * Gia a questo livello possiamo pensare di effettuare il parsing della stringa 
	 * di informats per recuperare il formato di lettura 
	 * 
	 * @param informat_string formato di lettura 
	 */
	
	public InputString setInputFormat(String informat_string) throws InvalidInformatStringException {
		/*Creare un oggetto ParseInformatString che andra a popolare    */
		/*un oggetto con tutte le informazioni per effettuare la parte  */
		/*di input di un passo di step                                  */
		
		ParserInformatString parse_string=new ParserInformatString();
		parse_string.parser(informat_string);
		info_input = parse_string.createInputDichiarationInfo(); 
		return this;
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
	
	public Pattern getSeparator() {
		return separator;
	}
	
	public void renameVarToLoad(String new_name,String old_name) throws Exception {
		info_input.renameInputVarIn(new_name,old_name);
	}

	public void setSeparator(Pattern separator) {
		this.separator = separator;
	}
	
	public void setMissingValue(String input_missing) {
		this.token_missing=input_missing;
	}
	
	public String getMissingValue() {  
		return this.token_missing;
	}
	
	//non ha effetti, in quanto non carica nulla in memoria 
	public void close() {
		
	}
	
	public InputString setIndexForNewLine(int index_create_line) {
		this.index_create_line = index_create_line;
		return this;
	}
	
	public StringReader getStringReader() {
		if(this.index_create_line > 0)  {
			String new_data_string=divide(this.data_string,this.index_create_line);
			return new StringReader(new_data_string);
		}
		else {			
			return new StringReader(this.data_string);
		}	
	}
	
	
	public InputString clone() {
		InputString clone=null;
		try {
			clone=(InputString)super.clone();
			clone.info_input=this.info_input.clone();
		} 
		catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,"Clonazione InputString",e);
		}
		return clone;
	}
	
	
	private static String divide(String to_divide,int index_sep) {
		StringBuffer final_string=new StringBuffer();
		int block=1;
		int start_index=0;
		int end_index=index_sep;
		String appo=null;
		int lunghezza=to_divide.length();
		if(to_divide!=null) {
			while(end_index<=lunghezza)  {
				appo=to_divide.substring(start_index, end_index);
				final_string.append(appo);
				final_string.append(Config.NL);
				block+=1;
				start_index=end_index;
				end_index=index_sep*block;
			}
			if((index_sep*(block-1))!=lunghezza) logger.log(Level.WARNING,"Attenzione ! Esistono caratteri non letti sull'ultimo"+
					                                         " record logico. Lunghezza ultimo record insufficiente.");
		}
		
		return final_string.toString();
	}
}
