package it.ssc.step;

import it.ssc.ref.Input;
import it.ssc.step.parallel.Parallelizable;

public interface OuterJoinStep extends Parallelizable {
	
	public void setInputDataForLeftJoin(String ref1,String ref2)  throws Exception ;
	
	public void setInputDataForLeftJoin(Input input_reference,String ref) throws Exception ;
	
	public void setInputDataForLeftJoin(String ref,Input input_reference) throws Exception ;
	
	public void setInputDataForLeftJoin(Input input_reference1,Input input_reference2) throws Exception ;
	
	public void setInputDataForRightJoin(String ref1,String ref2)  throws Exception ;
	
	public void setInputDataForRightJoin(Input input_reference,String ref) throws Exception ;
	
	public void setInputDataForRightJoin(String ref,Input input_reference) throws Exception ;
	
	public void setInputDataForRightJoin(Input input_reference1,Input input_reference2) throws Exception ;
	
	
	
	public Object execute() throws Exception;
	
	public void setWhere(String where_condition);
	
	public void setDropVarOutput(String... name_field) ;

	public void setKeepVarOutput(String... name_field) ;

	public void declareNewVariable(String declare_new_var) ;
	
	public void declareJavaAttribute(String declare_java_var) ;
	
	public void setSourceCode(String source_code) ;
	
	public void setAppendOutput(boolean append);
	
	public void setOuterJoinVar(String var1,String var2);
	
	public Input getDataRefCreated() ;
	
	public void setParameter(ParameterStepInterface obj) ;
	
}
