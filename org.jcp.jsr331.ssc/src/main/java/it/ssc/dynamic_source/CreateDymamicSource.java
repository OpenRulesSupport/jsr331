package it.ssc.dynamic_source;

import it.ssc.pdv.PDVAll;
import it.ssc.pdv.PDVField;

public class CreateDymamicSource extends GenericDynamicSource implements DynamicSourceInterface {
	
	private String user_source;
	private String where_condition;
	private String equi_join_source;
	private String java_attribute;
	
	public CreateDymamicSource(PDVAll pdv, String user_source, String where_condition,String java_attribute) {
		super(pdv);
		this.user_source=user_source;
		this.where_condition=where_condition;
		this.equi_join_source="";
		this.java_attribute=java_attribute;
	}
	
	public void createSourceEquiJoin(String var1,String var2)  {
		String source_equi =new String();
		source_equi+="if("+var1+"==null && "+var2+"==null) _setIsEquiJoin(true);	"+nl;
		source_equi+="else if("+var1+"==null || "+var2+"==null) _setIsEquiJoin(false); "+nl;
		if(pdv.getField(var1).type==String.class || pdv.getField(var1).type==StringBuffer.class ) {
			source_equi+="else  _setIsEquiJoin("+var1+".equals("+var2+")) ;"+nl;
		}
		else {
			source_equi+="else _setIsEquiJoin(Wrapper.unwrapper("+var1+")==Wrapper.unwrapper.("+var2+")) ;"+nl;
		}
		
		this.equi_join_source=source_equi.toString();
	}
	
	public String getUserSource() {
		return this.user_source;
	}

	public String createCompleteJavaClassSource(String name_class) {
		String source = new String(nl);
		source += "import it.ssc.dynamic_source.*; "+nl;
		source += "import it.ssc.pdv.PDVAll; " +nl;
		
		source += "public final class "+name_class+" extends DynamicClassAbstract { "+nl;
		source += createAllSingleVariable();
		
		source += this.java_attribute                                          +nl;
		source += "public void _setPDV(PDVAll pdv) {                          "+nl;
		source += "	 super._setPDV(pdv);                                      "+nl;
		source += "}                                                          "+nl;

		if(this.equi_join_source!="") {
			source += "public void _runEquiJoin() {                           "+nl;
			source += this.equi_join_source;
			source += "}                                                      "+nl;
		}
		
		source += "public void _recallSourceFromUser(PDVAll pdv,boolean exec_equi_jion) throws Exception {   "+nl;
		source += "    _countObs();                                       "+nl;
		source += "    _loadAllSubFieldIntoVarThis(pdv);                  "+nl;
		if(this.equi_join_source!="") {
			source += "    if(exec_equi_jion) _runEquiJoin();             "+nl;
		}
		
		if(!where_condition.equals("")) {
			source += "    if(!_code_where()) delete();                   "+nl;
		}
		if(!user_source.equals("")) {
			source += "    _code_from_user();                             "+nl;
		}	
		source += "    _saveAllSubFieldFromVarThis(pdv);                  "+nl;
		source += "}                                                      "+nl;

		source += "private void _loadAllSubFieldIntoVarThis(PDVAll pdv) { "+nl;
		source += valorizeAllSingleVariable();
		source += "}    "+nl;
		
		source += "private void _code_from_user() throws Exception {      "+nl;
		source += user_source+ "                                          "+nl;
		source += "}                                                      "+nl;
		
		if(!where_condition.equals("")) {
			source += "private boolean _code_where() throws Exception {   "+nl;
			source += "  return "+  where_condition                        +nl;
			source += "}                                                  "+nl;
		}
		
		source += "private void _saveAllSubFieldFromVarThis(PDVAll pdv) { "+nl;
		source += saveAllSingleVariable();
		source += "}                                                      "+nl;
		source += "}                                                      "+nl;
		return source;
	}

	private String saveAllSingleVariable() {

		int size = this.pdv.getSize();
		PDVField<?> pdv_field;
		String save_var = new String("");
		for (int index_cicle_pdv = 0; index_cicle_pdv < size; index_cicle_pdv++) {
			pdv_field = pdv.getField(index_cicle_pdv);
			save_var += "     " + "if("+ pdv_field.getName() +"==null) pdv.getField(\""+ pdv_field.getName() + "\").is_null=true; "+nl;
			if(pdv_field.type==StringBuffer.class) {
				save_var += "      else  { " + "pdv.getField(\"" + pdv_field.getName() + "\").value_generics= new StringBuffer(" + pdv_field.getName() + "); "+nl;
				save_var += "      pdv.getField(\""+ pdv_field.getName() + "\").is_null=false; } "+nl;
			}
			else {
				save_var += "      else { " + "pdv.getField(\"" + pdv_field.getName() + "\").value_generics=" + pdv_field.getName() + "; "+nl;
				save_var += "      pdv.getField(\""+ pdv_field.getName() + "\").is_null=false; } "+nl;
			}	
		}
		return save_var;
	}
}
