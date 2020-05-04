package it.ssc.dynamic_source;

import it.ssc.parser.ParserValueDate;
import it.ssc.parser.DateFormat.DATE_FORMAT;
import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.util.GregCalForInFunction;
import it.ssc.util.GregorianCalendarFormat;

import java.util.GregorianCalendar;
import java.util.HashMap;

class VerifyConditionInAndNotin {
	
	private HashMap<String,GregCalForInFunction> date_in;
	
	protected VerifyConditionInAndNotin() {
		date_in=new HashMap<String,GregCalForInFunction>();
	}
	
	//String
	protected  boolean in(String var, String...  strings )  {
		for(String value:strings )  {
			if(var==null || value==null) {
				if(var==value) return true;
			}
			else if(var.equals(value)) return true;
		}
		return false;
	}
	
	protected  boolean notin(String var, String...  strings )  {
		return !in(var,strings);
	}
	
	//Double
	protected  boolean in(Double var, Double... doubles )  {
	
		for(Double value:doubles )  {
			if(var==null || value==null) {
				if (var==value)  return true;
			}
			else if(value==var) return true;
		}
		return false;
	}
	
	protected  boolean notin(Double var, Double... doubles )  {
		return !in(var,doubles);
	}
	
	
	//Integer
	protected  boolean in(Integer var, Integer...doubles  )  {
		for(Integer value:doubles )  {
			if(var==null || value==null) {
				if (var==value)  return true;
			}
			else if(value==var) return true;
		}
		return false;
	}
	
	protected  boolean notin(Integer var, Integer... doubles )  {
		return !in(var,doubles);
	}
	
	//Float 
	protected  boolean in(Float var, Float...doubles  )  {
		for(Float value:doubles )  {
			if(var==null || value==null) {
				if (var==value)  return true;
			}
			else if(value==var) return true;
		}
		return false;
	}
	
	protected  boolean notin(Float var, Float... doubles )  {
		return !in(var,doubles);
	}

	//long
	protected  boolean in(Long var, Long...doubles  )  {
		for(Long value:doubles )  {
			if(var==null || value==null) {
				if (var==value)  return true;
			}
			else if(value==var) return true;
		}
		return false;
	}
	
	protected  boolean notin(Long var, Long... doubles )  {
		return !in(var,doubles);
	}
	
	//Byte
	protected  boolean in(Byte var, Byte...doubles  )  {
		for(Byte value:doubles )  {
			if(var==null || value==null) {
				if (var==value)  return true;
			}
			else if(value==var) return true;
		}
		return false;
	}
	
	protected  boolean notin(Byte var, Byte... doubles )  {
		return !in(var,doubles);
	}
	
	
	//Char
	protected  boolean in(Character var, Character...doubles  )  {
		for(Character value:doubles )  {
			if(var==null || value==null) {
				if (var==value)  return true;  
			}
			else if(value==var) return true;  
		}
		return false;
	}
	
	protected  boolean notin(Character var, Character... doubles )  {
		return !in(var,doubles);
	}
	
	
	//Calendar
	
	protected  boolean in(GregorianCalendar var, String... dates)	throws InvalidDateFormatException {
		for (String value : dates) {
			if(var == null && value == null)  {
				return true; 
			}
			else  if ( (var != null && value != null) ) {
				GregCalForInFunction greg_val_for_in=date_in.get(value);
				 if(greg_val_for_in==null) {
					 
					if (ParserValueDate.isPatternDdMmYyyy(value)) {
						GregorianCalendar calen = ParserValueDate.parser(value,DATE_FORMAT.GG_MM_AAAA);
						date_in.put(value, greg_val_for_in=new GregCalForInFunction(calen,DATE_FORMAT.GG_MM_AAAA));
					}
					else if(ParserValueDate.isPatternHhMmSs(value))  {
						GregorianCalendar calen = ParserValueDate.parser(value,DATE_FORMAT.HH_MM_SS);
						date_in.put(value, greg_val_for_in=new GregCalForInFunction(calen,DATE_FORMAT.HH_MM_SS));
					}
					else {
						throw new InvalidDateFormatException("ERRORE ! Formato data/ora non valido passato alla funzione in(). Valori accettati GG/MM/AAAA (o GG-MM-AAAA) o HH:MM:SS");
					}
				 }
				
				 
				if (greg_val_for_in.getTypeFormat()==DATE_FORMAT.GG_MM_AAAA &&  GregorianCalendarFormat.equalsDdMmYyyy(var, greg_val_for_in.getCal())) return true; 
				else if (greg_val_for_in.getTypeFormat()==DATE_FORMAT.HH_MM_SS && GregorianCalendarFormat.equalsHhMmSs(var, greg_val_for_in.getCal())) return true; 
			} 
		}
		return false;
	}
	
	protected  boolean notin(GregorianCalendar var, String... dates)	throws InvalidDateFormatException {
		return !in(var,dates);
	}	
}
