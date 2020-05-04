package it.ssc.parser;

import it.ssc.parser.exception.InvalidBooleanFormatException;

public class ParserValueBoolean {
	
	public static boolean parser(String value) throws InvalidBooleanFormatException   {
		if(value.equalsIgnoreCase("true")) return true;
		else if(value.equalsIgnoreCase("t")) return true;
		else if(value.equalsIgnoreCase("false")) return false;
		else if(value.equalsIgnoreCase("f")) return false;
		else { 
			throw new InvalidBooleanFormatException("ERRORE ! Il valore "+value +" letto non e' un booleano (valori acettati 'F', 'FALSE', 'T' , 'TRUE')");
		}
	}
}
