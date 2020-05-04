package it.ssc.step.writedata;

import it.ssc.io.DataOutputStreamFormat;
import it.ssc.log.SscLogger;
import it.ssc.pdv.PDVField;
import it.ssc.pdv.PDVKeep;
import it.ssc.ref.Input;
import it.ssc.ref.InputRefFmtMemory;
import it.ssc.ref.OutputRefFmtMemory;
import it.ssc.ref.OutputRefInterface;
import it.ssc.step.exception.InvalidDichiarationOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteDataToFMTMemory implements WriteDataInterface {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private long obs=0;
	private OutputRefFmtMemory output_ref; 
	private byte[] arrray_missing_values; 
	private int num_var_writed;
	private boolean error=false;
	private ByteArrayOutputStream memory_out;

	WriteDataToFMTMemory(OutputRefInterface out_ref, OptionsWrite opt_write,PDVKeep pdv)	throws Exception {
		this.output_ref = (OutputRefFmtMemory) out_ref;
		arrray_missing_values=ManagerMissingValues.createByteArray(pdv.getSizeFieldKeep());
		memory_out=new ByteArrayOutputStream();
		if(opt_write.isAppendOutput()) {
			logger.log(Level.WARNING," E' attiva' l'opzione di Append a true. Questa non ha effetti nell'operazione di caricamento dei dataset in memoria! Append=false");
		}
	}

	public void readFromPDVWriteOutput(PDVKeep pdv) throws IOException, InvalidDichiarationOptions {
		try {			
			if(pdv.isRecordDeleted()) return;
			ManagerMissingValues.resetArray(arrray_missing_values);
			num_var_writed=0;		
			DataOutputStreamFormat file_out_data=new DataOutputStreamFormat(memory_out);
					
			for(PDVField pdv_field:pdv.getListFieldKeep())  { 
		
				num_var_writed++;
				
				if(pdv_field.is_null) ManagerMissingValues.setMissingToArray(arrray_missing_values, num_var_writed);
				
				if (pdv_field.type == String.class) {
					file_out_data.writeUTFfromField(pdv_field) ;
				} 
				else if (pdv_field.type == StringBuffer.class) {
					file_out_data.writeCharsFromField(pdv_field);
				}
				else if (pdv_field.type == Double.class) {
					file_out_data.writeDouble((Double)pdv_field.value_generics);
				}
				else if (pdv_field.type == GregorianCalendar.class) {
					file_out_data.writeLong(((GregorianCalendar)pdv_field.value_generics).getTimeInMillis());
				}
				else if (pdv_field.type == Integer.class) {
					file_out_data.writeInt((Integer)pdv_field.value_generics);
				}
				else if (pdv_field.type == Short.class) {
					file_out_data.writeShort((Short)pdv_field.value_generics);
				}
				else if (pdv_field.type == Character.class) { 
					file_out_data.writeChar((Character)pdv_field.value_generics);
				}
				else if (pdv_field.type == Float.class) {
					file_out_data.writeFloat((Float)pdv_field.value_generics);
				}
				else if (pdv_field.type == Byte.class) { 
					file_out_data.writeByte((Byte)pdv_field.value_generics);
				}
				else if (pdv_field.type == Boolean.class) { 
					file_out_data.writeBoolean((Boolean)pdv_field.value_generics);
				}
				else if (pdv_field.type == Long.class) { 
					file_out_data.writeLong((Long)pdv_field.value_generics);
				}
			}
			file_out_data.write(arrray_missing_values);
			obs++;
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE,"Il dataset di output puo' essere incompleto. ");
			error=true;
			throw e;
		}
	}
	
	// il parametro error_data_stpe e true se si e' verificato un errore in fase di datastep
	// lo si passsa da fuori periche questo puo' esser causato anche da errori di lettura. 
	public void close(boolean error_data_stpe,PDVKeep pdv) throws Exception { 
		this.output_ref.createMetaData(pdv, obs);
		if(error_data_stpe) error=true;
		if(!error) logger.log(Level.INFO,"Numero di osservazioni scritte sul dataset in memoria: "+obs);
	}
	
	
	public Input getDataRefCreated() throws Exception {
		return new InputRefFmtMemory(this.memory_out.toByteArray(),this.output_ref.getMeta());
	}
}
