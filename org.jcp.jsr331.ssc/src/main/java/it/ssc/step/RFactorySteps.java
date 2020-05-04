package it.ssc.step;

import it.ssc.datasource.DataSource_Impl;
import it.ssc.context.SessionIPRIV;
import it.ssc.datasource.DataSource;
import it.ssc.ref.FactoryInputRefFromLibrary;
import it.ssc.ref.FactoryOutputRefFromLibrary;
import it.ssc.ref.Input;
import it.ssc.ref.InputString;
import it.ssc.ref.InputFile;
import it.ssc.ref.InputRows;
import it.ssc.ref.InputRefDB;
import it.ssc.ref.InputRefDBSQL;
import it.ssc.ref.InputRefFmt;
import it.ssc.ref.InputRefFmtMemory;
import it.ssc.ref.OutputRefDB;
import it.ssc.ref.OutputRefFile;
import it.ssc.ref.OutputRefFmt;
import it.ssc.ref.OutputRefFmtMemory;
import it.ssc.ref.OutputRefInterface;
import it.ssc.step.exception.OverWriteSameTable;


public class RFactorySteps implements FactorySteps {
	private SessionIPRIV parent_session; 
	
	public RFactorySteps(SessionIPRIV parent_session) { 
		this.parent_session=parent_session; 
	}
	
	public DataSource createDataSource(Input input_reference) throws Exception  {
		this.parent_session.generateExceptionOfSessionClose();
		return new DataSource_Impl(input_reference,	parent_session);
	}
	
	public DataSource createDataSource(String name_input_dataset) throws Exception  {
		this.parent_session.generateExceptionOfSessionClose();
		Input input_ref=  FactoryInputRefFromLibrary.createInputRef( name_input_dataset, parent_session);
		return new DataSource_Impl(input_ref,	parent_session);
	}
	
	
	public DataStep createDataStep(String new_dataset, String input_dataset) throws Exception {
		this.parent_session.generateExceptionOfSessionClose();
		OutputRefInterface output_ref=FactoryOutputRefFromLibrary.createOutputRef( new_dataset, parent_session);
		Input input_ref=  FactoryInputRefFromLibrary.createInputRef( input_dataset, parent_session);
		//se input e output hanno lo stesso nome 
		generateExceptionIsHaveSameName(output_ref,input_ref);
		return new RDataProcess(output_ref,input_ref,parent_session);
	}

	public DataStep createDataStep(String new_dataset,Input input_reference) throws Exception {
		this.parent_session.generateExceptionOfSessionClose();
		OutputRefInterface output_ref=FactoryOutputRefFromLibrary.createOutputRef( new_dataset, parent_session);
		generateExceptionIsHaveSameName(output_ref,input_reference);
		return new RDataProcess(output_ref,input_reference,parent_session);
	}
	
	public FileStep createFileStep(String path_file, String input_dataset) throws Exception {
		this.parent_session.generateExceptionOfSessionClose();
		OutputRefInterface output_ref=new OutputRefFile(path_file);
		Input input_ref=  FactoryInputRefFromLibrary.createInputRef( input_dataset, parent_session);
		return new RFileProcess(output_ref,input_ref,parent_session);
	}

	public FileStep createFileStep(String path_file, Input input_reference) throws Exception {
		this.parent_session.generateExceptionOfSessionClose();
		OutputRefInterface output_ref=new OutputRefFile(path_file);
		return new RFileProcess(output_ref,input_reference,parent_session);
	}
	
	public CrossJoinStep createCrossJoinStep(String new_dataset) throws Exception { 
		this.parent_session.generateExceptionOfSessionClose();
		OutputRefInterface output_ref=FactoryOutputRefFromLibrary.createOutputRef( new_dataset, parent_session);
		return new RCrossJoinStep(output_ref,parent_session);
	}
	
	public OuterJoinStep createOuterJoinStep(String new_dataset)  throws Exception { 
		this.parent_session.generateExceptionOfSessionClose();
		OutputRefInterface output_ref=FactoryOutputRefFromLibrary.createOutputRef( new_dataset, parent_session);
		return new ROuterJoinStep(output_ref,parent_session);
	}
	
	public DataStep createMemoryStep(String input_dataset) throws Exception {
		this.parent_session.generateExceptionOfSessionClose();
		OutputRefInterface output_ref=new OutputRefFmtMemory();
		Input input_ref=  FactoryInputRefFromLibrary.createInputRef( input_dataset, parent_session);
		return new RDataProcess(output_ref,input_ref,parent_session);
	}

	public DataStep createMemoryStep(Input input_reference) throws Exception {
		this.parent_session.generateExceptionOfSessionClose();
		OutputRefInterface output_ref=new OutputRefFmtMemory();
		return new RDataProcess(output_ref,input_reference,parent_session);
	}
	
    public SortStep createSortStep(String new_dataset, String input_dataset) throws Exception {
    	this.parent_session.generateExceptionOfSessionClose();
    	OutputRefInterface output_ref=FactoryOutputRefFromLibrary.createOutputRef( new_dataset, parent_session);
		Input input_ref=  FactoryInputRefFromLibrary.createInputRef( input_dataset, parent_session);
		generateExceptionIsHaveSameName(output_ref,input_ref);
		return new RSortProcess(output_ref,input_ref,parent_session);
    }
	
	public SortStep createSortStep(String new_dataset, Input input_reference) throws Exception {
		this.parent_session.generateExceptionOfSessionClose();
		OutputRefInterface output_ref=FactoryOutputRefFromLibrary.createOutputRef( new_dataset, parent_session);
		generateExceptionIsHaveSameName(output_ref,input_reference);
		return new RSortProcess(output_ref,input_reference,parent_session);
	}
	
	//se l'input e l'output hanno stesso nome sulla stessa libreria genera eccezzione 
	 static void generateExceptionIsHaveSameName(OutputRefInterface output_ref, Input input_ref) throws OverWriteSameTable {
		String lib_input=null; 
		String table_input=null;
		
		if(input_ref instanceof InputRefFmt) {
			lib_input=((InputRefFmt)input_ref).getNameLibrary();
			table_input=((InputRefFmt)input_ref).getNameTable(); 
		}
		else if(input_ref instanceof InputRefDB) {
			lib_input=((InputRefDB)input_ref).getNameLibrary();
			table_input=((InputRefDB)input_ref).getNameTable(); 
		}
		else if(input_ref instanceof InputFile) { 
			return ;
		}
		else if(input_ref instanceof InputString) { 
			return ;
		}
		
		else if(input_ref instanceof InputRows) { 
			return ;
		}
		
		else if(input_ref instanceof InputRefDBSQL) {
			return ;
		}
		else if(input_ref instanceof InputRefFmtMemory) {
			return ;
		}
		
		
		
		String lib_out=null; 
		String table_out=null;
		
		if(output_ref instanceof OutputRefFmt) {
			lib_out=((OutputRefFmt)output_ref).getNameLibrary();
			table_out=((OutputRefFmt)output_ref).getNameTable(); 
		}
		else if(output_ref instanceof OutputRefDB) {
			lib_out=((OutputRefDB)output_ref).getNameLibrary();
			table_out=((OutputRefDB)output_ref).getNameTable(); 
		}
		else if(output_ref instanceof OutputRefFile) { 
			return ;
		}
		else if(output_ref instanceof OutputRefFmtMemory) {
			return ;
		}
		if(lib_input.equalsIgnoreCase(lib_out)) {
			if(table_input.equalsIgnoreCase(table_out))  {
				throw new OverWriteSameTable("ERRORE ! Non si puo' sovrascrivere la tabella di input. ");
			}
		}
	}
}
