package it.ssc.pdv;

import it.ssc.parser.DateFormat;
import it.ssc.parser.ParserValueBoolean;
import it.ssc.parser.ParserValueDate;
import it.ssc.parser.InputSubDichiarationVar.SETTING_MISSING;
import it.ssc.parser.exception.InvalidBooleanFormatException;
import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.step.exception.InvalidObjectException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public final class PDVField<T> {
	private String name;  
	public Class<T> type;  
	public int lentgh_field;  
	public int type_sql;  //serve per creare il DDL per la creazione della tabella 
	public DateFormat.DATE_FORMAT date_format_input;  
	public SETTING_MISSING manager_missing_value;   
	public T value_generics;
	public boolean is_null;
	public boolean drop;
	public int precision;
	public int scale;
	public boolean is_retain;
	private String token_missing;
	
	
	public PDVField(String key, Class<T> type,String token_missink) throws InvalidDateFormatException {
		this.token_missing=token_missink;
		this.name=key;
		this.type=type;
		loadInitValue();
	}
	
	public String getName() {
		return this.name;
	}
	  	
	public void loadValueFromScanner(Scanner s) throws InvalidDateFormatException, InvalidBooleanFormatException { 
		loadValue(s.next()); 
	}
	
	
	public void loadValueFromObject(Object valore) throws InvalidDateFormatException, InvalidBooleanFormatException, InvalidObjectException { 
		is_null=false;
		if (valore==null) {
			is_null=true;
			return ;
		}
	
		if(valore.getClass()==type) {
			value_generics=(T)valore;
		}
		else if(valore instanceof java.util.Date)   {
			value_generics=(T) new GregorianCalendar();
			((GregorianCalendar)value_generics).setTime((Date)valore);
		}
		else if(type==String.class || type==StringBuffer.class)  {
			loadValue(valore.toString());
		}
		else {
			throw new InvalidObjectException("Il tipo passato non corrisponde alla dichiarazione");
		}	
	}
	
	@SuppressWarnings("unchecked")
	public boolean loadValueFromDB(Object value_db) throws InvalidDateFormatException, InvalidBooleanFormatException {
		
		is_null=false;
		if (value_db==null) {
			is_null=true;
			return true;
		}
		if(value_db.getClass()==type) {
			value_generics=(T)value_db;
			return true;
		}
		
		else if(value_db instanceof BigDecimal && type==Double.class)   {
			value_generics=(T) new Double( ((BigDecimal)value_db).doubleValue());
			return true;
			
		}
		else if(value_db instanceof java.util.Date)   {
			value_generics=(T) new GregorianCalendar();
			((GregorianCalendar)value_generics).setTime((Date)value_db);
			return true;
		}
		else if(type==String.class || type==StringBuffer.class)  {
			//QUA SAREBBE DA METTER UN  TRY CACH per  rigenerazione dell'eeccezione
			loadValue(value_db.toString());
			return true;
		}
		else return false;
	}
	 
	@SuppressWarnings("unchecked")
	public void loadValue(String value) throws InvalidDateFormatException, InvalidBooleanFormatException {
		
		Object value_con = null;
		is_null=false;
		
		if (manager_missing_value == SETTING_MISSING.MISSING_NULL) {
			//System.out.println("TIPPE:"+type);
			if (type == String.class || type == StringBuffer.class) {
				if (value.trim().equals("")) {
					is_null = true;
					return;
				} 
			}
			else if (type == Character.class) {
				//System.out.println("TIP:"+type);
				if (value.charAt(0) == ' ') {
					is_null = true;
					return;
				}
			}
		}
		else if(manager_missing_value==SETTING_MISSING.MISSING_SPACE) {
			if (type == String.class || type == StringBuffer.class || type == Character.class)  {
				if (token_missing!=null && value.trim().equals(token_missing))  {
					value=" ";
				}
			}	
		}
		
		if (token_missing!=null && value.trim().equals(token_missing) ) {
			is_null=true;
			return;
		}
		
		if (type == Double.class) {
			value_con = Double.valueOf(value);
		} 
		else if (type == String.class) {
			/*modifica del 22/11/2014*/
			if(lentgh_field - value.length() < 0 ) {
				value=value.substring(0, lentgh_field);
			}
			value_con = value;
		} 
		else if (type == StringBuffer.class) {
			/*modifica del 22/11/2014*/
			if(lentgh_field - value.length() < 0 ) {
				value=value.substring(0, lentgh_field);
			}
			value_con = new StringBuffer(value);
		} 
		else if (type == GregorianCalendar.class) {
			value_con = ParserValueDate.parser(value, date_format_input);
		} 
		else if (type == Integer.class) {
			value_con = Integer.valueOf(value);
		} 
		else if (type == Short.class) {
			value_con = Short.valueOf(value);
		}
		else if (type == Long.class) {
			value_con = Long.valueOf(value);
		} 
		else if (type == Boolean.class) {
			value_con = ParserValueBoolean.parser(value);
		} 
		else if (type == Float.class) {
			value_con = Float.valueOf(value);
		} 
		else if (type == Byte.class) {
			value_con = Byte.valueOf(value);
		} 
		else if (type == Character.class) {
			value_con = value.charAt(0);
		}
		value_generics= (T) value_con;
	}
	
	
	@SuppressWarnings("unchecked")
	public void loadInitValue() throws InvalidDateFormatException {

		Object value_con = null;
		if (type == Double.class) {
			value_con = Double.valueOf("0.0");
		} 
		else if (type == String.class) {
			value_con = "";
		} 
		else if (type == StringBuffer.class) {
			value_con = new StringBuffer("");
		} 
		else if (type == GregorianCalendar.class) {
			value_con = ParserValueDate.parser("01/01/70",DateFormat.DATE_FORMAT.GG_MM_AA);
		} 
		else if (type == Integer.class) {
			value_con = Integer.valueOf("0");
		} 
		else if (type == Short.class) {
			value_con = Short.valueOf("0");
		} 
		else if (type == Long.class) {
			value_con = Long.valueOf("0");
		} 
		else if (type == Boolean.class) {
			value_con = Boolean.valueOf("false");
		} 
		else if (type == Float.class) {
			value_con = Float.valueOf("0.0");
		} 
		else if (type == Byte.class) {
			value_con = Byte.valueOf("0");
		} 
		else if (type == Character.class) {
			value_con = 'A';
		}
		value_generics = (T) value_con;  
	}
}  
