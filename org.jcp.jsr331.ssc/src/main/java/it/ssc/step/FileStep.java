package it.ssc.step;

import java.util.Locale;

import it.ssc.step.exception.InvalidDichiarationOptions;
import it.ssc.step.parallel.Parallelizable;

public interface FileStep  extends Parallelizable {
	
	
	//USARE anche questo metodo per toglierli 
	public void setFormatVar(String nome_campo, String nome_formato);
	
	public FileStep printf(String string_of_record,  String... args);
	
	public void printAllVar();

	public void setAppendOutput(boolean append);

	public Object execute() throws Exception;

	public void setWhere(String where_condition);
	
	public void setSourceCode(String source_code);

	public void setMaxObsRead(long obs_read) throws InvalidDichiarationOptions;
	
	public void setOutputMissing(String token_miss) ;
	
	public void declareNewVar(String informat_declare);
	
	public void setParameter(ParameterStepInterface obj) ;
	
	public void printHeader();

	public void printHeader(String header);
	
	public void setLocale(Locale l);
	
	public static final String PRINT_ALL="_ALL_";
	public static final String PRINT_ALL_HEADER="_ALL_HEAD_";


}
