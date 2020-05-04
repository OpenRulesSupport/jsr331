package it.ssc.dynamic_source;

import it.ssc.context.Config;
import it.ssc.pdv.PDVAll;
import it.ssc.pdv.PDVField;

abstract class GenericDynamicSource implements DynamicSourceInterface {
	protected PDVAll pdv;
	protected static final String nl=Config.NL;
	
	protected GenericDynamicSource(PDVAll pdv) {
		this.pdv=pdv;
	}
	
	public abstract String createCompleteJavaClassSource(String name_class) ;
	
	public String getUserSource() {
		return ""; 
	}
	
	protected String createAllSingleVariable() {
		int size = this.pdv.getSize();
		PDVField<?> pdv_field;
		String dich_var = new String("");
		for (int index_cicle_pdv = 0; index_cicle_pdv < size; index_cicle_pdv++) {
			for (index_cicle_pdv = 0; index_cicle_pdv < pdv.getSize(); index_cicle_pdv++) {
				pdv_field = pdv.getField(index_cicle_pdv);
				if(pdv_field.type==StringBuffer.class) {
					dich_var +=  "     String " + pdv_field.getName() + ";"+nl;
				}
				else 	{
					dich_var +=  "    " + pdv_field.type.getName() + " "+ pdv_field.getName() + ";"+nl;
				}	
			}
		}
		return dich_var;
	}
	
	protected String valorizeAllSingleVariable() {
		int size = this.pdv.getSize();
		PDVField<?> pdv_field;
		String valorize_var = new String("");
		for (int index_cicle_pdv = 0; index_cicle_pdv < size; index_cicle_pdv++) {
			pdv_field = pdv.getField(index_cicle_pdv);
			if(pdv_field.is_retain) continue;
			valorize_var += "     if(pdv.getField(\""+ pdv_field.getName() + "\").is_null) " +pdv_field.getName() +"=null;";
			if(pdv_field.type==StringBuffer.class) {
				
				valorize_var += "    else " + pdv_field.getName() + "= pdv.getField(\""
			     + pdv_field.getName() + "\").value_generics.toString(); "+nl;
			}
			else {
				valorize_var += "    else " + pdv_field.getName() + "= ("+ pdv_field.type.getName() + ")pdv.getField(\""
								     + pdv_field.getName() + "\").value_generics; "+nl;
			}
		}
		return valorize_var;
	}

}
