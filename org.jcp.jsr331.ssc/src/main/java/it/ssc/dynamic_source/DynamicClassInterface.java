package it.ssc.dynamic_source;

import it.ssc.pdv.PDVAll;
import it.ssc.step.ParameterStepInterface;

public interface DynamicClassInterface  {
	
	public void _recallSourceFromUser(PDVAll pdv,boolean exec_equi_join) throws Exception;
	public void _setPDV(PDVAll pdv);
	public Object _getReturnObject();
	public boolean _isEquiJoin() ;
	public void _setParameterStep(ParameterStepInterface obj) ;
}
