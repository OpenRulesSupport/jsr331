package it.ssc.ref;


import it.ssc.library.DbLibrary;
import it.ssc.library.Library;
import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.metadata.FieldInterface;
import it.ssc.metadata.exception.TypeSqlNotSupported;
import it.ssc.metadata.sql.CreateFieldsFromResultsetMetadata;
import it.ssc.step.exception.InvalidDichiarationOptions;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputRefDBSQL implements  InputRefDBInterface {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private static final TYPE_REF type_ref = TYPE_REF.REF_DB;
	
	private ResultSet result;
	private String select_sql;
	//private Connection connect;
	private ArrayList<FieldInterface> list_filed;
	private String[] rename_var;
	private DbLibrary  library;
	
	public InputRefDBSQL(Library lib, String select_sql) 	 {
		//this.connect=connect;
		this.library=(DbLibrary)lib;
		this.select_sql=select_sql;
	}
	
	public void executeQuery() throws SQLException, TypeSqlNotSupported, InvalidDichiarationOptions, InvalidLibraryException {
		Statement statement = library.getConnection().createStatement();
		if(statement.getMaxRows()!=0) {
			logger.log(SscLevel.NOTE,"Max dimension resultset:"+statement.getMaxRows());
		}
		logger.log(Level.INFO,"Execute sql query : "+select_sql); 
		this.result = statement.executeQuery(select_sql);
		ResultSetMetaData meta = this.result.getMetaData();
		list_filed=new CreateFieldsFromResultsetMetadata(meta).getFieldsdCreated();
		renameOnListofVar() ;
	}
	
	public void renameVarToLoad(String new_name,String old_name) throws InvalidDichiarationOptions {
		rename_var=new String[2];
		rename_var[0]=new_name;
		rename_var[1]=old_name;
		
	}
	
	private void renameOnListofVar() throws InvalidDichiarationOptions {
		if(rename_var==null) return;
		boolean trovato=false;
		for(FieldInterface field:list_filed) {
			if(field.getName().equals(rename_var[1])) {
				field.renameField(rename_var[0]);
				trovato=true;
			}
		}
		if(!trovato) throw new InvalidDichiarationOptions("La variabile da rinominare "+rename_var[1] +" non esiste");	
	}
	
	public java.sql.ResultSet getResultSet() {
		return this.result;
	}

	public TYPE_REF getTypeRef() {
		return type_ref;
	}

	public int getColumnCount() throws SQLException { 
		return list_filed.size();
	}

	public String getColumnName(int index_column) throws SQLException {
		return list_filed.get(index_column-1).getName();
	}

	public FieldInterface getField(int index_column) {
		return list_filed.get(index_column-1);
	}
	
	public ArrayList<FieldInterface> getListField() {
		return list_filed;
	}
	
	public String getSql()  {
		return this.select_sql;
	}
	
	//non ha effetti, in quanto non carica nulla in memoria 
	public void close() {
		
	}
}
