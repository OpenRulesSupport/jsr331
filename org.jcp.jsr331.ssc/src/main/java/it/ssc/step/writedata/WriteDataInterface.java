package it.ssc.step.writedata;


import it.ssc.pdv.PDVKeep;
import it.ssc.ref.Input;

public interface WriteDataInterface {

	public void readFromPDVWriteOutput(PDVKeep pdv) throws Exception;
	
	public void close(boolean data_step_error,PDVKeep pdv) throws Exception;
	
	public Input getDataRefCreated() throws Exception;
   
}
