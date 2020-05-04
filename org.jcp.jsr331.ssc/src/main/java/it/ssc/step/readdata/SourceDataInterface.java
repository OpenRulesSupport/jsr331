package it.ssc.step.readdata;

import it.ssc.pdv.PDVAll;
import it.ssc.step.exception.ErrorStepInvocation;

public interface SourceDataInterface  {
	
	public boolean readFromSourceWriteIntoPDV(PDVAll pdv)  throws Exception;
	public void close() throws Exception;
	public void setLogActive(boolean active);
	public void readNullFromSourceWriteIntoPDV(PDVAll pdv)  throws ErrorStepInvocation; 
}
