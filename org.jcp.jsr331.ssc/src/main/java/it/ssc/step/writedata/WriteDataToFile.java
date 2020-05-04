package it.ssc.step.writedata;

import it.ssc.log.SscLogger;
import it.ssc.pdv.PDV;
import it.ssc.pdv.PDVField;
import it.ssc.pdv.PDVKeep;
import it.ssc.ref.Input;
import it.ssc.ref.OutputRefFile;
import it.ssc.ref.OutputRefInterface;
import it.ssc.step.RFileProcess;
import it.ssc.step.exception.InvalidDichiarationOptions;
import it.ssc.util.Formatter;

import java.io.*;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

final  class WriteDataToFile implements WriteDataInterface {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private OutputRefFile output_ref;
	private Formatter pwriter_out;
	private long obs=0;
	private String[] var_to_print;
	private String format_print;
	private Object[] value_var;
	private boolean error=false;
	private boolean append;
	private String new_line="";
	
	
	WriteDataToFile(OutputRefInterface out_ref, OptionsWrite opt_write, PDVKeep pdv) throws InvalidDichiarationOptions, IOException {
		
		//Controllare che printf sia stata dichiarata e non sia vuota
		this.output_ref = (OutputRefFile) out_ref;
		append=opt_write.isAppendOutput(); 
		
		BufferedWriter buff=new BufferedWriter(new FileWriter(output_ref.getFile(),append));
		if(opt_write.getLocale()!=null) this.pwriter_out = new Formatter(buff,opt_write.getLocale());
		else this.pwriter_out = new Formatter(buff);
		this.pwriter_out.setNullPrintable(opt_write.getOutputTokenMissing());
		
		this.var_to_print=opt_write.getVarToPrint();
		this.format_print=opt_write.getStringFormatPrintf();
		if(this.format_print.equals(RFileProcess.PRINT_ALL))  {
			this.format_print=formatsForAllVar(pdv);
			this.var_to_print=arrayAllVarName(pdv);
		}
		
		this.printHeader(opt_write.getHeaderPrintfFile());
		this.value_var=new Object[this.var_to_print.length];
	}
	
	
	private void printHeader(String header) {
		
		if(header !=null && header.equals(RFileProcess.PRINT_ALL_HEADER)) {
			String list_var="";
			for(String name_var:this.var_to_print) {
				list_var+=" "+name_var;
			}
			this.pwriter_out.format(list_var+"%n", (Object)null);
		}
		else if(header !=null)  {
			this.pwriter_out.format(header+"%n", (Object)null);
		}
	}

	public void readFromPDVWriteOutput(PDVKeep pdv) throws Exception {
		PDV pdv_total=(PDV)pdv;
		try  {
			if(pdv_total.isRecordDeleted()) return;
			
			for(int a=0; a< this.var_to_print.length; a++) {
				PDVField pdv_field=pdv_total.getField(this.var_to_print[a]);
				//si potrebbe mettere se e' una stringa ".", pero' poi come si fa con
				// i numeri ? 
				if(pdv_field.is_null)  {
					value_var[a]=null;
				}
				else {
					value_var[a]=pdv_field.value_generics;
				} 	
			} 	
			
			pwriter_out.format(new_line +format_print, value_var);
			new_line="%n";
		} 
		catch (Exception e) {
			logger.log(Level.SEVERE,"Il file di output "+output_ref.getFile().getAbsolutePath()+ " puo essere incompleto");
			error=true;
			throw e;
		}
		obs++;
	}

	public void close(boolean error_data_step, PDVKeep pdv) throws Exception {
		if(!error && !error_data_step) { 
			if(!append) {
				logger.log(Level.INFO,"Numero di osservazioni scritte sul file "+output_ref.getFile().getAbsolutePath()+ " :"+obs);
			}
			else {
				logger.log(Level.INFO,"Il file "+output_ref.getFile().getAbsolutePath()+ "e' stato aggiornato con il seguente numero di osservazioni :"+obs);
			}
		}
		if(pwriter_out!=null) pwriter_out.close();
	}
	

	
	private String formatsForAllVar(PDVKeep pdv) throws InvalidDichiarationOptions {
		String format="";
		PDV pdv_total=(PDV)pdv;
		int num_var=pdv_total.getSize();
		for(int a=0;a<num_var;a++) {
			format+="%s ";
		}
		return format;
	}
	
	private String[] arrayAllVarName(PDVKeep pdv) throws InvalidDichiarationOptions {
		PDV pdv_total=(PDV)pdv;
		int num_var=pdv_total.getSize();
		String[] vars=new String[num_var];
		
		for(int a=0;a<num_var;a++) {
			vars[a]=pdv_total.getField(a).getName();
		}
		return vars;
	}
	
	public Input getDataRefCreated() {
		return null;
	}
}
