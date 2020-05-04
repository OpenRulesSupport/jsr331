package it.ssc.pdv;


import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.step.exception.InvalidDichiarationOptions;
import it.ssc.util.ArrayMap;
import java.util.ArrayList;


/**
 * 
 * Questo classe deve  gestire il  PDV (Program  Data  Vector), nel PDV  devono 
 * essere presenti le variabili che verranno lette dall'input e andranno scritte 
 * nell'output, quindi anche comprensive di nuovi campi che saranno create nella 
 * fase di DECLARE. Il pdv durante la  scrittura dei dataset nativi   SSC 
 * deve includere il numero di byte iniziale per la gestione dei null (array di byte).
 * Se il numero delle variabili e' minore o uguale a otto sara' sufficente un byte, 
 * se saranno < 16 basteranno 2 byte , e cosi via. Inizialmente  questi  byte 
 * saranno tutti impostati a 0x00; per assegnare eventualmente che nel primo blocco 
 * la settima variabile e' a null si impostera' 0x00 OR 2^7, per verificare invece 
 * se nel PDV c'e' una variabile a null, occorre che byte[0] AND 2^7 = 2^7;
 * <br><br>
 * La parte dati del PDV e' costituita da un hashmap ordinata, le cui chiavi sono 
 * i nomi dei campi.
 * Ad ogni chiave (nome del campo) e' associato un oggetto con le seguenti 
 * informazioni : 
 * <br>
 * 1) Nome del campo (ripetuto dalla chiave)
 * <br>
 * 2) Tipo del campo (di tipo class che e' possibile,utilizzare anche per   primitivi, 
 *    
 * <br>   
 * 3) Valore del dato su quel record, in prativa il valore del campo. Questo attributo 
 *    deve essere dello stesso tipo del dato letto.     
 * 
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * 
 */

public class PDV extends Wrapper implements PDVKeep, PDVAll {
	
	
	private ArrayMap<String,PDVField<?>> pdv_array;
	private ArrayList<PDVField<?>> pdv_list_keep;
	private boolean record_deleted;
	private String token_missing=null;
	
	
	
	public PDV() {
		pdv_array=new ArrayMap<String,PDVField<?> >();
		record_deleted=false;
	}
	
	public <T> PDVField<T> addNewField(String key, Class<T> type) throws InvalidDateFormatException   {
		PDVField<T> pdvf=new PDVField<T>(key,type,token_missing);
		pdv_array.put(key, pdvf);
		return pdvf;
	}
	
	public void setTokenMissing(String token_miss) {
		this.token_missing=token_miss;
	}
	
	
	public  PDVField<?> getField(String key) {
		return pdv_array.get(key);
	}
	
		
	public int getSizeFieldKeep() throws InvalidDichiarationOptions  {
		if(pdv_list_keep==null) {
			createArrayFieldKeep();
		}
		return pdv_list_keep.size();
	}
	
	public ArrayList<PDVField<?>> getListFieldKeep() throws InvalidDichiarationOptions {
		if(pdv_list_keep==null) {
			createArrayFieldKeep();
		}
		return pdv_list_keep;
	}
	
	private  void createArrayFieldKeep() throws InvalidDichiarationOptions  {
		Iterable<PDVField<?>> iterable =pdv_array.getIterableValues();
		pdv_list_keep=new ArrayList<PDVField<?>>();
		for (PDVField<?> pdv_field:iterable) {
			if(!pdv_field.drop) {
				pdv_list_keep.add(pdv_field);
			}
		}
		if(pdv_list_keep.isEmpty()) { 
			throw new InvalidDichiarationOptions("ERRORE ! Tutte le variabili sono state eliminate con"+
			 " l'opzione KEEP/DROP. Impossibile creare il dataset di output.");
		}		                             
	}
	
	
	public PDVField<?> getField(int index) {
		return pdv_array.get(index);
	}
	
	public int getSize() {
		return pdv_array.getSize();
	}
	
	public boolean isRecordDeleted() {
		return record_deleted;
	}

	public void setRecordDeleted(boolean record_del) {
		this.record_deleted = record_del;
	}
	
	public void resetAllVarToDropValue(boolean value) {
		
		Iterable<PDVField<?>> iterable =pdv_array.getIterableValues();
		for (PDVField<?> pdv_field:iterable) {
			pdv_field.drop=value;
		}
	}
	
}
