package it.ssc.step.writedata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.ssc.library.exception.InvalidLibraryException;
import it.ssc.log.SscLogger;
import it.ssc.metadata.exception.ReadMetadataSqlException;
import it.ssc.metadata.exception.TypeSqlNotSupported;
import it.ssc.metadata.sql.CreateSqlDDLFromFields;
import it.ssc.pdv.PDVField;
import it.ssc.pdv.PDVKeep;
import it.ssc.ref.Input;
import it.ssc.ref.InputRefDB;
import it.ssc.ref.OutputRefDB;
import it.ssc.ref.OutputRefInterface;
import it.ssc.step.exception.InvalidDichiarationOptions;

final class WriteDataToDB implements WriteDataInterface  {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private long obs=0;
	private OutputRefDB output_ref;
	private OptionsWrite opt_write;
	private int num_var_writed;
	private Connection connect_db;
	private PreparedStatement prepared_sql;
	private boolean error=false;
	private boolean sup_trans;
	
	WriteDataToDB(OutputRefInterface out_ref,OptionsWrite opt_write,PDVKeep pdv) throws Exception  {
		this.output_ref = (OutputRefDB) out_ref; 
		this.opt_write=opt_write; 
	
		this.connect_db= this.output_ref.getConnection();
		DatabaseMetaData meta=this.connect_db.getMetaData();
		sup_trans=meta.supportsTransactions();
		
		if(sup_trans) this.connect_db.setAutoCommit(false);
		if(!this.opt_write.isAppendOutput()) {
			String ddl_table = new CreateSqlDDLFromFields(output_ref.getNameTable(),pdv).getDDLDefinition();
			logger.log(Level.INFO,"DDL:"+ddl_table);
			sendStatementCreateTableSql(ddl_table); 
		}	
		prepared_sql=sendPreparedStatementInsertSql(pdv);
	}
	
	private void sendStatementCreateTableSql(String ddl_table) throws SQLException {
		try {
			connect_db.createStatement().executeUpdate(ddl_table);
		}
		catch(SQLException sqlex) {
			if(sup_trans) { 
				this.connect_db.rollback();
				this.connect_db.setAutoCommit(true);
			}	
							
			throw sqlex;
		} 
	}
	
	private PreparedStatement sendPreparedStatementInsertSql(PDVKeep pdv)	throws InvalidDichiarationOptions, SQLException {

		String insert_sql = "INSERT INTO " + output_ref.getNameTable().toUpperCase()
				          + " VALUES (";
		for (int a = 0; a < pdv.getSizeFieldKeep(); a++) {
			insert_sql += "?";
			if (a != (pdv.getSizeFieldKeep() - 1))	insert_sql += ",";
		}
		insert_sql += ")";
		return connect_db.prepareStatement(insert_sql);
	}
	
	public void readFromPDVWriteOutput(PDVKeep pdv)	throws InvalidDichiarationOptions, SQLException {

		try {
			if (pdv.isRecordDeleted())	return;
			num_var_writed = 0;
			for (PDVField pdv_field : pdv.getListFieldKeep()) {
				
				num_var_writed++;
				if(pdv_field.is_null) {
					prepared_sql.setNull(num_var_writed, pdv_field.type_sql);
				}
				else if (pdv_field.type == String.class) {
					prepared_sql.setString(num_var_writed,pdv_field.value_generics.toString())   ;
				} 
				else if (pdv_field.type == StringBuffer.class) {
					prepared_sql.setString(num_var_writed,pdv_field.value_generics.toString())   ;
				}
				else if (pdv_field.type == Double.class) {
					prepared_sql.setDouble(num_var_writed,(Double)pdv_field.value_generics)   ;
				}
				
				//VEDERE BENE PERCHE PUO ESSER PASSATO UN CALENDAR
				else if (pdv_field.type == GregorianCalendar.class) {
					prepared_sql.setTimestamp(num_var_writed,new Timestamp(((GregorianCalendar)pdv_field.value_generics).getTimeInMillis()))   ;
				}
				else if (pdv_field.type == Integer.class) {
					prepared_sql.setInt(num_var_writed,(Integer)pdv_field.value_generics)   ;
				}
				else if (pdv_field.type == Short.class) {
					prepared_sql.setShort(num_var_writed,(Short)pdv_field.value_generics)   ;
				}
				else if (pdv_field.type == Character.class) {
					prepared_sql.setString(num_var_writed,pdv_field.value_generics.toString())   ;
				}
				else if (pdv_field.type == Float.class) {
					prepared_sql.setFloat(num_var_writed,(Float)pdv_field.value_generics)   ;
				}
				else if (pdv_field.type == Byte.class) {
					prepared_sql.setByte(num_var_writed,(Byte)pdv_field.value_generics)   ;
				}
				else if (pdv_field.type == Boolean.class) {
					prepared_sql.setBoolean(num_var_writed,(Boolean)pdv_field.value_generics)   ;
				}
				else if (pdv_field.type == Long.class) {
					prepared_sql.setLong(num_var_writed,(Long)pdv_field.value_generics)   ;
				}
			}
			obs++;
			prepared_sql.addBatch();
			if(obs%1000==0) prepared_sql.executeBatch(); 
		}	
			 
		catch (SQLException sqlex) {
			if(sup_trans)  { 
				this.connect_db.rollback();
			    this.connect_db.setAutoCommit(true);
			}    
			error=true;
			throw sqlex;
		}
	}
	
	
	public void close(boolean error_data_step, PDVKeep pdv) throws SQLException {
		try {
			if (error || error_data_step) {
				if(sup_trans) connect_db.rollback();
			} 
			else if (prepared_sql != null) {
				if (obs % 1000 != 0) {
					prepared_sql.executeBatch();
				}
				prepared_sql.close();
				if(sup_trans)  connect_db.commit();
			}

			if (!error && !error_data_step)
				logger.log(Level.INFO,"Numero di osservazioni scritte nela tabella "+ 
						   output_ref.getNameLibrary()+"."+ output_ref.getNameTable() + " :" + obs);
		} 
		finally {
			if (connect_db != null) {
				if(sup_trans)  connect_db.setAutoCommit(true);
			}	
		}
	}
	
	
	public Input getDataRefCreated() throws SQLException, TypeSqlNotSupported, ReadMetadataSqlException, InvalidLibraryException {
		return new InputRefDB(this.output_ref.getLibrary(),this.output_ref.getNameTable());
	}
	
}
