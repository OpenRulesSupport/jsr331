package it.ssc.step.readdata;

import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.pdv.PDV;

public interface ReadDataInterface {
	
	public SourceDataInterface getSourceData() throws Exception;
	public PDV createPDV() throws InvalidDateFormatException; 
	
}
