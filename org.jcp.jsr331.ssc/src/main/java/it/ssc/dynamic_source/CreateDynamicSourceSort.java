package it.ssc.dynamic_source;

import java.util.ArrayList;
import java.util.Iterator;
import it.ssc.context.Config;

import it.ssc.pdv.PDVAll;
import it.ssc.pdv.PDVField;
import it.ssc.step.sort.SortItem;

public class CreateDynamicSourceSort extends ExternalizableDynamicSourceSort implements DynamicSourceInterface {
	
	private static final String nl=Config.NL;
	private ArrayList<SortItem> list_sort_item;

	public CreateDynamicSourceSort(PDVAll pdv,ArrayList<SortItem> list_sort_item) {
		super(pdv);
		this.list_sort_item=list_sort_item;
	}
	
	public String createCompleteJavaClassSource(String name_class) {
		String source = new String(nl);
		source += "import it.ssc.dynamic_source.*; "+nl;
		source += "import it.ssc.step.sort.CompareMy; " +nl;
		source += "import java.io.*; " +nl;
		source += "import it.ssc.pdv.PDVAll; " +nl;
		source += "public final class "+name_class+" implements DynamicClassSortInterface { "+nl;
		source += createAllSingleVariable();
		
		source += "private long nobs=0L;                                                  "+nl;
		
		source += "public "+name_class+"() { }                                            "+nl;
		
		source += "private void _countObs() {  nobs++; }                                 "+nl;
		
		source += "public DynamicClassSortInterface _loadRecord(PDVAll pdv) throws Exception {   "+nl;
		source += "    _loadAllSubFieldIntoVarThis(pdv);                                  "+nl;
		source += "    return ("+name_class+") this.clone();                              "+nl;
		source += "}                                                                      "+nl;

		source += "public void  _uploadRecord(DynamicClassSortInterface _record_sort, PDVAll pdv) throws Exception {   "+nl;
		source += "    _countObs();                                           "+nl;
		source += "    _loadRecordIntoPdv(("+name_class+")_record_sort,pdv);  "+nl;
		source += "}                                                          "+nl;

		source += "private void _loadAllSubFieldIntoVarThis(PDVAll pdv) { "+nl;
		source += valorizeAllSingleVariable();
		source += "}                                                       "+nl;
		
		source += "private void _loadRecordIntoPdv("+name_class+" _record_sort, PDVAll pdv) { "+nl;
		source += saveAllSingleVariableFromRecord();
		source += "}                                                       "+nl;
		
		source += "public int compareTo(DynamicClassSortInterface _record_sort) {     "+nl;
		source += "  "+name_class+" _record_sort_cast=("+name_class+") _record_sort;  "+nl;    	
		source +=     generateSourceCompareTo()                                        +nl;                 
		source += "}                                                                  "+nl; 
		
		
		source += "public void writeExternal(DataOutputStream out) throws IOException {         "+nl;
		source +=     writeExternal()              									  +nl; 
		source += "}          													      "+nl;
		
		source += "public void readExternal(DataInputStream in) throws IOException,ClassNotFoundException {          "+nl;
		source +=     readExternal()              									  +nl; 
		source += "}          													      "+nl;
		
		source += "}                                                                  "+nl;
		
		return source;
	}
	
	
	private String saveAllSingleVariableFromRecord() {

		int size = this.pdv.getSize();
		PDVField<?> pdv_field;
		String save_var = new String("");
		for (int index_cicle_pdv = 0; index_cicle_pdv < size; index_cicle_pdv++) {
			pdv_field = pdv.getField(index_cicle_pdv);
			save_var += "     " + "if(_record_sort."+ pdv_field.getName() +"==null) pdv.getField(\""+ pdv_field.getName() + "\").is_null=true; "+nl;
			if(pdv_field.type==StringBuffer.class) {
				save_var += "      else  { " + "pdv.getField(\"" + pdv_field.getName() + "\").value_generics= new StringBuffer(_record_sort." + pdv_field.getName() + "); "+nl;
				save_var += "      pdv.getField(\""+ pdv_field.getName() + "\").is_null=false; } "+nl;
			}
			else {
				save_var += "      else { " + "pdv.getField(\"" + pdv_field.getName() + "\").value_generics=_record_sort." + pdv_field.getName() + "; "+nl;
				save_var += "      pdv.getField(\""+ pdv_field.getName() + "\").is_null=false; } "+nl;
			}	
		}
		return save_var;
	}
	
	private String generateSourceCompareTo() { 
		
		String source="";
		Iterator<SortItem> iter=this.list_sort_item.iterator();
		int conta=0;
		ArrayList<String> list_else=new ArrayList<String>();
		while(iter.hasNext()) {
			SortItem item=iter.next(); 
			String appo=null;
			if(item.isAsc()) {
				 appo="CompareMy.compare("+item.getVarName()+",_record_sort_cast."+item.getVarName()+") ";
			}
			else {
				 appo="CompareMy.compare(_record_sort_cast."+item.getVarName()+","+item.getVarName()+") ";
			}
			
			if(iter.hasNext()) {
				conta++;
				source+="int confronto"+conta+"="+appo+"; ";
				source+=" if(confronto"+conta+"==0)  { ";
				
				list_else.add("} else { return confronto"+conta+"; }");
			}
			else  {
				source+="return "+appo+"; ";
			}
		}
		
		String[] array_string=new String[list_else.size()];
		array_string=list_else.toArray(array_string);
		for(int a=array_string.length-1;a > -1; a--) {
			source+=array_string[a];
		}
		return source;
	}
}
