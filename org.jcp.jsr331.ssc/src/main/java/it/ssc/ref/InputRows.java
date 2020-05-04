package it.ssc.ref;

import java.util.ArrayList;
import java.util.logging.Logger;

import it.ssc.context.Config;
import it.ssc.log.SscLogger;
import it.ssc.metadata.FieldInterface;
import it.ssc.parser.InputDichiarationInfo;
import it.ssc.parser.ParserInformatObject;
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

public class InputRows implements Input {

	private static final Logger logger=SscLogger.getLogger(); 
	
	private static final TYPE_REF type_ref = TYPE_REF.REF_OBJECT;
	private InputDichiarationInfo  info_input;
	private String token_missing;
	private ArrayList<Object[]> dati;
	
	{
		token_missing=Config.TOKEN_MISSING;
		dati=new ArrayList<Object[]>();
	}

	
	public InputRows()  {
		
	}
	
	public void addRow(Object... input) {
		dati.add(input);
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
		/*un oggetto con tutte le informazioni per effettuare la parte  */
		/*di input di un passo di step                                  */
		
		ParserInformatObject parse_string=new ParserInformatObject(); 
		parse_string.parser(informat_string);
		info_input = parse_string.createInputDichiarationInfo(); 
	}

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
	
	public void renameVarToLoad(String new_name,String old_name) throws Exception {
		info_input.renameInputVarIn(new_name,old_name);
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
	
	public ArrayList<Object[]> getListObject() {
		return  dati;
	}
	
	
	/*
	public InputObject clone() {
		InputObject clone=null;
		try {
			clone=(InputObject)super.clone();
			clone.info_input=this.info_input.clone();
		} 
		catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,"Clonazione InputString",e);
		}
		return clone;
	}
	*/
	
}

