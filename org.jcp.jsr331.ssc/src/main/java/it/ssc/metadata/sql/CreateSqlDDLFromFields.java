package it.ssc.metadata.sql;

import java.util.ArrayList;

import it.ssc.pdv.PDVField;
import it.ssc.pdv.PDVKeep;

import java.sql.Types;


public class CreateSqlDDLFromFields {
	private String def_table_ddl;
	
	public CreateSqlDDLFromFields(String name,PDVKeep pdv) throws Exception {
		
		def_table_ddl= "CREATE TABLE "+name+" (" ;
		def_table_ddl=def_table_ddl+appendType(pdv)+" )";
	}
	
	public String  getDDLDefinition() {
		return def_table_ddl;
	}

	private String appendType(PDVKeep pdv) throws Exception  {
		
		String ddl_var=" ";
		ArrayList<PDVField<?>> list=pdv.getListFieldKeep();
		int list_size=list.size();
		for(int a=0;a<list_size;a++) {
			PDVField<?> field=list.get(a);
			ddl_var+=" "+field.getName() +" "+mappingFiledVsTypeDB(field);
			if(a!=(list_size-1)) ddl_var+=","; 
		}
		return ddl_var;
	}
	
	private String mappingFiledVsTypeDB(PDVField<?> field) throws Exception {
		
		if (field.type_sql==Types.SMALLINT) {
			return "SMALLINT";
		} 
		else if (field.type_sql==Types.BIGINT) {  // 8 byte 
			return "BIGINT";
		} 
		else if (field.type_sql==Types.INTEGER) {  // 4 byte 
			return "INTEGER";
		} 
		else if (field.type_sql==Types.BOOLEAN) {  
			return "BOOLEAN";
		} 
		else if (field.type_sql==Types.CHAR) {   //  lunghezza fissa n
			return "CHAR("+field.lentgh_field+")";
		} 
		else if (field.type_sql==Types.VARCHAR) {   //  lunghezza max n
			return "VARCHAR("+field.lentgh_field+")";
		} 
		else if (field.type_sql==Types.FLOAT) {   //  double 
			return "FLOAT";
		} 
		else if (field.type_sql==Types.DOUBLE) {   //  double 
			return "FLOAT";
		} 
		
		else if (field.type_sql==Types.DECIMAL) {   //  double 
			if(field.scale >= 0 && field.precision > 0 && field.precision <= 25)  {
				return "DECIMAL("+field.precision+","+field.scale+")";
			}
			
			else {
				return "DECIMAL";
			}
		} 
		else if (field.type_sql==Types.NUMERIC ) {   //  double 
			if(field.scale >= 0 && field.precision > 0 && field.precision <= 25)  {
				return "NUMERIC("+field.precision+","+field.scale+")";
			}
			else {
				return "NUMERIC";
			}
		} 
		else if (field.type_sql==Types.REAL) {   //  float (ce l'ho)
			return "REAL";
		} 
		else if (field.type_sql==Types.TIMESTAMP) {   //  data e ora 
			return "TIMESTAMP";
		} 
		else if (field.type_sql==Types.TIME) {   //  data e ora 
			return "TIME";
		} 
		else if (field.type_sql==Types.DATE) {   //  data e ora 
			return "DATE";
		} 
		else {
			//COMPLETARE
			throw new Exception("Completare i tipi sql trattati:"+field.type_sql +" relativi al campo "+field.getName());
		}
	}

}
