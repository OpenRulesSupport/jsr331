package it.ssc.step.writedata;

import it.ssc.pdv.PDV;
import it.ssc.pdv.PDVKeep;
import it.ssc.ref.Input;
import it.ssc.ref.OutputRefInterface;

public class WriteData  {
	WriteDataInterface write_data;
	
	public WriteData(PDV pdv,OutputRefInterface ref,OptionsWrite opt_write) throws Exception  {
		
		ControlsOptionsKeep.controls(pdv, opt_write);
		ControlsOptionsKeep.controlsKeepPrintf(pdv, opt_write);
		
		if(ref.getTypeRef()==OutputRefInterface.TYPE_REF.REF_FMT) {
			write_data=new WriteDataToFMT(ref,opt_write,pdv);
		}
		else if(ref.getTypeRef()==OutputRefInterface.TYPE_REF.REF_DB) {
			write_data=new WriteDataToDB(ref,opt_write,pdv);
		}
		else if(ref.getTypeRef()==OutputRefInterface.TYPE_REF.REF_FILE) {
			write_data=new WriteDataToFile(ref,opt_write,pdv);
		}
		else if(ref.getTypeRef()==OutputRefInterface.TYPE_REF.REF_FMT_MEMORY) {
			write_data=new WriteDataToFMTMemory(ref,opt_write,pdv);
		}
		
	}
	
	public void readFromPDVWriteOutput(PDVKeep pdv) throws Exception { 
		write_data.readFromPDVWriteOutput(pdv);
	}
	public void close(boolean error_data_step,PDVKeep pdv) throws Exception {
		try {
			write_data.close(error_data_step,pdv);
		}
		catch(Exception e)  { 
			if(!error_data_step) throw e;
		}
	}
	
	public Input getDataRefCreated() throws Exception{
		return write_data.getDataRefCreated() ;
	}
}
