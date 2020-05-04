package it.ssc.parser;

import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.metadata.TypeSSC;
import it.ssc.parser.exception.InvalidInformatStringException;

import java.sql.Types;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

public class InputSubDichiarationVar implements InputSubDichiarationInterface,DateFormat {
	
	private static final Logger logger=SscLogger.getLogger();
	
	public enum SETTING_MISSING {MISSING_PROCEDURE, MISSING_SPACE, MISSING_NULL};
	
	private final TYPE_INPUT_STEP type;
	private String name_var; 
	private int start_column;
	private int end_column;
	private boolean is_column_format;
	private int length_var;
	private Class type_var;
	private int type_sql;
	private SETTING_MISSING manager_missing_value;
	private DATE_FORMAT formate_data;
	private boolean isRetain;
	// i valori mancanti sono gestiti con il null
	
	
    
	
	public InputSubDichiarationVar() {
		this.type=TYPE_INPUT_STEP.DICHIARATION_VAR;
		this.manager_missing_value=SETTING_MISSING.MISSING_PROCEDURE;
		this.start_column=0;
		this.end_column=0;
		this.length_var=0;
		this.is_column_format=false;
		this.isRetain=false;
	}
	
	public TYPE_INPUT_STEP getTypeInputStep() {
		return type;
	}
	
	public void setVarAsRetain() {
		this.isRetain=true;
	}

	public String getNameVar() {
		return name_var;
	}

	public void setNameVar(String name_var) {
		this.name_var = name_var.toUpperCase();
	}

	public int getStartColumn() {
		return start_column;
	}
	
	public void setStartAndEndColumn(int start,int end) throws InvalidInformatStringException {
		if(end < start ||  start==0) throw new InvalidInformatStringException(
		   "Start column non puo essere maggiore di End column o essere uguale a zero ");
		
		setStartColumn(start) ;
		setEndColumn(end) ;
		is_column_format=true;
		
	}

	private void setStartColumn(int start_column) {
		this.start_column = start_column;
	}
	
	public boolean isColumnFormat() {
		return is_column_format;
	}

	public int getEndColumn() {
		return end_column;
	}

	private void setEndColumn(int end_column) {
		this.end_column = end_column;
	}

	public int getLengthVar() {
		return length_var;
	}

	public void setLengthVar(int length_var) {
		this.length_var = length_var;
	}

	public Class getTypeVar() {
		return type_var;
	}
	
	public int getTypeSql() {
		return type_sql;
	}

	public void setTypeVar(Class type_var) {
		this.type_var = type_var;
		
		if(this.type_var==Integer.class) {
			type_sql= Types.INTEGER;
			length_var=TypeSSC.LENGTH_INT_BYTE;
		}
		else if(this.type_var==Long.class) {
			type_sql= Types.BIGINT;
			length_var=TypeSSC.LENGTH_LONG_BYTE;
		}
		else if(this.type_var==Short.class) {
			type_sql= Types.SMALLINT;
			length_var=TypeSSC.LENGTH_SHORT_BYTE;
		}
		else if(this.type_var==Boolean.class) {
			type_sql= Types.BOOLEAN;
			length_var=TypeSSC.LENGTH_BOOLEAN_BYTE;
		}
		else if(this.type_var==GregorianCalendar.class) {
			type_sql= Types.TIMESTAMP;
			length_var=TypeSSC.DEFAULT_LENGTH_DATE_BYTE;
		}
		else if(this.type_var==Float.class) {
			type_sql= Types.FLOAT;
			length_var=TypeSSC.LENGTH_FLOAT_BYTE;
		}
		else if(this.type_var==Double.class) {
			type_sql= Types.DOUBLE;
			length_var=TypeSSC.LENGTH_DOUBLE_BYTE;
		}	
		else if(this.type_var==Byte.class) {
			type_sql=Types.TINYINT;
			length_var=TypeSSC.LENGTH_BYTE;
		}
		else if(this.type_var==StringBuffer.class) {
			type_sql=Types.CHAR;
		}
		else if(this.type_var==String.class) {
			type_sql=Types.VARCHAR;
		}
		else if(this.type_var==Character.class) {
			type_sql=Types.CHAR;
			
		}
	}

	
	
	public void setSettingMissing(SETTING_MISSING set_mis) {
		if(set_mis ==SETTING_MISSING.MISSING_NULL)  {
			logger.log(SscLevel.NOTE,"Campo '"+name_var+"'. Gli eventuali valori space o blank saranno sostituiti con null ");
		}
		else if(set_mis ==SETTING_MISSING.MISSING_SPACE) {
			logger.log(SscLevel.NOTE,"Campo '"+name_var+"'. Gli eventuali valori null saranno sostituiti con space ");
		}
		manager_missing_value=set_mis;
	}
	
	public SETTING_MISSING getSettingMissing() {
		return manager_missing_value;
	}

	public DATE_FORMAT getFormatDate() {
		return formate_data;
	}

	public void setFormatDate(DATE_FORMAT formate_data) {
		this.formate_data = formate_data;
	}

	public boolean isRetain() {
		return isRetain;
	}
}
