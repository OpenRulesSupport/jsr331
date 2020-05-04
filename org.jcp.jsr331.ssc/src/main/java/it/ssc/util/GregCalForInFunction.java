package it.ssc.util;

import it.ssc.parser.DateFormat;

import java.util.GregorianCalendar;

public class GregCalForInFunction {
	private GregorianCalendar cal;
	private DateFormat.DATE_FORMAT type_format;
	
	public GregCalForInFunction(GregorianCalendar cal,DateFormat.DATE_FORMAT type_format)   {
		this.cal=cal;
		this.type_format=type_format;
	}

	public GregorianCalendar getCal() {
		return cal;
	}

	public DateFormat.DATE_FORMAT getTypeFormat() {
		return type_format;
	}

}
