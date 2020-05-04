package it.ssc.ref;


import it.ssc.library.DbLibrary;
import it.ssc.library.Library;
import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.metadata.FieldInterface;
import it.ssc.metadata.exception.ReadMetadataSqlException;
import it.ssc.metadata.exception.TypeSqlNotSupported;
import it.ssc.metadata.sql.CreateFieldsFromResultset;
import it.ssc.metadata.sql.CreateFieldsFromResultsetMetadata;
import it.ssc.step.exception.InvalidDichiarationOptions;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InputRefDB implements InputRefDBInterface { 
	
	private static final Logger logger=SscLogger.getLogger();
	
	private static final TYPE_REF type_ref = TYPE_REF.REF_DB;
	private String name_table;
	private DbLibrary  library;
	private ArrayList<FieldInterface> list_filed;
	private String select_sql;
	private ResultSet result;
	private String[] rename_var;
	
	public InputRefDB(Library lib, String name_ds) throws InvalidLibraryException, SQLException, TypeSqlNotSupported, ReadMetadataSqlException  { 
		this.name_table=name_ds;
		this.library=(DbLibrary)lib;
		this.select_sql="select * from "+this.name_table;
		//per problemi di Maiuscolo / minuscolo passo il nome che e' presente sul DB 
		String name_on_db=this.library.getCorrectNameTable(this.name_table);
		
		Matcher matcher_azioni=null;
		ResultSet rsmeta=null;
		if((matcher_azioni = isTwoLevel(name_on_db)).matches())  {
			String catalog=matcher_azioni.group(2); 
			String name_table=matcher_azioni.group(3);
			rsmeta= this.library.getConnection().getMetaData().getColumns(catalog, null, name_table, null);
		}
		else {
			rsmeta= this.library.getConnection().getMetaData().getColumns(null, null, name_on_db, null);
		}
		
		
		list_filed=new CreateFieldsFromResultset(rsmeta,name_table).getFieldsdCreated();
		rsmeta.close();
	} 
	
	public void renameVarToLoad(String new_name,String old_name) throws InvalidDichiarationOptions {
		rename_var=new String[2];
		rename_var[0]=new_name;
		rename_var[1]=old_name;
		renameOnListOfVar();
	}
	
	
	private Matcher isTwoLevel(String name_tab_on_db) {
		Pattern pattern_azioni = Pattern.compile("(([^\\.]+)\\.{1})([^\\.]+)");
		Matcher matcher_azioni = pattern_azioni.matcher(name_tab_on_db);
		
		return matcher_azioni;
	}
	
	private void renameOnListOfVar() throws InvalidDichiarationOptions {
		if(rename_var==null) return;
		boolean trovato=false;
		for(FieldInterface field:list_filed) {
			if(field.getName().equals(rename_var[1])) {
				field.renameField(rename_var[0]);
				trovato=true;
			}
		}
		if(!trovato) throw new InvalidDichiarationOptions("La variabile da rinominare "+rename_var[1] +" non esiste.");	
	}
	
	public void executeQuery() throws Exception {
		Statement statement = this.library.getConnection().createStatement();
		if(statement.getMaxRows()!=0) {
			logger.log(SscLevel.NOTE,"Massima  dimensione del resultset sql:"+statement.getMaxRows());
		}
		logger.log(Level.INFO,"Execute sql query : ("+select_sql+")"); 
		this.result = statement.executeQuery(select_sql);
		ResultSetMetaData meta = this.result.getMetaData();
		
		//sovrascrive i metadati recuperati da deatabasemetadata, con quelli
		//del resultset, che sembrano piu' corretti. Inoltre per omogeneita con 
		//i metadati presi dalle query, preferisco prendere sempre questi 
		list_filed=new CreateFieldsFromResultsetMetadata(meta).getFieldsdCreated();
		renameOnListOfVar();
		
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
	
	public String getNameTable() {
		return name_table;
	}
	public String getNameLibrary() {
		 return library.getName();
	}
	
	
	
	//non ha effetti, in quanto non carica nulla in memoria 
	public void close() {
		
	}
	
}
