package it.ssc.step;

import java.util.logging.Logger;

import it.ssc.context.SessionIPRIV;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.pdv.PDV;
import it.ssc.ref.Input;
import it.ssc.ref.OutputRefInterface;
import it.ssc.step.exception.ErrorStepInvocation;
import it.ssc.step.readdata.OptionsRead;
import it.ssc.step.readdata.ReadData;
import it.ssc.step.readdata.SourceDataInterface;
import it.ssc.step.trasformation.OptionsTrasformation;
import it.ssc.step.trasformation.TrasformationData;
import it.ssc.step.writedata.OptionsWrite;
import it.ssc.step.writedata.WriteData;

public class CoreProcess {
	
	private static final Logger logger=SscLogger.getLogger();
	
	protected Input input_ref; 
	protected OptionsRead opt_read;
	protected OptionsWrite opt_write;
	protected OptionsTrasformation opt_trasf;
	protected SessionIPRIV parent_session;
	protected OutputRefInterface output_ref;
	protected boolean execute=false;
	private Input ref_created;
	
	public Object execute() throws  Exception {
		
		this.parent_session.generateExceptionOfSessionClose(); 
		if(execute==false )  {
			execute=true;
		}
		else {
			throw new ErrorStepInvocation("ERRORE ! Questo passo di data e' stato gia' invocato. ");
		}
		
		/**
		 * LA SUDDIVISIONE TRA LE TRE FASI (READ;TRASF;WRITE): passiamo da un
		 * array di byte ad un oggetto PDV. PDV deve leggere tutti le
		 * informazioni in input selezionata dalla sorgente. PDV deve contenere
		 * i nomi dei campi ed i valori memorizzati come primitivi tranne la
		 * String che e' un oggetto. In fase di trasformazione posso essere
		 * aggiunti campi che rimangono nel PDV, se non esistono. E lo stesso
		 * PDV che gestisce gli array di byte per la gestione dei null;
		 * 
		 */
		long start=System.currentTimeMillis();

		ReadData read_data = new ReadData(input_ref, opt_read);
		//Crea il pdv 
		PDV pdv = read_data.createPDV();
		//trasforma e aggiunge nel pdv variabili di tipo declare
		//Deve controllare che non siano gia presenti nel PDV 
		TrasformationData trasf_data = new TrasformationData(pdv,opt_trasf,parent_session.getPathCompiler());
		
		WriteData write_data = null;
		SourceDataInterface source = null;
		Object return_object=null;
		boolean data_step_error=false; 
		
		try {
			//gli viene passata l'informazione delle variabili keep o drop dal opt_write
			write_data = new WriteData(pdv,output_ref,opt_write);
			source = read_data.getSourceData();
			while (source.readFromSourceWriteIntoPDV(pdv)) {  
				trasf_data.inizializePDV(pdv);
				trasf_data.trasformPDV(pdv);   
				write_data.readFromPDVWriteOutput(pdv);   
			}
			return_object=trasf_data.getReturnObject(); 
		} 
		catch(Exception e) {
			data_step_error=true;
			throw e;
		}
		finally {
			if (source != null) source.close();
			if (write_data != null) write_data.close(data_step_error,pdv);
		}
		
		long end=System.currentTimeMillis();
		logger.log(SscLevel.TIME,"Durata passo di data in "+(end-start)+" millisecondi");
		ref_created=write_data.getDataRefCreated();
		return return_object;
	}

	public Input getDataRefCreated() {
		return ref_created;
	}
}
