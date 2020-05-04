package it.ssc.step.readdata;


import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.pdv.PDV;
import it.ssc.ref.Input;

public class ReadData  {

	private ReadDataInterface read_data ;
	
	public ReadData(Input ref, OptionsRead opt_read) throws Exception {
		
		if(ref.getTypeRef()==Input.TYPE_REF.REF_SSC) {
			read_data=new ReadDataFromFmt(ref,opt_read); 
		}
		else if(ref.getTypeRef()==Input.TYPE_REF.REF_FILE) { 
			read_data=new ReadDataFromFile(ref,opt_read);
		}
		else if(ref.getTypeRef()==Input.TYPE_REF.REF_DB) {
			read_data=new ReadDataFromDB(ref,opt_read);
		}
		else if(ref.getTypeRef()==Input.TYPE_REF.REF_MEMORY) {
			read_data=new ReadDataFromFmtMemory(ref,opt_read);
		}
		else if(ref.getTypeRef()==Input.TYPE_REF.REF_STRING) {
			read_data=new ReadDataFromString(ref,opt_read);
		}
		else if(ref.getTypeRef()==Input.TYPE_REF.REF_OBJECT) {
			read_data=new ReadDataFromObject(ref,opt_read);
		}
	}
	
	/**
	 * Questo medoto deve ritornare il numero di byte di cui e costituito il pdv che
	 * verra' poi scritto nello stream di output. Il valore int restituito deve 
	 * considerare anche i byte iniziali per la gestione dei null; 
	 * 
	 * @return il numero di byte del pdv (program data vector)
	 */
	
	/*
	public int getBytesPDV() {
		return 3; 
	}
	*/
	
	/*L'oggetto source interface Deve essere il sorgente dati */
	
	public SourceDataInterface getSourceData() throws Exception {
		return read_data.getSourceData();
	}
	
	public PDV createPDV() throws InvalidDateFormatException  {
		return read_data.createPDV(); 
	}
}
