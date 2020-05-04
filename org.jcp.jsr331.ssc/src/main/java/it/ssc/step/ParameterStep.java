package it.ssc.step;

import java.util.HashMap;

public class ParameterStep implements ParameterStepInterface {
	private HashMap<String,Object> table;
	
	public ParameterStep() {
		table=new HashMap<String,Object>();
	}

	public void setParamAttribute(java.lang.String name, java.lang.Object value) {
		table.put(name, value);
	}

	public java.lang.Object getParamAttribute(java.lang.String name) {
		return table.get(name);
	}

}
