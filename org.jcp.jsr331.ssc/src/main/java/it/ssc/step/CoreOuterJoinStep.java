package it.ssc.step;


import java.util.logging.Logger;
import it.ssc.context.SessionIPRIV;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.pdv.MergePDV;
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

public class CoreOuterJoinStep {
	
	private static final Logger logger=SscLogger.getLogger();
	
	protected Input input_ref1;
	protected Input input_ref2;
	protected OptionsRead opt_read;
	protected OptionsWrite opt_write;
	protected OptionsTrasformation opt_trasf;
	protected SessionIPRIV parent_session;
	protected OutputRefInterface output_ref;
	protected boolean execute=false;
	private final static boolean EXEC_JOIN=true; 
	private final static boolean NO_EXEC_JOIN=false; 
	private Input ref_created;
	
	public Object execute() throws  Exception {
		
		this.parent_session.generateExceptionOfSessionClose(); 
		if(execute==false )  {
			execute=true;
		}
		else {
			throw new ErrorStepInvocation("ERRORE ! Questo passo di merge e' stato gia invocato. ");
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

		ReadData read_data1 = new ReadData(input_ref1, opt_read);
		ReadData read_data2 = new ReadData(input_ref2, opt_read); 
		//Crea i pdv 
		PDV pdv1 = read_data1.createPDV();
		PDV pdv2 = read_data2.createPDV();
		PDV pdv_out=MergePDV.createPDVMerge(pdv1,pdv2);
			
		TrasformationData trasf_data = new TrasformationData(pdv_out,opt_trasf,parent_session.getPathCompiler());
		WriteData write_data = null;
		SourceDataInterface source1 = null;
		SourceDataInterface source2 = null;
		boolean data_step_error=false;
		Object return_object=null;
		try {
			
			write_data = new WriteData(pdv_out,output_ref,opt_write);
			source1 = read_data1.getSourceData();
			source1.setLogActive(false);
			while (source1.readFromSourceWriteIntoPDV(pdv_out)) {
				source2 = read_data2.getSourceData();
				source2.setLogActive(false);
				boolean is_equi_join=false;
				while (source2.readFromSourceWriteIntoPDV(pdv_out)) {
					trasf_data.inizializePDV(pdv_out);
					trasf_data.trasformPDV(pdv_out,EXEC_JOIN);   
					if(!is_equi_join) is_equi_join= trasf_data.isEquiJoin();
					write_data.readFromPDVWriteOutput(pdv_out);   
				}	
				if(!is_equi_join) {
					source2.readNullFromSourceWriteIntoPDV(pdv_out);
					trasf_data.inizializePDV(pdv_out);
					trasf_data.trasformPDV(pdv_out,NO_EXEC_JOIN);   
					write_data.readFromPDVWriteOutput(pdv_out);   
				}
				source2.close();
			}  
			source1.close();
			return_object=trasf_data.getReturnObject();
		} 
		catch(Exception e) {
			data_step_error=true;
			throw e;
		}
		finally {
			if (write_data != null) write_data.close(data_step_error,pdv_out);
		}
		long end=System.currentTimeMillis();
		logger.log(SscLevel.TIME,"Durata passo di cross in "+(end-start)+" millisecondi.");
		ref_created=write_data.getDataRefCreated();
		return return_object;
	}
	
	public Input getDataRefCreated() {
		return ref_created;
	}
}
