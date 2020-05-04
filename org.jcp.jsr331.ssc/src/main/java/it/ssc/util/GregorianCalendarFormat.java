package it.ssc.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class GregorianCalendarFormat extends GregorianCalendar {
	
	
	private static final long serialVersionUID = 1L;
	
	public GregorianCalendarFormat(GregorianCalendar cal) {
		super();
		this.setTimeInMillis(cal.getTimeInMillis());
	}

	public GregorianCalendarFormat() {
		super();
		
	}
	public GregorianCalendar setTimeInMillix(long millix) {
		this.setTimeInMillis(millix);
		return this;
	}
	
	public static boolean equalsDdMmYyyy(GregorianCalendar cal1,GregorianCalendar cal2) {
		
		if(cal1.get(Calendar.YEAR)==cal2.get(Calendar.YEAR) && 
		cal1.get(Calendar.DAY_OF_YEAR)==cal2.get(Calendar.DAY_OF_YEAR))  return true;
		return false;
	}
	
	
	public static boolean equalsHhMmSs(GregorianCalendar cal1,GregorianCalendar cal2) {
		
		if(cal1.get(Calendar.HOUR_OF_DAY)==cal2.get(Calendar.HOUR_OF_DAY) && 
				cal1.get(Calendar.MINUTE)==cal2.get(Calendar.MINUTE) && 
		        cal1.get(Calendar.SECOND)==cal2.get(Calendar.SECOND))  return true;
		return false;
	}
	
	public String toString() {

		return 
		get(Calendar.DAY_OF_MONTH) + "/" +
		(get(Calendar.MONTH)+1) + "/" +
		get(Calendar.YEAR) + ":" +
		get(Calendar.HOUR_OF_DAY) + ":"+ 
		get(Calendar.MINUTE) + ":" + 
		get(Calendar.SECOND);

	}
}
