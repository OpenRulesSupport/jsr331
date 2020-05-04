package it.ssc.metadata.sql;

import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.metadata.Field;
import it.ssc.metadata.FieldInterface;
import it.ssc.metadata.TypeSSC;
import it.ssc.metadata.exception.TypeSqlNotSupported;



import java.sql.Types;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

abstract class AbstractCreateFileds {
	
	private static final Logger logger=SscLogger.getLogger();
	
	// fare attenzione a VARCHAR2(size [BYTE | CHAR]) 
	
	//UTILIZZATO SOLO PER MAPPARE DATI DAL DB 
	//data_type e' preso direttamente dal DB 
	protected Field createSingleField(String name, int size, int type_sql, String type_s_sql,int precision,int scale) throws TypeSqlNotSupported {
		Class<?> type_field=null;
		
		if (type_sql == Types.TINYINT) {  //1 byte  
			type_field=Byte.class;
			size=TypeSSC.LENGTH_BYTE;
		} 
		else if (type_sql == Types.SMALLINT) {  //2 byte 
			type_field=Short.class;
			size=TypeSSC.LENGTH_SHORT_BYTE;
		} 
		/*su alcuni sistemi TINYINT va da 0 a 255 su altri da -128 a 127*/
		else if (type_sql == Types.INTEGER) {  // 4 byte 
			type_field=Integer.class;
			size=TypeSSC.LENGTH_INT_BYTE;
		} 
		else if (type_sql == Types.BIGINT) {  // 8 byte 
			type_field=Long.class;
			size=TypeSSC.LENGTH_LONG_BYTE;
		} 
		else if (type_sql == Types.REAL) {   //  float (ce l'ho)
			type_field=Float.class;
			size=TypeSSC.LENGTH_FLOAT_BYTE;
		} 
		else if (type_sql == Types.FLOAT) {   //  double 
			type_field=Double.class;
			size=TypeSSC.LENGTH_DOUBLE_BYTE;
		} 
		else if (type_sql == Types.DOUBLE) {   //  double 
			type_field=Double.class;
			size=TypeSSC.LENGTH_DOUBLE_BYTE;
		} 
		
		else if (type_sql == Types.DECIMAL || type_sql == Types.NUMERIC) { // double
			logger.log(SscLevel.NOTE,"Il campo " + name +
					   " di tipo SQL DECIMAL/NUMBER con (precisione,scala) : ("+
					   precision+","+scale+")"+ " viene acquisito come java DOUBLE");
			type_field = Double.class;
			size = TypeSSC.LENGTH_DOUBLE_BYTE;
		}
		
		else if (type_sql == Types.BOOLEAN) {  
			type_field=Boolean.class;
			size=TypeSSC.LENGTH_BOOLEAN_BYTE;
		} 
		else if (type_sql == Types.CHAR) {   //  lunghezza fissa n
			type_field=StringBuffer.class;
			//size=size;
		} 
		else if (type_sql == Types.VARCHAR) {   //  lunghezza max n
			type_field=String.class;
			//size=size;
		} 
		
		else if (type_sql == Types.DATE) {   //  data calendario
			type_field=GregorianCalendar.class;
			size=TypeSSC.DEFAULT_LENGTH_DATE_BYTE;
		} 
		else if (type_sql == Types.TIMESTAMP) {   //  data e ora 
			type_field=GregorianCalendar.class;
			size=TypeSSC.DEFAULT_LENGTH_DATE_BYTE;
		} 
		else if (type_sql == Types.TIME) {   //  solo ora 
			type_field=GregorianCalendar.class;
			size=TypeSSC.DEFAULT_LENGTH_DATE_BYTE;
		} 
		else {
			throw new TypeSqlNotSupported("ERRORE !Tipo di dati SQL della colonna "+name+" non ancora supportato :"+type_s_sql +", codice sql:"+type_sql );
		}
		return new Field(type_field, size, name, precision, scale, type_sql);
	}
	public abstract ArrayList<FieldInterface> getFieldsdCreated();

}
