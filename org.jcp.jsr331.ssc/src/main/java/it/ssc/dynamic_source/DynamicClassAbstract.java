package it.ssc.dynamic_source;

import java.util.logging.Logger;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.pdv.PDVAll;
import it.ssc.step.ParameterStepInterface;

public abstract class DynamicClassAbstract extends VerifyConditionInAndNotin implements DynamicClassInterface {
	
	private static final Logger logger=SscLogger.getLogger();
	
	/*Deve rimanere privato affinche la sottoclasse dinamica non abbia accesso sul pdv*/
	private PDVAll private_pdv;
	private long nobs=0L;
	private  Object return_object;
	private boolean is_equi_join;
	private ParameterStepInterface parameter_object;
	
	public void _setIsEquiJoin(boolean is_equi)  {
		if(!is_equi)  delete();
		this.is_equi_join=is_equi;
	}
	
	protected void setReturnObject(Object obj) {
		return_object=obj;
	}
	
	protected Object getParamAttribute(String name_param) {
		if(parameter_object==null) return null;
		return parameter_object.getParamAttribute(name_param);
	}
	
	public boolean _isEquiJoin()  {
		return this.is_equi_join;
	}
	
	protected DynamicClassAbstract() {
		super();
	}
	
	public void _setPDV(PDVAll pdv) {
		this.private_pdv=pdv;
	}
		
	public Object _getReturnObject() {
		return return_object;
	}
	
	public void _setParameterStep(ParameterStepInterface obj) {
		 this.parameter_object=obj;
	}
	
	protected void delete() {
		this.private_pdv.setRecordDeleted(true);
	}
	
	protected void _countObs() {
		nobs++;
	}
	protected long getObs() {
		return nobs;
	}
	protected void log(Object... log) {
		for(Object obj:log) {
			logger.log(SscLevel.LOG,(obj).toString());
		}	
	}
	
	protected <T> T replaceNull(T value, T replace) {
		return (value==null ? replace : value);
	}
	
	/*
	protected String toStringDate(GregorianCalendar cal) {
		return "";
	}
	
	protected String toStringTime(GregorianCalendar cal) {
		return "";
	}*/
}
