package it.ssc.step;

import java.util.Date;
import java.util.Random;

import it.ssc.context.SessionIPRIV;
import it.ssc.ref.FactoryInputRefFromLibrary;
import it.ssc.ref.Input;
import it.ssc.ref.OutputRefInterface;
import it.ssc.ref.Input.TYPE_REF;
import it.ssc.step.readdata.OptionsRead;
import it.ssc.step.trasformation.OptionsTrasformation;
import it.ssc.step.writedata.OptionsWrite;


public class RCrossJoinStep extends CoreCrossJoinStep  implements CrossJoinStep {
	
	RCrossJoinStep(OutputRefInterface new_dataset_output, SessionIPRIV parent_session) {
		this.output_ref = new_dataset_output;
		this.parent_session = parent_session; 
		this.opt_trasf = new OptionsTrasformation();
		//uguale per tutti e due in quanto da questo non prende opzioni
		this.opt_read = new OptionsRead();
		this.opt_write = new OptionsWrite();
	}
	
	public void setEquiJoinVar(String var1,String var2) {
		this.opt_trasf.setJoin(var1,var2);
	}
	
	public void setInputDataForCross(String lib_dot_idataset1,String lib_dot_idataset2) throws Exception {
		 input_ref1=  FactoryInputRefFromLibrary.createInputRef( lib_dot_idataset1, parent_session);
		 input_ref2=  FactoryInputRefFromLibrary.createInputRef( lib_dot_idataset2, parent_session);
		 RFactorySteps.generateExceptionIsHaveSameName(output_ref,input_ref1);
		 RFactorySteps.generateExceptionIsHaveSameName(output_ref,input_ref2);
		 input_ref2=downloadRef2OnWork(input_ref2);
	}
	
	public void setInputDataForCross(Input input_reference,String lib_dot_idataset) throws Exception {
		input_ref1=input_reference;
		input_ref2=  FactoryInputRefFromLibrary.createInputRef( lib_dot_idataset, parent_session);
		RFactorySteps.generateExceptionIsHaveSameName(output_ref,input_ref1);
		RFactorySteps.generateExceptionIsHaveSameName(output_ref,input_ref2);
		input_ref2=downloadRef2OnWork(input_ref2);
	}
	
	public void setInputDataForCross(String lib_dot_idataset,Input input_reference) throws Exception {
		input_ref1=  FactoryInputRefFromLibrary.createInputRef( lib_dot_idataset, parent_session);
		input_ref2=input_reference;
		RFactorySteps.generateExceptionIsHaveSameName(output_ref,input_ref1);
		RFactorySteps.generateExceptionIsHaveSameName(output_ref,input_ref2);
		input_ref2=downloadRef2OnWork(input_ref2);
	}
	
	public void setInputDataForCross(Input input_reference1,Input input_reference2) throws Exception {
		input_ref1=input_reference1;
		input_ref2=input_reference2;
		RFactorySteps.generateExceptionIsHaveSameName(output_ref,input_ref1);
		RFactorySteps.generateExceptionIsHaveSameName(output_ref,input_ref2);
		input_ref2=downloadRef2OnWork(input_ref2);
	}
	
	public void setDropVarOutput(String... name_field) {
		opt_write.setDropOutput(name_field);
	}

	public void setKeepVarOutput(String... name_field) {
		opt_write.setKeepOutput(name_field);
	}

	public void declareNewVariable(String declare_new_var) {
		opt_trasf.setDeclareNewVar(declare_new_var);
	}
	
	public void declareJavaAttribute(String declare_java_var) {
		opt_trasf.setDeclareJavaAttribute(declare_java_var);
	}
	
	public void setSourceCode(String source_code) {
		this.opt_trasf.setSourceUserCode(source_code);
	}
	
	public void setParameter(ParameterStepInterface obj) {
		opt_trasf.setParameterStep( obj); 
	}
	
	public void setAppendOutput(boolean append) {
		opt_write.setAppendOutput(append);
	}
	
	public void setWhere(String where_condition) {
		opt_trasf.setWhereCondition(where_condition);
	}
	

	/*
	private InputDataInterface downloadRef2OnWorkV3(InputDataInterface input_ref2) throws Exception {
		if(! (input_ref2.getTypeRef() == TYPE_REF.REF_FMT_MEMORY) ) {
			FactoryStep factory_step = parent_session.getFactoryStep();
			DataStep datastep = factory_step.createMemoryStep(input_ref2);
			datastep.execute();
			input_ref2 = datastep.getDataRefCreated();
		}
		return input_ref2;
	}
	*/
	
	private Input downloadRef2OnWork(Input input_ref2) throws Exception {
		if(input_ref2.getTypeRef()==TYPE_REF.REF_DB || input_ref2.getTypeRef()==TYPE_REF.REF_FILE) {
			Random ra=new Random(new Date().getTime());
			String name_tmp="WORK.tmp_"+Math.abs(ra.nextInt());
			FactorySteps factory_step = parent_session.getFactorySteps();
			DataStep datastep = factory_step.createDataStep(name_tmp, input_ref2);
			datastep.execute();
			input_ref2=  FactoryInputRefFromLibrary.createInputRef( name_tmp, parent_session);
		}
		return input_ref2;
	}
	
	
	public void run() throws Exception {
		this.execute();
	}
	
	
}
