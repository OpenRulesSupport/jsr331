package it.ssc.step.trasformation;

import java.util.logging.Logger;

import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.step.ParameterStepInterface;

public class OptionsTrasformation {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private String source_code="";
	private String declare_new_var=null; // lo esplicito giusto per evidenza
	private String declare_java_attribute="";
	private String where_condition="";
	private BeanEquiJoin bean_equi;
	private ParameterStepInterface parameter_object;

	public String getSourceUserCode() {
		return source_code;
	}
	
	public void setParameterStep(ParameterStepInterface obj) {
		this.parameter_object=obj;
	}

	public void setSourceUserCode(String source_code) {
		this.source_code = source_code;
	}

	public String getDeclareNewVar() {
		return declare_new_var;
	}

	public void setDeclareNewVar(String declare_new_var) {
		logger.log(SscLevel.INFO,"DECLARE VARIABLE:"+declare_new_var);
		this.declare_new_var = declare_new_var;
	}
	
	public void setDeclareJavaAttribute(String declare_java_var) {
		if(declare_java_var==null) declare_java_var="";
		logger.log(SscLevel.INFO,"DECLARE JAVA ATTRIBUTE:"+declare_java_var);
		this.declare_java_attribute=  declare_java_var;
	}
	
	public String getDeclareJavaAttribute() {
		return declare_java_attribute;
	}

	public String getWhereCondition() {
		return where_condition;
	}

	public void setWhereCondition(String where_condition) {
		this.where_condition = where_condition;
	}
	
	public void setJoin(String var1,String var2) {
		bean_equi=new BeanEquiJoin(var1,var2);
	}
	
	public BeanEquiJoin getBeanJoin() {
		return bean_equi;
	}

	public ParameterStepInterface getParameterStep() {
		return parameter_object;
	}
}
