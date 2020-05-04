package it.ssc.pdv;

import it.ssc.pdv.PDVField;
import it.ssc.step.exception.InvalidDichiarationOptions;

import java.util.ArrayList;

public interface PDVKeep {
	
	public int getSizeFieldKeep()  throws InvalidDichiarationOptions;
	
	public ArrayList<PDVField<?>> getListFieldKeep() throws InvalidDichiarationOptions;
	
	public boolean isRecordDeleted() ;

}
