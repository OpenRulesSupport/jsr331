package it.ssc.step.writedata;

import java.io.*;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.ssc.io.DataOutputStreamFormat;
import it.ssc.log.SscLogger;
import it.ssc.pdv.PDVField;
import it.ssc.pdv.PDVKeep;
import it.ssc.ref.Input;
import it.ssc.ref.InputRefFmt;
import it.ssc.ref.OutputRefFmt;
import it.ssc.ref.OutputRefInterface;
import it.ssc.step.exception.InvalidDichiarationOptions;

final class WriteDataToFMT implements WriteDataInterface {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private ManagementWritingFileFmt manage_file;
	private long obs=0;
	private OutputRefFmt output_ref;
	private byte[] arrray_missing_values; 
	private int num_var_writed;
	private boolean error=false;

	WriteDataToFMT(OutputRefInterface out_ref, OptionsWrite opt_write,PDVKeep pdv)	throws Exception {
		this.output_ref = (OutputRefFmt) out_ref;
		//ATTENZIONE GESTIRE l'APPEND IN UN DATASET DI APPOGGIO E FARE RENAME 
		manage_file=new ManagementWritingFileFmt(output_ref,opt_write,pdv.getListFieldKeep());
		arrray_missing_values=ManagerMissingValues.createByteArray(pdv.getSizeFieldKeep());
	}

	public void readFromPDVWriteOutput(PDVKeep pdv) throws IOException, InvalidDichiarationOptions {
		try {			
			if(pdv.isRecordDeleted()) return;
			ManagerMissingValues.resetArray(arrray_missing_values);
			num_var_writed=0;		
			DataOutputStreamFormat file_out_data=manage_file.getDataOutpuStream();
					
			for(PDVField<?> pdv_field:pdv.getListFieldKeep())  { 
		
				num_var_writed++;
				
				if(pdv_field.is_null) ManagerMissingValues.setMissingToArray(arrray_missing_values, num_var_writed);
				
				if (pdv_field.type == String.class) {
					file_out_data.writeUTFfromField((PDVField<String>)pdv_field) ;
				} 
				else if (pdv_field.type == StringBuffer.class) {
					file_out_data.writeCharsFromField((PDVField<StringBuffer>)pdv_field);
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
			logger.log(Level.SEVERE,"Il dataset di output "+output_ref.getNameComplete()+ " puo essere incompleto. ");
			error=true;
			throw e;
		}
	}
	
	// il parametro error_data_stpe e true se si e' verificato un errore in fase di datastep
	// lo si passsa da fuori periche questo puo' esser causato anche da errori di lettura. 
	public void close(boolean error_data_stpe,PDVKeep pdv) throws Exception {
		long new_obs=obs;
		if(error_data_stpe) error=true;
		if( manage_file.isAppend()) {
			new_obs=obs+manage_file.getOldNumberObs();
		}
		if (manage_file != null)  manage_file.close(error,new_obs,pdv);
		if(!error) logger.log(Level.INFO,"Numero di osservazioni scritte sul dataset "+output_ref.getNameComplete()+": "+obs);
		if(!error && manage_file.isAppend()) logger.log(Level.INFO,"Il dataset "+output_ref.getNameComplete()+" e' stato aggiornato ed ha il seguente numero di record : "+new_obs);
		
		
	}
	
	
	public Input getDataRefCreated() throws Exception {
		return new InputRefFmt(this.output_ref.getLibrary(),this.output_ref.getNameTable());
	}
	
}
